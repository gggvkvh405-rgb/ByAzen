/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3532
 */
package ru.wexside.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.misc.AimJitter;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public class HumanizedRotationStrategy
implements RotationStrategy {
    static final float value = 16.0f;
    private final AimJitter field12 = new AimJitter(20.0f, 10.0f, 2, 0.6f);
    static final int slot = 9;
    static final float value2 = 18.0f;
    static final float value3 = 24.0f;
    private Angle field16;
    static final float value4 = 12.0f;

    private Angle process(Angle angle) {
        boolean bl;
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        float f = (threadLocalRandom.nextBoolean() ? 1.0f : -1.0f) * threadLocalRandom.nextFloat(18.0f, 24.0f);
        float f2 = threadLocalRandom.nextFloat(12.0f, 16.0f);
        boolean bl2 = angle.getFloatType2() + f2 <= 90.0f;
        boolean bl3 = bl = angle.getFloatType2() - f2 >= -90.0f;
        float f3 = bl2 && bl ? (threadLocalRandom.nextBoolean() ? 1.0f : -1.0f) : (bl ? -1.0f : 1.0f);
        float f4 = class_3532.method_15363((float)(angle.getFloatType2() + f3 * f2), (float)-90.0f, (float)90.0f);
        return new Angle(angle.getFloatType() + f, f4);
    }

    @Override
    public RotationApplyResult process(RotationState rotationState, RotationIntent intent) {
        Angle currentAngle = rotationState.getPlayerAngle();
        if (!intent.hasTarget() || intent.targetAngle() == null) {
            this.field16 = null;
            return RotationApplyResult.notReady(this.field12.process(currentAngle));
        }
        if (intent.urgency() == AttackUrgency.HIT) {
            this.field16 = this.process(intent.targetAngle());
            return RotationApplyResult.applied(intent.targetAngle(), true);
        }
        Angle baseAngle = this.field16 != null && rotationState.getTicksSinceHit() < 9 ? this.field16 : currentAngle;
        return RotationApplyResult.applied(this.field12.process(baseAngle), false);
    }

    @Override
    public void onDeactivated(RotationState iIiIlIIliI2) {
        this.field12.update();
        this.field16 = null;
    }

    @Override
    public void onActivated(RotationState iIiIlIIliI2) {
        this.field12.update();
        this.field16 = null;
    }
}

