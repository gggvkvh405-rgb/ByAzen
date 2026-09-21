package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;

/**
 * Полные темы клиента (идея №50 из IDEAS.md): тёмная, светлая и неоновая.
 * Меняется не только акцент, но и подложки, цвет текста и ванильные экраны.
 */
public final class ThemeLookModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Тема оформления"));
    public final ModeSetting look = this.register(new ModeSetting("Тема", "Тёмная — как раньше; светлая — светлые панели и тёмный текст; неоновая — глубокий фон и свечение акцента.",
            "Тёмная", "Тёмная", "Светлая", "Неоновая"));
    public final BooleanSetting recolorText = this.register(new BooleanSetting("Перекрашивать текст", "Подстраивать цвет надписей под тему, чтобы всё читалось (в светлой теме светлый текст становится тёмным).", true));
    public final BooleanSetting panels = this.register(new BooleanSetting("Перекрашивать панели", "Подстраивать стеклянные подложки клиента под выбранную тему.", true));
    public final BooleanSetting vanillaScreens = this.register(new BooleanSetting("Ванильные экраны", "Подкрашивать инвентарь, сундуки и ванильные меню под тему клиента.", true));
    public final BooleanSetting neonGlow = this.register(new BooleanSetting("Неоновое свечение", "В неоновой теме подложки получают цветной ореол.", true).visible(() -> this.look.is("Неоновая")));

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    public ThemeLookModule() {
        super("Theme", "Полные темы: тёмная, светлая, неоновая — вместе с ванильными экранами.");
    }
}
