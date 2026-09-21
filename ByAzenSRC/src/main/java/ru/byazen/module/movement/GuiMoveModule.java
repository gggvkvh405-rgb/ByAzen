/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1041
 *  net.minecraft.class_1713
 *  net.minecraft.class_2596
 *  net.minecraft.class_2645
 *  net.minecraft.class_2649
 *  net.minecraft.class_2653
 *  net.minecraft.class_2813
 *  net.minecraft.class_2815
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_408
 *  net.minecraft.class_437
 *  net.minecraft.class_473
 *  net.minecraft.class_490
 *  net.minecraft.class_746
 *  net.minecraft.class_7743
 */
package ru.byazen.module.movement;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_1041;
import net.minecraft.class_1713;
import net.minecraft.class_2596;
import net.minecraft.class_2645;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_2813;
import net.minecraft.class_2815;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3675;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_473;
import net.minecraft.class_490;
import net.minecraft.class_746;
import net.minecraft.class_7743;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.event.OutgoingPacketEvent;
import ru.byazen.misc.ElapsedTimer;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.ui.ByAzenScreen;
import ru.byazen.util.InventoryController;

public class GuiMoveModule
extends Module
implements ConfigSerializable {
    private volatile boolean waitingForConfirm;
    private volatile boolean flushingFunTime;
    private volatile boolean sendingOwnPackets;
    private volatile int funTimeFlushTicks;
    private final ModeSetting bypass;
    private volatile boolean delayingClicks;
    private volatile int clickDelayTicks;
    private final Queue<class_2813> clickQueue = new ConcurrentLinkedQueue<class_2813>();
    private final ElapsedTimer confirmTimer = new ElapsedTimer();
    private final Queue<class_2596<?>> funTimeQueue = new ConcurrentLinkedQueue();
    private volatile class_2815 pendingClose;
    private volatile int closeDelayTicks;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private volatile boolean movingKeysDown;
    private volatile boolean wasDelayingClicks;

    public GuiMoveModule(EventBus eventBus) {
        super(eventBus, "gui_move", "Gui Move", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0434\u0432\u0438\u0433\u0430\u0442\u044c\u0441\u044f \u043f\u0440\u0438 \u043e\u0442\u043a\u0440\u044b\u0442\u043e\u043c GUI", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.bypass = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Off", "FT", "Spooky-T").defaultOption("Off").name("\u041e\u0431\u0445\u043e\u0434").id("bypass").description("\u0420\u0435\u0436\u0438\u043c \u043e\u0431\u0445\u043e\u0434\u0430 \u043a\u043b\u0438\u043a\u043e\u0432 \u043f\u043e \u0441\u043b\u043e\u0442\u0430\u043c")).build();
        this.registerSetting(this.bypass);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
        this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
    }

    private void onClientTick(ClientTickEvent event) {
        boolean busy = false;
        if (this.enabledSetting.isEnabled() && this.spookyTimeMode()) {
            this.tickSpookyTime();
            busy = this.delayingClicks || !this.clickQueue.isEmpty() || this.pendingClose != null;
        } else {
            this.resetSpookyTime();
        }
        if (this.enabledSetting.isEnabled() && this.funTimeMode()) {
            this.tickFunTime();
            busy = busy || this.flushingFunTime;
        } else {
            this.resetFunTime();
        }
        this.setInventoryBusy(busy);
        class_437 screen = class_310.method_1551().field_1755;
        if (!this.enabledSetting.isEnabled() || screen == null || this.compute2(screen)) {
            if (screen != null) {
                this.releaseMovementKeys();
            }
            return;
        }
        this.pressMovementKeys();
    }

    private void onOutgoingPacket(OutgoingPacketEvent event) {
        if (!this.enabledSetting.isEnabled() || this.sendingOwnPackets) {
            return;
        }
        if (this.funTimeMode()) {
            this.onFunTimeOutgoing(event);
        } else if (this.spookyTimeMode()) {
            this.onSpookyTimeOutgoing(event);
        }
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        if (this.funTimeMode()) {
            class_2645 close;
            class_2596<?> packet = event.getPacket();
            if (packet instanceof class_2645 && (close = (class_2645)packet).method_36148() == 0) {
                event.update();
            }
            return;
        }
        if (!this.spookyTimeMode() || !this.waitingForConfirm || this.confirmTimer.process(2000L)) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2653 || packet instanceof class_2649) {
            this.waitingForConfirm = false;
            event.update();
        }
    }

    private void resetFunTime() {
        if (!this.funTimeQueue.isEmpty()) {
            this.funTimeQueue.clear();
        }
        this.flushingFunTime = false;
        this.funTimeFlushTicks = 0;
    }

    private void pressMovementKeys() {
        class_304[] keys = this.movementKeys();
        if (keys.length == 0) {
            return;
        }
        class_1041 window = class_310.method_1551().method_22683();
        for (class_304 key : keys) {
            class_3675.class_306 bound = class_3675.method_15981((String)key.method_1428());
            boolean pressed = bound.method_1442() != class_3675.class_307.field_1672 && class_3675.method_15987((class_1041)window, (int)bound.method_1444());
            key.method_23481(pressed);
        }
    }

    private boolean spookyTimeMode() {
        return "Spooky-T".equals(this.bypass.getSelectedOption());
    }

    private void onSpookyTimeOutgoing(OutgoingPacketEvent event) {
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2815) {
            class_2815 close = (class_2815)packet;
            if (this.shouldHoldClose()) {
                this.pendingClose = close;
                this.closeDelayTicks = 0;
                event.update();
            }
            return;
        }
        if (!(packet instanceof class_2813)) {
            return;
        }
        class_2813 click = (class_2813)packet;
        if (!this.clickBusy() || click.comp_3844() == -1) {
            return;
        }
        this.armClickDelay(click.comp_3846());
        if (!this.delayingClicks && this.queueClick(click)) {
            this.waitingForConfirm = true;
            this.confirmTimer.update();
            event.update();
        }
    }

    private void resetSpookyTime() {
        if (!this.clickQueue.isEmpty()) {
            this.clickQueue.clear();
        }
        this.clickDelayTicks = 0;
        this.delayingClicks = false;
        this.wasDelayingClicks = false;
        this.waitingForConfirm = false;
        this.movingKeysDown = false;
        this.pendingClose = null;
        this.closeDelayTicks = 0;
    }

    private boolean isKeyDown(class_1041 window, class_304 key) {
        class_3675.class_306 bound = class_3675.method_15981((String)key.method_1428());
        return bound.method_1442() != class_3675.class_307.field_1672 && class_3675.method_15987((class_1041)window, (int)bound.method_1444());
    }

    private void tickFunTime() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.resetFunTime();
            return;
        }
        this.updateMovingKeys();
        if (!this.flushingFunTime) {
            this.funTimeFlushTicks = 0;
            return;
        }
        if (this.funTimeFlushTicks > 2) {
            this.sendingOwnPackets = true;
            try {
                class_2596<?> packet;
                while ((packet = this.funTimeQueue.poll()) != null) {
                    player.field_3944.method_52787(packet);
                }
            }
            finally {
                this.sendingOwnPackets = false;
            }
            this.flushingFunTime = false;
            this.funTimeFlushTicks = 0;
            return;
        }
        ++this.funTimeFlushTicks;
    }

    private boolean compute2(class_437 screen) {
        return screen instanceof class_408 || screen instanceof class_7743 || screen instanceof class_473 || screen instanceof ByAzenScreen;
    }

    private boolean shouldHoldClose() {
        if (this.clickBusy() || !this.clickQueue.isEmpty()) {
            return true;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_5624();
    }

    private boolean holdingItemInInventory(class_746 player) {
        if (!(class_310.method_1551().field_1755 instanceof class_490)) {
            return false;
        }
        if (!this.clickBusy()) {
            return false;
        }
        return !player.field_7512.method_34255().method_7960();
    }

    private boolean queueClick(class_2813 packet) {
        if (this.clickQueue.contains(packet)) {
            return false;
        }
        return this.clickQueue.add(packet);
    }

    private void flushPendingClose(class_746 player) {
        if (this.pendingClose == null) {
            return;
        }
        if (!this.clickQueue.isEmpty() || this.closeDelayTicks <= 1) {
            ++this.closeDelayTicks;
            return;
        }
        this.sendingOwnPackets = true;
        try {
            player.field_3944.method_52787((class_2596)this.pendingClose);
        }
        finally {
            this.sendingOwnPackets = false;
        }
        this.pendingClose = null;
        this.closeDelayTicks = 0;
    }

    private int delayFor(class_1713 actionType) {
        return actionType == class_1713.field_7790 ? 1 : (this.clickDelayTicks > 1 ? 2 : 3);
    }

    private void setInventoryBusy(boolean busy) {
        InventoryController member6090 = ByAzenClient.getInventoryController();
        if (member6090 == null) {
            return;
        }
        if (busy) {
            member6090.update();
        } else {
            member6090.update2();
        }
    }

    private void onFunTimeOutgoing(OutgoingPacketEvent event) {
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2813) {
            class_2813 click = (class_2813)packet;
            if (class_310.method_1551().field_1755 instanceof class_490 && this.movingKeysDown && click.comp_3844() != -1) {
                this.funTimeQueue.add((class_2596<?>)click);
                event.update();
            }
        } else if (packet instanceof class_2815) {
            class_2815 close = (class_2815)packet;
            if (!this.funTimeQueue.isEmpty()) {
                this.funTimeQueue.add((class_2596<?>)close);
                this.flushingFunTime = true;
                event.update();
            }
        }
    }

    private class_304[] movementKeys() {
        class_315 options = class_310.method_1551().field_1690;
        if (options == null) {
            return new class_304[0];
        }
        return new class_304[]{options.field_1894, options.field_1913, options.field_1881, options.field_1849, options.field_1903, options.field_1867};
    }

    private void flushClickQueue(class_746 player) {
        if (this.clickQueue.isEmpty()) {
            return;
        }
        this.sendingOwnPackets = true;
        try {
            class_2813 click;
            while ((click = this.clickQueue.poll()) != null) {
                player.field_3944.method_52787((class_2596)click);
            }
        }
        finally {
            this.sendingOwnPackets = false;
        }
    }

    private boolean funTimeMode() {
        return "FT".equals(this.bypass.getSelectedOption());
    }

    private boolean clickBusy() {
        return this.movingKeysDown || this.delayingClicks;
    }

    private void releaseMovementKeys() {
        for (class_304 key : this.movementKeys()) {
            key.method_23481(false);
        }
    }

    private void armClickDelay(class_1713 actionType) {
        this.clickDelayTicks = this.delayFor(actionType == null ? class_1713.field_7790 : actionType) + 1;
    }

    private void updateMovingKeys() {
        class_315 options = class_310.method_1551().field_1690;
        if (options == null) {
            this.movingKeysDown = false;
            return;
        }
        class_1041 window = class_310.method_1551().method_22683();
        this.movingKeysDown = this.isKeyDown(window, options.field_1894) || this.isKeyDown(window, options.field_1881) || this.isKeyDown(window, options.field_1913) || this.isKeyDown(window, options.field_1849);
    }

    private void tickSpookyTime() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.resetSpookyTime();
            return;
        }
        this.updateMovingKeys();
        if (this.wasDelayingClicks && this.delayingClicks && this.clickDelayTicks > 0) {
            this.flushClickQueue(player);
        }
        this.wasDelayingClicks = this.delayingClicks;
        if (this.holdingItemInInventory(player)) {
            this.clickDelayTicks = this.delayFor(class_1713.field_7790) + 1;
        } else if (this.clickDelayTicks > 0) {
            --this.clickDelayTicks;
        }
        this.delayingClicks = this.clickDelayTicks > 0;
        this.flushPendingClose(player);
    }
}

