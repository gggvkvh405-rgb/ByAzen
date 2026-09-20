package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.event.EventTick;
import org.zenith.event.EventUpdateHealth;
import org.zenith.hud.HudElement;
import org.zenith.hud.HudElement;
import org.zenith.module.render.Interface;
import org.zenith.module.Module;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class HudStatusPanel extends HudElement {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int382 = 10;
   public static final float float270 = 15.0F;
   public static final long long150 = 600000L;
   public final UiAnimation var14359 = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final Deque<HudStatusState> deque2 = new ArrayDeque<>();
   public final Set<String> set17 = new HashSet<>();
   public final Map<UUID, HudStatusTarget> map50 = new HashMap<>();
   public boolean boolean165 = false;
   public final MultiSelectSetting types = new MultiSelectSetting("module.interface.notifications.types", "module.interface.notifications.types.desc");
   public final MultiSelectSetting.Option modeSettingVar15924 = new MultiSelectSetting.Option(this.types, "module.interface.notifications.types.modules", true);
   public final MultiSelectSetting.Option modeSettingVar15925 = new MultiSelectSetting.Option(this.types, "module.interface.notifications.types.strength", true);
   public final MultiSelectSetting.Option modeSettingVar15926 = new MultiSelectSetting.Option(this.types, "module.interface.notifications.types.armor", true);
   public final MultiSelectSetting.Option modeSettingVar15927 = new MultiSelectSetting.Option(this.types, "module.interface.notifications.types.totems", true);
   public final ModeSetting direction = new ModeSetting(
      "module.interface.notifications.direction",
      "module.interface.notifications.direction.desc",
      "module.interface.notifications.direction.down",
      "module.interface.notifications.direction.up"
   );

   @Override
   public void on23(CustomDrawContext var1) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      this.width = 91.928F;
      this.height = 17.0F;
      long i = System.currentTimeMillis();
      Iterator<HudStatusState> iterator = this.deque2.iterator();
      this.var14359.on23((minecraftClient3.currentScreen instanceof ChatScreen || ZenithClient.on23().NbtEditor().isRenderHud()) && this.deque2.isEmpty());
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
      float f = this.x;
      float f1 = this.y;
      var1.pushMatrix();
      var1.getMatrices().translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F);
      var1.getMatrices().scale(this.var14359.CancellableEvent(), this.var14359.CancellableEvent());
      var1.getMatrices().translate(-(this.x + this.width / 2.0F), -(this.y + this.height / 2.0F));
      float f2 = 17.0F;
      float f3 = Interface.float212();
      var1.drawBlurHud(this.x, this.y, this.width, f2, 21.0F, CornerRadius.MovementInputEvent(f3), ArgbColor.var11934);
      var1.drawRoundedRect(this.x, this.y, this.width, f2, CornerRadius.MovementInputEvent(f3), zenithstyle.getHudBackground().getColor());
      var1.drawRoundedRect(this.x, this.y, 16.0F, f2, CornerRadius.MovementInputEvent(f3), zenithstyle.getHeaderHudBackground().getColor());
      var1.drawText(font1, "A", this.x + (16.0F - font1.width("A")) / 2.0F, this.y + (f2 - font1.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor());
      var1.drawText(
         font, "Пример уведомления", this.x + 16.0F + GuiStyle.PADDING * 2, this.y + (17.0F - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor()
      );
      f1 += (f2 + 6.0F) * this.var14359.CancellableEvent();
      var1.popMatrix();
      int j = 0;

      while (iterator.hasNext()) {
         HudStatusState li1llil1lllil111il11l_ii1il11l111ii11iilx = iterator.next();
         if (!li1llil1lllil111il11l_ii1il11l111ii11iilx.val144
            && i - li1llil1lllil111il11l_ii1il11l111ii11iilx.long142 > li1llil1lllil111il11l_ii1il11l111ii11iilx.val429) {
            li1llil1lllil111il11l_ii1il11l111ii11iilx.val144 = true;
            li1llil1lllil111il11l_ii1il11l111ii11iilx.val146.on23(0.0F);
         }

         if (li1llil1lllil111il11l_ii1il11l111ii11iilx.val144 && li1llil1lllil111il11l_ii1il11l111ii11iilx.val146.CancellableEvent() < 0.01F) {
            iterator.remove();
         } else {
            if (!li1llil1lllil111il11l_ii1il11l111ii11iilx.val144) {
               if (li1llil1lllil111il11l_ii1il11l111ii11iilx.val145.isDone() && li1llil1lllil111il11l_ii1il11l111ii11iilx.val145.CancellableEvent() == 0.0F) {
                  li1llil1lllil111il11l_ii1il11l111ii11iilx.val145.UiAnimation(j);
               }

               li1llil1lllil111il11l_ii1il11l111ii11iilx.val145.on23(j);
               j++;
            }

            li1llil1lllil111il11l_ii1il11l111ii11iilx.val146.on23(li1llil1lllil111il11l_ii1il11l111ii11iilx.val144 ? 0.0F : 1.0F);
         }
      }

      for (HudStatusState li1llil1lllil111il11l_ii1il11l111ii11iilx : this.deque2) {
         float f4 = 6.0F;
         float f5 = li1llil1lllil111il11l_ii1il11l111ii11iilx.val145.CancellableEvent() * (f2 + f4);
         if (f5 < 100.0F) {
            li1llil1lllil111il11l_ii1il11l111ii11iilx.on23(var1, f, f1 + (this.direction.is(0) ? f5 : -f5), font, zenithstyle, f2, this);
         } else {
            li1llil1lllil111il11l_ii1il11l111ii11iilx.long142 = System.currentTimeMillis();
         }
      }
   }

   public HudStatusPanel(String var1, float var2, float var3, float var4, float var5, float var6, float var7, HudElement.Anchor var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      EventManager.register(this);
   }

   public boolean boolean148() {
      return this.modeSettingVar15924.isEnabled();
   }

   public void on23(Module var1, boolean var2) {
      this.on23(var1, var2, 3000L);
   }

   public void on23(Module var1, boolean var2, long var3) {
      this.deque2.addLast(new HudStatusRenderer(var1, var2, var3));
   }

   public void UiAnimation(String var1, Text var2) {
      this.UiAnimation(var1, var2, 1500L);
   }

   public void UiAnimation(String var1, Text var2, long var3) {
      this.deque2.addLast(new HudStatusTextRenderer(var1, var2, var3));
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      if (clientplayerentity != null && minecraftClient3.world != null) {
         if (this.on23(this.modeSettingVar15925)) {
            this.UiAnimation(clientplayerentity);
         } else {
            this.boolean165 = false;
         }

         if (this.on23(this.modeSettingVar15926)) {
            this.Easing(clientplayerentity);
         } else {
            this.set17.clear();
         }

         if (this.on23(this.modeSettingVar15927)) {
            if (clientplayerentity.age % 20 == 0) {
               this.map50.values().removeIf(HudStatusTarget::float206);
            }
         } else {
            this.map50.clear();
         }
      } else {
         this.int303();
      }
   }

   @EventTarget
   public void on23(EventUpdateHealth var1) {
      if (this.on23(this.modeSettingVar15927) && minecraftClient3.world != null) {
         EntityStatusS2CPacket entitystatuss2cpacket = var1.NoDelay();
         if (entitystatuss2cpacket.getStatus() == 35
            && entitystatuss2cpacket.getEntity(minecraftClient3.world) instanceof PlayerEntity playerentity
            && playerentity != minecraftClient3.player) {
            HudStatusTarget li1llil1lllil111il11l_Var160 = this.map50.computeIfAbsent(playerentity.getUuid(), var1x -> new HudStatusTarget(this));
            li1llil1lllil111il11l_Var160.ColorAnimator(playerentity);
            return;
         }
      }
   }

   public void UiAnimation(PlayerEntity var1) {
      StatusEffectInstance statuseffectinstance = var1.getStatusEffect(StatusEffects.STRENGTH);
      if (statuseffectinstance == null) {
         this.boolean165 = false;
      } else {
         int i = statuseffectinstance.getDuration() / 20;
         if (i <= 10 && !this.boolean165) {
            this.boolean165 = true;
            this.UiAnimation("3", this.CloudUserProfile("Сила заканчивается:", i + " сек"), 3500L);
         }

         if (i > 10) {
            this.boolean165 = false;
         }
      }
   }

   public void Easing(PlayerEntity var1) {
      HashSet hashset = new HashSet();

      for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
         ItemStack itemstack = var1.getEquippedStack(slot);
         if (itemstack != null && !itemstack.isEmpty() && itemstack.isDamageable()) {
            String s = itemstack.getItem().getTranslationKey();
            hashset.add(s);
            int i = itemstack.getMaxDamage();
            if (i > 0) {
               int j = i - itemstack.getDamage();
               float f = j * 100.0F / i;
               if (f <= 15.0F && !this.set17.contains(s)) {
                  this.set17.add(s);
                  this.UiAnimation(
                     "3",
                     this.CloudUserProfile("Прочность низкая:", itemstack.getItem().getName().getString() + " • " + String.format("%.1f", f) + "%"),
                     3500L
                  );
               }

               if (f > 15.0F) {
                  this.set17.remove(s);
               }
            }
         }
      }

      this.set17.retainAll(hashset);
   }

   public boolean on23(MultiSelectSetting.Option var1) {
      return var1.isEnabled() && Interface.interfaceField.isEnabled() && Interface.interfaceField.boolean67() && !EffectEngine.double69();
   }

   public void int303() {
      this.boolean165 = false;
      this.set17.clear();
      this.map50.clear();
   }

   public Text CloudUserProfile(String var1, String var2) {
      return this.on23(var1, ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor().call001())
         .append(Text.literal(" "))
         .append(this.on23(var2, ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor().call001()));
   }

   public Text on23(PlayerEntity var1, int var2) {
      return this.on23(var1.getGameProfile().name(), ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001())
         .append(this.on23(" потерял ", ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
         .append(this.on23(var2 + " ", ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
         .append(this.on23(this.ServiceException(var2) + ".", ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor().call001()));
   }

   public MutableText on23(String var1, int var2) {
      return Text.literal(var1).setStyle(Style.EMPTY.withColor(var2));
   }

   public String ServiceException(int var1) {
      int i = var1 % 100;
      if (i >= 11 && i <= 14) {
         return "тотемов";
      }

      return switch (var1 % 10) {
         case 1 -> "тотем";
         case 2, 3, 4 -> "тотема";
         default -> "тотемов";
      };
   }
}
