/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_9276
 *  net.minecraft.class_9334
 */
package ru.byazen.module.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.BlockInteractEvent;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.HudRenderEvent;
import ru.byazen.misc.BundleUse;
import ru.byazen.misc.Bundles;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.ClickSlotAction;
import ru.byazen.misc.InventoryAction;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.ItemAlerts;
import ru.byazen.misc.ItemBindBox;
import ru.byazen.misc.ItemHelperCatalog;
import ru.byazen.misc.ItemHelperEntry;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.util.InventoryController;
import ru.byazen.util.ItemStatusHudElement;

public class ItemHelperModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "item_helper";
    private final BooleanSetting enabledSetting;
    private final ModeSetting swapMode;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private final BooleanSetting visualizer;
    private final BooleanSetting visualizerAll;
    private final MultiSelectSetting visualizerItems;
    private final BooleanSetting highlightEmpty;
    private ItemStatusHudElement visualizerHud;
    private ItemHelperEntry currentItem;
    private BundleUse bundleUse;
    private int itemSlot = -1;
    private int previousSlot = -1;
    private boolean swappedFromInventory;
    private boolean releasing;

    public ItemHelperModule(EventBus eventBus) {
        super(eventBus, OWNER, "Item Helper", "\u0411\u044b\u0441\u0442\u0440\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u043f\u043e \u0431\u0438\u043d\u0434\u0430\u043c", ModuleCategory.valueOf("COMBAT"), "itemhelper", "items");
        List<ItemHelperEntry> presets = ItemHelperCatalog.list;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.swapMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit").defaultOption("Default").name("Mode").id("swap_mode").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0432\u0430\u043f\u0430: Default - \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e, Legit - \u0441 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u043e\u0439")).build();
        this.registerSetting(this.swapMode);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
        ItemHelperEntry chorus = presets.get(0);
        BindSetting chorusBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(chorus, bind)).onPressed(bind -> this.onRelease(chorus, bind)).name("\u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430").id("bind_chorus").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430")).build();
        this.registerSetting(chorusBind);
        ItemHelperEntry gapple = presets.get(1);
        BindSetting gappleBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(gapple, bind)).onPressed(bind -> this.onRelease(gapple, bind)).name("\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e").id("bind_gapple").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e")).build();
        this.registerSetting(gappleBind);
        ItemHelperEntry enchantedGapple = presets.get(2);
        BindSetting enchantedGappleBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(enchantedGapple, bind)).onPressed(bind -> this.onRelease(enchantedGapple, bind)).name("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e").id("bind_enchanted_gapple").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e")).build();
        this.registerSetting(enchantedGappleBind);
        ItemHelperEntry instantHealing = presets.get(3);
        BindSetting instantHealingBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(instantHealing, bind)).onPressed(bind -> this.onRelease(instantHealing, bind)).name("\u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f").id("bind_instant_healing").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f")).build();
        this.registerSetting(instantHealingBind);
        ItemHelperEntry shield = presets.get(4);
        BindSetting shieldBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(shield, bind)).onPressed(bind -> this.onRelease(shield, bind)).name("\u0429\u0438\u0442").id("bind_shield").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u0429\u0438\u0442")).build();
        this.registerSetting(shieldBind);
        ItemHelperEntry crossbow = presets.get(5);
        BindSetting crossbowBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(crossbow, bind)).onPressed(bind -> this.onRelease(crossbow, bind)).name("\u0410\u0440\u0431\u0430\u043b\u0435\u0442").id("bind_crossbow").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u0410\u0440\u0431\u0430\u043b\u0435\u0442")).build();
        this.registerSetting(crossbowBind);
        ItemHelperEntry milk = presets.get(6);
        BindSetting milkBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(bind -> this.onPress(milk, bind)).onPressed(bind -> this.onRelease(milk, bind)).name("\u041c\u043e\u043b\u043e\u043a\u043e").id("bind_milk").description("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u043e\u043a\u0430 \u0437\u0430\u0436\u0430\u0442\u043e: \u041c\u043e\u043b\u043e\u043a\u043e")).build();
        this.registerSetting(milkBind);
        this.visualizer = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Visualizer").id("visualizer").description("\u0421\u0435\u0442\u043a\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0441 \u0431\u0438\u043d\u0434\u0430\u043c\u0438")).build();
        this.registerSetting(this.visualizer);
        this.visualizerAll = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0412\u0441\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").id("visualizer_all").description("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u0441\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0441\u0435\u0442\u043a\u0435").visibleWhen(this.visualizer::isEnabled)).build();
        this.registerSetting(this.visualizerAll);
        MultiSelectSetting visualizerItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("\u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430", "\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "\u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f", "\u0429\u0438\u0442", "\u0410\u0440\u0431\u0430\u043b\u0435\u0442", "\u041c\u043e\u043b\u043e\u043a\u043e").selectAll(true).optionListEnabled(false).name("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").id("visualizer_items").description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0441\u0435\u0442\u043a\u0435").visibleWhen(() -> this.visualizer.isEnabled() && !this.visualizerAll.isEnabled())).build();
        visualizerItemsSetting.setOptions(new String[]{"\u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430", "\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0435 \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "\u0417\u0435\u043b\u044c\u0435 \u0438\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f", "\u0429\u0438\u0442", "\u0410\u0440\u0431\u0430\u043b\u0435\u0442", "\u041c\u043e\u043b\u043e\u043a\u043e"});
        this.visualizerItems = visualizerItemsSetting;
        this.registerSetting(visualizerItemsSetting);
        this.highlightEmpty = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Highlight Empty").id("highlight_empty").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0449\u0438\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432").visibleWhen(this.visualizer::isEnabled)).build();
        this.registerSetting(this.highlightEmpty);
        ArrayList<ItemBindBox> boxes = new ArrayList<ItemBindBox>();
        BindSetting[] binds = new BindSetting[]{chorusBind, gappleBind, enchantedGappleBind, instantHealingBind, shieldBind, crossbowBind, milkBind};
        for (int i = 0; i < presets.size(); ++i) {
            ItemHelperEntry preset = presets.get(i);
            boxes.add(new ItemBindBox(preset.getItem(), binds[i], stack -> ItemHelperModule.matchesItem(preset, stack), () -> this.isVisualizerItemEnabled(preset)));
        }
        this.visualizerHud = new ItemStatusHudElement("Item Helper", this::isVisualizerEnabled, boxes, this.highlightEmpty::isEnabled);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(HudRenderEvent.class, event -> {
            if (this.visualizerHud != null) {
                this.visualizerHud.renderFrame();
            }
        });
        this.listen(BlockInteractEvent.class, this::onBlockInteract);
    }

    private void onTick() {
        if (this.currentItem == null) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.reset();
            return;
        }
        if (this.releasing) {
            class_310.method_1551().field_1690.field_1904.method_23481(false);
            if (this.bundleUse == null && this.swappedFromInventory) {
                this.swapSlots(this.itemSlot, this.previousSlot);
            } else if (this.bundleUse == null && this.previousSlot != -1 && this.previousSlot != this.itemSlot) {
                player.method_31548().method_61496(this.previousSlot);
            }
            this.reset();
            return;
        }
        if (this.bundleUse != null && !ItemHelperModule.matches(player.method_31548().method_5438(this.itemSlot), this.currentItem)) {
            return;
        }
        class_310.method_1551().field_1690.field_1904.method_23481(true);
    }

    private void reset() {
        this.currentItem = null;
        this.itemSlot = -1;
        this.previousSlot = -1;
        this.swappedFromInventory = false;
        this.releasing = false;
        this.bundleUse = null;
    }

    private void onPress(ItemHelperEntry itemBind, BindSetting ignored) {
        this.startUsing(itemBind);
    }

    private void onRelease(ItemHelperEntry itemBind, BindSetting ignored) {
        if (this.currentItem != itemBind) {
            return;
        }
        this.releasing = true;
    }

    private void startUsing(ItemHelperEntry itemBind) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        if (this.currentItem != null) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1661 inventory = player.method_31548();
        int slot = this.findSlot(inventory, itemBind);
        boolean inBundle = this.fromBundle.isEnabled() && (itemBind.isInstantHealingPotion() ? this.findBestHealingInBundle(inventory) != null : Bundles.contains(inventory, stack -> stack.method_7909() == itemBind.getItem()));
        class_1799 preview = new class_1799((class_1935)itemBind.getItem());
        if (slot == -1 && !inBundle) {
            ItemAlerts.warnMissing(preview, itemBind.getName());
            return;
        }
        if (ItemAlerts.isBusy(null, preview, itemBind.getName())) {
            return;
        }
        if (this.fromBundle.isEnabled() && this.bundleHasBetterHealing(inventory, itemBind, slot) && this.useFromBundle(player, itemBind)) {
            return;
        }
        if (slot == -1) {
            return;
        }
        this.equip(player, itemBind, slot);
    }

    private void equip(class_746 player, ItemHelperEntry itemBind, int slot) {
        this.currentItem = itemBind;
        this.itemSlot = slot;
        this.previousSlot = player.method_31548().method_67532();
        if (slot < 9) {
            this.swappedFromInventory = false;
            if (this.previousSlot != slot) {
                player.method_31548().method_61496(slot);
            }
        } else {
            this.swappedFromInventory = true;
            this.swapSlots(slot, this.previousSlot);
        }
    }

    private boolean useFromBundle(class_746 player, ItemHelperEntry itemBind) {
        int[] found;
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null || inventory.isActive()) {
            return false;
        }
        int[] nArray = found = itemBind.isInstantHealingPotion() ? this.findBestHealingInBundle(player.method_31548()) : Bundles.findInBundle(player.method_31548(), stack -> stack.method_7909() == itemBind.getItem());
        if (found == null) {
            return false;
        }
        BundleUse session = Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], this.ftMode.isEnabled());
        if (session == null) {
            return false;
        }
        this.bundleUse = session;
        this.currentItem = itemBind;
        this.itemSlot = session.slot();
        this.previousSlot = session.slot();
        this.swappedFromInventory = false;
        return true;
    }

    private boolean bundleHasBetterHealing(class_1661 inventory, ItemHelperEntry itemBind, int slot) {
        if (slot == -1) {
            return true;
        }
        if (!itemBind.isInstantHealingPotion()) {
            return false;
        }
        int[] found = this.findBestHealingInBundle(inventory);
        return found != null && found[2] > ItemHelperModule.healingAmplifier(inventory.method_5438(slot));
    }

    private void swapSlots(int slot, int selected) {
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        ClickPolicy policy = "Legit".equalsIgnoreCase(this.swapMode.getSelectedOption()) ? ClickPolicy.SWAP : ClickPolicy.VISIBLE;
        inventory.submit(this.task(new ClickSlotAction(slot, selected), policy));
    }

    private int findSlot(class_1661 inventory, ItemHelperEntry itemBind) {
        if (itemBind.isInstantHealingPotion()) {
            return this.findBestHealingSlot(inventory);
        }
        for (int slot = 0; slot < 36; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7960() || stack.method_7909() != itemBind.getItem()) continue;
            return slot;
        }
        return -1;
    }

    private int findBestHealingSlot(class_1661 inventory) {
        int bestSlot = -1;
        int bestAmplifier = -1;
        for (int slot = 0; slot < 36; ++slot) {
            int amplifier = ItemHelperModule.healingAmplifier(inventory.method_5438(slot));
            if (amplifier <= bestAmplifier) continue;
            bestAmplifier = amplifier;
            bestSlot = slot;
        }
        return bestSlot;
    }

    private int[] findBestHealingInBundle(class_1661 inventory) {
        int[] best = null;
        int bestAmplifier = -1;
        for (int slot = 0; slot < 36; ++slot) {
            class_9276 contents = (class_9276)inventory.method_5438(slot).method_58694(class_9334.field_49650);
            if (contents == null) continue;
            for (int nested = 0; nested < contents.method_57426(); ++nested) {
                int amplifier = ItemHelperModule.healingAmplifier(contents.method_57422(nested));
                if (amplifier <= bestAmplifier) continue;
                bestAmplifier = amplifier;
                best = new int[]{slot, nested, amplifier};
            }
        }
        return best;
    }

    private void onBlockInteract(BlockInteractEvent event) {
        if (this.currentItem != null) {
            event.cancel();
        }
    }

    private boolean isVisualizerEnabled() {
        return this.enabledSetting.isEnabled() && this.visualizer.isEnabled();
    }

    private boolean isVisualizerItemEnabled(ItemHelperEntry itemBind) {
        return this.visualizerAll.isEnabled() || this.visualizerItems.getSelectedOptions().contains(itemBind.getName());
    }

    private static boolean matches(class_1799 stack, ItemHelperEntry itemBind) {
        return itemBind.isInstantHealingPotion() ? ItemHelperModule.isHealingPotion(stack) : stack.method_7909() == itemBind.getItem();
    }

    private static boolean matchesItem(ItemHelperEntry itemBind, class_1799 stack) {
        return stack.method_7909() == itemBind.getItem();
    }

    private static boolean isHealingPotion(class_1799 stack) {
        return ItemHelperModule.healingAmplifier(stack) >= 0;
    }

    private static int healingAmplifier(class_1799 stack) {
        if (stack.method_7909() != class_1802.field_8574) {
            return -1;
        }
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents == null) {
            return -1;
        }
        int amplifier = -1;
        for (class_1293 instance : contents.method_57397()) {
            if (!instance.method_5579().equals((Object)class_1294.field_5915)) continue;
            amplifier = Math.max(amplifier, instance.method_5578());
        }
        return amplifier;
    }

    private InventoryTask task(InventoryAction action, ClickPolicy policy) {
        return InventoryTask.builder().action(action).owner(OWNER).flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.HIGH).build();
    }
}

