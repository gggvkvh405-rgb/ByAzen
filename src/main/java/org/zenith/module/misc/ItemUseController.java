package org.zenith.module.misc;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.MovementController;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.PreventActionEvent;
import org.zenith.event.RefreshCacheEvent;
import org.zenith.event.StopUsingItemEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "ItemUseController", description = "", category = Category.MISC)
public final class ItemUseController extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ItemUseController itemUseController = new ItemUseController();
   public final BooleanSetting newVersion = new BooleanSetting("module.itemUseController.newVersion", "module.itemUseController.newVersion.desc", false);
   public final BooleanSetting aim = new BooleanSetting("module.itemUseController.aim", "module.itemUseController.aim.desc", false);
   public final MultiSelectSetting modeSetting10 = MultiSelectSetting.on23(
      "module.itemUseController.items",
      "module.itemUseController.items.desc",
      List.of("module.itemUseController.bow", "module.itemUseController.crossbow", "module.itemUseController.snowball", "module.itemUseController.trident")
   );
   public final NumberSetting predictSetting3 = new NumberSetting(
      "module.itemUseController.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );
   boolean cancel = false;
   boolean val312 = false;

   public boolean call080() {
      ItemStack itemstack = minecraftClient3.player.getMainHandStack();
      Item item = itemstack.getItem();
      if (this.modeSetting10.RotationUpdateStartEvent("module.itemUseController.bow") && item == Items.BOW) {
         return true;
      } else if (this.modeSetting10.RotationUpdateStartEvent("module.itemUseController.crossbow") && item == Items.CROSSBOW) {
         return true;
      } else {
         return this.modeSetting10.RotationUpdateStartEvent("module.itemUseController.snowball") && item == Items.SNOWBALL
            ? true
            : this.modeSetting10.RotationUpdateStartEvent("module.itemUseController.trident") && item == Items.TRIDENT;
      }
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (!this.newVersion.isEnabled()) {
         this.cancel = false;
         if (minecraftClient3.getOverlay() == null
            && minecraftClient3.currentScreen == null
            && (ZenithClient.on23().CloudRouter().ZClass092() != null || this.aim.isEnabled() && this.call080())
            && !ZenithClient.on23()
               .CloudRouter()
               .ZClass092()
               .EmoteManager(new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch()))
               .EventMotion(5.0F)
            && (
               minecraftClient3.options.useKey.isPressed()
                     && minecraftClient3.itemUseCooldown == 0
                     && !minecraftClient3.player.isUsingItem()
                     && minecraftClient3.player.getMainHandStack().getMaxUseTime(minecraftClient3.player) == 0
                  || ZenithClient.on23().FileLogger().ColorAnimator(RefreshCacheEvent.class)
            )) {
            Rotation ililiiili1ll1li11 = this.double11();
            Rotation ililiiili1ll1li111 = val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11);
            ZenithClient.on23().CloudRouter().on23(new RotationTask(ililiiili1ll1li111, () -> ililiiili1ll1li111, val001.HudPreviewItem()), 20, this);
            this.cancel = true;
         }
      } else if (minecraftClient3.getOverlay() == null
         && minecraftClient3.currentScreen == null
         && (ZenithClient.on23().CloudRouter().ZClass092() != null || this.aim.isEnabled() && this.call080())
         && (
            minecraftClient3.options.useKey.isPressed()
                  && minecraftClient3.itemUseCooldown == 0
                  && !minecraftClient3.player.isUsingItem()
                  && minecraftClient3.player.getMainHandStack().getMaxUseTime(minecraftClient3.player) == 0
               || ZenithClient.on23().FileLogger().ColorAnimator(RefreshCacheEvent.class)
         )) {
         Rotation ililiiili1ll1li112 = this.double11();
         Rotation ililiiili1ll1li114 = val001.on23(val001.HudPreviewItem(), ililiiili1ll1li112);
         ZenithClient.on23().CloudRouter().on23(new RotationTask(ililiiili1ll1li114, () -> ililiiili1ll1li114, val001.HudPreviewItem()), 20, this);
      }

      this.val312 = false;
      if (!minecraftClient3.options.useKey.isPressed()
         && minecraftClient3.player.isUsingItem()
         && (ZenithClient.on23().CloudRouter().ZClass092() != null || this.aim.isEnabled() && this.call080())) {
         Rotation ililiiili1ll1li113 = this.double11();
         if (!ZenithClient.on23().CloudRouter().LineShader().EmoteManager(this.double11()).EventMotion(5.0F)) {
            Rotation ililiiili1ll1li115 = val001.on23(val001.HudPreviewItem(), ililiiili1ll1li113);
            ZenithClient.on23().CloudRouter().on23(new RotationTask(ililiiili1ll1li115, () -> ililiiili1ll1li115, val001.HudPreviewItem()), 20, this);
            this.val312 = true;
         }
      }
   }

   @EventTarget
   public void ColorAnimator(PreventActionEvent var1) {
      if (!this.newVersion.isEnabled() && this.cancel) {
         var1.cancel();
      }
   }

   public Rotation double11() {
      Rotation ililiiili1ll1li11 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());

      try {
         if (this.aim.isEnabled() && this.call080()) {
            ItemStack itemstack = minecraftClient3.player.getMainHandStack();
            LivingEntity livingentity = Aura.aura.var11813();
            if (!(livingentity instanceof PlayerEntity playerentity)) {
               return ililiiili1ll1li11;
            } else {
               Rotation ililiiili1ll1li111 = RotationMath.Event08(
                  this.ColorAnimator(
                        this.predictSetting3.getCurrent() == 0.0F
                           ? playerentity.getBoundingBox()
                           : MovementController.ColorAnimator(playerentity, (int)this.predictSetting3.getCurrent()).box9
                     )
                     .subtract(
                        MovementController.TargetAcquireEvent(1)
                           .TriggerBot
                           .add(0.0, minecraftClient3.player.getEyeHeight(minecraftClient3.player.getPose()), 0.0)
                     )
               );
               float f = ililiiili1ll1li111.GrimGlide();
               int i = Math.round(ililiiili1ll1li111.GuiWalk());
               float f1 = Float.MAX_VALUE;
               Rotation ililiiili1ll1li112 = ililiiili1ll1li11;

               for (int j = 0; j < 90; j++) {
                  int[] aint = new int[]{i + j, i - j};

                  for (int k : aint) {
                     Rotation ililiiili1ll1li113 = new Rotation(f, k);
                     List<HitResult> list = Predictions.predictions.on23(itemstack, itemstack.getItem(), ililiiili1ll1li113);
                     if (list != null
                        && !list.isEmpty()
                        && list.getFirst() instanceof EntityHitResult entityhitresult
                        && entityhitresult.getEntity().equals(livingentity)) {
                        float f2 = Math.abs(ililiiili1ll1li111.GuiWalk() - k);
                        if (f2 <= 5.0F) {
                           return ililiiili1ll1li113;
                        }

                        if (f2 < f1) {
                           f1 = f2;
                           ililiiili1ll1li112 = ililiiili1ll1li113;
                        }
                     }
                  }
               }

               return ililiiili1ll1li112;
            }
         } else {
            return ililiiili1ll1li11;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         return ililiiili1ll1li11;
      }
   }

   public Vec3d ColorAnimator(Box var1) {
      return new Vec3d(
         MathHelper.lerp(0.5, var1.minX, var1.maxX),
         MathHelper.lerp(0.8, var1.minY, var1.maxY),
         MathHelper.lerp(0.5, var1.minZ, var1.maxZ)
      );
   }

   @EventTarget
   public void UiAnimation(StopUsingItemEvent var1) {
      if (this.val312) {
         var1.cancel();
      }
   }
}
