/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2678
 *  net.minecraft.class_2743
 *  net.minecraft.class_2749
 *  net.minecraft.class_2767
 *  net.minecraft.class_2828
 *  net.minecraft.class_2828$class_2831
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_2743;
import net.minecraft.class_2749;
import net.minecraft.class_2767;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.AttackWindowEvent;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.event.OutgoingPacketEvent;
import ru.byazen.event.PreAttackEvent;
import ru.byazen.misc.AttackOptions;
import ru.byazen.misc.AttackUrgency;
import ru.byazen.misc.CorrectionMode;
import ru.byazen.misc.HotbarTracker;
import ru.byazen.misc.RaycastMode;
import ru.byazen.misc.ReachHelper;
import ru.byazen.misc.RotationApplyResult;
import ru.byazen.misc.ShieldBreaker;
import ru.byazen.misc.SprintReset;
import ru.byazen.misc.SprintResetMode;
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
import ru.byazen.util.AimCalculator;
import ru.byazen.util.Angle;
import ru.byazen.util.AuraProcessor;
import ru.byazen.util.CriticalsHandler;
import ru.byazen.util.RotationController;
import ru.byazen.util.RotationIntent;
import ru.byazen.util.TargetSelector;

public class AttackAuraModule
extends Module
implements ConfigSerializable {
    private static volatile AttackAuraModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting range;
    private final NumberSetting additionalRange;
    private final MultiSelectSetting targets;
    private final ModeSetting sorting;
    private final ModeSetting rotationMode;
    private final NumberSetting fov;
    private final ModeSetting attackMode;
    private final NumberSetting cps;
    private final ModeSetting earlyHit;
    private final NumberSetting accuracy;
    private final ModeSetting correctionMode;
    private final ModeSetting sprintReset;
    private final BooleanSetting noSprintNear;
    private final NumberSetting noSprintDistance;
    private final BooleanSetting ignoreWhileUsing;
    private final BooleanSetting throughWalls;
    private final BooleanSetting criticals;
    private final BooleanSetting jumpOnly;
    private final NumberSetting maceFallDistance;
    private final BooleanSetting swordOnly;
    private final BooleanSetting breakShield;
    private final BooleanSetting desyncShield;
    private final BooleanSetting tpsSync;
    private final HotbarTracker hotbarTracker = new HotbarTracker();
    private final Map<Integer, Integer> hitDelayHistogram = new HashMap<Integer, Integer>();
    private TargetSelector targetSelector;
    private AimCalculator aimCalculator;
    private CriticalsHandler criticalsHandler;
    private AuraProcessor auraProcessor;
    private class_1309 target;
    private boolean rotationReady;
    private boolean adaptiveReady;
    private boolean calibratingAdaptive;
    private boolean heardHitSound;
    private int silenceTicks;
    private int quietTicks;
    private int calibrationTicks;
    private int learnedHitDelay;
    private int packetsThisWindow;

    public AttackAuraModule(EventBus eventBus) {
        super(eventBus, "attack_aura", "Attack Aura", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0431\u044c\u0451\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432\u043e\u043a\u0440\u0443\u0433", ModuleCategory.valueOf("COMBAT"), new String[0]);
        MultiSelectSetting targetsSetting;
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("Test killaura module").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.range = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 12.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435").id("range").description("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0434\u043b\u044f \u0430\u0442\u0430\u043a\u0438")).build();
        this.registerSetting(this.range);
        this.additionalRange = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 6.0).defaultValue(2.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435").id("additional_range").description("\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043f\u043e\u0438\u0441\u043a\u0430 \u0446\u0435\u043b\u0435\u0439 \u0438 \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f")).build();
        this.registerSetting(this.additionalRange);
        this.targets = targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers").selectAll(false).optionListEnabled(false).name("\u0426\u0435\u043b\u0438").id("targets").description("\u0422\u0438\u043f\u044b \u0446\u0435\u043b\u0435\u0439 \u0434\u043b\u044f \u0430\u0442\u0430\u043a\u0438").withKeybind()).build();
        this.registerSetting(targetsSetting);
        this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Health", "Distance", "Crosshair").defaultOption("Distance").name("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430").id("sorting").description("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430 \u0446\u0435\u043b\u0435\u0439 \u0434\u043b\u044f \u0430\u0442\u0430\u043a\u0438").withKeybind()).build();
        this.registerSetting(this.sorting);
        this.rotationMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Simple", "FT Snap", "RW", "Spooky", "Spooky Test").defaultOption("Simple").name("\u0420\u0435\u0436\u0438\u043c").id("mode").description("\u0420\u0435\u0436\u0438\u043c \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f").withKeybind()).build();
        this.registerSetting(this.rotationMode);
        this.fov = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 360.0).defaultValue(180.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("FOV").id("fov").description("\u041f\u043e\u043b\u0435 \u0437\u0440\u0435\u043d\u0438\u044f \u0434\u043b\u044f \u043f\u043e\u0438\u0441\u043a\u0430 \u0446\u0435\u043b\u0438").visibleWhen(() -> "FT Snap".equals(this.rotationMode.getSelectedOption()))).build();
        this.registerSetting(this.fov);
        this.attackMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("1.9+", "1.8").defaultOption("1.9+").name("\u0420\u0435\u0436\u0438\u043c \u0430\u0442\u0430\u043a\u0438").id("attack_mode").description("\u0420\u0435\u0436\u0438\u043c \u0430\u0442\u0430\u043a\u0438").withKeybind()).build();
        this.registerSetting(this.attackMode);
        this.cps = ((NumberSettingBuilder)NumberSetting.builder().range(5.0, 20.0).defaultValue(15.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("CPS").id("cps").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043a\u043b\u0438\u043a\u043e\u0432 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443").visibleWhen(() -> "1.8".equals(this.attackMode.getSelectedOption()))).build();
        this.registerSetting(this.cps);
        this.earlyHit = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("\u0412\u0441\u0435\u0433\u0434\u0430", "\u0410\u0434\u0430\u043f\u0442\u0438\u0432\u043d\u044b\u0439", "\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d").defaultOption("\u0410\u0434\u0430\u043f\u0442\u0438\u0432\u043d\u044b\u0439").name("\u0420\u0430\u043d\u043d\u0438\u0439 \u0443\u0434\u0430\u0440").id("early_hit").description("\u041c\u043e\u0434\u0443\u043b\u044c \u0431\u044c\u0451\u0442 \u0440\u0430\u043d\u044c\u0448\u0435 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0433\u043e \u0443\u0434\u0430\u0440\u0430\n\u0420\u0435\u043a\u043e\u043c\u0435\u043d\u0434\u0443\u0435\u0442\u0441\u044f \u0430\u0434\u0430\u043f\u0442\u0438\u0432\u043d\u044b\u0439 \u0440\u0435\u0436\u0438\u043c, \u0432\u0441\u0435\u0433\u0434\u0430 - \u043d\u0430 \u0441\u0442\u0440\u0430\u0445 \u0438 \u0440\u0438\u0441\u043a").withKeybind()).build();
        this.registerSetting(this.earlyHit);
        this.accuracy = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 100.0).defaultValue(100.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("\u0422\u043e\u0447\u043d\u043e\u0441\u0442\u044c").id("accuracy").description("\u0428\u0430\u043d\u0441 \u043f\u043e\u043f\u0430\u0441\u0442\u044c \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435")).build();
        this.registerSetting(this.accuracy);
        this.correctionMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Focused", "Free", "Lock").defaultOption("Focused").name("\u0420\u0435\u0436\u0438\u043c \u043a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u0438").id("correction_mode").description("\u0420\u0435\u0436\u0438\u043c \u043a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f").withKeybind()).build();
        this.registerSetting(this.correctionMode);
        this.sprintReset = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Packet", "Semi-Legit", "Legit").defaultOption("Semi-Legit").name("\u0421\u0431\u0440\u043e\u0441 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").id("sprint_reset").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0431\u0440\u043e\u0441\u0430 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").withKeybind()).build();
        this.registerSetting(this.sprintReset);
        this.noSprintNear = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0411\u0435\u0437 \u0441\u043f\u0440\u0438\u043d\u0442\u0430 \u0443 \u0446\u0435\u043b\u0438").id("no_sprint_near").description("\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0441\u043f\u0440\u0438\u043d\u0442\u0430 \u0440\u044f\u0434\u043e\u043c \u0441 \u0446\u0435\u043b\u044c\u044e").withKeybind().visibleWhen(() -> "Legit".equals(this.sprintReset.getSelectedOption()))).build();
        this.registerSetting(this.noSprintNear);
        this.noSprintDistance = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 5.0).defaultValue(3.0).multiplier(1.0).precision(1).animationSpeed(20.0f).showMarkers().snapTo(0.5).name("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0431\u0435\u0437 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").id("no_sprint_distance").description("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u0434\u043e \u0446\u0435\u043b\u0438 \u0434\u043b\u044f \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u0441\u043f\u0440\u0438\u043d\u0442\u0430").visibleWhen(() -> "Legit".equals(this.sprintReset.getSelectedOption()) && this.noSprintNear.isEnabled())).build();
        this.registerSetting(this.noSprintDistance);
        this.ignoreWhileUsing = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0418\u0433\u043d\u043e\u0440 \u043f\u0440\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438").id("ignore_while_using").description("\u041e\u0442\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043c\u043e\u0434\u0443\u043b\u044c, \u043f\u043e\u043a\u0430 \u0432\u044b \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442").withKeybind()).build();
        this.registerSetting(this.ignoreWhileUsing);
        this.throughWalls = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0421\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b").id("through_walls").description("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0430\u0442\u0430\u043a\u043e\u0432\u0430\u0442\u044c \u0441\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b").withKeybind()).build();
        this.registerSetting(this.throughWalls);
        this.criticals = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u041a\u0440\u0438\u0442\u044b").id("criticals").description("\u0410\u0442\u0430\u043a\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u043c \u0443\u0440\u043e\u043d\u043e\u043c").withKeybind()).build();
        this.registerSetting(this.criticals);
        this.jumpOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u043f\u0440\u044b\u0436\u043a\u0435").id("jump_only").description("\u041d\u0430\u043d\u043e\u0441\u0438\u0442 \u043a\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0443\u0440\u043e\u043d \u0442\u043e\u043b\u044c\u043a\u043e\n\u043f\u0440\u0438 \u0437\u0430\u0436\u0430\u0442\u043e\u0439 \u043a\u043d\u043e\u043f\u043a\u0435 \u043f\u0440\u044b\u0436\u043a\u0430").withKeybind().visibleWhen(() -> this.criticals.isEnabled())).build();
        this.registerSetting(this.jumpOnly);
        this.maceFallDistance = ((NumberSettingBuilder)NumberSetting.builder().range(1.5, 8.0).defaultValue(1.6).multiplier(1.0).precision(1).animationSpeed(20.0f).snapTo(0.1).name("\u0411\u0443\u043b\u0430\u0432\u0430: \u043c\u0438\u043d. \u043f\u0430\u0434\u0435\u043d\u0438\u0435").id("mace_fall_distance").description("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043f\u0430\u0434\u0435\u043d\u0438\u044f, \u0441 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u0431\u0443\u043b\u0430\u0432\u0430 \u0431\u044c\u0451\u0442 \u0441\u043c\u044d\u0448\u0435\u043c")).build();
        this.registerSetting(this.maceFallDistance);
        this.swordOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u043c\u0435\u0447").id("sword_only").description("\u0411\u0438\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u043c\u0435\u0447\u043e\u043c").withKeybind()).build();
        this.registerSetting(this.swordOnly);
        this.breakShield = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u041b\u043e\u043c\u0430\u0442\u044c \u0449\u0438\u0442").id("break_shield").description("\u041b\u043e\u043c\u0430\u0442\u044c \u0449\u0438\u0442\u044b \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u043e\u0432").withKeybind()).build();
        this.registerSetting(this.breakShield);
        this.desyncShield = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0414\u0435\u0441\u0438\u043d\u0445\u0440. \u0449\u0438\u0442\u0430").id("desync_shield").description("\u0414\u0435\u0441\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u0449\u0438\u0442\u0430").withKeybind()).build();
        this.registerSetting(this.desyncShield);
        this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("TPS Sync").id("tps_sync").description("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u043a\u0443\u043b\u0434\u0430\u0443\u043d \u0441 TPS \u0441\u0435\u0440\u0432\u0435\u0440\u0430").withKeybind()).build();
        this.registerSetting(this.tpsSync);
    }

    @Override
    protected void initialize() {
        this.targetSelector = new TargetSelector();
        this.aimCalculator = new AimCalculator();
        this.criticalsHandler = new CriticalsHandler();
        this.auraProcessor = new AuraProcessor(this.criticalsHandler);
        this.listen(ClientTickEvent.class, event -> this.onClientTick());
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
        this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
        this.listen(AttackWindowEvent.class, this::onAttackWindow);
        this.listen(PreAttackEvent.class, this::onPreAttack);
    }

    public boolean isActive() {
        return this.enabledSetting.isEnabled();
    }

    public class_1309 getLivingEntity() {
        return this.target;
    }

    public class_1309 getTarget() {
        return this.target;
    }

    public static boolean isActive4() {
        AttackAuraModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.target != null;
    }

    public static boolean hasTarget() {
        return AttackAuraModule.isActive4();
    }

    private void onClientTick() {
        class_1309 selected;
        this.criticalsHandler.tick();
        this.auraProcessor.tick();
        SprintReset.setActive(false);
        ShieldBreaker.reset();
        RotationController rotations = ByAzenClient.getRotationController();
        if (rotations == null) {
            return;
        }
        if (!this.enabledSetting.isEnabled() || class_310.method_1551().field_1724 == null) {
            this.targetSelector.reset();
            this.clearTarget();
            this.stopRotations(rotations);
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), this.sorting.getSelectedOption(), this.fov.getIntValue());
        float searchRange = this.range.getFloatValue() + this.additionalRange.getFloatValue();
        if (this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
            searchRange = Math.max(searchRange, this.range.getFloatValue() + 1.5f);
        }
        if ((selected = this.targetSelector.findTarget(filter, searchRange, this.sorting.getSelectedOption(), this.throughWalls.isEnabled())) == null) {
            this.clearTarget();
            this.holdIdleRotation(rotations);
            return;
        }
        this.tryBreakShield(player);
        SprintReset.setActive(this.shouldBlockSprint(selected));
        class_243 aimPoint = this.aimCalculator.calculateAimPoint(selected, this.currentLook(), this.range.getFloatValue(), searchRange, this.throughWalls.isEnabled());
        if (aimPoint == null) {
            this.clearTarget();
            this.holdIdleRotation(rotations);
            return;
        }
        class_243 eyes = class_310.method_1551().field_1724.method_33571();
        Angle targetAngle = Angle.fromVectors(eyes, aimPoint);
        CorrectionMode correction = CorrectionMode.fromName(this.correctionMode.getSelectedOption());
        AttackUrgency urgency = this.resolveAttackUrgency(selected, targetAngle);
        RotationIntent intent = new RotationIntent(selected, aimPoint, targetAngle, urgency, correction, false);
        RotationApplyResult result = rotations.apply(intent, this.rotationMode.getSelectedOption());
        this.target = selected;
        this.rotationReady = result.isReady();
        if (urgency == AttackUrgency.HIT && this.rotationReady) {
            rotations.markAttacked();
        }
    }

    private void tryAttack(boolean ready) {
        if (!this.enabledSetting.isEnabled() || this.target == null) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || player.method_7325()) {
            return;
        }
        if (this.ignoreWhileUsing.isEnabled() && class_310.method_1551().field_1690.field_1904.method_1434()) {
            return;
        }
        if (this.swordOnly.isEnabled() && !this.isHoldingSword()) {
            return;
        }
        this.auraProcessor.getHitCooldown().setAttackRequested(ready);
        this.auraProcessor.getHitCooldown().setTpsSync(this.tpsSync.isEnabled());
        Angle look = ByAzenClient.getRotationController().getAngle();
        if (look == null) {
            return;
        }
        AttackOptions options = new AttackOptions(look, this.range.getFloatValue(), this.cps.getIntValue(), true, true, SprintResetMode.process(this.sprintReset.getSelectedOption()), this.criticals.isEnabled(), this.throughWalls.isEnabled() ? RaycastMode.THROUGH_WALLS : RaycastMode.VISIBLE, "1.8".equals(this.attackMode.getSelectedOption()), this.accuracy.getValue(), this.breakShield.isEnabled(), this.desyncShield.isEnabled(), this.jumpOnly.isEnabled(), (float)this.maceFallDistance.getValue());
        if (this.auraProcessor.attack(this.target, options)) {
            ByAzenClient.getRotationController().notifyHit();
        }
    }

    private void onAttackWindow(AttackWindowEvent event) {
        String mode;
        if (event.isPre()) {
            this.updateAdaptiveWindow();
        }
        if ("\u041e\u0442\u043a\u043b\u044e\u0447\u0435\u043d".equals(mode = this.earlyHit.getSelectedOption())) {
            if (event.isPre()) {
                this.tryAttack(true);
            }
            return;
        }
        if ("\u0412\u0441\u0435\u0433\u0434\u0430".equals(mode)) {
            this.tryAttack(!event.isPost());
            return;
        }
        if (this.isAdaptiveEarlyHit()) {
            this.tryAttack(event.isPre());
        } else {
            this.tryAttack(!event.isPost());
        }
    }

    private boolean isAdaptiveEarlyHit() {
        return "\u0410\u0434\u0430\u043f\u0442\u0438\u0432\u043d\u044b\u0439".equals(this.earlyHit.getSelectedOption()) && this.adaptiveReady;
    }

    private void updateAdaptiveWindow() {
        boolean wasReady = this.adaptiveReady;
        this.tickAdaptiveReady();
        if (this.isAdaptiveEarlyHit()) {
            if (!wasReady) {
                this.calibratingAdaptive = true;
                this.calibrationTicks = 0;
                this.learnedHitDelay = 0;
                this.hitDelayHistogram.clear();
            } else if (this.calibratingAdaptive) {
                this.hitDelayHistogram.merge(this.packetsThisWindow, 1, Integer::sum);
                if (++this.calibrationTicks >= 40) {
                    this.learnedHitDelay = this.mostCommonHitDelay();
                    this.calibratingAdaptive = false;
                }
            }
            this.packetsThisWindow = 0;
            this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(false);
        } else {
            this.packetsThisWindow = 0;
            this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
        }
    }

    private void tickAdaptiveReady() {
        if (!this.adaptiveReady) {
            if (this.heardHitSound) {
                this.quietTicks = 0;
                if (++this.silenceTicks >= 50) {
                    this.adaptiveReady = true;
                }
            } else if (++this.quietTicks >= 4) {
                this.silenceTicks = 0;
            }
        }
        this.heardHitSound = false;
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        class_2596<?> packet;
        if (event.getPacket() instanceof class_2749) {
            this.criticalsHandler.getVelocityTracker().reset();
        }
        if ((packet = event.getPacket()) instanceof class_2743) {
            class_2743 velocityPacket = (class_2743)packet;
            class_746 player = class_310.method_1551().field_1724;
            if (player != null && velocityPacket.method_11818() == player.method_5628()) {
                this.criticalsHandler.getVelocityTracker().setYVelocity(velocityPacket.method_73085().field_1351);
            }
        }
        if (event.getPacket() instanceof class_2767) {
            this.heardHitSound = true;
        } else if (event.getPacket() instanceof class_2678) {
            this.criticalsHandler.getVelocityTracker().reset();
            this.adaptiveReady = false;
            this.silenceTicks = 0;
            this.quietTicks = 0;
            this.heardHitSound = false;
            this.calibratingAdaptive = false;
            this.calibrationTicks = 0;
            this.learnedHitDelay = 0;
            this.packetsThisWindow = 0;
            this.hitDelayHistogram.clear();
            this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
        }
    }

    private void onOutgoingPacket(OutgoingPacketEvent event) {
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2828.class_2831) {
            class_2828.class_2831 lookAndMove = (class_2828.class_2831)packet;
            this.criticalsHandler.getVelocityTracker().onMovementPacket(lookAndMove);
        }
        if (!(event.getPacket() instanceof class_2828)) {
            return;
        }
        if (!this.isAdaptiveEarlyHit()) {
            return;
        }
        ++this.packetsThisWindow;
        if (!this.calibratingAdaptive && this.learnedHitDelay > 0 && this.packetsThisWindow >= this.learnedHitDelay) {
            this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
        }
    }

    private void onPreAttack(PreAttackEvent event) {
        RotationController rotations = ByAzenClient.getRotationController();
        if (this.enabledSetting.isEnabled() && rotations != null && rotations.ticksSinceHit() < 4) {
            event.cancel();
        }
        if (!event.isCancelled()) {
            this.criticalsHandler.onAttack();
        }
    }

    private AttackUrgency resolveAttackUrgency(class_1309 selected, Angle targetAngle) {
        boolean cooldownReady;
        if (!"FT Snap".equals(this.rotationMode.getSelectedOption())) {
            return AttackUrgency.SKIP;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return AttackUrgency.SKIP;
        }
        if (this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
            if (!this.auraProcessor.getHitCooldown().isActive()) {
                return AttackUrgency.SKIP;
            }
            if (ReachHelper.raycastEntity((class_1297)selected, targetAngle, this.range.getFloatValue(), this.throughWalls.isEnabled()) != selected) {
                return AttackUrgency.SKIP;
            }
            boolean falling = !player.method_24828() && !player.method_6101() && player.field_6017 > 0.0;
            return falling ? AttackUrgency.HIT : AttackUrgency.SKIP;
        }
        boolean bl = cooldownReady = "1.8".equals(this.attackMode.getSelectedOption()) || player.method_7261(0.5f) >= 0.9f;
        if (!cooldownReady) {
            return AttackUrgency.SKIP;
        }
        if (!this.auraProcessor.getHitCooldown().isActive()) {
            return AttackUrgency.SKIP;
        }
        if (ReachHelper.raycastEntity((class_1297)selected, targetAngle, this.range.getFloatValue(), this.throughWalls.isEnabled()) != selected) {
            return AttackUrgency.SKIP;
        }
        RaycastMode raycastMode = this.throughWalls.isEnabled() ? RaycastMode.THROUGH_WALLS : RaycastMode.VISIBLE;
        return this.criticalsHandler.process(this.criticals.isEnabled(), raycastMode) ? AttackUrgency.HIT : AttackUrgency.SKIP;
    }

    private boolean shouldBlockSprint(class_1309 selected) {
        return this.shouldStopSprintNearTarget(selected) || this.shouldStopSprintForMace(selected) || this.shouldStopSprintForShieldBreak(selected);
    }

    private boolean shouldStopSprintNearTarget(class_1309 selected) {
        if (!this.noSprintNear.isEnabled()) {
            return false;
        }
        if (!"Legit".equals(this.sprintReset.getSelectedOption())) {
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null && TargetSelector.distanceTo(selected) <= (double)this.noSprintDistance.getFloatValue();
    }

    private boolean shouldStopSprintForMace(class_1309 selected) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || !this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
            return false;
        }
        return TargetSelector.distanceTo(selected) <= (double)(this.range.getFloatValue() + 1.5f);
    }

    private boolean shouldStopSprintForShieldBreak(class_1309 selected) {
        if (!"Legit".equals(this.sprintReset.getSelectedOption()) || !ShieldBreaker.isActive()) {
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return false;
        }
        if (!this.auraProcessor.getHitCooldown().isActive()) {
            return false;
        }
        return !(TargetSelector.distanceTo(selected) > (double)this.range.getFloatValue());
    }

    private void tryBreakShield(class_746 player) {
        class_1799 predicted;
        if (!"Legit".equals(this.sprintReset.getSelectedOption()) && !this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
            return;
        }
        try {
            predicted = this.hotbarTracker.simulate(1);
        }
        catch (Throwable ignored) {
            return;
        }
        if (predicted == null) {
            return;
        }
        int maxUseTime = predicted.method_7935((class_1309)player);
        double useProgress = maxUseTime > 0 ? (double)player.method_6048() / (double)maxUseTime : 0.0;
        ShieldBreaker.consider(player.method_6039(), useProgress);
    }

    private void stopRotations(RotationController rotations) {
        if (rotations.hasRotation()) {
            rotations.reset();
        }
    }

    private void holdIdleRotation(RotationController rotations) {
        if (!rotations.hasRotation()) {
            return;
        }
        rotations.apply(RotationIntent.empty(), this.rotationMode.getSelectedOption());
    }

    private void clearTarget() {
        this.target = null;
        this.rotationReady = false;
    }

    private Angle currentLook() {
        Angle rotation = ByAzenClient.getRotationController().getAngle();
        if (rotation != null) {
            return rotation;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null ? new Angle(player.method_36454(), player.method_36455()) : Angle.ZERO;
    }

    private boolean isHoldingSword() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_6047().method_31573(class_3489.field_42611);
    }

    private int mostCommonHitDelay() {
        int delay = 0;
        int bestCount = -1;
        for (Map.Entry<Integer, Integer> entry : this.hitDelayHistogram.entrySet()) {
            int packets = entry.getKey();
            int count = entry.getValue();
            if (count < bestCount || count == bestCount && packets <= delay) continue;
            delay = packets;
            bestCount = count;
        }
        return delay;
    }
}

