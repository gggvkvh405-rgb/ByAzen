package org.zenith.module.combat;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;


import net.minecraft.network.listener.ClientPlayPacketListener;
import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.EventTickEnd;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "Backtrack", description = "", category = Category.COMBAT)
public final class Backtrack extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Backtrack reachV3 = new Backtrack();
   public final NumberSetting delay3 = new NumberSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
   public final BooleanSetting renderBox = new BooleanSetting("module.backtrack.renderBox", "module.backtrack.renderBox.desc", true);
   public final Queue<Backtrack.Service> queue5 = new ConcurrentLinkedQueue<>();
   public Vec3d vec3d33 = null;
   public Vec3d vec3d34 = null;
   public CooldownTimer zClass06731 = new CooldownTimer();
   public final CooldownTimer zClass06732 = new CooldownTimer();
   public final CooldownTimer zClass06733 = new CooldownTimer();
   public boolean boolean37 = false;
   public Box box8 = null;

   @Override
   public void onDisable() {
      this.boolean37 = true;
   }

   public void ItemRegistry(Vec3d var1) {
      if (var1 != null && !this.queue5.isEmpty()) {
         for (Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil : this.queue5) {
            ili1111ii1l1li_ii1il11l111ii11iil.TradeGuardService(true);
            if (ili1111ii1l1li_ii1il11l111ii11iil.var126() != null && ili1111ii1l1li_ii1il11l111ii11iil.var126().equals(var1)) {
               break;
            }
         }
      }
   }

   @EventTarget
   public void ItemRegistry(EventTick var1) {
      if (this.zClass054() == null) {
         this.vec3d33 = null;
         this.box8 = null;
         this.vec3d34 = null;
      } else {
         if (this.vec3d33 == null || this.zClass06731.EventModifyMouseRotationInput(1000L)) {
            this.vec3d33 = this.zClass054().getEntityPos();
            this.vec3d34 = this.zClass054().getEntityPos();
         }

         if (this.vec3d33 != null) {
            this.box8 = this.zClass054().dimensions.getBoxAt(this.vec3d33);
         } else {
            this.box8 = null;
         }
      }
   }

   public LivingEntity zClass054() {
      return TriggerBot.triggerBot.zClass054() != null ? TriggerBot.triggerBot.zClass054() : Aura.aura.zClass054();
   }

   @EventTarget
   public void on23(EventTickEnd var1) {
      if (minecraftClient3.player != null && this.zClass054() != null) {
         Vec3d vec3d = minecraftClient3.player.getCameraPosVec(1.0F);
         Box box = minecraftClient3.player.getBoundingBox();
         Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil = null;
         Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil1 = null;
         int i = 0;

         for (Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil2 : this.queue5) {
            if (ili1111ii1l1li_ii1il11l111ii11iil2.var126() != null
               && this.zClass054().dimensions.getBoxAt(ili1111ii1l1li_ii1il11l111ii11iil2.var126()).intersects(minecraftClient3.player.getBoundingBox())) {
               ili1111ii1l1li_ii1il11l111ii11iil1 = ili1111ii1l1li_ii1il11l111ii11iil2;
               if (i > 0) {
                  break;
               }

               i++;
            }
         }

         if (ili1111ii1l1li_ii1il11l111ii11iil1 != null) {
            ili1111ii1l1li_ii1il11l111ii11iil = ili1111ii1l1li_ii1il11l111ii11iil1;
         } else {
            double d1 = Double.POSITIVE_INFINITY;
            Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil3 = null;

            for (Backtrack.Service ili1111ii1l1li_ii1il11l111ii11iil4 : this.queue5) {
               if (ili1111ii1l1li_ii1il11l111ii11iil4.var126() != null) {
                  double d0 = minecraftClient3.player.squaredDistanceTo(ili1111ii1l1li_ii1il11l111ii11iil4.vec3d17);
                  if (d0 < d1) {
                     d1 = d0;
                     ili1111ii1l1li_ii1il11l111ii11iil3 = ili1111ii1l1li_ii1il11l111ii11iil4;
                  }
               }
            }

            ili1111ii1l1li_ii1il11l111ii11iil = ili1111ii1l1li_ii1il11l111ii11iil3;
         }

         Vec3d vec3d1 = ili1111ii1l1li_ii1il11l111ii11iil != null ? ili1111ii1l1li_ii1il11l111ii11iil.var126() : null;
         this.vec3d34 = vec3d1;
         this.ItemRegistry(vec3d1);
      } else {
         this.vec3d34 = null;
      }
   }

   @EventTarget
   public void Easing(EventHookWorldRender var1) {
      if (this.box8 != null && this.renderBox.isEnabled()) {
         WorldRender.on23(this.box8, -1, 1.0F);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows()) {
         if (this.zClass06731 == null) {
            this.zClass06731 = new CooldownTimer();
         }

         try {
            Packet packet = var1.ItemScroller();
            if (packet instanceof DisconnectS2CPacket || packet instanceof PlayerPositionLookS2CPacket) {
               this.zClass06732.reset();
               this.zClass06733.reset();
            }

            LivingEntity livingentity = minecraftClient3.world == null ? null : this.zClass054();
            if (livingentity != null) {
               if (this.vec3d33 == null) {
                  return;
               }

               if (packet instanceof EntityPositionS2CPacket entitypositions2cpacket && entitypositions2cpacket.entityId() == livingentity.getId()) {
                  this.vec3d33 = new Vec3d(
                     entitypositions2cpacket.change().position().getX(),
                     entitypositions2cpacket.change().position().getY(),
                     entitypositions2cpacket.change().position().getZ()
                  );
                  this.zClass06731.reset();
               } else if (packet instanceof EntityPositionSyncS2CPacket entitypositionsyncs2cpacket && entitypositionsyncs2cpacket.id() == livingentity.getId()) {
                  this.vec3d33 = entitypositionsyncs2cpacket.values().position();
                  this.zClass06731.reset();
               } else if (packet instanceof EntityS2CPacket entitys2cpacket
                  && entitys2cpacket.getEntity(minecraftClient3.world) == livingentity
                  && this.vec3d33 != null) {
                  this.vec3d33 = this.vec3d33
                     .add(entitys2cpacket.getDeltaX() / 4096.0, entitys2cpacket.getDeltaY() / 4096.0, entitys2cpacket.getDeltaZ() / 4096.0);
                  this.zClass06731.reset();
               }
            }

            if (!this.queue5.isEmpty()
               || this.zClass06732.EventModifyMouseRotationInput(200L)
                  && minecraftClient3.world != null
                  && livingentity != null
                  && this.zClass06733.EventModifyMouseRotationInput(200L)) {
               if (!(packet instanceof ChatMessageC2SPacket)
                  && !(packet instanceof HealthUpdateS2CPacket)
                  && !(packet instanceof PlaySoundS2CPacket)
                  && !(packet instanceof GameMessageS2CPacket)
                  && !(packet instanceof CommandExecutionC2SPacket)) {
                  var1.cancel();
                  Vec3d vec3d = this.on23(packet, livingentity) ? this.vec3d33 : null;
                  this.queue5.add(new Backtrack.Service(packet, vec3d));
                  return;
               }

               return;
            }

            return;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @EventTarget
   public void on23(EventHookPacketProcess var1) {
      long i = System.currentTimeMillis();
      if (minecraftClient3.world == null || minecraftClient3.player == null) {
         this.queue5.clear();
      }

      try {
         this.queue5.removeIf(var3 -> {
            boolean flag = (float)(i - var3.getTime()) >= this.delay3.getCurrent();
            boolean flag1 = var3.random5();
            if (!flag && this.zClass06732.EventModifyMouseRotationInput(200L) && !flag1 && minecraftClient3.world != null && this.zClass054() != null) {
               return false;
            }

            try {
               var3.ItemScroller().apply(minecraftClient3.getNetworkHandler());
            } catch (Throwable throwable) {
               if (throwable instanceof ClassCastException) {
                  return true;
               }

               throwable.printStackTrace();
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

   public boolean on23(Packet<?> var1, Entity var2) {
      if (var2 == null) {
         return false;
      } else {
         int i = var2.getId();
         if (var1 instanceof EntityPositionS2CPacket entitypositions2cpacket) {
            return entitypositions2cpacket.entityId() == i;
         } else if (var1 instanceof EntityPositionSyncS2CPacket entitypositionsyncs2cpacket) {
            return entitypositionsyncs2cpacket.id() == i;
         } else {
            return var1 instanceof EntityS2CPacket entitys2cpacket ? entitys2cpacket.getEntity(minecraftClient3.world) == var2 : false;
         }
      }
   }

   public Vec3d float73() {
      return this.vec3d33;
   }

   public void ItemSpec(Vec3d var1) {
      this.vec3d33 = var1;
   }

   public Vec3d float272() {
      return this.vec3d34;
   }


   public static class Service {
      public final Packet<?> packet3;
      public final long long91;
      public final Vec3d vec3d17;
      public boolean boolean81;

      public Service(Packet<?> var1, Vec3d var2) {
         this.packet3 = var1;
         this.long91 = System.currentTimeMillis();
         this.vec3d17 = var2;
         this.boolean81 = false;
      }

      public Packet<ClientPlayPacketListener> ItemScroller() {
         return (Packet<ClientPlayPacketListener>)this.packet3;
      }

      public Packet<?> float273() {
         return this.packet3;
      }

      public long getTime() {
         return this.long91;
      }

      public Vec3d var126() {
         return this.vec3d17;
      }

      public boolean random5() {
         return this.boolean81;
      }

      public void TradeGuardService(boolean var1) {
         this.boolean81 = var1;
      }
   }
}
