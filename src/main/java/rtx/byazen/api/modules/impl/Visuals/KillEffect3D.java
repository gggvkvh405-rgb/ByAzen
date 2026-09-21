package rtx.byazen.api.modules.impl.Visuals;

import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.mods.killeffect.KillEffectBridge;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * ByAzen module for the bundled "Kill Effect" mod: 3D kill effects built from Blockbench models.
 * <p>
 * The module is the master switch of the mod - while it is disabled the mod cannot spawn any
 * effect (see KillEffectSpawnMixin) - and it additionally keeps the chosen effect in sync with
 * the client settings, because the mod itself does not persist the selection.
 */
public final class KillEffect3D
extends Module {

    private static final String[] EFFECT_IDS = {
            "shark_attack", "ice_shatter", "glass_shatter",
            "knockout_ko", "bubble_burst", "rust_decay",
            "quicksand", "spectral_fade", "stone_crumble",
            "sand_dissolve", "water_evaporation", "sound_wave_disperse",
            "digital_disintegration", "tentacle_grasp", "plantfood_feasting",
            "tertis_smash", "light_absorption", "astral_projection",
            "arcade_gameover", "dissolve_into_ash", "hellfire_burn",
            "void_collapse", "angelic_bless", "colorful_explosion",
            "feather_scatter", "nature_reclaim", "hologram_flicker_out",
            "liquid_meltdown", "imposter_instinct", "kfx_abstracted",
            "kfx_acidic_corrosion", "kfx_clockwork_disassembly", "kfx_frost_infection",
            "kfx_graffiti_spray", "kfx_ink_blots", "kfx_magnetic_resonance",
            "kfx_origami_fold", "kfx_plasma_orb", "kfx_pure_form",
    };

    private static final String[] EFFECT_NAMES = {
            "Атака Акулы", "Разрыв Льда", "Осколки Стекла",
            "Нокаут KO", "Взрыв Пузырей", "Ржавчина",
            "Зыбучий Песок", "Призрачное Угасание", "Каменная Крошка",
            "Растворение в Песке", "Испарение", "Звуковая Волна",
            "Цифровой Распад", "Хватка Щупалец", "Пир Плотоядного Растения",
            "Тетрис Краш", "Поглощение Света", "Астральная Проекция",
            "Аркадный Геймовер", "Раствориться в Пепле", "Адское Пламя",
            "Схлопывание в Пустоту", "Ангельское Благословение", "Красочный Взрыв",
            "Россыпь Перьев", "Власть Природы", "Мерцание Голограммы",
            "Жидкий Расплав", "Инстинкт Самозванца", "Абстракция",
            "Кислотная Коррозия", "Часовой Механизм", "Морозная Инфекция",
            "Граффити", "Чернильные Кляксы", "Магнитный Резонанс",
            "Оригами", "Плазменная Сфера", "Чистая Форма",
    };

    private final SeparatorSetting effectSeparator = this.register(new SeparatorSetting("Эффект"));
    private final SelectSetting effectSetting = this.register(new SelectSetting("Эффект", "3D-эффект, который играет при убийстве.").value(EFFECT_NAMES).selected(EFFECT_NAMES[0]));
    private final ButtonSetting openMenu = this.register(new ButtonSetting("Меню эффектов", "Полное меню Kill Effect с превью всех эффектов.").label("Открыть").onClick(KillEffectBridge::openMenu));

    private boolean pendingApply;
    private boolean warnedUnavailable;

    public KillEffect3D() {
        super("Kill Effect 3D", "3D-эффекты убийства: модели, анимации и выбор эффекта. Пока модуль выключен, эффекты не появляются.", Category.VISUALS);
        this.effectSetting.setChangeListener(this::onSettingsChanged);
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
        if (KillEffectBridge.available()) {
            ChatMessage.send("Kill Effect 3D включён: меню эффектов — кнопка «Открыть» в настройках модуля");
        }
    }

    private static String idForName(String name) {
        for (int i = 0; i < KillEffect3D.EFFECT_NAMES.length; ++i) {
            if (KillEffect3D.EFFECT_NAMES[i].equals(name)) {
                return KillEffect3D.EFFECT_IDS[i];
            }
        }
        return KillEffect3D.EFFECT_IDS[0];
    }

    private static String nameForId(String id) {
        for (int i = 0; i < KillEffect3D.EFFECT_IDS.length; ++i) {
            if (KillEffect3D.EFFECT_IDS[i].equals(id)) {
                return KillEffect3D.EFFECT_NAMES[i];
            }
        }
        return null;
    }

    private void applySettings() {
        KillEffectBridge.setSelectedId(KillEffect3D.idForName(this.effectSetting.getValue()));
    }

    private void mirrorFromMod() {
        String actual = KillEffectBridge.selectedId();
        if (actual == null) {
            this.applySettings();
            return;
        }
        String name = KillEffect3D.nameForId(actual);
        if (name == null) {
            this.applySettings();
            return;
        }
        if (!name.equals(this.effectSetting.getValue())) {
            this.effectSetting.selected(name);
        }
    }

    private void suppressEffects() {
        java.util.List<?> active = KillEffectBridge.activeEffects();
        if (!active.isEmpty()) {
            active.clear();
        }
    }

    void tick() {
        if (!KillEffectBridge.available()) {
            if (!this.warnedUnavailable) {
                this.warnedUnavailable = true;
                ChatMessage.error("Kill Effect: " + KillEffectBridge.lastError() + " (переустановите ByAzen целиком)");
            }
            return;
        }
        if (!this.isEnabled()) {
            this.suppressEffects();
            return;
        }
        if (this.pendingApply) {
            this.pendingApply = false;
            this.applySettings();
        }
        this.mirrorFromMod();
    }

    public static final class Ticker {
        private final KillEffect3D module;

        private Ticker(KillEffect3D module) {
            this.module = module;
        }

        @EventHandler
        public void onTick(TickEvent tickEvent) {
            this.module.tick();
        }
    }
}
