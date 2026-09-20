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
import net.minecraft.client.MinecraftClient;
import org.zenith.event.EventTick;
import org.zenith.setting.TextSetting;
import org.zenith.setting.TextSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ScoreboardHelper;

@ModuleInfo(name = "AutoPay", category = Category.MISC, description = "")
public final class AutoPay extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoPay autoPay = new AutoPay();
   public static final long long21 = 5000L;
   public final TextSetting nick = new TextSetting("autopay.nick", "", "autopay.nick.empty", TextSetting.Validator.TradeGuardService(16));
   public final TextSetting maxAmount = new TextSetting(
      "autopay.maxAmount",
      "100000",
      "autopay.maxAmount.empty",
      TextSetting.Validator.on23(12, var0 -> var0.isEmpty() || var0.chars().allMatch(Character::isDigit))
   );
   public final CooldownTimer zClass06710 = new CooldownTimer();
   public long long22 = -1L;

   @Override
   public void onEnable() {
      this.long22 = -1L;
      this.zClass06710.reset();
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         long i = ScoreboardHelper.on23(minecraftClient3.world.getScoreboard(), minecraftClient3.player.getNameForScoreboard());
         if (i >= 0L) {
            this.long22 = i;
         }

         if (this.long22 >= 0L) {
            String s = this.nick.getValue().trim();
            long j = MacroManager(this.maxAmount.getValue().trim());
            if (!s.isEmpty() && j >= 0L) {
               long k = this.long22 - j;
               if (k > 0L && this.zClass06710.EventModifyMouseRotationInput(5000L)) {
                  minecraftClient3.player.networkHandler.sendChatCommand("pay " + s + " " + k);
                  this.zClass06710.reset();
               }
            }
         }
      }
   }

   public static long MacroManager(String var0) {
      if (var0.isEmpty()) {
         return -1L;
      }

      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException numberformatexception) {
         return -1L;
      }
   }

   public long double129() {
      return this.long22;
   }
}
