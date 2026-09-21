/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.player;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.FriendList;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public final class AutoSpawnModule
extends Module
implements ConfigSerializable {
    private static final long COOLDOWN_MS = 9000L;
    private static final String MODE_AUTO = "Auto";
    private static final String MODE_KEY = "Key";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final ModeSetting mode;
    private final BindSetting key;
    private final NumberSetting range;
    private long lastCommandAt;

    public AutoSpawnModule(EventBus eventBus) {
        super(eventBus, "auto_spawn", "Auto Spawn", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0441\u043f\u0430\u0432\u043d \u043f\u0440\u0438 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0438\u0438 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u043e\u0431\u043b\u0438\u0437\u043e\u0441\u0442\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(MODE_KEY, MODE_AUTO).defaultOption(MODE_KEY).name("Mode").id("mode").description("\u0420\u0435\u0436\u0438\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438 \u043d\u0430 \u0441\u043f\u0430\u0432\u043d")).build();
        this.registerSetting(this.mode);
        this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onKeyPress).name(MODE_KEY).id("key").description("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u0438 \u043d\u0430 \u0441\u043f\u0430\u0432\u043d").visibleWhen(() -> MODE_KEY.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.key);
        this.range = ((NumberSettingBuilder)NumberSetting.builder().range(16.0, 150.0).defaultValue(50.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Range").id("range").description("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0438\u044f \u0438\u0433\u0440\u043e\u043a\u0430").visibleWhen(() -> MODE_AUTO.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.range);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onTick);
    }

    private void onTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled() || !MODE_AUTO.equals(this.mode.getSelectedOption())) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null) {
            return;
        }
        FriendList friends = ByAzenClient.getFriends();
        for (class_1657 other : client.field_1687.method_18456()) {
            if (other == player || friends != null && friends.contains(other.method_5477().getString()) || !((double)player.method_5739((class_1297)other) < this.range.getValue())) continue;
            this.sendSpawn();
            return;
        }
    }

    private void onKeyPress(BindSetting ignored) {
        if (this.enabledSetting.isEnabled() && MODE_KEY.equals(this.mode.getSelectedOption())) {
            this.sendSpawn();
        }
    }

    private void sendSpawn() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastCommandAt < 9000L) {
            return;
        }
        player.field_3944.method_45730("spawn");
        this.lastCommandAt = now;
    }
}

