/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.render;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.misc.ConsumablesEspRenderer;
import ru.byazen.misc.ConsumablesEspSettings;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;

public final class ConsumablesESPModule
extends Module
implements ConfigSerializable {
    private static final String[] CONSUMABLE_TYPES = new String[]{"\u0422\u0440\u0430\u043f\u043a\u0430", "\u041f\u043b\u0430\u0441\u0442", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430"};
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0443 \u0437\u043e\u043d \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u043e\u0432").withKeybind().toggle()).build();
    private final BooleanSetting self;
    private final MultiSelectSetting selfTypes;
    private final ColorSetting selfColor;
    private final BooleanSetting enemies;
    private final MultiSelectSetting enemyTypes;
    private final ColorSetting enemyColor;
    private final ConsumablesEspSettings options;
    private final ConsumablesEspRenderer renderer;

    public ConsumablesESPModule(EventBus eventBus) {
        super(eventBus, "consumables_esp", "Consumables ESP", "\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u0437\u043e\u043d \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u043e\u0432", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.self = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Self").id("self").description("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0441\u0432\u043e\u0438 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438")).build();
        this.registerSetting(this.self);
        MultiSelectSetting selfTypesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("\u0422\u0440\u0430\u043f\u043a\u0430", "\u041f\u043b\u0430\u0441\u0442", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430").selectAll(true).optionListEnabled(false).name("Self Types").id("self_types").description("\u041a\u0430\u043a\u0438\u0435 \u0441\u0432\u043e\u0438 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438 \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c").aliases("self types", "\u0441\u0432\u043e\u0438 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438").visibleWhen(this.self::isEnabled)).build();
        selfTypesSetting.setOptions(CONSUMABLE_TYPES);
        this.selfTypes = selfTypesSetting;
        this.registerSetting(selfTypesSetting);
        ColorSetting selfColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(5).name("Self Color").id("self_color").description("\u0426\u0432\u0435\u0442 \u0441\u0432\u043e\u0438\u0445 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u043e\u0432").aliases("self color", "\u0446\u0432\u0435\u0442 \u0441\u0432\u043e\u0438\u0445").visibleWhen(this.self::isEnabled)).build();
        this.applyPalette(selfColorSetting);
        this.selfColor = selfColorSetting;
        this.registerSetting(selfColorSetting);
        this.enemies = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Enemies").id("enemies").description("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0438\u0435 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438")).build();
        this.registerSetting(this.enemies);
        MultiSelectSetting enemyTypesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("\u0422\u0440\u0430\u043f\u043a\u0430", "\u041f\u043b\u0430\u0441\u0442", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430").selectAll(true).optionListEnabled(false).name("Enemy Types").id("enemy_types").description("\u041a\u0430\u043a\u0438\u0435 \u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0438\u0435 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438 \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c").aliases("enemy types", "\u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0438\u0435 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u0438").visibleWhen(this.enemies::isEnabled)).build();
        enemyTypesSetting.setOptions(CONSUMABLE_TYPES);
        this.enemyTypes = enemyTypesSetting;
        this.registerSetting(enemyTypesSetting);
        ColorSetting enemyColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(3).name("Enemy Color").id("enemy_color").description("\u0426\u0432\u0435\u0442 \u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0438\u0445 \u0440\u0430\u0441\u0445\u043e\u0434\u043d\u0438\u043a\u043e\u0432").aliases("enemy color", "\u0446\u0432\u0435\u0442 \u0432\u0440\u0430\u0436\u0435\u0441\u043a\u0438\u0445").visibleWhen(this.enemies::isEnabled)).build();
        this.applyPalette(enemyColorSetting);
        this.enemyColor = enemyColorSetting;
        this.registerSetting(enemyColorSetting);
        this.options = new ConsumablesEspSettings(this.enabledSetting, this.self, this.selfTypes, this.selfColor, this.enemies, this.enemyTypes, this.enemyColor);
        this.renderer = new ConsumablesEspRenderer(this.options);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, event -> this.renderer.setWorldRenderEvent((WorldRenderEvent)event));
        this.listen(WorldSessionEvent.class, event -> this.renderer.update());
    }

    private void applyPalette(ColorSetting setting) {
        setting.setPrimaryColor(0, -11753627);
        setting.setPrimaryColor(1, -1543135);
        setting.setPrimaryColor(2, -9279489);
        setting.setPrimaryColor(3, -46001);
        setting.setPrimaryColor(4, -13218);
        setting.setPrimaryColor(5, -10582785);
        setting.setPrimaryColor(6, -2732032);
    }
}

