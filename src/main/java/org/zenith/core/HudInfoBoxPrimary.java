package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.TextUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudInfoBoxPrimary extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation var14344;
   public final MultiSelectSetting infoBoxes = new MultiSelectSetting("Info Boxes");
   public final MultiSelectSetting.Option modeSettingVar15919;
   public final MultiSelectSetting.Option modeSettingVar15920 = new MultiSelectSetting.Option(this.infoBoxes, "Coordinates", true);
   public final UiAnimation var14345;

   public HudInfoBoxPrimary(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.modeSettingVar15919 = new MultiSelectSetting.Option(this.infoBoxes, "BPS", true);
      this.var14344 = new UiAnimation(200L, Easing.StopUsingItemEvent);
      this.var14345 = new UiAnimation(200L, Easing.StopUsingItemEvent);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      if (minecraftClient3.player == null) {
         this.width = 0.0F;
         this.height = 0.0F;
      } else {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.ICONS.getFont(5.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
         double d0 = Math.hypot(
               minecraftClient3.player.getX() - minecraftClient3.player.lastX,
               minecraftClient3.player.getZ() - minecraftClient3.player.lastZ
            )
            * 20.0;
         int i = (int)minecraftClient3.player.getX();
         int j = (int)minecraftClient3.player.getY();
         int k = (int)minecraftClient3.player.getZ();
         if (TextUtils.isActive()) {
            i = TextUtils.EventUpdateHealth(i);
            k = TextUtils.JumpEvent(k);
         }

         String s = String.format(Locale.US, "%.2f", d0);
         String s1 = String.format(Locale.US, "%.2f", ZenithClient.on23().CloudApiClient().set11());
         String s2 = this.boolean147();
         String s3 = String.valueOf(MinecraftClient.getInstance().getCurrentFps());
         boolean flag = !this.modeSettingVar15920.isEnabled() && !this.modeSettingVar15919.isEnabled();
         ArrayList<HudInfoBoxPrimaryLine> arraylist = new ArrayList<>(5);
         arraylist.add(this.UiAnimation(font1, font2, font, i, j, k, this.var14344, flag || this.modeSettingVar15920.isEnabled()));
         arraylist.add(this.UiAnimation(font1, font2, font, "1", s, " bps", this.var14345, flag || this.modeSettingVar15919.isEnabled()));
         ArrayList arraylist1 = new ArrayList(arraylist.size());
         float f = -GuiStyle.PADDING;

         for (HudInfoBoxPrimaryLine i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil : arraylist) {
            if (i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil.float29 > 0.25F) {
               arraylist1.add(i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil);
               f += i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil.float29 + GuiStyle.PADDING.intValue();
            }
         }

         if (arraylist1.isEmpty()) {
            arraylist1 = arraylist;
         }

         float f6 = 17.0F;
         this.width = f;
         this.height = f6;
         float f7 = Interface.float212();
         var1.drawBlurHud(this.x, this.y, f, f6, 21.0F, CornerRadius.MovementInputEvent(f7), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, f, f6, CornerRadius.MovementInputEvent(f7), zenithstyle.getHudBackground().getColor());
         float f1 = this.x;

         for (int l = 0; l < arraylist1.size(); l++) {
            HudInfoBoxPrimaryLine i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1 = (HudInfoBoxPrimaryLine)arraylist1.get(l);
            var1.drawRoundedRect(
               f1,
               this.y,
               i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.float29,
               f6,
               CornerRadius.MovementInputEvent(f7),
               zenithstyle.getHeaderHudBackground().getColor()
            );
            float f2 = f1 + GuiStyle.PADDING.intValue();
            float f3 = this.y + (f6 - font.height()) / 2.0F;
            var1.drawText(font, i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.string18, f2, f3, zenithstyle.getPrimaryColor().getColor());
            float f4 = f2 + i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.float28 + GuiStyle.PADDING.intValue() / 2.0F;
            float f5 = Math.max(
               0.0F,
               i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.float29
                  - GuiStyle.PADDING.intValue() / 2.0F * 2.0F
                  - i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.float28
                  - GuiStyle.PADDING.intValue() / 2.0F
            );
            this.on23(
               var1,
               i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.list34,
               font1,
               font2,
               f4,
               this.y,
               f6,
               f5,
               zenithstyle.getTextEnable().getColor(),
               zenithstyle.getTextTertiary().getColor()
            );
            f1 += i1iiiil1liili1il1lil1liii1li_ii1il11l111ii11iil1.float29 + (l != arraylist1.size() - 1 ? GuiStyle.PADDING : 0);
         }
      }
   }

   public HudInfoBoxPrimaryLine UiAnimation(Font var1, Font var2, Font var3, int var4, int var5, int var6, UiAnimation var7, boolean var8) {
      List<HudInfoBoxPrimaryState> list = List.of(
         HudInfoBoxPrimaryState.Event14(String.valueOf(var4)),
         HudInfoBoxPrimaryState.HealthUpdateEvent("x "),
         HudInfoBoxPrimaryState.Event14(String.valueOf(var5)),
         HudInfoBoxPrimaryState.HealthUpdateEvent("y "),
         HudInfoBoxPrimaryState.Event14(String.valueOf(var6)),
         HudInfoBoxPrimaryState.HealthUpdateEvent("z")
      );
      return this.UiAnimation(var3, var1, var2, "J", list, var7, var8);
   }

   public HudInfoBoxPrimaryLine UiAnimation(Font var1, Font var2, Font var3, String var4, String var5, String var6, UiAnimation var7, boolean var8) {
      List<HudInfoBoxPrimaryState> list = List.of(HudInfoBoxPrimaryState.Event14(var5), HudInfoBoxPrimaryState.HealthUpdateEvent(var6));
      return this.UiAnimation(var3, var1, var2, var4, list, var7, var8);
   }

   public HudInfoBoxPrimaryLine UiAnimation(Font var1, Font var2, Font var3, String var4, List<HudInfoBoxPrimaryState> var5, UiAnimation var6, boolean var7) {
      float f = var1.width(var4);
      float f1 = 0.0F;

      for (HudInfoBoxPrimaryState i1iiiil1liili1il1lil1liii1li_l1i1illlili : var5) {
         f1 += i1iiiil1liili1il1lil1liii1li_l1i1illlili.boolean61
            ? var3.width(i1iiiil1liili1il1lil1liii1li_l1i1illlili.string19)
            : var2.width(i1iiiil1liili1il1lil1liii1li_l1i1illlili.string19);
      }

      float f2 = var7 ? GuiStyle.PADDING.intValue() + f + GuiStyle.PADDING.intValue() / 2.0F + f1 + GuiStyle.PADDING.intValue() : 0.0F;
      float f3 = MathHelper.lerp(0.2F, var6.CancellableEvent(), f2);
      var6.setValue(f3);
      return new HudInfoBoxPrimaryLine(var4, f, var5, f3);
   }

   public void on23(
      CustomDrawContext var1,
      List<HudInfoBoxPrimaryState> var2,
      Font var3,
      Font var4,
      float var5,
      float var6,
      float var7,
      float var8,
      ArgbColor var9,
      ArgbColor var10
   ) {
      float f = 0.0F;

      for (HudInfoBoxPrimaryState i1iiiil1liili1il1lil1liii1li_l1i1illlili : var2) {
         Font font = i1iiiil1liili1il1lil1liii1li_l1i1illlili.boolean61 ? var4 : var3;
         float f1 = font.width(i1iiiil1liili1il1lil1liii1li_l1i1illlili.string19);
         float f2 = var8 - f;
         if (f2 <= 0.1F) {
            break;
         }

         ArgbColor i11ii1llliilllii1i1 = i1iiiil1liili1il1lil1liii1li_l1i1illlili.boolean61 ? var10 : var9;
         float f3 = var6 + (var7 - font.height()) / 2.0F;
         var1.drawText(font, i1iiiil1liili1il1lil1liii1li_l1i1illlili.string19, var5 + f, f3, i11ii1llliilllii1i1, true, 0.72F, 1.0F, f2);
         f += f1;
      }
   }

   public String boolean147() {
      if (minecraftClient3.player != null && minecraftClient3.getNetworkHandler() != null) {
         PlayerListEntry playerlistentry = minecraftClient3.getNetworkHandler().getPlayerListEntry(minecraftClient3.player.getUuid());
         return playerlistentry == null ? "-" : String.valueOf(Math.max(0, playerlistentry.getLatency()));
      } else {
         return "-";
      }
   }
}
