/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 */
package ru.byazen.item;

import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import ru.byazen.item.ItemBadgeCategory;

public record ItemBadge(class_2561 label, ItemBadgeCategory category) {
    public static ItemBadge fromStack(class_1799 stack) {
        ItemBadgeCategory category;
        if (stack == null || stack.method_7960()) {
            return null;
        }
        class_2561 label = stack.method_7964();
        String normalizedName = label.getString().toLowerCase(Locale.ROOT);
        if (ItemBadge.containsAny(normalizedName, "\u0442\u0430\u043b\u0438\u0441\u043c\u0430\u043d", "talisman", "\u0430\u043c\u0443\u043b\u0435\u0442", "amulet")) {
            category = ItemBadgeCategory.TALISMAN;
        } else if (ItemBadge.containsAny(normalizedName, "\u0441\u0444\u0435\u0440\u0430", "sphere", "\u0448\u0430\u0440", "orb")) {
            category = ItemBadgeCategory.SPHERE;
        } else {
            return null;
        }
        return new ItemBadge((class_2561)label.method_27661(), category);
    }

    private static boolean containsAny(String value, String ... markers) {
        for (String marker : markers) {
            if (!value.contains(marker)) continue;
            return true;
        }
        return false;
    }
}

