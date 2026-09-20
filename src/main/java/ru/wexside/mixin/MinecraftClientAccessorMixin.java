/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package ru.wexside.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import ru.wexside.misc.AttackInvoker;
import ru.wexside.misc.ItemUseCooldownAccessor;

@Mixin(value={class_310.class})
public abstract class MinecraftClientAccessorMixin
implements ItemUseCooldownAccessor,
AttackInvoker {
    @Shadow
    private int field_1752;

    @Override
    @Invoker(value="method_1536")
    public abstract boolean invokeAttack();

    @Override
    public void setItemUseCooldown(int cooldown) {
        this.field_1752 = cooldown;
    }
}

