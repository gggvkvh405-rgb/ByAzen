package org.zenith.hud;

import org.zenith.core.ClientProvider;

public class ScrollHandler implements ClientProvider {
   public double double95;
   public double value = 0.0;
   public double double96 = 0.0;
   public double double97 = 8.0;
   public static final double double98 = 0.4;
   public static final double double99 = 1.0;

   public void update() {
      this.double96 = Math.max(Math.min(this.double96, 0.0), -this.double95);
      double d0 = this.double96 - this.value;
      this.value += d0 * 0.4;
      if (Math.abs(d0) < 0.1) {
         this.value = this.double96;
      }
   }

   public double float260() {
      return -this.value;
   }

   public void CloudRouter(double var1) {
      this.double96 = this.double96 + var1 * this.double97;
   }

   public double float261() {
      return this.double95;
   }

   public double float262() {
      return this.double96;
   }

   public double call095() {
      return this.double97;
   }

   public void ProtocolMessage(double var1) {
      this.double95 = var1;
   }

   public void setValue(double var1) {
      this.value = var1;
   }

   public void AnalyticsTracker(double var1) {
      this.double96 = var1;
   }

   public void ConfigJsonUtil(double var1) {
      this.double97 = var1;
   }
}
