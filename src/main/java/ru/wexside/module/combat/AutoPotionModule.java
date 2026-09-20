/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1812
 *  net.minecraft.class_1844
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package ru.wexside.module.combat;

import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1812;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.PotionEntry;
import ru.wexside.misc.SwapTiming;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.HotbarSlotLock;
import ru.wexside.util.InventoryController;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class AutoPotionModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "auto_potion";
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting potions;
    private final List<PotionEntry> potionPresets = List.of(new PotionEntry("Fire Resistance", (class_6880<class_1291>)class_1294.field_5918), new PotionEntry("Strength", (class_6880<class_1291>)class_1294.field_5910), new PotionEntry("Speed", (class_6880<class_1291>)class_1294.field_5904));
    private int lookTicks;
    private long lastUseTime;
    private Angle throwAngle;

    public AutoPotionModule(EventBus eventBus) {
        super(eventBus, OWNER, "Auto Potion", "\u0411\u0440\u043e\u0441\u0430\u0435\u0442 \u0437\u0435\u043b\u044c\u044f \u043f\u043e\u0434 \u0441\u0435\u0431\u044f \u043f\u0440\u0438 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0438\u0438 \u044d\u0444\u0444\u0435\u043a\u0442\u0430", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting potionsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Fire Resistance", "Strength", "Speed").selectAll(false).optionListEnabled(false).name("Potions").id("potions").description("\u0421\u043f\u0438\u0441\u043e\u043a \u0437\u0435\u043b\u0438\u0439 \u0434\u043b\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f")).build();
        potionsSetting.setOptions(new String[0]);
        this.potions = potionsSetting;
        this.registerSetting(potionsSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        if (this.lookTicks > 0) {
            this.tickLook(player);
            return;
        }
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        if (System.currentTimeMillis() - this.lastUseTime < 250L) {
            return;
        }
        if (!player.method_24828()) {
            return;
        }
        int slot = this.findPotionSlot(player);
        if (slot == -1) {
            return;
        }
        this.throwAngle = new Angle(player.method_36454(), 90.0f);
        this.lookTicks = 5;
        this.applyLook(player, this.throwAngle);
        if (slot < 9) {
            this.throwFromHotbar(player, slot);
        } else {
            this.throwFromInventory(player, slot);
        }
        this.lastUseTime = System.currentTimeMillis();
    }

    private void tickLook(class_746 player) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null) {
            this.lookTicks = 0;
            this.throwAngle = null;
            return;
        }
        --this.lookTicks;
        if (this.lookTicks > 0 && this.throwAngle != null) {
            this.applyLook(player, this.throwAngle);
        } else {
            this.releaseLook();
        }
    }

    private void applyLook(class_746 player, Angle angle) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null) {
            return;
        }
        rotations.process2(new RotationIntent((class_1309)player, null, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false), "FT Snap");
    }

    private void releaseLook() {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations != null) {
            rotations.update3();
        }
        this.throwAngle = null;
    }

    private void throwFromHotbar(class_746 player, int slot) {
        class_1661 inventory = player.method_31548();
        int selected = inventory.method_67532();
        if (selected != slot) {
            inventory.method_61496(slot);
        }
        this.useMainHand();
        HotbarSlotLock hotbar = WexSideClient.getHotbarSlotLock();
        if (hotbar != null && selected != slot) {
            hotbar.process(selected, 400L);
        }
    }

    private void throwFromInventory(class_746 player, int slot) {
        InventoryController inventory = WexSideClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        int selected = player.method_31548().method_67532();
        int containerSlot = slot < 9 ? slot + 36 : slot;
        inventory.submit(this.task(inventory.process2(containerSlot, selected, this::useMainHand, SwapTiming.DEFAULT), ClickPolicy.VISIBLE));
    }

    private void useMainHand() {
        class_746 player = class_310.method_1551().field_1724;
        class_636 interactionManager = class_310.method_1551().field_1761;
        if (player == null || interactionManager == null) {
            return;
        }
        if (player.method_6047().method_7960()) {
            return;
        }
        interactionManager.method_2919((class_1657)player, class_1268.field_5808);
        player.method_6104(class_1268.field_5808);
    }

    private int findPotionSlot(class_746 player) {
        class_1661 inventory = player.method_31548();
        List<String> selected = this.potions.getSelectedOptions();
        for (PotionEntry preset : this.potionPresets) {
            int slot;
            if (!selected.contains(preset.getName()) || player.method_6059(preset.getEffect()) || (slot = this.findMatchingPotion(inventory, preset.getEffect())) == -1) continue;
            return slot;
        }
        return -1;
    }

    private int findMatchingPotion(class_1661 inventory, class_6880<class_1291> effect) {
        for (int slot = 0; slot < 36; ++slot) {
            class_1844 contents;
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7960() || !(stack.method_7909() instanceof class_1812) || (contents = (class_1844)stack.method_58694(class_9334.field_49651)) == null) continue;
            for (class_1293 instance : contents.method_57397()) {
                if (!instance.method_5579().equals(effect)) continue;
                return slot;
            }
        }
        return -1;
    }

    private InventoryTask task(InventoryAction action, ClickPolicy policy) {
        return InventoryTask.builder().action(action).owner(OWNER).flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build();
    }
}

