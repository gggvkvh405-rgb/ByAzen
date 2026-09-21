package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Formatting;
import rtx.byazen.ByAzen;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.sounds.Sounds;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Планировщик напоминаний (идея №103 из IDEAS.md): «через 30 минут выпить воды», «через час размяться».
 * Напоминания можно ставить командой {@code remind <минуты> <текст>} или готовыми кнопками в настройках.
 */
public final class RemindersModule
extends Module {

    private static final String FILE = "reminders";
    private static final int LIMIT = 40;

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("Поведение"));
    public final BooleanSetting toast = this.register(new BooleanSetting("Тост", "Показывать всплывающее уведомление.", true));
    public final BooleanSetting chat = this.register(new BooleanSetting("Писать в чат", "Дублировать напоминание в чат.", true));
    public final BooleanSetting sound = this.register(new BooleanSetting("Звук", "Проигрывать звук уведомления.", true));

    public final SeparatorSetting presets = this.register(new SeparatorSetting("Готовые напоминания"));
    public final ButtonSetting water = this.register(new ButtonSetting("Вода", "Напомнить выпить воды через 30 минут.").label("+30 мин").onClick(() -> this.add(30, "Выпить воды")));
    public final ButtonSetting stretch = this.register(new ButtonSetting("Размяться", "Напомнить размяться через 60 минут.").label("+60 мин").onClick(() -> this.add(60, "Размяться и размять глаза")));
    public final ButtonSetting check = this.register(new ButtonSetting("Проверить AFK", "Напомнить проверить, не выкинуло ли с сервера.").label("+15 мин").onClick(() -> this.add(15, "Проверить AFK")));
    public final ButtonSetting clear = this.register(new ButtonSetting("Сбросить", "Удалить все напоминания.").label("Очистить").onClick(this::clear));

    private final List<RemindersModule.Entry> entries = new ArrayList<RemindersModule.Entry>();
    private boolean loaded;

    public RemindersModule() {
        super("Reminders", "Планировщик напоминаний: «через 30 минут выпить воды», команда remind <минуты> <текст>.", Category.UTILS);
    }

    /** Все запланированные напоминания (в порядке добавления). */
    public List<RemindersModule.Entry> entries() {
        this.ensureLoaded();
        synchronized (this.entries) {
            return new ArrayList<RemindersModule.Entry>(this.entries);
        }
    }

    public void add(int minutes, String text) {
        this.ensureLoaded();
        if (minutes <= 0 || text == null || text.isBlank()) {
            return;
        }
        RemindersModule.Entry entry = new RemindersModule.Entry(text.trim(), System.currentTimeMillis() + (long) minutes * 60000L, minutes);
        synchronized (this.entries) {
            this.entries.add(entry);
            while (this.entries.size() > LIMIT) {
                this.entries.remove(0);
            }
        }
        this.save();
        ChatMessage.brandmessage("Напоминание через " + minutes + " мин: " + entry.text);
    }

    public void clear() {
        this.ensureLoaded();
        synchronized (this.entries) {
            this.entries.clear();
        }
        this.save();
        ChatMessage.brandmessage("Все напоминания удалены.");
    }

    public boolean removeAt(int index) {
        this.ensureLoaded();
        synchronized (this.entries) {
            if (index < 0 || index >= this.entries.size()) {
                return false;
            }
            this.entries.remove(index);
        }
        this.save();
        return true;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.ensureLoaded();
        long now = System.currentTimeMillis();
        RemindersModule.Entry due = null;
        synchronized (this.entries) {
            for (int i = 0; i < this.entries.size(); ++i) {
                if (this.entries.get(i).dueAt <= now) {
                    due = this.entries.remove(i);
                    break;
                }
            }
        }
        if (due == null) {
            return;
        }
        this.save();
        if (this.toast.getValue()) {
            NotificationsModule.notify("⏰ " + due.text, 4000L);
        }
        if (this.chat.getValue()) {
            ChatMessage.brandmessage(net.minecraft.text.Text.literal("⏰ " + due.text).formatted(Formatting.AQUA));
        }
        if (this.sound.getValue()) {
            Sounds.play("settings_open");
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
            JsonArray array = root.has("items") && root.get("items").isJsonArray() ? root.getAsJsonArray("items") : new JsonArray();
            synchronized (this.entries) {
                for (JsonElement element : array) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject object = element.getAsJsonObject();
                    String text = object.has("text") ? object.get("text").getAsString() : "";
                    long dueAt = object.has("due") ? object.get("due").getAsLong() : 0L;
                    if (!text.isBlank() && dueAt > 0L) {
                        this.entries.add(new RemindersModule.Entry(text, dueAt, 0));
                    }
                }
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Reminders could not be loaded: {}", throwable.toString());
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            synchronized (this.entries) {
                for (RemindersModule.Entry entry : this.entries) {
                    JsonObject object = new JsonObject();
                    object.addProperty("text", entry.text);
                    object.addProperty("due", entry.dueAt);
                    array.add(object);
                }
            }
            root.add("items", array);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Reminders could not be saved: {}", throwable.toString());
        }
    }

    /** Одно напоминание. */
    public static final class Entry {
        public final String text;
        public final long dueAt;
        public final int minutes;

        public Entry(String text, long dueAt, int minutes) {
            this.text = text;
            this.dueAt = dueAt;
            this.minutes = minutes;
        }

        public long minutesLeft() {
            return Math.max(0L, (this.dueAt - System.currentTimeMillis()) / 60000L);
        }
    }
}
