package com.oussama_chatri.string;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null) return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) return false;
        }
        return true;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    public static String reverse(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }

    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static boolean equalsIgnoreCase(CharSequence s1, CharSequence s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.toString().equalsIgnoreCase(s2.toString());
    }

    public static String join(Collection<?> elements, String delimiter) {
        if (elements == null) return null;
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = elements.iterator();
        while (it.hasNext()) {
            Object el = it.next();
            sb.append(el == null ? "null" : el.toString());
            if (it.hasNext()) sb.append(delimiter);
        }
        return sb.toString();
    }

    public static String join(Object[] array, String delimiter) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] == null ? "null" : array[i].toString());
            if (i < array.length - 1) sb.append(delimiter);
        }
        return sb.toString();
    }

    public static String repeat(String str, int count) {
        if (str == null) return null;
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) return null;
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String[] split(String str, char delimiter) {
        if (str == null) return null;
        List<String> list = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) {
                list.add(str.substring(start, i));
                start = i + 1;
            }
        }
        list.add(str.substring(start));
        return list.toArray(new String[0]);
    }

    public static boolean containsAny(CharSequence cs, CharSequence... searchStrings) {
        if (cs == null || searchStrings == null) return false;
        String str = cs.toString();
        for (CharSequence search : searchStrings) {
            if (search != null && str.contains(search)) return true;
        }
        return false;
    }

    public static boolean containsIgnoreCase(CharSequence cs, CharSequence search) {
        if (cs == null || search == null) return false;
        return cs.toString().toLowerCase().contains(search.toString().toLowerCase());
    }

    public static String normalizeWhitespace(String str) {
        if (str == null) return null;
        return str.trim().replaceAll("\\s+", " ");
    }

    public static String toSnakeCase(String str) {
        if (isEmpty(str)) return str;
        return str.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
    }

    public static String toCamelCase(String str) {
        if (isEmpty(str)) return str;
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : str.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public static String safeTrim(String str) {
        return str == null ? null : str.trim();
    }

    public static String toBase64(String str) {
        if (str == null) return null;
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String fromBase64(String base64) {
        if (base64 == null) return null;
        return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) return false;
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isDigit(cs.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isAlpha(CharSequence cs) {
        if (isEmpty(cs)) return false;
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isLetter(cs.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isAlphanumeric(CharSequence cs) {
        if (isEmpty(cs)) return false;
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }

    public static String removeWhitespace(String str) {
        if (str == null) return null;
        return str.replaceAll("\\s", "");
    }

    public static String padLeft(String str, int size, char padChar) {
        if (str == null) return null;
        int pads = size - str.length();
        if (pads <= 0) return str;
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < pads; i++) sb.append(padChar);
        sb.append(str);
        return sb.toString();
    }

    public static String padRight(String str, int size, char padChar) {
        if (str == null) return null;
        int pads = size - str.length();
        if (pads <= 0) return str;
        StringBuilder sb = new StringBuilder(size);
        sb.append(str);
        for (int i = 0; i < pads; i++) sb.append(padChar);
        return sb.toString();
    }

    public static String capitalizeWords(String str) {
        if (isEmpty(str)) return str;
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder(str.length());
        for (String w : words) {
            if (w.length() > 0) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                        .append(w.substring(1).toLowerCase())
                        .append(' ');
            }
        }
        return sb.toString().trim();
    }

    public static boolean startsWithIgnoreCase(CharSequence cs, CharSequence prefix) {
        if (cs == null || prefix == null) return false;
        if (cs.length() < prefix.length()) return false;
        return cs.subSequence(0, prefix.length()).toString().equalsIgnoreCase(prefix.toString());
    }

    public static boolean endsWithIgnoreCase(CharSequence cs, CharSequence suffix) {
        if (cs == null || suffix == null) return false;
        if (cs.length() < suffix.length()) return false;
        return cs.subSequence(cs.length() - suffix.length(), cs.length())
                .toString().equalsIgnoreCase(suffix.toString());
    }

    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) return str;
        if (str.startsWith(remove)) return str.substring(remove.length());
        return str;
    }

    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) return str;
        if (str.endsWith(remove)) return str.substring(0, str.length() - remove.length());
        return str;
    }

    public static String abbreviate(String str, int maxWidth) {
        if (str == null) return null;
        if (maxWidth < 4) throw new IllegalArgumentException("maxWidth must be at least 4");
        if (str.length() <= maxWidth) return str;
        return str.substring(0, maxWidth - 3) + "...";
    }

    public static String escapeHtml(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder(str.length());
        for (char c : str.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                case '/': sb.append("&#x2F;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String unescapeHtml(String str) {
        if (str == null) return null;
        return str.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#x27;", "'")
                .replace("&#x2F;", "/");
    }

    public static String removeAll(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) return str;
        return str.replace(remove, "");
    }

    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) return 0;
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        if (str == null) return null;
        if (separator == null || separator.isEmpty()) return str.split("");
        List<String> result = new ArrayList<>();
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(separator, pos)) != -1) {
            result.add(str.substring(pos, idx));
            pos = idx + separator.length();
        }
        result.add(str.substring(pos));
        return result.toArray(new String[0]);
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) return null;
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static byte[] fromHexString(String hex) {
        if (hex == null || hex.length() % 2 != 0) throw new IllegalArgumentException("Invalid hex string");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i=0; i<bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return bytes;
    }
}
