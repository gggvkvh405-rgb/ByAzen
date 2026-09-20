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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "AutoDuels", category = Category.MISC, description = "Кидает дуэль на RW")
public final class AutoDuels extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoDuels autoDuels = new AutoDuels();
   public static final Pattern pattern = Pattern.compile("(?!)");
   public final ModeSetting mode = new ModeSetting("module.autoDuels.mode", "module.autoDuels.mode.desc");
   public final ModeSetting.Option modeSetting3Var15923 = new ModeSetting.Option(this.mode, "module.autoDuels.shield");
   public final ModeSetting.Option modeSetting3Var15924 = new ModeSetting.Option(this.mode, "module.autoDuels.shipi");
   public final ModeSetting.Option modeSetting3Var15925 = new ModeSetting.Option(this.mode, "module.autoDuels.bow");
   public final ModeSetting.Option modeSetting3Var15926 = new ModeSetting.Option(this.mode, "module.autoDuels.totem");
   public final ModeSetting.Option modeSetting3Var15927 = new ModeSetting.Option(this.mode, "module.autoDuels.noDebuff");
   public final ModeSetting.Option modeSetting3Var15928 = new ModeSetting.Option(this.mode, "module.autoDuels.balls");
   public final ModeSetting.Option modeSetting3Var15929 = new ModeSetting.Option(this.mode, "module.autoDuels.classik").int210();
   public final ModeSetting.Option modeSetting3Var15930 = new ModeSetting.Option(this.mode, "module.autoDuels.cheats");
   public final ModeSetting.Option modeSetting3Var15931 = new ModeSetting.Option(this.mode, "module.autoDuels.nezer");
   public final CooldownTimer zClass0677 = new CooldownTimer();
   public final List<String> list7 = new ArrayList<>();

   @EventTarget
   public void onUpdate(EventTick var1) {
      ArrayList<String> arraylist = new ArrayList<>();
      Collections.shuffle(arraylist);

      for (PlayerListEntry playerlistentry : minecraftClient3.player.networkHandler.getPlayerList()) {
         arraylist.add(playerlistentry.getProfile().name());
      }

      for (String s1 : arraylist) {
         if (this.FriendStore(s1)
            && this.zClass0677.EventModifyMouseRotationInput(750L)
            && !this.list7.contains(s1)
            && !s1.equals(minecraftClient3.player.getNameForScoreboard())) {
            minecraftClient3.player.networkHandler.sendChatCommand("duel " + s1);
            this.list7.add(s1);
            this.zClass0677.reset();
         }
      }

      if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
         String s = minecraftClient3.currentScreen.getTitle().getString();
         if (s.contains("Выбор набора")) {
            minecraftClient3.interactionManager
               .clickSlot(
                  minecraftClient3.player.currentScreenHandler.syncId,
                  this.mode.getValues().indexOf(this.mode.getRandomEnabledElement()),
                  0,
                  SlotActionType.PICKUP,
                  minecraftClient3.player
               );
         } else if (s.contains("Настройка поединка")) {
            minecraftClient3.interactionManager
               .clickSlot(minecraftClient3.player.currentScreenHandler.syncId, 0, 0, SlotActionType.PICKUP, minecraftClient3.player);
         }
      }
   }

   public boolean FriendStore(String var1) {
      return var1 != null && pattern.matcher(var1).matches();
   }

   @EventTarget
   public void TextScanner(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() instanceof GameMessageS2CPacket gamemessages2cpacket) {
         String s = gamemessages2cpacket.content().getString();
         if (s.contains("Принял") && !s.contains("не принял")) {
            this.list7.clear();
            this.toggle();
         }

         if (s.contains("дуэль") && (s.contains("найдена") || s.contains("началась") || s.contains("старт"))) {
            this.list7.clear();
            this.toggle();
         }

         if (s.contains("победил") || s.contains("проиграл") || s.contains("ничья")) {
            this.list7.clear();
            this.toggle();
         }

         if (s.contains("Баланс") || s.contains("отключил запросы")) {
            var1.cancel();
         }
      }
   }

   @Override
   public void onEnable() {
      if (minecraftClient3.player == null) {
         this.setEnabled(false);
      } else {
         this.zClass0677.reset();
         super.onEnable();
      }
   }
}
