/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10192
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package ru.byazen.module.combat;

import java.util.function.Predicate;
import net.minecraft.class_10192;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9334;
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
import ru.byazen.misc.SwapSlotsAction;
import ru.byazen.misc.SwapTiming;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.misc.UseItemAction;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.combat.AttackAuraModule;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.Angle;
import ru.byazen.util.InventoryController;
import ru.byazen.util.RotationController;
import ru.byazen.util.RotationIntent;

public class ElytraHelperModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "elytra_helper";
    private static final int CHEST_SLOT = 6;
    private static volatile ElytraHelperModule instance;
    private final BooleanSetting enabledSetting;
    private final ModeSetting mode;
    private final BindSetting swapKey;
    private final BindSetting fireworkKey;
    private final BooleanSetting autoFly;
    private final BooleanSetting autoFirework;
    private final BooleanSetting followTarget;
    private final NumberSetting fireworkDelay;
    private final BooleanSetting armorOnLand;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private AttackAuraModule attackAura;
    private boolean fireworkUsedAfterTakeoff;
    private boolean requestStartGlide;
    private boolean wasOnGround;
    private int glideCooldown;
    private long lastFireworkTime;

    public ElytraHelperModule(EventBus eventBus) {
        super(eventBus, OWNER, "Elytra Helper", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u044d\u043b\u0438\u0442\u0440\u043e\u0439 \u0438 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430\u043c\u0438", ModuleCategory.valueOf("COMBAT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit", "FS").defaultOption("Default").name("Mode").id("mode").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0432\u0430\u043f\u0430 \u044d\u043b\u0438\u0442\u0440\u044b")).build();
        this.registerSetting(this.mode);
        this.swapKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onSwapBind).name("Swap Key").id("swap_key").description("\u041a\u043d\u043e\u043f\u043a\u0430 \u043d\u0430\u0434\u0435\u0432\u0430\u043d\u0438\u044f \u044d\u043b\u0438\u0442\u0440\u044b")).build();
        this.registerSetting(this.swapKey);
        this.fireworkKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onFireworkBind).name("Firework Key").id("firework_key").description("\u041a\u043d\u043e\u043f\u043a\u0430 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430")).build();
        this.registerSetting(this.fireworkKey);
        this.autoFly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Auto Fly").id("auto_fly").description("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0435 \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043f\u043e\u043b\u0451\u0442\u0430")).build();
        this.registerSetting(this.autoFly);
        this.autoFirework = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Auto Firework").id("auto_firework").description("\u0417\u0430\u043f\u0443\u0441\u043a \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430 \u043f\u043e\u0441\u043b\u0435 \u0432\u0437\u043b\u0451\u0442\u0430").visibleWhen(this.autoFly::isEnabled)).build();
        this.registerSetting(this.autoFirework);
        this.followTarget = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Follow Target").id("follow_target").description("\u041f\u043e\u043b\u0451\u0442 \u0437\u0430 \u0446\u0435\u043b\u044c\u044e")).build();
        this.registerSetting(this.followTarget);
        this.fireworkDelay = ((NumberSettingBuilder)NumberSetting.builder().range(100.0, 5000.0).defaultValue(500.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Firework Delay").id("firework_delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430\u043c\u0438").visibleWhen(this.followTarget::isEnabled)).build();
        this.registerSetting(this.fireworkDelay);
        this.armorOnLand = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Armor On Land").id("armor_on_land").description("\u041d\u0430\u0434\u0435\u0432\u0430\u0435\u0442 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u043f\u0440\u0438 \u043f\u0440\u0438\u0437\u0435\u043c\u043b\u0435\u043d\u0438\u0438")).build();
        this.registerSetting(this.armorOnLand);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u044d\u043b\u0438\u0442\u0440\u0443 \u0438 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0438 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        boolean wearingElytra = player.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
        boolean onGround = player.method_24828();
        if (wearingElytra && this.armorOnLand.isEnabled() && onGround && !this.wasOnGround) {
            this.swapChest();
        }
        if (this.autoFly.isEnabled() && wearingElytra) {
            this.tickAutoFly(player, onGround);
        }
        if (this.followTarget.isEnabled() && wearingElytra) {
            this.tickFollowTarget(player);
        }
        this.wasOnGround = onGround;
    }

    private void onSwapBind(BindSetting ignored) {
        this.swapChest();
    }

    private void onFireworkBind(BindSetting ignored) {
        this.useFirework();
    }

    private void useFirework() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        if (player.method_6079().method_31574(class_1802.field_8639)) {
            inventory.submit(this.task(new UseItemAction(), ClickPolicy.SILENT));
            this.lastFireworkTime = System.currentTimeMillis();
            return;
        }
        int slot = Inventories.findSlot(class_1802.field_8639);
        if (slot == -1) {
            int[] found;
            if (this.fromBundle.isEnabled() && (found = Bundles.findInBundle(player.method_31548(), stack -> stack.method_31574(class_1802.field_8639))) != null) {
                Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], this.ftMode.isEnabled(), inventory::update3);
                this.lastFireworkTime = System.currentTimeMillis();
                return;
            }
            class_1799 firework = new class_1799((class_1935)class_1802.field_8639);
            ItemAlerts.warnMissing(firework, firework.method_7964().getString());
            return;
        }
        if (slot < 9) {
            inventory.submit(this.task(new HotbarSelectAction(slot, true), ClickPolicy.SILENT));
        } else {
            this.swapFireworkFromInventory(player, slot, inventory);
        }
        this.lastFireworkTime = System.currentTimeMillis();
    }

    private void swapFireworkFromInventory(class_746 player, int slot, InventoryController inventory) {
        int selected = player.method_31548().method_67532();
        int containerSlot = slot < 9 ? slot + 36 : slot;
        inventory.submit(this.task(inventory.process2(containerSlot, selected, inventory::update3, this.swapTiming()), ClickPolicy.VISIBLE));
    }

    private void swapChest() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        class_1799 chest = player.method_6118(class_1304.field_6174);
        boolean wearingElytra = chest.method_31574(class_1802.field_8833);
        int slot = wearingElytra ? this.findChestplateSlot(player) : this.findElytraSlot(player);
        int n = slot;
        if (slot == -1 && this.fromBundle.isEnabled() && this.useChestFromBundle(player, inventory, chest)) {
            return;
        }
        if (slot == -1 && wearingElytra) {
            slot = this.findEmptySlot(player);
        }
        if (slot == -1) {
            if (wearingElytra) {
                ItemAlerts.warnMissing(new class_1799((class_1935)class_1802.field_8058), "\u041d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a");
            } else {
                class_1799 elytra = new class_1799((class_1935)class_1802.field_8833);
                ItemAlerts.warnMissing(elytra, elytra.method_7964().getString());
            }
            return;
        }
        inventory.submit(this.task(new SwapSlotsAction(6, slot), this.legitMode() ? ClickPolicy.SWAP : ClickPolicy.VISIBLE));
    }

    private boolean useChestFromBundle(class_746 player, InventoryController inventory, class_1799 chest) {
        if (inventory.isActive()) {
            return false;
        }
        Predicate<class_1799> predicate = chest.method_31574(class_1802.field_8833) ? ElytraHelperModule::isChestArmor : stack -> stack.method_31574(class_1802.field_8833) && stack.method_7919() < stack.method_7936() - 1;
        int[] found = Bundles.findInBundle(player.method_31548(), predicate);
        if (found == null) {
            return false;
        }
        return Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], 6, this.ftMode.isEnabled());
    }

    private void tickFollowTarget(class_746 player) {
        class_1309 target = this.getAttackAuraTarget();
        if (target == null) {
            return;
        }
        RotationController rotations = ByAzenClient.getRotationController();
        if (rotations == null) {
            return;
        }
        class_243 from = player.method_33571();
        class_243 to = target.method_33571();
        Angle angle = Angle.fromVectors(from, to);
        rotations.process2(new RotationIntent(target, to, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false), "Simple");
        long now = System.currentTimeMillis();
        long delay = (long)this.fireworkDelay.getFloatValue();
        boolean fire = target.field_6235 == 0 && now - this.lastFireworkTime >= delay || player.method_5739((class_1297)target) < 10.0f && now - this.lastFireworkTime >= 500L;
        boolean bl = fire;
        if (fire) {
            this.useFirework();
        }
    }

    private void tickAutoFly(class_746 player, boolean onGround) {
        if (this.glideCooldown > 0) {
            --this.glideCooldown;
        }
        if (onGround) {
            this.fireworkUsedAfterTakeoff = false;
            this.requestStartGlide = true;
            return;
        }
        if (player.method_6128()) {
            if (this.autoFirework.isEnabled() && !this.fireworkUsedAfterTakeoff) {
                this.fireworkUsedAfterTakeoff = true;
                this.useFirework();
            }
            return;
        }
        if (this.glideCooldown > 0) {
            return;
        }
        if (player.method_18798().field_1351 >= 0.0) {
            return;
        }
        if (player.method_5799() || player.method_5869() || player.method_31549().field_7479) {
            return;
        }
        this.requestStartGlide = true;
        this.glideCooldown = 5;
    }

    private class_1309 getAttackAuraTarget() {
        if (this.attackAura == null) {
            for (Module module : ByAzenClient.getInstance().getModuleManager().getModules()) {
                AttackAuraModule aura;
                if (!(module instanceof AttackAuraModule)) continue;
                this.attackAura = aura = (AttackAuraModule)module;
                break;
            }
        }
        return this.attackAura == null ? null : this.attackAura.getLivingEntity();
    }

    private static boolean isChestArmor(class_1799 stack) {
        if (stack.method_7960() || stack.method_31574(class_1802.field_8833)) {
            return false;
        }
        class_10192 equippable = (class_10192)stack.method_58694(class_9334.field_54196);
        return equippable != null && equippable.comp_3174() == class_1304.field_6174;
    }

    private int findElytraSlot(class_746 player) {
        class_1661 inventory = player.method_31548();
        for (int slot = 0; slot < 36; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (!stack.method_31574(class_1802.field_8833) || stack.method_7919() >= stack.method_7936() - 1) continue;
            return slot;
        }
        return -1;
    }

    private int findChestplateSlot(class_746 player) {
        class_1661 inventory = player.method_31548();
        for (int slot = 0; slot < 36; ++slot) {
            if (!ElytraHelperModule.isChestArmor(inventory.method_5438(slot))) continue;
            return slot;
        }
        return -1;
    }

    private int findEmptySlot(class_746 player) {
        class_1661 inventory = player.method_31548();
        for (int slot = 0; slot < 36; ++slot) {
            if (!inventory.method_5438(slot).method_7960()) continue;
            return slot;
        }
        return -1;
    }

    private InventoryTask task(InventoryAction action, ClickPolicy policy) {
        return InventoryTask.builder().action(action).owner(OWNER).flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build();
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

    public static boolean isEnabled2() {
        ElytraHelperModule module = instance;
        if (module == null || !module.requestStartGlide) {
            return false;
        }
        module.requestStartGlide = false;
        return true;
    }
}

