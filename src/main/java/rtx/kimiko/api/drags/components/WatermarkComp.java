package rtx.kimiko.api.drags.components;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.impl.Interface.WatermarkModule;
import rtx.kimiko.api.ui.settings.Setting;
import rtx.kimiko.api.ui.settings.impl.BoolSetting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class WatermarkComp
extends Draggable {
    private static final float PAD_X = 9.0f;
    private static final float PAD_Y = 6.0f;
    private static final float FONT = 8.0f;
    private static final float BRAND_FONT_SIZE = 11.0f;
    private static final float H = 20.0f;
    private static final String BRAND_FONT = "byazen";
    private static final String INFO_FONT = "montserrat-medium";
    private static final String[] BRAND_GLYPHS = new String[]{"B", "y", "A", "z", "e", "n"};
    private static final String SEPARATOR = " | ";
    private static final float TEXT_Y_LIFT = 1.0f;
    private static final float MIN_TEXT_ALPHA = 0.003921569f;
    private static final long INDEXED_GRADIENT_PERIOD_MS = 1200L;
    private static final float INDEXED_GRADIENT_STEP = 15.0f;
    private static final int FALLBACK_BRAND_COLOR = -2234369;
    private static final float DOT_RADIUS = 1.0f;
    private static final float DOT_GAP = 6.0f;
    private static final String FALLBACK_NAME = "Player";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private final SmoothAnimation visibility = new SmoothAnimation();
    private boolean lastTargetVisible;
    private float currentWidth = 80.0f;
    private boolean staticMetricsReady;
    private float brandWidth;
    private final float[] brandGlyphWidths = new float[BRAND_GLYPHS.length];
    private float separatorWidth;
    private String cachedName = "";
    private float nameWidth;
    private String timeText = "";
    private float timeWidth;
    private float discordBlockWidth;

    public WatermarkComp() {
        super("watermark", 5.0f, 5.0f);
        this.visibility.set(0.0);
    }

    @Override
    public String displayName() {
        return "Watermark";
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return 20.0f;
    }

    @Override
    public boolean isInteractive() {
        return this.shouldShow();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        WatermarkModule watermarkModule = ModuleManager.get().get(WatermarkModule.class);
        if (watermarkModule != null) {
            list.add(new BoolSetting(watermarkModule.showNick));
            list.add(new BoolSetting(watermarkModule.showTime));
        }
        return list;
    }

    private static int indexedPaletteColor(int[] nArray, float f) {
        int n = nArray.length;
        if (n <= 1) {
            return nArray[0];
        }
        float f2 = WatermarkComp.normalizedCycle(f) / 360.0f;
        float f3 = f2 < 0.5f ? f2 * 2.0f : (1.0f - f2) * 2.0f;
        float f4 = f3 * (float)(n - 1);
        int n2 = (int)f4;
        if (n2 > n - 2) {
            n2 = n - 2;
        }
        return ColorUtil.lerpColor(nArray[n2], nArray[n2 + 1], f4 - (float)n2);
    }

    private static int[] watermarkPalette(InterfaceModule interfaceModule) {
        int[] nArray = ClientAccent.currentPalette();
        if (nArray == null || nArray.length == 0) {
            int n = ColorUtil.lerpColor(-2234369, -1, 0.18f);
            return new int[]{n, ColorUtil.lerpColor(n, -1, 0.46f)};
        }
        int[] nArray2 = new int[nArray.length];
        for (int i = 0; i < nArray.length; ++i) {
            nArray2[i] = ColorUtil.lerpColor(WatermarkComp.opaque(nArray[i]), -1, 0.18f);
        }
        return nArray2;
    }

    private static void drawIndexedGradientGlyph(String string, String string2, float f, float f2, float f3, int n, int[] nArray, float f4, float f5) {
        float f6 = Math.max(0.0f, Math.min(1.0f, f4));
        if (f6 <= 0.003921569f || string2 == null || string2.isEmpty()) {
            return;
        }
        int n2 = ColorUtil.multAlpha(WatermarkComp.indexedPaletteColor(nArray, f5 + (float)n * 15.0f), f6);
        Render2D.msdfText(string, string2, f, f2, f3, n2);
    }

    private float drawInfoBlock(float f, float f2, float f3, float f4, float f5, int[] nArray, float f6) {
        WatermarkModule watermarkModule = ModuleManager.get().get(WatermarkModule.class);
        boolean bl = watermarkModule == null || watermarkModule.showNick.getValue();
        boolean bl2 = watermarkModule == null || watermarkModule.showTime.getValue();
        float f7 = f2 + 10.0f + 0.5f;
        float f8 = f4 + 0.5f;
        int n = Math.round(this.nameWidth / 8.0f);
        int n2 = 0;
        int n3 = n + 1;
        if (bl) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, n2, f5));
            WatermarkComp.drawGradientString(INFO_FONT, this.cachedName, f3 += 8.0f, f8, 8.0f, 1, nArray, f5, f6);
            f3 += this.nameWidth;
        }
        if (bl2) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, n3, f5));
            int n4 = n3 + 1;
            WatermarkComp.drawGradientString(INFO_FONT, this.timeText, f3 += 8.0f, f8, 8.0f, n4, nArray, f5, f6);
            f3 += this.timeWidth;
        }
        return f3;
    }

    private static float indexedGradientPhase() {
        return (float)(System.currentTimeMillis() % 1200L) / 1200.0f * 360.0f;
    }

    private static float normalizedCycle(float f) {
        float f2 = f % 360.0f;
        return f2 < 0.0f ? f2 + 360.0f : f2;
    }

    private void updateTextCache() {
        if (!this.staticMetricsReady) {
            this.brandWidth = 0.0f;
            for (int i = 0; i < BRAND_GLYPHS.length; ++i) {
                float f;
                this.brandGlyphWidths[i] = f = Render2D.msdfWidth(BRAND_FONT, BRAND_GLYPHS[i], 11.0f);
                this.brandWidth += f;
            }
            this.separatorWidth = Render2D.msdfWidth(INFO_FONT, SEPARATOR, 8.0f);
            this.staticMetricsReady = true;
        }
        this.updateInfoCache();
    }

    private static void drawIndexedGradientText(String string, String[] stringArray, float[] fArray, float f, float f2, float f3, int n, int[] nArray, float f4, float f5) {
        float f6 = Math.max(0.0f, Math.min(1.0f, f4));
        if (f6 <= 0.003921569f || stringArray == null || stringArray.length == 0) {
            return;
        }
        float f7 = f;
        for (int i = 0; i < stringArray.length; ++i) {
            String string2 = stringArray[i];
            if (string2 != null && !string2.isEmpty()) {
                WatermarkComp.drawIndexedGradientGlyph(string, string2, f7, f2, f3, n + i, nArray, f6, f5);
            }
            f7 += i < fArray.length ? fArray[i] : Render2D.msdfWidth(string, string2, f3);
        }
    }

    private static int gradientDotColor(int[] nArray, float f, int n, float f2) {
        int n2 = WatermarkComp.indexedPaletteColor(nArray, f + (float)n * 15.0f);
        return ColorUtil.multAlpha(n2, Math.max(0.0f, Math.min(1.0f, f2)));
    }

    private void updateInfoCache() {
        WatermarkModule watermarkModule;
        String string;
        String string2 = WatermarkComp.playerName();
        if (!string2.equals(this.cachedName)) {
            this.cachedName = string2;
            this.nameWidth = Render2D.msdfWidth(INFO_FONT, string2, 8.0f);
        }
        if (!(string = LocalTime.now().format(TIME_FORMAT)).equals(this.timeText)) {
            this.timeText = string;
            this.timeWidth = Render2D.msdfWidth(INFO_FONT, string, 8.0f);
        }
        boolean bl = (watermarkModule = ModuleManager.get().get(WatermarkModule.class)) == null || watermarkModule.showNick.getValue();
        boolean bl2 = watermarkModule == null || watermarkModule.showTime.getValue();
        float f = 14.0f;
        this.discordBlockWidth = (bl ? f + this.nameWidth : 0.0f) + (bl2 ? f + this.timeWidth : 0.0f);
    }

    private static void drawGradientString(String string, String string2, float f, float f2, float f3, int n, int[] nArray, float f4, float f5) {
        int n2;
        float f6 = Math.max(0.0f, Math.min(1.0f, f4));
        if (f6 <= 0.003921569f || string2 == null || string2.isEmpty()) {
            return;
        }
        float f7 = f;
        int n3 = n;
        for (int i = 0; i < string2.length(); i += n2) {
            int n4 = string2.codePointAt(i);
            n2 = Character.charCount(n4);
            String string3 = string2.substring(i, i + n2);
            WatermarkComp.drawIndexedGradientGlyph(string, string3, f7, f2, f3, n3, nArray, f6, f5);
            f7 += Render2D.msdfWidth(string, string3, f3);
            ++n3;
        }
    }

    @Override
    protected void render(DrawContext drawContext) {
        boolean bl = this.shouldShow();
        if (bl != this.lastTargetVisible) {
            this.visibility.run(bl ? 1.0 : 0.0, bl ? 0.18 : 0.12, Easings.CUBIC_OUT, false);
            this.lastTargetVisible = bl;
        }
        this.visibility.update();
        float f = this.visibility.get();
        if (bl && f <= 0.01f) {
            f = 0.01f;
        }
        if (f <= 0.01f && !bl) {
            return;
        }
        this.updateTextCache();
        this.currentWidth = this.brandWidth + this.discordBlockWidth + 18.0f;
        float f2 = 0.92f + f * 0.08f;
        float f3 = this.getX() + this.currentWidth * 0.5f;
        float f4 = this.getY() + 10.0f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f3, f4);
        drawContext.getMatrices().scale(f2);
        drawContext.getMatrices().translate(-f3, -f4);
        Render2D.beginFrame(drawContext);
        float f5 = this.getX();
        float f6 = this.getY();
        RectUtil.drawClientRect(f5, f6, this.currentWidth, 20.0f, 10.0f, f);
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        int[] nArray = WatermarkComp.watermarkPalette(interfaceModule);
        float f7 = WatermarkComp.indexedGradientPhase();
        float f8 = f5 + 9.0f;
        float f9 = f6 + 6.0f - 1.0f;
        WatermarkComp.drawIndexedGradientText(BRAND_FONT, BRAND_GLYPHS, this.brandGlyphWidths, f8, f9, 11.0f, 1, nArray, f, f7);
        this.drawInfoBlock(f5, f6, f8 += this.brandWidth, f9, f, nArray, f7);
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    private static String playerName() {
        String string;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getSession() != null && minecraftClient.getSession().getUsername() != null && !minecraftClient.getSession().getUsername().isBlank()) {
            return minecraftClient.getSession().getUsername();
        }
        if (minecraftClient.player != null && (string = minecraftClient.player.getGameProfile().name()) != null && !string.isBlank()) {
            return string;
        }
        return FALLBACK_NAME;
    }

    private boolean shouldShow() {
        WatermarkModule watermarkModule = ModuleManager.get().get(WatermarkModule.class);
        return watermarkModule != null && watermarkModule.isEnabled();
    }

    private static int opaque(int n) {
        return n & 0xFFFFFF | 0xFF000000;
    }
}

