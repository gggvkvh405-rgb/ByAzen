package rtx.byazen.api.modules.impl.Visuals;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.SwordPreviewScreen;

public final class CustomSwords
extends Module {
    private static final String[] WEAPONS = new String[]{"Abominable Blade", "Abominable Great Saber", "Abominable Scythe", "Acidic Cleaver", "Amethyst Shuriken", "Ancient Royal Great Sword", "Aquatic Sacred Blade", "Arcanethyst", "Ashura's Blade", "Awakened Lichblade", "Blood Edge", "Bloody Death", "Bramblethorn", "Brimstone Claymore", "Carian Knight's Sword", "Chrono Blade", "Corrupted Mythic Blade", "Creation Splitter", "Crescent Rose", "Cyber Katana", "Cyber Mantis Blade", "Cyber Sword", "Cybernetic Chainsaw Blade", "Cybernetic Katana", "Cybernetic Knife", "Dainsleif", "Dark Blade", "Dark Cleaver", "Death Knight's Dagger", "Death Knight's Sword", "Demigod's Unholy Blade", "Demigod's Unholy Halberd", "Demon Lord's Great Axe", "Demon Lord's Sword", "Demonic Blade", "Demonic Cleaver", "Divine Axe Rhitta", "Divine Justice", "Divine Punisher", "Divine Reaper", "Dragon Slaying blade", "Edge Of The Astral Plane", "Emberblade", "Enigma", "Epic Sword", "Estoc", "Fallen God's Spear", "Fallen God's Sword", "Floral Longsword", "Floral Sabre", "Forest Guardian's Glaive", "Frost Axe", "Frost Blade", "Frost Scythe", "Hearthflame", "Hero Sword", "Holy Moonlight Sword", "Hornet's Needle", "Icewhisper", "Jade Halberd", "Katana", "Legendary Sword", "Longsword", "Magi Scythe", "Masamune", "Mjolnir", "Molten Blade", "Molten Sword", "Muramasa", "Mystical Spellblade", "Mythic Blade", "Ocean's Rage", "Partisan", "Pharaoh's Treasure", "Pheonix Grace", "Plague Longsword", "Power Fuse Hammer", "Power Fuse Sword", "Requiem of the Ninth Abyss", "Ribbon Cleaver", "Righteous Relic", "Rivers Of Blood", "Royal Chakram", "Royal Rapier", "Sabre", "Scissor Blade", "Sculk Cleaver", "Sculk Scythe", "Sculk Sword", "Sentinel's Will", "Silverine Blade", "Soul Claws", "Soul Collector", "Soul Devourer", "Soul Edge", "Soul Harvester", "Soul Stealer", "Soulrender", "Star's Edge", "Steel Sword", "Stop Sign", "Storm Bringer", "Storm's Edge", "Sunbreak", "Tengen's Blade", "Terra Blade", "Thousand Demon Daggers", "Thunder Bringer", "Thunderbrand", "True Excalibur", "Vampiric Needle", "Wakizashi", "Watcher Claymore", "Watching Warglaive", "Waxweaver", "Whisperwind", "Wickpiercer", "Wraith Scythe", "Yoru"};
    private static final String[] DISPLAY_WEAPONS = CustomSwords.displayNames();
    private static CustomSwords instance;
    private final BooleanSetting selfOnly = this.register(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0432\u043e\u0439", "\u0417\u0430\u043c\u0435\u043d\u044f\u0442\u044c \u043c\u043e\u0434\u0435\u043b\u044c \u043c\u0435\u0447\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0443 \u0441\u0432\u043e\u0435\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430.", true));
    private final ModeSetting weapon = this.register(new ModeSetting("\u041e\u0440\u0443\u0436\u0438\u0435", "\u041c\u043e\u0434\u0435\u043b\u044c, \u043a\u043e\u0442\u043e\u0440\u0430\u044f \u0437\u0430\u043c\u0435\u043d\u0438\u0442 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u043c\u0435\u0447\u0438.", DISPLAY_WEAPONS[0], DISPLAY_WEAPONS));

    private final SeparatorSetting hudSeparator = this.register(new SeparatorSetting("В интерфейсе"));
    private final BooleanSetting hudIcon = this.register(new BooleanSetting("Иконка в HUD", "Показывать выбранное оружие в HUD: иконкой и названием модели.", false));
    private final BooleanSetting hudName = this.register(new BooleanSetting("Название в HUD", "Подписывать модель рядом с иконкой.", true)).visible(this.hudIcon::getValue);
    private final SliderSetting hudScale = this.register(new SliderSetting("Размер иконки, %", "Насколько крупная иконка оружия в HUD.", 100.0f, 60.0f, 220.0f, 5.0f)).visible(this.hudIcon::getValue);
    private final BooleanSetting hudShadow = this.register(new BooleanSetting("Тень текста", "Мягкая тень под названием модели, чтобы читалось на любом фоне.", true)).visible(this.hudIcon::getValue);

    private final SeparatorSetting menuSeparator = this.register(new SeparatorSetting("Редактор"));
    private final ButtonSetting menu = this.register(new ButtonSetting("Меню оружия", "Открыть редактор моделей: большое превью, каталог и поиск по названию.").label("Открыть").onClick(CustomSwords::openMenu));

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

    /** Открывает редактор моделей с большим превью. */
    public static void openMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new SwordPreviewScreen());
        }
    }

    /** Все доступные модели оружия в порядке каталога. */
    public static String[] names() {
        return (String[])DISPLAY_WEAPONS.clone();
    }

    /** Выбор модели по отображаемому названию. */
    public void selectByDisplay(String string) {
        if (string == null) {
            return;
        }
        for (String display : DISPLAY_WEAPONS) {
            if (display.equals(string)) {
                this.weapon.setSelected(display);
                return;
            }
        }
    }

    /** Выбранная модель в том виде, в котором её видно в меню. */
    public String selectedDisplay() {
        return this.weapon.getSelected();
    }

    /** Настройки виджета HUD — их показывает редактор интерфейса. */
    public BooleanSetting hudToggle() {
        return this.hudIcon;
    }

    public BooleanSetting hudNameToggle() {
        return this.hudName;
    }

    public BooleanSetting hudShadowToggle() {
        return this.hudShadow;
    }

    public SliderSetting hudScaleSetting() {
        return this.hudScale;
    }

    public boolean hudEnabled() {
        return this.isEnabled() && this.hudIcon.getValue();
    }

    public boolean hudShowName() {
        return this.hudName.getValue();
    }

    public boolean hudShowShadow() {
        return this.hudShadow.getValue();
    }

    public float hudScale() {
        return this.hudScale.getValue() / 100.0f;
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

