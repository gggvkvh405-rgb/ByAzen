/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2487
 *  net.minecraft.class_2520
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_746
 *  net.minecraft.class_9276
 *  net.minecraft.class_9279
 *  net.minecraft.class_9334
 */
package ru.byazen.module.combat;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.KeyPressedEvent;
import ru.byazen.event.MousePressedEvent;
import ru.byazen.misc.Bundles;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.ClickSlotAction;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.ItemAlerts;
import ru.byazen.misc.SwapIcon;
import ru.byazen.misc.SwapWheelScreen;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.misc.VisibilityCondition;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.combat.AutoTotemModule;
import ru.byazen.notification.ItemNotification;
import ru.byazen.notification.NotificationCenter;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.InventoryController;

public class AutoSwapModule
extends Module
implements ConfigSerializable {
    private static final String OWNER = "auto_swap";
    private static final String BUKKIT_VALUES = "PublicBukkitValues";
    private static final String SESSION_KEY = "minecraft:s";
    private static final String DON_ITEM = "minecraft:don-item";
    private static final int OFFHAND_SLOT = 40;
    private static final int HOLD_TICKS_FOR_WHEEL = 4;
    private static final String SHORT_PRESS_WHEEL = "\u041a\u0440\u0443\u0433\u043e\u0432\u043e\u0439 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440";
    private final BooleanSetting enabledSetting;
    private final ModeSetting mode;
    private final ModeSetting serverMode;
    private final BooleanSetting separateBinds;
    private final BindSetting actionBind;
    private final BindSetting swapBind;
    private final BindSetting selectorBind;
    private final ModeSetting swapFrom;
    private final ModeSetting swapTo;
    private final ModeSetting shortPressMode;
    private final NumberSetting segments;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private String lastSwapFrom;
    private String lastSwapTo;
    private class_1799 lastWheelStack = class_1799.field_8037;
    private class_1799 previousWheelStack = class_1799.field_8037;
    private int holdTicks;
    private boolean actionHeld;
    private boolean wheelOpened;

    public AutoSwapModule(EventBus eventBus) {
        super(eventBus, OWNER, "Auto Swap", "\u041c\u0435\u043d\u044f\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043f\u0440\u0438 \u043f\u0440\u043e\u0436\u0430\u0442\u0438\u0438", ModuleCategory.valueOf("COMBAT"), "autoswap", "swap");
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit").defaultOption("Default").name("Mode").id("mode").description("Default - \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u044b\u0439, Legit - \u0441 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u043e\u0439")).build();
        this.registerSetting(this.mode);
        this.serverMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("FT", "Others").defaultOption("FT").name("Server Mode").id("server_mode").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0430")).build();
        this.registerSetting(this.serverMode);
        this.separateBinds = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u044b\u0435 \u0431\u0438\u043d\u0434\u044b").id("separate_binds").description("\u041e\u0442\u0434\u0435\u043b\u044c\u043d\u044b\u0435 \u043a\u043d\u043e\u043f\u043a\u0438 \u0434\u043b\u044f \u0441\u0432\u0430\u043f\u0430 \u0438 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440\u0430")).build();
        this.registerSetting(this.separateBinds);
        this.actionBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Action Button").id("action_bind").description("\u041d\u0430\u0436\u0430\u0442\u044c - \u0441\u0432\u0430\u043f, \u0443\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0442\u044c - \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440").visibleWhen(() -> !this.separateBinds.isEnabled())).build();
        this.registerSetting(this.actionBind);
        Supplier[] supplierArray = new Supplier[1];
        supplierArray[0] = this.separateBinds::isEnabled;
        VisibilityCondition separateBindVisibility = VisibilityCondition.process("separate_binds", supplierArray);
        this.swapBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onSwapBind).name("\u0421\u0432\u0430\u043f").id("swap_bind").description("\u0417\u0430\u043c\u0435\u043d\u0430 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u0432 \u043e\u0444\u0444\u0445\u0435\u043d\u0434\u0435").visibility(separateBindVisibility)).build();
        this.registerSetting(this.swapBind);
        this.selectorBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::onSelectorBind).name("\u0421\u0435\u043b\u0435\u043a\u0442\u043e\u0440").id("selector_bind").description("\u041a\u0440\u0443\u0433\u043e\u0432\u043e\u0439 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432").visibility(separateBindVisibility)).build();
        this.registerSetting(this.selectorBind);
        this.swapFrom = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Shield", "Sphere", "Totem", "GApple", "Firework").defaultOption("Shield").name("Swap From").id("swap_from").description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442, \u043a\u043e\u0442\u043e\u0440\u044b\u0439 \u0431\u0443\u0434\u0435\u0442 \u0437\u0430\u043c\u0435\u043d\u0451\u043d")).build();
        this.registerSetting(this.swapFrom);
        this.swapTo = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Shield", "Sphere", "Totem", "GApple", "Firework").defaultOption("Totem").name("Swap To").id("swap_to").description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442, \u043d\u0430 \u043a\u043e\u0442\u043e\u0440\u044b\u0439 \u0431\u0443\u0434\u0435\u0442 \u0437\u0430\u043c\u0435\u043d\u0430")).build();
        this.registerSetting(this.swapTo);
        this.shortPressMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("\u041d\u0430\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0435", SHORT_PRESS_WHEEL).defaultOption("\u041d\u0430\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0435").dynamicName(() -> this.separateBinds.isEnabled() ? "\u0427\u0442\u043e \u043c\u0435\u043d\u044f\u0442\u044c \u0441\u0432\u0430\u043f\u043e\u043c" : "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0430\u0442\u044c \u043a\u043e\u0440\u043e\u0442\u043a\u0438\u043c \u043d\u0430\u0436\u0430\u0442\u0438\u0435\u043c").id("short_press_mode").description("\u041d\u0430\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0435 - \u043f\u0430\u0440\u0430 \u0438\u0437 \u0441\u0435\u043b\u0435\u043a\u0442\u0431\u043e\u043a\u0441\u043e\u0432 \u0432\u044b\u0448\u0435\n\u041a\u0440\u0443\u0433\u043e\u0432\u043e\u0439 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440 - \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043a\u043e\u043b\u0435\u0441\u0430")).build();
        this.registerSetting(this.shortPressMode);
        this.segments = ((NumberSettingBuilder)NumberSetting.builder().range(2.0, 9.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(1.0).name("Segments").id("segments").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0441\u0435\u0433\u043c\u0435\u043d\u0442\u043e\u0432 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440\u0430")).build();
        this.registerSetting(this.segments);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(KeyPressedEvent.class, event -> this.onKey(event.key()));
        this.listen(MousePressedEvent.class, event -> this.onMouse(event.button()));
    }

    @Override
    public void readConfig(DataInputStream dataInputStream) throws IOException {
        super.readConfig(dataInputStream);
        if (this.swapBind.getBindInput().isUnbound() && this.selectorBind.getBindInput().isUnbound()) {
            this.swapBind.setBindInput(this.actionBind.getBindInput());
        }
    }

    private void onTick() {
        this.resetHistoryIfPairChanged();
        if (!this.enabledSetting.isEnabled() || this.separateBinds.isEnabled()) {
            this.resetHoldState();
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1755 != null && !(client.field_1755 instanceof SwapWheelScreen)) {
            this.resetHoldState();
            return;
        }
        boolean held = this.actionBind.isPressed();
        if (held) {
            if (!this.actionHeld) {
                this.holdTicks = 0;
                this.wheelOpened = false;
            }
            ++this.holdTicks;
            if (!this.wheelOpened && this.holdTicks >= 4 && client.field_1755 == null && client.field_1724 != null) {
                client.method_1507((class_437)this.openWheel(this.actionBind));
                this.wheelOpened = true;
            }
        } else {
            if (this.actionHeld && !this.wheelOpened && this.holdTicks < 4) {
                this.swapOffhand();
            }
            this.holdTicks = 0;
        }
        this.actionHeld = held;
    }

    private void onSwapBind(BindSetting ignored) {
        this.swapFromSeparateBind();
    }

    private void onSelectorBind(BindSetting ignored) {
        this.openSelector();
    }

    public void forgetStack(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return;
        }
        if (AutoSwapModule.sameItem(this.lastWheelStack, stack)) {
            this.lastWheelStack = class_1799.field_8037;
        }
        if (AutoSwapModule.sameItem(this.previousWheelStack, stack)) {
            this.previousWheelStack = class_1799.field_8037;
        }
    }

    public void selectStack(class_1799 stack) {
        if (!this.enabledSetting.isEnabled() || stack == null || stack.method_7960()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1799 offhand = player.method_6079();
        if (AutoSwapModule.sameItem(offhand, stack)) {
            return;
        }
        int slot = this.findInventorySlot(player.method_31548(), stack);
        if (slot == -1) {
            if (this.useFromBundle(player, stack)) {
                this.showHover(stack);
                this.rememberWheelStack(stack);
            } else {
                ItemAlerts.warnMissing(stack, stack.method_7964().getString());
            }
            return;
        }
        if (!this.swapToOffhand(slot)) {
            return;
        }
        this.showHover(stack);
        this.rememberWheelStack(stack);
    }

    public SwapIcon iconFor(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return SwapIcon.AVAILABLE;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return SwapIcon.AVAILABLE;
        }
        if (AutoSwapModule.sameItem(player.method_6079(), stack)) {
            return SwapIcon.AVAILABLE;
        }
        class_1661 inventory = player.method_31548();
        for (int slot = 0; slot < 36; ++slot) {
            if (!AutoSwapModule.sameItem(inventory.method_5438(slot), stack)) continue;
            return SwapIcon.AVAILABLE;
        }
        if (this.fromBundle.isEnabled() && Bundles.contains(inventory, candidate -> AutoSwapModule.sameItem(candidate, stack))) {
            return SwapIcon.IN_BUNDLE;
        }
        return SwapIcon.MISSING;
    }

    private class_1799 counterpart(class_746 player, class_1799 offhand) {
        String from = this.swapFrom.getSelectedOption();
        String to = this.swapTo.getSelectedOption();
        String wanted = this.predicate(from).test(offhand) ? to : from;
        return this.findByName(player.method_31548(), wanted);
    }

    private SwapWheelScreen openWheel(BindSetting bind) {
        return new SwapWheelScreen(bind, this.activeBinds(), this.segments.getIntValue(), this::selectStack, this::forgetStack, this::iconFor);
    }

    private static class_2487 stripSessionKeys(class_2487 nbt) {
        class_2487 copy = nbt.method_10553();
        class_2487 bukkit = copy.method_10562(BUKKIT_VALUES).orElse(null);
        if (bukkit != null) {
            bukkit.method_10551(SESSION_KEY);
            copy.method_10566(BUKKIT_VALUES, (class_2520)bukkit);
        }
        return copy;
    }

    private static boolean sameCustomData(class_2487 left, class_2487 right) {
        if (Objects.equals(left, right)) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return Objects.equals(AutoSwapModule.stripSessionKeys(left), AutoSwapModule.stripSessionKeys(right));
    }

    private static boolean sameItem(class_1799 left, class_1799 right) {
        if (left.method_7960() || right.method_7960()) {
            return false;
        }
        if (left.method_7909() != right.method_7909()) {
            return false;
        }
        if (!Objects.equals(left.method_58694(class_9334.field_49631), right.method_58694(class_9334.field_49631))) {
            return false;
        }
        if (!AutoSwapModule.sameCustomData(AutoSwapModule.customData(left), AutoSwapModule.customData(right))) {
            return false;
        }
        return Objects.equals(left.method_58694(class_9334.field_49632), right.method_58694(class_9334.field_49632));
    }

    private static class_2487 customData(class_1799 stack) {
        class_9279 component = (class_9279)stack.method_58694(class_9334.field_49628);
        return component == null ? null : component.method_57461();
    }

    private void swapFromSeparateBind() {
        if (!this.enabledSetting.isEnabled() || !this.separateBinds.isEnabled()) {
            return;
        }
        if (class_310.method_1551().field_1755 != null) {
            return;
        }
        this.swapOffhand();
    }

    private List<BindSetting> activeBinds() {
        return this.separateBinds.isEnabled() ? List.of(this.swapBind, this.selectorBind) : List.of(this.actionBind);
    }

    private boolean swapToOffhand(int slot) {
        if (AutoTotemModule.isActive()) {
            return false;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null) {
            return false;
        }
        ClickPolicy policy = "Legit".equalsIgnoreCase(this.mode.getSelectedOption()) ? ClickPolicy.SWAP : ClickPolicy.VISIBLE;
        inventory.submit(InventoryTask.builder().action(new ClickSlotAction(slot, 40)).owner(OWNER).flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build());
        return true;
    }

    private class_1799 nextWheelStack(class_1799 offhand) {
        class_1799 previous;
        List<class_1799> items = SwapWheelScreen.items();
        if (items.isEmpty()) {
            return class_1799.field_8037;
        }
        if (items.size() == 1) {
            return items.get(0).method_7972();
        }
        if (items.size() == 2) {
            class_1799 first = items.get(0);
            class_1799 second = items.get(1);
            return AutoSwapModule.sameItem(offhand, first) ? second.method_7972() : first.method_7972();
        }
        class_1799 last = this.listed(items, this.lastWheelStack) ? this.lastWheelStack : class_1799.field_8037;
        class_1799 class_17992 = previous = this.listed(items, this.previousWheelStack) ? this.previousWheelStack : class_1799.field_8037;
        if (last.method_7960() && previous.method_7960()) {
            return items.get(0).method_7972();
        }
        if (last.method_7960()) {
            return previous.method_7972();
        }
        if (previous.method_7960()) {
            return last.method_7972();
        }
        if (AutoSwapModule.sameItem(offhand, last)) {
            return previous.method_7972();
        }
        if (AutoSwapModule.sameItem(offhand, previous)) {
            return last.method_7972();
        }
        return last.method_7972();
    }

    private void onKey(int code) {
        if (!this.enabledSetting.isEnabled() || class_310.method_1551().field_1755 != null) {
            return;
        }
        for (BindSetting bind : this.activeBinds()) {
            if (!bind.getBindInput().matchesKeyboard(code)) continue;
            return;
        }
        class_1799 stack = SwapWheelScreen.stackAt(code);
        if (!stack.method_7960()) {
            this.selectStack(stack);
        }
    }

    private void swapOffhand() {
        class_1799 wanted;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1799 offhand = player.method_6079();
        boolean fromWheel = SHORT_PRESS_WHEEL.equals(this.shortPressMode.getSelectedOption());
        class_1799 class_17992 = wanted = fromWheel ? this.nextWheelStack(offhand) : this.counterpart(player, offhand);
        if (wanted.method_7960() || AutoSwapModule.sameItem(offhand, wanted)) {
            return;
        }
        int slot = this.findInventorySlot(player.method_31548(), wanted);
        if (slot == -1) {
            if (this.useFromBundle(player, wanted)) {
                this.showHover(wanted);
                if (fromWheel) {
                    this.rememberWheelStack(wanted);
                }
            }
            return;
        }
        if (!this.swapToOffhand(slot)) {
            return;
        }
        this.showHover(wanted);
        if (fromWheel) {
            this.rememberWheelStack(wanted);
        }
    }

    private int findInventorySlot(class_1661 inventory, class_1799 stack) {
        for (int slot = 0; slot < 36; ++slot) {
            if (!AutoSwapModule.sameItem(inventory.method_5438(slot), stack)) continue;
            return slot < 9 ? slot + 36 : slot;
        }
        return -1;
    }

    private void resetHistoryIfPairChanged() {
        String from = this.swapFrom.getSelectedOption();
        String to = this.swapTo.getSelectedOption();
        if (Objects.equals(from, this.lastSwapFrom) && Objects.equals(to, this.lastSwapTo)) {
            return;
        }
        this.lastSwapFrom = from;
        this.lastSwapTo = to;
        this.lastWheelStack = class_1799.field_8037;
        this.previousWheelStack = class_1799.field_8037;
    }

    private class_1799 findMatching(class_1661 inventory, Predicate<class_1799> predicate) {
        for (int slot = 0; slot < 36; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_7960() || !predicate.test(stack)) continue;
            return stack.method_7972();
        }
        if (!this.fromBundle.isEnabled()) {
            return class_1799.field_8037;
        }
        int[] found = Bundles.findInBundle(inventory, predicate);
        if (found == null) {
            return class_1799.field_8037;
        }
        class_9276 contents = (class_9276)inventory.method_5438(found[0]).method_58694(class_9334.field_49650);
        return contents == null ? class_1799.field_8037 : contents.method_57422(found[1]).method_7972();
    }

    private Predicate<class_1799> predicate(String name) {
        if (name == null) {
            return stack -> false;
        }
        return switch (name) {
            case "Shield" -> stack -> stack.method_31574(class_1802.field_8255);
            case "Sphere" -> this::isSphere;
            case "Totem" -> stack -> stack.method_31574(class_1802.field_8288);
            case "GApple" -> stack -> stack.method_31574(class_1802.field_8463);
            case "Firework" -> stack -> stack.method_31574(class_1802.field_8639);
            default -> stack -> false;
        };
    }

    private boolean hasDonItem(class_1799 stack) {
        class_2487 nbt = AutoSwapModule.customData(stack);
        if (nbt == null) {
            return false;
        }
        class_2487 bukkit = nbt.method_10553().method_10562(BUKKIT_VALUES).orElse(null);
        return bukkit != null && bukkit.method_10545(DON_ITEM);
    }

    private boolean useFromBundle(class_746 player, class_1799 stack) {
        if (!this.fromBundle.isEnabled() || AutoTotemModule.isActive()) {
            return false;
        }
        InventoryController inventory = ByAzenClient.getInventoryController();
        if (inventory == null || inventory.isActive()) {
            return false;
        }
        int[] found = Bundles.findInBundle(player.method_31548(), candidate -> AutoSwapModule.sameItem(candidate, stack));
        if (found == null) {
            return false;
        }
        return Bundles.useFromBundle(player, inventory, OWNER, found[0], found[1], 45, this.ftMode.isEnabled());
    }

    private void rememberWheelStack(class_1799 stack) {
        if (stack.method_7960()) {
            return;
        }
        class_1799 copy = stack.method_7972();
        if (AutoSwapModule.sameItem(copy, this.lastWheelStack)) {
            return;
        }
        if (AutoSwapModule.sameItem(copy, this.previousWheelStack)) {
            class_1799 swap = this.lastWheelStack;
            this.lastWheelStack = copy;
            this.previousWheelStack = swap;
            return;
        }
        this.previousWheelStack = this.lastWheelStack;
        this.lastWheelStack = copy;
    }

    private boolean listed(List<class_1799> items, class_1799 stack) {
        if (stack.method_7960()) {
            return false;
        }
        for (class_1799 candidate : items) {
            if (!AutoSwapModule.sameItem(candidate, stack)) continue;
            return true;
        }
        return false;
    }

    private void showHover(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return;
        }
        NotificationCenter overlays = ByAzenClient.getNotificationCenter();
        if (overlays != null) {
            overlays.push(new ItemNotification(stack));
        }
    }

    private void onMouse(int button) {
        if (!this.enabledSetting.isEnabled() || class_310.method_1551().field_1755 != null) {
            return;
        }
        for (BindSetting bind : this.activeBinds()) {
            if (!bind.getBindInput().matchesMouse(button)) continue;
            return;
        }
        class_1799 stack = SwapWheelScreen.stackAt(SwapWheelScreen.indexAt(button));
        if (!stack.method_7960()) {
            this.selectStack(stack);
        }
    }

    private void openSelector() {
        if (!this.enabledSetting.isEnabled() || !this.separateBinds.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1755 != null) {
            return;
        }
        client.method_1507((class_437)this.openWheel(this.selectorBind));
    }

    private class_1799 findByName(class_1661 inventory, String name) {
        class_1799 enchanted;
        if ("Totem".equals(name) && !(enchanted = this.findMatching(inventory, stack -> stack.method_31574(class_1802.field_8288) && stack.method_7958())).method_7960()) {
            return enchanted;
        }
        return this.findMatching(inventory, this.predicate(name));
    }

    private boolean isSphere(class_1799 stack) {
        if (!stack.method_31574(class_1802.field_8575)) {
            return false;
        }
        if (!"FT".equals(this.serverMode.getSelectedOption())) {
            return true;
        }
        return this.hasDonItem(stack);
    }

    private void resetHoldState() {
        this.actionHeld = false;
        this.holdTicks = 0;
        this.wheelOpened = false;
    }
}

