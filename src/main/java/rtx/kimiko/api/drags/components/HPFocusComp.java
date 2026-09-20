package rtx.kimiko.api.drags.components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import rtx.kimiko.api.drags.DragSystem;
import rtx.kimiko.api.drags.Draggable;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.HPFocus;
import rtx.kimiko.utils.animations.Easings;
import rtx.kimiko.utils.animations.SmoothAnimation;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.network.Network;
import rtx.kimiko.utils.render.others.RectUtil;
import rtx.kimiko.utils.render.post.hpfocus.HPFocusRenderer;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.Render2DCoordinateSpace;

public final class HPFocusComp
extends Draggable {
    private static final float BASE_HEIGHT = 46.0f;
    private static final float RADIUS = 5.0f;
    private static final float BORDER = 2.0f;
    private static final float DEFAULT_ASPECT = 3.8461537f;
    private final SmoothAnimation visibility = new SmoothAnimation();

    public HPFocusComp() {
        super("hpfocus", 342.0f, 40.0f);
        this.visibility.set(0.0);
    }

    private static HPFocus module() {
        return ModuleManager.get().get(HPFocus.class);
    }

    @Override
    public float width() {
        return HPFocusComp.sizeFor(HPFocusComp.module())[0];
    }

    @Override
    public float height() {
        return HPFocusComp.sizeFor(HPFocusComp.module())[1];
    }

    private static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    @Override
    public boolean isInteractive() {
        HPFocus hPFocus = HPFocusComp.module();
        return hPFocus != null && hPFocus.isEnabled();
    }

    @Override
    protected void render(DrawContext drawContext) {
        Framebuffer framebuffer;
        boolean bl;
        HPFocus hPFocus = HPFocusComp.module();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl2 = hPFocus != null && hPFocus.isEnabled();
        boolean bl3 = DragSystem.get().isDragModeActive();
        boolean bl4 = bl = minecraftClient.options != null && minecraftClient.options.hudHidden;
        boolean bl5 = !bl2 || bl || minecraftClient.player == null ? false : (bl3 ? true : Network.getResolvedHealth((LivingEntity)minecraftClient.player, true) <= hPFocus.hpThresholdHp());
        this.visibility.run(bl5 ? 1.0 : 0.0, bl5 ? 0.18 : 0.14, Easings.CUBIC_OUT, true);
        this.visibility.update();
        float f = this.visibility.get();
        if (f <= 0.01f) {
            return;
        }
        HPFocusRenderer.requestCapture();
        float[] fArray = HPFocusComp.sizeFor(hPFocus);
        float f2 = fArray[0];
        float f3 = fArray[1];
        float f4 = this.getX();
        float f5 = this.getY();
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(f4, f5, f2, f3, 5.0f, f);
        float f6 = f2 - 4.0f;
        float f7 = f3 - 4.0f;
        if (f6 > 1.0f && f7 > 1.0f && HPFocusRenderer.hasCapture() && (framebuffer = minecraftClient.getFramebuffer()) != null && framebuffer.textureWidth > 0 && framebuffer.textureHeight > 0) {
            float f8 = framebuffer.textureWidth;
            float f9 = framebuffer.textureHeight;
            float f10 = Render2DCoordinateSpace.guiScale();
            float f11 = hPFocus.captureWidthGui() * f10 * 0.5f;
            float f12 = HPFocusComp.clamp01((f8 * 0.5f - f11) / f8);
            float f13 = HPFocusComp.clamp01((f8 * 0.5f + f11) / f8);
            float f14 = HPFocusComp.clamp01(hPFocus.captureHeightGui() * f10 / f9);
            float f15 = 0.0f;
            Render2D.imageUv("kimiko:hpfocus_scene", f4 + 2.0f, f5 + 2.0f, f6, f7, Math.max(0.0f, 3.0f), 1.0f, f12, f14, f13, f15, ColorUtil.multAlpha(-1, f));
        }
        Render2D.flush();
    }

    private static float[] sizeFor(HPFocus hPFocus) {
        float f = hPFocus != null ? hPFocus.scale() : 1.3f;
        float f2 = hPFocus != null ? hPFocus.captureWidthGui() : 200.0f;
        float f3 = hPFocus != null ? hPFocus.captureHeightGui() : 52.0f;
        float f4 = f3 > 0.5f ? f2 / f3 : 3.8461537f;
        float f5 = 46.0f * f;
        return new float[]{f5 * f4, f5};
    }
}

