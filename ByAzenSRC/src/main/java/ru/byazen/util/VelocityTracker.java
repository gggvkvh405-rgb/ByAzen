/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_238
 *  net.minecraft.class_2828$class_2831
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.byazen.util;

import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

public final class VelocityTracker {
    private double velocityY;
    private double lastY = Double.NaN;
    private boolean lastWasGrounded = true;
    static final double JUMP_MOTION = 0.42;
    private boolean hittingGround;
    static final double DRAG = 0.98;
    static final double MOTION_EPSILON = 0.003;

    public void reset() {
        this.update();
    }

    public void onMovementPacket(class_2828.class_2831 packet) {
        this.atomicLong(packet);
    }

    public void setYVelocity(double velocityY) {
        this.setDoubleType(velocityY);
    }

    public void update() {
        this.velocityY = 0.0;
        this.lastY = Double.NaN;
        this.lastWasGrounded = true;
        this.hittingGround = false;
    }

    public void atomicLong(class_2828.class_2831 packet) {
        class_746 player = class_310.method_1551().field_1724;
        class_638 world = class_310.method_1551().field_1687;
        if (player == null || world == null) {
            return;
        }
        double y = packet.method_12269(Double.isNaN(this.lastY) ? player.method_23318() : this.lastY);
        if (packet.method_12273()) {
            this.velocityY = 0.0;
            this.hittingGround = false;
        } else {
            if (this.lastWasGrounded && packet.method_36171() && !Double.isNaN(this.lastY) && y > this.lastY) {
                this.velocityY = VelocityTracker.jumpMotion(player);
            }
            this.tickCollision(player, (class_1937)world);
        }
        this.lastWasGrounded = packet.method_12273();
        if (packet.method_36171()) {
            this.lastY = y;
        }
    }

    public void setDoubleType(double velocityY) {
        this.velocityY = velocityY;
    }

    public boolean isActive() {
        return this.hittingGround;
    }

    private void tickCollision(class_746 player, class_1937 world) {
        class_238 movedBox;
        this.hittingGround = false;
        if (this.velocityY != 0.0 && world.method_20812((class_1297)player, movedBox = player.method_5829().method_989(0.0, this.velocityY, 0.0)).iterator().hasNext()) {
            if (this.velocityY < 0.0) {
                this.hittingGround = true;
            }
            this.velocityY = 0.0;
        }
        this.velocityY = (this.velocityY - 0.08) * 0.98;
        if (Math.abs(this.velocityY) < 0.003) {
            this.velocityY = 0.0;
        }
    }

    private static double jumpMotion(class_746 player) {
        class_1293 jumpBoost = player.method_6112(class_1294.field_5913);
        return 0.42 + (jumpBoost != null ? 0.1 * (double)(jumpBoost.method_5578() + 1) : 0.0);
    }

    private static double process2(class_746 player) {
        return VelocityTracker.jumpMotion(player);
    }
}

