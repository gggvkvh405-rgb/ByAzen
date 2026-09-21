/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.byazen.module.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_1113;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.setting.NumberUnit;
import ru.byazen.util.CustomSoundLibrary;

public final class CustomSoundsModule
extends Module
implements ConfigSerializable {
    static volatile CustomSoundsModule customSoundsModule2;
    private final Map<String, String> displayNames = new LinkedHashMap<String, String>();
    private final Map<String, ModeSetting> soundSettings = new LinkedHashMap<String, ModeSetting>();
    private final Map<String, Map<String, String>> soundFilesByCategory = new LinkedHashMap<String, Map<String, String>>();
    private final Map<String, NumberSetting> volumeSettings = new LinkedHashMap<String, NumberSetting>();
    private final MultiSelectSetting categories;
    private final BooleanSetting enabledSetting;
    private final CustomSoundLibrary soundLibrary;

    public CustomSoundsModule(EventBus eventBus) {
        super(eventBus, "custom_sounds", "Custom Sounds", "\u0417\u0430\u043c\u0435\u043d\u0430 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432 \u0441\u0432\u043e\u0438\u043c\u0438 \u0444\u0430\u0439\u043b\u0430\u043c\u0438", ModuleCategory.valueOf("MISC"), new String[0]);
        customSoundsModule2 = this;
        this.soundLibrary = this.createSoundLibrary();
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435 \u0437\u0432\u0443\u043a\u0438 \u0432\u043c\u0435\u0441\u0442\u043e \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0445").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting categoriesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("\u041d\u0435\u0442 \u0444\u0430\u0439\u043b\u043e\u0432").selectAll(false).optionListEnabled(false).name("Categories").id("categories").description("\u041a\u0430\u043a\u0438\u0435 \u0437\u0432\u0443\u043a\u0438 \u043f\u043e\u0434\u043c\u0435\u043d\u044f\u0442\u044c")).build();
        categoriesSetting.setOptions(new String[0]);
        this.categories = categoriesSetting;
        this.registerSetting(categoriesSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.refreshSoundSettings());
    }

    private CustomSoundLibrary createSoundLibrary() {
        ByAzenClient client = ByAzenClient.getInstance();
        if (client == null || client.getConfigDirectory() == null) {
            return null;
        }
        try {
            return new CustomSoundLibrary(client.getConfigDirectory().resolve("client").resolve("sounds"));
        }
        catch (IOException ignored) {
            return null;
        }
    }

    private void refreshSoundSettings() {
        String[] stringArray;
        if (this.soundLibrary == null) {
            return;
        }
        this.soundLibrary.update();
        for (String string : this.soundLibrary.getList()) {
            if (this.displayNames.containsKey(string)) continue;
            String string2 = this.uniqueDisplayName(CustomSoundsModule.formatDisplayName(string));
            this.displayNames.put(string, string2);
            this.registerSoundSettings(string, string2);
        }
        ArrayList<String> availableCategories = new ArrayList<String>();
        for (Map.Entry<String, String> entry : this.displayNames.entrySet()) {
            String soundId = entry.getKey();
            Map<String, String> files = CustomSoundsModule.buildDisplayNameMap(this.soundLibrary.process5(soundId));
            this.soundFilesByCategory.put(soundId, files);
            ModeSetting modeSetting = this.soundSettings.get(soundId);
            if (modeSetting != null) {
                modeSetting.setOptions(files.keySet().toArray(new String[0]));
            }
            if (files.isEmpty()) continue;
            availableCategories.add(entry.getValue());
        }
        if (availableCategories.isEmpty()) {
            String[] stringArray2;
            stringArray = stringArray2 = new String[1];
            stringArray2[0] = "\u041d\u0435\u0442 \u0444\u0430\u0439\u043b\u043e\u0432";
        } else {
            stringArray = availableCategories.toArray(new String[0]);
        }
        this.categories.setOptions(stringArray);
    }

    private void registerSoundSettings(String soundId, String displayName) {
        String settingId = CustomSoundsModule.sanitizeSettingId(soundId);
        Map<String, String> files = CustomSoundsModule.buildDisplayNameMap(this.soundLibrary.process5(soundId));
        this.soundFilesByCategory.put(soundId, files);
        String defaultFile = files.isEmpty() ? "" : files.keySet().iterator().next();
        ModeSetting soundSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("sound_" + settingId)).name(displayName + " Sound")).description("\u0424\u0430\u0439\u043b \u0434\u043b\u044f: " + displayName)).options(files.keySet().toArray(new String[0])).defaultOption(defaultFile).visibleWhen(() -> this.isCategoryEnabled(soundId))).build();
        NumberSetting volumeSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("volume_" + settingId)).name(displayName + " Volume")).description("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c: " + displayName)).range(1.0, 100.0).defaultValue(100.0).precision(0).formatter(NumberUnit.PERCENT).visibleWhen(() -> this.isCategoryEnabled(soundId))).build();
        this.soundSettings.put(soundId, soundSetting);
        this.volumeSettings.put(soundId, volumeSetting);
        this.registerSetting(soundSetting);
        this.registerSetting(volumeSetting);
    }

    private String uniqueDisplayName(String baseName) {
        if (!this.displayNames.containsValue(baseName)) {
            return baseName;
        }
        int suffix = 2;
        while (this.displayNames.containsValue(baseName + " [" + suffix + "]")) {
            ++suffix;
        }
        return baseName + " [" + suffix + "]";
    }

    private static String sanitizeSettingId(String soundId) {
        return soundId.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "_");
    }

    private static String formatDisplayName(String soundId) {
        String[] parts = soundId.split("\\.");
        String label = parts.length >= 2 ? parts[parts.length - 2] + " " + parts[parts.length - 1] : parts[parts.length - 1];
        return CustomSoundsModule.titleCase(label.replace('_', ' '));
    }

    private static String titleCase(String value) {
        StringBuilder builder = new StringBuilder(value.length());
        boolean capitalizeNext = true;
        for (int index = 0; index < value.length(); ++index) {
            char character = value.charAt(index);
            if (capitalizeNext && Character.isLetter(character)) {
                builder.append(Character.toUpperCase(character));
                capitalizeNext = false;
            } else {
                builder.append(character);
            }
            if (character != ' ' && character != '-') continue;
            capitalizeNext = true;
        }
        return builder.toString();
    }

    private boolean isCategoryEnabled(String soundId) {
        String displayName = this.displayNames.get(soundId);
        return displayName != null && this.categories.getSelectedOptions().contains(displayName);
    }

    private String resolveReplacementFile(String soundId) {
        String selected;
        Map<String, String> files = this.soundFilesByCategory.get(soundId);
        ModeSetting modeSetting = this.soundSettings.get(soundId);
        if (files == null || files.isEmpty()) {
            return null;
        }
        if (modeSetting != null && (selected = files.get(modeSetting.getSelectedOption())) != null && this.soundLibrary.process7(selected)) {
            return selected;
        }
        List<String> available = this.soundLibrary.process5(soundId);
        return available.isEmpty() ? null : available.get(0);
    }

    private float volumeFor(String soundId) {
        NumberSetting volumeSetting = this.volumeSettings.get(soundId);
        double percent = volumeSetting == null ? 100.0 : volumeSetting.getValue();
        return (float)Math.max(0.0, Math.min(1.0, percent / 100.0));
    }

    public static boolean compute8(class_1113 instance, long seed) {
        CustomSoundsModule module = customSoundsModule2;
        if (module == null || module.soundLibrary == null || instance == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        class_2960 soundId = instance.method_4775();
        String normalizedId = "minecraft".equals(soundId.method_12836()) ? soundId.method_12832() : soundId.toString();
        String string = normalizedId;
        if (!module.soundLibrary.process2(normalizedId) || !module.isCategoryEnabled(normalizedId)) {
            return false;
        }
        String replacement = module.resolveReplacementFile(normalizedId);
        if (replacement == null) {
            return false;
        }
        float volume = module.volumeFor(normalizedId);
        class_310 client = class_310.method_1551();
        class_243 cameraPos = client.field_1773.method_19418().method_71156();
        boolean positional = cameraPos != null && !instance.method_4787() && instance.method_4777() != class_1113.class_1114.field_5478;
        boolean bl = positional;
        if (positional) {
            module.soundLibrary.process6(replacement, instance.method_4784(), instance.method_4779(), instance.method_4778(), cameraPos.field_1352, cameraPos.field_1351, cameraPos.field_1350, volume, seed);
        } else {
            module.soundLibrary.process(replacement, volume, seed);
        }
        return true;
    }

    private static Map<String, String> buildDisplayNameMap(List<String> files) {
        LinkedHashMap<String, String> mapped = new LinkedHashMap<String, String>();
        for (String file : files) {
            int slash = file.indexOf(47);
            String baseName = slash >= 0 ? file.substring(slash + 1) : file;
            Object displayName = baseName;
            int suffix = 2;
            while (mapped.containsKey(displayName)) {
                displayName = baseName + " (" + suffix++ + ")";
            }
            mapped.put((String)displayName, file);
        }
        return mapped;
    }
}

