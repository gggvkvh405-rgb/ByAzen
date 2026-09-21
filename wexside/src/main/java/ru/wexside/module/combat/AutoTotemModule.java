/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1541
 *  net.minecraft.class_1548
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_1935
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_9276
 *  net.minecraft.class_9334
 */
package ru.wexside.module.combat;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1548;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.FriendList;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.VisibilityCondition;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;
import ru.wexside.util.entity.NpcDetector;

public class AutoTotemModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "auto_totem";
    private static final int OFFHAND_CONTAINER_SLOT = 45;
    private static final int TOTEM_POP_LOCK_TICKS = 20;
    private static final int SWAP_CONFIRM_TICKS = 10;
    private static volatile AutoTotemModule instance;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting consider;
    private final NumberSetting health;
    private final NumberSetting maceHealth;
    private final BooleanSetting ignoreAutoswap;
    private final BooleanSetting swapBack;
    private final NumberSetting swapBackDelay;
    private final BooleanSetting ignoreWhenUsing;
    private final MultiSelectSetting ignoreOnlyItems;
    private final MultiSelectSetting ignoreExceptThreats;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private int swapBackTicks;
    private int pendingOffhandTicks;
    private int totemPopTicks;
    private class_1799 expectedOffhand = class_1799.field_8037;
    private class_1799 previousOffhand = class_1799.field_8037;

    public AutoTotemModule(EventBus eventBus) {
        super(eventBus, OWNER, "Auto Totem", "\u0411\u0435\u0440\u0451\u0442 \u0442\u043e\u0442\u0435\u043c \u0432 \u043e\u0444\u0445\u0435\u043d\u0434 \u043f\u0440\u0438 \u043e\u043f\u0430\u0441\u043d\u043e\u0441\u0442\u0438", ModuleCategory.valueOf("COMBAT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting considerSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Absorption", "Falling", "Crystals", "Anchor", "Mace").selectAll(false).optionListEnabled(false).name("Consider").id("consider").description("\u0424\u0430\u043a\u0442\u043e\u0440\u044b \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438")).build();
        considerSetting.setOptions(new String[0]);
        this.consider = considerSetting;
        this.registerSetting(considerSetting);
        this.health = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 20.0).defaultValue(4.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(1.0).name("Health").id("health").description("\u041c\u0438\u043d\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 HP \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438")).build();
        this.registerSetting(this.health);
        this.maceHealth = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 20.0).defaultValue(10.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(1.0).name("Mace HP").id("mace_health").description("\u041f\u043e\u0440\u043e\u0433 HP, \u043d\u0438\u0436\u0435 \u043a\u043e\u0442\u043e\u0440\u043e\u0433\u043e \u0443\u0433\u0440\u043e\u0437\u0430 Mace \u0444\u0443\u043d\u043a\u0446\u0438\u043e\u043d\u0438\u0440\u0443\u0435\u0442").visibleWhen(() -> this.consider.getSelectedOptions().contains("Mace"))).build();
        this.registerSetting(this.maceHealth);
        this.ignoreAutoswap = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Ignore AutoSwap").id("ignore_autoswap").description("\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 AutoSwap \u043a\u043e\u0433\u0434\u0430 \u043d\u0443\u0436\u0435\u043d \u0442\u043e\u0442\u0435\u043c (HP-\u043f\u043e\u0440\u043e\u0433 \u0438\u043b\u0438 \u0443\u0433\u0440\u043e\u0437\u0430)")).build();
        this.registerSetting(this.ignoreAutoswap);
        this.swapBack = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Swap-Back").id("swap_back").description("\u0412\u043e\u0437\u0432\u0440\u0430\u0442 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0435\u0433\u043e \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u0438\u0437 \u043e\u0444\u0445\u0435\u043d\u0434\u0430 \u043a\u043e\u0433\u0434\u0430 \u0442\u043e\u0442\u0435\u043c \u043d\u0435 \u043d\u0443\u0436\u0435\u043d")).build();
        this.registerSetting(this.swapBack);
        this.swapBackDelay = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 6.0).defaultValue(3.0).multiplier(1.0).precision(1).animationSpeed(20.0f).snapTo(0.5).name("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u0430").id("swap_back_delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043f\u0435\u0440\u0435\u0434 \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u043e\u043c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u043f\u043e\u0441\u043b\u0435 \u0442\u043e\u0433\u043e \u043a\u0430\u043a \u0442\u043e\u0442\u0435\u043c \u0441\u0442\u0430\u043b \u043d\u0435 \u043d\u0443\u0436\u0435\u043d\n\u041f\u043e\u043c\u043e\u0433\u0430\u0435\u0442 \u043f\u0440\u0438 \u0430\u043a\u0442\u0438\u0432\u043d\u043e\u043c PvP \u043a\u043e\u0433\u0434\u0430 HP \u0434\u0451\u0440\u0433\u0430\u0435\u0442\u0441\u044f \u0432 \u0440\u0430\u0439\u043e\u043d\u0435 \u043f\u043e\u0440\u043e\u0433\u0430").visibleWhen(this.swapBack::isEnabled)).build();
        this.registerSetting(this.swapBackDelay);
        this.ignoreWhenUsing = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0418\u0433\u043d\u043e\u0440 \u043f\u0440\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438").id("ignore_when_using").description("\u041d\u0435 \u0441\u0432\u0430\u043f\u0430\u0442\u044c \u0435\u0441\u043b\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442\u0441\u044f \u043f\u0440\u0435\u0434\u043c\u0435\u0442")).build();
        this.registerSetting(this.ignoreWhenUsing);
        Supplier[] supplierArray = new Supplier[1];
        supplierArray[0] = this.ignoreWhenUsing::isEnabled;
        VisibilityCondition ignoreWhenUsingVisibility = VisibilityCondition.process("ignore_when_using", supplierArray);
        MultiSelectSetting ignoreOnlyItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("GApple", "\u0427\u0430\u0440\u043a\u0430", "\u0417\u0435\u043b\u044c\u0435 \u0418\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f").selectAll(false).optionListEnabled(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u0435\u0441\u043b\u0438:").id("ignore_only_items").description("\u0418\u0433\u043d\u043e\u0440 \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0438\u0441\u043f. \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0441\u0435\u0439\u0432\u044f\u0442 \u043f\u043e HP-\u043f\u043e\u0440\u043e\u0433\u0443. \u0415\u0441\u043b\u0438 0/3 - \u0430\u0431\u0441\u043e\u043b\u044e\u0442\u043d\u043e \u043b\u044e\u0431\u043e\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442").visibility(ignoreWhenUsingVisibility)).build();
        ignoreOnlyItemsSetting.setOptions(new String[0]);
        this.ignoreOnlyItems = ignoreOnlyItemsSetting;
        this.registerSetting(ignoreOnlyItemsSetting);
        MultiSelectSetting ignoreExceptThreatsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Falling", "Crystals", "Anchor", "Mace").selectAll(false).optionListEnabled(false).name("\u0417\u0430 \u0438\u0441\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435\u043c").id("ignore_except_threats").description("\u0421\u0432\u0430\u043f \u043d\u0430 \u0442\u043e\u0442\u0435\u043c \u0434\u0430\u0436\u0435 \u043f\u0440\u0438 \u0438\u0433\u043d\u043e\u0440\u0435, \u0435\u0441\u043b\u0438 \u0435\u0441\u0442\u044c \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u0430\u044f \u0443\u0433\u0440\u043e\u0437\u0430").visibility(ignoreWhenUsingVisibility)).build();
        ignoreExceptThreatsSetting.setOptions(new String[0]);
        this.ignoreExceptThreats = ignoreExceptThreatsSetting;
        this.registerSetting(ignoreExceptThreatsSetting);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u0442\u043e\u0442\u0435\u043c \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(TotemPopEvent.class, this::onTotemPop);
    }

    public static boolean isActive() {
        AutoTotemModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled() || !module.ignoreAutoswap.isEnabled()) {
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null && module.needsTotem(player);
    }

    private void onTick() {
        boolean holdingTotem;
        boolean totemPopLock;
        if (!this.enabledSetting.isEnabled()) {
            this.reset();
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            this.reset();
            return;
        }
        this.tickPendingOffhand(player);
        if (this.totemPopTicks > 0) {
            --this.totemPopTicks;
        }
        if (this.shouldIgnoreWhileUsing(player)) {
            return;
        }
        InventoryController inventory = WexSideClient.getInventoryController();
        if (inventory == null) {
            return;
        }
        boolean needTotem = this.needsTotem(player);
        if (needTotem) {
            this.swapBackTicks = this.swapBackDelayTicks();
        } else if (this.swapBackTicks > 0) {
            --this.swapBackTicks;
        }
        boolean bl = totemPopLock = this.totemPopTicks > 0;
        if (totemPopLock) {
            this.swapBackTicks = 0;
        }
        if (!totemPopLock && this.isSwapPending()) {
            return;
        }
        class_1661 playerInventory = player.method_31548();
        class_1799 offhand = player.method_6079();
        boolean bl2 = holdingTotem = offhand.method_7909() == class_1802.field_8288;
        if (needTotem) {
            this.equipTotem(player, inventory, playerInventory, offhand, holdingTotem);
            return;
        }
        this.trySwapBack(player, inventory, playerInventory);
    }

    private void equipTotem(class_746 player, InventoryController inventory, class_1661 playerInventory, class_1799 offhand, boolean holdingTotem) {
        boolean offhandGlint;
        if (player.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8288))) {
            return;
        }
        int totemSlot = this.findTotemSlot(playerInventory);
        if (totemSlot == -1) {
            int[] found;
            if (!holdingTotem && this.fromBundle.isEnabled() && !inventory.isActive() && (found = Bundles.findInBundle(playerInventory, stack -> stack.method_31574(class_1802.field_8288))) != null) {
                class_1799 previous = offhand.method_7972();
                class_1799 fromBundle = this.stackFromBundle(playerInventory, found);
                if (Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], 45, this.ftMode.isEnabled())) {
                    if (!previous.method_7960()) {
                        this.previousOffhand = previous;
                    }
                    this.rememberSwap(fromBundle);
                }
            }
            return;
        }
        boolean foundGlint = playerInventory.method_5438(totemSlot).method_7958();
        boolean bl = offhandGlint = holdingTotem && offhand.method_7958();
        if (!holdingTotem || offhandGlint && !foundGlint) {
            if (!offhand.method_7960()) {
                this.previousOffhand = offhand.method_7972();
            }
            class_1799 totem = playerInventory.method_5438(totemSlot).method_7972();
            this.swapToOffhand(inventory, Inventories.toContainerSlot(totemSlot));
            this.rememberSwap(totem);
        }
    }

    private void trySwapBack(class_746 player, InventoryController inventory, class_1661 playerInventory) {
        if (!this.swapBack.isEnabled() || this.previousOffhand.method_7960() || this.swapBackTicks > 0) {
            return;
        }
        int slot = this.findMatchingSlot(playerInventory, this.previousOffhand);
        if (slot != -1) {
            class_1799 restored = playerInventory.method_5438(slot).method_7972();
            this.swapToOffhand(inventory, Inventories.toContainerSlot(slot));
            this.rememberSwap(restored);
            this.previousOffhand = class_1799.field_8037;
            return;
        }
        if (!this.fromBundle.isEnabled()) {
            this.previousOffhand = class_1799.field_8037;
            return;
        }
        class_1799 previous = this.previousOffhand;
        int[] found = Bundles.findInBundle(playerInventory, stack -> class_1799.method_7973((class_1799)stack, (class_1799)previous));
        if (found == null) {
            this.previousOffhand = class_1799.field_8037;
            return;
        }
        if (inventory.isActive()) {
            return;
        }
        if (Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], 45, this.ftMode.isEnabled())) {
            this.rememberSwap(previous);
        }
        this.previousOffhand = class_1799.field_8037;
    }

    private void onTotemPop(TotemPopEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || event.getEntity() != player) {
            return;
        }
        this.totemPopTicks = 20;
        this.swapBackTicks = 0;
    }

    private void tickPendingOffhand(class_746 player) {
        if (this.pendingOffhandTicks <= 0) {
            return;
        }
        if (class_1799.method_7973((class_1799)player.method_6079(), (class_1799)this.expectedOffhand)) {
            this.pendingOffhandTicks = 0;
            this.expectedOffhand = class_1799.field_8037;
            return;
        }
        if (--this.pendingOffhandTicks <= 0) {
            this.expectedOffhand = class_1799.field_8037;
        }
    }

    private boolean shouldIgnoreWhileUsing(class_746 player) {
        if (!this.ignoreWhenUsing.isEnabled()) {
            return false;
        }
        if (!this.isUsingIgnoredItem(player)) {
            return false;
        }
        return !this.hasThreat(player, this.ignoreExceptThreats.getSelectedOptions());
    }

    private boolean isUsingIgnoredItem(class_746 player) {
        List<String> items = this.ignoreOnlyItems.getSelectedOptions();
        if (items.isEmpty()) {
            return class_310.method_1551().field_1690.field_1904.method_1434();
        }
        if (!player.method_6115()) {
            return false;
        }
        class_1799 active = player.method_6030();
        if (items.contains("GApple") && active.method_31574(class_1802.field_8463)) {
            return true;
        }
        if (items.contains("\u0427\u0430\u0440\u043a\u0430") && active.method_31574(class_1802.field_8367)) {
            return true;
        }
        return items.contains("\u0417\u0435\u043b\u044c\u0435 \u0418\u0441\u0446\u0435\u043b\u0435\u043d\u0438\u044f") && this.isHealingPotion(active);
    }

    private boolean needsTotem(class_746 player) {
        if (this.hasConsideredThreat(player)) {
            return true;
        }
        float hp = player.method_6032();
        if (this.consider.getSelectedOptions().contains("Absorption")) {
            hp += player.method_6059(class_1294.field_5898) ? player.method_6067() : 0.0f;
        }
        return hp <= this.health.getFloatValue();
    }

    private boolean hasConsideredThreat(class_746 player) {
        return this.hasThreat(player, this.consider.getSelectedOptions());
    }

    private boolean hasThreat(class_746 player, List<String> threats) {
        if (threats.contains("Crystals") && this.isNearExplosive(player)) {
            return true;
        }
        if (threats.contains("Anchor") && this.isNearCobweb(player)) {
            return true;
        }
        if (threats.contains("Falling") && this.isFallingFar(player)) {
            return true;
        }
        return threats.contains("Mace") && this.isMaceThreat(player);
    }

    private boolean isNearExplosive(class_746 player) {
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return false;
        }
        for (class_1297 entity : world.method_18112()) {
            if (!this.isExplosive(entity) || !((double)player.method_5739(entity) <= 6.0)) continue;
            return true;
        }
        return false;
    }

    private boolean isExplosive(class_1297 entity) {
        return entity instanceof class_1511 || entity instanceof class_1548 || entity instanceof class_1541;
    }

    private boolean isNearCobweb(class_746 player) {
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return false;
        }
        class_2338 origin = player.method_24515();
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (int x = -6; x <= 6; ++x) {
            for (int y = -6; y <= 6; ++y) {
                for (int z = -6; z <= 6; ++z) {
                    cursor.method_10103(origin.method_10263() + x, origin.method_10264() + y, origin.method_10260() + z);
                    if (!world.method_8320((class_2338)cursor).method_27852(class_2246.field_10343)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFallingFar(class_746 player) {
        if (player.method_5799()) {
            return false;
        }
        if (player.method_6101()) {
            return false;
        }
        return player.field_6017 > 10.0;
    }

    private boolean isMaceThreat(class_746 player) {
        if (player.method_6032() > this.maceHealth.getFloatValue()) {
            return false;
        }
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return false;
        }
        for (class_1657 other : world.method_18456()) {
            if (!this.isMaceAttacker(player, other)) continue;
            return true;
        }
        return false;
    }

    private boolean isMaceAttacker(class_746 player, class_1657 other) {
        double dz;
        if (other == player || !other.method_5805()) {
            return false;
        }
        if (!other.method_6047().method_31574(class_1802.field_49814)) {
            return false;
        }
        FriendList friends = WexSideClient.getFriends();
        if (friends != null && friends.contains(other.method_5477().getString())) {
            return false;
        }
        NpcDetector npcDetector = WexSideClient.getNpcDetector();
        if (npcDetector != null && npcDetector.isNpc((class_1309)other)) {
            return false;
        }
        if (other.method_24828()) {
            return false;
        }
        if (other.method_23318() - other.field_6036 > -0.15) {
            return false;
        }
        if (other.method_23318() - player.method_23318() < 2.0) {
            return false;
        }
        double dx = other.method_23317() - player.method_23317();
        return dx * dx + (dz = other.method_23321() - player.method_23321()) * dz <= 25.0;
    }

    private int findTotemSlot(class_1661 inventory) {
        int enchanted = -1;
        for (int slot = 0; slot < 36; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7909() != class_1802.field_8288) continue;
            if (!stack.method_7958()) {
                return slot;
            }
            enchanted = slot;
        }
        return enchanted;
    }

    private class_1799 stackFromBundle(class_1661 inventory, int[] found) {
        class_9276 contents = (class_9276)inventory.method_5438(found[0]).method_58694(class_9334.field_49650);
        return contents == null ? class_1799.field_8037 : contents.method_57422(found[1]).method_7972();
    }

    private int findMatchingSlot(class_1661 inventory, class_1799 target) {
        for (int slot = 0; slot < 36; ++slot) {
            if (!class_1799.method_7973((class_1799)inventory.method_5438(slot), (class_1799)target)) continue;
            return slot;
        }
        return -1;
    }

    private boolean isHealingPotion(class_1799 stack) {
        if (!stack.method_31574(class_1802.field_8574)) {
            return false;
        }
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents == null) {
            return false;
        }
        for (class_1293 effect : contents.method_57397()) {
            if (!effect.method_5579().equals((Object)class_1294.field_5915)) continue;
            return true;
        }
        return false;
    }

    private void swapToOffhand(InventoryController inventory, int containerSlot) {
        inventory.submit(InventoryTask.builder().action(new ClickSlotAction(containerSlot, 40)).owner(OWNER).flag(TaskFlag.DEFAULT).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).build());
    }

    private void rememberSwap(class_1799 stack) {
        this.expectedOffhand = stack == null ? class_1799.field_8037 : stack.method_7972();
        this.pendingOffhandTicks = 10;
        this.totemPopTicks = 0;
    }

    private int swapBackDelayTicks() {
        return Math.round(this.swapBackDelay.getFloatValue() * 20.0f);
    }

    private boolean isSwapPending() {
        return this.pendingOffhandTicks > 0;
    }

    private void reset() {
        this.swapBackTicks = 0;
        this.pendingOffhandTicks = 0;
        this.totemPopTicks = 0;
        this.expectedOffhand = class_1799.field_8037;
    }
}

