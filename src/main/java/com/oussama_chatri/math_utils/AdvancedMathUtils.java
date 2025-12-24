package com.oussama_chatri.math_utils;

import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class AdvancedMathUtils {

    public static class Matrix {
        private double[][] data;
        private int rows;
        private int cols;

        public Matrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            this.data = new double[rows][cols];
        }

        public Matrix(double[][] data) {
            this.rows = data.length;
            this.cols = data[0].length;
            this.data = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(data[i], 0, this.data[i], 0, cols);
            }
        }

        public Matrix add(Matrix other) {
            if (this.rows != other.rows || this.cols != other.cols) {
                throw new IllegalArgumentException("Matrix dimensions must match");
            }
            Matrix result = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] + other.data[i][j];
                }
            }
            return result;
        }

        public Matrix subtract(Matrix other) {
            if (this.rows != other.rows || this.cols != other.cols) {
                throw new IllegalArgumentException("Matrix dimensions must match");
            }
            Matrix result = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] - other.data[i][j];
                }
            }
            return result;
        }

        public Matrix multiply(Matrix other) {
            if (this.cols != other.rows) {
                throw new IllegalArgumentException("Invalid matrix dimensions for multiplication");
            }
            Matrix result = new Matrix(this.rows, other.cols);
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < other.cols; j++) {
                    for (int k = 0; k < this.cols; k++) {
                        result.data[i][j] += this.data[i][k] * other.data[k][j];
                    }
                }
            }
            return result;
        }

        public Matrix scalarMultiply(double scalar) {
            Matrix result = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] * scalar;
                }
            }
            return result;
        }

        public Matrix transpose() {
            Matrix result = new Matrix(cols, rows);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[j][i] = this.data[i][j];
                }
            }
            return result;
        }

        public double determinant() {
            if (rows != cols) {
                throw new IllegalArgumentException("Matrix must be square");
            }
            return calculateDeterminant(data, rows);
        }

        private double calculateDeterminant(double[][] matrix, int n) {
            if (n == 1) return matrix[0][0];
            if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

            double det = 0;
            for (int i = 0; i < n; i++) {
                det += Math.pow(-1, i) * matrix[0][i] * calculateDeterminant(getMinor(matrix, 0, i, n), n - 1);
            }
            return det;
        }

        private double[][] getMinor(double[][] matrix, int row, int col, int n) {
            double[][] minor = new double[n - 1][n - 1];
            int r = 0;
            for (int i = 0; i < n; i++) {
                if (i == row) continue;
                int c = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    minor[r][c] = matrix[i][j];
                    c++;
                }
                r++;
            }
            return minor;
        }

        public double[][] getData() {
            return data;
        }
    }

    public static class Vector {
        private double[] components;

        public Vector(double... components) {
            this.components = components.clone();
        }

        public double magnitude() {
            double sum = 0;
            for (double component : components) {
                sum += component * component;
            }
            return Math.sqrt(sum);
        }

        public Vector normalize() {
            double mag = magnitude();
            if (mag == 0) throw new ArithmeticException("Cannot normalize zero vector");
            double[] normalized = new double[components.length];
            for (int i = 0; i < components.length; i++) {
                normalized[i] = components[i] / mag;
            }
            return new Vector(normalized);
        }

        public double dot(Vector other) {
            if (this.components.length != other.components.length) {
                throw new IllegalArgumentException("Vectors must have same dimension");
            }
            double result = 0;
            for (int i = 0; i < components.length; i++) {
                result += this.components[i] * other.components[i];
            }
            return result;
        }

        public Vector cross(Vector other) {
            if (this.components.length != 3 || other.components.length != 3) {
                throw new IllegalArgumentException("Cross product only defined for 3D vectors");
            }
            return new Vector(
                    this.components[1] * other.components[2] - this.components[2] * other.components[1],
                    this.components[2] * other.components[0] - this.components[0] * other.components[2],
                    this.components[0] * other.components[1] - this.components[1] * other.components[0]
            );
        }

        public Vector add(Vector other) {
            if (this.components.length != other.components.length) {
                throw new IllegalArgumentException("Vectors must have same dimension");
            }
            double[] result = new double[components.length];
            for (int i = 0; i < components.length; i++) {
                result[i] = this.components[i] + other.components[i];
            }
            return new Vector(result);
        }

        public Vector subtract(Vector other) {
            if (this.components.length != other.components.length) {
                throw new IllegalArgumentException("Vectors must have same dimension");
            }
            double[] result = new double[components.length];
            for (int i = 0; i < components.length; i++) {
                result[i] = this.components[i] - other.components[i];
            }
            return new Vector(result);
        }

        public Vector scale(double scalar) {
            double[] result = new double[components.length];
            for (int i = 0; i < components.length; i++) {
                result[i] = components[i] * scalar;
            }
            return new Vector(result);
        }

        public double[] getComponents() {
            return components.clone();
        }
    }

    public static class Complex {
        private double real;
        private double imaginary;

        public Complex(double real, double imaginary) {
            this.real = real;
            this.imaginary = imaginary;
        }

        public Complex add(Complex other) {
            return new Complex(this.real + other.real, this.imaginary + other.imaginary);
        }

        public Complex subtract(Complex other) {
            return new Complex(this.real - other.real, this.imaginary - other.imaginary);
        }

        public Complex multiply(Complex other) {
            double r = this.real * other.real - this.imaginary * other.imaginary;
            double i = this.real * other.imaginary + this.imaginary * other.real;
            return new Complex(r, i);
        }

        public Complex divide(Complex other) {
            double denominator = other.real * other.real + other.imaginary * other.imaginary;
            double r = (this.real * other.real + this.imaginary * other.imaginary) / denominator;
            double i = (this.imaginary * other.real - this.real * other.imaginary) / denominator;
            return new Complex(r, i);
        }

        public double magnitude() {
            return Math.sqrt(real * real + imaginary * imaginary);
        }

        public double phase() {
            return Math.atan2(imaginary, real);
        }

        public Complex conjugate() {
            return new Complex(real, -imaginary);
        }

        @Override
        public String toString() {
            return real + (imaginary >= 0 ? " + " : " - ") + Math.abs(imaginary) + "i";
        }
    }

    public static double[] solveQuadratic(double a, double b, double c) {
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return new double[0];
        } else if (discriminant == 0) {
            return new double[]{-b / (2 * a)};
        } else {
            double sqrt = Math.sqrt(discriminant);
            return new double[]{
                    (-b + sqrt) / (2 * a),
                    (-b - sqrt) / (2 * a)
            };
        }
    }

    public static double[] solveCubic(double a, double b, double c, double d) {
        b /= a;
        c /= a;
        d /= a;

        double q = (3 * c - b * b) / 9;
        double r = (9 * b * c - 27 * d - 2 * b * b * b) / 54;
        double discriminant = q * q * q + r * r;

        double term1 = b / 3;

        if (discriminant > 0) {
            double s = r + Math.sqrt(discriminant);
            s = Math.cbrt(s);
            double t = r - Math.sqrt(discriminant);
            t = Math.cbrt(t);
            return new double[]{-term1 + s + t};
        } else if (discriminant == 0) {
            double rSign = (r >= 0) ? Math.cbrt(r) : -Math.cbrt(-r);
            return new double[]{-term1 + 2 * rSign, -term1 - rSign};
        } else {
            q = -q;
            double dum1 = Math.acos(r / Math.sqrt(q * q * q));
            double r13 = 2 * Math.sqrt(q);
            return new double[]{
                    -term1 + r13 * Math.cos(dum1 / 3),
                    -term1 + r13 * Math.cos((dum1 + 2 * Math.PI) / 3),
                    -term1 + r13 * Math.cos((dum1 + 4 * Math.PI) / 3)
            };
        }
    }

    public static double integrate(java.util.function.Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f.apply(a) + f.apply(b));
        for (int i = 1; i < n; i++) {
            sum += f.apply(a + i * h);
        }
        return sum * h;
    }

    public static double derivative(java.util.function.Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    public static double newtonRaphson(java.util.function.Function<Double, Double> f,
                                       java.util.function.Function<Double, Double> fPrime,
                                       double x0, double tolerance, int maxIterations) {
        double x = x0;
        for (int i = 0; i < maxIterations; i++) {
            double fx = f.apply(x);
            if (Math.abs(fx) < tolerance) {
                return x;
            }
            x = x - fx / fPrime.apply(x);
        }
        throw new ArithmeticException("Newton-Raphson did not converge");
    }

    public static double bisection(java.util.function.Function<Double, Double> f,
                                   double a, double b, double tolerance) {
        if (f.apply(a) * f.apply(b) >= 0) {
            throw new IllegalArgumentException("Function must have opposite signs at endpoints");
        }

        double c = a;
        while ((b - a) >= tolerance) {
            c = (a + b) / 2;
            if (f.apply(c) == 0.0) {
                break;
            } else if (f.apply(c) * f.apply(a) < 0) {
                b = c;
            } else {
                a = c;
            }
        }
        return c;
    }

    public static List<Integer> primeFactorization(int n) {
        List<Integer> factors = new ArrayList<>();
        while (n % 2 == 0) {
            factors.add(2);
            n /= 2;
        }
        for (int i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        if (n > 2) {
            factors.add(n);
        }
        return factors;
    }

    public static List<Integer> sieveOfEratosthenes(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;

        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }

    public static long modularExponentiation(long base, long exponent, long modulus) {
        long result = 1;
        base = base % modulus;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * base) % modulus;
            }
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return result;
    }

    public static long extendedGCD(long a, long b, long[] xy) {
        if (b == 0) {
            xy[0] = 1;
            xy[1] = 0;
            return a;
        }
        long[] xy1 = new long[2];
        long gcd = extendedGCD(b, a % b, xy1);
        xy[0] = xy1[1];
        xy[1] = xy1[0] - (a / b) * xy1[1];
        return gcd;
    }

    public static long modularInverse(long a, long m) {
        long[] xy = new long[2];
        long gcd = extendedGCD(a, m, xy);
        if (gcd != 1) {
            throw new ArithmeticException("Modular inverse does not exist");
        }
        return (xy[0] % m + m) % m;
    }

    public static long chineseRemainderTheorem(long[] remainders, long[] moduli) {
        long product = 1;
        for (long mod : moduli) {
            product *= mod;
        }

        long result = 0;
        for (int i = 0; i < remainders.length; i++) {
            long pp = product / moduli[i];
            result += remainders[i] * modularInverse(pp, moduli[i]) * pp;
        }
        return result % product;
    }

    public static long binomialCoefficient(int n, int k) {
        if (k > n - k) {
            k = n - k;
        }
        long result = 1;
        for (int i = 0; i < k; i++) {
            result *= (n - i);
            result /= (i + 1);
        }
        return result;
    }

    public static long catalan(int n) {
        return binomialCoefficient(2 * n, n) / (n + 1);
    }

    public static double[][] polynomialRegression(double[] x, double[] y, int degree) {
        int n = x.length;
        Matrix X = new Matrix(n, degree + 1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= degree; j++) {
                X.getData()[i][j] = Math.pow(x[i], j);
            }
        }

        Matrix Y = new Matrix(n, 1);
        for (int i = 0; i < n; i++) {
            Y.getData()[i][0] = y[i];
        }

        return X.getData();
    }

    public static double pearsonCorrelation(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        int n = x.length;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        return numerator / denominator;
    }

    public static double[] linearRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{slope, intercept};
    }

    public static double[] fourierTransform(double[] signal) {
        int n = signal.length;
        double[] real = new double[n];
        double[] imag = new double[n];

        for (int k = 0; k < n; k++) {
            for (int t = 0; t < n; t++) {
                double angle = 2 * Math.PI * t * k / n;
                real[k] += signal[t] * Math.cos(angle);
                imag[k] -= signal[t] * Math.sin(angle);
            }
        }

        double[] magnitude = new double[n];
        for (int i = 0; i < n; i++) {
            magnitude[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
        }
        return magnitude;
    }

    public static BigInteger bigFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static BigInteger bigPower(long base, int exponent) {
        return BigInteger.valueOf(base).pow(exponent);
    }

    public static double entropy(double[] probabilities) {
        double entropy = 0;
        for (double p : probabilities) {
            if (p > 0) {
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }

    public static double[][] convolution(double[][] matrix, double[][] kernel) {
        int mRows = matrix.length;
        int mCols = matrix[0].length;
        int kRows = kernel.length;
        int kCols = kernel[0].length;

        int outRows = mRows - kRows + 1;
        int outCols = mCols - kCols + 1;
        double[][] result = new double[outRows][outCols];

        for (int i = 0; i < outRows; i++) {
            for (int j = 0; j < outCols; j++) {
                double sum = 0;
                for (int ki = 0; ki < kRows; ki++) {
                    for (int kj = 0; kj < kCols; kj++) {
                        sum += matrix[i + ki][j + kj] * kernel[ki][kj];
                    }
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Matrix m1 = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix m2 = new Matrix(new double[][]{{5, 6}, {7, 8}});
        System.out.println("Matrix determinant: " + m1.determinant());

        Vector v1 = new Vector(3, 4);
        System.out.println("Vector magnitude: " + v1.magnitude());

        Complex c1 = new Complex(3, 4);
        Complex c2 = new Complex(1, 2);
        System.out.println("Complex multiplication: " + c1.multiply(c2));

        double[] roots = solveQuadratic(1, -3, 2);
        System.out.println("Quadratic roots: " + Arrays.toString(roots));

        List<Integer> primes = sieveOfEratosthenes(50);
        System.out.println("Primes up to 50: " + primes);

        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 5, 4, 5};
        double[] regression = linearRegression(x, y);
        System.out.println("Linear regression (slope, intercept): " + Arrays.toString(regression));
    }
}
