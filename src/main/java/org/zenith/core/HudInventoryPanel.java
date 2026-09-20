package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.event.PacketEvent;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudInventoryPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float float248 = 17.0F;
   public static final float float249 = 7.0F;
   public final Map<String, HudInvRowRenderer> map47 = new LinkedHashMap<>();
   public final Map<String, HudInvEntry> getScrollOffset = new LinkedHashMap<>();
   public final Map<String, HudInvEntry> map48 = new LinkedHashMap<>();
   public final UiAnimation var14338 = new UiAnimation(200L, 100.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14339 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation var14340 = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (minecraftClient3 != null && minecraftClient3.world != null) {
         if (var1.ItemScroller() instanceof CooldownUpdateS2CPacket cooldownupdates2cpacket) {
            Item item = (Item)Registries.ITEM.get(cooldownupdates2cpacket.cooldownGroup());
            int i = cooldownupdates2cpacket.cooldown();
            String s = "vanilla:" + item.getTranslationKey();
            if (i <= 0) {
               this.map48.remove(s);
               this.map47.remove(s);
               return;
            }

            long j = minecraftClient3.world.getTime();
            this.map48.put(s, HudInvEntry.on23(s, item.getName().getString(), item.getDefaultStack(), j, i));
         } else if (var1.ItemScroller() instanceof PlayerRespawnS2CPacket) {
            this.map48.clear();
            this.getScrollOffset.clear();
            this.map47.clear();
         }
      }
   }

   @Override
   public void on23(CustomDrawContext var1) {
      if (minecraftClient3 != null) {
         this.float204();
         if (this.map47.isEmpty()) {
            this.var14339.on23(0.0F);
         } else {
            HudInvRowRenderer lii1l1l11illii1lilliililiil111_ii1il11l111ii11iilx = this.map47.values().iterator().next();
            this.var14339.on23(this.map47.size() == 1 && lii1l1l11illii1lilliililiil111_ii1il11l111ii11iilx.var14311.BotDisconnectEvent() == 0.0F ? 0.0F : 1.0F);
         }

         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         Font font = Fonts.NEW_ICONS.getFont(5.5F);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float f = this.x;
         float f1 = this.y;
         float f2 = (float)(
            17.0F + GuiStyle.PADDING.intValue()
               + this.map47.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue()) * var0.var14311.CancellableEvent()).sum()
         );
         float f3 = (float)this.map47.values().stream().mapToDouble(HudInvRowRenderer::float205).max().orElse(100.0);
         f3 = this.var14338.on23(f3);
         this.width = f3;
         this.height = f2;
         float f4 = Interface.float212();
         this.var14340.on23(minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud() || !this.map47.isEmpty());
         var1.pushMatrix();
         var1.getMatrices().translate(f + f3 / 2.0F, f1 + f2 / 2.0F);
         var1.getMatrices().scale(this.var14340.CancellableEvent(), this.var14340.CancellableEvent());
         var1.getMatrices().translate(-(f + f3 / 2.0F), -(f1 + f2 / 2.0F));
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(f4);
         var1.drawBlurHud(f, f1, f3, f2, 21.0F, ii1il11l111ii11iil, ArgbColor.var11934);
         var1.drawRoundedRect(f, f1, f3, f2, ii1il11l111ii11iil, zenithstyle.getHudBackground().getColor());
         var1.drawRoundedRect(f, f1, f3, 17.0F, ii1il11l111ii11iil, zenithstyle.getHeaderHudBackground().getColor());
         var1.drawText(font, "n", f + 8.0F, f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
         var1.drawText(font, "m", f + f3 - 8.0F - font.width("m"), f1 + (17.0F - font.height()) / 2.0F, zenithstyle.getTextTertiary().getColor());
         var1.drawText(
            font1,
            "Cooldown",
            f + 8.0F + font.width("n") + GuiStyle.PADDING.intValue(),
            f1 + (17.0F - font1.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor()
         );
         if (this.var14340.CancellableEvent() == 1.0F) {
            float f5 = f1 + 17.0F + GuiStyle.PADDING.intValue();
            var1.enableScissor((int)f, (int)f1, (int)(f + f3), (int)(f1 + f2));

            for (HudInvRowRenderer lii1l1l11illii1lilliililiil111_ii1il11l111ii11iil : this.map47.values()) {
               lii1l1l11illii1lilliililiil111_ii1il11l111ii11iil.on23(var1, f, f5, f3);
               f5 += (lii1l1l11illii1lilliililiil111_ii1il11l111ii11iil.getHeight() + GuiStyle.PADDING.intValue())
                  * lii1l1l11illii1lilliililiil111_ii1il11l111ii11iil.var14311.CancellableEvent();
            }

            var1.disableScissor();
         }

         this.map47.entrySet().removeIf(var0 -> var0.getValue().float206());
         this.vec3d38();
         var1.popMatrix();
      }
   }

   public HudInventoryPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      EventManager.register(this);
   }

   @EventTarget
   public void Easing(PacketEvent var1) {
      if (minecraftClient3 != null && minecraftClient3.player != null) {
         ItemStack itemstack = minecraftClient3.player.getActiveItem();
         if (itemstack != null && !itemstack.isEmpty() && minecraftClient3.player.getItemUseTime() >= itemstack.getMaxUseTime(minecraftClient3.player)) {
            PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)itemstack.get(DataComponentTypes.POTION_CONTENTS);
            if (potioncontentscomponent != null && (potioncontentscomponent.getColor() == 33461 || potioncontentscomponent.getColor() == -515037)) {
               ItemStack itemstack1 = Items.POTION.getDefaultStack();
               itemstack1.set(
                  DataComponentTypes.POTION_CONTENTS,
                  new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(potioncontentscomponent.getColor()), List.of(), Optional.empty())
               );
               this.on23(itemstack1, "Исцел", 10000L);
            }
         }
      }
   }

   public void on23(ItemStack var1, long var2) {
      String s = "custom:" + var1.getItem().getTranslationKey();
      this.on23(s, var1.getItem().getName().getString(), var1, var2);
   }

   public void on23(ItemStack var1, String var2, long var3) {
      this.on23("custom:" + var2, var2, var1, var3);
   }

   public void on23(String var1, String var2, ItemStack var3, long var4) {
      if (minecraftClient3 != null && !this.map47.containsKey(var1)) {
         long i = System.nanoTime();
         this.getScrollOffset.put(var1, HudInvEntry.UiAnimation(var1, var2, var3, i, Math.max(1L, var4) * 1000000L));
      }
   }

   public void UiAnimation(String var1, String var2, ItemStack var3, long var4) {
      if (minecraftClient3 != null && minecraftClient3.world != null && !this.map47.containsKey(var1)) {
         long i = minecraftClient3.world.getTime();
         this.getScrollOffset.put(var1, HudInvEntry.on23(var1, var2, var3, i, Math.max(1L, var4)));
      }
   }

   public void float204() {
      long i = this.vec3d39();

      for (HudInvEntry lii1l1l11illii1lilliililiil111_illi1l1l1x : this.map48.values()) {
         if (!lii1l1l11illii1lilliililiil111_illi1l1l1x.EnchantItemSpec(i)) {
            this.map47.computeIfAbsent(lii1l1l11illii1lilliililiil111_illi1l1l1x.call017, var2 -> this.on23(lii1l1l11illii1lilliililiil111_illi1l1l1x));
         }
      }

      for (HudInvEntry lii1l1l11illii1lilliililiil111_illi1l1l1x : this.getScrollOffset.values()) {
         long j = this.on23(lii1l1l11illii1lilliililiil111_illi1l1l1x.call024);
         if (!lii1l1l11illii1lilliililiil111_illi1l1l1x.EnchantItemSpec(j)) {
            this.map47.computeIfAbsent(lii1l1l11illii1lilliililiil111_illi1l1l1x.call017, var2 -> this.on23(lii1l1l11illii1lilliililiil111_illi1l1l1x));
         }
      }
   }

   public HudInvRowRenderer on23(HudInvEntry var1) {
      long i = this.on23(var1.call024);
      return new HudInvRowRenderer(
         this,
         var1.call210,
         var1.call001,
         var1.NbtItemSpec(i),
         () -> var1.NbtItemSpec(this.on23(var1.call024)),
         () -> var1.EnchantItemSpec(this.on23(var1.call024)),
         () -> var1.SimpleItemBuilder(this.on23(var1.call024))
      );
   }

   public void vec3d38() {
      long i = this.vec3d39();
      this.map48.entrySet().removeIf(var2 -> var2.getValue().EnchantItemSpec(i));
      this.getScrollOffset.entrySet().removeIf(var1x -> var1x.getValue().EnchantItemSpec(this.on23(var1x.getValue().call024)));
   }

   public long vec3d39() {
      return minecraftClient3 != null && minecraftClient3.world != null ? minecraftClient3.world.getTime() : 0L;
   }

   public long on23(HudInvSortMode var1) {
      return var1 == HudInvSortMode.call010 ? this.vec3d39() : 0L;
   }
}
