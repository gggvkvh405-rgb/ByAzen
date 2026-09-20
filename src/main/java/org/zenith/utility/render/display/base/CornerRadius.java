package org.zenith.utility.render.display.base;

public record CornerRadius(float float396, float float397, float float398, float float399) {
   public static final CornerRadius var159 = new CornerRadius(0.0F, 0.0F, 0.0F, 0.0F);

   public static CornerRadius MovementInputEvent(float var0) {
      return new CornerRadius(var0, var0, var0, var0);
   }

   public static CornerRadius Event14(float var0) {
      return new CornerRadius(var0, 0.0F, 0.0F, 0.0F);
   }

   public static CornerRadius HealthUpdateEvent(float var0) {
      return new CornerRadius(0.0F, var0, 0.0F, 0.0F);
   }

   public static CornerRadius RenderTickEvent(float var0) {
      return new CornerRadius(0.0F, 0.0F, var0, 0.0F);
   }

   public static CornerRadius Event18Ext(float var0) {
      return new CornerRadius(0.0F, 0.0F, 0.0F, var0);
   }

   public static CornerRadius BotPacketEvent(float var0, float var1) {
      return new CornerRadius(var0, var1, 0.0F, 0.0F);
   }

   public static CornerRadius Event29(float var0) {
      return new CornerRadius(var0, var0, 0.0F, 0.0F);
   }

   public static CornerRadius BotRespawnEvent(float var0, float var1) {
      return new CornerRadius(0.0F, 0.0F, var1, var0);
   }

   public static CornerRadius RotationUpdateStartEvent(float var0) {
      return new CornerRadius(0.0F, 0.0F, var0, var0);
   }

   public static CornerRadius BotTickEvent(float var0, float var1) {
      return new CornerRadius(var0, 0.0F, 0.0F, var1);
   }

   public static CornerRadius VelocityChangeEvent(float var0, float var1) {
      return new CornerRadius(0.0F, var0, var1, 0.0F);
   }

   @Override
   public String toString() {
      return "BorderRadius{topLeftRadius="
         + this.float396
         + ", topRightRadius="
         + this.float397
         + ", bottomRightRadius="
         + this.float398
         + ", bottomLeftRadius="
         + this.float399
         + "}";
   }

   public float var14311() {
      return this.float396;
   }

   public float var14312() {
      return this.float397;
   }

   public float itemStack9() {
      return this.float398;
   }

   public float string63() {
      return this.float399;
   }
}
