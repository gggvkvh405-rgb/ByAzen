/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1703
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 */
package ru.byazen.misc;

import net.minecraft.class_1703;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public final class ItemAlerts {
    private ItemAlerts() {
    }

    public static void warnMissing(class_1799 stack, String name) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        client.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u041d\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430: " + name)), true);
    }

    public static boolean isBusy(class_1703 ignored, class_1799 stack, String ignoredName) {
        class_310 client = class_310.method_1551();
        return client.field_1724 != null && client.field_1724.method_7357().method_7904(stack);
    }
}

