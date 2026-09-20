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


import net.minecraft.world.World;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.HudRenderEvent;
import org.zenith.render.ScreenProjection;
import org.zenith.render.ShapeRenderer;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.util.RaycastUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(name = "Predictions", category = Category.RENDER, description = "Показывает куда упадет предмет")
public final class Predictions extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final BooleanSetting renderItemEntity = new BooleanSetting("module.predictions.renderItemEntity", "module.predictions.renderItemEntity.desc", true);
   public final ModeSetting mode12 = new ModeSetting(
      "module.predictions.mode", "module.predictions.mode.desc", "module.predictions.sparks", "module.predictions.fire", "module.predictions.frost"
   );
   public static final int int223 = 1;
   public static final Identifier[] val453 = new Identifier[0];
   public static final Identifier identifier4 = Identifier.of("zenith", "textures/glow.png");
   public static final double double92 = 1.0E-4;
   public static final float float154 = 0.11F;
   public static final Matrix4f matrix4f6 = new Matrix4f();
   public static final int int224 = 56;
   public static final double double93 = 0.017857142857142856;
   public static final double double94 = 2.399963;
   public final List<Predictions.Projectile> list70 = new ArrayList<>();
   public final List<List<Vec3d>> list71 = new ArrayList<>();
   public List<Entity> list72 = List.of();
   public final Map<Item, Predictions.Simulation> map22 = new HashMap<>();
   public final Map<Item, ProjectileEntity> map23 = new HashMap<>();
   public final Matrix4f matrix4f7 = new Matrix4f();
   public final Matrix4f matrix4f8 = new Matrix4f();
   public final Matrix4f matrix4f9 = new Matrix4f();
   public final List<Predictions.Simulator> list73 = new ArrayList<>();
   public final Map<Integer, Long> map24 = new HashMap<>();
   public final Map<Integer, Vec3d> map25 = new HashMap<>();
   public long long124;
   public final Random random3 = new Random();
   public static final Predictions predictions = new Predictions();
   public static final List<ItemStack> list74 = new ArrayList<>();
   public static final int int225 = 48;

   @Override
   public void onDisable() {
      this.list70.clear();
      this.list71.clear();
      this.list72 = List.of();
      this.map22.clear();
      this.map23.clear();
      this.list73.clear();
      this.map24.clear();
      this.map25.clear();
      super.onDisable();
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      long i = System.currentTimeMillis();

      for (Predictions.Simulator lii1l1lll1_illi1l1l1 : this.list73) {
         lii1l1lll1_illi1l1l1.update();
         lii1l1lll1_illi1l1l1.update();
         lii1l1lll1_illi1l1l1.update();
      }

      this.list73.removeIf(var2x -> i - var2x.val320 > 600L);
      this.list70.clear();
      this.list71.clear();
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.list72 = this.call138();

         for (Entity entity : this.list72) {
            Vec3d vec3d = entity.getVelocity();
            Vec3d vec3d1 = entity.getEntityPos();
            int j = 0;
            ArrayList arraylist = new ArrayList();
            arraylist.add(vec3d1);

            for (int k = 0; k < 300; k++) {
               Vec3d vec3d2 = vec3d1;
               vec3d1 = vec3d1.add(vec3d);
               vec3d = this.on23(entity, vec3d2, vec3d);
               BlockHitResult blockhitresult = RaycastUtils.on23(vec3d2, vec3d1, ShapeType.COLLIDER, entity);
               if (!blockhitresult.getType().equals(Type.MISS)) {
                  vec3d1 = blockhitresult.getPos();
               }

               arraylist.add(vec3d1);
               boolean flag = !minecraftClient3.world
                  .getOtherEntities(
                     entity,
                     new Box(vec3d2, vec3d1).expand(0.25),
                     var0 -> var0 instanceof LivingEntity livingentity && livingentity != minecraftClient3.player && livingentity.isAlive()
                  )
                  .isEmpty();
               if (blockhitresult.getType().equals(Type.BLOCK)
                  || vec3d1.y < -128.0
                  || flag
                  || blockhitresult.getType().equals(Type.ENTITY)) {
                  this.on23(entity, vec3d1, j);
                  break;
               }

               j++;
            }

            this.list71.add(arraylist);
         }
      } else {
         this.list72 = List.of();
      }
   }

   @EventTarget
   public void onDraw(HudRenderEvent var1) {
      for (Predictions.Projectile lii1l1lll1_l1i1illlili : this.list70) {
         Vec3d vec3d = ScreenProjection.BotDisconnectEvent(lii1l1lll1_l1i1illlili.vec3d30());
         int i = lii1l1lll1_l1i1illlili.int191();
         if (vec3d != null && !(vec3d.z <= 0.0) && !(vec3d.z >= 1.0) && ScreenProjection.BotWorldJoinEvent(lii1l1lll1_l1i1illlili.vec3d30())) {
            ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
            Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
            double d0 = i * 50 / 1000.0;
            String s = String.format("%.1f", d0) + " сек";
            float f = font.width(s);
            float f1 = 11.2F;
            float f2 = 2.0F;
            float f3 = (float)vec3d.getX();
            float f4 = (float)vec3d.getY();
            float f5 = f1 + f2 + f;
            float f6 = f3 - f5 / 2.0F;
            float f7 = f4 - f1 / 2.0F;
            float f8 = f6 - f2;
            float f9 = f7 - f2;
            float f10 = f5 + f2 * 2.0F;
            float f11 = f1 + f2 * 2.0F;
            this.pushCenteredScale(var1.Bot(), f3, f4, 1.0F, 1.0F);
            CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(Interface.float212() * 0.6F);
            ShapeRenderer.ItemRegistry(var1.Bot().getMatrices(), f8, f9, f10, f11, 22.0F, ii1il11l111ii11iil, ArgbColor.var11934);
            var1.Bot().drawRoundedRect(f8, f9, f10, f11, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
            float f12 = f7 + (f1 - f1) / 2.0F;
            var1.Bot().pushMatrix();
            var1.Bot().getMatrices().translate(f6, f12);
            var1.Bot().getMatrices().scale(0.7F, 0.7F);
            var1.Bot().drawItem(lii1l1lll1_l1i1illlili.itemStack10(), 0, 0);
            var1.Bot().popMatrix();
            float f13 = f6 + f1 + f2;
            float f14 = f4 - font.height() / 2.0F;
            var1.Bot().drawText(font, s, f13, f14, zenithstyle.getTextEnable().getColor());
            this.pop(var1.Bot());
         }
      }
   }

   public void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      var1.pushMatrix();
      var1.getMatrices().translate(var2, var3);
      var1.getMatrices().scale(var4 * EntityESP.entityESP.getSize(), var5 * EntityESP.entityESP.getSize());
      var1.getMatrices().translate(-var2, -var3);
   }

   public void pop(CustomDrawContext var1) {
      var1.popMatrix();
   }

   @EventTarget
   public void ColorAnimator(EventHookWorldRender var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.on23(var1.ClanUpgrade(), List.of(minecraftClient3.player.getMainHandStack(), minecraftClient3.player.getOffHandStack()));
         if (!this.list71.isEmpty()) {
            this.on23(this.list71, var1.CropFarmer());
         }

         if (this.mode12.is(0) && !this.list72.isEmpty()) {
            this.Easing(this.list72, System.currentTimeMillis());
         }
      }
   }

   public void on23(List<List<Vec3d>> var1, float var2) {
      Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
      Vec3d vec3d = camera.getCameraPos();
      matrix4f6.identity().rotateX(camera.getPitch() * (float) (Math.PI / 180.0)).rotateY((camera.getYaw() + 180.0F) * (float) (Math.PI / 180.0));
      GL11.glEnable(2881);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.disableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      org.zenith.render.LegacyRenderBridge.setTexture(0, WorldRender.identifier14);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float f = (float)(System.nanoTime() % 10000000000L) / 1.0E9F;

      for (int i = 0; i < var1.size(); i++) {
         List list = var1.get(i);
         if (list.size() >= 2) {
            Vec3d vec3d1 = Vec3d.ZERO;
            if (i < this.list72.size()) {
               Entity entity = this.list72.get(i);
               vec3d1 = entity.getLerpedPos(var2).subtract((Vec3d)list.getFirst());
            }

            this.on23(bufferbuilder, list, vec3d1, vec3d, f, 0.28599998F, 0.3F);
            this.on23(bufferbuilder, list, vec3d1, vec3d, f, 0.11F, 0.9F);
         }
      }

      BuiltBuffer builtbuffer = bufferbuilder.endNullable();
      if (builtbuffer != null) {
         org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
      }

      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableBlend();
      GL11.glDisable(2881);
   }

   public void on23(BufferBuilder var1, List<Vec3d> var2, Vec3d var3, Vec3d var4, float var5, float var6, float var7) {
      int i = var2.size();
      Matrix4f matrix4f = matrix4f6;
      Vec3d vec3d = null;
      Vec3d vec3d1 = null;
      float f = 0.0F;
      ArgbColor i11ii1llliilllii1i1 = null;

      for (int j = 0; j < i; j++) {
         Vec3d vec3d2 = var2.get(j).add(var3).subtract(var4);
         Vec3d vec3d3 = this.on23(var2, j, i);
         if (vec3d3 != null) {
            double d0 = vec3d2.length();
            float f1 = (float)(var6 * Math.max(1.0, d0 / 26.0));
            Vec3d vec3d4 = this.ItemRegistry(vec3d3, vec3d2);
            Vec3d vec3d5 = vec3d2.add(vec3d4.multiply(f1));
            Vec3d vec3d6 = vec3d2.subtract(vec3d4.multiply(f1));
            float f2 = (float)j / (i - 1);
            float f3 = MathHelper.clamp(j / 3.0F, 0.0F, 1.0F);
            float f4 = MathHelper.clamp((i - 1 - j) / 6.0F, 0.0F, 1.0F);
            float f5 = 0.78F + 0.22F * MathHelper.sin(f2 * 11.0F - var5 * 5.0F);
            float f6 = var7 * f3 * f4 * f5;
            ArgbColor i11ii1llliilllii1i11 = ZenithClient.on23().TextScanner().getClientColor((int)(f2 * 180.0F));
            if (vec3d != null) {
               int k = this.on23(i11ii1llliilllii1i1, f);
               int l = this.on23(i11ii1llliilllii1i11, f6);
               var1.vertex(matrix4f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).texture(0.0F, 0.5F).color(k);
               var1.vertex(matrix4f, (float)vec3d1.x, (float)vec3d1.y, (float)vec3d1.z)
                  .texture(1.0F, 0.5F)
                  .color(k);
               var1.vertex(matrix4f, (float)vec3d6.x, (float)vec3d6.y, (float)vec3d6.z)
                  .texture(1.0F, 0.5F)
                  .color(l);
               var1.vertex(matrix4f, (float)vec3d5.x, (float)vec3d5.y, (float)vec3d5.z)
                  .texture(0.0F, 0.5F)
                  .color(l);
            }

            vec3d = vec3d5;
            vec3d1 = vec3d6;
            f = f6;
            i11ii1llliilllii1i1 = i11ii1llliilllii1i11;
         }
      }
   }

   public Vec3d on23(List<Vec3d> var1, int var2, int var3) {
      Vec3d vec3d = Vec3d.ZERO;
      if (var2 > 0) {
         vec3d = vec3d.add(var1.get(var2).subtract(var1.get(var2 - 1)));
      }

      if (var2 < var3 - 1) {
         vec3d = vec3d.add(var1.get(var2 + 1).subtract(var1.get(var2)));
      }

      return vec3d.lengthSquared() < 1.0E-8 ? null : vec3d.normalize();
   }

   public Vec3d ItemRegistry(Vec3d var1, Vec3d var2) {
      Vec3d vec3d = var2.lengthSquared() < 1.0E-8 ? new Vec3d(0.0, 0.0, 1.0) : var2.normalize().negate();
      Vec3d vec3d1 = var1.crossProduct(vec3d);
      if (vec3d1.lengthSquared() < 1.0E-8) {
         vec3d1 = var1.crossProduct(new Vec3d(0.0, 1.0, 0.0));
         if (vec3d1.lengthSquared() < 1.0E-8) {
            vec3d1 = var1.crossProduct(new Vec3d(1.0, 0.0, 0.0));
         }
      }

      return vec3d1.normalize();
   }

   public int on23(ArgbColor var1, float var2) {
      int i = (int)(MathHelper.clamp(var2, 0.0F, 1.0F) * 255.0F);
      return i << 24 | var1.float240() << 16 | var1.var14323() << 8 | var1.var14324();
   }

   public void Easing(List<Entity> var1, long var2) {
      Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
      Vec3d vec3d = camera.getCameraPos();
      float f = camera.getPitch();
      float f1 = camera.getYaw();
      float f2 = f * (float) (Math.PI / 180.0);
      float f3 = f1 * (float) (Math.PI / 180.0);
      this.matrix4f7.identity().rotateX(f2).rotateY((f1 + 180.0F) * (float) (Math.PI / 180.0));
      this.matrix4f8.identity().rotateY(-f3).rotateX(f2);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      org.zenith.render.LegacyRenderBridge.setTexture(0, identifier4);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      byte b0 = 8;
      ArgbColor[] ai11ii1llliilllii1i1 = new ArgbColor[8];

      for (int i = 0; i < 8; i++) {
         ai11ii1llliilllii1i1[i] = ZenithClient.on23().TextScanner().getClientColor(i * 45);
      }

      float f12 = minecraftClient3.getRenderTickCounter().getTickProgress(false);
      double d0 = System.nanoTime() % 1000000000000L / 1.0E9;
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (Entity entity : var1) {
         Vec3d vec3d1 = entity.getVelocity();
         double d1 = vec3d1.x * vec3d1.x + vec3d1.y * vec3d1.y + vec3d1.z * vec3d1.z;
         if (!(d1 < 1.0E-4)) {
            Vec3d vec3d2 = entity.getLerpedPos(f12);
            double d2 = vec3d2.x;
            double d3 = vec3d2.y + entity.getHeight() * 0.5;
            double d4 = vec3d2.z;
            double d5 = Math.sqrt(vec3d2.squaredDistanceTo(vec3d));
            float f4 = (float)MathHelper.clamp(d5 / 22.0, 1.0, 6.0);
            int j = d5 > 96.0 ? 28 : 56;
            double d6 = 1.0 / Math.sqrt(d1);
            double d7 = vec3d1.x * d6;
            double d8 = vec3d1.y * d6;
            double d9 = vec3d1.z * d6;
            double d10 = -d9;
            double d11 = 0.0;
            double d12 = d7;
            double d13 = d10 * d10 + d7 * d7;
            if (d13 < 1.0E-4) {
               d10 = 0.0;
               d11 = d9;
               d12 = -d8;
               d13 = d9 * d9 + d12 * d12;
            }

            double d14 = 1.0 / Math.sqrt(d13);
            d10 *= d14;
            d11 *= d14;
            d12 *= d14;
            double d15 = d11 * d9 - d12 * d8;
            double d16 = d12 * d7 - d10 * d9;
            double d17 = d10 * d8 - d11 * d7;
            float f5 = MathHelper.clamp(entity.getWidth() * 0.5F, 0.09F, 0.22F) * f4;
            double d18 = Math.min(2.4, Math.sqrt(d1) * 4.5);
            double d19 = (0.45 + d18 * 0.3) * f4;
            double d20 = (0.34 + d18 * 0.26) * f4;
            double d21 = Math.PI * 3;
            double d22 = d0 * 0.3 + entity.getId() * 0.071;

            for (int k = 0; k < j; k++) {
               double d23 = d22 + k * (1.0 / j);
               d23 -= Math.floor(d23);
               float f6 = (float)Math.min(1.0, d23 / 0.22);
               float f7 = (float)(1.0 - d23);
               float f8 = f6 * f6 * (3.0F - 2.0F * f6);
               float f9 = f7 * f7 * (3.0F - 2.0F * f7);
               float f10 = f8 * f9;
               if (!(f10 < 0.02F)) {
                  double d24 = d23 * (Math.PI * 3) + k * 2.399963 * 0.25 + d0 * 0.3;
                  double d25 = d19 * d23;
                  double d26 = k * 0.6180339887 % 1.0;
                  double d27 = d20 * (0.35 + d26 * 0.75) * (0.25 + d23 * 0.75);
                  double d28 = Math.cos(d24) * d27;
                  double d29 = Math.sin(d24) * d27;
                  double d30 = d2 - d7 * d25 + d10 * d28 + d15 * d29 - vec3d.x;
                  double d31 = d3 - d8 * d25 + d11 * d28 + d16 * d29 - vec3d.y;
                  double d32 = d4 - d9 * d25 + d12 * d28 + d17 * d29 - vec3d.z;
                  float f11 = f5 * (1.0F - (float)d23 * 0.55F);
                  ArgbColor i11ii1llliilllii1i1 = ai11ii1llliilllii1i1[k % 8];
                  this.on23(bufferbuilder, d30, d31, d32, f11 * 2.2F, f10 * 0.7F, i11ii1llliilllii1i1, false);
                  if (f10 > 0.3F) {
                     this.on23(bufferbuilder, d30, d31, d32, f11, f10 * 1.9F, i11ii1llliilllii1i1, true);
                  }
               }
            }
         }
      }

      BuiltBuffer builtbuffer = bufferbuilder.endNullable();
      if (builtbuffer != null) {
         org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
      }

      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
   }

   public void on23(BufferBuilder var1, double var2, double var4, double var6, float var8, float var9, ArgbColor var10, boolean var11) {
      this.matrix4f9.set(this.matrix4f7).translate((float)var2, (float)var4, (float)var6).mul(this.matrix4f8);
      int i = this.UiAnimation(var10, var11 ? var9 : var9 * 0.7F);
      float f = var8 * (var11 ? 0.72F : 0.9F);
      var1.vertex(this.matrix4f9, f, -f, 0.0F).texture(0.0F, 1.0F).color(i);
      var1.vertex(this.matrix4f9, -f, -f, 0.0F).texture(1.0F, 1.0F).color(i);
      var1.vertex(this.matrix4f9, -f, f, 0.0F).texture(1.0F, 0.0F).color(i);
      var1.vertex(this.matrix4f9, f, f, 0.0F).texture(0.0F, 0.0F).color(i);
   }

   public int UiAnimation(ArgbColor var1, float var2) {
      int i = MathHelper.clamp((int)(var2 * 255.0F), 0, 255);
      return i << 24 | var1.float240() << 16 | var1.var14323() << 8 | var1.var14324();
   }

   public void CloudApiClient(Entity var1) {
      int i = var1.getId();
      long j = System.currentTimeMillis();
      if (!this.map24.containsKey(i) || j - this.map24.get(i) >= 20L) {
         this.map24.put(i, j);
         Vec3d vec3d = var1.getEntityPos();
         Vec3d vec3d1 = var1.getVelocity().normalize().multiply(-0.05);

         for (int k = 0; k < 3; k++) {
            double d0 = (this.random3.nextDouble() - 0.5) * 0.35;
            double d1 = (this.random3.nextDouble() - 0.5) * 0.35;
            double d2 = (this.random3.nextDouble() - 0.5) * 0.35;
            double d3 = vec3d1.x + (this.random3.nextDouble() - 0.5) * 0.06;
            double d4 = vec3d1.y + (this.random3.nextDouble() - 0.5) * 0.06 - 0.01;
            double d5 = vec3d1.z + (this.random3.nextDouble() - 0.5) * 0.06;
            this.list73
               .add(new Predictions.Simulator(vec3d.x + d0, vec3d.y + d1, vec3d.z + d2, d3, d4, d5, this.random3.nextInt(90), j));
         }

         this.map24.entrySet().removeIf(var2x -> j - var2x.getValue() > 5000L);
      }
   }

   public void ColorAnimator(MatrixStack var1) {
      if (!this.list73.isEmpty()) {
         long i = System.currentTimeMillis();
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.disableCull();
         org.zenith.render.LegacyRenderBridge.disableDepthTest();
         org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
         org.zenith.render.LegacyRenderBridge.setTexture(0, WorldRender.identifier14);
         org.zenith.render.LegacyRenderBridge.usePositionTexColor();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;

         for (Predictions.Simulator lii1l1lll1_illi1l1l1 : this.list73) {
            long j = i - lii1l1lll1_illi1l1l1.val320;
            float f = (float)j / 600.0F;
            float f1;
            if (f < 0.2F) {
               f1 = f / 0.2F;
            } else {
               f1 = 1.0F - (f - 0.2F) / 0.8F;
            }

            f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
            float f2 = 0.27F * (1.0F - f * 0.6F);
            double d0 = lii1l1lll1_illi1l1l1.x - camera.getCameraPos().x;
            double d1 = lii1l1lll1_illi1l1l1.y - camera.getCameraPos().y;
            double d2 = lii1l1lll1_illi1l1l1.z - camera.getCameraPos().z;
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrixstack.translate(d0, d1, d2);
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
            int k = ZenithClient.on23().TextScanner().getClientColor(lii1l1lll1_illi1l1l1.val140).SprintStateEvent(f1).call001();
            bufferbuilder.vertex(matrix4f, -f2 / 2.0F, -f2 / 2.0F, 0.0F).texture(0.0F, 0.0F).color(k);
            bufferbuilder.vertex(matrix4f, -f2 / 2.0F, f2 / 2.0F, 0.0F).texture(0.0F, 1.0F).color(k);
            bufferbuilder.vertex(matrix4f, f2 / 2.0F, f2 / 2.0F, 0.0F).texture(1.0F, 1.0F).color(k);
            bufferbuilder.vertex(matrix4f, f2 / 2.0F, -f2 / 2.0F, 0.0F).texture(1.0F, 0.0F).color(k);
         }

         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         org.zenith.render.LegacyRenderBridge.enableDepthTest();
         org.zenith.render.LegacyRenderBridge.enableCull();
         org.zenith.render.LegacyRenderBridge.disableBlend();
      }
   }

   public void on23(MatrixStack var1, List<ItemStack> var2) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && !var2.isEmpty()) {
         ItemStack itemstack = var2.getFirst();
         Predictions.Simulation lii1l1lll1_ii1il11l111ii11iil = this.map22.get(itemstack.getItem());
         if (lii1l1lll1_ii1il11l111ii11iil == null
            || lii1l1lll1_ii1il11l111ii11iil.call211() != minecraftClient3.player.age
            || lii1l1lll1_ii1il11l111ii11iil.call212() != minecraftClient3.world) {
            List<HitResult> list = this.on23(itemstack, minecraftClient3.player.getActiveItem().getItem(), RotationMath.boolean122());
            list = list == null ? List.of() : list.stream().filter(Objects::nonNull).toList();
            lii1l1lll1_ii1il11l111ii11iil = new Predictions.Simulation(minecraftClient3.player.age, minecraftClient3.world, list);
            this.map22.put(itemstack.getItem(), lii1l1lll1_ii1il11l111ii11iil);
         }

         if (!lii1l1lll1_ii1il11l111ii11iil.call176().isEmpty()) {
            this.UiAnimation(var1, lii1l1lll1_ii1il11l111ii11iil.call176());
         }
      }
   }

   public List<HitResult> on23(ItemStack var1, Item var2, Rotation var3) {
      Object object = var1.getItem();
      Objects.requireNonNull(object);

      return switch (object) {
         case ExperienceBottleItem experiencebottleitem -> this.on23(
            this.on23(var1, () -> new ExperienceBottleEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 0.8
         );
         case SplashPotionItem splashpotionitem -> this.on23(
            this.on23(var1, () -> new SplashPotionEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 0.55
         );
         case TridentItem tridentitem when tridentitem.equals(var2) && minecraftClient3.player.getItemUseTime() >= 10 -> this.on23(
            this.on23(var1, () -> new TridentEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 2.5
         );
         case SnowballItem snowballitem -> this.on23(
            this.on23(var1, () -> new SnowballEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 1.5
         );
         case EggItem eggitem -> this.on23(this.on23(var1, () -> new EggEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 1.5);
         case EnderPearlItem enderpearlitem -> this.on23(
            this.on23(var1, () -> new EnderPearlEntity(minecraftClient3.world, minecraftClient3.player, var1)), var3, 1.5
         );
         case BowItem bowitem when bowitem.equals(var2) && minecraftClient3.player.isUsingItem() -> this.on23(
            this.on23(var1, () -> new ArrowEntity(minecraftClient3.world, minecraftClient3.player, var1, var1)),
            var3,
            3.0F
               * MathHelper.clamp((minecraftClient3.player.getItemUseTime() + minecraftClient3.getRenderTickCounter().getTickProgress(false)) / 20.0F, 0.0F, 1.0F)
         );
         case CrossbowItem crossbowitem when CrossbowItem.isCharged(var1) -> {
            ChargedProjectilesComponent chargedprojectilescomponent = (ChargedProjectilesComponent)var1.get(DataComponentTypes.CHARGED_PROJECTILES);
            ArrayList arraylist = new ArrayList();
            if (chargedprojectilescomponent != null) {
               float f = ((ItemStack)chargedprojectilescomponent.getProjectiles().getFirst()).isOf(Items.FIREWORK_ROCKET) ? 100.0F : 3.0F;
               arraylist.add(
                  this.on23(var3.int202(), this.on23(var1, () -> new ArrowEntity(minecraftClient3.world, minecraftClient3.player, var1, var1)), f)
               );
               if (chargedprojectilescomponent.getProjectiles().size() > 2) {
                  float f1 = minecraftClient3.player.getPitch() / 90.0F;
                  float f2 = f1 * f1 * f1 * f1 * f1;
                  float f3 = MathHelper.lerp(Math.abs(f2), 10, 90);
                  float f4 = MathHelper.lerp(f2, 0, 10);
                  arraylist.add(
                     this.on23(
                        new Rotation(minecraftClient3.player.getYaw() - f3, minecraftClient3.player.getPitch() - f4).int202(),
                        this.on23(var1, () -> new ArrowEntity(minecraftClient3.world, minecraftClient3.player, var1, var1)),
                        f
                     )
                  );
                  arraylist.add(
                     this.on23(
                        new Rotation(minecraftClient3.player.getYaw() + f3, minecraftClient3.player.getPitch() - f4).int202(),
                        this.on23(var1, () -> new ArrowEntity(minecraftClient3.world, minecraftClient3.player, var1, var1)),
                        f
                     )
                  );
               }
            }

            yield arraylist;
         }
         default -> null;
      };
   }

   public ProjectileEntity on23(ItemStack var1, Supplier<? extends ProjectileEntity> var2) {
      ProjectileEntity projectileentity = this.map23.get(var1.getItem());
      if (projectileentity != null
         && projectileentity.getEntityWorld() == minecraftClient3.world
         && projectileentity.getOwner() == minecraftClient3.player) {
         projectileentity.setPosition(
            minecraftClient3.player.getX(), minecraftClient3.player.getEyeY() - 0.1F, minecraftClient3.player.getZ()
         );
         projectileentity.setVelocity(Vec3d.ZERO);
         projectileentity.setOnGround(false);
         if (projectileentity instanceof ThrownItemEntity thrownitementity) {
            thrownitementity.setItem(var1);
         }
      } else {
         projectileentity = var2.get();
         this.map23.put(var1.getItem(), projectileentity);
      }

      return projectileentity;
   }

   public void UiAnimation(MatrixStack var1, List<HitResult> var2) {
      Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
      long i = System.nanoTime();
      float f = this.long124 == 0L ? 0.05F : MathHelper.clamp((float)(i - this.long124) / 1.0E9F, 0.0F, 0.1F);
      this.long124 = i;
      float f1 = 1.0F - (float)Math.exp(-f * 20.0);
      float f2 = (float)(i % 10000000000L) / 1.0E9F;
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.disableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      org.zenith.render.LegacyRenderBridge.setTexture(0, WorldRender.identifier14);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int j = 0; j < var2.size(); j++) {
         HitResult hitresult = var2.get(j);
         Vec3d vec3d = hitresult.getPos();
         Vec3d vec3d1 = this.map25.get(j);
         if (vec3d1 != null && !(vec3d1.squaredDistanceTo(vec3d) > 64.0)) {
            vec3d1 = vec3d1.lerp(vec3d, f1);
         } else {
            vec3d1 = vec3d;
         }

         this.map25.put(j, vec3d1);
         Vec3d vec3d2 = vec3d1.subtract(camera.getCameraPos());
         boolean flag = hitresult.getType().equals(Type.ENTITY);
         ArgbColor i11ii1llliilllii1i1 = flag ? ArgbColor.var11937 : ZenithClient.on23().TextScanner().getClientColor(90);
         var1.push();
         var1.translate(vec3d2.x, vec3d2.y, vec3d2.z);
         switch (this.on23(hitresult)) {
            case UP:
            default:
               break;
            case DOWN:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
               break;
            case NORTH:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
               break;
            case SOUTH:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
               break;
            case WEST:
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
               break;
            case EAST:
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
         }

         var1.translate(0.0, 0.01, 0.0);
         Matrix4f matrix4f = var1.peek().getPositionMatrix();
         float f3 = 0.85F + 0.15F * MathHelper.sin(f2 * 3.2F);
         this.on23(bufferbuilder, matrix4f, 0.62F * f3, this.on23(i11ii1llliilllii1i1, 0.22F));
         this.on23(bufferbuilder, matrix4f, 0.3F * f3, 0.045F, this.on23(i11ii1llliilllii1i1, 0.95F), 0.0F, 48, 1.0F);
         this.on23(bufferbuilder, matrix4f, 0.17F, 0.018F, this.on23(i11ii1llliilllii1i1, 0.55F), 0.0F, 48, 1.0F);
         this.on23(bufferbuilder, matrix4f, 0.4F, 0.05F, this.on23(i11ii1llliilllii1i1, 0.8F), f2 * 0.6F, 4, 0.22F);
         var1.pop();
      }

      BuiltBuffer builtbuffer = bufferbuilder.endNullable();
      if (builtbuffer != null) {
         org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
      }

      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableBlend();
   }

   public void on23(BufferBuilder var1, Matrix4f var2, float var3, int var4) {
      var1.vertex(var2, -var3, 0.0F, -var3).texture(0.0F, 0.0F).color(var4);
      var1.vertex(var2, -var3, 0.0F, var3).texture(0.0F, 1.0F).color(var4);
      var1.vertex(var2, var3, 0.0F, var3).texture(1.0F, 1.0F).color(var4);
      var1.vertex(var2, var3, 0.0F, -var3).texture(1.0F, 0.0F).color(var4);
   }

   public void on23(BufferBuilder var1, Matrix4f var2, float var3, float var4, int var5, float var6, int var7, float var8) {
      float f = var3 - var4;
      float f1 = var3 + var4;
      float f2 = (float) (Math.PI * 2) / var7;

      for (int i = 0; i < var7; i++) {
         float f3 = var6 + i * f2;
         float f4 = f3 + f2 * var8;
         float f5 = MathHelper.cos(f3);
         float f6 = MathHelper.sin(f3);
         float f7 = MathHelper.cos(f4);
         float f8 = MathHelper.sin(f4);
         var1.vertex(var2, f5 * f, 0.0F, f6 * f).texture(0.0F, 0.5F).color(var5);
         var1.vertex(var2, f5 * f1, 0.0F, f6 * f1).texture(1.0F, 0.5F).color(var5);
         var1.vertex(var2, f7 * f1, 0.0F, f8 * f1).texture(1.0F, 0.5F).color(var5);
         var1.vertex(var2, f7 * f, 0.0F, f8 * f).texture(0.0F, 0.5F).color(var5);
      }
   }

   public List<Entity> call138() {
      return EffectEngine.double66()
         .filter(
            var1 -> (var1 instanceof PersistentProjectileEntity || var1 instanceof ThrownItemEntity || var1 instanceof ItemEntity && this.renderItemEntity.isEnabled())
               && !this.CloudUserProfile(var1)
         )
         .toList();
   }

   public boolean call139() {
      return this.isEnabled() && this.mode12.getIndex() >= 1;
   }

   public Identifier call220() {
      int i = this.mode12.getIndex() - 1;
      return i >= 0 && i < val453.length ? val453[i] : null;
   }

   public boolean MediaTrackInfo(Entity var1) {
      if (this.call139() && var1 != null) {
         return var1 instanceof PersistentProjectileEntity || var1 instanceof ThrownItemEntity || var1 instanceof ItemEntity && this.renderItemEntity.isEnabled()
            ? !this.CloudUserProfile(var1)
            : false;
      } else {
         return false;
      }
   }

   public List<HitResult> on23(ProjectileEntity var1, Rotation var2, double var3) {
      return new ArrayList<>(Collections.singleton(this.on23(var2.int202(), var1, var3)));
   }

   public List<HitResult> on23(ProjectileEntity var1, double var2) {
      return this.on23(var1, RotationMath.boolean122(), var2);
   }

   public List<HitResult> on23(ItemStack var1, Rotation var2, Vec3d var3, Vec3d var4, int var5) {
      if (var1.getItem() instanceof BowItem && minecraftClient3.player.isUsingItem()) {
         float f = minecraftClient3.player.getItemUseTime() + minecraftClient3.getRenderTickCounter().getTickProgress(false) + var5;
         double d0 = 3.0F * MathHelper.clamp(f / 20.0F, 0.0F, 1.0F);
         HitResult hitresult = this.on23(
            var3, var4, var2.int202(), this.on23(var1, () -> new ArrowEntity(minecraftClient3.world, minecraftClient3.player, var1, var1)), d0
         );
         return hitresult == null ? List.of() : new ArrayList<>(Collections.singleton(hitresult));
      } else {
         return null;
      }
   }

   public HitResult on23(Vec3d var1, ProjectileEntity var2, double var3) {
      Vec3d vec3d = minecraftClient3.player
         .getEntityPos()
         .subtract(minecraftClient3.player.lastX, minecraftClient3.player.lastY, minecraftClient3.player.lastZ);
      if (var2 instanceof ArrowEntity arrowentity && arrowentity.getItemStack().getItem() instanceof CrossbowItem) {
         vec3d = Vec3d.ZERO;
      }

      return this.on23(
         minecraftClient3.player
            .getEyePos()
            .add(MathUtils.CloudResponse(minecraftClient3.player).subtract(minecraftClient3.player.getEntityPos())),
         vec3d,
         var1,
         var2,
         var3
      );
   }

   public HitResult on23(Vec3d var1, Vec3d var2, Vec3d var3, ProjectileEntity var4, double var5) {
      double d0 = var3.length();
      return d0 <= 1.0E-6 ? null : this.on23(var1, var3.multiply(var5 / d0).add(var2), var4);
   }

   public HitResult on23(ProjectileEntity var1) {
      return this.on23(var1.getEntityPos(), var1.getVelocity(), var1);
   }

   public HitResult on23(Vec3d var1, Vec3d var2, ProjectileEntity var3) {
      for (int i = 0; i < 300; i++) {
         Vec3d vec3d = var1;
         var1 = var1.add(var2);
         var2 = this.on23(var3, vec3d, var2);
         BlockHitResult blockhitresult = RaycastUtils.on23(vec3d, var1, ShapeType.COLLIDER, var3);
         if (!blockhitresult.getType().equals(Type.MISS)) {
            return blockhitresult;
         }

         List<Entity> list = minecraftClient3.world
            .getOtherEntities(
               var3,
               new Box(vec3d, var1).expand(0.3),
               var1x -> var1x != var3.getOwner()
                  && var1x instanceof LivingEntity livingentity
                  && livingentity != minecraftClient3.player
                  && livingentity.isAlive()
            );
         if (!list.isEmpty()) {
            return new EntityHitResult(list.getFirst(), var1);
         }

         if (var1.y < -128.0) {
            break;
         }
      }

      return null;
   }

   public Vec3d on23(Entity var1, Vec3d var2, Vec3d var3) {
      boolean flag = Objects.requireNonNull(minecraftClient3.world)
         .getBlockState(BlockPos.ofFloored(var2))
         .getFluidState()
         .isIn(FluidTags.WATER);
      Objects.requireNonNull(var1);

      float f = switch (var1) {
         case TridentEntity tridententity -> 0.99F;
         case PersistentProjectileEntity persistentprojectileentity when flag -> 0.6F;
         default -> flag ? 0.8F : 0.99F;
      };
      return var3.multiply(f).add(0.0, -var1.getFinalGravity(), 0.0);
   }

   public void on23(Entity var1, Vec3d var2, int var3) {
      Objects.requireNonNull(var1);
      switch (var1) {
         case ItemEntity itementity:
            this.list70.add(new Predictions.Projectile(itementity.getStack(), var2, var3));
            break;
         case ThrownItemEntity thrownitementity:
            this.list70.add(new Predictions.Projectile(thrownitementity.getStack(), var2, var3));
            break;
         case PersistentProjectileEntity persistentprojectileentity:
            this.list70.add(new Predictions.Projectile(persistentprojectileentity.getItemStack(), var2, var3));
            break;
         default:
      }
   }

   public Direction on23(HitResult var1) {
      if (var1 instanceof BlockHitResult blockhitresult) {
         return blockhitresult.getSide();
      } else {
         Vec3d vec3d = var1.getPos().subtract(minecraftClient3.player.getEyePos()).normalize();
         return Direction.getFacing(vec3d.x, vec3d.y, vec3d.z);
      }
   }

   public boolean CloudUserProfile(Entity var1) {
      boolean flag = var1.getX() == var1.lastX && var1.getY() == var1.lastY && var1.getZ() == var1.lastZ;
      boolean flag1 = var1 instanceof ItemEntity && (var1.isOnGround() || EffectEngine.on23(var1.getBoundingBox().expand(2.0), Blocks.WATER));
      return flag || flag1;
   }


   public record Projectile(ItemStack itemStack10, Vec3d vec3d30, int int191) {
      public ItemStack double108() {
         return this.itemStack10;
      }

      public Vec3d VisualSettingsStore() {
         return this.vec3d30;
      }

      public int getTicks() {
         return this.int191;
      }
   }

   public record Simulation(int int190, World world, List<HitResult> list55) {
      public int call211() {
         return this.int190;
      }

      public World call212() {
         return this.world;
      }

      public List<HitResult> call176() {
         return this.list55;
      }
   }

   public static class Simulator {
      double x;
      double y;
      double z;
      double val095;
      double val060;
      double val096;
      int val140;
      long val320;

      Simulator(double var1, double var3, double var5, double var7, double var9, double var11, int var13, long var14) {
         this.x = var1;
         this.y = var3;
         this.z = var5;
         this.val095 = var7;
         this.val060 = var9;
         this.val096 = var11;
         this.val140 = var13;
         this.val320 = var14;
      }

      void update() {
         this.x = this.x + this.val095;
         this.y = this.y + this.val060;
         this.z = this.z + this.val096;
         this.val060 -= 0.002;
         this.val095 *= 0.98;
         this.val060 *= 0.98;
         this.val096 *= 0.98;
      }
   }
}
