/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1542
 *  net.minecraft.class_1657
 *  net.minecraft.class_746
 */
package ru.byazen.model.esp;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.FriendList;
import ru.byazen.model.esp.EspRelation;
import ru.byazen.model.esp.EspTargetType;

public final class EspTargetClassifier {
    private EspTargetClassifier() {
    }

    public static EspTargetType targetType(class_1297 entity, class_746 localPlayer) {
        if (entity == localPlayer) {
            return EspTargetType.SELF;
        }
        if (entity instanceof class_1657) {
            return EspTargetType.PLAYERS;
        }
        if (entity instanceof class_1542) {
            return EspTargetType.ITEMS;
        }
        return EspTargetType.ENTITIES;
    }

    public static EspRelation relation(class_1297 entity) {
        if (entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            FriendList friends = ByAzenClient.getFriends();
            if (friends != null && friends.contains(player.method_5477().getString())) {
                return EspRelation.FRIEND;
            }
        }
        return EspRelation.DEFAULT;
    }
}

