package rtx.byazen.mixin;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import rtx.byazen.api.modules.impl.Visuals.CustomSwords;

@Mixin(ItemModelManager.class)
public abstract class ItemModelResolverMixin {
    @ModifyVariable(
        method = "update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0,
        require = 1
    )
    private ItemStack byazen_replaceSwordModel(
        ItemStack stack,
        ItemRenderState renderState,
        ItemStack originalStack,
        ItemDisplayContext displayContext,
        World world,
        HeldItemContext itemOwner,
        int seed
    ) {
        CustomSwords module = CustomSwords.getInstance();
        if (module == null || !module.isEnabled() || !ItemModelResolverMixin.byazen_isSword(stack)) {
            return stack;
        }
        if (module.isSelfOnly()) {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            if (itemOwner != null && itemOwner.getEntity() != null && itemOwner.getEntity() != minecraft.player) {
                return stack;
            }
        }
        ItemStack renderedStack = stack.copy();
        renderedStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(module.getSelectedWeapon()), List.of()));
        return renderedStack;
    }

    private static boolean byazen_isSword(ItemStack stack) {
        return stack.isOf(Items.WOODEN_SWORD) || stack.isOf(Items.STONE_SWORD) || stack.isOf(Items.IRON_SWORD) || stack.isOf(Items.GOLDEN_SWORD) || stack.isOf(Items.DIAMOND_SWORD) || stack.isOf(Items.NETHERITE_SWORD);
    }
}
