package org.zenith.util;

public class CooldownTimer {
   public long long156;

   public CooldownTimer() {
      this.reset();
   }

   public boolean EventMouseButton(long var1) {
      boolean flag = this.EventModifyMouseRotationInput(var1);
      if (flag) {
         this.reset();
      }

      return flag;
   }

   public boolean EventEntityCollision(float var1) {
      return System.currentTimeMillis() - (long)var1 >= this.long156;
   }

   public boolean EventModifyMouseRotationInput(long var1) {
      return System.currentTimeMillis() - var1 >= this.long156;
   }

   public void reset() {
      this.long156 = System.currentTimeMillis();
   }

   public long var11933() {
      return System.currentTimeMillis() - this.long156;
   }

   public long var11926() {
      return this.long156;
   }

   public void EventMixin_modifySetScreenArg(long var1) {
      this.long156 = var1;
   }
}
