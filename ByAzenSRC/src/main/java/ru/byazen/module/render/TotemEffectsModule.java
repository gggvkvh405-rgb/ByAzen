/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.render;

import java.util.LinkedHashMap;
import java.util.Map;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.TotemPopEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.misc.TotemEffectComposite;
import ru.byazen.misc.TotemEffectRenderer;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.Snowflake;
import ru.byazen.util.TotemEffectSettings;
import ru.byazen.util.TotemGhostRenderer;
import ru.byazen.util.TotemHologramRenderer;

public final class TotemEffectsModule
extends Module
implements ConfigSerializable {
    private static final String HOLOGRAM = "Hologram";
    private static final String PARTICLES = "Particles";
    private static final String GHOST = "Ghost";
    private static final String BOTH = "Both";
    static volatile TotemEffectsModule instance;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final ModeSetting mode;
    private final ModeSetting particleType;
    private final NumberSetting particleCount;
    private final BooleanSetting physics;
    private final NumberSetting speed;
    private final NumberSetting lifeTime;
    private final TotemEffectSettings settings;
    private final TotemEffectRenderer hologram;
    private final TotemEffectRenderer particles;
    private final TotemEffectRenderer ghost;
    private final TotemEffectRenderer both;
    private final Map<String, TotemEffectRenderer> effects;
    private String lastMode;

    public TotemEffectsModule(EventBus eventBus) {
        super(eventBus, "totem_effects", "Totem Effects", "\u042d\u0444\u0444\u0435\u043a\u0442\u044b \u043f\u0440\u0438 \u0441\u0440\u0430\u0431\u0430\u0442\u044b\u0432\u0430\u043d\u0438\u0438 \u0442\u043e\u0442\u0435\u043c\u0430 \u0443 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442\u044b \u043f\u0440\u0438 \u0441\u0440\u0430\u0431\u0430\u0442\u044b\u0432\u0430\u043d\u0438\u0438 \u0442\u043e\u0442\u0435\u043c\u0430").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u044d\u0444\u0444\u0435\u043a\u0442\u0430").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(HOLOGRAM, PARTICLES, GHOST, BOTH).defaultOption(HOLOGRAM).name("Mode").id("mode").description("\u0422\u0438\u043f \u044d\u0444\u0444\u0435\u043a\u0442\u0430")).build();
        this.registerSetting(this.mode);
        this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Star", "Mini-star", "Snowflake", "Dollar", "Cross").defaultOption("Star").name("Particle type").id("particle_type").description("\u0422\u0438\u043f \u0447\u0430\u0441\u0442\u0438\u0446").visibleWhen(this::particlesVisible)).build();
        this.registerSetting(this.particleType);
        this.particleCount = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.5).precision(0).animationSpeed(20.0f).snapTo(10.0).name("Particles count").id("particle_count").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446").visibleWhen(this::particlesVisible)).build();
        this.registerSetting(this.particleCount);
        this.physics = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Physics").id("physics").description("\u0424\u0438\u0437\u0438\u043a\u0430 \u0447\u0430\u0441\u0442\u0438\u0446 (\u043e\u0442\u0441\u043a\u043e\u043a \u043e\u0442 \u0431\u043b\u043e\u043a\u043e\u0432, \u0433\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u044f)").aliases("physics", "\u0444\u0438\u0437\u0438\u043a\u0430").visibleWhen(this::particlesVisible)).build();
        this.registerSetting(this.physics);
        this.speed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(2.0).multiplier(0.025).precision(0).animationSpeed(20.0f).name("Speed").id("speed").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446").aliases("speed", "\u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c").visibleWhen(this::particlesVisible)).build();
        this.registerSetting(this.speed);
        this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(40.0).multiplier(1.0).precision(0).animationSpeed(20.0f).snapTo(10.0).name("Life time").id("life_time").description("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446").visibleWhen(this::particlesVisible)).build();
        this.registerSetting(this.lifeTime);
        this.settings = new TotemEffectSettings(this.color, this.particleType, this.particleCount, this.physics, this.speed, this.lifeTime);
        this.hologram = new TotemHologramRenderer(this.settings);
        this.particles = new Snowflake(this.settings);
        this.ghost = new TotemGhostRenderer(this.settings);
        this.both = new TotemEffectComposite(this.hologram, this.particles, this.ghost);
        this.effects = this.buildEffects();
    }

    @Override
    protected void initialize() {
        this.listen(TotemPopEvent.class, this::onTotemPop);
        this.listen(WorldRenderEvent.class, this::onWorldRender);
        this.listen(WorldSessionEvent.class, event -> this.clearEffects());
    }

    public static boolean isEnabled() {
        TotemEffectsModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    private void onTotemPop(TotemPopEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        this.activeEffect().setTotemPopEvent(event);
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.clearEffects();
            return;
        }
        this.syncMode();
        this.activeEffect().render(event);
    }

    private Map<String, TotemEffectRenderer> buildEffects() {
        LinkedHashMap<String, TotemEffectRenderer> map = new LinkedHashMap<String, TotemEffectRenderer>();
        map.put(HOLOGRAM, this.hologram);
        map.put(PARTICLES, this.particles);
        map.put(GHOST, this.ghost);
        map.put(BOTH, this.both);
        return map;
    }

    private TotemEffectRenderer activeEffect() {
        return this.effects.getOrDefault(this.mode.getSelectedOption(), this.hologram);
    }

    private void syncMode() {
        String current = this.mode.getSelectedOption();
        if (current == null) {
            return;
        }
        if (this.lastMode == null) {
            this.lastMode = current;
            return;
        }
        if (!current.equals(this.lastMode)) {
            this.clearEffects();
            this.lastMode = current;
        }
    }

    private boolean particlesVisible() {
        if (!this.enabledSetting.isEnabled()) {
            return false;
        }
        String current = this.mode.getSelectedOption();
        return PARTICLES.equals(current) || BOTH.equals(current);
    }

    private void clearEffects() {
        this.effects.values().forEach(TotemEffectRenderer::update2);
    }
}

