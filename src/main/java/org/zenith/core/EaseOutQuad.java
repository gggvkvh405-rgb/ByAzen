package org.zenith.core;

public class EaseOutQuad extends EaseBase {
   public EaseOutQuad() {
   }

   public EaseOutQuad(float var1) {
      super(var1);
   }

   @Override
   public float ease(float var1, float var2, float var3, float var4) {
      float f = this.BotPacketEvent();
      float f1;
      return var3 * ((f1 = var1 / var4 - 1.0F) * f1 * ((f + 1.0F) * f1 + f) + 1.0F) + var2;
   }
}
