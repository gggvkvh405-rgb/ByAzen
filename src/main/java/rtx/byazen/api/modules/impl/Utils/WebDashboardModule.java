package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.LocalHttp;
import rtx.byazen.utils.web.WebBridge;
import rtx.byazen.utils.web.WebService;

/**
 * Веб-дашборд (идея №176 из IDEAS.md).
 * <p>
 * Отдельная страница со графиками FPS и памяти, статистикой сессии, музыкой и локальным рейтингом
 * сессий: итоги сохраняются каждую минуту, поэтому видно, какая сессия была самой удачной. Данные не
 * уходят в интернет — страницу отдаёт сам клиент, а история лежит в конфиге ByAzen.
 */
public final class WebDashboardModule
extends Module {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd.MM HH:mm").withZone(ZoneId.systemDefault());

    private final SeparatorSetting group = this.register(new SeparatorSetting("Веб-дашборд"));
    private final ButtonSetting link = this.register(new ButtonSetting("Ссылка в чат", "Напечатать адрес дашборда с ключом доступа.").label("Ссылка").onClick(WebDashboardModule::printLink));
    private final ButtonSetting rating = this.register(new ButtonSetting("Рейтинг сессий в чат", "Показать сохранённые итоги последних сессий.").label("Рейтинг").onClick(WebDashboardModule::printRating));
    private final ButtonSetting wipe = this.register(new ButtonSetting("Очистить рейтинг", "Удалить сохранённые итоги сессий.").label("Очистить").onClick(WebDashboardModule::clearRating));

    public WebDashboardModule() {
        super("Web Dashboard", "Дашборд в браузере: графики FPS и памяти, статистика, рейтинг сессий.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        WebService.refresh();
        WebBridge.pushEvent("web", "Дашборд включён");
    }

    @Override
    protected void onDisable() {
        WebService.refresh();
    }

    private static void printLink() {
        if (!LocalHttp.get().running()) {
            WebService.refresh();
        }
        ChatMessage.send("§bДашборд: §f" + LocalHttp.get().baseUrl() + "/dashboard?t=" + LocalHttp.get().token());
    }

    private static void printRating() {
        JsonObject root = RepositoryStorage.readObject("web_sessions");
        JsonArray sessions = root.has("sessions") ? root.getAsJsonArray("sessions") : new JsonArray();
        if (sessions.isEmpty()) {
            ChatMessage.send("§7Итогов сессий ещё нет — они сохраняются каждую минуту игры");
            return;
        }
        ChatMessage.send("§bЛокальный рейтинг сессий:");
        for (int i = sessions.size() - 1; i >= 0 && i >= sessions.size() - 5; --i) {
            JsonObject session = sessions.get(i).getAsJsonObject();
            ChatMessage.send("§7• " + TIME.format(Instant.ofEpochMilli(session.get("time").getAsLong()))
                    + " §f" + session.get("minutes").getAsInt() + " мин §7· блоков " + session.get("blocks").getAsInt()
                    + " · убийств " + session.get("kills").getAsInt() + " §7· путь " + session.get("distance").getAsInt());
        }
    }

    private static void clearRating() {
        RepositoryStorage.write("web_sessions", new JsonObject());
        ChatMessage.send("§bИтоги сессий очищены");
    }
}
