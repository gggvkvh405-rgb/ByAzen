package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.NotesModule;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Заметки с привязкой к координатам (идея №108 из IDEAS.md).
 * <p>
 * {@code note <текст>} — записать заметку в текущей точке, {@code note list} — показать список,
 * {@code note del <номер>} — удалить одну, {@code note clear} — очистить всё.
 */
public final class NoteCommand
extends Command {

    public NoteCommand() {
        super("note", "Заметки с координатами", "notes", "заметка");
    }

    private NotesModule module() {
        return ModuleManager.get().get(NotesModule.class);
    }

    @Override
    public void execute(String label, String[] args) {
        NotesModule module = this.module();
        if (module == null) {
            this.logDirect("Модуль Notes не найден.", Formatting.RED);
            return;
        }
        if (args.length == 0) {
            module.printList();
            return;
        }
        String first = args[0].toLowerCase(Locale.ROOT);
        if (first.equals("list") || first.equals("список")) {
            module.printList();
            return;
        }
        if (first.equals("clear") || first.equals("очистить")) {
            module.clear();
            return;
        }
        if (first.equals("del") || first.equals("delete") || first.equals("удалить")) {
            if (args.length < 2) {
                this.logDirect("Укажите номер заметки: note del 2", Formatting.RED);
                return;
            }
            try {
                int index = Integer.parseInt(args[1]) - 1;
                if (module.removeAt(index)) {
                    this.logDirect("Заметка удалена.", Formatting.GREEN);
                }
                else {
                    this.logDirect("Заметки с таким номером нет.", Formatting.RED);
                }
            }
            catch (NumberFormatException numberFormatException) {
                this.logDirect("Номер должен быть числом: note del 2", Formatting.RED);
            }
            return;
        }
        String text = String.join(" ", args).trim();
        if (text.isEmpty()) {
            this.usage();
            return;
        }
        int x = 0;
        int y = 0;
        int z = 0;
        String world = "";
        if (this.mc.player != null && this.mc.world != null) {
            x = (int) Math.round(this.mc.player.getX());
            y = (int) Math.round(this.mc.player.getY());
            z = (int) Math.round(this.mc.player.getZ());
            world = this.mc.world.getRegistryKey().getValue().getPath();
        }
        NotesModule.Note note = module.add(text, x, y, z, world);
        ChatMessage.brandmessage("Заметка сохранена: " + note.text + " (" + x + " " + y + " " + z + ")");
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Заметки, привязанные к координатам.",
                "", "Использование:",
                "> note найти базу у горы",
                "> note list",
                "> note del 1",
                "> note clear");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("list", "del", "clear").filter(value -> value.startsWith(args[0].toLowerCase(Locale.ROOT)));
        }
        return Stream.empty();
    }
}
