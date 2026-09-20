/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1304
 *  net.minecraft.class_1661
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_745
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_745;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class FakePlayerModule
extends Module
implements ConfigSerializable {
    private static final int FAKE_ID = -1338;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private class_638 trackedWorld;
    private class_745 fakePlayer;
    private boolean spawned;

    public FakePlayerModule(EventBus eventBus) {
        super(eventBus, "fake_player", "Fake Player", "\u0421\u043f\u0430\u0432\u043d\u0438\u0442 \u0432\u0430\u0448\u0435\u0433\u043e \u0434\u0432\u043e\u0439\u043d\u0438\u043a\u0430", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onTick);
        this.listen(WorldSessionEvent.class, event -> this.despawn());
    }

    private void onTick(ClientTickEvent event) {
        class_310 client = class_310.method_1551();
        class_638 world = client.field_1687;
        if (this.trackedWorld != world) {
            this.trackedWorld = world;
            if (this.spawned) {
                this.forceDespawn();
                return;
            }
        }
        if (this.spawned && (client.field_1724 == null || !client.field_1724.method_5805())) {
            this.forceDespawn();
            return;
        }
        if (this.enabledSetting.isEnabled() && !this.spawned) {
            this.spawn();
        } else if (!this.enabledSetting.isEnabled() && this.spawned) {
            this.despawn();
        }
    }

    private void spawn() {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null || !player.method_5805()) {
            this.enabledSetting.setEnabled(false);
            return;
        }
        this.trackedWorld = client.field_1687;
        this.copyFrom(player);
        this.spawned = true;
    }

    private void despawn() {
        this.removeFake();
        this.spawned = false;
    }

    private void forceDespawn() {
        this.despawn();
        this.enabledSetting.setEnabled(false);
    }

    private void removeFake() {
        class_745 fake = this.fakePlayer;
        this.fakePlayer = null;
        if (fake == null) {
            return;
        }
        try {
            fake.method_5650(class_1297.class_5529.field_26999);
            fake.method_31472();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void copyFrom(class_746 player) {
        class_310 client = class_310.method_1551();
        class_745 fake = new class_745(client.field_1687, player.method_7334());
        fake.method_5719((class_1297)player);
        fake.method_5878((class_1297)player);
        this.copyInventory(fake.method_31548(), player.method_31548());
        for (class_1304 slot : class_1304.values()) {
            fake.method_5673(slot, player.method_6118(slot).method_7972());
        }
        fake.method_5838(-1338);
        client.field_1687.method_53875((class_1297)fake);
        this.fakePlayer = fake;
    }

    private void copyInventory(class_1661 dest, class_1661 source) {
        for (int i = 0; i < source.method_5439(); ++i) {
            dest.method_5447(i, source.method_5438(i).method_7972());
        }
    }
}

