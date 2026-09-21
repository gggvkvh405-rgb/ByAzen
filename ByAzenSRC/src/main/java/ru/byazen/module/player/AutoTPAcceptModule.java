/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_7438
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 */
package ru.byazen.module.player;

import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_7438;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class AutoTPAcceptModule
extends Module
implements ConfigSerializable {
    private static final String[] REQUEST_HINTS = new String[]{"\u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "\u0445\u043e\u0447\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "\u043f\u0440\u043e\u0441\u0438\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "requested to teleport", "has requested teleport", "tpa"};
    private final BooleanSetting enabledSetting;
    private final BooleanSetting onlyFriends;
    private final Queue<String> pending = new ConcurrentLinkedQueue<String>();

    public AutoTPAcceptModule(EventBus eventBus) {
        super(eventBus, "auto_tp_accept", "Auto TPAccept", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u0435\u0442 \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.onlyFriends = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Only-Friends").id("only_friends").description("\u041f\u0440\u0438\u043d\u0438\u043c\u0430\u0442\u044c \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0442 \u0434\u0440\u0443\u0437\u0435\u0439")).build();
        this.registerSetting(this.onlyFriends);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        String message;
        if (!this.enabledSetting.isEnabled()) {
            this.pending.clear();
            return;
        }
        boolean accept = false;
        while ((message = this.pending.poll()) != null) {
            if (this.onlyFriends.isEnabled() && !this.isFriendRequest(message)) continue;
            accept = true;
        }
        if (accept) {
            this.accept();
        }
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        String message = this.textOf(event.getPacket());
        if (message != null && this.isTeleportRequest(message)) {
            this.pending.add(message);
        }
    }

    private String textOf(class_2596<?> packet) {
        class_2561 text = null;
        if (packet instanceof class_7439) {
            class_7439 gameMessage = (class_7439)packet;
            text = gameMessage.comp_763();
        } else if (packet instanceof class_7438) {
            class_7438 chatMessage = (class_7438)packet;
            text = chatMessage.comp_1103();
        }
        return text == null ? null : text.getString();
    }

    private String stripColor(String message) {
        StringBuilder builder = new StringBuilder(message.length());
        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char ch = chars[i];
            if (ch == '\u00a7') {
                ++i;
                continue;
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    private boolean isFriendRequest(String message) {
        if (ByAzenClient.getFriends() == null) {
            return false;
        }
        String stripped = this.stripColor(message).toLowerCase(Locale.ROOT);
        for (String friend : ByAzenClient.getFriends().getNames()) {
            if (!stripped.contains(friend)) continue;
            return true;
        }
        return false;
    }

    private void accept() {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            player.field_3944.method_45730("tpaccept");
        }
    }

    private boolean isTeleportRequest(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        for (String hint : REQUEST_HINTS) {
            if (!lower.contains(hint)) continue;
            return true;
        }
        return false;
    }
}

