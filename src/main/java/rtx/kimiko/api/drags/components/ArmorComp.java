package rtx.kimiko.api.drags.components;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Matrix3x2f;
import rtx.kimiko.api.drags.DragSystem;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.ArmorModule;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.Render2DCoordinateSpace;

public final class ArmorComp
extends Draggable {
    private static final float ITEM = 16.0f;
    private static final float PAD = 2.0f;
    private static final float RADIUS = 4.0f;
    private static final float EDGE_ENTER = 16.0f;
    private static final float EDGE_EXIT = 26.0f;
    private static final EquipmentSlot[] ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final SmoothAnimation visibility = new SmoothAnimation();
    private int side;
    private int previewSide;
    private int sideBeforeDrag;
    private boolean wasDragging;
    private boolean sideResolved;
    private float width = 20.0f;
    private float height = 20.0f;
    private float previewWidth = this.width;
    private float previewHeight = this.height;
    private List<ItemStack> shownItems = List.of();

    public ArmorComp() {
        super("armor", 166.0f, 5.0f);
        this.visibility.set(0.0);
    }

    @Override
    public float width() {
        return this.width;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public float overlayHeight() {
        return this.previewHeight;
    }

    @Override
    public boolean isInteractive() {
        return ArmorComp.componentEnabled();
    }

    @Override
    public float overlayWidth() {
        return this.previewWidth;
    }

    private static boolean componentEnabled() {
        ArmorModule armorModule = ModuleManager.get().get(ArmorModule.class);
        return armorModule != null && armorModule.isEnabled();
    }

    private static int dockFromPosition(float f, float f2, float f3) {
        if (f >= f3 - f2 - 5.0f - 1.0f) {
            return 1;
        }
        if (f <= 6.0f) {
            return -1;
        }
        return 0;
    }

    private static float orientWidth(int n, float f, float f2) {
        return n != 0 ? f2 : f;
    }

    private static List<ItemStack> previewItems() {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>(4);
        arrayList.add(Items.DIAMOND_HELMET.getDefaultStack());
        arrayList.add(Items.DIAMOND_CHESTPLATE.getDefaultStack());
        arrayList.add(Items.DIAMOND_LEGGINGS.getDefaultStack());
        arrayList.add(Items.DIAMOND_BOOTS.getDefaultStack());
        return arrayList;
    }

    private static float orientHeight(int n, float f, float f2) {
        return n != 0 ? f : f2;
    }

    private static List<ItemStack> collectItems() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>(ARMOR.length);
        if (minecraftClient.player != null) {
            for (EquipmentSlot equipmentSlot : ARMOR) {
                ItemStack itemStack = minecraftClient.player.getEquippedStack(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                arrayList.add(itemStack);
            }
        }
        return arrayList;
    }

    @Override
    protected void render(DrawContext drawContext) {
        List<ItemStack> list;
        boolean bl = ArmorComp.componentEnabled();
        boolean bl2 = DragSystem.get().isDragModeActive();
        List<ItemStack> list2 = list = bl ? ArmorComp.collectItems() : List.of();
        if (list.isEmpty() && bl && bl2) {
            list = ArmorComp.previewItems();
        }
        if (!list.isEmpty()) {
            this.shownItems = list;
        }
        boolean bl3 = bl && !list.isEmpty();
        this.visibility.run(bl3 ? 1.0 : 0.0, bl3 ? 0.18 : 0.12, Easings.CUBIC_OUT, true);
        this.visibility.update();
        float f = this.visibility.get();
        if (f <= 0.01f || this.shownItems.isEmpty()) {
            return;
        }
        List<ItemStack> list3 = this.shownItems;
        float f2 = 4.0f + (float)list3.size() * 16.0f;
        float f3 = 20.0f;
        float f4 = Math.max(1.0f, Position.screenWidth());
        boolean bl4 = f2 <= f4 - 10.0f;
        this.updateSide(this.getDrag().isDragging(), bl4, f3, f4);
        this.width = ArmorComp.orientWidth(this.side, f2, f3);
        this.height = ArmorComp.orientHeight(this.side, f2, f3);
        this.previewWidth = ArmorComp.orientWidth(this.previewSide, f2, f3);
        this.previewHeight = ArmorComp.orientHeight(this.previewSide, f2, f3);
        boolean bl5 = this.side != 0;
        float f5 = this.getX();
        float f6 = this.getY();
        float f7 = 0.96f + f * 0.04f;
        float f8 = f5 + this.width * 0.5f;
        float f9 = f6 + this.height * 0.5f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f8, f9);
        drawContext.getMatrices().scale(f7);
        drawContext.getMatrices().translate(-f8, -f9);
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(f5, f6, this.width, this.height, 4.0f, f);
        Render2D.flush();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float f10 = 8.0f;
        float f11 = 1.0f * Math.max(0.0f, Math.min(1.0f, f));
        float f12 = 0.0f;
        for (ItemStack itemStack : list3) {
            float f13 = f5 + 2.0f + (bl5 ? 0.0f : f12);
            float f14 = f6 + 2.0f + (bl5 ? f12 : 0.0f);
            drawContext.getMatrices().pushMatrix();
            Render2DCoordinateSpace.applyGuiScaleIndependence((Matrix3x2f)drawContext.getMatrices());
            drawContext.getMatrices().translate(f13 + f10, f14 + f10);
            drawContext.getMatrices().scale(f11, f11);
            drawContext.getMatrices().translate(-8.0f, -8.0f);
            drawContext.drawItem(itemStack, 0, 0);
            drawContext.drawStackOverlay(textRenderer, itemStack, 0, 0);
            drawContext.getMatrices().popMatrix();
            f12 += 16.0f;
        }
        drawContext.getMatrices().popMatrix();
    }

    private static int edgeSide(float f, float f2, int n) {
        if (n > 0) {
            return f < f2 - 26.0f ? 0 : 1;
        }
        if (n < 0) {
            return f > 26.0f ? 0 : -1;
        }
        if (f >= f2 - 16.0f) {
            return 1;
        }
        if (f <= 16.0f) {
            return -1;
        }
        return 0;
    }

    private void updateSide(boolean bl, boolean bl2, float f, float f2) {
        boolean bl3 = !this.wasDragging && bl;
        boolean bl4 = this.wasDragging && !bl;
        this.wasDragging = bl;
        if (bl3) {
            this.sideBeforeDrag = this.side;
        }
        if (bl) {
            int n = bl2 ? ArmorComp.edgeSide(Position.mouseX(), f2, this.previewSide) : 0;
            if (n != 0 != (this.previewSide != 0)) {
                this.getDrag().swapGrabOffset();
            }
            this.previewSide = n;
            this.sideResolved = true;
            return;
        }
        if (bl4) {
            this.side = this.getDrag().wasCancelled() ? this.sideBeforeDrag : this.previewSide;
        } else if (!this.sideResolved) {
            this.side = bl2 ? ArmorComp.dockFromPosition(this.getDrag().getTargetX(), f, f2) : 0;
            this.sideResolved = true;
        }
        this.previewSide = this.side;
        if (this.side > 0) {
            this.getDrag().setTargetX(f2 - f - 5.0f);
        } else if (this.side < 0) {
            this.getDrag().setTargetX(5.0f);
        }
    }
}

