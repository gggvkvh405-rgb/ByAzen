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

import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
@ModuleInfo(name = "BetterMinecraft", description = "", category = Category.RENDER)
public final class BetterMinecraft extends Module {
   public static final BetterMinecraft betterMinecraft = new BetterMinecraft();
   public static final long long74 = 130L;
   public static final long long75 = 110L;
   public static final float float11 = 0.86F;
   public final MultiSelectSetting u0410U043dU0438U043cU0438U0440U043eU0432U0430U0442U044c = new MultiSelectSetting("Анимировать");
   public final MultiSelectSetting.Option modeSettingVar1595 = new MultiSelectSetting.Option(this.u0410U043dU0438U043cU0438U0440U043eU0432U0430U0442U044c, "Хранилища", true);
   public final MultiSelectSetting.Option modeSettingVar1596 = new MultiSelectSetting.Option(this.u0410U043dU0438U043cU0438U0440U043eU0432U0430U0442U044c, "Инвентарь", true);

   public boolean float318() {
      return this.isEnabled() && this.modeSettingVar1595.isEnabled();
   }

   public boolean int423() {
      return this.isEnabled() && this.modeSettingVar1596.isEnabled();
   }

   public float EmoteMetadata(long var1) {
      float f = this.BotPacketEvent((float)(System.currentTimeMillis() - var1) / 130.0F);
      return 0.86F + 0.13999999F * this.BotRespawnEvent(f);
   }

   public float on23(long var1, float var3) {
      float f = this.BotPacketEvent((float)(System.currentTimeMillis() - var1) / 110.0F);
      return var3 * (1.0F - this.BotRespawnEvent(f));
   }

   public boolean EmoteManager(long var1) {
      return System.currentTimeMillis() - var1 >= 110L;
   }

   public float BotPacketEvent(float var1) {
      return Math.clamp(var1, 0.0F, 1.0F);
   }

   public float BotRespawnEvent(float var1) {
      return var1 < 0.5F ? 2.0F * var1 * var1 : 1.0F - (float)Math.pow(-2.0F * var1 + 2.0F, 2.0) / 2.0F;
   }
}
