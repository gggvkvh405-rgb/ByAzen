/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3532
 */
package ru.wexside.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.util.Angle;

public class AimJitter {
    private final int slot;
    private Angle angle;
    private final float value;
    private int slot2;
    private final float value2;
    private final float value3;
    private Angle angle2 = Angle.angle;

    public AimJitter() {
        this(30.0f, 15.0f, 2, 0.6f);
    }

    public AimJitter(float f, float f2, int n, float f3) {
        this.angle = Angle.angle;
        this.value2 = f;
        this.value = f2;
        this.slot = n;
        this.value3 = f3;
    }

    public void update() {
        this.angle2 = Angle.angle;
        this.angle = Angle.angle;
        this.slot2 = 0;
    }

    public Angle process(Angle angle) {
        float f;
        float f2;
        if (++this.slot2 >= this.slot) {
            this.slot2 = 0;
            f2 = ThreadLocalRandom.current().nextFloat(-this.value2, this.value2);
            f = ThreadLocalRandom.current().nextFloat(-this.value, this.value);
            this.angle = new Angle(f2, f);
        }
        f2 = class_3532.method_16439((float)this.value3, (float)this.angle2.getFloatType(), (float)this.angle.getFloatType());
        f = class_3532.method_16439((float)this.value3, (float)this.angle2.getFloatType2(), (float)this.angle.getFloatType2());
        this.angle2 = new Angle(f2, f);
        float f3 = angle.getFloatType() + this.angle2.getFloatType();
        float f4 = class_3532.method_15363((float)(angle.getFloatType2() + this.angle2.getFloatType2()), (float)-90.0f, (float)90.0f);
        return new Angle(f3, f4);
    }
}

