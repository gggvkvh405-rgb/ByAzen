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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.EffectEngine;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventTick;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.util.CooldownTimer;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "AppleFarm", category = Category.PLAYER, description = "module.appleFarm.desc")
public final class AppleFarm extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AppleFarm appleFarm = new AppleFarm();
   public static final Set<Block> set = Set.of(Blocks.OAK_LEAVES, Blocks.OAK_LOG);
   public static final float float2 = 6.0F;
   public static final int int2 = 4;
   public static final int int3 = 8;
   public static final long long2 = 120L;
   public final Map<String, CooldownTimer> map = new ConcurrentHashMap<>();
   public final Map<String, Rotation> map2 = new ConcurrentHashMap<>();
   public final Map<String, Integer> map3 = new ConcurrentHashMap<>();

   public CooldownTimer RotationTask(String var1) {
      return this.map.computeIfAbsent(var1, var0 -> new CooldownTimer());
   }

   public void EventMixin_modifySetScreenArg(int var1) {
      minecraftClient3.player.getInventory().setSelectedSlot(var1);
      minecraftClient3.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(var1));
   }

   @Override
   public void onEnable() {
      if (minecraftClient3.player != null && this.ItemRegistry(Items.BONE_MEAL) && this.ItemRegistry(Items.OAK_SAPLING)) {
         String s = "main";
         this.map2.remove(s);
         this.map3.remove(s);
         this.RotationTask(s).reset();
         super.onEnable();
      } else {
         this.setToggled(false);
         StyledTextBuilder.RefreshCacheEvent("Нужны костная мука и ростки дуба в инвентаре");
      }
   }

   @Override
   public void onDisable() {
      String s = "main";
      this.map2.remove(s);
      Integer integer = this.map3.remove(s);
      if (integer != null && minecraftClient3.player != null) {
         this.EventMixin_modifySetScreenArg(integer);
      }

      super.onDisable();
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         String s = "main";
         double d0 = minecraftClient3.player.getBlockInteractionRange();
         double d1 = d0 * d0;
         Vec3d vec3d = minecraftClient3.player.getEyePos();
         double d2 = vec3d.getX();
         double d3 = vec3d.getY();
         double d4 = vec3d.getZ();
         BlockPos blockpos = minecraftClient3.player.getBlockPos();
         int i = blockpos.getX();
         int j = blockpos.getY();
         int k = blockpos.getZ();
         int l = Math.min(8, (int)Math.ceil(d0) + 1);
         Mutable mutable = new Mutable();
         Mutable mutable1 = new Mutable();
         BlockPos blockpos1 = null;
         BlockPos blockpos2 = null;
         BlockPos blockpos3 = null;
         double d5 = Double.MAX_VALUE;
         double d6 = Double.MAX_VALUE;
         double d7 = Double.MAX_VALUE;

         for (int i1 = i - 4; i1 <= i + 4; i1++) {
            double d8 = i1 + 0.5 - d2;
            double d9 = d8 * d8;

            for (int j1 = k - 4; j1 <= k + 4; j1++) {
               double d10 = j1 + 0.5 - d4;
               double d11 = d9 + d10 * d10;
               if (!(d11 > d1)) {
                  for (int k1 = j - l; k1 <= j + l; k1++) {
                     mutable.set(i1, k1, j1);
                     BlockState blockstate = minecraftClient3.world.getBlockState(mutable);
                     if (!blockstate.isAir()) {
                        if (blockstate.isOf(Blocks.OAK_LOG) || blockstate.isOf(Blocks.OAK_LEAVES)) {
                           double d15 = k1 + 0.5 - d3;
                           double d17 = d11 + d15 * d15;
                           if (d17 <= d1 && d17 < d5) {
                              d5 = d17;
                              blockpos1 = mutable.toImmutable();
                           }
                        } else if (blockstate.isOf(Blocks.OAK_SAPLING)) {
                           if (i1 != i || j1 != k) {
                              double d12 = k1 + 0.5 - d3;
                              double d13 = d11 + d12 * d12;
                              if (d13 <= d1 && d13 < d6) {
                                 d6 = d13;
                                 blockpos2 = mutable.toImmutable();
                              }
                           }
                        } else if (set.contains(blockstate.getBlock()) && (i1 != i || j1 != k)) {
                           double d14 = k1 + 1.0 - d3;
                           double d16 = d11 + d14 * d14;
                           if (d16 <= d1 && d16 < d7 && minecraftClient3.world.getBlockState(mutable1.set(i1, k1 + 1, j1)).isAir()) {
                              d7 = d16;
                              blockpos3 = mutable.toImmutable();
                           }
                        }
                     }
                  }
               }
            }
         }

         BlockPos blockpos4 = blockpos1 != null ? blockpos1 : (blockpos2 != null ? blockpos2 : blockpos3);
         if (blockpos4 == null) {
            this.map2.remove(s);
         } else {
            Vec3d vec3d1 = blockpos4 == blockpos3 ? Vec3d.ofCenter(blockpos4).add(0.0, 0.5, 0.0) : Vec3d.ofCenter(blockpos4);
            Rotation ililiiili1ll1li11 = RotationMath.Event08(vec3d1.subtract(vec3d));
            this.map2.put(s, ililiiili1ll1li11);
            val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.RectBatch(), ililiiili1ll1li11), val001.RectBatch()), 7, this);
         }
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.interactionManager != null) {
         String s = "main";
         if (minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
            Integer integer = this.map3.remove(s);
            if (integer != null) {
               this.EventMixin_modifySetScreenArg(integer);
            }

            PlayerInventory playerinventory = minecraftClient3.player.getInventory();
            boolean flag = false;
            boolean flag1 = false;
            int i = 0;

            for (int j = playerinventory.size(); i < j; i++) {
               Item item = playerinventory.getStack(i).getItem();
               if (item == Items.OAK_SAPLING) {
                  flag = true;
                  if (flag1) {
                     break;
                  }
               } else if (item == Items.BONE_MEAL) {
                  flag1 = true;
                  if (flag) {
                     break;
                  }
               }
            }

            if (!flag) {
               this.MotorPolicyNet("Кончились ростки дуба");
            } else if (!flag1) {
               this.MotorPolicyNet("Кончилась костная мука");
            } else {
               this.call236();
               Rotation ililiiili1ll1li11 = this.map2.get(s);
               if (ililiiili1ll1li11 != null) {
                  Rotation ililiiili1ll1li111 = val003.CloudRouter().LineShader();
                  if (ililiiili1ll1li111.on23(ililiiili1ll1li11, 6.0F)) {
                     double d0 = minecraftClient3.player.getBlockInteractionRange();
                     BlockHitResult blockhitresult = RaycastUtils.on23(
                        minecraftClient3.player.getCameraPosVec(1.0F),
                        ililiiili1ll1li111,
                        d0,
                        var0 -> {
                           if (var0 == null) {
                              return false;
                           }

                           BlockState blockstate1 = minecraftClient3.world.getBlockState(var0.getBlockPos());
                           return blockstate1.isOf(Blocks.OAK_LOG)
                              || blockstate1.isOf(Blocks.OAK_LEAVES)
                              || blockstate1.isOf(Blocks.OAK_SAPLING)
                              || set.contains(blockstate1.getBlock())
                                 && minecraftClient3.world.getBlockState(var0.getBlockPos().up()).isAir();
                        }
                     );
                     if (blockhitresult != null && blockhitresult.getType() != Type.MISS) {
                        BlockState blockstate = minecraftClient3.world.getBlockState(blockhitresult.getBlockPos());
                        if (!blockstate.isOf(Blocks.OAK_LOG) && !blockstate.isOf(Blocks.OAK_LEAVES)) {
                           if (this.RotationTask(s).EventModifyMouseRotationInput(120L)) {
                              if (blockstate.isOf(Blocks.OAK_SAPLING)) {
                                 int k = ScreenUtils.on23(var1x -> playerinventory.getStack(var1x).getItem() == Items.BONE_MEAL);
                                 if (k == -1) {
                                    if (TaskScheduler.Easing(AppleFarm.class)) {
                                       Slot slot = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, Items.BONE_MEAL);
                                       if (slot != null) {
                                          int i1 = this.call237();
                                          TaskScheduler.on23(AppleFarm.class, () -> ScreenUtils.on23(slot, i1, true));
                                       }
                                    }

                                    return;
                                 }

                                 int l = playerinventory.selectedSlot;
                                 if (l != k) {
                                    this.EventMixin_modifySetScreenArg(k);
                                    this.map3.put(s, l);
                                 }

                                 EffectEngine.on23(blockhitresult, Hand.MAIN_HAND);
                              } else {
                                 EffectEngine.on23(blockhitresult, Hand.OFF_HAND);
                              }

                              this.RotationTask(s).reset();
                           }
                        } else {
                           minecraftClient3.interactionManager.updateBlockBreakingProgress(blockhitresult.getBlockPos(), blockhitresult.getSide());
                           minecraftClient3.player.swingHand(Hand.MAIN_HAND);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void call236() {
      if (minecraftClient3.player.getStackInHand(Hand.OFF_HAND).getItem() != Items.OAK_SAPLING && TaskScheduler.Easing(AppleFarm.class)) {
         Slot slot = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, Items.OAK_SAPLING);
         if (slot != null) {
            TaskScheduler.on23(AppleFarm.class, () -> ScreenUtils.on23(slot, Hand.OFF_HAND, true));
         }
      }
   }

   public int call237() {
      PlayerInventory playerinventory = minecraftClient3.player.getInventory();

      for (int i = 0; i < 9; i++) {
         if (playerinventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      return playerinventory.selectedSlot;
   }

   public void MotorPolicyNet(String var1) {
      StyledTextBuilder.RefreshCacheEvent(var1);
      this.setToggled(false);
   }

   public boolean ItemRegistry(Item var1) {
      PlayerInventory playerinventory = minecraftClient3.player.getInventory();
      int i = 0;

      for (int j = playerinventory.size(); i < j; i++) {
         if (playerinventory.getStack(i).getItem() == var1) {
            return true;
         }
      }

      return false;
   }
}
