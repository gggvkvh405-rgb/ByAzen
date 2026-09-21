/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.AttackUrgency;
import ru.byazen.misc.Bundles;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.CorrectionMode;
import ru.byazen.misc.HotbarSelectAction;
import ru.byazen.misc.Inventories;
import ru.byazen.misc.InventoryAction;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.ItemAlerts;
import ru.byazen.misc.SwapTiming;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.misc.UseItemAction;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.util.Angle;
import ru.byazen.util.InventoryController;
import ru.byazen.util.RotationController;
import ru.byazen.util.RotationIntent;

public class ClickPearlModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "click_pearl";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final ModeSetting mode;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private final BindSetting key;
    private Angle heldAngle;
    private int holdTicks;
    private boolean pendingThrow;

    public ClickPearlModule(EventBus eventBus) {
        super(eventBus, OWNER, "Click Pearl", "\u0411\u0440\u043e\u0441\u043e\u043a \u044d\u043d\u0434\u0435\u0440-\u043f\u0435\u0440\u043b\u0430 \u0431\u0438\u043d\u0434\u043e\u043c", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit", "FS").defaultOption("Default").name("Mode").id("mode").description("\u0420\u0435\u0436\u0438\u043c \u0431\u0440\u043e\u0441\u0430\u043d\u0438\u044f")).build();
        this.registerSetting(this.mode);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u043f\u0435\u0440\u043b \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
        this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onKeyPress).name("Key").id("key").description("\u041a\u043d\u043e\u043f\u043a\u0430 \u0431\u0440\u043e\u0441\u043a\u0430")).build();
        this.registerSetting(this.key);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled()) {
            if (this.holdTicks > 0 || this.pendingThrow) {
                this.reset();
            }
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.reset();
            return;
        }
        if (this.holdTicks > 0) {
            --this.holdTicks;
            if (this.heldAngle != null) {
                this.applyLook(player, this.heldAngle);
            }
            if (this.holdTicks <= 0) {
                this.releaseLook();
            }
        }
        if (this.pendingThrow && this.lookReady()) {
            this.throwPearl(player);
            this.pendingThrow = false;
        }
    }

    private void onKeyPress(BindSetting ignored) {
        this.queueThrow();
    }

    private void queueThrow() {
        if (!this.enabledSetting.isEnabled() || this.pendingThrow) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1799 pearl = new class_1799((class_1935)class_1802.field_8634);
        boolean offhand = player.method_6079().method_31574(class_1802.field_8634);
        int slot = offhand ? -1 : Inventories.findSlot(class_1802.field_8634);
        boolean inBundle = this.fromBundle.isEnabled() && Bundles.contains(player.method_31548(), stack -> stack.method_31574(class_1802.field_8634));
        boolean bl = inBundle;
        if (!offhand && slot == -1 && !inBundle) {
            ItemAlerts.warnMissing(pearl, pearl.method_7964().getString());
            return;
        }
        if (ItemAlerts.isBusy(null, pearl, pearl.method_7964().getString())) {
            return;
        }
        RotationController rotations = ByAzenClient.getRotationController();
        Angle look = rotations != null ? rotations.getAngle() : null;
        Angle angle = look;
        if (look != null) {
            this.heldAngle = look;
            this.holdTicks = this.legitMode() ? 14 : 6;
            this.applyLook(player, look);
        }
        this.pendingThrow = true;
    }

    private void throwPearl(class_746 player) {
        if (player.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8634))) {
            return;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        if (player.method_6079().method_31574(class_1802.field_8634)) {
            inventory.submit(this.task(new UseItemAction(), ClickPolicy.SILENT));
            return;
        }
        int slot = Inventories.findSlot(class_1802.field_8634);
        if (slot == -1) {
            int[] found;
            if (this.fromBundle.isEnabled() && (found = Bundles.findInBundle(player.method_31548(), stack -> stack.method_31574(class_1802.field_8634))) != null) {
                Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], this.ftMode.isEnabled(), inventory::update3);
            }
            return;
        }
        if (slot < 9) {
            inventory.submit(this.task(new HotbarSelectAction(slot, true), ClickPolicy.SILENT));
            return;
        }
        this.swapFromInventory(player, slot, inventory);
    }

    private void swapFromInventory(class_746 player, int slot, InventoryController inventory) {
        int selected = player.method_31548().method_67532();
        int containerSlot = slot < 9 ? slot + 36 : slot;
        inventory.submit(this.task(inventory.process2(containerSlot, selected, inventory::update3, this.swapTiming()), ClickPolicy.VISIBLE));
    }

    private InventoryTask task(InventoryAction action, ClickPolicy policy) {
        return InventoryTask.builder().action(action).owner(OWNER).flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build();
    }

    private void applyLook(class_746 player, Angle angle) {
        RotationController rotations = ByAzenClient.getRotationController();
        if (rotations == null) {
            return;
        }
        rotations.process2(new RotationIntent((class_1309)player, null, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false), "FT Snap");
    }

    private void releaseLook() {
        RotationController rotations = ByAzenClient.getRotationController();
        if (rotations != null) {
            rotations.update3();
        }
        this.heldAngle = null;
    }

    private boolean lookReady() {
        if (this.heldAngle == null) {
            return true;
        }
        RotationController rotations = ByAzenClient.getRotationController();
        if (rotations == null) {
            return true;
        }
        Angle current = rotations.getAngle();
        return current == null || current.process(this.heldAngle) <= 10.0f;
    }

    private boolean legitMode() {
        return "Legit".equals(this.mode.getSelectedOption());
    }

    private SwapTiming swapTiming() {
        String value = this.mode.getSelectedOption();
        if ("Legit".equals(value)) {
            return SwapTiming.LEGIT;
        }
        if ("FS".equals(value)) {
            return SwapTiming.FUNTIME;
        }
        return SwapTiming.DEFAULT;
    }

    private void reset() {
        if (this.heldAngle != null) {
            this.releaseLook();
        }
        this.heldAngle = null;
        this.holdTicks = 0;
        this.pendingThrow = false;
    }
}

