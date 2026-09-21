/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import ru.byazen.event.EventBus;
import ru.byazen.misc.BoxEspSettings;
import ru.byazen.misc.GlowEspSettings;
import ru.byazen.misc.ModelEspSettings;
import ru.byazen.misc.NameTagSettings;
import ru.byazen.misc.WorldBoxSettings;
import ru.byazen.model.esp.EspRelation;
import ru.byazen.model.esp.EspTargetType;
import ru.byazen.module.Module;
import ru.byazen.module.misc.EspFeatureModule;
import ru.byazen.render.ChamsSettings;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.setting.NumberUnit;

public final class EspFeatureRegistry {
    private final EnumMap<EspTargetType, EnumMap<EspRelation, BoxEspSettings>> box2dSettings;
    private final EnumMap<EspTargetType, EnumMap<EspRelation, WorldBoxSettings>> worldBoxSettings;
    private final EnumMap<EspRelation, GlowEspSettings> glowSettings;
    private final EnumMap<EspRelation, ModelEspSettings> modelEspSettings;
    private final EnumMap<EspTargetType, EnumMap<EspRelation, NameTagSettings>> nameTagSettings;
    private final EnumMap<EspRelation, ChamsSettings> chamsSettings;
    private final EnumMap<EspTargetType, EnumMap<EspRelation, List<Module>>> modulesByTarget;
    private final EventBus eventBus;
    private final List<Module> modules;

    public EspFeatureRegistry(EventBus eventBus) {
        this.eventBus = eventBus;
        this.box2dSettings = new EnumMap(EspTargetType.class);
        this.worldBoxSettings = new EnumMap(EspTargetType.class);
        this.glowSettings = new EnumMap(EspRelation.class);
        this.modelEspSettings = new EnumMap(EspRelation.class);
        this.nameTagSettings = new EnumMap(EspTargetType.class);
        this.chamsSettings = new EnumMap(EspRelation.class);
        this.modulesByTarget = new EnumMap(EspTargetType.class);
        this.modules = new ArrayList<Module>();
        for (EspTargetType targetType : EspTargetType.values()) {
            EspRelation[] espRelationArray;
            EnumMap<EspRelation, List<Module>> modulesByRelation = new EnumMap<EspRelation, List<Module>>(EspRelation.class);
            if (targetType == EspTargetType.PLAYERS) {
                espRelationArray = EspRelation.values();
            } else {
                EspRelation[] espRelationArray2 = new EspRelation[1];
                espRelationArray = espRelationArray2;
                espRelationArray2[0] = EspRelation.DEFAULT;
            }
            EspRelation[] relations = espRelationArray;
            for (EspRelation relation : espRelationArray) {
                List<Module> modules = List.copyOf(this.createFeatureModules(targetType, relation));
                modulesByRelation.put(relation, modules);
                this.modules.addAll(modules);
            }
            this.modulesByTarget.put(targetType, modulesByRelation);
        }
    }

    public WorldBoxSettings getWorldBoxSettings(EspTargetType espTargetType, EspRelation espRelation) {
        return (WorldBoxSettings)this.getSettings(this.worldBoxSettings, espTargetType, espRelation);
    }

    public EspRelation getRelation(Module module) {
        for (EnumMap<EspRelation, List<Module>> enumMap : this.modulesByTarget.values()) {
            if (enumMap.getOrDefault((Object)EspRelation.DEFAULT, List.of()).contains(module)) {
                return EspRelation.DEFAULT;
            }
            if (!enumMap.getOrDefault((Object)EspRelation.FRIEND, List.of()).contains(module)) continue;
            return EspRelation.FRIEND;
        }
        return EspRelation.DEFAULT;
    }

    public ModelEspSettings getModelEspSettings(EspRelation espRelation) {
        return this.modelEspSettings.get((Object)espRelation);
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public List<Module> getDefaultModules() {
        ArrayList<Module> arrayList = new ArrayList<Module>();
        for (EnumMap<EspRelation, List<Module>> enumMap : this.modulesByTarget.values()) {
            for (Module module : enumMap.getOrDefault((Object)EspRelation.DEFAULT, List.of())) {
                if (arrayList.contains(module)) continue;
                arrayList.add(module);
            }
        }
        return arrayList;
    }

    public boolean hasEnabledNameTags() {
        for (EnumMap<EspRelation, NameTagSettings> enumMap : this.nameTagSettings.values()) {
            for (NameTagSettings talisman : enumMap.values()) {
                if (!talisman.isEnabled()) continue;
                return true;
            }
        }
        return false;
    }

    private EspFeatureModule createGlowModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "glow"), "Glow ESP", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435-\u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0441\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435-\u043e\u0431\u0432\u043e\u0434\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432")).withKeybind()).value(false).build();
        ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("player_color")).name("Player color")).description("\u0426\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f")).build();
        NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("max_distance")).name("Max distance")).description("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438")).range(4.0, 12.0).defaultValue(8.0).multiplier(16.0).showMarkers().formatter(NumberUnit.BLOCKS).build();
        NumberSetting number = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("radius")).name("Radius")).description("\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f")).range(8.0, 16.0).defaultValue(12.0).formatter(NumberUnit.PIXELS).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        cls0919Module.setSetting(colorSetting);
        cls0919Module.setSetting(numberSetting);
        cls0919Module.setSetting(number);
        this.glowSettings.put(espRelation, new GlowEspSettings(booleanSetting, colorSetting, numberSetting, number));
        return cls0919Module;
    }

    private EspFeatureModule createNameTagModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "nametags"), "Name Tags", "\u041d\u0438\u043a\u0438 \u0438 \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043d\u0430\u0434 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044f\u043c\u0438", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043d\u0438\u043a\u0438 \u043d\u0430\u0434 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044f\u043c\u0438")).withKeybind()).value(false).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        MultiSelectSetting multiSelectSetting = null;
        BooleanSetting toggle = null;
        BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("overlap")).name("Overlap")).description("\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043f\u0435\u0440\u0435\u043a\u0440\u044b\u0432\u0430\u044e\u0449\u0438\u0435\u0441\u044f \u0442\u0435\u0433\u0438")).withKeybind()).value(false).build();
        NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("threshold")).name("Threshold")).description("\u041f\u043e\u0440\u043e\u0433 \u043f\u0435\u0440\u0435\u043a\u0440\u044b\u0442\u0438\u044f \u0434\u043b\u044f \u0441\u043a\u0440\u044b\u0442\u0438\u044f")).range(0.0, 1.0).defaultValue(0.5).precision(2).visibleWhen(toggle2::isEnabled)).build();
        if (espTargetType != EspTargetType.ITEMS) {
            String[] stringArray;
            if (espTargetType == EspTargetType.ENTITIES) {
                String[] stringArray2 = new String[2];
                stringArray2[0] = "Health";
                stringArray = stringArray2;
                stringArray2[1] = "Money";
            } else {
                String[] stringArray3 = new String[4];
                stringArray3[0] = "Health";
                stringArray3[1] = "Items";
                stringArray3[2] = "Sphere";
                stringArray = stringArray3;
                stringArray3[3] = "Talisman";
            }
            String[] stringArray4 = stringArray;
            MultiSelectSetting multi = ((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().id("display")).name("Display")).description("\u0427\u0442\u043e \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0440\u044f\u0434\u043e\u043c \u0441 \u043d\u0438\u043a\u043e\u043c")).options(stringArray4).build();
            multi.getSelectedOptions().add("Health");
            multi.getSelectedOptions().add("Items");
            multi.getSelectedOptions().add("Talisman");
            multiSelectSetting = multi;
            toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("show_enchantments")).name("Show Enchantments")).description("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u044f \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432")).withKeybind()).visibleWhen(() -> multi.getSelectedOptions().contains("Items"))).value(false).build();
            cls0919Module.setSetting(multiSelectSetting);
            cls0919Module.setSetting(toggle);
        }
        cls0919Module.setSetting(toggle2);
        cls0919Module.setSetting(numberSetting);
        this.registerSettings(this.nameTagSettings, espTargetType, espRelation, new NameTagSettings(booleanSetting, multiSelectSetting, toggle, toggle2, numberSetting));
        return cls0919Module;
    }

    private String createModuleId(EspTargetType espTargetType, EspRelation espRelation, String string) {
        if (espTargetType == EspTargetType.PLAYERS && espRelation == EspRelation.FRIEND) {
            String string2 = string;
            return "esp_player_friend_" + string2;
        }
        String string3 = string;
        String string4 = espTargetType.name().toLowerCase();
        return "esp_" + string4 + "_" + string3;
    }

    public boolean hasEnabledGlow() {
        for (GlowEspSettings glowEspSettings : this.glowSettings.values()) {
            if (!glowEspSettings.isEnabled()) continue;
            return true;
        }
        return false;
    }

    public BoxEspSettings getBox2dSettings(EspTargetType espTargetType, EspRelation espRelation) {
        return (BoxEspSettings)this.getSettings(this.box2dSettings, espTargetType, espRelation);
    }

    public boolean hasEnabledWorldBox() {
        for (EnumMap<EspRelation, WorldBoxSettings> enumMap : this.worldBoxSettings.values()) {
            for (WorldBoxSettings dotted : enumMap.values()) {
                if (!dotted.isEnabled()) continue;
                return true;
            }
        }
        return false;
    }

    private EspFeatureModule createModelEspModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "model"), "Model ESP", "3D-\u043c\u043e\u0434\u0435\u043b\u044c \u0438\u043b\u0438 \u0441\u043a\u0435\u043b\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0441\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u041e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0430 3D-\u043c\u043e\u0434\u0435\u043b\u0438/\u0441\u043a\u0435\u043b\u0435\u0442\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432")).withKeybind()).value(false).build();
        ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("type")).name("Type")).description("\u041c\u043e\u0434\u0435\u043b\u044c \u0438\u043b\u0438 \u0441\u043a\u0435\u043b\u0435\u0442")).options("Model", "Skeleton").defaultOption("Model").build();
        ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("color")).name("Color")).description("\u0426\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438/\u0441\u043a\u0435\u043b\u0435\u0442\u0430")).build();
        BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("filled")).name("Filled")).description("\u0417\u0430\u043b\u0438\u0432\u0430\u0442\u044c \u043c\u043e\u0434\u0435\u043b\u044c")).withKeybind()).visibleWhen(() -> "Model".equals(modeSetting.getSelectedOption()))).build();
        ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("filled_color")).name("Filled color")).description("\u0426\u0432\u0435\u0442 \u0437\u0430\u043b\u0438\u0432\u043a\u0438 \u043c\u043e\u0434\u0435\u043b\u0438")).visibleWhen(() -> "Model".equals(modeSetting.getSelectedOption()) && toggle.isEnabled())).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        cls0919Module.setSetting(modeSetting);
        cls0919Module.setSetting(colorSetting);
        cls0919Module.setSetting(toggle);
        cls0919Module.setSetting(colorSetting2);
        this.modelEspSettings.put(espRelation, new ModelEspSettings(booleanSetting, modeSetting, colorSetting, toggle, colorSetting2));
        return cls0919Module;
    }

    public List<Module> getModules(EspTargetType espTargetType, EspRelation espRelation) {
        EnumMap<EspRelation, List<Module>> enumMap = this.modulesByTarget.get((Object)espTargetType);
        if (enumMap == null) {
            return List.of();
        }
        List<Module> list = enumMap.get((Object)espRelation);
        return list == null ? List.of() : list;
    }

    private List<Module> createFeatureModules(EspTargetType espTargetType, EspRelation espRelation) {
        ArrayList<Module> arrayList = new ArrayList<Module>();
        arrayList.add(this.createNameTagModule(espTargetType, espRelation));
        if (espTargetType != EspTargetType.SELF) {
            arrayList.add(this.createBox2dModule(espTargetType, espRelation));
            arrayList.add(this.createWorldBoxModule(espTargetType, espRelation));
            if (espTargetType == EspTargetType.PLAYERS) {
                arrayList.add(this.createChamsModule(espTargetType, espRelation));
                arrayList.add(this.createGlowModule(espTargetType, espRelation));
                arrayList.add(this.createModelEspModule(espTargetType, espRelation));
            }
        }
        return arrayList;
    }

    private EspFeatureModule createWorldBoxModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "worldbox"), "WorldBox", "\u041e\u0431\u044a\u0451\u043c\u043d\u0430\u044f 3D-\u0440\u0430\u043c\u043a\u0430 \u0432\u043e\u043a\u0440\u0443\u0433 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c 3D-\u0440\u0430\u043c\u043a\u0443")).withKeybind()).value(false).build();
        ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("style")).name("Style")).description("\u0421\u0442\u0438\u043b\u044c \u043a\u043e\u043d\u0442\u0443\u0440\u0430 \u0440\u0430\u043c\u043a\u0438")).options("Dotted", "Straight").defaultIndex(1).build();
        ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("color")).name("Color")).description("\u0426\u0432\u0435\u0442 \u0440\u0430\u043c\u043a\u0438")).build();
        NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("scale")).name("Box scale")).description("\u041c\u0430\u0441\u0448\u0442\u0430\u0431 \u0440\u0430\u043c\u043a\u0438")).range(1.0, 10.0).defaultValue(1.0).precision(0).build();
        BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("depth")).name("Depth")).description("\u0423\u0447\u0438\u0442\u044b\u0432\u0430\u0442\u044c \u0433\u043b\u0443\u0431\u0438\u043d\u0443 (\u0441\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b)")).withKeybind()).value(true).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        cls0919Module.setSetting(modeSetting);
        cls0919Module.setSetting(colorSetting);
        cls0919Module.setSetting(numberSetting);
        cls0919Module.setSetting(toggle);
        this.registerSettings(this.worldBoxSettings, espTargetType, espRelation, new WorldBoxSettings(booleanSetting, modeSetting, colorSetting, numberSetting, toggle));
        return cls0919Module;
    }

    public NameTagSettings getNameTagSettings(EspTargetType espTargetType, EspRelation espRelation) {
        return (NameTagSettings)this.getSettings(this.nameTagSettings, espTargetType, espRelation);
    }

    public boolean hasEnabledChams() {
        for (ChamsSettings chamsSettings : this.chamsSettings.values()) {
            if (!chamsSettings.isEnabled()) continue;
            return true;
        }
        return false;
    }

    public ChamsSettings getChamsSettings(EspRelation espRelation) {
        return this.chamsSettings.get((Object)espRelation);
    }

    private EspFeatureModule createChamsModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "chams"), "Chams", "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u044b\u0435/\u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u0435/\u043c\u0435\u0442\u0430\u043b\u043b\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u043e\u0432\u0435\u0440\u043b\u0435\u0438 \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u041e\u0432\u0435\u0440\u043b\u0435\u0439 \u043d\u0430 \u043c\u043e\u0434\u0435\u043b\u044f\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432")).withKeybind()).value(false).build();
        ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("mode")).name("Mode")).description("\u0421\u0442\u0438\u043b\u044c \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438")).options(ChamsSettings.MATERIAL_MODES).defaultOption("Glass").build();
        BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("fill_visible_chams")).name("Fill visible")).description("\u0417\u0430\u043b\u0438\u0432\u0430\u0442\u044c \u0432\u0438\u0434\u0438\u043c\u044b\u0435 \u0447\u0430\u0441\u0442\u0438 \u043c\u043e\u0434\u0435\u043b\u0438")).withKeybind()).value(true).build();
        ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("visible_color")).name("Visible color")).description("\u0426\u0432\u0435\u0442 \u0432\u0438\u0434\u0438\u043c\u044b\u0445 \u0447\u0430\u0441\u0442\u0435\u0439")).visibleWhen(toggle::isEnabled)).build();
        BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("fill_hidden_chams")).name("Fill hidden")).description("\u0417\u0430\u043b\u0438\u0432\u0430\u0442\u044c \u0441\u043a\u0440\u044b\u0442\u044b\u0435 \u0447\u0430\u0441\u0442\u0438 \u043c\u043e\u0434\u0435\u043b\u0438")).withKeybind()).value(true).build();
        ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("hidden_color")).name("Hidden color")).description("\u0426\u0432\u0435\u0442 \u0441\u043a\u0440\u044b\u0442\u044b\u0445 \u0447\u0430\u0441\u0442\u0435\u0439")).visibleWhen(toggle2::isEnabled)).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        cls0919Module.setSetting(modeSetting);
        cls0919Module.setSetting(toggle);
        cls0919Module.setSetting(colorSetting);
        cls0919Module.setSetting(toggle2);
        cls0919Module.setSetting(colorSetting2);
        this.chamsSettings.put(espRelation, new ChamsSettings(booleanSetting, modeSetting, toggle, colorSetting, toggle2, colorSetting2));
        return cls0919Module;
    }

    private <T> void registerSettings(EnumMap<EspTargetType, EnumMap<EspRelation, T>> enumMap, EspTargetType espTargetType2, EspRelation espRelation, T t) {
        EnumMap enumMap2 = enumMap.computeIfAbsent(espTargetType2, espTargetType -> new EnumMap(EspRelation.class));
        if (espTargetType2 == EspTargetType.PLAYERS) {
            enumMap2.put(espRelation, t);
            return;
        }
        for (EspRelation espRelation2 : EspRelation.values()) {
            enumMap2.put(espRelation2, t);
        }
    }

    private EspFeatureModule createBox2dModule(EspTargetType espTargetType, EspRelation espRelation) {
        EspFeatureModule cls0919Module = new EspFeatureModule(this.eventBus, this.createModuleId(espTargetType, espRelation, "box2d"), "2D ESP", "2D-\u0431\u043e\u043a\u0441 \u0441\u043e \u0448\u043a\u0430\u043b\u0430\u043c\u0438 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f \u0438 \u0431\u0440\u043e\u043d\u0438", this.getCategoryTitle(espTargetType, espRelation));
        BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("enabled")).name("Enabled")).description("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 2D-\u0431\u043e\u043a\u0441\u043e\u043c")).withKeybind()).value(false).build();
        BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("box")).name("Box")).description("\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0440\u0430\u043c\u043a\u0443 \u0432\u043e\u043a\u0440\u0443\u0433 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438")).withKeybind()).value(true).build();
        ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("box_type")).name("Box Type")).description("\u0424\u043e\u0440\u043c\u0430 \u0440\u0430\u043c\u043a\u0438")).options("Rectangle", "Corner").defaultIndex(0).visibleWhen(toggle::isEnabled)).build();
        ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("box_color")).name("Box Color")).description("\u0426\u0432\u0435\u0442 \u0440\u0430\u043c\u043a\u0438")).selectedIndex(4).visibleWhen(toggle::isEnabled)).build();
        BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("health_bar")).name("Health Bar")).description("\u0428\u043a\u0430\u043b\u0430 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f \u0441\u043b\u0435\u0432\u0430 \u043e\u0442 \u0431\u043e\u043a\u0441\u0430")).withKeybind()).value(false).build();
        ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("health_color")).name("Health Color")).description("\u0426\u0432\u0435\u0442 \u0448\u043a\u0430\u043b\u044b \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f")).selectedIndex(3).visibleWhen(toggle2::isEnabled)).build();
        BooleanSetting toggle3 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("armor_bar")).name("Armor Bar")).description("\u0428\u043a\u0430\u043b\u0430 \u0431\u0440\u043e\u043d\u0438 \u043f\u043e\u0434 \u0431\u043e\u043a\u0441\u043e\u043c")).withKeybind()).value(false).build();
        ColorSetting colorSetting3 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("bar_color")).name("Bar Color")).description("\u0426\u0432\u0435\u0442 \u0448\u043a\u0430\u043b\u044b \u0431\u0440\u043e\u043d\u0438")).selectedIndex(5).visibleWhen(toggle3::isEnabled)).build();
        BooleanSetting toggle4 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("partner_items")).name("Partner Items")).description("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b Server Helper \u043f\u043e\u0434 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044c\u044e \u0441 % \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u0438")).withKeybind()).value(false).build();
        cls0919Module.setBooleanSetting(booleanSetting);
        cls0919Module.setSetting(toggle);
        cls0919Module.setSetting(modeSetting);
        cls0919Module.setSetting(colorSetting);
        cls0919Module.setSetting(toggle2);
        cls0919Module.setSetting(colorSetting2);
        cls0919Module.setSetting(toggle3);
        cls0919Module.setSetting(colorSetting3);
        cls0919Module.setSetting(toggle4);
        this.registerSettings(this.box2dSettings, espTargetType, espRelation, new BoxEspSettings(booleanSetting, toggle, modeSetting, colorSetting, toggle2, colorSetting2, toggle3, colorSetting3, toggle4));
        return cls0919Module;
    }

    public EspTargetType getTargetType(Module module) {
        for (EspTargetType espTargetType : this.modulesByTarget.keySet()) {
            for (List<Module> list : this.modulesByTarget.get((Object)espTargetType).values()) {
                if (!list.contains(module)) continue;
                return espTargetType;
            }
        }
        return null;
    }

    public boolean hasEnabledModelEsp() {
        for (ModelEspSettings skeleton : this.modelEspSettings.values()) {
            if (!skeleton.isEnabled()) continue;
            return true;
        }
        return false;
    }

    public boolean hasEnabledBox2d() {
        for (EnumMap<EspRelation, BoxEspSettings> enumMap : this.box2dSettings.values()) {
            for (BoxEspSettings rectangle : enumMap.values()) {
                if (!rectangle.isEnabled()) continue;
                return true;
            }
        }
        return false;
    }

    public GlowEspSettings getGlowSettings(EspRelation espRelation) {
        return this.glowSettings.get((Object)espRelation);
    }

    private String getCategoryTitle(EspTargetType espTargetType, EspRelation espRelation) {
        if (espTargetType == EspTargetType.PLAYERS && espRelation == EspRelation.FRIEND) {
            return "Friends";
        }
        return espTargetType.getTitle();
    }

    private <T> T getSettings(EnumMap<EspTargetType, EnumMap<EspRelation, T>> enumMap, EspTargetType espTargetType, EspRelation espRelation) {
        EnumMap<EspRelation, T> enumMap2 = enumMap.get((Object)espTargetType);
        return enumMap2 == null ? null : (T)enumMap2.get((Object)espRelation);
    }
}

