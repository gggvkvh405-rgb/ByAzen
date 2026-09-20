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


import net.minecraft.network.listener.ClientPlayPacketListener;
import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.PacketEvent;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "Velocity", category = Category.MOVEMENT, description = "")
public final class Velocity extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Velocity velocity = new Velocity();
   public final NumberSetting delay5 = new NumberSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
   public final Queue<Velocity.Mode> queue7 = new ConcurrentLinkedQueue<>();
   public final CooldownTimer zClass06741 = new CooldownTimer();
   public boolean boolean166;
   public boolean boolean37;

   @Override
   public void onEnable() {
      if (Backtrack.reachV3.isEnabled()) {
         StyledTextBuilder.RefreshCacheEvent("Не работает с Backtrack");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.boolean37 = true;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (Backtrack.reachV3.isEnabled()) {
         StyledTextBuilder.RefreshCacheEvent("Не работает с Backtrack");
         this.toggle();
      } else if (var1.Arrows()) {
         try {
            Packet packet = var1.ItemScroller();
            if (!(packet instanceof DisconnectS2CPacket) && !(packet instanceof PlayerPositionLookS2CPacket)) {
               if (minecraftClient3.player != null && minecraftClient3.world != null) {
                  if (!this.boolean166) {
                     if (!this.EnchantItemSpec(packet) || minecraftClient3.player.isOnGround()) {
                        return;
                     }

                     this.boolean166 = true;
                     this.zClass06741.reset();
                  }

                  if (!(packet instanceof ChatMessageC2SPacket)
                     && !(packet instanceof HealthUpdateS2CPacket)
                     && !(packet instanceof PlaySoundS2CPacket)
                     && !(packet instanceof GameMessageS2CPacket)
                     && !(packet instanceof CommandExecutionC2SPacket)) {
                     var1.cancel();
                     this.queue7.add(new Velocity.Mode(packet));
                     return;
                  }

                  return;
               }

               return;
            }

            this.call049();
            return;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @EventTarget
   public void on23(EventHookPacketProcess var1) {
      if (minecraftClient3.world == null || minecraftClient3.player == null) {
         this.queue7.clear();
         this.boolean166 = false;
      }

      if (this.boolean166 && !this.queue7.isEmpty()) {
         boolean flag = minecraftClient3.player != null && minecraftClient3.player.isOnGround();
         boolean flag1 = this.zClass06741.EventModifyMouseRotationInput((long)this.delay5.getCurrent());
         if (flag || flag1) {
            this.call049();
         }
      }

      if (this.boolean37) {
         this.boolean37 = false;
         this.call049();
         super.onDisable();
      }
   }

   public void call049() {
      while (!this.queue7.isEmpty()) {
         Velocity.Mode iii111i1ililli1ii_l1i1illlili = this.queue7.poll();
         if (iii111i1ililli1ii_l1i1illlili != null) {
            try {
               iii111i1ililli1ii_l1i1illlili.ItemScroller().apply(minecraftClient3.getNetworkHandler());
            } catch (Throwable throwable) {
               if (!(throwable instanceof ClassCastException)) {
                  throwable.printStackTrace();
               }
            }
         }
      }

      this.boolean166 = false;
   }

   public boolean EnchantItemSpec(Packet<?> var1) {
      return minecraftClient3.player != null
         && var1 instanceof EntityVelocityUpdateS2CPacket entityvelocityupdates2cpacket
         && entityvelocityupdates2cpacket.getEntityId() == minecraftClient3.player.getId();
   }


   public static class Mode {
      public final Packet<?> packet2;
      public final long long90;

      public Mode(Packet<?> var1) {
         this.packet2 = var1;
         this.long90 = System.currentTimeMillis();
      }

      public Packet<ClientPlayPacketListener> ItemScroller() {
         return (Packet<ClientPlayPacketListener>)this.packet2;
      }

      public Packet<?> float273() {
         return this.packet2;
      }

      public long getTime() {
         return this.long90;
      }
   }

   public record PendingVelocity(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket, long long89) {
      public EntityVelocityUpdateS2CPacket call063() {
         return this.entityVelocityUpdateS2CPacket;
      }

      public long int392() {
         return this.long89;
      }
   }
}
