/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140
 *  net.minecraft.class_1144
 *  net.minecraft.class_124
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10609
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_2583
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 */
package ru.wexside.module.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_1144;
import net.minecraft.class_124;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BlockedSoundList;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.SoundMapAccessor;
import ru.wexside.misc.SoundSystemAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class SoundRemoverModule
extends Module
implements ConfigSerializable {
    static volatile SoundRemoverModule soundRemoverModule2;
    private static final Map<String, String[]> SOUND_GROUPS;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting groups;
    private final BooleanSetting logSounds;
    private final Set<String> loggedSoundIds = new HashSet<String>();
    private String lastSyncSignature;

    public SoundRemoverModule(EventBus eventBus) {
        super(eventBus, "sound_remover", "Sound Remover", "\u0417\u0430\u0433\u043b\u0443\u0448\u0430\u0435\u0442 \u043b\u044e\u0431\u044b\u0435 \u0437\u0432\u0443\u043a\u0438 \u043f\u043e \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438: \u0433\u0440\u0443\u043f\u043f\u044b \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0445 + \u043a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435 \u0441\u0435\u0440\u0432\u0435\u0440\u043d\u044b\u0435", ModuleCategory.valueOf("MISC"), new String[0]);
        soundRemoverModule2 = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0417\u0430\u0433\u043b\u0443\u0448\u0430\u0442\u044c \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0437\u0432\u0443\u043a\u0438").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting groupsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("\u0412\u0437\u0440\u044b\u0432\u044b", "\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0438", "\u041e\u043f\u044b\u0442 \u0438 \u0443\u0440\u043e\u0432\u043d\u0438", "\u0423\u0440\u043e\u043d", "\u0421\u043c\u0435\u0440\u0442\u044c", "\u042d\u043d\u0434\u0435\u0440 \u0414\u0440\u0430\u043a\u043e\u043d", "\u0412\u0430\u0440\u0434\u0435\u043d", "\u0412\u0438\u0437\u0435\u0440", "\u0413\u0430\u0441\u0442/\u043e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0448\u0430\u0440", "\u041c\u043e\u043b\u043d\u0438\u0438 \u0438 \u0433\u0440\u043e\u0437\u0430", "\u041f\u043e\u0433\u043e\u0434\u0430 \u0438 \u0434\u043e\u0436\u0434\u044c", "\u0428\u0430\u0433\u0438", "\u0418\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441", "\u0413\u043e\u043b\u043e\u0441\u0430 \u043c\u043e\u0431\u043e\u0432", "\u0414\u0432\u0435\u0440\u0438 \u0438 \u043b\u044e\u043a\u0438", "\u0421\u0443\u043d\u0434\u0443\u043a\u0438/\u0445\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0430", "\u041f\u043e\u0440\u0442\u0430\u043b\u044b").selectAll(false).optionListEnabled(false).name("Groups").id("groups").description("\u0413\u0440\u0443\u043f\u043f\u044b \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432 \u0434\u043b\u044f \u0437\u0430\u0433\u043b\u0443\u0448\u0435\u043d\u0438\u044f").aliases("groups", "\u0433\u0440\u0443\u043f\u043f\u044b")).build();
        groupsSetting.setOptions(new String[0]);
        this.groups = groupsSetting;
        this.registerSetting(groupsSetting);
        this.logSounds = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Log sounds").id("log_sounds").description("\u041f\u0435\u0447\u0430\u0442\u0430\u0442\u044c \u0432 \u0447\u0430\u0442 \u043a\u0430\u0436\u0434\u044b\u0439 \u0441\u043b\u044b\u0448\u0438\u043c\u044b\u0439 \u0437\u0432\u0443\u043a \u0441 \u043a\u043d\u043e\u043f\u043a\u043e\u0439 [\u0437\u0430\u0433\u043b\u0443\u0448\u0438\u0442\u044c] \u2014 \u0434\u043b\u044f \u043f\u043e\u0438\u0441\u043a\u0430 id \u043a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0445 \u0441\u0435\u0440\u0432\u0435\u0440\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432")).build();
        this.registerSetting(this.logSounds);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        String enabledFlag = this.enabledSetting.isEnabled() ? "1" : "0";
        String signature = enabledFlag + "|" + String.join((CharSequence)",", this.groups.getSelectedOptions());
        if (signature.equals(this.lastSyncSignature)) {
            return;
        }
        this.lastSyncSignature = signature;
        if (this.enabledSetting.isEnabled()) {
            this.muteActiveSounds();
        }
    }

    private void muteActiveSounds() {
        ArrayList<class_1113> instances;
        class_310 client = class_310.method_1551();
        class_1144 soundManager = client.method_1483();
        if (!(soundManager instanceof SoundSystemAccessor)) {
            return;
        }
        SoundSystemAccessor callback54 = (SoundSystemAccessor)soundManager;
        class_1140 soundEngine = callback54.getSoundSystem();
        if (!(soundEngine instanceof SoundMapAccessor)) {
            return;
        }
        SoundMapAccessor callback38 = (SoundMapAccessor)soundEngine;
        Map<class_1113, ?> activeSounds = callback38.getMap();
        if (activeSounds == null || activeSounds.isEmpty()) {
            return;
        }
        try {
            instances = new ArrayList<class_1113>();
            for (class_1113 key : activeSounds.keySet()) {
                if (!(key instanceof class_1113)) continue;
                class_1113 instance = key;
                instances.add(instance);
            }
        }
        catch (RuntimeException ignored) {
            return;
        }
        HashSet<class_2960> blockedIds = new HashSet<class_2960>();
        for (class_1113 instance : instances) {
            class_2960 id = instance.method_4775();
            if (id == null || !SoundRemoverModule.shouldBlock(id)) continue;
            blockedIds.add(id);
        }
        for (class_2960 id : blockedIds) {
            soundManager.method_4875(id, null);
        }
    }

    public static void handle(class_2960 soundId, boolean muted) {
        SoundRemoverModule module = soundRemoverModule2;
        if (module == null || soundId == null || !module.enabledSetting.isEnabled()) {
            return;
        }
        if (!module.logSounds.isEnabled()) {
            module.loggedSoundIds.clear();
            return;
        }
        String normalized = soundId.toString().toLowerCase(Locale.ROOT);
        if (!module.loggedSoundIds.add(normalized)) {
            return;
        }
        class_5250 message = SoundRemoverModule.buildLogMessage(soundId.toString(), muted);
        class_310.method_1551().execute(() -> ClientChat.send((class_2561)message));
    }

    public static boolean compute4(class_2960 soundId) {
        SoundRemoverModule module = soundRemoverModule2;
        if (module == null || soundId == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        String normalized = soundId.toString().toLowerCase(Locale.ROOT);
        if (SoundRemoverModule.matchesSelectedGroups(module.groups.getSelectedOptions(), normalized)) {
            return true;
        }
        BlockedSoundList customBlocked = WexSideClient.getBlockedSoundList();
        return customBlocked != null && customBlocked.contains(normalized);
    }

    private static boolean matchesSelectedGroups(List<String> groups, String soundId) {
        if (groups == null || groups.isEmpty()) {
            return false;
        }
        for (String group : groups) {
            if (!SoundRemoverModule.matchesGroup(group, soundId)) continue;
            return true;
        }
        return false;
    }

    private static boolean matchesGroup(String group, String soundId) {
        String[] fragments = SOUND_GROUPS.get(group);
        return fragments != null && SoundRemoverModule.containsAny(soundId, fragments);
    }

    private static boolean containsAny(String soundId, String ... fragments) {
        for (String fragment : fragments) {
            if (!soundId.contains(fragment)) continue;
            return true;
        }
        return false;
    }

    private static class_5250 buildLogMessage(String soundId, boolean muted) {
        class_5250 message = class_2561.method_43470((String)soundId).method_27692(class_124.field_1080);
        message.method_10852((class_2561)class_2561.method_43470((String)" "));
        if (muted) {
            message.method_10852((class_2561)SoundRemoverModule.clickableText("[\u0432\u0435\u0440\u043d\u0443\u0442\u044c]", class_124.field_1060, ".sound remove " + soundId, "\u041a\u043b\u0438\u043a\u043d\u0438, \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0437\u0432\u0443\u043a"));
        } else {
            message.method_10852((class_2561)SoundRemoverModule.clickableText("[\u0437\u0430\u0433\u043b\u0443\u0448\u0438\u0442\u044c]", class_124.field_1054, ".sound add " + soundId, "\u041a\u043b\u0438\u043a\u043d\u0438, \u0447\u0442\u043e\u0431\u044b \u0437\u0430\u0433\u043b\u0443\u0448\u0438\u0442\u044c \u0437\u0432\u0443\u043a"));
        }
        return message;
    }

    private static class_5250 clickableText(String label, class_124 color, String command, String hover) {
        return class_2561.method_43470((String)label).method_10862(class_2583.field_24360.method_10977(color).method_30938(Boolean.valueOf(true)).method_10958((class_2558)new class_2558.class_10609(command)).method_10949((class_2568)new class_2568.class_10613((class_2561)class_2561.method_43470((String)hover))));
    }

    private static boolean shouldBlock(class_2960 soundId) {
        return SoundRemoverModule.compute4(soundId);
    }

    private static Map<String, String[]> buildSoundGroups() {
        LinkedHashMap<String, String[]> groups = new LinkedHashMap<String, String[]>();
        groups.put("\u0412\u0437\u0440\u044b\u0432\u044b", new String[]{"explode", "explosion", "tnt", "creeper", "blast"});
        groups.put("\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0438", new String[]{"firework", "fireworks"});
        groups.put("\u041e\u043f\u044b\u0442 \u0438 \u0443\u0440\u043e\u0432\u043d\u0438", new String[]{"experience", "levelup", "orb.pickup", "random.levelup"});
        groups.put("\u0423\u0440\u043e\u043d", new String[]{"hurt", "damage", "fall", "hit", "injured"});
        groups.put("\u0421\u043c\u0435\u0440\u0442\u044c", new String[]{"death", "die"});
        groups.put("\u042d\u043d\u0434\u0435\u0440 \u0414\u0440\u0430\u043a\u043e\u043d", new String[]{"dragon", "enderdragon"});
        groups.put("\u0412\u0430\u0440\u0434\u0435\u043d", new String[]{"warden"});
        groups.put("\u0412\u0438\u0437\u0435\u0440", new String[]{"wither"});
        groups.put("\u0413\u0430\u0441\u0442/\u043e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0448\u0430\u0440", new String[]{"ghast", "fireball", "blaze"});
        groups.put("\u041c\u043e\u043b\u043d\u0438\u0438 \u0438 \u0433\u0440\u043e\u0437\u0430", new String[]{"thunder", "lightning"});
        groups.put("\u041f\u043e\u0433\u043e\u0434\u0430 \u0438 \u0434\u043e\u0436\u0434\u044c", new String[]{"weather", "rain", "ambient.weather"});
        groups.put("\u0428\u0430\u0433\u0438", new String[]{"step", "footstep", "walk"});
        groups.put("\u0418\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441", new String[]{"ui.", "click", "button", "inventory", "screen", "toast"});
        groups.put("\u0413\u043e\u043b\u043e\u0441\u0430 \u043c\u043e\u0431\u043e\u0432", new String[]{"entity.", "mob", "ambient"});
        groups.put("\u0414\u0432\u0435\u0440\u0438 \u0438 \u043b\u044e\u043a\u0438", new String[]{"door", "trapdoor", "fence_gate", "gate"});
        groups.put("\u0421\u0443\u043d\u0434\u0443\u043a\u0438/\u0445\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0430", new String[]{"chest", "shulker", "barrel", "ender_chest"});
        groups.put("\u041f\u043e\u0440\u0442\u0430\u043b\u044b", new String[]{"portal", "warp"});
        return Map.copyOf(groups);
    }

    static {
        SOUND_GROUPS = SoundRemoverModule.buildSoundGroups();
    }
}

