package com.oussama_chatri.networkUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Advanced Network Utilities with SSL handling, async operations, connection pooling,
 * retry mechanisms, proxy support, and comprehensive HTTP client features.
 */
public class AdvancedNetworkUtils {

    private static final int DEFAULT_TIMEOUT = 10000;
    private static final int MAX_REDIRECTS = 5;
    private static final String USER_AGENT = "Mozilla/5.0 (Java AdvancedNetworkUtils/1.0)";

    // Connection pool for reusing connections
    private static final Map<String, HttpURLConnection> connectionPool = new ConcurrentHashMap<>();

    // Executor for async operations
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // ==================== ADVANCED HTTP CLIENT ====================

    /**
     * HTTP Request Builder for fluent API
     */
    public static class HttpRequest {
        private String url;
        private String method = "GET";
        private Map<String, String> headers = new HashMap<>();
        private String body;
        private int timeout = DEFAULT_TIMEOUT;
        private boolean followRedirects = true;
        private int maxRetries = 0;
        private int retryDelay = 1000;
        private Proxy proxy;
        private boolean acceptGzip = true;
        private CookieManager cookieManager;

        public HttpRequest(String url) {
            this.url = url;
            this.headers.put("User-Agent", USER_AGENT);
        }

        public HttpRequest method(String method) {
            this.method = method;
            return this;
        }

        public HttpRequest header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpRequest headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public HttpRequest body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpRequest followRedirects(boolean follow) {
            this.followRedirects = follow;
            return this;
        }

        public HttpRequest retry(int maxRetries, int delayMs) {
            this.maxRetries = maxRetries;
            this.retryDelay = delayMs;
            return this;
        }

        public HttpRequest proxy(String host, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            return this;
        }

        public HttpRequest acceptGzip(boolean accept) {
            this.acceptGzip = accept;
            if (accept) {
                headers.put("Accept-Encoding", "gzip, deflate");
            }
            return this;
        }

        public HttpRequest withCookies() {
            this.cookieManager = new CookieManager();
            this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            return this;
        }

        public HttpResponse execute() throws IOException {
            IOException lastException = null;

            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    return executeRequest();
                } catch (IOException e) {
                    lastException = e;
                    if (attempt < maxRetries) {
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Retry interrupted", ie);
                        }
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            throw lastException;
        }

        private HttpResponse executeRequest() throws IOException, URISyntaxException {
            URL urlObj = new URL(url);
            HttpURLConnection connection;

            if (proxy != null) {
                connection = (HttpURLConnection) urlObj.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) urlObj.openConnection();
            }

            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setInstanceFollowRedirects(followRedirects);

            // Set headers
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            // Handle cookies
            if (cookieManager != null) {
                Map<String, List<String>> cookieHeaders = cookieManager.get(
                        urlObj.toURI(), new HashMap<>()
                );
                for (Map.Entry<String, List<String>> entry : cookieHeaders.entrySet()) {
                    if (entry.getKey() != null) {
                        connection.setRequestProperty(entry.getKey(), String.join(";", entry.getValue()));
                    }
                }
            }

            // Send body if present
            if (body != null && !body.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }

            // Read response
            int responseCode = connection.getResponseCode();
            String responseBody = readResponse(connection, acceptGzip);
            Map<String, List<String>> responseHeaders = connection.getHeaderFields();

            // Store cookies
            if (cookieManager != null) {
                cookieManager.put(urlObj.toURI(), responseHeaders);
            }

            connection.disconnect();

            return new HttpResponse(responseCode, responseBody, responseHeaders);
        }

        public CompletableFuture<HttpResponse> executeAsync() {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return execute();
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executorService);
        }
    }

    /**
     * HTTP Response wrapper
     */
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, List<String>> headers;

        public HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        public int getStatusCode() { return statusCode; }
        public String getBody() { return body; }
        public Map<String, List<String>> getHeaders() { return headers; }

        public boolean isSuccessful() {
            return statusCode >= 200 && statusCode < 300;
        }

        public String getHeader(String name) {
            List<String> values = headers.get(name);
            return values != null && !values.isEmpty() ? values.get(0) : null;
        }

        public List<String> getHeaders(String name) {
            return headers.getOrDefault(name, Collections.emptyList());
        }

        @Override
        public String toString() {
            return "HttpResponse{statusCode=" + statusCode + ", bodyLength=" +
                    (body != null ? body.length() : 0) + "}";
        }
    }

    /**
     * Create new HTTP request
     */
    public static HttpRequest request(String url) {
        return new HttpRequest(url);
    }

    // ==================== ADVANCED PINGING ====================

    /**
     * Ping result with detailed information
     */
    public static class PingResult {
        private final String url;
        private final boolean reachable;
        private final long responseTime;
        private final int statusCode;
        private final String error;

        public PingResult(String url, boolean reachable, long responseTime, int statusCode, String error) {
            this.url = url;
            this.reachable = reachable;
            this.responseTime = responseTime;
            this.statusCode = statusCode;
            this.error = error;
        }

        public String getUrl() { return url; }
        public boolean isReachable() { return reachable; }
        public long getResponseTime() { return responseTime; }
        public int getStatusCode() { return statusCode; }
        public String getError() { return error; }

        @Override
        public String toString() {
            return String.format("PingResult{url='%s', reachable=%s, responseTime=%dms, statusCode=%d}",
                    url, reachable, responseTime, statusCode);
        }
    }

    /**
     * Ping URL with detailed result
     */
    public static PingResult pingDetailed(String url) {
        long startTime = System.currentTimeMillis();

        try {
            HttpResponse response = request(url)
                    .method("HEAD")
                    .timeout(5000)
                    .execute();

            long responseTime = System.currentTimeMillis() - startTime;
            return new PingResult(url, true, responseTime, response.getStatusCode(), null);
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return new PingResult(url, false, responseTime, -1, e.getMessage());
        }
    }

    /**
     * Ping multiple URLs concurrently
     */
    public static Map<String, PingResult> pingConcurrent(List<String> urls) {
        Map<String, PingResult> results = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String url : urls) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                results.put(url, pingDetailed(url));
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return results;
    }

    /**
     * Monitor URL availability continuously
     */
    public static void monitorUrl(String url, int intervalSeconds, UrlMonitorCallback callback) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            PingResult result = pingDetailed(url);
            callback.onPingResult(result);
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public interface UrlMonitorCallback {
        void onPingResult(PingResult result);
    }

    // ==================== SSL/TLS UTILITIES ====================

    /**
     * Disable SSL certificate validation (use with caution!)
     */
    public static void disableSSLValidation() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable SSL validation", e);
        }
    }

    /**
     * Get SSL certificate information
     */
    public static Map<String, String> getSSLCertificateInfo(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.connect();

        Map<String, String> certInfo = new LinkedHashMap<>();

        for (java.security.cert.Certificate cert : connection.getServerCertificates()) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) cert;
                certInfo.put("Subject", x509.getSubjectDN().toString());
                certInfo.put("Issuer", x509.getIssuerDN().toString());
                certInfo.put("Serial Number", x509.getSerialNumber().toString());
                certInfo.put("Valid From", x509.getNotBefore().toString());
                certInfo.put("Valid Until", x509.getNotAfter().toString());
                certInfo.put("Signature Algorithm", x509.getSigAlgName());
            }
        }

        connection.disconnect();
        return certInfo;
    }

    /**
     * Check if SSL certificate is valid
     */
    public static boolean isSSLCertificateValid(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            connection.disconnect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== ADVANCED IP UTILITIES ====================

    /**
     * Get detailed IP information
     */
    public static class IpInfo {
        private final String ip;
        private final String hostname;
        private final boolean reachable;
        private final boolean isPrivate;
        private final String type; // IPv4 or IPv6

        public IpInfo(String ip, String hostname, boolean reachable, boolean isPrivate, String type) {
            this.ip = ip;
            this.hostname = hostname;
            this.reachable = reachable;
            this.isPrivate = isPrivate;
            this.type = type;
        }

        public String getIp() { return ip; }
        public String getHostname() { return hostname; }
        public boolean isReachable() { return reachable; }
        public boolean isPrivate() { return isPrivate; }
        public String getType() { return type; }

        @Override
        public String toString() {
            return String.format("IpInfo{ip='%s', hostname='%s', reachable=%s, private=%s, type='%s'}",
                    ip, hostname, reachable, isPrivate, type);
        }
    }

    /**
     * Get detailed IP information
     */
    public static IpInfo getIpInfo(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);

        String hostname = address.getHostName();
        boolean reachable = false;
        try {
            reachable = address.isReachable(3000);
        } catch (IOException e) {
            // Not reachable
        }

        boolean isPrivate = address.isSiteLocalAddress() || address.isLoopbackAddress();
        String type = address instanceof Inet4Address ? "IPv4" : "IPv6";

        return new IpInfo(ip, hostname, reachable, isPrivate, type);
    }

    /**
     * Scan port range on host
     */
    public static List<Integer> scanPorts(String host, int startPort, int endPort, int timeout) {
        List<Integer> openPorts = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                if (isPortOpen(host, currentPort, timeout)) {
                    openPorts.add(currentPort);
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Collections.sort(openPorts);
        return openPorts;
    }

    /**
     * Check if specific port is open
     */
    public static boolean isPortOpen(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get geolocation info from IP using external API
     */
    public static Map<String, Object> getIpGeolocation(String ip) throws IOException {
        String apiUrl = "http://ip-api.com/json/" + ip;
        HttpResponse response = request(apiUrl).execute();

        if (response.isSuccessful()) {
            return parseJsonToMap(response.getBody());
        }

        throw new IOException("Failed to get geolocation: " + response.getStatusCode());
    }

    /**
     * Calculate subnet mask from CIDR notation
     */
    public static String cidrToSubnetMask(int cidr) {
        long mask = 0xFFFFFFFF << (32 - cidr);
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xFF,
                (mask >> 16) & 0xFF,
                (mask >> 8) & 0xFF,
                mask & 0xFF
        );
    }

    /**
     * Check if IP is in subnet
     */
    public static boolean isIpInSubnet(String ip, String subnet, String mask) throws UnknownHostException {
        InetAddress ipAddr = InetAddress.getByName(ip);
        InetAddress subnetAddr = InetAddress.getByName(subnet);
        InetAddress maskAddr = InetAddress.getByName(mask);

        byte[] ipBytes = ipAddr.getAddress();
        byte[] subnetBytes = subnetAddr.getAddress();
        byte[] maskBytes = maskAddr.getAddress();

        for (int i = 0; i < ipBytes.length; i++) {
            if ((ipBytes[i] & maskBytes[i]) != (subnetBytes[i] & maskBytes[i])) {
                return false;
            }
        }

        return true;
    }

    // ==================== WEB SCRAPING UTILITIES ====================

    /**
     * Extract all URLs from HTML content
     */
    public static List<String> extractUrls(String html) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    /**
     * Extract all email addresses from text
     */
    public static List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            emails.add(matcher.group());
        }

        return emails;
    }

    /**
     * Download webpage and all linked resources
     */
    public static void downloadWebpage(String url, String destinationDir) throws IOException {
        File dir = new File(destinationDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Download main HTML
        HttpResponse response = request(url).execute();
        String html = response.getBody();

        // Save HTML
        File htmlFile = new File(dir, "index.html");
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(html);
        }

        // Extract and download linked resources
        List<String> urls = extractUrls(html);
        for (String linkedUrl : urls) {
            if (linkedUrl.endsWith(".css") || linkedUrl.endsWith(".js") ||
                    linkedUrl.endsWith(".jpg") || linkedUrl.endsWith(".png") ||
                    linkedUrl.endsWith(".gif")) {
                try {
                    String filename = new File(new URL(linkedUrl).getPath()).getName();
                    downloadFile(linkedUrl, new File(dir, filename).getAbsolutePath());
                } catch (Exception e) {
                    // Skip failed downloads
                }
            }
        }
    }

    /**
     * Download file with progress callback
     */
    public static void downloadFile(String url, String destination) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);

        long fileSize = connection.getContentLengthLong();

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }

        connection.disconnect();
    }

    // ==================== BANDWIDTH & SPEED TESTING ====================

    /**
     * Test download speed
     */
    public static double testDownloadSpeed(String url, int durationSeconds) throws IOException {
        long startTime = System.currentTimeMillis();
        long bytesRead = 0;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);

        try (InputStream in = connection.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;

            while ((read = in.read(buffer)) != -1) {
                bytesRead += read;

                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= durationSeconds * 1000) {
                    break;
                }
            }
        }

        connection.disconnect();

        long elapsedMs = System.currentTimeMillis() - startTime;
        double elapsedSec = elapsedMs / 1000.0;
        double mbps = (bytesRead * 8) / (elapsedSec * 1_000_000);

        return mbps;
    }

    /**
     * Measure network latency
     */
    public static long measureLatency(String host) {
        long totalTime = 0;
        int attempts = 5;

        for (int i = 0; i < attempts; i++) {
            long startTime = System.nanoTime();
            try {
                InetAddress address = InetAddress.getByName(host);
                address.isReachable(3000);
            } catch (IOException e) {
                return -1;
            }
            totalTime += (System.nanoTime() - startTime);
        }

        return totalTime / (attempts * 1_000_000); // Convert to milliseconds
    }

    // ==================== DNS UTILITIES ====================

    /**
     * Perform DNS lookup
     */
    public static List<String> dnsLookup(String hostname) throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(hostname);
        List<String> ips = new ArrayList<>();

        for (InetAddress address : addresses) {
            ips.add(address.getHostAddress());
        }

        return ips;
    }

    /**
     * Perform reverse DNS lookup
     */
    public static String reverseDnsLookup(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);
        return address.getCanonicalHostName();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Read response handling GZIP compression
     */
    private static String readResponse(HttpURLConnection connection, boolean handleGzip) throws IOException {
        int responseCode = connection.getResponseCode();

        InputStream inputStream = responseCode >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();

        if (inputStream == null) {
            return "";
        }

        // Handle GZIP compression
        String encoding = connection.getContentEncoding();
        if (handleGzip && encoding != null && encoding.equalsIgnoreCase("gzip")) {
            inputStream = new GZIPInputStream(inputStream);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        return response.toString().trim();
    }

    /**
     * Simple JSON parser to map (for basic JSON responses)
     */
    private static Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();

        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1, json.length() - 1);
        }

        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * Cleanup resources
     */
    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Print comprehensive network diagnostics
     */
    public static void printNetworkDiagnostics(String url) {
        System.out.println("=== Network Diagnostics for " + url + " ===");

        try {
            URL urlObj = new URL(url);
            String host = urlObj.getHost();

            System.out.println("\n--- Basic Info ---");
            System.out.println("Host: " + host);
            System.out.println("Protocol: " + urlObj.getProtocol());
            System.out.println("Port: " + (urlObj.getPort() == -1 ? urlObj.getDefaultPort() : urlObj.getPort()));

            System.out.println("\n--- DNS Lookup ---");
            List<String> ips = dnsLookup(host);
            ips.forEach(ip -> System.out.println("  IP: " + ip));

            System.out.println("\n--- Ping Test ---");
            PingResult ping = pingDetailed(url);
            System.out.println("  " + ping);

            System.out.println("\n--- Latency ---");
            long latency = measureLatency(host);
            System.out.println("  Average Latency: " + latency + "ms");

            if (url.startsWith("https")) {
                System.out.println("\n--- SSL Certificate ---");
                Map<String, String> certInfo = getSSLCertificateInfo(url);
                certInfo.forEach((key, value) -> System.out.println("  " + key + ": " + value));
            }

            System.out.println("\n--- HTTP Headers ---");
            HttpResponse response = request(url).method("HEAD").execute();
            response.getHeaders().forEach((key, values) -> {
                if (key != null) {
                    System.out.println("  " + key + ": " + String.join(", ", values));
                }
            });

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
