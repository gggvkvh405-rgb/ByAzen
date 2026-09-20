package rtx.kimiko.api.drags.components;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.impl.Interface.KeyStrokesModule;
import rtx.kimiko.api.ui.settings.Setting;
import rtx.kimiko.api.ui.settings.impl.BoolSetting;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.ClientPalette;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class KeyStrokesComp
extends Draggable {
    private static final float KEY = 22.0f;
    private static final float GAP = 2.5f;
    private static final float MOUSE_H = 16.0f;
    private static final float SPACE_H = 14.5f;
    private static final float FALLBACK_RADIUS = 5.0f;
    private static final float WIDTH = 71.0f;
    private static final String FONT = "montserrat-medium";
    private static final float KEY_FONT = 8.5f;
    private static final float MOUSE_FONT = 7.0f;
    private static final float SINK = 1.1f;
    private static final int LABEL_IDLE = -4604215;
    private static final int TOP_IDLE = -233038042;
    private static final int BOT_IDLE = -233498851;
    private static final int KEY_W = 0;
    private static final int KEY_A = 1;
    private static final int KEY_S = 2;
    private static final int KEY_D = 3;
    private static final int KEY_LMB = 4;
    private static final int KEY_RMB = 5;
    private static final int KEY_SPACE = 6;
    private final SmoothAnimation visibility = new SmoothAnimation();
    private final SmoothAnimation[] press = new SmoothAnimation[7];

    public KeyStrokesComp() {
        super("keystrokes", 5.0f, 225.0f);
        this.visibility.set(0.0);
        for (int i = 0; i < this.press.length; ++i) {
            this.press[i] = new SmoothAnimation();
            this.press[i].set(0.0);
        }
    }

    private static KeyStrokesModule module() {
        return ModuleManager.get().get(KeyStrokesModule.class);
    }

    @Override
    public String displayName() {
        return "KeyStrokes";
    }

    @Override
    public float width() {
        return 71.0f;
    }

    @Override
    public float height() {
        KeyStrokesModule keyStrokesModule = KeyStrokesComp.module();
        float f = 46.5f;
        if (keyStrokesModule == null || keyStrokesModule.showMouse.getValue()) {
            f += 18.5f;
        }
        if (keyStrokesModule == null || keyStrokesModule.showSpace.getValue()) {
            f += 17.0f;
        }
        return f;
    }

    @Override
    public boolean isInteractive() {
        KeyStrokesModule keyStrokesModule = KeyStrokesComp.module();
        return keyStrokesModule != null && keyStrokesModule.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        KeyStrokesModule keyStrokesModule = KeyStrokesComp.module();
        if (keyStrokesModule != null) {
            list.add(new BoolSetting(keyStrokesModule.showMouse));
            list.add(new BoolSetting(keyStrokesModule.showCps));
            list.add(new BoolSetting(keyStrokesModule.showSpace));
        }
        return list;
    }

    private static float panelRadius() {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        return interfaceModule != null ? interfaceModule.rectCornerRadius.getFloat() : 5.0f;
    }

    @Override
    protected void render(DrawContext drawContext) {
        KeyStrokesModule keyStrokesModule = KeyStrokesComp.module();
        boolean bl = keyStrokesModule != null && keyStrokesModule.isEnabled();
        this.visibility.run(bl ? 1.0 : 0.0, bl ? 0.18 : 0.12, Easings.CUBIC_OUT, true);
        this.visibility.update();
        float f = this.visibility.get();
        if (f <= 0.01f) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl2 = keyStrokesModule == null || keyStrokesModule.showMouse.getValue();
        boolean bl3 = keyStrokesModule != null && keyStrokesModule.showCps.getValue();
        boolean bl4 = keyStrokesModule == null || keyStrokesModule.showSpace.getValue();
        float f2 = this.getX();
        float f3 = this.getY();
        float f4 = this.width();
        float f5 = this.height();
        float f6 = 0.92f + f * 0.08f;
        float f7 = f2 + f4 * 0.5f;
        float f8 = f3 + f5 * 0.5f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f7, f8);
        drawContext.getMatrices().scale(f6);
        drawContext.getMatrices().translate(-f7, -f8);
        Render2D.beginFrame(drawContext);
        float f9 = f3;
        this.drawKey(0, f2 + 22.0f + 2.5f, f9, 22.0f, 22.0f, KeyStrokesComp.keyLabel(minecraftClient.options.forwardKey), 8.5f, minecraftClient.options.forwardKey.isPressed(), f);
        this.drawKey(1, f2, f9 += 24.5f, 22.0f, 22.0f, KeyStrokesComp.keyLabel(minecraftClient.options.leftKey), 8.5f, minecraftClient.options.leftKey.isPressed(), f);
        this.drawKey(2, f2 + 22.0f + 2.5f, f9, 22.0f, 22.0f, KeyStrokesComp.keyLabel(minecraftClient.options.backKey), 8.5f, minecraftClient.options.backKey.isPressed(), f);
        this.drawKey(3, f2 + 49.0f, f9, 22.0f, 22.0f, KeyStrokesComp.keyLabel(minecraftClient.options.rightKey), 8.5f, minecraftClient.options.rightKey.isPressed(), f);
        f9 += 24.5f;
        if (bl2) {
            float f10 = (f4 - 2.5f) * 0.5f;
            this.drawKey(4, f2, f9, f10, 16.0f, KeyStrokesComp.mouseLabel(keyStrokesModule, true, bl3), 7.0f, minecraftClient.options.attackKey.isPressed(), f);
            this.drawKey(5, f2 + f10 + 2.5f, f9, f10, 16.0f, KeyStrokesComp.mouseLabel(keyStrokesModule, false, bl3), 7.0f, minecraftClient.options.useKey.isPressed(), f);
            f9 += 18.5f;
        }
        if (bl4) {
            this.drawKey(6, f2, f9, f4, 14.5f, null, 0.0f, minecraftClient.options.jumpKey.isPressed(), f);
        }
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    private static String mouseLabel(KeyStrokesModule keyStrokesModule, boolean bl, boolean bl2) {
        if (bl2 && keyStrokesModule != null) {
            int n;
            int n2 = n = bl ? keyStrokesModule.leftCps() : keyStrokesModule.rightCps();
            if (n > 0) {
                return n + " CPS";
            }
        }
        return bl ? "LMB" : "RMB";
    }

    private static String keyLabel(KeyBinding keyBinding) {
        Object object = keyBinding.getBoundKeyTranslationKey();
        if (object == null || ((String)object).isBlank()) {
            return "?";
        }
        if (((String)object).startsWith("key.keyboard.")) {
            object = ((String)object).substring("key.keyboard.".length());
        } else if (((String)object).startsWith("key.mouse.")) {
            object = "M " + ((String)object).substring("key.mouse.".length());
        }
        return ((String)object).replace('.', ' ').toUpperCase(Locale.ROOT);
    }

    private void drawKey(int n, float f, float f2, float f3, float f4, String string, float f5, boolean bl, float f6) {
        int n2;
        SmoothAnimation smoothAnimation = this.press[n];
        smoothAnimation.run(bl ? 1.0 : 0.0, bl ? 0.55 : 0.28, Easings.CUBIC_OUT, true);
        smoothAnimation.update();
        float f7 = smoothAnimation.get();
        float f8 = 1.1f * f7;
        float f9 = f + f8;
        float f10 = f2 + f8;
        float f11 = f3 - f8 * 2.0f;
        float f12 = f4 - f8 * 2.0f;
        float f13 = Math.min(KeyStrokesComp.panelRadius(), Math.min(f11, f12) * 0.5f);
        float f14 = f6 * (1.0f - f7);
        if (f14 > 0.003921569f) {
            n2 = ColorUtil.multAlpha(-233038042, f14);
            int n3 = ColorUtil.multAlpha(-233498851, f14);
            Render2D.rect(f9, f10, f11, f12, f13, n2, n2, n3, n3);
        }
        if (f7 > 0.003921569f) {
            RectUtil.drawClientRect(f9, f10, f11, f12, f13, f6 * f7);
        }
        n2 = ColorUtil.rgba(255, 255, 255, Math.round(26.0f * f6));
        int[] nArray = ClientPalette.cornerColors(0.5882353f * f6);
        Render2D.outline(f9, f10, f11, f12, f13, 0.5f, ColorUtil.lerpColor(n2, nArray[0], f7), ColorUtil.lerpColor(n2, nArray[1], f7), ColorUtil.lerpColor(n2, nArray[2], f7), ColorUtil.lerpColor(n2, nArray[3], f7));
        int n4 = ColorUtil.multAlpha(ColorUtil.lerpColor(-4604215, -1, f7), f6);
        if (string == null) {
            float f15 = f11 * 0.42f;
            float f16 = 2.5f;
            Render2D.rect(f9 + (f11 - f15) * 0.5f, f10 + (f12 - f16) * 0.5f, f15, f16, f16 * 0.5f, n4, n4, n4, n4);
            return;
        }
        float f17 = f5;
        float f18 = Render2D.msdfWidth(FONT, string, f17);
        while (f18 > f11 - 6.0f && f17 > 5.5f) {
            f18 = Render2D.msdfWidth(FONT, string, f17 -= 0.5f);
        }
        Render2D.msdfText(FONT, string, f9 + (f11 - f18) * 0.5f, f10 + (f12 - f17) * 0.5f - f17 * 0.12f, f17, n4);
    }
}

