/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_408
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.AttackWindowEvent;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.ReachHelper;
import ru.byazen.misc.TargetFilter;
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
import ru.byazen.util.Angle;
import ru.byazen.util.TargetSelector;

public class AimAssistModule
extends Module
implements ConfigSerializable {
    private static final String MODE_ASSIST = "\u0414\u043e\u0432\u043e\u0434\u043a\u0430";
    private static final String MODE_HELPER = "\u041f\u043e\u043c\u043e\u0449\u043d\u0438\u043a";
    private static final String MODE_CAPTURE = "\u0417\u0430\u0445\u0432\u0430\u0442";
    private static final String MODE_DIRECT = "\u041f\u0440\u044f\u043c\u043e\u0439";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0414\u043e\u0432\u043e\u0434\u0438\u0442 \u043a\u0430\u043c\u0435\u0440\u0443 \u0434\u043e \u0446\u0435\u043b\u0438").withKeybind().toggle()).build();
    private final ModeSetting mode;
    private final BooleanSetting magnet;
    private final NumberSetting magnetStrength;
    private final NumberSetting yawSpeed;
    private final NumberSetting pitchSpeed;
    private final NumberSetting captureSpeedNear;
    private final NumberSetting captureSpeedFar;
    private final NumberSetting captureZone;
    private final NumberSetting shakeStrength;
    private final NumberSetting aimHeight;
    private final NumberSetting attackRange;
    private final NumberSetting rotationRange;
    private final NumberSetting fov;
    private final MultiSelectSetting targets;
    private final ModeSetting sorting;
    private final BooleanSetting throughWalls;
    private final BooleanSetting focusOne;
    private final BooleanSetting noGui;
    private final BooleanSetting inInventory;
    private TargetSelector targetSelector;
    private class_1309 target;
    private class_243 smoothedDelta;
    private int onTargetHold;
    private int trackedId = -1;
    private int shakeTargetId = -1;
    private int wanderTargetId = -1;
    private long lastFrameNanos;
    private long nextShakeNanos;
    private boolean applied;
    private float lastYaw;
    private float lastPitch;
    private float mouseYaw;
    private float mousePitch;
    private float mouseActivity;
    private float yawQuantError;
    private float pitchQuantError;
    private float captureYawStep;
    private float capturePitchStep;
    private float assistGain = 0.36f;
    private float onTargetScale = 1.0f;
    private float magnetLock;
    private float magnetAmount;
    private float wanderX;
    private float wanderZ;
    private float shakeYaw;
    private float shakePitch;

    public AimAssistModule(EventBus eventBus) {
        super(eventBus, "aim_assist", "Aim Assist", "\u041f\u043e\u043c\u043e\u0449\u043d\u0438\u043a \u043f\u0440\u0438\u0446\u0435\u043b\u0438\u0432\u0430\u043d\u0438\u044f", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(MODE_ASSIST, MODE_HELPER, MODE_CAPTURE, MODE_DIRECT).defaultOption(MODE_ASSIST).name("\u0420\u0435\u0436\u0438\u043c").id("mode").description("\u041a\u0430\u043a \u0438\u043c\u0435\u043d\u043d\u043e \u0442\u044f\u043d\u0443\u0442\u044c \u043a\u0430\u043c\u0435\u0440\u0443").withKeybind()).build();
        this.registerSetting(this.mode);
        this.magnet = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u041c\u0430\u0433\u043d\u0438\u0442").id("magnet").description("\u0422\u044f\u043d\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u043a\u0430 \u0442\u044b \u0441\u0430\u043c \u0432\u0435\u0434\u0451\u0448\u044c \u043c\u044b\u0448\u043a\u043e\u0439 \u0438 \u043d\u0435 \u0431\u043e\u043b\u044c\u0448\u0435, \u0447\u0435\u043c \u043d\u0430 \u0442\u0432\u043e\u0439 \u0436\u0435 \u0441\u0434\u0432\u0438\u0433").aliases("magnet", "\u043c\u0430\u0433\u043d\u0438\u0442").visibleWhen(() -> MODE_HELPER.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.magnet);
        this.magnetStrength = ((NumberSettingBuilder)NumberSetting.builder().range(0.1, 2.5).defaultValue(0.9999999999999999).multiplier(1.0).precision(2).animationSpeed(20.0f).name("\u0421\u0438\u043b\u0430 \u043c\u0430\u0433\u043d\u0438\u0442\u0430").id("magnet_strength").description("\u0412\u043e \u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0440\u0430\u0437 \u043c\u0430\u0433\u043d\u0438\u0442 \u043c\u043e\u0436\u0435\u0442 \u0443\u0441\u0438\u043b\u0438\u0442\u044c \u0442\u0432\u043e\u0439 \u0441\u043e\u0431\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u0441\u0434\u0432\u0438\u0433").aliases("magnet strength", "\u0441\u0438\u043b\u0430 \u043c\u0430\u0433\u043d\u0438\u0442\u0430").visibleWhen(() -> MODE_HELPER.equals(this.mode.getSelectedOption()) && this.magnet.isEnabled())).build();
        this.registerSetting(this.magnetStrength);
        this.yawSpeed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 180.0).defaultValue(60.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e \u0433\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u0438").id("yaw_speed").description("\u041f\u0440\u0435\u0434\u0435\u043b \u0434\u043e\u0432\u043e\u0434\u0430 \u043f\u043e yaw")).build();
        this.registerSetting(this.yawSpeed);
        this.pitchSpeed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 180.0).defaultValue(30.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438").id("pitch_speed").description("\u041f\u0440\u0435\u0434\u0435\u043b \u0434\u043e\u0432\u043e\u0434\u0430 \u043f\u043e pitch")).build();
        this.registerSetting(this.pitchSpeed);
        this.captureSpeedNear = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 180.0).defaultValue(25.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0431\u043b\u0438\u0437\u0438 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").id("capture_speed_near").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c, \u043a\u043e\u0433\u0434\u0430 \u0446\u0435\u043b\u044c \u043f\u043e\u0447\u0442\u0438 \u043f\u043e\u0434 \u043f\u0440\u0438\u0446\u0435\u043b\u043e\u043c").visibleWhen(() -> MODE_CAPTURE.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.captureSpeedNear);
        this.captureSpeedFar = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 180.0).defaultValue(90.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0434\u0430\u043b\u0438 \u043e\u0442 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").id("capture_speed_far").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c, \u043a\u043e\u0433\u0434\u0430 \u0446\u0435\u043b\u044c \u0434\u0430\u043b\u0435\u043a\u043e \u043e\u0442 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").visibleWhen(() -> MODE_CAPTURE.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.captureSpeedFar);
        this.captureZone = ((NumberSettingBuilder)NumberSetting.builder().range(0.5, 90.0).defaultValue(20.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0417\u043e\u043d\u0430 \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0430").id("capture_zone").description("\u0423\u0433\u043e\u043b, \u043d\u0430 \u043a\u043e\u0442\u043e\u0440\u043e\u043c \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0438\u0442 \u043e\u0442 \u0434\u0430\u043b\u044c\u043d\u0435\u0439 \u043a \u0431\u043b\u0438\u0436\u043d\u0435\u0439").visibleWhen(() -> MODE_CAPTURE.equals(this.mode.getSelectedOption()))).build();
        this.registerSetting(this.captureZone);
        this.shakeStrength = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 10.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0414\u0440\u043e\u0436\u0430\u043d\u0438\u0435").id("shake_strength").description("\u0416\u0438\u0432\u043e\u0441\u0442\u044c \u043f\u0440\u0438\u0446\u0435\u043b\u0430: \u0431\u043b\u0443\u0436\u0434\u0430\u043d\u0438\u0435 \u0442\u043e\u0447\u043a\u0438, \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0435 \u0441\u043c\u0435\u0449\u0435\u043d\u0438\u044f \u0438 \u043e\u0441\u043b\u0430\u0431\u043b\u0435\u043d\u0438\u0435 \u0434\u043e\u0432\u043e\u0434\u0430 \u043d\u0430 \u0446\u0435\u043b\u0438")).build();
        this.registerSetting(this.shakeStrength);
        this.aimHeight = ((NumberSettingBuilder)NumberSetting.builder().range(0.18, 0.92).defaultValue(0.6).multiplier(1.0).precision(2).animationSpeed(20.0f).name("\u0412\u044b\u0441\u043e\u0442\u0430 \u0442\u043e\u0447\u043a\u0438").id("aim_height").description("\u0414\u043e\u043b\u044f \u0445\u0438\u0442\u0431\u043e\u043a\u0441\u0430 \u0441\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445, \u0432 \u043a\u043e\u0442\u043e\u0440\u0443\u044e \u0446\u0435\u043b\u0438\u0442\u044c\u0441\u044f").aliases("height", "\u0432\u044b\u0441\u043e\u0442\u0430")).build();
        this.registerSetting(this.aimHeight);
        this.attackRange = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 6.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0443\u0434\u0430\u0440\u0430").id("attack_range").description("\u041d\u0438\u0436\u043d\u044f\u044f \u0433\u0440\u0430\u043d\u0438\u0446\u0430 \u043b\u0443\u0447\u0430 \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438 \u043f\u0440\u0438\u0446\u0435\u043b\u0430")).build();
        this.registerSetting(this.attackRange);
        this.rotationRange = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 12.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u044f").id("rotation_range").description("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043f\u043e\u0438\u0441\u043a\u0430 \u0446\u0435\u043b\u0438 \u0438 \u0432\u0435\u0440\u0445\u043d\u044f\u044f \u0433\u0440\u0430\u043d\u0438\u0446\u0430 \u043b\u0443\u0447\u0430 \u043f\u0440\u043e\u0432\u0435\u0440\u043a\u0438")).build();
        this.registerSetting(this.rotationRange);
        this.fov = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 360.0).defaultValue(120.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("FOV").id("fov").description("\u0421\u0435\u043a\u0442\u043e\u0440 \u043f\u043e\u0438\u0441\u043a\u0430 \u0446\u0435\u043b\u0438")).build();
        this.registerSetting(this.fov);
        MultiSelectSetting targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers").selectAll(false).optionListEnabled(false).name("\u0426\u0435\u043b\u0438").id("targets").description("\u0422\u0438\u043f\u044b \u0446\u0435\u043b\u0435\u0439 \u0434\u043b\u044f \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u044f")).build();
        targetsSetting.setOptions(new String[0]);
        this.targets = targetsSetting;
        this.registerSetting(targetsSetting);
        this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Health", "Distance", "Crosshair").defaultOption("Distance").name("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430").id("sorting").description("\u041a\u0430\u043a \u0432\u044b\u0431\u0438\u0440\u0430\u0442\u044c \u0446\u0435\u043b\u044c \u0438\u0437 \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u0438\u0445")).build();
        this.registerSetting(this.sorting);
        this.throughWalls = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0421\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b").id("through_walls").description("\u041d\u0430\u0432\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u043d\u0430 \u0446\u0435\u043b\u044c \u0437\u0430 \u0431\u043b\u043e\u043a\u0430\u043c\u0438").aliases("walls", "\u0441\u0442\u0435\u043d\u044b")).build();
        this.registerSetting(this.throughWalls);
        this.focusOne = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0424\u043e\u043a\u0443\u0441 \u043d\u0430 \u043e\u0434\u043d\u043e\u043c").id("focus_one").description("\u0414\u0435\u0440\u0436\u0430\u0442\u044c \u043e\u0434\u043d\u0443 \u0446\u0435\u043b\u044c, \u043f\u043e\u043a\u0430 \u043e\u043d\u0430 \u0432\u0430\u043b\u0438\u0434\u043d\u0430, \u0432\u043c\u0435\u0441\u0442\u043e \u043f\u0435\u0440\u0435\u0432\u044b\u0431\u043e\u0440\u0430 \u043a\u0430\u0436\u0434\u044b\u0439 \u0442\u0438\u043a").aliases("focus", "\u0444\u043e\u043a\u0443\u0441")).build();
        this.registerSetting(this.focusOne);
        this.noGui = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u041d\u0435 \u0432\u043e\u0434\u0438\u0442\u044c \u0432 GUI").id("no_gui").description("\u041e\u0442\u043f\u0443\u0441\u043a\u0430\u0442\u044c \u043a\u0430\u043c\u0435\u0440\u0443, \u043f\u043e\u043a\u0430 \u043e\u0442\u043a\u0440\u044b\u0442 \u044d\u043a\u0440\u0430\u043d. \u0427\u0430\u0442 \u044d\u043a\u0440\u0430\u043d\u043e\u043c \u043d\u0435 \u0441\u0447\u0438\u0442\u0430\u0435\u0442\u0441\u044f")).build();
        this.registerSetting(this.noGui);
        this.inInventory = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0412\u043e\u0434\u0438\u0442\u044c \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435").id("in_inventory").description("\u0418\u0441\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0434\u043b\u044f \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f \u0438 \u0441\u0443\u043d\u0434\u0443\u043a\u043e\u0432").aliases("inventory", "\u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c").visibleWhen(this.noGui::isEnabled)).build();
        this.registerSetting(this.inInventory);
    }

    @Override
    protected void initialize() {
        this.targetSelector = new TargetSelector();
        this.listen(ClientTickEvent.class, event -> this.selectTarget());
        this.listen(AttackWindowEvent.class, this::onAttackWindow);
    }

    private void rollShake(class_1309 entity, boolean captured) {
        long now = System.nanoTime();
        if (!captured && this.shakeTargetId == entity.method_5628() && now < this.nextShakeNanos) {
            return;
        }
        this.shakeTargetId = entity.method_5628();
        float strength = class_3532.method_15363((float)(this.shakeStrength.getFloatValue() / 10.0f), (float)0.0f, (float)1.0f);
        float yawRange = 0.012f + strength * 0.03f;
        float pitchRange = 0.008f + strength * 0.024f;
        this.shakeYaw = ThreadLocalRandom.current().nextFloat(-yawRange, yawRange);
        this.shakePitch = ThreadLocalRandom.current().nextFloat(-pitchRange, pitchRange);
        this.nextShakeNanos = now + ThreadLocalRandom.current().nextLong(340000000L, 760000000L);
    }

    private static class_243 predictClimb(class_1309 entity, class_243 motion) {
        class_243 look = entity.method_5720();
        float pitchRad = entity.method_36455() * ((float)Math.PI / 180);
        double horizontalLook = Math.sqrt(look.field_1352 * look.field_1352 + look.field_1350 * look.field_1350);
        double motionLength = motion.method_37267();
        double gravity = AimAssistModule.effectiveGravity(entity);
        double pitchCosSq = class_3532.method_33723((double)Math.cos(pitchRad));
        motion = motion.method_1031(0.0, gravity * (-1.0 + pitchCosSq * 0.75), 0.0);
        if (motion.field_1351 < 0.0 && horizontalLook > 0.0) {
            double lift = motion.field_1351 * -0.1 * pitchCosSq;
            motion = motion.method_1031(look.field_1352 * lift / horizontalLook, lift, look.field_1350 * lift / horizontalLook);
        }
        if (pitchRad < 0.0f && horizontalLook > 0.0) {
            double boost = motionLength * (double)(-class_3532.method_15374((double)pitchRad)) * 0.04;
            motion = motion.method_1031(-look.field_1352 * boost / horizontalLook, boost * 3.2, -look.field_1350 * boost / horizontalLook);
        }
        if (horizontalLook > 0.0) {
            motion = motion.method_1031((look.field_1352 / horizontalLook * motionLength - motion.field_1352) * 0.1, 0.0, (look.field_1350 / horizontalLook * motionLength - motion.field_1350) * 0.1);
        }
        return motion.method_18805(0.99, 0.98, 0.99);
    }

    private boolean blockedByScreen() {
        if (!this.noGui.isEnabled()) {
            return false;
        }
        class_437 screen = class_310.method_1551().field_1755;
        if (screen == null || screen instanceof class_408) {
            return false;
        }
        return !this.inInventory.isEnabled() || !(screen instanceof class_465);
    }

    private void onAttackWindow(AttackWindowEvent event) {
        float nextPitch;
        float nextYaw;
        if (!event.isPre() && !event.isPost()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        class_1309 target = this.target;
        if (!this.enabledSetting.isEnabled() || player == null || !player.method_5805() || target == null || this.blockedByScreen()) {
            this.lastFrameNanos = 0L;
            this.resetAssist();
            return;
        }
        float dt = this.frameDelta();
        float yaw = player.method_36454();
        float pitch = player.method_36455();
        this.updateWander(dt, target);
        boolean captured = this.captureTarget(target.method_5628());
        float mouseWeight = this.updateMouse(yaw, pitch, dt);
        String mode = this.mode.getSelectedOption();
        boolean assist = MODE_ASSIST.equals(mode);
        boolean helper = MODE_HELPER.equals(mode);
        boolean capture = MODE_CAPTURE.equals(mode);
        boolean smooth = assist || helper;
        this.rollShake(target, captured);
        class_243 aimPoint = this.aimPoint(player, target);
        if (target.method_6101()) {
            aimPoint = aimPoint.method_1019(AimAssistModule.predictClimb(target, target.method_18798()));
        }
        class_243 eye = player.method_33571();
        class_243 delta = aimPoint.method_1020(eye).method_1031((double)this.wanderX, 0.0, (double)this.wanderZ);
        class_243 look = smooth ? this.smoothDelta(delta, dt, captured) : delta;
        class_243 vec3d = look;
        if (!smooth) {
            this.smoothedDelta = null;
            this.onTargetHold = 0;
            this.onTargetScale = 1.0f;
        }
        Angle angle = Angle.fromDelta(look);
        float targetYaw = angle.getYaw();
        class_238 box = target.method_5829();
        double horizontal = Math.sqrt(look.field_1352 * look.field_1352 + look.field_1350 * look.field_1350);
        float maxPitch = (float)(-Math.toDegrees(Math.atan2(box.field_1325 - eye.field_1351 - 0.2, horizontal)));
        float minPitch = (float)(-Math.toDegrees(Math.atan2(box.field_1322 - eye.field_1351 - 0.2, horizontal)));
        float targetPitch = class_3532.method_15363((float)angle.getPitch(), (float)maxPitch, (float)minPitch);
        float yawDelta = class_3532.method_15393((float)(targetYaw - yaw));
        float pitchDelta = class_3532.method_15393((float)(targetPitch - pitch));
        float yawCap = Math.max(1.0f, this.yawSpeed.getFloatValue()) * 2.8f;
        float pitchCap = Math.max(1.0f, this.pitchSpeed.getFloatValue()) * 2.2f;
        float yawFactor = AimAssistModule.expApproach(dt, 2.8f, class_3532.method_15363((float)(this.yawSpeed.getFloatValue() / 60.0f), (float)0.0f, (float)4.2f) * 4.4f);
        float pitchFactor = AimAssistModule.expApproach(dt, 2.4f, class_3532.method_15363((float)(this.pitchSpeed.getFloatValue() / 30.0f), (float)0.0f, (float)4.4f) * 3.8f);
        float yawStep = class_3532.method_15363((float)(yawDelta * yawFactor), (float)(-yawCap * dt), (float)(yawCap * dt));
        float pitchStep = class_3532.method_15363((float)(pitchDelta * pitchFactor), (float)(-pitchCap * dt), (float)(pitchCap * dt));
        if (helper && this.magnet.isEnabled()) {
            boolean onTarget = this.lookingAt(player, target, yaw, pitch);
            float[] magnet = this.magnetAim(yaw, pitch, yawDelta, pitchDelta, dt, captured, onTarget);
            nextYaw = magnet[0];
            nextPitch = magnet[1];
        } else if (capture) {
            if (captured) {
                this.captureYawStep = 0.0f;
                this.capturePitchStep = 0.0f;
            }
            float error = Math.abs(yawDelta) + Math.abs(pitchDelta) * 0.7f;
            float zone = Math.max(0.5f, this.captureZone.getFloatValue());
            float t = class_3532.method_15363((float)(error / zone), (float)0.0f, (float)1.0f);
            float speed = class_3532.method_16439((float)t, (float)Math.max(1.0f, this.captureSpeedNear.getFloatValue()), (float)Math.max(1.0f, this.captureSpeedFar.getFloatValue()));
            float capYaw = speed * 2.8f;
            float capPitch = speed * 2.2f;
            float captureYawFactor = AimAssistModule.expApproach(dt, 2.8f, class_3532.method_15363((float)(speed / 60.0f), (float)0.0f, (float)4.2f) * 4.4f);
            float capturePitchFactor = AimAssistModule.expApproach(dt, 2.4f, class_3532.method_15363((float)(speed / 30.0f), (float)0.0f, (float)4.4f) * 3.8f);
            float stepYaw = class_3532.method_15363((float)(yawDelta * captureYawFactor), (float)(-capYaw * dt), (float)(capYaw * dt));
            float stepPitch = class_3532.method_15363((float)(pitchDelta * capturePitchFactor), (float)(-capPitch * dt), (float)(capPitch * dt));
            stepYaw = stepYaw * 0.7f + this.captureYawStep * 0.3f;
            stepPitch = stepPitch * 0.7f + this.capturePitchStep * 0.3f;
            this.captureYawStep = stepYaw;
            this.capturePitchStep = stepPitch;
            nextYaw = yaw + stepYaw;
            nextPitch = pitch + stepPitch;
        } else if (assist) {
            float gain = this.assistGain(yawDelta, pitchDelta, mouseWeight);
            this.assistGain = captured ? gain : class_3532.method_16439((float)class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 16.0f), (float)0.12f, (float)0.5f), (float)this.assistGain, (float)gain);
            boolean onTarget = this.lookingAt(player, target, yaw, pitch);
            if (onTarget) {
                this.onTargetHold = 5;
            } else if (this.onTargetHold > 0) {
                --this.onTargetHold;
            }
            float scale = onTarget || this.onTargetHold > 0 ? this.onTargetShakeScale() : 1.0f;
            this.onTargetScale = captured ? scale : class_3532.method_16439((float)class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 8.0f), (float)0.04f, (float)0.22f), (float)this.onTargetScale, (float)scale);
            nextYaw = yaw + yawStep * this.onTargetScale * this.assistGain;
            nextPitch = pitch + pitchStep * this.onTargetScale * this.assistGain;
        } else {
            nextYaw = yaw + yawStep;
            nextPitch = pitch + pitchStep;
        }
        double frame = AimAssistModule.frameTime();
        nextYaw = this.quantize(yaw, nextYaw, frame, true);
        nextPitch = class_3532.method_15363((float)this.quantize(pitch, class_3532.method_15363((float)nextPitch, (float)-90.0f, (float)90.0f), frame, false), (float)-90.0f, (float)90.0f);
        player.method_36456(nextYaw);
        player.method_36457(nextPitch);
        this.lastYaw = nextYaw;
        this.lastPitch = nextPitch;
        this.applied = true;
    }

    private float updateMouse(float yaw, float pitch, float dt) {
        if (!this.applied) {
            this.mouseYaw = 0.0f;
            this.mousePitch = 0.0f;
            this.mouseActivity = 0.0f;
            return 0.0f;
        }
        this.mouseYaw = class_3532.method_15393((float)(yaw - this.lastYaw));
        this.mousePitch = class_3532.method_15363((float)(pitch - this.lastPitch), (float)-45.0f, (float)45.0f);
        float deadzone = (float)Math.max(0.004, AimAssistModule.frameTime() * 0.08);
        float motion = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch) * 1.35f;
        float active = class_3532.method_15363((float)((motion - deadzone) / 0.62f), (float)0.0f, (float)1.0f);
        this.mouseActivity = class_3532.method_16439((float)class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 18.0f), (float)0.16f, (float)0.48f), (float)this.mouseActivity, (float)active);
        return this.mouseActivity;
    }

    private class_243 smoothDelta(class_243 delta, float dt, boolean captured) {
        if (captured || this.smoothedDelta == null) {
            this.smoothedDelta = delta;
            return delta;
        }
        float t = class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 13.0f), (float)0.06f, (float)0.28f);
        this.smoothedDelta = this.smoothedDelta.method_1019(delta.method_1020(this.smoothedDelta).method_1021((double)t));
        return this.smoothedDelta;
    }

    private float frameDelta() {
        long now = System.nanoTime();
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = now;
            return 0.016666668f;
        }
        float dt = (float)(now - this.lastFrameNanos) / 1.0E9f;
        this.lastFrameNanos = now;
        return class_3532.method_15363((float)dt, (float)0.004166667f, (float)0.033333335f);
    }

    private void resetAssist() {
        this.wanderTargetId = -1;
        this.wanderX = 0.0f;
        this.wanderZ = 0.0f;
        this.trackedId = -1;
        this.onTargetHold = 0;
        this.onTargetScale = 1.0f;
        this.assistGain = 0.36f;
        this.captureYawStep = 0.0f;
        this.capturePitchStep = 0.0f;
        this.smoothedDelta = null;
        this.shakeTargetId = -1;
        this.nextShakeNanos = 0L;
        this.shakeYaw = 0.0f;
        this.shakePitch = 0.0f;
        this.magnetLock = 0.0f;
        this.magnetAmount = 0.0f;
        this.lastYaw = 0.0f;
        this.lastPitch = 0.0f;
        this.mouseYaw = 0.0f;
        this.mousePitch = 0.0f;
        this.mouseActivity = 0.0f;
        this.applied = false;
        this.yawQuantError = 0.0f;
        this.pitchQuantError = 0.0f;
    }

    private float onTargetShakeScale() {
        return class_3532.method_15363((float)(class_3532.method_15363((float)this.shakeStrength.getFloatValue(), (float)0.0f, (float)10.0f) / 10.0f * 0.22f), (float)0.0f, (float)0.22f);
    }

    private static float expApproach(float dt, float base, float extra) {
        return 1.0f - (float)Math.exp(-dt * (base + extra));
    }

    private float quantize(float from, float to, double step, boolean yawAxis) {
        if (step <= 0.0) {
            return to;
        }
        float error = yawAxis ? this.yawQuantError : this.pitchQuantError;
        float delta = to - from + error;
        float snapped = (float)((double)Math.round((double)delta / step) * step);
        float leftover = class_3532.method_15363((float)(delta - snapped), (float)((float)(-step)), (float)((float)step));
        if (yawAxis) {
            this.yawQuantError = leftover;
        } else {
            this.pitchQuantError = leftover;
        }
        return from + snapped;
    }

    private float assistGain(float yawDelta, float pitchDelta, float mouseWeight) {
        float mouse = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch) * 1.35f;
        if (mouse <= 0.002f) {
            return 0.36f;
        }
        float aligned = this.mouseYaw * yawDelta + this.mousePitch * pitchDelta * 0.72f;
        float step = Math.max(0.001f, Math.abs(yawDelta) + Math.abs(pitchDelta) * 0.72f);
        float match = class_3532.method_15363((float)(aligned / Math.max(0.001f, mouse * step)), (float)-1.0f, (float)1.0f);
        float scale = match > 0.0f ? class_3532.method_16439((float)match, (float)0.62f, (float)1.0f) : 0.3f;
        return class_3532.method_15363((float)((0.42f + mouseWeight * 0.86f) * scale), (float)0.24f, (float)1.22f);
    }

    private static double effectiveGravity(class_1309 entity) {
        double gravity = 0.08;
        boolean falling = entity.method_18798().field_1351 <= 0.0;
        return falling && entity.method_6059(class_1294.field_5906) ? Math.min(gravity, 0.01) : gravity;
    }

    private boolean lookingAt(class_746 player, class_1309 entity, float yaw, float pitch) {
        float range = (float)Math.min((double)this.rotationRange.getFloatValue(), Math.max((double)this.attackRange.getFloatValue(), (double)player.method_5739((class_1297)entity) + 0.75));
        if (ReachHelper.raycastEntity((class_1297)entity, new Angle(yaw, pitch), range, this.throughWalls.isEnabled()) != null) {
            return true;
        }
        class_243 eye = player.method_33571();
        class_243 end = eye.method_1019(class_243.method_1030((float)pitch, (float)yaw).method_1021((double)range));
        return entity.method_5829().method_1009(0.08, 0.22, 0.08).method_992(eye, end).isPresent();
    }

    private void selectTarget() {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && !player.method_5805()) {
            this.enabledSetting.setEnabled(false);
        }
        if (!this.enabledSetting.isEnabled() || player == null || class_310.method_1551().field_1687 == null) {
            this.targetSelector.update();
            this.target = null;
            return;
        }
        if (!this.focusOne.isEnabled()) {
            this.targetSelector.update();
        }
        TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), "Legit", this.fov.getIntValue());
        this.target = this.targetSelector.process(filter, this.rotationRange.getFloatValue(), this.sorting.getSelectedOption(), this.throughWalls.isEnabled());
    }

    private class_243 aimPoint(class_746 player, class_1309 entity) {
        class_238 box = entity.method_5829();
        class_243 center = box.method_1005();
        class_243 eye = player.method_33571();
        class_243 offset = new class_243(center.field_1352 - eye.field_1352, 0.0, center.field_1350 - eye.field_1350);
        class_243 side = offset.method_1027() > 1.0E-5 ? new class_243(-offset.field_1350, 0.0, offset.field_1352).method_1029() : class_243.field_1353;
        float strength = class_3532.method_15363((float)(this.shakeStrength.getFloatValue() / 10.0f), (float)0.0f, (float)1.0f);
        float wobble = (float)Math.sin((double)System.nanoTime() * 1.0E-9 * 1.35 + (double)((float)entity.method_5628() * 0.37f)) * 0.006f * strength;
        double y = box.field_1322 + (double)(entity.method_17682() * class_3532.method_15363((float)this.aimHeight.getFloatValue(), (float)0.18f, (float)0.92f)) + (double)this.shakePitch;
        return new class_243(center.field_1352 + side.field_1352 * (double)(this.shakeYaw + wobble), class_3532.method_15350((double)y, (double)(box.field_1322 + 0.18), (double)(box.field_1325 - 0.08)), center.field_1350 + side.field_1350 * (double)(this.shakeYaw + wobble));
    }

    private void updateWander(float dt, class_1309 entity) {
        int id = entity.method_5628();
        if (this.wanderTargetId != id) {
            this.wanderX = 0.0f;
            this.wanderZ = 0.0f;
            this.wanderTargetId = id;
        }
        float strength = class_3532.method_15363((float)this.shakeStrength.getFloatValue(), (float)0.0f, (float)10.0f);
        if (strength <= 0.001f) {
            this.wanderX = 0.0f;
            this.wanderZ = 0.0f;
            return;
        }
        double time = (double)System.nanoTime() * 1.0E-9;
        float phase = (float)(id & 0x3FF) * 0.173f;
        float amplitude = strength * 0.013f;
        float x = (float)(Math.sin(time * 1.47 + (double)phase) + Math.sin(time * 2.13 + (double)(phase * 0.41f)) * (double)0.32f) * amplitude;
        float z = (float)(Math.cos(time * 1.31 + (double)(phase * 0.7f)) + Math.sin(time * 1.91 + (double)phase + (double)1.4f) * (double)0.28f) * amplitude;
        float t = class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 5.2f), (float)0.025f, (float)0.16f);
        this.wanderX = class_3532.method_16439((float)t, (float)this.wanderX, (float)class_3532.method_15363((float)x, (float)(-amplitude), (float)amplitude));
        this.wanderZ = class_3532.method_16439((float)t, (float)this.wanderZ, (float)class_3532.method_15363((float)z, (float)(-amplitude), (float)amplitude));
    }

    private float[] magnetAim(float yaw, float pitch, float yawDelta, float pitchDelta, float dt, boolean captured, boolean onTarget) {
        if (captured) {
            this.magnetLock = 0.0f;
            this.magnetAmount = 0.0f;
        }
        float mouse = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch);
        float deadzone = (float)Math.max(0.02, AimAssistModule.frameTime() * 0.5);
        if (mouse < deadzone) {
            this.magnetAmount *= 0.6f;
            return new float[]{yaw, pitch};
        }
        float absYaw = Math.abs(yawDelta);
        float absPitch = Math.abs(pitchDelta);
        float error = absYaw + absPitch * 0.62f;
        float fov = class_3532.method_15363((float)(this.fov.getFloatValue() * 0.5f), (float)24.0f, (float)180.0f);
        if (!onTarget && error > fov) {
            this.magnetAmount *= 0.6f;
            return new float[]{yaw, pitch};
        }
        float step = Math.max(0.001f, absYaw + absPitch);
        float aligned = this.mouseYaw * yawDelta + this.mousePitch * pitchDelta;
        float match = class_3532.method_15363((float)(aligned / (mouse * step)), (float)0.0f, (float)1.0f);
        float lock = onTarget ? class_3532.method_15363((float)(1.0f - error / 8.5f), (float)0.35f, (float)1.0f) : 0.0f;
        this.magnetLock = class_3532.method_16439((float)class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, onTarget ? 7.5f : 4.2f), (float)0.02f, (float)(onTarget ? 0.2f : 0.12f)), (float)this.magnetLock, (float)lock);
        float strength = class_3532.method_15363((float)this.magnetStrength.getFloatValue(), (float)0.1f, (float)2.5f);
        float yawScale = class_3532.method_15363((float)(this.yawSpeed.getFloatValue() / 60.0f), (float)0.5f, (float)3.0f);
        float wanted = class_3532.method_15363((float)(0.42f * yawScale * strength), (float)0.0f, (float)0.95f) * match * class_3532.method_16439((float)this.magnetLock, (float)1.0f, (float)0.45f);
        float approach = wanted > this.magnetAmount ? class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 42.0f), (float)0.2f, (float)0.85f) : class_3532.method_15363((float)AimAssistModule.expApproach(dt, 0.0f, 12.0f), (float)0.08f, (float)0.4f);
        this.magnetAmount = class_3532.method_16439((float)approach, (float)this.magnetAmount, (float)wanted);
        float addYaw = yawDelta * this.magnetAmount;
        float addPitch = pitchDelta * this.magnetAmount * 0.92f;
        float clampScale = class_3532.method_15363((float)(0.85f * strength), (float)0.3f, (float)1.7f);
        float yawLimit = Math.abs(this.mouseYaw) * clampScale;
        float pitchLimit = Math.abs(this.mousePitch) * clampScale + Math.abs(this.mouseYaw) * 0.12f;
        addYaw = AimAssistModule.clampToDelta(class_3532.method_15363((float)addYaw, (float)(-yawLimit), (float)yawLimit), yawDelta);
        addPitch = AimAssistModule.clampToDelta(class_3532.method_15363((float)addPitch, (float)(-pitchLimit), (float)pitchLimit), pitchDelta);
        return new float[]{yaw + addYaw, pitch + addPitch};
    }

    private static float clampToDelta(float value, float delta) {
        float abs = Math.abs(delta);
        return abs <= 0.001f ? 0.0f : class_3532.method_15363((float)value, (float)(-abs), (float)abs);
    }

    private boolean captureTarget(int id) {
        if (this.trackedId == id) {
            return false;
        }
        this.trackedId = id;
        this.onTargetHold = 0;
        this.onTargetScale = 1.0f;
        this.smoothedDelta = null;
        this.magnetLock = 0.0f;
        this.magnetAmount = 0.0f;
        return true;
    }

    private static double frameTime() {
        return class_310.method_1551().method_61966().method_60636();
    }
}

