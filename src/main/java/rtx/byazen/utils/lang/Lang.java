package rtx.byazen.utils.lang;

import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.LanguageModule;

/**
 * Переключатель RU/EN (идея №44 из IDEAS.md): интерфейс клиента может быть на русском или английском.
 * <p>
 * Значение кэшируется: строки запрашивают перевод в каждой отрисовке, лишние обходы списка модулей не нужны.
 */
public final class Lang {

    public static final String RU = "Русский";
    public static final String EN = "English";
    private static volatile long cachedAt;
    private static volatile boolean english;

    private Lang() {
    }

    public static boolean isEnglish() {
        long now = System.currentTimeMillis();
        if (now - Lang.cachedAt < 500L) {
            return Lang.english;
        }
        LanguageModule module = ModuleManager.get().get(LanguageModule.class);
        Lang.english = module != null && module.isEnabled() && module.language.is(EN);
        Lang.cachedAt = now;
        return Lang.english;
    }

    /** Перевод строки: русский вариант для RU, английский для EN. */
    public static String t(String russian, String english) {
        return Lang.isEnglish() ? english : russian;
    }
}
