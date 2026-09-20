package org.zenith.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.FriendSkinResolver;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudHotbarPanel extends HudElement {
   public static final float float256 = 91.0F;
   public static final float float257 = 21.0F;
   public static final float float258 = 85.0F;
   public static final float float259 = 7.0F;
   public static final float float260 = 1.0F;
   public static final float float261 = 6.0F;
   public static final float float262 = 24.0F;
   public static final float float263 = 20.0F;
   public final UiAnimation var14348 = new UiAnimation(250L, Easing.EventWindowSizeChanged);
   public final Map<String, HudHotbarAnim> map49 = new HashMap<>();

   public HudHotbarPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.width = 91.0F;
      this.height = 85.0F;
   }

   @Override
   public void on23(CustomDrawContext var1) {
      List<CloudUserProfile> list = this.float203();
      this.width = 91.0F;
      this.height = list.isEmpty() ? 85.0F : list.size() * 85.0F + Math.max(0, list.size() - 1) * 24.0F;
      if (list.isEmpty()) {
         this.var14348.on23(0.0F);
         if (this.var14348.CancellableEvent() <= 0.01F) {
            return;
         }
      } else {
         this.var14348.on23(1.0F);
      }

      float f = this.var14348.CancellableEvent();
      if (!(f <= 0.01F)) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
         var1.getMatrices().scale(f, f);
         var1.getMatrices().translate(-(this.x + this.width / 2.0F), -(this.y + this.height / 2.0F));
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         float f1 = Interface.float212();
         float f2 = this.y;

         for (CloudUserProfile li1ilil1i11ii111l11l : list) {
            this.on23(var1, zenithstyle, f1, li1ilil1i11ii111l11l, f2);
            f2 += 109.0F;
         }

         var1.getMatrices().popMatrix();
      }
   }

   public void on23(CustomDrawContext var1, ZenithStyle var2, float var3, CloudUserProfile var4, float var5) {
      HudHotbarAnim iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil = this.map49.computeIfAbsent(var4.id(), var0 -> new HudHotbarAnim());
      InventoryUtils l11illi1i11 = var4.TargetAcquireEvent();
      int i = l11illi1i11 != null ? l11illi1i11.PacketEvent() : 0;
      if (i != iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil.SoundManager) {
         iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil.call103.on23(0.0F);
         iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil.SoundManager = i;
      }

      iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil.call103.on23(1.0F);
      var1.drawBlurHud(this.x, var5, this.width, 85.0F, 21.0F, CornerRadius.MovementInputEvent(var3), ArgbColor.var11934);
      var1.drawRoundedRect(this.x, var5, this.width, 85.0F, CornerRadius.MovementInputEvent(var3), var2.getHudBackground().getColor());
      var1.drawRoundedRect(this.x, var5, this.width, 21.0F, CornerRadius.MovementInputEvent(var3), var2.getHeaderHudBackground().getColor());
      this.on23(var1, var2, var4, var5, iii1lii1li1llilllil11li1i1i1l1_ii1il11l111ii11iil);
      this.on23(var1, var2, l11illi1i11, var5);
      this.UiAnimation(var1, var2, l11illi1i11, var5);
   }

   public void on23(CustomDrawContext var1, ZenithStyle var2, CloudUserProfile var3, float var4, HudHotbarAnim var5) {
      float f = 5.5F;
      float f1 = 1.5F;
      float f2 = GuiStyle.PADDING.intValue();
      float f3 = f + f2 + f1;
      float f4 = var4 + (21.0F - f3) / 2.0F;
      float f5 = this.x + 6.0F;
      String s = !var3.username().isBlank() ? var3.username() : var3.id();
      Identifier identifier = FriendSkinResolver.resolveSkin(s);
      var1.drawPlayerHeadWithRoundedShader(identifier, f5, f4 + 0.5F, f, CornerRadius.MovementInputEvent(1.0F), ArgbColor.var11934);
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f6 = f5 + f + 3.0F;
      float f7 = f4 + (f - font.height()) / 2.0F;
      var1.drawText(font, s, f6, f7, var2.getTextEnable().getColor());
      float f8 = this.UiAnimation(var3);
      float f9 = MathHelper.clamp(f8 / 20.0F, 0.0F, 1.0F);
      float f10 = MathHelper.clamp(var5.call050.on23(f9), 0.0F, 1.0F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      String s1 = (int)Math.ceil(f8) + "hp";
      float f11 = font1.width(s1);
      var1.drawText(font1, s1, this.x + this.width - 6.0F - f11, f7, var2.getPrimaryColor().getColor());
      float f12 = f4 + f + f2;
      float f13 = this.width - 12.0F;
      var1.drawRoundedRect(f5, f12, f13, f1, CornerRadius.MovementInputEvent(0.1F), var2.getFieldBorder().getColor());
      if (f10 > 0.0F) {
         var1.drawRoundedRect(f5, f12, f13 * f10, f1, CornerRadius.MovementInputEvent(0.1F), var2.getPrimaryColor().getColor());
      }
   }

   public void on23(CustomDrawContext var1, ZenithStyle var2, InventoryUtils var3, float var4) {
      float f = var4 + 21.0F + 6.0F;
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      int i = var3 != null ? var3.Event18Ext3() : 0;
      var1.drawText(font, "Inventory", this.x + 6.0F, f, var2.getTextTertiary().getColor());
      String s = String.valueOf(i);
      var1.drawText(font, s, this.x + this.width - 6.0F - font.width(s), f, var2.getTextTertiary().getColor());
      float f1 = f + font.height() + 3.0F;
      this.on23(var1, var2, var3, f1, 3, true);
   }

   public void UiAnimation(CustomDrawContext var1, ZenithStyle var2, InventoryUtils var3, float var4) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = 25.0F;
      float f1 = var4 + 21.0F + 6.0F + font.height() + 3.0F;
      float f2 = f1 + f + 4.0F;
      int i = var3 != null ? var3.EventRenderScreenHook() : 0;
      var1.drawText(font, "Hotbar", this.x + 6.0F, f2, var2.getTextTertiary().getColor());
      String s = String.valueOf(i);
      var1.drawText(font, s, this.x + this.width - 6.0F - font.width(s), f2, var2.getTextTertiary().getColor());
      float f3 = f2 + font.height() + 3.0F;
      this.on23(var1, var2, var3, f3, 1, false);
   }

   public void on23(CustomDrawContext var1, ZenithStyle var2, InventoryUtils var3, float var4, int var5, boolean var6) {
      byte b0 = 9;
      float f = b0 * 7.0F + (b0 - 1) * 2.0F;
      float f1 = this.x + (this.width - f) / 2.0F;
      Font font = Fonts.ICONS.getFont(4.5F);
      ArgbColor i11ii1llliilllii1i1 = var2.getTextTertiary().getColor();

      for (int i = 0; i < var5; i++) {
         for (int j = 0; j < b0; j++) {
            float f2 = f1 + j * 9.0F;
            float f3 = var4 + i * 9.0F;
            ItemStack itemstack = this.on23(var3, var6, i, j);
            if (itemstack != null && !itemstack.isEmpty()) {
               float f4 = 0.4375F;
               var1.pushMatrix();
               var1.getMatrices().translate(f2, f3);
               var1.getMatrices().scale(f4, f4);
               var1.drawItem(itemstack, 0, 0);
               var1.drawItemBar(itemstack, 0, 0);
               var1.drawCooldownProgress(itemstack, 0, 0);
               var1.popMatrix();
            } else {
               var1.drawText(font, "M", f2 + (7.0F - font.width("M")) / 2.0F, f3 + (7.0F - font.height()) / 2.0F, i11ii1llliilllii1i1);
            }
         }
      }
   }

   public ItemStack on23(InventoryUtils var1, boolean var2, int var3, int var4) {
      if (var1 == null) {
         return ItemStack.EMPTY;
      } else {
         return var2 ? var1.Easing(var3 * 9 + var4) : var1.UiAnimation(var4);
      }
   }

   public float UiAnimation(CloudUserProfile var1) {
      BotFeatureRegistry ili1ll11li1ili11l1i1l11l1 = var1.RotationUpdateStartEvent();
      return ili1ll11li1ili11l1i1l11l1 != null ? Math.max(0.0F, ili1ll11li1ili11l1i1l11l1.Item()) : 0.0F;
   }

   public List<CloudUserProfile> float203() {
      List<CloudUserProfile> list = ZenithClient.on23().MediaTrackInfo().ShaderHand();
      List<CloudUserProfile> arraylist = new ArrayList<>();
      if (list == null) {
         return arraylist;
      }

      for (CloudUserProfile li1ilil1i11ii111l11l : list) {
         if (li1ilil1i11ii111l11l.FovEvent().isEnabled() && li1ilil1i11ii111l11l.TargetAcquireEvent() != null && li1ilil1i11ii111l11l.EventGetBasicProjectionMatrixHook()) {
            arraylist.add(li1ilil1i11ii111l11l);
         }
      }

      return arraylist;
   }
}
