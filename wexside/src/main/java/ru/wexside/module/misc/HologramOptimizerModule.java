/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.HologramImpostorRenderer;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HologramOptimizerModule
extends Module
implements ConfigSerializable {
    private static volatile HologramOptimizerModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting distance;
    private final NumberSetting quality;

    public HologramOptimizerModule(EventBus eventBus) {
        super(eventBus, "hologram_optimizer", "Hologram Optimizer", "\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0430\u0446\u0438\u044f \u0440\u0435\u043d\u0434\u0435\u0440\u0430 \u0433\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c", ModuleCategory.valueOf("MISC"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0430\u0446\u0438\u044f \u0440\u0435\u043d\u0434\u0435\u0440\u0430 \u0433\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.distance = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 128.0).defaultValue(48.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Distance").id("distance").description("\u041c\u0430\u043a\u0441. \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0440\u0435\u043d\u0434\u0435\u0440\u0430 \u0433\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c")).build();
        this.registerSetting(this.distance);
        this.quality = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 4.0).defaultValue(4.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Quality").id("quality").description("\u041a\u0440\u0430\u0442\u043d\u043e\u0441\u0442\u044c \u0441\u0443\u043f\u0435\u0440-\u0441\u044d\u043c\u043f\u043b\u0438\u043d\u0433\u0430")).build();
        this.registerSetting(this.quality);
    }

    @Override
    protected void initialize() {
        this.listen(WorldSessionEvent.class, event -> HologramImpostorRenderer.clear());
    }

    public static boolean isEnabled() {
        return HologramOptimizerModule.isActive2();
    }

    public static boolean isActive2() {
        HologramOptimizerModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public static double maxDistanceSquared() {
        HologramOptimizerModule module = instance;
        if (module == null) {
            return Double.MAX_VALUE;
        }
        double distance = module.distance.getValue();
        return distance * distance;
    }

    public static int qualityScale() {
        HologramOptimizerModule module = instance;
        if (module == null) {
            return 2;
        }
        int quality = (int)Math.round(module.quality.getValue());
        return Math.max(1, Math.min(4, quality));
    }
}

