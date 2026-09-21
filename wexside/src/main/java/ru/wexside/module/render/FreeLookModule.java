/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_5498
 *  net.minecraft.class_746
 */
package ru.wexside.module.render;

import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class FreeLookModule
extends Module
implements ConfigSerializable {
    private static volatile FreeLookModule instance;
    private final BooleanSetting enabledSetting;
    private boolean wasEnabled;
    private boolean looking;
    private float yaw;
    private float pitch;
    private class_5498 savedPerspective;

    public FreeLookModule(EventBus eventBus) {
        super(eventBus, "free_look", "Free Look", "\u0421\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0439 \u043e\u0431\u0437\u043e\u0440", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0439 \u043e\u0431\u0437\u043e\u0440").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.tickLook());
        this.listen(WorldRenderEvent.class, event -> this.applyPerspective());
    }

    public static boolean isEnabled() {
        FreeLookModule module = instance;
        return module != null && module.looking;
    }

    public static float getFloatType() {
        FreeLookModule module = instance;
        return module == null ? 0.0f : module.pitch;
    }

    public static float getFloatType2() {
        FreeLookModule module = instance;
        return module == null ? 0.0f : module.yaw;
    }

    public static void handle(double deltaX, double deltaY) {
        FreeLookModule module = instance;
        if (module == null) {
            return;
        }
        module.yaw += (float)(deltaX * 0.15);
        module.pitch = class_3532.method_15363((float)(module.pitch + (float)(deltaY * 0.15)), (float)-90.0f, (float)90.0f);
    }

    private void applyPerspective() {
        if (!this.looking) {
            return;
        }
        class_310.method_1551().field_1690.method_31043(class_5498.field_26665);
    }

    private void tickLook() {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (!this.enabledSetting.isEnabled()) {
            if (this.wasEnabled) {
                this.stopLooking();
                this.wasEnabled = false;
            }
            return;
        }
        this.wasEnabled = true;
        if (player == null || client.field_1687 == null) {
            this.looking = false;
            return;
        }
        if (!this.looking) {
            this.yaw = player.method_36454();
            this.pitch = player.method_36455();
            this.savedPerspective = client.field_1690.method_31044();
            this.looking = true;
        }
    }

    private void stopLooking() {
        if (this.savedPerspective != null) {
            class_310.method_1551().field_1690.method_31043(this.savedPerspective);
        }
        this.looking = false;
        this.savedPerspective = null;
    }
}

