package org.zenith.core;

public class EaseInOutCubic extends EaseBase {
   public EaseInOutCubic() {
   }

   public EaseInOutCubic(float var1) {
      super(var1);
   }

   @Override
   public float ease(float var1, float var2, float var3, float var4) {
      float f = this.BotPacketEvent();
      float f1;
      float f2;
      float f3;
      float f4;
      return (f1 = var1 / (var4 / 2.0F)) < 1.0F
         ? var3 / 2.0F * f1 * f1 * (((f3 = f * 1.525F) + 1.0F) * f1 - f3) + var2
         : var3 / 2.0F * ((f2 = f1 - 2.0F) * f2 * (((f4 = f * 1.525F) + 1.0F) * f2 + f4) + 2.0F) + var2;
   }
}
