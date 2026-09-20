package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.CachedProfile;
import org.zenith.core.ProfileCacheStore;
import org.zenith.core.SlotRenderRule;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;

public class SortCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int INVENTORY_SIZE = 36;
   public static final int MAX_PACKETS_PER_TICK = 10;
   public String activeLayoutName;
   public SortCommand_SortTask sortTask;
   public final SuggestionProvider<CommandSource> layoutSuggestions = (var1, var2) -> CommandSource.suggestMatching(
      this.getSortManager().abstractClientPlayerEntity().stream().map(CachedProfile::getName), var2
   );

   public SortCommand() {
      super("sort");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Использование: .sort <save/load/list/delete>");
         return 1;
      });
      var1.then(literal("save").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.save(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      var1.then(((LiteralArgumentBuilder)literal("load").executes(var1x -> {
         this.load(null);
         return 1;
      })).then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.load(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
      var1.then(literal("list").executes(var1x -> {
         this.list();
         return 1;
      }));
      var1.then(literal("delete").then(arg("name", StringArgumentType.word()).suggests(this.layoutSuggestions).executes(var1x -> {
         this.delete(StringArgumentType.getString(var1x, "name"));
         return 1;
      })));
   }

   public void save(String var1) {
      if (this.isInvalidName(var1)) {
         StyledTextBuilder.RotationLegitStrategy("Имя может содержать только буквы, цифры, '.', '_' и '-'");
      } else {
         ClientPlayerEntity clientplayerentity = this.getAvailablePlayer();
         if (clientplayerentity != null) {
            ArrayList arraylist = new ArrayList();
            PlayerInventory playerinventory = clientplayerentity.getInventory();

            for (int i = 0; i < 36; i++) {
               ItemStack itemstack = playerinventory.getStack(i);
               if (!itemstack.isEmpty()) {
                  arraylist.add(
                     new SlotRenderRule(
                        i, Registries.ITEM.getId(itemstack.getItem()).toString(), itemstack.hasEnchantments(), this.getCustomDataKeys(itemstack)
                     )
                  );
               }
            }

            if (this.getSortManager().on23(var1, arraylist)) {
               this.activeLayoutName = var1;
               StyledTextBuilder.RefreshCacheEvent("Расположение инвентаря '" + var1 + "' сохранено");
            } else {
               StyledTextBuilder.RotationLegitStrategy("Не удалось сохранить расположение '" + var1 + "'");
            }
         }
      }
   }

   public void load(String var1) {
      ClientPlayerEntity clientplayerentity = this.getAvailablePlayer();
      if (clientplayerentity != null) {
         if (!clientplayerentity.currentScreenHandler.getCursorStack().isEmpty()) {
            StyledTextBuilder.RotationLegitStrategy("Освободи курсор перед сортировкой инвентаря");
         } else {
            CachedProfile lll111iiili1il1l1ill1il1l_l1i1illlili = this.findLayout(var1);
            if (lll111iiili1il1l1ill1il1l_l1i1illlili == null) {
               String s = var1 == null ? "" : " '" + var1 + "'";
               StyledTextBuilder.RotationLegitStrategy("Сохраненное расположение" + s + " не найдено");
            } else {
               List<SlotRenderRule> list = lll111iiili1il1l1ill1il1l_l1i1illlili.WorldTweaks()
                  .stream()
                  .filter(var0 -> var0.getSlot() >= 0 && var0.getSlot() < 36)
                  .sorted(Comparator.comparingInt(SlotRenderRule::getSlot))
                  .toList();
               this.activeLayoutName = lll111iiili1il1l1ill1il1l_l1i1illlili.getName();
               this.sortTask = new SortCommand_SortTask(lll111iiili1il1l1ill1il1l_l1i1illlili.getName(), clientplayerentity, list);
               StyledTextBuilder.RefreshCacheEvent("Сортировка '" + lll111iiili1il1l1ill1il1l_l1i1illlili.getName() + "' запущена");
            }
         }
      }
   }

   public void list() {
      List<CachedProfile> list = this.getSortManager()
         .abstractClientPlayerEntity()
         .stream()
         .sorted(Comparator.comparing(CachedProfile::getName, String.CASE_INSENSITIVE_ORDER))
         .toList();
      if (list.isEmpty()) {
         StyledTextBuilder.RotationLegitStrategy("Сохраненных расположений нет");
      } else {
         StyledTextBuilder.RefreshCacheEvent("Сохраненные расположения (" + list.size() + "):");
         list.forEach(var0 -> StyledTextBuilder.RefreshCacheEvent("- " + var0.getName()));
      }
   }

   public void delete(String var1) {
      ProfileCacheStore lll111iiili1il1l1ill1il1l = this.getSortManager();
      CachedProfile lll111iiili1il1l1ill1il1l_l1i1illlili = lll111iiili1il1l1ill1il1l.EventWindowSizeChanged(var1);
      if (lll111iiili1il1l1ill1il1l_l1i1illlili == null) {
         StyledTextBuilder.RotationLegitStrategy("Сохраненное расположение '" + var1 + "' не найдено");
      } else if (!lll111iiili1il1l1ill1il1l.delete(var1)) {
         StyledTextBuilder.RotationLegitStrategy("Не удалось удалить расположение '" + var1 + "'");
      } else {
         if (this.sortTask != null && this.sortTask.name.equalsIgnoreCase(var1)) {
            this.sortTask = null;
         }

         if (this.activeLayoutName != null && this.activeLayoutName.equalsIgnoreCase(var1)) {
            this.activeLayoutName = null;
         }

         StyledTextBuilder.RefreshCacheEvent("Расположение '" + lll111iiili1il1l1ill1il1l_l1i1illlili.getName() + "' удалено");
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      SortCommand_SortTask sortcommand_sorttask = this.sortTask;
      if (sortcommand_sorttask != null) {
         ClientPlayerEntity clientplayerentity = minecraftClient3.player;
         ClientPlayerInteractionManager clientplayerinteractionmanager = minecraftClient3.interactionManager;
         if (clientplayerentity == null
            || clientplayerentity != sortcommand_sorttask.player
            || clientplayerinteractionmanager == null
            || clientplayerentity.currentScreenHandler != clientplayerentity.playerScreenHandler) {
            this.cancelSort("Сортировка остановлена: инвентарь игрока недоступен");
         } else if (!clientplayerentity.currentScreenHandler.getCursorStack().isEmpty()) {
            this.cancelSort("Сортировка остановлена: на курсоре находится предмет");
         } else {
            int i = 0;
            PlayerInventory playerinventory = clientplayerentity.getInventory();

            while (sortcommand_sorttask.itemIndex < sortcommand_sorttask.desiredItems.size()) {
               SlotRenderRule lll111iiili1il1l1ill1il1l_ii1il11l111ii11iil = sortcommand_sorttask.desiredItems.get(sortcommand_sorttask.itemIndex);
               int j = lll111iiili1il1l1ill1il1l_ii1il11l111ii11iil.getSlot();
               if (this.matchesItem(playerinventory.getStack(j), lll111iiili1il1l1ill1il1l_ii1il11l111ii11iil)) {
                  sortcommand_sorttask.fixedSlots.add(j);
                  sortcommand_sorttask.itemIndex++;
               } else {
                  int k = this.findMatchingSlot(playerinventory, lll111iiili1il1l1ill1il1l_ii1il11l111ii11iil, j, sortcommand_sorttask.fixedSlots);
                  if (k == -1) {
                     sortcommand_sorttask.missing++;
                     sortcommand_sorttask.itemIndex++;
                  } else {
                     int l = this.getSwapPacketCount(k, j);
                     if (i + l > 10) {
                        break;
                     }

                     int i1 = this.swapSlots(clientplayerentity, clientplayerinteractionmanager, k, j);
                     i += i1;
                     sortcommand_sorttask.sentPackets += i1;
                     sortcommand_sorttask.fixedSlots.add(j);
                     sortcommand_sorttask.moved++;
                     sortcommand_sorttask.itemIndex++;
                  }
               }
            }

            if (sortcommand_sorttask.itemIndex >= sortcommand_sorttask.desiredItems.size()) {
               this.finishSort(sortcommand_sorttask);
            }
         }
      }
   }

   public boolean matchesItem(ItemStack var1, SlotRenderRule var2) {
      if (var1.isEmpty()) {
         return false;
      } else if (!var2.BoxShaderRenderer().equals(Registries.ITEM.getId(var1.getItem()).toString())) {
         return false;
      } else {
         return var2.customDrawContext() != var1.hasEnchantments() ? false : var2.float22().equals(this.getCustomDataKeys(var1));
      }
   }

   public Set<String> getCustomDataKeys(ItemStack var1) {
      NbtComponent nbtcomponent = (NbtComponent)var1.get(DataComponentTypes.CUSTOM_DATA);
      return nbtcomponent == null ? Set.of() : Set.copyOf(nbtcomponent.copyNbt().getKeys());
   }

   public int findMatchingSlot(PlayerInventory var1, SlotRenderRule var2, int var3, Set<Integer> var4) {
      int i = -1;
      int j = Integer.MAX_VALUE;

      for (int k = 0; k < 36; k++) {
         if (!var4.contains(k) && this.matchesItem(var1.getStack(k), var2)) {
            int l = this.getSwapPacketCount(k, var3);
            if (l < j) {
               i = k;
               j = l;
            }
         }
      }

      return i;
   }

   public int swapSlots(ClientPlayerEntity var1, ClientPlayerInteractionManager var2, int var3, int var4) {
      int i = this.toHandlerSlot(var3);
      int j = this.toHandlerSlot(var4);
      int k = var1.playerScreenHandler.syncId;
      if (this.isHotbarSlot(var4)) {
         var2.clickSlot(k, i, var4, SlotActionType.SWAP, var1);
         return 1;
      } else if (this.isHotbarSlot(var3)) {
         var2.clickSlot(k, j, var3, SlotActionType.SWAP, var1);
         return 1;
      } else {
         int l = var1.getInventory().selectedSlot;
         var2.clickSlot(k, i, l, SlotActionType.SWAP, var1);
         var2.clickSlot(k, j, l, SlotActionType.SWAP, var1);
         var2.clickSlot(k, i, l, SlotActionType.SWAP, var1);
         return 3;
      }
   }

   public int getSwapPacketCount(int var1, int var2) {
      return !this.isHotbarSlot(var1) && !this.isHotbarSlot(var2) ? 3 : 1;
   }

   public boolean isHotbarSlot(int var1) {
      return var1 >= 0 && var1 < 9;
   }

   public int toHandlerSlot(int var1) {
      return var1 < 9 ? 36 + var1 : var1;
   }

   public CachedProfile findLayout(String var1) {
      ProfileCacheStore lll111iiili1il1l1ill1il1l = this.getSortManager();
      if (var1 != null) {
         return lll111iiili1il1l1ill1il1l.EventWindowSizeChanged(var1);
      }

      if (this.activeLayoutName != null) {
         CachedProfile lll111iiili1il1l1ill1il1l_l1i1illlili = lll111iiili1il1l1ill1il1l.EventWindowSizeChanged(this.activeLayoutName);
         if (lll111iiili1il1l1ill1il1l_l1i1illlili != null) {
            return lll111iiili1il1l1ill1il1l_l1i1illlili;
         }
      }

      return lll111iiili1il1l1ill1il1l.abstractClientPlayerEntity().stream().max(Comparator.comparingLong(CachedProfile::getVar125)).orElse(null);
   }

   public ProfileCacheStore getSortManager() {
      return ZenithClient.on23().getSortManager();
   }

   public ClientPlayerEntity getAvailablePlayer() {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      if (clientplayerentity == null || minecraftClient3.world == null || minecraftClient3.interactionManager == null) {
         StyledTextBuilder.RotationLegitStrategy("Сначала зайди в мир");
         return null;
      } else if (clientplayerentity.currentScreenHandler != clientplayerentity.playerScreenHandler) {
         StyledTextBuilder.RotationLegitStrategy("Закрой открытый контейнер перед сортировкой");
         return null;
      } else {
         return clientplayerentity;
      }
   }

   public boolean isInvalidName(String var1) {
      return var1 == null || var1.equals(".") || var1.equals("..") || !var1.matches("[\\p{L}\\p{N}._-]+");
   }

   public void finishSort(SortCommand_SortTask var1) {
      this.sortTask = null;
      if (var1.missing == 0) {
         StyledTextBuilder.RefreshCacheEvent("Расположение '" + var1.name + "' загружено, перемещено: " + var1.moved + ", пакетов: " + var1.sentPackets);
      } else {
         StyledTextBuilder.RotationLegitStrategy(
            "Расположение '"
               + var1.name
               + "' загружено частично: перемещено "
               + var1.moved
               + ", отсутствует предметов: "
               + var1.missing
               + ", пакетов: "
               + var1.sentPackets
         );
      }
   }

   public void cancelSort(String var1) {
      this.sortTask = null;
      StyledTextBuilder.RotationLegitStrategy(var1);
   }
}
