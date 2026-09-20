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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.BlockFaceHit;
import org.zenith.core.EffectEngine;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PacketEvent;
import org.zenith.rotation.RotationMath;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.MovementUtils;

@ModuleInfo(name = "CastleFly", description = "", category = Category.MOVEMENT)
public final class CastleFly extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final CastleFly castleFly = new CastleFly();
   public BlockFaceHit zClass027Var159;
   public ModeSetting mode3 = new ModeSetting("module.flyBypass.mode", "module.flyBypass.mode.desc", "module.flyBypass.safeMode", "module.flyBypass.riskMode");
   public boolean boolean37 = false;
   public int int89;
   public final NumberSetting delay = new NumberSetting("module.flyBypass.delay", 500.0F, 50.0F, 1000.0F, 50.0F, "module.flyBypass.delay.desc", "ms");
   public final Queue<Backtrack.Service> queue2 = new ConcurrentLinkedQueue<>();
   public BlockHitResult blockHitResult = null;
   int int338 = 0;

   @Override
   public void onEnable() {
      super.onEnable();
      StyledTextBuilder.RefreshCacheEvent("Для работы держите ЛЮБОЙ блок руках");
   }

   @Override
   public void onDisable() {
      this.boolean37 = true;
   }

   @EventTarget
   public void ItemRegistry(EventHookWorldRender var1) {
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && !var1.isCancelled()) {
         try {
            Packet packet = var1.ItemScroller();
            if (this.queue2.isEmpty() && minecraftClient3.world == null) {
               return;
            }

            if (packet instanceof ChatMessageC2SPacket || packet instanceof HealthUpdateS2CPacket || packet instanceof GameMessageS2CPacket || packet instanceof CommandExecutionC2SPacket) {
               return;
            }

            var1.cancel();
            Vec3d vec3d = null;
            if (packet instanceof BlockUpdateS2CPacket blockupdates2cpacket) {
               vec3d = new Vec3d(blockupdates2cpacket.getPos());
            }

            this.queue2.add(new Backtrack.Service(packet, vec3d));
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @EventTarget
   public void on23(EventHookPacketProcess var1) {
      long i = System.currentTimeMillis();
      if (minecraftClient3.world == null || minecraftClient3.player == null) {
         this.queue2.clear();
      }

      try {
         this.queue2.removeIf(var3 -> {
            boolean flag = i - var3.getTime() >= (long)(this.delay.getCurrent() * 2.0F);
            if (!flag && minecraftClient3.world != null && !this.boolean37) {
               return false;
            }

            try {
               var3.ItemScroller().apply(minecraftClient3.getNetworkHandler());
            } catch (Throwable var6) {
            }

            return true;
         });
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      if (this.boolean37) {
         this.boolean37 = false;
         super.onDisable();
      }
   }

   @EventTarget
   public void NbtItemSpec(RotationUpdateStartEvent var1) {
   }

   @EventTarget(0)
   public void EnchantItemSpec(MovementInputEvent var1) {
      MovementUtils.on23(var1, ZenithClient.on23().CloudRouter().LineShader().GrimGlide(), RotationMath.boolean122().GrimGlide());
   }

   @EventTarget
   public void EnchantItemSpec(EventTick var1) {
      if (this.int338 > 0) {
         this.int338--;
      } else if (this.mode3.is(0)) {
         if (minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult && blockhitresult.getType() == Type.BLOCK) {
            EffectEngine.on23(blockhitresult, Hand.MAIN_HAND);
            this.int338 = 2;
         }
      } else if (minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult1
         && this.zClass027Var159 != null
         && this.zClass027Var159.zClass095Var165().equals(blockhitresult1.getBlockPos())) {
         Vec3d vec3d = minecraftClient3.player.getEyePos();
         BlockPos blockpos = blockhitresult1.getBlockPos().offset(this.zClass027Var159.double70());
         if (!on23(vec3d, blockpos, this.zClass027Var159.double70())) {
            return;
         }

         EffectEngine.on23(
            new BlockHitResult(
               this.zClass027Var159.zClass095Var165().toCenterPos().add((float)(Math.random() / 2.0)),
               this.zClass027Var159.double70(),
               this.zClass027Var159.zClass095Var165(),
               false
            ),
            Hand.MAIN_HAND
         );
         this.int338 = 1;
      }
   }

   public static boolean on23(Vec3d var0, BlockPos var1, Direction var2) {
      double d0 = Double.MAX_VALUE;
      double d1 = Double.MIN_VALUE;
      Box box = new Box(
         var0.x, minecraftClient3.player.getY() - 1.0, var0.z, var0.x, var0.y + d1, var0.z
      );
      Box box1 = new Box(var1.offset(var2));
      if (box.intersects(box1)) {
         return true;
      }

      return switch (var2) {
         case NORTH -> box.minZ > box1.minZ;
         case SOUTH -> box.maxZ < box1.maxZ;
         case EAST -> box.maxX < box1.maxX;
         case WEST -> box.minX > box1.minX;
         case UP -> box.maxY < box1.maxY;
         case DOWN -> box.minY > box1.minY;
         default -> throw new MatchException(null, null);
      };
   }

   public BlockFaceHit CloudApiClient(BlockPos var1) {
      BlockFaceHit iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = null;
      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1);
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(-1, 0, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(1, 0, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, 0, 1));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, 0, -1));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(-2, 0, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(2, 0, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, 0, 2));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, 0, -2));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, -1, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(1, -1, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(-1, -1, 0));
      if (iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null) {
         return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil;
      }

      iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil = EffectEngine.EventModifyMouseRotationInput(var1.add(0, -1, 1));
      return iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil != null
         ? iii1l1lii1lliiil1ll1iill1_ii1il11l111ii11iil
         : EffectEngine.EventModifyMouseRotationInput(var1.add(0, -1, -1));
   }

   public boolean ItemSpec(double var1, double var3) {
      return !minecraftClient3.world
         .getBlockCollisions(minecraftClient3.player, minecraftClient3.player.getBoundingBox().expand(-0.1, 0.0, -0.1).offset(var1, -2.0, var3))
         .iterator()
         .hasNext();
   }
}
