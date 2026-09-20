package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;

public final class CustomSwords
extends Module {
    private static final String[] WEAPONS = new String[]{"Abominable Blade", "Abominable Great Saber", "Abominable Scythe", "Acidic Cleaver", "Amethyst Shuriken", "Ancient Royal Great Sword", "Aquatic Sacred Blade", "Arcanethyst", "Ashura's Blade", "Awakened Lichblade", "Blood Edge", "Bloody Death", "Bramblethorn", "Brimstone Claymore", "Carian Knight's Sword", "Chrono Blade", "Corrupted Mythic Blade", "Creation Splitter", "Crescent Rose", "Cyber Katana", "Cyber Mantis Blade", "Cyber Sword", "Cybernetic Chainsaw Blade", "Cybernetic Katana", "Cybernetic Knife", "Dainsleif", "Dark Blade", "Dark Cleaver", "Death Knight's Dagger", "Death Knight's Sword", "Demigod's Unholy Blade", "Demigod's Unholy Halberd", "Demon Lord's Great Axe", "Demon Lord's Sword", "Demonic Blade", "Demonic Cleaver", "Divine Axe Rhitta", "Divine Justice", "Divine Punisher", "Divine Reaper", "Dragon Slaying blade", "Edge Of The Astral Plane", "Emberblade", "Enigma", "Epic Sword", "Estoc", "Fallen God's Spear", "Fallen God's Sword", "Floral Longsword", "Floral Sabre", "Forest Guardian's Glaive", "Frost Axe", "Frost Blade", "Frost Scythe", "Hearthflame", "Hero Sword", "Holy Moonlight Sword", "Hornet's Needle", "Icewhisper", "Jade Halberd", "Katana", "Legendary Sword", "Longsword", "Magi Scythe", "Masamune", "Mjolnir", "Molten Blade", "Molten Sword", "Muramasa", "Mystical Spellblade", "Mythic Blade", "Ocean's Rage", "Partisan", "Pharaoh's Treasure", "Pheonix Grace", "Plague Longsword", "Power Fuse Hammer", "Power Fuse Sword", "Requiem of the Ninth Abyss", "Ribbon Cleaver", "Righteous Relic", "Rivers Of Blood", "Royal Chakram", "Royal Rapier", "Sabre", "Scissor Blade", "Sculk Cleaver", "Sculk Scythe", "Sculk Sword", "Sentinel's Will", "Silverine Blade", "Soul Claws", "Soul Collector", "Soul Devourer", "Soul Edge", "Soul Harvester", "Soul Stealer", "Soulrender", "Star's Edge", "Steel Sword", "Stop Sign", "Storm Bringer", "Storm's Edge", "Sunbreak", "Tengen's Blade", "Terra Blade", "Thousand Demon Daggers", "Thunder Bringer", "Thunderbrand", "True Excalibur", "Vampiric Needle", "Wakizashi", "Watcher Claymore", "Watching Warglaive", "Waxweaver", "Whisperwind", "Wickpiercer", "Wraith Scythe", "Yoru"};
    private static final String[] DISPLAY_WEAPONS = CustomSwords.displayNames();
    private static CustomSwords instance;
    private final BooleanSetting selfOnly = this.register(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0432\u043e\u0439", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u043c\u043e\u0434\u0435\u043b\u044c \u043c\u0435\u0447\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0443 \u0441\u0432\u043e\u0435\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430.", true));
    private final ModeSetting weapon = this.register(new ModeSetting("\u041e\u0440\u0443\u0436\u0438\u0435", "\u041c\u043e\u0434\u0435\u043b\u044c, \u043a\u043e\u0442\u043e\u0440\u0430\u044f \u0437\u0430\u043c\u0435\u043d\u0438\u0442 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u043c\u0435\u0447\u0438.", DISPLAY_WEAPONS[0], DISPLAY_WEAPONS));

    public CustomSwords() {
        super("Custom Swords", "\u0417\u0430\u043c\u0435\u043d\u044f\u0435\u0442 \u043c\u043e\u0434\u0435\u043b\u0438 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0445 \u043c\u0435\u0447\u0435\u0439.", Category.VISUALS);
        instance = this;
    }

    public static CustomSwords getInstance() {
        return instance;
    }

    private static String[] displayNames() {
        String[] stringArray = new String[WEAPONS.length];
        for (int i = 0; i < WEAPONS.length; ++i) {
            stringArray[i] = WEAPONS[i].replace("'", "");
        }
        return stringArray;
    }

    public boolean isSelfOnly() {
        return this.selfOnly.getValue();
    }

    public String getSelectedWeapon() {
        String string = this.weapon.getSelected();
        for (int i = 0; i < DISPLAY_WEAPONS.length; ++i) {
            if (!DISPLAY_WEAPONS[i].equals(string)) continue;
            return WEAPONS[i];
        }
        return string;
    }
}

