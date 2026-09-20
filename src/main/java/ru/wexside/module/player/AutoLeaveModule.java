/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_419
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_5250
 */
package ru.wexside.module.player;

import net.minecraft.class_124;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_419;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_5250;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.entity.NpcDetector;

public class AutoLeaveModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final BooleanSetting ignoreFriends;

    public AutoLeaveModule(EventBus eventBus) {
        super(eventBus, "auto_leave", "Auto Leave", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u044b\u0445\u043e\u0434\u0438\u0442 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u043f\u0440\u0438 \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0438\u0438 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043f\u043e\u0431\u043b\u0438\u0437\u043e\u0441\u0442\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.ignoreFriends = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Ignore Friends").id("ignore_friends").description("\u0418\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0434\u0440\u0443\u0437\u0435\u0439").aliases("friends", "\u0434\u0440\u0443\u0437\u044c\u044f")).build();
        this.registerSetting(this.ignoreFriends);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onTick);
    }

    private void onTick(ClientTickEvent event) {
        class_310 client = class_310.method_1551();
        if (!this.enabledSetting.isEnabled() || client.field_1687 == null || client.field_1724 == null) {
            return;
        }
        for (class_1657 other : client.field_1687.method_18456()) {
            if (other == client.field_1724 || this.ignoreFriends.isEnabled() && this.isFriend(other) || this.isNpc(other)) continue;
            if (FunTimeServerContext.isPvpLocked()) {
                return;
            }
            this.leave();
            return;
        }
    }

    private void leave() {
        this.enabledSetting.setEnabled(false);
        class_310 client = class_310.method_1551();
        class_5250 reason = class_2561.method_43470((String)"AutoLeave");
        client.method_76795((class_437)new class_419((class_437)new class_442(), (class_2561)reason, (class_2561)reason), false);
    }

    private boolean isFriend(class_1657 player) {
        FriendList friends = WexSideClient.getFriends();
        return friends != null && friends.contains(class_124.method_539((String)player.method_5477().getString()));
    }

    private boolean isNpc(class_1657 player) {
        NpcDetector npcDetector = WexSideClient.getNpcDetector();
        return npcDetector != null && npcDetector.isNpc((class_1309)player);
    }
}

