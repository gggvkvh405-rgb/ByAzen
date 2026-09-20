package org.zenith.utility.mixin.accessors;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFrameEntity.class)
public interface ItemFrameEntityAccessor {
   @Accessor("ROTATION")
   static TrackedData<Integer> getRotationData() {
      throw new AssertionError();
   }

   @Accessor("ITEM_STACK")
   static TrackedData<ItemStack> getItemStackData() {
      throw new AssertionError();
   }
}
