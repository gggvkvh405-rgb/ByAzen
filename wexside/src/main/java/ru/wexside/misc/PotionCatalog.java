/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1842
 *  net.minecraft.class_1844
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_6880
 *  net.minecraft.class_6880$class_6883
 *  net.minecraft.class_7923
 */
package ru.wexside.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.ServerKind;
import ru.wexside.server.ServerHelperActions;

public final class PotionCatalog {
    private static final List<PotionCatalogEntry> ALL_ENTRIES = PotionCatalog.buildEntries();
    private static final Map<String, PotionCatalogEntry> ENTRIES_BY_ID = PotionCatalog.indexEntries(ALL_ENTRIES);

    private PotionCatalog() {
    }

    public static List<PotionCatalogEntry> inventoryEntries() {
        return ALL_ENTRIES;
    }

    public static List<PotionCatalogEntry> allEntries() {
        return ALL_ENTRIES;
    }

    public static PotionCatalogEntry findById(String id) {
        return id == null ? null : ENTRIES_BY_ID.get(id);
    }

    public static int findSlot(class_1661 inventory, PotionCatalogEntry entry, ServerKind server) {
        if (inventory == null || entry == null) {
            return -1;
        }
        for (int slot = 0; slot < inventory.method_5439(); ++slot) {
            if (!entry.matches(inventory.method_5438(slot), server)) continue;
            return slot;
        }
        return -1;
    }

    public static class_1799 findStack(class_1661 inventory, PotionCatalogEntry entry, ServerKind server) {
        int slot = PotionCatalog.findSlot(inventory, entry, server);
        return slot < 0 ? class_1799.field_8037 : inventory.method_5438(slot);
    }

    private static List<PotionCatalogEntry> buildEntries() {
        ArrayList<PotionCatalogEntry> entries = new ArrayList<PotionCatalogEntry>();
        for (class_6880.class_6883 potionEntry : class_7923.field_41179.method_42017().toList()) {
            class_2960 id = potionEntry.method_40237().method_29177();
            class_1842 potion = (class_1842)potionEntry.comp_349();
            String name = class_2561.method_43471((String)potion.method_63990()).getString();
            int color = new class_1844((class_6880)potionEntry).method_8064();
            entries.add(PotionCatalogEntry.forPotion(id.toString(), name, color, (class_6880<class_1842>)potionEntry));
        }
        ServerHelperActions.ALL.stream().map(PotionCatalogEntry::forServerAction).forEach(entries::add);
        return List.copyOf(entries);
    }

    private static Map<String, PotionCatalogEntry> indexEntries(List<PotionCatalogEntry> entries) {
        LinkedHashMap<String, PotionCatalogEntry> index = new LinkedHashMap<String, PotionCatalogEntry>();
        for (PotionCatalogEntry entry : entries) {
            index.put(entry.getId(), entry);
        }
        return Map.copyOf(index);
    }
}

