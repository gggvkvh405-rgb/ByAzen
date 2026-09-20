package rtx.kimiko.api.modules.impl.Interface;
import java.awt.Color;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.api.ui.theme.ThemeManager;

public final class InterfaceModule
extends Module {
    public static final String CLIENT_COLOR_THEMES = "\u0422\u0435\u043c\u044b";
    public static final String CLIENT_COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    public static final String CLIENT_COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    public static final String GRADIENT_HORIZONTAL = "\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u044b\u0439";
    public static final String GRADIENT_BLOBS = "\u0416\u0438\u0434\u043a\u0438\u0435 \u043f\u044f\u0442\u043d\u0430";
    public static final String GRADIENT_SQUARE = "\u041f\u043e \u043a\u0432\u0430\u0434\u0440\u0430\u0442\u0443";
    private static final float THEME_COLOR_ALPHA = 204.0f;
    private static InterfaceModule instance;
    private final int[] rainbowPalette = new int[9];
    private final SeparatorSetting rectLayout = this.register(new SeparatorSetting("\u041a\u043b\u0438\u0435\u043d\u0442-\u0440\u0435\u043a\u0442"));
    public final NumberSetting rectCornerRadius = this.register(new NumberSetting("\u0421\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u0435 \u0443\u0433\u043b\u043e\u0432", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u044f \u0443\u0433\u043b\u043e\u0432 \u043e\u0431\u0449\u0438\u0445 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u043e\u0432 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430.", 7.0, 2.0, 10.0, 1.0));
    private final SeparatorSetting rectBackdrop = this.register(new SeparatorSetting("\u0424\u043e\u043d"));
    public final NumberSetting rectBackdropBlur = this.register(new NumberSetting("\u0420\u0430\u0437\u043c\u044b\u0442\u0438\u0435 \u0444\u043e\u043d\u0430", "\u0420\u0430\u0434\u0438\u0443\u0441 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f \u0444\u043e\u043d\u0430 \u0437\u0430 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430\u043c\u0438 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430.", 18.0, 0.0, 64.0, 1.0));
    public final NumberSetting rectRefractionStrength = this.register(new NumberSetting("\u041f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0438\u043b\u044c\u043d\u043e \u0441\u0442\u0435\u043a\u043b\u043e \u0438\u0441\u043a\u0430\u0436\u0430\u0435\u0442 \u0440\u0430\u0437\u043c\u044b\u0442\u044b\u0439 \u0444\u043e\u043d.", 0.3, 0.0, 0.8, 0.01));
    private final SeparatorSetting rectColors = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442\u0430"));
    public final ModeSetting gradientStyle = this.register(new ModeSetting("\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", "\u0421\u043f\u043e\u0441\u043e\u0431 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430 \u0432 \u043a\u043b\u0438\u0435\u043d\u0442-\u0440\u0435\u043a\u0442\u0430\u0445. \u00ab\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u044b\u0439\u00bb \u2014 \u0431\u0435\u0433\u0443\u0449\u0438\u0439 \u043f\u0435\u0440\u0435\u043b\u0438\u0432 \u043f\u043e \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u0438; \u00ab\u0416\u0438\u0434\u043a\u0438\u0435 \u043f\u044f\u0442\u043d\u0430\u00bb \u2014 \u0434\u0440\u0435\u0439\u0444\u0443\u044e\u0449\u0438\u0435 \u0446\u0432\u0435\u0442\u043e\u0432\u044b\u0435 \u043a\u0430\u043f\u043b\u0438; \u00ab\u041f\u043e \u043a\u0432\u0430\u0434\u0440\u0430\u0442\u0443\u00bb \u2014 \u0446\u0432\u0435\u0442\u0430 \u043f\u043e 4 \u0443\u0433\u043b\u0430\u043c.", "\u0416\u0438\u0434\u043a\u0438\u0435 \u043f\u044f\u0442\u043d\u0430", "\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u044b\u0439", "\u0416\u0438\u0434\u043a\u0438\u0435 \u043f\u044f\u0442\u043d\u0430", "\u041f\u043e \u043a\u0432\u0430\u0434\u0440\u0430\u0442\u0443"));
    public final ModeSetting clientColorMode = this.register(new ModeSetting("\u0426\u0432\u0435\u0442 \u043a\u043b\u0438\u0435\u043d\u0442\u0430", "\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a \u0430\u043a\u0446\u0435\u043d\u0442\u043d\u043e\u0433\u043e \u0446\u0432\u0435\u0442\u0430 \u043a\u043b\u0438\u0435\u043d\u0442\u0430. \u00abThemes\u00bb \u0441\u043b\u0435\u0434\u0443\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u0439 \u0442\u0435\u043c\u0435 GUI; \u00abCustom\u00bb \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442 \u0446\u0432\u0435\u0442\u0430 \u043d\u0438\u0436\u0435; \u00abRainbow\u00bb \u043f\u0440\u043e\u043a\u0440\u0443\u0447\u0438\u0432\u0430\u0435\u0442 \u044f\u0440\u043a\u0438\u0439 \u0430\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439 \u0441\u043f\u0435\u043a\u0442\u0440.", "\u0422\u0435\u043c\u044b", "\u0422\u0435\u043c\u044b", "\u0421\u0432\u043e\u0439", "\u0420\u0430\u0434\u0443\u0433\u0430"));
    public final NumberSetting rainbowSpeed = this.register(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u0430\u0434\u0443\u0433\u0438", "\u041a\u0430\u043a \u0431\u044b\u0441\u0442\u0440\u043e \u0440\u0430\u0434\u0443\u0433\u0430 \u043f\u0440\u043e\u043a\u0440\u0443\u0447\u0438\u0432\u0430\u0435\u0442 \u043e\u0442\u0442\u0435\u043d\u043a\u0438.", 1.0, 0.1, 4.0, 0.1).visibleWhen(this::isRainbowClientColor));
    public final NumberSetting rainbowSpread = this.register(new NumberSetting("\u0420\u0430\u0437\u0431\u0440\u043e\u0441 \u0440\u0430\u0434\u0443\u0433\u0438", "\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u043c\u0435\u0436\u0434\u0443 \u043e\u0442\u0442\u0435\u043d\u043a\u0430\u043c\u0438 \u043d\u0430 \u043a\u043e\u043d\u0446\u0430\u0445 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430 \u2014 \u0431\u043e\u043b\u044c\u0448\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u043e\u0445\u0432\u0430\u0442\u044b\u0432\u0430\u0435\u0442 \u0431\u043e\u043b\u044c\u0448\u0435 \u0441\u043f\u0435\u043a\u0442\u0440\u0430.", 0.18, 0.02, 0.5, 0.01).visibleWhen(this::isRainbowClientColor));
    public final NumberSetting rainbowSaturation = this.register(new NumberSetting("\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0440\u0430\u0434\u0443\u0433\u0438", "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0446\u0432\u0435\u0442\u0430 \u0440\u0430\u0434\u0443\u0433\u0438.", 0.85, 0.3, 1.0, 0.01).visibleWhen(this::isRainbowClientColor));
    public final ColorSetting rectColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u043e\u0442\u0442\u0435\u043d\u043e\u043a \u0441\u0442\u0435\u043a\u043b\u0430 \u0438 \u0446\u0432\u0435\u0442 \u043a\u0440\u0430\u044f.", new Color(-857872385, true)).visibleWhen(this::isCustomClientColor));
    public final BooleanSetting rectUseSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0412\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0432\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0434\u043b\u044f \u0443\u0433\u043b\u043e\u0432\u043e\u0433\u043e \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430.", false).visibleWhen(this::isCustomClientColor));
    public final ColorSetting rectSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430 \u0441\u0442\u0435\u043a\u043b\u0430.", new Color(-855690602, true)).visibleWhen(() -> this.isCustomClientColor() && this.rectUseSecondColor.getValue()));
    public final BooleanSetting rectColorMovement = this.register(new BooleanSetting("\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435 \u0446\u0432\u0435\u0442\u0430", "\u0410\u043d\u0438\u043c\u0438\u0440\u0443\u0435\u0442 \u0434\u0432\u0443\u0445\u0446\u0432\u0435\u0442\u043d\u044b\u0439 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442 \u0432\u043e\u043a\u0440\u0443\u0433 \u0443\u0433\u043b\u043e\u0432 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u0430.", false).visibleWhen(this::usesSecondClientColor));
    private final SeparatorSetting rectRefraction = this.register(new SeparatorSetting("\u041a\u0440\u0430\u0439 \u0441\u0442\u0435\u043a\u043b\u0430"));
    public final NumberSetting rectEdgeStrength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u043a\u0440\u0430\u044f", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0438\u043b\u044c\u043d\u043e \u0446\u0432\u0435\u0442 \u043a\u0440\u0430\u044f \u043f\u043e\u0434\u043c\u0435\u0448\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 \u0441\u0442\u0435\u043a\u043b\u043e.", 0.18, 0.0, 1.0, 0.01));
    public final NumberSetting rectEdgeSharpness = this.register(new NumberSetting("\u0420\u0435\u0437\u043a\u043e\u0441\u0442\u044c \u043a\u0440\u0430\u044f", "\u0427\u0435\u043c \u0432\u044b\u0448\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435, \u0442\u0435\u043c \u0442\u043e\u043d\u044c\u0448\u0435 \u0438 \u0440\u0435\u0437\u0447\u0435 \u0431\u043b\u0438\u043a.", 55.0, 2.0, 100.0, 1.0).visibleWhen(() -> this.rectEdgeStrength.getFloat() > 0.0f));
    private final SeparatorSetting rectGlowSeparator = this.register(new SeparatorSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435"));
    public final BooleanSetting rectGlow = this.register(new BooleanSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u0420\u0438\u0441\u0443\u0435\u0442 \u043c\u044f\u0433\u043a\u043e\u0435 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0432\u043e\u043a\u0440\u0443\u0433 \u043f\u0440\u044f\u043c\u043e\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a\u043e\u0432 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430.", false));
    public final NumberSetting rectGlowIntensity = this.register(new NumberSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f.", 0.6, 0.0, 2.0, 0.05).visibleWhen(this.rectGlow::getValue));
    public final NumberSetting rectGlowRadius = this.register(new NumberSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0434\u0430\u043b\u0435\u043a\u043e \u0440\u0430\u0441\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435.", 15.0, 15.0, 70.0, 1.0).visibleWhen(this.rectGlow::getValue));
    private final SeparatorSetting miscSeparator = this.register(new SeparatorSetting("\u041f\u0440\u043e\u0447\u0435\u0435"));
    public final BooleanSetting hudIcons = this.register(new BooleanSetting("\u0418\u043a\u043e\u043d\u043a\u0438", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0438\u043a\u043e\u043d\u043a\u0443 \u0441\u043f\u0440\u0430\u0432\u0430 \u0432 \u0437\u0430\u0433\u043e\u043b\u043e\u0432\u043a\u0435 HUD-\u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432, \u0430 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0441\u0434\u0432\u0438\u0433\u0430\u0435\u0442 \u0432\u043b\u0435\u0432\u043e.", false));
    public final ModeSetting dragStyle = this.register(new ModeSetting("\u041f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0435", "\u041e\u0431\u044b\u0447\u043d\u043e\u0435 \u0441\u0440\u0430\u0437\u0443 \u0434\u0432\u0438\u0433\u0430\u0435\u0442 \u044d\u043b\u0435\u043c\u0435\u043d\u0442, \u043f\u0440\u043e\u0435\u043a\u0446\u0438\u044f \u0441\u043d\u0430\u0447\u0430\u043b\u0430 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u043d\u043e\u0432\u043e\u0435 \u043c\u0435\u0441\u0442\u043e.", "\u041f\u0440\u043e\u0435\u043a\u0446\u0438\u044f", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041f\u0440\u043e\u0435\u043a\u0446\u0438\u044f"));
    public final BooleanSetting dragJitter = this.register(new BooleanSetting("\u0422\u0440\u044f\u0441\u043a\u0430 \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0438", "\u041b\u0451\u0433\u043a\u043e\u0435 \u0434\u0440\u043e\u0436\u0430\u043d\u0438\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430, \u043f\u043e\u043a\u0430 \u0435\u0433\u043e \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u044e\u0442. \u0412\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u0434\u043b\u044f \u0441\u0442\u0430\u0442\u0438\u0447\u043d\u043e\u0433\u043e \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u044f.", true).visibleWhen(() -> this.dragStyle.is("\u041f\u0440\u043e\u0435\u043a\u0446\u0438\u044f")));
    public final BooleanSetting dragWaves = this.register(new BooleanSetting("\u0412\u043e\u043b\u043d\u044b \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0438", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0448\u0435\u0439\u0434\u0435\u0440\u043d\u044b\u0435 \u0432\u043e\u043b\u043d\u044b \u043f\u043e\u0437\u0430\u0434\u0438 \u0437\u0430\u0436\u0430\u0442\u043e\u0433\u043e \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430.", true).visibleWhen(() -> this.dragStyle.is("\u041f\u0440\u043e\u0435\u043a\u0446\u0438\u044f")));
    public final BooleanSetting dragTilt = this.register(new BooleanSetting("\u041d\u0430\u043a\u043b\u043e\u043d \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0438", "\u041f\u043b\u0430\u0432\u043d\u043e \u043d\u0430\u043a\u043b\u043e\u043d\u044f\u0435\u0442 \u044d\u043b\u0435\u043c\u0435\u043d\u0442 \u0432 \u0441\u0442\u043e\u0440\u043e\u043d\u0443 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f \u043f\u0440\u0438 \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0438.", true).visibleWhen(() -> this.dragStyle.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439")));

    public InterfaceModule() {
        super("Interface", "\u041e\u0431\u0449\u0438\u0439 \u0441\u0442\u0438\u043b\u044c (\u0441\u0442\u0435\u043a\u043b\u043e, \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435, \u0446\u0432\u0435\u0442\u0430) \u0434\u043b\u044f \u0432\u0441\u0435\u0445 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430.", Category.DISPLAY);
        instance = this;
    }

    public static InterfaceModule getInstance() {
        return instance;
    }

    private static int lerpWhite(int n, float f) {
        float f2 = Math.max(0.0f, Math.min(1.0f, f));
        int n2 = Math.round(255.0f + (float)((n >> 16 & 0xFF) - 255) * f2);
        int n3 = Math.round(255.0f + (float)((n >> 8 & 0xFF) - 255) * f2);
        int n4 = Math.round(255.0f + (float)((n & 0xFF) - 255) * f2);
        return n2 << 16 | n3 << 8 | n4;
    }

    public int clientPrimaryColorOpaque() {
        return 0xFF000000 | this.clientPrimaryColor() & 0xFFFFFF;
    }

    public boolean usesSecondClientColor() {
        return this.isThemeClientColor() || this.isRainbowClientColor() || this.rectUseSecondColor.getValue();
    }

    public int clientSecondaryColorOpaque() {
        return 0xFF000000 | this.clientSecondaryColor() & 0xFFFFFF;
    }

    private int[] rainbowPalette() {
        float f = this.rainbowSaturation.getFloat();
        float f2 = Math.min(1.0f, this.rainbowSpread.getFloat() * 4.0f);
        float f3 = ClientAccent.rainbowBaseHue();
        int n = this.rainbowPalette.length;
        for (int i = 0; i < n; ++i) {
            float f4 = f3 + (float)i / (float)(n - 1) * f2;
            f4 -= (float)Math.floor(f4);
            int n2 = Color.HSBtoRGB(f4, 1.0f, 1.0f) & 0xFFFFFF;
            this.rainbowPalette[i] = InterfaceModule.lerpWhite(n2, f);
        }
        return this.rainbowPalette;
    }

    public boolean clientColorMovement() {
        return this.usesSecondClientColor() && this.rectColorMovement.getValue();
    }

    public int clientPrimaryColor() {
        return ClientAccent.gradientA(204.0f);
    }

    public boolean isThemeClientColor() {
        return this.clientColorMode.is(CLIENT_COLOR_THEMES);
    }

    public int gradientStyleId() {
        if (this.gradientStyle.is(GRADIENT_BLOBS)) {
            return 1;
        }
        return this.gradientStyle.is(GRADIENT_SQUARE) ? 2 : 0;
    }

    public int[] clientPalette() {
        int[] nArray;
        if (this.isRainbowClientColor()) {
            return this.rainbowPalette();
        }
        if (this.isThemeClientColor() && (nArray = ThemeManager.blendedPalette()) != null && nArray.length > 0) {
            return nArray;
        }
        if (this.rectUseSecondColor.getValue()) {
            return new int[]{this.rectColor.getColor() & 0xFFFFFF, this.rectSecondColor.getColor() & 0xFFFFFF};
        }
        return new int[]{this.rectColor.getColor() & 0xFFFFFF};
    }

    public boolean isRainbowClientColor() {
        return this.clientColorMode.is(CLIENT_COLOR_RAINBOW);
    }

    public int clientSecondaryColor() {
        return ClientAccent.gradientB(204.0f);
    }

    public boolean isCustomClientColor() {
        return this.clientColorMode.is(CLIENT_COLOR_CUSTOM);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }
}

