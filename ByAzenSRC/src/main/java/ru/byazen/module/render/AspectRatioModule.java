/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.render;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.AspectRatioEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public final class AspectRatioModule
extends Module
implements ConfigSerializable {
    private final ModeSetting ratio;
    private final NumberSetting number;
    private final BooleanSetting enabledSetting;
    private static final String string = "Custom";

    private void onGame(AspectRatioEvent gameEvent) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        float f = switch (this.ratio.getSelectedOption()) {
            case "21:9" -> 2.3333333f;
            case "16:10" -> 1.6f;
            case "16:9" -> 1.7777778f;
            case "4:3" -> 1.3333334f;
            default -> this.number.getFloatValue();
        };
        gameEvent.setAspectRatio(f);
    }

    @Override
    protected void initialize() {
        this.listen(AspectRatioEvent.class, this::onGame);
    }

    public AspectRatioModule(EventBus eventBus) {
        super(eventBus, "aspect_ratio", "Aspect Ratio", "\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0441\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u044f \u0441\u0442\u043e\u0440\u043e\u043d", ModuleCategory.valueOf("RENDER"), new String[0]);
        NumberSetting numberSetting;
        ModeSetting modeSetting;
        BooleanSetting booleanSetting;
        this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0441\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u044f \u0441\u0442\u043e\u0440\u043e\u043d").withKeybind().toggle()).build();
        this.registerSetting(booleanSetting);
        this.ratio = modeSetting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(string, "21:9", "16:10", "16:9", "4:3").defaultOption(string).name("Ratio").id("ratio").description("\u0421\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u0435 \u0441\u0442\u043e\u0440\u043e\u043d").aliases("ratio", "\u0441\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u0435")).build();
        this.registerSetting(modeSetting);
        this.number = numberSetting = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 2.0).defaultValue(1.5).multiplier(1.0).precision(1).animationSpeed(20.0f).name("Value").id("value").description("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0441\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u044f").aliases("value", "\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435").visibleWhen(() -> string.equals(this.ratio.getSelectedOption()))).build();
        this.registerSetting(numberSetting);
    }
}

