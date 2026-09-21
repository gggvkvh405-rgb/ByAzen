/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1703
 *  net.minecraft.class_2561
 *  net.minecraft.class_437
 */
package ru.byazen.util;

import net.minecraft.class_1703;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public final class ContainerScreenHelper {
    private ContainerScreenHelper() {
    }

    public static boolean isAuctionContainer(class_1703 handler, class_437 screen) {
        String lower;
        class_2561 title;
        if (screen != null && (title = screen.method_25440()) != null && ((lower = title.getString().toLowerCase()).contains("auction") || lower.contains("\u0430\u0443\u043a\u0446\u0438\u043e\u043d") || lower.contains("donmarket") || lower.contains("\u0434\u043e\u043d\u043c\u0430\u0440\u043a\u0435\u0442"))) {
            return true;
        }
        return handler != null && handler.field_7763 != 0 && handler.field_7761.size() >= 27;
    }

    public static boolean isPlayerInventoryContainer(class_1703 handler, class_437 screen) {
        return !ContainerScreenHelper.isAuctionContainer(handler, screen);
    }
}

