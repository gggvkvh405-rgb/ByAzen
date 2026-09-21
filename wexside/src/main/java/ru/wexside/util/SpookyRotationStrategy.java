/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_3532
 */
package ru.wexside.util;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3532;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public final class SpookyRotationStrategy
implements RotationStrategy {
    static final float value = 360.0f;
    static final float value2 = 25.0f;
    static final int slot = 3;
    private float value4;
    static final float value5 = 5.0f;
    static final float value6 = 0.4f;
    static final float value7 = 1.0E-4f;
    static final float value8 = 16.0f;
    static final float value9 = 4.0f;
    static final float value10 = 1.70158f;
    private float value11 = 1.0f;
    private float value12;
    static final float value13 = 4.0f;
    static final float value14 = 0.1f;
    static final double value15 = 70.0;
    private float value16;
    static final int slot2 = 1;
    static final float value17 = 89.0f;
    static final float value18 = 6.0f;
    static final float value19 = 10.0f;
    private float value3;

    @Override
    public RotationApplyResult process(RotationState iIiIlIIliI2, RotationIntent intent) {
        Angle angle;
        Angle angle2 = iIiIlIIliI2.getPlayerAngle();
        Angle angle3 = angle = iIiIlIIliI2.getAppliedAngle() != null ? iIiIlIIliI2.getAppliedAngle() : angle2;
        if (!intent.hasTarget() || intent.targetAngle() == null) {
            if (angle.process(angle2) < 1.0f) {
                this.value11 = 1.0f;
                return RotationApplyResult.notReady(angle2);
            }
            return RotationApplyResult.notReady(this.process3(iIiIlIIliI2, angle, angle2, null));
        }
        Angle angle4 = this.process3(iIiIlIIliI2, angle, intent.targetAngle(), iIiIlIIliI2.getTarget());
        boolean bl = angle4.process(intent.targetAngle()) < 5.0f || this.process6(iIiIlIIliI2.getTarget(), angle4);
        return RotationApplyResult.applied(angle4, bl && iIiIlIIliI2.isAttackReady());
    }

    @Override
    public void onDeactivated(RotationState iIiIlIIliI2) {
        this.value11 = 1.0f;
    }

    @Override
    public void onActivated(RotationState iIiIlIIliI2) {
        this.value11 = 1.0f;
    }

    private boolean process2(RotationState iIiIlIIliI2, Angle angle, class_1309 entity2, ThreadLocalRandom threadLocalRandom) {
        if (iIiIlIIliI2.getTicksSinceAttack() <= threadLocalRandom.nextInt(1, 3)) {
            return true;
        }
        return this.process6(entity2, angle);
    }

    private Angle process3(RotationState iIiIlIIliI2, Angle angle, Angle angle2, class_1309 entity2) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        float f = class_3532.method_15393((float)(angle2.getFloatType() - angle.getFloatType()));
        float f2 = class_3532.method_15393((float)(angle2.getFloatType2() - angle.getFloatType2()));
        float f3 = (float)Math.hypot(Math.abs(f), Math.abs(f2));
        if (f3 < 1.0E-4f) {
            f3 = 1.0E-4f;
        }
        float f4 = threadLocalRandom.nextFloat(25.0f, 360.0f);
        float f5 = threadLocalRandom.nextFloat(4.0f, 10.0f);
        float f6 = (float)(5.0 * Math.cos((double)System.currentTimeMillis() / 70.0));
        float f7 = (float)(4.0 * Math.sin((double)System.currentTimeMillis() / 70.0));
        if (this.process2(iIiIlIIliI2, angle, entity2, threadLocalRandom)) {
            f4 = 0.0f;
            f5 = 0.0f;
            f6 = 0.0f;
            f7 = 0.0f;
        }
        float f8 = Math.abs(f / f3) * f4;
        float f9 = Math.abs(f2 / f3) * f5;
        float f10 = angle.getFloatType() + class_3532.method_15363((float)f, (float)(-f8), (float)f8);
        float f11 = class_3532.method_15363((float)(angle.getFloatType2() + class_3532.method_15363((float)f2, (float)(-f9), (float)f9)), (float)-89.0f, (float)89.0f);
        return this.process5(angle, f10 + f6, f11 + f7, threadLocalRandom.nextFloat(0.1f, 0.4f));
    }

    private static float process4(float f) {
        float f2 = 2.70158f;
        float f3 = f - 1.0f;
        return 1.0f + f2 * f3 * f3 * f3 + 1.70158f * f3 * f3;
    }

    private Angle process5(Angle angle, float f, float f2, float f3) {
        float f4 = Math.abs(class_3532.method_15393((float)(f - this.value4)));
        float f5 = Math.abs(f2 - this.value16);
        if (this.value11 >= 1.0f || f4 > 6.0f || f5 > 6.0f) {
            this.value3 = angle.getFloatType();
            this.value12 = angle.getFloatType2();
            this.value4 = f;
            this.value16 = f2;
            this.value11 = 0.0f;
        }
        this.value11 = class_3532.method_15363((float)(this.value11 + f3), (float)0.0f, (float)1.0f);
        float f6 = SpookyRotationStrategy.process4(this.value11);
        float f7 = class_3532.method_15393((float)(this.value4 - this.value3));
        float f8 = this.value16 - this.value12;
        float f9 = this.value3 + f7 * f6;
        float f10 = class_3532.method_15363((float)(this.value12 + f8 * f6), (float)-89.0f, (float)89.0f);
        return new Angle(f9, f10);
    }

    private boolean process6(class_1309 entity2, Angle angle) {
        return entity2 != null && ReachHelper.raycastEntity((class_1297)entity2, angle, 16.0f, true) != null;
    }
}

