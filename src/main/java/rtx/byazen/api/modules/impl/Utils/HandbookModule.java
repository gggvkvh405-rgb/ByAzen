package rtx.byazen.api.modules.impl.Utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.chat.commands.CommandManager;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.HandbookScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.handbook.Handbook;
import rtx.byazen.utils.key.KeyBind;

/**
 * Оффлайн-справочник (идея №106 из IDEAS.md).
 * <p>
 * Открывает справочник с клавиши или из настроек: команды клиента с текущим префиксом, назначенные
 * клавиши, пересчёт координат между Нижним и Верхним миром, зелья, зачарования, тайминги и мелочи
 * выживания. Всё лежит внутри клиента, поэтому работает без интернета и открывается мгновенно.
 */
public final class HandbookModule
extends Module {

    public final SeparatorSetting windowGroup = this.register(new SeparatorSetting("Окно"));
    public final ButtonSetting open = this.register(new ButtonSetting("Открыть справочник", "Все разделы с поиском и калькулятором координат.")
            .label("Открыть").onClick(HandbookModule::openScreen));
    public final BindSetting openKey = this.register(new BindSetting("Клавиша справочника", "Быстрое открытие справочника."));
    public final ButtonSetting commandsNow = this.register(new ButtonSetting("Команды в чат", "Вывести список команд клиента в чат.").label("Команды").onClick(HandbookModule::printCommands));
    public final ButtonSetting portalsNow = this.register(new ButtonSetting("Порталы в чат", "Как связываются порталы и как считать координаты.").label("Порталы").onClick(HandbookModule::printPortals));
    public final ButtonSetting potionsNow = this.register(new ButtonSetting("Зелья в чат", "Таблица зелий и их длительности.").label("Зелья").onClick(HandbookModule::printPotions));
    public final SeparatorSetting joinGroup = this.register(new SeparatorSetting("При входе"));
    public final BooleanSetting printOnJoin = this.register(new BooleanSetting("Подсказка при входе", "Один раз за вход напомнить про справочник и команды.", true));

    private boolean printed;
    private boolean keyDown;

    public HandbookModule() {
        super("Handbook", "Оффлайн-справочник: команды, клавиши, координаты и порталы, зелья, зачарования, тайминги.", Category.UTILS);
    }

    /** Открывает справочник со всеми разделами клиента. */
    public static void openScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        HandbookScreen screen = new HandbookScreen(client.currentScreen);
        screen.addSection(Handbook.keys(HandbookModule.keyLines()));
        screen.addSection(Handbook.commands(HandbookModule.prefix(), HandbookModule.commandLines()));
        client.setScreen(screen);
    }

    @Override
    protected void onEnable() {
        this.printed = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || this.mc == null || this.mc.getWindow() == null) {
            return;
        }
        if (this.isEnabled() && !this.printed && this.mc.player != null && this.mc.player.age >= 40) {
            this.printed = true;
            if (this.printOnJoin.getValue()) {
                ChatMessage.send("Справочник ByAzen: " + HandbookModule.prefix() + "help в чате или клавиша справочника. Есть калькулятор координат.");
            }
        }
        boolean down = this.openKey.isBound() && this.openKey.getValue().isDown(this.mc.getWindow().getHandle());
        if (down && !this.keyDown && this.isEnabled()) {
            HandbookModule.openScreen();
        }
        this.keyDown = down;
    }

    private static String prefix() {
        String prefix = CommandManager.get().getPrefix();
        return prefix == null || prefix.isEmpty() ? "." : prefix;
    }

    private static List<String> commandLines() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            for (Command command : CommandManager.get().getCommands()) {
                String description = command.getShortDesc();
                lines.add(command.getName() + (description == null || description.isEmpty() ? "" : " — " + description));
            }
        }
        catch (Throwable ignored) {
            lines.add("help — список команд клиента");
        }
        return lines;
    }

    /** Пары «клавиша — действие» для раздела клавиш. */
    public static List<String> keyLines() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            for (Module module : ModuleManager.get().getAll()) {
                KeyBind bind = module.getBind();
                if (bind == null || !bind.isBound()) {
                    continue;
                }
                lines.add(bind.getDisplayName() + " — " + module.getName());
            }
        }
        catch (Throwable ignored) {
            lines.add("Назначенных клавиш не найдено");
        }
        return lines;
    }

    private static void printCommands() {
        ChatMessage.send("Команды клиента (префикс «" + HandbookModule.prefix() + "»):");
        for (String line : HandbookModule.commandLines()) {
            ChatMessage.send("· " + HandbookModule.prefix() + line);
        }
    }

    private static void printPortals() {
        for (String line : Handbook.coordinates("0 0")) {
            ChatMessage.send(line);
        }
        ChatMessage.send("Формула: Нижний мир × 8 = Верхний мир, Верхний мир ÷ 8 = Нижний мир.");
        ChatMessage.send("Портал связывается, если точки в пределах 128 блоков в Верхнем мире.");
    }

    private static void printPotions() {
        for (Handbook.Section section : Handbook.build()) {
            if (!section.title.equals("Зелья")) {
                continue;
            }
            ChatMessage.send("Зелья:");
            for (String line : section.lines) {
                ChatMessage.send("· " + line);
            }
            return;
        }
    }
}
