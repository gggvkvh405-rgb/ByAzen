package org.zenith.module.player;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.TargetAcquireEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.util.RaycastUtils;

@ModuleInfo(name = "AutoLoot", category = Category.PLAYER, description = "")
public final class AutoLoot extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final BooleanSetting ignoreWalls = new BooleanSetting("module.autoLoot.ignoreWalls", "module.autoLoot.ignoreWalls.desc", true);
   public final BooleanSetting ignoreEntity = new BooleanSetting("module.autoLoot.ignoreEntity", "module.autoLoot.ignoreEntity.desc", true);
   public static final AutoLoot autoLoot = new AutoLoot();
   public MerchantEntity merchantEntity;

   @EventTarget
   public void NbtEditor(RotationUpdateStartEvent var1) {
      EffectEngine.double66()
         .filter(MerchantEntity.class::isInstance)
         .map(MerchantEntity.class::cast)
         .filter(var0 -> var0.hasStackEquipped(EquipmentSlot.MAINHAND) || var0.hasStackEquipped(EquipmentSlot.OFFHAND))
         .findFirst()
         .ifPresent(var1x -> {
            double d0 = var1x.getX() - minecraftClient3.player.getX();
            double d1 = var1x.getY() - minecraftClient3.player.getY();
            double d2 = var1x.getZ() - minecraftClient3.player.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            double d4 = MathHelper.wrapDegrees(-(MathHelper.atan2(d1, d3) * 180.0 / (float) Math.PI));
            double d5 = MathHelper.wrapDegrees(MathHelper.atan2(d2, d0) * 180.0 / (float) Math.PI - 90.0);
            Rotation ililiiili1ll1li11 = new Rotation((float)d5, (float)d4);
            val002.on23(new RotationTask(ililiiili1ll1li11, () -> {
               this.merchantEntity = var1x;
               return val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11);
            }, val001.HudPreviewItem()), 3, this);
         });
   }

   @EventTarget
   public void Easing(TargetAcquireEvent var1) {
      if (this.merchantEntity != null) {
         if (!this.ignoreWalls.isEnabled() && !this.ignoreEntity.isEnabled()) {
            if (minecraftClient3.crosshairTarget instanceof EntityHitResult entityhitresult1 && entityhitresult1.getEntity() == this.merchantEntity) {
               minecraftClient3.interactionManager.interactEntityAtLocation(minecraftClient3.player, entityhitresult1.getEntity(), entityhitresult1, Hand.OFF_HAND);
            }

            this.merchantEntity = null;
         } else {
            EntityHitResult entityhitresult = RaycastUtils.on23(
               3.0, val003.CloudRouter().LineShader(), var1x -> !this.ignoreEntity.isEnabled() || this.merchantEntity == var1x
            );
            if (!this.ignoreWalls.isEnabled()
               && !RaycastUtils.on23(
                  val003.CloudRouter().LineShader(), minecraftClient3.player.getCameraPosVec(1.0F), this.merchantEntity.getBoundingBox(), 3.0, true
               )) {
               this.merchantEntity = null;
            } else {
               if (entityhitresult.getEntity() == this.merchantEntity) {
                  ActionResult actionresult = minecraftClient3.interactionManager
                     .interactEntityAtLocation(minecraftClient3.player, entityhitresult.getEntity(), entityhitresult, Hand.OFF_HAND);
                  if (actionresult instanceof Success) {
                     minecraftClient3.player.swingHand(Hand.OFF_HAND);
                  }
               }

               this.merchantEntity = null;
            }
         }
      }
   }
}
