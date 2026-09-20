package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

@Deprecated
public class HotbarSwapper {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static int TextScanner(Item var0) {
      return on23(var0, 0, 35);
   }

   public static int NbtItemSpec(Item var0) {
      return on23(var0, 0, 8);
   }

   public static int EnchantItemSpec(Item var0) {
      return on23(var0, 9, 35);
   }

   public static int on23(Item var0, int var1, int var2) {
      for (int i = var2; i >= var1; i--) {
         if (minecraftClient3.player.getInventory().getStack(i).getItem() == var0) {
            return i;
         }
      }

      return -1;
   }

   public static int PotionItemBuilder(int var0, int var1) {
      for (int i = var1; i >= var0; i--) {
         if (minecraftClient3.player.getInventory().getStack(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public static void on23(SwapPhase var0, int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         switch (var0) {
            case val417:
               minecraftClient3.player.getInventory().selectedSlot = var1;
               break;
            case val418:
               minecraftClient3.player.getInventory().selectedSlot = var1;
               PacketDispatcher.sendPacket(new UpdateSelectedSlotC2SPacket(var1));
               break;
            case val419:
               ProfileItemBuilder(var1, var2);
         }
      }
   }

   public static void UiAnimation(SwapPhase var0, int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         switch (var0) {
            case val417:
               minecraftClient3.player.getInventory().selectedSlot = var2;
               break;
            case val418:
               minecraftClient3.player.getInventory().selectedSlot = var2;
               PacketDispatcher.sendPacket(new UpdateSelectedSlotC2SPacket(var2));
               break;
            case val419:
               ProfileItemBuilder(var1, var2);
         }
      }
   }

   public static void ProfileItemBuilder(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, var0, 0, SlotActionType.PICKUP, minecraftClient3.player);
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, var1, 0, SlotActionType.PICKUP, minecraftClient3.player);
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, var0, 0, SlotActionType.PICKUP, minecraftClient3.player);
      }
   }

   public static void swap(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, Event05(var0), 0, SlotActionType.PICKUP, minecraftClient3.player);
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, Event05(var1), 0, SlotActionType.PICKUP, minecraftClient3.player);
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, Event05(var0), 0, SlotActionType.PICKUP, minecraftClient3.player);
      }
   }

   public static void StringCodec(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         swap(var0, var1);
      }
   }

   public static int Event05(int var0) {
      return var0 >= 0 && var0 <= 8 ? 36 + var0 : var0;
   }

   public static void on23(SwapHand var0) {
      switch (var0) {
         case val506:
            minecraftClient3.player.swingHand(Hand.MAIN_HAND);
            break;
         case val507:
            minecraftClient3.player.swingHand(Hand.OFF_HAND);
            break;
         case val508:
            PacketDispatcher.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
      }
   }
}
