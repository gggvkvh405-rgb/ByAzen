package org.zenith.util;

public class StopWatch {
   public long startTime;

   public StopWatch() {
      this.reset();
   }

   public boolean BotFeatureRegistry(double var1) {
      return System.currentTimeMillis() - var1 >= this.startTime;
   }

   public boolean ServiceException(double var1) {
      boolean flag = this.BotFeatureRegistry(var1);
      if (flag) {
         this.reset();
      }

      return flag;
   }

   public void reset() {
      this.startTime = System.currentTimeMillis();
   }

   public int var11925() {
      return Math.toIntExact(System.currentTimeMillis() - this.startTime);
   }

   public void EventInjectHandleInputEvents(long var1) {
      this.startTime = System.currentTimeMillis() - var1;
   }

   public long getStartTime() {
      return this.startTime;
   }
}
