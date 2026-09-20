package rtx.kimiko.api.drags.components;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.ArrayListModule;
import rtx.kimiko.api.modules.impl.Interface.ClickGui;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.animations.Easing;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class ArrayListComp
extends Draggable {
    private static final String ROW_FONT = "montserrat-medium";
    private static final float ROW_SIZE = 7.2f;
    private static final float ROW_HEIGHT = 8.7f;
    private static final float PAD_X = 1.5f;
    private static final float PAD_Y_UP = 0.0f;
    private static final float PAD_Y_DOWN = 1.0f;
    private static final float RADIUS = 3.0f;
    private static final float MIN_WIDTH = 1.0f;
    private static final double ROW_IN_SECONDS = 0.22;
    private static final double ROW_OUT_SECONDS = 0.16;
    private static final float MIN_TEXT_ALPHA = 0.003921569f;
    private static final float RAINBOW_ROW_STEP = 0.05f;
    private static final float WAVE_FREQ = 0.05f;
    private static final float RAINBOW_WAVE_HUE_SHIFT = 0.1f;
    private static final Easing ROW_EASING = d -> d * d * (3.0 - 2.0 * d);
    private final Map<Module, SmoothAnimation> rowAnimations = new IdentityHashMap<Module, SmoothAnimation>();
    private final Map<Module, Boolean> rowTargets = new IdentityHashMap<Module, Boolean>();
    private final SmoothAnimation visibility = new SmoothAnimation();
    private boolean lastTargetVisible;
    private float currentWidth = 1.0f;
    private float currentHeight = 9.7f;
    private float[] rowWidths = new float[0];
    private float[] rowHeights = new float[0];
    private int rowCountMetric = 0;
    private boolean leftAlignedState = true;
    private boolean bottomAnchoredState = false;
    private double wavePhase = 0.0;
    private long lastWaveNow = 0L;
    private float lastPinnedHeight = Float.NaN;

    public ArrayListComp() {
        super("arraylist", 888.0f, 5.0f);
        this.visibility.set(0.0);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
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
        return this.shouldShow();
    }

    private void refreshMetrics(List<Module> list) {
        if (this.rowWidths.length < list.size()) {
            this.rowWidths = new float[list.size()];
            this.rowHeights = new float[list.size()];
        }
        float f = 0.0f;
        float f2 = 0.0f;
        int n = 0;
        for (Module module : list) {
            float f3 = this.rowProgress(module);
            if (f3 <= 0.001f) continue;
            float f4 = Render2D.msdfWidth(ROW_FONT, module.getName(), 7.2f) + 3.0f;
            float f5 = 8.7f * f3;
            f = Math.max(f, f4);
            f2 += f5;
            this.rowWidths[n] = f4;
            this.rowHeights[n] = f5;
            ++n;
        }
        this.rowCountMetric = n;
        this.currentWidth = Math.max(f, 1.0f);
        this.currentHeight = f2 + 0.0f + 1.0f;
    }

    private float rowProgress(Module module) {
        SmoothAnimation smoothAnimation = this.rowAnimations.get(module);
        return smoothAnimation == null ? 0.0f : ArrayListComp.clamp(smoothAnimation.get(), 0.0f, 1.0f);
    }

    private boolean isBottomHalf() {
        float f = Math.max(1.0f, Position.screenHeight());
        return this.getY() + this.currentHeight * 0.5f > f * 0.5f;
    }

    private void updateOrientation() {
        this.leftAlignedState = !this.isRightHalf();
        this.bottomAnchoredState = this.isBottomHalf();
    }

    private void updateAnimations() {
        for (Module module2 : ModuleManager.get().getAll()) {
            boolean bl = ArrayListComp.isListed(module2);
            SmoothAnimation smoothAnimation = this.rowAnimations.computeIfAbsent(module2, m -> {
                SmoothAnimation anim = new SmoothAnimation();
                anim.set(0.0);
                return anim;
            });
            Boolean bl2 = this.rowTargets.get(module2);
            if (bl2 == null || bl2 != bl) {
                smoothAnimation.run(bl ? 1.0 : 0.0, bl ? 0.22 : 0.16, ROW_EASING, false);
                this.rowTargets.put(module2, bl);
            }
            smoothAnimation.update();
        }
    }

    private List<Module> visibleRows() {
        ArrayList<Module> arrayList = new ArrayList<Module>();
        for (Module module2 : ModuleManager.get().getAll()) {
            if (ArrayListComp.isExcluded(module2) || !(this.rowProgress(module2) > 0.001f)) continue;
            arrayList.add(module2);
        }
        arrayList.sort(Comparator.comparingDouble((Module module) -> Render2D.msdfWidth(ROW_FONT, module.getName(), 7.2f)).reversed());
        return arrayList;
    }

    private static void drawGradientText(String string, float f, float f2, float f3, int n) {
        float f4 = ArrayListComp.clamp(f3, 0.0f, 1.0f);
        if (f4 <= 0.003921569f || string == null || string.isEmpty()) {
            return;
        }
        Render2D.msdfText(ROW_FONT, string, f, f2, 7.2f, n);
    }

    private boolean isRightHalf() {
        float f = Math.max(1.0f, Position.screenWidth());
        return this.getX() + this.currentWidth * 0.5f > f * 0.5f;
    }

    private static boolean isExcluded(Module module) {
        return module instanceof ClickGui;
    }

    @Override
    protected void render(DrawContext drawContext) {
        boolean bl;
        float f;
        this.updateAnimations();
        this.updateOrientation();
        boolean bl2 = this.bottomAnchoredState;
        List<Module> list = this.visibleRows();
        if (bl2) {
            Collections.reverse(list);
        }
        this.refreshMetrics(list);
        if (!Float.isNaN(this.lastPinnedHeight) && bl2 && !this.getDrag().isDragging() && Math.abs(f = this.currentHeight - this.lastPinnedHeight) > 1.0E-4f) {
            this.getDrag().adjustY(-f);
            this.getDrag().syncToTarget();
        }
        this.lastPinnedHeight = this.currentHeight;
        boolean bl3 = bl = this.shouldShow() && !list.isEmpty();
        if (bl != this.lastTargetVisible) {
            this.visibility.run(bl ? 1.0 : 0.0, bl ? 0.18 : 0.12, Easings.CUBIC_OUT, false);
            this.lastTargetVisible = bl;
        }
        this.visibility.update();
        float f2 = this.visibility.get();
        if (bl && f2 <= 0.01f) {
            f2 = 0.01f;
        }
        if (f2 <= 0.01f && !bl) {
            return;
        }
        float f3 = this.getX();
        float f4 = this.getY();
        boolean bl4 = this.leftAlignedState;
        float f5 = 0.92f + f2 * 0.08f;
        float f6 = f3 + this.currentWidth * 0.5f;
        float f7 = f4 + this.currentHeight * 0.5f;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(f6, f7);
        drawContext.getMatrices().scale(f5);
        drawContext.getMatrices().translate(-f6, -f7);
        Render2D.beginFrame(drawContext);
        if (this.rowCountMetric > 0) {
            RectUtil.drawClientShape(f3, f4, this.rowWidths, this.rowHeights, this.rowCountMetric, 0.0f, 1.0f, 3.0f, f2, bl4, bl2);
        } else {
            RectUtil.drawClientRect(f3, f4, this.currentWidth, this.currentHeight, 3.0f, f2);
        }
        ArrayListModule arrayListModule = ModuleManager.get().get(ArrayListModule.class);
        double d = arrayListModule == null ? 0.0 : arrayListModule.waveSpeed();
        long l = System.currentTimeMillis();
        long l2 = this.lastWaveNow == 0L ? 0L : Math.max(0L, Math.min(100L, l - this.lastWaveNow));
        this.lastWaveNow = l;
        this.wavePhase = (this.wavePhase + (double)l2 * d) % (Math.PI * 2);
        float f8 = (float)this.wavePhase;
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        boolean bl5 = interfaceModule != null && interfaceModule.isRainbowClientColor();
        int n = 0;
        float f9 = f4 + 0.0f;
        for (Module module : list) {
            int n2;
            float f10 = this.rowProgress(module);
            if (f10 <= 0.001f) continue;
            float f11 = 8.7f * f10;
            float f12 = Render2D.msdfWidth(ROW_FONT, module.getName(), 7.2f);
            float f13 = bl4 ? f3 + 1.5f : f3 + this.currentWidth - 1.5f - f12;
            float f14 = f9 + (f11 - 7.2f) * 0.5f - 0.3f;
            float f15 = ArrayListComp.clamp(f2 * f10, 0.0f, 1.0f);
            float f16 = 0.5f + 0.5f * (float)Math.sin((f9 + f11 * 0.5f) * 0.05f - f8);
            if (bl5) {
                n2 = ClientAccent.mix(ClientAccent.rainbowFlow((float)(-n) * 0.05f, f15 * 255.0f), ClientAccent.rainbowFlow((float)(-n) * 0.05f - 0.1f, f15 * 255.0f), f16);
            } else {
                int n3;
                int n4 = ClientAccent.gradientA(f15 * 255.0f);
                if ((n4 & 0xFFFFFF) == ((n3 = ClientAccent.gradientB(f15 * 255.0f)) & 0xFFFFFF)) {
                    n3 = ArrayListComp.mixWhite(n4, 0.3f);
                }
                n2 = ClientAccent.mix(n4, n3, f16);
            }
            ArrayListComp.drawGradientText(module.getName(), f13, f14, f15, n2);
            ++n;
            f9 += f11;
        }
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    private boolean shouldShow() {
        ArrayListModule arrayListModule = ModuleManager.get().get(ArrayListModule.class);
        return arrayListModule != null && arrayListModule.isEnabled();
    }

    private static boolean isListed(Module module) {
        if (module == null || !module.isEnabled() || ArrayListComp.isExcluded(module)) {
            return false;
        }
        ArrayListModule arrayListModule = ModuleManager.get().get(ArrayListModule.class);
        return arrayListModule == null || arrayListModule.categoryShown(module.getCategory());
    }

    private static int mixWhite(int n, float f) {
        int n2 = n >>> 24 & 0xFF;
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        n3 += Math.round((float)(255 - n3) * f);
        n4 += Math.round((float)(255 - n4) * f);
        n5 += Math.round((float)(255 - n5) * f);
        return n2 << 24 | n3 << 16 | n4 << 8 | n5;
    }
}

