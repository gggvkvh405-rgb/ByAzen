/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10042
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_922
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_10042;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.module.render.SeeInvisiblesModule;

@Mixin(value={class_922.class})
public abstract class LivingEntityRendererMixin {
    @Shadow
    public abstract class_2960 method_3885(class_10042 var1);

    @Inject(method={"method_24302"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$useTranslucentLayer(class_10042 state, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<class_1921> callback) {
        if (SeeInvisiblesModule.isEnabled() && state.field_53333) {
            callback.setReturnValue(class_12249.method_75998(this.method_3885(state)));
        }
    }

    @Inject(method={"method_62484"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$applyInvisibleAlpha(class_10042 state, CallbackInfoReturnable<Integer> callback) {
        if (SeeInvisiblesModule.isEnabled() && state.field_53333) {
            callback.setReturnValue(SeeInvisiblesModule.getIntType());
        }
    }
}

