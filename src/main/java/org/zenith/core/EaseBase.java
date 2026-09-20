package org.zenith.core;

public abstract class EaseBase implements Easing {
   public static final float EventTick = 1.70158F;
   public float EventTickEnd;

   public EaseBase() {
      this(1.70158F);
   }

   public EaseBase(float var1) {
      this.EventTickEnd = var1;
   }

   public void ItemSpec(float var1) {
      this.EventTickEnd = var1;
   }

   public float BotPacketEvent() {
      return this.EventTickEnd;
   }
}
