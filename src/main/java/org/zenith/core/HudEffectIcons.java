package org.zenith.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudEffectIcons extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float243 = 17.0F;
   public static final float float244 = 7.0F;
   public final UiAnimation var14331 = new UiAnimation(200L, 100.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14332 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14333 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14334 = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public final UiAnimation var14335 = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public final Map<String, HudEffectIconRenderer> map46 = new LinkedHashMap<>();
   public final Set<String> set16 = new HashSet<>();

   @Override
   public void on23(CustomDrawContext var1) {
      if (minecraftClient3.player != null) {
         this.set16.clear();
         List<StatusEffectInstance> arraylist = new ArrayList<>(minecraftClient3.player.getActiveStatusEffects().values());
         int i = 0;
         int j = 0;

         for (StatusEffectInstance statuseffectinstance : arraylist) {
            String s = this.Easing(statuseffectinstance);
            boolean flag = this.on23(statuseffectinstance);
            this.set16.add(s);
            if (flag) {
               i++;
            } else {
               j++;
            }

            if (!this.map46.containsKey(s)) {
               this.map46.put(s, new HudEffectIconRenderer(this, statuseffectinstance, flag));
            }
         }

         this.map46.values().removeIf(HudEffectIconRenderer::float206);
         if (this.map46.isEmpty()) {
            this.var14332.on23(0.0F);
         } else {
            HudEffectIconRenderer il1ll1il1i1ilii1_ii1il11l111ii11iil1 = this.map46.values().iterator().next();
            this.var14332.on23(this.map46.size() == 1 && il1ll1il1i1ilii1_ii1il11l111ii11iil1.var1437.BotDisconnectEvent() == 0.0F ? 0.0F : 1.0F);
         }

         this.var14334.on23(i > 0 ? 1.0F : 0.0F);
         this.var14335.on23(j > 0 ? 1.0F : 0.0F);
         List<HudEffectIconRenderer> list = this.map46.values().stream().filter(HudEffectIconRenderer::int438).toList();
         List<HudEffectIconRenderer> list1 = this.map46.values().stream().filter(var0 -> !var0.int438()).toList();
         Font font = Fonts.NEW_ICONS.getFont(5.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         float f = this.x;
         float f1 = this.y;
         float f2 = (float)list.stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var1437.CancellableEvent()).sum();
         float f3 = (float)list1.stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var1437.CancellableEvent()).sum();
         float f4 = 17.0F + GuiStyle.PADDING.intValue();
         f4 += (5 + GuiStyle.PADDING) * this.var14334.CancellableEvent();
         f4 += f2;
         f4 += (5 + GuiStyle.PADDING) * this.var14335.CancellableEvent();
         f4 += f3;
         float f5 = (float)this.map46.values().stream().mapToDouble(HudEffectIconRenderer::float205).max().orElse(100.0);
         float f6 = this.UiAnimation("Positive", i);
         float f7 = this.UiAnimation("Negative", j);
         float f8 = Math.max(f5, Math.max(f6, f7));
         f8 = this.var14331.on23(f8);
         this.width = f8;
         this.height = f4;
         this.var14333.on23(minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud() || !this.map46.isEmpty());
         float f9 = Interface.float212();
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f9);
         var1.pushMatrix();
         var1.getMatrices().translate(f + f8 / 2.0F, f1 + f4 / 2.0F);
         var1.getMatrices().scale(this.var14333.CancellableEvent(), this.var14333.CancellableEvent());
         var1.getMatrices().translate(-(f + f8 / 2.0F), -(f1 + f4 / 2.0F));
         var1.drawBlurHud(f, f1, f8, f4, 21.0F, ii1il11l111ii11iil, ArgbColor.var11934);
         var1.drawRoundedRect(f, f1, f8, f4, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
         var1.drawRoundedRect(f, f1, f8, 17.0F, ii1il11l111ii11iil, zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font, "o", f + 8.0F, f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
         var1.drawText(font, "m", f + f8 - 8.0F - font.width("m"), f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
         var1.drawText(
            font1,
            "Potions",
            f + 8.0F + font.width("0") + GuiStyle.PADDING.intValue(),
            f1 + (17.0F - font1.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor()
         );
         if (this.var14333.CancellableEvent() == 1.0F) {
            float f10 = f1 + 17.0F + GuiStyle.PADDING.intValue();
            var1.enableScissor((int)f, (int)f1, (int)(f + f8), (int)(f1 + f4));
            f10 = this.on23(var1, f, f10, f8, "Positive", i, this.var14334, true, zenithstyle);

            for (HudEffectIconRenderer il1ll1il1i1ilii1_ii1il11l111ii11iil : list) {
               il1ll1il1i1ilii1_ii1il11l111ii11iil.on23(var1, f, f10, f8);
               f10 += (il1ll1il1i1ilii1_ii1il11l111ii11iil.getHeight() + GuiStyle.PADDING.intValue()) * il1ll1il1i1ilii1_ii1il11l111ii11iil.var1437.CancellableEvent();
            }

            f10 = this.on23(var1, f, f10, f8, "Negative", j, this.var14335, false, zenithstyle);

            for (HudEffectIconRenderer il1ll1il1i1ilii1_ii1il11l111ii11iil2 : list1) {
               il1ll1il1i1ilii1_ii1il11l111ii11iil2.on23(var1, f, f10, f8);
               f10 += (il1ll1il1i1ilii1_ii1il11l111ii11iil2.getHeight() + GuiStyle.PADDING.intValue()) * il1ll1il1i1ilii1_ii1il11l111ii11iil2.var1437.CancellableEvent();
            }

            var1.disableScissor();
         }

         var1.popMatrix();
      }
   }

   public HudEffectIcons(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public float on23(CustomDrawContext var1, float var2, float var3, float var4, String var5, int var6, UiAnimation var7, boolean var8, ZenithStyle var9) {
      float f = var7.CancellableEvent();
      if (f <= 0.0F) {
         return var3;
      }

      Font font = Fonts.NEW_REGULAR.getFont(5.4F);
      Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
      String s = String.valueOf(var6);
      float f1 = font1.width(s);
      float f2 = Math.max(7.0F, GuiStyle.PADDING.intValue() + f1);
      var1.pushMatrix();
      var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + 3.5F);
      var1.getMatrices().scale(f, f);
      var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + 3.5F));
      ArgbColor i11ii1llliilllii1i1 = var9.getTextTertiary().getColor();
      var1.drawText(font, var5, var2 + 8.0F, var3 + (7.0F - font.height()) / 2.0F, i11ii1llliilllii1i1);
      float f3 = var2 + var4 - f2 - GuiStyle.PADDING * 2;
      var1.drawText(font1, s, f3 + (f2 - f1) / 2.0F, var3 + (7.0F - font1.height()) / 2.0F, i11ii1llliilllii1i1);
      var1.popMatrix();
      return var3 + (5 + GuiStyle.PADDING) * f;
   }

   public float UiAnimation(String var1, int var2) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
      String s = String.valueOf(Math.max(var2, 0));
      float f = font1.width(s);
      float f1 = Math.max(7.0F, GuiStyle.PADDING.intValue() + f);
      float f2 = 8.0F + font.width(var1) + GuiStyle.PADDING.intValue();
      float f3 = GuiStyle.PADDING * 2 + f1 + 8.0F;
      return Math.max(100.0F, f2 + f3 + 8.0F);
   }

   public boolean on23(StatusEffectInstance var1) {
      return ((StatusEffect)var1.getEffectType().value()).getCategory().equals(StatusEffectCategory.BENEFICIAL);
   }

   public String UiAnimation(StatusEffectInstance var1) {
      String s = I18n.translate(((StatusEffect)var1.getEffectType().value()).getTranslationKey(), new Object[0]);
      String s1 = this.CloudRouter(var1.getAmplifier());
      return s + " " + s1;
   }

   public String Easing(StatusEffectInstance var1) {
      return ((StatusEffect)var1.getEffectType().value()).getTranslationKey() + var1.getAmplifier();
   }

   public String CloudRouter(int var1) {
      return String.valueOf(var1 + 1);
   }

   public String ProtocolMessage(int var1) {
      int i = var1 / 20;
      int j = i / 60;
      int k = i % 60;
      return String.format("%02d:%02d", j, k);
   }
}
