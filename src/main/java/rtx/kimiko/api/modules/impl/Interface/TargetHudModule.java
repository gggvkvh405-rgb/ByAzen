package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;

public final class TargetHudModule
extends InterfaceComponentModule {
    private static final String MODE_CLASSIC = "\u041a\u043b\u0430\u0441\u0441\u0438\u0447\u0435\u0441\u043a\u0438\u0439";
    private static final String MODE_NEW = "\u041d\u043e\u0432\u044b\u0439";
    private static final String BAR_FROM_HP = "\u041e\u0442 \u0445\u043f";
    private static final String BAR_WHITE = "\u0411\u0435\u043b\u0430\u044f";
    private static final String BAR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442\u0441\u043a\u0438\u0439";
    public final ModeSetting hudMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434 TargetHud.", "\u041a\u043b\u0430\u0441\u0441\u0438\u0447\u0435\u0441\u043a\u0438\u0439", "\u041a\u043b\u0430\u0441\u0441\u0438\u0447\u0435\u0441\u043a\u0438\u0439", "\u041d\u043e\u0432\u044b\u0439"));
    public final ModeSetting barColorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u043f\u043e\u043b\u043e\u0441\u044b \u0445\u043f", "\u0426\u0432\u0435\u0442 \u0437\u0430\u043b\u0438\u0432\u043a\u0438 \u043f\u043e\u043b\u043e\u0441\u044b \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f.", "\u041e\u0442 \u0445\u043f", "\u041e\u0442 \u0445\u043f", "\u0411\u0435\u043b\u0430\u044f", "\u041a\u043b\u0438\u0435\u043d\u0442\u0441\u043a\u0438\u0439"));
    public final BooleanSetting showArmor = this.register(new BooleanSetting("\u0411\u0440\u043e\u043d\u044f", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0431\u0440\u043e\u043d\u044e \u0438 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0440\u0443\u043a\u0430\u0445 \u0446\u0435\u043b\u0438.", true));
    public final BooleanSetting followTarget = this.register(new BooleanSetting("\u0421\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u044c", "HUD \u043f\u043b\u0430\u0432\u043d\u043e \u0441\u043b\u0435\u0434\u0443\u0435\u0442 \u0437\u0430 \u0446\u0435\u043b\u044c\u044e \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435 \u0438 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442\u0441\u044f \u043d\u0430 \u0441\u0432\u043e\u0451 \u043c\u0435\u0441\u0442\u043e, \u043a\u043e\u0433\u0434\u0430 \u0446\u0435\u043b\u044c \u0432\u043d\u0435 \u044d\u043a\u0440\u0430\u043d\u0430.", false));

    public TargetHudModule() {
        super("TargetHud", "\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u043c\u044b\u0439 HUD \u0441 \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u0435\u0439 \u043e \u0446\u0435\u043b\u0438.");
    }

    public boolean showArmor() {
        return this.showArmor.getValue();
    }

    public boolean isNewMode() {
        return this.hudMode.is(MODE_NEW);
    }

    public boolean barWhite() {
        return this.barColorMode.is(BAR_WHITE);
    }

    public boolean barClient() {
        return this.barColorMode.is(BAR_CLIENT);
    }

    public boolean followTarget() {
        return this.followTarget.getValue();
    }
}

