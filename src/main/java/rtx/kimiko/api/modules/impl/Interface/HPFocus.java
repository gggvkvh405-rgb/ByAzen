package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;

public final class HPFocus
extends InterfaceComponentModule {
    public final NumberSetting scale = this.register(new NumberSetting("\u041c\u0430\u0441\u0448\u0442\u0430\u0431", "\u041e\u0431\u0449\u0438\u0439 \u0440\u0430\u0437\u043c\u0435\u0440 \u043e\u043a\u043d\u0430 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435.", 1.3, 0.4, 3.0, 0.05));
    public final NumberSetting captureWidth = this.register(new NumberSetting("\u0428\u0438\u0440\u0438\u043d\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u0430", "\u0428\u0438\u0440\u0438\u043d\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u044b\u0432\u0430\u0435\u043c\u043e\u0439 \u043e\u0431\u043b\u0430\u0441\u0442\u0438 \u0445\u043e\u0442\u0431\u0430\u0440\u0430 (\u0432 \u043f\u0438\u043a\u0441\u0435\u043b\u044f\u0445 GUI).", 200.0, 120.0, 320.0, 2.0));
    public final NumberSetting captureHeight = this.register(new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u0430", "\u0412\u044b\u0441\u043e\u0442\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u044b\u0432\u0430\u0435\u043c\u043e\u0439 \u043e\u0431\u043b\u0430\u0441\u0442\u0438 \u2014 \u0445\u043e\u0442\u0431\u0430\u0440, \u0431\u0440\u043e\u043d\u044f, \u0435\u0434\u0430 \u0438 \u0445\u043f (\u0432 \u043f\u0438\u043a\u0441\u0435\u043b\u044f\u0445 GUI).", 52.0, 30.0, 110.0, 1.0));
    public final NumberSetting hpThreshold = this.register(new NumberSetting("\u041f\u043e\u0440\u043e\u0433 \u0425\u041f", "\u041f\u0440\u0438 \u043a\u0430\u043a\u043e\u043c \u0443\u0440\u043e\u0432\u043d\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f (\u0425\u041f) \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043e\u043a\u043d\u043e.", 10.0, 0.0, 20.0, 0.5));

    public HPFocus() {
        super("HPFocus", "\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u043c\u043e\u0435 \u0437\u0430\u0437\u0443\u043c\u043b\u0435\u043d\u043d\u043e\u0435 \u043e\u043a\u043d\u043e \u0445\u043e\u0442\u0431\u0430\u0440\u0430, \u0431\u0440\u043e\u043d\u0438, \u0435\u0434\u044b \u0438 \u0445\u043f \u043f\u0440\u0438 \u043d\u0438\u0437\u043a\u043e\u043c \u0425\u041f.");
    }

    public float scale() {
        return this.scale.getValue();
    }

    public float captureWidthGui() {
        return this.captureWidth.getValue();
    }

    public float hpThresholdHp() {
        return this.hpThreshold.getValue();
    }

    public float captureHeightGui() {
        return this.captureHeight.getValue();
    }
}

