package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Группы виджетов (идея №31 из IDEAS.md): виджеты, выстроенные в колонку (или строку),
 * получают одну общую подложку и читаются как один блок.
 */
public final class HudGroupsModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Группы виджетов"));
    public final ModeSetting direction = this.register(new ModeSetting("Направление", "Как группировать виджеты: в колонку или в строку.", "Вертикально", "Вертикально", "Горизонтально"));
    public final SliderSetting tolerance = this.register(new SliderSetting("Допуск выравнивания", "Насколько виджеты могут быть смещены друг относительно друга, чтобы считаться одной группой.")
            .range(1.0f, 40.0f).increment(1.0f).setValue(8.0f));
    public final SliderSetting gap = this.register(new SliderSetting("Отступ в группе", "Расстояние между виджетами внутри группы, на которое рассчитывается подложка.")
            .range(0.0f, 24.0f).increment(1.0f).setValue(6.0f));
    public final SliderSetting padding = this.register(new SliderSetting("Поля подложки", "Насколько подложка выходит за границы виджетов.")
            .range(2.0f, 20.0f).increment(1.0f).setValue(7.0f));
    public final SliderSetting radius = this.register(new SliderSetting("Скругление", "Радиус скругления общей подложки.")
            .range(4.0f, 20.0f).increment(1.0f).setValue(9.0f));
    public final SliderSetting alpha = this.register(new SliderSetting("Плотность", "Плотность подложки в процентах.")
            .range(20.0f, 100.0f).increment(5.0f).setValue(85.0f));
    public final BooleanSetting backdrop = this.register(new BooleanSetting("Стеклянная подложка", "Использовать общую стеклянную подложку клиента, а не плоскую тёмную.", true));
    public final BooleanSetting frame = this.register(new BooleanSetting("Обводка группы", "Тонкая акцентная рамка вокруг группы.", true));
    public final BooleanSetting separators = this.register(new BooleanSetting("Разделители", "Тонкие линии между виджетами внутри группы.", false));

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public HudGroupsModule() {
        super("HUD Groups", "Объединяет виджеты, стоящие в одной колонке, в один блок с общей подложкой.");
    }
}
