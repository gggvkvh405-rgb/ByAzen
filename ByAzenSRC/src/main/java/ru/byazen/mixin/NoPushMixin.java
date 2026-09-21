/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.module.player.NoPushModule;

public final class NoPushMixin {
    private NoPushMixin() {
    }

    @Mixin(value={class_746.class})
    public static abstract class ClientPlayerHook {
        @Inject(method={"method_30673"}, at={@At(value="HEAD")}, cancellable=true)
        private void byazen$ignoreBlockPush(double x, double z, CallbackInfo callback) {
            if (NoPushModule.compute("Blocks")) {
                callback.cancel();
            }
        }
    }

    @Mixin(value={class_1657.class})
    public static abstract class PlayerHook {
        @Inject(method={"method_5675"}, at={@At(value="HEAD")}, cancellable=true)
        private void byazen$ignoreFluidPush(CallbackInfoReturnable<Boolean> callback) {
            if ((Object)this == class_310.method_1551().field_1724 && NoPushModule.compute("Water")) {
                callback.setReturnValue(false);
            }
        }
    }

    @Mixin(value={class_1297.class})
    public static abstract class EntityHook {
        @Inject(method={"method_5697"}, at={@At(value="HEAD")}, cancellable=true)
        private void byazen$ignoreEntityPush(class_1297 other, CallbackInfo callback) {
            if ((Object)this == class_310.method_1551().field_1724 && NoPushModule.compute("Players")) {
                callback.cancel();
            }
        }
    }
}

