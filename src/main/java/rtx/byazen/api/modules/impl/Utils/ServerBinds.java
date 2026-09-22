package rtx.byazen.api.modules.impl.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.key.KeyBind;
import rtx.byazen.utils.server.ServerBindProfiles;
import rtx.byazen.utils.server.ServerPinger;

/**
 * Профили клавиш под сервер (идея №97 из IDEAS.md).
 * <p>
 * У каждого сервера свои клавиши: на знакомом адресе клиент сам расставляет сохранённый набор, на
 * незнакомом запоминает текущий, а общий профиль применяется там, где своего пока нет. Локальные
 * адреса не трогаются — в одиночной игре клавиши остаются как есть.
 */
public final class ServerBinds
extends Module {

    public final SeparatorSetting group = this.register(new SeparatorSetting("Профили клавиш"));
    public final BooleanSetting autoApply = this.register(new BooleanSetting("Применять при входе", "Автоматически расставлять клавиши сохранённого профиля.", true));
    public final BooleanSetting autoRemember = this.register(new BooleanSetting("Запоминать новый сервер", "На незнакомом сервере сохранять текущие клавиши в его профиль.", true));
    public final BooleanSetting onlyRemote = this.register(new BooleanSetting("Только удалённые сервера", "Не вести профили для одиночной игры и локальной сети.", true));
    public final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, какой профиль клавиш применён.", false));
    public final ButtonSetting saveHere = this.register(new ButtonSetting("Сохранить профиль сервера", "Запомнить текущие клавиши за адресом сервера.").label("Сохранить").onClick(ServerBinds::saveHere));
    public final ButtonSetting applyHere = this.register(new ButtonSetting("Применить профиль сервера", "Расставить клавиши из профиля текущего сервера.").label("Применить").onClick(ServerBinds::applyHere));
    public final ButtonSetting saveDefault = this.register(new ButtonSetting("Сохранить общий профиль", "Набор клавиш для серверов без своего профиля.").label("Общий").onClick(ServerBinds::saveDefault));
    public final ButtonSetting forgetHere = this.register(new ButtonSetting("Забыть профиль сервера", "Убрать сохранённый набор клавиш этого адреса.").label("Забыть").onClick(ServerBinds::forgetHere));

    private String lastAddress = "";

    public ServerBinds() {
        super("ServerBinds", "Профили клавиш под сервер: свой набор биндов для каждого адреса, общий — для остальных.", Category.UTILS);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null) {
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
        if (this.onlyRemote.getValue() && !ServerBindProfiles.isRemote(address)) {
            return;
        }
        if (this.autoApply.getValue() && ServerBindProfiles.size(address) > 0) {
            int applied = ServerBinds.apply(address);
            if (applied > 0 && this.tellInChat.getValue()) {
                ChatMessage.send("Клавиши сервера " + ServerPinger.prettyAddress(address) + " применены: " + applied + " шт.");
            }
            return;
        }
        if (this.autoRemember.getValue() && first) {
            int saved = ServerBinds.snapshot().size();
            ServerBindProfiles.saveProfile(address, ServerBinds.snapshot());
            if (saved > 0 && this.tellInChat.getValue()) {
                ChatMessage.send("Клавиши для " + ServerPinger.prettyAddress(address) + " запомнены: " + saved + " шт.");
            }
        }
    }

    /** Адрес текущего сервера; пустая строка — одиночная игра. */
    public static String currentAddress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getCurrentServerEntry() == null) {
            return "";
        }
        return ServerPinger.cleanAddress(client.getCurrentServerEntry().address);
    }

    /** Снимок клавиш всех модулей: «имя модуля → код клавиши». */
    public static Map<String, Integer> snapshot() {
        LinkedHashMap<String, Integer> binds = new LinkedHashMap<String, Integer>();
        for (Module module : ModuleManager.get().getAll()) {
            KeyBind bind = module.getBind();
            if (bind != null && bind.isBound()) {
                binds.put(module.getName(), bind.getCode());
            }
        }
        return binds;
    }

    /** Расставляет клавиши из профиля. Возвращает число изменённых. */
    public static int apply(String address) {
        Map<String, Integer> profile = ServerBindProfiles.profileFor(address);
        if (profile.isEmpty()) {
            return 0;
        }
        int changed = 0;
        for (Map.Entry<String, Integer> pair : profile.entrySet()) {
            Module module = ModuleManager.get().findByName(pair.getKey());
            if (module == null) {
                continue;
            }
            int code = pair.getValue();
            KeyBind current = module.getBind();
            if (code < 0) {
                if (current != null && current.isBound()) {
                    module.setBind(KeyBind.NONE);
                    ++changed;
                }
                continue;
            }
            if (current == null || !current.isBound() || current.getCode() != code) {
                module.setBind(new KeyBind(code));
                ++changed;
            }
        }
        return changed;
    }

    /** Кладёт профиль на место, снимая клавиши с модулей, которых в профиле нет. */
    public static int applyExact(String address) {
        Map<String, Integer> profile = ServerBindProfiles.profileFor(address);
        if (profile.isEmpty()) {
            return 0;
        }
        int changed = 0;
        for (Module module : ModuleManager.get().getAll()) {
            KeyBind bind = module.getBind();
            Integer code = profile.get(module.getName());
            if (code == null) {
                if (bind != null && bind.isBound()) {
                    module.setBind(KeyBind.NONE);
                    ++changed;
                }
                continue;
            }
            if (code < 0) {
                continue;
            }
            if (bind == null || !bind.isBound() || bind.getCode() != code) {
                module.setBind(new KeyBind(code));
                ++changed;
            }
        }
        return changed;
    }

    private static void saveHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty()) {
            ChatMessage.send("Профиль сервера доступен только в игре на сервере.");
            return;
        }
        int saved = ServerBinds.snapshot().size();
        ServerBindProfiles.saveProfile(address, ServerBinds.snapshot());
        ChatMessage.send("Клавиши сервера " + ServerPinger.prettyAddress(address) + " сохранены: " + saved + " шт.");
    }

    private static void applyHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty()) {
            ChatMessage.send("Профиль сервера доступен только в игре на сервере.");
            return;
        }
        int changed = ServerBinds.apply(address);
        ChatMessage.send(changed > 0
                ? "Клавиши сервера применены: изменено " + changed + " шт."
                : "Для этого сервера профиль пока не сохранён.");
    }

    private static void saveDefault() {
        int saved = ServerBinds.snapshot().size();
        ServerBindProfiles.saveDefault(ServerBinds.snapshot());
        ChatMessage.send("Общий профиль клавиш сохранён: " + saved + " шт.");
    }

    private static void forgetHere() {
        String address = ServerBinds.currentAddress();
        if (address.isEmpty()) {
            ChatMessage.send("Забывать нечего: вы не на сервере.");
            return;
        }
        ChatMessage.send(ServerBindProfiles.forget(address)
                ? "Профиль " + ServerPinger.prettyAddress(address) + " забыт."
                : "У этого сервера не было своего профиля.");
    }

    /** Подсказка для настроек: сколько профилей уже собрано. */
    public static String summary() {
        List<String> addresses = ServerBindProfiles.addresses();
        String current = ServerBinds.currentAddress();
        return ServerBindProfiles.summary() + (current.isEmpty() ? " · сейчас одиночная игра" : " · сервер: " + ServerPinger.prettyAddress(current));
    }

    /** Имя модуля в нижнем регистре: используется в подсказках. */
    public String hint() {
        return ServerBinds.summary().toLowerCase(Locale.ROOT);
    }
}
