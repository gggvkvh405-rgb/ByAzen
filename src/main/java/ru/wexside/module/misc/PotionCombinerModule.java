/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.wexside.module.misc;

import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ServerKind;
import ru.wexside.misc.SwapTiming;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.misc.PotionCombinerRadialScreen;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.PotionPresetController;

public final class PotionCombinerModule
extends Module
implements ConfigSerializable {
    private static volatile PotionCombinerModule instance;
    private final BooleanSetting enabledSetting;
    private final ModeSetting serverMode;
    private final ModeSetting swapMode;
    private final BooleanSetting fromBundle;
    private final BooleanSetting ftMode;
    private final BindSetting menuKey;
    private final BindSetting radialSelector;

    public PotionCombinerModule(EventBus eventBus) {
        super(eventBus, "potion_combiner", "Potion Combiner", "\u0411\u0440\u043e\u0441\u043e\u043a \u043d\u0430\u0431\u043e\u0440\u0430 \u0437\u0435\u043b\u0438\u0439 \u043f\u043e\u0434 \u0441\u0435\u0431\u044f \u043f\u043e \u0431\u0438\u043d\u0434\u0430\u043c \u043f\u0440\u0435\u0441\u0435\u0442\u043e\u0432", ModuleCategory.valueOf("MISC"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.serverMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("FT", "Others").defaultOption("FT").name("Server Mode").id("server_mode").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0430")).build();
        this.registerSetting(this.serverMode);
        this.swapMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Default", "Legit", "FS").defaultOption("Default").name("Mode").id("swap_mode").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0432\u0430\u043f\u0430: Default - \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e, Legit - \u0441 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u043e\u0439")).build();
        this.registerSetting(this.swapMode);
        this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0418\u0437 \u043c\u0435\u0448\u043a\u043e\u0432").id("from_bundle").description("\u0414\u043e\u0441\u0442\u0430\u0432\u0430\u0442\u044c \u0437\u0435\u043b\u044c\u044f \u0438\u0437 \u043c\u0435\u0448\u043a\u0430 \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.fromBundle);
        this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("FT-Mode").id("ft_mode").description("\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0448\u043a\u043e\u0432 \u0431\u0435\u0437 \u043b\u0438\u043c\u0438\u0442\u0430 \u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u0438").visibleWhen(this.fromBundle::isEnabled)).build();
        this.registerSetting(this.ftMode);
        this.menuKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Menu Key").id("menu_key").description("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0435\u043d\u044e \u043f\u0440\u0435\u0441\u0435\u0442\u043e\u0432")).build();
        this.registerSetting(this.menuKey);
        this.radialSelector = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).onReleased(this::openRadialMenu).name("Radial Selector").id("radial_selector").description("\u041a\u0440\u0443\u0433\u043e\u0432\u043e\u0439 \u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u0438\u0437\u0431\u0440\u0430\u043d\u043d\u044b\u0445 \u043f\u0440\u0435\u0441\u0435\u0442\u043e\u0432")).build();
        this.registerSetting(this.radialSelector);
    }

    @Override
    protected void initialize() {
    }

    private void openRadialMenu(BindSetting ignored) {
        PotionPresetController combiner;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        WexSideClient wexSide = WexSideClient.getInstance();
        PotionPresetController potionPresetController = combiner = wexSide == null ? null : wexSide.getPotionPresetController();
        if (client.field_1724 == null || client.field_1755 != null || combiner == null || combiner.getFavoritePresets().isEmpty()) {
            return;
        }
        client.method_1507((class_437)new PotionCombinerRadialScreen(this.radialSelector, combiner, combiner::queuePreset));
    }

    public static boolean isActive() {
        return PotionCombinerModule.isEnabled();
    }

    public static boolean isEnabled() {
        PotionCombinerModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public static boolean isActive2() {
        return PotionCombinerModule.isEnabled2();
    }

    public static boolean isEnabled2() {
        PotionCombinerModule module = instance;
        return module != null && module.fromBundle.isEnabled();
    }

    public static boolean isActive3() {
        return PotionCombinerModule.isEnabled3();
    }

    public static boolean isEnabled3() {
        PotionCombinerModule module = instance;
        return module != null && module.ftMode.isEnabled();
    }

    public static ServerKind getServerKind() {
        PotionCombinerModule module = instance;
        return module == null ? ServerKind.GENERAL : ServerKind.parse(module.serverMode.getSelectedOption());
    }

    public static SwapTiming getSwapTiming() {
        PotionCombinerModule module = instance;
        if (module == null) {
            return SwapTiming.DEFAULT;
        }
        String mode = module.swapMode.getSelectedOption();
        if (mode != null && mode.equalsIgnoreCase("Legit")) {
            return SwapTiming.LEGIT;
        }
        if (mode != null && mode.equalsIgnoreCase("FS")) {
            return SwapTiming.FUNTIME;
        }
        return SwapTiming.DEFAULT;
    }

    public static boolean process(int keyCode) {
        PotionCombinerModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.menuKey.getBindInput().matchesKeyboard(keyCode);
    }
}

