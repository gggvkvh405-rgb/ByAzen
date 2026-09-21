/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3532
 */
package ru.wexside.util;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public final class RandomizedDirectRotationStrategy
implements RotationStrategy {
    private static final float value = 60.0f;
    private static final float value2 = 1.0f;
    private static final float value3 = 60.0f;
    private static final float value4 = 45.0f;
    private static final float value5 = 5.0f;

    @Override
    public RotationApplyResult process(RotationState iIiIlIIliI2, RotationIntent intent) {
        Angle angle;
        Angle angle2 = iIiIlIIliI2.getPlayerAngle();
        Angle angle3 = angle = iIiIlIIliI2.getAppliedAngle() != null ? iIiIlIIliI2.getAppliedAngle() : angle2;
        if (!intent.hasTarget() || intent.targetAngle() == null) {
            if (angle.process(angle2) < 1.0f) {
                return RotationApplyResult.notReady(angle2);
            }
            return RotationApplyResult.notReady(RandomizedDirectRotationStrategy.process2(angle, angle2));
        }
        Angle angle4 = RandomizedDirectRotationStrategy.process2(angle, intent.targetAngle());
        boolean bl = angle4.process(intent.targetAngle()) < 5.0f;
        return RotationApplyResult.applied(angle4, bl && iIiIlIIliI2.isAttackReady());
    }

    @Override
    public void onDeactivated(RotationState iIiIlIIliI2) {
    }

    @Override
    public void onActivated(RotationState iIiIlIIliI2) {
    }

    private static Angle process2(Angle angle, Angle angle2) {
        float f = class_3532.method_15393((float)(angle2.getFloatType() - angle.getFloatType()));
        float f2 = class_3532.method_15393((float)(angle2.getFloatType2() - angle.getFloatType2()));
        float f3 = (float)Math.hypot(Math.abs(f), Math.abs(f2));
        if (f3 == 0.0f) {
            return angle;
        }
        float f4 = Math.abs(f / f3) * 60.0f;
        float f5 = Math.abs(f2 / f3) * 45.0f;
        float f6 = class_3532.method_15363((float)f, (float)(-f4), (float)f4);
        float f7 = class_3532.method_15363((float)f2, (float)(-f5), (float)f5);
        float f8 = Math.abs(f6) + Math.abs(f7);
        float f9 = 60.0f * ThreadLocalRandom.current().nextFloat(0.9f, 1.0f);
        float f10 = f8 == 0.0f ? 0.0f : Math.abs(f6 / f8) * f9;
        float f11 = f8 == 0.0f ? 0.0f : Math.abs(f7 / f8) * f9;
        float f12 = angle.getFloatType() + class_3532.method_15363((float)f6, (float)(-f10), (float)f10);
        float f13 = angle.getFloatType2() + class_3532.method_15363((float)f7, (float)(-f11), (float)f11);
        return new Angle(f12, f13);
    }
}

