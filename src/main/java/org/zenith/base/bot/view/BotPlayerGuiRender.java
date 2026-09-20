package org.zenith.base.bot.view;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;

public final class BotPlayerGuiRender {
   public static final BotPlayerRenderer RENDERER = new BotPlayerRenderer();

   public static void drawEntity(
      DrawContext var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, BotPlayer var9, BotPlayHandler var10
   ) {
      float f = (var1 + var3) / 2.0F;
      float f1 = (var2 + var4) / 2.0F;
      var0.enableScissor(var1, var2, var3, var4);
      float f2 = (float)Math.atan((f - var7) / 40.0F);
      float f3 = (float)Math.atan((f1 - var8) / 40.0F);
      Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf cameraRotation = new Quaternionf().rotateX(f3 * 20.0F * (float)(Math.PI / 180.0));
      quaternionf.mul(cameraRotation);
      RENDERER.fillState(var9, BotPlayerRenderer.resolveSkin(var9, var10), 1.0F, var9.getX(), var9.getY(), var9.getZ(), false);
      PlayerEntityRenderState state = RENDERER.state;
      state.light = 15728880;
      state.shadowPieces.clear();
      state.outlineColor = 0;
      state.bodyYaw = 180.0F + f2 * 20.0F;
      state.relativeHeadYaw = f2 * 20.0F;
      state.pitch = -f3 * 20.0F;
      float f9 = var9.getScale();
      Vector3f vector3f = new Vector3f(0.0F, var9.getHeight() / 2.0F + var6 * f9, 0.0F);
      float f10 = var5 / f9;
      var0.addEntity(state, f10, vector3f, quaternionf, cameraRotation, var1, var2, var3, var4);
      var0.disableScissor();
   }
}
