package org.zenith.core;

import net.minecraft.entity.player.PlayerEntity;

class HudStatusTarget {
   public final HudStatusPanel val430;
   public long long116;
   public int int189;

   public HudStatusTarget(HudStatusPanel var1) {
      this.val430 = var1;
      this.long116 = System.currentTimeMillis();
      this.int189 = 0;
   }

   public void ColorAnimator(PlayerEntity var1) {
      this.long116 = System.currentTimeMillis();
      this.int189++;
      this.val430.UiAnimation("Y", this.val430.on23(var1, this.int189), 3000L);
   }

   public boolean float206() {
      return System.currentTimeMillis() - this.long116 > 600000L;
   }
}
