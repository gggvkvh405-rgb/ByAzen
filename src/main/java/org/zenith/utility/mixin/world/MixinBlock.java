package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.event.EventInjectPlaced;

@Mixin(Block.class)
public class MixinBlock {
   @Inject(method = "onPlaced", at = @At("HEAD"))
   public void injectPlaced(World var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5, CallbackInfo var6) {
      if (MinecraftClient.getInstance().player == var4) {
         EventInjectPlaced il1li1l1i1ii1i1iiilli = new EventInjectPlaced(var2, var3);
         EventManager.call(il1li1l1i1ii1i1iiilli);
      }
   }
}
