package rtx.byazen.api.modules.impl.Visuals;

import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.mods.waveycapes.versionless.CapeMovement;
import rtx.byazen.api.mods.waveycapes.versionless.CapeStyle;
import rtx.byazen.api.mods.waveycapes.versionless.ModBase;
import rtx.byazen.api.mods.waveycapes.versionless.WindMode;
import rtx.byazen.utils.cosmetics.CosmeticSounds;

/**
 * Плащи с анимацией и физикой (идея №123 из IDEAS.md).
 * <p>
 * Плащ ByAzen переливается градиентом и живёт по-настоящему: он развевается на бегу, отстаёт при
 * резком повороте и подпрыгивает на прыжке. Здесь настраивается сам характер ткани — плавный или
 * блочный край, тип симуляции, ветер и вес — плюс готовые пресеты под наборы «Зима», «Неон» и
 * «Космос», чтобы подобрать поведение одним нажатием.
 */
public final class AnimatedCapes
extends Module {

    private static final String STYLE_SMOOTH = "Плавный";
    private static final String STYLE_BLOCKY = "Блочный";
    private static final String MOVE_VANILLA = "Обычное покачивание";
    private static final String MOVE_BASIC = "Мягкая физика";
    private static final String MOVE_3D = "Физика в объёме";
    private static final String MOVE_DUNGEONS = "Как в Dungeons";
    private static final String WIND_NONE = "Без ветра";
    private static final String WIND_WAVES = "Волны";

    private static AnimatedCapes instance;
    private String applied = "";

    private final SelectSetting style = this.register(new SelectSetting("Стиль плаща", "Плавные ленты или блочный край, как в старом стиле.")
            .value(STYLE_SMOOTH, STYLE_BLOCKY).selected(STYLE_SMOOTH));
    private final SelectSetting movement = this.register(new SelectSetting("Физика", "Как считаются узлы плаща при движении.")
            .value(MOVE_VANILLA, MOVE_BASIC, MOVE_3D, MOVE_DUNGEONS).selected(MOVE_3D));
    private final SelectSetting wind = this.register(new SelectSetting("Ветер", "Добавляет бегущую волну по ткани.")
            .value(WIND_NONE, WIND_WAVES).selected(WIND_WAVES));
    private final SliderSetting gravity = this.register(new SliderSetting("Вес ткани", "Чем больше, тем сильнее плащ тянет вниз.").range(0.0f, 60.0f).increment(1.0f).setValue(25.0f));
    private final SliderSetting lengthFactor = this.register(new SliderSetting("Длина сегмента", "Насколько высоко плащ поднимается при движении.").range(1.0f, 12.0f).increment(1.0f).setValue(6.0f));
    private final SliderSetting strafeFactor = this.register(new SliderSetting("Отставание", "Как сильно плащ уезжает в сторону при повороте.").range(1.0f, 8.0f).increment(1.0f).setValue(2.0f));
    private final BooleanSetting ownCape = this.register(new BooleanSetting("Плащ ByAzen", "Показывать переливающийся плащ ByAzen вместо обычного.", true));
    private final ButtonSetting presetWinter = this.register(new ButtonSetting("Пресет «Зима»", "Тяжёлая ткань, плавные линии, спокойный ветер.").label("Зима").onClick(() -> AnimatedCapes.preset("winter")));
    private final ButtonSetting presetNeon = this.register(new ButtonSetting("Пресет «Неон»", "Лёгкая ткань, быстрые волны, резкая физика.").label("Неон").onClick(() -> AnimatedCapes.preset("neon")));
    private final ButtonSetting presetSpace = this.register(new ButtonSetting("Пресет «Космос»", "Невесомая ткань, медленные волны в объёме.").label("Космос").onClick(() -> AnimatedCapes.preset("space")));

    public AnimatedCapes() {
        super("Animated Capes", "Плащи ByAzen с физикой: градиент, ветер, вес ткани и пресеты наборов.", Category.VISUALS);
        instance = this;
    }

    public static AnimatedCapes getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        rtx.byazen.utils.render.cape.CapeGradient.setEnabled(false);
    }

    @Override
    protected void onEnable() {
        this.apply();
    }

    /** Показывать ли переливающийся плащ ByAzen вместо обычного. */
    public static boolean capeEnabled() {
        AnimatedCapes module = AnimatedCapes.instance;
        return module != null && module.isEnabled() && module.ownCape.getValue();
    }

    private void apply() {
        ModBase base = ModBase.INSTANCE;
        if (ModBase.config == null) {
            try {
                if (base != null) {
                    base.init();
                }
            }
            catch (Throwable ignored) {
                return;
            }
        }
        if (ModBase.config == null) {
            return;
        }
        ModBase.config.capeStyle = MOVE_VANILLA.equals(this.style.getSelected()) ? CapeStyle.BLOCKY : CapeStyle.SMOOTH;
        ModBase.config.capeMovement = this.movement();
        ModBase.config.windMode = WIND_WAVES.equals(this.wind.getSelected()) ? WindMode.WAVES : WindMode.NONE;
        ModBase.config.gravity = Math.round(this.gravity.getFloat());
        ModBase.config.heightMultiplier = Math.round(this.lengthFactor.getFloat());
        ModBase.config.straveMultiplier = Math.round(this.strafeFactor.getFloat());
        rtx.byazen.utils.render.cape.CapeGradient.setEnabled(this.ownCape.getValue());
        try {
            if (base != null) {
                base.writeConfig();
            }
        }
        catch (Throwable ignored) {
            // конфиг не критичен: настройки применены в памяти
        }
    }

    private CapeMovement movement() {
        String selected = this.movement.getSelected();
        if (MOVE_VANILLA.equals(selected)) {
            return CapeMovement.VANILLA;
        }
        if (MOVE_BASIC.equals(selected)) {
            return CapeMovement.BASIC_SIMULATION;
        }
        if (MOVE_DUNGEONS.equals(selected)) {
            return CapeMovement.DUNGEONS;
        }
        return CapeMovement.BASIC_SIMULATION_3D;
    }

    /** Готовые пресеты под наборы ByAzen. */
    public static void preset(String flavor) {
        AnimatedCapes module = AnimatedCapes.instance;
        if (module == null) {
            return;
        }
        if ("winter".equals(flavor)) {
            module.style.setSelected(STYLE_SMOOTH);
            module.movement.setSelected(MOVE_3D);
            module.wind.setSelected(WIND_WAVES);
            module.gravity.setValue(32.0f);
            module.lengthFactor.setValue(5.0f);
            module.strafeFactor.setValue(2.0f);
        } else if ("neon".equals(flavor)) {
            module.style.setSelected(STYLE_SMOOTH);
            module.movement.setSelected(MOVE_BASIC);
            module.wind.setSelected(WIND_WAVES);
            module.gravity.setValue(16.0f);
            module.lengthFactor.setValue(9.0f);
            module.strafeFactor.setValue(5.0f);
        } else {
            module.style.setSelected(STYLE_SMOOTH);
            module.movement.setSelected(MOVE_3D);
            module.wind.setSelected(WIND_WAVES);
            module.gravity.setValue(10.0f);
            module.lengthFactor.setValue(11.0f);
            module.strafeFactor.setValue(3.0f);
        }
        module.apply();
        CosmeticSounds.playSet(flavor, 0.85f);
        if (!module.isEnabled()) {
            module.enable();
        }
    }

    /** Настройки применяются сразу, как только меняются. */
    @EventHandler
    public void onTick(TickEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        String signature = this.style.getSelected() + "|" + this.movement.getSelected() + "|" + this.wind.getSelected()
                + "|" + Math.round(this.gravity.getFloat()) + "|" + Math.round(this.lengthFactor.getFloat())
                + "|" + Math.round(this.strafeFactor.getFloat()) + "|" + this.ownCape.getValue();
        if (!signature.equals(this.applied)) {
            this.applied = signature;
            this.apply();
        }
    }
}
