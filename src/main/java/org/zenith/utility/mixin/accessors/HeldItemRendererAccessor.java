package org.zenith.utility.mixin.accessors;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HeldItemRenderer.class)
public interface HeldItemRendererAccessor {
   @Accessor("mainHand")
   ItemStack zenith_getMainHand();

   @Accessor("mainHand")
   void zenith_setMainHand(ItemStack var1);

   @Accessor("offHand")
   ItemStack zenith_getOffHand();

   @Accessor("offHand")
   void zenith_setOffHand(ItemStack var1);

   @Accessor("equipProgressMainHand")
   float zenith_getEquipProgressMainHand();

   @Accessor("equipProgressMainHand")
   void zenith_setEquipProgressMainHand(float var1);

   @Accessor("lastEquipProgressMainHand")
   float zenith_getPrevEquipProgressMainHand();

   @Accessor("lastEquipProgressMainHand")
   void zenith_setPrevEquipProgressMainHand(float var1);

   @Accessor("equipProgressOffHand")
   float zenith_getEquipProgressOffHand();

   @Accessor("equipProgressOffHand")
   void zenith_setEquipProgressOffHand(float var1);

   @Accessor("lastEquipProgressOffHand")
   float zenith_getPrevEquipProgressOffHand();

   @Accessor("lastEquipProgressOffHand")
   void zenith_setPrevEquipProgressOffHand(float var1);
}
