package org.zenith.module.misc;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PreventActionEvent;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MovementUtils;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "AutoTrap", description = "", category = Category.MISC)
public final class AutoTrap extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoTrap autoTrap = new AutoTrap();
   public final NumberSetting distance3 = new NumberSetting("module.autoWeb.distance", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoTrap.distance.desc", "b");
   public final ModeSetting hand = new ModeSetting("module.autoWeb.hand", "module.autoTrap.hand.desc", "module.autoWeb.rightHand", "module.autoWeb.leftHand");
   public PlayerEntity playerEntity;
   public Slot slot = null;
   public Rotation var1182 = null;
   public Rotation var1183 = null;
   public AutoTrap.PlacementRule autoTrapVar159 = null;
   public CooldownTimer zClass06713 = new CooldownTimer();
   List<BlockPos> list;

   @Override
   public void onEnable() {
      if (EffectEngine.double69()) {
         this.setEnabled(false);
      } else {
         super.onEnable();
      }
   }

   public boolean boolean183() {
      return this.autoTrapVar159 == null && this.slot == null || !this.isEnabled();
   }

   @EventTarget
   public void Easing(PreventActionEvent var1) {
      if (!var1.isCancelled() && minecraftClient3.player.age % 2 == 0) {
         BlockHitResult blockhitresult = this.autoTrapVar159 != null ? this.autoTrapVar159.isRotate(ZenithClient.on23().CloudRouter().LineShader()) : null;
         if (blockhitresult != null
            && minecraftClient3.player.getStackInHand(this.hand.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem() == Items.OBSIDIAN) {
            EffectEngine.on23(blockhitresult, this.hand.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND);
            WorldRender.on23(new Box(blockhitresult.getBlockPos()), -1, 1.0F);
            this.var1182 = null;
            this.autoTrapVar159 = null;
         }
      }
   }

   @EventTarget
   public void TextScanner(RotationUpdateStartEvent var1) {
      this.playerEntity = this.int152();
      if (this.playerEntity != null) {
         this.int151();
         Rotation ililiiili1ll1li11 = this.var1182;
         if (ililiiili1ll1li11 != null) {
            val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11), val001.HudPreviewItem()), 5, this);
         }
      }
   }

   public void int151() {
      try {
         Slot slot = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, var0 -> var0.getStack().getItem() == Items.OBSIDIAN);
         byte b0 = 0;
         if (slot != null) {
            BlockPos blockpos = this.playerEntity.getBlockPos();
            Box box = this.playerEntity.getBoundingBox();
            List<BlockPos> arraylist = new ArrayList<>();

            for (int i = (int)Math.floor(box.minX); i <= (int)Math.floor(box.maxX); i++) {
               for (int j = (int)Math.floor(box.minY); j <= (int)Math.floor(box.maxY + 1.0); j++) {
                  for (int k = (int)Math.floor(box.minZ); k <= (int)Math.floor(box.maxZ); k++) {
                     arraylist.add(new BlockPos(i, j, k));
                  }
               }
            }

            this.list = arraylist.stream().flatMap(var0 -> Stream.of(Direction.values()).map(var0::offset)).distinct().filter(var1x -> {
               if (box.intersects(new Box(var1x))) {
                  return false;
               }

               if (minecraftClient3.player.getBoundingBox().intersects(new Box(var1x))) {
                  return false;
               }

               BlockState blockstate1 = minecraftClient3.world.getBlockState(var1x);
               return blockstate1.isReplaceable();
            }).sorted(Comparator.comparingDouble(var1x -> var1x.getSquaredDistance(this.playerEntity.getEntityPos()))).toList();

            label87:
            for (BlockPos blockpos1 : this.list) {
               BlockState blockstate = minecraftClient3.world.getBlockState(blockpos1);
               if (blockstate.isReplaceable() && !blockstate.isAir()) {
                  Rotation ililiiili1ll1li111 = RotationMath.Event08(
                     new Vec3d(blockpos1.getX() + 0.5F, blockpos1.getY() + 0.5F, blockpos1.getZ() + 0.5F)
                  );
                  BlockHitResult blockhitresult1 = RaycastUtils.on23(
                     minecraftClient3.player.getCameraPosVec(1.0F), ililiiili1ll1li111, 3.0, var1x -> var1x != null && this.list.contains(var1x.getBlockPos())
                  );
                  if (blockhitresult1 != null && blockhitresult1.getType() != Type.MISS) {
                     this.autoTrapVar159 = var1x -> {
                        BlockHitResult blockhitresult2 = RaycastUtils.on23(
                           minecraftClient3.player.getCameraPosVec(1.0F),
                           var1x,
                           this.distance3.getCurrent(),
                           var1xx -> var1xx != null
                              && !minecraftClient3.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                              && this.list.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                        );
                        return blockhitresult2 != null && blockhitresult2.getType() != Type.MISS ? blockhitresult2 : null;
                     };
                     this.var1182 = ililiiili1ll1li111;
                  }
               } else {
                  Direction[] adirection = Direction.values();
                  int l = adirection.length;
                  int i1 = 0;

                  while (true) {
                     if (i1 < l) {
                        Direction direction = adirection[i1];
                        Vec3d vec3d = Vec3d.ofCenter(blockpos1).add(Vec3d.of(direction.getVector()).multiply(0.5));
                        Rotation ililiiili1ll1li11 = RotationMath.BotChatEvent(vec3d);
                        BlockHitResult blockhitresult = RaycastUtils.on23(
                           minecraftClient3.player.getCameraPosVec(1.0F),
                           ililiiili1ll1li11,
                           this.distance3.getCurrent(),
                           var1x -> var1x != null
                              && !minecraftClient3.world.getBlockState(var1x.getBlockPos()).isReplaceable()
                              && this.list.contains(var1x.getBlockPos().offset(var1x.getSide()))
                        );
                        if (blockhitresult == null || blockhitresult.getType() == Type.MISS) {
                           i1++;
                           continue;
                        }

                        if (b0 == 0) {
                           this.autoTrapVar159 = var1x -> {
                              BlockHitResult blockhitresult2 = RaycastUtils.on23(
                                 minecraftClient3.player.getCameraPosVec(1.0F),
                                 var1x,
                                 this.distance3.getCurrent(),
                                 var1xx -> var1xx != null
                                    && !minecraftClient3.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                                    && this.list.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                              );
                              return blockhitresult2 != null && blockhitresult2.getType() != Type.MISS ? blockhitresult2 : null;
                           };
                           this.var1182 = ililiiili1ll1li11;
                        } else {
                           this.var1183 = ililiiili1ll1li11;
                        }

                        b0++;
                     }

                     if (b0 == 2) {
                        break label87;
                     }
                     break;
                  }
               }
            }

            if (TaskScheduler.Easing(AutoWeb.class) && (TaskScheduler.Easing(AutoTotem.class) || this.hand.is(0))) {
               if (b0 > 0) {
                  if (minecraftClient3.player.getStackInHand(this.hand.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem()
                     != Items.OBSIDIAN) {
                     TaskScheduler.on23(AutoWeb.class, () -> ScreenUtils.on23(slot, this.hand.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true));
                     if (this.slot == null) {
                        this.slot = slot;
                     }
                  }
               } else if (this.slot != null) {
                  Slot slot1 = this.slot;
                  TaskScheduler.on23(AutoWeb.class, () -> ScreenUtils.on23(slot1, this.hand.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true));
                  this.slot = null;
                  return;
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @EventTarget
   public void UiAnimation(MovementInputEvent var1) {
      if (this.autoTrapVar159 != null) {
         MovementUtils.on23(var1, val003.CloudRouter().LineShader().GrimGlide(), minecraftClient3.player.getYaw());
      }
   }

   public PlayerEntity int152() {
      return minecraftClient3.world
         .getPlayers()
         .stream()
         .filter(
            var1 -> var1 != minecraftClient3.player
               && !ZenithClient.on23().MediaTrackInfo().UiAnimation(var1)
               && minecraftClient3.player.squaredDistanceTo(var1) < (this.distance3.getCurrent() + 0.1) * (this.distance3.getCurrent() + 0.1)
         )
         .min(Comparator.comparingDouble(var0 -> minecraftClient3.player.squaredDistanceTo(var0) - (var0 == Aura.aura.var11813() ? 100 : 0)))
         .orElse(null);
   }


   public interface PlacementRule {
      BlockHitResult isRotate(Rotation var1);
   }
}
