/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2828
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.wexside.mixin;

import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.util.RotationController;

@Mixin(value={class_2828.class})
public abstract class PlayerMovePacketRotationMixin {
    @Shadow
    @Final
    protected boolean field_12888;
    @Shadow
    @Final
    @Mutable
    protected float field_12887;
    @Shadow
    @Final
    @Mutable
    protected float field_12885;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void wexside$applySilentRotation(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean horizontalCollision, boolean changePosition, boolean changeLook, CallbackInfo callback) {
        if (!this.field_12888) {
            return;
        }
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null) {
            return;
        }
        Float requestedYaw = rotations.getFloatType2();
        if (requestedYaw == null) {
            return;
        }
        this.field_12887 = requestedYaw.floatValue();
        this.field_12885 = rotations.getFloatType();
        rotations.onTick(this.field_12885);
    }
}

