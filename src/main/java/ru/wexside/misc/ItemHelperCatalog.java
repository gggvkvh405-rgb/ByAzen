/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1802
 */
package ru.wexside.misc;

import java.util.List;
import net.minecraft.class_1802;
import ru.wexside.misc.ItemHelperEntry;

public final class ItemHelperCatalog {
    public static final ItemHelperEntry CHORUS = new ItemHelperEntry("chorus", "\u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430", class_1802.field_8233, false);
    public static final ItemHelperEntry GOLDEN_APPLE = new ItemHelperEntry("gapple", "\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", class_1802.field_8463, false);
    public static final ItemHelperEntry ENCHANTED_GOLDEN_APPLE = new ItemHelperEntry("enchanted_gapple", "\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", class_1802.field_8367, false);
    public static final ItemHelperEntry INSTANT_HEALING = new ItemHelperEntry("instant_healing", "\u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f", class_1802.field_8574, true);
    public static final ItemHelperEntry SHIELD = new ItemHelperEntry("shield", "\u0429\u0438\u0442", class_1802.field_8255, false);
    public static final ItemHelperEntry CROSSBOW = new ItemHelperEntry("crossbow", "\u0410\u0440\u0431\u0430\u043b\u0435\u0442", class_1802.field_8399, false);
    public static final ItemHelperEntry MILK = new ItemHelperEntry("milk", "\u041c\u043e\u043b\u043e\u043a\u043e", class_1802.field_8103, false);
    public static final List<ItemHelperEntry> list = List.of(CHORUS, GOLDEN_APPLE, ENCHANTED_GOLDEN_APPLE, INSTANT_HEALING, SHIELD, CROSSBOW, MILK);
    public static final ItemHelperEntry itemHelperEntry2 = CHORUS;
    public static final ItemHelperEntry itemHelperEntry = GOLDEN_APPLE;
    public static final ItemHelperEntry itemHelperEntry3 = ENCHANTED_GOLDEN_APPLE;
    public static final ItemHelperEntry itemHelperEntry4 = INSTANT_HEALING;
    public static final ItemHelperEntry itemHelperEntry5 = SHIELD;
    public static final ItemHelperEntry itemHelperEntry6 = CROSSBOW;
    public static final ItemHelperEntry itemHelperEntry7 = MILK;

    private ItemHelperCatalog() {
    }
}

