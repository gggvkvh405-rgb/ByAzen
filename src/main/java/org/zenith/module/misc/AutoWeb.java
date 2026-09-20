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
import java.util.Comparator;
import java.util.List;
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

@ModuleInfo(name = "AutoWeb", description = "", category = Category.MISC)
public final class AutoWeb extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoWeb autoWeb = new AutoWeb();
   public final NumberSetting searchXZ = new NumberSetting("module.autoWeb.searchXZ", 4.0F, 0.0F, 6.0F, 0.1F, "module.autoWeb.searchXZ.desc", "b");
   public final NumberSetting search = new NumberSetting("module.autoWeb.search", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.search.desc", "b");
   public final NumberSetting distance4 = new NumberSetting("module.autoWeb.distance", 4.0F, 1.0F, 6.0F, 0.1F, "module.autoWeb.distance.desc", "b");
   public final ModeSetting hand2 = new ModeSetting("module.autoWeb.hand", "module.autoWeb.hand.desc", "module.autoWeb.rightHand", "module.autoWeb.leftHand");
   public PlayerEntity playerEntity;
   public Slot slot = null;
   public Rotation var1182 = null;
   public Rotation var1183 = null;
   public AutoWeb.PlacementRule autoWebVar159 = null;
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

   @EventTarget
   public void Easing(PreventActionEvent var1) {
      if (AutoTrap.autoTrap.boolean183() && !var1.isCancelled() && minecraftClient3.player.age % 2 == 0) {
         BlockHitResult blockhitresult = this.autoWebVar159 != null ? this.autoWebVar159.isRotate(ZenithClient.on23().CloudRouter().LineShader()) : null;
         if (blockhitresult != null
            && minecraftClient3.player.getStackInHand(this.hand2.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem() == Items.COBWEB
            )
          {
            EffectEngine.on23(blockhitresult, this.hand2.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND);
            WorldRender.on23(new Box(blockhitresult.getBlockPos()), -1, 1.0F);
            this.var1182 = null;
            this.autoWebVar159 = null;
         }
      }
   }

   @EventTarget
   public void TextScanner(RotationUpdateStartEvent var1) {
      if (AutoTrap.autoTrap.boolean183()) {
         this.playerEntity = this.int152();
         if (this.playerEntity != null) {
            this.int151();
            Rotation ililiiili1ll1li11 = this.var1182;
            if (ililiiili1ll1li11 != null) {
               val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11), val001.HudPreviewItem()), 5, this);
            }
         }
      }
   }

   public void int151() {
      if (AutoTrap.autoTrap.boolean183()) {
         try {
            Slot slot = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, var0 -> var0.getStack().getItem() == Items.COBWEB);
            byte b0 = 0;
            if (slot != null) {
               BlockPos blockpos = this.playerEntity.getBlockPos();
               this.list = EffectEngine.on23(blockpos, (int)Math.ceil(this.searchXZ.getCurrent()), (int)Math.ceil(this.search.getCurrent()))
                  .stream()
                  .filter(
                     var1x -> {
                        BlockState blockstate1 = minecraftClient3.world.getBlockState(var1x);
                        return blockstate1.isAir()
                           && this.playerEntity
                              .getBoundingBox()
                              .expand(this.searchXZ.getCurrent(), this.search.getCurrent(), this.searchXZ.getCurrent())
                              .intersects(new Box(var1x));
                     }
                  )
                  .sorted(Comparator.comparingDouble(var1x -> var1x.getSquaredDistance(blockpos)))
                  .toList();

               label79:
               for (BlockPos blockpos1 : this.list) {
                  BlockState blockstate = minecraftClient3.world.getBlockState(blockpos1);
                  if (blockstate.isReplaceable() && !blockstate.isAir()) {
                     Rotation ililiiili1ll1li11 = RotationMath.Event08(
                        new Vec3d(blockpos1.getX() + 0.5F, blockpos1.getY() + 0.5F, blockpos1.getZ() + 0.5F)
                     );
                     BlockHitResult blockhitresult1 = RaycastUtils.on23(
                        minecraftClient3.player.getCameraPosVec(1.0F),
                        ililiiili1ll1li11,
                        3.0,
                        var1x -> var1x != null && this.list.contains(var1x.getBlockPos())
                     );
                     if (blockhitresult1 != null && blockhitresult1.getType() != Type.MISS) {
                        this.autoWebVar159 = var1x -> {
                           BlockHitResult blockhitresult2 = RaycastUtils.on23(
                              minecraftClient3.player.getCameraPosVec(1.0F),
                              var1x,
                              this.distance4.getCurrent(),
                              var1xx -> var1xx != null
                                 && !minecraftClient3.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                                 && this.list.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                           );
                           return blockhitresult2 != null && blockhitresult2.getType() != Type.MISS ? blockhitresult2 : null;
                        };
                        this.var1182 = ililiiili1ll1li11;
                     }
                  } else {
                     Direction[] adirection = Direction.values();
                     int i = adirection.length;
                     int j = 0;

                     while (true) {
                        if (j < i) {
                           Direction direction = adirection[j];
                           Vec3d vec3d = Vec3d.ofCenter(blockpos1).add(Vec3d.of(direction.getVector()).multiply(0.5));
                           Rotation ililiiili1ll1li111 = RotationMath.BotChatEvent(vec3d);
                           BlockHitResult blockhitresult = RaycastUtils.on23(
                              minecraftClient3.player.getCameraPosVec(1.0F),
                              ililiiili1ll1li111,
                              this.distance4.getCurrent(),
                              var1x -> var1x != null
                                 && !minecraftClient3.world.getBlockState(var1x.getBlockPos()).isReplaceable()
                                 && this.list.contains(var1x.getBlockPos().offset(var1x.getSide()))
                           );
                           if (blockhitresult == null || blockhitresult.getType() == Type.MISS) {
                              j++;
                              continue;
                           }

                           if (b0 == 0) {
                              this.autoWebVar159 = var1x -> {
                                 BlockHitResult blockhitresult2 = RaycastUtils.on23(
                                    minecraftClient3.player.getCameraPosVec(1.0F),
                                    var1x,
                                    this.distance4.getCurrent(),
                                    var1xx -> var1xx != null
                                       && !minecraftClient3.world.getBlockState(var1xx.getBlockPos()).isReplaceable()
                                       && this.list.contains(var1xx.getBlockPos().offset(var1xx.getSide()))
                                 );
                                 return blockhitresult2 != null && blockhitresult2.getType() != Type.MISS ? blockhitresult2 : null;
                              };
                              this.var1182 = ililiiili1ll1li111;
                           } else {
                              this.var1183 = ililiiili1ll1li111;
                           }

                           b0++;
                        }

                        if (b0 == 2) {
                           break label79;
                        }
                        break;
                     }
                  }
               }

               if (TaskScheduler.Easing(AutoWeb.class) && (TaskScheduler.Easing(AutoTotem.class) || this.hand2.is(0))) {
                  if (b0 > 0) {
                     if (minecraftClient3.player.getStackInHand(this.hand2.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND).getItem()
                        != Items.COBWEB) {
                        TaskScheduler.on23(AutoWeb.class, () -> ScreenUtils.on23(slot, this.hand2.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true));
                        if (this.slot == null) {
                           this.slot = slot;
                        }
                     }
                  } else if (this.slot != null) {
                     Slot slot1 = this.slot;
                     TaskScheduler.on23(AutoWeb.class, () -> ScreenUtils.on23(slot1, this.hand2.is(0) ? Hand.MAIN_HAND : Hand.OFF_HAND, true));
                     this.slot = null;
                     return;
                  }
               }
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @EventTarget
   public void UiAnimation(MovementInputEvent var1) {
      if (this.autoWebVar159 != null) {
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
               && minecraftClient3.player.squaredDistanceTo(var1) < (this.distance4.getCurrent() + 0.1) * (this.distance4.getCurrent() + 0.1)
         )
         .min(Comparator.comparingDouble(var0 -> minecraftClient3.player.squaredDistanceTo(var0) - (var0 == Aura.aura.var11813() ? 100 : 0)))
         .orElse(null);
   }


   public interface PlacementRule {
      BlockHitResult isRotate(Rotation var1);
   }
}
