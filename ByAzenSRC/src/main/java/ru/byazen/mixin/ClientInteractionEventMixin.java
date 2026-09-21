/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.byazen.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.byazen.ByAzenClient;
import ru.byazen.event.BlockInteractEvent;
import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.EventBus;

@Mixin(value={class_636.class})
public abstract class ClientInteractionEventMixin {
    @Shadow
    @Final
    private class_310 field_3712;

    @Inject(method={"method_2918"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$beforeAttack(class_1657 player, class_1297 target, CallbackInfo callback) {
        EventBus eventBus = ByAzenClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        EntityAttackEvent event = new EntityAttackEvent(target);
        eventBus.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_2896"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$beforeBlockInteract(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> callback) {
        if (this.field_3712.field_1687 == null) {
            return;
        }
        EventBus eventBus = ByAzenClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        BlockInteractEvent event = new BlockInteractEvent(this.field_3712.field_1687.method_8320(hit.method_17777()).method_26204());
        eventBus.post(event);
        if (event.isCancelled()) {
            callback.setReturnValue(class_1269.field_5814);
        }
    }
}

