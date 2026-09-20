package org.zenith.module.render;

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
import net.minecraft.client.option.Perspective;
import net.minecraft.util.hit.HitResult.Type;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudDrawContext;

@ModuleInfo(name = "Crosshair", category = Category.RENDER, description = "Кастомный прицел")
public final class Crosshair extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Crosshair crosshair = new Crosshair();
   public final NumberSetting thickness = new NumberSetting("module.crosshair.thickness", 1.0F, 0.5F, 3.0F, 0.1F, "module.crosshair.thickness.desc", "px");
   public final NumberSetting length = new NumberSetting("module.crosshair.length", 3.0F, 1.0F, 8.0F, 0.5F, "module.crosshair.length.desc", "px");
   public final NumberSetting gap = new NumberSetting("module.crosshair.gap", 2.0F, 0.0F, 5.0F, 0.5F, "module.crosshair.gap.desc", "px");
   public final BooleanSetting dynamicGap = new BooleanSetting("module.crosshair.dynamicGap", "module.crosshair.dynamicGap.desc", false);
   public final BooleanSetting useEntityColor = new BooleanSetting("module.crosshair.useEntityColor", "module.crosshair.useEntityColor.desc", false);
   public final ArgbColor var119 = new ArgbColor(255, 0, 0, 255);

   @EventTarget(0)
   public void on23(EventRenderScreenHook var1) {
      try {
         if (minecraftClient3.player == null || minecraftClient3.world == null) {
            return;
         }

         if (minecraftClient3.options.getPerspective() != Perspective.FIRST_PERSON) {
            return;
         }

         HudDrawContext ililll1lli1i11l11l111i1l1 = var1.WarpFarm();
         float f = minecraftClient3.getWindow().getScaledWidth() / 2.0F;
         float f1 = minecraftClient3.getWindow().getScaledHeight() / 2.0F;
         float f2 = this.gap.getCurrent();
         if (this.dynamicGap.isEnabled()) {
            float f3 = 1.0F - minecraftClient3.player.getAttackCooldownProgress(0.0F);
            f2 += 8.0F * f3;
         }

         float f5 = this.thickness.getCurrent();
         float f4 = this.length.getCurrent();
         ArgbColor i11ii1llliilllii1i1 = this.useEntityColor.isEnabled()
               && minecraftClient3.crosshairTarget != null
               && minecraftClient3.crosshairTarget.getType() == Type.ENTITY
            ? this.var119
            : new ArgbColor(255, 255, 255, 255);
         this.on23(ililll1lli1i11l11l111i1l1, f - f5 / 2.0F, f1 - f2 - f4, f5, f4, i11ii1llliilllii1i1);
         this.on23(ililll1lli1i11l11l111i1l1, f - f5 / 2.0F, f1 + f2, f5, f4, i11ii1llliilllii1i1);
         this.on23(ililll1lli1i11l11l111i1l1, f - f2 - f4, f1 - f5 / 2.0F, f4, f5, i11ii1llliilllii1i1);
         this.on23(ililll1lli1i11l11l111i1l1, f + f2, f1 - f5 / 2.0F, f4, f5, i11ii1llliilllii1i1);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void on23(CustomDrawContext var1, float var2, float var3, float var4, float var5, ArgbColor var6) {
      var1.drawRect(var2, var3, var4, var5, var6);
   }
}
