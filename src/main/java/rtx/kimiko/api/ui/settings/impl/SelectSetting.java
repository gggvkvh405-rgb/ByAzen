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
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.sounds.Sounds;

public class SelectSetting
implements Setting {
    private static final int MAX_VISIBLE = 7;
    private static final float ITEM_H = 12.0f;
    private static final float PAD = 3.0f;
    private static final float TEXT_SIZE = 5.5f;
    private final rtx.kimiko.api.modules.settings.impl.SelectSetting backend;
    private boolean open;
    private final Decelerate dropAnim;
    private float scroll;
    private float scrollTarget;
    private long lastScrollNs;
    private static final float EDGE_FADE = 4.0f;
    private static final float EDGE_INSET = 1.0f;

    public SelectSetting(rtx.kimiko.api.modules.settings.impl.SelectSetting selectSetting) {
        this.backend = selectSetting;
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

    private static int clamp(int n, int n2, int n3) {
        return n < n2 ? n2 : (n > n3 ? n3 : n);
    }

    private static float clamp(float f, float f2, float f3) {
        return f < f2 ? f2 : (f > f3 ? f3 : f);
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void renderOverlay(DrawContext drawContext, float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        float f7;
        float f8;
        float f9 = this.dropAnim.getOutput().floatValue();
        if (f9 <= 0.01f) {
            return;
        }
        float f10 = f9 * f4;
        List<String> list = this.backend.getOptions();
        float f11 = this.dropWidth();
        float f12 = f + f3 - f11 - 4.0f;
        float f13 = f2 + 16.0f + 1.0f;
        int n = Math.min(list.size(), 7);
        float f14 = (float)n * 12.0f + 6.0f;
        String string = this.backend.getSelected();
        boolean bl = list.size() > 7;
        this.updateScroll();
        RenderHelper.drawDropBackground(f12, f13, f11, f14, f10);
        float f15 = f13 + 1.0f;
        float f16 = f13 + f14 - 1.0f;
        for (int i = 0; i < list.size(); ++i) {
            f8 = f13 + 3.0f + (float)i * 12.0f - this.scroll;
            f7 = f8 + 2.0f;
            f6 = f8 + 12.0f - 2.0f;
            if (f6 <= f15 || f7 >= f16) continue;
            float f17 = f5 = bl ? Math.min(SelectSetting.edgeAlpha(f7, f15, f16), SelectSetting.edgeAlpha(f6, f15, f16)) : 1.0f;
            if (f5 <= 0.01f) continue;
            boolean bl2 = list.get(i).equals(string);
            int n2 = bl2 ? ClientAccent.accentSoft(230.0f * f10 * f5) : new Color(255, 255, 255, (int)(140.0f * f10 * f5)).getRGB();
            Fonts.MONTSERRAT_MEDIUM.draw(list.get(i), f12 + 6.0f, f8 + 2.5f, 5.5f, n2);
            if (!bl2) continue;
            AccentGradient.fillVertical(f12 + f11 - (float)(bl ? 8 : 6), f8 + 6.0f - 1.0f, 2.0f, 2.0f, 1.0f, 200.0f * f10 * f5);
        }
        if (bl) {
            float f18 = this.maxScroll();
            f8 = f12 + f11 - 3.0f;
            f7 = f13 + 2.0f;
            f6 = f14 - 4.0f;
            Render2D.rect(f8, f7, 1.6f, f6, 0.8f, new Color(255, 255, 255, (int)(28.0f * f10)).getRGB());
            f5 = Math.max(10.0f, f6 * 7.0f / (float)list.size());
            float f19 = f7 + (f18 <= 0.0f ? 0.0f : this.scroll / f18 * (f6 - f5));
            Render2D.rect(f8, f19, 1.6f, f5, 0.8f, ClientAccent.accentSoft(190.0f * f10));
        }
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        String string = this.backend.getSelected();
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
        String string = this.backend.getSelected();
        float f6 = Fonts.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f7 = f + f3 - f6 - 4.0f;
        float f8 = f2 + 2.0f;
        if (f4 >= f7 && f4 <= f7 + f6 && f5 >= f8 && f5 <= f8 + 12.0f) {
            this.open = !this.open;
            Sounds.play(this.open ? "settings_open" : "settings_close");
            this.dropAnim.setDirection(this.open ? Direction.FORWARDS : Direction.BACKWARDS);
            this.dropAnim.counter.resetCounter();
            if (this.open) {
                this.initScrollToSelected();
            }
            return true;
        }
        return false;
    }

    private void updateScroll() {
        long l = System.nanoTime();
        float f = this.lastScrollNs == 0L ? 0.0f : Math.min(0.05f, (float)(l - this.lastScrollNs) / 1.0E9f);
        this.lastScrollNs = l;
        float f2 = this.maxScroll();
        this.scrollTarget = SelectSetting.clamp(this.scrollTarget, 0.0f, f2);
        this.scroll += (this.scrollTarget - this.scroll) * (1.0f - (float)Math.exp(-f * 18.0f));
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        this.scroll = SelectSetting.clamp(this.scroll, 0.0f, f2);
    }

    private void initScrollToSelected() {
        float f;
        List<String> list = this.backend.getOptions();
        int n = Math.max(0, list.indexOf(this.backend.getSelected()));
        int n2 = Math.max(0, list.size() - 7);
        int n3 = SelectSetting.clamp(n - 3, 0, n2);
        this.scrollTarget = f = (float)n3 * 12.0f;
        this.scroll = f;
        this.lastScrollNs = 0L;
    }

    private float dropWidth() {
        float f = 0.0f;
        for (String string : this.backend.getOptions()) {
            f = Math.max(f, Fonts.MONTSERRAT_MEDIUM.width(string, 5.5f));
        }
        float f2 = f + 16.0f;
        return this.backend.getOptions().size() > 7 ? f2 + 5.0f : f2;
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
    public boolean scrollOverlay(float f, float f2, float f3, float f4, float f5, double d) {
        if (!this.open) {
            return false;
        }
        float f6 = this.dropWidth();
        float f7 = f + f3 - f6 - 4.0f;
        float f8 = f2 + 16.0f + 1.0f;
        int n = Math.min(this.backend.getOptions().size(), 7);
        float f9 = (float)n * 12.0f + 6.0f;
        if (f4 < f7 || f4 > f7 + f6 || f5 < f8 || f5 > f8 + f9) {
            return false;
        }
        float f10 = this.maxScroll();
        if (f10 > 0.0f) {
            this.scrollTarget = SelectSetting.clamp(this.scrollTarget - (float)d * 12.0f, 0.0f, f10);
        }
        return true;
    }

    @Override
    public boolean clickOverlay(float f, float f2, float f3, float f4, float f5) {
        float f6 = this.dropAnim.getOutput().floatValue();
        if (!this.open || f6 <= 0.1f) {
            return false;
        }
        List<String> list = this.backend.getOptions();
        float f7 = this.dropWidth();
        float f8 = f + f3 - f7 - 4.0f;
        float f9 = f2 + 16.0f + 1.0f;
        int n = Math.min(list.size(), 7);
        float f10 = (float)n * 12.0f + 6.0f;
        if (f4 < f8 || f4 > f8 + f7 || f5 < f9 + 3.0f || f5 > f9 + f10 - 3.0f) {
            return false;
        }
        int n2 = (int)Math.floor((f5 - (f9 + 3.0f) + this.scroll) / 12.0f);
        if (n2 >= 0 && n2 < list.size()) {
            this.backend.setSelected(list.get(n2));
            this.closeOverlay();
        }
        return true;
    }

    private static float edgeAlpha(float f, float f2, float f3) {
        float f4 = SelectSetting.clamp((f - f2) / 4.0f, 0.0f, 1.0f);
        return Math.min(f4, SelectSetting.clamp((f3 - f) / 4.0f, 0.0f, 1.0f));
    }

    @Override
    public boolean hasOverlay() {
        return this.dropAnim.getOutput().floatValue() > 0.01f;
    }

    private float maxScroll() {
        return (float)Math.max(0, this.backend.getOptions().size() - 7) * 12.0f;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + Fonts.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + Fonts.MONTSERRAT_MEDIUM.width(this.backend.getSelected(), 6.0f) + 10.0f + 8.0f;
    }
}

