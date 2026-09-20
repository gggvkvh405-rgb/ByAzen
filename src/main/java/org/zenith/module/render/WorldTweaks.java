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
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.event.EventGetFogColorHook;
import org.zenith.event.PacketEvent;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "WorldTweaks", description = "", category = Category.RENDER)
public final class WorldTweaks extends Module {
   public final MultiSelectSetting modeSetting19 = MultiSelectSetting.on23(
      "module.worldTweaks.modeSetting",
      "module.worldTweaks.modeSetting.desc",
      List.of("module.worldTweaks.lighting", "module.worldTweaks.fog", "module.worldTweaks.time", "module.worldTweaks.saturation")
   );
   public final NumberSetting brightSetting = new NumberSetting(
      "module.worldTweaks.brightSetting",
      1.0F,
      0.0F,
      1.0F,
      0.1F,
      "module.worldTweaks.brightSetting.desc",
      "%",
      () -> this.modeSetting19.ConfigJsonUtil(0),
      null
   );
   public final ColorSetting colorFog = new ColorSetting(
      "module.worldTweaks.colorFog", "module.worldTweaks.colorFog.desc", ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor()
   );
   public final NumberSetting distanceSetting3 = new NumberSetting(
      "module.worldTweaks.distanceSetting",
      80.0F,
      10.0F,
      255.0F,
      5.0F,
      "module.worldTweaks.distanceSetting.desc",
      "b",
      () -> this.modeSetting19.ConfigJsonUtil(1),
      null
   );
   public final NumberSetting timeSetting = new NumberSetting(
      "module.worldTweaks.timeSetting", 12.0F, 0.0F, 24.0F, 1.0F, "module.worldTweaks.timeSetting.desc", "h", () -> this.modeSetting19.ConfigJsonUtil(2), null
   );
   public final NumberSetting contrastSetting = new NumberSetting(
      "module.worldTweaks.contrastSetting",
      1.0F,
      0.0F,
      3.0F,
      0.1F,
      "module.worldTweaks.contrastSetting.desc",
      "x",
      () -> this.modeSetting19.ConfigJsonUtil(3),
      null
   );
   public final BooleanSetting noneRaning = new BooleanSetting("module.worldTweaks.noneRaning", "module.worldTweaks.noneRaning.desc", true);
   public static final WorldTweaks worldTweaks = new WorldTweaks();
   public boolean boolean173;

   public void BotDisconnectEvent(boolean var1) {
      this.boolean173 = var1;
   }

   public float int326() {
      return this.boolean173 && this.isEnabled() && this.modeSetting19.ConfigJsonUtil(3) ? this.contrastSetting.getCurrent() : 1.0F;
   }

   @EventTarget
   public void on23(EventGetFogColorHook var1) {
      if (this.modeSetting19.ConfigJsonUtil(1)) {
         var1.ProfileItemBuilder(this.distanceSetting3.getCurrent());
         var1.setColor(this.colorFog.getIntColor());
         var1.setCancelled(true);
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   public void PotionItemBuilder(PacketEvent var1) {
   }
}
