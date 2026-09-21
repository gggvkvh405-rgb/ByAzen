/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3532
 */
package ru.byazen.util;

import net.minecraft.class_3532;
import ru.byazen.util.Angle;

public class FixedStepRotationSmoother {
    private final float value;

    public FixedStepRotationSmoother(float f) {
        this.value = f;
    }

    public Angle process(Angle angle, Angle angle2) {
        float f = class_3532.method_15393((float)(angle2.getFloatType() - angle.getFloatType()));
        float f2 = class_3532.method_15393((float)(angle2.getFloatType2() - angle.getFloatType2()));
        float f3 = Math.abs(f) + Math.abs(f2);
        if (f3 < 1.0E-4f) {
            return angle2;
        }
        float f4 = Math.abs(f) / f3 * this.value;
        float f5 = Math.abs(f2) / f3 * this.value;
        float f6 = angle.getFloatType() + class_3532.method_15363((float)f, (float)(-f4), (float)f4);
        float f7 = class_3532.method_15363((float)(angle.getFloatType2() + class_3532.method_15363((float)f2, (float)(-f5), (float)f5)), (float)-90.0f, (float)90.0f);
        return new Angle(f6, f7);
    }
}

