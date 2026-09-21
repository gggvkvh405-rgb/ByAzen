/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.wexside.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.movement.AirStuckModule;
import ru.wexside.module.movement.AutoSprintModule;
import ru.wexside.module.movement.SpeedModule;

@Mixin(value={class_1657.class})
public abstract class PlayerEntityFeatureMixin {
    private class_243 wexside$velocityBeforeAttack;
    private boolean wexside$sprintingBeforeAttack;
    private boolean wexside$restoreSprintAfterAttack;

    @Inject(method={"method_6091"}, at={@At(value="HEAD")}, cancellable=true)
    private void wexside$freezeAirStuckMovement(class_243 movement, CallbackInfo callback) {
        class_746 player;
        Object playerEntityFeatureMixin = this;
        if (playerEntityFeatureMixin instanceof class_746 && AirStuckModule.compute2(player = (class_746)playerEntityFeatureMixin)) {
            player.method_18799(class_243.field_1353);
            callback.cancel();
        }
    }

    @Inject(method={"method_6029"}, at={@At(value="HEAD")}, cancellable=true)
    private void wexside$applyCollisionSpeed(CallbackInfoReturnable<Float> callback) {
        if ((Object)this == class_310.method_1551().field_1724 && SpeedModule.value > 0.0f) {
            callback.setReturnValue(Float.valueOf(SpeedModule.value));
        }
    }

    @Inject(method={"method_7324"}, at={@At(value="HEAD")})
    private void wexside$captureSprintState(class_1297 target, CallbackInfo callback) {
        class_1657 player = (class_1657)((Object)this);
        boolean bl = this.wexside$restoreSprintAfterAttack = player == class_310.method_1551().field_1724 && AutoSprintModule.isEnabled2() && !player.method_7325();
        if (this.wexside$restoreSprintAfterAttack) {
            this.wexside$velocityBeforeAttack = player.method_18798();
            this.wexside$sprintingBeforeAttack = player.method_5624();
        }
    }

    @Inject(method={"method_7324"}, at={@At(value="TAIL")})
    private void wexside$restoreSprintState(class_1297 target, CallbackInfo callback) {
        if (!this.wexside$restoreSprintAfterAttack) {
            return;
        }
        class_1657 player = (class_1657)((Object)this);
        player.method_18799(this.wexside$velocityBeforeAttack);
        player.method_5728(this.wexside$sprintingBeforeAttack);
        this.wexside$restoreSprintAfterAttack = false;
    }
}

