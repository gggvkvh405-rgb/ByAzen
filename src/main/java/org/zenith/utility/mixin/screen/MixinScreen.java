package org.zenith.utility.mixin.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.core.RenderHook;

@Mixin(Screen.class)
public class MixinScreen implements RenderHook {
   @Unique
   public long startTime = System.currentTimeMillis();
   @Shadow
   @Nullable
   protected MinecraftClient client;

   @Unique
   public long getStartTime() {
      return this.startTime;
   }

   @Unique
   @Override
   public long zenithDLC_callGetStartTime() {
      return this.startTime;
   }
}
