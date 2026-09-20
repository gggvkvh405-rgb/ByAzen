package org.zenith.base.font;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.zenith.utility.render.display.base.GradientRadius;

public final class MsdfGlyph {
   public final int code;
   public final float minU;
   public final float maxU;
   public final float minV;
   public final float maxV;
   public final float advance;
   public final float topPosition;
   public final float width;
   public final float height;

   public MsdfGlyph(FontData_GlyphData var1, float var2, float var3) {
      this.code = var1.unicode();
      this.advance = var1.advance();
      FontData_BoundsData fontdata_boundsdata = var1.atlasBounds();
      if (fontdata_boundsdata != null) {
         this.minU = fontdata_boundsdata.left() / var2;
         this.maxU = fontdata_boundsdata.right() / var2;
         this.minV = 1.0F - fontdata_boundsdata.top() / var3;
         this.maxV = 1.0F - fontdata_boundsdata.bottom() / var3;
      } else {
         this.minU = this.maxU = this.minV = this.maxV = 0.0F;
      }

      FontData_BoundsData fontdata_boundsdata1 = var1.planeBounds();
      if (fontdata_boundsdata1 != null) {
         this.width = fontdata_boundsdata1.right() - fontdata_boundsdata1.left();
         this.height = fontdata_boundsdata1.top() - fontdata_boundsdata1.bottom();
         this.topPosition = fontdata_boundsdata1.top();
      } else {
         this.width = this.height = this.topPosition = 0.0F;
      }
   }

   public float apply(Matrix4f var1, VertexConsumer var2, float var3, float var4, float var5, float var6, int var7) {
      var5 -= this.topPosition * var3;
      float f = this.width * var3;
      float f1 = this.height * var3;
      var2.vertex(var1, var4, var5, var6).texture(this.minU, this.minV).color(var7);
      var2.vertex(var1, var4, var5 + f1, var6).texture(this.minU, this.maxV).color(var7);
      var2.vertex(var1, var4 + f, var5 + f1, var6).texture(this.maxU, this.maxV).color(var7);
      var2.vertex(var1, var4 + f, var5, var6).texture(this.maxU, this.minV).color(var7);
      return this.advance * var3;
   }

   public float apply(Matrix4f var1, VertexConsumer var2, float var3, float var4, float var5, float var6, GradientRadius var7) {
      var5 -= this.topPosition * var3;
      float f = this.width * var3;
      float f1 = this.height * var3;
      var2.vertex(var1, var4, var5, var6).texture(this.minU, this.minV).color(var7.call010().call001());
      var2.vertex(var1, var4, var5 + f1, var6).texture(this.minU, this.maxV).color(var7.call014().call001());
      var2.vertex(var1, var4 + f, var5 + f1, var6).texture(this.maxU, this.maxV).color(var7.call017().call001());
      var2.vertex(var1, var4 + f, var5, var6).texture(this.maxU, this.minV).color(var7.call052().call001());
      return this.advance * var3;
   }

   public float getWidth(float var1) {
      return this.advance * var1;
   }

   public int getCharCode() {
      return this.code;
   }
}
