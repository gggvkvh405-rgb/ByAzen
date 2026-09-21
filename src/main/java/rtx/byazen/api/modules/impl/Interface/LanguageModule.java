package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.lang.Lang;

/**
 * Язык интерфейса клиента (идея №44 из IDEAS.md): русский или английский.
 * Переключение применяется сразу к экранам клиента и подсказкам; подписи настроек читаются один раз
 * при старте, поэтому их язык меняется после перезахода в игру.
 */
public final class LanguageModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Язык"));
    public final ModeSetting language = this.register(new ModeSetting("Язык интерфейса", "Русский — как раньше; English — английские подписи в экранах клиента и подсказках.",
            Lang.RU, Lang.RU, Lang.EN));

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    public LanguageModule() {
        super("Language", "Переключатель RU/EN для интерфейса клиента: экраны, подсказки, уведомления.");
    }
}
