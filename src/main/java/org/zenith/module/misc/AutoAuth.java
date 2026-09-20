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


import com.google.gson.JsonObject;
import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import java.util.Map.Entry;
import org.zenith.event.PacketEvent;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.TextSetting;
import org.zenith.setting.ButtonSetting;

@ModuleInfo(name = "AutoAuth", category = Category.MISC, description = "Авто регистрация")
public final class AutoAuth extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoAuth autoAuth = new AutoAuth();
   public final TextSetting booleanSetting2 = new AutoAuth.TransientPasswordSetting(
         this, "autoauth.passSetting.command", "autoauth.passSetting.command.desc", "BogdanSuperCoder", "Default password"
      )
      .secret();
   public final BooleanSetting login = new BooleanSetting("autoauth.loginSetting.login", "autoauth.loginSetting.description", true);
   public final File file = new File("autoauth_accounts.txt");
   public final BooleanSetting randomPass = new BooleanSetting("autoauth.randomPass", "autoauth.randomPass.description", true);
   public final ButtonSetting openFile = new ButtonSetting("autoauth.openFile", "K", () -> {
      try {
         String s = this.file.getAbsolutePath();
         new ProcessBuilder("cmd", "/c", "start", "", "\"" + s + "\"").start();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   });
   public final Map<String, String> map4 = new HashMap<>();

   public AutoAuth() {
      this.int343();
   }

   public void int343() {
      try {
         if (!this.file.exists()) {
            this.file.createNewFile();
            this.int344();
            return;
         }

         for (String s : Files.readAllLines(this.file.toPath())) {
            if (s.contains(":")) {
               String[] astring = s.split(":", 2);
               if (astring[0].trim().equalsIgnoreCase("default")) {
                  this.booleanSetting2.setValue(astring[1].trim());
               } else {
                  String[] astring1 = s.split(":", 3);
                  if (astring1.length >= 3) {
                     String s1 = astring1[0].trim().toLowerCase();
                     String s2 = astring1[1].trim();
                     String s3 = astring1[2].trim();
                     String s4 = s1 + ":" + s2.toLowerCase();
                     this.map4.put(s4, s3);
                  }
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void int344() {
      try {
         StringBuilder stringbuilder = new StringBuilder();
         stringbuilder.append("default : ").append(this.booleanSetting2.getValue()).append("\n");

         for (Entry<String, String> entry : this.map4.entrySet()) {
            String s = entry.getKey();
            String s1 = entry.getValue();
            String[] astring = s.split(":", 2);
            String s2 = astring[0];
            String s3 = astring[1];
            stringbuilder.append(s2).append(" : ").append(s3).append(" : ").append(s1).append("\n");
         }

         Files.writeString(this.file.toPath(), stringbuilder.toString(), StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public String int345() {
      String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
      Random random = new Random();
      StringBuilder stringbuilder = new StringBuilder();

      for (int i = 0; i < 12; i++) {
         stringbuilder.append(s.charAt(random.nextInt(s.length())));
      }

      return stringbuilder.toString();
   }

   public String SprintEvent(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String[] astring = var1.split("\\.");
         return astring.length >= 2 ? astring[astring.length - 2] : var1;
      } else {
         return "unknown";
      }
   }

   @EventTarget
   public void ItemSpec(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() instanceof GameMessageS2CPacket gamemessages2cpacket && minecraftClient3.getNetworkHandler() != null) {
         String s5 = gamemessages2cpacket.content().getString().toLowerCase();
         String s = minecraftClient3.getCurrentServerEntry() != null ? minecraftClient3.getCurrentServerEntry().address : "";
         String s1 = this.SprintEvent(s).toLowerCase();
         String s2 = minecraftClient3.getSession().getUsername().toLowerCase();
         String s3 = s1 + ":" + s2;
         String s4 = this.map4.getOrDefault(s3, null);
         if (!s5.contains("/register") && !s5.contains("зарегистрируйтесь")) {
            if (this.login.isEnabled() && (s5.contains("/login") || s5.contains("/ʟ [пᴀᴘᴏль]"))) {
               if (s4 == null) {
                  s4 = this.booleanSetting2.getValue();
                  this.map4.put(s3, s4);
                  this.int344();
               }

               minecraftClient3.getNetworkHandler().sendChatCommand("login " + s4);
            }
         } else {
            if (s4 == null) {
               s4 = this.randomPass.isEnabled() ? this.int345() : this.booleanSetting2.getValue();
            }

            this.map4.put(s3, s4);
            this.int344();
            minecraftClient3.getNetworkHandler().sendChatCommand("register %s %s".formatted(s4, s4));
         }
      }
   }


   public static class TransientPasswordSetting extends TextSetting {
      public final AutoAuth val504;

      TransientPasswordSetting(AutoAuth var1, String var2, String var3, String var4, String var5) {
         super(var2, var3, var4, var5);
         this.val504 = var1;
      }

      @Override
      public void safe(JsonObject var1) {
      }

      @Override
      public void load(JsonObject var1) {
      }

      @Override
      public boolean setValueSafe(String var1) {
         boolean flag = !var1.equals(this.getValue());
         if (!super.setValueSafe(var1)) {
            return false;
         }

         if (flag) {
            this.val504.int344();
         }

         return true;
      }
   }
}
