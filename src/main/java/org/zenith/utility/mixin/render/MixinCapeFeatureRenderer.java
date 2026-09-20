package org.zenith.utility.mixin.render;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.ZenithClient;
import org.zenith.core.ClickFxController;
import org.zenith.core.ClickFxPoint;
import org.zenith.core.ClickFxState;
import org.zenith.core.TranslationKey;
import org.zenith.module.render.Cape;

@Mixin(CapeFeatureRenderer.class)
public class MixinCapeFeatureRenderer {
   @Unique
   public final int PART_COUNT = ClickFxController.string69();

   @Inject(
      method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V",
      at = @At("HEAD"),
      cancellable = true
   )
   public void onRender(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, PlayerEntityRenderState var4, float var5, float var6, CallbackInfo var7) {
      try {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (!Cape.cape.isEnabled() || minecraftclient.player == null) {
            return;
         }

         if (minecraftclient.player.getId() == var4.id
            || ZenithClient.on23().MediaTrackInfo().isFriend(var4.playerName == null ? "" : var4.playerName.getString())) {
            var7.cancel();
            ClickFxController l111lliil1ilill1l1i11li = TranslationKey.GuiWalkEvent(var4.id);
            if (l111lliil1ilill1l1i11li == null) {
               return;
            }

            if (l111lliil1ilill1l1i11li.int330().size() < 2) {
               return;
            }

            if (minecraftclient.getResourceManager()
               .getResource(Identifier.of("zenith", "capes/cape" + Cape.cape.animationMode.getIndex() + ".png"))
               .isEmpty()) {
               return;
            }

            float f = minecraftclient.getRenderTickCounter().getTickProgress(true);
            RenderLayer renderLayer = RenderLayers.entityTranslucent(
               Identifier.of("zenith", "capes/cape" + Cape.cape.animationMode.getIndex() + ".png")
            );
            var2.submitCustom(var1, renderLayer, (matrixEntry, vertexConsumer) -> {
               MatrixStack capeMatrices = new MatrixStack();
               capeMatrices.multiplyPositionMatrix(matrixEntry.getPositionMatrix());
               this.renderSimulationCape(capeMatrices, vertexConsumer, var3, var4, l111lliil1ilill1l1i11li, f);
            });
         }
      } catch (Exception exception) {
         System.out.println("EBALLL Cape ");
         exception.printStackTrace();
      }
   }

   @Unique
   public void renderSimulationCape(MatrixStack var1, VertexConsumer vertexconsumer, int var3, PlayerEntityRenderState var4, ClickFxController var5, float var6) {
      List<ClickFxState> list = var5.int330();
      if (list.size() >= 2) {
         Matrix4f matrix4f = null;

         for (int i = 0; i < this.PART_COUNT; i++) {
            this.modifyPoseStackSimulation(var1, var4, i, var5, var6);
            Matrix4f matrix4f1 = var1.peek().getPositionMatrix();
            if (matrix4f == null) {
               matrix4f = new Matrix4f(matrix4f1);
            }

            if (i == 0) {
               this.addTopVertex(vertexconsumer, matrix4f1, matrix4f, 0.3F, 0.0F, 0.0F, -0.3F, 0.0F, -0.06F, i, var3);
            }

            if (i == this.PART_COUNT - 1) {
               this.addBottomVertex(
                  vertexconsumer,
                  matrix4f1,
                  matrix4f1,
                  0.3F,
                  (i + 1) * (0.96F / this.PART_COUNT),
                  0.0F,
                  -0.3F,
                  (i + 1) * (0.96F / this.PART_COUNT),
                  -0.06F,
                  i,
                  var3
               );
            }

            this.addLeftVertex(
               vertexconsumer, matrix4f1, matrix4f, -0.3F, (i + 1) * (0.96F / this.PART_COUNT), 0.0F, -0.3F, i * (0.96F / this.PART_COUNT), -0.06F, i, var3
            );
            this.addRightVertex(
               vertexconsumer, matrix4f1, matrix4f, 0.3F, (i + 1) * (0.96F / this.PART_COUNT), 0.0F, 0.3F, i * (0.96F / this.PART_COUNT), -0.06F, i, var3
            );
            this.addBackVertex(
               vertexconsumer, matrix4f1, matrix4f, 0.3F, (i + 1) * (0.96F / this.PART_COUNT), -0.06F, -0.3F, i * (0.96F / this.PART_COUNT), -0.06F, i, var3
            );
            this.addFrontVertex(
               vertexconsumer, matrix4f, matrix4f1, 0.3F, (i + 1) * (0.96F / this.PART_COUNT), 0.0F, -0.3F, i * (0.96F / this.PART_COUNT), 0.0F, i, var3
            );
            matrix4f = new Matrix4f(matrix4f1);
            var1.pop();
         }
      }
   }

   @Unique
   public void modifyPoseStackSimulation(MatrixStack var1, PlayerEntityRenderState var2, int var3, ClickFxController var4, float var5) {
      var1.push();
      var1.translate(0.0, 0.0, 0.125);
      ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = var4.int330().get(0);
      l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = var4.int330().get(var3);
      float f = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventInjectHandleInputEvents(var5)
         - l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventInjectHandleInputEvents(var5);
      if (f > 0.0F) {
         f = 0.0F;
      }

      float f1 = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventMouseButton(var5) - var3 - l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventMouseButton(var5);
      float f2 = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventModifyMouseRotationInput(var5)
         - l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.EventModifyMouseRotationInput(var5);
      float f3 = this.getRotation(var5, var3, var4);
      float f4 = 0.0F;
      if (var2.isInSneakingPose) {
         f4 += 25.0F;
         var1.translate(0.0, 0.15, 0.0);
      }

      float f5 = this.getNaturalWindSwing(var3, false) * Cape.cape.boolean105();
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F + f4 + f5));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      var1.translate(-f2 / this.PART_COUNT, f1 / this.PART_COUNT, f / this.PART_COUNT);
      var1.translate(0.0, 0.03, -0.03);
      var1.translate(0.0, var3 * 1.0F / this.PART_COUNT, 0.0);
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-f3));
      var1.translate(0.0, -var3 * 1.0F / this.PART_COUNT, 0.0);
      var1.translate(0.0, -0.03, 0.03);
   }

   @Unique
   public float getRotation(float var1, int var2, ClickFxController var3) {
      if (var2 == this.PART_COUNT - 1) {
         return this.getRotation(var1, var2 - 1, var3);
      }

      ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1xx = var3.list70.get(var2).EventMixin_modifySetScreenArg(var1);
      ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1x = var3.list70.get(var2 + 1).EventMixin_modifySetScreenArg(var1);
      l111lliil1ilill1l1i11li_illi1l1l1xx = l111lliil1ilill1l1i11li_illi1l1l1x.ColorAnimator(l111lliil1ilill1l1i11li_illi1l1l1xx);
      return (float)(Math.toDegrees(Math.atan2(l111lliil1ilill1l1i11li_illi1l1l1xx.x, l111lliil1ilill1l1i11li_illi1l1l1xx.y)) + 180.0);
   }

   @Unique
   public float getNaturalWindSwing(int var1, boolean var2) {
      long i = System.currentTimeMillis() / (var2 ? 9 : 3) % 360L;
      float f = (float)(var1 + 1) / this.PART_COUNT;
      return (float)(Math.sin(Math.toRadians(f * 360.0F - (float)i)) * 3.0);
   }

   @Unique
   public void addBackVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.015625F;
      float f1 = 0.171875F;
      float f2 = 0.03125F;
      float f3 = 0.53125F;
      float f4 = f3 - f2;
      float f5 = f4 / this.PART_COUNT;
      float f6 = f2 + f5 * (var10 + 1);
      float f7 = f2 + f5 * var10;
      if (var4 < var7) {
         float f8 = var4;
         var4 = var7;
         var7 = f8;
      }

      if (var5 < var8) {
         float f9 = var5;
         var5 = var8;
         var8 = f9;
         Matrix4f matrix4f = var2;
         var2 = var3;
         var3 = matrix4f;
      }

      var1.vertex(var3, var4, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, -1.0F);
      var1.vertex(var3, var7, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, -1.0F);
      var1.vertex(var2, var7, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, -1.0F);
      var1.vertex(var2, var4, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f1, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, -1.0F);
   }

   @Unique
   public void addFrontVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.1875F;
      float f1 = 0.34375F;
      float f2 = 0.03125F;
      float f3 = 0.53125F;
      float f4 = f3 - f2;
      float f5 = f4 / this.PART_COUNT;
      float f6 = f2 + f5 * (var10 + 1);
      float f7 = f2 + f5 * var10;
      if (var4 < var7) {
         float f8 = var4;
         var4 = var7;
         var7 = f8;
      }

      if (var5 < var8) {
         float f9 = var5;
         var5 = var8;
         var8 = f9;
         Matrix4f matrix4f = var2;
         var2 = var3;
         var3 = matrix4f;
      }

      var1.vertex(var3, var4, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var3, var7, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var2, var7, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, 1.0F);
      var1.vertex(var2, var4, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f1, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 0.0F, 1.0F);
   }

   @Unique
   public void addLeftVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.0F;
      float f1 = 0.015625F;
      float f2 = 0.03125F;
      float f3 = 0.53125F;
      float f4 = f3 - f2;
      float f5 = f4 / this.PART_COUNT;
      float f6 = f2 + f5 * (var10 + 1);
      float f7 = f2 + f5 * var10;
      if (var4 < var7) {
         var7 = var4;
      }

      if (var5 < var8) {
         float f8 = var5;
         var5 = var8;
         var8 = f8;
      }

      var1.vertex(var2, var7, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(-1.0F, 0.0F, 0.0F);
      var1.vertex(var2, var7, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(-1.0F, 0.0F, 0.0F);
      var1.vertex(var3, var7, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(-1.0F, 0.0F, 0.0F);
      var1.vertex(var3, var7, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(-1.0F, 0.0F, 0.0F);
   }

   @Unique
   public void addRightVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.171875F;
      float f1 = 0.1875F;
      float f2 = 0.03125F;
      float f3 = 0.53125F;
      float f4 = f3 - f2;
      float f5 = f4 / this.PART_COUNT;
      float f6 = f2 + f5 * (var10 + 1);
      float f7 = f2 + f5 * var10;
      if (var4 < var7) {
         var7 = var4;
      }

      if (var5 < var8) {
         float f8 = var5;
         var5 = var8;
         var8 = f8;
      }

      var1.vertex(var2, var7, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(1.0F, 0.0F, 0.0F);
      var1.vertex(var2, var7, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f6)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(1.0F, 0.0F, 0.0F);
      var1.vertex(var3, var7, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(1.0F, 0.0F, 0.0F);
      var1.vertex(var3, var7, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f7)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(1.0F, 0.0F, 0.0F);
   }

   @Unique
   public void addTopVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.015625F;
      float f1 = 0.171875F;
      float f2 = 0.0F;
      float f3 = 0.03125F;
      if (var4 < var7) {
         float f4 = var4;
         var4 = var7;
         var7 = f4;
      }

      if (var5 < var8) {
         float f5 = var5;
         var5 = var8;
         var8 = f5;
      }

      var1.vertex(var3, var4, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f3)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 1.0F, 0.0F);
      var1.vertex(var3, var7, var8, var6)
         .color(short1, short2, short3, short4)
         .texture(f, f3)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 1.0F, 0.0F);
      var1.vertex(var2, var7, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f2)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 1.0F, 0.0F);
      var1.vertex(var2, var4, var5, var9)
         .color(short1, short2, short3, short4)
         .texture(f1, f2)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, 1.0F, 0.0F);
   }

   @Unique
   public void addBottomVertex(
      VertexConsumer var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11
   ) {
      short short1 = 255;
      short short2 = 255;
      short short3 = 255;
      short short4 = 255;
      float f = 0.171875F;
      float f1 = 0.328125F;
      float f2 = 0.0F;
      float f3 = 0.03125F;
      if (var4 < var7) {
         float f4 = var4;
         var4 = var7;
         var7 = f4;
      }

      if (var5 < var8) {
         float f5 = var5;
         var5 = var8;
         var8 = f5;
      }

      var1.vertex(var3, var4, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f1, f2)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, -1.0F, 0.0F);
      var1.vertex(var3, var7, var8, var9)
         .color(short1, short2, short3, short4)
         .texture(f, f2)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, -1.0F, 0.0F);
      var1.vertex(var2, var7, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f, f3)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, -1.0F, 0.0F);
      var1.vertex(var2, var4, var5, var6)
         .color(short1, short2, short3, short4)
         .texture(f1, f3)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(var11)
         .normal(0.0F, -1.0F, 0.0F);
   }
}
