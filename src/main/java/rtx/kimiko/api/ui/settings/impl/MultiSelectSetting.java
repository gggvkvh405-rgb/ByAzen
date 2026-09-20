package rtx.kimiko.api.ui.settings.impl;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.kimiko.api.ui.settings.RenderHelper;
import rtx.kimiko.api.ui.settings.Setting;
import rtx.kimiko.api.ui.theme.AccentGradient;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.animations.Decelerate;
import rtx.kimiko.utils.animations.Direction;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.sounds.Sounds;

public class MultiSelectSetting
implements Setting {
    private final rtx.kimiko.api.modules.settings.impl.MultiSelectSetting backend;
    private boolean open;
    private final Decelerate dropAnim;

    public MultiSelectSetting(rtx.kimiko.api.modules.settings.impl.MultiSelectSetting multiSelectSetting) {
        this.backend = multiSelectSetting;
        this.dropAnim = new Decelerate();
        this.dropAnim.setMs(200);
        this.dropAnim.setValue(1.0);
        this.dropAnim.setDirection(Direction.BACKWARDS);
        this.dropAnim.counter.setTime(System.currentTimeMillis() - 10000L);
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    private String label() {
        return this.backend.getSelected().size() + " of " + this.backend.getOptions().size();
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void renderOverlay(DrawContext drawContext, float f, float f2, float f3, float f4) {
        float f5 = this.dropAnim.getOutput().floatValue();
        if (f5 <= 0.01f) {
            return;
        }
        float f6 = f5 * f4;
        float f7 = f2 + 16.0f + 1.0f;
        float f8 = 12.0f;
        float f9 = 3.0f;
        float f10 = 0.0f;
        List<String> list = this.backend.getOptions();
        for (String string : list) {
            f10 = Math.max(f10, Fonts.MONTSERRAT_MEDIUM.width(string, 5.5f));
        }
        float f11 = f10 + 20.0f;
        float f12 = (float)list.size() * f8 + f9 * 2.0f;
        String string = this.label();
        float f13 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f14 = f + f3 - f13 - 4.0f + f13 - f11;
        RenderHelper.drawDropBackground(f14, f7, f11, f12, f6);
        for (int i = 0; i < list.size(); ++i) {
            float f15 = f7 + f9 + (float)i * f8;
            String string2 = list.get(i);
            boolean bl = this.backend.isSelected(string2);
            int n = bl ? ClientAccent.accentSoft(230.0f * f6) : new Color(255, 255, 255, (int)(140.0f * f6)).getRGB();
            Fonts.MONTSERRAT_MEDIUM.draw(string2, f14 + 6.0f, f15 + 2.5f, 5.5f, n);
            if (!bl) continue;
            AccentGradient.fillVertical(f14 + f11 - 8.0f, f15 + f8 * 0.5f - 1.0f, 2.0f, 2.0f, 1.0f, 200.0f * f6);
        }
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        String string = this.label();
        float f5 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f6 = f + f3 - f5 - 4.0f;
        float f7 = f2 + 2.0f;
        RenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
        RenderHelper.drawBtn(f6, f7, f5, 12.0f, string, f4);
    }

    @Override
    public boolean isVisible() {
        return this.backend.isVisible();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        String string = this.label();
        float f6 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f7 = f + f3 - f6 - 4.0f;
        float f8 = f2 + 2.0f;
        if (f4 >= f7 && f4 <= f7 + f6 && f5 >= f8 && f5 <= f8 + 12.0f) {
            this.open = !this.open;
            Sounds.play(this.open ? "settings_open" : "settings_close");
            this.dropAnim.setDirection(this.open ? Direction.FORWARDS : Direction.BACKWARDS);
            this.dropAnim.counter.resetCounter();
            return true;
        }
        return false;
    }

    @Override
    public void closeOverlay() {
        if (this.open) {
            this.open = false;
            Sounds.play("settings_close");
            this.dropAnim.setDirection(Direction.BACKWARDS);
            this.dropAnim.counter.resetCounter();
        }
    }

    @Override
    public boolean isOverlayOpen() {
        return this.open;
    }

    @Override
    public boolean clickOverlay(float f, float f2, float f3, float f4, float f5) {
        float f6 = this.dropAnim.getOutput().floatValue();
        if (!this.open || f6 <= 0.1f) {
            return false;
        }
        List<String> list = this.backend.getOptions();
        float f7 = f2 + 16.0f + 1.0f;
        float f8 = 12.0f;
        float f9 = 3.0f;
        float f10 = 0.0f;
        for (String string : list) {
            f10 = Math.max(f10, Fonts.MONTSERRAT_MEDIUM.width(string, 5.5f));
        }
        float f11 = f10 + 20.0f;
        float f12 = (float)list.size() * f8 + f9 * 2.0f;
        float f13 = Fonts.MONTSERRAT_MEDIUM.width(this.label(), 6.0f) + 10.0f;
        float f14 = f + f3 - f13 - 4.0f + f13 - f11;
        if (f4 < f14 || f4 > f14 + f11 || f5 < f7 || f5 > f7 + f12) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            float f15 = f7 + f9 + (float)i * f8;
            if (!(f5 >= f15) || !(f5 <= f15 + f8)) continue;
            this.backend.toggle(list.get(i));
            return true;
        }
        return true;
    }

    @Override
    public boolean hasOverlay() {
        return this.dropAnim.getOutput().floatValue() > 0.01f;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + Fonts.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + Fonts.MONTSERRAT_MEDIUM.width(this.label(), 6.0f) + 10.0f + 8.0f;
    }
}

