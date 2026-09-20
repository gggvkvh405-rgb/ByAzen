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
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.zenith.event.EventModifyMouseRotationInput;
import org.zenith.event.MovementInputEvent;
import org.zenith.managers.TargetSelector;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.MovementUtils;

@ModuleInfo(name = "AimAssist", description = "", category = Category.COMBAT)
public class AimAssist extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AimAssist aimAssist = new AimAssist();
   public static final float floatField = 0.15F;
   public final NumberSetting distance = new NumberSetting("module.aimAssist.distance", 4.0F, 0.5F, 6.0F, 0.1F);
   public final BooleanSetting changeX = new BooleanSetting("module.aimAssist.changeX", true);
   public final NumberSetting accelerationX = new NumberSetting("module.aimAssist.accelerationX", 1.75F, 0.1F, 2.5F, 0.05F, () -> this.changeX.isEnabled());
   public final NumberSetting decelerationX = new NumberSetting("module.aimAssist.decelerationX", 0.4F, 0.1F, 1.0F, 0.05F, () -> this.changeX.isEnabled());
   public final BooleanSetting changeY = new BooleanSetting("module.aimAssist.changeY", false);
   public final NumberSetting accelerationY = new NumberSetting("module.aimAssist.accelerationY", 1.25F, 0.1F, 2.5F, 0.05F, () -> this.changeY.isEnabled());
   public final NumberSetting decelerationY = new NumberSetting("module.aimAssist.decelerationY", 0.75F, 0.1F, 1.0F, 0.05F, () -> this.changeY.isEnabled());
   public final NumberSetting diffRangeX = new NumberSetting("module.aimAssist.diffRangeX", 20.0F, 0.0F, 100.0F, 5.0F, this.changeX::isEnabled);
   public final NumberSetting diffRangeY = new NumberSetting("module.aimAssist.diffRangeY", 20.0F, 0.0F, 100.0F, 5.0F, this.changeY::isEnabled);
   public final SettingGroup targetSettingWindow = new SettingGroup(
      "module.aimAssist.targetSettingWindow",
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
   public final ModeSetting correction = new ModeSetting("module.aura.correction", "module.aura.correction.desc");
   public final ModeSetting.Option modeSetting3Var159 = new ModeSetting.Option(this.correction, "module.aura.correctionFocus").int210();
   public final ModeSetting.Option modeSetting3Var1592 = new ModeSetting.Option(this.correction, "module.aura.correctionNone");
   public LivingEntity livingEntity;

   @EventTarget
   public void on23(EventModifyMouseRotationInput var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.livingEntity = this.zClass022();
         if (this.livingEntity != null) {
            Rotation ililiiili1ll1li11 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());
            Rotation ililiiili1ll1li111 = RotationMath.Event08(
               this.livingEntity.getBoundingBox().getCenter().subtract(minecraftClient3.player.getEyePos())
            );
            float f = MathHelper.wrapDegrees(ililiiili1ll1li111.GrimGlide() - ililiiili1ll1li11.GrimGlide());
            float f1 = MathHelper.wrapDegrees(ililiiili1ll1li111.GuiWalk() - ililiiili1ll1li11.GuiWalk());
            if (this.changeX.isEnabled() && Math.abs(f) > this.diffRangeX.getCurrent()) {
               var1.Easing(this.on23(var1.CraftingExecutor(), f, this.accelerationX.getCurrent(), Math.abs(f) < 40.0F ? this.decelerationX.getCurrent() : 1.0F));
            }

            if (this.changeY.isEnabled() && Math.abs(f1) > this.diffRangeY.getCurrent()) {
               var1.ColorAnimator(this.on23(var1.BlockPosEntry(), f1, this.accelerationY.getCurrent(), this.decelerationY.getCurrent()));
            }
         }
      } else {
         this.livingEntity = null;
      }
   }

   public LivingEntity zClass054() {
      return !this.isEnabled() ? null : this.livingEntity;
   }

   public LivingEntity zClass022() {
      return TargetSelector.on23(
         minecraftClient3.world.getEntities(),
         this.distance.getCurrent(),
         true,
         ((MultiSelectSetting)this.targetSettingWindow.getSettings().get(0)).zClass100Var143Var143(),
         ((MultiSelectSetting)this.targetSettingWindow.getSettings().get(1)).zClass100Var143Var143(),
         ((BooleanSetting)this.targetSettingWindow.getSettings().get(2)).isEnabled()
      );
   }

   public double on23(double var1, float var3, float var4, float var5) {
      boolean flag = Math.signum(var1) == Math.signum(var3);
      float f = flag ? var4 : var5;
      return var1 * f;
   }

   @EventTarget
   public void UiAnimation(MovementInputEvent var1) {
      if (!this.modeSetting3Var1592.isSelected() && this.livingEntity != null) {
         Rotation ililiiili1ll1li11 = RotationMath.Event08(
            this.livingEntity.getBoundingBox().getCenter().subtract(minecraftClient3.player.getBoundingBox().getCenter())
         );
         MovementUtils.on23(var1, val002.LineShader().GrimGlide(), ililiiili1ll1li11.GrimGlide());
      }
   }
}
