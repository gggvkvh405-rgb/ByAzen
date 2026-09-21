/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_312
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.byazen.mixin;

import net.minecraft.class_312;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.byazen.module.movement.FreeCameraModule;
import ru.byazen.module.render.FreeLookModule;

@Mixin(value={class_312.class})
public abstract class MouseFreeLookMixin {
    @Redirect(method={"method_1606"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_746;method_5872(DD)V"))
    private void byazen$redirectLookDirection(class_746 player, double cursorDeltaX, double cursorDeltaY) {
        if (FreeCameraModule.isEnabled()) {
            FreeCameraModule.handle(cursorDeltaX, cursorDeltaY);
            return;
        }
        if (FreeLookModule.isEnabled()) {
            FreeLookModule.handle(cursorDeltaX, cursorDeltaY);
            return;
        }
        player.method_5872(cursorDeltaX, cursorDeltaY);
    }
}

