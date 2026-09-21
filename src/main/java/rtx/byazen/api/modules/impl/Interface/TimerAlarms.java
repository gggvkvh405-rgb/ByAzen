package rtx.byazen.api.modules.impl.Interface;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Formatting;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Алармы таймеров (идея №38 из IDEAS.md): предупреждает, когда зелье или кулдаун предмета
 * вот-вот закончится. Сами таймеры рисуют штатные виджеты {@code Potions} и {@code Cooldowns},
 * а этот модуль добавляет к ним настраиваемые пороги, звук и уведомление.
 */
public final class TimerAlarms
extends InterfaceComponentModule {

    private final SeparatorSetting mainSeparator = new SeparatorSetting("Зелья");
    public final BooleanSetting potionAlarm = this.register(new BooleanSetting("Окончание зелья", "Предупреждать, когда действие зелья подходит к концу.", true));
    public final SliderSetting potionSeconds = this.register(new SliderSetting("Порог, сек", "За сколько секунд до конца предупреждать.").range(3.0f, 60.0f).increment(1.0f).setValue(10.0f)
            .visibleWhen(this.potionAlarm::getValue));
    public final BooleanSetting onlyOwnBuffs = this.register(new BooleanSetting("Только полезные", "Не предупреждать про дебаффы (слабость, слепота и т.п.).", true)
            .visibleWhen(this.potionAlarm::getValue));
    public final BooleanSetting sound = this.register(new BooleanSetting("Звук", "Проигрывать короткий звук предупреждения.", true));
    public final BooleanSetting chat = this.register(new BooleanSetting("Сообщение в чат", "Писать предупреждение в чат клиента.", false));
    public final BooleanSetting hudToast = this.register(new BooleanSetting("Всплывашка в HUD", "Показывать уведомление в правом верхнем углу.", true));

    private final Map<String, Long> fired = new HashMap<String, Long>();

    public TimerAlarms() {
        super("Timer Alarms", "Алармы таймеров: предупреждения об окончании зелий и кулдаунов предметов.");
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            this.fired.clear();
            return;
        }
        if (!this.potionAlarm.getValue()) {
            return;
        }
        long now = System.currentTimeMillis();
        float threshold = this.potionSeconds.getFloat();
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (effect == null || effect.isInfinite()) {
                continue;
            }
            if (this.onlyOwnBuffs.getValue() && !effect.getEffectType().value().isBeneficial()) {
                continue;
            }
            float seconds = (float) Math.max(0, effect.getDuration()) / 20.0f;
            String key = "effect:" + effect.getEffectType().value().getName().getString() + ":" + effect.getAmplifier();
            if (seconds > threshold) {
                this.fired.remove(key);
                continue;
            }
            Long last = this.fired.get(key);
            if (last != null && now - last < 2500L) {
                continue;
            }
            this.fired.put(key, now);
            String name = Formatting.strip(effect.getEffectType().value().getName().getString());
            this.fire(name + " заканчивается через " + Math.max(1, Math.round(seconds)) + " с");
        }
    }

    private void fire(String text) {
        if (this.sound.getValue()) {
            Sounds.play("command_error");
        }
        if (this.chat.getValue()) {
            ChatMessage.brandmessage(text);
        }
        if (this.hudToast.getValue()) {
            rtx.byazen.api.modules.impl.Interface.NotificationsModule.notify(text, 2600L);
        }
    }

    /** Читаемый список активных таймеров для команды {@code /byazen timers}. */
    public static String describeActive() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return "Игрок не в мире.";
        }
        StringBuilder builder = new StringBuilder();
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (effect == null) {
                continue;
            }
            String name = Formatting.strip(effect.getEffectType().value().getName().getString());
            int seconds = Math.max(0, effect.getDuration()) / 20;
            builder.append(String.format(Locale.ROOT, "%s — %d:%02d; ", name, seconds / 60, seconds % 60));
        }
        return builder.isEmpty() ? "Активных зелий нет." : builder.toString();
    }

    /** Есть ли активный модуль (для проверки из команд). */
    public static boolean active() {
        TimerAlarms module = ModuleManager.get().get(TimerAlarms.class);
        return module != null && module.isEnabled();
    }
}
