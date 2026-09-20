/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_4587
 */
package ru.wexside.module.render;

import net.minecraft.class_1268;
import net.minecraft.class_4587;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HandRenderEvent;
import ru.wexside.event.HandRenderPhase;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.hand.EnergyAuraMode;
import ru.wexside.render.hand.HandAuraEffects;
import ru.wexside.render.hand.HandMaterialMode;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HandAuraModule
extends Module
implements ConfigSerializable {
    static final int ENERGY_COLOR_PRESET = -1125978881;
    static final int MATERIAL_COLOR_PRESET = -1427968769;
    private static volatile HandAuraModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting energy;
    private final ModeSetting energyMode;
    private final ColorSetting energyColor;
    private final NumberSetting energyRadius;
    private final BooleanSetting fillHands;
    private final BooleanSetting material;
    private final ModeSetting materialMode;
    private final ColorSetting materialColor;
    private final NumberSetting materialRadius;
    private boolean lastEnabledState;

    public HandAuraModule(EventBus eventBus) {
        super(eventBus, "hand_aura", "Hand Aura", "\u042d\u0444\u0444\u0435\u043a\u0442\u044b \u043d\u0430 \u0440\u0443\u043a\u0430\u0445", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.energy = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Effect").id("energy").description("\u042d\u043d\u0435\u0440\u0433\u0435\u0442\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u0430\u0443\u0440\u0430 \u0432\u043e\u043a\u0440\u0443\u0433 \u0440\u0443\u043a")).build();
        this.registerSetting(this.energy);
        this.energyMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Static", "Hand Aura", "Ribbons").defaultOption("Static").name("Effect mode").id("energy_mode").description("\u0420\u0435\u0436\u0438\u043c \u044d\u043d\u0435\u0440\u0433\u0435\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u044d\u0444\u0444\u0435\u043a\u0442\u0430").visibleWhen(this.energy::isEnabled)).build();
        this.registerSetting(this.energyMode);
        ColorSetting energyColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Effect color").id("energy_color").description("\u0426\u0432\u0435\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u0430").visibleWhen(this.energy::isEnabled)).build();
        energyColorSetting.setPrimaryColor(0, -1125978881);
        energyColorSetting.setPrimaryColor(1, -1543135);
        energyColorSetting.setPrimaryColor(2, -9279489);
        energyColorSetting.setPrimaryColor(3, -46001);
        energyColorSetting.setPrimaryColor(4, -13218);
        energyColorSetting.setPrimaryColor(5, -10582785);
        energyColorSetting.setPrimaryColor(6, -2732032);
        this.energyColor = energyColorSetting;
        this.registerSetting(energyColorSetting);
        this.energyRadius = ((NumberSettingBuilder)NumberSetting.builder().range(0.5, 2.0).defaultValue(1.0).multiplier(1.0).precision(1).animationSpeed(20.0f).name("Energy radius").id("energy_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u044d\u0444\u0444\u0435\u043a\u0442\u0430 \u0432\u043e\u043a\u0440\u0443\u0433 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432").visibleWhen(this.energy::isEnabled)).build();
        this.registerSetting(this.energyRadius);
        this.fillHands = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Fill hands").id("fill_hands").description("\u041d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u0432\u0435\u0440\u0445 \u0440\u0443\u043a").visibleWhen(this.energy::isEnabled)).build();
        this.registerSetting(this.fillHands);
        this.material = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Material").id("material").description("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0440\u0443\u043a\u0443")).build();
        this.registerSetting(this.material);
        this.materialMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Glass Hand", "Liquid Glass", "Chrome", "Simple", "Fill").defaultOption("Glass Hand").name("Material mode").id("material_mode").description("\u0420\u0435\u0436\u0438\u043c \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b").visibleWhen(this.material::isEnabled)).build();
        this.registerSetting(this.materialMode);
        ColorSetting materialColorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Material color").id("material_color").description("\u0426\u0432\u0435\u0442 \u043d\u0430\u043a\u043b\u0430\u0434\u044b\u0432\u0430\u0435\u043c\u043e\u0439 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b").visibleWhen(this.material::isEnabled)).build();
        materialColorSetting.setPrimaryColor(0, -1427968769);
        materialColorSetting.setPrimaryColor(1, -1543135);
        materialColorSetting.setPrimaryColor(2, -9279489);
        materialColorSetting.setPrimaryColor(3, -46001);
        materialColorSetting.setPrimaryColor(4, -13218);
        materialColorSetting.setPrimaryColor(5, -10582785);
        materialColorSetting.setPrimaryColor(6, -2732032);
        this.materialColor = materialColorSetting;
        this.registerSetting(materialColorSetting);
        this.materialRadius = ((NumberSettingBuilder)NumberSetting.builder().range(0.6, 1.8).defaultValue(1.0).multiplier(1.0).precision(2).animationSpeed(20.0f).name("Material radius").id("material_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u044c\u043d\u043e\u0433\u043e \u044d\u0444\u0444\u0435\u043a\u0442\u0430").visibleWhen(this.material::isEnabled)).build();
        this.registerSetting(this.materialRadius);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onToggle());
        this.listen(WorldSessionEvent.class, event -> this.onWorldChange());
        this.listen(HandRenderEvent.class, this::onHandRender);
    }

    public static HandAuraModule getInstance() {
        return instance;
    }

    public static boolean isModuleEnabled() {
        HandAuraModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public void applyEnergyEffect(class_1268 hand, Object matrices) {
        if (!(this.enabledSetting.isEnabled() && this.isEnergyEnabled() && matrices instanceof class_4587)) {
            return;
        }
        class_4587 matrixStack = (class_4587)matrices;
        HandAuraEffects.applyHandEnergy(hand, matrixStack);
    }

    private void onToggle() {
        boolean enabled = this.enabledSetting.isEnabled();
        if (enabled == this.lastEnabledState) {
            return;
        }
        this.lastEnabledState = enabled;
        if (enabled) {
            HandAuraEffects.reset();
            this.syncRenderers();
        } else {
            HandAuraEffects.disableAll();
            HandAuraEffects.reset();
        }
    }

    private void onWorldChange() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        HandAuraEffects.reset();
        this.syncRenderers();
    }

    private void onHandRender(HandRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        this.syncRenderers();
        if (event.phase() == HandRenderPhase.BEFORE) {
            HandAuraEffects.beforeHandRender(event.matrices(), event.tickDelta());
            return;
        }
        HandAuraEffects.afterHandRender(event.matrices());
    }

    private void syncRenderers() {
        boolean energyEnabled = this.isEnergyEnabled();
        boolean materialEnabled = this.isMaterialEnabled();
        boolean energyGradient = this.energyColor.isAstolfoMode() || this.energyColor.isDoubleColorMode();
        HandAuraEffects.configureEnergy(energyEnabled, this.resolveEnergyMode(), this.energyColor.getColor(0.0f), energyGradient ? this.energyColor.getColor(0.5f) : this.energyColor.getColor(0.0f), energyGradient, (float)this.energyRadius.getValue(), this.fillHands.isEnabled());
        if (!materialEnabled) {
            HandAuraEffects.configureMaterial(false, this.resolveMaterialMode(), 0.0f, 0, 0, false);
            return;
        }
        boolean materialGradient = this.materialColor.isAstolfoMode() || this.materialColor.isDoubleColorMode();
        HandAuraEffects.configureMaterial(true, this.resolveMaterialMode(), (float)this.materialRadius.getValue(), this.materialColor.getColor(0.0f), materialGradient ? this.materialColor.getColor(0.5f) : this.materialColor.getColor(0.0f), materialGradient);
    }

    private boolean isEnergyEnabled() {
        return this.enabledSetting.isEnabled() && this.energy.isEnabled();
    }

    private boolean isMaterialEnabled() {
        return this.enabledSetting.isEnabled() && this.material.isEnabled();
    }

    private EnergyAuraMode resolveEnergyMode() {
        return HandAuraEffects.resolveEnergyMode(this.energyMode.getSelectedOption());
    }

    private HandMaterialMode resolveMaterialMode() {
        return HandAuraEffects.resolveMaterialMode(this.materialMode.getSelectedOption());
    }
}

