package org.zenith.base.bot.view;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;

final class BotHeldItemRenderer {
   public static final RenderLayer MAP_BACKGROUND = RenderLayers.text(Identifier.ofVanilla("textures/map/map_background.png"));
   public static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayers.text(Identifier.ofVanilla("textures/map/map_background_checkerboard.png"));
   public final MapRenderState mapRenderState = new MapRenderState();
   public ItemStack mainHand = ItemStack.EMPTY;
   public ItemStack offHand = ItemStack.EMPTY;
   public float equipProgressMainHand;
   public float prevEquipProgressMainHand;
   public float equipProgressOffHand;
   public float prevEquipProgressOffHand;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public void renderFirstPersonItem(
      BotPlayer var1,
      BotPlayHandler var2,
      float var3,
      float var4,
      Hand var5,
      float var6,
      ItemStack var7,
      float var8,
      MatrixStack var9,
      OrderedRenderCommandQueue var10,
      int var11
   ) {
      if (!var1.isUsingSpyglass()) {
         boolean flag = var5 == Hand.MAIN_HAND;
         Arm arm = flag ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var9.push();
         if (var7.isEmpty()) {
            if (flag && !var1.isInvisible()) {
               this.renderArmHoldingItem(var1, var2, var9, var10, var11, var8, var6, arm);
            }
         } else if (var7.contains(DataComponentTypes.MAP_ID)) {
            if (flag && this.offHand.isEmpty()) {
               this.renderMapInBothHands(var1, var2, var9, var10, var11, var4, var8, var6);
            } else {
               this.renderMapInOneHand(var1, var2, var9, var10, var11, var8, arm, var6, var7);
            }
         } else if (var7.isOf(Items.CROSSBOW)) {
            boolean flag1 = CrossbowItem.isCharged(var7);
            boolean flag2 = arm == Arm.RIGHT;
            int i = flag2 ? 1 : -1;
            if (var1.isUsingItem() && var1.getItemUseTimeLeft() > 0 && var1.getActiveHand() == var5) {
               this.applyEquipOffset(var9, arm, var8);
               var9.translate(i * -0.4785682F, -0.094387F, 0.05731531F);
               var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
               var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 65.3F));
               var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * -9.785F));
               float f = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
               float f1 = f / CrossbowItem.getPullTime(var7, var1);
               if (f1 > 1.0F) {
                  f1 = 1.0F;
               }

               if (f1 > 0.1F) {
                  float f2 = MathHelper.sin((f - 0.1F) * 1.3F) * (f1 - 0.1F);
                  var9.translate(0.0F, f2 * 0.004F, 0.0F);
               }

               var9.translate(0.0F, 0.0F, f1 * 0.04F);
               var9.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
               var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i * 45.0F));
            } else {
               this.swingArm(var6, var8, var9, i, arm);
               if (flag1 && var6 < 0.001F && flag) {
                  var9.translate(i * -0.641864F, 0.0F, 0.0F);
                  var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 10.0F));
               }
            }

            this.renderItem(var1, var7, flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag2, var9, var10, var11);
         } else {
            boolean flag3 = arm == Arm.RIGHT;
            int j = flag3 ? 1 : -1;
            if (var1.isUsingItem() && var1.getItemUseTimeLeft() > 0 && var1.getActiveHand() == var5) {
               switch (var7.getUseAction()) {
                  case NONE:
                     this.applyEquipOffset(var9, arm, var8);
                     break;
                  case EAT:
                  case DRINK:
                     this.applyEatOrDrinkTransformation(var9, var3, arm, var7, var1);
                     this.applyEquipOffset(var9, arm, var8);
                     break;
                  case BLOCK:
                     this.applyEquipOffset(var9, arm, var8);
                     if (!(var7.getItem() instanceof ShieldItem)) {
                        var9.translate(j * -0.14142136F, 0.08F, 0.14142136F);
                        var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-102.25F));
                        var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 13.365F));
                        var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * 78.05F));
                     }
                     break;
                  case BOW:
                     this.applyEquipOffset(var9, arm, var8);
                     var9.translate(j * -0.2785682F, 0.18344387F, 0.15731531F);
                     var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                     var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 35.3F));
                     var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -9.785F));
                     float f4 = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
                     float f6 = f4 / 20.0F;
                     f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;
                     if (f6 > 1.0F) {
                        f6 = 1.0F;
                     }

                     if (f6 > 0.1F) {
                        float f8 = MathHelper.sin((f4 - 0.1F) * 1.3F) * (f6 - 0.1F);
                        var9.translate(0.0F, f8 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, f6 * 0.04F);
                     var9.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                     var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(j * 45.0F));
                     break;
                  case SPEAR:
                     this.applyEquipOffset(var9, arm, var8);
                     var9.translate(j * -0.5F, 0.7F, 0.1F);
                     var9.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                     var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 35.3F));
                     var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -9.785F));
                     float f3 = var7.getMaxUseTime(var1) - (var1.getItemUseTimeLeft() - var3 + 1.0F);
                     float f5 = Math.min(f3 / 10.0F, 1.0F);
                     if (f5 > 0.1F) {
                        float f7 = MathHelper.sin((f3 - 0.1F) * 1.3F) * (f5 - 0.1F);
                        var9.translate(0.0F, f7 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, f5 * 0.2F);
                     var9.scale(1.0F, 1.0F, 1.0F + f5 * 0.2F);
                     var9.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(j * 45.0F));
                     break;
                  case BRUSH:
                     this.applyBrushTransformation(var9, var3, arm, var1, var8);
                     break;
                  case BUNDLE:
                  default:
                     this.swingArm(var6, var8, var9, j, arm);
               }
            } else if (var1.isUsingRiptide()) {
               this.applyEquipOffset(var9, arm, var8);
               var9.translate(j * -0.4F, 0.8F, 0.3F);
               var9.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 65.0F));
               var9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -85.0F));
            } else {
               this.swingArm(var6, var8, var9, j, arm);
            }

            this.renderItem(var1, var7, flag3 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag3, var9, var10, var11);
         }

         var9.pop();
      }
   }

   void tick(BotPlayer var1) {
      this.prevEquipProgressMainHand = this.equipProgressMainHand;
      this.prevEquipProgressOffHand = this.equipProgressOffHand;
      ItemStack itemstack = var1.getMainHandStack();
      ItemStack itemstack1 = var1.getOffHandStack();
      if (this.shouldSkipHandAnimationOnSwap(this.mainHand, itemstack)) {
         this.mainHand = itemstack;
      }

      if (this.shouldSkipHandAnimationOnSwap(this.offHand, itemstack1)) {
         this.offHand = itemstack1;
      }

      if (var1.isRiding()) {
         this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float f = var1.getAttackCooldownProgress(1.0F);
         float f1 = this.mainHand != itemstack ? 0.0F : f * f * f;
         float f2 = this.offHand != itemstack1 ? 0.0F : 1.0F;
         this.equipProgressMainHand = this.equipProgressMainHand + MathHelper.clamp(f1 - this.equipProgressMainHand, -0.4F, 0.4F);
         this.equipProgressOffHand = this.equipProgressOffHand + MathHelper.clamp(f2 - this.equipProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equipProgressMainHand < 0.1F) {
         this.mainHand = itemstack;
      }

      if (this.equipProgressOffHand < 0.1F) {
         this.offHand = itemstack1;
      }
   }

   void snap(BotPlayer var1) {
      this.mainHand = var1.getMainHandStack();
      this.offHand = var1.getOffHandStack();
      this.equipProgressMainHand = this.prevEquipProgressMainHand = 1.0F;
      this.equipProgressOffHand = this.prevEquipProgressOffHand = 1.0F;
   }

   public boolean shouldSkipHandAnimationOnSwap(ItemStack var1, ItemStack var2) {
      return ItemStack.areEqual(var1, var2) || !minecraftClient3.getItemModelManager().hasHandAnimationOnSwap(var2);
   }

   void render(BotPlayer var1, BotPlayHandler var2, float var3, MatrixStack var4, OrderedRenderCommandQueue var5, int var6) {
      float f = var1.getHandSwingProgress(var3);
      Hand hand = var1.preferredHand != null ? var1.preferredHand : Hand.MAIN_HAND;
      float f1 = var1.getLerpedPitch(var3);
      BotHeldItemRenderer_HandRenderType bothelditemrenderer_handrendertype = getHandRenderType(var1);
      if (bothelditemrenderer_handrendertype.renderMainHand) {
         float f2 = hand == Hand.MAIN_HAND ? f : 0.0F;
         float f3 = 1.0F - MathHelper.lerp(var3, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(var1, var2, var3, f1, Hand.MAIN_HAND, f2, this.mainHand, f3, var4, var5, var6);
      }

      if (bothelditemrenderer_handrendertype.renderOffHand) {
         float f4 = hand == Hand.OFF_HAND ? f : 0.0F;
         float f5 = 1.0F - MathHelper.lerp(var3, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(var1, var2, var3, f1, Hand.OFF_HAND, f4, this.offHand, f5, var4, var5, var6);
      }

   }

   public static BotHeldItemRenderer_HandRenderType getHandRenderType(BotPlayer var0) {
      ItemStack itemstack = var0.getMainHandStack();
      ItemStack itemstack1 = var0.getOffHandStack();
      boolean flag = itemstack.isOf(Items.BOW) || itemstack1.isOf(Items.BOW);
      boolean flag1 = itemstack.isOf(Items.CROSSBOW) || itemstack1.isOf(Items.CROSSBOW);
      if (!flag && !flag1) {
         return BotHeldItemRenderer_HandRenderType.RENDER_BOTH_HANDS;
      } else if (var0.isUsingItem()) {
         return getUsingItemHandRenderType(var0);
      } else {
         return isChargedCrossbow(itemstack) ? BotHeldItemRenderer_HandRenderType.RENDER_MAIN_HAND_ONLY : BotHeldItemRenderer_HandRenderType.RENDER_BOTH_HANDS;
      }
   }

   public static BotHeldItemRenderer_HandRenderType getUsingItemHandRenderType(BotPlayer var0) {
      ItemStack itemstack = var0.getActiveItem();
      Hand hand = var0.getActiveHand();
      if (!itemstack.isOf(Items.BOW) && !itemstack.isOf(Items.CROSSBOW)) {
         return hand == Hand.MAIN_HAND && isChargedCrossbow(var0.getOffHandStack())
            ? BotHeldItemRenderer_HandRenderType.RENDER_MAIN_HAND_ONLY
            : BotHeldItemRenderer_HandRenderType.RENDER_BOTH_HANDS;
      } else {
         return BotHeldItemRenderer_HandRenderType.shouldOnlyRender(hand);
      }
   }

   public static boolean isChargedCrossbow(ItemStack var0) {
      return var0.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(var0);
   }

   public void renderItem(BotPlayer var1, ItemStack var2, ItemDisplayContext var3, boolean var4, MatrixStack var5, OrderedRenderCommandQueue var6, int var7) {
      if (!var2.isEmpty()) {
         ItemRenderState renderState = new ItemRenderState();
         minecraftClient3.getItemModelManager()
            .clearAndUpdate(renderState, var2, var3, var1.getWorld(), var1, var1.getId() + var3.ordinal());
         renderState.render(var5, var6, var7, OverlayTexture.DEFAULT_UV, 0);
      }
   }

   public void renderArmHoldingItem(BotPlayer var1, BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, float var7, Arm var8) {
      boolean flag = var8 != Arm.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.sqrt(var7);
      float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
      float f3 = 0.4F * MathHelper.sin(f1 * (float) (Math.PI * 2));
      float f4 = -0.4F * MathHelper.sin(var7 * (float) Math.PI);
      var3.translate(f * (f2 + 0.64000005F), f3 + -0.6F + var6 * -0.6F, f4 + -0.71999997F);
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 45.0F));
      float f5 = MathHelper.sin(var7 * var7 * (float) Math.PI);
      float f6 = MathHelper.sin(f1 * (float) Math.PI);
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * f6 * 70.0F));
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * f5 * -20.0F));
      var3.translate(f * -1.0F, 3.6F, 3.5F);
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 120.0F));
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(200.0F));
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * -135.0F));
      var3.translate(f * 5.6F, 0.0F, 0.0F);
      this.renderArm(var1, var2, var3, var4, var5, var8);
   }

   public void renderArm(BotPlayer var1, BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, Arm var6) {
      SkinTextures skintextures = BotPlayerRenderer.resolveSkin(var1, var2);
      PlayerEntityRenderer playerentityrenderer = BotPlayerRenderer.rendererFor(skintextures.model());
      if (playerentityrenderer != null) {
         Identifier identifier = skintextures.body().texturePath();
         if (var6 == Arm.RIGHT) {
              playerentityrenderer.renderRightArm(var3, var4, var5, identifier, true);
         } else {
              playerentityrenderer.renderLeftArm(var3, var4, var5, identifier, true);
         }
      }
   }

   public void renderMapInOneHand(
      BotPlayer var1, BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, Arm var7, float var8, ItemStack var9
   ) {
      float f = var7 == Arm.RIGHT ? 1.0F : -1.0F;
      var3.translate(f * 0.125F, -0.125F, 0.0F);
      if (!var1.isInvisible()) {
         var3.push();
         var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0F));
         this.renderArmHoldingItem(var1, var2, var3, var4, var5, var6, var8, var7);
         var3.pop();
      }

      var3.push();
      var3.translate(f * 0.51F, -0.08F + var6 * -1.2F, -0.75F);
      float f1 = MathHelper.sqrt(var8);
      float f2 = MathHelper.sin(f1 * (float) Math.PI);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.sin(f1 * (float) (Math.PI * 2));
      float f5 = -0.3F * MathHelper.sin(var8 * (float) Math.PI);
      var3.translate(f * f3, f4 - 0.3F * f2, f5);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * -45.0F));
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * f2 * -30.0F));
      this.renderFirstPersonMap(var1, var3, var4, var5, var9);
      var3.pop();
   }

   public void renderMapInBothHands(BotPlayer var1, BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, float var6, float var7, float var8) {
      float f = MathHelper.sqrt(var8);
      float f1 = -0.2F * MathHelper.sin(var8 * (float) Math.PI);
      float f2 = -0.4F * MathHelper.sin(f * (float) Math.PI);
      var3.translate(0.0F, -f1 / 2.0F, f2);
      float f3 = this.getMapAngle(var6);
      var3.translate(0.0F, 0.04F + var7 * -1.2F + f3 * -0.5F, -0.72F);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3 * -85.0F));
      if (!var1.isInvisible()) {
         var3.push();
         var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         this.renderPlainArm(var1, var2, var3, var4, var5, Arm.RIGHT);
         this.renderPlainArm(var1, var2, var3, var4, var5, Arm.LEFT);
         var3.pop();
      }

      float f4 = MathHelper.sin(f * (float) Math.PI);
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f4 * 20.0F));
      var3.scale(2.0F, 2.0F, 2.0F);
      this.renderFirstPersonMap(var1, var3, var4, var5, this.mainHand);
   }

   public void renderPlainArm(BotPlayer var1, BotPlayHandler var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, Arm var6) {
      var3.push();
      float f = var6 == Arm.RIGHT ? 1.0F : -1.0F;
      var3.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(92.0F));
      var3.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
      var3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * -41.0F));
      var3.translate(f * 0.3F, -1.1F, 0.45F);
      this.renderArm(var1, var2, var3, var4, var5, var6);
      var3.pop();
   }

   public void renderFirstPersonMap(BotPlayer var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, ItemStack var5) {
      var2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      var2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      var2.scale(0.38F, 0.38F, 0.38F);
      var2.translate(-0.5F, -0.5F, 0.0F);
      var2.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapIdComponent mapidcomponent = (MapIdComponent)var5.get(DataComponentTypes.MAP_ID);
      MapState mapstate = FilledMapItem.getMapState(mapidcomponent, var1.getWorld());
      RenderLayer background = mapstate == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD;
      var3.submitCustom(var2, background, (matrix, vertexconsumer) -> {
         vertexconsumer.vertex(matrix, -7.0F, 135.0F, 0.0F).color(-1).texture(0.0F, 1.0F).light(var4);
         vertexconsumer.vertex(matrix, 135.0F, 135.0F, 0.0F).color(-1).texture(1.0F, 1.0F).light(var4);
         vertexconsumer.vertex(matrix, 135.0F, -7.0F, 0.0F).color(-1).texture(1.0F, 0.0F).light(var4);
         vertexconsumer.vertex(matrix, -7.0F, -7.0F, 0.0F).color(-1).texture(0.0F, 0.0F).light(var4);
      });
      if (mapstate != null) {
         MapRenderer maprenderer = minecraftClient3.getMapRenderer();
         maprenderer.update(mapidcomponent, mapstate, this.mapRenderState);
         maprenderer.draw(this.mapRenderState, var2, var3, false, var4);
      }
   }

   public float getMapAngle(float var1) {
      float f = 1.0F - var1 / 45.0F + 0.1F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
   }

   public void applyEatOrDrinkTransformation(MatrixStack var1, float var2, Arm var3, ItemStack var4, BotPlayer var5) {
      float f = var5.getItemUseTimeLeft() - var2 + 1.0F;
      float f1 = f / var4.getMaxUseTime(var5);
      if (f1 < 0.8F) {
         float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
         var1.translate(0.0F, f2, 0.0F);
      }

      float f3 = 1.0F - (float)Math.pow(f1, 27.0);
      int i = var3 == Arm.RIGHT ? 1 : -1;
      var1.translate(f3 * 0.6F * i, f3 * -0.5F, 0.0F);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * f3 * 90.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3 * 10.0F));
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f3 * 30.0F));
   }

   public void applyBrushTransformation(MatrixStack var1, float var2, Arm var3, BotPlayer var4, float var5) {
      this.applyEquipOffset(var1, var3, var5);
      float f = var4.getItemUseTimeLeft() % 10;
      float f1 = f - var2 + 1.0F;
      float f2 = 1.0F - f1 / 10.0F;
      float f3 = -15.0F + 75.0F * MathHelper.cos(f2 * 2.0F * (float) Math.PI);
      if (var3 != Arm.RIGHT) {
         var1.translate(0.1, 0.83, 0.35);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3));
         var1.translate(-0.3, 0.22, 0.35);
      } else {
         var1.translate(-0.25, 0.22, 0.35);
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f3));
      }
   }

   public void applySwingOffset(MatrixStack var1, Arm var2, float var3) {
      int i = var2 == Arm.RIGHT ? 1 : -1;
      float f = MathHelper.sin(var3 * var3 * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + f * -20.0F)));
      float f1 = MathHelper.sin(MathHelper.sqrt(var3) * (float) Math.PI);
      var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f1 * -20.0F));
      var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f1 * -80.0F));
      var1.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
   }

   public void applyEquipOffset(MatrixStack var1, Arm var2, float var3) {
      int i = var2 == Arm.RIGHT ? 1 : -1;
      var1.translate(i * 0.56F, -0.52F + var3 * -0.6F, -0.72F);
   }

   public void swingArm(float var1, float var2, MatrixStack var3, int var4, Arm var5) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt(var1) * (float) Math.PI);
      float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(var1) * (float) (Math.PI * 2));
      float f2 = -0.2F * MathHelper.sin(var1 * (float) Math.PI);
      var3.translate(var4 * f, f1, f2);
      this.applyEquipOffset(var3, var5, var2);
      this.applySwingOffset(var3, var5, var1);
   }
}
