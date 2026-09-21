package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Отдельный от ванильного масштаб интерфейса клиента (идея №34 из IDEAS.md):
 * удобно на 4K и ultrawide, когда ванильные «маленький/обычный/большой» не подходят.
 */
public final class UiScaleModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Масштаб клиента"));
    public final SliderSetting scale = this.register(new SliderSetting("Масштаб", "Размер интерфейса клиента: 0.5 — мельче вдвое, 2.0 — крупнее вдвое.")
            .range(0.5f, 2.0f).increment(0.05f).setValue(1.0f));
    public final BooleanSetting applyToClickGui = this.register(new BooleanSetting("Менять ClickGui", "Масштабировать меню клиента (ClickGui и экраны ByAzen).", true));
    public final BooleanSetting resetOnDrag = this.register(new BooleanSetting("Сброс при расстановке HUD", "На время перетаскивания виджетов масштаб выключается, чтобы мышь и виджеты совпадали.", true));

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public UiScaleModule() {
        super("UI Scale", "Свой масштаб интерфейса клиента для 4K и широких мониторов.");
    }
}
