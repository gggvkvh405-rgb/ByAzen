package org.zenith.module.combat;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ZenithClient;
import org.zenith.core.MovementController;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.NumberSetting;
import org.zenith.util.RaycastUtils;

@ModuleInfo(name = "TridentAimbot", description = "", category = Category.COMBAT)
public final class TridentAimbot extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TridentAimbot tridentAimbot = new TridentAimbot();
   public static final int int319 = 1;
   public static final double double106 = 2.5;
   public static final int int320 = 90;
   public static final int int321 = 50;
   public final NumberSetting predictSetting4 = new NumberSetting(
      "module.elytraTarget.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (minecraftClient3.player.isUsingItem() && minecraftClient3.player.getActiveItem().getItem() == Items.TRIDENT) {
         Rotation ililiiili1ll1li11 = this.double11();
         if (ililiiili1ll1li11 == null) {
            return;
         }

         Rotation ililiiili1ll1li111 = val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11);
         ZenithClient.on23().CloudRouter().on23(new RotationTask(ililiiili1ll1li111, () -> ililiiili1ll1li111, val001.HudPreviewItem()), 20, this);
      }
   }

   public Rotation double11() {
      Rotation ililiiili1ll1li11 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());

      try {
         ItemStack itemstack = minecraftClient3.player.getActiveItem();
         MovementController il11i11i111i1i1l1il = MovementController.TargetAcquireEvent(1);
         Vec3d vec3d = this.ColorAnimator(il11i11i111i1i1l1il);
         LivingEntity livingentity = this.on23(il11i11i111i1i1l1il, itemstack);
         if (livingentity == null) {
            return null;
         }

         Box box = this.TextScanner(livingentity);
         Rotation ililiiili1ll1li111 = Rotation.ItemServiceBase(this.ColorAnimator(box), vec3d);
         return this.on23(itemstack, il11i11i111i1i1l1il, ililiiili1ll1li111, box);
      } catch (Exception exception) {
         exception.printStackTrace();
         return ililiiili1ll1li11;
      }
   }

   public Vec3d ItemSpec(LivingEntity var1) {
      return this.ColorAnimator(this.TextScanner(var1));
   }

   public LivingEntity zClass054() {
      return minecraftClient3.player.isUsingItem() && minecraftClient3.player.getActiveItem().getItem() == Items.TRIDENT
         ? this.on23(MovementController.TargetAcquireEvent(1), minecraftClient3.player.getActiveItem())
         : null;
   }

   public LivingEntity on23(MovementController var1, ItemStack var2) {
      List<HitResult> list = this.on23(var2, RotationMath.boolean122(), var1);
      if (list != null && !list.isEmpty()) {
         HitResult hitresult = list.getFirst();
         if (hitresult == null) {
            return null;
         } else if (hitresult instanceof EntityHitResult entityhitresult
            && entityhitresult.getEntity() instanceof LivingEntity livingentity
            && !ZenithClient.on23().MediaTrackInfo().UiAnimation(livingentity)) {
            return livingentity;
         } else {
            PlayerEntity playerentity1 = null;
            double d1 = Double.MAX_VALUE;

            for (PlayerEntity playerentity : minecraftClient3.world.getPlayers()) {
               if (minecraftClient3.player != playerentity && !ZenithClient.on23().MediaTrackInfo().UiAnimation(playerentity)) {
                  double d0 = this.ItemSpec(playerentity).squaredDistanceTo(hitresult.getPos());
                  if (d0 < d1) {
                     d1 = d0;
                     playerentity1 = playerentity;
                  }
               }
            }

            return playerentity1;
         }
      } else {
         return null;
      }
   }

   public Vec3d ColorAnimator(Box var1) {
      return new Vec3d(
         MathHelper.lerp(0.5, var1.minX, var1.maxX),
         MathHelper.lerp(0.8, var1.minY, var1.maxY),
         MathHelper.lerp(0.5, var1.minZ, var1.maxZ)
      );
   }

   public List<HitResult> on23(ItemStack var1, Rotation var2, MovementController var3) {
      HitResult hitresult = Predictions.predictions
         .on23(
            this.ColorAnimator(var3),
            var3.vec3d22,
            var2.int202(),
            new TridentEntity(minecraftClient3.world, minecraftClient3.player, var1),
            this.float64()
         );
      return hitresult == null ? List.of() : List.of(hitresult);
   }

   public Vec3d ColorAnimator(MovementController var1) {
      return var1.TriggerBot.add(0.0, minecraftClient3.player.getEyeHeight(minecraftClient3.player.getPose()), 0.0);
   }

   public int double12() {
      return 1 + (int)this.predictSetting4.getCurrent();
   }

   public Box TextScanner(LivingEntity var1) {
      if (var1 instanceof PlayerEntity playerentity) {
         return MovementController.ColorAnimator(playerentity, this.double12()).box9;
      } else {
         Vec3d vec3d = var1.getEntityPos().subtract(var1.lastX, var1.lastY, var1.lastZ);
         return var1.getBoundingBox().offset(vec3d.multiply(1.0));
      }
   }

   public Rotation on23(ItemStack var1, MovementController var2, Rotation var3, Box var4) {
      TridentEntity tridententity = new TridentEntity(minecraftClient3.world, minecraftClient3.player, var1);
      Vec3d vec3d = this.ColorAnimator(var2);
      Vec3d vec3d1 = var2.vec3d22;
      double d0 = this.float64();
      if (this.on23(var3, vec3d, vec3d1, var4, tridententity, d0)) {
         return var3;
      }

      int i = Math.round(var3.GrimGlide());
      int j = Math.round(var3.GuiWalk());
      Integer integer = this.on23(i, j, vec3d, vec3d1, var4, tridententity, d0);
      if (integer == null) {
         return null;
      }

      Rotation ililiiili1ll1li11 = new Rotation(i, integer.intValue());
      return this.on23(ililiiili1ll1li11, vec3d, vec3d1, var4, tridententity, d0)
         ? ililiiili1ll1li11
         : this.UiAnimation(i, integer, vec3d, vec3d1, var4, tridententity, d0);
   }

   public Integer on23(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      for (int i = 0; i <= 90; i++) {
         int j = var2 + i;
         if (this.Easing(var1, j, var3, var4, var5, var6, var7)) {
            return j;
         }

         if (i != 0) {
            int k = var2 - i;
            if (this.Easing(var1, k, var3, var4, var5, var6, var7)) {
               return k;
            }
         }
      }

      return null;
   }

   public Rotation UiAnimation(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      for (int i = 1; i <= 50; i++) {
         Rotation ililiiili1ll1li11 = new Rotation(var1 + i, var2);
         if (this.on23(ililiiili1ll1li11, var3, var4, var5, var6, var7)) {
            return ililiiili1ll1li11.Event08(3.0F, 0.0F);
         }

         Rotation ililiiili1ll1li111 = new Rotation(var1 - i, var2);
         if (this.on23(ililiiili1ll1li111, var3, var4, var5, var6, var7)) {
            return ililiiili1ll1li111.Event08(-3.0F, 0.0F);
         }
      }

      return null;
   }

   public boolean Easing(int var1, int var2, Vec3d var3, Vec3d var4, Box var5, TridentEntity var6, double var7) {
      Rotation ililiiili1ll1li11 = new Rotation(var1, var2);
      Vec3d vec3d = ililiiili1ll1li11.int202();
      double d0 = vec3d.length();
      if (d0 <= 1.0E-6) {
         return false;
      }

      Vec3d vec3d1 = this.ColorAnimator(var5);
      Vec3d vec3d2 = new Vec3d(vec3d1.x - var3.x, 0.0, vec3d1.z - var3.z);
      double d1 = vec3d2.horizontalLength();
      if (d1 <= 1.0E-6) {
         return true;
      }

      vec3d2 = vec3d2.normalize();
      Vec3d vec3d3 = var3;
      Vec3d vec3d4 = vec3d.multiply(var7 / d0).add(var4);

      for (int i = 0; i < 300; i++) {
         Vec3d vec3d5 = vec3d3;
         vec3d3 = vec3d3.add(vec3d4);
         Vec3d vec3d6 = Predictions.predictions.on23(var6, vec3d5, vec3d4);
         BlockHitResult blockhitresult = RaycastUtils.on23(vec3d5, vec3d3, ShapeType.COLLIDER, var6);
         double d2 = this.on23(vec3d5, vec3d3, blockhitresult);
         double d3 = this.UiAnimation(vec3d5, var3, vec3d2);
         double d4 = this.UiAnimation(vec3d3, var3, vec3d2);
         if (d4 >= d1) {
            double d5 = d4 - d3;
            double d6 = d5 <= 1.0E-6 ? 0.0 : MathHelper.clamp((d1 - d3) / d5, 0.0, 1.0);
            if (!(d6 <= d2)) {
               return false;
            }

            double d7 = MathHelper.lerp(d6, vec3d5.y, vec3d3.y);
            return d7 >= var5.minY && d7 <= var5.maxY;
         }

         if (d2 < 1.0 || vec3d3.y < -128.0) {
            return false;
         }

         vec3d4 = vec3d6;
      }

      return false;
   }

   public boolean on23(Rotation var1, Vec3d var2, Vec3d var3, Box var4, TridentEntity var5, double var6) {
      Vec3d vec3d = var1.int202();
      double d0 = vec3d.length();
      if (d0 <= 1.0E-6) {
         return false;
      }

      Vec3d vec3d1 = var2;
      Vec3d vec3d2 = vec3d.multiply(var6 / d0).add(var3);

      for (int i = 0; i < 300; i++) {
         Vec3d vec3d3 = vec3d1;
         vec3d1 = vec3d1.add(vec3d2);
         Vec3d vec3d4 = Predictions.predictions.on23(var5, vec3d3, vec3d2);
         BlockHitResult blockhitresult = RaycastUtils.on23(vec3d3, vec3d1, ShapeType.COLLIDER, var5);
         double d1 = blockhitresult.getType() == Type.MISS ? Double.POSITIVE_INFINITY : vec3d3.squaredDistanceTo(blockhitresult.getPos());
         Optional<Vec3d> optional = var4.raycast(vec3d3, vec3d1);
         if (optional.isPresent() && vec3d3.squaredDistanceTo(optional.get()) <= d1) {
            return true;
         }

         if (blockhitresult.getType() != Type.MISS || vec3d1.y < -128.0) {
            return false;
         }

         vec3d2 = vec3d4;
      }

      return false;
   }

   public double float64() {
      return 2.5;
   }

   public double UiAnimation(Vec3d var1, Vec3d var2, Vec3d var3) {
      Vec3d vec3d = var1.subtract(var2);
      return vec3d.x * var3.x + vec3d.z * var3.z;
   }

   public double on23(Vec3d var1, Vec3d var2, BlockHitResult var3) {
      if (var3.getType() == Type.MISS) {
         return Double.POSITIVE_INFINITY;
      }

      double d0 = var1.distanceTo(var2);
      return d0 <= 1.0E-6 ? 0.0 : var1.distanceTo(var3.getPos()) / d0;
   }
}
