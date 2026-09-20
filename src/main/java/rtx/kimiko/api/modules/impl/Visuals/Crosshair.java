package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.hit.EntityHitResult;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ButtonSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;
import rtx.kimiko.api.modules.settings.impl.TextSetting;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.api.ui.crosshair.CrosshairEditorScreen;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.outline.outline360.Outline360Range;
import rtx.kimiko.utils.render.render2d.rectangle.rectdefault.BuiltRectangle;

public class Crosshair
extends Module {
    private static final int BLACK = -16777216;
    public static final int GRID = 15;
    private final ModeSetting mode = this.register(new ModeSetting("\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434", "\u0422\u0438\u043f \u043f\u0440\u0438\u0446\u0435\u043b\u0430.", "\u041a\u0440\u0435\u0441\u0442\u0438\u043a", "\u041a\u0440\u0435\u0441\u0442\u0438\u043a", "\u041a\u0440\u0443\u0436\u043e\u043a", "\u0421\u0432\u043e\u0439"));
    private final SliderSetting attackOffset = this.register(new SliderSetting("\u041e\u0442\u0441\u0442\u0443\u043f \u0430\u0442\u0430\u043a\u0438", "\u0420\u0430\u0437\u0431\u0440\u043e\u0441 \u043b\u0443\u0447\u0435\u0439 \u043f\u043e \u043a\u0443\u043b\u0434\u0430\u0443\u043d\u0443 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430.").range(0, 20).setValue(10.0f).visible(() -> this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")));
    private final SliderSetting indent = this.register(new SliderSetting("\u041e\u0442\u0441\u0442\u0443\u043f", "\u041e\u0442\u0441\u0442\u0443\u043f \u043b\u0443\u0447\u0435\u0439 \u043e\u0442 \u0446\u0435\u043d\u0442\u0440\u0430 \u044d\u043a\u0440\u0430\u043d\u0430.").range(0, 5).setValue(0.0f).visible(() -> this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")));
    private final SliderSetting size1 = this.register(new SliderSetting("\u0414\u043b\u0438\u043d\u0430", "\u0414\u043b\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0439 \u043f\u0440\u0438\u0446\u0435\u043b\u0430.").range(2.0f, 10.0f).increment(0.5f).setValue(4.0f).visible(() -> this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")));
    private final SliderSetting size2 = this.register(new SliderSetting("\u0422\u043e\u043b\u0449\u0438\u043d\u0430", "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0439 \u043f\u0440\u0438\u0446\u0435\u043b\u0430.").range(1.0f, 4.0f).increment(0.5f).setValue(1.0f).visible(() -> this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")));
    private final BooleanSetting drawDot = this.register(new BooleanSetting("\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0442\u043e\u0447\u043a\u0443", "\u0426\u0435\u043d\u0442\u0440\u0430\u043b\u044c\u043d\u0430\u044f \u0442\u043e\u0447\u043a\u0430 \u043f\u043e\u0434 \u043f\u0435\u0440\u0435\u043a\u0440\u0435\u0441\u0442\u0438\u0435\u043c.", false).visible(() -> this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")));
    private final BooleanSetting onKrytka = this.register(new BooleanSetting("\u0418\u0437\u043c\u0435\u043d\u044f\u0442\u044c \u043f\u043e\u0437\u0438\u0446\u0438\u044e \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438 \u043c\u044b\u0448\u0438", "\u0418\u043d\u0435\u0440\u0446\u0438\u044f \u043f\u0440\u0438\u0446\u0435\u043b\u0430 \u043f\u0440\u0438 \u043f\u043e\u0432\u043e\u0440\u043e\u0442\u0435 \u043a\u0430\u043c\u0435\u0440\u044b.", true).visible(() -> this.mode.is("\u041a\u0440\u0443\u0436\u043e\u043a")));
    private final ButtonSetting customEditor = this.register(new ButtonSetting("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440", "\u041d\u0430\u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0441\u0432\u043e\u0439 \u043f\u0440\u0438\u0446\u0435\u043b \u043f\u043e \u043f\u0438\u043a\u0441\u0435\u043b\u044f\u043c.").label("\u041e\u0442\u043a\u0440\u044b\u0442\u044c").onClick(this::openEditor).visible(() -> this.mode.is("\u0421\u0432\u043e\u0439")));
    private final ModeSetting customColorMode = this.register(new ModeSetting("\u0426\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u043f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u043e\u0433\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0430.", "\u0411\u0435\u043b\u044b\u0439", "\u0411\u0435\u043b\u044b\u0439", "\u0422\u0435\u043c\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430").visibleWhen(() -> this.mode.is("\u0421\u0432\u043e\u0439")));
    private final SliderSetting customScale = this.register(new SliderSetting("\u041c\u0430\u0441\u0448\u0442\u0430\u0431", "\u0420\u0430\u0437\u043c\u0435\u0440 \u043e\u0434\u043d\u043e\u0433\u043e \u043f\u0438\u043a\u0441\u0435\u043b\u044f \u043f\u0440\u0438\u0446\u0435\u043b\u0430.").range(0.5f, 3.0f).increment(0.5f).setValue(1.0f).visible(() -> this.mode.is("\u0421\u0432\u043e\u0439")));
    private final BooleanSetting customOutline = this.register(new BooleanSetting("\u041e\u0431\u0432\u043e\u0434\u043a\u0430", "\u0422\u0451\u043c\u043d\u0430\u044f \u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0432\u043e\u043a\u0440\u0443\u0433 \u043f\u0438\u043a\u0441\u0435\u043b\u0435\u0439.", true).visible(() -> this.mode.is("\u0421\u0432\u043e\u0439")));
    private final BooleanSetting customTargetReact = this.register(new BooleanSetting("\u0420\u0435\u0430\u043a\u0446\u0438\u044f \u043d\u0430 \u0446\u0435\u043b\u044c", "\u041f\u0440\u0438\u0446\u0435\u043b \u043a\u0440\u0430\u0441\u043d\u0435\u0435\u0442 \u043f\u0440\u0438 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0438 \u043d\u0430 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044c.", true).visible(() -> this.mode.is("\u0421\u0432\u043e\u0439")));
    private final TextSetting customPixels = this.register(new TextSetting("\u041f\u0438\u043a\u0441\u0435\u043b\u0438", "\u0414\u0430\u043d\u043d\u044b\u0435 \u043f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u043e\u0433\u043e \u043f\u0440\u0438\u0446\u0435\u043b\u0430.").setText(Crosshair.defaultGrid()).visible(() -> false));
    private float red = 1.0f;
    private float prevYaw;
    private float prevPitch;
    private boolean prevInit;
    private long lastNs = System.nanoTime();

    public Crosshair() {
        super("Crosshair", "\u0418\u0437\u043c\u0435\u043d\u044f\u0435\u0442 \u0432\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434 \u043f\u0440\u0438\u0446\u0435\u043b\u0430.", Category.VISUALS);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    @Override
    protected void onEnable() {
        this.prevInit = false;
    }

    private void openEditor() {
        CrosshairEditorScreen crosshairEditorScreen = new CrosshairEditorScreen(this, UI.INSTANCE);
        if (this.mc.currentScreen == UI.INSTANCE) {
            UI.closeInto(crosshairEditorScreen);
        } else {
            this.mc.setScreen((Screen)crosshairEditorScreen);
        }
    }

    private void renderMain(float f, float f2, float f3, float f4, float f5, float f6, float f7, int n) {
        Crosshair.sharpRect(f - f7 - f5 / 2.0f, f2 - f3 - f6 - f5 / 2.0f, f4 + f5, f3 + f5, n);
        Crosshair.sharpRect(f - f7 - f5 / 2.0f, f2 + f6 - f5 / 2.0f, f4 + f5, f3 + f5, n);
        Crosshair.sharpRect(f - f3 - f6 - f5 / 2.0f, f2 - f7 - f5 / 2.0f, f3 + f5, f4 + f5, n);
        Crosshair.sharpRect(f + f6 - f5 / 2.0f, f2 - f7 - f5 / 2.0f, f3 + f5, f4 + f5, n);
    }

    private static void sharpRect(float f, float f2, float f3, float f4, int n) {
        if (f3 <= 0.0f || f4 <= 0.0f) {
            return;
        }
        Render2D.rect(new BuiltRectangle(f, f2, f3, f4, 0.0f, n).withSmoothness(0.0f));
    }

    private static float pingpong(float f) {
        float f2 = f * 2.0f;
        return f2 <= 1.0f ? f2 : 2.0f - f2;
    }

    public boolean[] customGrid() {
        String string = this.customPixels.getText();
        boolean[] blArray = new boolean[225];
        if (string == null || string.length() != blArray.length) {
            string = Crosshair.defaultGrid();
        }
        for (int i = 0; i < blArray.length; ++i) {
            blArray[i] = string.charAt(i) == '1';
        }
        return blArray;
    }

    public static String defaultGrid() {
        StringBuilder stringBuilder = new StringBuilder(225);
        int n = 7;
        for (int i = 0; i < 225; ++i) {
            int n2 = i / 15;
            int n3 = i % 15;
            boolean bl = n3 == n && (n2 >= n - 5 && n2 <= n - 2 || n2 >= n + 2 && n2 <= n + 5);
            boolean bl2 = n2 == n && (n3 >= n - 5 && n3 <= n - 2 || n3 >= n + 2 && n3 <= n + 5);
            boolean bl3 = n2 == n && n3 == n;
            stringBuilder.append((char)(bl || bl2 || bl3 ? 49 : 48));
        }
        return stringBuilder.toString();
    }

    private static int multRedWhite(float f) {
        int n = Math.min(255, Math.round(255.0f / Math.max(1.0E-4f, f)));
        return 0xFFFF0000 | n << 8 | n;
    }

    public void renderGridStyled(boolean[] blArray, float f, float f2, float f3, float f4) {
        int n;
        int n2;
        float f5 = 15.0f * f3 * 0.5f;
        float f6 = Math.round(f - f5);
        float f7 = Math.round(f2 - f5);
        if (this.customOutline.getValue()) {
            float f8 = Math.min(0.5f, f3 * 0.5f);
            for (n2 = 0; n2 < blArray.length; ++n2) {
                if (!blArray[n2]) continue;
                n = n2 / 15;
                int n3 = n2 % 15;
                Crosshair.sharpRect(f6 + (float)n3 * f3 - f8, f7 + (float)n * f3 - f8, f3 + f8 * 2.0f, f3 + f8 * 2.0f, -939524096);
            }
        }
        for (int i = 0; i < blArray.length; ++i) {
            if (!blArray[i]) continue;
            n2 = i / 15;
            n = i % 15;
            Crosshair.sharpRect(f6 + (float)n * f3, f7 + (float)n2 * f3, f3, f3, this.customPixelColor(n, f4));
        }
    }

    private void renderCustom(float f, float f2, float f3) {
        float f4 = this.customTargetReact.getValue() && this.mc.crosshairTarget instanceof EntityHitResult ? 5.0f : 1.0f;
        this.red += (f4 - this.red) * Math.min(1.0f, f3 * 10.0f);
        this.renderGridStyled(this.customGrid(), f * 0.5f, f2 * 0.5f, this.customScale.getValue(), this.red);
    }

    private void renderCross(float f, float f2, float f3, float f4) {
        float f5 = this.mc.crosshairTarget instanceof EntityHitResult ? 5.0f : 1.0f;
        float f6 = Math.min(1.0f, f4 * 10.0f);
        this.red += (f5 - this.red) * f6;
        int n = Crosshair.multRedWhite(this.red);
        int n2 = -16777216;
        float f7 = f / 2.0f;
        float f8 = f2 / 2.0f;
        float f9 = (float)this.attackOffset.getInt() - (float)this.attackOffset.getInt() * this.mc.player.getAttackCooldownProgress(f3);
        float f10 = this.size1.getValue();
        float f11 = this.size2.getValue();
        float f12 = f11 / 2.0f;
        float f13 = (float)this.indent.getInt() + f9;
        if (this.drawDot.getValue()) {
            Crosshair.sharpRect(f7 - 1.0f, f8 - 1.0f, 2.0f, 2.0f, n2);
            Crosshair.sharpRect(f7 - 0.5f, f8 - 0.5f, 1.0f, 1.0f, n);
        }
        this.renderMain(f7, f8, f10, f11, 1.0f, f13, f12, n2);
        this.renderMain(f7, f8, f10, f11, 0.0f, f13, f12, n);
    }

    private void renderCircle(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7;
        float f8;
        float f9;
        float f10;
        if (this.onKrytka.getValue()) {
            f10 = this.mc.player.getYaw();
            f9 = this.mc.player.getPitch();
            if (!this.prevInit) {
                this.prevYaw = f10;
                this.prevPitch = f9;
                this.prevInit = true;
            }
            f8 = (f10 - this.prevYaw) * 0.5f;
            f7 = (f9 - this.prevPitch) * 0.5f;
            if (Float.isFinite(f8)) {
                f += f8;
            }
            if (Float.isFinite(f7)) {
                f2 += f7;
            }
            float f11 = 50.0f;
            f = Crosshair.clamp(f, f11, f3 - f11);
            f2 = Crosshair.clamp(f2, f11, f4 - f11);
            float f12 = 1.0f - (float)Math.exp(-f6 * 3.6f);
            this.prevYaw += (f10 - this.prevYaw) * f12;
            this.prevPitch += (f9 - this.prevPitch) * f12;
        } else {
            this.prevInit = false;
        }
        if (!Float.isFinite(f)) {
            f = f3 * 0.5f;
        }
        if (!Float.isFinite(f2)) {
            f2 = f4 * 0.5f;
        }
        float f13 = f10 = (f10 = this.mc.player.getHandSwingProgress(f5)) > 0.5f ? 1.0f - f10 : f10;
        if (!Float.isFinite(f10)) {
            f10 = 0.0f;
        }
        f9 = 5.0f;
        f8 = 1.6f;
        Render2D.outline360(f - f9, f2 - f9, f9 * 2.0f, f9 * 2.0f, f9, f8, -938997752, new Outline360Range[0]);
        f7 = 360.0f * Math.max(0.0f, 1.0f - f10 * 2.0f);
        Crosshair.drawGradientRing(f, f2, f9, f8, f7, 255.0f);
    }

    public void setCustomGrid(boolean[] blArray) {
        StringBuilder stringBuilder = new StringBuilder(225);
        for (int i = 0; i < 225; ++i) {
            stringBuilder.append((char)(i < blArray.length && blArray[i] ? 49 : 48));
        }
        this.customPixels.setText(stringBuilder.toString());
    }

    @EventHandler
    private void onHudRender(HudRenderEvent hudRenderEvent) {
        if (!this.isEnabled() || this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (!this.mc.options.getPerspective().isFirstPerson()) {
            return;
        }
        DrawContext drawContext = hudRenderEvent.getGraphics();
        float f = hudRenderEvent.getPartialTick();
        long l = System.nanoTime();
        float f2 = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        float f3 = Position.screenWidth();
        float f4 = Position.screenHeight();
        Render2D.beginFrame(drawContext);
        if (this.mode.is("\u041a\u0440\u0435\u0441\u0442\u0438\u043a")) {
            this.renderCross(f3, f4, f, f2);
        } else if (this.mode.is("\u0421\u0432\u043e\u0439")) {
            this.renderCustom(f3, f4, f2);
        } else {
            this.renderCircle(f3 * 0.5f, f4 * 0.5f, f3, f4, f, f2);
        }
        Render2D.flush();
    }

    private int customPixelColor(int n, float f) {
        float f2;
        int n2;
        if (this.customColorMode.is("\u0422\u0435\u043c\u0430")) {
            n2 = ClientAccent.gradientColor(Crosshair.pingpong((float)n / 14.0f), 255.0f);
        } else if (this.customColorMode.is("\u0420\u0430\u0434\u0443\u0433\u0430")) {
            f2 = (float)(System.currentTimeMillis() % 4000L) / 4000.0f + (float)n * 0.03f;
            n2 = Color.HSBtoRGB(f2 - (float)Math.floor(f2), 0.65f, 1.0f);
        } else {
            n2 = -1;
        }
        f2 = Crosshair.clamp((f - 1.0f) / 4.0f, 0.0f, 1.0f);
        return f2 <= 0.001f ? n2 : ClientAccent.mix(n2 |= 0xFF000000, -44976, f2);
    }

    private static void drawGradientRing(float f, float f2, float f3, float f4, float f5, float f6) {
        int n;
        if (f5 < 1.0f) {
            return;
        }
        int n2 = 16;
        float f7 = Math.min(14.0f, f5 * 0.5f);
        boolean bl = f5 >= 359.999f;
        float f8 = bl ? 360.0f / (float)n2 * 0.5f : 0.0f;
        ArrayList<Outline360Range> arrayList = new ArrayList<Outline360Range>(n2);
        for (n = 0; n < n2; ++n) {
            float f9 = f8 + f5 * (float)n / (float)n2;
            float f10 = f8 + f5 * (float)(n + 1) / (float)n2;
            int n3 = ClientAccent.gradientColor(Crosshair.pingpong((float)n / (float)n2), f6);
            int n4 = ClientAccent.gradientColor(Crosshair.pingpong((float)(n + 1) / (float)n2), f6);
            float f11 = !bl && n == 0 ? f7 : 0.0f;
            float f12 = !bl && n == n2 - 1 ? f7 : 0.0f;
            arrayList.add(Outline360Range.gradient((float)f9, (float)f10, (int)n3, (int)n4, (float)f11, (float)f12));
        }
        n = bl ? ClientAccent.accent(f6) : ClientAccent.accent(1.0f);
        Render2D.outline360(f - f3, f2 - f3, f3 * 2.0f, f3 * 2.0f, f3, f4, n, arrayList);
    }
}

