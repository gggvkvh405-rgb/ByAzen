/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1842
 *  net.minecraft.class_1844
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_9276
 *  net.minecraft.class_9334
 */
package ru.wexside.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.input.BindInput;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.HotbarSelectAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.ItemAlerts;
import ru.wexside.misc.ItemUseCooldownAccessor;
import ru.wexside.misc.KeybindRegistry;
import ru.wexside.misc.PotionCatalog;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.PotionPreset;
import ru.wexside.misc.PotionPresetDraft;
import ru.wexside.misc.PotionPresetKeybind;
import ru.wexside.misc.PotionPresetStore;
import ru.wexside.misc.ServerKind;
import ru.wexside.misc.SwapTiming;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.UseItemAction;
import ru.wexside.module.misc.PotionCombinerModule;
import ru.wexside.util.Angle;
import ru.wexside.util.InventoryController;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public final class PotionPresetController {
    private static final int MAX_FAVORITES = 5;
    private static final int ROTATION_HOLD_TICKS = 2;
    private static final float ROTATION_RANDOMIZATION_RANGE = 4.0f;
    private static final int USE_COOLDOWN_TICKS = 1;
    private static final float ROTATION_TOLERANCE = 3.0f;
    private static final String TASK_OWNER = "potion-combiner";
    private static final float DOWNWARD_PITCH = 90.0f;
    private static final int INVENTORY_SLOT_COUNT_WITH_OFFHAND = 37;
    private static final int MAX_ROTATION_ATTEMPTS = 8;
    private static final String ROTATION_OWNER = "potion-combiner-rotation";
    private final KeybindRegistry keybindRegistry;
    private final PotionPresetStore potionPresetStore;
    private int useCooldownTicks;
    private float targetPitch = 90.0f;
    private int rotationHoldTicks;
    private int rotationAttemptCount;
    private final Map<String, class_1799> availableStacksById;
    private int rotationRandomizationStep;
    private final List<PotionCatalogEntry> pendingPotions;
    private Angle downwardAngle;
    private final Map<PotionPresetDraft, PotionPresetKeybind> keybindsByPreset;
    private int revision;
    private final List<PotionPresetDraft> presets = new ArrayList<PotionPresetDraft>();

    public PotionPresetController(EventBus eventBus, KeybindRegistry keybindRegistry, PotionPresetStore potionPresetStore) {
        this.keybindsByPreset = new IdentityHashMap<PotionPresetDraft, PotionPresetKeybind>();
        this.pendingPotions = new ArrayList<PotionCatalogEntry>();
        this.availableStacksById = new HashMap<String, class_1799>();
        this.keybindRegistry = keybindRegistry;
        this.potionPresetStore = potionPresetStore;
        eventBus.subscribe(ClientTickEvent.class, event -> this.tick());
        this.loadPresets();
    }

    public void refreshInventoryIndex() {
        this.availableStacksById.clear();
        class_1661 inventory = this.getInventory();
        if (inventory == null) {
            return;
        }
        ServerKind serverKind = PotionCombinerModule.getServerKind();
        List<PotionCatalogEntry> catalog = PotionCatalog.inventoryEntries();
        boolean searchBundles = PotionCombinerModule.isActive();
        for (int slot = 0; slot < 37; ++slot) {
            class_1799 stack = slot == 36 ? inventory.method_5438(40) : inventory.method_5438(slot);
            this.indexStack(stack, catalog, serverKind);
            class_9276 bundle = (class_9276)stack.method_58694(class_9334.field_49650);
            if (!searchBundles || bundle == null) continue;
            for (int itemIndex = 0; itemIndex < bundle.method_57426(); ++itemIndex) {
                this.indexStack(bundle.method_57422(itemIndex), catalog, serverKind);
            }
        }
    }

    public List<PotionPresetDraft> getPresets() {
        return this.presets;
    }

    public PotionPresetDraft createPreset(String name) {
        PotionPresetDraft preset = new PotionPresetDraft(name);
        this.presets.add(preset);
        this.registerKeybind(preset);
        ++this.revision;
        this.savePresets();
        return preset;
    }

    public List<PotionCatalogEntry> getCatalogSortedByAvailability() {
        ArrayList<PotionCatalogEntry> available = new ArrayList<PotionCatalogEntry>();
        ArrayList<PotionCatalogEntry> unavailable = new ArrayList<PotionCatalogEntry>();
        for (PotionCatalogEntry entry : PotionCatalog.allEntries()) {
            if (this.availableStacksById.containsKey(entry.getId())) {
                available.add(entry);
                continue;
            }
            unavailable.add(entry);
        }
        available.addAll(unavailable);
        return available;
    }

    private void loadPresets() {
        this.presets.clear();
        for (PotionPresetKeybind keybind : this.keybindsByPreset.values()) {
            this.keybindRegistry.unregister(keybind);
        }
        this.keybindsByPreset.clear();
        for (PotionPreset storedPreset : this.potionPresetStore.getList()) {
            PotionPresetDraft preset = new PotionPresetDraft(storedPreset.name());
            preset.setBindInput(BindInput.fromLegacyCode(storedPreset.keyCode()));
            preset.setFavorite(storedPreset.favorite());
            List<String> potionIds = storedPreset.potionIds();
            for (int slot = 0; slot < 4 && slot < potionIds.size(); ++slot) {
                String potionId = potionIds.get(slot);
                preset.setPotionId(slot, potionId == null || potionId.isEmpty() ? null : potionId);
            }
            this.presets.add(preset);
            this.registerKeybind(preset);
        }
        ++this.revision;
    }

    private void tick() {
        this.tickRotationHold();
        this.processPendingPotions();
    }

    public class_1799 resolveStack(PotionCatalogEntry entry) {
        if (entry == null) {
            return class_1799.field_8037;
        }
        class_1799 stack = this.availableStacksById.get(entry.getId());
        return stack == null ? entry.createDefaultStack() : stack;
    }

    public void savePresets() {
        ArrayList<PotionPreset> storedPresets = new ArrayList<PotionPreset>();
        for (PotionPresetDraft preset : this.presets) {
            ArrayList<String> potionIds = new ArrayList<String>();
            for (int slot = 0; slot < 4; ++slot) {
                String potionId = preset.getPotionId(slot);
                potionIds.add(potionId == null ? "" : potionId);
            }
            storedPresets.add(new PotionPreset(preset.getName(), preset.getBindInput().toLegacyCode(), preset.isFavorite(), potionIds));
        }
        this.potionPresetStore.setList(storedPresets);
        try {
            this.potionPresetStore.save();
        }
        catch (IOException exception) {
            WexSideClient.getInstance().getLogger().error("Failed to save potion presets", (Throwable)exception);
        }
    }

    private void indexStack(class_1799 stack, List<PotionCatalogEntry> catalog, ServerKind serverKind) {
        if (stack == null || stack.method_7960()) {
            return;
        }
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents != null) {
            contents.comp_2378().map(entry -> class_7923.field_41179.method_10221(entry.comp_349())).ifPresent(id -> this.availableStacksById.putIfAbsent(id.toString(), stack));
        }
        for (PotionCatalogEntry entry2 : catalog) {
            if (!entry2.matches(stack, serverKind)) continue;
            this.availableStacksById.putIfAbsent(entry2.getId(), stack);
        }
    }

    public List<PotionPresetDraft> getFavoritePresets() {
        ArrayList<PotionPresetDraft> favorites = new ArrayList<PotionPresetDraft>();
        for (PotionPresetDraft preset : this.presets) {
            if (!preset.isFavorite()) continue;
            favorites.add(preset);
        }
        return favorites;
    }

    public void deletePreset(PotionPresetDraft preset) {
        if (this.presets.remove(preset)) {
            this.keybindRegistry.unregister(this.keybindsByPreset.remove(preset));
            ++this.revision;
            this.savePresets();
        }
    }

    private float calculateQuantizedPitch() {
        double sensitivity = (Double)class_310.method_1551().field_1690.method_42495().method_41753();
        double base = sensitivity * 0.6 + 0.2;
        double mouseStep = base * base * base * 8.0 * 0.15;
        if (mouseStep <= 0.0) {
            return 90.0f;
        }
        int stepCount = Math.max(1, (int)Math.floor(4.0 / mouseStep));
        this.rotationRandomizationStep = (this.rotationRandomizationStep + 1 + ThreadLocalRandom.current().nextInt(stepCount)) % (stepCount + 1);
        return 90.0f - (float)(mouseStep * (double)this.rotationRandomizationStep);
    }

    private void tickRotationHold() {
        if (this.rotationHoldTicks <= 0) {
            return;
        }
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null) {
            this.rotationHoldTicks = 0;
            this.downwardAngle = null;
            return;
        }
        --this.rotationHoldTicks;
        if (this.rotationHoldTicks > 0 && this.downwardAngle != null) {
            this.applyDownwardRotation(rotations, this.downwardAngle);
        } else {
            rotations.update3();
            this.downwardAngle = null;
        }
    }

    public boolean isAvailable(PotionCatalogEntry entry) {
        return entry != null && this.availableStacksById.containsKey(entry.getId());
    }

    private void usePotion(class_746 player, InventoryController inventory, PotionCatalogEntry entry, class_1799 stack, ServerKind serverKind, SwapTiming timing, boolean searchBundles, boolean preserveBundleSlot) {
        boolean throwable = this.isThrowablePotion(stack);
        if (entry.matches(player.method_6079(), serverKind)) {
            if (throwable) {
                this.beginDownwardRotation();
            }
            inventory.submit(InventoryTask.builder().action(new UseItemAction()).owner(TASK_OWNER).flag(TaskFlag.DEFAULT).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
            return;
        }
        class_1661 playerInventory = player.method_31548();
        int slot = PotionCatalog.findSlot(playerInventory, entry, serverKind);
        if (slot == -1) {
            if (!searchBundles) {
                return;
            }
            int[] bundleLocation = Bundles.findInBundle(playerInventory, item -> entry.matches((class_1799)item, serverKind));
            if (bundleLocation == null) {
                return;
            }
            Bundles.useFromBundle(player, inventory, TASK_OWNER, bundleLocation[0], bundleLocation[1], preserveBundleSlot, () -> {
                if (throwable) {
                    this.beginDownwardRotation();
                }
                inventory.update3();
            });
            return;
        }
        if (slot < 9) {
            if (throwable) {
                this.beginDownwardRotation();
            }
            inventory.submit(InventoryTask.builder().action(new HotbarSelectAction(slot, true)).owner(TASK_OWNER).flag(TaskFlag.DEFAULT).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
            return;
        }
        Runnable afterSwap = () -> {
            if (throwable) {
                this.beginDownwardRotation();
            }
            inventory.update3();
        };
        inventory.submit(InventoryTask.builder().action(inventory.process2(slot, playerInventory.method_67532(), afterSwap, timing)).owner(TASK_OWNER).flag(TaskFlag.DEFAULT).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).blocking(true).build());
    }

    public void queuePreset(PotionPresetDraft preset) {
        if (preset == null || !PotionCombinerModule.isActive2()) {
            return;
        }
        for (PotionCatalogEntry entry : preset.getPotions()) {
            if (this.pendingPotions.contains(entry)) continue;
            this.pendingPotions.add(entry);
        }
    }

    private void registerKeybind(PotionPresetDraft preset) {
        PotionPresetKeybind keybind = new PotionPresetKeybind(this, preset);
        this.keybindsByPreset.put(preset, keybind);
        this.keybindRegistry.register(keybind);
    }

    private boolean isAimingDown() {
        RotationController rotations = WexSideClient.getRotationController();
        return rotations != null && rotations.process3(this.targetPitch, 3.0f);
    }

    private boolean isThrowablePotion(class_1799 stack) {
        return stack.method_31574(class_1802.field_8436) || stack.method_31574(class_1802.field_8150);
    }

    private class_1661 getInventory() {
        class_746 player = class_310.method_1551().field_1724;
        return player == null ? null : player.method_31548();
    }

    private void applyDownwardRotation(RotationController rotations, Angle angle) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        rotations.process2(new RotationIntent((class_1309)player, null, angle, AttackUrgency.HIT, CorrectionMode.NONE, false), ROTATION_OWNER);
    }

    public int getRevision() {
        return this.revision;
    }

    public boolean toggleFavorite(PotionPresetDraft preset) {
        if (preset.isFavorite()) {
            preset.setFavorite(false);
            this.savePresets();
            return true;
        }
        if (this.getFavoritePresets().size() >= 5) {
            return false;
        }
        preset.setFavorite(true);
        this.savePresets();
        return true;
    }

    private void beginDownwardRotation() {
        class_746 player = class_310.method_1551().field_1724;
        RotationController rotations = WexSideClient.getRotationController();
        if (player == null || rotations == null) {
            return;
        }
        this.downwardAngle = new Angle(player.method_36454(), this.targetPitch);
        this.rotationHoldTicks = 3;
        this.applyDownwardRotation(rotations, this.downwardAngle);
    }

    private void processPendingPotions() {
        class_1799 stack2;
        boolean foundInBundle;
        if (this.pendingPotions.isEmpty()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        InventoryController inventory = WexSideClient.getInventoryController();
        if (player == null || inventory == null) {
            this.pendingPotions.clear();
            this.rotationAttemptCount = 0;
            return;
        }
        ((ItemUseCooldownAccessor)class_310.method_1551()).setItemUseCooldown(0);
        if (this.useCooldownTicks > 0) {
            --this.useCooldownTicks;
            return;
        }
        if (inventory.process(TASK_OWNER)) {
            return;
        }
        ServerKind serverKind = PotionCombinerModule.getServerKind();
        boolean searchBundles = PotionCombinerModule.isActive();
        PotionCatalogEntry entry = this.pendingPotions.get(0);
        class_1799 inventoryStack = PotionCatalog.findStack(player.method_31548(), entry, serverKind);
        boolean bl = foundInBundle = searchBundles && Bundles.contains(player.method_31548(), stack -> entry.matches((class_1799)stack, serverKind));
        if (inventoryStack.method_7960() && !foundInBundle) {
            ItemAlerts.warnMissing(entry.createDefaultStack(), entry.getDisplayName());
            this.pendingPotions.remove(0);
            this.rotationAttemptCount = 0;
            return;
        }
        class_1799 class_17992 = stack2 = inventoryStack.method_7960() ? entry.createDefaultStack() : inventoryStack;
        if (ItemAlerts.isBusy(player.field_7512, stack2, entry.getDisplayName())) {
            this.pendingPotions.remove(0);
            this.rotationAttemptCount = 0;
            return;
        }
        if (this.isThrowablePotion(stack2)) {
            if (this.rotationAttemptCount == 0) {
                this.targetPitch = this.calculateQuantizedPitch();
            }
            if (!this.isAimingDown()) {
                this.beginDownwardRotation();
                if (this.rotationAttemptCount++ < 8) {
                    return;
                }
            }
        }
        this.usePotion(player, inventory, entry, stack2, serverKind, PotionCombinerModule.getSwapTiming(), searchBundles, PotionCombinerModule.isActive3());
        this.pendingPotions.remove(0);
        this.rotationAttemptCount = 0;
        this.useCooldownTicks = 1;
    }
}

