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
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zenith.ZenithClient;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.TimerSpeed;

@ModuleInfo(name = "Timer", description = "Ускоряет время в игре", category = Category.MOVEMENT)
public final class Timer extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Timer timer = new Timer();
   public final ModeSetting mode15 = new ModeSetting("module.timer.mode", "module.timer.mode.desc", "module.timer.mode.normal", "module.timer.mode.grim");
   public final NumberSetting speed8 = new NumberSetting("module.timer.speed", 2.0F, 0.1F, 10.0F, 0.1F, "module.timer.speed.desc", "x");
   public final KeySetting boostKey = new KeySetting("module.timer.boostKey", "module.timer.boostKey.desc", -1, () -> this.mode15.is(1));
   public final ModeSetting onFlag = new ModeSetting(
      "module.timer.onFlag", "module.timer.onFlag.desc", "module.timer.onFlag.reset", "module.timer.onFlag.disable", "module.timer.onFlag.none"
   );
   public float float189 = 1.0F;
   public float float190;
   public long long136;
   public long long137;

   @Override
   public void onEnable() {
      this.float189 = 1.0F;
      this.float190 = 0.0F;
      if (this.mode15.is(1)) {
         this.long136 = System.currentTimeMillis();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.BotWorldJoinEvent(1.0F);
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.mode15.is(0)) {
            this.BotWorldJoinEvent(this.speed8.getCurrent());
         } else if (this.mode15.is(1)) {
            long i = System.currentTimeMillis() - this.long137;
            if (!(this.float190 <= 0.0F) && this.EventModifyMouseRotationInput(this.boostKey.getKeyCode()) && i >= 2000L) {
               this.float190 = MathHelper.clamp(this.float190 - (0.0025F * this.speed8.getCurrent() - 0.0025F), 0.0F, 1.0F);
               this.BotWorldJoinEvent(Math.max(this.speed8.getCurrent(), 1.0F));
            } else {
               this.BotWorldJoinEvent(1.0F);
            }
         } else {
            this.BotWorldJoinEvent(1.0F);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && var1.Arrows()) {
         Packet packet = var1.ItemScroller();
         if (this.mode15.is(1) && packet instanceof CommonPingS2CPacket) {
            long i = System.currentTimeMillis() - this.long137;
            if (i > 2000L) {
               if (System.currentTimeMillis() - this.long136 > 25000L) {
                  this.long136 = System.currentTimeMillis();
                  this.float190 = 0.0F;
                  return;
               }

               if (!this.call409()) {
                  this.float190 = MathHelper.clamp(this.float190 + 0.005F, 0.0F, 1.0F);
               }

               var1.cancel();
            }
         }

         if (packet instanceof PlayerPositionLookS2CPacket) {
            this.long137 = System.currentTimeMillis();
            if (this.onFlag.is(0)) {
               this.BotWorldJoinEvent(1.0F);
               this.float190 = 0.0F;
            } else if (this.onFlag.is(1)) {
               this.float190 = 0.0F;
               this.RotationEasing("Отключён т.к. тебя флагнуло!");
            }
         }

         if (packet instanceof EntityVelocityUpdateS2CPacket entityvelocityupdates2cpacket
            && this.mode15.is(1)
            && entityvelocityupdates2cpacket.getEntityId() == minecraftClient3.player.getId()) {
            this.BotWorldJoinEvent(1.0F);
            this.float190 = 0.0F;
         }
      }
   }

   public float call008() {
      return this.isEnabled() ? this.float189 : 1.0F;
   }

   public float call048() {
      if (!this.isEnabled()) {
         return 0.0F;
      } else {
         return this.mode15.is(0) ? 1.0F : MathHelper.clamp(this.float190, 0.0F, 1.0F);
      }
   }

   public void BotWorldJoinEvent(float var1) {
      this.float189 = var1;
      TimerSpeed.BotWorldJoinEvent(var1);
   }

   public void RotationEasing(String var1) {
      ZenithClient.on23().ConfigJsonUtil().on23(this.getCategory().getIcon(), Text.of(var1));
      this.setToggled(false);
   }

   public boolean call409() {
      return minecraftClient3.player.input.getMovementInput().y != 0.0F || minecraftClient3.player.input.getMovementInput().x != 0.0F;
   }

   public boolean EventModifyMouseRotationInput(int var1) {
      return var1 != -1 && GLFW.glfwGetKey(minecraftClient3.getWindow().getHandle(), var1) == 1;
   }
}
