package org.zenith.hud;


import java.util.Objects;
import org.zenith.module.Module;
import org.zenith.util.ScoreboardUtils;
import java.util.LinkedHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class KeybindsHud extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public LinkedHashSet<KeybindsHud.StyledTextValue> linkedHashSet = new LinkedHashSet<>();
   public UiAnimation var14317 = new UiAnimation(200L, 100.0F, Easing.StopUsingItemEvent);
   public UiAnimation var14318 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public UiAnimation var14319 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);

   public KeybindsHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void on23(CustomDrawContext var1) {
      Font font = Fonts.NEW_ICONS.getFont(5.5F);
      ZenithClient.on23().ColorAnimator().MenuScreenId().forEach(var1x -> {
         if (var1x.getKeyCode() != -1 && this.linkedHashSet.stream().noneMatch(var1xxx -> var1xxx.module2 == var1x)) {
            this.linkedHashSet.addLast(new KeybindsHud.StyledTextValue(this, var1x));
         }
      });
      this.linkedHashSet.removeIf(var0 -> (!var0.module2.isEnabled() || var0.module2.getKeyCode() == -1) && var0.float206());
      if (this.linkedHashSet.isEmpty()) {
         this.var14318.on23(0.0F);
      } else {
         this.var14318.on23(this.linkedHashSet.size() == 1 && this.linkedHashSet.getFirst().var1434.BotDisconnectEvent() == 0.0F ? 0.0F : 1.0F);
      }

      float f = this.x;
      float f1 = this.y;
      float f2 = 1.5F;
      float f3 = (float)(
         17 + GuiStyle.PADDING
            + this.linkedHashSet.stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var1434.CancellableEvent()).sum()
      );
      float f4 = (float)this.linkedHashSet.stream().mapToDouble(KeybindsHud.StyledTextValue::float205).max().orElse(100.0);
      f4 = this.var14317.on23(f4);
      this.width = f4;
      this.height = f3;
      this.var14319.on23(minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud() || !this.linkedHashSet.isEmpty());
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      var1.pushMatrix();
      float f5 = Interface.float212();
      CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f5);
      var1.getMatrices().translate(f + f4 / 2.0F, f1 + f3 / 2.0F);
      var1.getMatrices().scale(this.var14319.CancellableEvent(), this.var14319.CancellableEvent());
      var1.getMatrices().translate(-(f + f4 / 2.0F), -(f1 + f3 / 2.0F));
      var1.drawBlurHud(f, f1, f4, f3, 21.0F, ii1il11l111ii11iil, ArgbColor.var11934);
      var1.drawRoundedRect(f, f1, f4, f3, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
      var1.drawRoundedRect(f, f1, f4, 17.0F, ii1il11l111ii11iil, zenithstyle.getHeaderHudBackground().getColor());
      var1.drawText(font, "n", f + 8.0F, f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
      var1.drawText(font, "m", f + f4 - 8.0F - font.width("M"), f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      var1.drawText(
         font1,
         "Keybinds",
         f + 8.0F + font.width("n") + GuiStyle.PADDING.intValue(),
         f1 + (17.0F - font1.height()) / 2.0F,
         zenithstyle.getTextEnable().getColor()
      );
      if (this.var14319.CancellableEvent() == 1.0F) {
         float f6 = f1 + 17.0F + GuiStyle.PADDING.intValue();
         int i = 0;
         var1.enableScissor((int)f, (int)f1, (int)(f + f4), (int)(f1 + f3));

         for (KeybindsHud.StyledTextValue iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil : this.linkedHashSet) {
            iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil.on23(var1, f, f6, f4, i, f5);
            f6 += (iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil.getHeight() + GuiStyle.PADDING.intValue())
               * iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil.var1434.CancellableEvent();
            i++;
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }


   public static class StyledTextValue {
      public final UiAnimation var1434 = new UiAnimation(150L, Easing.StopUsingItemEvent);
      public final Module module2;
      public float width;

      public StyledTextValue(KeybindsHud var1, Module var2) {
         this.module2 = var2;
      }

      public float float205() {
         float f = 100.0F;
         float f1 = Fonts.NEW_MEDIUM.getWidth(this.module2.getName(), 5.4F);
         Font font = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String s = ScoreboardUtils.EventPosHook(this.module2.getKeyCode());
         float f2 = font.width(s);
         float f3 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f2);
         float f4 = 16.0F + f3;
         float f5 = f - (f4 + 8.0F);
         if (f5 < 8.0F + f1 + 8.0F) {
            float f6 = f1 + 8.0F + 8.0F - f5;
            f += f6;
         }

         return f;
      }

      public float getHeight() {
         return 7.0F;
      }

      public void on23(CustomDrawContext var1, float var2, float var3, float var4, int var5, float var6) {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.NEW_ICONS.getFont(5.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.4F);
         this.var1434.on23(this.module2.isEnabled() && this.module2.getKeyCode() != -1 ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         float f = this.var1434.CancellableEvent();
         var1.getMatrices().scale(f, f);
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         Font font2 = Fonts.NEW_SEMIBOLD.getFont(5.4F);
         String s = ScoreboardUtils.EventPosHook(this.module2.getKeyCode());
         float f1 = font2.width(s);
         float f2 = Math.max(this.getHeight(), GuiStyle.PADDING.intValue() + f1);
         var1.drawText(
            font, this.module2.getCategory().getIcon(), var2 + 8.0F, var3 + (this.getHeight() - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor()
         );
         var1.drawText(
            font1,
            this.module2.getName(),
            var2 + 8.0F + font.width(this.module2.getCategory().getIcon()) + GuiStyle.PADDING.intValue(),
            var3 + (this.getHeight() - font1.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor()
         );
         float f3 = var2 + var4 - f2 - GuiStyle.PADDING * 2;
         var1.drawRoundedRect(f3, var3, f2, this.getHeight(), CornerRadius.MovementInputEvent(1.0F), zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font2, s, f3 + (f2 - f1) / 2.0F, var3 + (this.getHeight() - font1.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
         var1.popMatrix();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            StyledTextValue iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil = (StyledTextValue)var1;
            return Objects.equals(this.module2.getId(), iiiliilli1i1li111i11lil1liil_ii1il11l111ii11iil.module2.getId());
         } else {
            return false;
         }
      }

      public boolean float206() {
         return this.var1434.CancellableEvent() == 0.0F;
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.module2.getId());
      }

      public int Easing(StyledTextValue var1) {
         return this.module2.getName().compareTo(var1.module2.getName());
      }
   }
}
