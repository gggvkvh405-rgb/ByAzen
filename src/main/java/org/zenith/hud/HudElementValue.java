package org.zenith.hud;


import org.zenith.core.ClientProvider;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameHud.HeartType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudElementValue extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final List<HudElementValue.NumericValue> list85 = new ArrayList<>();
   public UiAnimation var14316 = new UiAnimation(200L, Easing.CloseScreenEvent);
   public MutableText mutableText;

   public void on23(DrawContext var1) {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      if (clientplayerentity != null) {
         int i = MathHelper.ceil(clientplayerentity.getHealth());
         boolean flag = minecraftClient3.inGameHud.heartJumpEndTick > minecraftClient3.inGameHud.ticks
            && (minecraftClient3.inGameHud.heartJumpEndTick - minecraftClient3.inGameHud.ticks) / 3L % 2L == 1L;
         long j = Util.getMeasuringTimeMs();
         if (i < minecraftClient3.inGameHud.lastHealthValue && clientplayerentity.timeUntilRegen > 0) {
            minecraftClient3.inGameHud.lastHealthCheckTime = j;
            minecraftClient3.inGameHud.heartJumpEndTick = minecraftClient3.inGameHud.ticks + 20;
         } else if (i > minecraftClient3.inGameHud.lastHealthValue && clientplayerentity.timeUntilRegen > 0) {
            minecraftClient3.inGameHud.lastHealthCheckTime = j;
            minecraftClient3.inGameHud.heartJumpEndTick = minecraftClient3.inGameHud.ticks + 10;
         }

         if (j - minecraftClient3.inGameHud.lastHealthCheckTime > 1000L) {
            minecraftClient3.inGameHud.renderHealthValue = i;
            minecraftClient3.inGameHud.lastHealthCheckTime = j;
         }

         minecraftClient3.inGameHud.lastHealthValue = i;
         int k = minecraftClient3.inGameHud.renderHealthValue;
         minecraftClient3.inGameHud.random.setSeed(minecraftClient3.inGameHud.ticks * 312871);
         int l = var1.getScaledWindowWidth() / 2 - 91;
         int i1 = var1.getScaledWindowWidth() / 2 + 90 + 36;
         int j1 = var1.getScaledWindowHeight() - 39;
         float f = Math.max((float)clientplayerentity.getAttributeValue(EntityAttributes.MAX_HEALTH), Math.max(k, i));
         int k1 = MathHelper.ceil(clientplayerentity.getAbsorptionAmount());
         int l1 = MathHelper.ceil((f + k1) / 2.0F / 10.0F);
         int i2 = Math.max(10 - (l1 - 2), 3);
         int j2 = j1 - 10;
         int k2 = -1;
         if (clientplayerentity.hasStatusEffect(StatusEffects.REGENERATION)) {
            k2 = minecraftClient3.inGameHud.ticks % MathHelper.ceil(f + 5.0F);
         }

         Profilers.get().push("armor");
         on23(var1, clientplayerentity, j1, l1, i2, l);
         Profilers.get().swap("health");
         this.on23(var1, clientplayerentity, l, j1, i2, k2, f, i, k, k1, flag);
         LivingEntity livingentity = minecraftClient3.inGameHud.getRiddenEntity();
         int l2 = minecraftClient3.inGameHud.getHeartCount(livingentity);
         if (l2 == 0) {
            Profilers.get().swap("food");
            this.on23(var1, clientplayerentity, j1, i1);
            j2 -= 10;
         }

         Profilers.get().swap("air");
         this.on23(var1, clientplayerentity, l2, j2, i1);
         Profilers.get().pop();
      }
   }

   public HudElementValue(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      float f = 22.0F;
      this.width = f * 9.0F;
      this.height = f;

      for (int i = 0; i < 9; i++) {
         this.list85.add(new HudElementValue.NumericValue(this, i));
      }
   }

   @Override
   public void on23(CustomDrawContext var1) {
      this.height = 22.0F;
      this.width = 198.0F;
      float f = this.getX();
      float f1 = this.getY();
      float f2 = Interface.float212();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (minecraftClient3.interactionManager.getCurrentGameMode().isCreative()) {
         this.on23(var1, f1 - 35.0F);
         this.on23(var1, minecraftClient3.getRenderTickCounter(), f1 - 35.0F - 9.0F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(6.0F);
         int l = minecraftClient3.player.experienceLevel;
         var1.drawText(
            font1, String.valueOf(l), f + this.width / 2.0F - font1.width(String.valueOf(l)) / 2.0F, f1 - 15.0F + font1.height() / 2.0F, ArgbColor.var11936
         );
         var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f2), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f2), zenithstyle.getHudBackground().getColor());
         ItemStack itemstack1 = minecraftClient3.player.getOffHandStack();
         if (!itemstack1.isEmpty()) {
            float f8 = f - this.height - 12.0F;
            var1.drawBlurHud(f8, f1, this.height, this.height, 21.0F, CornerRadius.MovementInputEvent(f2), ArgbColor.var11934);
            var1.drawRoundedRect(f8, f1, this.height, this.height, CornerRadius.MovementInputEvent(f2), zenithstyle.getHudBackground().getColor());
            var1.pushMatrix();
            var1.getMatrices().translate(f8 + 4.6F, f1 + 4.6F);
            var1.getMatrices().scale(0.8F, 0.8F);
            var1.drawItem(itemstack1, 0, 0);
            var1.drawItemBar(itemstack1, 0, 0);
            var1.drawCooldownProgress(itemstack1, 0, 0);
            var1.popMatrix();
            if (itemstack1.getCount() > 1) {
               String s1 = "x" + itemstack1.getCount();
               float f11 = font1.width(s1);
               float f12 = f8 + 22.0F - f11 - 1.0F;
               float f13 = f1 + 22.0F - font1.height() - 3.0F;
               var1.drawText(font1, s1, f12, f13, zenithstyle.getTextTertiary().getColor());
            }
         }

         float f9 = f;

         for (HudElementValue.NumericValue il1l1iillii111il111li1i1i1l_ii1il11l111ii11iil : this.list85) {
            il1l1iillii111il111li1i1i1l_ii1il11l111ii11iil.on23(var1, f9, f1, zenithstyle);
            f9 += this.height;
         }
      } else if (minecraftClient3.interactionManager.hasStatusBars()) {
         int i = var1.getScaledWindowWidth() / 2 - 91;
         int j = var1.getScaledWindowHeight() - 39;
         float f3 = 0.9F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f, f1 - 12.0F);
         var1.getMatrices().scale(f3, f3);
         var1.getMatrices().translate(-i, -j);
         if (!minecraftClient3.interactionManager.getCurrentGameMode().isCreative()) {
            this.on23((DrawContext)var1);
         }

         var1.getMatrices().popMatrix();
         this.on23(var1, f1 - 35.0F);
         this.on23(var1, minecraftClient3.getRenderTickCounter(), f1 - 35.0F - 9.0F);
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         int k = minecraftClient3.player.experienceLevel;
         var1.drawText(
            font, String.valueOf(k), f + this.width / 2.0F - font.width(String.valueOf(k)) / 2.0F, f1 - 15.0F + font.height() / 2.0F, ArgbColor.var11936
         );
         var1.drawBlurHud(this.x, this.y, this.width, this.height, 21.0F, CornerRadius.MovementInputEvent(f2), ArgbColor.var11934);
         var1.drawRoundedRect(this.x, this.y, this.width, this.height, CornerRadius.MovementInputEvent(f2), zenithstyle.getHudBackground().getColor());
         ItemStack itemstack = minecraftClient3.player.getOffHandStack();
         if (!itemstack.isEmpty()) {
            float f4 = f - this.height - 12.0F;
            var1.drawBlurHud(f4, f1, this.height, this.height, 21.0F, CornerRadius.MovementInputEvent(f2), ArgbColor.var11934);
            var1.drawRoundedRect(f4, f1, this.height, this.height, CornerRadius.MovementInputEvent(f2), zenithstyle.getHudBackground().getColor());
            var1.pushMatrix();
            var1.getMatrices().translate(f4 + 4.6F, f1 + 4.6F);
            var1.getMatrices().scale(0.8F, 0.8F);
            var1.drawItem(itemstack, 0, 0);
            var1.drawItemBar(itemstack, 0, 0);
            var1.drawCooldownProgress(itemstack, 0, 0);
            var1.popMatrix();
            if (itemstack.getCount() > 1) {
               String s = "x" + itemstack.getCount();
               float f5 = font.width(s);
               float f6 = f4 + 22.0F - f5 - 3.0F;
               float f7 = f1 + 22.0F - font.height() - 3.0F;
               var1.drawText(font, s, f6, f7, zenithstyle.getTextTertiary().getColor());
            }
         }

         float f10 = f;

         for (HudElementValue.NumericValue il1l1iillii111il111li1i1i1l_ii1il11l111ii11iil1 : this.list85) {
            il1l1iillii111il111li1i1i1l_ii1il11l111ii11iil1.on23(var1, f10, f1, zenithstyle);
            f10 += this.height;
         }
      }
   }

   public void on23(CustomDrawContext var1, float var2) {
      if (this.var14316 == null) {
         this.var14316 = new UiAnimation(300L, Easing.CloseScreenEvent);
      }

      this.var14316.on23(200L);
      this.var14316.on23(Easing.CloseScreenEvent);
      if (minecraftClient3.inGameHud.heldItemTooltipFade > 0 && !minecraftClient3.inGameHud.currentStack.isEmpty()) {
         this.var14316.on23(1.0F);
         this.mutableText = Text.empty()
            .append(minecraftClient3.inGameHud.currentStack.getName())
            .formatted(minecraftClient3.inGameHud.currentStack.getRarity().getFormatting());
         if (minecraftClient3.inGameHud.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
            this.mutableText.formatted(Formatting.ITALIC);
         }
      } else {
         this.var14316.on23(0.0F);
      }

      if (this.mutableText != null && this.var14316.CancellableEvent() > 0.0F) {
         this.on23(var1, this.mutableText, var2);
      }
   }

   public void on23(CustomDrawContext var1, MutableText var2, float var3) {
      Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
      float f = font.width(var2);
      float f1 = (this.width - f) / 2.0F;
      int i = (int)var3;
      if (!minecraftClient3.interactionManager.hasStatusBars() || minecraftClient3.interactionManager.getCurrentGameMode().isCreative()) {
         i += 14;
      }

      short short1 = 255;
      if (short1 > 0) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.x + f1, i);
         var1.drawText(font, var2, 0.0F, 0.0F, ColorHelper.withAlpha((int)(short1 * this.var14316.CancellableEvent()), -1));
         var1.getMatrices().popMatrix();
      }
   }

   public final void on23(CustomDrawContext var1, RenderTickCounter var2, float var3) {
      if (minecraftClient3.inGameHud.overlayMessage != null && minecraftClient3.inGameHud.overlayRemaining > 0) {
         float f = minecraftClient3.inGameHud.overlayRemaining - var2.getTickProgress(false);
         int i = (int)(f * 255.0F / 20.0F);
         if (i > 255) {
            i = 255;
         }

         if (i > 8) {
            Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(this.x + this.width / 2.0F, var3);
            int j;
            if (minecraftClient3.inGameHud.overlayTinted) {
               j = MathHelper.hsvToArgb(f / 50.0F, 0.7F, 0.6F, i);
            } else {
               j = ColorHelper.withAlpha(i, -1);
            }

            float f1 = font.width(minecraftClient3.inGameHud.overlayMessage);
            var1.getMatrices().translate(-f1 / 2.0F, -font.height() / 2.0F);
            var1.drawText(font, minecraftClient3.inGameHud.overlayMessage, 0.0F, 0.0F, j);
            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public void ServiceException(float var1, float var2) {
      if (!(var2 <= 0.0F) && !(var1 <= 0.0F)) {
         if (!this.priorityBlockingQueue()) {
            float f = this.float217 > 0.0F ? this.float217 : var1;
            float f1 = this.float218 > 0.0F ? this.float218 : var2;
            this.CloudRouter(f, f1);
         }

         this.float217 = var1;
         this.float218 = var2;
         float f2 = this.TradeGuardService(var1) + this.float219 * this.CommandManager(var1);
         float f3 = this.ModuleStateStore(var2) + this.float220 * this.EmoteMetadata(var2);
         this.y = this.CloudResponse(f3, var2) + this.int483();
      }
   }

   @Override
   protected void CloudRouter(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float f = this.CommandManager(var1);
         float f1 = this.EmoteMetadata(var2);
         if (!(f <= 0.0F) && !(f1 <= 0.0F)) {
            this.float220 = (this.blockPos31() - this.ModuleStateStore(var2)) / f1;
         }
      }
   }

   public static void on23(DrawContext var0, PlayerEntity var1, int var2, int var3, int var4, int var5) {
      int i = var1.getArmor();
      if (i > 0) {
         int j = var2 - (var3 - 1) * var4 - 10;

         for (int k = 0; k < 10; k++) {
            int l = var5 + k * 8;
            if (k * 2 + 1 < i) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_FULL_TEXTURE, l, j, 9, 9);
            }

            if (k * 2 + 1 == i) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_HALF_TEXTURE, l, j, 9, 9);
            }

            if (k * 2 + 1 > i) {
               var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.ARMOR_EMPTY_TEXTURE, l, j, 9, 9);
            }
         }
      }
   }

   public void on23(DrawContext var1, PlayerEntity var2, int var3, int var4, int var5, int var6, float var7, int var8, int var9, int var10, boolean var11) {
      HeartType hearttype = HeartType.fromPlayerState(var2);
      boolean flag = var2.getEntityWorld().getLevelProperties().isHardcore();
      int i = MathHelper.ceil(var7 / 2.0);
      int j = MathHelper.ceil(var10 / 2.0);
      int k = i * 2;

      for (int l = i + j - 1; l >= 0; l--) {
         int i1 = l / 10;
         int j1 = l % 10;
         int k1 = var3 + j1 * 8;
         int l1 = var4 - i1 * var5;
         if (var8 + var10 <= 4) {
            l1 += minecraftClient3.inGameHud.random.nextInt(2);
         }

         if (l < i && l == var6) {
            l1 -= 2;
         }

         this.on23(var1, HeartType.CONTAINER, k1, l1, flag, var11, false);
         int i2 = l * 2;
         boolean flag1 = l >= i;
         if (flag1) {
            int j2 = i2 - k;
            if (j2 < var10) {
               boolean flag2 = j2 + 1 == var10;
               this.on23(var1, hearttype == HeartType.WITHERED ? hearttype : HeartType.ABSORBING, k1, l1, flag, false, flag2);
            }
         }

         if (var11 && i2 < var9) {
            boolean flag3 = i2 + 1 == var9;
            this.on23(var1, hearttype, k1, l1, flag, true, flag3);
         }

         if (i2 < var8) {
            boolean flag4 = i2 + 1 == var8;
            this.on23(var1, hearttype, k1, l1, flag, false, flag4);
         }
      }
   }

   public void on23(DrawContext var1, PlayerEntity var2, int var3, int var4) {
      HungerManager hungermanager = var2.getHungerManager();
      int i = hungermanager.getFoodLevel();

      for (int j = 0; j < 10; j++) {
         int k = var3;
         Identifier identifier;
         Identifier identifier1;
         Identifier identifier2;
         if (var2.hasStatusEffect(StatusEffects.HUNGER)) {
            identifier = InGameHud.FOOD_EMPTY_HUNGER_TEXTURE;
            identifier1 = InGameHud.FOOD_HALF_HUNGER_TEXTURE;
            identifier2 = InGameHud.FOOD_FULL_HUNGER_TEXTURE;
         } else {
            identifier = InGameHud.FOOD_EMPTY_TEXTURE;
            identifier1 = InGameHud.FOOD_HALF_TEXTURE;
            identifier2 = InGameHud.FOOD_FULL_TEXTURE;
         }

         if (var2.getHungerManager().getSaturationLevel() <= 0.0F && minecraftClient3.inGameHud.ticks % (i * 3 + 1) == 0) {
            k = var3 + (minecraftClient3.inGameHud.random.nextInt(3) - 1);
         }

         int l = var4 - j * 8 - 9;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, l, k, 9, 9);
         if (j * 2 + 1 < i) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, l, k, 9, 9);
         }

         if (j * 2 + 1 == i) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier1, l, k, 9, 9);
         }
      }
   }

   public void on23(DrawContext var1, HeartType var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, var2.getTexture(var5, var7, var6), var3, var4, 9, 9);
   }

   public void on23(DrawContext var1, PlayerEntity var2, int var3, int var4, int var5) {
      int i = var2.getMaxAir();
      int j = Math.clamp(var2.getAir(), 0, i);
      boolean flag = var2.isSubmergedIn(FluidTags.WATER);
      if (flag || j < i) {
         var4 = this.ItemSpec(var3, var4);
         int k = this.on23(j, i, -2);
         int l = this.on23(j, i, 0);
         int i1 = 10 - this.on23(j, i, this.Easing(j, flag));
         boolean flag1 = k != l;
         if (!flag) {
            minecraftClient3.inGameHud.lastBurstBubble = 0;
         }

         for (int j1 = 1; j1 <= 10; j1++) {
            int k1 = var5 - (j1 - 1) * 8 - 9;
            if (j1 <= k) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_TEXTURE, k1, var4, 9, 9);
            } else if (flag1 && j1 == l && flag) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_BURSTING_TEXTURE, k1, var4, 9, 9);
               minecraftClient3.inGameHud.playBurstSound(j1, var2, i1);
            } else if (j1 > 10 - i1) {
               int l1 = i1 == 10 && minecraftClient3.inGameHud.ticks % 2 == 0 ? minecraftClient3.inGameHud.random.nextInt(2) : 0;
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.AIR_EMPTY_TEXTURE, k1, var4 + l1, 9, 9);
            }
         }
      }
   }

   public int ItemSpec(int var1, int var2) {
      int i = minecraftClient3.inGameHud.getHeartRows(var1) - 1;
      return var2 - i * 10;
   }

   public int on23(int var1, int var2, int var3) {
      return MathHelper.ceil((float)((var1 + var3) * 10) / var2);
   }

   public int Easing(int var1, boolean var2) {
      return var1 != 0 && var2 ? 1 : 0;
   }


   public static class NumericValue {
      public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
      public final UiAnimation var1435 = new UiAnimation(150L, 0.0F, Easing.StopUsingItemEvent);
      public final UiAnimation var1436 = new UiAnimation(150L, 0.0F, Easing.StopUsingItemEvent);
      public final int int126;

      public NumericValue(HudElementValue var1, int var2) {
         this.int126 = var2;
      }

      public void on23(CustomDrawContext var1, float var2, float var3, ZenithStyle var4) {
         float f = Interface.float212();
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f);
         this.var1435.on23(80L);
         Font font = Fonts.NEW_MEDIUM.getFont(5.4F);
         this.var1435.on23(this.int126 == ClientProvider.minecraftClient3.player.getInventory().selectedSlot ? 1.0F : 0.0F);
         ArgbColor i11ii1llliilllii1i1 = var4.getHeaderHudBackground().getColor(this.var1435.CancellableEvent());
         ArgbColor i11ii1llliilllii1i11 = var4.getTextSecondary().getColor().Easing(var4.getTextEnable().getColor(), this.var1435.CancellableEvent());
         ItemStack itemstack = (ItemStack)ClientProvider.minecraftClient3.player.getInventory().main.get(this.int126);
         this.var1436.on23(itemstack.isEmpty());
         var1.drawRoundedRect(var2, var3, 22.0F, 22.0F, ii1il11l111ii11iil, i11ii1llliilllii1i1);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + 4.6F, var3 + 4.6F);
         var1.getMatrices().scale(0.8F, 0.8F);
         var1.drawItem(itemstack, 0, 0);
         var1.drawItemBar(itemstack, 0, 0);
         var1.drawCooldownProgress(itemstack, 0, 0);
         var1.popMatrix();
         var1.drawText(
            font,
            String.valueOf(this.int126 + 1),
            var2 + (22.0F - font.width(String.valueOf(this.int126 + 1))) / 2.0F,
            var3 + (22.0F - font.height()) / 2.0F,
            var4.getTextTertiary().getColor(this.var1436.CancellableEvent())
         );
         if (itemstack.getCount() > 1) {
            String s = "x" + itemstack.getCount();
            float f1 = font.width(s);
            float f2 = var2 + 22.0F - f1 - 3.0F;
            float f3 = var3 + 22.0F - font.height() - 3.0F;
            var1.drawText(font, s, f2, f3, i11ii1llliilllii1i11);
         }
      }
   }
}
