package org.zenith.core;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud.ScoreDisplayEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.TextUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudDrawContext;

public class HudTabList extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation var14341 = new UiAnimation(250L, Easing.EventInjectHandleInputEvents);

   public HudTabList(String var1) {
      super(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, null);
   }

   @Override
   protected void on23(CustomDrawContext var1, HudElement.Service var2) {
   }

   @Override
   protected void UiAnimation(CustomDrawContext var1, HudElement.Service var2) {
   }

   @Override
   public void on23(float var1, float var2) {
   }

   @Override
   public void ProtocolMessage(float var1, float var2) {
   }

   @Override
   public void double109() {
   }

   @Override
   public void ServiceException(float var1, float var2) {
   }

   @Override
   public void on23(CustomDrawContext var1, float var2, float var3, Interface var4, float var5, float var6) {
   }

   @Override
   public JsonObject save() {
      return new JsonObject();
   }

   @Override
   public void load(JsonObject var1) {
   }

   @Override
   public float double110() {
      return 1.0F;
   }

   @Override
   public void on23(HudDrawContext var1) {
      float f = this.double110();
      var1.pushMatrix();
      var1.getMatrices().translate(this.x + this.width / 2.0F, this.y);
      var1.getMatrices().scale(f, f);
      var1.getMatrices().translate(-this.x - this.width / 2.0F, -this.y);
      this.on23((CustomDrawContext)var1);
      var1.popMatrix();
   }

   @Override
   public void on23(CustomDrawContext var1) {
      Scoreboard scoreboard = minecraftClient3.world.getScoreboard();
      ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
      this.var14341
         .on23(
            minecraftClient3.options.playerListKey.isPressed()
               && (!minecraftClient3.isInSingleplayer() || minecraftClient3.player.networkHandler.getListedPlayerListEntries().size() > 1 || scoreboardobjective != null)
         );
      if (this.var14341.CancellableEvent() != 0.0F) {
         this.on23(var1, var1.getScaledWindowWidth(), scoreboard, scoreboardobjective);
      }

      this.var14341.on23(250L);
      this.var14341.on23(Easing.EventInjectHandleInputEvents);
   }

   public void on23(DrawContext var1, int var2, Scoreboard var3, ScoreboardObjective var4) {
      float f = this.var14341.CancellableEvent();
      List<PlayerListEntry> list = minecraftClient3.inGameHud.getPlayerListHud().collectPlayerEntries();
      ArrayList arraylist = new ArrayList(list.size());
      int i = minecraftClient3.textRenderer.getWidth(" ");
      int j = 0;
      int k = 0;

      for (PlayerListEntry playerlistentry : list) {
         Text text = minecraftClient3.inGameHud.getPlayerListHud().getPlayerName(playerlistentry);
         j = Math.max(j, minecraftClient3.textRenderer.getWidth(text));
         int l = 0;
         MutableText mutabletext = null;
         int i1 = 0;
         if (var4 != null) {
            ScoreHolder scoreholder = ScoreHolder.fromProfile(playerlistentry.getProfile());
            ReadableScoreboardScore readablescoreboardscore = var3.getScore(scoreholder, var4);
            if (readablescoreboardscore != null) {
               l = readablescoreboardscore.getScore();
            }

            if (var4.getRenderType() != RenderType.HEARTS) {
               NumberFormat numberformat = var4.getNumberFormatOr(StyledNumberFormat.YELLOW);
               mutabletext = ReadableScoreboardScore.getFormattedScore(readablescoreboardscore, numberformat);
               i1 = minecraftClient3.textRenderer.getWidth(mutabletext);
               k = Math.max(k, i1 > 0 ? i + i1 : 0);
            }
         }

         arraylist.add(new ScoreDisplayEntry(text, l, mutabletext, i1));
      }

      if (!minecraftClient3.inGameHud.getPlayerListHud().hearts.isEmpty()) {
         Set<UUID> set = list.stream().map(var0 -> var0.getProfile().id()).collect(Collectors.toSet());
         minecraftClient3.inGameHud.getPlayerListHud().hearts.keySet().removeIf(var1x -> !set.contains(var1x));
      }

      int k3 = list.size();
      int l3 = k3;

      int i4;
      for (i4 = 1; l3 > 20; l3 = (k3 + i4 - 1) / i4) {
         i4++;
      }

      boolean flag1 = true;
      int j4;
      if (var4 != null) {
         j4 = var4.getRenderType() == RenderType.HEARTS ? 90 : k;
      } else {
         j4 = 0;
      }

      int k4 = Math.min(i4 * ((flag1 ? 9 : 0) + j + j4 + 13), var2 - 50) / i4;
      int l4 = var2 / 2 - (k4 * i4 + (i4 - 1) * 5) / 2;
      this.x = l4;
      int i5 = 10;
      int j5 = k4 * i4 + (i4 - 1) * 5;
      this.y = i5;
      this.width = j5;
      List<OrderedText> list1 = null;
      if (minecraftClient3.inGameHud.getPlayerListHud().header != null) {
         Text text1 = TextUtils.ItemSpec(minecraftClient3.inGameHud.getPlayerListHud().header);
         list1 = minecraftClient3.textRenderer.wrapLines(text1, var2 - 50);

         for (OrderedText orderedtext : list1) {
            j5 = Math.max(j5, minecraftClient3.textRenderer.getWidth(orderedtext));
         }
      }

      List<OrderedText> list2 = null;
      if (minecraftClient3.inGameHud.getPlayerListHud().footer != null) {
         Text text2 = TextUtils.ItemSpec(minecraftClient3.inGameHud.getPlayerListHud().footer);
         list2 = minecraftClient3.textRenderer.wrapLines(text2, var2 - 50);

         for (OrderedText orderedtext1 : list2) {
            j5 = Math.max(j5, minecraftClient3.textRenderer.getWidth(orderedtext1));
         }
      }

      int k5 = list1 != null ? list1.size() * 9 : 0;
      int l5 = list1 != null ? 1 : 0;
      int i6 = l3 * 9;
      int j1 = list2 != null ? 1 : 0;
      int k1 = list2 != null ? list2.size() * 9 : 0;
      int l1 = k5 + l5 + i6 + j1 + k1;
      this.height = l1;
      float f1 = var2 / 2 - 1;
      float f2 = 10.0F + l1 / 2.0F;
      float f3 = Math.max(1.0E-4F, f);
      MatrixStack matrixstack = org.zenith.render.GuiMatrixAdapter.toMatrixStack(var1.getMatrices());
      matrixstack.push();
      matrixstack.translate(f1, f2, 0.0F);
      matrixstack.scale(f3, f3, 1.0F);
      matrixstack.translate(-f1, -f2, 0.0F);
      if (list1 != null) {
         var1.fill(var2 / 2 - j5 / 2 - 1, i5 - 1, var2 / 2 + j5 / 2 + 1, i5 + list1.size() * 9, Integer.MIN_VALUE);

         for (OrderedText orderedtext2 : list1) {
            int i2 = minecraftClient3.textRenderer.getWidth(orderedtext2);
            var1.drawTextWithShadow(minecraftClient3.textRenderer, orderedtext2, var2 / 2 - i2 / 2, i5, -1);
            i5 += 9;
         }

         i5++;
      }

      var1.fill(var2 / 2 - j5 / 2 - 1, i5 - 1, var2 / 2 + j5 / 2 + 1, i5 + l3 * 9, Integer.MIN_VALUE);
      int j6 = minecraftClient3.options.getTextBackgroundColor(553648127);

      for (int k6 = 0; k6 < k3; k6++) {
         int l6 = k6 / l3;
         int j2 = k6 % l3;
         int k2 = l4 + l6 * k4 + l6 * 5;
         int l2 = i5 + j2 * 9;
         var1.fill(k2, l2, k2 + k4, l2 + 8, j6);
         if (k6 < list.size()) {
            PlayerListEntry playerlistentry1 = list.get(k6);
            ScoreDisplayEntry scoredisplayentry = (ScoreDisplayEntry)arraylist.get(k6);
            GameProfile gameprofile = playerlistentry1.getProfile();
            int i3 = k2;
            if (flag1) {
               PlayerEntity playerentity = minecraftClient3.world.getPlayerByUuid(gameprofile.id());
               boolean flag = playerentity != null && net.minecraft.client.render.entity.PlayerEntityRenderer.shouldFlipUpsideDown(playerentity);
               PlayerSkinDrawer.draw(var1, playerlistentry1.getSkinTextures().body().texturePath(), k2, l2, 8, playerlistentry1.shouldShowHat(), flag, -1);
               i3 = k2 + 9;
            }

            int j7 = playerlistentry1.getGameMode() == GameMode.SPECTATOR ? -1859310289 : -1;
            var1.drawTextWithShadow(minecraftClient3.textRenderer, scoredisplayentry.name(), i3, l2, j7);
            if (var4 != null && playerlistentry1.getGameMode() != GameMode.SPECTATOR) {
               int k7 = i3 + j + 1;
               int j3 = k7 + j4;
               if (j3 - k7 > 5) {
                  minecraftClient3.inGameHud.getPlayerListHud().renderScoreboardObjective(var4, l2, scoredisplayentry, k7, j3, gameprofile.id(), var1);
               }
            }

            minecraftClient3.inGameHud.getPlayerListHud().renderLatencyIcon(var1, k4, i3 - (flag1 ? 9 : 0), l2, playerlistentry1);
         }
      }

      if (list2 != null) {
         i5 += l3 * 9 + 1;
         var1.fill(var2 / 2 - j5 / 2 - 1, i5 - 1, var2 / 2 + j5 / 2 + 1, i5 + list2.size() * 9, Integer.MIN_VALUE);

         for (OrderedText orderedtext3 : list2) {
            int i7 = minecraftClient3.textRenderer.getWidth(orderedtext3);
            var1.drawTextWithShadow(minecraftClient3.textRenderer, orderedtext3, var2 / 2 - i7 / 2, i5, -1);
            i5 += 9;
         }
      }

      matrixstack.pop();
   }

   @Override
   public boolean ColorAnimator(double var1, double var3) {
      return false;
   }

   @Override
   public float blockPos30() {
      return 0.0F;
   }

   @Override
   public float blockPos31() {
      return 0.0F;
   }

   @Override
   public float zClass06744() {
      return 0.0F;
   }

   @Override
   public float int437() {
      return 0.0F;
   }
}
