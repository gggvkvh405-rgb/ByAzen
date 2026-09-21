/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1713
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.Inventories;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class AutoEatModule
extends Module
implements ConfigSerializable {
    public static volatile boolean eating;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final NumberSetting satiety;
    private int previousHotbarSlot = -1;
    private int swappedInventorySlot = -1;
    private int swappedHotbarSlot = -1;
    private boolean useKeyHeld;

    public AutoEatModule(EventBus eventBus) {
        super(eventBus, "auto_eat", "Auto Eat", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0435\u0441\u0442 \u043f\u0440\u0438 \u043d\u0438\u0437\u043a\u043e\u0439 \u0441\u044b\u0442\u043e\u0441\u0442\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.satiety = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 20.0).defaultValue(18.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Satiety").id("satiety").description("\u041d\u0430\u0447\u0438\u043d\u0430\u0442\u044c \u0435\u0441\u0442\u044c \u043f\u0440\u0438 \u0441\u044b\u0442\u043e\u0441\u0442\u0438 <=")).build();
        this.registerSetting(this.satiety);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(WorldSessionEvent.class, event -> this.reset());
    }

    private void onTick() {
        class_746 player = class_310.method_1551().field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || class_310.method_1551().field_1687 == null) {
            this.reset();
            return;
        }
        int foodLevel = player.method_7344().method_7586();
        int threshold = class_3532.method_15340((int)this.satiety.getIntValue(), (int)0, (int)20);
        if (!eating) {
            if (foodLevel < 20 && foodLevel <= threshold) {
                eating = true;
            } else {
                this.releaseUseKey();
                return;
            }
        }
        if (foodLevel >= 20) {
            this.stopEating(player);
            return;
        }
        switch (this.prepareFood(player).ordinal()) {
            case 0: {
                class_310.method_1551().field_1690.field_1904.method_23481(true);
                this.useKeyHeld = true;
                break;
            }
            case 1: {
                this.releaseUseKey();
                break;
            }
            case 2: {
                this.reset();
            }
        }
    }

    private EatState prepareFood(class_746 player) {
        if (this.swappedInventorySlot != -1 && !Inventories.isFood(player.method_6047())) {
            this.restoreInventorySwap(player);
        }
        if (Inventories.isFood(player.method_6047())) {
            return this.useFood(player, class_1268.field_5808);
        }
        if (Inventories.isFood(player.method_6079())) {
            return this.useFood(player, class_1268.field_5810);
        }
        int hotbarSlot = Inventories.findFoodHotbar();
        if (hotbarSlot != -1) {
            this.selectHotbar(player, hotbarSlot);
            return this.useFood(player, class_1268.field_5808);
        }
        int inventorySlot = Inventories.findFoodInventory();
        if (inventorySlot != -1) {
            this.swapFromInventory(player, inventorySlot);
            return this.useFood(player, class_1268.field_5808);
        }
        return EatState.NONE;
    }

    private EatState useFood(class_746 player, class_1268 hand) {
        if (player.method_6115()) {
            if (player.method_6058() != hand) {
                player.method_6075();
                return EatState.WAIT;
            }
            return EatState.READY;
        }
        class_636 interactions = class_310.method_1551().field_1761;
        if (interactions != null) {
            interactions.method_2919((class_1657)player, hand);
        }
        return EatState.READY;
    }

    private void selectHotbar(class_746 player, int slot) {
        class_1661 inventory = player.method_31548();
        if (this.previousHotbarSlot == -1) {
            this.previousHotbarSlot = inventory.method_67532();
        }
        inventory.method_61496(slot);
    }

    private void swapFromInventory(class_746 player, int slot) {
        class_636 interactions = class_310.method_1551().field_1761;
        if (this.swappedInventorySlot != -1 || interactions == null) {
            return;
        }
        int selected = player.method_31548().method_67532();
        interactions.method_2906(player.field_7512.field_7763, slot, selected, class_1713.field_7791, (class_1657)player);
        this.swappedInventorySlot = slot;
        this.swappedHotbarSlot = selected;
    }

    private void restoreInventorySwap(class_746 player) {
        if (this.swappedInventorySlot == -1) {
            return;
        }
        class_636 interactions = class_310.method_1551().field_1761;
        if (interactions != null) {
            interactions.method_2906(player.field_7512.field_7763, this.swappedInventorySlot, this.swappedHotbarSlot, class_1713.field_7791, (class_1657)player);
        }
        this.swappedInventorySlot = -1;
        this.swappedHotbarSlot = -1;
    }

    private void restoreHotbar(class_746 player) {
        if (this.previousHotbarSlot == -1) {
            return;
        }
        player.method_31548().method_61496(this.previousHotbarSlot);
        this.previousHotbarSlot = -1;
    }

    private void stopEating(class_746 player) {
        this.releaseUseKey();
        eating = false;
        this.restoreInventorySwap(player);
        this.restoreHotbar(player);
    }

    private void reset() {
        this.releaseUseKey();
        eating = false;
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            this.restoreInventorySwap(player);
            this.restoreHotbar(player);
        } else {
            this.swappedInventorySlot = -1;
            this.swappedHotbarSlot = -1;
            this.previousHotbarSlot = -1;
        }
    }

    private void releaseUseKey() {
        if (!this.useKeyHeld) {
            return;
        }
        class_310.method_1551().field_1690.field_1904.method_23481(false);
        this.useKeyHeld = false;
    }

    private static enum EatState {
        READY,
        WAIT,
        NONE;

    }
}

