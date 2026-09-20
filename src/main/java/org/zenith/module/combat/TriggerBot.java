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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.MovementController;
import org.zenith.core.PlayerStateService;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.TargetAcquireEvent;
import org.zenith.event.EventInjectAddEntity;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PreventActionEvent;
import org.zenith.event.SprintStateEvent;
import org.zenith.managers.TargetSelector;
import org.zenith.rotation.Rotation;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MathUtils;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "TriggerBot", category = Category.COMBAT, description = "Бьет таргета")
public final class TriggerBot extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TriggerBot triggerBot = new TriggerBot();
   public final ModeSetting sprintMode2 = new ModeSetting("module.aura.sprintMode", "module.aura.sprintMode.desc");
   public final ModeSetting.Option modeSetting3Var15949 = new ModeSetting.Option(this.sprintMode2, "module.aura.sprintHvh");
   public final ModeSetting.Option modeSetting3Var15950 = new ModeSetting.Option(this.sprintMode2, "module.aura.sprintNormal").int210();
   public final ModeSetting.Option modeSetting3Var15951 = new ModeSetting.Option(this.sprintMode2, "module.aura.sprintLegit");
   public final ModeSetting.Option modeSetting3Var15952 = new ModeSetting.Option(this.sprintMode2, "module.aura.sprintSuperLegit");
   public final ModeSetting.Option modeSetting3Var15953 = new ModeSetting.Option(this.sprintMode2, "module.aura.sprintNone");
   public final NumberSetting distance6 = new NumberSetting("module.aura.distance", 3.0F, 0.5F, 6.0F, 0.1F, "module.aura.distance.desc", "b");
   public final MultiSelectSetting settings2 = new MultiSelectSetting("module.aura.settings", "module.aura.settings.desc");
   public final MultiSelectSetting.Option modeSettingVar15915 = new MultiSelectSetting.Option(this.settings2, "module.aura.shieldBreak", true);
   public final MultiSelectSetting.Option modeSettingVar15916 = new MultiSelectSetting.Option(this.settings2, "module.aura.shielRealese", true);
   public final MultiSelectSetting.Option modeSettingVar15917 = new MultiSelectSetting.Option(this.settings2, "module.aura.eatUseAttack", true);
   public final MultiSelectSetting.Option modeSettingVar15918 = new MultiSelectSetting.Option(this.settings2, "module.aura.attackIgnoreWals", true);
   public final ModeSetting cooldownMode2 = new ModeSetting("module.aura.cooldownMode", "module.aura.cooldownMode.desc");
   public final ModeSetting.Option modeSetting3Var15954 = new ModeSetting.Option(this.cooldownMode2, "module.aura.slowMode");
   public final ModeSetting.Option modeSetting3Var15955 = new ModeSetting.Option(this.cooldownMode2, "module.aura.speedMode").int210();
   public final ModeSetting.Option modeSetting3Var15956 = new ModeSetting.Option(this.cooldownMode2, "module.aura.customMode");
   public final NumberSetting timeCooldown2 = new NumberSetting(
      "module.aura.timeCooldown", 0.0F, -1.0F, 1.0F, 0.5F, "module.aura.timeCooldown.desc", "t", this.modeSetting3Var15956::isSelected, null
   );
   public final NumberSetting attackCooldown2 = new NumberSetting(
      "module.aura.attackCooldown", 1.0F, 0.0F, 1.0F, 0.01F, "module.aura.attackCooldown.desc", "%", this.modeSetting3Var15956::isSelected, null
   );
   public final BooleanSetting randomDelay = new BooleanSetting("module.aura.randomDelay", "module.aura.randomDelay.desc", false);
   public final SettingGroup u041dU0430U0441U0442U0440U043eU0439U043aU0438U0446U0435U043bU0438 = new SettingGroup(
      "Настройки цели",
      "module.aura.targetWindow.desc",
      () -> true,
      MultiSelectSetting.on23(
         "module.aura.targetTypeSetting",
         "module.aura.targetTypeSetting.desc",
         List.of("module.aura.targetPlayers", "module.aura.noarmor", "module.aura.targetHostile", "module.aura.targetPeaceful")
      ),
      MultiSelectSetting.on23(
         "module.aura.targetSortSetting",
         "module.aura.targetSortSetting.desc",
         List.of("module.aura.targetFov", "module.aura.targetArmor", "module.aura.targetHp", "module.aura.targetDistance")
      ),
      new BooleanSetting("module.aura.safeTarget", "module.aura.safeTarget.desc", false)
   );
   public final BooleanSetting onlyCrit2 = new BooleanSetting("module.aura.onlyCrit", "module.aura.onlyCrit.desc", true);
   public final BooleanSetting smartCrit2 = new BooleanSetting("module.aura.smartCrit", "module.aura.smartCrit.desc", false, this.onlyCrit2::isEnabled);
   public LivingEntity livingEntity = null;
   public Slot slot = null;
   public LivingEntity livingEntity2 = null;
   public final CooldownTimer zClass06738 = new CooldownTimer();
   public boolean booleanField = false;
   public boolean boolean2 = false;
   public boolean boolean3 = false;
   public CooldownTimer zClass06739;
   int val478 = 0;
   int val335 = 0;

   @EventTarget
   public void on23(EventInjectAddEntity var1) {
      if (var1.ElytraFly() instanceof ClientPlayerEntity) {
         this.livingEntity = null;
         this.livingEntity2 = null;
      }
   }

   @EventTarget
   public void on23(TargetAcquireEvent var1) {
      if (this.livingEntity != null && this.var135() && this.boolean2) {
         this.booleanField = false;
         this.boolean2 = false;
         this.boolean3 = true;
      }
   }

   @EventTarget
   public void ColorAnimator(EventTick var1) {
      if (this.zClass06739 == null) {
         this.zClass06739 = new CooldownTimer();
      }

      if (this.boolean3) {
         if (this.modeSetting3Var15949.isSelected() && ZenithClient.on23().CloudApiClient().var11917()) {
            minecraftClient3.player.setSprinting(false);
            minecraftClient3.player.sendSprintingPacket();
         }

         PlayerStateService.Easing(this.livingEntity);
         this.val478 = minecraftClient3.player.age - this.val335;
         this.val335 = minecraftClient3.player.age;
      }
   }

   @EventTarget
   public void UiAnimation(RotationUpdateStartEvent var1) {
      if (minecraftClient3.player.isDead()) {
         this.livingEntity = null;
      } else {
         this.boolean3 = false;
         this.livingEntity = this.zClass022();
         if (minecraftClient3.targetedEntity instanceof LivingEntity livingentity
            && TargetSelector.on23(
               ((MultiSelectSetting)this.u041dU0430U0441U0442U0440U043eU0439U043aU0438U0446U0435U043bU0438.getSettings().get(0)).zClass100Var143Var143(), livingentity
            )) {
            this.livingEntity = livingentity;
         }

         if (this.livingEntity != null) {
            if (minecraftClient3.world.getEntityById(this.livingEntity.getId()) instanceof LivingEntity livingentity1) {
               this.livingEntity = livingentity1;
            } else {
               this.livingEntity = null;
            }

            this.livingEntity2 = this.livingEntity;
         }

         this.booleanField = false;
         this.boolean2 = false;
         if (this.livingEntity != null) {
            this.booleanField = this.zClass110();
            this.boolean2 = this.var162();
            Slot slot1 = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, var0 -> var0.getStack().getItem() instanceof AxeItem);
            boolean flag = this.modeSettingVar15915.isEnabled()
               && this.zClass06738.EventModifyMouseRotationInput(300L)
               && ZenithClient.on23().FileLogger().Easing(TriggerBot.class)
               && slot1 != null
               && EffectEngine.NbtEditor(this.livingEntity);
            if (flag) {
               if (minecraftClient3.player.getMainHandStack().getItem() instanceof AxeItem) {
                  if (this.var135()) {
                     PlayerStateService.Easing(this.livingEntity);
                     this.zClass06738.reset();
                     this.booleanField = false;
                     this.boolean2 = false;
                  }
               } else if (TaskScheduler.Easing(TriggerBot.class)) {
                  TaskScheduler.on23(TriggerBot.class, () -> {
                     if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                        ScreenUtils.closeScreen();
                     }

                     ScreenUtils.on23(slot1, Hand.MAIN_HAND, true);
                  });
                  if (this.slot == null) {
                     this.slot = slot1;
                  }
               }
            } else if (this.slot != null && TaskScheduler.Easing(TriggerBot.class)) {
               Slot slot = this.slot;
               this.slot = null;
               TaskScheduler.on23(TriggerBot.class, () -> {
                  if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                     ScreenUtils.closeScreen();
                  }

                  ScreenUtils.on23(slot, Hand.MAIN_HAND, true);
               });
            }

            if (this.booleanField
               && this.modeSettingVar15916.isEnabled()
               && minecraftClient3.player.isUsingItem()
               && minecraftClient3.player.getActiveItem().getItem() instanceof ShieldItem) {
               minecraftClient3.interactionManager.stopUsingItem(minecraftClient3.player);
            }
         }
      }
   }

   public boolean var135() {
      return this.ColorAnimator(val002.LineShader());
   }

   public boolean ColorAnimator(Rotation var1) {
      return this.UiAnimation(var1, this.var1357(), this.livingEntity.getBoundingBox()) || this.UiAnimation(var1, this.var1357(), this.var11812());
   }

   public boolean UiAnimation(Rotation var1, Vec3d var2, Box var3) {
      return RaycastUtils.on23(var1, var2, var3, this.distance6.getCurrent(), !this.modeSettingVar15918.isEnabled());
   }

   public Vec3d var1357() {
      return minecraftClient3.player.getCameraPosVec(1.0F);
   }

   public Vec3d on23(Box var1, float var2) {
      return new Vec3d(
         MathHelper.lerp(0.5, var1.minX, var1.maxX),
         MathHelper.lerp(var2, var1.minY, var1.maxY),
         MathHelper.lerp(0.5, var1.minZ, var1.maxZ)
      );
   }

   public Box var11812() {
      return this.livingEntity.getBoundingBox();
   }

   @EventTarget
   public void Easing(MovementInputEvent var1) {
      if (this.livingEntity != null) {
         this.ColorAnimator(var1);
      }
   }

   @EventTarget(0)
   public void on23(PreventActionEvent var1) {
      if (this.boolean3) {
         var1.cancel();
      }
   }

   public boolean zClass110() {
      return this.CommandManager(1);
   }

   public boolean CommandManager(int var1) {
      MovementController il11i11i111i1i1l1il = MovementController.TargetAcquireEvent(var1);
      if (EffectEngine.NbtEditor(this.livingEntity)) {
         return false;
      } else if (minecraftClient3.player.isUsingItem()
         && (!this.modeSettingVar15916.isEnabled() || !(minecraftClient3.player.getActiveItem().getItem() instanceof ShieldItem))
         && !this.modeSettingVar15917.isEnabled()) {
         return false;
      } else {
         return !this.onlyCrit2.isEnabled()
               || PlayerStateService.on23(il11i11i111i1i1l1il)
               || PlayerStateService.UiAnimation(il11i11i111i1i1l1il)
               || this.smartCrit2.isEnabled() && !minecraftClient3.options.jumpKey.isPressed()
            ? this.on23(1.0F, !this.zClass013())
            : false;
      }
   }

   public boolean zClass013() {
      if (!minecraftClient3.player.isGliding() && !PlayerStateService.ScreenProjection()) {
         return !this.onlyCrit2.isEnabled() ? false : !this.smartCrit2.isEnabled() || minecraftClient3.options.jumpKey.isPressed();
      } else {
         return false;
      }
   }

   public boolean var162() {
      if (EffectEngine.NbtEditor(this.livingEntity)) {
         return false;
      }

      if (minecraftClient3.player.isUsingItem() && !this.modeSettingVar15917.isEnabled()) {
         return false;
      }

      if (!this.zClass013()
         || PlayerStateService.RandomUtils()
            && (!ZenithClient.on23().CloudApiClient().var11917() || this.modeSetting3Var15953.isSelected() || this.modeSetting3Var15949.isSelected())) {
         if (this.randomDelay.isEnabled()) {
            long i = minecraftClient3.player.age - this.val335;
            if (MathUtils.BotDisconnectEvent((float)i, this.val478) == 0.0F) {
               return false;
            }
         }

         return this.on23(0.0F, !this.zClass013() || this.booleanField);
      } else {
         return false;
      }
   }

   public boolean on23(float var1, boolean var2) {
      if (this.cooldownMode2.is(1)) {
         return !PlayerStateService.ScreenProjection() && !minecraftClient3.player.isOnGround()
            ? minecraftClient3.player.getAttackCooldownProgress(0.5F) > 0.9
            : minecraftClient3.player.getAttackCooldownProgress(0.5F) >= 1.0F;
      }

      if (!this.modeSetting3Var15954.isSelected()) {
         var2 = false;
      }

      float f = this.modeSetting3Var15956.isSelected() ? this.timeCooldown2.getCurrent() : 1.0F;
      float f1 = this.modeSetting3Var15956.isSelected() ? this.attackCooldown2.getCurrent() : 1.0F;
      return var2 && !minecraftClient3.player.isSubmergedInWater()
         ? minecraftClient3.player.getAttackCooldownProgress(0.0F + var1) == 1.0F
         : minecraftClient3.player.getAttackCooldownProgress(f + var1) >= f1;
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   public void ColorAnimator(MovementInputEvent var1) {
      if (this.module4()) {
         boolean flag = minecraftClient3.options.sprintKey.isPressed();
         boolean flag1 = minecraftClient3.options.forwardKey.isPressed();
         if (this.modeSetting3Var15952.isSelected()) {
            if (!minecraftClient3.player.isOnGround()) {
               flag = false;
               if (minecraftClient3.player.isSprinting()) {
                  flag1 = false;
               }
            }
         } else if (!this.boolean3 && !minecraftClient3.player.isOnGround() && this.zClass110()) {
            if (this.modeSetting3Var15951.isSelected()) {
               flag = false;
               if (minecraftClient3.player.isSprinting()) {
                  flag1 = false;
               }
            }

            if (this.modeSetting3Var15950.isSelected()) {
               if (minecraftClient3.player.isSprinting()) {
                  minecraftClient3.player.setSprinting(false);
               }

               flag = false;
            }
         }

         if (!flag) {
            minecraftClient3.options.sprintKey.setPressed(false);
            var1.TextScanner(false);
         }

         if (!flag1) {
            var1.ItemSpec(false);
         }
      }
   }

   @EventTarget
   public void on23(SprintStateEvent var1) {
      if (this.livingEntity != null
         && !this.boolean3
         && !minecraftClient3.player.isOnGround()
         && this.zClass110()
         && this.modeSetting3Var15950.isSelected()) {
         var1.PotionItemBuilder(false);
      }
   }

   public boolean module4() {
      return !this.modeSetting3Var15953.isSelected() && !PlayerStateService.ScreenProjection();
   }

   public LivingEntity zClass022() {
      return TargetSelector.on23(
         minecraftClient3.world.getEntities(),
         this.distance6.getCurrent() + 0.5F,
         this.modeSettingVar15918.isEnabled(),
         ((MultiSelectSetting)this.u041dU0430U0441U0442U0440U043eU0439U043aU0438U0446U0435U043bU0438.getSettings().get(0)).zClass100Var143Var143(),
         ((MultiSelectSetting)this.u041dU0430U0441U0442U0440U043eU0439U043aU0438U0446U0435U043bU0438.getSettings().get(1)).zClass100Var143Var143(),
         ((BooleanSetting)this.u041dU0430U0441U0442U0440U043eU0439U043aU0438U0446U0435U043bU0438.getSettings().get(2)).isEnabled()
      );
   }

   public LivingEntity zClass054() {
      return this.isEnabled() ? this.livingEntity : null;
   }

   public LivingEntity var11813() {
      return this.livingEntity2;
   }

   public boolean int391() {
      return this.booleanField;
   }

   public boolean zClass007Var159() {
      return this.boolean2;
   }

   public CooldownTimer string35() {
      return this.zClass06739;
   }
}
