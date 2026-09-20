package org.zenith.module.player;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;

@ModuleInfo(name = "ClanUpgrade", category = Category.PLAYER, description = "Автоматическое улучшение клана")
public final class ClanUpgrade extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ClanUpgrade clanUpgrade = new ClanUpgrade();
   public static final float float16 = 90.0F;
   public static final float float17 = 0.0F;
   public float float18 = 0.0F;
   public float float19 = 0.0F;
   public boolean boolean43 = false;

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.interactionManager != null) {
         ItemStack itemstack = minecraftClient3.player.getStackInHand(Hand.MAIN_HAND);
         if (itemstack.getItem() != Items.REDSTONE) {
            StyledTextBuilder.AimPolicyRotationStrategy("Возьмите редстоун в руку!");
         } else {
            BlockPos blockpos = minecraftClient3.player.getBlockPos();
            BlockState blockstate = minecraftClient3.world.getBlockState(blockpos);
            if (!this.boolean43) {
               this.float18 = minecraftClient3.player.getPitch();
               this.float19 = minecraftClient3.player.getYaw();
               this.boolean43 = true;
            }

            minecraftClient3.player.setPitch(90.0F);
            minecraftClient3.player.setYaw(0.0F);
            if (blockstate.isAir()) {
               this.CancellableEvent(blockpos);
            } else if (blockstate.getBlock() == Blocks.REDSTONE_WIRE) {
               this.Event08(blockpos);
            }
         }
      }
   }

   public void CancellableEvent(BlockPos var1) {
      BlockPos blockpos = var1.down();
      BlockState blockstate = minecraftClient3.world.getBlockState(blockpos);
      if (blockstate.isSolid()) {
         Vec3d vec3d = new Vec3d(blockpos.getX() + 0.5, blockpos.getY() + 1.0, blockpos.getZ() + 0.5);
         BlockHitResult blockhitresult = new BlockHitResult(vec3d, Direction.UP, blockpos, false);
         minecraftClient3.interactionManager.interactBlock(minecraftClient3.player, Hand.MAIN_HAND, blockhitresult);
         minecraftClient3.player.swingHand(Hand.MAIN_HAND);
      }
   }

   public void Event08(BlockPos var1) {
      minecraftClient3.interactionManager.attackBlock(var1, Direction.UP);
      minecraftClient3.player.swingHand(Hand.MAIN_HAND);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.boolean43 && minecraftClient3.player != null) {
         minecraftClient3.player.setPitch(this.float18);
         minecraftClient3.player.setYaw(this.float19);
         this.boolean43 = false;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.boolean43 = false;
      if (minecraftClient3.player != null) {
         this.float18 = minecraftClient3.player.getPitch();
         this.float19 = minecraftClient3.player.getYaw();
      }
   }
}
