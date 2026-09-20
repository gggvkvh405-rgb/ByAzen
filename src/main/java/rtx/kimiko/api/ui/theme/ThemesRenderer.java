package rtx.kimiko.api.ui.theme;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.api.ui.theme.AccentGradient;
import rtx.kimiko.api.ui.theme.Theme;
import rtx.kimiko.api.ui.theme.ThemeManager;
import rtx.kimiko.mixin.accessor.GuiGraphicsExtractorAccessor;
import rtx.kimiko.utils.animations.Decelerate;
import rtx.kimiko.utils.animations.Direction;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class ThemesRenderer {
    private static final float FADE_OUT_DURATION = 0.15f;
    private static final int ROW_FADE_MS = 380;
    private static final int QUICK_ROW_FADE_MS = 320;
    private static final float SLIDE_PX = 6.0f;
    private static final float CARD_H = 35.0f;
    private static final float CONTENT_Y_OFFSET = 5.0f;
    private static final float CONTENT_HEIGHT = 280.0f;
    private final Map<Theme, Decelerate> selectAnims = new EnumMap<Theme, Decelerate>(Theme.class);
    private final Map<Theme, Float> hoverAnims = new EnumMap<Theme, Float>(Theme.class);
    private final Map<Integer, Decelerate> rowAppearAnims = new HashMap<Integer, Decelerate>();
    private int appearFadeMs = 320;
    private long appearBaseMs;
    private boolean appearInitialFrame;
    private boolean transitioning = false;
    private float fadeOutTime = 0.0f;
    private float scroll;
    private float scrollTarget;
    private float contentH;
    public static final int MAX_BLUR_CARDS = 32;
    private final float[] cardBlurRects = new float[192];
    private int cardBlurCount;
    private float cardBlurMaxPhase;
    private boolean appearComposite;

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public void open() {
        this.open(false);
    }

    public void open(boolean bl) {
        this.rowAppearAnims.clear();
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.appearFadeMs = bl ? 320 : 380;
        this.appearBaseMs = System.currentTimeMillis();
        this.appearInitialFrame = true;
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5) {
        this.render(drawContext, f, f2, f3, f4, 1.0f, f5);
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = 1.0f - (float)Math.exp(-f6 * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * f7;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        if (this.appearComposite && !this.transitioning && this.hasAppearWork()) {
            GuiRenderState guiRenderState = ((GuiGraphicsExtractorAccessor)drawContext).kimiko_getGuiRenderState();
            guiRenderState.createNewRootLayer();
            guiRenderState.applyBlur();
            UI.markCardStratum();
        }
        this.renderCards(drawContext, f, f2, f3, f4, f5, f6, true);
        this.appearInitialFrame = false;
    }

    public boolean click(float f, float f2, float f3, float f4, float f5) {
        if (this.transitioning) {
            return false;
        }
        float f6 = f + 117.0f;
        float f7 = f2 + 5.0f;
        float f8 = f3 - 122.0f;
        float f9 = 3.0f;
        float f10 = 4.0f;
        float f11 = (f8 - f9 - f10 * 2.0f) * 0.5f;
        float f12 = f6 + f10;
        float f13 = f12 + f11 + f9;
        if (f4 < f6 || f4 > f6 + f8 || f5 < f7 || f5 > f7 + 280.0f) {
            return false;
        }
        Theme[] themeArray = Theme.values();
        for (int i = 0; i < themeArray.length; ++i) {
            int n = i % 2;
            int n2 = i / 2;
            float f14 = n == 0 ? f12 : f13;
            float f15 = f7 + f10 + (float)n2 * (35.0f + f9) - this.scroll;
            if (!(f4 >= f14) || !(f4 <= f14 + f11) || !(f5 >= f15) || !(f5 <= f15 + 35.0f)) continue;
            ThemeManager.set(themeArray[i]);
            return true;
        }
        return false;
    }

    private static Decelerate createAnim(int n) {
        Decelerate decelerate = (Decelerate)new Decelerate().setMs(n).setValue(1.0);
        decelerate.setDirection(Direction.BACKWARDS);
        decelerate.counter.setTime(System.currentTimeMillis() - 10000L);
        return decelerate;
    }

    public void scroll(double d, float f) {
        float f2 = Math.max(0.0f, this.contentH - f + 8.0f);
        this.scrollTarget = ThemesRenderer.clamp(this.scrollTarget - (float)d * 18.0f, 0.0f, f2);
    }

    public boolean isTransitioning() {
        return this.transitioning;
    }

    public float cardBlurMaxPhase() {
        return this.cardBlurMaxPhase;
    }

    public float[] cardBlurRects() {
        return this.cardBlurRects;
    }

    public int cardBlurCount() {
        return this.cardBlurCount;
    }

    public float currentScroll() {
        return this.scroll;
    }

    public void resetCardBlur() {
        this.cardBlurCount = 0;
        this.cardBlurMaxPhase = 0.0f;
    }

    public void setAppearComposite(boolean bl) {
        this.appearComposite = bl;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadeOutTime = 0.0f;
    }

    public void resetScroll() {
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
    }

    private boolean hasAppearWork() {
        if (this.appearInitialFrame) {
            return true;
        }
        if (Math.abs(this.scrollTarget - this.scroll) > 0.05f) {
            return true;
        }
        for (Decelerate decelerate : this.rowAppearAnims.values()) {
            if (!(decelerate.getOutput().floatValue() < 0.999f)) continue;
            return true;
        }
        return false;
    }

    private void renderScrollBar(float f, float f2, float f3, float f4, float f5, float f6) {
        if (f5 <= 0.5f) {
            return;
        }
        float f7 = 3.0f;
        float f8 = f + f3 + 0.25f;
        float f9 = f2 + f7;
        float f10 = f4 - f7 * 2.0f;
        float f11 = ThemesRenderer.clamp(f4 / Math.max(this.contentH, f4), 0.0f, 1.0f);
        float f12 = ThemesRenderer.clamp(f10 * f11, 12.0f, f10);
        float f13 = Math.max(0.0f, f10 - f12);
        float f14 = ThemesRenderer.clamp(this.scroll / Math.max(f5, 1.0f), 0.0f, 1.0f);
        float f15 = f9 + f13 * f14;
        Render2D.rect(f8, f9, 1.25f, f10, 1.0f, ThemeManager.rgba(0xFFFFFF, 18.0f * f6));
        AccentGradient.fillVertical(f8, f15, 1.25f, f12, 1.0f, 165.0f * f6);
    }

    public boolean isFadeOutDone() {
        return this.transitioning && this.fadeOutTime <= 0.0f;
    }

    private void renderCards(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, boolean bl) {
        int n;
        float f7 = f + 117.0f;
        float f8 = f2 + 5.0f;
        float f9 = f3 - 122.0f;
        float f10 = 3.0f;
        float f11 = 4.0f;
        float f12 = 4.0f;
        float f13 = (f9 - f10 - f11 * 2.0f) * 0.5f;
        float f14 = f7 + f11;
        float f15 = f14 + f13 + f10;
        float f16 = 280.0f;
        Render2D.pushScissor(drawContext, f7, f8, f9, f16);
        float f17 = Position.mouseX();
        float f18 = Position.mouseY();
        float f19 = (1.0f - f5) * 8.0f;
        boolean bl2 = UI.isOpen();
        boolean bl3 = bl2 && f17 >= f7 && f17 <= f7 + f9 && f18 >= f8 && f18 <= f8 + f16;
        float f20 = bl2 ? 1.0f - (float)Math.exp(-f6 * 16.0f) : 0.0f;
        Theme[] themeArray = Theme.values();
        for (n = 0; n < themeArray.length; ++n) {
            float f21;
            float f22;
            float f23;
            Theme theme2 = themeArray[n];
            int n2 = n % 2;
            int n3 = n / 2;
            float f24 = n2 == 0 ? f14 : f15;
            float f25 = f8 + f11 + (float)n3 * (35.0f + f10) + f19 - this.scroll;
            boolean bl4 = bl3 && f17 >= f24 && f17 <= f24 + f13 && f18 >= f25 && f18 <= f25 + 35.0f;
            float f26 = this.hoverAnims.getOrDefault((Object)theme2, Float.valueOf(0.0f)).floatValue();
            f26 += ((bl4 ? 1.0f : 0.0f) - f26) * f20;
            this.hoverAnims.put(theme2, Float.valueOf(f26));
            if (f25 + 35.0f < f8 - 5.0f || f25 > f8 + f16 + 5.0f) continue;
            float f27 = f23 = bl ? this.rowAppear(n3) : 1.0f;
            if (f23 < 0.001f) continue;
            float f28 = Math.min(1.0f, f23 / 0.6f);
            float f29 = f28 * f28;
            float f30 = f23 < 0.6f ? 0.0f : (f23 - 0.6f) / 0.4f;
            float f31 = 1.0f - f30 * f30 * (3.0f - 2.0f * f30);
            f25 += (1.0f - f28) * 6.0f;
            boolean bl5 = f23 < 0.999f;
            boolean bl6 = this.appearComposite && bl5;
            float f32 = f4 * (!bl5 || bl6 ? 1.0f : f29);
            if (bl5) {
                float f33 = 0.85f + 0.15f * f28;
                if (bl6 && this.cardBlurCount < 32) {
                    int n4 = this.cardBlurCount * 6;
                    this.cardBlurRects[n4] = f24;
                    this.cardBlurRects[n4 + 1] = f25;
                    this.cardBlurRects[n4 + 2] = f13;
                    this.cardBlurRects[n4 + 3] = 35.0f;
                    this.cardBlurRects[n4 + 4] = f29;
                    this.cardBlurRects[n4 + 5] = f31;
                    ++this.cardBlurCount;
                    this.cardBlurMaxPhase = Math.max(this.cardBlurMaxPhase, f31);
                }
                f22 = f24 + f13 * 0.5f;
                f21 = f25 + 17.5f;
                drawContext.getMatrices().pushMatrix();
                drawContext.getMatrices().translate(f22, f21);
                drawContext.getMatrices().scale(f33, f33);
                drawContext.getMatrices().translate(-f22, -f21);
            }
            Decelerate decelerate = this.selectAnims.computeIfAbsent(theme2, theme -> ThemesRenderer.createAnim(220));
            decelerate.setDirection(theme2 == ThemeManager.current() ? Direction.FORWARDS : Direction.BACKWARDS);
            f22 = decelerate.getOutput().floatValue();
            Render2D.rect(f24, f25, f13, 35.0f, f12, ThemeManager.rgba(0, (40.0f + 18.0f * f26) * f32));
            if (f26 > 0.01f) {
                Render2D.rect(f24, f25, f13, 35.0f, f12, ThemeManager.rgba(0xFFFFFF, 8.0f * f26 * f32));
            }
            if ((f21 = (14.0f + 12.0f * f26) * (1.0f - f22) * f32) > 0.5f) {
                Render2D.outline(f24, f25, f13, 35.0f, f12, f12, f12, f12, 0.6f, ThemeManager.rgba(0xFFFFFF, f21));
            }
            if (f22 > 0.01f) {
                int n5 = theme2.gradientA();
                int n6 = theme2.gradientB();
                int n7 = ThemeManager.mix(ThemeManager.rgba(n5, 255.0f), ThemeManager.rgba(n6, 255.0f), 0.5f) & 0xFFFFFF;
                Render2D.rect(f24 + 2.0f, f25 + 3.0f, 1.0f, 29.0f, 6.0f, ThemeManager.rgba(n5, 185.0f * f22 * f32), ThemeManager.rgba(n5, 185.0f * f22 * f32), ThemeManager.rgba(n6, 185.0f * f22 * f32), ThemeManager.rgba(n6, 185.0f * f22 * f32));
                Render2D.outline(f24, f25, f13, 35.0f, f12, f12, f12, f12, 0.7f, ThemeManager.rgba(n5, 110.0f * f22 * f32), ThemeManager.rgba(n7, 110.0f * f22 * f32), ThemeManager.rgba(n6, 110.0f * f22 * f32), ThemeManager.rgba(n7, 110.0f * f22 * f32));
            }
            float f34 = f24 + 8.0f + f22 * 3.0f;
            Fonts.MONTSERRAT_MEDIUM.draw(theme2.displayName(), f34, f25 + 6.0f, 7.0f, ThemeManager.rgba(0xFFFFFF, (200.0f + 35.0f * f26 + 20.0f * f22) * f32));
            if (f22 > 0.01f) {
                float f35 = f24 + f13 - 9.0f;
                Render2D.rect(f35, f25 + 8.5f, 3.0f, 3.0f, 1.5f, ThemeManager.rgba(theme2.accentBrightRgb(), 220.0f * f22 * f32));
            }
            int[] nArray = theme2.palette().length >= 2 ? theme2.palette() : theme2.shades();
            float f36 = 9.0f;
            float f37 = 3.0f;
            float f38 = f25 + 35.0f - f36 - 7.0f;
            int n8 = Math.min(nArray.length, 5);
            for (int i = 0; i < n8; ++i) {
                float f39 = f24 + 8.0f + (float)i * (f36 + f37);
                Render2D.rect(f39, f38, f36, f36, 3.0f, ThemeManager.rgba(nArray[i], 235.0f * f32));
            }
            if (!bl5) continue;
            drawContext.getMatrices().popMatrix();
        }
        n = (themeArray.length + 1) / 2;
        this.contentH = (float)n * (35.0f + f10) - f10;
        float f40 = Math.max(0.0f, this.contentH - f16 + f11 * 2.0f);
        this.scrollTarget = ThemesRenderer.clamp(this.scrollTarget, 0.0f, f40);
        this.scroll = ThemesRenderer.clamp(this.scroll, 0.0f, f40);
        Render2D.popScissor(drawContext);
        this.renderScrollBar(f7, f8, f9, f16, f40, f4);
    }

    public void beginFadeOut() {
        this.fadeOutTime = 0.15f;
        this.transitioning = true;
    }

    private float rowAppear(int n) {
        Decelerate decelerate = this.rowAppearAnims.get(n);
        if (decelerate == null) {
            long l = this.appearInitialFrame ? this.appearBaseMs : System.currentTimeMillis();
            decelerate = (Decelerate)new Decelerate().setMs(this.appearFadeMs).setValue(1.0);
            decelerate.counter.setTime(l);
            this.rowAppearAnims.put(n, decelerate);
        }
        float f = decelerate.getOutput().floatValue();
        return Math.max(0.0f, Math.min(1.0f, f));
    }
}

