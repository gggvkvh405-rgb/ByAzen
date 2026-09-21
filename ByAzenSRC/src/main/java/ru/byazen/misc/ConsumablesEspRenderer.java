/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1680
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 */
package ru.byazen.misc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1680;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.ConsumableEspEffect;
import ru.byazen.misc.ConsumablesEspSettings;
import ru.byazen.misc.RadialConsumableEspRenderer;
import ru.byazen.misc.SnowballAreaEspRenderer;
import ru.byazen.server.FunTimeServerContext;
import ru.byazen.util.PlacementConsumableEspRenderer;

public final class ConsumablesEspRenderer {
    private final PlacementConsumableEspRenderer placementConsumableEspRenderer;
    private final SnowballAreaEspRenderer snowballAreaEspRenderer;
    static final double value = 1024.0;
    static final double process9 = 32.0;
    private final ConsumablesEspSettings consumablesEspSettings;
    private final RadialConsumableEspRenderer radialConsumableEspRenderer;
    private final List<ConsumableEspEffect> effects;

    public ConsumablesEspRenderer(ConsumablesEspSettings consumablesEspSettings) {
        this.consumablesEspSettings = consumablesEspSettings;
        this.placementConsumableEspRenderer = new PlacementConsumableEspRenderer(consumablesEspSettings);
        this.radialConsumableEspRenderer = new RadialConsumableEspRenderer(consumablesEspSettings);
        this.snowballAreaEspRenderer = new SnowballAreaEspRenderer(consumablesEspSettings);
        this.effects = List.of(this.placementConsumableEspRenderer, this.radialConsumableEspRenderer, this.snowballAreaEspRenderer);
    }

    private void process(WorldRenderEvent floatTypeEvent2, class_310 mc, Set<UUID> set) {
        if (!this.consumablesEspSettings.isActive3()) {
            return;
        }
        for (class_1657 player : mc.field_1687.method_18456()) {
            ConsumableEspEffect callback26;
            class_1792 iiIilIIilI2;
            if (player == mc.field_1724 || mc.field_1724.method_5858((class_1297)player) > 1024.0 || !this.consumablesEspSettings.process6(iiIilIIilI2 = player.method_6047().method_7909()) || (callback26 = this.process5(iiIilIIilI2)) == null) continue;
            this.process3(player.method_5667(), callback26);
            set.add(player.method_5667());
            callback26.process(floatTypeEvent2, player, this.consumablesEspSettings.process7(iiIilIIilI2));
        }
    }

    public void setWorldRenderEvent(WorldRenderEvent floatTypeEvent2) {
        class_310 mc = class_310.method_1551();
        if (!this.consumablesEspSettings.isActive() || mc.field_1724 == null || mc.field_1687 == null) {
            this.update();
            return;
        }
        if (mc.method_1493() || !FunTimeServerContext.isConnected()) {
            this.update();
            return;
        }
        HashSet<UUID> hashSet = new HashSet<UUID>();
        this.process4(floatTypeEvent2, mc, hashSet);
        this.process(floatTypeEvent2, mc, hashSet);
        this.effects.forEach(callback26 -> callback26.setSet(hashSet));
        this.process2(floatTypeEvent2, mc);
    }

    private void process2(WorldRenderEvent floatTypeEvent2, class_310 mc) {
        boolean bl;
        boolean bl2 = this.consumablesEspSettings.isActive2() && this.consumablesEspSettings.process3(class_1802.field_8543);
        boolean bl3 = bl = this.consumablesEspSettings.isActive3() && this.consumablesEspSettings.process6(class_1802.field_8543);
        if (!bl2 && !bl) {
            return;
        }
        for (class_1297 entity2 : mc.field_1687.method_18112()) {
            class_1657 ownerPlayer;
            boolean bl4;
            class_1680 snowballEntity;
            if (!(entity2 instanceof class_1680) || mc.field_1724.method_5858((class_1297)(snowballEntity = (class_1680)entity2)) > 1024.0) continue;
            class_1297 iIiiiilIiI2 = snowballEntity.method_24921();
            boolean bl5 = iIiiiilIiI2 == mc.field_1724 && bl2;
            boolean bl6 = bl4 = iIiiiilIiI2 instanceof class_1657 && (ownerPlayer = (class_1657)iIiiiilIiI2) != mc.field_1724 && bl;
            if (!bl5 && !bl4) continue;
            this.snowballAreaEspRenderer.process2(floatTypeEvent2, snowballEntity, bl5 ? this.consumablesEspSettings.process2(class_1802.field_8543) : this.consumablesEspSettings.process7(class_1802.field_8543));
        }
    }

    private void process3(UUID uUID, ConsumableEspEffect callback262) {
        this.effects.stream().filter(member3652 -> member3652 != callback262).forEach(callback26 -> callback26.setUUID(uUID));
    }

    public void update() {
        this.effects.forEach(ConsumableEspEffect::update);
    }

    private void process4(WorldRenderEvent floatTypeEvent2, class_310 mc, Set<UUID> set) {
        if (!this.consumablesEspSettings.isActive2()) {
            return;
        }
        class_1799 stack = mc.field_1724.method_6047();
        if (mc.field_1724.method_7357().method_7904(stack)) {
            return;
        }
        class_1792 iiIilIIilI2 = stack.method_7909();
        if (!this.consumablesEspSettings.process3(iiIilIIilI2)) {
            return;
        }
        ConsumableEspEffect callback26 = this.process5(iiIilIIilI2);
        if (callback26 == null) {
            return;
        }
        this.process3(mc.field_1724.method_5667(), callback26);
        set.add(mc.field_1724.method_5667());
        callback26.process5(floatTypeEvent2, (class_1657)mc.field_1724, this.consumablesEspSettings.process2(iiIilIIilI2));
    }

    private ConsumableEspEffect process5(class_1792 iiIilIIilI2) {
        return this.effects.stream().filter(callback26 -> callback26.process3(iiIilIIilI2)).findFirst().orElse(null);
    }
}

