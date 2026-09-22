package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.server.DisconnectInfo;
import rtx.byazen.utils.server.ServerPinger;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Авто-переподключение (идея №119 из IDEAS.md).
 * <p>
 * Когда связь обрывается, клиент возвращает игрока на тот же сервер: с задержкой, с ростом интервала
 * между попытками и с чтением места в очереди с экрана отключения. После бана или кика переподключение
 * не выполняется — чтобы не выглядеть навязчиво, — а на экране видно обратный отсчёт и полоску.
 */
public final class AutoReconnect
extends Module {

    public final SeparatorSetting attemptGroup = this.register(new SeparatorSetting("Попытки"));
    public final SliderSetting delay = this.register(new SliderSetting("Задержка, сек", "Сколько ждать перед первым возвратом на сервер.")
            .range(3, 60).increment(1).setValue(8));
    public final BooleanSetting backoff = this.register(new BooleanSetting("Растущая задержка", "Увеличивать паузу после каждой неудачной попытки.", true));
    public final SliderSetting maxAttempts = this.register(new SliderSetting("Максимум попыток", "Сколько раз пробовать переподключиться.")
            .range(1, 10).increment(1).setValue(3));
    public final ModeSetting queueMode = this.register(new ModeSetting("Очередь", "Как вести себя, если сервер держит в очереди.", "Своя задержка", "Своя задержка", "Ждать очередь", "Не ждать"));

    public final SeparatorSetting safetyGroup = this.register(new SeparatorSetting("Осторожность"));
    public final BooleanSetting skipPunishment = this.register(new BooleanSetting("Не входить после бана", "Не переподключаться, если причина похожа на бан или кик.", true));
    public final BooleanSetting askAttention = this.register(new BooleanSetting("Уважать пароль и капчу", "Не переподключаться, если сервер просит пароль или проверку.", true));
    public final BooleanSetting notify = this.register(new BooleanSetting("Уведомлять", "Писать в чат о начале и результате переподключения.", true));

    private static AutoReconnect instance;

    private String address = "";
    private String reason = "";
    private int queue;
    private int queueTotal;
    private float left;
    private int attempts;
    private boolean active;
    private boolean finished;
    private boolean warnedPunishment;

    public AutoReconnect() {
        super("AutoReconnect", "Авто-переподключение: возврат на сервер после обрыва, чтение очереди и обратный отсчёт в HUD.", Category.UTILS);
        instance = this;
    }

    public static AutoReconnect instance() {
        return instance;
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    private void reset() {
        this.address = "";
        this.reason = "";
        this.queue = 0;
        this.queueTotal = 0;
        this.left = 0.0f;
        this.attempts = 0;
        this.active = false;
        this.finished = false;
        this.warnedPunishment = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null || this.mc.player == null) {
            return;
        }
        Screen screen = DisconnectInfo.disconnectScreen();
        if (screen == null) {
            if (this.active || this.finished) {
                this.reset();
            }
            return;
        }
        if (this.finished) {
            return;
        }
        if (!this.active) {
            if (!this.start(screen)) {
                return;
            }
        }
        this.left -= 0.05f;
        if (this.left <= 0.0f) {
            this.reconnect();
        }
    }

    /** Разбирает экран отключения и решает, стоит ли возвращаться. */
    private boolean start(Screen screen) {
        String reason = DisconnectInfo.reason(screen);
        this.reason = DisconnectInfo.shortReason(reason);
        this.queue = DisconnectInfo.queuePosition(screen);
        this.queueTotal = DisconnectInfo.queueTotal(screen);
        this.address = ServerBinds.currentAddress();
        if (this.address.isEmpty() && this.mc.getCurrentServerEntry() != null) {
            this.address = ServerPinger.cleanAddress(this.mc.getCurrentServerEntry().address);
        }
        if (this.address.isEmpty()) {
            this.finished = true;
            return false;
        }
        if (this.skipPunishment.getValue() && DisconnectInfo.looksLikePunishment(reason)) {
            if (!this.warnedPunishment) {
                this.warnedPunishment = true;
                this.finished = true;
                if (this.notify.getValue()) {
                    ChatMessage.send("Переподключение отменено: " + this.reason);
                }
            }
            return false;
        }
        if (this.askAttention.getValue() && DisconnectInfo.needsAttention(reason)) {
            this.finished = true;
            if (this.notify.getValue()) {
                ChatMessage.send("Сервер просит пароль или проверку — переподключайтесь вручную.");
            }
            return false;
        }
        float wait = this.delay.getValue();
        if (this.queueMode.is("Ждать очередь") && this.queue > 0) {
            wait = Math.max(wait, (float)Math.min(90, 4 + this.queue));
        }
        if (this.queueMode.is("Не ждать")) {
            wait = Math.max(3.0f, this.delay.getValue() * 0.5f);
        }
        if (this.backoff.getValue() && this.attempts > 0) {
            wait *= (float)Math.min(4, 1 << Math.min(3, this.attempts));
        }
        this.left = wait;
        this.active = true;
        ++this.attempts;
        Sounds.play("select_category");
        if (this.notify.getValue()) {
            ChatMessage.send("Связь потеряна: " + this.reason);
            ChatMessage.send("Возврат на " + ServerPinger.prettyAddress(this.address) + " через " + Math.round(wait) + " с"
                    + (this.queue > 0 ? " (место в очереди " + this.queue + (this.queueTotal > 0 ? " из " + this.queueTotal : "") + ")" : "")
                    + (this.attempts > 1 ? " · попытка " + this.attempts : ""));
        }
        return true;
    }

    private void reconnect() {
        this.active = false;
        if (this.attempts >= (int)this.maxAttempts.getValue()) {
            this.finished = true;
            if (this.notify.getValue()) {
                ChatMessage.send("Попытки переподключения закончились — зайдите на сервер вручную.");
            }
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        ServerInfo info = new ServerInfo(ServerPinger.prettyAddress(this.address), this.address, ServerInfo.ServerType.OTHER);
        try {
            ConnectScreen.connect(null, client, new ServerAddress(ServerPinger.hostOf(this.address), ServerPinger.portOf(this.address)), info, false, null);
        }
        catch (Throwable throwable) {
            this.finished = true;
            if (this.notify.getValue()) {
                ChatMessage.send("Не удалось вернуться на сервер: " + throwable.getClass().getSimpleName());
            }
        }
    }

    /** Идёт ли отсчёт прямо сейчас. */
    public static boolean counting() {
        return instance != null && instance.active;
    }

    /** Сколько секунд осталось до возврата. */
    public static float secondsLeft() {
        return instance == null ? 0.0f : Math.max(0.0f, instance.left);
    }

    /** Полоска прогресса отсчёта: 0 — только начали, 1 — возврат вот-вот. */
    public static float progress() {
        if (instance == null || !instance.active) {
            return 0.0f;
        }
        float total = Math.max(1.0f, instance.delay.getValue() * (float)Math.min(4, 1 << Math.min(3, Math.max(0, instance.attempts - 1))));
        return Math.max(0.0f, Math.min(1.0f, 1.0f - instance.left / total));
    }

    /** Адрес, на который вернёт клиент. */
    public static String target() {
        return instance == null ? "" : ServerPinger.prettyAddress(instance.address);
    }

    /** Место в очереди с экрана отключения; 0 — очереди нет. */
    public static int queuePlace() {
        return instance == null ? 0 : instance.queue;
    }

    /** Всего мест в очереди; 0 — не указано. */
    public static int queueSize() {
        return instance == null ? 0 : instance.queueTotal;
    }

    /** Сколько попыток уже сделано. */
    public static int attemptCount() {
        return instance == null ? 0 : instance.attempts;
    }

    /** Короткая причина обрыва. */
    public static String lastReason() {
        return instance == null ? "" : instance.reason;
    }
}
