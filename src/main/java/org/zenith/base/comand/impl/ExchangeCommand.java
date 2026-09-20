package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;

public class ExchangeCommand extends CommandAbstract {
   public static final Pattern EXPECTED = Pattern.compile("(?!)(.*)");
   public static final Pattern GIVE = Pattern.compile("(?!)(.*)");
   public static final Pattern PURCHASED = Pattern.compile("(?!)(.*)");
   public static final int CONFIRM_SLOT = 13;
   public static final int DECREASE_SLOT = 12;
   public static final int INCREASE_SLOT = 14;
   public static final int STUCK_LIMIT = 40;
   public static final int ADJUST_RETRY_INTERVAL = 5;
   public int phase;
   public int ticks;
   public int retryTicks;
   public int targetAmount = -1;
   public int remainingAmount = -1;
   public int lastGive = -1;
   public int lastClickSlot = -1;
   public int capBelowGoal = -1;
   public int stuckTicks;
   public int lastConfirmedGive = -1;
   public int lastReportedExpected = -1;
   public boolean waitingPurchase;

   public ExchangeCommand() {
      super("exc");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Использование: .exc <количество>");
         return 1;
      });
      var1.then(arg("amount", IntegerArgumentType.integer(1)).executes(var1x -> {
         int i = (Integer)var1x.getArgument("amount", Integer.class);
         this.stop();
         this.targetAmount = i;
         this.remainingAmount = i;
         this.send();
         return 1;
      }));
   }

   public void send() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player != null && minecraftclient.player.currentScreenHandler != minecraftclient.player.playerScreenHandler) {
         minecraftclient.player.closeHandledScreen();
      }

      if (minecraftclient.getNetworkHandler() != null) {
         minecraftclient.getNetworkHandler().sendChatCommand("exchange");
      }

      this.phase = 1;
      this.ticks = 0;
      this.waitingPurchase = false;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.resetAdjustState();
   }

   public void resetAdjustState() {
      this.lastGive = -1;
      this.lastClickSlot = -1;
      this.capBelowGoal = -1;
      this.stuckTicks = 0;
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (this.retryTicks > 0 && --this.retryTicks == 0) {
         this.send();
      }

      if (this.phase != 0) {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.player != null && minecraftclient.interactionManager != null) {
            ScreenHandler screenhandler = minecraftclient.player.currentScreenHandler;
            boolean flag = screenhandler != null && screenhandler != minecraftclient.player.playerScreenHandler;
            if (this.phase == 1) {
               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (!flag) {
                  return;
               }

               int i = -1;
               if (screenhandler.isValid(2)) {
                  i = this.parseLore(screenhandler.getSlot(2).getStack(), EXPECTED);
               }

               if (i >= 0 && i != this.lastReportedExpected) {
                  this.lastReportedExpected = i;
                  StyledTextBuilder.RefreshCacheEvent("Ожидается: " + i + " коинов");
               }

               int j = this.goal();
               if (j >= 0 && i >= 0 && j < i) {
                  minecraftclient.interactionManager.clickSlot(screenhandler.syncId, 2, 1, SlotActionType.PICKUP, minecraftclient.player);
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               this.phase = 2;
               this.ticks = 0;
            }

            if (this.phase == 2) {
               if (!flag) {
                  this.stop();
                  return;
               }

               if (this.isBuyScreen(minecraftclient)) {
                  this.phase = 3;
                  this.ticks = 0;
                  this.resetAdjustState();
                  return;
               }

               if (++this.ticks > 100) {
                  this.stop();
                  return;
               }

               if (this.ticks % 10 == 1) {
                  minecraftclient.interactionManager.clickSlot(screenhandler.syncId, 2, 0, SlotActionType.PICKUP, minecraftclient.player);
               }
            }

            if (this.phase == 3) {
               if (++this.ticks > 200) {
                  this.stop();
               } else if (flag && this.isBuyScreen(minecraftclient)) {
                  int k = -1;
                  if (screenhandler.isValid(13)) {
                     k = this.parseLore(screenhandler.getSlot(13).getStack(), GIVE);
                  }

                  if (k >= 0) {
                     int l = this.goal();
                     if (k == l) {
                        this.confirmPurchase(minecraftclient, screenhandler, k);
                     } else {
                        boolean flag1 = k != this.lastGive;
                        if (flag1) {
                           if (!this.onGiveChanged(k, l)) {
                              return;
                           }
                        } else {
                           this.stuckTicks++;
                        }

                        if (k > l) {
                           if (this.stuckTicks > 40) {
                              StyledTextBuilder.RotationLegitStrategy("Exchange: не удалось уменьшить количество до " + l + ", отмена");
                              this.stop();
                           } else if (this.shouldAdjust(flag1)) {
                              this.adjust(minecraftclient, screenhandler, 12, k);
                           }
                        } else if (k > 0 && this.capBelowGoal >= 0 && k <= l && k >= this.capBelowGoal) {
                           this.confirmPurchase(minecraftclient, screenhandler, k);
                        } else if (this.stuckTicks > 40) {
                           if (k > 0) {
                              this.confirmPurchase(minecraftclient, screenhandler, k);
                           } else {
                              this.stop();
                           }
                        } else if (this.shouldAdjust(flag1)) {
                           this.adjust(minecraftclient, screenhandler, 14, k);
                        }
                     }
                  }
               }
            } else if (this.phase == 4) {
               this.ticks++;
               boolean flag2 = flag && this.isBuyScreen(minecraftclient);
               if (!flag2 && this.ticks > 10) {
                  this.onPurchased(this.lastConfirmedGive);
                  return;
               }

               if (this.ticks > 200) {
                  StyledTextBuilder.RotationLegitStrategy("Exchange: нет подтверждения покупки, отмена");
                  this.stop();
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && this.waitingPurchase) {
         String s = this.packetText(var1.ItemScroller());
         if (s != null) {
            Matcher matcher = PURCHASED.matcher(s);
            if (matcher.find()) {
               int i;
               try {
                  i = Integer.parseInt(matcher.group(1).replaceAll("\\s", ""));
               } catch (NumberFormatException numberformatexception) {
                  return;
               }

               this.onPurchased(i);
            }
         }
      }
   }

   public boolean onGiveChanged(int var1, int var2) {
      if (this.lastGive >= 0) {
         if (this.lastClickSlot == 12 && var1 > this.lastGive) {
            StyledTextBuilder.RotationLegitStrategy("Exchange: кнопка уменьшения увеличивает количество, отмена");
            this.stop();
            return false;
         }

         if (this.lastClickSlot == 14 && var1 > var2 && this.lastGive <= var2) {
            this.capBelowGoal = this.lastGive;
         }
      }

      this.stuckTicks = 0;
      return true;
   }

   public boolean shouldAdjust(boolean var1) {
      return var1 || this.stuckTicks > 0 && this.stuckTicks % 5 == 0;
   }

   public void adjust(MinecraftClient var1, ScreenHandler var2, int var3, int var4) {
      var1.interactionManager.clickSlot(var2.syncId, var3, 0, SlotActionType.PICKUP, var1.player);
      this.lastClickSlot = var3;
      this.lastGive = var4;
   }

   public void confirmPurchase(MinecraftClient var1, ScreenHandler var2, int var3) {
      this.waitingPurchase = true;
      this.lastConfirmedGive = var3;
      this.phase = 4;
      this.ticks = 0;
      var1.interactionManager.clickSlot(var2.syncId, 13, 0, SlotActionType.PICKUP, var1.player);
   }

   public void onPurchased(int var1) {
      if (this.phase != 0 || this.waitingPurchase) {
         this.waitingPurchase = false;
         this.phase = 0;
         int i = this.lastConfirmedGive > 0 ? this.lastConfirmedGive : var1;
         this.lastConfirmedGive = -1;
         if (i <= 0) {
            StyledTextBuilder.RotationLegitStrategy("Exchange: не удалось определить количество покупки, отмена");
            this.stop();
         } else {
            this.remainingAmount -= i;
            if (this.remainingAmount <= 0) {
               StyledTextBuilder.RefreshCacheEvent("Закупка завершена");
               this.stop();
            } else {
               StyledTextBuilder.RefreshCacheEvent("Осталось закупить: " + this.remainingAmount);
               this.retryTicks = 10;
            }
         }
      }
   }

   public String packetText(Packet<?> var1) {
      if (var1 instanceof GameMessageS2CPacket gamemessages2cpacket) {
         return gamemessages2cpacket.content().getString();
      } else {
         return var1 instanceof ProfilelessChatMessageS2CPacket profilelesschatmessages2cpacket ? profilelesschatmessages2cpacket.message().getString() : null;
      }
   }

   public int goal() {
      return this.remainingAmount >= 0 ? this.remainingAmount : this.targetAmount;
   }

   public void stop() {
      this.phase = 0;
      this.ticks = 0;
      this.retryTicks = 0;
      this.targetAmount = -1;
      this.remainingAmount = -1;
      this.lastConfirmedGive = -1;
      this.lastReportedExpected = -1;
      this.waitingPurchase = false;
      this.resetAdjustState();
   }

   public boolean isBuyScreen(MinecraftClient var1) {
      return var1.currentScreen != null && var1.currentScreen.getTitle().getString().contains("Покупка");
   }

   public int parseLore(ItemStack var1, Pattern var2) {
      if (var1.isEmpty()) {
         return -1;
      }

      LoreComponent lorecomponent = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (lorecomponent == null) {
         return -1;
      }

      for (Text text : lorecomponent.lines()) {
         Matcher matcher = var2.matcher(text.getString());
         if (matcher.find()) {
            try {
               return Integer.parseInt(matcher.group(1).replaceAll("\\s", ""));
            } catch (NumberFormatException numberformatexception) {
               return -1;
            }
         }
      }

      return -1;
   }
}
