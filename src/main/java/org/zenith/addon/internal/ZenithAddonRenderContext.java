package org.zenith.addon.internal;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import org.zenith.addon.api.render.AddonRenderContext;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

final class ZenithAddonRenderContext implements AddonRenderContext {
   public final HudDrawContext context;

   ZenithAddonRenderContext(HudDrawContext var1) {
      this.context = Objects.requireNonNull(var1, "context");
   }

   public int width() {
      return MinecraftClient.getInstance().getWindow().getScaledWidth();
   }

   public int height() {
      return MinecraftClient.getInstance().getWindow().getScaledHeight();
   }

   public float tickDelta() {
      return this.context.getDelta();
   }

   public void rect(float var1, float var2, float var3, float var4, int var5) {
      if (drawable(var3, var4)) {
         this.context.drawRect(var1, var2, var3, var4, new ArgbColor(var5));
      }
   }

   public void roundedRect(float var1, float var2, float var3, float var4, float var5, int var6) {
      if (drawable(var3, var4)) {
         float f = Math.max(0.0F, Math.min(finite(var5), Math.min(var3, var4) / 2.0F));
         this.context.drawRoundedRect(var1, var2, var3, var4, CornerRadius.MovementInputEvent(f), new ArgbColor(var6));
      }
   }

   public void text(String var1, float var2, float var3, float var4, int var5) {
      this.context.drawText(font(var4), Objects.requireNonNullElse(var1, ""), var2, var3, new ArgbColor(var5));
   }

   public float textWidth(String var1, float var2) {
      return font(var2).width(Objects.requireNonNullElse(var1, ""));
   }

   public float textHeight(float var1) {
      return font(var1).height();
   }

   public void pushClip(float var1, float var2, float var3, float var4) {
      if (drawable(var3, var4)) {
         this.context.enableScissor(var1, var2, var1 + var3, var2 + var4);
      }
   }

   public void popClip() {
      this.context.disableScissor();
   }

   public static Font font(float var0) {
      return Fonts.NEW_REGULAR.getFont(Math.max(1.0F, finite(var0)));
   }

   public static boolean drawable(float var0, float var1) {
      return Float.isFinite(var0) && Float.isFinite(var1) && var0 > 0.0F && var1 > 0.0F;
   }

   public static float finite(float var0) {
      return Float.isFinite(var0) ? var0 : 0.0F;
   }
}
