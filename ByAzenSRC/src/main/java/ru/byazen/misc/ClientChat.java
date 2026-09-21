/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_746;
import ru.byazen.util.ColorUtils;

public final class ClientChat {
    private static final int BRAND_COLOR = -7643914;

    private ClientChat() {
    }

    public static void send(String message) {
        ClientChat.send((class_2561)class_2561.method_43470((String)message));
    }

    public static void send(class_2561 message) {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            player.method_7353((class_2561)ClientChat.createPrefix().method_10852((class_2561)class_2561.method_43470((String)" >> ").method_27692(class_124.field_1064)).method_10852(message), false);
        }
    }

    private static class_5250 createPrefix() {
        String brand = "ByAzen";
        class_5250 result = class_2561.method_43473();
        for (int index = 0; index < brand.length(); ++index) {
            int color = ColorUtils.animatedGradient(-7643914, -1, index * 12, 12);
            result.method_10852((class_2561)class_2561.method_43470((String)String.valueOf(brand.charAt(index))).method_10862(class_2583.field_24360.method_27703(class_5251.method_27717((int)color))));
        }
        return result;
    }
}

