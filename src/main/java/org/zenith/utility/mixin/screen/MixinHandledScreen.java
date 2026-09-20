package org.zenith.utility.mixin.screen;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.core.DrawContextSink;
import org.zenith.event.EventClick;
import org.zenith.event.EventRender;
import org.zenith.module.player.AHHelper;
import org.zenith.module.misc.AutoInventory;
import org.zenith.module.render.BetterMinecraft;
import org.zenith.module.misc.ContainerHelper;
import org.zenith.util.ItemCountUtils;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen implements DrawContextSink {
   @Unique
   @Mutable
   public boolean isAuc;
   @Unique
   @Mutable
   public Slot lowSumSlotId = null;
   @Unique
   @Mutable
   public Slot lowAllSumSlotId = null;
   @Unique
   public int zenith_lastScanRevision = -1;
   @Unique
   public long zenith_openedAt = System.currentTimeMillis();
   @Unique
   public long zenith_closedAt;
   @Unique
   public float zenith_closeStartScale = 1.0F;
   @Unique
   public boolean zenith_closingAnimation;
   @Unique
   public boolean zenith_forceClose;
   @Unique
   public boolean zenith_scaleApplied;
   @Shadow
   @Final
   protected ScreenHandler handler;
   @Shadow
   protected int y;
   @Shadow
   protected int x;
   @Shadow
   protected int backgroundWidth;
   @Shadow
   protected int backgroundHeight;
   @Shadow
   @Nullable
   protected Slot focusedSlot;

   protected MixinHandledScreen(Text var1) {
      super(var1);
   }

   @Shadow
   public abstract ScreenHandler getScreenHandler();

   @Inject(method = "init", at = @At("HEAD"))
   public void zenith_initScaleAnimation(CallbackInfo var1) {
      this.zenith_openedAt = System.currentTimeMillis();
      this.zenith_closedAt = 0L;
      this.zenith_closeStartScale = 1.0F;
      this.zenith_closingAnimation = false;
      this.zenith_forceClose = false;
      this.zenith_scaleApplied = false;
   }

   @Inject(
      method = "renderBackground",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V")
   )
   public void zenith_applyBetterMinecraftScale(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.zenith_scaleApplied = false;
      if (this.zenith_shouldAnimateBetterMinecraft()) {
         float f = this.zenith_getScale();
         if (!(Math.abs(f - 1.0F) < 0.001F)) {
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(this.width / 2.0F, this.height / 2.0F);
            var1.getMatrices().scale(f, f);
            var1.getMatrices().translate(-this.width / 2.0F, -this.height / 2.0F);
            this.zenith_scaleApplied = true;
         }
      }
   }

   @Inject(method = "close", at = @At("HEAD"), cancellable = true)
   public void zenith_closeWithBetterMinecraftAnimation(CallbackInfo var1) {
      if (!this.zenith_forceClose && this.zenith_shouldAnimateBetterMinecraft()) {
         this.zenith_startClosingAnimation();
         var1.cancel();
      }
   }

   @Inject(method = "tick", at = @At("HEAD"))
   public void tickScreen(CallbackInfo var1) {
      try {
         if (!AHHelper.aHHelper.isEnabled()) {
            this.isAuc = false;
            this.lowSumSlotId = null;
            this.lowAllSumSlotId = null;
            this.zenith_lastScanRevision = -1;
            return;
         }

         int i = this.handler.getRevision();
         if (i == this.zenith_lastScanRevision) {
            return;
         }

         this.zenith_lastScanRevision = i;
         this.isAuc = ItemCountUtils.ProfileItemBuilder(this.handler);
         this.lowSumSlotId = null;
         this.lowAllSumSlotId = null;
         if (this.isAuc) {
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;

            for (int l = 0; l < 44; l++) {
               Slot slot = (Slot)this.getScreenHandler().slots.get(l);
               if (!slot.getStack().isEmpty() && !ItemCountUtils.ModuleToggleEvent(slot.getStack())) {
                  int i1 = ItemCountUtils.EventInjectHandleInputEvents(slot.getStack());
                  if (i1 != Integer.MAX_VALUE) {
                     if (i1 < j) {
                        this.lowSumSlotId = slot;
                        j = i1;
                     }

                     int j1 = ItemCountUtils.EventModifyMouseRotationInput(slot.getStack());
                     if (j1 != Integer.MAX_VALUE && j1 < k) {
                        k = j1;
                        this.lowAllSumSlotId = slot;
                     }
                  }
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Inject(
      method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/screen/slot/SlotActionType;)V"
      ),
      cancellable = true
   )
   public void onClick(Slot var1, int var2, int var3, SlotActionType var4, CallbackInfo var5) {
      EventClick ili11ii1l1ill1lllil1 = new EventClick(this.handler.syncId, var2, var3, var4);
      EventManager.call(ili11ii1l1ill1lllil1);
      if (ili11ii1l1ill1lllil1.isCancelled()) {
         var5.cancel();
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void zenith_renderAuctionHighlights(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.isAuc && AHHelper.aHHelper.isEnabled()) {
         if (this.lowSumSlotId != null) {
            AHHelper.aHHelper.on23(var1, this.x, this.y, this.lowSumSlotId, true);
         }

         if (this.lowAllSumSlotId != null && this.lowAllSumSlotId != this.lowSumSlotId) {
            AHHelper.aHHelper.on23(var1, this.x, this.y, this.lowAllSumSlotId, false);
         }
      }
   }

   @Inject(method = "init", at = @At("RETURN"))
   public void injectInit(CallbackInfo var1) {
      try {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (AutoInventory.autoInventory.isEnabled()) {
            this.addDrawableChild(
               ButtonWidget.builder(
                     Text.of("AutoSbor: " + AutoInventory.autoInventory.isEnabled()), var0 -> AutoInventory.autoInventory.toggle()
                  )
                  .dimensions(this.width / 2 + 2, this.y, 98, 20)
                  .build()
            );
         }

         if (ContainerHelper.containerHelper.isEnabled()) {
            if ((Object)this instanceof InventoryScreen) {
               this.addDrawableChild(
                  ButtonWidget.builder(
                        Text.of("Выкинуть все"),
                        var2x -> {
                           for (int i = 0; i < minecraftclient.player.currentScreenHandler.slots.size(); i++) {
                              ItemStack itemstack = minecraftclient.player.currentScreenHandler.getSlot(i).getStack();
                              if (!itemstack.isEmpty()) {
                                 EventClick ili11ii1l1ill1lllil1 = new EventClick(this.handler.syncId, i, itemstack.getCount(), SlotActionType.THROW);
                                 EventManager.call(ili11ii1l1ill1lllil1);
                                 if (!ili11ii1l1ill1lllil1.isCancelled()) {
                                    minecraftclient.interactionManager
                                       .clickSlot(this.handler.syncId, i, itemstack.getCount(), SlotActionType.THROW, minecraftclient.player);
                                 }
                              }
                           }
                        }
                     )
                     .dimensions(this.x + 5, this.y - 25, this.backgroundWidth - 10, 20)
                     .build()
               );
            } else if (GenericContainerScreen.class.isInstance(this) || ShulkerBoxScreen.class.isInstance(this)) {
               this.addDrawableChild(
                  ButtonWidget.builder(
                        Text.of("Выкинуть"),
                        var2x -> {
                           for (int i = 0; i < this.handler.slots.size() - 36; i++) {
                              ItemStack itemstack = this.handler.getSlot(i).getStack();
                              if (!itemstack.isEmpty()) {
                                 minecraftclient.interactionManager
                                    .clickSlot(this.handler.syncId, i, itemstack.getCount(), SlotActionType.THROW, minecraftclient.player);
                              }
                           }
                        }
                     )
                     .dimensions(this.x, this.y - 25, 56, 20)
                     .build()
               );
               this.addDrawableChild(ButtonWidget.builder(Text.of("Сложить"), var2x -> {
                  for (int i = this.handler.slots.size() - 36; i < this.handler.slots.size(); i++) {
                     if (!this.handler.getSlot(i).getStack().isEmpty()) {
                        minecraftclient.interactionManager.clickSlot(this.handler.syncId, i, 0, SlotActionType.QUICK_MOVE, minecraftclient.player);
                     }
                  }
               }).dimensions(this.x + 56 + 4, this.y - 25, 56, 20).build());
               this.addDrawableChild(ButtonWidget.builder(Text.of("Забрать"), var2x -> {
                  for (int i = 0; i < this.handler.slots.size() - 36; i++) {
                     if (!this.handler.getSlot(i).getStack().isEmpty()) {
                        minecraftclient.interactionManager.clickSlot(this.handler.syncId, i, 0, SlotActionType.QUICK_MOVE, minecraftclient.player);
                     }
                  }
               }).dimensions(this.x + 56 + 56 + 4 + 4, this.y - 25, 56, 20).build());
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   public void render(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      EventManager.call(new EventRender(var1, this.focusedSlot, this.backgroundWidth, this.backgroundHeight));
      if (!this.zenith_isInventoryScreen()) {
         this.zenith_betterMinecraft_popScaleIfNeeded(var1);
         this.zenith_finishClosingAnimation();
      }
   }

   @Unique
   @Override
   public boolean zenith_betterMinecraft_isClosingAnimation() {
      return this.zenith_closingAnimation && this.zenith_shouldAnimateBetterMinecraft();
   }

   @Unique
   @Override
   public void zenith_betterMinecraft_popScaleIfNeeded(DrawContext var1) {
      if (this.zenith_scaleApplied) {
         var1.getMatrices().popMatrix();
         this.zenith_scaleApplied = false;
      }
   }

   @Unique
   @Override
   public void zenith_betterMinecraft_finishClosingAnimation() {
      this.zenith_finishClosingAnimation();
   }

   @Unique
   public boolean zenith_shouldAnimateBetterMinecraft() {
      return this.zenith_isInventoryScreen() ? BetterMinecraft.betterMinecraft.int423() : BetterMinecraft.betterMinecraft.float318();
   }

   @Unique
   public boolean zenith_isInventoryScreen() {
      return InventoryScreen.class.isInstance(this);
   }

   @Unique
   public float zenith_getScale() {
      return this.zenith_closingAnimation
         ? BetterMinecraft.betterMinecraft.on23(this.zenith_closedAt, this.zenith_closeStartScale)
         : BetterMinecraft.betterMinecraft.EmoteMetadata(this.zenith_openedAt);
   }

   @Unique
   public void zenith_startClosingAnimation() {
      if (!this.zenith_closingAnimation) {
         this.zenith_closeStartScale = this.zenith_getScale();
         this.zenith_closedAt = System.currentTimeMillis();
         this.zenith_closingAnimation = true;
      }
   }

   @Unique
   public void zenith_finishClosingAnimation() {
      if (this.zenith_closingAnimation && BetterMinecraft.betterMinecraft.EmoteManager(this.zenith_closedAt)) {
         this.zenith_forceClose = true;
         this.close();
         this.zenith_forceClose = false;
      }
   }
}
