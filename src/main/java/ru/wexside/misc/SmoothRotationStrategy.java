/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.misc;

import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.util.Angle;
import ru.wexside.util.FixedStepRotationSmoother;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public class SmoothRotationStrategy
implements RotationStrategy {
    private final FixedStepRotationSmoother field12 = new FixedStepRotationSmoother(45.0f);
    static final float value = 1.0f;
    static final float value2 = 2.0f;

    @Override
    public RotationApplyResult process(RotationState iIiIlIIliI2, RotationIntent intent) {
        Angle angle;
        Angle angle2 = iIiIlIIliI2.getPlayerAngle();
        Angle angle3 = angle = iIiIlIIliI2.getAppliedAngle() != null ? iIiIlIIliI2.getAppliedAngle() : angle2;
        if (!intent.hasTarget() || intent.targetAngle() == null) {
            if (angle.process(angle2) < 1.0f) {
                return RotationApplyResult.notReady(angle2);
            }
            return RotationApplyResult.notReady(this.field12.process(angle, angle2));
        }
        Angle angle4 = this.field12.process(angle, intent.targetAngle());
        boolean bl = angle4.process(intent.targetAngle()) < 2.0f;
        return RotationApplyResult.applied(angle4, bl && iIiIlIIliI2.isAttackReady());
    }

    @Override
    public void onDeactivated(RotationState iIiIlIIliI2) {
    }

    @Override
    public void onActivated(RotationState iIiIlIIliI2) {
    }
}

