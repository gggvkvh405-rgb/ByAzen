package org.zenith.base.font;

import net.minecraft.text.Text;

public class Font {
   public MsdfFont font;
   public float size;

   public float height() {
      return MsdfRenderer.textLineOffset(this.font, this.size);
   }

   public float getStringHeight(String var1) {
      float f = 0.0F;
      float f1 = 0.0F;

      for (char c0 : (var1.isEmpty() ? " " : var1).toCharArray()) {
         if (c0 == '\n') {
            f = f == 0.0F ? this.height() : f;
            f1 += f;
            f = 0.0F;
         } else {
            f = Math.max(this.height(), f);
         }
      }

      return f + f1;
   }

   public float width(String var1) {
      return this.font.getWidth(var1, this.size);
   }

   public float width(Text var1) {
      return this.font.getTextWidth(var1, this.size);
   }

   public MsdfFont getFont() {
      return this.font;
   }

   public float getSize() {
      return this.size;
   }

   public Font(MsdfFont var1, float var2) {
      this.font = var1;
      this.size = var2;
   }
}
