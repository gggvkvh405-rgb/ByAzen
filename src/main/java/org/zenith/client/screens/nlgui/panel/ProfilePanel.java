package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import net.minecraft.util.math.RotationAxis;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.ClientSession;
import org.zenith.core.CloudPoller;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ButtonSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class ProfilePanel extends Panel {
   public final UiAnimation animationVisible = new UiAnimation(300L, 0.0F, Easing.StopUsingItemEvent);
   public boolean expanded = false;
   public final GuiBooleanSetting steamMode = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.streamerMode", false), 112.0F);
   public final GuiBooleanSetting discordRPC = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.discordRpc", true), 112.0F);
   public final GuiButtonSetting webProfile = new GuiButtonSetting(new ButtonSetting("panel.profile.setting.webProfile", "J", () -> {
      try {
         new ProcessBuilder("explorer", CloudPoller.file7.getAbsolutePath()).start();
      } catch (IOException var1) {
      }
   }), 112.0F);
   public final GuiButtonSetting openFolder = new GuiButtonSetting(new ButtonSetting("panel.profile.setting.openFolder", "K", () -> {
      try {
         new ProcessBuilder("explorer", CloudPoller.file7.getAbsolutePath()).start();
      } catch (IOException var1) {
      }
   }), 112.0F);
   public final UiAnimation languageLeftHoverAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation languageRightHoverAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation languageSwitchAnimation = new UiAnimation(120L, 1.0F, Easing.HotbarInputEvent);
   public CornerRadiusF languageLeftBounds;
   public CornerRadiusF languageRightBounds;
   public CornerRadiusF exitBounds;
   public String displayedLanguageName;
   public String outgoingLanguageName;
   public int languageSwitchDirection = 1;
   public boolean languageSwitchActive;

   public void renderTill(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, CornerRadius.var159, var9.getLeftBackground().getColor().SprintStateEvent(var4));
      float f = var5 + GuiStyle.PADDING * 2;
      float f1 = var6 + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      ClientSession ii1il11l111ii11iil_ii1il11l111ii11iil = ZenithClient.on23().CommandManager();
      float f2 = 1.4F;
      String s = "01.01.2048";
      DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate localdate = LocalDate.parse(s, datetimeformatter);
      LocalDate localdate1 = LocalDate.now();
      float f3;
      long i;
      if (localdate1.isAfter(localdate)) {
         f3 = 0.0F;
         i = 0L;
      } else {
         i = ChronoUnit.DAYS.between(localdate1, localdate);
         f3 = Math.min(1.0F, Math.max(0.0F, (float)i / 365.0F));
      }

      var1.drawText(font, "Active: " + i + " day", f, f1, var9.getTextEnable().getColor().SprintStateEvent(var4));
      Font font1 = Fonts.NEW_ICONS.getFont(5.0F);
      float f4 = font1.width("G");
      var1.drawText(
         font,
         "Extend",
         var5 + var7 - GuiStyle.PADDING * 2 - f4 - font.width("Extend") - GuiStyle.PADDING.intValue(),
         f1,
         var9.getPrimaryColor().getColor().SprintStateEvent(var4)
      );
      var1.drawText(font1, "G", var5 + var7 - GuiStyle.PADDING * 2 - f4, f1, var9.getPrimaryColor().getColor().SprintStateEvent(var4));
      float f5 = var6 + var8 - GuiStyle.PADDING * 2 - f2;
      float f6 = var7 - GuiStyle.PADDING * 4;
      float f7 = f6 * f3;
      var1.drawRoundedRectBatched(f, f5, f6, f2, CornerRadius.MovementInputEvent(0.01F), var9.getFieldSurfaceBackground().getColor().SprintStateEvent(var4));
      var1.drawRoundedRectBatched(f, f5, f7, f2, CornerRadius.MovementInputEvent(0.01F), var9.getPrimaryColor().getColor().SprintStateEvent(var4));
      var1.flushRoundedRects();
   }

   public void renderSettings(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, CornerRadius.var159, var9.getRightBackground().getColor().SprintStateEvent(var4));
      float f = var5 + GuiStyle.PADDING * 2;
      float f1 = var6 + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font1 = Fonts.NEW_REGULAR.getFont(5.0F);
      var1.drawText(font1, "Settings", f, f1, var9.getTextSecondary().getColor().SprintStateEvent(var4));
      f1 += font1.height() + GuiStyle.PADDING.intValue();
      boolean flag = ZenithClient.on23().NbtEditor().isRenderIcon();
      ZenithClient.on23().NbtEditor().setRenderIcon(false);
      this.steamMode.render(var1, var2, var3, f, f1, var4);
      f1 += this.steamMode.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.discordRPC.render(var1, var2, var3, f, f1, var4);
      ZenithClient.on23().NbtEditor().setRenderIcon(flag);
      float f2 = this.steamMode.getHeight();
      float f3 = f1 + this.discordRPC.getAnimHeight() + GuiStyle.PADDING.intValue();
      String s = "A";
      float f4 = GuiStyle.PADDING.intValue();
      float f5 = 6.0F;
      float f6 = 2.0F;
      Font font2 = Fonts.ICONS.getFont(5.0F);
      float f7 = font2.width(s);
      float f8 = this.getLanguageBlockWidth(font1, f5, f6);
      float f9 = var5 + var7 - GuiStyle.PADDING.intValue() * 2.0F;
      float f10 = f9 - f7;
      float f11 = f10 - f4 - f8;
      float f12 = f11 - f4 - f7;
      float f13 = f3 + (f2 - font.height()) / 2.0F;
      float f14 = f3 + (f2 - font2.height()) / 2.0F;
      float f15 = f14 + 0.9F;
      float f16 = f3 + 1.4F;
      float f17 = f11 + f5 + f6;
      float f18 = f3 + (f2 - font1.height()) / 2.0F;
      float f19 = 2.0F;
      this.languageLeftBounds = new CornerRadiusF(f12 - f19, f3 - f19, f7 + f19 * 2.0F, f2 + f19 * 2.0F);
      this.languageRightBounds = new CornerRadiusF(f10 - f19, f3 - f19, f7 + f19 * 2.0F, f2 + f19 * 2.0F);
      boolean flag1 = this.languageLeftBounds.PotionItemBuilder(var2, var3);
      boolean flag2 = this.languageRightBounds.PotionItemBuilder(var2, var3);
      float f20 = this.languageLeftHoverAnimation.on23(flag1 ? 1.0F : 0.0F);
      float f21 = this.languageRightHoverAnimation.on23(flag2 ? 1.0F : 0.0F);
      String s1 = ZenithClient.on23().Easing().getLanguageName();
      this.syncDisplayedLanguage(s1);
      var1.drawText(font, "Language", f, f13, var9.getTextEnable().getColor().SprintStateEvent(var4));
      this.drawLanguageArrow(var1, font2, s, f12, f15, var9.getPrimaryColor().getColor().SprintStateEvent(var4), 180.0F);
      if (this.languageSwitchActive) {
         float f22 = this.clamp01(this.languageSwitchAnimation.EmotePlayback());
         float f23 = Math.max(8.0F, f8 + f4);
         float f24 = -this.languageSwitchDirection * f22 * f23;
         float f25 = this.languageSwitchDirection * (1.0F - f22) * f23;
         var1.enableScissor(f11 - 1.0F, f3 - f19, f11 + f8 + 1.0F, f3 + f2 + f19);
         this.drawLanguageValue(var1, font1, this.outgoingLanguageName, f11 + f24, f16, f17 + f24, f18, f5, var4, var9);
         this.drawLanguageValue(var1, font1, this.displayedLanguageName, f11 + f25, f16, f17 + f25, f18, f5, var4, var9);
         var1.disableScissor();
         if (this.languageSwitchAnimation.isDone() || f22 >= 0.999F) {
            this.languageSwitchActive = false;
            this.outgoingLanguageName = null;
         }
      } else {
         this.drawLanguageValue(var1, font1, this.displayedLanguageName, f11, f16, f17, f18, f5, var4, var9);
      }

      this.drawLanguageArrow(var1, font2, s, f10, f14, var9.getPrimaryColor().getColor().SprintStateEvent(var4), 0.0F);
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.animationVisible.on23(this.expanded);
      this.animationVisible.on23(200L);
      var4 *= this.animationVisible.CancellableEvent();
      float f = 128.0F;
      float f1 = 190.0F;
      var1.enableScissor(var5, var6, var5 + f + GuiStyle.PADDING * 4, var6 + f1);
      var5 += (f + GuiStyle.PADDING.intValue()) * (1.0F - this.animationVisible.CancellableEvent());
      if (ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F) {
         ShapeRenderer.on23(
            var1.getMatrices(),
            var5,
            var6,
            f,
            f1,
            ZenithClient.on23().NbtEditor().getBlurPower(),
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            ArgbColor.var11934.SprintStateEvent(var4),
            true,
            false
         );
      }

      float f2 = 29.0F;
      float f3 = 27.0F;
      float f4 = 56.0F;
      float f5 = 45.0F;
      float f6 = 33.0F;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.renderHeader(var1, var2, var3, var4, var5, var6, f, f2, zenithstyle);
      this.renderTill(var1, var2, var3, var4, var5, var6 + f2, f, f3, zenithstyle);
      this.renderSettings(var1, var2, var3, var4, var5, var6 + f2 + f3, f, f4, zenithstyle);
      this.renderInfo(var1, var2, var3, var4, var5, var6 + f2 + f3 + f4, f, f5, zenithstyle);
      this.renderButton(var1, var2, var3, var4, var5, var6 + f2 + f3 + f4 + f5, f, f6, zenithstyle);
      var1.disableScissor();
   }

   public void renderButton(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, CornerRadius.RotationUpdateStartEvent(GuiStyle.ROUND.intValue()), var9.getRightBackground().getColor().SprintStateEvent(var4));
      float f = var5 + GuiStyle.PADDING * 2;
      float f1 = var6 + GuiStyle.PADDING * 2;
      float f2 = var7 - GuiStyle.PADDING * 4;
      float f3 = var8 - GuiStyle.PADDING * 4;
      var1.drawRoundedRect(f, f1, f2, f3, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F), new ArgbColor("#FF97A0").SprintStateEvent(var4));
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font1 = Fonts.NEW_ICONS.getFont(5.0F);
      float f4 = font1.width("I");
      float f5 = font.width("Русская рулетка");
      float f6 = f4 + GuiStyle.PADDING.intValue() + f5;
      float f7 = f + (f2 - f6) / 2.0F;
      var1.drawText(font1, "I", f7, f1 + (f3 - font1.height()) / 2.0F, var9.getTextEnable().getColor(var4));
      var1.drawText(font, "Русская рулетка", f7 + f4 + GuiStyle.PADDING.intValue(), f1 + (f3 - font.height()) / 2.0F, var9.getTextEnable().getColor(var4));
   }

   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, CornerRadius.Event29(GuiStyle.ROUND.intValue()), var9.getRightBackground().getColor().SprintStateEvent(var4));
      float f = var5 + GuiStyle.PADDING * 2;
      float f1 = var6 + GuiStyle.PADDING * 2;
      float f2 = 13.0F;
      ShapeRenderer.on23(
         var1.getMatrices(),
         ZenithClient.on23("icons/avatar.png"),
         f,
         f1,
         f2,
         f2,
         CornerRadius.MovementInputEvent(4.0F),
         ArgbColor.var11934.SprintStateEvent(var4)
      );
      Font font = Fonts.NEW_MEDIUM.getFont(4.2F);
      Font font1 = Fonts.NEW_REGULAR.getFont(4.5F);
      float f3 = f + f2 + GuiStyle.PADDING.intValue();
      var1.drawText(font, ClientSession.DISPLAY_CREDIT_FIRST_LINE, f3, f1 + 1.0F, var9.getTextEnable().getColor().SprintStateEvent(var4));
      var1.drawText(
         font1,
         ClientSession.DISPLAY_CREDIT_SECOND_LINE,
         f3,
         f1 + f2 - font1.height() - 1.0F,
         var9.getTextTertiary().getColor().SprintStateEvent(var4)
      );
      Font font2 = Fonts.NEW_ICONS.getFont(4.0F);
      this.exitBounds = new CornerRadiusF(var5 + var7 - font2.width("2") - GuiStyle.PADDING * 2, f1 + 2.0F, 5.0F, 5.0F);
      var1.drawText(font2, "2", var5 + var7 - font2.width("2") - GuiStyle.PADDING * 2, f1 + 2.0F, var9.getTextTertiary().getColor().SprintStateEvent(var4));
   }

   public void drawLanguageArrow(HudDrawContext var1, Font var2, String var3, float var4, float var5, ArgbColor var6, float var7) {
      float f = var4 + var2.width(var3) / 2.0F;
      float f1 = var5 + var2.height() / 2.0F;
      var1.pushMatrix();
      var1.getMatrices().translate(f, f1);
      var1.getMatrices().rotate((float)Math.toRadians(var7));
      var1.getMatrices().translate(-f, -f1);
      var1.drawText(var2, var3, var4, var5, var6);
      var1.popMatrix();
   }

   public void drawLanguageValue(
      HudDrawContext var1, Font var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      String s = this.normalizeLanguageName(var3);
      ShapeRenderer.on23(
         var1.getMatrices(),
         ZenithClient.on23(this.getLanguageFlagPath(s)),
         var4,
         var5,
         var8,
         5.5F,
         CornerRadius.MovementInputEvent(1.0F),
         ArgbColor.var11934.SprintStateEvent(var9)
      );
      var1.drawText(var2, s.toUpperCase(Locale.ENGLISH), var6, var7, var10.getTextEnable().getColor().SprintStateEvent(var9));
   }

   public float getLanguageBlockWidth(Font var1, float var2, float var3) {
      float f = Math.max(Math.max(var1.width("RU"), var1.width("EN")), Math.max(var1.width("PL"), var1.width("TR")));
      return var2 + var3 + f;
   }

   public String getLanguageFlagPath(String var1) {
      String s = this.normalizeLanguageName(var1);

      return switch (s) {
         case "ru" -> "icons/russia.png";
         case "pl" -> "icons/poland.png";
         case "tr" -> "icons/turkey.png";
         default -> "icons/usa.png";
      };
   }

   public void syncDisplayedLanguage(String var1) {
      if (this.displayedLanguageName == null) {
         this.displayedLanguageName = var1;
      } else if (!this.languageSwitchActive && !this.sameLanguage(this.displayedLanguageName, var1)) {
         this.displayedLanguageName = var1;
      }
   }

   public void switchLanguage(boolean var1) {
      String s = ZenithClient.on23().Easing().getLanguageName();
      ZenithClient.on23().Easing().StringCodec(var1);
      String s1 = ZenithClient.on23().Easing().getLanguageName();
      if (!this.sameLanguage(s, s1)) {
         this.outgoingLanguageName = this.displayedLanguageName == null ? s : this.displayedLanguageName;
         this.displayedLanguageName = s1;
         this.languageSwitchDirection = var1 ? 1 : -1;
         this.languageSwitchActive = true;
         this.languageSwitchAnimation.UiAnimation(0.0F);
         this.languageSwitchAnimation.Easing(1.0F);
      }
   }

   public boolean sameLanguage(String var1, String var2) {
      return this.normalizeLanguageName(var1).equals(this.normalizeLanguageName(var2));
   }

   public String normalizeLanguageName(String var1) {
      return var1 != null && !var1.isBlank() ? var1.trim().toLowerCase(Locale.ENGLISH) : "ru";
   }

   public float clamp01(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   public void renderInfo(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, CornerRadius.var159, var9.getLeftBackground().getColor().SprintStateEvent(var4));
      float f = var5 + GuiStyle.PADDING * 2;
      float f1 = var6 + GuiStyle.PADDING * 2;
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font1 = Fonts.NEW_REGULAR.getFont(5.0F);
      var1.drawText(font1, "Info", f, f1, var9.getTextSecondary().getColor().SprintStateEvent(var4));
      f1 += font1.height() + GuiStyle.PADDING.intValue();
      this.webProfile.render(var1, var2, var3, f, f1, var4);
      f1 += this.webProfile.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.openFolder.render(var1, var2, var3, f, f1, var4);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5.int203() != 0 || !this.expanded) {
         return false;
      }

      if (this.steamMode.onMouseClicked(var1, var3, var5)) {
         return true;
      }

      if (this.discordRPC.onMouseClicked(var1, var3, var5)) {
         return true;
      }

      if (this.webProfile.onMouseClicked(var1, var3, var5)) {
         return true;
      }

      if (this.openFolder.onMouseClicked(var1, var3, var5)) {
         return true;
      }

      if (this.exitBounds != null && this.exitBounds.on23(var1, var3, 2.0F)) {
         this.expanded = false;
      }

      if (this.languageLeftBounds != null && this.languageLeftBounds.PotionItemBuilder(var1, var3)) {
         this.switchLanguage(false);
         return true;
      } else if (this.languageRightBounds != null && this.languageRightBounds.PotionItemBuilder(var1, var3)) {
         this.switchLanguage(true);
         return true;
      } else {
         return false;
      }
   }

   public void setExpanded(boolean var1) {
      this.expanded = var1;
   }

   public void toggleExpanded() {
      this.expanded = !this.expanded;
   }

   public boolean isRender() {
      return this.animationVisible.CancellableEvent() > 0.0F || this.expanded;
   }

   public float getAnimationProgress() {
      return this.animationVisible.CancellableEvent();
   }

   public void safe(JsonObject var1) {
      var1.addProperty("expanded", this.expanded);
      var1.addProperty("language", ZenithClient.on23().Easing().CryptoUtils());
      this.steamMode.getSetting().safe(var1);
      this.discordRPC.getSetting().safe(var1);
   }

   public void load(JsonObject var1) {
      if (var1.has("expanded")) {
         this.expanded = var1.get("expanded").getAsBoolean();
      }

      if (var1.has("language")) {
         ZenithClient.on23().Easing().CloseScreenEvent(var1.get("language").getAsString());
      }

      if (var1.has(this.steamMode.getSetting().getKey())) {
         this.steamMode.getSetting().load(var1);
      }

      if (var1.has(this.discordRPC.getSetting().getKey())) {
         this.discordRPC.getSetting().load(var1);
      }
   }
}
