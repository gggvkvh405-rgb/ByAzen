package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;

/**
 * Часы и дата в HUD. Идея №36 из IDEAS.md.
 */
public final class ClockModule
extends InterfaceComponentModule {

    public final SeparatorSetting appearance = this.register(new SeparatorSetting("Часы"));
    public final BooleanSetting h24 = this.register(new BooleanSetting("24-часовой формат", "Показывать время в 24-часовом формате.", true));
    public final BooleanSetting seconds = this.register(new BooleanSetting("Секунды", "Показывать секунды.", false));
    public final BooleanSetting showDate = this.register(new BooleanSetting("Дата", "Показывать дату под временем.", true));
    public final SelectSetting icon = this.register(new SelectSetting("Иконка", "Векторная иконка рядом с часами (идея №35).").value(rtx.byazen.utils.render.icons.IconLibrary.NAMES).selected(rtx.byazen.utils.render.icons.IconLibrary.NAMES[8]));

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public ClockModule() {
        super("Clock", "Часы и дата в HUD: свой формат, секунды, дата. Виджет таскается в редакторе интерфейса.");
    }
}
