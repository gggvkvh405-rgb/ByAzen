package rtx.byazen.api.mods.waveycapes.versionless.util;
import java.util.function.IntPredicate;

public final class Mth {
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = 1.5707964f;
    public static final float TWO_PI = (float)Math.PI * 2;
    public static final float DEG_TO_RAD = (float)Math.PI / 180;
    public static final float RAD_TO_DEG = 57.295776f;
    public static final float EPSILON = 1.0E-5f;
    public static final float SQRT_OF_TWO = 1.4142135f;
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    private Mth() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double)i / 256.0;
            double d2 = Math.asin(d);
            Mth.COS_TAB[i] = Math.cos(d2);
            Mth.ASIN_TAB[i] = d2;
        }
    }

    public static double length(double d, double d2) {
        return Math.sqrt(Mth.lengthSquared(d, d2));
    }

    public static double length(double d, double d2, double d3) {
        return Math.sqrt(Mth.lengthSquared(d, d2, d3));
    }

    public static float abs(float f) {
        return Math.abs(f);
    }

    public static int abs(int n) {
        return Math.abs(n);
    }

    public static float sin(float f) {
        return (float)Math.sin(f);
    }

    public static float cos(float f) {
        return (float)Math.cos(f);
    }

    public static double atan2(double d, double d2) {
        double d3;
        boolean bl;
        boolean bl2;
        boolean bl3;
        double d4 = d2 * d2 + d * d;
        if (Double.isNaN(d4)) {
            return Double.NaN;
        }
        boolean bl4 = bl3 = d < 0.0;
        if (bl3) {
            d = -d;
        }
        boolean bl5 = bl2 = d2 < 0.0;
        if (bl2) {
            d2 = -d2;
        }
        boolean bl6 = bl = d > d2;
        if (bl) {
            d3 = d2;
            d2 = d;
            d = d3;
        }
        d3 = Mth.fastInvSqrt(d4);
        d2 *= d3;
        double d5 = FRAC_BIAS + (d *= d3);
        int n = (int)Double.doubleToRawLongBits(d5);
        double d6 = ASIN_TAB[n];
        double d7 = COS_TAB[n];
        double d8 = d5 - FRAC_BIAS;
        double d9 = d * d7 - d2 * d8;
        double d10 = (6.0 + d9 * d9) * d9 * 0.16666666666666666;
        double d11 = d6 + d10;
        if (bl) {
            d11 = 1.5707963267948966 - d11;
        }
        if (bl2) {
            d11 = Math.PI - d11;
        }
        if (bl3) {
            d11 = -d11;
        }
        return d11;
    }

    public static float sqrt(float f) {
        return (float)Math.sqrt(f);
    }

    public static int floor(double d) {
        int n = (int)d;
        return d < (double)n ? n - 1 : n;
    }

    public static int floor(float f) {
        int n = (int)f;
        return f < (float)n ? n - 1 : n;
    }

    public static int ceil(double d) {
        int n = (int)d;
        return d > (double)n ? n + 1 : n;
    }

    public static int ceil(float f) {
        int n = (int)f;
        return f > (float)n ? n + 1 : n;
    }

    public static double getDouble(String string, double d) {
        try {
            return Double.parseDouble(string);
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    public static double getDouble(String string, double d, double d2) {
        return Math.max(d2, Mth.getDouble(string, d));
    }

    public static byte clamp(byte by, byte by2, byte by3) {
        if (by < by2) {
            return by2;
        }
        if (by > by3) {
            return by3;
        }
        return by;
    }

    public static int clamp(int n, int n2, int n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }

    public static double clamp(double d, double d2, double d3) {
        if (d < d2) {
            return d2;
        }
        if (d > d3) {
            return d3;
        }
        return d;
    }

    public static long clamp(long l, long l2, long l3) {
        if (l < l2) {
            return l2;
        }
        if (l > l3) {
            return l3;
        }
        return l;
    }

    public static float clamp(float f, float f2, float f3) {
        if (f < f2) {
            return f2;
        }
        if (f > f3) {
            return f3;
        }
        return f;
    }

    public static float map(float f, float f2, float f3, float f4, float f5) {
        return Mth.lerp(Mth.inverseLerp(f, f2, f3), f4, f5);
    }

    public static double map(double d, double d2, double d3, double d4, double d5) {
        return Mth.lerp(Mth.inverseLerp(d, d2, d3), d4, d5);
    }

    public static int sign(double d) {
        if (d == 0.0) {
            return 0;
        }
        return d > 0.0 ? 1 : -1;
    }

    public static boolean equal(double d, double d2) {
        return Math.abs(d2 - d) < (double)1.0E-5f;
    }

    public static boolean equal(float f, float f2) {
        return Math.abs(f2 - f) < 1.0E-5f;
    }

    public static int color(float f, float f2, float f3) {
        return Mth.color(Mth.floor(f * 255.0f), Mth.floor(f2 * 255.0f), Mth.floor(f3 * 255.0f));
    }

    public static int color(int n, int n2, int n3) {
        int n4 = n;
        n4 = (n4 << 8) + n2;
        n4 = (n4 << 8) + n3;
        return n4;
    }

    public static int binarySearch(int n, int n2, IntPredicate intPredicate) {
        int n3 = n2 - n;
        while (n3 > 0) {
            int n4 = n3 / 2;
            int n5 = n + n4;
            if (intPredicate.test(n5)) {
                n3 = n4;
                continue;
            }
            n = n5 + 1;
            n3 -= n4 + 1;
        }
        return n;
    }

    public static float truncate(float f, float f2) {
        float f3 = (float)Math.pow(10.0, f2);
        return (float)((int)(f * f3)) / f3;
    }

    public static int square(int n) {
        return n * n;
    }

    public static long square(long l) {
        return l * l;
    }

    public static float square(float f) {
        return f * f;
    }

    public static double square(double d) {
        return d * d;
    }

    public static int smallestEncompassingPowerOfTwo(int n) {
        int n2 = n - 1;
        n2 |= n2 >> 1;
        n2 |= n2 >> 2;
        n2 |= n2 >> 4;
        n2 |= n2 >> 8;
        n2 |= n2 >> 16;
        return n2 + 1;
    }

    public static int log2(int n) {
        return Mth.ceillog2(n) - (Mth.isPowerOfTwo(n) ? 0 : 1);
    }

    public static double frac(double d) {
        return d - (double)Mth.lfloor(d);
    }

    public static float frac(float f) {
        return f - (float)Mth.floor(f);
    }

    public static double average(long[] lArray) {
        long l = 0L;
        for (long l2 : lArray) {
            l += l2;
        }
        return l / (long)lArray.length;
    }

    public static int ceillog2(int n) {
        n = Mth.isPowerOfTwo(n) ? n : Mth.smallestEncompassingPowerOfTwo(n);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)n * 125613361L >> 27) & 0x1F];
    }

    public static float cube(float f) {
        return f * f * f;
    }

    public static long getSeed(int n, int n2, int n3) {
        long l = (long)(n * 3129871) ^ (long)n3 * 116129781L ^ (long)n2;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static double lerp(double d, double d2, double d3) {
        return d2 + d * (d3 - d2);
    }

    public static float lerp(float f, float f2, float f3) {
        return f2 + f * (f3 - f2);
    }

    public static float approach(float f, float f2, float f3) {
        f3 = Mth.abs(f3);
        if (f < f2) {
            return Mth.clamp(f + f3, f, f2);
        }
        return Mth.clamp(f - f3, f2, f);
    }

    public static boolean isPowerOfTwo(int n) {
        return n != 0 && (n & n - 1) == 0;
    }

    public static double[] binBiModalNormalDistribution(double d, double d2, double d3, double d4, double d5, double d6, int n, int n2) {
        double[] dArray = new double[n2 - n + 1];
        int n3 = 0;
        for (int i = n; i <= n2; ++i) {
            dArray[n3] = Math.max(0.0, d * StrictMath.exp(-((double)i - d3) * ((double)i - d3) / 2.0 * d2 * d2) + d4 * StrictMath.exp(-((double)i - d6) * ((double)i - d6) / 2.0 * d5 * d5));
            ++n3;
        }
        return dArray;
    }

    public static double lengthSquared(double d, double d2, double d3) {
        return d * d + d2 * d2 + d3 * d3;
    }

    public static double lengthSquared(double d, double d2) {
        return d * d + d2 * d2;
    }

    public static double smoothstep(double d) {
        return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
    }

    public static double wrapDegrees(double d) {
        double d2 = d % 360.0;
        if (d2 >= 180.0) {
            d2 -= 360.0;
        }
        if (d2 < -180.0) {
            d2 += 360.0;
        }
        return d2;
    }

    public static float wrapDegrees(float f) {
        float f2 = f % 360.0f;
        if (f2 >= 180.0f) {
            f2 -= 360.0f;
        }
        if (f2 < -180.0f) {
            f2 += 360.0f;
        }
        return f2;
    }

    public static int wrapDegrees(int n) {
        int n2 = n % 360;
        if (n2 >= 180) {
            n2 -= 360;
        }
        if (n2 < -180) {
            n2 += 360;
        }
        return n2;
    }

    public static int intFloorDiv(int n, int n2) {
        return Math.floorDiv(n, n2);
    }

    public static boolean isDivisionInteger(int n, int n2) {
        return n / n2 * n2 == n;
    }

    public static float degreesDifferenceAbs(float f, float f2) {
        return Mth.abs(Mth.degreesDifference(f, f2));
    }

    public static float rotateIfNecessary(float f, float f2, float f3) {
        float f4 = Mth.degreesDifference(f, f2);
        float f5 = Mth.clamp(f4, -f3, f3);
        return f2 - f5;
    }

    public static float approachDegrees(float f, float f2, float f3) {
        float f4 = Mth.degreesDifference(f, f2);
        return Mth.approach(f, f + f4, f3);
    }

    public static int colorMultiply(int n, float f, float f2, float f3) {
        int n2 = (n & 0xFF0000) >> 16;
        int n3 = (n & 0xFF00) >> 8;
        int n4 = (n & 0xFF) >> 0;
        int n5 = (int)((float)n2 * f);
        int n6 = (int)((float)n3 * f2);
        int n7 = (int)((float)n4 * f3);
        return n & 0xFF000000 | n5 << 16 | n6 << 8 | n7;
    }

    public static int colorMultiply(int n, int n2) {
        int n3 = (n & 0xFF0000) >> 16;
        int n4 = (n2 & 0xFF0000) >> 16;
        int n5 = (n & 0xFF00) >> 8;
        int n6 = (n2 & 0xFF00) >> 8;
        int n7 = (n & 0xFF) >> 0;
        int n8 = (n2 & 0xFF) >> 0;
        int n9 = (int)((float)(n3 * n4) / 255.0f);
        int n10 = (int)((float)(n5 * n6) / 255.0f);
        int n11 = (int)((float)(n7 * n8) / 255.0f);
        return n & 0xFF000000 | n9 << 16 | n10 << 8 | n11;
    }

    public static double inverseLerp(double d, double d2, double d3) {
        return (d - d2) / (d3 - d2);
    }

    public static float inverseLerp(float f, float f2, float f3) {
        return (f - f2) / (f3 - f2);
    }

    public static double fastInvSqrt(double d) {
        double d2 = 0.5 * d;
        long l = Double.doubleToRawLongBits(d);
        l = 6910469410427058090L - (l >> 1);
        d = Double.longBitsToDouble(l);
        d *= 1.5 - d2 * d * d;
        return d;
    }

    public static float fastInvSqrt(float f) {
        float f2 = 0.5f * f;
        int n = Float.floatToIntBits(f);
        n = 1597463007 - (n >> 1);
        f = Float.intBitsToFloat(n);
        f *= 1.5f - f2 * f * f;
        return f;
    }

    public static long murmurHash3Mixer(long l) {
        l ^= l >>> 33;
        l *= -49064778989728563L;
        l ^= l >>> 33;
        l *= -4265267296055464877L;
        l ^= l >>> 33;
        return l;
    }

    public static int murmurHash3Mixer(int n) {
        n ^= n >>> 16;
        n *= -2048144789;
        n ^= n >>> 13;
        n *= -1028477387;
        n ^= n >>> 16;
        return n;
    }

    public static double[] binLogDistribution(double d, double d2, int n, int n2) {
        double[] dArray = new double[n2 - n + 1];
        int n3 = 0;
        for (int i = n; i <= n2; ++i) {
            dArray[n3] = Math.max(d * StrictMath.log(i) + d2, 0.0);
            ++n3;
        }
        return dArray;
    }

    public static float triangleWave(float f, float f2) {
        return (Math.abs(f % f2 - f2 * 0.5f) - f2 * 0.25f) / f2 * 0.25f;
    }

    public static float degreesDifference(float f, float f2) {
        return Mth.wrapDegrees(f2 - f);
    }

    public static float positiveModulo(float f, float f2) {
        return (f % f2 + f2) % f2;
    }

    public static double positiveModulo(double d, double d2) {
        return (d % d2 + d2) % d2;
    }

    public static int positiveModulo(int n, int n2) {
        return Math.floorMod(n, n2);
    }

    public static double smoothstepDerivative(double d) {
        return 30.0 * d * d * (d - 1.0) * (d - 1.0);
    }

    public static float diffuseLight(float f, float f2, float f3) {
        return Math.min(f * f * 0.6f + f2 * f2 * (3.0f + f2) / 4.0f + f3 * f3 * 0.8f, 1.0f);
    }

    public static int positiveCeilDiv(int n, int n2) {
        return -Math.floorDiv(-n, n2);
    }

    public static float clampedLerp(float f, float f2, float f3) {
        if (f3 < 0.0f) {
            return f;
        }
        if (f3 > 1.0f) {
            return f2;
        }
        return Mth.lerp(f3, f, f2);
    }

    public static double clampedLerp(double d, double d2, double d3) {
        if (d3 < 0.0) {
            return d;
        }
        if (d3 > 1.0) {
            return d2;
        }
        return Mth.lerp(d3, d, d2);
    }

    public static double[] cumulativeSum(double ... dArray) {
        int n;
        double d = 0.0;
        for (double d2 : dArray) {
            d += d2;
        }
        for (n = 0; n < dArray.length; ++n) {
            dArray[n] = dArray[n] / d;
        }
        for (n = 0; n < dArray.length; ++n) {
            dArray[n] = (n == 0 ? 0.0 : dArray[n - 1]) + dArray[n];
        }
        return dArray;
    }

    public static float fastInvCubeRoot(float f) {
        int n = Float.floatToIntBits(f);
        n = 1419967116 - n / 3;
        float f2 = Float.intBitsToFloat(n);
        f2 = 0.6666667f * f2 + 0.33333334f * f2 * f2 * f;
        f2 = 0.6666667f * f2 + 0.33333334f * f2 * f2 * f;
        return f2;
    }

    public static int roundToward(int n, int n2) {
        return Mth.positiveCeilDiv(n, n2) * n2;
    }

    public static double[] binNormalDistribution(double d, double d2, double d3, int n, int n2) {
        double[] dArray = new double[n2 - n + 1];
        int n3 = 0;
        for (int i = n; i <= n2; ++i) {
            dArray[n3] = Math.max(0.0, d * StrictMath.exp(-((double)i - d3) * ((double)i - d3) / 2.0 * d2 * d2));
            ++n3;
        }
        return dArray;
    }

    public static int fastFloor(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static float rotLerp(float f, float f2, float f3) {
        return f2 + f * Mth.wrapDegrees(f3 - f2);
    }

    public static float catmullrom(float f, float f2, float f3, float f4, float f5) {
        return 0.5f * (2.0f * f3 + (f4 - f2) * f + (2.0f * f2 - 5.0f * f3 + 4.0f * f4 - f5) * f * f + (3.0f * f3 - f2 - 3.0f * f4 + f5) * f * f * f);
    }

    public static int quantize(double d, int n) {
        return Mth.floor(d / (double)n) * n;
    }

    public static float rotlerp(float f, float f2, float f3) {
        float f4;
        for (f4 = f2 - f; f4 < -180.0f; f4 += 360.0f) {
        }
        while (f4 >= 180.0f) {
            f4 -= 360.0f;
        }
        return f + f3 * f4;
    }

    public static int absFloor(double d) {
        return (int)(d >= 0.0 ? d : -d + 1.0);
    }

    public static float clampedMap(float f, float f2, float f3, float f4, float f5) {
        return Mth.clampedLerp(f4, f5, Mth.inverseLerp(f, f2, f3));
    }

    public static double clampedMap(double d, double d2, double d3, double d4, double d5) {
        return Mth.clampedLerp(d4, d5, Mth.inverseLerp(d, d2, d3));
    }

    public static long lfloor(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }

    public static double lerp3(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10, double d11) {
        return Mth.lerp(d3, Mth.lerp2(d, d2, d4, d5, d6, d7), Mth.lerp2(d, d2, d8, d9, d10, d11));
    }

    public static double absMax(double d, double d2) {
        if (d < 0.0) {
            d = -d;
        }
        if (d2 < 0.0) {
            d2 = -d2;
        }
        return d > d2 ? d : d2;
    }

    public static int hsvToRgb(float f, float f2, float f3) {
        int n = (int)(f * 6.0f) % 6;
        float f4 = f * 6.0f - (float)n;
        float f5 = f3 * (1.0f - f2);
        float f6 = f3 * (1.0f - f4 * f2);
        float f7 = f3 * (1.0f - (1.0f - f4) * f2);
        switch (n) {
            case 0: {
                float f8 = f3;
                float f9 = f7;
                float f10 = f5;
                int n2 = Mth.clamp((int)(f8 * 255.0f), 0, 255);
                int n3 = Mth.clamp((int)(f9 * 255.0f), 0, 255);
                int n4 = Mth.clamp((int)(f10 * 255.0f), 0, 255);
                return n2 << 16 | n3 << 8 | n4;
            }
            case 1: {
                float f11 = f6;
                float f12 = f3;
                float f13 = f5;
                int n5 = Mth.clamp((int)(f11 * 255.0f), 0, 255);
                int n6 = Mth.clamp((int)(f12 * 255.0f), 0, 255);
                int n7 = Mth.clamp((int)(f13 * 255.0f), 0, 255);
                return n5 << 16 | n6 << 8 | n7;
            }
            case 2: {
                float f14 = f5;
                float f15 = f3;
                float f16 = f7;
                int n8 = Mth.clamp((int)(f14 * 255.0f), 0, 255);
                int n9 = Mth.clamp((int)(f15 * 255.0f), 0, 255);
                int n10 = Mth.clamp((int)(f16 * 255.0f), 0, 255);
                return n8 << 16 | n9 << 8 | n10;
            }
            case 3: {
                float f17 = f5;
                float f18 = f6;
                float f19 = f3;
                int n11 = Mth.clamp((int)(f17 * 255.0f), 0, 255);
                int n12 = Mth.clamp((int)(f18 * 255.0f), 0, 255);
                int n13 = Mth.clamp((int)(f19 * 255.0f), 0, 255);
                return n11 << 16 | n12 << 8 | n13;
            }
            case 4: {
                float f20 = f7;
                float f21 = f5;
                float f22 = f3;
                int n14 = Mth.clamp((int)(f20 * 255.0f), 0, 255);
                int n15 = Mth.clamp((int)(f21 * 255.0f), 0, 255);
                int n16 = Mth.clamp((int)(f22 * 255.0f), 0, 255);
                return n14 << 16 | n15 << 8 | n16;
            }
            case 5: {
                float f23 = f3;
                float f24 = f5;
                float f25 = f6;
                int n17 = Mth.clamp((int)(f23 * 255.0f), 0, 255);
                int n18 = Mth.clamp((int)(f24 * 255.0f), 0, 255);
                int n19 = Mth.clamp((int)(f25 * 255.0f), 0, 255);
                return n17 << 16 | n18 << 8 | n19;
            }
        }
        throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + f2 + ", " + f3);
    }

    public static float rotWrap(double d) {
        while (d >= 180.0) {
            d -= 360.0;
        }
        while (d < -180.0) {
            d += 360.0;
        }
        return (float)d;
    }

    public static double lerp2(double d, double d2, double d3, double d4, double d5, double d6) {
        return Mth.lerp(d2, Mth.lerp(d, d3, d4), Mth.lerp(d, d5, d6));
    }
}

