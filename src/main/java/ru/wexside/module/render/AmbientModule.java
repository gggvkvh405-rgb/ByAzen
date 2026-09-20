/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2761
 */
package ru.wexside.module.render;

import net.minecraft.class_2761;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class AmbientModule
extends Module
implements ConfigSerializable {
    private static final String SHADER = "Shader";
    private static final String DEFAULT = "Default";
    private static final String COLOR = "Color";
    private static final double TIME_LERP = 0.15;
    private static final String[] SKY_SHADERS = new String[]{"Water", "Silk", "Aurora", "Nebula", "Milky Veil", "Plasma", "Frost", "Ember", "Pulse", "Sky Stripes"};
    private static volatile AmbientModule instance;
    private final BooleanSetting enabledSetting;
    private final ModeSetting skyMode;
    private final ColorSetting skyColor;
    private final ModeSetting skyShader;
    private final NumberSetting skyIntensity;
    private final NumberSetting skySpeed;
    private final BooleanSetting fog;
    private final ColorSetting fogColor;
    private final NumberSetting fogRadius;
    private final BooleanSetting changeTimeOfDay;
    private final NumberSetting timeOfDay;
    private double smoothedTime;
    private boolean timeInitialized;

    public AmbientModule(EventBus eventBus) {
        super(eventBus, "ambient", "Ambient", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043e\u043a\u0440\u0443\u0436\u0435\u043d\u0438\u044f", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.skyMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(DEFAULT, COLOR, SHADER).defaultOption(DEFAULT).name("Sky mode").id("sky_mode").description("\u0420\u0435\u0436\u0438\u043c \u043d\u0435\u0431\u0430").aliases("sky", "\u043d\u0435\u0431\u043e")).build();
        this.registerSetting(this.skyMode);
        ColorSetting sky = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Sky color").id("sky_color").description("\u0426\u0432\u0435\u0442 \u043d\u0435\u0431\u0430").aliases("sky color", "\u0446\u0432\u0435\u0442 \u043d\u0435\u0431\u0430").visibleWhen(() -> !DEFAULT.equals(this.skyMode.getSelectedOption()))).build();
        sky.setPrimaryColor(0, -11108117);
        sky.setPrimaryColor(1, -1543135);
        sky.setPrimaryColor(2, -9279489);
        sky.setPrimaryColor(3, -46001);
        sky.setPrimaryColor(4, -13218);
        sky.setPrimaryColor(5, -10582785);
        sky.setPrimaryColor(6, -2732032);
        this.skyColor = sky;
        this.registerSetting(sky);
        this.skyShader = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Water", "Silk", "Aurora", "Nebula", "Milky Veil", "Plasma", "Frost", "Ember", "Pulse", "Sky Stripes").defaultOption("Water").name("Sky shader").id("sky_shader").description("\u0428\u0435\u0439\u0434\u0435\u0440 \u043d\u0435\u0431\u0430").aliases("sky shader", "\u0448\u0435\u0439\u0434\u0435\u0440 \u043d\u0435\u0431\u0430").visibleWhen(() -> SHADER.equals(this.skyMode.getSelectedOption()))).build();
        this.registerSetting(this.skyShader);
        this.skyIntensity = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 100.0).defaultValue(25.0).multiplier(0.02).precision(0).animationSpeed(20.0f).markers(25.0).snapTo(25.0).name("Sky intensity").id("sky_intensity").description("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c \u0448\u0435\u0439\u0434\u0435\u0440\u0430 \u043d\u0435\u0431\u0430").aliases("sky intensity", "\u0438\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c").visibleWhen(() -> SHADER.equals(this.skyMode.getSelectedOption()))).build();
        this.registerSetting(this.skyIntensity);
        this.skySpeed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 10.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Sky speed").id("sky_speed").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0448\u0435\u0439\u0434\u0435\u0440\u0430 \u043d\u0435\u0431\u0430").aliases("sky speed", "\u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c").visibleWhen(() -> SHADER.equals(this.skyMode.getSelectedOption()))).build();
        this.registerSetting(this.skySpeed);
        this.fog = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Change fog").id("fog").description("\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0442\u0443\u043c\u0430\u043d").aliases("fog", "\u0442\u0443\u043c\u0430\u043d")).build();
        this.registerSetting(this.fog);
        ColorSetting fogSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Fog color").id("fog_color").description("\u0426\u0432\u0435\u0442 \u0442\u0443\u043c\u0430\u043d\u0430").aliases("fog color", "\u0446\u0432\u0435\u0442 \u0442\u0443\u043c\u0430\u043d\u0430").visibleWhen(this.fog::isEnabled)).build();
        fogSetting.setPrimaryColor(0, -4270357);
        fogSetting.setPrimaryColor(1, -1543135);
        fogSetting.setPrimaryColor(2, -9279489);
        fogSetting.setPrimaryColor(3, -46001);
        fogSetting.setPrimaryColor(4, -13218);
        fogSetting.setPrimaryColor(5, -10582785);
        fogSetting.setPrimaryColor(6, -2732032);
        this.fogColor = fogSetting;
        this.registerSetting(fogSetting);
        this.fogRadius = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 12.0).defaultValue(4.0).multiplier(16.0).precision(0).animationSpeed(20.0f).markers(1.0).name("Fog radius").id("fog_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u0442\u0443\u043c\u0430\u043d\u0430").aliases("fog radius", "\u0440\u0430\u0434\u0438\u0443\u0441 \u0442\u0443\u043c\u0430\u043d\u0430").visibleWhen(this.fog::isEnabled)).build();
        this.registerSetting(this.fogRadius);
        this.changeTimeOfDay = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Change time of day").id("change_time_of_day").description("\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0432\u0440\u0435\u043c\u044f \u0441\u0443\u0442\u043e\u043a")).build();
        this.registerSetting(this.changeTimeOfDay);
        this.timeOfDay = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 23.0).defaultValue(6.0).multiplier(1000.0).precision(0).animationSpeed(20.0f).name("Time of day").id("time_of_day").description("\u0412\u0440\u0435\u043c\u044f \u0441\u0443\u0442\u043e\u043a").visibleWhen(this.changeTimeOfDay::isEnabled)).build();
        this.registerSetting(this.timeOfDay);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, event -> {
            if (!this.customTimeEnabled()) {
                return;
            }
            if (event.getPacket() instanceof class_2761) {
                event.update();
            }
        });
        this.listen(ClientTickEvent.class, event -> {
            if (!this.customTimeEnabled()) {
                this.timeInitialized = false;
                return;
            }
            double target = this.timeOfDay.getValue();
            if (!this.timeInitialized) {
                this.smoothedTime = target;
                this.timeInitialized = true;
                return;
            }
            this.smoothedTime += (target - this.smoothedTime) * 0.15;
        });
    }

    public static int getIntType() {
        AmbientModule module = instance;
        if (module == null) {
            return -16777216;
        }
        return module.skyColor.getPrimaryColor() | 0xFF000000;
    }

    public static int getIntType2() {
        AmbientModule module = instance;
        return module == null ? -1 : module.fogColor.getColor();
    }

    public static boolean isEnabled() {
        AmbientModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && SHADER.equals(module.skyMode.getSelectedOption());
    }

    public static boolean isEnabled2() {
        AmbientModule module = instance;
        return module != null && module.customTimeEnabled();
    }

    public static double getDoubleType() {
        AmbientModule module = instance;
        return module == null ? 224.0 : module.fogRadius.getValue();
    }

    public static long getLongType() {
        AmbientModule module = instance;
        if (module == null) {
            return 0L;
        }
        return (long)(module.timeInitialized ? module.smoothedTime : module.timeOfDay.getValue());
    }

    public static boolean isEnabled4() {
        AmbientModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.fog.isEnabled();
    }

    public static boolean isEnabled5() {
        AmbientModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        String selected = module.skyMode.getSelectedOption();
        return COLOR.equals(selected) || SHADER.equals(selected);
    }

    private boolean customTimeEnabled() {
        return this.enabledSetting.isEnabled() && this.changeTimeOfDay.isEnabled();
    }

    private int shaderIndex() {
        String selected = this.skyShader.getSelectedOption();
        for (int i = 0; i < SKY_SHADERS.length; ++i) {
            if (!SKY_SHADERS[i].equals(selected)) continue;
            return i;
        }
        return 0;
    }
}

