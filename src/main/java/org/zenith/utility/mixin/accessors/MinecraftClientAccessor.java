package org.zenith.utility.mixin.accessors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
   @Invoker("setWorld")
   void zenith_setWorld(@Nullable ClientWorld var1);

   @Invoker("doItemUse")
   void zenith_doItemUse();

   @Invoker("doAttack")
   boolean zenith_doAttack();

   @Accessor("framebuffer")
   @Mutable
   void zenith_setFramebuffer(Framebuffer var1);

   @Accessor("session")
   @Mutable
   void zenith_setSession(Session var1);

   @Accessor("attackCooldown")
   int zenith_getAttackCooldown();

   @Accessor("attackCooldown")
   void zenith_setAttackCooldown(int var1);

   @Accessor("worldRenderer")
   WorldRenderer zenith_getWorldRenderer();

   @Accessor("worldRenderer")
   @Mutable
   void zenith_setWorldRenderer(WorldRenderer var1);
}
