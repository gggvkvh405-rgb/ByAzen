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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.ZenithClient;
import org.zenith.event.AttackEntityEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;

@ModuleInfo(name = "KillEffect", category = Category.RENDER, description = "module.killEffect.desc")
public class KillEffect extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final KillEffect killEffect = new KillEffect();
   public static final long long101 = 1500L;
   public static final float float80 = 6.5F;
   public static final float float81 = 0.4F;
   public static final float float82 = 0.6F;
   public static final float float83 = 0.015625F;
   public static final float float84 = 1.5F;
   public static final float float85 = 1.0F;
   public final ModeSetting mode10 = new ModeSetting(
      "module.killEffect.mode",
      "module.killEffect.mode.desc",
      "module.killEffect.mode.cubes",
      "module.killEffect.mode.pyramids",
      "module.killEffect.mode.body",
      "module.killEffect.mode.blackHole",
      "module.killEffect.mode.fountain"
   );
   public final NumberSetting duration = new NumberSetting(
      "module.killEffect.duration", 1500.0F, 400.0F, 4000.0F, 100.0F, "module.killEffect.duration.desc", "ms"
   );
   public final NumberSetting scale2 = new NumberSetting("module.killEffect.scale", 1.0F, 0.3F, 3.0F, 0.1F, "module.killEffect.scale.desc", "x");
   public final NumberSetting count2 = new NumberSetting(
      "module.killEffect.count", 20.0F, 4.0F, 80.0F, 1.0F, "module.killEffect.count.desc", "x", () -> !this.mode10.is(2), null
   );
   public final NumberSetting speed4 = new NumberSetting("module.killEffect.speed", 1.0F, 0.2F, 3.0F, 0.1F, "module.killEffect.speed.desc", "x");
   public final ModeSetting particleType = new ModeSetting(
      "module.killEffect.particleType",
      "module.killEffect.particleType.desc",
      () -> this.mode10.is(3) || this.mode10.is(4),
      "module.killEffect.particleType.cubes",
      "module.killEffect.particleType.pyramids",
      "module.killEffect.particleType.stars",
      "module.killEffect.particleType.totems",
      "module.killEffect.particleType.random"
   );
   public final ModeSetting color2 = new ModeSetting(
      "module.killEffect.color", "module.killEffect.color.desc", "module.particles.sync", "module.particles.custom"
   );
   public final ColorSetting customColor3 = new ColorSetting(
      "module.killEffect.customColor", "module.killEffect.customColor.desc", ArgbColor.var11934, () -> this.color2.is(1)
   );
   public final List<KillEffect.DefaultService> list46 = new ArrayList<>();
   public final Map<Integer, Long> map20 = new HashMap<>();
   public final Random random = new Random();
   public final Matrix3f matrix3f = new Matrix3f();
   public final Vector3f vector3f = new Vector3f();
   public final Vector3f vector3f2 = new Vector3f();

   @Override
   public void onEnable() {
      super.onEnable();
      this.list46.clear();
      this.map20.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.list46.clear();
      this.map20.clear();
   }

   @EventTarget
   public void ColorAnimator(AttackEntityEvent var1) {
      if (var1.ElytraTarget() == AttackEntityEvent.on23.call077) {
         Entity entity = var1.ElytraMotion();
         if (this.ProfileItemBuilder(entity)) {
            this.map20.put(entity.getId(), System.currentTimeMillis());
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows()
         && minecraftClient3.world != null
         && var1.ItemScroller() instanceof EntityStatusS2CPacket entitystatuss2cpacket
         && entitystatuss2cpacket.getStatus() == 3) {
         Entity entity = entitystatuss2cpacket.getEntity(minecraftClient3.world);
         if (entity != null && this.ProfileItemBuilder(entity)) {
            long i = System.currentTimeMillis();
            Long olong = this.map20.remove(entity.getId());
            if (olong != null && i - olong <= 1500L) {
               this.NbtEditor(entity);
            }
         }
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      this.call416();
      if (minecraftClient3.world != null && !this.list46.isEmpty()) {
         long i = System.currentTimeMillis();
         float f = this.duration.getCurrent();
         MatrixStack matrixstack = var1.ClanUpgrade();
         Vec3d vec3d = minecraftClient3.getEntityRenderDispatcher().camera.getCameraPos();
         Iterator<KillEffect.DefaultService> iterator = this.list46.iterator();

         while (iterator.hasNext()) {
            KillEffect.DefaultService liii1111liiii1lll1ll11il1_l1i1illlili = iterator.next();
            float f1 = f * liii1111liiii1lll1ll11il1_l1i1illlili.val422;
            float f2 = (float)(i - liii1111liiii1lll1ll11il1_l1i1illlili.val313) / f1;
            if (f2 >= 1.0F) {
               iterator.remove();
            } else {
               float f3 = (float)(i - liii1111liiii1lll1ll11il1_l1i1illlili.val313) / 1000.0F;
               this.on23(liii1111liiii1lll1ll11il1_l1i1illlili, f2, f3, 1.5F, matrixstack, vec3d);
            }
         }
      }
   }

   public void NbtEditor(Entity var1) {
      float f = this.scale2.getCurrent();
      if (this.mode10.is(2)) {
         this.on23(var1, f);
      } else if (this.mode10.is(3)) {
         this.Easing(var1, f);
      } else if (this.mode10.is(4)) {
         this.UiAnimation(var1, f);
      } else {
         int i = Math.max(1, Math.round(this.count2.getCurrent()));
         float f1 = this.speed4.getCurrent();
         Vec3d vec3d = var1.getEntityPos().add(0.0, var1.getHeight() * 0.5, 0.0);
         KillEffect.CoreService[] aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil = new KillEffect.CoreService[i];

         for (int j = 0; j < i; j++) {
            float f2 = this.random.nextFloat() * (float) (Math.PI * 2);
            float f3 = this.random.nextFloat();
            float f4 = (2.2F + this.random.nextFloat() * 2.4F) * f * (0.55F + (1.0F - f3) * 0.6F) * f1;
            float f5 = (1.4F + f3 * 3.2F) * f * f1;
            Vec3d vec3d1 = new Vec3d(Math.cos(f2) * f4, f5, Math.sin(f2) * f4);
            float f6 = (0.18F + this.random.nextFloat() * 0.18F) * f;
            Vec3d vec3d2 = new Vec3d(
               this.random.nextFloat() * (float) (Math.PI * 2),
               this.random.nextFloat() * (float) (Math.PI * 2),
               this.random.nextFloat() * (float) (Math.PI * 2)
            );
            Vec3d vec3d3 = new Vec3d(
               (this.random.nextFloat() - 0.5F) * 12.0F, (this.random.nextFloat() - 0.5F) * 12.0F, (this.random.nextFloat() - 0.5F) * 12.0F
            );
            aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil[j] = new KillEffect.CoreService(vec3d, vec3d1, vec3d2, vec3d3, f6, f6, f6, f6, null);
         }

         this.list46
            .add(
               new KillEffect.DefaultService(
                  System.currentTimeMillis(), this.random.nextInt(180), this.mode10.getIndex(), aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil, null
               )
            );
      }
   }

   public void on23(Entity var1, float var2) {
      float f = Math.max(0.5F, var1.getHeight());
      Vec3d vec3d = var1.getEntityPos();
      Identifier identifier = this.PotionItemBuilder(var1);
      KillEffect.Particle[][] aliii1111liiii1lll1ll11il1_illi1l1l1 = null;
      if (identifier != null) {
         aliii1111liiii1lll1ll11il1_illi1l1l1 = new KillEffect.Particle[][]{
            Easing(0.0F, 0.0F, 8.0F, 8.0F, 8.0F),
            Easing(16.0F, 16.0F, 8.0F, 12.0F, 4.0F),
            Easing(40.0F, 16.0F, 4.0F, 12.0F, 4.0F),
            Easing(32.0F, 48.0F, 4.0F, 12.0F, 4.0F),
            Easing(0.0F, 16.0F, 4.0F, 12.0F, 4.0F),
            Easing(16.0F, 48.0F, 4.0F, 12.0F, 4.0F)
         };
      }

      float[][] afloat = new float[][]{
         {0.0F, 0.88F, 0.0F, 0.25F, 0.25F, 0.25F},
         {0.0F, 0.6F, 0.0F, 0.3F, 0.4F, 0.18F},
         {-0.21F, 0.6F, 0.0F, 0.12F, 0.4F, 0.12F},
         {0.21F, 0.6F, 0.0F, 0.12F, 0.4F, 0.12F},
         {-0.08F, 0.2F, 0.0F, 0.14F, 0.4F, 0.14F},
         {0.08F, 0.2F, 0.0F, 0.14F, 0.4F, 0.14F}
      };
      KillEffect.CoreService[] aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil = new KillEffect.CoreService[afloat.length];

      for (int i = 0; i < afloat.length; i++) {
         float[] afloat1 = afloat[i];
         Vec3d vec3d1 = vec3d.add(afloat1[0] * f, afloat1[1] * f, afloat1[2] * f);
         double d0 = afloat1[0];
         double d1 = afloat1[2];
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         double d3;
         double d4;
         if (d2 < 1.0E-4) {
            double d5 = this.random.nextFloat() * (float) (Math.PI * 2);
            d3 = Math.cos(d5);
            d4 = Math.sin(d5);
         } else {
            d3 = d0 / d2;
            d4 = d1 / d2;
         }

         float f4 = this.speed4.getCurrent();
         float f1 = (1.8F + this.random.nextFloat() * 1.6F) * var2 * f4;
         float f2 = (2.2F + this.random.nextFloat() * 2.0F) * var2 * f4;
         Vec3d vec3d2 = new Vec3d(d3 * f1 + (this.random.nextFloat() - 0.5F) * var2 * f4, f2, d4 * f1 + (this.random.nextFloat() - 0.5F) * var2 * f4);
         Vec3d vec3d3 = new Vec3d(
            this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2)
         );
         Vec3d vec3d4 = new Vec3d(
            (this.random.nextFloat() - 0.5F) * 10.0F, (this.random.nextFloat() - 0.5F) * 10.0F, (this.random.nextFloat() - 0.5F) * 10.0F
         );
         float f3 = 0.2F * f * var2;
         KillEffect.Particle[] aliii1111liiii1lll1ll11il1_illi1l1l11 = aliii1111liiii1lll1ll11il1_illi1l1l1 == null
            ? null
            : aliii1111liiii1lll1ll11il1_illi1l1l1[i];
         aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil[i] = new KillEffect.CoreService(
            vec3d1, vec3d2, vec3d3, vec3d4, afloat1[3] * f * var2, afloat1[4] * f * var2, afloat1[5] * f * var2, f3, aliii1111liiii1lll1ll11il1_illi1l1l11
         );
      }

      this.list46
         .add(new KillEffect.DefaultService(System.currentTimeMillis(), this.random.nextInt(180), 2, aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil, identifier));
   }

   public void UiAnimation(Entity var1, float var2) {
      Vec3d vec3d = var1.getEntityPos();
      int i = Math.max(1, Math.round(this.count2.getCurrent()));
      float f = this.speed4.getCurrent();
      float f1 = 2.0F * this.duration.getCurrent() / 1000.0F;
      KillEffect.CoreService[] aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil = new KillEffect.CoreService[i];

      for (int j = 0; j < i; j++) {
         int k = this.call269();
         float f2 = this.random.nextFloat() * (float) (Math.PI * 2);
         float f3 = (float)Math.sqrt(this.random.nextFloat()) * 0.83F * var2;
         Vec3d vec3d1 = vec3d.add(Math.cos(f2) * f3, 0.0, Math.sin(f2) * f3);
         float f4 = (1.6F + this.random.nextFloat() * 1.6F) * var2 * f;
         Vec3d vec3d2 = new Vec3d(0.0, f4, 0.0);
         float f5 = (0.22F + this.random.nextFloat() * 0.22F) * var2;
         Vec3d vec3d3 = new Vec3d(
            this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2)
         );
         Vec3d vec3d4 = new Vec3d(
            (this.random.nextFloat() - 0.5F) * 7.0F, (this.random.nextFloat() - 0.5F) * 7.0F, (this.random.nextFloat() - 0.5F) * 7.0F
         );
         float f6 = this.random.nextFloat() * 0.18F * f1;
         aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil[j] = new KillEffect.CoreService(vec3d1, vec3d2, vec3d3, vec3d4, f5, f5, f5, f6, null, k);
      }

      this.list46
         .add(new KillEffect.DefaultService(System.currentTimeMillis(), this.random.nextInt(180), 4, aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil, null, 2.0F));
   }

   public void Easing(Entity var1, float var2) {
      Vec3d vec3d = var1.getEntityPos().add(0.0, var1.getHeight() * 0.5, 0.0);
      int i = Math.max(1, Math.round(this.count2.getCurrent()));
      KillEffect.CoreService[] aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil = new KillEffect.CoreService[i];

      for (int j = 0; j < i; j++) {
         int k = this.call269();
         float f = this.random.nextFloat() * (float) (Math.PI * 2);
         float f1 = (this.random.nextFloat() - 0.5F) * 0.85F;
         float f2 = (1.8F + this.random.nextFloat() * 1.4F) * var2;
         double d0 = Math.cos(f1);
         double d1 = Math.cos(f) * d0 * f2;
         double d2 = Math.sin(f1) * f2 * 0.45;
         double d3 = Math.sin(f) * d0 * f2;
         Vec3d vec3d1 = new Vec3d(d1, d2, d3);
         float f3 = (0.16F + this.random.nextFloat() * 0.18F) * var2;
         Vec3d vec3d2 = new Vec3d(
            this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2), this.random.nextFloat() * (float) (Math.PI * 2)
         );
         Vec3d vec3d3 = new Vec3d(
            (this.random.nextFloat() - 0.5F) * 18.0F, (this.random.nextFloat() - 0.5F) * 18.0F, (this.random.nextFloat() - 0.5F) * 18.0F
         );
         aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil[j] = new KillEffect.CoreService(vec3d, vec3d1, vec3d2, vec3d3, f3, f3, f3, f3, null, k);
      }

      this.list46.add(new KillEffect.DefaultService(System.currentTimeMillis(), this.random.nextInt(180), 3, aliii1111liiii1lll1ll11il1_ii1il11l111ii11iil, null));
   }

   public int call269() {
      int i = this.particleType.getIndex();
      return i >= 4 ? this.random.nextInt(4) : i;
   }

   public Identifier PotionItemBuilder(Entity var1) {
      if (var1 instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
         return abstractclientplayerentity.getSkin().body().texturePath();
      } else {
         try {
            if (minecraftClient3.getEntityRenderDispatcher().getRenderer(var1) instanceof LivingEntityRenderer livingentityrenderer) {
               if (livingentityrenderer.createRenderState() instanceof LivingEntityRenderState livingentityrenderstate) {
                  livingentityrenderer.updateRenderState((LivingEntity)var1, livingentityrenderstate, 1.0F);
                  return livingentityrenderer.getTexture(livingentityrenderstate);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } catch (Throwable throwable) {
            return null;
         }
      }
   }

   public static KillEffect.Particle[] Easing(float var0, float var1, float var2, float var3, float var4) {
      float f = 0.015625F;
      return new KillEffect.Particle[]{
         new KillEffect.Particle((var0 + var4 + var2) * f, var1 * f, (var0 + var4 + var2 + var2) * f, (var1 + var4) * f),
         new KillEffect.Particle((var0 + var4) * f, var1 * f, (var0 + var4 + var2) * f, (var1 + var4) * f),
         new KillEffect.Particle((var0 + var4) * f, (var1 + var4) * f, (var0 + var4 + var2) * f, (var1 + var4 + var3) * f),
         new KillEffect.Particle((var0 + var4 + var2 + var4) * f, (var1 + var4) * f, (var0 + var4 + var2 + var4 + var2) * f, (var1 + var4 + var3) * f),
         new KillEffect.Particle(var0 * f, (var1 + var4) * f, (var0 + var4) * f, (var1 + var4 + var3) * f),
         new KillEffect.Particle((var0 + var4 + var2) * f, (var1 + var4) * f, (var0 + var4 + var2 + var4) * f, (var1 + var4 + var3) * f)
      };
   }

   public void on23(KillEffect.DefaultService var1, float var2, float var3, float var4, MatrixStack var5, Vec3d var6) {
      ArgbColor i11ii1llliilllii1i1 = this.EventClick(var1.val140);
      if (var1.val141 == 3) {
         this.on23(var1, var2, var3, var4, i11ii1llliilllii1i1);
      } else if (var1.val141 == 4) {
         this.UiAnimation(var1, var2, var3, var4, i11ii1llliilllii1i1);
      } else {
         float f = 1.0F - var2;
         float f1 = f * f;
         if (!(f1 <= 0.01F)) {
            boolean flag = var1.val141 == 2;
            boolean flag1 = var1.val141 == 1;
            int i = ColorUtils.ColorAnimator(i11ii1llliilllii1i1.call001(), f1);
            float f2 = MathHelper.lerp(var2, 1.0F, flag ? 0.7F : 0.35F);
            float f3 = 0.0F;
            if (flag) {
               float f4 = MathHelper.clamp((var2 - 0.4F) / 0.20000002F, 0.0F, 1.0F);
               f3 = f4 * f4 * (3.0F - 2.0F * f4);
            }

            for (KillEffect.CoreService liii1111liiii1lll1ll11il1_ii1il11l111ii11iil : var1.val142) {
               double d0 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.x
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.x * var3;
               double d1 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.y
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.y * var3
                  - 3.25F * var3 * var3;
               double d2 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.z
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.z * var3;
               double d3 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.x
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.x * var3;
               double d4 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.y
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.y * var3;
               double d5 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.z
                  + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.z * var3;
               if (flag) {
                  float f5 = MathHelper.lerp(
                     f3, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val143
                  );
                  float f6 = MathHelper.lerp(
                     f3, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val194, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val143
                  );
                  float f7 = MathHelper.lerp(
                     f3, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val195, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val143
                  );
                  float f8 = f5 * f2 * 0.5F;
                  float f9 = f6 * f2 * 0.5F;
                  float f10 = f7 * f2 * 0.5F;
                  boolean flag2 = var1.val314 != null && liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val315 != null;
                  if (flag2 && f3 < 0.999F) {
                     float f11 = (1.0F - f3) * f1;
                     this.on23(var5, var1.val314, var6, d0, d1, d2, f8, f9, f10, d3, d4, d5, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val315, f11);
                  }

                  float f16;
                  float f12;
                  if (flag2) {
                     f16 = f1 * f3 * 0.9F;
                     f12 = f1 * f3;
                  } else {
                     f16 = f1 * (0.55F + 0.35F * f3);
                     f12 = f1;
                  }

                  if (f16 > 0.01F || f12 > 0.01F) {
                     int j = ColorUtils.ColorAnimator(i11ii1llliilllii1i1.call001(), f16);
                     int k = ColorUtils.ColorAnimator(i11ii1llliilllii1i1.EventTick(0.4F).call001(), f12);
                     this.on23(d0, d1, d2, f8, f9, f10, d3, d4, d5, j, k, var4);
                  }
               } else {
                  float f13 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068 * f2 * 0.5F;
                  float f14 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val194 * f2 * 0.5F;
                  float f15 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val195 * f2 * 0.5F;
                  if (flag1) {
                     this.UiAnimation(d0, d1, d2, f13, f14, f15, d3, d4, d5, i, var4);
                  } else {
                     this.on23(d0, d1, d2, f13, f14, f15, d3, d4, d5, i, var4);
                  }
               }
            }
         }
      }
   }

   public void on23(KillEffect.DefaultService var1, float var2, float var3, float var4, ArgbColor var5) {
      double d0 = 1.0 - var2;
      d0 *= d0;
      double d1 = var2 * (float) (Math.PI * 2) * 2.5 * this.speed4.getCurrent();
      double d2 = Math.sin(d1);
      double d3 = Math.cos(d1);
      float f = var2 > 0.82F ? (1.0F - var2) / 0.18F : 1.0F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      if (!(f < 0.01F)) {
         int i = ColorUtils.ColorAnimator(var5.call001(), f);
         int j = ColorUtils.ColorAnimator(var5.EventTick(0.5F).call001(), f);
         Vec3d vec3d = var1.val142[0].val048;
         Particles ii1l11il1iililiil = Particles.particles;
         float[] afloat = ii1l11il1iililiil.getThis2();
         float[] afloat1 = ii1l11il1iililiil.getThis();

         for (KillEffect.CoreService liii1111liiii1lll1ll11il1_ii1il11l111ii11iil : var1.val142) {
            double d4 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.x * d3
               - liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.z * d2;
            double d5 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.x * d2
               + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.z * d3;
            double d6 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.y;
            double d7 = vec3d.x + d4 * d0;
            double d9 = vec3d.y + d6 * d0;
            double d11 = vec3d.z + d5 * d0;
            double d13 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.x + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.x * var3;
            double d14 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.y + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.y * var3;
            double d15 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.z + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.z * var3;
            float f1 = (float)(0.25 + 0.75 * d0);
            float f2 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068 * f1 * 0.5F;
            float f3 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val194 * f1 * 0.5F;
            float f4 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val195 * f1 * 0.5F;
            int k = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val316;
            if (k == 2 && afloat == null) {
               k = 1;
            }

            if (k == 3 && afloat1 == null) {
               k = 0;
            }

            switch (k) {
               case 1:
                  this.UiAnimation(d7, d9, d11, f2, f3, f4, d13, d14, d15, i, var4);
                  break;
               case 2:
                  this.on23(afloat, d7, d9, d11, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068 * f1, (float)d13, (float)d14, (float)d15, i, var4);
                  break;
               case 3:
                  this.on23(afloat1, d7, d9, d11, liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068 * f1, (float)d13, (float)d14, (float)d15, i, var4);
                  break;
               default:
                  this.on23(d7, d9, d11, f2, f3, f4, d13, d14, d15, i, var4);
            }
         }

         float f5 = MathHelper.sin(var2 * (float) (Math.PI * 2) * 4.0F) * 0.4F + 0.6F;
         float f6 = 0.12F * f5 * (1.0F - var2 * 0.5F) * this.scale2.getCurrent();
         if (f6 > 0.01F) {
            this.on23(vec3d.x, vec3d.y, vec3d.z, f6, f6, f6, var2 * 4.0, var2 * 5.0, var2 * 3.0, j, var4 * 1.4F);
         }

         byte b0 = 18;
         float f7 = (float)(0.55 - 0.45 * var2) * this.scale2.getCurrent();
         if (f7 > 0.05F) {
            double d16 = var2 * (float) (Math.PI * 2) * 6.0;
            double d17 = vec3d.x + Math.cos(d16) * f7;
            double d18 = vec3d.z + Math.sin(d16) * f7;

            for (int l = 1; l <= b0; l++) {
               double d8 = d16 + (double)l / b0 * (float) (Math.PI * 2);
               double d10 = vec3d.x + Math.cos(d8) * f7;
               double d12 = vec3d.z + Math.sin(d8) * f7;
               this.on23(new Vec3d(d17, vec3d.y, d18), new Vec3d(d10, vec3d.y, d12), j, var4);
               d17 = d10;
               d18 = d12;
            }
         }
      }
   }

   public void UiAnimation(KillEffect.DefaultService var1, float var2, float var3, float var4, ArgbColor var5) {
      float f = 1.0F - var2;
      float f1 = f * f;
      if (!(f1 <= 0.01F)) {
         Particles ii1l11il1iililiil = Particles.particles;
         float[] afloat = ii1l11il1iililiil.getThis2();
         float[] afloat1 = ii1l11il1iililiil.getThis();
         int i = var5.call001();
         int j = ColorUtils.ColorAnimator(i, f1);

         for (KillEffect.CoreService liii1111liiii1lll1ll11il1_ii1il11l111ii11iil : var1.val142) {
            float f2 = var3 - liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val143;
            if (!(f2 <= 0.0F)) {
               double d0 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.x + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.x * f2;
               double d1 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.y + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.y * f2;
               double d2 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val048.z + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val020.z * f2;
               float f3 = (float)(
                  liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.x + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.x * f2
               );
               float f4 = (float)(
                  liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.y + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.y * f2
               );
               float f5 = (float)(
                  liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val026.z + liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val027.z * f2
               );
               float f6 = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val068 * (1.0F - 0.25F * var2);
               int k = liii1111liiii1lll1ll11il1_ii1il11l111ii11iil.val316;
               if (k == 2 && afloat == null) {
                  k = 1;
               }

               if (k == 3 && afloat1 == null) {
                  k = 0;
               }

               switch (k) {
                  case 0:
                     this.on23(d0, d1, d2, f6 * 0.5F, f6 * 0.5F, f6 * 0.5F, f3, f4, f5, j, var4);
                     break;
                  case 1:
                     this.UiAnimation(d0, d1, d2, f6 * 0.5F, f6 * 0.6F, f6 * 0.5F, f3, f4, f5, j, var4);
                     break;
                  case 2:
                     this.on23(afloat, d0, d1, d2, f6, f3, f4, f5, j, var4);
                     break;
                  case 3:
                     this.on23(afloat1, d0, d1, d2, f6, f3, f4, f5, j, var4);
               }
            }
         }
      }
   }

   public void on23(float[] var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, int var12, float var13) {
      if (var1 != null && var1.length != 0) {
         this.matrix3f.identity().rotateY(var10).rotateX(var9).rotateZ(var11);

         for (byte b0 = 0; b0 < var1.length; b0 = (byte)(b0 + 6)) {
            this.vector3f.set(var1[b0] * var8, var1[b0 + 1] * var8, var1[b0 + 2] * var8);
            this.vector3f2.set(var1[b0 + 3] * var8, var1[b0 + 4] * var8, var1[b0 + 5] * var8);
            this.matrix3f.transform(this.vector3f);
            this.matrix3f.transform(this.vector3f2);
            this.on23(
               new Vec3d(var2 + this.vector3f.x, var4 + this.vector3f.y, var6 + this.vector3f.z),
               new Vec3d(var2 + this.vector3f2.x, var4 + this.vector3f2.y, var6 + this.vector3f2.z),
               var12,
               var13
            );
         }
      }
   }

   public Vec3d on23(
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23
   ) {
      double d0 = var3 * var13 - var5 * var15;
      double d1 = var3 * var15 + var5 * var13;
      double d2 = var1 * var17 + d1 * var19;
      double d3 = -var1 * var19 + d1 * var17;
      double d4 = d2 * var21 - d0 * var23;
      double d5 = d2 * var23 + d0 * var21;
      return new Vec3d(var7 + d4, var9 + d5, var11 + d3);
   }

   public void on23(double var1, double var3, double var5, float var7, float var8, float var9, double var10, double var12, double var14, int var16, float var17) {
      double d0 = Math.cos(var10);
      double d1 = Math.sin(var10);
      double d2 = Math.cos(var12);
      double d3 = Math.sin(var12);
      double d4 = Math.cos(var14);
      double d5 = Math.sin(var14);
      Vec3d[] avec3d = new Vec3d[8];

      for (int i = 0; i < 8; i++) {
         double d6 = (i & 4) != 0 ? var7 : -var7;
         double d7 = (i & 2) != 0 ? var8 : -var8;
         double d8 = (i & 1) != 0 ? var9 : -var9;
         avec3d[i] = this.on23(d6, d7, d8, var1, var3, var5, d0, d1, d2, d3, d4, d5);
      }

      this.on23(avec3d[0], avec3d[4], var16, var17);
      this.on23(avec3d[4], avec3d[5], var16, var17);
      this.on23(avec3d[5], avec3d[1], var16, var17);
      this.on23(avec3d[1], avec3d[0], var16, var17);
      this.on23(avec3d[2], avec3d[6], var16, var17);
      this.on23(avec3d[6], avec3d[7], var16, var17);
      this.on23(avec3d[7], avec3d[3], var16, var17);
      this.on23(avec3d[3], avec3d[2], var16, var17);
      this.on23(avec3d[0], avec3d[2], var16, var17);
      this.on23(avec3d[4], avec3d[6], var16, var17);
      this.on23(avec3d[5], avec3d[7], var16, var17);
      this.on23(avec3d[1], avec3d[3], var16, var17);
   }

   public void UiAnimation(
      double var1, double var3, double var5, float var7, float var8, float var9, double var10, double var12, double var14, int var16, float var17
   ) {
      double d0 = Math.cos(var10);
      double d1 = Math.sin(var10);
      double d2 = Math.cos(var12);
      double d3 = Math.sin(var12);
      double d4 = Math.cos(var14);
      double d5 = Math.sin(var14);
      Vec3d[] avec3d = new Vec3d[]{
         this.on23(-var7, -var8, -var9, var1, var3, var5, d0, d1, d2, d3, d4, d5),
         this.on23(var7, -var8, -var9, var1, var3, var5, d0, d1, d2, d3, d4, d5),
         this.on23(var7, -var8, var9, var1, var3, var5, d0, d1, d2, d3, d4, d5),
         this.on23(-var7, -var8, var9, var1, var3, var5, d0, d1, d2, d3, d4, d5),
         this.on23(0.0, var8, 0.0, var1, var3, var5, d0, d1, d2, d3, d4, d5)
      };
      this.on23(avec3d[0], avec3d[1], var16, var17);
      this.on23(avec3d[1], avec3d[2], var16, var17);
      this.on23(avec3d[2], avec3d[3], var16, var17);
      this.on23(avec3d[3], avec3d[0], var16, var17);
      this.on23(avec3d[0], avec3d[4], var16, var17);
      this.on23(avec3d[1], avec3d[4], var16, var17);
      this.on23(avec3d[2], avec3d[4], var16, var17);
      this.on23(avec3d[3], avec3d[4], var16, var17);
   }

   public void on23(
      double var1, double var3, double var5, float var7, float var8, float var9, double var10, double var12, double var14, int var16, int var17, float var18
   ) {
      double d0 = Math.cos(var10);
      double d1 = Math.sin(var10);
      double d2 = Math.cos(var12);
      double d3 = Math.sin(var12);
      double d4 = Math.cos(var14);
      double d5 = Math.sin(var14);
      Vec3d[] avec3d = new Vec3d[8];

      for (int i = 0; i < 8; i++) {
         double d6 = (i & 4) != 0 ? var7 : -var7;
         double d7 = (i & 2) != 0 ? var8 : -var8;
         double d8 = (i & 1) != 0 ? var9 : -var9;
         avec3d[i] = this.on23(d6, d7, d8, var1, var3, var5, d0, d1, d2, d3, d4, d5);
      }

      WorldRender.on23(avec3d[0], avec3d[4], avec3d[5], avec3d[1], var16, true);
      WorldRender.on23(avec3d[2], avec3d[3], avec3d[7], avec3d[6], var16, true);
      WorldRender.on23(avec3d[0], avec3d[2], avec3d[6], avec3d[4], var16, true);
      WorldRender.on23(avec3d[1], avec3d[5], avec3d[7], avec3d[3], var16, true);
      WorldRender.on23(avec3d[0], avec3d[1], avec3d[3], avec3d[2], var16, true);
      WorldRender.on23(avec3d[4], avec3d[6], avec3d[7], avec3d[5], var16, true);
      this.on23(avec3d[0], avec3d[4], var17, var18);
      this.on23(avec3d[4], avec3d[5], var17, var18);
      this.on23(avec3d[5], avec3d[1], var17, var18);
      this.on23(avec3d[1], avec3d[0], var17, var18);
      this.on23(avec3d[2], avec3d[6], var17, var18);
      this.on23(avec3d[6], avec3d[7], var17, var18);
      this.on23(avec3d[7], avec3d[3], var17, var18);
      this.on23(avec3d[3], avec3d[2], var17, var18);
      this.on23(avec3d[0], avec3d[2], var17, var18);
      this.on23(avec3d[4], avec3d[6], var17, var18);
      this.on23(avec3d[5], avec3d[7], var17, var18);
      this.on23(avec3d[1], avec3d[3], var17, var18);
   }

   public void on23(
      MatrixStack var1,
      Identifier var2,
      Vec3d var3,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      float var12,
      double var13,
      double var15,
      double var17,
      KillEffect.Particle[] var19,
      float var20
   ) {
      if (!(var20 <= 0.005F)) {
         double d0 = Math.cos(var13);
         double d1 = Math.sin(var13);
         double d2 = Math.cos(var15);
         double d3 = Math.sin(var15);
         double d4 = Math.cos(var17);
         double d5 = Math.sin(var17);
         double d6 = var4 - var3.x;
         double d7 = var6 - var3.y;
         double d8 = var8 - var3.z;
         Vec3d[] avec3d = new Vec3d[8];

         for (int i = 0; i < 8; i++) {
            double d9 = (i & 4) != 0 ? var10 : -var10;
            double d10 = (i & 2) != 0 ? var11 : -var11;
            double d11 = (i & 1) != 0 ? var12 : -var12;
            avec3d[i] = this.on23(d9, d10, d11, d6, d7, d8, d0, d1, d2, d3, d4, d5);
         }

         int j = MathHelper.clamp(Math.round(var20 * 255.0F), 0, 255);
         int k = j << 24 | 16777215;
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.disableCull();
         org.zenith.render.LegacyRenderBridge.usePositionTexColor();
         org.zenith.render.LegacyRenderBridge.setTexture(0, var2);
         Matrix4f matrix4f = var1.peek().getPositionMatrix();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         this.on23(bufferbuilder, matrix4f, avec3d[4], avec3d[0], avec3d[1], avec3d[5], var19[0], k);
         this.on23(bufferbuilder, matrix4f, avec3d[2], avec3d[6], avec3d[7], avec3d[3], var19[1], k);
         this.on23(bufferbuilder, matrix4f, avec3d[2], avec3d[6], avec3d[4], avec3d[0], var19[2], k);
         this.on23(bufferbuilder, matrix4f, avec3d[7], avec3d[3], avec3d[1], avec3d[5], var19[3], k);
         this.on23(bufferbuilder, matrix4f, avec3d[2], avec3d[3], avec3d[1], avec3d[0], var19[4], k);
         this.on23(bufferbuilder, matrix4f, avec3d[7], avec3d[6], avec3d[4], avec3d[5], var19[5], k);
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         org.zenith.render.LegacyRenderBridge.enableCull();
         org.zenith.render.LegacyRenderBridge.disableBlend();
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public void on23(BufferBuilder var1, Matrix4f var2, Vec3d var3, Vec3d var4, Vec3d var5, Vec3d var6, KillEffect.Particle var7, int var8) {
      var1.vertex(var2, (float)var3.x, (float)var3.y, (float)var3.z)
         .texture(var7.float116(), var7.float117())
         .color(var8);
      var1.vertex(var2, (float)var4.x, (float)var4.y, (float)var4.z)
         .texture(var7.float118(), var7.float117())
         .color(var8);
      var1.vertex(var2, (float)var5.x, (float)var5.y, (float)var5.z)
         .texture(var7.float118(), var7.float119())
         .color(var8);
      var1.vertex(var2, (float)var6.x, (float)var6.y, (float)var6.z)
         .texture(var7.float116(), var7.float119())
         .color(var8);
   }

   public void on23(Vec3d var1, Vec3d var2, int var3, float var4) {
      float f = 1.0F;
      WorldRender.on23(var1, var2, ColorUtils.ColorAnimator(var3, 0.05F * f), var4 * 5.5F, true);
      WorldRender.on23(var1, var2, ColorUtils.ColorAnimator(var3, 0.12F + 0.06F * f), var4 * 3.5F, true);
      WorldRender.on23(var1, var2, ColorUtils.ColorAnimator(var3, 0.28F + 0.1F * f), var4 * 1.9F, true);
      WorldRender.on23(var1, var2, var3, var4, true);
   }

   public void call416() {
      if (!this.map20.isEmpty()) {
         long i = System.currentTimeMillis();
         this.map20.values().removeIf(var2 -> i - var2 > 1500L);
      }
   }

   public boolean ProfileItemBuilder(Entity var1) {
      return var1 instanceof PlayerEntity && var1 != minecraftClient3.player;
   }

   public ArgbColor EventClick(int var1) {
      return this.color2.is(0) ? ZenithClient.on23().TextScanner().getClientColor(var1) : this.customColor3.getColor();
   }


   public static final class DefaultService {
      final long val313;
      final int val140;
      final int val141;
      final CoreService[] val142;
      final Identifier val314;
      final float val422;

      DefaultService(long var1, int var3, int var4, CoreService[] var5, Identifier var6) {
         this(var1, var3, var4, var5, var6, 1.0F);
      }

      DefaultService(long var1, int var3, int var4, CoreService[] var5, Identifier var6, float var7) {
         this.val313 = var1;
         this.val140 = var3;
         this.val141 = var4;
         this.val142 = var5;
         this.val314 = var6;
         this.val422 = var7;
      }
   }

   public static final class CoreService {
      final Vec3d val048;
      final Vec3d val020;
      final Vec3d val026;
      final Vec3d val027;
      final float val068;
      final float val194;
      final float val195;
      final float val143;
      final Particle[] val315;
      final int val316;

      CoreService(Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, float var5, float var6, float var7, float var8, Particle[] var9) {
         this(var1, var2, var3, var4, var5, var6, var7, var8, var9, 0);
      }

      CoreService(
         Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4, float var5, float var6, float var7, float var8, Particle[] var9, int var10
      ) {
         this.val048 = var1;
         this.val020 = var2;
         this.val026 = var3;
         this.val027 = var4;
         this.val068 = var5;
         this.val194 = var6;
         this.val195 = var7;
         this.val143 = var8;
         this.val315 = var9;
         this.val316 = var10;
      }
   }

   public record Particle(float float116, float float117, float float118, float float119) {
      public float call411() {
         return this.float116;
      }

      public float call412() {
         return this.float117;
      }

      public float call441() {
         return this.float118;
      }

      public float call477() {
         return this.float119;
      }
   }
}
