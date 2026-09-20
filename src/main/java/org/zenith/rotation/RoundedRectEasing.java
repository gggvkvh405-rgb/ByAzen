package org.zenith.rotation;

import org.zenith.core.BotActivity;

public class RoundedRectEasing extends RotationEasingBase {
   public int int338;
   public boolean boolean152;
   public boolean boolean153;
   public float float36;
   public float float211;
   public float float37;
   public float float212;

   @Override
   public BotActivity call110() {
      return BotActivity.call416;
   }

   public static int call458() {
      return 3;
   }

   public static boolean isTrue() {
      return true;
   }

   public static boolean isTrue2() {
      return true;
   }

   public static float call264() {
      return 1.75F;
   }

   public static float call205() {
      return 0.4F;
   }

   public static float call459() {
      return 1.25F;
   }

   public static float call206() {
      return 0.75F;
   }

   RoundedRectEasing(int var1, boolean var2, boolean var3, float var4, float var5, float var6, float var7) {
      this.int338 = var1;
      this.boolean152 = var2;
      this.boolean153 = var3;
      this.float36 = var4;
      this.float211 = var5;
      this.float37 = var6;
      this.float212 = var7;
   }

   public static RoundedRectEasing.Animation call270() {
      return new RoundedRectEasing.Animation();
   }

   public int call495() {
      return this.int338;
   }

   public boolean call496() {
      return this.boolean152;
   }

   public boolean call497() {
      return this.boolean153;
   }

   public float int94() {
      return this.float36;
   }

   public float int95() {
      return this.float211;
   }

   public float int96() {
      return this.float37;
   }

   public float call262() {
      return this.float212;
   }


   public static class Animation {
      public boolean boolean62;
      public int int112;
      public boolean boolean63;
      public boolean boolean64;
      public boolean boolean65;
      public boolean boolean66;
      public boolean boolean67;
      public float float30;
      public boolean boolean68;
      public float float31;
      public boolean boolean69;
      public float float32;
      public boolean boolean70;
      public float float33;

      Animation() {
      }

      public Animation InventoryUtils(int var1) {
         this.int112 = var1;
         this.boolean62 = true;
         return this;
      }

      public Animation CloudApiClient(boolean var1) {
         this.boolean64 = var1;
         this.boolean63 = true;
         return this;
      }

      public Animation MediaTrackInfo(boolean var1) {
         this.boolean66 = var1;
         this.boolean65 = true;
         return this;
      }

      public Animation CloudRouter(float var1) {
         this.float30 = var1;
         this.boolean67 = true;
         return this;
      }

      public Animation ProtocolMessage(float var1) {
         this.float31 = var1;
         this.boolean68 = true;
         return this;
      }

      public Animation AnalyticsTracker(float var1) {
         this.float32 = var1;
         this.boolean69 = true;
         return this;
      }

      public Animation ConfigJsonUtil(float var1) {
         this.float33 = var1;
         this.boolean70 = true;
         return this;
      }

      public RoundedRectEasing call200() {
         int i = this.int112;
         if (!this.boolean62) {
            i = RoundedRectEasing.call458();
         }

         boolean flag = this.boolean64;
         if (!this.boolean63) {
            flag = RoundedRectEasing.isTrue();
         }

         boolean flag1 = this.boolean66;
         if (!this.boolean65) {
            flag1 = RoundedRectEasing.isTrue2();
         }

         float f = this.float30;
         if (!this.boolean67) {
            f = RoundedRectEasing.call264();
         }

         float f1 = this.float31;
         if (!this.boolean68) {
            f1 = RoundedRectEasing.call205();
         }

         float f2 = this.float32;
         if (!this.boolean69) {
            f2 = RoundedRectEasing.call459();
         }

         float f3 = this.float33;
         if (!this.boolean70) {
            f3 = RoundedRectEasing.call206();
         }

         return new RoundedRectEasing(i, flag, flag1, f, f1, f2, f3);
      }

      @Override
      public String toString() {
         return "GownoRotationConfig.GownoRotationConfigBuilder(tick$value="
            + this.int112
            + ", multiplyYaw$value="
            + this.boolean64
            + ", multiplyPitch$value="
            + this.boolean66
            + ", yawAcceleration$value="
            + this.float30
            + ", yawDeceleration$value="
            + this.float31
            + ", pitchAcceleration$value="
            + this.float32
            + ", pitchDeceleration$value="
            + this.float33
            + ")";
      }
   }
}
