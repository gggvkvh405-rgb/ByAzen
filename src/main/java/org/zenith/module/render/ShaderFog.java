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

import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;

@ModuleInfo(name = "ShaderFog", category = Category.RENDER, description = "module.shaderFog.desc")
public final class ShaderFog extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ShaderFog shaderFog = new ShaderFog();
   public static final int int297 = 0;
   public static final int int298 = 1;
   public static final int int299 = 2;
   public static final int int300 = 3;
   public static final int int301 = 4;
   public final ModeSetting shaderMode = new ModeSetting(
      "module.shaderFog.shaderMode",
      "module.shaderFog.shaderMode.desc",
      "module.shaderFog.gradient",
      "module.shaderFog.galaxy",
      "module.shaderFog.aqua",
      "module.shaderFog.purple",
      "module.shaderFog.overcast"
   );
   public final ModeSetting colorMode3 = new ModeSetting(
      "module.shaderFog.colorMode", "module.shaderFog.colorMode.desc", () -> this.shaderMode.is(0), "module.shaderFog.sync", "module.shaderFog.custom"
   );
   public final NumberSetting syncAlpha2 = new NumberSetting(
      "module.shaderFog.syncAlpha",
      132.0F,
      0.0F,
      255.0F,
      1.0F,
      "module.shaderFog.syncAlpha.desc",
      "a",
      () -> this.shaderMode.is(0) && this.colorMode3.is(0),
      null
   );
   public final ColorSetting firstColor = new ColorSetting(
      "module.shaderFog.firstColor", new ArgbColor(86, 162, 255, 128), () -> this.shaderMode.is(0) && this.colorMode3.is(1)
   );
   public final ColorSetting secondColor2 = new ColorSetting(
      "module.shaderFog.secondColor", new ArgbColor(190, 112, 255, 118), () -> this.shaderMode.is(0) && this.colorMode3.is(1)
   );
   public final ColorSetting purpleColor = new ColorSetting("module.shaderFog.purpleColor", new ArgbColor(185, 56, 195), () -> this.shaderMode.is(3));
   public final NumberSetting intensity2 = new NumberSetting("module.shaderFog.intensity", 0.05F, 0.0F, 1.5F, 0.05F, "module.shaderFog.intensity.desc", "x");
   public final NumberSetting timeSpeed3 = new NumberSetting(
      "module.shaderFog.timeSpeed", 0.1F, 0.0F, 5.0F, 0.05F, "module.shaderFog.timeSpeed.desc", "x", this::call222, null
   );

   public boolean double156() {
      return this.isEnabled() && minecraftClient3.world != null && minecraftClient3.player != null && minecraftClient3.getFramebuffer() != null;
   }

   public float call223() {
      return this.intensity2.getCurrent();
   }

   public float call179() {
      return this.timeSpeed3.getCurrent();
   }

   public int call224() {
      return this.shaderMode.getIndex();
   }

   public int call225() {
      return this.colorMode3.is(0)
         ? ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor().SprintStateEvent(this.syncAlpha2.getCurrent() / 255.0F).call001()
         : this.firstColor.getIntColor();
   }

   public int call226() {
      return this.colorMode3.is(0)
         ? ZenithClient.on23()
            .TextScanner()
            .getCurrentStyle()
            .getSecondaryPrimaryColor()
            .getColor()
            .SprintStateEvent(this.syncAlpha2.getCurrent() / 255.0F)
            .call001()
         : this.secondColor2.getIntColor();
   }

   public int call227() {
      return this.purpleColor.getIntColor();
   }

   public boolean call222() {
      return this.shaderMode.is(1) || this.shaderMode.is(2) || this.shaderMode.is(3) || this.shaderMode.is(4);
   }
}
