package org.zenith.hud;


import net.minecraft.client.util.DefaultSkinHelper;
import org.zenith.core.ClientProvider;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import java.util.Map.Entry;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.util.TextReplaceUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudElementMedia extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float236 = 17.0F;
   public static final float float237 = 7.0F;
   public UiAnimation var14320 = new UiAnimation(200L, 100.0F, Easing.StopUsingItemEvent);
   public UiAnimation var14321 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public UiAnimation var14322 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public Map<String, HudElementMedia.PlaybackController> map37 = new LinkedHashMap<>();
   public Set<String> set12 = Set.of("helper", "ᴀдмин", "moder", "staff", "admin", "curator", "стажёр", "сотрудник", "помощник", "админ", "модер");
   public Map<String, Identifier> map38 = new HashMap<>();
   public long long145 = 0L;
   public long long146 = 0L;
   public Set<String> set13 = new HashSet<>();

   public HudElementMedia(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      long i = System.currentTimeMillis();
      if (i - this.long145 > 1000L && minecraftClient3.getNetworkHandler() != null) {
         this.int439();
         this.long145 = i;
      }

      if (i - this.long146 > 30000L) {
         this.map38.clear();
         this.long146 = i;
      }

      this.map37.entrySet().removeIf(var0 -> var0.getValue().float206());
      if (this.map37.isEmpty()) {
         this.var14321.on23(0.0F);
      } else {
         HudElementMedia.PlaybackController l11i1l1l11l111iil1_l1i1illlilix = this.map37.values().iterator().next();
         this.var14321.on23(this.map37.size() == 1 && l11i1l1l11l111iil1_l1i1illlilix.var14310.BotDisconnectEvent() == 0.0F ? 0.0F : 1.0F);
      }

      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font = Fonts.NEW_ICONS.getFont(5.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = this.x;
      float f1 = this.y;
      float f2 = (float)(
         17.0F + GuiStyle.PADDING.intValue()
            + this.map37.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var14310.CancellableEvent()).sum()
      );
      float f3 = (float)this.map37.values().stream().mapToDouble(HudElementMedia.PlaybackController::float205).max().orElse(100.0);
      f3 = this.var14320.on23(f3);
      this.width = f3;
      this.height = f2;
      float f4 = Interface.float212();
      this.var14322.on23(minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud() || !this.map37.isEmpty());
      var1.pushMatrix();
      var1.getMatrices().translate(f + f3 / 2.0F, f1 + f2 / 2.0F);
      var1.getMatrices().scale(this.var14322.CancellableEvent(), this.var14322.CancellableEvent());
      var1.getMatrices().translate(-(f + f3 / 2.0F), -(f1 + f2 / 2.0F));
      var1.drawBlurHud(f, f1, f3, f2, 21.0F, CornerRadius.MovementInputEvent(f4), ArgbColor.var11934);
      var1.drawRoundedRect(f, f1, f3, f2, CornerRadius.MovementInputEvent(f4), zenithstyle.getHudBackground().getColor());
      var1.drawRoundedRect(f, f1, f3, 17.0F, CornerRadius.MovementInputEvent(f4), zenithstyle.getHeaderHudBackground().getColor());
      var1.drawText(font, "P", f + 8.0F, f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
      var1.drawText(font, "m", f + f3 - 8.0F - font.width("m"), f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
      var1.drawText(
         font1,
         "Staffs",
         f + 8.0F + font.width("P") + GuiStyle.PADDING.intValue(),
         f1 + (17.0F - font1.height()) / 2.0F,
         zenithstyle.getTextEnable().getColor()
      );
      if (this.var14322.CancellableEvent() == 1.0F) {
         float f5 = f1 + 17.0F + GuiStyle.PADDING.intValue();
         var1.enableScissor((int)f, (int)f1, (int)(f + f3), (int)(f1 + f2));

         for (Entry<String, HudElementMedia.PlaybackController> entry : this.map37.entrySet()) {
            HudElementMedia.PlaybackController l11i1l1l11l111iil1_l1i1illlili = entry.getValue();
            l11i1l1l11l111iil1_l1i1illlili.on23(var1, f, f5, f3, this.set13.contains(entry.getKey()));
            f5 += (l11i1l1l11l111iil1_l1i1illlili.getHeight() + GuiStyle.PADDING.intValue()) * l11i1l1l11l111iil1_l1i1illlili.var14310.CancellableEvent();
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }

   public void int439() {
      if (minecraftClient3.getNetworkHandler() != null) {
         this.set13.clear();

         for (PlayerListEntry playerlistentry : minecraftClient3.getNetworkHandler().getPlayerList()) {
            GameProfile gameprofile = playerlistentry.getProfile();
            Text text = playerlistentry.getDisplayName();
            if (gameprofile != null) {
               String s = gameprofile.name();
               boolean flag = ZenithClient.on23().CloudUserProfile().CrosshairTargetUpdateEvent(s);
               if (text != null || flag) {
                  String s1 = text != null ? text.getString() : s;
                  String s2 = s1.replace(s, "").trim();
                  if (flag || this.RenderTickEvent(s2) && s2.length() >= 2) {
                     HudElementMedia.MediaType l11i1l1l11l111iil1_illi1l1l1 = playerlistentry.getGameMode() == GameMode.SPECTATOR
                        ? HudElementMedia.MediaType.val510
                        : HudElementMedia.MediaType.val509;
                     if (text != null) {
                        text = text.getString().contains(gameprofile.name())
                           ? TextReplaceUtils.on23(text, gameprofile.name(), false)
                           : TextReplaceUtils.on23(text, false);
                     } else {
                        text = Text.of(s);
                     }

                     Text text1 = text;
                     this.map37.computeIfAbsent(s1, var5x -> new HudElementMedia.PlaybackController(this, text1, s1, s, l11i1l1l11l111iil1_illi1l1l1));
                     this.set13.add(s1);
                  }
               }
            }
         }
      }
   }

   public boolean RenderTickEvent(String var1) {
      String s = var1.toLowerCase(Locale.US);

      for (String s1 : this.set12) {
         if (s.contains(s1)) {
            return true;
         }
      }

      return false;
   }

   public String ItemRegistry(long var1) {
      long i = var1 / 60000L;
      long j = var1 % 60000L / 1000L;
      return String.format("%d:%02d", i, j);
   }


   public static class PlaybackController {
      public final HudElementMedia val196;
      public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
      public final UiAnimation var14310;
      public final Text text5;
      public final String string40;
      public final String string41;
      public final MediaType var140Var1652;
      public final long long102;

      public PlaybackController(HudElementMedia var1, Text var2, String var3, String var4, MediaType var5) {
         this.val196 = var1;
         this.var14310 = new UiAnimation(150L, 0.01F, Easing.StopUsingItemEvent);
         this.text5 = var2;
         this.string40 = var3;
         this.string41 = var4;
         this.var140Var1652 = var5;
         this.long102 = System.currentTimeMillis();
      }

      public float float205() {
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         float f = 100.0F;
         String s = this.val196.ItemRegistry(System.currentTimeMillis() - this.long102);
         float f1 = 14 + GuiStyle.PADDING + font.width(this.text5);
         float f2 = font1.width(s);
         float f3 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f2);
         float f4 = GuiStyle.PADDING * 2 + f3 + 8.0F;
         float f5 = f - (f4 + 8.0F);
         if (f5 < f1 + 8.0F) {
            f += f1 + 8.0F - f5;
         }

         return f;
      }

      public float getHeight() {
         return 7.0F;
      }

      public void on23(CustomDrawContext var1, float var2, float var3, float var4, boolean var5) {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         this.var14310.on23(var5 ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         var1.getMatrices().scale(this.var14310.CancellableEvent(), this.var14310.CancellableEvent());
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         Identifier identifier = this.val196.map38.get(this.string41);
         if (identifier == null && ClientProvider.minecraftClient3.getNetworkHandler() != null) {
            PlayerListEntry playerlistentry = ClientProvider.minecraftClient3
               .getNetworkHandler()
               .getPlayerList()
               .stream()
               .filter(var1x -> var1x.getProfile() != null && this.string41.equals(var1x.getProfile().name()))
               .findFirst()
               .orElse(null);
            if (playerlistentry != null && playerlistentry.getSkinTextures() != null) {
               identifier = playerlistentry.getSkinTextures().body().texturePath();
               this.val196.map38.put(this.string41, identifier);
            }
         }

         if (identifier == null) {
            identifier = DefaultSkinHelper.getSteve().body().texturePath();
         }

         float f5 = 6.0F;
         float f = var2 + 8.0F;
         float f1 = var3 + (this.getHeight() - f5) / 2.0F;
         var1.drawPlayerHeadWithRoundedShader(identifier, f, f1, f5, CornerRadius.MovementInputEvent(1.6F), ArgbColor.var11934);
         var1.drawText(
            font,
            this.text5,
            var2 + 8.0F + f5 + GuiStyle.PADDING.intValue(),
            var3 + (this.getHeight() - font.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor().call001()
         );
         String s = this.val196.ItemRegistry(System.currentTimeMillis() - this.long102);
         float f2 = font1.width(s);
         float f3 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f2);
         float f4 = var2 + var4 - f3 - GuiStyle.PADDING * 2;
         var1.drawRoundedRect(f4, var3, f3, this.getHeight(), CornerRadius.MovementInputEvent(1.0F), zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font1, s, f4 + (f3 - f2) / 2.0F, var3 + (this.getHeight() - font1.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
         var1.popMatrix();
      }

      public boolean float206() {
         return this.var14310.CancellableEvent() == 0.0F;
      }
   }

   public static class MediaSource {
      public Text text4;
      public String name;
      public boolean boolean110;
      public MediaType var140Var165;

      public Text int440() {
         return this.text4;
      }

      public String getName() {
         return this.name;
      }

      public boolean int441() {
         return this.boolean110;
      }

      public MediaType int442() {
         return this.var140Var165;
      }

      public void Easing(Text var1) {
         this.text4 = var1;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public void AnalyticsTracker(boolean var1) {
         this.boolean110 = var1;
      }

      public void on23(MediaType var1) {
         this.var140Var165 = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof MediaSource l11i1l1l11l111iil1_ii1il11l111ii11iil)) {
            return false;
         } else {
            if (!l11i1l1l11l111iil1_ii1il11l111ii11iil.canEqual(this)) {
               return false;
            }

            if (this.int441() != l11i1l1l11l111iil1_ii1il11l111ii11iil.int441()) {
               return false;
            }

            Text text = this.int440();
            Text text1 = l11i1l1l11l111iil1_ii1il11l111ii11iil.int440();
            if (text == null ? text1 == null : text.equals(text1)) {
               String s = this.getName();
               String s1 = l11i1l1l11l111iil1_ii1il11l111ii11iil.getName();
               if (s == null ? s1 == null : s.equals(s1)) {
                  MediaType l11i1l1l11l111iil1_illi1l1l1x = this.int442();
                  l11i1l1l11l111iil1_illi1l1l1x = l11i1l1l11l111iil1_ii1il11l111ii11iil.int442();
                  return l11i1l1l11l111iil1_illi1l1l1x == null
                     ? l11i1l1l11l111iil1_illi1l1l1x == null
                     : l11i1l1l11l111iil1_illi1l1l1x.equals(l11i1l1l11l111iil1_illi1l1l1x);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof MediaSource;
      }

      @Override
      public int hashCode() {
         byte b0 = 59;
         int i = 1;
         i = i * 59 + (this.int441() ? 79 : 97);
         Text text = this.int440();
         i = i * 59 + (text == null ? 43 : text.hashCode());
         String s = this.getName();
         i = i * 59 + (s == null ? 43 : s.hashCode());
         MediaType l11i1l1l11l111iil1_illi1l1l1 = this.int442();
         return i * 59 + (l11i1l1l11l111iil1_illi1l1l1 == null ? 43 : l11i1l1l11l111iil1_illi1l1l1.hashCode());
      }

      @Override
      public String toString() {
         return "StaffComponent.Staff(prefix=" + this.int440() + ", name=" + this.getName() + ", isSpec=" + this.int441() + ", status=" + this.int442() + ")";
      }

      public MediaSource(Text var1, String var2, boolean var3, MediaType var4) {
         this.text4 = var1;
         this.name = var2;
         this.boolean110 = var3;
         this.var140Var165 = var4;
      }
   }

   public enum MediaType {
      val509,
      val510;
   }
}
