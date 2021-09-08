package arekkuusu.gsl.api.helper;

import com.google.common.primitives.Booleans;

public class MathHelper {
    public final static double Epsilon = 1E-5D;

    public static boolean fuzzyEqual(float a, float b) {
        return Math.abs(a - b) <= Epsilon;
    }

    public static boolean fuzzyEqual(double a, double b) {
        return Math.abs(a - b) <= Epsilon;
    }

    public static int fuzzyCompare(double a, double b) {
        return fuzzyEqual(a, b) ? 0 : a < b ? -1 : b < a ? 1 : Booleans.compare(Double.isNaN(a), Double.isNaN(b));
    }

    public static int clamp(int n, int min, int max) {
        return n < min ? min : Math.min(n, max);
    }

    public static double clamp(double n, double min, double max) {
        return n < min ? min : Math.min(n, max);
    }
}
