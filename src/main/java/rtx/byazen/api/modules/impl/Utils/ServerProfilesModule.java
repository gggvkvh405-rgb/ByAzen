package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.theme.ThemeManager;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.server.ServerPinger;
import rtx.byazen.utils.server.ServerProfiles;

/**
 * Профили под сервер (идея №152 из IDEAS.md).
 * <p>
 * На разных серверах нужны разные наборы модулей и оформление. Профиль запоминает включённые
 * модули и тему и расставляет их при входе на знакомый адрес: где-то важно PvP-окружение, где-то —
 * косметика и музыка. Одиночная игра не трогается, клавиши ведёт отдельный профиль клавиш.
 */
public final class ServerProfilesModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Профили сервера"));
    private final BooleanSetting autoApply = this.register(new BooleanSetting("Применять при входе", "Расставлять модули и тему сохранённого профиля.").setValue(true));
    private final BooleanSetting autoRemember = this.register(new BooleanSetting("Запоминать новый сервер", "На незнакомом адресе запоминать текущий набор модулей и тему.").setValue(true));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, какой профиль применён.").setValue(true));
    private final ButtonSetting saveHere = this.register(new ButtonSetting("Сохранить профиль сервера", "Запомнить текущий набор модулей и тему для этого адреса.").label("Сохранить").onClick(ServerProfilesModule::saveHere));
    private final ButtonSetting applyHere = this.register(new ButtonSetting("Применить профиль сервера", "Расставить модули и тему из профиля этого адреса.").label("Применить").onClick(ServerProfilesModule::applyHere));
    private final ButtonSetting forgetHere = this.register(new ButtonSetting("Забыть профиль сервера", "Убрать сохранённый профиль этого адреса.").label("Забыть").onClick(ServerProfilesModule::forgetHere));

    private String lastAddress = "";

    public ServerProfilesModule() {
        super("Server Profiles", "Профили модулей и темы под каждый сервер: своё окружение на знакомых адресах.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!event.isPost() || !this.isEnabled() || this.mc == null) {
            return;
        }
        String address = ServerBinds.currentAddress();
        if (address.equals(this.lastAddress)) {
            return;
        }
        boolean first = this.lastAddress.isEmpty();
        this.lastAddress = address;
        if (address.isEmpty()) {
            return;
        }
        if (ServerProfiles.has(address)) {
            if (this.autoApply.getValue()) {
                String report = ServerProfiles.apply(address);
                if (!report.isEmpty() && this.tellInChat.getValue()) {
                    ChatMessage.send("Профиль " + ServerPinger.prettyAddress(address) + " применён: " + report);
                }
            }
            return;
        }
        if (this.autoRemember.getValue() && first) {
            ServerProfiles.remember(address, ThemeManager.current().name());
            if (this.tellInChat.getValue()) {
                ChatMessage.send("Профиль для " + ServerPinger.prettyAddress(address) + " запомнен");
            }
        }
    }

    private static void saveHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty()) {
            ChatMessage.error("Сначала зайдите на сервер");
            return;
        }
        ServerProfiles.remember(address, ThemeManager.current().name());
        ChatMessage.send("Профиль " + ServerPinger.prettyAddress(address) + " сохранён");
    }

    private static void applyHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty() || !ServerProfiles.has(address)) {
            ChatMessage.error("Для этого адреса профиля нет");
            return;
        }
        String report = ServerProfiles.apply(address);
        ChatMessage.send("Профиль применён: " + report);
    }

    private static void forgetHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty() || !ServerProfiles.forget(address)) {
            ChatMessage.error("Профиль не найден");
            return;
        }
        ChatMessage.send("Профиль " + ServerPinger.prettyAddress(address) + " забыт");
    }
}
