package rtx.byazen.api.modules.impl.Utils;

import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ConfigBackups;

/**
 * Автобэкапы конфигов (идея №102 из IDEAS.md).
 * <p>
 * Копия настроек делается при запуске, по таймеру и перед сменой профиля или импортом конфигурации.
 * Список копий выводится в чат, восстановление — одной кнопкой, старые копии обрезаются, чтобы папка
 * резервных копий не росла бесконечно.
 */
public final class ConfigBackupModule
extends Module {

    public final SeparatorSetting autoGroup = this.register(new SeparatorSetting("Автоматически"));
    public final BooleanSetting onStart = this.register(new BooleanSetting("Копия при запуске", "Сохранять настройки при первом входе в игру.", true));
    public final BooleanSetting onTimer = this.register(new BooleanSetting("Копия по таймеру", "Сохранять настройки каждые N минут игры.", false));
    public final SliderSetting timerMinutes = this.register(new SliderSetting("Минут между копиями", "Как часто делать копию по таймеру.")
            .range(5, 180).increment(5).setValue(30).visible(() -> this.onTimer.getValue()));
    public final BooleanSetting beforeProfile = this.register(new BooleanSetting("Перед профилями", "Копия перед загрузкой профиля настроек.", true));
    public final SliderSetting keepCopies = this.register(new SliderSetting("Сколько копий хранить", "Старые копии сверх этого числа удаляются.")
            .range(3, 40).increment(1).setValue(12));

    public final SeparatorSetting manualGroup = this.register(new SeparatorSetting("Вручную"));
    public final ButtonSetting makeBackup = this.register(new ButtonSetting("Сделать копию", "Сохранить текущие настройки в резервную копию.").label("Копия").onClick(ConfigBackupModule::manualBackup));
    public final ButtonSetting restoreLatest = this.register(new ButtonSetting("Восстановить последнюю", "Вернуть настройки из самой свежей копии.").label("Вернуть").onClick(ConfigBackupModule::restoreLatest));
    public final ButtonSetting listBackups = this.register(new ButtonSetting("Показать список", "Вывести список копий в чат.").label("Список").onClick(ConfigBackupModule::printList));
    public final ButtonSetting openFolder = this.register(new ButtonSetting("Открыть папку копий", "Открыть папку с резервными копиями в проводнике.").label("Папка").onClick(ConfigBackupModule::openFolder));

    private long lastTimerMs;

    public ConfigBackupModule() {
        super("ConfigBackups", "Автобэкапы конфигов: копии при запуске, по таймеру, вручную и перед сменой профиля.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        this.lastTimerMs = System.currentTimeMillis();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null || this.mc.player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        String stamp = "start";
        if (this.lastTimerMs == 0L) {
            this.lastTimerMs = now;
            if (!this.onStart.getValue()) {
                return;
            }
        }
        else {
            if (!this.onTimer.getValue()) {
                return;
            }
            long interval = (long)this.timerMinutes.getValue() * 60000L;
            if (now - this.lastTimerMs < interval) {
                return;
            }
            this.lastTimerMs = now;
            stamp = "timer";
        }
        String name = ConfigBackups.create(stamp);
        if (!name.isEmpty()) {
            int removed = ConfigBackups.prune((int)this.keepCopies.getValue());
            ConfigBackupModule.notify("Резервная копия настроек: " + name + (removed > 0 ? " · старых удалено: " + removed : ""));
        }
    }

    /** Копия перед сменой профиля или импортом: вызывается из интерфейса настроек. */
    public static void beforeProfileChange() {
        Module module = ModuleManager.get().findByName("ConfigBackups");
        if (!(module instanceof ConfigBackupModule) || !module.isEnabled() || !((ConfigBackupModule)module).beforeProfile.getValue()) {
            return;
        }
        String name = ConfigBackupModule.backupNow("profile");
        if (!name.isEmpty()) {
            ConfigBackupModule.notify("Перед сменой профиля сохранена копия: " + name);
        }
    }

    /** Копия по требованию других модулей — например, перед импортом конфигурации. */
    public static String backupNow(String reason) {
        ConfigManager.saveAll();
        String name = ConfigBackups.create(reason);
        if (!name.isEmpty()) {
            ConfigBackups.prune(12);
        }
        return name;
    }

    private static void manualBackup() {
        ConfigManager.saveAll();
        String name = ConfigBackups.create("manual");
        if (name.isEmpty()) {
            ConfigBackupModule.notify("Не удалось сделать копию: папка настроек недоступна.");
            return;
        }
        ConfigBackupModule.notify("Копия сохранена: " + name + " · " + ConfigBackups.summary());
    }

    private static void restoreLatest() {
        List<ConfigBackups.Backup> all = ConfigBackups.list();
        if (all.isEmpty()) {
            ConfigBackupModule.notify("Резервных копий пока нет.");
            return;
        }
        ConfigBackups.Backup latest = all.get(0);
        ConfigBackupModule.notify("Перед восстановлением сохранена копия: "
                + ConfigBackupModule.backupNow("profile"));
        if (ConfigBackups.restore(latest.name)) {
            ConfigManager.loadAll();
            ConfigBackupModule.notify("Настройки восстановлены из копии " + latest.name + " (" + latest.dateText() + ").");
        }
        else {
            ConfigBackupModule.notify("Не удалось восстановить копию " + latest.name + ".");
        }
    }

    private static void printList() {
        List<ConfigBackups.Backup> all = ConfigBackups.list();
        if (all.isEmpty()) {
            ConfigBackupModule.notify("Резервных копий пока нет — они появятся при запуске игры.");
            return;
        }
        ConfigBackupModule.notify("Резервные копии: " + all.size());
        for (int i = 0; i < Math.min(8, all.size()); ++i) {
            ConfigBackups.Backup backup = all.get(i);
            ConfigBackupModule.notify("· " + backup.name + " — " + backup.dateText() + ", " + backup.reasonText() + ", " + backup.sizeText());
        }
    }

    private static void openFolder() {
        Path folder = ConfigBackups.directory();
        try {
            java.nio.file.Files.createDirectories(folder);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(folder.toFile());
                ConfigBackupModule.notify("Папка копий открыта: " + folder);
                return;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.keyboard.setClipboard(folder.toString());
            }
            ConfigBackupModule.notify("Путь к папке копий скопирован: " + folder);
        }
        catch (Throwable throwable) {
            ConfigBackupModule.notify("Не удалось открыть папку копий.");
        }
    }

    private static void notify(String message) {
        ChatMessage.send(message);
    }

    /** Подсказка для настроек. */
    public static String summary() {
        return ConfigBackups.summary();
    }
}
