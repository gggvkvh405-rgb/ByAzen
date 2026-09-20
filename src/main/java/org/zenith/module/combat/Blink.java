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

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.PacketEvent;
import org.zenith.event.PacketSendEvent;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "Blink", description = "Queues outgoing packets until disabled", category = Category.COMBAT)
public final class Blink extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Blink blink = new Blink();
   public static final long long76 = 1000L;
   public static final double double9 = 1.5;
   public final NumberSetting packetsPerSecond = new NumberSetting(
      "module.blink.packetsPerSecond", 283.0F, 1.0F, 500.0F, 1.0F, "module.blink.packetsPerSecond.desc", "pps"
   );
   public final Queue<Blink.QueuedPacket> queue = new ConcurrentLinkedQueue<>();
   public final Set<Integer> set6 = new HashSet<>();
   public volatile boolean boolean49;
   public boolean boolean33;
   public boolean boolean34;
   public long long77;
   public long long78;
   public double double10;
   public Vec3d vec3d6;

   @Override
   public void onEnable() {
      this.queue.clear();
      this.call075();
      if (!this.boolean34) {
         this.boolean34 = true;
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      if (this.boolean34) {
         if (!this.queue.isEmpty() && minecraftClient3.getNetworkHandler() != null) {
            this.call169();
            this.call044();
         } else {
            this.call028();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() != null) {
         if (minecraftClient3.player == null || minecraftClient3.player.isDead()) {
            this.call012();
            if (this.isEnabled()) {
               this.setToggled(false);
            } else if (this.boolean49) {
               this.call028();
            }
         } else if (var1.ItemScroller() instanceof DisconnectS2CPacket || var1.ItemScroller() instanceof PlayerPositionLookS2CPacket) {
            this.call012();
            if (this.boolean49) {
               this.call028();
            }
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventHookPacketProcess var1) {
      if (this.boolean49) {
         this.call044();
      }
   }

   @EventTarget(0)
   public void on23(PacketSendEvent var1) {
      if (!var1.isCancelled() && !this.boolean33 && var1.ItemScroller() != null) {
         Packet packet = var1.ItemScroller();
         if (this.boolean49) {
            var1.cancel();
            this.Easing(packet);
         } else if (minecraftClient3.player == null || minecraftClient3.world == null) {
            this.call012();
         } else if (!this.UiAnimation(packet)) {
            var1.cancel();
            this.Easing(packet);
         }
      }
   }

   public boolean call045() {
      return this.boolean49 || this.boolean33;
   }

   public boolean UiAnimation(Packet<?> var1) {
      return var1 instanceof ChatMessageC2SPacket || var1 instanceof CommandExecutionC2SPacket || var1 instanceof ResourcePackStatusC2SPacket;
   }

   public void Easing(Packet<?> var1) {
      this.queue.add(new Blink.QueuedPacket(var1, this.ColorAnimator(var1)));
   }

   public void call169() {
      this.boolean49 = true;
      this.boolean33 = false;
      this.long78 = System.currentTimeMillis();
      this.long77 = 0L;
      this.double10 = Math.max(1.0, this.call046());
      this.vec3d6 = null;
      this.set6.clear();
   }

   public void call044() {
      if (this.boolean49) {
         if (minecraftClient3.getNetworkHandler() == null || minecraftClient3.world == null) {
            this.call028();
         } else if (this.queue.isEmpty()) {
            this.call028();
         } else {
            long i = System.currentTimeMillis();
            if (this.long77 > i) {
               this.long78 = i;
            } else {
               if (this.long77 != 0L) {
                  this.long77 = 0L;
                  this.long78 = i;
                  this.double10 = Math.max(this.double10, 1.0);
               } else {
                  this.ItemServiceBase(i);
               }

               while (this.double10 >= 1.0 && !this.queue.isEmpty()) {
                  Blink.QueuedPacket l1li1iili1li1l11i11ilii111_ii1il11l111ii11iil = this.queue.poll();
                  this.on23(l1li1iili1li1l11i11ilii111_ii1il11l111ii11iil);
                  this.double10--;
                  if (l1li1iili1li1l11i11ilii111_ii1il11l111ii11iil.call047() != null
                     && this.TextScanner(l1li1iili1li1l11i11ilii111_ii1il11l111ii11iil.call047())) {
                     this.long77 = System.currentTimeMillis() + 200L;
                     this.long78 = System.currentTimeMillis();
                     this.double10 = 0.0;
                     break;
                  }
               }

               if (this.queue.isEmpty()) {
                  this.call028();
               }
            }
         }
      }
   }

   public void ItemServiceBase(long var1) {
      long i = Math.max(0L, var1 - this.long78);
      this.long78 = var1;
      this.double10 = this.double10 + this.call124() * (i / 1000.0);
      this.double10 = Math.min(this.double10, this.call124());
   }

   public double call124() {
      return Math.max(1.0, this.packetsPerSecond.getCurrent() * 2.0F);
   }

   public double call046() {
      return this.call124() / 20.0;
   }

   public void on23(Blink.QueuedPacket var1) {
      if (minecraftClient3.getNetworkHandler() != null) {
         this.boolean33 = true;

         try {
            minecraftClient3.getNetworkHandler().sendPacket(var1.call076());
         } finally {
            this.boolean33 = false;
         }
      }
   }

   public boolean TextScanner(Vec3d var1) {
      Vec3d vec3d = this.vec3d6 == null ? var1 : this.vec3d6;
      this.vec3d6 = var1;
      return this.UiAnimation(vec3d, var1);
   }

   public boolean UiAnimation(Vec3d var1, Vec3d var2) {
      if (minecraftClient3.world == null) {
         return false;
      }

      Box box = new Box(
            Math.min(var1.x, var2.x),
            Math.min(var1.y, var2.y),
            Math.min(var1.z, var2.z),
            Math.max(var1.x, var2.x),
            Math.max(var1.y, var2.y),
            Math.max(var1.z, var2.z)
         )
         .expand(1.5);
      boolean flag = false;
      double d0 = 2.25;

      for (ItemEntity itementity : minecraftClient3.world.getEntitiesByClass(ItemEntity.class, box, var0 -> !var0.isRemoved())) {
         if (!this.set6.contains(itementity.getId()) && this.on23(itementity.getEntityPos(), var1, var2) <= d0) {
            this.set6.add(itementity.getId());
            flag = true;
         }
      }

      return flag;
   }

   public double on23(Vec3d var1, Vec3d var2, Vec3d var3) {
      double d0 = var3.x - var2.x;
      double d1 = var3.y - var2.y;
      double d2 = var3.z - var2.z;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d3 <= 1.0E-6) {
         return var1.squaredDistanceTo(var3);
      }

      double d4 = ((var1.x - var2.x) * d0 + (var1.y - var2.y) * d1 + (var1.z - var2.z) * d2) / d3;
      d4 = Math.max(0.0, Math.min(1.0, d4));
      double d5 = var2.x + d0 * d4;
      double d6 = var2.y + d1 * d4;
      double d7 = var2.z + d2 * d4;
      double d8 = var1.x - d5;
      double d9 = var1.y - d6;
      double d10 = var1.z - d7;
      return d8 * d8 + d9 * d9 + d10 * d10;
   }

   public Vec3d ColorAnimator(Packet<?> var1) {
      if (var1 instanceof PlayerMoveC2SPacket playermovec2spacket && minecraftClient3.player != null) {
         double d0 = playermovec2spacket.getX(Double.NaN);
         double d1 = playermovec2spacket.getY(Double.NaN);
         double d2 = playermovec2spacket.getZ(Double.NaN);
         return !Double.isNaN(d0) && !Double.isNaN(d1) && !Double.isNaN(d2) ? new Vec3d(d0, d1, d2) : null;
      } else {
         return null;
      }
   }

   public void call028() {
      this.call012();
      this.call075();
      if (this.boolean34) {
         this.boolean34 = false;
         super.onDisable();
      }
   }

   public void call012() {
      this.queue.clear();
   }

   public void call075() {
      this.boolean49 = false;
      this.boolean33 = false;
      this.long77 = 0L;
      this.long78 = 0L;
      this.double10 = 0.0;
      this.vec3d6 = null;
      this.set6.clear();
   }


   public record QueuedPacket(Packet<?> packet4, Vec3d vec3d29) {
      public Packet<?> call076() {
         return this.packet4;
      }

      public Vec3d call047() {
         return this.vec3d29;
      }
   }
}
