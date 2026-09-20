package org.zenith.module.combat;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;


import net.minecraft.client.util.math.MatrixStack;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.event.EventClickSlotHook;
import org.zenith.event.EventMouseButton;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

@ModuleInfo(name = "AutoSwap", category = Category.COMBAT, description = "Автоматический свап предметов")
public final class AutoSwap extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoSwap autoSwap = new AutoSwap();
   public final ModeSetting mode3 = new ModeSetting(
      "module.autoSwap.mode", "module.autoSwap.mode.desc", "module.autoSwap.mode.items", "module.autoSwap.mode.wheel"
   );
   public final ModeSetting itemType = new ModeSetting(
      "module.autoSwap.itemType",
      "module.autoSwap.itemType.desc",
      () -> this.mode3.is(0),
      "module.autoSwap.itemHead",
      "module.autoSwap.itemTotem",
      "module.autoSwap.itemGapple",
      "module.autoSwap.itemShield"
   );
   public final ModeSetting swapType = new ModeSetting(
      "module.autoSwap.swapType",
      "module.autoSwap.swapType.desc",
      () -> this.mode3.is(0),
      "module.autoSwap.swapHead",
      "module.autoSwap.swapTotem",
      "module.autoSwap.swapGapple",
      "module.autoSwap.swapShield"
   );
   public final KeySetting keyToSwap = new KeySetting("module.autoSwap.keyToSwap", "module.autoSwap.keyToSwap.desc", -1, () -> this.mode3.is(0));
   public final KeySetting keyToSwapCerb = new KeySetting(
      "module.autoSwap.keyToSwapCerb", "module.autoSwap.keyToSwapCerb.desc", -1, () -> this.mode3.is(0) && val003.CloudApiClient().call003()
   );
   public final KeySetting wheelBind = new KeySetting("module.autoSwap.wheelBind", "module.autoSwap.wheelBind.desc", -1, () -> this.mode3.is(1));
   public final int int48 = 3;
   public final String string6 = "minecraft:air";
   public final String string7 = "item";
   public final String string8 = "stack";
   public final float float4 = 60.0F;
   public final float float5 = 94.0F;
   public final float float6 = 0.35F;
   public final long long23 = 250L;
   public final AutoSwap.StoredItem[] val029 = new AutoSwap.StoredItem[3];
   public boolean boolean13;
   public boolean boolean14;
   public int int49 = -1;
   public int int50 = -1;
   public int int51 = -1;
   public long long24;
   public long long136;
   public final Random random4 = new Random();
   public final UiAnimation var1432 = new UiAnimation(280L, Easing.RenderTickEvent);
   public final UiAnimation[] val224 = new UiAnimation[]{
      new UiAnimation(160L, Easing.EventClick), new UiAnimation(160L, Easing.EventClick), new UiAnimation(160L, Easing.EventClick)
   };

   public AutoSwap() {
      for (int i = 0; i < this.val029.length; i++) {
         this.val029[i] = new AutoSwap.StoredItem(this);
      }
   }

   @Override
   public void onDisable() {
      this.call023();
      super.onDisable();
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (this.mode3.is(1)) {
            this.UiAnimation(var1);
         } else if (minecraftClient3.currentScreen == null && var1.TridentAimbot() == 1) {
            Slot slot = this.string120();
            if (var1.ItemRegistry(this.keyToSwap.getKeyCode())) {
               Slot slot1 = ScreenUtils.on23(
                  this.on23(this.itemType),
                  Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
                  var1x -> var1x != slot && var1x.id != 46 && var1x.id != 45
               );
               Slot slot2 = ScreenUtils.on23(
                  this.on23(this.swapType),
                  Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
                  var1x -> var1x != slot && var1x.id != 46 && var1x.id != 45
               );
               Slot slot3 = slot1 != null && minecraftClient3.player.getOffHandStack().getItem() != slot1.getStack().getItem() ? slot1 : slot2;
               this.ItemSpec(slot3);
            }

            if (var1.ItemRegistry(this.keyToSwapCerb.getKeyCode()) && slot != null) {
               this.ItemSpec(slot);
            }
         }
      }
   }

   @EventTarget(4)
   public void on23(EventRenderScreenHook var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.mode3.is(1)) {
            if (minecraftClient3.currentScreen == null) {
               if (this.int49 != -1) {
                  this.call023();
               } else {
                  this.UiAnimation(var1);
               }
            }
         } else if (this.boolean13 || this.boolean14) {
            this.call023();
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventMouseButton var1) {
      if (this.mode3.is(1)
         && this.boolean13
         && minecraftClient3.player != null
         && minecraftClient3.world != null
         && minecraftClient3.currentScreen == null
         && var1.TridentAimbot() == 1) {
         float f = this.call158();
         float f1 = this.call159();
         float f2 = minecraftClient3.getWindow().getScaledWidth() / 2.0F;
         float f3 = minecraftClient3.getWindow().getScaledHeight() / 2.0F;
         if (var1.ContainerScanner() == 1 && this.BotFeaturesDto(f, f1, f2, f3)) {
            this.call455();
         } else {
            int i = this.UiAnimation(f, f1, f2, f3, 60.0F);
            if (i != -1) {
               this.TextScanner(var1.ContainerScanner(), i);
            }
         }
      }
   }

   @EventTarget
   public void on23(EventClickSlotHook var1) {
      if (this.mode3.is(1)
         && this.int49 != -1
         && minecraftClient3.player != null
         && minecraftClient3.currentScreen instanceof InventoryScreen
         && var1.HeldItemWatcher() == SlotActionType.PICKUP) {
         this.UiAnimation(var1);
      }
   }

   public void UiAnimation(EventTriggerKeyEvent var1) {
      if (var1.is(this.wheelBind.getKeyCode())) {
         if (var1.TridentAimbot() == 1) {
            this.call396();
         } else if (var1.TridentAimbot() == 0) {
            if (this.int49 != -1 && minecraftClient3.currentScreen instanceof InventoryScreen) {
               return;
            }

            this.call023();
         }
      }
   }

   public void call396() {
      if (minecraftClient3.currentScreen == null) {
         this.boolean13 = true;
         this.CloudResponse(true);
      }
   }

   public void TextScanner(int var1, int var2) {
      if (var1 == 1) {
         this.ModuleStateStore(var2);
      } else if (var1 == 0) {
         this.CloudPoller(var2);
      }
   }

   public void ModuleStateStore(int var1) {
      AutoSwap.StoredItem l1li1l1_illi1l1l1 = this.EmotePlayback(var1);
      if (l1li1l1_illi1l1l1 != null) {
         l1li1l1_illi1l1l1.clear();
         this.int50 = var1;
         this.long24 = System.currentTimeMillis() + 250L;
      }
   }

   public void call455() {
      for (AutoSwap.StoredItem l1li1l1_illi1l1l1 : this.val029) {
         l1li1l1_illi1l1l1.clear();
      }

      this.int50 = -1;
      this.long24 = System.currentTimeMillis() + 250L;
   }

   public void CloudPoller(int var1) {
      this.int49 = var1;
      minecraftClient3.setScreen(new InventoryScreen(minecraftClient3.player));
   }

   public void UiAnimation(EventClickSlotHook var1) {
      DefaultedList defaultedlist = minecraftClient3.player.currentScreenHandler.slots;
      if (var1.SlotIndex() >= 0 && var1.SlotIndex() < defaultedlist.size()) {
         ItemStack itemstack = ((Slot)defaultedlist.get(var1.SlotIndex())).getStack();
         if (!itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
            AutoSwap.StoredItem l1li1l1_illi1l1l1 = this.EmotePlayback(this.int49);
            if (l1li1l1_illi1l1l1 != null) {
               l1li1l1_illi1l1l1.ColorAnimator(itemstack);
               var1.setCancelled(true);
               minecraftClient3.setScreen(null);
               this.call023();
            }
         }
      }
   }

   public void UiAnimation(EventRenderScreenHook var1) {
      float f = this.var1432.on23(this.boolean13 ? 1.0F : 0.0F);
      if (f <= 0.005F && !this.boolean13) {
         for (UiAnimation l1i1illlili : this.val224) {
            l1i1illlili.setValue(0.0F);
         }
      } else {
         if (this.boolean13) {
            this.CloudResponse(true);
         }

         AutoSwap.RenderContext l1li1l1_ii1il11l111ii11iil = this.on23(var1.WarpFarm(), f);
         if (l1li1l1_ii1il11l111ii11iil != null) {
            if (this.boolean13 && l1li1l1_ii1il11l111ii11iil.float107 > 0.4F) {
               this.EmoteMetadata(l1li1l1_ii1il11l111ii11iil.int172);
            }

            this.on23(l1li1l1_ii1il11l111ii11iil);
            this.UiAnimation(l1li1l1_ii1il11l111ii11iil);
            this.Easing(l1li1l1_ii1il11l111ii11iil);
            this.ColorAnimator(l1li1l1_ii1il11l111ii11iil);
            this.ItemSpec(l1li1l1_ii1il11l111ii11iil);
         }
      }
   }

   public AutoSwap.RenderContext on23(HudDrawContext var1, float var2) {
      float f = minecraftClient3.getWindow().getScaledWidth() / 2.0F;
      float f1 = minecraftClient3.getWindow().getScaledHeight() / 2.0F;
      float f2 = 0.85F + 0.15F * var2;
      float f3 = 60.0F * f2;
      float f4 = 94.0F * f2;
      int i = this.boolean13 ? this.UiAnimation(var1.getMouseX(), var1.getMouseY(), f, f1, f3) : -1;
      boolean flag = this.boolean13 && this.BotFeaturesDto(var1.getMouseX(), var1.getMouseY(), f, f1);
      ZenithStyle zenithstyle = val003.TextScanner().getCurrentStyle();
      return zenithstyle == null ? null : new AutoSwap.RenderContext(var1, org.zenith.render.GuiMatrixAdapter.toMatrixStack(var1.getMatrices()), f, f1, f3, f4, f2, var2, i, flag, zenithstyle);
   }

   public void on23(AutoSwap.RenderContext var1) {
      float f = val003.NbtEditor().getBlurPower();
      if (!(f <= 0.0F)) {
         float f1 = var1.float105 + 7.0F;
         ShapeRenderer.on23(
            var1.matrixStack3,
            var1.float102 - f1,
            var1.float103 - f1,
            f1 * 2.0F,
            f1 * 2.0F,
            f,
            CornerRadius.MovementInputEvent(f1),
            ArgbColor.var11934.SprintStateEvent(var1.float107 * 0.92F),
            true,
            false
         );
      }
   }

   public void UiAnimation(AutoSwap.RenderContext var1) {
      float f = this.int296();
      float f1 = this.call242();
      float f2 = f - f1;
      boolean flag = this.int50 != -1 && System.currentTimeMillis() <= this.long24;

      for (int i = 0; i < 3; i++) {
         float f3 = this.val224[i].on23(i == var1.int172 ? 1.0F : 0.0F);
         boolean flag1 = this.EmoteManager(i);
         AutoSwap.ColorPair l1li1l1_l1i1illlili = this.on23(var1, f3, flag && i == this.int50, flag1);
         float f4 = var1.float105 + f3 * 6.0F;
         this.on23(var1, i * f + f1 * 0.5F);
         this.on23(var1, f4, f2, l1li1l1_l1i1illlili.var11931, l1li1l1_l1i1illlili.var11932);
         if (f3 > 0.01F) {
            ShapeRenderer.on23(
               var1.matrixStack3,
               var1.float102 - f4,
               var1.float103 - f4,
               f4 * 2.0F,
               f4 * 2.0F,
               1.35F + f3,
               f2,
               0.0F,
               var1.zenithStyle.getPrimaryColor().getColor().SprintStateEvent(var1.float107 * f3 * 0.8F)
            );
         }

         var1.matrixStack3.pop();
      }
   }

   public float int296() {
      return 120.0F;
   }

   public float call242() {
      return 2.4F;
   }

   public AutoSwap.ColorPair on23(AutoSwap.RenderContext var1, float var2, boolean var3, boolean var4) {
      if (var3) {
         return new AutoSwap.ColorPair(new ArgbColor(255, 90, 90, (int)(220.0F * var1.float107)), new ArgbColor(210, 40, 40, (int)(170.0F * var1.float107)));
      }

      if (var4) {
         return new AutoSwap.ColorPair(
            var1.zenithStyle
               .getDisableActiveBg()
               .getColor()
               .SprintStateEvent(var1.float107)
               .Easing(var1.zenithStyle.getPrimaryColor().getColor().SprintStateEvent(var1.float107), var2 * 0.45F),
            var1.zenithStyle
               .getRightBackground()
               .getColor()
               .SprintStateEvent(var1.float107 * 0.9F)
               .Easing(var1.zenithStyle.getPrimaryColor().getColor().SprintPacketEvent(0.55F).SprintStateEvent(var1.float107), var2 * 0.4F)
         );
      }

      ArgbColor i11ii1llliilllii1i1 = var1.zenithStyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var1.float107);
      ArgbColor i11ii1llliilllii1i11 = var1.zenithStyle.getRightBackground().getColor().SprintStateEvent(var1.float107 * 0.92F);
      if (var2 <= 0.001F) {
         return new AutoSwap.ColorPair(i11ii1llliilllii1i1, i11ii1llliilllii1i11);
      }

      ArgbColor i11ii1llliilllii1i12 = var1.zenithStyle
         .getSurfaceEnableBackground()
         .getColor()
         .SprintStateEvent(var1.float107)
         .Easing(var1.zenithStyle.getPrimaryColor().getColor().SprintStateEvent(var1.float107), var2 * 0.65F);
      ArgbColor i11ii1llliilllii1i13 = var1.zenithStyle.getPrimaryColor().getColor().SprintPacketEvent(0.48F).SprintStateEvent(var1.float107);
      return new AutoSwap.ColorPair(i11ii1llliilllii1i1.Easing(i11ii1llliilllii1i12, var2), i11ii1llliilllii1i11.Easing(i11ii1llliilllii1i13, var2));
   }

   public void on23(AutoSwap.RenderContext var1, float var2) {
      var1.matrixStack3.push();
      var1.matrixStack3.translate(var1.float102, var1.float103, 0.0F);
      var1.matrixStack3.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var2));
      var1.matrixStack3.translate(-var1.float102, -var1.float103, 0.0F);
   }

   public void on23(AutoSwap.RenderContext var1, float var2, float var3, ArgbColor var4, ArgbColor var5) {
      ShapeRenderer.on23(
         var1.matrixStack3, var1.float102 - var2, var1.float103 - var2, var2 * 2.0F, var2 * 2.0F, var2 - var1.float104, var3, 0.0F, var4, var5, var5, var4
      );
   }

   public void Easing(AutoSwap.RenderContext var1) {
      for (int i = 0; i < 3; i++) {
         this.on23(var1, i);
      }
   }

   public void on23(AutoSwap.RenderContext var1, int var2) {
      ItemStack itemstack = this.CosmeticManager(var2);
      float f = this.val224[var2].CancellableEvent();
      float f1 = this.CancellableEvent(var2);
      float f2 = (var1.float104 + var1.float105) / 2.0F + f * 4.0F;
      float f3 = var1.float102 + (float)Math.cos(f1) * f2;
      float f4 = var1.float103 + (float)Math.sin(f1) * f2;
      if (!itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
         var1.val018.drawItem(itemstack, (int)(f3 - 8.0F), (int)(f4 - 8.0F));
         this.on23(var1, itemstack, f3, f4);
      } else {
         this.on23(var1, f3, f4, f);
      }
   }

   public void on23(AutoSwap.RenderContext var1, float var2, float var3, float var4) {
      ArgbColor i11ii1llliilllii1i1 = var1.zenithStyle.getTextTertiary().getColor().SprintStateEvent(var1.float107 * (0.62F + var4 * 0.38F));
      var1.val018.drawRoundedRect(var2 - 5.0F, var3 - 0.7F, 10.0F, 1.4F, CornerRadius.MovementInputEvent(0.7F), i11ii1llliilllii1i1);
      var1.val018.drawRoundedRect(var2 - 0.7F, var3 - 5.0F, 1.4F, 10.0F, CornerRadius.MovementInputEvent(0.7F), i11ii1llliilllii1i1);
   }

   public void on23(AutoSwap.RenderContext var1, ItemStack var2, float var3, float var4) {
      int i = this.TextScanner(var2);
      if (i > 1) {
         Font font = Fonts.NEW_SEMIBOLD.getFont(6.0F);
         String s = String.valueOf(i);
         float f = var3 + 8.0F - font.width(s);
         float f1 = var4 + 5.5F;
         var1.val018.drawText(font, s, f + 0.5F, f1 + 0.5F, var1.zenithStyle.getRightBackground().getColor().SprintStateEvent(var1.float107));
         var1.val018.drawText(font, s, f, f1, var1.zenithStyle.getTextEnable().getColor().SprintStateEvent(var1.float107));
      }
   }

   public void ColorAnimator(AutoSwap.RenderContext var1) {
      this.ItemRegistry(var1);
      if (minecraftClient3.player != null) {
         ItemStack itemstack = minecraftClient3.player.getOffHandStack();
         if (!itemstack.isEmpty() && itemstack.getItem() != Items.AIR) {
            var1.val018.drawItem(itemstack, (int)(var1.float102 - 8.0F), (int)(var1.float103 - 8.0F));
         }
      }
   }

   public void ItemRegistry(AutoSwap.RenderContext var1) {
      float f = 32.0F * var1.float106;
      var1.val018
         .drawRoundedRect(
            var1.float102 - f,
            var1.float103 - f,
            f * 2.0F,
            f * 2.0F,
            CornerRadius.MovementInputEvent(f),
            var1.zenithStyle.getLeftBackground().getColor().SprintStateEvent(var1.float107 * 0.97F)
         );
      if (var1.boolean115) {
         var1.val018
            .drawRoundedBorder(
               var1.float102 - f - 1.0F,
               var1.float103 - f - 1.0F,
               (f + 1.0F) * 2.0F,
               (f + 1.0F) * 2.0F,
               1.2F,
               CornerRadius.MovementInputEvent(f + 1.0F),
               var1.zenithStyle.getPrimaryColor().getColor().SprintStateEvent(var1.float107 * 0.75F)
            );
      }
   }

   public void ItemSpec(AutoSwap.RenderContext var1) {
      if (!(var1.float107 <= 0.5F)) {
         String s;
         String s1;
         if (var1.boolean115) {
            s = "Вторая рука";
            s1 = "ПКМ очистить всё";
         } else if (var1.int172 == -1) {
            s = "AutoSwap";
            s1 = "Наведитесь на нужный предмет";
         } else {
            ItemStack itemstack = this.CosmeticManager(var1.int172);
            boolean flag = itemstack.isEmpty() || itemstack.getItem() == Items.AIR;
            s = flag ? "Пустой слот" : itemstack.getName().getString();
            s1 = this.ColorAnimator(var1.int172, flag);
         }

         Font font = Fonts.NEW_SEMIBOLD.getFont(7.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float f = Math.max(font.width(s), font1.width(s1)) + 20.0F;
         float f1 = var1.float102 - f / 2.0F;
         float f2 = var1.float103 - var1.float105 - 38.0F;
         float f3 = val003.NbtEditor().getBlurPower();
         if (f3 > 0.0F) {
            ShapeRenderer.on23(
               var1.matrixStack3, f1, f2, f, 27.0F, f3, CornerRadius.MovementInputEvent(7.0F), ArgbColor.var11934.SprintStateEvent(var1.float107), true, false
            );
         }

         var1.val018
            .drawRoundedRect(
               f1, f2, f, 27.0F, CornerRadius.MovementInputEvent(7.0F), var1.zenithStyle.getLeftBackground().getColor().SprintStateEvent(var1.float107 * 0.92F)
            );
         var1.val018
            .drawText(font, s, var1.float102 - font.width(s) / 2.0F, f2 + 5.0F, var1.zenithStyle.getTextEnable().getColor().SprintStateEvent(var1.float107));
         var1.val018
            .drawText(
               font1, s1, var1.float102 - font1.width(s1) / 2.0F, f2 + 16.0F, var1.zenithStyle.getTextSecondary().getColor().SprintStateEvent(var1.float107)
            );
      }
   }

   public String ColorAnimator(int var1, boolean var2) {
      if (var2) {
         return "ЛКМ привязать";
      } else {
         return this.EmoteManager(var1) ? "Нет в инвентаре" : "ПКМ очистить";
      }
   }

   public void ItemSpec(Slot var1) {
      if (var1 != null && TaskScheduler.Easing(AutoTotem.class) && TaskScheduler.Easing(AutoSwap.class)) {
         TaskScheduler.on23(AutoSwap.class, () -> {
            if (TaskScheduler.Easing(AutoTotem.class)) {
               ScreenUtils.on23(var1, Hand.OFF_HAND, true);
            }
         });
         OffHandManager.offHandManager.reset();
      }
   }

   public Slot string120() {
      return this.keyToSwapCerb.getKeyCode() == -1
         ? null
         : ScreenUtils.on23(
            Items.PLAYER_HEAD,
            var0 -> {
               if (var0.getStack().isEmpty()) {
                  return false;
               }

               NbtComponent nbtcomponent = (NbtComponent)var0.getStack().get(DataComponentTypes.CUSTOM_DATA);
               return nbtcomponent != null
                  && nbtcomponent.copyNbt().contains("SkullOwner")
                  && nbtcomponent.copyNbt()
                     .get("SkullOwner")
                     .toString()
                     .contains(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
                     );
            }
         );
   }

   public Item on23(ModeSetting var1) {
      return switch (var1.getIndex()) {
         case 0 -> Items.PLAYER_HEAD;
         case 1 -> Items.TOTEM_OF_UNDYING;
         case 2 -> Items.GOLDEN_APPLE;
         case 3 -> Items.SHIELD;
         default -> Items.AIR;
      };
   }

   public void EmoteMetadata(int var1) {
      if (var1 == -1) {
         this.int51 = -1;
      } else if (var1 != this.int51) {
         ItemStack itemstack = this.CosmeticManager(var1);
         if (itemstack.isEmpty() || itemstack.getItem() == Items.AIR) {
            this.int51 = -1;
         } else if (this.on23(minecraftClient3.player.getOffHandStack(), itemstack)) {
            this.int51 = var1;
         } else {
            Slot slot = this.ItemSpec(itemstack);
            if (slot == null) {
               this.int51 = var1;
            } else {
               long i = System.currentTimeMillis();
               if (i - this.long136 >= 200L + this.random4.nextInt(80)) {
                  this.long136 = i;
                  this.ItemSpec(slot);
               }

               this.int51 = var1;
            }
         }
      }
   }

   public boolean EmoteManager(int var1) {
      ItemStack itemstack = this.CosmeticManager(var1);
      return !this.NbtItemSpec(itemstack) && this.ItemSpec(itemstack) == null && !this.SimpleItemBuilder(itemstack);
   }

   public Slot ItemSpec(ItemStack var1) {
      return this.NbtItemSpec(var1)
         ? null
         : ScreenUtils.on23(
            var1.getItem(),
            Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
            var2 -> var2.id != 46 && var2.id != 45 && this.on23(var2.getStack(), var1)
         );
   }

   public int TextScanner(ItemStack var1) {
      if (!this.NbtItemSpec(var1) && minecraftClient3.player != null && minecraftClient3.player.currentScreenHandler != null) {
         int i = 0;

         for (Slot slot : minecraftClient3.player.currentScreenHandler.slots) {
            if (slot.id != 46) {
               ItemStack itemstack = slot.getStack();
               if (this.on23(itemstack, var1)) {
                  i += itemstack.getCount();
               }
            }
         }

         return i;
      } else {
         return 0;
      }
   }

   public ItemStack CosmeticManager(int var1) {
      AutoSwap.StoredItem l1li1l1_illi1l1l1 = this.EmotePlayback(var1);
      return l1li1l1_illi1l1l1 == null ? ItemStack.EMPTY : l1li1l1_illi1l1l1.AutoZamok();
   }

   public boolean NbtItemSpec(ItemStack var1) {
      return var1 == null || var1.isEmpty() || var1.getItem() == Items.AIR;
   }

   public ItemStack EnchantItemSpec(ItemStack var1) {
      ItemStack itemstack = var1.copy();
      itemstack.setCount(1);
      return itemstack;
   }

   public ItemStack SprintStateEvent(String var1) {
      if (var1 != null && !var1.isBlank()) {
         Identifier identifier = Identifier.tryParse(var1);
         if (identifier != null && Registries.ITEM.containsId(identifier)) {
            Item item = (Item)Registries.ITEM.get(identifier);
            return item == Items.AIR ? ItemStack.EMPTY : item.getDefaultStack();
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean on23(ItemStack var1, ItemStack var2) {
      if (!var1.isEmpty() && !var2.isEmpty()) {
         return var2.getItem() != Items.PLAYER_HEAD ? var1.getItem() == var2.getItem() : ItemStack.areItemsAndComponentsEqual(var1, var2);
      } else {
         return false;
      }
   }

   public boolean SimpleItemBuilder(ItemStack var1) {
      return this.on23(minecraftClient3.player.getOffHandStack(), var1);
   }

   public AutoSwap.StoredItem EmotePlayback(int var1) {
      return var1 >= 0 && var1 < this.val029.length ? this.val029[var1] : null;
   }

   public String ItemServiceBase(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && minecraftClient3.world != null) {
         try {
            RegistryOps registryops = minecraftClient3.world.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult<JsonElement> dataresult = ItemStack.CODEC.encodeStart(registryops, var1);
            return dataresult.result().<String>map(JsonElement::toString).orElse(null);
         } catch (Exception exception) {
            return null;
         }
      } else {
         return null;
      }
   }

   public ItemStack SprintPacketEvent(String var1) {
      if (var1 != null && !var1.isBlank() && minecraftClient3.world != null) {
         try {
            JsonElement jsonelement = JsonParser.parseString(var1);
            RegistryOps registryops = minecraftClient3.world.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult dataresult = ItemStack.CODEC.decode(registryops, jsonelement);
            if (dataresult.result().isEmpty()) {
               return ItemStack.EMPTY;
            }

            ItemStack itemstack = (ItemStack)((Pair)dataresult.result().get()).getFirst();
            if (itemstack.isEmpty()) {
               return ItemStack.EMPTY;
            }

            itemstack.setCount(1);
            return itemstack;
         } catch (Exception exception) {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public JsonObject save() {
      JsonObject jsonobject = super.save();
      JsonArray jsonarray = new JsonArray();

      for (AutoSwap.StoredItem l1li1l1_illi1l1l1 : this.val029) {
         jsonarray.add(l1li1l1_illi1l1l1.save());
      }

      jsonobject.add("WheelItems", jsonarray);
      return jsonobject;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("WheelItems") && var1.get("WheelItems").isJsonArray()) {
         for (AutoSwap.StoredItem l1li1l1_illi1l1l1 : this.val029) {
            l1li1l1_illi1l1l1.clear();
         }

         JsonArray jsonarray = var1.getAsJsonArray("WheelItems");

         for (int i = 0; i < this.val029.length && i < jsonarray.size(); i++) {
            this.val029[i].UiAnimation(jsonarray.get(i));
         }
      }
   }

   public int UiAnimation(float var1, float var2, float var3, float var4, float var5) {
      float f = var1 - var3;
      float f1 = var2 - var4;
      float f2 = (float)Math.sqrt(f * f + f1 * f1);
      if (f2 < var5 * 0.35F) {
         return -1;
      }

      double d0 = Math.atan2(f1, f) + (Math.PI / 2);
      if (d0 < 0.0) {
         d0 += Math.PI * 2;
      }

      int i = (int)Math.floor(d0 / (Math.PI * 2) * 3.0);
      return i >= 0 && i < 3 ? i : -1;
   }

   public boolean BotFeaturesDto(float var1, float var2, float var3, float var4) {
      float f = var1 - var3;
      float f1 = var2 - var4;
      return Math.sqrt(f * f + f1 * f1) <= 21.0;
   }

   public float CancellableEvent(int var1) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((var1 + 0.5) / 3.0));
   }

   public float call158() {
      return (float)(minecraftClient3.mouse.getX() * minecraftClient3.getWindow().getScaledWidth() / minecraftClient3.getWindow().getWidth());
   }

   public float call159() {
      return (float)(minecraftClient3.mouse.getY() * minecraftClient3.getWindow().getScaledHeight() / minecraftClient3.getWindow().getHeight());
   }

   public void CloudResponse(boolean var1) {
      if (minecraftClient3 != null && minecraftClient3.mouse != null) {
         if (var1) {
            if (!this.boolean14) {
               minecraftClient3.mouse.unlockCursor();
               this.boolean14 = true;
            }
         } else if (this.boolean14) {
            if (minecraftClient3.currentScreen == null) {
               minecraftClient3.mouse.lockCursor();
            }

            this.boolean14 = false;
         }
      }
   }

   public void call023() {
      this.boolean13 = false;
      this.int49 = -1;
      this.int50 = -1;
      this.int51 = -1;
      this.long24 = 0L;
      this.CloudResponse(false);
   }


   public static final class ColorPair {
      public final ArgbColor var11931;
      public final ArgbColor var11932;

      public ColorPair(ArgbColor var1, ArgbColor var2) {
         this.var11931 = var1;
         this.var11932 = var2;
      }
   }

   public static final class RenderContext {
      public final HudDrawContext val018;
      public final MatrixStack matrixStack3;
      public final float float102;
      public final float float103;
      public final float float104;
      public final float float105;
      public final float float106;
      public final float float107;
      public final int int172;
      public final boolean boolean115;
      public final ZenithStyle zenithStyle;

      public RenderContext(
         HudDrawContext var1, MatrixStack var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10, ZenithStyle var11
      ) {
         this.val018 = var1;
         this.matrixStack3 = var2;
         this.float102 = var3;
         this.float103 = var4;
         this.float104 = var5;
         this.float105 = var6;
         this.float106 = var7;
         this.float107 = var8;
         this.int172 = var9;
         this.boolean115 = var10;
         this.zenithStyle = var11;
      }
   }

   public static final class StoredItem {
      public final AutoSwap val071;
      public String string61;
      public String string62;
      public ItemStack itemStack8;

      public StoredItem(AutoSwap var1) {
         this.val071 = var1;
         this.string61 = "minecraft:air";
      }

      public void clear() {
         this.string61 = "minecraft:air";
         this.string62 = null;
         this.itemStack8 = null;
      }

      public void ColorAnimator(ItemStack var1) {
         if (this.val071.NbtItemSpec(var1)) {
            this.clear();
         } else {
            ItemStack itemstack = this.val071.EnchantItemSpec(var1);
            this.string61 = Registries.ITEM.getId(itemstack.getItem()).toString();
            this.string62 = this.val071.ItemServiceBase(itemstack);
            this.itemStack8 = itemstack;
         }
      }

      public ItemStack AutoZamok() {
         if (this.itemStack8 != null) {
            return this.itemStack8;
         }

         ItemStack itemstack = this.val071.SprintPacketEvent(this.string62);
         if (itemstack.isEmpty()) {
            itemstack = this.val071.SprintStateEvent(this.string61);
         }

         this.itemStack8 = itemstack;
         return itemstack;
      }

      public JsonObject save() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", this.string61 == null ? "minecraft:air" : this.string61);
         String s = this.string62;
         if ((s == null || s.isBlank()) && this.itemStack8 != null && !this.itemStack8.isEmpty()) {
            s = this.val071.ItemServiceBase(this.itemStack8);
         }

         if (s != null && !s.isBlank()) {
            jsonobject.addProperty("stack", s);
         }

         return jsonobject;
      }

      public void UiAnimation(JsonElement var1) {
         this.clear();
         if (var1 != null) {
            if (var1.isJsonPrimitive()) {
               this.string61 = var1.getAsString();
            } else if (var1.isJsonObject()) {
               JsonObject jsonobject = var1.getAsJsonObject();
               this.string61 = jsonobject.has("item") ? jsonobject.get("item").getAsString() : "minecraft:air";
               this.string62 = jsonobject.has("stack") ? jsonobject.get("stack").getAsString() : null;
               ItemStack itemstack = this.val071.SprintPacketEvent(this.string62);
               if (!itemstack.isEmpty()) {
                  this.itemStack8 = itemstack;
                  this.string61 = Registries.ITEM.getId(itemstack.getItem()).toString();
               }
            }
         }
      }
   }
}
