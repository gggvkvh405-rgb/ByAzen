package org.zenith.module.movement;

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
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.core.EffectEngine;
import org.zenith.core.MovementController;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.TargetAcquireEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "Spider", category = Category.MOVEMENT, description = "")
public final class Spider extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Spider spider = new Spider();

   @EventTarget
   public void ItemRegistry(RotationUpdateStartEvent var1) {
      boolean flag = minecraftClient3.player.getOffHandStack().getItem() instanceof BlockItem;
      int i = ScreenUtils.on23(var0 -> minecraftClient3.player.getInventory().getStack(var0).getItem() instanceof BlockItem);
      BlockPos blockpos = this.call102();
      if ((flag || i != -1) && !blockpos.equals(BlockPos.ORIGIN)) {
         Vec3d vec3d = blockpos.toCenterPos().add(0.0, 0.0, 0.0);
         Direction direction = Direction.getFacing(
            vec3d.x - minecraftClient3.player.getX(),
            vec3d.y - minecraftClient3.player.getY(),
            vec3d.z - minecraftClient3.player.getZ()
         );
         Rotation ililiiili1ll1li11 = new Rotation(
            minecraftClient3.player.getYaw(),
            RotationMath.BotChatEvent(vec3d.subtract(new Vec3d(direction.getVector()).multiply(0.5))).GuiWalk()
         );
         val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11), val001.HudPreviewItem()), 1, this, 2);
      }
   }

   @EventTarget
   public void on23(TargetAcquireEvent var1) {
      boolean flag = minecraftClient3.player.getOffHandStack().getItem() instanceof BlockItem;
      int i = ScreenUtils.on23(var0 -> minecraftClient3.player.getInventory().getStack(var0).getItem() instanceof BlockItem);
      BlockPos blockpos = this.call102();
      if ((flag || i != -1) && !blockpos.equals(BlockPos.ORIGIN)) {
         ItemStack itemstack = flag ? minecraftClient3.player.getOffHandStack() : minecraftClient3.player.getInventory().getStack(i);
         Hand hand = flag ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (this.CloudRouter(itemstack) && minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult) {
            int j = minecraftClient3.player.inventory.selectedSlot;
            if (!flag) {
               minecraftClient3.player.inventory.selectedSlot = i;
            }

            EffectEngine.on23(blockhitresult, hand);
            if (!flag) {
               minecraftClient3.player.inventory.selectedSlot = j;
            }
         }
      }
   }

   public boolean CloudRouter(ItemStack var1) {
      BlockPos blockpos = this.NameProtect();
      BlockItem blockitem = (BlockItem)var1.getItem();
      VoxelShape voxelshape = blockitem.getBlock().getDefaultState().getCollisionShape(minecraftClient3.world, blockpos);
      if (voxelshape.isEmpty()) {
         return false;
      }

      Box box = voxelshape.getBoundingBox().offset(blockpos);
      return !box.intersects(minecraftClient3.player.getBoundingBox()) && box.intersects(MovementController.TargetAcquireEvent(2).box9);
   }

   public BlockPos call102() {
      BlockPos blockpos = this.NameProtect();
      return minecraftClient3.world.getBlockState(blockpos).isSolid()
         ? BlockPos.ORIGIN
         : Stream.of(blockpos.west(), blockpos.east(), blockpos.south(), blockpos.north())
            .filter(var0 -> minecraftClient3.world.getBlockState(var0).isSolid())
            .findFirst()
            .orElse(BlockPos.ORIGIN);
   }

   public BlockPos NameProtect() {
      return BlockPos.ofFloored(MovementController.TargetAcquireEvent(1).TriggerBot.add(0.0, -0.001, 0.0));
   }
}
