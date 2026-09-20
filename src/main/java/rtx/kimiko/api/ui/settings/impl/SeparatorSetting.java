package rtx.kimiko.api.ui.settings.impl;
import rtx.kimiko.api.ui.settings.Setting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.render2d.Render2D;

public class SeparatorSetting
implements Setting {
    public static final float HEIGHT = 18.0f;
    private final rtx.kimiko.api.modules.settings.impl.SeparatorSetting backend;

    public SeparatorSetting(rtx.kimiko.api.modules.settings.impl.SeparatorSetting separatorSetting) {
        this.backend = separatorSetting;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 18.0f;
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        String string = this.backend.getName();
        float f7 = Fonts.MONTSERRAT_MEDIUM.width(string, f6 = 6.0f);
        if (f7 > (f5 = Math.max(8.0f, f3 - 24.0f))) {
            f7 = f5;
        }
        float f8 = f + f3 / 2.0f;
        float f9 = f8 - f7 / 2.0f;
        float f10 = f2 + 9.0f - f6 / 2.0f;
        float f11 = f2 + 9.0f;
        float f12 = 6.0f;
        float f13 = 4.0f;
        int n = ClientAccent.accentSoft(210.0f * f4);
        int n2 = ClientAccent.accentSoft(55.0f * f4);
        float f14 = f + f13;
        float f15 = Math.max(0.0f, f9 - f12 - f14);
        float f16 = f9 + f7 + f12;
        float f17 = Math.max(0.0f, f + f3 - f13 - f16);
        if (f15 > 1.0f) {
            Render2D.rect(f14, f11, f15, 1.0f, 0.5f, n2);
        }
        if (f17 > 1.0f) {
            Render2D.rect(f16, f11, f17, 1.0f, 0.5f, n2);
        }
        Fonts.MONTSERRAT_MEDIUM.draw(string, f9, f10, f6, n);
    }

    @Override
    public boolean isVisible() {
        return this.backend.isVisible();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        return false;
    }

    @Override
    public float preferredWidth() {
        return Fonts.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.0f) + 48.0f;
    }
}

