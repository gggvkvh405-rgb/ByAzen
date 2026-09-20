/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1893
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_9276
 *  net.minecraft.class_9304
 *  net.minecraft.class_9334
 */
package ru.wexside.module.player;

import java.util.List;
import java.util.Optional;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9304;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ActionSequence;
import ru.wexside.misc.BundleUse;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.RunnableAction;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.TimedAction;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.InventoryController;

public class AutoToolModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "auto_tool";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final BooleanSetting fromInventory;
    private final BooleanSetting swapBack;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private int rememberedHotbarSlot = -1;
    private int inventorySourceSlot = -1;
    private int inventoryHotbarSlot = -1;
    private int hotbarSwitchSlot = -1;
    private boolean inventorySwapActive;
    private boolean hotbarSwitchActive;
    private boolean toolActive;
    private BundleUse bundleUse;

    public AutoToolModule(EventBus eventBus) {
        super(eventBus, OWNER, "Auto Tool", "\u0410\u0432\u0442\u043e\u0441\u043c\u0435\u043d\u0430 \u043d\u0430 \u043b\u0443\u0447\u0448\u0438\u0439 \u0438\u043d\u0441\u0442\u0440\u0443\u043c\u0435\u043d\u0442", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.fromInventory = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("From Inventory").id("from_inventory").description("\u0411\u0440\u0430\u0442\u044c \u0438\u0437 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f")).build();
        this.registerSetting(this.fromInventory);
        this.swapBack = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Swap Back").id("swap_back").description("\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442")).build();
        this.registerSetting(this.swapBack);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u0438\u043d\u0441\u0442\u0440\u0443\u043c\u0435\u043d\u0442 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        boolean mining;
        if (!this.enabledSetting.isEnabled()) {
            this.reset();
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        class_638 world = class_310.method_1551().field_1687;
        if (player == null || world == null || player.method_7325()) {
            this.reset();
            return;
        }
        class_2338 pos = this.targetedBlock(world);
        boolean bl = mining = pos != null && class_310.method_1551().field_1690.field_1886.method_1434();
        if (!mining) {
            this.onStopMining();
            return;
        }
        class_2680 state = world.method_8320(pos);
        int bestSlot = this.findBestSlot(state, player, this.fromInventory.isEnabled() ? 36 : 9);
        if (this.fromBundle.isEnabled() && this.tryUseBundle(player, state, bestSlot)) {
            this.toolActive = true;
            return;
        }
        if (this.bundleUse != null) {
            if (this.inventoryBetterThanBundle(player, state, bestSlot)) {
                this.restoreBundle();
            }
            this.toolActive = true;
            return;
        }
        if (bestSlot == -1) {
            this.toolActive = true;
            return;
        }
        if (bestSlot <= 8) {
            this.switchHotbar(player, bestSlot);
            this.toolActive = true;
            return;
        }
        this.switchFromInventory(player, bestSlot);
        this.toolActive = true;
    }

    private boolean inventoryBetterThanBundle(class_746 player, class_2680 state, int slot) {
        if (slot == -1 || this.bundleUse == null) {
            return false;
        }
        class_1661 inventory = player.method_31548();
        return this.miningSpeed(inventory.method_5438(slot), state) > this.miningSpeed(inventory.method_5438(this.bundleUse.slot()), state);
    }

    private boolean restoreBundle() {
        if (this.bundleUse == null) {
            return true;
        }
        this.bundleUse = null;
        return true;
    }

    private void restorePrevious() {
        if (this.inventorySwapActive) {
            this.restoreInventorySwap();
        } else if (this.hotbarSwitchActive) {
            this.restoreHotbarSwitch();
        } else {
            this.clearSwapState();
        }
    }

    private void restoreInventorySwap() {
        if (this.inventorySourceSlot < 9 || this.inventoryHotbarSlot < 0 || this.inventoryHotbarSlot > 8 || this.rememberedHotbarSlot < 0 || this.rememberedHotbarSlot > 8) {
            this.clearSwapState();
            return;
        }
        InventoryController inventory = WexSideClient.getInventoryController();
        if (inventory == null) {
            this.clearSwapState();
            return;
        }
        int sourceSlot = this.inventorySourceSlot;
        int hotbarSlot = this.inventoryHotbarSlot;
        int remembered = this.rememberedHotbarSlot;
        inventory.submit(InventoryTask.builder().action(this.swapSequence(this.toContainerSlot(sourceSlot), hotbarSlot, () -> {
            class_746 player = class_310.method_1551().field_1724;
            if (player != null) {
                this.selectHotbar(player, remembered);
            }
        })).owner(OWNER).flag(TaskFlag.REPLACE).policy(ClickPolicy.VISIBLE).priority(TaskPriority.HIGH).build());
        this.clearSwapState();
    }

    private void restoreHotbarSwitch() {
        if (this.rememberedHotbarSlot < 0 || this.rememberedHotbarSlot > 8) {
            this.clearSwapState();
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            this.selectHotbar(player, this.rememberedHotbarSlot);
        }
        this.clearSwapState();
    }

    private void selectHotbar(class_746 player, int slot) {
        if (slot < 0 || slot > 8) {
            return;
        }
        class_1661 inventory = player.method_31548();
        if (inventory.method_67532() == slot) {
            return;
        }
        inventory.method_61496(slot);
    }

    private float miningSpeed(class_1799 stack, class_2680 state) {
        if (stack.method_7960()) {
            return 1.0f;
        }
        float speed = stack.method_7924(state);
        if (speed <= 1.0f) {
            return 1.0f;
        }
        int efficiency = this.efficiencyLevel(stack);
        if (efficiency > 0) {
            speed += (float)(efficiency * efficiency + 1);
        }
        return this.isCorrectTool(stack, state) ? speed : speed * 0.3f;
    }

    private class_2338 targetedBlock(class_638 world) {
        class_239 hit = class_310.method_1551().field_1765;
        if (!(hit instanceof class_3965)) {
            return null;
        }
        class_3965 blockHit = (class_3965)hit;
        class_2338 pos = blockHit.method_17777();
        return world.method_8320(pos).method_26215() ? null : pos;
    }

    private int findBestSlot(class_2680 state, class_746 player, int limit) {
        if (state.method_26215()) {
            return -1;
        }
        int bestSlot = -1;
        float bestSpeed = 1.0f;
        class_1661 inventory = player.method_31548();
        for (int slot = 0; slot < limit; ++slot) {
            float speed;
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7960() || !((speed = this.miningSpeed(stack, state)) > bestSpeed)) continue;
            bestSpeed = speed;
            bestSlot = slot;
        }
        return bestSlot;
    }

    private int efficiencyLevel(class_1799 stack) {
        class_9304 enchants = (class_9304)stack.method_58695(class_9334.field_49633, (Object)class_9304.field_49385);
        for (class_6880 enchantment : enchants.method_57534()) {
            Optional key = enchantment.method_40230();
            if (!key.isPresent() || !((class_5321)key.get()).equals(class_1893.field_9131)) continue;
            return enchants.method_57536(enchantment);
        }
        return 0;
    }

    private int toContainerSlot(int slot) {
        return slot < 9 ? slot + 36 : slot;
    }

    private boolean tryUseBundle(class_746 player, class_2680 state, int bestSlot) {
        float current;
        if (state.method_26215() || this.bundleUse != null) {
            return false;
        }
        InventoryController inventory = WexSideClient.getInventoryController();
        if (inventory == null || inventory.isActive()) {
            return false;
        }
        class_1661 playerInventory = player.method_31548();
        int[] found = this.findInBundles(playerInventory, state, current = bestSlot == -1 ? 1.0f : this.miningSpeed(playerInventory.method_5438(bestSlot), state));
        if (found == null) {
            return false;
        }
        this.bundleUse = Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], this.ftMode.isEnabled());
        return this.bundleUse != null;
    }

    private void onStopMining() {
        if (!this.toolActive) {
            return;
        }
        if (this.swapBack.isEnabled()) {
            this.restorePrevious();
        } else {
            this.clearSwapState();
        }
        if (!this.restoreBundle()) {
            return;
        }
        this.toolActive = false;
    }

    private void switchHotbar(class_746 player, int slot) {
        if (this.inventorySwapActive) {
            if (slot == this.inventoryHotbarSlot) {
                this.selectHotbar(player, slot);
                return;
            }
            if (this.swapBack.isEnabled()) {
                this.restorePrevious();
            } else {
                this.clearInventorySwap();
            }
        }
        if (this.hotbarSwitchActive && this.hotbarSwitchSlot == slot) {
            this.selectHotbar(player, slot);
            return;
        }
        if (this.shouldRememberSlot()) {
            this.rememberedHotbarSlot = player.method_31548().method_67532();
        }
        this.hotbarSwitchActive = true;
        this.hotbarSwitchSlot = slot;
        this.selectHotbar(player, slot);
    }

    private void switchFromInventory(class_746 player, int slot) {
        InventoryController inventory;
        if (slot < 9 || slot >= 36) {
            return;
        }
        if (this.inventorySwapActive && this.inventorySourceSlot == slot) {
            return;
        }
        if (this.hotbarSwitchActive) {
            if (this.swapBack.isEnabled()) {
                this.restorePrevious();
            } else {
                this.clearHotbarSwitch();
            }
        }
        if (this.inventorySwapActive) {
            if (this.swapBack.isEnabled()) {
                this.restorePrevious();
            } else {
                this.clearInventorySwap();
            }
        }
        if ((inventory = WexSideClient.getInventoryController()) == null || inventory.process(OWNER)) {
            return;
        }
        if (this.shouldRememberSlot()) {
            this.rememberedHotbarSlot = player.method_31548().method_67532();
        }
        this.inventoryHotbarSlot = this.pickHotbarDestination(player.method_31548());
        this.inventorySourceSlot = slot;
        this.inventorySwapActive = true;
        int hotbarSlot = this.inventoryHotbarSlot;
        inventory.submit(InventoryTask.builder().action(this.swapSequence(this.toContainerSlot(slot), hotbarSlot, () -> {
            class_746 current = class_310.method_1551().field_1724;
            if (current != null) {
                this.selectHotbar(current, hotbarSlot);
            }
        })).owner(OWNER).flag(TaskFlag.REPLACE).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).build());
    }

    private InventoryAction swapSequence(int containerSlot, int hotbarSlot, Runnable after) {
        return new ActionSequence(List.of(new TimedAction(0, new ClickSlotAction(containerSlot, hotbarSlot)), new TimedAction(0, new RunnableAction(after))));
    }

    private int[] findInBundles(class_1661 inventory, class_2680 state, float minSpeed) {
        int[] best = null;
        float bestSpeed = minSpeed;
        for (int slot = 0; slot < 36; ++slot) {
            class_9276 contents = (class_9276)inventory.method_5438(slot).method_58694(class_9334.field_49650);
            if (contents == null) continue;
            for (int nested = 0; nested < contents.method_57426(); ++nested) {
                float speed;
                class_1799 stack = contents.method_57422(nested);
                if (stack.method_7960() || !((speed = this.miningSpeed(stack, state)) > bestSpeed)) continue;
                bestSpeed = speed;
                best = new int[]{slot, nested};
            }
        }
        return best;
    }

    private void clearHotbarSwitch() {
        this.hotbarSwitchActive = false;
        this.hotbarSwitchSlot = -1;
    }

    private void clearInventorySwap() {
        this.inventorySwapActive = false;
        this.inventorySourceSlot = -1;
        this.inventoryHotbarSlot = -1;
    }

    private void clearSwapState() {
        this.clearHotbarSwitch();
        this.clearInventorySwap();
        this.rememberedHotbarSlot = -1;
    }

    private void reset() {
        this.restoreBundle();
        this.bundleUse = null;
        this.hotbarSwitchActive = false;
        this.inventorySwapActive = false;
        this.hotbarSwitchSlot = -1;
        this.inventorySourceSlot = -1;
        this.rememberedHotbarSlot = -1;
        this.inventoryHotbarSlot = -1;
        this.toolActive = false;
    }

    private boolean shouldRememberSlot() {
        return !this.hotbarSwitchActive && !this.inventorySwapActive && this.rememberedHotbarSlot == -1;
    }

    private boolean isCorrectTool(class_1799 stack, class_2680 state) {
        return !state.method_29291() || stack.method_7951(state);
    }

    private int pickHotbarDestination(class_1661 inventory) {
        int selected = inventory.method_67532();
        int fallback = -1;
        for (int i = 0; i < 9; ++i) {
            int slot = (selected + i) % 9;
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7960()) {
                return slot;
            }
            if (this.isProtected(stack) || fallback != -1) continue;
            fallback = slot;
        }
        return fallback == -1 ? selected : fallback;
    }

    private boolean isProtected(class_1799 stack) {
        return stack.method_7958() || stack.method_31574(class_1802.field_8288) || stack.method_57826(class_9334.field_50075);
    }
}

