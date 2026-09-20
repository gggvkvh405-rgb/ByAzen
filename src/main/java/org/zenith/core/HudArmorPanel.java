package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.misc.ClickAction;
import org.zenith.module.misc.ClickAction;
import org.zenith.module.render.Interface;
import org.zenith.module.misc.ServerHelper;
import org.zenith.module.misc.ServerHelper;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScoreboardUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudArmorPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float245 = 17.0F;
   public static final float float246 = 10.0F;
   public static final float float247 = 118.0F;
   public final UiAnimation var14336 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14337 = new UiAnimation(200L, 118.0F, Easing.StopUsingItemEvent);

   public HudArmorPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font = Fonts.NEW_ICONS.getFont(5.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font3 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      List<HudArmorSlotInfo> list = this.long140();
      boolean flag = minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud();
      float f = this.var14337.on23(this.on23(list, font2, font3));
      float f1 = 17.0F + GuiStyle.PADDING.intValue();
      if (!list.isEmpty()) {
         f1 += list.size() * (10.0F + GuiStyle.PADDING.intValue());
      }

      this.width = f;
      this.height = f1;
      this.var14336.on23(flag || !list.isEmpty());
      if (!(this.var14336.CancellableEvent() <= 0.01F)) {
         float f2 = this.x;
         float f3 = this.y;
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(Interface.float212());
         var1.pushMatrix();
         var1.getMatrices().translate(f2 + f / 2.0F, f3 + f1 / 2.0F);
         var1.getMatrices().scale(this.var14336.CancellableEvent(), this.var14336.CancellableEvent());
         var1.getMatrices().translate(-(f2 + f / 2.0F), -(f3 + f1 / 2.0F));
         var1.drawBlurHud(f2, f3, f, f1, 21.0F, ii1il11l111ii11iil, ArgbColor.var11934);
         var1.drawRoundedRect(f2, f3, f, f1, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
         var1.drawRoundedRect(f2, f3, f, 17.0F, ii1il11l111ii11iil, zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font, "n", f2 + 8.0F, f3 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
         var1.drawText(font, "m", f2 + f - 8.0F - font.width("m"), f3 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
         var1.drawText(
            font1,
            "ItemBinds",
            f2 + 8.0F + font.width("n") + GuiStyle.PADDING.intValue(),
            f3 + (17.0F - font1.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor()
         );
         float f4 = f3 + 17.0F + GuiStyle.PADDING.intValue();
         if (!list.isEmpty()) {
            for (HudArmorSlotInfo illlli1liiiil1i1lll111_ii1il11l111ii11iil : list) {
               this.on23(var1, illlli1liiiil1i1lll111_ii1il11l111ii11iil, f2, f4, f, font2, font3, zenithstyle);
               f4 += 10.0F + GuiStyle.PADDING.intValue();
            }
         }

         var1.popMatrix();
      }
   }

   public void on23(CustomDrawContext var1, HudArmorSlotInfo var2, float var3, float var4, float var5, Font var6, Font var7, ZenithStyle var8) {
      try {
         ArgbColor i11ii1llliilllii1i1 = var8.getHeaderHudBackground().getColor();
         ArgbColor i11ii1llliilllii1i11 = var8.getTextEnable().getColor();
         ArgbColor i11ii1llliilllii1i12 = var8.getTextEnable().getColor();
         float f = 10.0F;
         float f1 = var3 + 8.0F;
         float f2 = var7.width(var2.double107());
         float f3 = Math.max(14.0F, f2 + GuiStyle.PADDING.intValue() * 2.0F);
         float f4 = var3 + var5 - f3 - 8.0F;
         float f5 = f1 + f + GuiStyle.PADDING.intValue();
         this.on23(var1, var2.double108(), f1, var4, f);
         var1.drawText(var6, var2.AutoLeave(), f5, var4 + (10.0F - var6.height()) / 2.0F, i11ii1llliilllii1i12);
         var1.drawRoundedRect(f4, var4, f3, 10.0F, CornerRadius.MovementInputEvent(1.5F), i11ii1llliilllii1i1);
         var1.drawText(var7, var2.double107(), f4 + (f3 - f2) / 2.0F, var4 + (10.0F - var7.height()) / 2.0F, i11ii1llliilllii1i11);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void on23(CustomDrawContext var1, ItemStack var2, float var3, float var4, float var5) {
      float f = 8.0F;
      float f1 = f / 16.0F;
      var1.pushMatrix();
      var1.getMatrices().translate(var3 + (var5 - f) / 2.0F, var4 + (var5 - f) / 2.0F);
      var1.getMatrices().scale(f1, f1);
      var1.drawItem(var2, 0, 0);
      var1.drawItemBar(var2, 0, 0);
      var1.drawCooldownProgress(var2, 0, 0);
      var1.popMatrix();
   }

   public float on23(List<HudArmorSlotInfo> var1, Font var2, Font var3) {
      float f = 100.0F;

      for (HudArmorSlotInfo illlli1liiiil1i1lll111_ii1il11l111ii11iil : var1) {
         float f1 = Math.max(14.0F, var3.width(illlli1liiiil1i1lll111_ii1il11l111ii11iil.double107()) + GuiStyle.PADDING.intValue() * 2.0F);
         float f2 = 18.0F + GuiStyle.PADDING.intValue() + var2.width(illlli1liiiil1i1lll111_ii1il11l111ii11iil.AutoLeave()) + 8.0F + f1 + 8.0F;
         f = Math.max(f, f2);
      }

      return f;
   }

   public List<HudArmorSlotInfo> long140() {
      List<HudArmorSlotInfo> arraylist = new ArrayList<>();
      if (minecraftClient3 != null && minecraftClient3.player != null) {
         ServerHelper l1il1ili1illil1i = ServerHelper.serverHelper;
         this.on23(arraylist, l1il1ili1illil1i.int153(), l1il1ili1illil1i.int154());

         for (ServerHelper.HotkeyAction l1il1ili1illil1i_l1i1illlili : l1il1ili1illil1i.double18()) {
            this.on23(arraylist, l1il1ili1illil1i_l1i1illlili.double22(), l1il1ili1illil1i_l1i1illlili.int156());
         }

         ClickAction liil1li11l111lil1liiii1ill = ClickAction.clickAction;
         this.on23(arraylist, liil1li11l111lil1liiii1ill.double19(), ScreenUtils.SimpleItemBuilder(Items.EXPERIENCE_BOTTLE));

         for (ClickAction.ItemAction liil1li11l111lil1liiii1ill_ii1il11l111ii11iil : liil1li11l111lil1liiii1ill.double18()) {
            this.on23(
               arraylist,
               liil1li11l111lil1liiii1ill_ii1il11l111ii11iil.double22(),
               ScreenUtils.SimpleItemBuilder(liil1li11l111lil1liiii1ill_ii1il11l111ii11iil.double21())
            );
         }

         return arraylist;
      } else {
         return arraylist;
      }
   }

   public void on23(List<HudArmorSlotInfo> var1, KeySetting var2, Slot var3) {
      if (var2 != null && var3 != null && var2.getKeyCode() != -1 && var2.isVisible()) {
         ItemStack itemstack = var3.getStack().copy();
         if (!itemstack.isEmpty()) {
            var1.add(new HudArmorSlotInfo(var2.getName(), ScoreboardUtils.EventPosHook(var2.getKeyCode()), var2.getKeyCode(), itemstack));
         }
      }
   }
}
