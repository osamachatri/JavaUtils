package com.oussama_chatri.cryptoUtils;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AdvancedCryptoUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int RSA_KEY_SIZE = 2048;
    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int ARGON2_MEMORY = 65536;
    private static final int ARGON2_ITERATIONS = 3;
    private static final int ARGON2_PARALLELISM = 4;

    public static class KeyPairResult {
        public final String publicKey;
        public final String privateKey;

        public KeyPairResult(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    public static KeyPairResult generateRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyGen.initialize(RSA_KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();

            String publicKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPrivate().getEncoded());

            return new KeyPairResult(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException("RSA key pair generation failed", e);
        }
    }

    public static String encryptRSA(String plaintext, String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA encryption failed", e);
        }
    }

    public static String decryptRSA(String ciphertext, String privateKeyStr) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA decryption failed", e);
        }
    }

    public static String signRSA(String data, String privateKeyStr) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();

            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new RuntimeException("RSA signing failed", e);
        }
    }

    public static boolean verifyRSASignature(String data, String signatureStr, String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public static String encryptAES_GCM(String plaintext, String keyStr) {
        try {
            byte[] key = Base64.getDecoder().decode(keyStr);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM encryption failed", e);
        }
    }

    public static String decryptAES_GCM(String ciphertext, String keyStr) {
        try {
            byte[] key = Base64.getDecoder().decode(keyStr);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            byte[] combined = Base64.getDecoder().decode(ciphertext);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM decryption failed", e);
        }
    }

    public static String generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, new SecureRandom());
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("AES key generation failed", e);
        }
    }

    public static class JWT {
        private final Map<String, Object> header;
        private final Map<String, Object> payload;

        public JWT() {
            this.header = new HashMap<>();
            this.payload = new HashMap<>();
            this.header.put("alg", "HS256");
            this.header.put("typ", "JWT");
        }

        public JWT setClaim(String key, Object value) {
            payload.put(key, value);
            return this;
        }

        public JWT setExpiration(long expirationMinutes) {
            long exp = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)
                    .getEpochSecond();
            payload.put("exp", exp);
            return this;
        }

        public JWT setIssuedAt() {
            payload.put("iat", Instant.now().getEpochSecond());
            return this;
        }

        public JWT setSubject(String subject) {
            payload.put("sub", subject);
            return this;
        }

        public String sign(String secret) {
            try {
                String headerJson = mapToJson(header);
                String payloadJson = mapToJson(payload);

                String headerEncoded = base64UrlEncode(headerJson);
                String payloadEncoded = base64UrlEncode(payloadJson);

                String data = headerEncoded + "." + payloadEncoded;
                String signature = createSignature(data, secret);

                return data + "." + signature;
            } catch (Exception e) {
                throw new RuntimeException("JWT signing failed", e);
            }
        }

        private String createSignature(String data, String secret) throws Exception {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signature);
        }

        private String base64UrlEncode(String input) {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(input.getBytes(StandardCharsets.UTF_8));
        }

        private String base64UrlEncode(byte[] input) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
        }

        private String mapToJson(Map<String, Object> map) {
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(entry.getKey()).append("\":");
                Object value = entry.getValue();
                if (value instanceof String) {
                    json.append("\"").append(value).append("\"");
                } else {
                    json.append(value);
                }
                first = false;
            }
            json.append("}");
            return json.toString();
        }
    }

    public static Map<String, Object> verifyJWT(String token, String secret) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String headerEncoded = parts[0];
            String payloadEncoded = parts[1];
            String signature = parts[2];

            String data = headerEncoded + "." + payloadEncoded;
            String expectedSignature = createJWTSignature(data, secret);

            if (!MessageDigest.isEqual(
                    signature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                throw new SecurityException("Invalid signature");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(payloadEncoded),
                    StandardCharsets.UTF_8
            );

            Map<String, Object> payload = parseJson(payloadJson);

            if (payload.containsKey("exp")) {
                long exp = ((Number) payload.get("exp")).longValue();
                if (Instant.now().getEpochSecond() > exp) {
                    throw new SecurityException("Token expired");
                }
            }

            return payload;
        } catch (Exception e) {
            throw new RuntimeException("JWT verification failed", e);
        }
    }

    private static String createJWTSignature(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        mac.init(secretKey);
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }

    private static Map<String, Object> parseJson(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                String value = keyValue[1].trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    map.put(key, value.substring(1, value.length() - 1));
                } else {
                    try {
                        map.put(key, Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        map.put(key, value);
                    }
                }
            }
        }
        return map;
    }

    public static class SecureToken {
        private final String token;
        private final long expiresAt;
        private final Map<String, String> metadata;

        public SecureToken(int length, long expirationMinutes) {
            this.token = generateCryptoToken(length);
            this.expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)
                    .toEpochMilli();
            this.metadata = new HashMap<>();
        }

        public String getToken() {
            return token;
        }

        public boolean isExpired() {
            return Instant.now().toEpochMilli() > expiresAt;
        }

        public long getExpiresAt() {
            return expiresAt;
        }

        public void addMetadata(String key, String value) {
            metadata.put(key, value);
        }

        public String getMetadata(String key) {
            return metadata.get(key);
        }

        public String toStorageFormat(String secret) {
            String hash = hmacSHA256(token, secret);
            return hash + ":" + expiresAt + ":" + mapToString(metadata);
        }

        public static boolean verify(String token, String storedHash, String secret) {
            String computedHash = hmacSHA256(token, secret);
            return MessageDigest.isEqual(
                    computedHash.getBytes(StandardCharsets.UTF_8),
                    storedHash.getBytes(StandardCharsets.UTF_8)
            );
        }

        private String mapToString(Map<String, String> map) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            return Base64.getEncoder().encodeToString(
                    sb.toString().getBytes(StandardCharsets.UTF_8)
            );
        }

        private static String hmacSHA256(String data, String key) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKey = new SecretKeySpec(
                        key.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );
                mac.init(secretKey);
                byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(hmac);
            } catch (Exception e) {
                throw new RuntimeException("HMAC generation failed", e);
            }
        }

        private static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }

    private static String generateCryptoToken(int length) {
        byte[] token = new byte[length];
        new SecureRandom().nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    public static String deriveKey(String password, String salt, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt.getBytes(StandardCharsets.UTF_8),
                    100000,
                    keyLength
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] key = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            throw new RuntimeException("Key derivation failed", e);
        }
    }

    public static String encryptWithKey(String plaintext, String derivedKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(derivedKey);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decryptWithKey(String ciphertext, String derivedKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(derivedKey);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            byte[] combined = Base64.getDecoder().decode(ciphertext);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
