/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.misc;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.Setting;

public final class EspFeatureModule
extends Module
implements ConfigSerializable {
    private final String group;

    public EspFeatureModule(EventBus eventBus, String id, String displayName, String description, String group) {
        super(eventBus, id, displayName, description, ModuleCategory.valueOf("RENDER"), new String[0]);
        this.group = group;
    }

    @Override
    protected void initialize() {
    }

    public void setBooleanSetting(BooleanSetting setting) {
        this.registerSetting(this.registerToggle(setting));
    }

    public void setSetting(Setting setting) {
        this.registerSetting(setting);
    }

    public String getString() {
        return this.group;
    }
}

