/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10039
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_4587
 *  net.minecraft.class_7833
 *  net.minecraft.class_916
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_10039;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_916;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.module.render.ItemPhysicModule;

@Mixin(value={class_916.class})
public abstract class ItemEntityRendererMixin {
    @Inject(method={"method_3996"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_916;method_72986(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10428;Lnet/minecraft/class_5819;Lnet/minecraft/class_238;)V", shift=At.Shift.BEFORE)})
    private void byazen$layItemOnGround(class_10039 state, class_4587 matrices, class_11659 queue, class_12075 cameraState, CallbackInfo callback) {
        if (ItemPhysicModule.isEnabled()) {
            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
        }
    }
}

