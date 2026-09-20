package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.event.EventMouseButton;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.combat.AimAssist;
import org.zenith.module.combat.Aura;
import org.zenith.module.render.Interface;
import org.zenith.module.misc.NameProtect;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudDrawContext;

public class HudTargetPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation var14355 = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation var14356 = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation var14357 = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation var14358 = new UiAnimation(150L, Easing.EventWindowSizeChanged);
   public LivingEntity livingEntity;
   public CornerRadiusF bounds;
   public String string103 = "";

   public void on23(HudDrawContext var1, LivingEntity var2, float var3) {
      float f = this.x;
      float f1 = this.y;
      float f2 = 109.0F;
      float f3 = 25.0F;
      float f4 = 17.0F;
      float f5 = GuiStyle.PADDING.intValue();
      float f6 = 7.5F;
      float f7 = Interface.float212();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      ArgbColor i11ii1llliilllii1i1 = zenithstyle.getHeaderHudBackground().getColor();
      ArgbColor i11ii1llliilllii1i11 = zenithstyle.getHudBackground().getColor();
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(f + f2 / 2.0F, f1 + f3 / 2.0F);
      var1.getMatrices().scale(var3, var3);
      var1.getMatrices().translate(-(f + f2 / 2.0F), -(f1 + f3 / 2.0F));
      var1.drawBlurHud(this.x, this.y, f2, f3, 21.0F, CornerRadius.MovementInputEvent(f7), ArgbColor.var11934);
      var1.drawRoundedRect(f, f1, f2, f3, CornerRadius.MovementInputEvent(f7), i11ii1llliilllii1i11);
      var1.drawRoundedRect(f, f1, f4 + f5 * 2.0F, f3, CornerRadius.MovementInputEvent(f7), i11ii1llliilllii1i1);
      float f8 = Math.min(999.0F, EffectEngine.SimpleItemBuilder(var2));
      float f9 = Math.max(1.0F, var2.getMaxHealth());
      float f10 = MathHelper.clamp(f8 / f9, 0.0F, 1.0F);
      float f11 = MathHelper.clamp(this.var14355.on23(f10), 0.0F, 1.0F);
      float f12 = Math.max(0.0F, f8 - var2.getMaxHealth());
      float f13 = this.var14356.on23(f12 / f9);
      float f14 = f + f5;
      float f15 = f1 + (f3 - f4) / 2.0F;
      if (var2 instanceof PlayerEntity playerentity) {
         float f16 = MathHelper.clamp(var2.hurtTime / 10.0F, 0.0F, 1.0F);
         ArgbColor i11ii1llliilllii1i12 = ArgbColor.var11934.Easing(ArgbColor.var11937, f16);
         var1.drawPlayerHeadWithRoundedShader(
            ((AbstractClientPlayerEntity)playerentity).getSkin().body().texturePath(),
            f14,
            f15,
            f4,
            CornerRadius.MovementInputEvent(Interface.float212() / 2.0F),
            i11ii1llliilllii1i12
         );
      } else {
         Font font2 = Fonts.MEDIUM.getFont(12.0F);
         var1.drawText(font2, "?", f14 + (f4 - font2.width("?")) / 2.0F, f15 + f4 / 2.0F - font2.height() / 2.0F, ArgbColor.var11934);
      }

      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      String s2 = NameProtect.ItemStackStore(var2.getName().getString());
      float f19 = f2 - (f4 + f5 * 2.0F + 50.0F) - 30.0F;
      String s = s2;
      Vector2f vector2f = this.int213();
      this.bounds = new CornerRadiusF(f14 + f4 + f5 * 2.0F, f15 - 3.0F, f19, 6.0F);
      if (this.bounds.PotionItemBuilder(vector2f.x(), vector2f.y())) {
         s = "Скопировать";
      } else {
         this.bounds = null;
      }

      var1.drawText(font1, s, f14 + f4 + f5 * 2.0F, f15 + f5 / 2.0F, zenithstyle.getTextEnable().getColor().call001(), true, 0.4F, 1.0F, 50.0F);
      String s1 = Math.round(f8) + "";
      if (var2 instanceof PlayerEntity playerentity1) {
         this.on23(var1, playerentity1, f14 + f4 + f5 * 2.0F, f15 + f4 - 6.0F - f5 / 2.0F, f + f2 - (f14 + f4 + f5 * 2.0F), f5, f6);
      }

      float f20 = 17.0F;
      float f17 = this.x + f2 - f5 - f20;
      float f18 = this.y + (f3 - f20) / 2.0F;
      var1.drawArcBorder(f17, f18, f20, f20, 1.0F, 360.0F, 0.5F, zenithstyle.getFieldBorder().getColor());
      var1.drawArcBorder(f17, f18, f20, f20, 1.0F, 360.0F * f11, 0.5F, zenithstyle.getPrimaryColor().getColor());
      if (f13 != 0.0F) {
         var1.drawArcBorder(f17, f18, f20, f20, 1.0F, 360.0F * f13, 0.5F, ArgbColor.var11939);
      }

      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      var1.drawText(font, s1, f17 + (f20 - font.width(s1)) / 2.0F, f18 + (f20 - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor());
      var1.getMatrices().popMatrix();
   }

   public void on23(CustomDrawContext var1, PlayerEntity var2, float var3, float var4, float var5, float var6, float var7) {
      float f = 7.0F;
      float f1 = 0.5F;
      float f2 = var3;
      float f3 = var4 + 0.5F;
      Font font = Fonts.ICONS.getFont(4.5F);
      ItemStack[] aitemstack = new ItemStack[]{
         var2.getMainHandStack(),
         var2.getOffHandStack(),
         var2.getInventory().getStack(39),
         var2.getInventory().getStack(38),
         var2.getInventory().getStack(37),
         var2.getInventory().getStack(36)
      };

      for (ItemStack itemstack : aitemstack) {
         if (!itemstack.isEmpty()) {
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(f2 + (f - 7.0F) / 2.0F, f3 + (f - 7.0F) / 2.0F);
            var1.getMatrices().scale(0.4375F, 0.4375F);
            var1.drawItem(itemstack, 0, 0);
            var1.drawItemBar(itemstack, 0, 0);
            var1.drawCooldownProgress(itemstack, 0, 0);
            var1.getMatrices().popMatrix();
         } else {
            var1.drawText(
               font,
               "M",
               f2 + (f - font.width("X")) / 2.0F,
               f3 + (f - font.height()) / 2.0F,
               ZenithClient.on23().TextScanner().getCurrentStyle().getTextTertiary().getColor()
            );
         }

         f2 += f + f1;
      }
   }

   public HudTargetPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public boolean on23(EventMouseButton var1) {
      Vector2f vector2f = this.int213();
      if (this.bounds != null && this.bounds.PotionItemBuilder(vector2f.x(), vector2f.y()) && minecraftClient3.player != null) {
         minecraftClient3.keyboard.setClipboard(minecraftClient3.player.getGameProfile().name());
         return true;
      } else {
         return super.on23(var1);
      }
   }

   @Override
   public void on23(CustomDrawContext var1) {
      this.width = 109.0F;
      this.height = 25.0F;
      Aura lli1i11i1i1l11il1i111i1llliii1 = Aura.aura;
      Object object = !(minecraftClient3.currentScreen instanceof ChatScreen) && !ZenithClient.on23().NbtEditor().isRenderHud()
         ? (lli1i11i1i1l11il1i111i1llliii1.zClass054() == null ? AimAssist.aimAssist.zClass054() : lli1i11i1i1l11il1i111i1llliii1.zClass054())
         : minecraftClient3.player;
      this.on23((LivingEntity)object);
      if (this.var14357.CancellableEvent() != 0.0F && this.livingEntity != null) {
         String s = this.livingEntity.getName().getString();
         this.on23((HudDrawContext)var1, this.livingEntity, this.var14357.CancellableEvent());
      }
   }

   public void on23(LivingEntity var1) {
      if (var1 == null) {
         this.var14357.on23(0.0F);
         if (this.var14357.CancellableEvent() == 0.0F) {
            this.livingEntity = null;
         }
      } else if (var1 != this.livingEntity) {
         this.var14357.on23(0.0F);
         if (this.var14357.CancellableEvent() == 0.0F) {
            this.livingEntity = var1;
         }
      } else {
         this.var14357.on23(1.0F);
      }
   }
}
