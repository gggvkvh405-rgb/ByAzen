package rtx.kimiko.utils.inventory;

import net.minecraft.item.ItemStack;

public enum SlotCategory {
    POTION(SlotItem.Group.POTION, 0xFF3232);

    private final SlotItem.Group itemGroup;
    private final int defaultColor;

    SlotCategory(SlotItem.Group itemGroup, int defaultColor) {
        this.itemGroup = itemGroup;
        this.defaultColor = defaultColor;
    }

    public SlotItem.Group getItemGroup() {
        return this.itemGroup;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public static SlotCategory classify(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (SlotItem.match(stack, SlotItem.Group.POTION) != null) return POTION;
        return null;
    }
}
