package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;

public final class WatermarkModule
extends InterfaceComponentModule {
    public final BooleanSetting showNick = this.register(new BooleanSetting("\u041d\u0438\u043a", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430 \u0432 Watermark.", true));
    public final BooleanSetting showTime = this.register(new BooleanSetting("\u0412\u0440\u0435\u043c\u044f", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u0440\u0435\u043c\u044f \u0432 Watermark.", true));

    public WatermarkModule() {
        super("Watermark", "\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u043c\u044b\u0439 \u0432\u043e\u0442\u0435\u0440\u043c\u0430\u0440\u043a \u043a\u043b\u0438\u0435\u043d\u0442\u0430 \u0441 fps/tps.");
    }
}

