package org.zenith.module.misc;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventClick;
import org.zenith.event.EventRender;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ScreenUtils;
import org.zenith.util.StopWatch;

@ModuleInfo(name = "ItemScroller", description = "Перемещение преметов без задержки", category = Category.MISC)
public final class ItemScroller extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ItemScroller itemScroller = new ItemScroller();
   public final NumberSetting scrollerSetting = new NumberSetting(
      "module.itemScroller.scrollerSetting", 100.0F, 0.0F, 200.0F, 10.0F, "module.itemScroller.scrollerSetting.desc", "ms"
   );
   public final StopWatch stopWatch2 = new StopWatch();

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   public void on23(EventRender var1) {
      Slot slot = var1.AutoLoot();
      SlotActionType slotactiontype = EffectEngine.on23(minecraftClient3.options.dropKey.getDefaultKey())
         ? SlotActionType.THROW
         : (EffectEngine.on23(minecraftClient3.options.attackKey.getDefaultKey()) ? SlotActionType.QUICK_MOVE : null);
      if (this.call413()
         && ZenithClient.on23().FileLogger().ImageEncoder()
         && !this.call104()
         && slot != null
         && slot.hasStack()
         && slotactiontype != null
         && this.stopWatch2.ServiceException(this.scrollerSetting.getCurrent())) {
         EventClick ili11ii1l1ill1lllil1 = new EventClick(
            minecraftClient3.player.currentScreenHandler.syncId, slot.id, slotactiontype.equals(SlotActionType.THROW) ? 1 : 0, slotactiontype
         );
         EventManager.call(ili11ii1l1ill1lllil1);
         if (!ili11ii1l1ill1lllil1.isCancelled()) {
            minecraftClient3.interactionManager
               .clickSlot(
                  minecraftClient3.player.currentScreenHandler.syncId,
                  slot.id,
                  slotactiontype.equals(SlotActionType.THROW) ? 1 : 0,
                  slotactiontype,
                  minecraftClient3.player
               );
         }
      }
   }

   @EventTarget
   public void on23(EventClick var1) {
      int i = var1.PricedItem();
      if (i >= 0 && i <= minecraftClient3.player.currentScreenHandler.slots.size()) {
         Slot slot = minecraftClient3.player.currentScreenHandler.getSlot(i);
         Item item = slot.getStack().getItem();
         if (item != null
            && ZenithClient.on23().FileLogger().ImageEncoder()
            && minecraftClient3.currentScreen != null
            && this.call104()
            && var1.HeldItemWatcher() != SlotActionType.THROW
            && var1.HeldItemWatcher() != SlotActionType.SWAP
            && this.stopWatch2.ServiceException(100.0)) {
            ScreenUtils.call006()
               .filter(var2x -> var2x.getStack().getItem().equals(item) && var2x.inventory.equals(slot.inventory))
               .forEach(
                  var1xx -> {
                     EventClick ili11ii1l1ill1lllil1 = new EventClick(
                        minecraftClient3.player.currentScreenHandler.syncId, var1xx.id, 1, var1.HeldItemWatcher()
                     );
                     EventManager.call(ili11ii1l1ill1lllil1);
                     if (!ili11ii1l1ill1lllil1.isCancelled()) {
                        minecraftClient3.interactionManager
                           .clickSlot(
                              minecraftClient3.player.currentScreenHandler.syncId, var1xx.id, 1, var1.HeldItemWatcher(), minecraftClient3.player
                           );
                     }
                  }
               );
         }
      }
   }

   public boolean call413() {
      return EffectEngine.on23(minecraftClient3.options.sneakKey.getDefaultKey());
   }

   public boolean call104() {
      return EffectEngine.on23(minecraftClient3.options.sprintKey.getDefaultKey());
   }
}
