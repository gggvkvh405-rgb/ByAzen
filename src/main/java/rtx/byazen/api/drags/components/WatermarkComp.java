package rtx.byazen.api.drags.components;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Interface.WatermarkModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.animations.Easings;
import rtx.byazen.utils.animations.SmoothAnimation;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

public final class WatermarkComp
extends Draggable {
    private static final float PAD_X = 9.0f;
    private static final float PAD_Y = 6.0f;
    private static final float FONT = 8.0f;
    private static final float BRAND_FONT_SIZE = 9.5f;
    private static final float INFO_FONT_SIZE = 8.0f;
    private static final float BRAND_TRACKING = 0.35f;
    private static final float H = 20.0f;
    private static final String BRAND_FONT = "montserrat-extrabold";
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
    private float cachedFps = 0.0f;
    private int cachedPing = -1;
    private float cachedTps = 20.0f;
    private long lastWorldTime = -1L;
    private long tpsWindowStart;
    private long tpsTicks;
    private float fpsWidth;
    private float pingWidth;
    private float tpsWidth;
    private float currentWidth = 80.0f;
    private boolean staticMetricsReady;
    private float brandWidth;
    private String appliedPreset = "";
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

    private static int[] watermarkPalette(InterfaceModule interfaceModule, WatermarkModule module) {
        if (module != null && module.palette.is("Свой")) {
            int[] custom = ClientAccent.shadesFromCustom(module.customColor.getValue() & 0xFFFFFF, 0, false);
            int[] shades = new int[custom.length];
            for (int i = 0; i < custom.length; ++i) {
                shades[i] = ColorUtil.lerpColor(WatermarkComp.opaque(custom[i]), -1, 0.18f);
            }
            return shades;
        }
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
        boolean fps = watermarkModule != null && watermarkModule.showFps.getValue();
        boolean ping = watermarkModule != null && watermarkModule.showPing.getValue();
        boolean tps = watermarkModule != null && watermarkModule.showTps.getValue();
        float f7 = f2 + 10.0f + 0.5f;
        float f8 = f2 + 10.0f - INFO_FONT_SIZE * 0.6f;
        int n = Math.round(this.nameWidth / 8.0f);
        int n2 = 0;
        int n3 = n + 1;
        if (bl) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, n2, f5));
            WatermarkComp.drawGradientString(INFO_FONT, this.cachedName, f3 += 8.0f, f8, INFO_FONT_SIZE, 1, nArray, f5, f6);
            f3 += this.nameWidth;
        }
        if (bl2) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, n3, f5));
            int n4 = n3 + 1;
            WatermarkComp.drawGradientString(INFO_FONT, this.timeText, f3 += 8.0f, f8, INFO_FONT_SIZE, n4, nArray, f5, f6);
            f3 += this.timeWidth;
            n3 = n4;
        }
        this.updateRuntimeStats();
        if (fps) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, ++n3, f5));
            String text = "fps " + Math.round(this.cachedFps);
            WatermarkComp.drawGradientString(INFO_FONT, text, f3 += 8.0f, f8, INFO_FONT_SIZE, n3 + 1, nArray, f5, f6);
            f3 += this.fpsWidth;
        }
        if (ping) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, ++n3, f5));
            String text = this.cachedPing < 0 ? "ping —" : "ping " + this.cachedPing + "ms";
            WatermarkComp.drawGradientString(INFO_FONT, text, f3 += 8.0f, f8, INFO_FONT_SIZE, n3 + 1, nArray, f5, f6);
            f3 += this.pingWidth;
        }
        if (tps) {
            Render2D.circle((f3 += 6.0f) + 1.0f, f7, 1.0f, WatermarkComp.gradientDotColor(nArray, f6, ++n3, f5));
            String text = "tps " + String.format(Locale.ROOT, "%.1f", this.cachedTps);
            WatermarkComp.drawGradientString(INFO_FONT, text, f3 += 8.0f, f8, INFO_FONT_SIZE, n3 + 1, nArray, f5, f6);
            f3 += this.tpsWidth;
        }
        return f3;
    }

    /** Обновляет fps, пинг и оценку tps для водяного знака. */
    private void updateRuntimeStats() {
        MinecraftClient client = MinecraftClient.getInstance();
        this.cachedFps = client == null ? 0.0f : (float) client.getCurrentFps();
        this.cachedPing = -1;
        if (client != null && client.player != null && client.getNetworkHandler() != null) {
            net.minecraft.client.network.PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) {
                this.cachedPing = Math.max(0, entry.getLatency());
            }
        }
        long now = System.currentTimeMillis();
        if (client != null && client.world != null) {
            long worldTime = client.world.getTime();
            if (this.lastWorldTime < 0L) {
                this.lastWorldTime = worldTime;
                this.tpsWindowStart = now;
                this.tpsTicks = 0L;
            }
            else if (worldTime != this.lastWorldTime) {
                this.tpsTicks += worldTime - this.lastWorldTime;
                this.lastWorldTime = worldTime;
            }
            long elapsed = now - this.tpsWindowStart;
            if (elapsed >= 1000L) {
                this.cachedTps = Math.max(0.0f, Math.min(20.0f, (float) this.tpsTicks * 1000.0f / (float) elapsed));
                this.tpsTicks = 0L;
                this.tpsWindowStart = now;
            }
        }
        String fps = "fps " + Math.round(this.cachedFps);
        String ping = this.cachedPing < 0 ? "ping —" : "ping " + this.cachedPing + "ms";
        String tps = "tps " + String.format(Locale.ROOT, "%.1f", this.cachedTps);
        this.fpsWidth = Render2D.msdfWidth(INFO_FONT, fps, INFO_FONT_SIZE);
        this.pingWidth = Render2D.msdfWidth(INFO_FONT, ping, INFO_FONT_SIZE);
        this.tpsWidth = Render2D.msdfWidth(INFO_FONT, tps, INFO_FONT_SIZE);
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
                this.brandGlyphWidths[i] = f = Render2D.msdfWidth(BRAND_FONT, BRAND_GLYPHS[i], BRAND_FONT_SIZE) + BRAND_TRACKING;
                this.brandWidth += f;
            }
            this.separatorWidth = Render2D.msdfWidth(INFO_FONT, SEPARATOR, INFO_FONT_SIZE);
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
        String string2 = WatermarkComp.watermarkName();
        if (!string2.equals(this.cachedName)) {
            this.cachedName = string2;
            this.nameWidth = Render2D.msdfWidth(INFO_FONT, string2, INFO_FONT_SIZE);
        }
        if (!(string = LocalTime.now().format(TIME_FORMAT)).equals(this.timeText)) {
            this.timeText = string;
            this.timeWidth = Render2D.msdfWidth(INFO_FONT, string, INFO_FONT_SIZE);
        }
        boolean bl = (watermarkModule = ModuleManager.get().get(WatermarkModule.class)) == null || watermarkModule.showNick.getValue();
        boolean bl2 = watermarkModule == null || watermarkModule.showTime.getValue();
        boolean fps = watermarkModule != null && watermarkModule.showFps.getValue();
        boolean ping = watermarkModule != null && watermarkModule.showPing.getValue();
        boolean tps = watermarkModule != null && watermarkModule.showTps.getValue();
        if (!(watermarkModule == null || watermarkModule.showData())) {
            bl = false;
            bl2 = false;
            fps = false;
            ping = false;
            tps = false;
        }
        float f = 14.0f;
        this.discordBlockWidth = (bl ? f + this.nameWidth : 0.0f) + (bl2 ? f + this.timeWidth : 0.0f)
                + (fps ? f + this.fpsWidth : 0.0f) + (ping ? f + this.pingWidth : 0.0f) + (tps ? f + this.tpsWidth : 0.0f);
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
        this.applyPositionPreset();
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
        WatermarkModule styleModule = ModuleManager.get().get(WatermarkModule.class);
        boolean compact = styleModule != null && !styleModule.hasPill();
        this.currentWidth = this.brandWidth + this.discordBlockWidth + (compact ? 8.0f : 18.0f);
        float f2 = 0.92f + f * 0.08f;
        float f3 = this.getX() + this.currentWidth * 0.5f;
        float f4 = this.getY() + 10.0f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f3, f4);
        drawContext.getMatrices().scale(f2);
        drawContext.getMatrices().translate(-f3, -f4);
        Render2D.beginFrame(drawContext);
        WatermarkModule module = ModuleManager.get().get(WatermarkModule.class);
        boolean pill = module == null || module.hasPill();
        boolean data = module == null || module.showData();
        float f5 = this.getX();
        float f6 = this.getY();
        if (pill) {
            RectUtil.drawClientRect(f5, f6, this.currentWidth, 20.0f, 10.0f, f);
            Render2D.outline(f5, f6, this.currentWidth, 20.0f, 10.0f, 0.7f, ClientAccent.accentSoft(30.0f * f));
        }
        else {
            // «минимал»: тонкая акцентная полоса слева вместо плашки
            Render2D.rect(f5, f6 + 3.0f, 2.0f, 14.0f, 1.0f, ClientAccent.accentSoft(180.0f * f));
        }
        int[] nArray = WatermarkComp.watermarkPalette(ModuleManager.get().get(InterfaceModule.class), module);
        float f7 = WatermarkComp.indexedGradientPhase();
        float f8 = f5 + (pill ? 9.0f : 12.0f);
        float f9 = f6 + 10.0f - BRAND_FONT_SIZE * 0.6f;
        WatermarkComp.drawIndexedGradientText(BRAND_FONT, BRAND_GLYPHS, this.brandGlyphWidths, f8, f9, BRAND_FONT_SIZE, 1, nArray, f, f7);
        if (data) {
            this.drawInfoBlock(f5, f6, f8 += this.brandWidth, f9, f, nArray, f7);
        }
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    /** Применяет выбранный пресет позиции (или оставляет место, куда пользователь перетащил плашку). */
    private void applyPositionPreset() {
        WatermarkModule watermarkModule = ModuleManager.get().get(WatermarkModule.class);
        if (watermarkModule == null) {
            return;
        }
        String string = watermarkModule.position.getSelected();
        if (string == null || string.equals(WatermarkModule.POS_CUSTOM) || string.equals(this.appliedPreset)) {
            return;
        }
        this.appliedPreset = string;
        float f = Math.max(60.0f, this.currentWidth);
        float f2 = Position.screenWidth();
        float f3 = Position.screenHeight();
        float f4 = 6.0f;
        float f5 = Math.max(6.0f, f2 - f - 6.0f);
        float f6 = 6.0f;
        float f7 = Math.max(6.0f, f3 - 34.0f);
        float f8 = (f2 - f) * 0.5f;
        float f9;
        float f10;
        if (string.equals(WatermarkModule.POS_TOP_LEFT)) {
            f9 = f4;
            f10 = f6;
        }
        else if (string.equals(WatermarkModule.POS_TOP_CENTER)) {
            f9 = f8;
            f10 = f6;
        }
        else if (string.equals(WatermarkModule.POS_TOP_RIGHT)) {
            f9 = f5;
            f10 = f6;
        }
        else if (string.equals(WatermarkModule.POS_BOTTOM_LEFT)) {
            f9 = f4;
            f10 = f7;
        }
        else if (string.equals(WatermarkModule.POS_BOTTOM_CENTER)) {
            f9 = f8;
            f10 = f7;
        }
        else {
            f9 = f5;
            f10 = f7;
        }
        rtx.byazen.api.drags.DragController dragController = this.getDrag();
        dragController.setTargetX(f9);
        dragController.setTargetY(f10);
        dragController.syncToTarget();
        this.getPosition().set(dragController.getTargetX(), dragController.getTargetY(), f, 20.0f);
    }

    /** Ник для водяного знака: на стриме подменяется, если включён Streamer Mode. */
    private static String watermarkName() {
        WatermarkModule module = ModuleManager.get().get(WatermarkModule.class);
        if (module != null && module.streamSafe.getValue()
                && rtx.byazen.api.modules.impl.Utils.StreamerMode.active()) {
            String replacement = rtx.byazen.api.modules.impl.Utils.StreamerMode.replacementName();
            if (replacement != null && !replacement.isBlank()) {
                return replacement;
            }
        }
        return WatermarkComp.playerName();
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

