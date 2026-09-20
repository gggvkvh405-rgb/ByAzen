package org.zenith.utility.render.display.base;

import org.zenith.util.MathUtils;

public class AnimationValue {
   float x;
   float y;
   float width;
   float height;

   public boolean PotionItemBuilder(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.x, this.y, this.width, this.height);
   }

   public AnimationValue(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public void setX(float var1) {
      this.x = var1;
   }

   public void setY(float var1) {
      this.y = var1;
   }

   public void setWidth(float var1) {
      this.width = var1;
   }

   public void setHeight(float var1) {
      this.height = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (var1 instanceof AnimationValue l1i1illlili) {
         if (!l1i1illlili.canEqual(this)) {
            return false;
         } else if (Float.compare(this.getX(), l1i1illlili.getX()) != 0) {
            return false;
         } else if (Float.compare(this.getY(), l1i1illlili.getY()) != 0) {
            return false;
         } else {
            return Float.compare(this.getWidth(), l1i1illlili.getWidth()) != 0 ? false : Float.compare(this.getHeight(), l1i1illlili.getHeight()) == 0;
         }
      } else {
         return false;
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof AnimationValue;
   }

   @Override
   public int hashCode() {
      byte b0 = 59;
      int i = 1;
      i = i * 59 + Float.floatToIntBits(this.getX());
      i = i * 59 + Float.floatToIntBits(this.getY());
      i = i * 59 + Float.floatToIntBits(this.getWidth());
      return i * 59 + Float.floatToIntBits(this.getHeight());
   }

   @Override
   public String toString() {
      return "ChangeRect(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
   }
}
