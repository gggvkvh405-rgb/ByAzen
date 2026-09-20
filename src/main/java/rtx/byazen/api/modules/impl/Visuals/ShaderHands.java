package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import java.awt.Color;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.color.RainbowLut;
import rtx.byazen.utils.render.post.handsflame.HandsFlameRenderer;
import rtx.byazen.utils.render.post.shaderhands.ShaderHandsRenderer;

public final class ShaderHands
extends Module {
    private static final int DEFAULT_COLOR = -10785543;
    private static final String MODE_OLD = "\u0421\u0442\u0430\u0440\u044b\u0439";
    private static final String MODE_NEW = "\u041d\u043e\u0432\u044b\u0439";
    private static final String GLOW_ONLY = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435";
    private static final String OUTLINE_ONLY = "\u041e\u0431\u0432\u043e\u0434\u043a\u0430";
    private static final String GLOW_OUTLINE = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 + \u043e\u0431\u0432\u043e\u0434\u043a\u0430";
    private static final String COLOR_ITEM = "\u041f\u0440\u0435\u0434\u043c\u0435\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static ShaderHands instance;
    private final SeparatorSetting glassSeparator = this.register(new SeparatorSetting("\u0421\u0442\u0435\u043a\u043b\u043e"));
    private final BooleanSetting glass = this.register(new BooleanSetting("\u0421\u0442\u0435\u043a\u043b\u043e", "\u0421\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u0435 \u0440\u0443\u043a\u0438: \u043e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u043c\u0438\u0440\u0430 \u0441\u043a\u0432\u043e\u0437\u044c \u043c\u043e\u0434\u0435\u043b\u044c \u0441 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435\u043c.", true));
    private final SliderSetting glassSaturation = this.register(new SliderSetting("\u0421\u0430\u0442\u0443\u0440\u0430\u0446\u0438\u044f \u0441\u0442\u0435\u043a\u043b\u0430", "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u044f \u043c\u0438\u0440\u0430 \u0432 \u0440\u0443\u043a\u0430\u0445.").range(0.0f, 3.0f).increment(0.05f).setValue(1.45f).visible(this::isGlassOn));
    private final SliderSetting glassWhite = this.register(new SliderSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0441\u0442\u0435\u043a\u043b\u0430", "\u041f\u043e\u0434\u044a\u0451\u043c \u043a \u0431\u0435\u043b\u043e\u043c\u0443: 0 = \u0442\u0451\u043c\u043d\u043e\u0435, 1 = \u0431\u0435\u043b\u043e\u0435.").range(0.0f, 1.0f).increment(0.02f).setValue(0.78f).visible(this::isGlassOn));
    private final SliderSetting glassDistort = this.register(new SliderSetting("\u0418\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435 \u0441\u0442\u0435\u043a\u043b\u0430", "\u0421\u0438\u043b\u0430 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f \u043e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u044f. 0 = \u0440\u043e\u0432\u043d\u043e\u0435 \u0441\u0442\u0435\u043a\u043b\u043e.").range(0.0f, 0.05f).increment(0.001f).setValue(0.012f).visible(this::isGlassOn));
    private final SliderSetting glassTint = this.register(new SliderSetting("\u041f\u043e\u0434\u043a\u0440\u0430\u0441 \u0441\u0442\u0435\u043a\u043b\u0430", "\u041f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0430 \u043e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u044f \u0446\u0432\u0435\u0442\u043e\u043c \u043a\u043b\u0438\u0435\u043d\u0442\u0430.").range(0.0f, 1.0f).increment(0.02f).setValue(0.22f).visible(this::isGlassOn));
    private final SeparatorSetting glowSeparator = this.register(new SeparatorSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435"));
    private final ModeSetting renderMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u0421\u0442\u0430\u0440\u044b\u0439 \u2014 \u0433\u043b\u043e\u0443/\u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a, \u041d\u043e\u0432\u044b\u0439 \u2014 \u0448\u0435\u0439\u0434\u0435\u0440\u043d\u043e\u0435 \u043f\u043b\u0430\u043c\u044f. \u0421\u0442\u0435\u043a\u043b\u043e \u0441\u043e\u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e \u0441 \u043e\u0431\u043e\u0438\u043c\u0438.", "\u0421\u0442\u0430\u0440\u044b\u0439", "\u0421\u0442\u0430\u0440\u044b\u0439", "\u041d\u043e\u0432\u044b\u0439"));
    private final BooleanSetting glow = this.register(new BooleanSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435/\u043e\u0431\u0432\u043e\u0434\u043a\u0443 \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a (\u0441\u0442\u0438\u043b\u044c GlowEsp).", false).visible(this::isOldMode));
    private final ModeSetting glowMode = this.register((ModeSetting)new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0427\u0442\u043e \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c: \u0442\u043e\u043b\u044c\u043a\u043e \u0433\u043b\u043e\u0443, \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0431\u0432\u043e\u0434\u043a\u0443 \u0438\u043b\u0438 \u0432\u043c\u0435\u0441\u0442\u0435.", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u041e\u0431\u0432\u043e\u0434\u043a\u0430", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 + \u043e\u0431\u0432\u043e\u0434\u043a\u0430").visible(() -> this.isOldMode() && this.glow.getValue()));
    private final SliderSetting radius = this.register(new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a.").range(1.0f, 12.0f).increment(1.0f).setValue(6.0f).visible(() -> this.isOldMode() && this.glow.getValue()));
    private final SliderSetting glowStrength = this.register(new SliderSetting("\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f.").range(0.1f, 3.0f).increment(0.05f).setValue(1.8f).visible(() -> this.isOldMode() && this.glow.getValue()));
    private final BooleanSetting blending = this.register(new BooleanSetting("\u0421\u043c\u0435\u0448\u0438\u0432\u0430\u043d\u0438\u0435", "\u0410\u0434\u0434\u0438\u0442\u0438\u0432\u043d\u043e \u043f\u043e\u0434\u043c\u0435\u0448\u0438\u0432\u0430\u0442\u044c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043a \u0441\u0446\u0435\u043d\u0435 (\u043a\u0430\u043a \u0441\u0432\u0435\u0442/bloom).", false).visible(() -> this.isOldMode() && this.glow.getValue()));
    private final SliderSetting outlineWidth = this.register(new SliderSetting("\u0428\u0438\u0440\u0438\u043d\u0430 \u043a\u043e\u043d\u0442\u0443\u0440\u0430", "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a.").range(0.1f, 0.5f).increment(0.05f).setValue(0.3f).visible(() -> this.isOldMode() && this.glow.getValue() && !this.glowMode.is(GLOW_ONLY)));
    private final ModeSetting glowColorMode = this.register((ModeSetting)new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0426\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f/\u043e\u0431\u0432\u043e\u0434\u043a\u0438 \u0440\u0443\u043a: \u0440\u0430\u0434\u0443\u0433\u0430, \u0446\u0432\u0435\u0442 \u043a\u043b\u0438\u0435\u043d\u0442\u0430 \u0438\u043b\u0438 \u0441\u0432\u043e\u0439.", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439").visible(() -> this.isOldMode() && this.glow.getValue()));
    private final BooleanSetting glowSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 (\u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442).", false).visible(() -> this.isOldMode() && this.glow.getValue() && this.glowColorMode.is(COLOR_CUSTOM)));
    private final ColorSetting glowColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u0440\u0443\u043a.", new Color(91, 108, 249, 255)).visible(() -> this.isOldMode() && this.glow.getValue() && this.glowColorMode.is(COLOR_CUSTOM)));
    private final ColorSetting glowColor2 = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f \u0440\u0443\u043a.", new Color(255, 50, 150, 255)).visible(() -> this.isOldMode() && this.glow.getValue() && this.glowColorMode.is(COLOR_CUSTOM) && this.glowSecondColor.getValue()));
    private final SeparatorSetting flameSeparator = this.register(new SeparatorSetting("\u041f\u043b\u0430\u043c\u044f").visible(this::isNewMode));
    private final BooleanSetting flame = this.register(new BooleanSetting("\u041f\u043b\u0430\u043c\u044f", "\u0412\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0448\u0435\u0439\u0434\u0435\u0440\u043d\u043e\u0435 \u043f\u043b\u0430\u043c\u044f \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a.", true).visible(this::isNewMode));
    private final BooleanSetting onlyItems = this.register(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0441 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u043c", "\u0420\u0438\u0441\u0443\u0435\u0442 \u043f\u043b\u0430\u043c\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043a\u043e\u0433\u0434\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0435.", false).visible(this::isFlameSettingsVisible));
    private final ModeSetting flameColorMode = this.register((ModeSetting)new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442", "\u0421\u0432\u043e\u0439", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430").visible(this::isFlameSettingsVisible));
    private final ColorSetting flameColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.", new Color(255, 255, 255, 255)).visible(() -> this.isFlameSettingsVisible() && this.flameColorMode.is(COLOR_CUSTOM)));
    private final SliderSetting flameStrength = this.register(new SliderSetting("\u0421\u0438\u043b\u0430", "\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.").range(0.0f, 2.0f).increment(0.05f).setValue(0.85f).visible(this::isFlameSettingsVisible));
    private final SliderSetting flameRiseSpeed = this.register(new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e\u0434\u044a\u0435\u043c\u0430", "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e\u0434\u044a\u0435\u043c\u0430 \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.").range(0.0f, 2.0f).increment(0.05f).setValue(0.0f).visible(this::isFlameSettingsVisible));
    private final SliderSetting flameWobble = this.register(new SliderSetting("\u041a\u043e\u043b\u0435\u0431\u0430\u043d\u0438\u0435", "\u0411\u043e\u043a\u043e\u0432\u0430\u044f \u0442\u0443\u0440\u0431\u0443\u043b\u0435\u043d\u0442\u043d\u043e\u0441\u0442\u044c \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.").range(0.0f, 2.0f).increment(0.05f).setValue(0.65f).visible(this::isFlameSettingsVisible));
    private final SliderSetting flameLength = this.register(new SliderSetting("\u0414\u043b\u0438\u043d\u0430", "\u0414\u043b\u0438\u043d\u0430 \u0441\u043b\u0435\u0434\u0430 \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.").range(0.1f, 2.5f).increment(0.05f).setValue(0.95f).visible(this::isFlameSettingsVisible));
    private final SliderSetting flameBrightness = this.register(new SliderSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u043f\u043b\u0430\u043c\u0435\u043d\u0438 \u0440\u0443\u043a.").range(0.0f, 2.0f).increment(0.05f).setValue(0.9f).visible(this::isFlameSettingsVisible));

    public ShaderHands() {
        super("ShaderHands", "\u0428\u0435\u0439\u0434\u0435\u0440\u043d\u044b\u0435 \u0440\u0443\u043a\u0438: \u0441\u0442\u0435\u043a\u043b\u043e (\u0447\u0435\u043a\u0431\u043e\u043a\u0441) + \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u2014 \u0421\u0442\u0430\u0440\u044b\u0439 (\u0433\u043b\u043e\u0443/\u043e\u0431\u0432\u043e\u0434\u043a\u0430) \u0438\u043b\u0438 \u041d\u043e\u0432\u044b\u0439 (\u043f\u043b\u0430\u043c\u044f).", Category.VISUALS);
        instance = this;
    }

    public static ShaderHands getInstance() {
        ShaderHands shaderHands = ModuleManager.get().get(ShaderHands.class);
        return shaderHands != null ? shaderHands : instance;
    }

    public static boolean isActive() {
        ShaderHands shaderHands = ShaderHands.getInstance();
        return shaderHands != null && shaderHands.isEnabled();
    }

    private boolean isNewMode() {
        return this.renderMode.is(MODE_NEW);
    }

    @Override
    protected void onDisable() {
        HandsFlameRenderer.setFlameEnabled(false);
        HandsFlameRenderer.shutdown();
        ShaderHandsRenderer.clear();
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (tickEvent.isPre()) {
            this.syncFlameRenderer();
        }
    }

    @Override
    protected void onEnable() {
        this.syncFlameRenderer();
    }

    private static int glowFade(int n, int n2, int n3) {
        int n4 = (int)((System.currentTimeMillis() / 8L + (long)n) % 360L);
        n4 = n4 >= 180 ? 360 - n4 : n4;
        return ColorUtil.lerpColor(n2, n3, (float)n4 / 180.0f);
    }

    private boolean isOldMode() {
        return this.renderMode.is(MODE_OLD);
    }

    private boolean isGlassOn() {
        return this.glass.getValue();
    }

    private int[] glowGradientColors() {
        return new int[]{this.glowColorAt(0), this.glowColorAt(90), this.glowColorAt(180), this.glowColorAt(270)};
    }

    public static void composite() {
        boolean bl;
        ShaderHands shaderHands = ShaderHands.getInstance();
        if (shaderHands == null || !shaderHands.isEnabled()) {
            return;
        }
        boolean bl2 = shaderHands.isGlassOn();
        boolean bl3 = bl = shaderHands.isOldMode() && shaderHands.glow.getValue();
        if (!bl2 && !bl) {
            return;
        }
        int n = shaderHands.glowMode.is(OUTLINE_ONLY) ? 1 : (shaderHands.glowMode.is(GLOW_OUTLINE) ? 2 : 0);
        ShaderHandsRenderer.composite(ShaderHands.baseColor(), shaderHands.glowGradientColors(), bl2, bl, n, shaderHands.radius.getFloat(), shaderHands.outlineWidth.getFloat(), shaderHands.glowStrength.getFloat(), shaderHands.blending.getValue(), shaderHands.glassSaturation.getFloat(), shaderHands.glassWhite.getFloat(), shaderHands.glassDistort.getFloat(), shaderHands.glassTint.getFloat());
    }

    private static int baseColor() {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        return interfaceModule != null ? interfaceModule.clientPrimaryColorOpaque() : -10785543;
    }

    private void syncFlameRenderer() {
        if (!this.isNewMode()) {
            HandsFlameRenderer.setFlameEnabled(false);
            return;
        }
        HandsFlameRenderer.setFlameEnabled(this.flame.getValue());
        HandsFlameRenderer.configure(this.flameStrength.getFloat(), this.flameRiseSpeed.getFloat(), this.flameWobble.getFloat(), this.flameLength.getFloat(), this.flameBrightness.getFloat(), this.flameColorMode.is(COLOR_ITEM) ? 0 : 1, this.resolveFlameColor(), this.onlyItems.getValue(), this.flameColorMode.is(COLOR_CLIENT));
    }

    private int glowColorAt(int n) {
        int n2;
        int n3;
        if (this.glowColorMode.is(COLOR_RAINBOW)) {
            return ShaderHands.glowRainbow(n);
        }
        if (this.glowColorMode.is(COLOR_CUSTOM)) {
            n3 = ColorUtil.withAlpha(this.glowColor.getColorOpaque(), 255);
            n2 = this.glowSecondColor.getValue() ? ColorUtil.withAlpha(this.glowColor2.getColorOpaque(), 255) : n3;
        } else {
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            if (interfaceModule != null) {
                n3 = interfaceModule.clientPrimaryColorOpaque();
                n2 = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n3;
            } else {
                n3 = -10785543;
                n2 = -10785543;
            }
        }
        if (n3 == n2) {
            return n3 | 0xFF000000;
        }
        return ShaderHands.glowFade(n, n3, n2) | 0xFF000000;
    }

    private static int glowRainbow(int n) {
        int n2 = (int)((System.currentTimeMillis() / 8L + (long)n) % 360L);
        int n3 = RainbowLut.sample((int)n2, (float)1.0f, (float)1.0f);
        return 0xFF000000 | n3 & 0xFFFFFF;
    }

    private boolean isFlameSettingsVisible() {
        return this.isNewMode() && this.flame.getValue();
    }

    public static boolean isOldModeActive() {
        ShaderHands shaderHands = ShaderHands.getInstance();
        return shaderHands != null && shaderHands.isEnabled() && (shaderHands.isGlassOn() || shaderHands.isOldMode() && shaderHands.glow.getValue());
    }

    private int resolveFlameColor() {
        if (this.flameColorMode.is(COLOR_CUSTOM)) {
            return ColorUtil.withAlpha(this.flameColor.getColorOpaque(), 255);
        }
        if (this.flameColorMode.is(COLOR_CLIENT)) {
            return ShaderHands.baseColor();
        }
        if (this.flameColorMode.is(COLOR_RAINBOW)) {
            float f = (float)(System.currentTimeMillis() % 4000L) / 4000.0f;
            return 0xFF000000 | Color.HSBtoRGB(f, 0.72f, 1.0f) & 0xFFFFFF;
        }
        return ColorUtil.withAlpha(this.flameColor.getColorOpaque(), 255);
    }

    public static boolean isNewModeActive() {
        ShaderHands shaderHands = ShaderHands.getInstance();
        return shaderHands != null && shaderHands.isEnabled() && shaderHands.isNewMode() && shaderHands.flame.getValue();
    }
}

