package org.zenith.client.screens.override.main.page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.layout.MainMenuLayout;
import org.zenith.core.ClientSession;
import org.zenith.core.UsageStatStore;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class HomeMenuPage implements MainMenuPage {
   public static final float WIDTH = MainMenuLayout.px(522.0F);
   public static final float BODY_HEIGHT = MainMenuLayout.px(398.0F);
   public static final float FOOTER_HEIGHT = MainMenuLayout.px(74.0F);
   public static final List<String> CHANGELOG = List.of(
      "Improved KillAura rotations.",
      "Added new Flight mode for latest bypasses.",
      "Enhanced Scaffold with smart placement.",
      "Added Player ESP with health bars.",
      "New ChestStealer with custom delay.",
      "Improved Speed module stability.",
      "Added AutoArmor with priority.",
      "Optimized Inventory Manager.",
      "Removed outdated bypasses.",
      "Fixed random crashes during world loading."
   );

   @Override
   public float bodyWidth() {
      return WIDTH;
   }

   @Override
   public float bodyHeight() {
      return BODY_HEIGHT;
   }

   @Override
   public float footerHeight() {
      return FOOTER_HEIGHT;
   }

   @Override
   public void renderBody(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5) {
      ArgbColor i11ii1llliilllii1i1 = var5.getPrimaryColor().getColor();
      float f = MainMenuLayout.px(55.0F);
      float f1 = MainMenuLayout.px(10.0F);
      float f2 = MainMenuLayout.px(10.0F);
      this.drawPanel(var1, var4.x(), var4.y(), var4.width(), f, f2, var5);
      this.drawVersionHeader(var1, var4, f, i11ii1llliilllii1i1, var5);
      float f3 = var4.y() + f + f1;
      float f4 = var4.height() - f - f1;
      this.drawPanel(var1, var4.x(), f3, var4.width(), f4, f2, var5);
      this.drawChangelog(var1, var4.x(), f3, var4.width(), f4, i11ii1llliilllii1i1, var5);
   }

   public void drawVersionHeader(HudDrawContext var1, CornerRadiusF var2, float var3, ArgbColor var4, ZenithStyle var5) {
      Font font = Fonts.MEDIUM.getFont(MainMenuLayout.px(10.0F));
      Font font1 = Fonts.ICONS.getFont(MainMenuLayout.px(9.0F));
      float f = MainMenuLayout.px(24.0F);
      String s = "5";
      float f1 = var2.y() + (var3 - font1.height()) / 2.0F;
      var1.drawText(font1, s, var2.x() + f, f1, var4);
      var1.drawText(
         font,
         "Version 1.4",
         var2.x() + f + font1.width(s) + MainMenuLayout.px(10.0F),
         var2.y() + (var3 - font.height()) / 2.0F,
         var5.getTextEnable().getColor()
      );
      String s1 = "^";
      var1.drawText(font, s1, var2.x() + var2.width() - f - font.width(s1), var2.y() + (var3 - font.height()) / 2.0F, var5.getTextTertiary().getColor());
   }

   public void drawChangelog(HudDrawContext var1, float var2, float var3, float var4, float var5, ArgbColor var6, ZenithStyle var7) {
      Font font = Fonts.MEDIUM.getFont(MainMenuLayout.px(9.0F));
      float f = MainMenuLayout.px(24.0F);
      float f1 = MainMenuLayout.px(18.0F);
      float f2 = MainMenuLayout.px(27.0F);
      float f3 = var3 + f1;

      for (int i = 0; i < CHANGELOG.size(); i++) {
         boolean flag = i >= CHANGELOG.size() - 2;
         ArgbColor i11ii1llliilllii1i1 = flag ? var7.getTextTertiary().getColor() : var6;
         ArgbColor i11ii1llliilllii1i11 = flag ? var7.getTextSecondary().getColor() : var7.getTextEnable().getColor();
         String s = flag ? "-" : "+";
         var1.drawText(font, s, var2 + f, f3, i11ii1llliilllii1i1);
         var1.drawText(font, CHANGELOG.get(i), var2 + f + MainMenuLayout.px(20.0F), f3, i11ii1llliilllii1i11);
         f3 += f2;
      }

      float f4 = var2 + var4 - MainMenuLayout.px(20.0F);
      float f5 = var3 + MainMenuLayout.px(18.0F);
      float f6 = var5 - MainMenuLayout.px(36.0F);
      var1.drawRoundedRect(f4, f5, MainMenuLayout.px(2.0F), f6, CornerRadius.MovementInputEvent(MainMenuLayout.px(1.0F)), var7.getFieldBorder().getColor());
      var1.drawRoundedRect(
         f4, f5, MainMenuLayout.px(2.0F), f6 * 0.42F, CornerRadius.MovementInputEvent(MainMenuLayout.px(1.0F)), var7.getTextTertiary().getColor()
      );
   }

   @Override
   public void renderFooter(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5) {
      ClientSession ii1il11l111ii11iil_ii1il11l111ii11iil = ZenithClient.on23().CommandManager();
      String s = ZenithClient.on23().BotFeaturesDto() == null ? "0m" : UsageStatStore.ItemRegistry(ZenithClient.on23().BotFeaturesDto().ViewModel());
      List<HomeMenuPage_Stat> list = List.of(
         new HomeMenuPage_Stat("G", this.subscriptionDays(ii1il11l111ii11iil_ii1il11l111ii11iil.EmoteMetadata()), "Subscription"),
         new HomeMenuPage_Stat("a", ii1il11l111ii11iil_ii1il11l111ii11iil.CloudPoller(), "UID"),
         new HomeMenuPage_Stat("L", s, "Playtime"),
         new HomeMenuPage_Stat("e", "2,396", "Online")
      );
      float f = var4.width() / list.size();

      for (int i = 0; i < list.size(); i++) {
         this.drawStat(var1, list.get(i), var4.x() + f * i, var4.y(), f, var4.height(), var5);
      }
   }

   public void drawStat(HudDrawContext var1, HomeMenuPage_Stat var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      ArgbColor i11ii1llliilllii1i1 = var7.getPrimaryColor().getColor();
      Font font = Fonts.ICONS.getFont(MainMenuLayout.px(12.0F));
      Font font1 = Fonts.MEDIUM.getFont(MainMenuLayout.px(9.0F));
      Font font2 = Fonts.MEDIUM.getFont(MainMenuLayout.px(8.0F));
      float f = var3 + (var5 - font.width(var2.icon())) / 2.0F;
      var1.drawText(font, var2.icon(), f, var4, i11ii1llliilllii1i1);
      float f1 = var4 + MainMenuLayout.px(30.0F);
      var1.drawText(font1, var2.value(), var3 + (var5 - font1.width(var2.value())) / 2.0F, f1, var7.getTextEnable().getColor());
      float f2 = Math.min(var4 + var6 - font2.height(), f1 + MainMenuLayout.px(18.0F));
      var1.drawText(font2, var2.label(), var3 + (var5 - font2.width(var2.label())) / 2.0F, f2, var7.getTextTertiary().getColor());
   }

   public String subscriptionDays(String var1) {
      try {
         LocalDate localdate = LocalDate.parse(var1, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
         long i = Math.max(0L, ChronoUnit.DAYS.between(LocalDate.now(), localdate));
         return i + " days left";
      } catch (Exception exception) {
         return var1 != null && !var1.isBlank() ? var1 : "-";
      }
   }

   public void drawPanel(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      var1.drawRoundedRect(var2, var3, var4, var5, CornerRadius.MovementInputEvent(var6), var7.getSurfaceDisableBackground().getColor());
      var1.drawRoundedBorder(var2, var3, var4, var5, -0.1F, CornerRadius.MovementInputEvent(var6), var7.getFieldBorder().getColor());
   }
}
