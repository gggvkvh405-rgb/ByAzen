/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class CameraModule
extends Module
implements ConfigSerializable {
    private static final float CLIP_SCALE = 3.0f;
    private static final float SMOOTH_SPEED = 12.0f;
    private static volatile CameraModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting clip;
    private final BooleanSetting smooth;
    private float smoothedDistance;

    public CameraModule(EventBus eventBus) {
        super(eventBus, "camera", "Camera", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043a\u0430\u043c\u0435\u0440\u044b \u043e\u0442 \u0442\u0440\u0435\u0442\u044c\u0435\u0433\u043e \u043b\u0438\u0446\u0430", ModuleCategory.valueOf("RENDER"), "camera", "\u043a\u0430\u043c\u0435\u0440\u0430");
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043a\u0430\u043c\u0435\u0440\u044b").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.clip = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Clip").id("clip").description("\u0418\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u0442\u0435\u043d\u044b")).build();
        this.registerSetting(this.clip);
        this.smooth = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Smooth").id("smooth").description("\u041f\u043b\u0430\u0432\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c")).build();
        this.registerSetting(this.smooth);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled2() {
        CameraModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public static boolean isEnabled3() {
        CameraModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.smooth.isEnabled();
    }

    public static boolean isEnabled4() {
        CameraModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.clip.isEnabled();
    }

    public static void tick() {
        CameraModule module = instance;
        if (module != null) {
            module.smoothedDistance = Float.NaN;
        }
    }

    public static float compute(float distance) {
        CameraModule module = instance;
        if (module == null) {
            return distance;
        }
        if (Float.isNaN(module.smoothedDistance)) {
            module.smoothedDistance = 0.0f;
        }
        module.smoothedDistance = FrameInterpolator.lerpTowards(module.smoothedDistance, distance, 12.0f);
        return Math.max(distance / 3.0f, module.smoothedDistance);
    }
}

