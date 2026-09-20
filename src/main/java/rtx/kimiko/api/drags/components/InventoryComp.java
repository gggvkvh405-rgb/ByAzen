package rtx.kimiko.api.drags.components;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2f;
import rtx.kimiko.api.drags.DragSystem;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.impl.Interface.InventoryModule;
import rtx.kimiko.api.modules.impl.Visuals.ItemHighlight;
import rtx.kimiko.utils.animations.Easing;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.Render2DCoordinateSpace;

public final class InventoryComp
extends Draggable {
    private static final String TITLE_FONT = "montserrat-semibold";
    private static final String TITLE_TEXT = "Inventory";
    private static final float TITLE_SIZE = 8.2f;
    private static final float PAD_X = 9.0f;
    private static final String HEADER_ICON_FONT = "kimiko";
    private static final String HEADER_ICON_GLYPH = "n";
    private static final float HEADER_ICON_SIZE = 7.0f;
    private static final float HEADER_ICON_GAP = 6.0f;
    private static final float HEADER_ICON_SLIDE = 5.0f;
    private static final double HEADER_ICON_IN_SECONDS = 0.25;
    private static final double HEADER_ICON_OUT_SECONDS = 0.2;
    private static final float DIVIDER_Y = 22.0f;
    private static final float DIVIDER_HEIGHT = 0.5f;
    private static final int DIVIDER_COLOR = -1997220937;
    private static final float RADIUS = 5.0f;
    private static final int ITEMS_PER_ROW = 9;
    private static final int SLOT_START = 9;
    private static final int SLOT_END = 36;
    private static final int TOTAL = 27;
    private static final int ROWS = 3;
    private static final float CELL = 11.0f;
    private static final float ITEM = 8.0f;
    private static final float ITEM_INSET = 0.75f;
    private static final float ORIGIN_X = 4.0f;
    private static final float ORIGIN_Y = 28.0f;
    private static final float PANEL_WIDTH = 107.0f;
    private static final int LINE_COLOR = -1;
    private static final float SLOT_RATE = 13.0f;
    private static final float MIN_P = 0.02f;
    private static final float HEADER_HEIGHT = 22.0f;
    private static final float HEADER_EXTRA_WIDTH = 14.0f;
    private static final double WIDTH_SECONDS = 0.24;
    private static final double DIVIDER_IN_SECONDS = 0.24;
    private static final double DIVIDER_OUT_SECONDS = 0.14;
    private static final double ROW_IN_SECONDS = 0.22;
    private static final double ROW_OUT_SECONDS = 0.18;
    private static final long FADE_OUT_DELAY_MS = 100L;
    private static final float MIN_DIVIDER_ALPHA = 0.003921569f;
    private static final Easing SMOOTH = d -> d * d * (3.0 - 2.0 * d);
    private final SmoothAnimation visibility = new SmoothAnimation();
    private final SmoothAnimation widthAnimation = new SmoothAnimation();
    private final SmoothAnimation dividerAlpha = new SmoothAnimation();
    private final SmoothAnimation headerIconAnim = new SmoothAnimation();
    private boolean lastHeaderIconVisible;
    private List<ItemStack> stacks = new ArrayList<ItemStack>();
    private final float[] slotProgress = new float[27];
    private final ItemStack[] shownStacks = new ItemStack[27];
    private final SmoothAnimation extentAnimation = new SmoothAnimation();
    private float currentWidth = 107.0f;
    private float currentHeight = 64.0f;
    private float titleWidthCache = -1.0f;
    private boolean everHadContent;
    private long sizeCollapsedAtMs;
    private long lastNs = System.nanoTime();

    public InventoryComp() {
        super("inventory", 5.0f, 82.0f);
        this.visibility.set(0.0);
        this.widthAnimation.set(107.0);
        this.dividerAlpha.set(0.0);
        this.extentAnimation.set(0.0);
        this.headerIconAnim.set(0.0);
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return this.currentHeight;
    }

    @Override
    public boolean isInteractive() {
        return InventoryComp.componentEnabled();
    }

    private void updateHeaderIcon() {
        boolean bl = InventoryComp.hudIconsEnabled();
        if (bl != this.lastHeaderIconVisible) {
            this.headerIconAnim.run(bl ? 1.0 : 0.0, bl ? 0.25 : 0.2, SMOOTH, false);
            this.lastHeaderIconVisible = bl;
        }
        this.headerIconAnim.update();
    }

    private float headerMinWidth() {
        float f = this.titleWidth() + 14.0f;
        float f2 = this.headerIconProgress();
        if (f2 <= 0.001f) {
            return f;
        }
        float[] fArray = InventoryComp.textBounds(TITLE_FONT, TITLE_TEXT, 8.2f);
        float[] fArray2 = InventoryComp.textBounds(HEADER_ICON_FONT, HEADER_ICON_GLYPH, 7.0f);
        float f3 = 18.0f + (fArray[2] - fArray[0]) + 6.0f + (fArray2[2] - fArray2[0]);
        return f + Math.max(0.0f, f3 - f) * f2;
    }

    private static boolean hudIconsEnabled() {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        return interfaceModule != null && interfaceModule.hudIcons.getValue();
    }

    private boolean computeTargetVisible(boolean bl, float f) {
        boolean bl2;
        if (bl) {
            this.everHadContent = true;
            this.sizeCollapsedAtMs = 0L;
            return true;
        }
        if (!this.everHadContent) {
            return false;
        }
        boolean bl3 = bl2 = this.extentAnimation.get() > 0.02f || this.dividerAlpha.get() > 0.003921569f || this.currentHeight > 22.5f || this.widthAnimation.get() > f + 0.5f;
        if (bl2) {
            this.sizeCollapsedAtMs = 0L;
            return true;
        }
        if (this.sizeCollapsedAtMs == 0L) {
            this.sizeCollapsedAtMs = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - this.sizeCollapsedAtMs < 100L;
    }

    private float headerIconProgress() {
        return Math.max(0.0f, Math.min(1.0f, this.headerIconAnim.get()));
    }

    private static boolean componentEnabled() {
        InventoryModule inventoryModule = ModuleManager.get().get(InventoryModule.class);
        return inventoryModule != null && inventoryModule.isEnabled();
    }

    private static List<ItemStack> readInventory() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>(27);
        if (minecraftClient.player != null) {
            PlayerInventory playerInventory = minecraftClient.player.getInventory();
            for (int i = 9; i < 36; ++i) {
                arrayList.add(playerInventory.getStack(i));
            }
        }
        return arrayList;
    }

    private void drawSeparators(float f, float f2, float f3, int n, boolean bl) {
        for (int i = 0; i <= n; ++i) {
            int n2;
            float f4;
            float f5 = f4 = i == n ? this.pres(i, bl) : 1.0f;
            if (f4 <= 0.02f) continue;
            int n3 = ColorUtil.multAlpha(-1, f3 * 0.12f * f4);
            int n4 = i * 9;
            for (int j = 0; j < 8; ++j) {
                n2 = n4 + j;
                Render2D.rect(InventoryComp.cellX(f, n2) + 10.0f, InventoryComp.cellY(f2, n2), 0.5f, 9.0f, n3);
            }
            if (i >= n) continue;
            float f6 = i + 1 == n ? this.pres(i + 1, bl) : 1.0f;
            n2 = ColorUtil.multAlpha(-1, f3 * 0.12f * Math.min(f4, f6));
            for (int j = 0; j < 9; ++j) {
                int n5 = n4 + j;
                Render2D.rect(InventoryComp.cellX(f, n5) - 0.5f, InventoryComp.cellY(f2, n5) + 10.0f, 9.0f, 0.5f, n2);
            }
        }
    }

    private void drawDivider(float f, float f2, float f3, float f4) {
        if (f4 <= 0.003921569f) {
            return;
        }
        float f5 = Math.max(0.0f, this.currentWidth - 18.0f);
        float f6 = f5 * f4;
        if (f6 <= 0.25f) {
            return;
        }
        Render2D.rect(f + (this.currentWidth - f6) * 0.5f, f2 + 22.0f, f6, 0.5f, 0.0f, ColorUtil.multAlpha(-1997220937, f3 * f4));
    }

    @Override
    protected void render(DrawContext drawContext) {
        float f;
        boolean bl = InventoryComp.componentEnabled();
        boolean bl2 = DragSystem.get().isDragModeActive();
        this.stacks = bl ? InventoryComp.readInventory() : new ArrayList();
        boolean bl3 = false;
        for (ItemStack itemStack : this.stacks) {
            if (itemStack.isEmpty()) continue;
            bl3 = true;
            break;
        }
        boolean bl4 = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        boolean bl5 = bl && bl2 && !bl3;
        boolean bl6 = bl5 || bl && bl4 && !bl3;
        int n = -1;
        for (int i = 0; i < 3; ++i) {
            if (!bl6 && !this.rowHasItem(i) && !(this.pres(i, false) > 0.02f)) continue;
            n = i;
        }
        float f2 = n + 1;
        if (Math.abs(this.extentAnimation.getToValue() - (double)f2) > 0.01) {
            this.extentAnimation.run(f2, f2 >= this.extentAnimation.get() ? 0.22 : 0.18, SMOOTH, false);
        }
        this.extentAnimation.update();
        float f3 = this.extentAnimation.get();
        boolean bl7 = bl && (bl3 || bl2 || bl4);
        boolean bl8 = bl3 || bl6;
        this.dividerAlpha.run(bl8 ? 1.0 : 0.0, bl8 ? 0.24 : 0.14, SMOOTH, true);
        this.dividerAlpha.update();
        float f4 = InventoryComp.smoothstep(this.dividerAlpha.get());
        float f5 = 28.0f + f3 * 11.0f + 3.0f;
        float f6 = Math.max(f4, Math.min(1.0f, f3));
        this.currentHeight = 22.0f + (f5 - 22.0f) * f6;
        this.updateHeaderIcon();
        float f7 = this.headerMinWidth();
        boolean bl9 = f3 < 0.05f && f4 < 0.05f;
        float f8 = bl7 || !bl9 ? 107.0f : f7;
        this.widthAnimation.run(f8, 0.24, SMOOTH, true);
        this.widthAnimation.update();
        this.currentWidth = Math.max(f7, this.widthAnimation.get());
        boolean bl10 = this.computeTargetVisible(bl7, f7);
        this.visibility.run(bl10 ? 1.0 : 0.0, bl10 ? 0.18 : 0.12, Easings.CUBIC_OUT, true);
        this.visibility.update();
        float f9 = this.visibility.get();
        boolean bl11 = f9 >= 0.9f && this.currentWidth >= 106.0f;
        long l = System.nanoTime();
        float f10 = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        float f11 = 1.0f - (float)Math.exp(-f10 * 13.0f);
        for (int i = 0; i < 27; ++i) {
            ItemStack itemStack;
            ItemStack itemStack2 = itemStack = i < this.stacks.size() ? this.stacks.get(i) : ItemStack.EMPTY;
            if (!itemStack.isEmpty()) {
                this.shownStacks[i] = itemStack;
            }
            f = !itemStack.isEmpty() && bl11 ? 1.0f : 0.0f;
            int n2 = i;
            this.slotProgress[n2] = this.slotProgress[n2] + (f - this.slotProgress[i]) * f11;
        }
        if (f9 <= 0.01f) {
            return;
        }
        float f12 = this.getX();
        float f13 = this.getY();
        f = 0.96f + f9 * 0.04f;
        float f14 = f12 + this.currentWidth * 0.5f;
        float f15 = f13 + this.currentHeight * 0.5f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f14, f15);
        drawContext.getMatrices().scale(f);
        drawContext.getMatrices().translate(-f14, -f15);
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(f12, f13, this.currentWidth, this.currentHeight, 5.0f, f9);
        this.drawHeader(f12, f13, f9);
        this.drawDivider(f12, f13, f9, f4);
        Render2D.flush();
        Render2D.beginFrame(drawContext);
        Render2D.pushScissor(drawContext, f12, f13, this.currentWidth, this.currentHeight);
        this.drawSeparators(f12, f13, f9, n, bl6);
        Render2D.flush();
        this.drawItems(drawContext, f12, f13, f9, n);
        Render2D.popScissor(drawContext);
        drawContext.getMatrices().popMatrix();
    }

    private static float[] textBounds(String string, String string2, float f) {
        float[] fArray = Render2D.msdfBounds(string, string2, f);
        if (fArray != null && fArray.length >= 4 && fArray[3] - fArray[1] > 0.0f) {
            return fArray;
        }
        return new float[]{0.0f, 0.0f, Render2D.msdfWidth(string, string2, f), f};
    }

    private static int titleColor() {
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        int n = interfaceModule == null ? -1 : interfaceModule.clientPrimaryColorOpaque();
        return ColorUtil.lerpColor(-1, n, 0.1f);
    }

    private float titleWidth() {
        if (this.titleWidthCache <= 0.0f) {
            this.titleWidthCache = Render2D.msdfWidth(TITLE_FONT, TITLE_TEXT, 8.2f);
        }
        return this.titleWidthCache;
    }

    private static float cellX(float f, int n) {
        return f + 4.0f + 1.0f + (float)(n % 9) * 11.0f;
    }

    private void drawItems(DrawContext drawContext, float f, float f2, float f3, int n) {
        float f4;
        float f5;
        float f6;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float f7 = 4.0f;
        ItemHighlight itemHighlight = ItemHighlight.getInstance();
        if (itemHighlight != null && itemHighlight.isEnabled()) {
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            f6 = interfaceModule == null ? 2.0f : interfaceModule.rectCornerRadius.getFloat();
            f6 = Math.min(4.0f, Math.max(2.0f, f6));
            Render2D.beginFrame(drawContext);
            for (int i = 0; i < 27; ++i) {
                f5 = this.slotProgress[i];
                ItemStack itemStack = this.shownStacks[i];
                if (f5 <= 0.02f || itemStack == null || itemStack.isEmpty()) continue;
                int n2 = itemHighlight.backgroundFor(itemStack, false);
                boolean bl = i / 9 == n;
                f4 = bl && i % 9 == 8 ? f6 : 2.0f;
                float f8 = bl && i % 9 == 0 ? f6 : 2.0f;
                itemHighlight.drawRoundedSlotBackground(InventoryComp.cellX(f, i) + 0.75f, InventoryComp.cellY(f2, i) + 0.75f, 8.0f, n2, f3 * f5, 2.0f, 2.0f, f4, f8);
            }
            Render2D.flush();
        }
        for (int i = 0; i < 27; ++i) {
            ItemStack itemStack;
            f6 = this.slotProgress[i];
            if (f6 <= 0.02f || (itemStack = this.shownStacks[i]) == null || itemStack.isEmpty() || (f5 = Math.max(0.0f, Math.min(1.0f, f3 * f6))) <= 0.01f) continue;
            float f9 = 8.0f * InventoryComp.easeOutBack(f6);
            float f10 = InventoryComp.cellX(f, i) + 0.75f + f7;
            float f11 = InventoryComp.cellY(f2, i) + 0.75f + f7;
            f4 = f9 / 16.0f * f5;
            drawContext.getMatrices().pushMatrix();
            Render2DCoordinateSpace.applyGuiScaleIndependence((Matrix3x2f)drawContext.getMatrices());
            drawContext.getMatrices().translate(f10, f11);
            drawContext.getMatrices().scale(f4, f4);
            drawContext.getMatrices().translate(-8.0f, -8.0f);
            drawContext.drawItem(itemStack, 0, 0);
            drawContext.drawStackOverlay(textRenderer, itemStack, 0, 0);
            drawContext.getMatrices().popMatrix();
        }
    }

    private static float cellY(float f, int n) {
        return f + 28.0f + 1.0f + (float)(n / 9) * 11.0f;
    }

    private boolean rowHasItem(int n) {
        int n2 = n * 9;
        for (int i = 0; i < 9; ++i) {
            int n3 = n2 + i;
            if (n3 >= 27 || n3 >= this.stacks.size() || this.stacks.get(n3).isEmpty()) continue;
            return true;
        }
        return false;
    }

    private void drawHeader(float f, float f2, float f3) {
        float f4 = this.headerIconProgress();
        float[] fArray = InventoryComp.textBounds(TITLE_FONT, TITLE_TEXT, 8.2f);
        float f5 = fArray[2] - fArray[0];
        float f6 = f2 + 11.0f;
        float f7 = f6 - (fArray[1] + fArray[3]) * 0.5f;
        float f8 = f + (this.currentWidth - f5) * 0.5f - fArray[0];
        float f9 = f + 9.0f - fArray[0];
        float f10 = f8 + (f9 - f8) * f4;
        Render2D.msdfText(TITLE_FONT, TITLE_TEXT, f10, f7, 8.2f, ColorUtil.multAlpha(InventoryComp.titleColor(), f3));
        if (f4 > 0.003921569f) {
            float[] fArray2 = InventoryComp.textBounds(HEADER_ICON_FONT, HEADER_ICON_GLYPH, 7.0f);
            float f11 = f + this.currentWidth - 9.0f - fArray2[2] + 5.0f * (1.0f - f4);
            float f12 = f6 - (fArray2[1] + fArray2[3]) * 0.5f;
            Render2D.msdfText(HEADER_ICON_FONT, HEADER_ICON_GLYPH, f11, f12, 7.0f, ColorUtil.multAlpha(InventoryComp.titleColor(), f3 * f4 * 0.8f));
        }
    }

    private float pres(int n, boolean bl) {
        if (bl) {
            return 1.0f;
        }
        float f = 0.0f;
        int n2 = n * 9;
        for (int i = 0; i < 9; ++i) {
            int n3 = n2 + i;
            if (n3 >= 27) continue;
            f = Math.max(f, this.slotProgress[n3]);
        }
        return f;
    }

    private static float easeOutBack(float f) {
        float f2 = 1.70158f;
        float f3 = f2 + 1.0f;
        float f4 = f - 1.0f;
        return 1.0f + f3 * f4 * f4 * f4 + f2 * f4 * f4;
    }

    private static float smoothstep(float f) {
        float f2 = Math.max(0.0f, Math.min(1.0f, f));
        return f2 * f2 * (3.0f - 2.0f * f2);
    }
}

