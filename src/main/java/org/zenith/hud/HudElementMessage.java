package org.zenith.hud;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import java.util.Map.Entry;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.ChatTagParser;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudElementMessage extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float239 = 17.0F;
   public static final float float240 = 7.0F;
   public final UiAnimation var14323 = new UiAnimation(200L, 100.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14324 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14325 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final Map<String, HudElementMessage.MessageEntry> map44 = new LinkedHashMap<>();
   public final Set<String> set14 = new HashSet<>();

   public HudElementMessage(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      List<ChatTagParser> list = ZenithClient.on23().ProtocolMessage().getEvents();
      this.set14.clear();

      for (ChatTagParser lilli1lllliii1 : list) {
         String s = this.on23(lilli1lllliii1);
         this.set14.add(s);
         this.map44.computeIfAbsent(s, var2x -> new HudElementMessage.MessageEntry(this, lilli1lllliii1)).UiAnimation(lilli1lllliii1);
      }

      this.map44.values().removeIf(HudElementMessage.MessageEntry::float206);
      if (this.map44.isEmpty()) {
         this.var14324.on23(0.0F);
      } else {
         HudElementMessage.MessageEntry l11i11iilili_ii1il11l111ii11iil = this.map44.values().iterator().next();
         this.var14324.on23(this.map44.size() == 1 && l11i11iilili_ii1il11l111ii11iil.var1439.BotDisconnectEvent() == 0.0F ? 0.0F : 1.0F);
      }

      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font = Fonts.NEW_ICONS.getFont(5.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = this.x;
      float f1 = this.y;
      float f2 = (float)(
         17.0F + GuiStyle.PADDING.intValue()
            + this.map44.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var1439.CancellableEvent()).sum()
      );
      float f3 = (float)this.map44.values().stream().mapToDouble(HudElementMessage.MessageEntry::float205).max().orElse(100.0);
      f3 = this.var14323.on23(f3);
      this.width = f3;
      this.height = f2;
      this.var14325.on23(minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud() || !this.map44.isEmpty());
      float f4 = Interface.float212();
      var1.pushMatrix();
      var1.getMatrices().translate(f + f3 / 2.0F, f1 + f2 / 2.0F);
      var1.getMatrices().scale(this.var14325.CancellableEvent(), this.var14325.CancellableEvent());
      var1.getMatrices().translate(-(f + f3 / 2.0F), -(f1 + f2 / 2.0F));
      CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f4);
      var1.drawBlurHud(f, f1, f3, f2, 21.0F, ii1il11l111ii11iil, ArgbColor.var11934);
      var1.drawRoundedRect(f, f1, f3, f2, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
      var1.drawRoundedRect(f, f1, f3, 17.0F, ii1il11l111ii11iil, zenithstyle.getHeaderHudBackground().getColor());
      var1.drawText(font, "L", f + 8.0F, f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
      var1.drawText(font, "m", f + f3 - 8.0F - font.width("m"), f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
      var1.drawText(
         font1,
         "Events",
         f + 8.0F + font.width("L") + GuiStyle.PADDING.intValue(),
         f1 + (17.0F - font1.height()) / 2.0F,
         zenithstyle.getTextEnable().getColor()
      );
      if (this.var14325.CancellableEvent() == 1.0F) {
         float f5 = f1 + 17.0F + GuiStyle.PADDING.intValue();
         var1.enableScissor((int)f, (int)f1, (int)(f + f3), (int)(f1 + f2));

         for (Entry<String, HudElementMessage.MessageEntry> entry : this.map44.entrySet()) {
            HudElementMessage.MessageEntry l11i11iilili_ii1il11l111ii11iilx = entry.getValue();
            l11i11iilili_ii1il11l111ii11iilx.on23(var1, f, f5, f3, this.set14.contains(entry.getKey()));
            f5 += (l11i11iilili_ii1il11l111ii11iilx.getHeight() + GuiStyle.PADDING.intValue()) * l11i11iilili_ii1il11l111ii11iilx.var1439.CancellableEvent();
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }

   public String on23(ChatTagParser var1) {
      return var1.int370().name() + "|" + var1.call452() + "|" + var1.getMessage();
   }


   public static class MessageEntry {
      public final UiAnimation var1439 = new UiAnimation(150L, 0.01F, Easing.StopUsingItemEvent);
      public ChatTagParser var110;

      MessageEntry(HudElementMessage var1, ChatTagParser var2) {
         this.var110 = var2;
      }

      void UiAnimation(ChatTagParser var1) {
         this.var110 = var1;
      }

      float float205() {
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         Font font2 = Fonts.NEW_ICONS.getFont(5.5F);
         String s = this.var110.getDisplayName();
         String s1 = this.var110.call440();
         if (s1 == null || s1.isEmpty()) {
            s1 = "-";
         }

         float f = 100.0F;
         float f1 = 8.0F + font2.width(this.var110.getIcon()) + GuiStyle.PADDING.intValue() + font.width(s);
         float f2 = font1.width(s1);
         float f3 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f2);
         float f4 = GuiStyle.PADDING * 2 + f3 + 8.0F;
         float f5 = f - (f4 + 8.0F);
         if (f5 < f1 + 8.0F) {
            f += f1 + 8.0F - f5;
         }

         return f;
      }

      float getHeight() {
         return 7.0F;
      }

      void on23(CustomDrawContext var1, float var2, float var3, float var4, boolean var5) {
         this.var1439.on23(var5 ? 1.0F : 0.0F);
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         Font font2 = Fonts.NEW_ICONS.getFont(5.5F);
         String s = this.var110.getIcon();
         String s1 = this.var110.getDisplayName();
         String s2 = this.var110.call440();
         if (s2 == null || s2.isEmpty()) {
            s2 = "-";
         }

         float f = font1.width(s2);
         float f1 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         var1.getMatrices().scale(this.var1439.CancellableEvent(), this.var1439.CancellableEvent());
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         var1.drawText(font2, s, var2 + 8.0F, var3 + (this.getHeight() - font2.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
         var1.drawText(
            font,
            s1,
            var2 + 8.0F + font2.width(s) + GuiStyle.PADDING.intValue(),
            var3 + (this.getHeight() - font.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor()
         );
         float f2 = var2 + var4 - f1 - GuiStyle.PADDING * 2;
         var1.drawRoundedRect(f2, var3, f1, this.getHeight(), CornerRadius.MovementInputEvent(1.0F), zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font1, s2, f2 + (f1 - f) / 2.0F, var3 + (this.getHeight() - font1.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
         var1.popMatrix();
      }

      boolean float206() {
         return this.var1439.CancellableEvent() == 0.0F;
      }
   }
}
