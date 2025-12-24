package com.oussama_chatri.math_utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class BasicMathUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundDown(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.DOWN);
        return bd.doubleValue();
    }

    public static double roundUp(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.UP);
        return bd.doubleValue();
    }

    public static double percentage(double value, double total) {
        if (total == 0) throw new ArithmeticException("Total cannot be zero");
        return (value / total) * 100;
    }

    public static double percentageOf(double percentage, double total) {
        return (percentage / 100) * total;
    }

    public static double percentageIncrease(double oldValue, double newValue) {
        if (oldValue == 0) throw new ArithmeticException("Old value cannot be zero");
        return ((newValue - oldValue) / oldValue) * 100;
    }

    public static double percentageDecrease(double oldValue, double newValue) {
        return percentageIncrease(oldValue, newValue);
    }

    public static double addPercentage(double value, double percentage) {
        return value + (value * percentage / 100);
    }

    public static double subtractPercentage(double value, double percentage) {
        return value - (value * percentage / 100);
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public static double sqrt(double value) {
        return Math.sqrt(value);
    }

    public static double cbrt(double value) {
        return Math.cbrt(value);
    }

    public static double nthRoot(double value, int n) {
        return Math.pow(value, 1.0 / n);
    }

    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial not defined for negative numbers");
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    public static boolean isOdd(int n) {
        return n % 2 != 0;
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static double abs(double value) {
        return Math.abs(value);
    }

    public static int max(int... values) {
        return Arrays.stream(values).max().orElseThrow();
    }

    public static double max(double... values) {
        return Arrays.stream(values).max().orElseThrow();
    }

    public static int min(int... values) {
        return Arrays.stream(values).min().orElseThrow();
    }

    public static double min(double... values) {
        return Arrays.stream(values).min().orElseThrow();
    }

    public static double average(double... values) {
        return Arrays.stream(values).average().orElseThrow();
    }

    public static double sum(double... values) {
        return Arrays.stream(values).sum();
    }

    public static double median(double... values) {
        double[] sorted = Arrays.copyOf(values, values.length);
        Arrays.sort(sorted);
        int middle = sorted.length / 2;
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        } else {
            return sorted[middle];
        }
    }

    public static double variance(double... values) {
        double mean = average(values);
        double sumSquaredDiff = 0;
        for (double value : values) {
            sumSquaredDiff += Math.pow(value - mean, 2);
        }
        return sumSquaredDiff / values.length;
    }

    public static double standardDeviation(double... values) {
        return Math.sqrt(variance(values));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }

    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static double sin(double angle) {
        return Math.sin(angle);
    }

    public static double cos(double angle) {
        return Math.cos(angle);
    }

    public static double tan(double angle) {
        return Math.tan(angle);
    }

    public static double asin(double value) {
        return Math.asin(value);
    }

    public static double acos(double value) {
        return Math.acos(value);
    }

    public static double atan(double value) {
        return Math.atan(value);
    }

    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    public static double log(double value) {
        return Math.log(value);
    }

    public static double log10(double value) {
        return Math.log10(value);
    }

    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    public static double exp(double value) {
        return Math.exp(value);
    }

    public static double ceil(double value) {
        return Math.ceil(value);
    }

    public static double floor(double value) {
        return Math.floor(value);
    }

    public static long roundToLong(double value) {
        return Math.round(value);
    }

    public static int signum(double value) {
        return (int) Math.signum(value);
    }

    public static double hypotenuse(double a, double b) {
        return Math.hypot(a, b);
    }

    public static int fibonacci(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }

    public static double randomInRange(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    public static int randomInRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static double compound(double principal, double rate, int times, int years) {
        return principal * Math.pow(1 + (rate / times), times * years);
    }

    public static double simpleInterest(double principal, double rate, int years) {
        return principal * rate * years;
    }

    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static double pythagorean(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    public static double areaOfCircle(double radius) {
        return Math.PI * radius * radius;
    }

    public static double circumferenceOfCircle(double radius) {
        return 2 * Math.PI * radius;
    }

    public static double areaOfRectangle(double length, double width) {
        return length * width;
    }

    public static double areaOfTriangle(double base, double height) {
        return 0.5 * base * height;
    }

    public static double volumeOfSphere(double radius) {
        return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
    }

    public static double volumeOfCube(double side) {
        return Math.pow(side, 3);
    }

    public static double volumeOfCylinder(double radius, double height) {
        return Math.PI * radius * radius * height;
    }

    public static void main(String[] args) {
        System.out.println("Round 3.14159 to 2 places: " + round(3.14159, 2));
        System.out.println("50 is what % of 200: " + percentage(50, 200));
        System.out.println("25% of 200: " + percentageOf(25, 200));
        System.out.println("Factorial of 5: " + factorial(5));
        System.out.println("GCD of 48 and 18: " + gcd(48, 18));
        System.out.println("Is 17 prime: " + isPrime(17));
        System.out.println("Average of 1,2,3,4,5: " + average(1, 2, 3, 4, 5));
        System.out.println("Distance between (0,0) and (3,4): " + distance(0, 0, 3, 4));
        System.out.println("Area of circle with radius 5: " + areaOfCircle(5));
    }
}
