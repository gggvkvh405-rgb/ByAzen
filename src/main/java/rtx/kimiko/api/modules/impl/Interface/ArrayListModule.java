package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;

public final class ArrayListModule
extends InterfaceComponentModule {
    private final SliderSetting waveSpeedSetting = this.register(new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u043e\u043b\u043d\u044b", "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043b\u0438\u0432\u0430: \u0431\u043e\u043b\u044c\u0448\u0435 \u2014 \u0431\u044b\u0441\u0442\u0440\u0435\u0435.").setValue(18.0f).range(1, 100).increment(1));
    private final MultiSelectSetting categories = this.register(new MultiSelectSetting("\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438", "\u041c\u043e\u0434\u0443\u043b\u0438 \u043a\u0430\u043a\u0438\u0445 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0439 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432 \u0441\u043f\u0438\u0441\u043a\u0435.").value(Category.VISUALS.getDisplayName(), Category.DISPLAY.getDisplayName(), Category.UTILS.getDisplayName()).selected(Category.VISUALS.getDisplayName(), Category.UTILS.getDisplayName()));

    public ArrayListModule() {
        super("ArrayList", "\u041f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0435\u043c\u044b\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u043a\u043b\u044e\u0447\u0451\u043d\u043d\u044b\u0445 \u043c\u043e\u0434\u0443\u043b\u0435\u0439.");
    }

    public double waveSpeed() {
        return (double)this.waveSpeedSetting.getInt() * 1.0E-4;
    }

    public boolean categoryShown(Category category) {
        return category != null && this.categories.is(category.getDisplayName());
    }
}

