/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_238
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package ru.wexside.module.combat;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_638;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HitBoxesModule
extends Module
implements ConfigSerializable {
    private static final float SIZE_SCALE = 2.5f;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0420\u0430\u0441\u0448\u0438\u0440\u044f\u0435\u0442 \u0445\u0438\u0442-\u0431\u043e\u043a\u0441\u044b \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439").withKeybind().toggle()).build();
    private final BooleanSetting visible;
    private final NumberSetting size;
    private final BooleanSetting ignoreFriends;

    public HitBoxesModule(EventBus eventBus) {
        super(eventBus, "hit_boxes", "Hit Boxes", "\u0420\u0430\u0441\u0448\u0438\u0440\u044f\u0435\u0442 \u0445\u0438\u0442-\u0431\u043e\u043a\u0441\u044b \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.visible = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Visible").id("visible").description("\u041e\u0431\u0432\u043e\u0434\u043a\u0430 \u0445\u0438\u0442\u0431\u043e\u043a\u0441\u0430")).build();
        this.registerSetting(this.visible);
        this.size = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 3.0).defaultValue(0.5).multiplier(1.0).precision(1).animationSpeed(20.0f).name("Size").id("size").description("\u0420\u0430\u0437\u043c\u0435\u0440 \u0440\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u0438\u044f \u0445\u0438\u0442-\u0431\u043e\u043a\u0441\u0430").aliases("size", "\u0440\u0430\u0437\u043c\u0435\u0440")).build();
        this.registerSetting(this.size);
        this.ignoreFriends = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Ignore Friends").id("ignore_friends").description("\u041d\u0435 \u0440\u0430\u0441\u0448\u0438\u0440\u044f\u0442\u044c \u0445\u0438\u0442-\u0431\u043e\u043a\u0441\u044b \u0434\u0440\u0443\u0437\u0435\u0439")).build();
        this.registerSetting(this.ignoreFriends);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_638 world = client.field_1687;
        if (world == null || client.field_1724 == null) {
            return;
        }
        float expand = (float)this.size.getValue() * 2.5f;
        for (class_1297 entity : world.method_18112()) {
            if (!this.shouldExpand(entity, (class_1297)client.field_1724)) continue;
            entity.method_5857(this.expandedBox(entity, expand));
        }
    }

    private class_238 expandedBox(class_1297 entity, float expand) {
        class_238 box = entity.method_5829();
        return new class_238(entity.method_23317() - (double)expand, box.field_1322, entity.method_23321() - (double)expand, entity.method_23317() + (double)expand, box.field_1325, entity.method_23321() + (double)expand);
    }

    private boolean shouldExpand(class_1297 entity, class_1297 player) {
        if (entity == null || !entity.method_5805() || entity == player) {
            return false;
        }
        if (!this.ignoreFriends.isEnabled()) {
            return true;
        }
        FriendList friends = WexSideClient.getFriends();
        return friends == null || !friends.contains(entity.method_5477().getString());
    }
}

