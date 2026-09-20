/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.module.combat;

import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BundleUse;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.InventoryController;

public class MaceHelperModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "mace_helper";
    private static final int PHASE_IDLE = 0;
    private static final int PHASE_HOLD = 2;
    private static final int PHASE_RESTORE = 3;
    private static final int SESSION_TICKS = 100;
    private static final int BUNDLE_WAIT_TICKS = 20;
    private static volatile MaceHelperModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private boolean swapFromInventory;
    private boolean wasAirborne;
    private int restoreHotbarSlot;
    private int bundleWaitTicks;
    private int maceContainerSlot;
    private int selectedHotbarSlot;
    private int phase;
    private boolean pendingBundleExtract;
    private BundleUse bundleUse;
    private int sessionTicks;
    private boolean usingInventorySwap;

    public MaceHelperModule(EventBus eventBus) {
        super(eventBus, OWNER, "Mace Helper", "\u041f\u043e\u0441\u043b\u0435 \u043f\u0440\u043e\u0436\u0430\u0442\u0438\u044f \u0431\u0438\u043d\u0434\u0430 Wind Charge \u0432 Server Helper \u0431\u0435\u0440\u0451\u0442 \u0431\u0443\u043b\u0430\u0432\u0443 \u0434\u043e \u043f\u0440\u0438\u0437\u0435\u043c\u043b\u0435\u043d\u0438\u044f", ModuleCategory.valueOf("COMBAT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u0431\u0443\u043b\u0430\u0432\u0443 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (this.pendingBundleExtract) {
            if (!this.enabledSetting.isEnabled()) {
                this.pendingBundleExtract = false;
                return;
            }
            this.tickBundleExtract();
            return;
        }
        if (this.phase == 0) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        InventoryController inventory = WexSideClient.getInventoryController();
        if (player == null || inventory == null) {
            this.reset();
            return;
        }
        if (--this.sessionTicks <= 0) {
            this.restoreMace(player, inventory);
            this.reset();
            return;
        }
        if (this.phase == 2) {
            this.tickHold(player, inventory);
        } else {
            this.ensureHoldingMace(player);
        }
    }

    public static void refreshSession() {
        MaceHelperModule module = instance;
        if (module == null || module.phase == 0) {
            return;
        }
        module.sessionTicks = 100;
        if (module.phase == 3) {
            InventoryController inventory = WexSideClient.getInventoryController();
            if (inventory != null) {
                MaceHelperModule.cancelOwnedTasks(inventory);
            }
            module.phase = 2;
            module.wasAirborne = false;
        }
    }

    private void tickHold(class_746 player, InventoryController inventory) {
        this.ensureMaceSelected(player, inventory);
        if (!(player.method_24828() || player.method_5799() || player.method_6128())) {
            this.wasAirborne = true;
            return;
        }
        if (!this.wasAirborne) {
            return;
        }
        if ((this.usingInventorySwap || this.bundleUse != null) && inventory.t()) {
            return;
        }
        this.restoreMace(player, inventory);
        if (this.usingInventorySwap) {
            this.phase = 3;
        } else {
            this.reset();
        }
    }

    public static void selectMaceHotbar() {
        MaceHelperModule module = instance;
        if (module == null || module.phase == 0 || module.usingInventorySwap) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        int slot = Inventories.findHotbarSlot(class_1802.field_49814);
        if (slot != -1 && player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
        }
    }

    public static boolean isEnabled() {
        MaceHelperModule module = instance;
        if (module == null || !module.fromBundle.isEnabled()) {
            return false;
        }
        if (MaceHelperModule.findMaceContainerSlot() != -1) {
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null && Bundles.contains(player.method_31548(), stack -> stack.method_31574(class_1802.field_49814));
    }

    private void ensureHoldingMace(class_746 player) {
        if (!player.method_6047().method_31574(class_1802.field_49814)) {
            this.reset();
        }
    }

    private void tickBundleExtract() {
        if (--this.bundleWaitTicks <= 0) {
            this.pendingBundleExtract = false;
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        InventoryController inventory = WexSideClient.getInventoryController();
        if (player == null || inventory == null || inventory.isActive() || inventory.t()) {
            return;
        }
        int[] found = Bundles.findInBundle(player.method_31548(), stack -> stack.method_31574(class_1802.field_49814));
        if (found == null) {
            this.pendingBundleExtract = false;
            return;
        }
        BundleUse use = Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], this.ftMode.isEnabled());
        this.pendingBundleExtract = false;
        if (use == null) {
            return;
        }
        this.reset();
        this.bundleUse = use;
        this.restoreHotbarSlot = use.slot();
        this.usingInventorySwap = false;
        this.swapFromInventory = false;
        this.phase = 2;
        this.sessionTicks = 100;
    }

    private void restoreMace(class_746 player, InventoryController inventory) {
        if (this.bundleUse != null) {
            this.bundleUse = null;
            return;
        }
        if (this.usingInventorySwap) {
            if (this.maceContainerSlot != -1 && this.selectedHotbarSlot != -1) {
                this.submitHotbarSwap(inventory);
            }
        } else if (this.restoreHotbarSlot != -1) {
            player.method_31548().method_61496(this.restoreHotbarSlot);
        }
    }

    public static boolean isEnabled2() {
        MaceHelperModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && (MaceHelperModule.findMaceContainerSlot() != -1 || MaceHelperModule.isEnabled());
    }

    public static boolean isEnabled3() {
        return Inventories.findHotbarSlot(class_1802.field_49814) != -1;
    }

    public static int getIntType() {
        return MaceHelperModule.findMaceContainerSlot();
    }

    public static int findMaceContainerSlot() {
        int hotbar = Inventories.findHotbarSlot(class_1802.field_49814);
        if (hotbar != -1) {
            return Inventories.toContainerSlot(hotbar);
        }
        int inventory = Inventories.findInventorySlot(class_1802.field_49814);
        if (inventory != -1) {
            return Inventories.toContainerSlot(inventory);
        }
        return -1;
    }

    private void ensureMaceSelected(class_746 player, InventoryController inventory) {
        if (inventory.isActive()) {
            return;
        }
        int hotbar = Inventories.findHotbarSlot(class_1802.field_49814);
        if (hotbar != -1) {
            if (player.method_31548().method_67532() != hotbar) {
                player.method_31548().method_61496(hotbar);
            }
            return;
        }
        if (!this.swapFromInventory || inventory.t()) {
            return;
        }
        int inventorySlot = Inventories.findInventorySlot(class_1802.field_49814);
        if (inventorySlot == -1) {
            return;
        }
        this.selectedHotbarSlot = player.method_31548().method_67532();
        this.maceContainerSlot = Inventories.toContainerSlot(inventorySlot);
        this.submitHotbarSwap(inventory);
    }

    public static void startSwapSession(int maceSlot, int hotbarSlot) {
        MaceHelperModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return;
        }
        module.reset();
        module.usingInventorySwap = true;
        module.swapFromInventory = false;
        module.maceContainerSlot = maceSlot;
        module.selectedHotbarSlot = hotbarSlot;
        module.phase = 2;
        module.sessionTicks = 100;
    }

    public static boolean isEnabled4() {
        MaceHelperModule module = instance;
        return module != null && module.phase != 0 && !module.usingInventorySwap;
    }

    public static boolean isEnabled5() {
        MaceHelperModule module = instance;
        return module != null && module.phase != 0;
    }

    private void queueBundleExtract() {
        this.pendingBundleExtract = true;
        this.bundleWaitTicks = 20;
    }

    public static boolean isEnabled6() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_6047().method_31574(class_1802.field_49814);
    }

    private void submitHotbarSwap(InventoryController inventory) {
        inventory.submit(InventoryTask.builder().action(new ClickSlotAction(this.maceContainerSlot, this.selectedHotbarSlot)).owner(OWNER).flag(TaskFlag.DEFAULT).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).build());
    }

    public static void startSession() {
        MaceHelperModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        if (MaceHelperModule.findMaceContainerSlot() == -1) {
            if (MaceHelperModule.isEnabled()) {
                module.queueBundleExtract();
            }
            return;
        }
        boolean inHotbar = Inventories.findHotbarSlot(class_1802.field_49814) != -1;
        module.reset();
        module.restoreHotbarSlot = player.method_31548().method_67532();
        module.usingInventorySwap = !inHotbar;
        module.swapFromInventory = !inHotbar;
        module.phase = 2;
        module.sessionTicks = 100;
    }

    private static void cancelOwnedTasks(InventoryController inventory) {
        inventory.setString(OWNER);
    }

    private void reset() {
        this.phase = 0;
        this.restoreHotbarSlot = -1;
        this.usingInventorySwap = false;
        this.swapFromInventory = false;
        this.maceContainerSlot = -1;
        this.selectedHotbarSlot = -1;
        this.wasAirborne = false;
        this.pendingBundleExtract = false;
        this.bundleWaitTicks = 0;
        this.bundleUse = null;
    }
}

