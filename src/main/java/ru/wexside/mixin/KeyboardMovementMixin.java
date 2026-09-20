/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10185
 *  net.minecraft.class_241
 *  net.minecraft.class_743
 *  net.minecraft.class_744
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.wexside.mixin;

import net.minecraft.class_10185;
import net.minecraft.class_241;
import net.minecraft.class_743;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.movement.AutoJumpModule;
import ru.wexside.module.movement.AutoSprintModule;
import ru.wexside.module.movement.FreeCameraModule;

@Mixin(value={class_743.class})
public abstract class KeyboardMovementMixin
extends class_744 {
    @Inject(method={"method_3129"}, at={@At(value="TAIL")})
    private void wexside$applyMovementModules(CallbackInfo callback) {
        boolean sprint;
        if (FreeCameraModule.isEnabled()) {
            this.field_54155 = class_10185.field_54098;
            this.field_55868 = class_241.field_1340;
            return;
        }
        class_10185 input = this.field_54155;
        boolean jump = input.comp_3163() || AutoJumpModule.isEnabled();
        boolean bl = sprint = input.comp_3165() || AutoSprintModule.isEnabled();
        if (jump == input.comp_3163() && sprint == input.comp_3165()) {
            return;
        }
        this.field_54155 = new class_10185(input.comp_3159(), input.comp_3160(), input.comp_3161(), input.comp_3162(), jump, input.comp_3164(), sprint);
    }
}

