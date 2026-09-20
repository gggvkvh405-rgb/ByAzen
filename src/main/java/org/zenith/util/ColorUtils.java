package org.zenith.util;

import java.awt.Color;
import java.util.regex.Pattern;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;

public final class ColorUtils {
   public static final Pattern pattern10 = Pattern.compile("(?i)§[0-9A-FK-ORX]");
   public static final int int390 = -1;

   public static int Event18Ext3(int var0) {
      return var0 >> 16 & 0xFF;
   }

   public static int EventRenderScreenHook(int var0) {
      return var0 >> 8 & 0xFF;
   }

   public static int GameMessageEvent(int var0) {
      return var0 & 0xFF;
   }

   public static int PacketEvent(int var0) {
      return var0 >> 24 & 0xFF;
   }

   public static float PacketReceiveEvent(int var0) {
      return Event18Ext3(var0) / 255.0F;
   }

   public static float PacketSendEvent(int var0) {
      return EventRenderScreenHook(var0) / 255.0F;
   }

   public static float VisualSettingsStore(int var0) {
      return GameMessageEvent(var0) / 255.0F;
   }

   public static float Item(int var0) {
      return PacketEvent(var0) / 255.0F;
   }

   public static int[] FriendStore(int var0) {
      return new int[]{Event18Ext3(var0), EventRenderScreenHook(var0), GameMessageEvent(var0), PacketEvent(var0)};
   }

   public static int[] MacroManager(int var0) {
      return new int[]{Event18Ext3(var0), EventRenderScreenHook(var0), GameMessageEvent(var0)};
   }

   public static float[] UsageStatStore(int var0) {
      return new float[]{PacketReceiveEvent(var0), PacketSendEvent(var0), VisualSettingsStore(var0), Item(var0)};
   }

   public static float[] StaffList(int var0) {
      return new float[]{PacketReceiveEvent(var0), PacketSendEvent(var0), VisualSettingsStore(var0)};
   }

   public static boolean HudInfoBoxSecondary(String var0) {
      return var0 != null && var0.matches("(?i)^[a-f0-9]{6}$");
   }

   public static ArgbColor on23(String var0, ArgbColor var1) {
      if (!HudInfoBoxSecondary(var0)) {
         return var1;
      }

      int i = Integer.parseInt(var0, 16);
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int l = i & 0xFF;
      return new ArgbColor(new Color(j, k, l));
   }

   public static String EmoteManager(ArgbColor var0) {
      int i = var0.call001();
      return String.format("%06X", i & 16777215);
   }

   public static ArgbColor on23(int var0, int var1, ArgbColor var2, ArgbColor var3) {
      int i = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      i = (i >= 180 ? 360 - i : i) * 2;
      return UiAnimation(var2, var3, i / 360.0F);
   }

   public static ArgbColor on23(int var0, int var1, ArgbColor... var2) {
      int i = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      i = (i > 180 ? 360 - i : i) + 180;
      int j = (int)(i / 360.0F * var2.length);
      if (j == var2.length) {
         j--;
      }

      ArgbColor i11ii1llliilllii1i1 = var2[j];
      ArgbColor i11ii1llliilllii1i11 = var2[j == var2.length - 1 ? 0 : j + 1];
      return UiAnimation(i11ii1llliilllii1i1, i11ii1llliilllii1i11, i / 360.0F * var2.length - j);
   }

   public static ArgbColor UiAnimation(ArgbColor var0, ArgbColor var1, float var2) {
      return var0.Easing(var1, var2);
   }

   public static String HudSelectedItemPanel(String var0) {
      return var0 != null && !var0.isEmpty() ? pattern10.matcher(var0).replaceAll("") : null;
   }

   public static int ColorAnimator(int var0, float var1) {
      return on23(Event18Ext3(var0), EventRenderScreenHook(var0), GameMessageEvent(var0), Math.round(PacketEvent(var0) * var1));
   }

   public static int on23(int var0, int var1, int var2, int var3) {
      return MathHelper.clamp(var3, 0, 255) << 24
         | MathHelper.clamp(var0, 0, 255) << 16
         | MathHelper.clamp(var1, 0, 255) << 8
         | MathHelper.clamp(var2, 0, 255);
   }

   public static int ServerConfigStore(int var0) {
      return ZenithClient.on23().TextScanner().getClientColor(var0).call001();
   }

   public ColorUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
