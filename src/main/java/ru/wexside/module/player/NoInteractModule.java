/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_3481
 */
package ru.wexside.module.player;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3481;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.BlockInteractEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class NoInteractModule
extends Module
implements ConfigSerializable {
    private static final List<BlockFilter> FILTERS = List.of(new BlockFilter("Craft-Table", block -> block == class_2246.field_9980), new BlockFilter("Brewing-Stand", block -> block == class_2246.field_10333), new BlockFilter("Door", block -> block.method_9564().method_26164(class_3481.field_15495)), new BlockFilter("Hopper", block -> block == class_2246.field_10312), new BlockFilter("Button", block -> block.method_9564().method_26164(class_3481.field_15493)), new BlockFilter("Note-Block", block -> block == class_2246.field_10179), new BlockFilter("Trap-Door", block -> block.method_9564().method_26164(class_3481.field_15487)), new BlockFilter("Furnace", block -> block == class_2246.field_10181), new BlockFilter("Chest", block -> block == class_2246.field_10034), new BlockFilter("Trapped-Chest", block -> block == class_2246.field_10380), new BlockFilter("Ender-Chest", block -> block == class_2246.field_10443), new BlockFilter("Gate", block -> block.method_9564().method_26164(class_3481.field_25147)), new BlockFilter("Anvil", block -> block.method_9564().method_26164(class_3481.field_15486)), new BlockFilter("Dispenser", block -> block == class_2246.field_10200), new BlockFilter("Lever", block -> block == class_2246.field_10363), new BlockFilter("Sign", block -> block.method_9564().method_26164(class_3481.field_41282)), new BlockFilter("Shulker-Box", block -> block.method_9564().method_26164(class_3481.field_21490)), new BlockFilter("Barrel", block -> block == class_2246.field_16328), new BlockFilter("Smoker", block -> block == class_2246.field_16334), new BlockFilter("Dropper", block -> block == class_2246.field_10228), new BlockFilter("Crafter", block -> block == class_2246.field_46797), new BlockFilter("Beacon", block -> block == class_2246.field_10327), new BlockFilter("Enchanting-Table", block -> block == class_2246.field_10485), new BlockFilter("Grindstone", block -> block == class_2246.field_16337), new BlockFilter("Stonecutter", block -> block == class_2246.field_16335), new BlockFilter("Loom", block -> block == class_2246.field_10083), new BlockFilter("Cartography-Table", block -> block == class_2246.field_16336), new BlockFilter("Smithing-Table", block -> block == class_2246.field_16329), new BlockFilter("Lectern", block -> block == class_2246.field_16330), new BlockFilter("Chiseled-Bookshelf", block -> block == class_2246.field_40276), new BlockFilter("Decorated-Pot", block -> block == class_2246.field_42752), new BlockFilter("Comparator", block -> block == class_2246.field_10377), new BlockFilter("Repeater", block -> block == class_2246.field_10450), new BlockFilter("Jukebox", block -> block == class_2246.field_10223), new BlockFilter("Bell", block -> block == class_2246.field_16332), new BlockFilter("Composter", block -> block == class_2246.field_17563), new BlockFilter("Respawn-Anchor", block -> block == class_2246.field_23152), new BlockFilter("Cauldron", block -> block.method_9564().method_26164(class_3481.field_26985)));
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0441 \u0431\u043b\u043e\u043a\u0430\u043c\u0438").withKeybind().toggle()).build();
    private final BooleanSetting allBlocks;
    private final MultiSelectSetting ignore;

    public NoInteractModule(EventBus eventBus) {
        super(eventBus, "no_interact", "No Interact", "\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0441 \u0431\u043b\u043e\u043a\u0430\u043c\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.allBlocks = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("All Blocks").id("all_blocks").description("\u0411\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0430 \u0432\u0441\u0435\u0445 \u0431\u043b\u043e\u043a\u043e\u0432 \u0438\u0437 \u0441\u043f\u0438\u0441\u043a\u0430")).build();
        this.registerSetting(this.allBlocks);
        MultiSelectSetting ignoreSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Craft-Table", "Brewing-Stand", "Door", "Hopper", "Button", "Note-Block", "Trap-Door", "Furnace", "Chest", "Trapped-Chest", "Ender-Chest", "Gate", "Anvil", "Dispenser", "Lever", "Sign", "Shulker-Box", "Barrel", "Smoker", "Dropper", "Crafter", "Beacon", "Enchanting-Table", "Grindstone", "Stonecutter", "Loom", "Cartography-Table", "Smithing-Table", "Lectern", "Chiseled-Bookshelf", "Decorated-Pot", "Comparator", "Repeater", "Jukebox", "Bell", "Composter", "Respawn-Anchor", "Cauldron").selectAll(false).optionListEnabled(false).name("Ignore").id("ignore").description("\u0411\u043b\u043e\u043a\u0438, \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435 \u0441 \u043a\u043e\u0442\u043e\u0440\u044b\u043c\u0438 \u0431\u043b\u043e\u043a\u0438\u0440\u0443\u0435\u0442\u0441\u044f").visibleWhen(() -> !this.allBlocks.isEnabled())).build();
        ignoreSetting.setOptions(new String[0]);
        this.ignore = ignoreSetting;
        this.registerSetting(ignoreSetting);
    }

    @Override
    protected void initialize() {
        this.listen(BlockInteractEvent.class, this::onBlockInteract);
    }

    private void onBlockInteract(BlockInteractEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        if (this.shouldBlock(event.getBlock())) {
            event.cancel();
        }
    }

    private boolean shouldBlock(class_2248 block) {
        boolean all = this.allBlocks.isEnabled();
        List<String> selected = this.ignore.getSelectedOptions();
        for (BlockFilter filter : FILTERS) {
            if (!all && !selected.contains(filter.name) || !filter.matches.test(block)) continue;
            return true;
        }
        return false;
    }

    private record BlockFilter(String name, Predicate<class_2248> matches) {
    }
}

