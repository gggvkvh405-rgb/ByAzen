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
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventGetBasicProjectionMatrixHook;
import org.zenith.event.EventModifyMouseRotationInput;
import org.zenith.event.EventMouseScrollHook;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.event.FovEvent;
import org.zenith.event.SprintEvent;
import org.zenith.rotation.Rotation;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.MathUtils;

@ModuleInfo(name = "CameraTweaks", description = "Настройки камеры", category = Category.MISC)
public final class CameraTweaks extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final CameraTweaks cameraTweaks = new CameraTweaks();
   public float float13 = 110.0F;
   public float float14 = 30.0F;
   public float float15 = 30.0F;
   public Perspective perspective;
   public Rotation var1184;
   public final MultiSelectSetting modeSetting5 = MultiSelectSetting.on23(
      "module.cameraTweaks.multiSetting",
      "module.cameraTweaks.multiSetting.desc",
      List.of("module.cameraTweaks.ratio", "module.cameraTweaks.clip", "module.cameraTweaks.distance")
   );
   public final NumberSetting ratioSetting = new NumberSetting(
      "module.cameraTweaks.ratioSetting", 1.0F, 0.1F, 2.0F, 0.1F, "module.cameraTweaks.ratioSetting.desc", "x", () -> this.modeSetting5.ConfigJsonUtil(0), null
   );
   public final NumberSetting distanceSetting = new NumberSetting(
      "module.cameraTweaks.distanceSetting",
      3.0F,
      2.0F,
      5.0F,
      0.5F,
      "module.cameraTweaks.distanceSetting.desc",
      "b",
      () -> this.modeSetting5.ConfigJsonUtil(2),
      null
   );
   public final KeySetting zoomSetting = new KeySetting("module.cameraTweaks.zoomSetting", "module.cameraTweaks.zoomSetting.desc");
   public final KeySetting freeLookSetting = new KeySetting("module.cameraTweaks.freeLookSetting", "module.cameraTweaks.freeLookSetting.desc");

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (minecraftClient3.currentScreen == null) {
         if (var1.ItemRegistry(this.zoomSetting.getKeyCode())) {
            this.float13 = Math.min(this.float15, (Integer)minecraftClient3.options.getFov().getValue() - 20);
         }

         if (var1.UiAnimation(this.zoomSetting.getKeyCode(), true)) {
            this.float15 = this.float13;
            this.float13 = ((Integer)minecraftClient3.options.getFov().getValue()).intValue();
         }

         if (var1.ItemRegistry(this.freeLookSetting.getKeyCode())) {
            this.perspective = minecraftClient3.options.getPerspective();
         }
      }
   }

   @EventTarget
   public void on23(EventMouseScrollHook var1) {
      if (EffectEngine.on23(this.zoomSetting)) {
         this.float13 = (int)MathHelper.clamp(
            this.float13 - var1.TapeMouse() * 10.0, 10.0, ((Integer)minecraftClient3.options.getFov().getValue()).intValue()
         );
         var1.setCancelled(true);
      }
   }

   @EventTarget
   public void on23(FovEvent var1) {
      if (EffectEngine.on23(this.freeLookSetting)) {
         if (minecraftClient3.options.getPerspective().isFirstPerson()) {
            minecraftClient3.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         }
      } else if (this.perspective != null) {
         minecraftClient3.options.setPerspective(this.perspective);
         this.perspective = null;
      }

      if (this.zoomSetting.isVisible()) {
         var1.ItemServiceBase(
            (int)MathHelper.clamp(
               (this.float14 = MathUtils.on23(1.6, this.float14, this.float13)) + 1.0F,
               10.0F,
               ((Integer)minecraftClient3.options.getFov().getValue()).intValue()
            )
         );
         var1.cancel();
      }
   }

   @EventTarget
   public void UiAnimation(EventModifyMouseRotationInput var1) {
      if (EffectEngine.on23(this.freeLookSetting)) {
         this.var1184 = new Rotation(
            (float)(this.var1184.GrimGlide() + var1.CraftingExecutor() * 0.15F),
            (float)MathHelper.clamp(this.var1184.GuiWalk() + var1.BlockPosEntry() * 0.15F, -90.0, 90.0)
         );
         var1.setCancelled(true);
      } else {
         this.var1184 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());
      }
   }

   @EventTarget
   public void on23(SprintEvent var1) {
      var1.ProfileItemBuilder(this.modeSetting5.ConfigJsonUtil(1));
      if (this.modeSetting5.ConfigJsonUtil(2)) {
         var1.ProfileItemBuilder(this.distanceSetting.getCurrent());
      }

      if (this.var1184 != null) {
         var1.on23(this.var1184);
      }

      if (this.modeSetting5.ConfigJsonUtil(1) || this.modeSetting5.ConfigJsonUtil(2) || this.var1184 != null) {
         var1.cancel();
      }
   }

   @EventTarget
   public void on23(EventGetBasicProjectionMatrixHook var1) {
      if (this.modeSetting5.ConfigJsonUtil(0)) {
         var1.PotionItemBuilder(this.ratioSetting.getCurrent());
         var1.setCancelled(true);
      }
   }
}
