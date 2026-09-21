/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 */
package ru.byazen.util;

import java.util.Objects;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public final class Angle {
    public static final Angle ZERO;
    public static final Angle angle;
    private final float yaw;
    private final float pitch;

    public Angle(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getFloatType() {
        return this.yaw;
    }

    public float getFloatType2() {
        return this.pitch;
    }

    public boolean equals(Object clipToSpace) {
        if (this == clipToSpace) {
            return true;
        }
        if (!(clipToSpace instanceof Angle)) {
            return false;
        }
        Angle other = (Angle)clipToSpace;
        return Float.compare(this.yaw, other.yaw) == 0 && Float.compare(this.pitch, other.pitch) == 0;
    }

    public String toString() {
        return "Angle[yaw=" + this.yaw + ", pitch=" + this.pitch + "]";
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.yaw), Float.valueOf(this.pitch));
    }

    public static Angle fromVectors(class_243 from, class_243 to) {
        return Angle.fromDelta(to.method_1020(from));
    }

    public class_243 toVec3d() {
        float degToRad = (float)Math.PI / 180;
        float yawRad = -this.yaw * degToRad;
        float pitchRad = this.pitch * degToRad;
        float yawCos = class_3532.method_15362((double)yawRad);
        float yawSin = class_3532.method_15374((double)yawRad);
        float pitchCos = class_3532.method_15362((double)pitchRad);
        float pitchSin = class_3532.method_15374((double)pitchRad);
        return new class_243((double)(yawSin * pitchCos), (double)(-pitchSin), (double)(yawCos * pitchCos));
    }

    public float process(Angle other) {
        Angle delta = this.process4(other);
        return (float)Math.hypot(delta.yaw, delta.pitch);
    }

    public static Angle process2(class_243 from, class_1309 entity) {
        class_243 to = entity.method_5828(1.0f).method_1031(0.0, -0.4, 0.0);
        return Angle.fromVectors(from, to);
    }

    public static Angle process3(class_243 delta) {
        return Angle.fromDelta(delta);
    }

    public static Angle fromDelta(class_243 delta) {
        double horizontal = Math.sqrt(delta.field_1352 * delta.field_1352 + delta.field_1350 * delta.field_1350);
        float yaw = (float)(Math.toDegrees(Math.atan2(delta.field_1350, delta.field_1352)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(delta.field_1351, horizontal)));
        return new Angle(yaw, pitch);
    }

    public Angle process4(Angle other) {
        float yawDelta = class_3532.method_15393((float)(other.yaw - this.yaw));
        float pitchDelta = class_3532.method_15393((float)(other.pitch - this.pitch));
        return new Angle(yawDelta, pitchDelta);
    }

    public Angle getAngle() {
        return new Angle(this.yaw, class_3532.method_15363((float)this.pitch, (float)-90.0f, (float)90.0f));
    }

    static {
        angle = ZERO = new Angle(0.0f, 0.0f);
    }
}

