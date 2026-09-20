package rtx.byazen.utils.color;

import java.awt.Color;

public final class RainbowLut {
    public static int sample(int hueDegrees, float saturation, float brightness) {
        float hue = ((hueDegrees % 360) + 360) % 360 / 360.0f;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int[] table(float saturation, float brightness) {
        int[] arr = new int[360];
        for (int i = 0; i < 360; i++) {
            arr[i] = sample(i, saturation, brightness);
        }
        return arr;
    }
}