package com.oussama_chatri.networkUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic Network Utilities for URL pinging, IP extraction, and HTTP client operations.
 * Provides fundamental networking capabilities for common tasks.
 */
public class BasicNetworkUtils {

    private static final int DEFAULT_TIMEOUT = 5000; // 5 seconds
    private static final String USER_AGENT = "Mozilla/5.0 (Java NetworkUtils)";

    // ==================== URL PINGING ====================

    /**
     * Ping URL using HTTP HEAD request (fastest method)
     */
    public static boolean pingUrl(String urlString) {
        return pingUrl(urlString, DEFAULT_TIMEOUT);
    }

    /**
     * Ping URL with custom timeout
     */
    public static boolean pingUrl(String urlString, int timeout) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode >= 200 && responseCode < 400;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ping URL and return response code
     */
    public static int pingUrlWithCode(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        connection.disconnect();

        return responseCode;
    }

    /**
     * Check if host is reachable using InetAddress
     */
    public static boolean isHostReachable(String host) {
        return isHostReachable(host, DEFAULT_TIMEOUT);
    }

    /**
     * Check if host is reachable with timeout
     */
    public static boolean isHostReachable(String host, int timeout) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(timeout);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ping multiple URLs and return status map
     */
    public static Map<String, Boolean> pingMultipleUrls(List<String> urls) {
        Map<String, Boolean> results = new LinkedHashMap<>();
        for (String url : urls) {
            results.put(url, pingUrl(url));
        }
        return results;
    }

    /**
     * Measure URL response time in milliseconds
     */
    public static long measureResponseTime(String urlString) {
        long startTime = System.currentTimeMillis();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(DEFAULT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_TIMEOUT);
            connection.setRequestProperty("User-Agent", USER_AGENT);

            connection.getResponseCode();
            connection.disconnect();

            return System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            return -1;
        }
    }

    // ==================== IP EXTRACTION ====================

    /**
     * Get local IP address
     */
    public static String getLocalIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Get local hostname
     */
    public static String getLocalHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * Get all local IP addresses
     */
    public static List<String> getAllLocalIpAddresses() throws SocketException {
        List<String> ipAddresses = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    ipAddresses.add(address.getHostAddress());
                }
            }
        }

        return ipAddresses;
    }

    /**
     * Get IP address from hostname
     */
    public static String getIpFromHostname(String hostname) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(hostname);
        return address.getHostAddress();
    }

    /**
     * Get hostname from IP address
     */
    public static String getHostnameFromIp(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);
        return address.getHostName();
    }

    /**
     * Extract IP addresses from text using regex
     */
    public static List<String> extractIpAddresses(String text) {
        List<String> ips = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            ips.add(matcher.group());
        }

        return ips;
    }

    /**
     * Validate IP address format
     */
    public static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.isEmpty()) return false;

        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if IP is private/local
     */
    public static boolean isPrivateIp(String ip) {
        if (!isValidIpAddress(ip)) return false;

        String[] parts = ip.split("\\.");
        int first = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);

        // 10.0.0.0 - 10.255.255.255
        if (first == 10) return true;

        // 172.16.0.0 - 172.31.255.255
        if (first == 172 && second >= 16 && second <= 31) return true;

        // 192.168.0.0 - 192.168.255.255
        if (first == 192 && second == 168) return true;

        // Loopback
        if (first == 127) return true;

        return false;
    }

    /**
     * Get public IP address using external service
     */
    public static String getPublicIpAddress() throws IOException {
        String[] services = {
                "https://api.ipify.org",
                "https://checkip.amazonaws.com",
                "https://icanhazip.com"
        };

        for (String service : services) {
            try {
                return httpGet(service).trim();
            } catch (IOException e) {
                // Try next service
            }
        }

        throw new IOException("Unable to determine public IP address");
    }

    // ==================== HTTP CLIENT HELPERS ====================

    /**
     * Perform HTTP GET request
     */
    public static String httpGet(String urlString) throws IOException {
        return httpGet(urlString, null);
    }

    /**
     * Perform HTTP GET request with headers
     */
    public static String httpGet(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        return readResponse(connection);
    }

    /**
     * Perform HTTP POST request
     */
    public static String httpPost(String urlString, String data) throws IOException {
        return httpPost(urlString, data, null);
    }

    /**
     * Perform HTTP POST request with headers
     */
    public static String httpPost(String urlString, String data, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        // Write data
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return readResponse(connection);
    }

    /**
     * Perform HTTP PUT request
     */
    public static String httpPut(String urlString, String data) throws IOException {
        return httpPut(urlString, data, null);
    }

    /**
     * Perform HTTP PUT request with headers
     */
    public static String httpPut(String urlString, String data, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return readResponse(connection);
    }

    /**
     * Perform HTTP DELETE request
     */
    public static String httpDelete(String urlString) throws IOException {
        return httpDelete(urlString, null);
    }

    /**
     * Perform HTTP DELETE request with headers
     */
    public static String httpDelete(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        return readResponse(connection);
    }

    /**
     * Download file from URL
     */
    public static void downloadFile(String urlString, String destinationPath) throws IOException {
        URL url = new URL(urlString);

        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Get HTTP response headers
     */
    public static Map<String, List<String>> getResponseHeaders(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        connection.connect();
        Map<String, List<String>> headers = connection.getHeaderFields();
        connection.disconnect();

        return headers;
    }

    /**
     * Get specific response header
     */
    public static String getResponseHeader(String urlString, String headerName) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        connection.connect();
        String headerValue = connection.getHeaderField(headerName);
        connection.disconnect();

        return headerValue;
    }

    /**
     * Get content type of URL
     */
    public static String getContentType(String urlString) throws IOException {
        return getResponseHeader(urlString, "Content-Type");
    }

    /**
     * Get content length of URL
     */
    public static long getContentLength(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        long length = connection.getContentLengthLong();
        connection.disconnect();

        return length;
    }

    // ==================== URL UTILITIES ====================

    /**
     * Parse URL parameters into map
     */
    public static Map<String, String> parseUrlParameters(String url) {
        Map<String, String> params = new LinkedHashMap<>();

        try {
            String query = new URL(url).getQuery();
            if (query == null || query.isEmpty()) return params;

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    params.put(key, value);
                }
            }
        } catch (Exception e) {
            // Return empty map on error
        }

        return params;
    }

    /**
     * Build URL with parameters
     */
    public static String buildUrl(String baseUrl, Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) return baseUrl;

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append(baseUrl.contains("?") ? "&" : "?");

        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) urlBuilder.append("&");
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            first = false;
        }

        return urlBuilder.toString();
    }

    /**
     * Validate URL format
     */
    public static boolean isValidUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Extract domain from URL
     */
    public static String extractDomain(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        return url.getHost();
    }

    /**
     * Extract protocol from URL
     */
    public static String extractProtocol(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        return url.getProtocol();
    }

    /**
     * Extract port from URL
     */
    public static int extractPort(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        int port = url.getPort();
        return port == -1 ? url.getDefaultPort() : port;
    }

    /**
     * Extract path from URL
     */
    public static String extractPath(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        return url.getPath();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Read response from HTTP connection
     */
    private static String readResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        InputStream inputStream = responseCode >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();

        if (inputStream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        connection.disconnect();
        return response.toString().trim();
    }

    /**
     * URL encode string
     */
    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL decode string
     */
    public static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if URL uses HTTPS
     */
    public static boolean isHttps(String urlString) {
        try {
            URL url = new URL(urlString);
            return "https".equalsIgnoreCase(url.getProtocol());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Convert HTTP URL to HTTPS
     */
    public static String toHttps(String urlString) {
        return urlString.replaceFirst("^http://", "https://");
    }

    /**
     * Print network information
     */
    public static void printNetworkInfo() {
        try {
            System.out.println("=== Network Information ===");
            System.out.println("Local IP: " + getLocalIpAddress());
            System.out.println("Hostname: " + getLocalHostname());

            System.out.println("\nAll Local IPs:");
            for (String ip : getAllLocalIpAddresses()) {
                System.out.println("  - " + ip);
            }

            try {
                System.out.println("\nPublic IP: " + getPublicIpAddress());
            } catch (IOException e) {
                System.out.println("\nPublic IP: Unable to determine");
            }

        } catch (Exception e) {
            System.err.println("Error getting network info: " + e.getMessage());
        }
    }
}
