/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1703
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_310
 *  net.minecraft.class_4081
 *  net.minecraft.class_465
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package ru.wexside.module.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_4081;
import net.minecraft.class_465;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.SlotHighlight;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.ContainerScreenHelper;

public final class HealthHelperModule
extends Module
implements ConfigSerializable {
    private static volatile HealthHelperModule instance;
    private final BooleanSetting enabledSetting;
    private final ColorSetting buffColor;
    private final BooleanSetting debuffs;
    private final ColorSetting debuffColor;
    private final BooleanSetting gapple;
    private final ColorSetting gappleColor;
    private final BooleanSetting enchantedGapple;
    private final ColorSetting enchantedGappleColor;
    private final BooleanSetting tint;
    private final NumberSetting tintPeriod;

    public HealthHelperModule(EventBus eventBus) {
        super(eventBus, "health_helper", "Health Helper", "\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0438\u0441\u0446\u0435\u043b\u044f\u044e\u0449\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0438\u0441\u0446\u0435\u043b\u044f\u044e\u0449\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting buff = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color buff").id("buff_color").description("\u0426\u0432\u0435\u0442 \u043f\u043e\u043b\u0435\u0437\u043d\u044b\u0445 \u0437\u0435\u043b\u0438\u0439")).build();
        this.applyDefaultPalette(buff);
        this.buffColor = buff;
        this.registerSetting(buff);
        this.debuffs = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Debuffs").id("debuffs").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0432\u0440\u0435\u0434\u043d\u044b\u0435 \u0437\u0435\u043b\u044c\u044f \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435")).build();
        this.registerSetting(this.debuffs);
        ColorSetting debuff = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(3).name("Color debuff").id("debuff_color").description("\u0426\u0432\u0435\u0442 \u0432\u0440\u0435\u0434\u043d\u044b\u0445 \u0437\u0435\u043b\u0438\u0439").visibleWhen(this.debuffs::isEnabled)).build();
        this.applyDefaultPalette(debuff);
        this.debuffColor = debuff;
        this.registerSetting(debuff);
        this.gapple = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("GApple").id("gapple").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0437\u043e\u043b\u043e\u0442\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438")).build();
        this.registerSetting(this.gapple);
        ColorSetting gappleColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(4).name("Color gapple").id("gapple_color").description("\u0426\u0432\u0435\u0442 \u0437\u043e\u043b\u043e\u0442\u043e\u0433\u043e \u044f\u0431\u043b\u043e\u043a\u0430").visibleWhen(this.gapple::isEnabled)).build();
        this.applyDefaultPalette(gappleColorSetting);
        this.gappleColor = gappleColorSetting;
        this.registerSetting(gappleColorSetting);
        this.enchantedGapple = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("E-GApple").id("e_gapple").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0435 \u0437\u043e\u043b\u043e\u0442\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438")).build();
        this.registerSetting(this.enchantedGapple);
        ColorSetting egappleColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(2).name("Color e-gapple").id("e_gapple_color").description("\u0426\u0432\u0435\u0442 \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0433\u043e \u0437\u043e\u043b\u043e\u0442\u043e\u0433\u043e \u044f\u0431\u043b\u043e\u043a\u0430").visibleWhen(this.enchantedGapple::isEnabled)).build();
        this.applyDefaultPalette(egappleColorSetting);
        this.enchantedGappleColor = egappleColorSetting;
        this.registerSetting(egappleColorSetting);
        this.tint = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Tint").id("tint").description("\u041f\u0443\u043b\u044c\u0441\u0430\u0446\u0438\u044f \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438 \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438")).build();
        this.registerSetting(this.tint);
        this.tintPeriod = ((NumberSettingBuilder)NumberSetting.builder().range(500.0, 2000.0).defaultValue(1200.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Tint period").id("tint_period").description("\u041f\u0435\u0440\u0438\u043e\u0434 \u043f\u0443\u043b\u044c\u0441\u0430\u0446\u0438\u0438 \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438").visibleWhen(this.tint::isEnabled)).build();
        this.registerSetting(this.tintPeriod);
    }

    @Override
    protected void initialize() {
        WexSideClient.getSlotHighlightRegistry().setCallback56(this::collectHighlights);
    }

    private List<SlotHighlight> collectHighlights(class_465<?> screen) {
        if (!this.enabledSetting.isEnabled() || screen == null || HealthHelperModule.shouldSkipPlayer()) {
            return List.of();
        }
        class_1703 handler = screen.method_17577();
        if (handler == null || !ContainerScreenHelper.isPlayerInventoryContainer(handler, screen)) {
            return List.of();
        }
        ArrayList<SlotHighlight> highlights = new ArrayList<SlotHighlight>();
        for (int i = 0; i < handler.field_7761.size(); ++i) {
            class_1735 slot = (class_1735)handler.field_7761.get(i);
            Integer color = this.resolveColor(slot.method_7677());
            if (color == null) continue;
            highlights.add(new SlotHighlight(i, color));
        }
        return highlights;
    }

    public static int process7(class_1799 stack) {
        HealthHelperModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled() || stack == null || stack.method_7960() || HealthHelperModule.shouldSkipPlayer()) {
            return 0;
        }
        Integer color = module.resolveColor(stack);
        return color == null ? 0 : color;
    }

    private static boolean shouldSkipPlayer() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_68878();
    }

    private Integer resolveColor(class_1799 stack) {
        if (stack.method_31574(class_1802.field_8367)) {
            return this.enchantedGapple.isEnabled() ? Integer.valueOf(this.resolveTintedColor(this.enchantedGappleColor)) : null;
        }
        if (stack.method_31574(class_1802.field_8463)) {
            return this.gapple.isEnabled() ? Integer.valueOf(this.resolveTintedColor(this.gappleColor)) : null;
        }
        if (!(stack.method_31574(class_1802.field_8574) || stack.method_31574(class_1802.field_8436) || stack.method_31574(class_1802.field_8150))) {
            return null;
        }
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents == null || !contents.method_57405()) {
            return null;
        }
        if (this.isMostlyHarmful(contents)) {
            return this.debuffs.isEnabled() ? Integer.valueOf(this.resolveTintedColor(this.debuffColor)) : null;
        }
        return this.resolveTintedColor(this.buffColor);
    }

    private boolean isMostlyHarmful(class_1844 contents) {
        int harmful = 0;
        int beneficial = 0;
        for (class_1293 effect : contents.method_57397()) {
            class_1291 type = (class_1291)effect.method_5579().comp_349();
            if (type.method_18792() == class_4081.field_18272) {
                ++harmful;
                continue;
            }
            if (type.method_18792() != class_4081.field_18271) continue;
            ++beneficial;
        }
        return harmful > beneficial;
    }

    private int resolveTintedColor(ColorSetting colorSetting) {
        int color = this.resolveBaseColor(colorSetting);
        if (this.tint.isEnabled()) {
            color = ColorUtils.multiplyAlpha(color, this.getTintStrength());
        }
        return color;
    }

    private int resolveBaseColor(ColorSetting colorSetting) {
        if (colorSetting.isAstolfoMode()) {
            return colorSetting.getColor();
        }
        if (colorSetting.isDoubleColorMode()) {
            return ColorUtils.lerp(colorSetting.getPrimaryColor(), colorSetting.getSecondaryColor(), HealthHelperModule.pulse(1500L));
        }
        return colorSetting.getPrimaryColor();
    }

    private float getTintStrength() {
        long period = Math.max(1L, (long)this.tintPeriod.getIntValue());
        return (float)(0.3 + 0.7 * HealthHelperModule.pulse(period));
    }

    private static double pulse(long periodMs) {
        double phase = (double)(System.currentTimeMillis() % periodMs) / (double)periodMs;
        return 1.0 - Math.abs(2.0 * phase - 1.0);
    }

    private void applyDefaultPalette(ColorSetting colorSetting) {
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
    }
}

