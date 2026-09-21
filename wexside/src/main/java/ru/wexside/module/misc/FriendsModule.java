/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 */
package ru.wexside.module.misc;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class FriendsModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0440\u0443\u0437\u0435\u0439 \u0431\u0438\u043d\u0434\u043e\u043c \u0438 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0430 \u043f\u043e \u0434\u0440\u0443\u0437\u044c\u044f\u043c").withKeybind().toggle()).build();
    private final BooleanSetting clickFriend;
    private final BindSetting friendKey;
    private final BooleanSetting noFriendDamage;

    public FriendsModule(EventBus eventBus) {
        super(eventBus, "friends", "Friends", "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0440\u0443\u0437\u0435\u0439 \u0431\u0438\u043d\u0434\u043e\u043c \u0438 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043d\u0430 \u043f\u043e \u0434\u0440\u0443\u0437\u044c\u044f\u043c", ModuleCategory.valueOf("MISC"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.clickFriend = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Click Friend").id("click_friend").description("\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0442\u044c/\u0443\u0434\u0430\u043b\u044f\u0442\u044c \u0434\u0440\u0443\u0433\u0430 \u0431\u0438\u043d\u0434\u043e\u043c \u043f\u043e \u043d\u0430\u0432\u043e\u0434\u0438\u043c\u043e\u043c\u0443 \u0438\u0433\u0440\u043e\u043a\u0443")).build();
        this.registerSetting(this.clickFriend);
        this.friendKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onFriendKeyPress).name("Friend Key").id("friend_key").description("\u0411\u0438\u043d\u0434 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u044f/\u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f \u0434\u0440\u0443\u0433\u0430").aliases("friend key", "\u0431\u0438\u043d\u0434 \u0434\u0440\u0443\u0433\u0430").visibleWhen(this.clickFriend::isEnabled)).build();
        this.registerSetting(this.friendKey);
        this.noFriendDamage = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("No Friend Damage").id("no_friend_damage").description("\u041e\u0442\u043c\u0435\u043d\u044f\u0435\u0442 \u0430\u0442\u0430\u043a\u0443 \u043f\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c \u0438\u0437 \u0441\u043f\u0438\u0441\u043a\u0430 \u0434\u0440\u0443\u0437\u0435\u0439").aliases("no friend damage", "\u0431\u0435\u0437 \u0443\u0440\u043e\u043d\u0430 \u043f\u043e \u0434\u0440\u0443\u0437\u044c\u044f\u043c")).build();
        this.registerSetting(this.noFriendDamage);
    }

    @Override
    protected void initialize() {
        this.listen(EntityAttackEvent.class, this::onEntityAttack);
    }

    private void onFriendKeyPress(BindSetting ignored) {
        class_1657 player;
        if (!this.enabledSetting.isEnabled() || !this.clickFriend.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_1297 target = client.field_1692;
        if (!(target instanceof class_1657) || (player = (class_1657)target) == client.field_1724) {
            return;
        }
        FriendList friends = WexSideClient.getFriends();
        if (friends == null) {
            return;
        }
        String name = player.method_5477().getString();
        if (friends.contains(name)) {
            friends.remove(name);
            ClientChat.send("\u0414\u0440\u0443\u0433 " + name + " \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0441\u043f\u0438\u0441\u043a\u0430 \u0434\u0440\u0443\u0437\u0435\u0439");
        } else {
            friends.add(name);
            ClientChat.send("\u0414\u0440\u0443\u0433 " + name + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0441\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439");
        }
    }

    private void onEntityAttack(EntityAttackEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.noFriendDamage.isEnabled()) {
            return;
        }
        class_1297 entity = event.getEntity();
        if (entity instanceof class_1657 && this.isFriend(entity)) {
            event.update();
        }
    }

    private boolean isFriend(class_1297 entity) {
        FriendList friends = WexSideClient.getFriends();
        return friends != null && friends.contains(entity.method_5477().getString());
    }
}

