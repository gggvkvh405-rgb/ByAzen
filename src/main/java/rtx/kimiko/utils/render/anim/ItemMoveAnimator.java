package rtx.kimiko.utils.render.anim;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public final class ItemMoveAnimator {
    private static final ItemMoveAnimator INSTANCE = new ItemMoveAnimator();

    public static ItemMoveAnimator getInstance() {
        return INSTANCE;
    }

    public boolean isActive() {
        return false;
    }

    public static void beginFrame(ScreenHandler handler, int mouseX, int mouseY, int x, int y) {
    }

    public static float[] offset(Slot slot) {
        return null;
    }

    public void onSlotClick(int slotId, int button, int clickType) {
    }

    public void onDrawSlot(Slot slot, ItemStack stack, int x, int y) {
    }
}