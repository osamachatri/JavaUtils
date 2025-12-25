package com.oussama_chatri.fileUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.*;
import java.util.concurrent.atomic.AtomicLong;

public class AdvancedFileUtils {

    public static String calculateChecksum(String path, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream is = Files.newInputStream(Paths.get(path))) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String calculateMD5(String path) throws IOException, NoSuchAlgorithmException {
        return calculateChecksum(path, "MD5");
    }

    public static String calculateSHA256(String path) throws IOException, NoSuchAlgorithmException {
        return calculateChecksum(path, "SHA-256");
    }

    public static boolean compareFiles(String path1, String path2) throws IOException {
        if (!Files.exists(Paths.get(path1)) || !Files.exists(Paths.get(path2))) {
            return false;
        }
        if (Files.size(Paths.get(path1)) != Files.size(Paths.get(path2))) {
            return false;
        }
        try (InputStream is1 = Files.newInputStream(Paths.get(path1));
             InputStream is2 = Files.newInputStream(Paths.get(path2))) {
            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];
            int read1, read2;
            while ((read1 = is1.read(buffer1)) > 0) {
                read2 = is2.read(buffer2);
                if (read1 != read2 || !Arrays.equals(buffer1, 0, read1, buffer2, 0, read2)) {
                    return false;
                }
            }
            return is2.read() == -1;
        }
    }

    public static void safeDelete(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            return;
        }

        if (Files.isRegularFile(p)) {
            overwriteFile(path);
            Files.delete(p);
        } else if (Files.isDirectory(p)) {
            try (Stream<Path> walk = Files.walk(p)) {
                List<Path> paths = walk.sorted((a, b) -> b.compareTo(a))
                        .collect(Collectors.toList());
                for (Path path1 : paths) {
                    if (Files.isRegularFile(path1)) {
                        overwriteFile(path1.toString());
                    }
                    Files.delete(path1);
                }
            }
        }
    }

    private static void overwriteFile(String path) throws IOException {
        long size = Files.size(Paths.get(path));
        try (RandomAccessFile raf = new RandomAccessFile(path, "rws")) {
            raf.seek(0);
            byte[] zeros = new byte[4096];
            long remaining = size;
            while (remaining > 0) {
                int toWrite = (int) Math.min(zeros.length, remaining);
                raf.write(zeros, 0, toWrite);
                remaining -= toWrite;
            }
            raf.getFD().sync();
        }
    }

    public static void copyDirectory(String source, String destination) throws IOException {
        Path src = Paths.get(source);
        Path dest = Paths.get(destination);

        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(srcPath -> {
                try {
                    Path destPath = dest.resolve(src.relativize(srcPath));
                    if (Files.isDirectory(srcPath)) {
                        Files.createDirectories(destPath);
                    } else {
                        Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static long getDirectorySize(String directory) throws IOException {
        AtomicLong size = new AtomicLong(0);
        try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
            stream.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            size.addAndGet(Files.size(path));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        return size.get();
    }

    public static List<File> findFiles(String directory, String pattern) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> matcher.matches(path.getFileName()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public static List<File> findFilesRecursive(String directory, String extension) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(extension))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public static void zipDirectory(String sourceDir, String zipFile) throws IOException {
        Path source = Paths.get(sourceDir);
        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream(zipFile))) {
            Files.walk(source)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(source.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    public static void unzip(String zipFile, String destDir) throws IOException {
        Path dest = Paths.get(destDir);
        Files.createDirectories(dest);

        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path newPath = dest.resolve(zipEntry.getName());

                if (!newPath.normalize().startsWith(dest.normalize())) {
                    throw new IOException("Bad zip entry");
                }

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    public static Map<String, Object> getFileMetadata(String path) throws IOException {
        Path p = Paths.get(path);
        Map<String, Object> metadata = new HashMap<>();

        BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
        metadata.put("size", attrs.size());
        metadata.put("creationTime", attrs.creationTime().toMillis());
        metadata.put("lastModifiedTime", attrs.lastModifiedTime().toMillis());
        metadata.put("lastAccessTime", attrs.lastAccessTime().toMillis());
        metadata.put("isDirectory", attrs.isDirectory());
        metadata.put("isRegularFile", attrs.isRegularFile());
        metadata.put("isSymbolicLink", attrs.isSymbolicLink());

        try {
            PosixFileAttributes posixAttrs = Files.readAttributes(p, PosixFileAttributes.class);
            metadata.put("owner", posixAttrs.owner().getName());
            metadata.put("group", posixAttrs.group().getName());
            metadata.put("permissions", PosixFilePermissions.toString(posixAttrs.permissions()));
        } catch (UnsupportedOperationException | IOException e) {
        }

        return metadata;
    }

    public static void setFileTime(String path, long lastModifiedTime) throws IOException {
        Files.setLastModifiedTime(Paths.get(path),
                FileTime.fromMillis(lastModifiedTime));
    }

    public static String getOwner(String path) throws IOException {
        return Files.getOwner(Paths.get(path)).getName();
    }

    public static void setOwner(String path, String owner) throws IOException {
        UserPrincipalLookupService lookupService =
                FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(owner);
        Files.setOwner(Paths.get(path), userPrincipal);
    }

    public static void setPosixPermissions(String path, String permissions) throws IOException {
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
        Files.setPosixFilePermissions(Paths.get(path), perms);
    }

    public static String readFileWithEncoding(String path, String encoding) throws IOException {
        return Files.readString(Paths.get(path), java.nio.charset.Charset.forName(encoding));
    }

    public static void writeFileWithEncoding(String path, String content, String encoding)
            throws IOException {
        Files.writeString(Paths.get(path), content,
                java.nio.charset.Charset.forName(encoding));
    }

    public static BufferedReader createBufferedReader(String path) throws IOException {
        return Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
    }

    public static BufferedWriter createBufferedWriter(String path) throws IOException {
        return Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
    }

    public static void watchDirectory(String directory, Consumer<WatchEvent<?>> handler)
            throws IOException, InterruptedException {
        Path path = Paths.get(directory);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    handler.accept(event);
                }
                key.reset();
            }
        }
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix).toFile();
    }

    public static File createTempDirectory(String prefix) throws IOException {
        return Files.createTempDirectory(prefix).toFile();
    }

    public static boolean isSymbolicLink(String path) {
        return Files.isSymbolicLink(Paths.get(path));
    }

    public static String readSymbolicLink(String path) throws IOException {
        return Files.readSymbolicLink(Paths.get(path)).toString();
    }

    public static void createSymbolicLink(String link, String target) throws IOException {
        Files.createSymbolicLink(Paths.get(link), Paths.get(target));
    }

    public static void createHardLink(String link, String existing) throws IOException {
        Files.createLink(Paths.get(link), Paths.get(existing));
    }

    public static String getMimeType(String path) throws IOException {
        return Files.probeContentType(Paths.get(path));
    }

    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }
}
