package org.zenith.utility.mixin.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.core.ClientProvider;
import org.zenith.module.render.ViewArmorDurability;

@Mixin(EquipmentRenderer.class)
public class MixinEquipmentRenderer implements ClientProvider {
   @Unique
   private static final String RENDER_METHOD = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V";

   @Unique
   public ItemStack renderStack = ItemStack.EMPTY;

   @Inject(
      method = RENDER_METHOD,
      at = @At("HEAD")
   )
   public void onRenderHead(
      LayerType var1,
      RegistryKey<EquipmentAsset> var2,
      Model var3,
      Object state,
      ItemStack var4,
      MatrixStack var5,
      OrderedRenderCommandQueue var6,
      int var7,
      Identifier var8,
      int outlineColor,
      int initialOrder,
      CallbackInfo var9
   ) {
      this.renderStack = var4;
   }

   @Inject(
      method = RENDER_METHOD,
      at = @At("TAIL")
   )
   public void onRenderTail(
      LayerType var1,
      RegistryKey<EquipmentAsset> var2,
      Model var3,
      Object state,
      ItemStack var4,
      MatrixStack var5,
      OrderedRenderCommandQueue var6,
      int var7,
      Identifier var8,
      int outlineColor,
      int initialOrder,
      CallbackInfo var9
   ) {
      this.renderStack = ItemStack.EMPTY;
   }

   @ModifyArg(
      method = RENDER_METHOD,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
      ),
      index = 6
   )
   public int onModifyColor(int var1) {
      if (minecraftClient3.player == null || minecraftClient3.world == null) {
         return var1;
      }

      if (this.renderStack != null && !this.renderStack.isEmpty()) {
         ViewArmorDurability li11lillliiliil1ilill1ii1 = ViewArmorDurability.viewArmorDurability;
         if (!li11lillliiliil1ilill1ii1.isEnabled()) {
            return var1;
         }

         boolean flag = this.isSelfArmor(this.renderStack);
         return !li11lillliiliil1ilill1ii1.on23(this.renderStack, flag) ? var1 : li11lillliiliil1ilill1ii1.BotChatEvent(this.renderStack);
      } else {
         return var1;
      }
   }

   @Unique
   public boolean isSelfArmor(ItemStack var1) {
      if (minecraftClient3.player == null) {
         return false;
      }

      for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
         if (minecraftClient3.player.getEquippedStack(slot) == var1) {
            return true;
         }
      }

      return false;
   }
}
