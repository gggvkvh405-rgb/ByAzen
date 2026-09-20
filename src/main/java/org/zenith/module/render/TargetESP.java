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
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;
import org.zenith.util.MathUtils;

@ModuleInfo(name = "TargetESP", category = Category.RENDER, description = "module.targetESP.description")
public class TargetESP extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TargetESP targetESP = new TargetESP();
   public static final Identifier identifier5 = Identifier.of("zenith", "visuals/targetesp/targetesp.png");
   public static final Identifier identifier6 = Identifier.of("zenith", "visuals/targetesp/targetesp-2.png");
   public static final int int308 = 3;
   public static final float float188 = (float) (Math.PI / 180.0);
   public static final int int309 = 8;
   public static final int int310 = 14;
   public static final int int311 = 18;
   public static final int int312 = 20;
   public static final long long133 = 100L;
   public static final float[] val456 = new float[]{0.0F, 0.38268343F, 0.70710677F, 0.9238795F, 1.0F, 0.9238795F, 0.70710677F, 0.38268343F, 1.2246469E-16F};
   public static final float[] val457 = new float[]{-1.0F, -0.9238795F, -0.70710677F, -0.38268343F, 0.0F, 0.38268343F, 0.707039F, 0.9238795F, 1.0F};
   public static final float[] val458 = new float[]{
      1.0F,
      0.9009748F,
      0.6235112F,
      0.22256099F,
      -0.22246753F,
      -0.6234363F,
      -0.9009332F,
      -1.0F,
      -0.9009748F,
      -0.6235112F,
      -0.22256099F,
      0.22246753F,
      0.6234363F,
      0.9009332F,
      1.0F
   };
   public static final float[] val459 = new float[]{
      0.0F,
      0.4338714F,
      0.7818144F,
      0.9749188F,
      0.9749401F,
      0.7818742F,
      0.4339578F,
      1.2246469E-16F,
      -0.4338714F,
      -0.7818144F,
      -0.9749188F,
      -0.9749401F,
      -0.7818742F,
      -0.4339578F,
      0.0F
   };
   public static final float[] val460 = new float[]{
      1.0F,
      0.93972176F,
      0.76609236F,
      0.5000554F,
      0.17370063F,
      -0.17360622F,
      -0.4999723F,
      -0.7660307F,
      -0.939689F,
      -1.0F,
      -0.93972176F,
      -0.76609236F,
      -0.5000554F,
      -0.17370063F,
      0.17360622F,
      0.4999723F,
      0.7660307F,
      0.939689F,
      1.0F
   };
   public static final float[] val461 = new float[]{
      0.0F,
      0.34194008F,
      0.6427305F,
      0.86599344F,
      0.9847985F,
      0.9848152F,
      0.86604136F,
      0.6428039F,
      0.34203017F,
      1.2246469E-16F,
      -0.34194008F,
      -0.6427305F,
      -0.86599344F,
      -0.9847985F,
      -0.9848152F,
      -0.86604136F,
      -0.6428039F,
      -0.34203017F,
      0.0F
   };
   public static final float[] val516 = new float[]{
      1.0F,
      0.9510802F,
      0.8090508F,
      0.5878163F,
      0.30903524F,
      1.2246469E-16F,
      -0.30894405F,
      -0.5877387F,
      -0.8089945F,
      -0.9510506F,
      -1.0F,
      -0.9510802F,
      -0.8090508F,
      -0.5878163F,
      -0.30903524F,
      0.0F,
      0.30894405F,
      0.5877387F,
      0.8089945F,
      0.9510506F
   };
   public static final float[] val517 = new float[]{
      0.0F,
      0.30894405F,
      0.5877387F,
      0.8089945F,
      0.9510506F,
      1.0F,
      0.9510802F,
      0.8090508F,
      0.5878163F,
      0.30903524F,
      1.2246469E-16F,
      -0.30894405F,
      -0.5877387F,
      -0.8089945F,
      -0.9510506F,
      -1.0F,
      -0.9510802F,
      -0.8090508F,
      -0.5878163F,
      -0.30903524F
   };
   public final ModeSetting mode14 = new ModeSetting("module.targetESP.mode", "module.targetESP.mode.desc");
   public final ModeSetting.Option modeSetting3Var15941 = new ModeSetting.Option(this.mode14, "module.targetESP.texture.vortex").int210();
   public final ModeSetting.Option modeSetting3Var15942 = new ModeSetting.Option(this.mode14, "module.targetESP.texture.garland");
   public final ModeSetting.Option modeSetting3Var15943 = new ModeSetting.Option(this.mode14, "module.targetESP.texture.brackets");
   public final ModeSetting.Option modeSetting3Var15944 = new ModeSetting.Option(this.mode14, "module.targetESP.ghosts");
   public final ModeSetting.Option modeSetting3Var15945 = new ModeSetting.Option(this.mode14, "module.targetESP.circle");
   public final ModeSetting.Option modeSetting3Var15946 = new ModeSetting.Option(this.mode14, "module.targetESP.default");
   public final ModeSetting.Option modeSetting3Var15947 = new ModeSetting.Option(this.mode14, "Cube-Circle");
   public final ModeSetting.Option modeSetting3Var15948 = new ModeSetting.Option(this.mode14, "Cube");
   public final NumberSetting size4 = new NumberSetting(
      "module.targetESP.size",
      1.2F,
      0.3F,
      3.0F,
      0.05F,
      "module.targetESP.size.desc",
      "x",
      () -> this.modeSetting3Var15941.isSelected() || this.modeSetting3Var15942.isSelected() || this.modeSetting3Var15943.isSelected()
   );
   public final NumberSetting speed7 = new NumberSetting("module.targetESP.speed", 1.0F, 0.0F, 4.0F, 0.05F, "module.targetESP.speed.desc", "x");
   public final NumberSetting scale3 = new NumberSetting(
      "module.targetESP.scale",
      0.4F,
      0.1F,
      1.5F,
      0.1F,
      "module.targetESP.scale.desc",
      "x",
      () -> this.modeSetting3Var15944.isSelected()
         || this.modeSetting3Var15946.isSelected()
         || this.modeSetting3Var15947.isSelected()
         || this.modeSetting3Var15948.isSelected()
   );
   public final NumberSetting length2 = new NumberSetting(
      "module.targetESP.length", 4.0F, 1.0F, 10.0F, 1.0F, "module.targetESP.length.desc", "", this.modeSetting3Var15944::isSelected
   );
   public final BooleanSetting red = new BooleanSetting("module.targetESP.red", "module.targetESP.red.description", true);
   public final BooleanSetting showOnHover = new BooleanSetting("module.targetESP.showOnHover", "module.targetESP.showOnHover.description", false);
   public final UiAnimation var14315 = new UiAnimation(250L, Easing.StopUsingItemEvent);
   public final Random random4 = new Random();
   public final float[] val077 = new float[3];
   public final float[] val462 = new float[3];
   public final float[] val463 = new float[3];
   public final float[] val464 = new float[3];
   public final float[] val329 = new float[3];
   public final float[] val208 = new float[3];
   public final float[] val209 = new float[3];
   public final int[] val157 = new int[3];
   public final int[] val210 = new int[3];
   public final float[] val465 = new float[3];
   public final float[] val466 = new float[3];
   public final float[] val467 = new float[3];
   public final float[] val468 = new float[3];
   public final float[][] val330 = new float[3][20];
   public final float[][] val331 = new float[3][20];
   public final float[][] val332 = new float[3][20];
   public final float[][] val469 = new float[3][20];
   public final float[][] val470 = new float[3][20];
   public final float[][] val471 = new float[3][20];
   public final int[][] val472 = new int[3][20];
   public final int[][] val473 = new int[3][20];
   public final int[][] val474 = new int[3][20];
   public final Matrix4f matrix4f10 = new Matrix4f();
   public final long long134 = System.currentTimeMillis();
   public LivingEntity livingEntity;
   public long long135;
   public boolean boolean144;

   public TargetESP() {
      this.box7();
   }

   public void on23(Matrix4f var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, float var12, boolean var13) {
      org.zenith.render.LegacyRenderBridge.enableBlend();
      this.BotChatEvent(var13);
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      float f = var8 * 0.34F;
      float f1 = MathHelper.clamp(var8 * 0.025F, 0.045F, 0.085F);
      this.on23((float)var2, (float)var4, (float)var6, f, f1, var10, var9);
      org.zenith.render.LegacyRenderBridge.usePositionColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

      for (int i = 0; i < 3; i++) {
         this.on23(var1, bufferbuilder, i, var11, var12);
      }

      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      org.zenith.render.LegacyRenderBridge.depthMask(var13);
      org.zenith.render.LegacyRenderBridge.enableCull();
      bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

      for (int j = 0; j < 3; j++) {
         this.on23(var1, bufferbuilder, j);
      }

      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
   }

   public void StringCodec(EventHookWorldRender var1) {
      MatrixStack matrixstack = var1.ClanUpgrade();
      float f = this.red.isEnabled() ? MathHelper.clamp(this.livingEntity.hurtTime / 10.0F, 0.0F, 1.0F) : 0.0F;
      if (this.modeSetting3Var15944.isSelected()) {
         WorldRender.on23(
            var1,
            this.var14315.CancellableEvent(),
            f,
            (int)(this.length2.getCurrent() * 50.0F),
            20,
            2.0F,
            4.0F,
            this.scale3.getCurrent(),
            this.speed7.getCurrent(),
            this.livingEntity
         );
      } else if (this.modeSetting3Var15945.isSelected()) {
         WorldRender.on23(matrixstack, this.livingEntity, this.var14315, f);
      } else if (!this.modeSetting3Var15946.isSelected() && !this.modeSetting3Var15947.isSelected() && !this.modeSetting3Var15948.isSelected()) {
         Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
         Vec3d vec3d = MathUtils.CloudResponse(this.livingEntity);
         Vec3d vec3d1 = camera.getCameraPos();
         Box box = this.livingEntity.getBoundingBox();
         double d0 = vec3d.x - vec3d1.x;
         double d1 = vec3d.y - vec3d1.y;
         double d2 = vec3d.z - vec3d1.z;
         float f1 = (float)box.getLengthY();
         float f2 = this.speed7.getCurrent();
         float f3 = (float)Math.max(box.getLengthX(), box.getLengthZ()) * 5.0F * this.size4.getCurrent();
         float f4 = f3 * 0.5F;
         float f5 = (float)(System.currentTimeMillis() - this.long134) / 1000.0F;
         float f6 = f5 * f2;
         ArgbColor i11ii1llliilllii1i1 = ZenithClient.on23().TextScanner().getClientColor(90);
         if (f > 0.0F) {
            i11ii1llliilllii1i1 = i11ii1llliilllii1i1.Easing(ArgbColor.var11937, f);
         }

         int i = ColorUtils.ColorAnimator(i11ii1llliilllii1i1.call001(), this.var14315.CancellableEvent());
         float f7 = f1 * 0.62F - 0.006F;
         float f8 = camera.getYaw() * (float) (Math.PI / 180.0);
         float f9 = camera.getPitch() * (float) (Math.PI / 180.0);
         boolean flag = this.vec3d32();

         for (int j = 0; j < 3; j++) {
            float f10 = (MathHelper.sin(f6 * 3.4F + j * (float) (Math.PI * 2.0 / 3.0)) + 1.0F) * 0.5F;
            this.val329[j] = MathHelper.lerp(f10, 0.35F, 1.0F);
            this.val157[j] = ColorUtils.ColorAnimator(i, this.val329[j]);
            this.val210[j] = ColorUtils.ColorAnimator(i, this.val329[j] * 0.85F);
            this.val208[j] = -f6 * 140.0F * this.val464[j] + this.val077[j];
            this.val209[j] = MathHelper.sin(f6 * this.val463[j] + this.val462[j]) * 30.0F;
            float f11 = (90.0F + this.val208[j]) * (float) (Math.PI / 180.0);
            float f12 = this.val209[j] * (float) (Math.PI / 180.0);
            this.val465[j] = MathHelper.cos(f11);
            this.val466[j] = MathHelper.sin(f11);
            this.val467[j] = MathHelper.cos(f12);
            this.val468[j] = MathHelper.sin(f12);
         }

         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         if (this.modeSetting3Var15942.isSelected()) {
            this.on23(matrix4f, d0, d1, d2, f3, f6, f7, f8, f9, flag);
         } else {
            org.zenith.render.LegacyRenderBridge.enableBlend();
            this.BotChatEvent(flag);
            org.zenith.render.LegacyRenderBridge.depthMask(false);
            org.zenith.render.LegacyRenderBridge.disableCull();
            Identifier identifier1 = this.call108();
            float f13 = f7;
            org.zenith.render.LegacyRenderBridge.usePositionTexColor();
            org.zenith.render.LegacyRenderBridge.setTexture(0, identifier1);
            org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
            BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            for (int k = 0; k < 3; f13 += 0.006F) {
               this.on23(matrix4f, bufferbuilder, d0, d1, d2, f4, f13, this.val208[k], this.val209[k], this.val157[k]);
               k++;
            }

            org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
            int i1 = 0;

            for (int l = 0; l < 3; l++) {
               i1 = Math.max(i1, this.val210[l] >>> 24);
            }

            if (i1 > 0) {
               org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
               bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               f13 = f7;

               for (int j1 = 0; j1 < 3; f13 += 0.006F) {
                  if (this.val210[j1] >>> 24 != 0) {
                     this.on23(matrix4f, bufferbuilder, d0, d1, d2, f4, f13, this.val208[j1], this.val209[j1], this.val210[j1]);
                  }

                  j1++;
               }

               org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
            }

            org.zenith.render.LegacyRenderBridge.depthMask(true);
            org.zenith.render.LegacyRenderBridge.enableDepthTest();
            org.zenith.render.LegacyRenderBridge.enableCull();
            org.zenith.render.LegacyRenderBridge.disableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         }
      } else {
         Identifier identifier = this.modeSetting3Var15946.isSelected()
            ? WorldRender.identifier11
            : (this.modeSetting3Var15947.isSelected() ? WorldRender.identifier12 : WorldRender.identifier13);
         WorldRender.on23(this.livingEntity, this.scale3.getCurrent(), this.var14315.CancellableEvent(), f, identifier);
      }
   }

   public void on23(float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float f = var6;

      for (int i = 0; i < 3; f += 0.006F) {
         float f1 = this.val465[i];
         float f2 = this.val466[i];
         float f3 = this.val467[i];
         float f4 = this.val468[i];
         float f5 = var7 * 4.0F + i;

         for (int j = 0; j < 20; j++) {
            float f6 = val516[j] * var4;
            float f7 = val517[j] * var4;
            float f8 = (MathHelper.sin(f5 + j * 0.9F) + 1.0F) * 0.5F;
            float f9 = var5 * (0.9F + f8 * 0.28F);
            float f10 = f6 * f1 + f7 * f3 * f2;
            float f11 = -f7 * f4;
            float f12 = -f6 * f2 + f7 * f3 * f1;
            this.val330[i][j] = var1 + f10;
            this.val331[i][j] = var2 + f + f11;
            this.val332[i][j] = var3 + f12;
            this.val469[i][j] = f9;
            this.val470[i][j] = f9 * (3.6F + f8 * 0.7F);
            this.val471[i][j] = f9 * (1.9F + f8 * 0.3F);
            this.val472[i][j] = ColorUtils.ColorAnimator(this.val157[i], 0.78F + f8 * 0.22F);
            this.val473[i][j] = ColorUtils.ColorAnimator(this.val157[i], 0.105F + f8 * 0.035F);
            this.val474[i][j] = ColorUtils.ColorAnimator(this.val157[i], 0.18F + f8 * 0.055F);
         }

         i++;
      }
   }

   public void on23(Matrix4f var1, BufferBuilder var2, float var3, float var4, float var5, float var6, int var7) {
      this.matrix4f10.set(var1).translate(var3, var4, var5);
      float f = var3 * var3 + var4 * var4 + var5 * var5;
      int i = f > 144.0F ? 4 : (f > 64.0F ? 2 : 1);
      int j = f > 144.0F ? 4 : (f > 64.0F ? 2 : 1);

      for (int k = 0; k < 8; k += i) {
         int l = Math.min(k + i, 8);
         float f1 = val457[k] * var6;
         float f2 = val456[k] * var6;
         float f3 = val457[l] * var6;
         float f4 = val456[l] * var6;

         for (int i1 = 0; i1 < 14; i1 += j) {
            int j1 = Math.min(i1 + j, 14);
            float f5 = val458[i1];
            float f6 = val459[i1];
            float f7 = val458[j1];
            float f8 = val459[j1];
            var2.vertex(this.matrix4f10, f5 * f2, f1, f6 * f2).color(var7);
            var2.vertex(this.matrix4f10, f5 * f4, f3, f6 * f4).color(var7);
            var2.vertex(this.matrix4f10, f7 * f4, f3, f8 * f4).color(var7);
            var2.vertex(this.matrix4f10, f7 * f2, f1, f8 * f2).color(var7);
         }
      }
   }

   public void box7() {
      for (int i = 0; i < 3; i++) {
         this.val464[i] = 0.75F + this.random4.nextFloat() * 0.5F;
         this.val463[i] = 0.8F + this.random4.nextFloat() * 0.8F;
         this.val462[i] = this.random4.nextFloat() * (float) (Math.PI * 2);
      }

      for (int i1 = 0; i1 < 64; i1++) {
         for (int j = 0; j < 3; j++) {
            this.val077[j] = -30.0F + this.random4.nextFloat() * 60.0F;
         }

         boolean flag = true;

         for (int k = 0; k < 3 && flag; k++) {
            for (int l = k + 1; l < 3 && flag; l++) {
               if (Math.abs(this.val077[k] - this.val077[l]) < 22.0F) {
                  flag = false;
               }
            }
         }

         if (flag) {
            return;
         }
      }

      this.val077[0] = -30.0F + this.random4.nextFloat() * 4.0F;
      this.val077[1] = -2.0F + this.random4.nextFloat() * 4.0F;
      this.val077[2] = 30.0F - this.random4.nextFloat() * 4.0F;
   }

   @EventTarget
   public void ProfileItemBuilder(EventHookWorldRender var1) {
      LivingEntity livingentity;
      if (this.showOnHover.isEnabled()) {
         livingentity = minecraftClient3.targetedEntity instanceof LivingEntity var5 ? var5 : null;
      } else {
         livingentity = Aura.aura.zClass054() == null ? AimAssist.aimAssist.zClass054() : Aura.aura.zClass054();
      }

      if (livingentity != null && (!livingentity.isAlive() || livingentity.isRemoved())) {
         livingentity = null;
      }

      this.on23(livingentity);
      if (minecraftClient3.player != null && minecraftClient3.world != null && this.livingEntity != null && !(this.var14315.CancellableEvent() <= 0.01F)) {
         this.StringCodec(var1);
      }
   }

   @EventTarget
   public void NbtEditor(EventTick var1) {
      WorldRender.EventGetBasicProjectionMatrixHook(this.speed7.getCurrent());
   }

   public void on23(Matrix4f var1, BufferBuilder var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12, int var13) {
      this.matrix4f10
         .set(var1)
         .translate((float)var3, (float)(var5 + var10), (float)var7)
         .rotateY((90.0F + var11) * (float) (Math.PI / 180.0))
         .rotateX(var12 * (float) (Math.PI / 180.0));
      var2.vertex(this.matrix4f10, -var9, 0.0F, -var9).texture(0.0F, 0.0F).color(var13);
      var2.vertex(this.matrix4f10, -var9, 0.0F, var9).texture(0.0F, 1.0F).color(var13);
      var2.vertex(this.matrix4f10, var9, 0.0F, var9).texture(1.0F, 1.0F).color(var13);
      var2.vertex(this.matrix4f10, var9, 0.0F, -var9).texture(1.0F, 0.0F).color(var13);
   }

   public void on23(Matrix4f var1, BufferBuilder var2, int var3) {
      float[] afloat = this.val330[var3];
      float[] afloat1 = this.val331[var3];
      float[] afloat2 = this.val332[var3];
      float[] afloat3 = this.val469[var3];
      int[] aint = this.val472[var3];

      for (int i = 0; i < 20; i++) {
         int j = aint[i];
         if (j >>> 24 != 0) {
            this.on23(var1, var2, afloat[i], afloat1[i], afloat2[i], afloat3[i], j);
         }
      }
   }

   public void on23(Matrix4f var1, BufferBuilder var2, int var3, float var4, float var5) {
      float[] afloat = this.val330[var3];
      float[] afloat1 = this.val331[var3];
      float[] afloat2 = this.val332[var3];
      float[] afloat3 = this.val470[var3];
      float[] afloat4 = this.val471[var3];
      int[] aint = this.val473[var3];
      int[] aint1 = this.val474[var3];

      for (int i = 0; i < 20; i++) {
         int j = aint[i];
         int k = aint1[i];
         if (j >>> 24 != 0 || k >>> 24 != 0) {
            this.matrix4f10.set(var1).translate(afloat[i], afloat1[i], afloat2[i]).rotateY(-var4).rotateX(var5);
            if (j >>> 24 != 0) {
               this.on23(var2, afloat3[i], j);
            }

            if (k >>> 24 != 0) {
               this.on23(var2, afloat4[i], k);
            }
         }
      }
   }

   public void on23(BufferBuilder var1, float var2, int var3) {
      byte b0 = 0;

      for (int i = 0; i < 18; i++) {
         float f = val460[i];
         float f1 = val461[i];
         float f2 = val460[i + 1];
         float f3 = val461[i + 1];
         var1.vertex(this.matrix4f10, 0.0F, 0.0F, 0.0F).color(var3);
         var1.vertex(this.matrix4f10, f * var2, f1 * var2, 0.0F).color(b0);
         var1.vertex(this.matrix4f10, f2 * var2, f3 * var2, 0.0F).color(b0);
      }
   }

   public Identifier call108() {
      if (this.modeSetting3Var15943.isSelected()) {
         return identifier6;
      } else {
         return this.modeSetting3Var15941.isSelected() ? identifier5 : identifier5;
      }
   }

   public boolean vec3d32() {
      if (minecraftClient3.player != null && this.livingEntity != null) {
         long i = System.currentTimeMillis();
         if (i - this.long135 >= 100L) {
            this.boolean144 = minecraftClient3.player.canSee(this.livingEntity);
            this.long135 = i;
         }

         return this.boolean144;
      } else {
         return false;
      }
   }

   public void BotChatEvent(boolean var1) {
      if (var1) {
         org.zenith.render.LegacyRenderBridge.enableDepthTest();
      } else {
         org.zenith.render.LegacyRenderBridge.disableDepthTest();
      }
   }

   public void on23(LivingEntity var1) {
      if (var1 == null) {
         this.var14315.on23(0.0F);
         if (this.var14315.CancellableEvent() <= 0.01F) {
            this.livingEntity = null;
            this.long135 = 0L;
         }
      } else if (var1 != this.livingEntity) {
         this.var14315.on23(0.0F);
         if (this.var14315.CancellableEvent() <= 0.01F) {
            this.livingEntity = var1;
            this.long135 = 0L;
            this.box7();
         }
      } else {
         this.var14315.on23(1.0F);
      }
   }
}
