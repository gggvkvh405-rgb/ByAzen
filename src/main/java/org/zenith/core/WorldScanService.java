package org.zenith.core;

public final class WorldScanService {
   public final float float34;
   public final float float35;
   public final float float36;
   public final float float37;
   public final float float38;
   public final float float39;
   public final float float40;
   public final float float41;
   public final float float42;
   public final float float43;
   public final float float44;
   public final float float45;
   public final float float46;
   public final float float47;
   public final float float48;
   public final float float49;
   public final float float50;
   public final float float51;
   public final float float52;

   public WorldScanService(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var12, var13, var12, var13, 0.0F, 0.0F);
   }

   public WorldScanService(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19
   ) {
      this.float34 = var1;
      this.float35 = var2;
      this.float36 = var3;
      this.float37 = var4;
      this.float38 = var5;
      this.float39 = var6;
      this.float40 = var7;
      this.float41 = var8;
      this.float42 = var9;
      this.float43 = var10;
      this.float44 = var11;
      this.float45 = var12;
      this.float46 = var13;
      this.float47 = var14;
      this.float48 = var15;
      this.float49 = var16;
      this.float50 = var17;
      this.float51 = var18;
      this.float52 = var19;
   }

   public float list118() {
      return this.float34;
   }

   public float map61() {
      return this.float35;
   }

   public WorldScanService CloudResponse(float var1, float var2, float var3, float var4) {
      return new WorldScanService(
         var1,
         var2,
         var1 - var3,
         var2 - var4,
         this.float38,
         this.float39,
         this.float40,
         this.float41,
         this.float42,
         this.float43,
         this.float44,
         this.float45,
         this.float46,
         this.float47,
         this.float48,
         this.float49,
         this.float50,
         this.float51,
         this.float52
      );
   }

   public WorldScanService on23(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      return new WorldScanService(
         var1,
         var2,
         var1 - var3,
         var2 - var4,
         this.float38,
         this.float39,
         this.float40,
         this.float41,
         this.float42,
         this.float43,
         this.float44,
         this.float45,
         this.float46,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10
      );
   }

   public float[] int371() {
      return new float[]{this.float34, this.float35};
   }

   public float long147() {
      return this.float36;
   }

   public float zClass114() {
      return this.float37;
   }

   public float atomicLong3() {
      return this.float38;
   }

   public float atomicLong4() {
      return this.float39;
   }

   public float map41() {
      return this.float40;
   }

   public float map42() {
      return Math.abs(this.float34);
   }

   public float map43() {
      return Math.abs(this.float35);
   }

   public float clientWorld2() {
      return this.float41;
   }

   public float render() {
      return this.float42;
   }

   public float abstractClientPlayerEntity2() {
      return this.float43;
   }

   public float animationContainer() {
      return this.float44;
   }

   public float var3() {
      return this.float45;
   }

   public float long96() {
      return this.float46;
   }

   public float string37() {
      return this.float47;
   }

   public float long99() {
      return this.float48;
   }

   public float int140() {
      return this.float49;
   }

   public float var15Var143() {
      return this.float50;
   }

   public float var153() {
      return this.float51;
   }

   public float int139() {
      return this.float52;
   }
}
