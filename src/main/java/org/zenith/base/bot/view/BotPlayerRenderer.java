package org.zenith.base.bot.view;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.mixin.accessors.EntityRenderManagerAccessor;

final class BotPlayerRenderer {
   public final PlayerEntityRenderState state = new PlayerEntityRenderState();

   static PlayerEntityRenderer rendererFor(PlayerSkinType var0) {
      EntityRenderManagerAccessor entityrenderdispatcheraccessor = (EntityRenderManagerAccessor)MinecraftClient.getInstance().getEntityRenderDispatcher();
      EntityRenderer entityrenderer = entityrenderdispatcheraccessor.zenith_getModelRenderers().get(var0);
      if (entityrenderer == null) {
         entityrenderer = entityrenderdispatcheraccessor.zenith_getModelRenderers().get(PlayerSkinType.WIDE);
      }

      return entityrenderer instanceof PlayerEntityRenderer playerentityrenderer ? playerentityrenderer : null;
   }

   void render(
      PlayerEntity var1,
      BotPlayHandler var2,
      double var3,
      double var5,
      double var7,
      float var9,
      MatrixStack var10,
      OrderedRenderCommandQueue var11,
      CameraRenderState cameraState,
      int var12,
      boolean var13
   ) {
      SkinTextures skintextures = resolveSkin(var1, var2);
      PlayerEntityRenderer playerentityrenderer = rendererFor(skintextures.model());
      if (playerentityrenderer != null) {
         this.fillState(var1, skintextures, var9, var3, var5, var7, var13);
         double d0 = MathHelper.lerp(var9, var1.lastRenderX, var1.getX());
         double d1 = MathHelper.lerp(var9, var1.lastRenderY, var1.getY());
         double d2 = MathHelper.lerp(var9, var1.lastRenderZ, var1.getZ());
         var10.push();
         var10.translate(d0 - var3, d1 - var5, d2 - var7);
         playerentityrenderer.render(this.state, var10, var11, cameraState);
         var10.pop();
      }
   }

   static SkinTextures resolveSkin(PlayerEntity var0, BotPlayHandler var1) {
      if (var1 != null) {
         PlayerListEntry playerlistentry = var1.getPlayerListEntry(var0.getUuid());
         if (playerlistentry != null) {
            try {
               return playerlistentry.getSkinTextures();
            } catch (Exception var4) {
            }
         }
      }

      return DefaultSkinHelper.getSkinTextures(var0.getUuid());
   }

   public void fillState(PlayerEntity player, SkinTextures skin, float tickDelta, double cameraX, double cameraY, double cameraZ, boolean showName) {
      PlayerEntityRenderer renderer = rendererFor(skin.model());
      if (renderer == null) {
         return;
      }

      renderer.updateRenderState(player, this.state, tickDelta);
      this.state.skinTextures = skin;
      this.state.x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
      this.state.y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
      this.state.z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());
      this.state.squaredDistanceToCamera = MathHelper.squaredMagnitude(this.state.x - cameraX, this.state.y - cameraY, this.state.z - cameraZ);
      this.state.displayName = showName && this.state.squaredDistanceToCamera < 4096.0 ? player.getDisplayName() : null;
      this.state.capeVisible = false;
   }
}
