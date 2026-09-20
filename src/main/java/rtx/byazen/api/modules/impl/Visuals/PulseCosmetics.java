package rtx.byazen.api.modules.impl.Visuals;

import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.mods.pulsecosmetics.PulseCosmeticsBridge;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;

/**
 * ByAzen module for the bundled "Pulse Cosmetics" mod: capes, wings, hats, bodywear, pets and graffiti.
 * <p>
 * The module mirrors the visibility settings of the mod (so both the client and the mod menu stay in
 * sync) and acts as a master switch: while it is disabled, cosmetics of other players are hidden.
 */
public final class PulseCosmetics
extends Module {

    private static final String WINGS = "Крылья";
    private static final String CAPE = "Плащи";
    private static final String HAT = "Голова";
    private static final String BODYWEAR = "На тело";
    private static final String PET = "Питомцы";
    private static final String GRAFFITI = "Граффити";

    private static final String[] CATEGORY_KEYS = {"WINGS", "CAPE", "HAT", "BODYWEAR", "PET", "GRAFFITI"};
    private static final String[] CATEGORY_LABELS = {WINGS, CAPE, HAT, BODYWEAR, PET, GRAFFITI};

    private final SeparatorSetting cosmeticsSeparator = this.register(new SeparatorSetting("Косметика"));
    private final ButtonSetting openMenu = this.register(new ButtonSetting("Меню косметики", "Полное меню Pulse Cosmetics с превью и выбором косметики.").label("Открыть").onClick(() -> PulseCosmeticsBridge.openMenu("WINGS")));
    private final MultiSelectSetting otherCosmetics = this.register(new MultiSelectSetting("Чужая косметика", "Какие категории косметики других игроков показывать.").value(CATEGORY_LABELS).selected(CATEGORY_LABELS));
    private final BooleanSetting ownPetFirstPerson = this.register(new BooleanSetting("Питомец в 1-м лице", "Показывать своего питомца от первого лица.", true));

    private final SeparatorSetting graffitiSeparator = this.register(new SeparatorSetting("Граффити"));
    private final ButtonSetting graffitiCleaner = this.register(new ButtonSetting("Очиститель граффити", "Режим удаления граффити: наведитесь на рисунок и нажмите ПКМ.").label("Включить").onClick(PulseCosmeticsBridge::startGraffitiRemoval));

    private boolean pendingApply;

    public PulseCosmetics() {
        super("Pulse Cosmetics", "Косметика Pulse: меню выбора, чужая косметика по категориям, питомец от первого лица и граффити.", Category.VISUALS);
        this.otherCosmetics.setChangeListener(this::onSettingsChanged);
        this.ownPetFirstPerson.setChangeListener(this::onSettingsChanged);
        EventBus.get().subscribe(new Ticker(this));
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    private void onSettingsChanged() {
        ConfigManager.markDirty();
        this.pendingApply = true;
    }

    @Override
    protected void onEnable() {
        this.pendingApply = true;
    }

    private void applySettings() {
        for (int i = 0; i < CATEGORY_KEYS.length; ++i) {
            PulseCosmeticsBridge.setShowOther(CATEGORY_KEYS[i], this.otherCosmetics.is(CATEGORY_LABELS[i]));
        }
        PulseCosmeticsBridge.setOwnPetFirstPerson(this.ownPetFirstPerson.getValue());
    }

    private void mirrorFromMod() {
        for (int i = 0; i < CATEGORY_KEYS.length; ++i) {
            boolean shown = PulseCosmeticsBridge.showOther(CATEGORY_KEYS[i]);
            if (shown != this.otherCosmetics.is(CATEGORY_LABELS[i])) {
                this.otherCosmetics.toggle(CATEGORY_LABELS[i]);
            }
        }
        boolean pet = PulseCosmeticsBridge.ownPetFirstPerson();
        if (pet != this.ownPetFirstPerson.getValue()) {
            this.ownPetFirstPerson.setValue(pet);
        }
    }

    private void hideOthers() {
        for (String categoryKey : CATEGORY_KEYS) {
            PulseCosmeticsBridge.setShowOther(categoryKey, false);
        }
        PulseCosmeticsBridge.setOwnPetFirstPerson(false);
    }

    void tick() {
        if (!PulseCosmeticsBridge.available()) {
            return;
        }
        if (!this.isEnabled()) {
            this.hideOthers();
            return;
        }
        if (this.pendingApply) {
            this.pendingApply = false;
            this.applySettings();
        }
        this.mirrorFromMod();
    }

    public static final class Ticker {
        private final PulseCosmetics module;

        private Ticker(PulseCosmetics module) {
            this.module = module;
        }

        @EventHandler
        public void onTick(TickEvent tickEvent) {
            this.module.tick();
        }
    }
}
