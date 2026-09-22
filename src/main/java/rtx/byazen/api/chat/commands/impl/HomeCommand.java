package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.utils.nav.TeleportHelper;

/**
 * Помощник телепортов (идея №199 из IDEAS.md): {@code .home} и путевые точки.
 */
public final class HomeCommand
extends Command {

    public HomeCommand() {
        super("home", "Дом и телепорты: точка, расстояние, права на сервере", "дом", "byazen");
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length == 0) {
            this.logDirect("Дом ByAzen:", Formatting.GRAY);
            for (String line : TeleportHelper.describe()) {
                this.logDirect(line);
            }
            this.logDirect("Команды: home set · home go · home права · home рядом", Formatting.DARK_GRAY);
            return;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("set") || sub.equals("поставить") || sub.equals("sethome")) {
            this.logDirect(TeleportHelper.setHome(), Formatting.GREEN);
            return;
        }
        if (sub.equals("go") || sub.equals("идти") || sub.equals("tp")) {
            this.logDirect(TeleportHelper.goHome(), Formatting.GRAY);
            return;
        }
        if (sub.equals("права") || sub.equals("perm") || sub.equals("permissions")) {
            for (String line : TeleportHelper.permissions()) {
                this.logDirect(line);
            }
            return;
        }
        if (sub.equals("рядом") || sub.equals("near") || sub.equals("точки")) {
            for (String line : TeleportHelper.nearest(5)) {
                this.logDirect(line);
            }
            return;
        }
        this.logDirect("Не понял: home set | home go | home права | home рядом", Formatting.RED);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Помощник телепортов: домашняя точка, расстояние до неё, направление,",
                "ближайшие путевые точки и честная проверка прав на сервере.",
                "Клиент не перемещает игрока сам — он отправляет серверную команду home.",
                "", "Использование:", "  home          — показать дом и расстояние до него",
                "  home set      — поставить домашнюю точку здесь",
                "  home go       — попросить сервер телепортировать домой",
                "  home права    — что можно на этом сервере",
                "  home рядом    — ближайшие путевые точки");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length <= 1) {
            return Stream.of("set", "go", "права", "рядом");
        }
        return Stream.empty();
    }
}

