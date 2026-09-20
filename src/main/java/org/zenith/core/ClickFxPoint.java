package org.zenith.core;

public class ClickFxPoint {
   public float x;
   public float y;
   public float z;

   public ClickFxPoint(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public ClickFxPoint Easing(ClickFxPoint var1) {
      this.x = this.x + var1.x;
      this.y = this.y + var1.y;
      this.z = this.z + var1.z;
      return this;
   }

   public ClickFxPoint ColorAnimator(ClickFxPoint var1) {
      this.x = this.x - var1.x;
      this.y = this.y - var1.y;
      this.z = this.z - var1.z;
      return this;
   }

   public ClickFxPoint BlockInteractEvent(float var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      return this;
   }

   public ClickFxPoint EventClickSlotHook(float var1) {
      this.x /= var1;
      this.y /= var1;
      this.z /= var1;
      return this;
   }

   public ClickFxPoint string70() {
      return new ClickFxPoint(this.x, this.y, this.z);
   }

   public void ItemRegistry(ClickFxPoint var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
   }

   public float float126() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public float float127() {
      return (float)Math.sqrt(this.float126());
   }

   public ClickFxPoint float128() {
      float f = this.float127();
      if (f > 1.0E-4F) {
         this.x /= f;
         this.y /= f;
         this.z /= f;
      }

      return this;
   }

   public ClickFxPoint CloseScreenEvent(float var1) {
      float f = (float)Math.toRadians(var1);
      float f1 = (float)Math.cos(f);
      float f2 = (float)Math.sin(f);
      float f3 = this.x * f1 - this.y * f2;
      float f4 = this.x * f2 + this.y * f1;
      this.x = f3;
      this.y = f4;
      return this;
   }
}
