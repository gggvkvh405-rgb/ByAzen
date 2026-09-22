package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.render.cache.CacheManager;

/**
 * Кэш иконок и обложек с лимитом (идея №164 из IDEAS.md).
 * <p>
 * Обложки треков, локальные картинки и кадры GIF держатся в памяти ограниченно: у каждого кэша есть
 * предел, и самый старый элемент вытесняется. Здесь пределы настраиваются, здесь же видно, сколько
 * сейчас лежит и сколько это занимает.
 */
public final class CacheLimitsModule
extends Module {

    private final SliderSetting covers = this.register(new SliderSetting("Предел: обложки", "Сколько обложек треков держать в памяти.").range(8.0f, 128.0f).increment(4.0f).setValue(48.0f));
    private final SliderSetting images = this.register(new SliderSetting("Предел: картинки", "Сколько локальных картинок держать в памяти.").range(8.0f, 128.0f).increment(4.0f).setValue(72.0f));
    private final SliderSetting gifs = this.register(new SliderSetting("Предел: GIF", "Сколько анимаций держать в памяти (кадры дорогие).").range(4.0f, 64.0f).increment(2.0f).setValue(24.0f));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Действия"));
    private final ButtonSetting apply = this.register(new ButtonSetting("Применить пределы", "Пересчитать лимиты и вытеснить лишнее.").label("Применить").onClick(CacheLimitsModule::apply));
    private final ButtonSetting clear = this.register(new ButtonSetting("Очистить всё", "Освободить всю память кэшей — картинки подгрузятся заново.").label("Очистить").onClick(CacheLimitsModule::clear));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть окно", "Сводка по кэшам — в окне «Диагностика».").label("Окно").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_CACHE)));

    private boolean applied;

    public CacheLimitsModule() {
        super("Cache Limits", "Ограничивает кэш иконок, обложек и GIF, чтобы память не росла.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        this.applyLimits();
    }

    private void applyLimits() {
        CacheManager.setLimit("covers", (int)this.covers.getFloat());
        CacheManager.setLimit("images", (int)this.images.getFloat());
        CacheManager.setLimit("gifs", (int)this.gifs.getFloat());
    }

    private static void apply() {
        CacheLimitsModule module = rtx.byazen.api.modules.ModuleManager.get().get(CacheLimitsModule.class);
        if (module != null) {
            module.applyLimits();
        }
        ChatMessage.send("Пределы кэшей: " + CacheManager.summary());
    }

    /** Возвращает пределы к заводским (кнопка в окне «Диагностика»). */
    public static void resetLimits() {
        CacheLimitsModule module = rtx.byazen.api.modules.ModuleManager.get().get(CacheLimitsModule.class);
        if (module == null) {
            return;
        }
        module.covers.setValue(48.0f);
        module.images.setValue(72.0f);
        module.gifs.setValue(24.0f);
        module.applyLimits();
    }

    private static void clear() {
        CacheManager.clearAll();
        ChatMessage.send("Кэши очищены: " + CacheManager.summary());
    }
}
