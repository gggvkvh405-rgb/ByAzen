/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1536
 *  net.minecraft.class_1657
 *  net.minecraft.class_1802
 *  net.minecraft.class_2596
 *  net.minecraft.class_2767
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.byazen.module.player;

import net.minecraft.class_1268;
import net.minecraft.class_1536;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2767;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.misc.ElapsedTimer;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.player.AutoEatModule;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public class AutoFishModule
extends Module
implements ConfigSerializable {
    private static final long RECAST_DELAY_MS = 1000L;
    private static final double BITE_DISTANCE_SQ = 9.0;
    private final BooleanSetting enabledSetting;
    private final ElapsedTimer cooldown = new ElapsedTimer();
    private double splashX;
    private double splashY;
    private double splashZ;
    private volatile boolean bitePending;

    public AutoFishModule(EventBus eventBus) {
        super(eventBus, "auto_fish", "Auto Fish", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u0440\u044b\u0431\u0430\u043b\u043a\u0430", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onTick);
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(WorldSessionEvent.class, event -> this.reset());
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (!(packet instanceof class_2767)) {
            return;
        }
        class_2767 soundPacket = (class_2767)packet;
        if (!((class_3414)soundPacket.method_11894().comp_349()).equals((Object)class_3417.field_14660)) {
            return;
        }
        this.splashX = soundPacket.method_11890();
        this.splashY = soundPacket.method_11889();
        this.splashZ = soundPacket.method_11893();
        this.bitePending = true;
    }

    private void onTick(ClientTickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || class_310.method_1551().field_1687 == null || AutoEatModule.eating || !player.method_6047().method_31574(class_1802.field_8378)) {
            this.reset();
            return;
        }
        if (this.bitePending) {
            this.bitePending = false;
            class_1536 bobber = player.field_7513;
            if (bobber != null && bobber.method_73189().method_1028(this.splashX, this.splashY, this.splashZ) < 9.0) {
                this.useRod(player);
                this.cooldown.update();
                return;
            }
        }
        if (player.field_7513 == null && this.cooldown.process(1000L)) {
            this.useRod(player);
            this.cooldown.update();
        }
    }

    private void useRod(class_746 player) {
        class_636 interactions = class_310.method_1551().field_1761;
        if (interactions == null) {
            return;
        }
        interactions.method_2919((class_1657)player, class_1268.field_5808);
        player.method_6104(class_1268.field_5808);
    }

    private void reset() {
        this.bitePending = false;
    }
}

