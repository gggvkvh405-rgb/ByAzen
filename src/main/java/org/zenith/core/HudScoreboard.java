package org.zenith.core;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.module.misc.NameProtect;
import org.zenith.util.ArgbColor;
import org.zenith.util.TextReplaceUtils;
import org.zenith.util.TextUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudScoreboard extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public HudScoreboard(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      Scoreboard scoreboard = minecraftClient3.world.getScoreboard();
      ScoreboardObjective scoreboardobjective = null;
      Team team = scoreboard.getScoreHolderTeam(minecraftClient3.player.getNameForScoreboard());
      if (team != null) {
         ScoreboardDisplaySlot scoreboarddisplayslot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
         if (scoreboarddisplayslot != null) {
            scoreboardobjective = scoreboard.getObjectiveForSlot(scoreboarddisplayslot);
         }
      }

      ScoreboardObjective scoreboardobjective1 = scoreboardobjective != null ? scoreboardobjective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      if (scoreboardobjective1 != null) {
         this.on23(var1, scoreboardobjective1);
      } else {
         this.height = 0.0F;
      }
   }

   public void on23(CustomDrawContext var1, ScoreboardObjective var2) {
      Scoreboard scoreboard = var2.getScoreboard();
      Font font = Fonts.NEW_MEDIUM.getFont(7.0F);
      NumberFormat numberformat = var2.getNumberFormatOr(StyledNumberFormat.RED);
      List<HudScoreLine> list = scoreboard.getScoreboardEntries(var2)
         .stream()
         .filter(var0 -> !var0.hidden())
         .sorted(InGameHud.SCOREBOARD_ENTRY_COMPARATOR)
         .limit(15L)
         .map(var3x -> {
            Team team = scoreboard.getScoreHolderTeam(var3x.owner());
            MutableText mutabletext = Team.decorateName(team, var3x.name());
            Text text2 = TextUtils.TextScanner(mutabletext);
            if (text2 == null) {
               return null;
            }

            MutableText mutabletext1 = var3x.formatted(numberformat);
            float f7 = font.width(mutabletext1);
            return new HudScoreLine(text2, mutabletext1, (int)f7);
         })
         .filter(var0 -> var0 != null)
         .collect(Collectors.toList());
      Text text = var2.getDisplayName();
      float f = font.width(text);
      float f1 = font.width(": ");
      float f2 = f;

      for (HudScoreLine ll1ii11l1li1lli_ii1il11l111ii11iilx : list) {
         float f3 = font.width(ll1ii11l1li1lli_ii1il11l111ii11iilx.name());
         float f4 = f3 + (ll1ii11l1li1lli_ii1il11l111ii11iilx.scoreWidth() > 0 ? f1 + ll1ii11l1li1lli_ii1il11l111ii11iilx.scoreWidth() : 0.0F);
         f2 = Math.max(f2, f4);
      }

      int l1 = list.size();
      byte b0 = 9;
      byte b1 = 17;
      int i2 = l1 * b0;
      int i = (int)this.y;
      int j = i + b0;
      int k = i + b1;
      int l = on23(list, j, b0, k);
      int i1 = (int)(this.x + 8.0F);
      this.width = Math.max(0.0F, f2 + 16.0F);
      this.height = Math.max(b1, i2 + 10 + l);
      float f5 = Interface.float212();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (var1 instanceof CustomDrawContext) {
         var1.drawBlurHud(this.getX(), this.getY(), this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f5), ArgbColor.var11934);
         var1.drawRoundedRect(this.getX(), i, this.width, this.height, CornerRadius.MovementInputEvent(f5), zenithstyle.getHudBackground().getColor());
         var1.drawRoundedRect(this.getX(), i, this.width, b1, CornerRadius.MovementInputEvent(f5), zenithstyle.getHeaderHudBackground().getColor());
      }

      this.ServiceException(var1.getScaledWindowWidth(), var1.getScaledWindowHeight());
      float f6 = i1 + f2 / 2.0F - f / 2.0F;
      if (text.getSiblings().isEmpty()) {
         text = text.copy()
            .setStyle(Style.EMPTY.withColor(ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor().call001()));
      } else {
         Text object = Text.of("");

         for (Text text1 : text.getSiblings()) {
            object = object.copy()
               .append(
                  text1.copy()
                     .setStyle(
                        text1.getStyle().getColor() == null
                           ? Style.EMPTY.withColor(zenithstyle.getTextEnable().getIntColor())
                           : text1.getStyle()
                     )
               );
         }

         text = object;
      }

      var1.drawText(font, text, f6, i + (b1 - font.height()) / 2.0F);
      int j2 = 0;

      for (int k2 = 0; k2 < l1; k2++) {
         HudScoreLine ll1ii11l1li1lli_ii1il11l111ii11iil = list.get(k2);
         int j1 = j + k2 * b0 + j2;
         Text object1 = ll1ii11l1li1lli_ii1il11l111ii11iil.name();
         if (NameProtect.nameProtect.isEnabled()) {
            if (object1.getString().contains(minecraftClient3.player.getNameForScoreboard())) {
               object1 = TextReplaceUtils.UiAnimation(object1, minecraftClient3.player.getNameForScoreboard(), NameProtect.call029());
            } else if (NameProtect.nameProtect.int440() != null) {
               if (object1.getString().contains("Группа:")) {
                  object1 = TextReplaceUtils.Easing(object1, "Группа:", NameProtect.nameProtect.int440());
               } else if (object1.getString().contains("Ранг:")) {
                  object1 = TextReplaceUtils.Easing(object1, "Ранг:", NameProtect.nameProtect.int440());
               }
            }
         }

         if (UiAnimation(object1) && j1 < k) {
            int k1 = k - j1;
            j2 += k1;
            j1 += k1;
         }

         var1.drawText(font, object1, i1, j1, zenithstyle.getTextEnable().getIntColor());
      }
   }

   public static int on23(List<HudScoreLine> var0, int var1, int var2, int var3) {
      int i = 0;

      for (int j = 0; j < var0.size(); j++) {
         int k = var1 + j * var2 + i;
         if (UiAnimation(var0.get(j).name()) && k < var3) {
            i += var3 - k;
         }
      }

      return i;
   }

   public static boolean UiAnimation(Text var0) {
      String s = var0.getString();
      if (s.isBlank()) {
         return false;
      }

      for (int i = 0; i < s.length(); i++) {
         if (s.charAt(i) == 167) {
            i++;
         } else if (!Character.isWhitespace(s.charAt(i))) {
            return true;
         }
      }

      return false;
   }
}
