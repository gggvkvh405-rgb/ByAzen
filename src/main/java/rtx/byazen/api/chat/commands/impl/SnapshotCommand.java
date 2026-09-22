package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.utils.config.CloudConfigs;
import rtx.byazen.utils.config.ConfigDiff;

/**
 * Версии и сравнение настроек (идеи №151 и №204 из IDEAS.md): {@code .snapshot}.
 * <p>
 * Команда снимает снимок настроек, показывает список версий, сравнивает любые две из них — или
 * последнюю с текущим состоянием — и откатывает настройки назад. Полезно, когда после обновления
 * клиента «всё поехало»: видно, что именно изменилось, и одним действием возвращается как было.
 */
public final class SnapshotCommand
extends Command {

    public SnapshotCommand() {
        super("snapshot", "Версии настроек: снимок, список, сравнение, откат", "снимок", "snap");
    }

    @Override
    public void execute(String label, String[] args) {
        String sub = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";
        if (sub.equals("save") || sub.equals("snap") || sub.equals("снять") || sub.equals("создать")) {
            this.save(args.length > 1 ? args[1] : "ручной");
            return;
        }
        if (sub.equals("restore") || sub.equals("откат") || sub.equals("вернуть")) {
            this.restore(args.length > 1 ? args[1] : "");
            return;
        }
        if (sub.equals("diff") || sub.equals("сравнить") || sub.equals("разница")) {
            this.diff(args);
            return;
        }
        if (sub.equals("list") || sub.equals("список")) {
            this.list();
            return;
        }
        this.logDirect("Версии настроек ByAzen", Formatting.AQUA);
        this.logDirect(ConfigDiff.summary(), Formatting.GRAY);
        for (String line : ConfigDiff.latestAgainstCurrent()) {
            this.logDirect(line);
        }
        this.logDirect("Команды: snapshot save <метка> · snapshot list · snapshot diff [метка] · snapshot restore <метка>",
                Formatting.DARK_GRAY);
    }

    private void save(String label) {
        CloudConfigs.Version version = CloudConfigs.snapshot(label);
        if (version == null) {
            this.logDirect("Не получилось снять снимок настроек", Formatting.RED);
            return;
        }
        this.logDirect("Снимок сохранён: «" + ConfigDiff.label(version) + "» (" + version.sizeText() + ")", Formatting.GREEN);
    }

    private void list() {
        List<CloudConfigs.Version> versions = CloudConfigs.versions();
        this.logDirect(ConfigDiff.summary(), Formatting.GRAY);
        if (versions.isEmpty()) {
            this.logDirect("Версий пока нет — снимите первую: snapshot save до-настройки", Formatting.YELLOW);
            return;
        }
        for (CloudConfigs.Version version : versions) {
            this.logDirect(Formatting.GRAY + "• " + Formatting.WHITE + ConfigDiff.label(version) + Formatting.DARK_GRAY
                    + " · " + version.timeText() + " · " + version.sizeText());
        }
        this.logDirect("Сравнение последней версии с текущим состоянием: snapshot diff", Formatting.DARK_GRAY);
    }

    /** diff без аргумента — последняя версия против текущих настроек; с именами — две версии между собой. */
    private void diff(String[] args) {
        if (args.length == 1) {
            for (String line : ConfigDiff.latestAgainstCurrent()) {
                this.logDirect(line);
            }
            return;
        }
        CloudConfigs.Version left = ConfigDiff.find(args[1]);
        CloudConfigs.Version right = args.length > 2 ? ConfigDiff.find(args[2]) : null;
        if (left == null) {
            this.logDirect("Не нашёл версию «" + args[1] + "» — список: snapshot list", Formatting.RED);
            return;
        }
        List<String> rows = right == null ? ConfigDiff.againstCurrent(left) : ConfigDiff.between(left, right);
        this.logDirect("Сравнение настроек", Formatting.AQUA);
        for (String line : rows) {
            this.logDirect(line);
        }
    }

    private void restore(String needle) {
        CloudConfigs.Version version = ConfigDiff.find(needle);
        if (version == null) {
            this.logDirect("Не нашёл версию «" + needle + "» — список: snapshot list", Formatting.RED);
            return;
        }
        CloudConfigs.snapshot("перед-откатом");
        int applied = CloudConfigs.restore(version);
        if (applied < 0) {
            this.logDirect("Откат не удался — файл версии повреждён", Formatting.RED);
            return;
        }
        this.logDirect("Настройки откатаны к «" + ConfigDiff.label(version) + "» (модулей " + applied
                + "). Текущее состояние сохранено как «перед-откатом»", Formatting.GREEN);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Версии настроек: снимок, список, сравнение и откат.",
                "Каждый снимок хранит состояние всех модулей, клавиш и темы. Сравнение показывает,",
                "что именно изменилось: включённые модули, значения настроек и переставленные клавиши.",
                "", "Использование:", "  snapshot                     — сводка и разница последней версии с текущим",
                "  snapshot save <метка>        — снять снимок настроек", "  snapshot list                — список версий",
                "  snapshot diff [метка] [метка] — сравнить версии (или последнюю с текущим состоянием)",
                "  snapshot restore <метка>     — откатить настройки (перед откатом снимается копия)");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length <= 1) {
            return Stream.of("save", "list", "diff", "restore");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("diff") || args[0].equalsIgnoreCase("restore"))) {
            return CloudConfigs.versions().stream().map(ConfigDiff::label);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("diff")) {
            return CloudConfigs.versions().stream().map(ConfigDiff::label);
        }
        return Stream.empty();
    }
}
