package org.zenith.addon.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.addon.api.render.AddonFont;
import org.zenith.addon.api.render.AddonRenderBackend;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfFont;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

final class ZenithImmediateRenderBackend implements AddonRenderBackend {
   public final Map<AddonFont, MsdfFont> fonts = new ConcurrentHashMap<>();
   public DrawContext preparedBlurContext;

   public void prepareBlur(DrawContext var1, float var2) {
      if (this.preparedBlurContext != var1) {
         this.preparedBlurContext = var1;
         ZenithClient.on23().ModuleStateStore().UiAnimation(ui(var1));
      }
   }

   public void blur(DrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      Matrix4f matrix4f = org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices());
      ZenithClient.on23().ModuleStateStore().UiAnimation(matrix4f, var2, var3, var4, var5, CornerRadius.MovementInputEvent(var6), new ArgbColor(var7));
   }

   public void rect(DrawContext var1, float var2, float var3, float var4, float var5, int var6) {
      renderer(var1).rect(var2, var3, var4, var5, var6);
   }

   public void roundedRect(DrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      renderer(var1).roundedRect(var2, var3, var4, var5, var6, var7);
   }

   public void text(DrawContext var1, String var2, float var3, float var4, float var5, int var6) {
      renderer(var1).text(var2, var3, var4, var5, var6);
   }

   public void text(DrawContext var1, AddonFont var2, String var3, float var4, float var5, float var6, int var7) {
      ui(var1).drawText(this.font(var2, var6), var3, var4, var5, new ArgbColor(var7));
   }

   public float textWidth(String var1, float var2) {
      return Fonts.NEW_REGULAR.getFont(var2).width(var1);
   }

   public float textWidth(AddonFont var1, String var2, float var3) {
      return this.font(var1, var3).width(var2);
   }

   public float textHeight(float var1) {
      return Fonts.NEW_REGULAR.getFont(var1).height();
   }

   public float textHeight(AddonFont var1, float var2) {
      return this.font(var1, var2).height();
   }

   public static ZenithAddonRenderContext renderer(DrawContext var0) {
      return new ZenithAddonRenderContext(ui(var0));
   }

   public Font font(AddonFont var1, float var2) {
      MsdfFont msdffont = this.fonts
         .computeIfAbsent(var1, var0 -> MsdfFont.builder().name(var0.data().toString()).data(var0.data()).atlas(var0.atlas()).build());
      return msdffont.getFont(var2);
   }

   public static HudDrawContext ui(DrawContext var0) {
      return HudDrawContext.of(var0, 0, 0, 0.0F);
   }
}
