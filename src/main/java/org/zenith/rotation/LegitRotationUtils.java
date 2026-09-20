package org.zenith.rotation;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class LegitRotationUtils {
   public static final LegitRotationUtils.Interpolation zClass070Var159 = new LegitRotationUtils.Interpolation();
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static Rotation EmotePlayback(Vec3d var0) {
      return on23(zClass070Var159, var0);
   }

   public static Rotation on23(LegitRotationUtils.Interpolation var0, Vec3d var1) {
      return on23(var0, RotationMath.BotChatEvent(var1));
   }

   public static Rotation ModuleStateStore(Rotation var0) {
      return on23(zClass070Var159, var0);
   }

   public static Rotation on23(LegitRotationUtils.Interpolation var0, Rotation var1) {
      return on23(var0, var1, 34.0F, 18.0F, 0.55F);
   }

   public static Rotation UiAnimation(Rotation var0, float var1, float var2, float var3) {
      return on23(zClass070Var159, var0, var1, var2, var3);
   }

   public static Rotation on23(LegitRotationUtils.Interpolation var0, Rotation var1, float var2, float var3, float var4) {
      LegitRotationUtils.Interpolation l1l11l1i1li11lil1lli_ii1il11l111ii11iil = var0 == null ? zClass070Var159 : var0;
      Rotation ililiiili1ll1li11 = l1l11l1i1li11lil1lli_ii1il11l111ii11iil.var1188 == null
         ? RotationMath.boolean122()
         : l1l11l1i1li11lil1lli_ii1il11l111ii11iil.var1188;
      Rotation ililiiili1ll1li111 = on23(l1l11l1i1li11lil1lli_ii1il11l111ii11iil, ililiiili1ll1li11, var1, var2, var3, var4);
      return var1;
   }

   public static Rotation on23(Rotation var0, Rotation var1, float var2, float var3, float var4) {
      return on23(zClass070Var159, var0, var1, var2, var3, var4);
   }

   public static Rotation on23(LegitRotationUtils.Interpolation var0, Rotation var1, Rotation var2, float var3, float var4, float var5) {
      LegitRotationUtils.Interpolation l1l11l1i1li11lil1lli_ii1il11l111ii11iil = var0 == null ? zClass070Var159 : var0;
      UiAnimation(l1l11l1i1li11lil1lli_ii1il11l111ii11iil);
      float f = MathHelper.wrapDegrees(var2.GrimGlide() - var1.GrimGlide());
      float f1 = var2.GuiWalk() - var1.GuiWalk();
      float f2 = (float)Math.hypot(f, f1);
      float f3 = MathHelper.clamp(f2 / 12.0F, 0.0F, 1.0F);
      float f4 = MathHelper.clamp(f2 / 45.0F, 0.18F, 1.0F);
      on23(l1l11l1i1li11lil1lli_ii1il11l111ii11iil, f3);
      float f5 = MathHelper.clamp(var5 * l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float97, 0.08F, 0.95F);
      float f6 = CloudPoller(f, var3 * l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float96, f5, f4);
      float f7 = CloudPoller(f1, var4 * l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float96, f5, f4);
      float f8 = EnchantItemSpec(f, l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float98 + l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float100, f3);
      float f9 = EnchantItemSpec(f1, l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float99 + l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float101, f3);
      float f10 = MathHelper.clamp(f + f8, -f6, f6);
      float f11 = MathHelper.clamp(f1 + f9, -f7, f7);
      l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float94 = MathHelper.lerp(f5, l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float94, f10);
      l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float95 = MathHelper.lerp(f5, l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float95, f11);
      l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float94 = SimpleItemBuilder(l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float94, f, f2);
      l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float95 = SimpleItemBuilder(l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float95, f1, f2);
      Rotation ililiiili1ll1li11 = var1.Event08(l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float94, l1l11l1i1li11lil1lli_ii1il11l111ii11iil.float95);
      return ililiiili1ll1li11.CosmeticManager(var1);
   }

   public static void CloudPoller(Rotation var0) {
      if (minecraftClient3.player != null && var0 != null) {
         minecraftClient3.player.setYaw(var0.GrimGlide());
         minecraftClient3.player.setPitch(var0.GuiWalk());
      }
   }

   public static void reset() {
      on23(zClass070Var159);
   }

   public static void on23(LegitRotationUtils.Interpolation var0) {
      LegitRotationUtils.Interpolation l1l11l1i1li11lil1lli_ii1il11l111ii11iil = var0 == null ? zClass070Var159 : var0;
      l1l11l1i1li11lil1lli_ii1il11l111ii11iil.reset();
   }

   public static void UiAnimation(LegitRotationUtils.Interpolation var0) {
      if (var0.int167-- <= 0) {
         var0.int167 = MediaTrackInfo(3, 9);
         var0.float96 = EmotePlayback(0.86F, 1.18F);
         var0.float97 = EmotePlayback(0.82F, 1.08F);
         var0.float98 = ModuleToggleEvent(0.18F);
         var0.float99 = ModuleToggleEvent(0.11F);
      }
   }

   public static void on23(LegitRotationUtils.Interpolation var0, float var1) {
      float f = 0.025F + var1 * 0.08F;
      var0.float100 = MathHelper.lerp(0.22F, var0.float100, ModuleToggleEvent(f));
      var0.float101 = MathHelper.lerp(0.22F, var0.float101, ModuleToggleEvent(f * 0.65F));
   }

   public static float EnchantItemSpec(float var0, float var1, float var2) {
      float f = Math.signum(var0);
      float f1 = 0.0F;
      if (Math.abs(var0) > 8.0F) {
         f1 = MathHelper.clamp(var0 * EmotePlayback(0.01F, 0.035F), -1.15F, 1.15F);
      }

      return f1 + var1 * var2 + f * EmotePlayback(0.0F, 0.018F) * var2;
   }

   public static float SimpleItemBuilder(float var0, float var1, float var2) {
      return !(var2 > 2.0F) && Math.signum(var0) != Math.signum(var1) ? var0 * EmotePlayback(0.35F, 0.7F) : var0;
   }

   public static float CloudPoller(float var0, float var1, float var2, float var3) {
      float f = Math.abs(var0);
      float f1 = Math.max(0.1F, var1);
      float f2 = MathHelper.clamp(f / 35.0F, 0.22F, 1.0F);
      float f3 = 0.72F + var3 * EmotePlayback(0.22F, 0.38F);
      return Math.max(Rotation.logger2(), f1 * f2 * var2 * f3);
   }

   public static int MediaTrackInfo(int var0, int var1) {
      return ThreadLocalRandom.current().nextInt(var0, var1 + 1);
   }

   public static float EmotePlayback(float var0, float var1) {
      return (float)ThreadLocalRandom.current().nextDouble(var0, var1);
   }

   public static float ModuleToggleEvent(float var0) {
      return EmotePlayback(-var0, var0);
   }

   public LegitRotationUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }


   public static final class Interpolation {
      public Rotation var1188;
      public float float94;
      public float float95;
      public float float96 = 1.0F;
      public float float97 = 1.0F;
      public float float98;
      public float float99;
      public float float100;
      public float float101;
      public int int167;

      public void reset() {
         this.var1188 = null;
         this.float94 = 0.0F;
         this.float95 = 0.0F;
         this.float96 = 1.0F;
         this.float97 = 1.0F;
         this.float98 = 0.0F;
         this.float99 = 0.0F;
         this.float100 = 0.0F;
         this.float101 = 0.0F;
         this.int167 = 0;
      }
   }
}
