/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.misc;

import java.util.concurrent.ThreadLocalRandom;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.util.Angle;
import ru.wexside.util.FixedStepRotationSmoother;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public class RandomizedRotationStrategy
implements RotationStrategy {
    static final float value = 45.0f;
    static final float value2 = 1.0f;
    static final float value3 = 2.0f;

    @Override
    public RotationApplyResult process(RotationState iIiIlIIliI2, RotationIntent intent) {
        Angle angle = iIiIlIIliI2.getPlayerAngle();
        Angle angle2 = iIiIlIIliI2.getAppliedAngle() != null ? iIiIlIIliI2.getAppliedAngle() : angle;
        FixedStepRotationSmoother fixedStepRotationSmoother = new FixedStepRotationSmoother(45.0f * ThreadLocalRandom.current().nextFloat(0.9f, 1.0f));
        if (!intent.hasTarget() || intent.targetAngle() == null) {
            if (angle2.process(angle) < 1.0f) {
                return RotationApplyResult.notReady(angle);
            }
            return RotationApplyResult.notReady(fixedStepRotationSmoother.process(angle2, angle));
        }
        Angle angle3 = fixedStepRotationSmoother.process(angle2, intent.targetAngle());
        boolean bl = angle3.process(intent.targetAngle()) < 2.0f;
        return RotationApplyResult.applied(angle3, bl && iIiIlIIliI2.isAttackReady());
    }

    @Override
    public void onDeactivated(RotationState iIiIlIIliI2) {
    }

    @Override
    public void onActivated(RotationState iIiIlIIliI2) {
    }
}

