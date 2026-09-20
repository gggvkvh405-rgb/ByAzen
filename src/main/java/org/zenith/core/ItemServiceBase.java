package org.zenith.core;

import net.minecraft.item.ItemStack;

public class ItemServiceBase implements GameService {
   protected ItemStack PacketReceiveEvent;
   protected final String displayName;
   protected final String PacketSendEvent;
   protected final SoundCueKind VisualSettingsStore;

   public ItemServiceBase(ItemStack var1, String var2, SoundCueKind var3) {
      this.PacketReceiveEvent = var1;
      this.displayName = var2;
      this.PacketSendEvent = var2;
      this.VisualSettingsStore = var3;
   }

   public ItemServiceBase(ItemStack var1, String var2, String var3, SoundCueKind var4) {
      this.PacketReceiveEvent = var1;
      this.displayName = var2;
      this.PacketSendEvent = var3;
      this.VisualSettingsStore = var4;
   }

   public boolean UiAnimation(ItemStack var1) {
      return var1 != null && var1.getItem() == this.PacketReceiveEvent.getItem();
   }

   public ItemStack EventInjectHandleInputEvents() {
      return this.PacketReceiveEvent;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String EventMouseButton() {
      return this.PacketSendEvent;
   }

   public SoundCueKind EventModifyMouseRotationInput() {
      return this.VisualSettingsStore;
   }
}
