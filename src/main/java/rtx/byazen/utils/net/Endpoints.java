package rtx.byazen.utils.net;
import java.nio.charset.StandardCharsets;

public final class Endpoints {
    private static final byte[] K = new byte[]{107, 41, 87, 61, 18, 78, 113, -125, 29};
    private static final byte[] A = new byte[]{3, 93, 35, 77, 40, 97, 94, -79, 51, 89, 31, 121, 15, 32, 119, 95, -73, 45, 81, 26, 101, 12, 35, 123};
    private static final byte[] B = new byte[]{3, 93, 35, 77, 40, 97, 94, -79, 51, 89, 31, 121, 15, 32, 119, 95, -73, 45, 81, 26, 101, 12, 35, 127, 94, -18, 120, 31, 65, 56, 89, 61};
    private static final byte[] P = new byte[]{28, 90, 109, 18, 61, 124, 95, -79, 43, 69, 27, 101, 4, 60, 122, 65, -71, 46, 89, 24, 101, 13};

    private Endpoints() {
    }

    private static String d(byte[] byArray) {
        byte[] byArray2 = new byte[byArray.length];
        for (int i = 0; i < byArray.length; ++i) {
            byArray2[i] = (byte)(byArray[i] ^ K[i % K.length]);
        }
        return new String(byArray2, StandardCharsets.US_ASCII);
    }

    public static String funtime() {
        return Endpoints.d(B);
    }

    public static String irc() {
        return Endpoints.d(A);
    }

    public static String party() {
        return Endpoints.d(P);
    }
}

