package org.zenith.module.movement;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.TaskQueueWorker;
import org.zenith.event.CloseScreenEvent;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.GuiWalkEvent;
import org.zenith.event.EventClick;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PacketEvent;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.MovementUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "Gui Walk", category = Category.MOVEMENT, description = "Можно ходить в инвентаре или контейнере")
public final class GuiWalk extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final GuiWalk guiWalk = new GuiWalk();
   public final ModeSetting mode9 = new ModeSetting("module.guiWalk.mode", "module.guiWalk.mode.desc");
   public final ModeSetting.Option modeSetting3Var15936 = new ModeSetting.Option(this.mode9, "module.guiWalk.mode.visual").int210();
   public final ModeSetting.Option modeSetting3Var15937 = new ModeSetting.Option(this.mode9, "module.guiWalk.mode.grim");
   public final ModeSetting.Option modeSetting3Var15938 = new ModeSetting.Option(this.mode9, "HolyWorld");
   public final BooleanSetting cameraLock = new BooleanSetting(
      "module.guiWalk.cameraLock", "module.guiWalk.cameraLock.desc", true, this.modeSetting3Var15937::isSelected
   );
   public SlotActionType slotActionType = null;
   public final List<Packet<?>> list16 = new ArrayList<>();
   boolean val172 = false;
   boolean val122 = false;
   boolean val092 = false;

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (!this.modeSetting3Var15938.isSelected()) {
         try {
            if (EffectEngine.double69()) {
               return;
            }

            if (minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
               if (!this.val172) {
                  return;
               }

               this.val172 = false;
               if (Objects.requireNonNull(var1.ItemScroller()) instanceof ClickSlotC2SPacket clickslotc2spacket
                  && (!this.list16.isEmpty() || MovementUtils.double64())
                  && TaskScheduler.call203()) {
                  this.list16.add(clickslotc2spacket);
                  this.slotActionType = clickslotc2spacket.actionType();
                  var1.setCancelled(true);
               }
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (TaskScheduler.call203()
            && (!this.modeSetting3Var15936.isSelected() || !this.val122)
            && !this.val092
            && (!this.list16.isEmpty() || minecraftClient3.player.currentScreenHandler.getCursorStack().isEmpty())) {
            TaskScheduler.call204();
         }
      } else {
         this.val122 = false;
         this.val092 = false;
      }
   }

   @EventTarget
   public void on23(EventClick var1) {
      if (var1.PricedItem() >= 0) {
         Slot slot = minecraftClient3.player.currentScreenHandler.getSlot(var1.PricedItem());
         ItemStack itemstack = slot.getStack();
         if (itemstack.get(DataComponentTypes.CUSTOM_DATA) != null) {
         }
      }

      if (!this.modeSetting3Var15938.isSelected() && minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
         SlotActionType slotactiontype = var1.HeldItemWatcher();
         if (this.modeSetting3Var15936.isSelected()) {
            if (MovementUtils.double64()) {
               var1.setCancelled(true);
            } else {
               this.val122 = true;
            }
         }

         if (this.list16.isEmpty() && !MovementUtils.double64()) {
            this.val092 = true;
         } else if (var1.ContainerScanner() == 1 && slotactiontype.equals(SlotActionType.PICKUP)
            || this.slotActionType == SlotActionType.PICKUP && var1.HeldItemWatcher() == SlotActionType.QUICK_MOVE
            || this.slotActionType == SlotActionType.QUICK_MOVE && var1.HeldItemWatcher() == SlotActionType.PICKUP
            || this.slotActionType == SlotActionType.QUICK_MOVE && var1.HeldItemWatcher() == SlotActionType.PICKUP_ALL
            || this.slotActionType == SlotActionType.PICKUP_ALL && var1.HeldItemWatcher() == SlotActionType.QUICK_MOVE) {
            var1.setCancelled(true);
         } else {
            this.val172 = true;
         }
      }
   }

   @EventTarget
   public void on23(CloseScreenEvent var1) {
      if (!this.modeSetting3Var15938.isSelected() && minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (this.modeSetting3Var15936.isSelected()) {
            if (!this.val122) {
               var1.setCancelled(true);
            }

            this.val122 = false;
         } else if (this.val092 && this.list16.isEmpty()) {
            this.val092 = false;
         } else {
            this.val092 = false;
            if (MovementUtils.double64() || !this.list16.isEmpty()) {
               var1.setCancelled(true);
            }

            if (!this.list16.isEmpty()) {
               this.slotActionType = null;
               this.val172 = false;
               List<Packet<?>> arraylist = new ArrayList<>(this.list16);
               this.list16.clear();
               TaskQueueWorker ll1ill11111i_l1i1illlili = new TaskQueueWorker(GuiWalk.class);
               if (InventorySetting.inventorySetting.call099()) {
                  ll1ill11111i_l1i1illlili.on23(
                     MovementInputEvent.class,
                     var1x -> {
                        var1x.NoSlow();
                        if (!minecraftClient3.player.lastPlayerInput.jump()
                           && !minecraftClient3.player.isSprinting()
                           && !minecraftClient3.player.lastPlayerInput.forward()
                           && !minecraftClient3.player.lastPlayerInput.backward()
                           && !minecraftClient3.player.lastPlayerInput.left()
                           && !minecraftClient3.player.lastPlayerInput.right()) {
                           arraylist.forEach(var0x -> minecraftClient3.getNetworkHandler().sendPacket(var0x));
                           ScreenUtils.closeScreen();
                           return true;
                        } else {
                           return false;
                        }
                     }
                  );
               } else {
                  for (int i = 0; i < arraylist.size(); i++) {
                     Packet<?> packet = arraylist.get(i);
                     ll1ill11111i_l1i1illlili.on23(
                        GuiWalkEvent.class,
                        var1x -> {
                           if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                              ScreenUtils.closeScreen();
                           }

                           if (!minecraftClient3.player.lastPlayerInput.jump()
                              && !minecraftClient3.player.isSprinting()
                              && !minecraftClient3.player.lastPlayerInput.forward()
                              && !minecraftClient3.player.lastPlayerInput.backward()
                              && !minecraftClient3.player.lastPlayerInput.left()
                              && !minecraftClient3.player.lastPlayerInput.right()) {
                              minecraftClient3.getNetworkHandler().sendPacket(packet);
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                  }

                  ll1ill11111i_l1i1illlili.on23(
                     MovementInputEvent.class,
                     var0 -> {
                        if (!minecraftClient3.player.lastPlayerInput.jump()
                           && !minecraftClient3.player.isSprinting()
                           && !minecraftClient3.player.lastPlayerInput.forward()
                           && !minecraftClient3.player.lastPlayerInput.backward()
                           && !minecraftClient3.player.lastPlayerInput.left()
                           && !minecraftClient3.player.lastPlayerInput.right()) {
                           ScreenUtils.closeScreen();
                           return true;
                        } else {
                           return false;
                        }
                     }
                  );
                  ll1ill11111i_l1i1illlili.UiAnimation(MovementInputEvent.class, var0 -> {
                     var0.NoSlow();
                     return true;
                  });
                  if (this.cameraLock.isEnabled()) {
                     ll1ill11111i_l1i1illlili.UiAnimation(RotationUpdateStartEvent.class, var1x -> {
                        val002.on23(new RotationTask(val002.LineShader(), val002::LineShader, val002.int150().HudPreviewItem()), 999, this, 1);
                        return true;
                     });
                  }
               }

               ZenithClient.on23().FileLogger().on23(ll1ill11111i_l1i1illlili);
            }
         }
      }
   }
}
