package com.oussama_chatri.fileUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicFileUtils {

    public static String readFile(String path) throws IOException {
        return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
    }

    public static String readFile(File file) throws IOException {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    public static byte[] readFileBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static List<String> readLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
    }

    public static void writeFile(String path, String content) throws IOException {
        Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8);
    }

    public static void writeFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    }

    public static void writeFileBytes(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }

    public static void writeLines(String path, List<String> lines) throws IOException {
        Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
    }

    public static void appendToFile(String path, String content) throws IOException {
        Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void appendLine(String path, String line) throws IOException {
        Files.writeString(Paths.get(path), line + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static boolean isFile(String path) {
        return Files.isRegularFile(Paths.get(path));
    }

    public static boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    public static boolean createFile(String path) throws IOException {
        Path p = Paths.get(path);
        if (Files.exists(p)) {
            return false;
        }
        Files.createFile(p);
        return true;
    }

    public static boolean createDirectory(String path) throws IOException {
        Path p = Paths.get(path);
        if (Files.exists(p)) {
            return false;
        }
        Files.createDirectories(p);
        return true;
    }

    public static boolean delete(String path) throws IOException {
        return Files.deleteIfExists(Paths.get(path));
    }

    public static void forceDelete(String path) throws IOException {
        Path p = Paths.get(path);
        if (Files.isDirectory(p)) {
            try (Stream<Path> walk = Files.walk(p)) {
                walk.sorted((a, b) -> b.compareTo(a))
                        .forEach(path1 -> {
                            try {
                                Files.delete(path1);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        } else {
            Files.deleteIfExists(p);
        }
    }

    public static void copyFile(String source, String destination) throws IOException {
        Files.copy(Paths.get(source), Paths.get(destination),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public static void moveFile(String source, String destination) throws IOException {
        Files.move(Paths.get(source), Paths.get(destination),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public static long getFileSize(String path) throws IOException {
        return Files.size(Paths.get(path));
    }

    public static String getFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public static String getFileExtension(String path) {
        String name = getFileName(path);
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }

    public static String getFileNameWithoutExtension(String path) {
        String name = getFileName(path);
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }

    public static String getParentDirectory(String path) {
        Path parent = Paths.get(path).getParent();
        return parent != null ? parent.toString() : null;
    }

    public static long getLastModified(String path) throws IOException {
        return Files.getLastModifiedTime(Paths.get(path)).toMillis();
    }

    public static List<File> listFiles(String directory) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(directory))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public static List<File> listDirectories(String directory) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(directory))) {
            return stream
                    .filter(Files::isDirectory)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public static List<File> listAllFiles(String directory) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(directory))) {
            return stream
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    public static boolean isReadable(String path) {
        return Files.isReadable(Paths.get(path));
    }

    public static boolean isWritable(String path) {
        return Files.isWritable(Paths.get(path));
    }

    public static boolean isExecutable(String path) {
        return Files.isExecutable(Paths.get(path));
    }

    public static void setReadable(String path, boolean readable) {
        new File(path).setReadable(readable);
    }

    public static void setWritable(String path, boolean writable) {
        new File(path).setWritable(writable);
    }

    public static void setExecutable(String path, boolean executable) {
        new File(path).setExecutable(executable);
    }
}
