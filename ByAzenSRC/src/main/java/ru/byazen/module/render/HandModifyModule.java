/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1753
 *  net.minecraft.class_1764
 *  net.minecraft.class_1792
 *  net.minecraft.class_1835
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_746
 */
package ru.byazen.module.render;

import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1835;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.FirstPersonItemTransformEvent;
import ru.byazen.event.FirstPersonSwingTransformEvent;
import ru.byazen.event.HandSwingSpeedEvent;
import ru.byazen.misc.HandRenderTransforms;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.combat.AttackAuraModule;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public final class HandModifyModule
extends Module
implements ConfigSerializable {
    private static volatile HandModifyModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting positionHand;
    private final NumberSetting mainHandX;
    private final NumberSetting mainHandY;
    private final NumberSetting mainHandZ;
    private final NumberSetting offHandX;
    private final NumberSetting offHandY;
    private final NumberSetting offHandZ;
    private final BooleanSetting animationHand;
    private final ModeSetting swingType;
    private final BooleanSetting rotate;
    private final NumberSetting rotateSpeed;
    private final BooleanSetting attackAuraOnly;
    private final BooleanSetting staticPosition;
    private long rotateStartNanos;
    private AttackAuraModule attackAuraModule;

    public HandModifyModule(EventBus eventBus) {
        super(eventBus, "hand_modify", "Hand Modify", "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 \u043f\u043e\u0437\u0438\u0446\u0438\u0438 \u0440\u0443\u043a\u0438 \u0438 \u043a\u0430\u0441\u0442\u043e\u043c\u043d\u0430\u044f \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043c\u0430\u0445\u0430", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043c\u043e\u0434\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u044e \u0440\u0443\u043a\u0438").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.positionHand = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Position hand").id("position_hand").description("\u0421\u043c\u0435\u0449\u0430\u0442\u044c \u043f\u043e\u0437\u0438\u0446\u0438\u044e \u0440\u0443\u043a\u0438")).build();
        this.registerSetting(this.positionHand);
        this.mainHandX = this.offsetSetting("main_hand_x", "Main hand X");
        this.mainHandY = this.offsetSetting("main_hand_y", "Main hand Y");
        this.mainHandZ = this.offsetSetting("main_hand_z", "Main hand Z");
        this.offHandX = this.offsetSetting("off_hand_x", "Off hand X");
        this.offHandY = this.offsetSetting("off_hand_y", "Off hand Y");
        this.offHandZ = this.offsetSetting("off_hand_z", "Off hand Z");
        this.animationHand = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Animation hand").id("animation_hand").description("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u0430\u044f \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f")).build();
        this.registerSetting(this.animationHand);
        this.swingType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Smooth", "Mode 2", "Mode 3", "Mode 4", "Mode 5", "Mode 6", "Mode 7", "Vanilla", "None").defaultOption("Smooth").name("Swing type").id("swing_type").description("\u0422\u0438\u043f \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438").visibleWhen(this.animationHand::isEnabled)).build();
        this.registerSetting(this.swingType);
        this.rotate = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Rotate").id("rotate").description("\u0412\u0440\u0430\u0449\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0435 \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435").visibleWhen(this.animationHand::isEnabled)).build();
        this.registerSetting(this.rotate);
        this.rotateSpeed = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 5.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Rotate speed").id("rotate_speed").description("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f").visibleWhen(() -> this.animationHand.isEnabled() && this.rotate.isEnabled())).build();
        this.registerSetting(this.rotateSpeed);
        this.attackAuraOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Attack aura only").id("attack_aura_only").description("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0432\u043a\u043b\u044e\u0447\u0451\u043d\u043d\u043e\u0439 Attack Aura").visibleWhen(this.animationHand::isEnabled)).build();
        this.registerSetting(this.attackAuraOnly);
        this.staticPosition = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Static hand position").id("static_position").description("\u0421\u0442\u0430\u0442\u0438\u0447\u043d\u043e\u0435 \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435 \u0440\u0443\u043a\u0438")).build();
        this.registerSetting(this.staticPosition);
    }

    @Override
    protected void initialize() {
        this.listen(FirstPersonItemTransformEvent.class, this::onHandPosition);
        this.listen(HandSwingSpeedEvent.class, this::onSwingSpeed);
        this.listen(FirstPersonSwingTransformEvent.class, this::onSwingAnimation);
    }

    public static boolean isStaticHandPositionEnabled() {
        HandModifyModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.staticPosition.isEnabled();
    }

    private void onHandPosition(FirstPersonItemTransformEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.positionHand.isEnabled()) {
            return;
        }
        class_1268 hand = event.getHand();
        if (hand == class_1268.field_5808 && event.getStack().method_7909() instanceof class_1764) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        int side = HandModifyModule.sideMultiplier(player);
        class_4587 matrices = event.getMatrices();
        if (hand == class_1268.field_5808) {
            this.applyOffset(matrices, side, this.mainHandX, this.mainHandY, this.mainHandZ);
        } else {
            this.applyOffset(matrices, side, this.offHandX, this.offHandY, this.offHandZ);
        }
    }

    private void onSwingAnimation(FirstPersonSwingTransformEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.animationHand.isEnabled()) {
            return;
        }
        if (event.getHand() != class_1268.field_5808) {
            return;
        }
        String mode = this.swingType.getSelectedOption();
        if (mode == null || "Vanilla".equals(mode)) {
            return;
        }
        if (this.attackAuraOnly.isEnabled() && !this.isAttackAuraActive()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_1792 heldItem = player.method_6047().method_7909();
        if (heldItem instanceof class_1764 || heldItem instanceof class_1753 || heldItem instanceof class_1835) {
            return;
        }
        class_4587 matrices = event.getMatrices();
        float swingProgress = event.getSwingProgress();
        float equipProgress = event.getEquipProgress();
        int side = HandModifyModule.sideMultiplier(player);
        float swingSin = class_3532.method_15374((double)(swingProgress * (float)Math.PI));
        float sqrtSwing = class_3532.method_15374((double)(class_3532.method_15355((float)swingProgress) * (float)Math.PI));
        switch (mode) {
            case "None": {
                HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
                break;
            }
            case "Mode 2": {
                this.applyMode2(matrices, side, swingSin);
                break;
            }
            case "Mode 3": {
                this.applyMode3(matrices, side, swingSin);
                break;
            }
            case "Mode 4": {
                this.applyMode4(matrices, side, sqrtSwing);
                break;
            }
            case "Mode 5": {
                this.applyMode5(matrices, side, sqrtSwing);
                break;
            }
            case "Mode 6": {
                this.applyMode6(matrices, side, swingSin);
                break;
            }
            case "Mode 7": {
                this.applyMode7(matrices, side, sqrtSwing);
                break;
            }
            default: {
                this.applySmooth(matrices, side, swingProgress, equipProgress, sqrtSwing);
            }
        }
        if (this.rotate.isEnabled()) {
            this.applySpin(matrices, swingProgress);
        }
        event.update();
    }

    private void onSwingSpeed(HandSwingSpeedEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.animationHand.isEnabled()) {
            return;
        }
        event.setSpeedMultiplier(1.0f);
        event.update();
    }

    private void applyOffset(Object matrices, int side, NumberSetting x, NumberSetting y, NumberSetting z) {
        HandRenderTransforms.translate(matrices, (double)side * x.getValue(), y.getValue(), z.getValue());
    }

    private void applyMode2(Object matrices, int side, float swingSin) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.35f, -0.72f);
        HandRenderTransforms.scale(matrices, 0.8f, 0.8f, 0.8f);
        HandRenderTransforms.rotateX(matrices, (float)side * 80.0f);
        HandRenderTransforms.rotateY(matrices, (float)side * -57.0f);
        HandRenderTransforms.rotateZ(matrices, -80.0f - 70.0f * swingSin);
    }

    private void applyMode3(Object matrices, int side, float swingSin) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.35f, -0.72f);
        HandRenderTransforms.scale(matrices, 0.8f, 0.8f, 0.8f);
        HandRenderTransforms.rotateX(matrices, (float)side * 80.0f);
        HandRenderTransforms.rotateY(matrices, (float)side * -35.0f);
        HandRenderTransforms.rotateZ(matrices, -70.0f - 70.0f * swingSin);
    }

    private void applyMode4(Object matrices, int side, float sqrtSwing) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.4f, -0.72f);
        HandRenderTransforms.scale(matrices, 0.8f, 0.8f, 0.8f);
        HandRenderTransforms.rotateZ(matrices, 50.0f);
        HandRenderTransforms.rotateX(matrices, (float)side * -60.0f);
        HandRenderTransforms.rotateY(matrices, (float)side * (110.0f + 25.0f * sqrtSwing));
    }

    private void applyMode5(Object matrices, int side, float sqrtSwing) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.52f, -0.72f);
        HandRenderTransforms.rotateZ(matrices, -60.0f * sqrtSwing);
        HandRenderTransforms.rotateYNegative(matrices, (float)side * -25.0f * sqrtSwing);
        HandRenderTransforms.rotateYNegativeRadians(matrices, (float)side * -0.2f * sqrtSwing);
    }

    private void applyMode6(Object matrices, int side, float swingSin) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.35f, -0.72f);
        HandRenderTransforms.scale(matrices, 0.8f, 0.8f, 0.8f);
        HandRenderTransforms.rotateX(matrices, (float)side * 25.0f);
        HandRenderTransforms.rotateY(matrices, (float)side * -15.0f);
        HandRenderTransforms.rotateZ(matrices, -70.0f - 100.0f * swingSin);
    }

    private void applyMode7(Object matrices, int side, float sqrtSwing) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.35f, -0.72f);
        HandRenderTransforms.scale(matrices, 0.8f, 0.8f, 0.8f);
        HandRenderTransforms.rotateX(matrices, (float)side * 80.0f);
        HandRenderTransforms.rotateY(matrices, (float)side * -15.0f);
        HandRenderTransforms.rotateZ(matrices, -100.0f - 70.0f * sqrtSwing);
    }

    private void applySmooth(Object matrices, int side, float swingProgress, float equipProgress, float sqrtSwing) {
        HandRenderTransforms.translateLocal(matrices, (float)side * 0.56f, -0.52f + equipProgress * -0.25f, -0.72f);
        HandRenderTransforms.rotateX(matrices, (float)side * (45.0f + class_3532.method_15374((double)(swingProgress * swingProgress * (float)Math.PI)) * -20.0f));
        HandRenderTransforms.rotateY(matrices, (float)side * sqrtSwing * -20.0f);
        HandRenderTransforms.rotateZ(matrices, sqrtSwing * -80.0f);
        HandRenderTransforms.rotateX(matrices, (float)side * -45.0f);
    }

    private void applySpin(Object matrices, float swingProgress) {
        if (swingProgress > 0.0f) {
            long elapsedMs = (System.nanoTime() - this.rotateStartNanos) / 1000000L;
            int spinDegrees = (int)((double)elapsedMs * 2.4 / (double)this.rotateSpeed.getIntValue() % 720.0);
            if (spinDegrees >= 360) {
                spinDegrees -= 720;
            }
            HandRenderTransforms.rotateZSpin(matrices, spinDegrees);
        } else {
            this.rotateStartNanos = System.nanoTime();
        }
    }

    private boolean isAttackAuraActive() {
        AttackAuraModule aura = this.attackAuraModule;
        if (aura == null) {
            if (ByAzenClient.getInstance() == null || ByAzenClient.getInstance().getModuleManager() == null) {
                return false;
            }
            this.attackAuraModule = aura = ByAzenClient.getInstance().getModuleManager().getModule(AttackAuraModule.class);
        }
        return aura != null && aura.isActive();
    }

    private static int sideMultiplier(class_746 player) {
        class_1306 mainArm = player != null ? player.method_6068() : class_1306.field_6183;
        return mainArm == class_1306.field_6183 ? 1 : -1;
    }

    private NumberSetting offsetSetting(String id, String name) {
        NumberSetting setting = ((NumberSettingBuilder)NumberSetting.builder().range(-1.0, 1.0).defaultValue(0.0).multiplier(1.0).precision(2).animationSpeed(20.0f).name(name).id(id).description("\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 \u0440\u0443\u043a\u0438 \u043f\u043e \u043e\u0441\u0438").visibleWhen(this.positionHand::isEnabled)).build();
        this.registerSetting(setting);
        return setting;
    }
}

