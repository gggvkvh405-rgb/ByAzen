package org.zenith.utility.render.display.base;

import org.zenith.util.MathUtils;

public record CornerRadiusF(float float392, float float393, float float394, float float395) {
   public boolean PotionItemBuilder(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.float392, this.float393, this.float394, this.float395);
   }

   public boolean on23(double var1, double var3, float var5) {
      return MathUtils.on23(var1, var3, this.float392 - var5, this.float393 - var5, this.float394 + var5 * 2.0F, this.float395 + var5 * 2.0F);
   }

   public boolean on23(CornerRadiusF var1) {
      return this.float392 < var1.x() + var1.width()
         && this.float392 + this.float394 > var1.x()
         && this.float393 < var1.y() + var1.height()
         && this.float393 + this.float395 > var1.y();
   }

   public float x() {
      return this.float392;
   }

   public float y() {
      return this.float393;
   }

   public float width() {
      return this.float394;
   }

   public float height() {
      return this.float395;
   }
}
