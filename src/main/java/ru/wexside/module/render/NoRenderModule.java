/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 */
package ru.wexside.module.render;

import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.OverlayRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class NoRenderModule
extends Module
implements ConfigSerializable {
    static final String DYNAMIC_FOV = "Dynamic-FOV";
    static final String FLUID_ZOOM = "Fluid-Zoom";
    static final String FIRE_OVERLAY = "Fire-Overlay";
    static final String SCOREBOARD = "Scoreboard";
    static final String BLOCK_OVERLAY = "Block-Overlay";
    static final String TOTEM_OVERLAY = "Totem-Overlay";
    static final String GLOW = "Glow";
    static final String CAMERA_HURT = "Camera-Hurt";
    static final String VIEW_BOBBING = "View-Bobbing";
    static final String EFFECTS_ICONS = "Effects-Icons";
    static final String BAD_EFFECTS = "Bad-Effects";
    static final String BOSS_BAR = "Boss-Bar";
    static final String UNDER_LAVA = "Under-Lava";
    static final String WORLD_LAVA = "World-Lava";
    private static volatile NoRenderModule instance;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting remove;
    private boolean lavaHidden;

    public NoRenderModule(EventBus eventBus) {
        super(eventBus, "no_render", "No Render", "\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432 \u0440\u0435\u043d\u0434\u0435\u0440\u0430", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432 \u0440\u0435\u043d\u0434\u0435\u0440\u0430").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting removeSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(FIRE_OVERLAY, BLOCK_OVERLAY, CAMERA_HURT, TOTEM_OVERLAY, EFFECTS_ICONS, BAD_EFFECTS, BOSS_BAR, SCOREBOARD, GLOW, FLUID_ZOOM, UNDER_LAVA, WORLD_LAVA, VIEW_BOBBING, DYNAMIC_FOV).selectAll(false).optionListEnabled(false).name("Remove").id("remove").description("\u041e\u0442\u043a\u043b\u044e\u0447\u0430\u0435\u043c\u044b\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u044b")).build();
        removeSetting.setOptions(new String[0]);
        this.remove = removeSetting;
        this.registerSetting(removeSetting);
    }

    @Override
    protected void initialize() {
        this.listen(OverlayRenderEvent.class, this::onOverlay);
        this.listen(ClientTickEvent.class, this::onTick);
    }

    public static boolean isEnabled() {
        return NoRenderModule.compute(DYNAMIC_FOV);
    }

    public static boolean isEnabled2() {
        return NoRenderModule.compute(BAD_EFFECTS);
    }

    public static boolean isViewBobbingDisabled() {
        return NoRenderModule.compute(VIEW_BOBBING);
    }

    public static boolean isFluidZoomDisabled() {
        return NoRenderModule.compute(FLUID_ZOOM);
    }

    public static boolean isUnderLavaDisabled() {
        return NoRenderModule.compute(UNDER_LAVA);
    }

    public static boolean isWorldLavaDisabled() {
        return NoRenderModule.compute(WORLD_LAVA);
    }

    public static boolean compute(String option) {
        NoRenderModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.remove.getSelectedOptions().contains(option);
    }

    private void onTick(ClientTickEvent event) {
        boolean hideLava;
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        boolean bl = hideLava = this.enabledSetting.isEnabled() && this.remove.getSelectedOptions().contains(WORLD_LAVA);
        if (hideLava != this.lavaHidden) {
            if (client.field_1769 != null) {
                client.field_1769.method_3279();
            }
            this.lavaHidden = hideLava;
        }
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        List<String> selected = this.remove.getSelectedOptions();
        if (selected.contains(BAD_EFFECTS)) {
            this.stripEffect(client.field_1724, (class_6880<class_1291>)class_1294.field_5916);
            this.stripEffect(client.field_1724, (class_6880<class_1291>)class_1294.field_5919);
            this.stripEffect(client.field_1724, (class_6880<class_1291>)class_1294.field_38092);
        }
        if (selected.contains(GLOW)) {
            for (class_1297 entity : client.field_1687.method_18112()) {
                if (!entity.method_5851()) continue;
                entity.method_5834(false);
            }
        }
    }

    private void stripEffect(class_746 player, class_6880<class_1291> effect) {
        if (player.method_6059(effect)) {
            player.method_6016(effect);
        }
    }

    private void onOverlay(OverlayRenderEvent event) {
        String option;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        switch (event.type()) {
            default: {
                throw new MatchException(null, null);
            }
            case FIRE: {
                option = FIRE_OVERLAY;
                break;
            }
            case BLOCK: {
                option = BLOCK_OVERLAY;
                break;
            }
            case CAMERA_HURT: {
                option = CAMERA_HURT;
                break;
            }
            case TOTEM: {
                option = TOTEM_OVERLAY;
                break;
            }
            case STATUS_EFFECTS: {
                option = EFFECTS_ICONS;
                break;
            }
            case BOSS_BAR: {
                option = BOSS_BAR;
                break;
            }
            case SCOREBOARD: {
                option = SCOREBOARD;
            }
        }
        if (option != null && this.remove.getSelectedOptions().contains(option)) {
            event.update();
        }
    }
}

