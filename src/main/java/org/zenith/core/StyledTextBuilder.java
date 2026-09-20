package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.util.ArgbColor;

public final class StyledTextBuilder {
   public static final Text text14 = on23(ArgbColor.var11938, ArgbColor.var11934, ArgbColor.var11940);
   public static final String string124 = "\\u00A7.";
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void RefreshCacheEvent(String var0) {
      on23(TextAccent.call002, var0);
   }

   public static void on23(TextAccent var0, String var1) {
      if (minecraftClient3.player != null) {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         MutableText mutabletext = on23(
               zenithstyle.getPrimaryColor().getColor(), zenithstyle.getSecondaryPrimaryColor().getColor(), zenithstyle.getTextSecondary().getColor()
            )
            .append(Text.literal(" "))
            .append(on23(var1, var0, zenithstyle));
         minecraftClient3.player.sendMessage(mutabletext, false);
      }
   }

   public static void RotationLegitStrategy(String var0) {
      on23(TextAccent.call013, var0);
   }

   public static void AimPolicyRotationStrategy(String var0) {
      on23(TextAccent.call417, var0);
   }

   public static MutableText on23(ArgbColor var0, ArgbColor var1, ArgbColor var2) {
      return Text.empty()
         .append(on23("[ ", var2, false))
         .append(on23("Zenith", var0.EventTick(0.12F), var1.EventTick(0.08F), true, 0))
         .append(on23(" ]", var2, false));
   }

   public static MutableText on23(TextAccent var0, ServerTheme var1) {
      ArgbColor i11ii1llliilllii1i1 = var0.getColor().Easing(var1.getColor(), 0.15F).EventTick(0.08F);
      ArgbColor i11ii1llliilllii1i11 = i11ii1llliilllii1i1.Easing(var1.path11(), 0.25F);
      return Text.empty()
         .append(on23("<", var1.map40(), false))
         .append(on23(var0.call271(), i11ii1llliilllii1i1, i11ii1llliilllii1i11, true, 0))
         .append(on23(">", var1.map40(), false));
   }

   public static MutableText on23(String var0, TextAccent var1, ZenithStyle var2) {
      String s = var0 == null ? "" : var0.replaceAll("\\u00A7.", "");
      int i = Math.min(8, s.codePointCount(0, s.length()));
      ArgbColor i11ii1llliilllii1i1 = var1.getColor().Easing(var2.getPrimaryColor().getColor(), 0.3F).EventTick(0.12F);
      ArgbColor i11ii1llliilllii1i11 = var2.getTextEnable().getColor().Easing(var2.getSecondaryPrimaryColor().getColor(), 0.15F);
      return on23(s, i11ii1llliilllii1i1, i11ii1llliilllii1i11, false, i);
   }

   public static MutableText on23(String var0, ArgbColor var1, boolean var2) {
      return Text.literal(var0).setStyle(Style.EMPTY.withColor(var1.call001()).withBold(var2));
   }

   public static MutableText on23(String var0, ArgbColor var1, ArgbColor var2, boolean var3, int var4) {
      MutableText mutabletext = Text.empty();
      if (var0 != null && !var0.isEmpty()) {
         int i = var0.codePointCount(0, var0.length());
         int[] aint = new int[]{0};
         var0.codePoints()
            .forEach(
               var7x -> {
                  float f = i <= 1 ? 0.0F : (float)aint[0] / (i - 1);
                  ArgbColor i11ii1llliilllii1i1 = var1.Easing(var2, f);
                  boolean flag = var3 || aint[0] < var4;
                  mutabletext.append(
                     Text.literal(new String(Character.toChars(var7x)))
                        .setStyle(Style.EMPTY.withColor(i11ii1llliilllii1i1.call001()).withBold(flag))
                  );
                  aint[0]++;
               }
            );
         return mutabletext;
      } else {
         return mutabletext;
      }
   }

   public StyledTextBuilder() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
