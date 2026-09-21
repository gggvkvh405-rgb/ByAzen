/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.ArrayList;
import java.util.List;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.KeybindDescriptor;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleManager;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.Setting;
import ru.byazen.setting.SettingKeybind;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.ModuleKeybindGroup;

public final class ModuleKeybindEntryFactory {
    private ModuleKeybindEntryFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static List<KeybindDescriptor> process(Module module) {
        ArrayList<KeybindDescriptor> arrayList = new ArrayList<KeybindDescriptor>();
        for (Setting setting : module.getSettings()) {
            SettingKeybind configSerializable;
            if (setting instanceof BindSetting) {
                BindSetting bindSetting = (BindSetting)setting;
                arrayList.add(KeybindDescriptor.process2(bindSetting));
            }
            if (!setting.hasKeybind() || (configSerializable = setting.getKeybind()) == null) continue;
            arrayList.add(KeybindDescriptor.process3(configSerializable));
        }
        return arrayList;
    }

    public static List<ModuleKeybindGroup> process2(ModuleManager moduleManager) {
        ArrayList<ModuleKeybindGroup> arrayList = new ArrayList<ModuleKeybindGroup>();
        ArrayList<Module> arrayList2 = new ArrayList<Module>(moduleManager.getModules());
        EspFeatureRegistry espFeatures = ByAzenClient.getEspFeatureRegistry();
        if (espFeatures != null) {
            arrayList2.addAll(espFeatures.getModules());
        }
        for (Module module : arrayList2) {
            List<KeybindDescriptor> list = ModuleKeybindEntryFactory.process(module);
            if (list.isEmpty()) continue;
            arrayList.add(new ModuleKeybindGroup(module, list));
        }
        return arrayList;
    }
}

