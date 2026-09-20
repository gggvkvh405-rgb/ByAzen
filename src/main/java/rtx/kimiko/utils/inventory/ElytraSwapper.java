package rtx.kimiko.utils.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class ElytraSwapper {
    private ElytraSwapper() {}

    private static boolean isChestplate(ItemStack stack) {
        return stack.isOf(Items.NETHERITE_CHESTPLATE)
                || stack.isOf(Items.DIAMOND_CHESTPLATE)
                || stack.isOf(Items.IRON_CHESTPLATE)
                || stack.isOf(Items.GOLDEN_CHESTPLATE)
                || stack.isOf(Items.CHAINMAIL_CHESTPLATE)
                || stack.isOf(Items.LEATHER_CHESTPLATE);
    }

    public static int findTargetSlot() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return -1;
        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        boolean hasElytraEquipped = chest.isOf(Items.ELYTRA);
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i >= 36 ? i - 36 : i);
            if (hasElytraEquipped) {
                if (isChestplate(stack)) {
                    return i;
                }
            } else {
                if (stack.isOf(Items.ELYTRA)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
