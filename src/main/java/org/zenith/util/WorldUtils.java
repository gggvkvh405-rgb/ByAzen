package org.zenith.util;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.zenith.core.ClientProvider;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;

public class WorldUtils implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final CooldownTimer zClass06745 = new CooldownTimer();
   public String server = "Vanilla";
   public boolean boolean175;
   public int anarchy;
   public boolean boolean176;
   public boolean boolean177 = false;
   public final ArrayDeque<Float> arrayDeque = new ArrayDeque<>(20);
   public long long91;
   public long long153;
   public float float274;

   public WorldUtils() {
      EventManager.register(this);
   }

   @EventTarget
   public void on23(EventTick var1) {
      this.anarchy = this.soundEvent4();
      this.server = this.soundEvent3();
      this.boolean176 = this.call453();
      if (this.soundEvent6()) {
         this.zClass06745.reset();
      }
   }

   public float set11() {
      return ModuleSnapshotDto(this.float274);
   }

   public float soundEvent() {
      return ModuleSnapshotDto(20.0F * ((float)this.long153 / 1000.0F));
   }

   public float soundEvent2() {
      return (float)this.long153 / 1000.0F;
   }

   public static float ModuleSnapshotDto(double var0) {
      BigDecimal bigdecimal = new BigDecimal(var0);
      bigdecimal = bigdecimal.setScale(2, RoundingMode.HALF_UP);
      return bigdecimal.floatValue();
   }

   @EventTarget
   public void ProfileItemBuilder(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() instanceof WorldTimeUpdateS2CPacket) {
         synchronized (this.arrayDeque) {
            if (this.long91 != 0L) {
               this.long153 = System.currentTimeMillis() - this.long91;
               if (this.arrayDeque.size() > 20) {
                  this.arrayDeque.poll();
               }

               this.arrayDeque.add(20.0F * (1000.0F / (float)this.long153));
               float f = 0.0F;
               Float[] afloat = this.arrayDeque.toArray(new Float[0]);

               for (Float f1 : afloat) {
                  f += MathHelper.clamp(f1, 0.0F, 20.0F);
               }

               this.float274 = f / this.arrayDeque.size();
            }

            this.long91 = System.currentTimeMillis();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.ItemScroller() instanceof ClientCommandC2SPacket clientcommandc2spacket) {
         if (clientcommandc2spacket.getMode().equals(Mode.START_SPRINTING)) {
            this.boolean175 = true;
         } else if (clientcommandc2spacket.getMode().equals(Mode.STOP_SPRINTING)) {
            this.boolean175 = false;
         }
      }
   }

   public String soundEvent3() {
      if (!EffectEngine.double69()
         && minecraftClient3.getNetworkHandler() != null
         && minecraftClient3.getNetworkHandler().getServerInfo() != null
         && minecraftClient3.getNetworkHandler().getBrand() != null) {
         String s = minecraftClient3.getNetworkHandler().getServerInfo().address.toLowerCase();
         String s1 = minecraftClient3.getNetworkHandler().getBrand().toLowerCase();
         if (s1.contains("botfilter")) {
            return "FunTime";
         } else if (s.contains("funtime") || s.contains("skytime") || s.contains("space-times") || s.contains("funsky")) {
            return "CopyTime";
         } else if (TrajectoryDataset(s1)) {
            return "HolyWorld";
         } else {
            return s.contains("reallyworld") ? "ReallyWorld" : "Vanilla";
         }
      } else {
         return "Vanilla";
      }
   }

   public int soundEvent4() {
      Scoreboard scoreboard = minecraftClient3.world.getScoreboard();
      ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      String s = this.server;
      switch (s) {
         case "FunTime":
            if (scoreboardobjective != null) {
               String[] astring = scoreboardobjective.getDisplayName().getString().split("-");
               if (astring.length > 1) {
                  return Integer.parseInt(astring[1]);
               }
            }
         default:
            return -1;
         case "HolyWorld":
            return UiAnimation(minecraftClient3.world);
      }
   }

   public static int UiAnimation(World var0) {
      if (var0 == null) {
         return -1;
      }

      Scoreboard scoreboard = var0.getScoreboard();
      ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      if (scoreboardobjective == null) {
         return -1;
      }

      for (ScoreboardEntry scoreboardentry : scoreboard.getScoreboardEntries(scoreboardobjective)) {
         String s = Team.decorateName(scoreboard.getScoreHolderTeam(scoreboardentry.owner()), scoreboardentry.name()).getString();
         if (!s.isEmpty()) {
            String s1 = StringUtils.substringBetween(s, "#", " -◆-");
            if (s1 != null && !s1.isEmpty()) {
               try {
                  return Integer.parseInt(s1);
               } catch (NumberFormatException var8) {
               }
            }
         }
      }

      return -1;
   }

   public static boolean TrajectoryDataset(String var0) {
      if (var0 == null) {
         return false;
      }

      String s = var0.toLowerCase();
      return s.contains("holyworld") || s.contains("holywоrld") || s.contains("leaf") || s.contains("vk.com/idwok");
   }

   public boolean soundEvent5() {
      return !this.zClass06745.EventModifyMouseRotationInput(250L);
   }

   public boolean soundEvent6() {
      return minecraftClient3.inGameHud
         .getBossBarHud()
         .bossBars
         .values()
         .stream()
         .map(var0 -> var0.getName().getString().toLowerCase())
         .anyMatch(var0 -> var0.contains("pvp") || var0.contains("пвп"));
   }

   public boolean call453() {
      return minecraftClient3.inGameHud
         .getBossBarHud()
         .bossBars
         .values()
         .stream()
         .map(var0 -> var0.getName().getString().toLowerCase())
         .anyMatch(var0 -> (var0.contains("pvp") || var0.contains("пвп")) && (var0.contains("0") || var0.contains("1")));
   }

   public String call425() {
      return minecraftClient3.world.getRegistryKey().getValue().getPath();
   }

   public boolean soundEvent7() {
      return this.server.equals("CopyTime") || this.server.equals("SpookyTime") || this.server.equals("FunTime");
   }

   public boolean string86() {
      return this.server.equals("FunTime");
   }

   public boolean call030() {
      return this.server.equals("ReallyWorld");
   }

   public boolean call003() {
      return this.server.equals("HolyWorld");
   }

   public boolean string87() {
      return this.server.equals("Vanilla");
   }

   public CooldownTimer var111Var159() {
      return this.zClass06745;
   }

   public String getServer() {
      return this.server;
   }

   public boolean var11917() {
      return this.boolean175;
   }

   public int getAnarchy() {
      return this.anarchy;
   }

   public boolean var11918() {
      return this.boolean176;
   }

   public boolean var11919() {
      return this.boolean177;
   }

   public ArrayDeque<Float> var11920() {
      return this.arrayDeque;
   }

   public long getTime() {
      return this.long91;
   }

   public long var11921() {
      return this.long153;
   }
}
