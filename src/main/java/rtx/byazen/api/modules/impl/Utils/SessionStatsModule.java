package rtx.byazen.api.modules.impl.Utils;

import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.network.PacketEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;

/**
 * Статистика сессии: время в игре, пройденная дистанция, добытые блоки, убитые мобы.
 * Идея №112 из IDEAS.md. Всё считается на клиенте, ничего не отправляется на сервер.
 */
public final class SessionStatsModule
extends Module {

    public final SeparatorSetting lines = this.register(new SeparatorSetting("Что показывать"));
    public final BooleanSetting showTime = this.register(new BooleanSetting("Время в игре", "Сколько времени идёт сессия.", true));
    public final BooleanSetting showDistance = this.register(new BooleanSetting("Пройдено", "Пройденная дистанция в блоках.", true));
    public final BooleanSetting showBlocks = this.register(new BooleanSetting("Добыто блоков", "Сколько блоков сломано.", true));
    public final BooleanSetting showKills = this.register(new BooleanSetting("Убито мобов", "Сколько мобов убито.", true));
    public final ButtonSetting reset = this.register(new ButtonSetting("Сбросить", "Начать отсчёт статистики заново.").label("Сбросить").onClick(this::resetStats));

    private volatile long playtimeMs;
    private volatile double distance;
    private volatile long blocks;
    private volatile long kills;
    private double lastX;
    private double lastY;
    private double lastZ;
    private boolean hasLastPosition;
    private Entity pendingKill;

    public SessionStatsModule() {
        super("Session Stats", "Статистика сессии: время, дистанция, блоки, убийства — в HUD.", Category.UTILS);
    }

    public long playtimeMs() {
        return this.playtimeMs;
    }

    public double distance() {
        return this.distance;
    }

    public long blocks() {
        return this.blocks;
    }

    public long kills() {
        return this.kills;
    }

    public void resetStats() {
        this.playtimeMs = 0L;
        this.distance = 0.0;
        this.blocks = 0L;
        this.kills = 0L;
        this.hasLastPosition = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            this.hasLastPosition = false;
            this.pendingKill = null;
            return;
        }
        this.playtimeMs += 50L;
        double x = this.mc.player.getX();
        double y = this.mc.player.getY();
        double z = this.mc.player.getZ();
        if (this.hasLastPosition) {
            double dx = x - this.lastX;
            double dy = y - this.lastY;
            double dz = z - this.lastZ;
            double step = Math.sqrt(dx * dx + dy * dy + dz * dz);
            // телепорты и лаги не считаем пройденным путём
            if (step > 0.0 && step < 8.0) {
                this.distance += step;
            }
        }
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        this.hasLastPosition = true;
        if (this.pendingKill != null) {
            Entity target = this.pendingKill;
            if (target.isRemoved() || target instanceof LivingEntity && ((LivingEntity) target).getHealth() <= 0.0f) {
                ++this.kills;
                this.pendingKill = null;
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        Entity target = attackEntityEvent.getTarget();
        if (target instanceof LivingEntity) {
            this.pendingKill = target;
        }
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        if (packetEvent.isReceive()) {
            return;
        }
        if (!(packetEvent.getPacket() instanceof PlayerActionC2SPacket)) {
            return;
        }
        PlayerActionC2SPacket packet = (PlayerActionC2SPacket) packetEvent.getPacket();
        if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            ++this.blocks;
        }
    }

    public static String formatTime(long millis) {
        long totalSeconds = Math.max(0L, millis) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0L) {
            return hours + "ч " + minutes + "м";
        }
        if (minutes > 0L) {
            return minutes + "м " + seconds + "с";
        }
        return seconds + "с";
    }

    public static String formatDistance(double blocks) {
        if (blocks >= 1000.0) {
            return String.format(Locale.ROOT, "%.1f км", blocks / 1000.0);
        }
        return Math.round(blocks) + " м";
    }
}
