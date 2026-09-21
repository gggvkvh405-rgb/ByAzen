/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package ru.byazen.mixin;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.byazen.misc.LivingEntityStateAccessor;

@Mixin(value={class_1309.class})
public abstract class LivingEntityAccessorMixin
implements LivingEntityStateAccessor {
    @Shadow
    private int field_6228;

    @Override
    public void setJumpingCooldown(int cooldown) {
        this.field_6228 = cooldown;
    }
}

