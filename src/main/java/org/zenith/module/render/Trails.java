package org.zenith.module.render;

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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.core.BotFollowEntity;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.managers.BotEntity;
import org.zenith.render.ParticleRenderer;
import org.zenith.render.ParticleTextures;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;

@ModuleInfo(name = "Trails", category = Category.RENDER, description = "Светящийся след за игроком при движении")
public final class Trails extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Trails trails = new Trails();
   public static final Identifier identifier8 = Identifier.of("zenith", "particles/firefly.png");
   public static final double double101 = 9.0E-6;
   public static final double double102 = 0.02;
   public static final double double103 = 64.0;
   public static final int int314 = 3;
   public static final int int315 = 2;
   public static final int int316 = 2;
   public static final float float202 = 0.02F;
   public final ModeSetting mode16 = new ModeSetting("module.trails.mode", "module.trails.mode.desc", "module.trails.particles", "module.trails.trails");
   public final ModeSetting texture2 = new ModeSetting("module.trails.texture", "module.trails.particleTexture.desc", () -> this.mode16.is(0), getZClass019());
   public final BooleanSetting hideFirstPerson = new BooleanSetting("module.trails.hideFirstPerson", "module.trails.hideFirstPerson.desc", false);
   public final NumberSetting length3 = new NumberSetting(
      "module.trails.length", 150.0F, 50.0F, 300.0F, 10.0F, "module.trails.trailLength.desc", "x", () -> this.mode16.is(1), null
   );
   public final NumberSetting pointSize = new NumberSetting(
      "module.trails.pointSize", 0.5F, 0.1F, 2.0F, 0.1F, "module.trails.pointSize.desc", "x", () -> this.mode16.is(1), null
   );
   public final NumberSetting opacity = new NumberSetting(
      "module.trails.opacity", 100.0F, 10.0F, 100.0F, 5.0F, "module.trails.opacity.desc", "%", () -> this.mode16.is(1), null
   );
   public final NumberSetting particleCount = new NumberSetting(
      "module.trails.particleCount", 5.0F, 1.0F, 10.0F, 1.0F, "module.trails.particleCount.desc", "x", () -> this.mode16.is(0), null
   );
   public final NumberSetting particleSize = new NumberSetting(
      "module.trails.particleSize", 0.2F, 0.1F, 3.0F, 0.1F, "module.trails.particleSize.desc", "x", () -> this.mode16.is(0), null
   );
   public final NumberSetting particleSpeed = new NumberSetting(
      "module.trails.particleSpeed", 1.3F, 0.1F, 3.0F, 0.1F, "module.trails.particleSpeed.desc", "x", () -> this.mode16.is(0), null
   );
   public final NumberSetting particleLifetime = new NumberSetting(
      "module.trails.particleLifetime", 40.0F, 10.0F, 100.0F, 5.0F, "module.trails.particleLifetime.desc", "t", () -> this.mode16.is(0), null
   );
   public final ModeSetting color5 = new ModeSetting(
      "module.trails.color", "module.trails.colorMode.desc", "module.particles.sync", "module.particles.custom"
   );
   public final ColorSetting customColor6 = new ColorSetting(
      "module.trails.customColor", "module.trails.customColor.desc", ArgbColor.var11934, () -> this.color5.is(1)
   );
   public final Deque<Vec3d> deque = new ArrayDeque<>();
   public Vec3d vec3d35 = null;
   public int int317 = 0;
   public final List<BotFollowEntity> list83 = new ArrayList<>();
   public int int318 = 0;

   public static String[] getZClass019() {
      return ParticleTextures.getZClass019();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.deque.clear();
      this.vec3d35 = null;
      this.int317 = 0;
      this.list83.clear();
      this.int318 = 0;
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.mode16.is(0)) {
            Vec3d vec3d = minecraftClient3.player.getBoundingBox().getCenter();
            boolean flag = Math.abs(minecraftClient3.player.getVelocity().x) > 0.05
               || Math.abs(minecraftClient3.player.getVelocity().z) > 0.05
               || Math.abs(minecraftClient3.player.getVelocity().y) > 0.15;
            if (flag) {
               this.int318++;
               if (this.int318 >= 2) {
                  this.int318 = 0;
                  this.FileLogger(vec3d);
               }
            }

            this.list83.removeIf(BotEntity::float304);

            for (BotFollowEntity ii1li1ilii1l1ll1 : this.list83) {
               this.on23(ii1li1ilii1l1ll1, vec3d);
            }
         } else {
            int k = (int)this.length3.getCurrent();
            Vec3d vec3d2 = minecraftClient3.player.getEntityPos().add(0.0, minecraftClient3.player.getHeight() / 2.0, 0.0);
            if (this.vec3d35 == null) {
               this.deque.addLast(vec3d2);
               this.vec3d35 = vec3d2;
               this.int317 = 0;
            } else {
               double d1 = this.vec3d35.squaredDistanceTo(vec3d2);
               if (d1 > 9.0E-6) {
                  int i = Math.max(1, (int)(d1 / 0.02));
                  if (i > 1000) {
                     i = 0;
                  }

                  for (int j = 1; j <= i; j++) {
                     double d0 = (double)j / i;
                     Vec3d vec3d1 = this.vec3d35.lerp(vec3d2, d0);
                     this.deque.addLast(vec3d1);
                  }

                  this.vec3d35 = vec3d2;
                  this.int317 = 0;

                  while (this.deque.size() > k) {
                     this.deque.pollFirst();
                  }
               } else {
                  this.int317++;
                  if (this.int317 > 2) {
                     for (int l = 0; l < 3 && !this.deque.isEmpty(); l++) {
                        this.deque.pollFirst();
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && (!this.hideFirstPerson.isEnabled() || !minecraftClient3.options.getPerspective().isFirstPerson())) {
         if (this.mode16.is(0)) {
            if (!this.list83.isEmpty()) {
               ParticleRenderer.on23(minecraftClient3.gameRenderer.getCamera());
               ParticleRenderer.on23(var1.ClanUpgrade(), var1.CropFarmer(), this.list83);
            }
         } else if (this.deque.size() >= 2) {
            this.Easing(var1.ClanUpgrade());
         }
      }
   }

   public void Easing(MatrixStack var1) {
      Camera camera = minecraftClient3.gameRenderer.getCamera();
      Vec3d vec3d = camera.getCameraPos();
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.blendFunc(770, 1);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      org.zenith.render.LegacyRenderBridge.setTexture(0, identifier8);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float f = this.pointSize.getCurrent();
      int i = this.deque.size();
      float f1 = camera.getPitch();
      float f2 = camera.getYaw();
      int j = 0;

      for (Vec3d vec3d1 : this.deque) {
         float f3 = (float)j / i;
         float f4 = this.opacity.getCurrent() / 100.0F * 0.85F;
         float f5 = f3 * f3 * f4;
         if (f5 < 0.02F) {
            j++;
         } else {
            float f6 = f * (0.2F + f3 * 0.8F);
            Vec3d vec3d2 = vec3d1.subtract(vec3d);
            ArgbColor i11ii1llliilllii1i1 = this.EventClick(j);
            this.on23(bufferbuilder, f1, f2, vec3d2, f6 * 1.8F, f5 * 0.4F, i11ii1llliilllii1i1);
            this.on23(bufferbuilder, f1, f2, vec3d2, f6, f5, i11ii1llliilllii1i1);
            j++;
         }
      }

      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
   }

   public void on23(BufferBuilder var1, float var2, float var3, Vec3d var4, float var5, float var6, ArgbColor var7) {
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var2));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var3 + 180.0F));
      matrixstack.translate(var4.x, var4.y, var4.z);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var3));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var2));
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      int i = (int)(var6 * 255.0F) << 24 | var7.float240() << 16 | var7.var14323() << 8 | var7.var14324();
      float f = var5 / 2.0F;
      var1.vertex(matrix4f, f, -f, 0.0F).texture(0.0F, 1.0F).color(i);
      var1.vertex(matrix4f, -f, -f, 0.0F).texture(1.0F, 1.0F).color(i);
      var1.vertex(matrix4f, -f, f, 0.0F).texture(1.0F, 0.0F).color(i);
      var1.vertex(matrix4f, f, f, 0.0F).texture(0.0F, 0.0F).color(i);
   }

   public ArgbColor EventClick(int var1) {
      return this.color5.is(0) ? ZenithClient.on23().TextScanner().getClientColor(var1 * 2) : this.customColor6.getColor();
   }

   public void FileLogger(Vec3d var1) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      int i = (int)this.particleCount.getCurrent();
      float f = this.particleSpeed.getCurrent();
      Vec3d vec3d = minecraftClient3.player.getVelocity();
      Vec3d vec3d1 = vec3d.normalize().multiply(-1.0);

      for (int j = 0; j < i; j++) {
         double d0 = threadlocalrandom.nextDouble(-0.3, 0.3);
         double d1 = threadlocalrandom.nextDouble(-minecraftClient3.player.getHeight() / 2.1F, minecraftClient3.player.getHeight() / 2.0F);
         double d2 = threadlocalrandom.nextDouble(-0.3, 0.3);
         Vec3d vec3d2 = var1.add(vec3d1.x * 0.3 + d0, d1, vec3d1.z * 0.3 + d2);
         double d3 = threadlocalrandom.nextDouble(0.0, Math.PI * 2);
         double d4 = threadlocalrandom.nextDouble(-Math.PI / 4, Math.PI / 4);
         double d5 = Math.cos(d4);
         double d6 = Math.sin(d4);
         Vec3d vec3d3 = new Vec3d(
            Math.cos(d3) * d5 * threadlocalrandom.nextDouble(0.02, 0.06) * f,
            d6 * threadlocalrandom.nextDouble(0.02, 0.06) * f,
            Math.sin(d3) * d5 * threadlocalrandom.nextDouble(0.02, 0.06) * f
         );
         int k = (int)this.particleLifetime.getCurrent();
         int l = threadlocalrandom.nextInt(k / 2, k);
         float f1 = this.particleSize.getCurrent() * threadlocalrandom.nextFloat(0.7F, 1.3F);
         ArgbColor i11ii1llliilllii1i1 = this.EventClick(j * 10);
         float f2 = threadlocalrandom.nextFloat(0.0F, 360.0F);
         float f3 = threadlocalrandom.nextFloat(-2.0F, 2.0F);
         String s = this.texture2.get().toLowerCase();
         this.list83.add(new BotFollowEntity(vec3d2, vec3d3, l, f1, i11ii1llliilllii1i1, s, f2, f3));
      }
   }

   public void on23(BotFollowEntity var1, Vec3d var2) {
      var1.SprintStateEvent(var1.boolean87());
      double d0 = var1.WallBypass().distanceTo(var2);
      int i = d0 > 64.0 ? 8 : 1;
      var1.ItemUseEvent(var1.boolean87() - i);
      if (var1.boolean87() > 0) {
         var1.BotRespawnEvent(var1.WallBypass());
         float f = 1.0F - (float)var1.boolean87() / var1.boolean149();
         Vec3d vec3d = var1.boolean86();
         if (f > 0.6F) {
            float f1 = (f - 0.6F) / 0.4F;
            double d1 = 0.003 * f1;
            vec3d = new Vec3d(vec3d.x * 0.98, vec3d.y - d1, vec3d.z * 0.98);
         } else {
            vec3d = vec3d.multiply(0.98);
         }

         var1.BotTickEvent(vec3d);
         var1.UiAnimation(var1.WallBypass().add(vec3d));
         var1.EventHookTickEvent(var1.getRotation() + var1.var1112());
         if (var1.WallBypass().y <= minecraftClient3.world.getBottomY()) {
            var1.ItemUseEvent(0);
         }
      }
   }
}
