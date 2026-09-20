package rtx.kimiko.api.ui.settings;
import java.awt.Color;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class RenderHelper {
    private static final float REFERENCE_RADIUS = 7.0f;

    private RenderHelper() {
    }

    public static float effectiveCornerRadius(float f, float f2, float f3) {
        float f4 = Math.min(f2, f3) * 0.5f;
        return Math.min(f4, f * RenderHelper.radiusScale());
    }

    public static void drawPanelBg(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        int n = Math.max(0, Math.min(255, Math.round(40.0f * f9)));
        if (n <= 0) {
            return;
        }
        float f10 = RenderHelper.radiusScale();
        float f11 = Math.min(f3, f4) * 0.5f;
        float f12 = Math.min(f11, f5 * f10);
        float f13 = Math.min(f11, f6 * f10);
        float f14 = Math.min(f11, f7 * f10);
        float f15 = Math.min(f11, f8 * f10);
        Render2D.rect(f, f2, f3, f4, f12, f13, f14, f15, new Color(0, 0, 0, n).getRGB());
    }

    public static void drawPanelBg(float f, float f2, float f3, float f4, float f5, float f6) {
        RenderHelper.drawPanelBg(f, f2, f3, f4, f5, f5, f5, f5, f6);
    }

    public static float cornerEdgeInset(float f, float f2) {
        if (f <= f2) {
            return 0.0f;
        }
        float f3 = f - f2;
        return f - (float)Math.sqrt(Math.max(0.0f, f * f - f3 * f3));
    }

    public static void drawDropBackground(float f, float f2, float f3, float f4, float f5) {
        RectUtil.drawClientRectFixedRadius(f, f2, f3, f4, 2.0f, f5, 0.0f);
    }

    private static float radiusScale() {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        if (interfaceModule == null) {
            return 1.0f;
        }
        return interfaceModule.rectCornerRadius.getFloat() / 7.0f;
    }

    public static void drawName(String string, float f, float f2, float f3, float f4) {
        int n = new Color(255, 255, 255, (int)(200.0f * f4)).getRGB();
        RenderHelper.drawScrollingText(string, f + 6.0f, f2 + 5.0f, f3, 6.5f, n);
    }

    private static float easeInOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = f2 * 1.525f;
        if (f < 0.5f) {
            float f4 = 2.0f * f;
            return f4 * f4 * ((f3 + 1.0f) * f4 - f3) * 0.5f;
        }
        float f5 = 2.0f * f - 2.0f;
        return (f5 * f5 * ((f3 + 1.0f) * f5 + f3) + 2.0f) * 0.5f;
    }

    public static void drawScrollingText(String string, float f, float f2, float f3, float f4, int n) {
        float f5 = Fonts.MONTSERRAT_MEDIUM.width(string, f4);
        float f6 = f5 - f3;
        if (f6 <= 1.0f) {
            Fonts.MONTSERRAT_MEDIUM.draw(string, f, f2, f4, n);
            return;
        }
        float f7 = 11.0f;
        float f8 = 1.2f;
        float f9 = Math.max(0.35f, f6 / f7);
        double d = (double)(f8 + f9) * 2.0;
        double d2 = (double)System.nanoTime() / 1.0E9 % d;
        float f10 = d2 < (double)f8 ? 0.0f : (d2 < (double)(f8 + f9) ? f6 * RenderHelper.easeInOutBack((float)((d2 - (double)f8) / (double)f9)) : (d2 < (double)f8 * 2.0 + (double)f9 ? f6 : f6 * (1.0f - RenderHelper.easeInOutBack((float)((d2 - (double)f8 * 2.0 - (double)f9) / (double)f9)))));
        float f11 = Math.max(0.0f, Math.min(1.0f, f10 / 4.0f));
        float f12 = Math.max(0.0f, Math.min(1.0f, (f6 - f10) / 4.0f));
        Fonts.MONTSERRAT_MEDIUM.msdfFade(string, f - f10, f2, f4, n, f, f + f3, 5.0f, f11, f12);
    }

    public static void drawBtn(float f, float f2, float f3, float f4, String string, float f5) {
        RenderHelper.drawBtn(f, f2, f3, f4, string, f5, 0.0f);
    }

    public static void drawBtn(float f, float f2, float f3, float f4, String string, float f5, float f6) {
        RenderHelper.drawPanelBg(f, f2, f3, f4, 3.0f, f5);
        float f7 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.0f);
        Fonts.MONTSERRAT_MEDIUM.draw(string, f + (f3 - f7) * 0.5f + f6, f2 + (f4 - 7.0f) * 0.5f, 6.0f, ClientAccent.accentSoft(220.0f * f5));
    }
}

