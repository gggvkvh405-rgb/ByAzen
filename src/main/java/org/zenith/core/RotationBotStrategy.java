package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.module.combat.Aura;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationDelta;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationStrategyBase;

public final class RotationBotStrategy extends RotationStrategyBase {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int448 = Integer.MIN_VALUE;
   public static final float float359 = 0.21217355F;
   public static final int int449 = 64;
   public static final int int450 = 8;
   public static final double double151 = 2500.0;
   public static final double double152 = 0.0012799999999999999;
   public static final double double153 = 0.0014399999999999999;
   public static final double double154 = 0.2304;
   public static final double double155 = 0.1296;
   public static final double double156 = 1.0E-5;
   public final TrajectoryDataset var03 = TrajectoryDataset.call466();
   public final TargetInterpolator zClass101 = new TargetInterpolator();
   public final MovementSimulator zClass095 = botPlayer2();
   public int int403 = Integer.MIN_VALUE;
   public boolean boolean138;
   public float float360;
   public float float361;
   public boolean boolean197;
   public float float362;
   public float float363;
   public double double62;
   public double double63;
   public double double72;
   public double double73;
   public int[][] call078 = new int[0][2];
   public int int451;
   public String string114 = "idle";

   public static MovementSimulator botPlayer2() {
      MovementSimulator ll1i11iili1i1i11lilil11i11ill1 = MovementSimulator.TextScanner(1592639710L);
      if (ll1i11iili1i1i11lilil11i11ill1 == null) {
         System.err.println("[HumanBurst] calibrated motor plant unavailable");
      }

      return ll1i11iili1i1i11lilil11i11ill1;
   }

   public Rotation Easing(Rotation var1) {
      Rotation ililiiili1ll1li11 = val002.LineShader();
      if (this.zClass095 == null || this.var03 == null) {
         return ililiiili1ll1li11;
      }

      if (var1 == null || var1.string68()) {
         this.botClient3();
         return ililiiili1ll1li11;
      }

      if (minecraftClient3.player == null) {
         this.botClient3();
         return ililiiili1ll1li11;
      }

      LivingEntity livingentity = this.call009();
      if (livingentity != null && livingentity.isAlive() && livingentity.getBoundingBox() != null) {
         if (this.int403 != livingentity.getId()) {
            this.botClient3();
            this.int403 = livingentity.getId();
         }

         float f = ililiiili1ll1li11.GrimGlide();
         float f1 = ililiiili1ll1li11.GuiWalk();
         if (!this.boolean138) {
            this.float360 = f;
            this.float361 = f1;
            this.boolean138 = true;
            this.string114 = "warming-up";
            return ililiiili1ll1li11;
         }

         float f2 = MathHelper.wrapDegrees(f - this.float360);
         float f3 = f1 - this.float361;
         this.float360 = f;
         this.float361 = f1;
         float[] afloat = this.zClass101.on23(minecraftClient3, ililiiili1ll1li11, livingentity, f2, f3, 0.21217355F);
         if (afloat == null) {
            this.int92();
            this.var03.reset();
            this.zClass101.reset();
            this.string114 = "invalid-features";
            return ililiiili1ll1li11;
         }

         try {
            this.var03.UiAnimation(afloat, 0.21217355F);
         } catch (Throwable throwable2) {
            System.err.println("[HumanBurst] feature observation failed: " + throwable2);
            this.botClient3();
            this.string114 = "feature-error";
            return ililiiili1ll1li11;
         }

         BotStrategyState liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil = this.on23(
            ililiiili1ll1li11, minecraftClient3.player.getEyePos(), livingentity.getBoundingBox(), f2, f3
         );
         if (liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil == null) {
            this.int92();
            this.boolean197 = false;
            this.string114 = "invalid-frame";
            return ililiiili1ll1li11;
         }

         if (this.int451 < this.call078.length) {
            int[] aint1 = this.call078[this.int451++];
            this.string114 = "plan:" + this.int451 + "/" + this.call078.length;
            return this.on23(ililiiili1ll1li11, aint1);
         }

         TrajectorySample lilliil111i_Var160;
         try {
            lilliil111i_Var160 = this.var03.call467();
         } catch (Throwable throwable1) {
            System.err.println("[HumanBurst] inference failed: " + throwable1);
            this.int92();
            this.var03.reset();
            this.string114 = "inference-error";
            return ililiiili1ll1li11;
         }

         if (lilliil111i_Var160.call077() == 0) {
            this.string114 = "intent:hold,p=" + this.ServiceException(lilliil111i_Var160.var1185());
            return ililiiili1ll1li11;
         }

         if (lilliil111i_Var160.call077() == 1) {
            int[] aint2 = this.on23(lilliil111i_Var160, afloat, f1, 1);
            int l = this.UiAnimation(aint2[0], afloat[0]);
            int i1 = this.on23(f1, this.UiAnimation(aint2[1], afloat[1]));
            this.string114 = "intent:micro,end:" + l + "/" + i1 + ",p=" + this.ServiceException(lilliil111i_Var160.var1185());
            return this.on23(ililiiili1ll1li11, new int[]{l, i1});
         }

         SimPose ll1i11iili1i1i11lilil11i11ill1_l1i1illlili = new SimPose();
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double60 = 0.2304;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double61 = 0.1296;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double53 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double53;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double54 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double54;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double55 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double55;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double56 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double56;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double59 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double59;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double62 = this.double62;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double63 = this.double63;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double64 = 0.2304;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double65 = 0.1296;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double66 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double57;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double67 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double58;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double68 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double53;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double69 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double54;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double70 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double55;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double71 = liilll1lii1lil1iiii1ii1i_ii1il11l111ii11iil.double56;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double72 = this.double72;
         ll1i11iili1i1i11lilil11i11ill1_l1i1illlili.double73 = this.double73;

         SimBoxState ll1i11iili1i1i11lilil11i11ill1_l1iil11li;
         int[] aint;
         try {
            int i = Math.min(lilliil111i_Var160.call185(), 8);
            aint = this.on23(lilliil111i_Var160, afloat, f1, i);
            int j = aint[0];
            int k = aint[1];
            double d0 = 0.2304 + j * 0.21217355F * 0.0012799999999999999;
            double d1 = 0.1296 + k * 0.21217355F * 0.0014399999999999999;
            double d2 = this.on23(lilliil111i_Var160.vec3d40(), j, i) * 0.21217355F * 0.0012799999999999999;
            double d3 = this.on23(lilliil111i_Var160.playerInput2(), k, i) * 0.21217355F * 0.0014399999999999999;
            ll1i11iili1i1i11lilil11i11ill1_l1iil11li = this.zClass095.on23(ll1i11iili1i1i11lilil11i11ill1_l1i1illlili, i, d0, d1, d2, d3);
         } catch (Throwable throwable) {
            System.err.println("[PointClick] plan failed: " + throwable);
            this.int92();
            this.string114 = "plan-error";
            return ililiiili1ll1li11;
         }

         this.double62 = ll1i11iili1i1i11lilil11i11ill1_l1iil11li.double82;
         this.double63 = ll1i11iili1i1i11lilil11i11ill1_l1iil11li.double83;
         this.double72 = ll1i11iili1i1i11lilil11i11ill1_l1iil11li.double84;
         this.double73 = ll1i11iili1i1i11lilil11i11ill1_l1iil11li.double85;
         this.call078 = this.on23(ll1i11iili1i1i11lilil11i11ill1_l1iil11li);
         this.int451 = 0;
         this.string114 = "intent:bump,ticks:"
            + ll1i11iili1i1i11lilil11i11ill1_l1iil11li.int206
            + ",end:"
            + aint[0]
            + "/"
            + aint[1]
            + ",p="
            + this.ServiceException(lilliil111i_Var160.var1185());
         if (this.call078.length == 0) {
            return ililiiili1ll1li11;
         }

         int[] aint3 = this.call078[this.int451++];
         return this.on23(ililiiili1ll1li11, aint3);
      } else {
         this.botClient3();
         return ililiiili1ll1li11;
      }
   }

   public String botWorld() {
      return this.zClass095 != null && this.var03 != null ? this.string114 : "unavailable";
   }

   public BotStrategyState on23(Rotation var1, Vec3d var2, Box var3, float var4, float var5) {
      Vec3d vec3d = this.on23(var3);
      RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(this.on23(var2, vec3d));
      float f = liiilliiilil1l1i1111li1ii11.type2();
      float f1 = liiilliiilil1l1i1111li1ii11.path15();
      if (this.isFinite(f) && this.isFinite(f1)) {
         float f2 = Float.MAX_VALUE;
         float f3 = -Float.MAX_VALUE;
         float f4 = Float.MAX_VALUE;
         float f5 = -Float.MAX_VALUE;

         for (Vec3d vec3d1 : this.UiAnimation(var3)) {
            RotationDelta liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(this.on23(var2, vec3d1));
            float f6 = liiilliiilil1l1i1111li1ii11x.type2();
            float f7 = liiilliiilil1l1i1111li1ii11x.path15();
            if (this.isFinite(f6) && this.isFinite(f7)) {
               f6 = this.SimpleItemBuilder(f6, f);
               f2 = Math.min(f2, f6);
               f3 = Math.max(f3, f6);
               f4 = Math.min(f4, f7);
               f5 = Math.max(f5, f7);
            }
         }

         if (f2 == Float.MAX_VALUE) {
            f3 = f;
            f2 = f;
            f5 = f1;
            f4 = f1;
         }

         double d5 = var4 * 0.0012799999999999999 / 0.05;
         double d6 = var5 * 0.0014399999999999999 / 0.05;
         double d7 = 0.0;
         double d8 = 0.0;
         if (this.boolean197) {
            double d0 = f - (this.float362 - var4);
            double d1 = f1 - (this.float363 - var5);
            d7 = d0 * 0.0012799999999999999 / 0.05;
            d8 = d1 * 0.0014399999999999999 / 0.05;
         }

         this.float362 = f;
         this.float363 = f1;
         this.boolean197 = true;
         double d9 = Math.max((f3 - f2) * 0.0012799999999999999, 0.0);
         double d10 = Math.max((f5 - f4) * 0.0014399999999999999, 0.0);
         double d2 = Math.max(Math.min(d9, d10) * 0.5, 1.0E-5);
         d2 = Math.min(d2, Math.min(0.4608, 0.2592) * 0.5);
         double d3 = this.ItemSpec(0.2304 + f * 0.0012799999999999999, d2, 0.4608 - d2);
         double d4 = this.ItemSpec(0.1296 + f1 * 0.0014399999999999999, d2, 0.2592 - d2);
         boolean flag = f2 <= 0.0F && f3 >= 0.0F && f4 <= 0.0F && f5 >= 0.0F;
         return new BotStrategyState(d3, d4, d7, d8, d5, d6, d2, flag);
      } else {
         return null;
      }
   }

   public int[][] on23(SimBoxState var1) {
      int i = Math.min(var1.call274.length, var1.call275.length);
      int[][] aint = new int[i][2];
      double d0 = 0.0;
      double d1 = 0.0;
      int j = 0;
      int k = 0;

      for (int l = 0; l < i; l++) {
         double d2 = var1.call274[l] / 0.0012799999999999999;
         double d3 = var1.call275[l] / 0.0014399999999999999;
         d0 += d2 / 0.21217355F;
         d1 += d3 / 0.21217355F;
         int i1 = this.EnchantItemSpec(d0);
         int j1 = this.EnchantItemSpec(d1);
         aint[l][0] = i1 - j;
         aint[l][1] = j1 - k;
         j = i1;
         k = j1;
      }

      return aint;
   }

   public int UiAnimation(int var1, float var2) {
      int i = MathHelper.clamp(var1, -64, 64);
      return Float.isFinite(var2) && var2 * i < 0.0F ? 0 : i;
   }

   public int[] on23(TrajectorySample var1, float[] var2, float var3, int var4) {
      float f = this.on23(var1.call463(), var2[26], true);
      float f1 = this.on23(var1.block(), var2[27], false);
      float f2 = f * var4;
      float f3 = f1 * var4;
      float f4 = MathHelper.clamp(var1.blockPos22(), 0.2F, 0.8F);
      float f5 = MathHelper.clamp(var1.entityStatusS2CPacket(), 0.2F, 0.8F);
      float f6 = var2[2] + f2 + f4 * Math.max(var2[3] - var2[2], 0.0F);
      float f7 = var2[4] + f3 + f5 * Math.max(var2[5] - var2[4], 0.0F);
      int i = this.EnchantItemSpec(this.BotFeatureRegistry(f6));
      int j = this.EnchantItemSpec(f7);
      return new int[]{i, this.on23(var3, j)};
   }

   public float on23(float var1, float var2, boolean var3) {
      float f = Float.isFinite(var2) ? var2 : 0.0F;
      float f1 = Float.isFinite(var1) ? var1 : f;
      f1 = f1 * 0.75F + f * 0.25F;
      float f2 = Math.max(8.0F, Math.abs(f) * 3.0F + 8.0F);
      float f3 = var3 ? 96.0F : 48.0F;
      return MathHelper.clamp(f1, -Math.min(f2, f3), Math.min(f2, f3));
   }

   public float BotFeatureRegistry(float var1) {
      float f = 1696.7242F;
      return var1 - f * (float)Math.floor((var1 + f * 0.5F) / f);
   }

   public int on23(float var1, int var2) {
      int i = (int)Math.ceil((-90.0F - var1) / 0.21217355F);
      int j = (int)Math.floor((90.0F - var1) / 0.21217355F);
      return MathHelper.clamp(var2, i, j);
   }

   public int EnchantItemSpec(double var1) {
      return !Double.isFinite(var1) ? 0 : (int)Math.max(-2147483648L, Math.min(2147483647L, Math.round(var1)));
   }

   public double on23(float var1, int var2, int var3) {
      if (!Float.isFinite(var1)) {
         return 0.0;
      }

      double d0 = Math.max(var3 * 0.05, 0.05);
      double d1 = Math.max(200.0, 2.0 * Math.abs(var2) / d0);
      double d2 = Math.min(d1, 2500.0);
      return Math.max(-d2, Math.min(d2, var1));
   }

   public String ServiceException(float var1) {
      return Integer.toString(Math.round(MathHelper.clamp(var1, 0.0F, 1.0F) * 100.0F));
   }

   public Rotation on23(Rotation var1, int[] var2) {
      int i = this.on23(var1.GuiWalk(), var2[1]);
      return var2[0] == 0 && i == 0 ? var1 : var1.Event08(var2[0] * 0.21217355F, i * 0.21217355F);
   }

   public LivingEntity call009() {
      LivingEntity livingentity = Aura.aura.zClass054();
      return livingentity != null && livingentity.isAlive() ? livingentity : null;
   }

   public Rotation on23(Vec3d var1, Vec3d var2) {
      return RotationMath.Event08(var2.subtract(var1));
   }

   public Vec3d on23(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   public Vec3d[] UiAnimation(Box var1) {
      return new Vec3d[]{
         new Vec3d(var1.minX, var1.minY, var1.minZ),
         new Vec3d(var1.minX, var1.minY, var1.maxZ),
         new Vec3d(var1.minX, var1.maxY, var1.minZ),
         new Vec3d(var1.minX, var1.maxY, var1.maxZ),
         new Vec3d(var1.maxX, var1.minY, var1.minZ),
         new Vec3d(var1.maxX, var1.minY, var1.maxZ),
         new Vec3d(var1.maxX, var1.maxY, var1.minZ),
         new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
      };
   }

   public float SimpleItemBuilder(float var1, float var2) {
      float f = var1;

      while (f - var2 > 180.0F) {
         f -= 360.0F;
      }

      while (f - var2 < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public void int92() {
      this.call078 = new int[0][2];
      this.int451 = 0;
   }

   public void botClient3() {
      this.int403 = Integer.MIN_VALUE;
      this.boolean138 = false;
      this.float360 = 0.0F;
      this.float361 = 0.0F;
      this.boolean197 = false;
      this.float362 = 0.0F;
      this.float363 = 0.0F;
      this.double62 = 0.0;
      this.double63 = 0.0;
      this.double72 = 0.0;
      this.double73 = 0.0;
      if (this.var03 != null) {
         this.var03.reset();
      }

      this.zClass101.reset();
      this.string114 = "idle";
      this.int92();
   }

   public boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   public double ItemSpec(double var1, double var3, double var5) {
      return Math.max(var3, Math.min(var1, var5));
   }
}
