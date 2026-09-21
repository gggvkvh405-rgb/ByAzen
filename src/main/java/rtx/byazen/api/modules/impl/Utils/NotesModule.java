package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rtx.byazen.ByAzen;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Заметки в игре с привязкой к координатам (идея №108 из IDEAS.md).
 * Команда {@code note <текст>} добавляет заметку, {@code note list} показывает список с копированием координат.
 */
public final class NotesModule
extends Module {

    private static final String FILE = "notes";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT);

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("Заметки"));
    public final SliderSetting limit = this.register(new SliderSetting("Хранить заметок", "Сколько последних заметок держать в файле.").range(10, 300).increment(10).setValue(80.0f));
    public final BooleanSetting showWorld = this.register(new BooleanSetting("Мир в списке", "Показывать измерение в списке заметок.", true));
    public final ButtonSetting copyLast = this.register(new ButtonSetting("Последняя заметка", "Скопировать координаты последней заметки.").label("Копировать").onClick(this::copyLastCoords));
    public final ButtonSetting clear = this.register(new ButtonSetting("Удалить все", "Очистить список заметок.").label("Очистить").onClick(this::clear));

    private final List<NotesModule.Note> notes = new ArrayList<NotesModule.Note>();
    private boolean loaded;

    public NotesModule() {
        super("Notes", "Заметки с координатами: команда note <текст>, список с копированием координат в один клик.", Category.UTILS);
    }

    /** Заметки, новые первыми. */
    public List<NotesModule.Note> notes() {
        this.ensureLoaded();
        synchronized (this.notes) {
            return new ArrayList<NotesModule.Note>(this.notes);
        }
    }

    public NotesModule.Note add(String text, int x, int y, int z, String world) {
        this.ensureLoaded();
        NotesModule.Note note = new NotesModule.Note(text == null ? "" : text.trim(), x, y, z, world == null ? "" : world, System.currentTimeMillis());
        synchronized (this.notes) {
            this.notes.add(0, note);
            int max = (int) this.limit.getValue();
            while (this.notes.size() > max) {
                this.notes.remove(this.notes.size() - 1);
            }
        }
        this.save();
        return note;
    }

    public void clear() {
        this.ensureLoaded();
        synchronized (this.notes) {
            this.notes.clear();
        }
        this.save();
        ChatMessage.brandmessage("Список заметок очищен.");
    }

    public boolean removeAt(int index) {
        this.ensureLoaded();
        synchronized (this.notes) {
            if (index < 0 || index >= this.notes.size()) {
                return false;
            }
            this.notes.remove(index);
        }
        this.save();
        return true;
    }

    private void copyLastCoords() {
        List<NotesModule.Note> list = this.notes();
        if (list.isEmpty()) {
            ChatMessage.brandmessage("Заметок пока нет — добавьте командой note <текст>.");
            return;
        }
        NotesModule.Note note = list.get(0);
        String coords = note.x + " " + note.y + " " + note.z;
        if (this.mc != null && this.mc.keyboard != null) {
            this.mc.keyboard.setClipboard(coords);
            ChatMessage.brandmessage("Скопировано: " + coords + " — " + note.text);
        }
    }

    /** Печатает список заметок в чат с кликабельными координатами. */
    public void printList() {
        List<NotesModule.Note> list = this.notes();
        if (list.isEmpty()) {
            ChatMessage.brandmessage("Заметок пока нет — добавьте командой note <текст>.");
            return;
        }
        ChatMessage.brandmessage("Заметки (" + list.size() + "):");
        int shown = Math.min(10, list.size());
        for (int i = 0; i < shown; ++i) {
            NotesModule.Note note = list.get(i);
            String coords = note.x + " " + note.y + " " + note.z;
            String world = this.showWorld.getValue() && !note.world.isBlank() ? " [" + note.world + "]" : "";
            MutableText line = Text.literal("  " + (i + 1) + ". ").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal(note.text).formatted(Formatting.WHITE))
                    .append(Text.literal(" — " + coords + world + " · " + TIME_FORMAT.format(new Date(note.time)))
                            .formatted(Formatting.GRAY)
                            .styled(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(coords))));
            ChatMessage.brandmessage(line);
        }
        if (list.size() > shown) {
            ChatMessage.brandmessage("…и ещё " + (list.size() - shown) + " заметок.");
        }
    }

    // ------------------------------------------------------------------ storage

    private void ensureLoaded() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            JsonArray array = root.has("notes") && root.get("notes").isJsonArray() ? root.getAsJsonArray("notes") : new JsonArray();
            synchronized (this.notes) {
                for (JsonElement element : array) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject object = element.getAsJsonObject();
                    this.notes.add(new NotesModule.Note(
                            object.has("text") ? object.get("text").getAsString() : "",
                            object.has("x") ? object.get("x").getAsInt() : 0,
                            object.has("y") ? object.get("y").getAsInt() : 0,
                            object.has("z") ? object.get("z").getAsInt() : 0,
                            object.has("world") ? object.get("world").getAsString() : "",
                            object.has("time") ? object.get("time").getAsLong() : 0L));
                }
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Notes could not be loaded: {}", throwable.toString());
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            synchronized (this.notes) {
                for (NotesModule.Note note : this.notes) {
                    JsonObject object = new JsonObject();
                    object.addProperty("text", note.text);
                    object.addProperty("x", note.x);
                    object.addProperty("y", note.y);
                    object.addProperty("z", note.z);
                    object.addProperty("world", note.world);
                    object.addProperty("time", note.time);
                    array.add(object);
                }
            }
            root.add("notes", array);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Notes could not be saved: {}", throwable.toString());
        }
    }

    /** Одна заметка. */
    public static final class Note {
        public final String text;
        public final int x;
        public final int y;
        public final int z;
        public final String world;
        public final long time;

        public Note(String text, int x, int y, int z, String world, long time) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
            this.time = time;
        }
    }
}
