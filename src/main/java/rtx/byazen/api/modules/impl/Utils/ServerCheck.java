package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ServerCheckScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.server.ServerFavorites;
import rtx.byazen.utils.server.ServerPinger;

/**
 * Проверка серверов (идея №99 из IDEAS.md).
 * <p>
 * Окно со списком избранных адресов: пинг, игроки, версия и описание, плюс вход в один клик. Модуль
 * сообщает состояние сервера в чат при подключении и умеет проверять весь список одной кнопкой.
 */
public final class ServerCheck
extends Module {

    public final SeparatorSetting windowGroup = this.register(new SeparatorSetting("Окно"));
    public final ButtonSetting openWindow = this.register(new ButtonSetting("Открыть проверку серверов", "Список избранных адресов с пингом, игроками и быстрым входом.")
            .label("Открыть").onClick(ServerCheck::openScreen));
    public final SeparatorSetting joinGroup = this.register(new SeparatorSetting("При подключении"));
    public final BooleanSetting tellOnJoin = this.register(new BooleanSetting("Сообщать состояние", "При входе на сервер показать его пинг и число игроков.", true));
    public final BooleanSetting warnFavorites = this.register(new BooleanSetting("Проверять избранные", "При входе в игру проверять весь список избранного один раз.", false));

    private String lastAddress = "";
    private boolean favoritesChecked;

    public ServerCheck() {
        super("ServerCheck", "Проверка серверов: пинг, игроки, версия, избранные адреса и быстрый вход.", Category.UTILS);
    }

    /** Открывает окно проверки серверов. */
    public static void openScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ServerCheckScreen(client.currentScreen));
        }
    }

    @Override
    protected void onEnable() {
        this.favoritesChecked = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null || this.mc.player == null) {
            return;
        }
        if (!this.favoritesChecked && this.warnFavorites.getValue()) {
            this.favoritesChecked = true;
            if (!ServerFavorites.load().isEmpty()) {
                ChatMessage.send("Избранные сервера: " + ServerFavorites.summary()
                        + ". Полная проверка — в окне «Проверка серверов».");
            }
        }
        String address = ServerBinds.currentAddress();
        if (address.isEmpty() || address.equals(this.lastAddress)) {
            return;
        }
        this.lastAddress = address;
        if (!this.tellOnJoin.getValue()) {
            return;
        }
        ServerPinger.pingAsync(address).thenAccept(status -> MinecraftClient.getInstance().execute(() -> {
            if (!this.isEnabled()) {
                return;
            }
            if (status.online) {
                ChatMessage.send("Сервер " + ServerPinger.prettyAddress(address) + ": " + status.summary()
                        + " · " + status.versionText());
            }
            else {
                ChatMessage.send("Сервер " + ServerPinger.prettyAddress(address) + ": " + status.summary());
            }
        }));
    }

    /** Проверяет список и возвращает готовую сводку: используется командой и кнопкой. */
    public static void checkAll() {
        if (ServerFavorites.load().isEmpty()) {
            ChatMessage.send(Lang.t("Список избранных серверов пуст.", "The favorites list is empty."));
            return;
        }
        ChatMessage.send("Проверяю сервера: " + ServerFavorites.summary());
        for (ServerFavorites.Entry entry : ServerFavorites.load()) {
            ServerPinger.pingAsync(entry.address).thenAccept(status -> MinecraftClient.getInstance().execute(
                    () -> ChatMessage.send(ServerPinger.prettyAddress(status.address) + " — " + status.summary())));
        }
    }
}
