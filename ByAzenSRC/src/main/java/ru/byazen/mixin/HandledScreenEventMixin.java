/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11909
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import net.minecraft.class_11909;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.ItemHoverEvent;
import ru.byazen.event.TooltipRenderEvent;
import ru.byazen.module.hud.AnimateModule;

@Mixin(value={class_465.class})
public abstract class HandledScreenEventMixin {
    @Shadow
    protected class_1735 field_2787;
    @Unique
    private boolean byazen$animationMatrixPushed;

    @Inject(method={"method_25394"}, at={@At(value="HEAD")})
    private void byazen$beginScreenAnimation(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
        if (!AnimateModule.compute2((class_437)((Object)this))) {
            return;
        }
        context.method_51448().pushMatrix();
        AnimateModule.handle(context.method_51448(), context.method_51421(), context.method_51443());
        this.byazen$animationMatrixPushed = true;
    }

    @Inject(method={"method_25394"}, at={@At(value="RETURN")})
    private void byazen$endScreenAnimation(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
        if (!this.byazen$animationMatrixPushed) {
            return;
        }
        this.byazen$animationMatrixPushed = false;
        context.method_51448().popMatrix();
    }

    @ModifyVariable(method={"method_25394"}, at=@At(value="HEAD"), argsOnly=true, ordinal=0)
    private int byazen$transformRenderMouseX(int mouseX) {
        return AnimateModule.compute6(mouseX);
    }

    @ModifyVariable(method={"method_25394"}, at=@At(value="HEAD"), argsOnly=true, ordinal=1)
    private int byazen$transformRenderMouseY(int mouseY) {
        return AnimateModule.compute7(mouseY);
    }

    @ModifyVariable(method={"method_25402", "method_25403", "method_25406"}, at=@At(value="HEAD"), argsOnly=true)
    private class_11909 byazen$transformAnimatedClick(class_11909 click) {
        int transformedX = AnimateModule.compute6((int)Math.round(click.comp_4798()));
        int transformedY = AnimateModule.compute7((int)Math.round(click.comp_4799()));
        if (transformedX == (int)Math.round(click.comp_4798()) && transformedY == (int)Math.round(click.comp_4799())) {
            return click;
        }
        return new class_11909((double)transformedX, (double)transformedY, click.comp_4800());
    }

    @Inject(method={"method_2380"}, at={@At(value="HEAD")}, cancellable=true)
    private void beforeItemTooltip(class_332 context, int mouseX, int mouseY, CallbackInfo callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events == null || this.field_2787 == null) {
            return;
        }
        class_1799 stack = this.field_2787.method_7677();
        if (stack.method_7960()) {
            return;
        }
        ItemHoverEvent event = new ItemHoverEvent(stack);
        events.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_25394"}, at={@At(value="TAIL")})
    private void afterScreenRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
        EventBus events = ByAzenClient.getEventBus();
        if (events != null) {
            events.post(new TooltipRenderEvent(context, mouseX, mouseY));
        }
    }
}

