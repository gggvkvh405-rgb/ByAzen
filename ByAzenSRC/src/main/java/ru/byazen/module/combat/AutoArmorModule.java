/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10192
 *  net.minecraft.class_1304
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_490
 *  net.minecraft.class_5134
 *  net.minecraft.class_746
 *  net.minecraft.class_9276
 *  net.minecraft.class_9285
 *  net.minecraft.class_9334
 */
package ru.byazen.module.combat;

import java.util.HashSet;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_490;
import net.minecraft.class_5134;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.Bundles;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.Inventories;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.SwapSlotsAction;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.InventoryController;

public class AutoArmorModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "auto_armor";
    private static final long BUNDLE_RETRY_DELAY_MS = 3000L;
    private static final class_1304[] ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final class_1304[] ARMOR_SLOTS_KEEP_ELYTRA = new class_1304[]{class_1304.field_6169, class_1304.field_6172, class_1304.field_6166};
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private final BooleanSetting byDurability;
    private final NumberSetting durabilityThreshold;
    private long nextBundleAttemptAt;

    public AutoArmorModule(EventBus eventBus) {
        super(eventBus, OWNER, "Auto Armor", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043d\u0430\u0434\u0435\u0432\u0430\u0435\u0442 \u043b\u0443\u0447\u0448\u0443\u044e \u0431\u0440\u043e\u043d\u044e", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0423\u0447\u0438\u0442\u044b\u0432\u0430\u0442\u044c \u0431\u0440\u043e\u043d\u044e \u0432 \u043c\u0435\u0448\u043a\u0430\u0445")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
        this.byDurability = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u041f\u043e \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438").id("by_durability").description("\u041c\u0435\u043d\u044f\u0442\u044c \u0431\u0440\u043e\u043d\u044e, \u043a\u043e\u0433\u0434\u0430 \u0435\u0451 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c \u0443\u043f\u0430\u043b\u0430 \u0434\u043e \u043f\u0440\u0435\u0434\u0435\u043b\u0430")).build();
        this.registerSetting(this.byDurability);
        this.durabilityThreshold = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 99.0).defaultValue(10.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u041f\u043e\u0440\u043e\u0433 \u043f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u0438").id("durability_threshold").description("\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c, \u043f\u0440\u0438 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u0438 \u043d\u0438\u0436\u0435 \u0431\u0440\u043e\u043d\u044f \u043c\u0435\u043d\u044f\u0435\u0442\u0441\u044f \u043d\u0430 \u0446\u0435\u043b\u0443\u044e").visibleWhen(this.byDurability::isEnabled)).build();
        this.registerSetting(this.durabilityThreshold);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        class_1304[] slots;
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        if (!this.enabledSetting.isEnabled()) {
            inventory.setString(OWNER);
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || player.method_7325()) {
            return;
        }
        class_437 screen = class_310.method_1551().field_1755;
        if (screen != null && !(screen instanceof class_490)) {
            return;
        }
        boolean wearingElytra = player.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
        for (class_1304 slot : slots = wearingElytra ? ARMOR_SLOTS_KEEP_ELYTRA : ARMOR_SLOTS) {
            int armorSlot = Inventories.findArmorSlot(slot);
            if (armorSlot == -1) continue;
            int bestSlot = this.findBestArmorSlot(player, slot);
            if (this.tryEquipFromBundle(player, inventory, slot, armorSlot, bestSlot)) {
                return;
            }
            if (bestSlot == -1 || !this.isBetterArmor(slot, bestSlot)) continue;
            inventory.submit(InventoryTask.builder().action(new SwapSlotsAction(armorSlot, bestSlot)).owner(OWNER).flag(TaskFlag.REPLACE).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).build());
            return;
        }
    }

    private boolean isBetterArmor(class_1304 slot, int inventorySlot) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || inventorySlot < 0 || inventorySlot >= 36) {
            return false;
        }
        class_1799 candidate = player.method_31548().method_5438(inventorySlot);
        if (candidate.method_7960()) {
            return false;
        }
        class_10192 equippable = (class_10192)candidate.method_58694(class_9334.field_54196);
        if (equippable == null || equippable.comp_3174() != slot) {
            return false;
        }
        class_1799 equipped = player.method_6118(slot);
        if (this.isBelowDurabilityThreshold(equipped)) {
            return !this.isBelowDurabilityThreshold(candidate);
        }
        return AutoArmorModule.getProtection(candidate, slot) > AutoArmorModule.getProtection(equipped, slot);
    }

    private boolean isBelowDurabilityThreshold(class_1799 stack) {
        if (!this.byDurability.isEnabled() || stack.method_7960() || !stack.method_7963()) {
            return false;
        }
        int maxDamage = stack.method_7936();
        if (maxDamage <= 0) {
            return false;
        }
        double remainingPercent = (double)(maxDamage - stack.method_7919()) * 100.0 / (double)maxDamage;
        return remainingPercent <= this.durabilityThreshold.getValue();
    }

    private int findBestArmorSlot(class_746 player, class_1304 slot) {
        class_1799 equipped = player.method_6118(slot);
        boolean replaceDamaged = this.isBelowDurabilityThreshold(equipped);
        int bestProtection = replaceDamaged ? Integer.MIN_VALUE : AutoArmorModule.getProtection(equipped, slot);
        int bestSlot = -1;
        HashSet<Integer> seenProtection = new HashSet<Integer>();
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            int protection;
            class_10192 equippable;
            class_1799 stack = inventory.method_5438(i);
            if (stack.method_7960() || (equippable = (class_10192)stack.method_58694(class_9334.field_54196)) == null || equippable.comp_3174() != slot || replaceDamaged && this.isBelowDurabilityThreshold(stack) || seenProtection.contains(protection = AutoArmorModule.getProtection(stack, slot))) continue;
            seenProtection.add(protection);
            if (protection <= bestProtection) continue;
            bestProtection = protection;
            bestSlot = i;
        }
        return bestSlot;
    }

    private boolean tryEquipFromBundle(class_746 player, InventoryController inventory, class_1304 slot, int armorSlot, int bestInventorySlot) {
        int[] found;
        int bestProtection;
        if (!this.fromBundle.isEnabled() || inventory.isActive()) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now < this.nextBundleAttemptAt) {
            return false;
        }
        class_1661 playerInventory = player.method_31548();
        class_1799 equipped = player.method_6118(slot);
        boolean replaceDamaged = this.isBelowDurabilityThreshold(equipped);
        int n = bestProtection = replaceDamaged ? Integer.MIN_VALUE : AutoArmorModule.getProtection(equipped, slot);
        if (bestInventorySlot != -1) {
            class_1799 best = playerInventory.method_5438(bestInventorySlot);
            if (!replaceDamaged || !this.isBelowDurabilityThreshold(best)) {
                bestProtection = Math.max(bestProtection, AutoArmorModule.getProtection(best, slot));
            }
        }
        if ((found = this.findBestInBundle(playerInventory, slot, bestProtection, replaceDamaged)) == null) {
            return false;
        }
        if (Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], armorSlot, this.ftMode.isEnabled())) {
            return true;
        }
        this.nextBundleAttemptAt = now + 3000L;
        return false;
    }

    private int[] findBestInBundle(class_1661 inventory, class_1304 slot, int minProtection, boolean replaceDamaged) {
        int[] best = null;
        int bestProtection = minProtection;
        for (int i = 0; i < 36; ++i) {
            class_9276 contents = (class_9276)inventory.method_5438(i).method_58694(class_9334.field_49650);
            if (contents == null) continue;
            for (int nested = 0; nested < contents.method_57426(); ++nested) {
                int protection;
                class_10192 equippable;
                class_1799 stack = contents.method_57422(nested);
                if (stack.method_7960() || (equippable = (class_10192)stack.method_58694(class_9334.field_54196)) == null || equippable.comp_3174() != slot || replaceDamaged && this.isBelowDurabilityThreshold(stack) || (protection = AutoArmorModule.getProtection(stack, slot)) <= bestProtection) continue;
                bestProtection = protection;
                best = new int[]{i, nested};
            }
        }
        return best;
    }

    private static int getProtection(class_1799 stack, class_1304 slot) {
        class_9285 modifiers = (class_9285)stack.method_58694(class_9334.field_49636);
        if (modifiers == null) {
            return 0;
        }
        double[] total = new double[]{0.0};
        modifiers.method_57482(slot, (attribute, modifier) -> {
            if (attribute.method_55838(class_5134.field_23724)) {
                total[0] = total[0] + modifier.comp_2449();
            }
        });
        return (int)total[0];
    }
}

