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
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.zenith.ZenithClient;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.event.PacketSendEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ColorUtils;
import org.zenith.util.MathUtils;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "TrapTp", description = "Задерживает пакеты после использования трапки", category = Category.COMBAT)
public final class TrapTp extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TrapTp trapTp = new TrapTp();
   public static final long long138 = 2000L;
   public static final double double104 = 2.0;
   public static final double double105 = 5.0;
   public final ModeSetting mode17 = new ModeSetting("Mode", "", "module.serverHelper.normalTrap", "module.serverHelper.explosiveTrap");
   public final Queue<Packet<?>> queue6 = new ConcurrentLinkedQueue<>();
   public Vec3d vec3d36;
   public long long139;
   public boolean boolean49;

   @Override
   public void onEnable() {
      this.queue6.clear();
      this.boolean49 = false;
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.getNetworkHandler() != null) {
         this.vec3d36 = minecraftClient3.player.getEntityPos();
         this.long139 = System.currentTimeMillis();
         super.onEnable();
         ScreenUtils.ItemServiceBase(this.int340());
      } else {
         super.onEnable();
         this.disableSelf();
      }
   }

   @Override
   public void onDisable() {
      this.boolean174();
      this.vec3d36 = null;
      this.long139 = 0L;
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && !minecraftClient3.player.isDead()) {
         if (this.vec3d36 == null || this.int342() || System.currentTimeMillis() - this.long139 >= 2000L) {
            this.disableSelf();
         }
      } else {
         this.queue6.clear();
         this.disableSelf();
      }
   }

   @EventTarget
   public void Easing(EventHookWorldRender var1) {
      if (this.vec3d36 != null && minecraftClient3.player != null) {
         int i = ZenithClient.on23().TextScanner().getClientColor(90).call001();
         this.on23(var1.ClanUpgrade(), this.int341(), i);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() != null && (var1.ItemScroller() instanceof DisconnectS2CPacket || var1.ItemScroller() instanceof PlayerPositionLookS2CPacket)) {
         this.queue6.clear();
         this.disableSelf();
      }
   }

   @EventTarget(0)
   public void on23(PacketSendEvent var1) {
      if (!var1.isCancelled() && !this.boolean49 && !Blink.blink.call045() && var1.ItemScroller() != null) {
         if (minecraftClient3.player != null && minecraftClient3.world != null) {
            var1.cancel();
            this.queue6.add(var1.ItemScroller());
         } else {
            this.queue6.clear();
         }
      }
   }

   public boolean call045() {
      return this.boolean49;
   }

   public Item int340() {
      return this.mode17.is(1) ? Items.PRISMARINE_SHARD : Items.POPPED_CHORUS_FRUIT;
   }

   public double int341() {
      return this.mode17.is(1) ? 7.0 : 2.0;
   }

   public boolean int342() {
      if (minecraftClient3.player != null && this.vec3d36 != null) {
         double d0 = minecraftClient3.player.getX() - this.vec3d36.x;
         double d1 = minecraftClient3.player.getZ() - this.vec3d36.z;
         double d2 = this.int341() + 1.0;
         return d0 * d0 + d1 * d1 > d2 * d2;
      } else {
         return true;
      }
   }

   public void boolean174() {
      if (!this.queue6.isEmpty() && minecraftClient3.getNetworkHandler() != null) {
         this.boolean49 = true;

         Packet packet;
         try {
            while ((packet = this.queue6.poll()) != null) {
               minecraftClient3.getNetworkHandler().sendPacket(packet);
            }
         } finally {
            this.boolean49 = false;
            this.queue6.clear();
         }
      } else {
         this.queue6.clear();
         this.boolean49 = false;
      }
   }

   public void on23(MatrixStack var1, double var2, int var4) {
      Vec3d vec3d = this.vec3d36.add(0.0, 0.02, 0.0);
      Vec3d vec3d1 = vec3d.subtract(minecraftClient3.getEntityRenderDispatcher().camera.getCameraPos());
      GL11.glEnable(2881);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
      org.zenith.render.LegacyRenderBridge.usePositionColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      int i = 0;

      for (byte b0 = 90; i <= b0; i++) {
         Vec3d vec3d2 = MathUtils.on23(i, b0, var2);
         Vec3d vec3d3 = MathUtils.on23(i + 1, b0, var2);
         WorldRender.on23(
            var1,
            bufferbuilder,
            vec3d1.add(vec3d2),
            vec3d1.add(vec3d2.x, vec3d2.y + 2.0, vec3d2.z),
            ColorUtils.ColorAnimator(var4, 0.2F),
            ColorUtils.ColorAnimator(var4, 0.0F)
         );
         WorldRender.on23(vec3d.add(vec3d2), vec3d.add(vec3d3), var4, 2.0F, true);
      }

      i = 0;

      for (byte b1 = 90; i <= b1; i++) {
         Vec3d vec3d4 = MathUtils.on23(i, b1, var2);
         WorldRender.on23(
            var1,
            bufferbuilder,
            vec3d1.add(vec3d4),
            vec3d1.add(vec3d4.x, vec3d4.y - 2.0, vec3d4.z),
            ColorUtils.ColorAnimator(var4, 0.2F),
            ColorUtils.ColorAnimator(var4, 0.0F)
         );
      }

      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.disableBlend();
      GL11.glDisable(2881);
   }
}
