package org.zenith.core;

import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PotionItemBuilder extends ItemServiceBase {
   public final RegistryEntry<Potion> ServerConfigStore;

   public PotionItemBuilder(Item var1, RegistryEntry<Potion> var2, String var3, SoundCueKind var4) {
      super(var1.getDefaultStack(), var3, var4);
      this.ServerConfigStore = var2;
      this.PacketReceiveEvent.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.of(var2), Optional.empty(), List.of(), Optional.empty()));
   }

   @Override
   public boolean UiAnimation(ItemStack var1) {
      if (!super.UiAnimation(var1)) {
         return false;
      }

      PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      if (potioncontentscomponent == null) {
         return false;
      }

      Optional<RegistryEntry<Potion>> optional = potioncontentscomponent.potion();
      return optional.isEmpty() ? false : this.on23(optional.get());
   }

   public boolean on23(RegistryEntry<Potion> var1) {
      if (var1.equals(this.ServerConfigStore)) {
         return true;
      }

      Identifier identifier = Registries.POTION.getId((Potion)this.ServerConfigStore.value());
      Identifier identifier1 = Registries.POTION.getId((Potion)var1.value());
      return identifier != null && identifier1 != null
         ? identifier.getNamespace().equals(identifier1.getNamespace())
            && ColorAnimator(identifier.getPath()).equals(ColorAnimator(identifier1.getPath()))
         : false;
   }

   public static String ColorAnimator(String var0) {
      if (var0.startsWith("long_")) {
         return var0.substring("long_".length());
      } else {
         return var0.startsWith("strong_") ? var0.substring("strong_".length()) : var0;
      }
   }
}
