package rtx.kimiko.api.modules.impl.Visuals;
import java.awt.Color;
import net.minecraft.client.gl.Framebuffer;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.post.fogblur.FogBlurRenderer;

public class FogBlur
extends Module {
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u041a\u0430\u0441\u0442\u043e\u043c";
    private static FogBlur instance;
    private final NumberSetting fogStrength = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f", "\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f \u0434\u043b\u044f \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0442\u0443\u043c\u0430\u043d\u0430", 6.0, 1.0, 20.0, 1.0));
    private final NumberSetting fogDistance = this.register(new NumberSetting("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0442\u0443\u043c\u0430\u043d\u0430", "\u041d\u0430 \u043a\u0430\u043a\u043e\u0439 \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u0438 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f \u044d\u0444\u0444\u0435\u043a\u0442 \u0442\u0443\u043c\u0430\u043d\u0430", 50.0, 0.0, 200.0, 5.0));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u043d\u0438\u044f \u0442\u0443\u043c\u0430\u043d\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u041a\u0430\u0441\u0442\u043e\u043c"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u041a\u0430\u0441\u0442\u043e\u043c", false).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM)));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0442\u0443\u043c\u0430\u043d\u0430", new Color(255, 255, 255)).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0442\u0443\u043c\u0430\u043d\u0430", new Color(87, 87, 87, 129)).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue()));
    private final NumberSetting colorOpacity = this.register(new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0446\u0432\u0435\u0442\u0430", "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u043d\u0438\u044f \u0442\u0443\u043c\u0430\u043d\u0430", 30.0, 1.0, 100.0, 1.0));

    public FogBlur() {
        super("Fog Blur", "\u0420\u0430\u0437\u043c\u044b\u0432\u0430\u0435\u0442 \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u043f\u0438\u043a\u0441\u0435\u043b\u0438 \u043c\u0438\u0440\u0430 \u043f\u043e \u0433\u043b\u0443\u0431\u0438\u043d\u0435, \u0441\u043e\u0437\u0434\u0430\u0432\u0430\u044f \u044d\u0444\u0444\u0435\u043a\u0442 \u0442\u0443\u043c\u0430\u043d\u0430.", Category.VISUALS);
        instance = this;
    }

    public static FogBlur getInstance() {
        if (instance == null) {
            instance = ModuleManager.get().get(FogBlur.class);
        }
        return instance;
    }

    @Override
    protected void onDisable() {
        FogBlurRenderer.clear();
    }

    public int getCustomFogColor() {
        return -1;
    }

    public void onAfterTranslucent(Framebuffer framebuffer) {
        if (this.mc.player == null || this.mc.world == null || this.mc.gameRenderer == null) {
            return;
        }
        if (FogBlurRenderer.isDisabledAfterError()) {
            return;
        }
        int n = this.resolveTintColor();
        float f = this.colorOpacity.getFloat() / 100.0f;
        FogBlurRenderer.setBlurTint((float)(n >> 16 & 0xFF) / 255.0f, (float)(n >> 8 & 0xFF) / 255.0f, (float)(n & 0xFF) / 255.0f, f);
        FogBlurRenderer.apply(framebuffer, this.fogStrength.getFloat(), Math.max(1.0f, this.fogDistance.getFloat()), 100.0f, 0.52f, 2);
    }

    public boolean hasCustomFogDistance() {
        return false;
    }

    private int resolveTintColor() {
        if (this.colorMode.is(COLOR_CLIENT)) {
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            return interfaceModule != null ? interfaceModule.clientPrimaryColorOpaque() : -1;
        }
        if (this.colorMode.is(COLOR_CUSTOM)) {
            int n = this.customColor.getColor();
            if (!this.useSecondColor.getValue()) {
                return n;
            }
            float f = 0.5f + 0.5f * (float)Math.sin((double)System.currentTimeMillis() / 1200.0);
            return ColorUtil.lerpColor(n, this.customSecondColor.getColor(), f);
        }
        return ClientAccent.rainbowFlow(0.0f, 255.0f);
    }

    public float getFogDistanceFactor() {
        return 1.0f;
    }

    public boolean hasCustomFogColor() {
        return false;
    }
}

