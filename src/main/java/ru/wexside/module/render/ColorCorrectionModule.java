/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.ColorCorrectionEffect;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class ColorCorrectionModule
extends Module
implements ConfigSerializable {
    private static volatile ColorCorrectionModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting contrast;
    private final NumberSetting saturation;
    private final NumberSetting brightness;
    private final ColorCorrectionEffect pipeline = new ColorCorrectionEffect();

    public ColorCorrectionModule(EventBus eventBus) {
        super(eventBus, "color_correction", "Color Correction", "\u041f\u043e\u0441\u0442-\u043e\u0431\u0440\u0430\u0431\u043e\u0442\u043a\u0430 \u043a\u0430\u0434\u0440\u0430: \u043a\u043e\u043d\u0442\u0440\u0430\u0441\u0442, \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0438 \u044f\u0440\u043a\u043e\u0441\u0442\u044c", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0446\u0432\u0435\u0442\u043e\u043a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044e \u043a\u0430\u0434\u0440\u0430").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.contrast = ((NumberSettingBuilder)NumberSetting.builder().range(100.0, 200.0).defaultValue(100.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Contrast").id("contrast").description("\u041a\u043e\u043d\u0442\u0440\u0430\u0441\u0442 \u0438\u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f").aliases("contrast", "\u043a\u043e\u043d\u0442\u0440\u0430\u0441\u0442")).build();
        this.registerSetting(this.contrast);
        this.saturation = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 200.0).defaultValue(100.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Saturation").id("saturation").description("\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0446\u0432\u0435\u0442\u043e\u0432").aliases("saturation", "\u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c")).build();
        this.registerSetting(this.saturation);
        this.brightness = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 200.0).defaultValue(100.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Brightness").id("brightness").description("\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0438\u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f").aliases("brightness", "\u044f\u0440\u043a\u043e\u0441\u0442\u044c")).build();
        this.registerSetting(this.brightness);
    }

    @Override
    protected void initialize() {
        this.listen(WorldSessionEvent.class, event -> this.pipeline.close());
    }

    public static void tick() {
        ColorCorrectionModule module = instance;
        if (module == null) {
            return;
        }
        if (!module.enabledSetting.isEnabled()) {
            module.pipeline.close();
            return;
        }
        module.pipeline.apply((float)(module.contrast.getValue() / 100.0), (float)(module.saturation.getValue() / 100.0), (float)(module.brightness.getValue() / 100.0));
    }
}

