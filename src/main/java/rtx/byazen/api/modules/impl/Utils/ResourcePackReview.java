package rtx.byazen.api.modules.impl.Utils;

import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.respack.ResourcePackAudit;

/**
 * Ревизор ресурспаков (идея №115 из IDEAS.md).
 * <p>
 * Проверяет наборы в папке resourcepacks: версию упаковки, число файлов и размер, самые крупные
 * текстуры, ошибки упаковки и перекрытия между наборами. При входе в игру коротко сообщает итог, а
 * полный отчёт можно вывести в чат или отправить в уведомление.
 */
public final class ResourcePackReview
extends Module {

    public final SeparatorSetting autoGroup = this.register(new SeparatorSetting("Проверка"));
    public final BooleanSetting autoCheck = this.register(new BooleanSetting("Проверять при входе", "Один раз за вход в игру проверять наборы ресурсов.", true));
    public final SliderSetting maxSize = this.register(new SliderSetting("Предел размера, МБ", "Наборы крупнее этого размера отмечаются в отчёте.")
            .range(50, 800).increment(10).setValue(250));
    public final BooleanSetting bigTextures = this.register(new BooleanSetting("Следить за текстурами", "Отмечать текстуры крупнее 1024 пикселей — они и грузят память.", true));
    public final SeparatorSetting manualGroup = this.register(new SeparatorSetting("Вручную"));
    public final ButtonSetting runNow = this.register(new ButtonSetting("Проверить сейчас", "Пересчитать наборы и вывести отчёт в чат.").label("Проверить").onClick(ResourcePackReview::report));
    public final ButtonSetting shortSummary = this.register(new ButtonSetting("Краткий итог", "Одна строка: сколько наборов и есть ли замечания.").label("Итог").onClick(ResourcePackReview::summary));
    public final ButtonSetting openFolder = this.register(new ButtonSetting("Открыть папку ресурспаков", "Открыть папку resourcepacks в проводнике.").label("Папка").onClick(ResourcePackReview::openFolder));

    private boolean checked;

    public ResourcePackReview() {
        super("ResourcePackReview", "Ревизор ресурспаков: версия, размер, крупные текстуры, перекрытия и ошибки упаковки.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        this.checked = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null || this.mc.player == null || this.checked) {
            return;
        }
        if (this.mc.player.age < 40) {
            return;
        }
        this.checked = true;
        if (!this.autoCheck.getValue()) {
            return;
        }
        String quick = ResourcePackAudit.quick();
        if (quick.isEmpty()) {
            ChatMessage.send("Ресурспаки: " + ResourcePackAudit.summary());
            return;
        }
        ChatMessage.send("Ресурспаки требуют внимания: " + quick);
        ChatMessage.send("Полный отчёт — в модуле «Ревизор ресурспаков».");
    }

    private static void report() {
        List<String> lines = ResourcePackAudit.report();
        for (String line : lines) {
            ChatMessage.send(line);
        }
    }

    private static void summary() {
        ChatMessage.send("Ресурспаки: " + ResourcePackAudit.summary());
    }

    private static void openFolder() {
        Path folder = ResourcePackAudit.directory();
        try {
            java.nio.file.Files.createDirectories(folder);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(folder.toFile());
                ChatMessage.send("Папка ресурспаков открыта: " + folder);
                return;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.keyboard.setClipboard(folder.toString());
            }
            ChatMessage.send("Путь к папке ресурспаков скопирован: " + folder);
        }
        catch (Throwable throwable) {
            ChatMessage.send("Не удалось открыть папку ресурспаков.");
        }
    }
}
