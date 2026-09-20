package rtx.kimiko.utils.render.warmup;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.client.gui.DrawContext;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.gif.GifRenderer;
import rtx.kimiko.utils.render.render2d.outline.outline360.Outline360Range;

public final class Render2DWarmup {
    private static final int WARMUP_FRAMES = 5;
    private static final Render2DWarmup INSTANCE = new Render2DWarmup();
    private static volatile int framesLeft = 5;
    private static final int FAINT = 0x1FFFFFF;
    private static final int FAINT_ACCENT = 25859059;

    private Render2DWarmup() {
    }

    public static void reset() {
        framesLeft = 5;
    }

    public static void init() {
        EventBus.get().subscribe(INSTANCE);
    }

    @EventHandler
    public void onHud(HudRenderEvent hudRenderEvent) {
        Render2DWarmup.runWarmupFrame(hudRenderEvent.getGraphics());
    }

    public static void runWarmupFrame(DrawContext drawContext) {
        if (framesLeft <= 0) {
            return;
        }
        --framesLeft;
        Render2D.beginFrame(drawContext);
        Render2D.blur(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 8.0f, 1.0f, 0x1FFFFFF);
        Render2D.glass(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0, 1.0f, 25.0f, 0x1FFFFFF, 0.5f, true, 0.5f, 0.1f, 1.0f, 0.0f);
        Render2D.glassOutline(-20.0f, 0.0f, 2.0f, 2.0f, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 1.0f, 0, 1.0f, 25.0f, 0x1FFFFFF, 0.5f, true, 0.5f, 0.1f, 1.0f, 0.0f);
        Render2D.rect(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0x1FFFFFF);
        Render2D.rect(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0x1FFFFFF, 0x1FFFFFF, 0x1FFFFFF, 0x1FFFFFF);
        Render2D.outline(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.7f, 0, 25859059, 0, 25859059);
        Render2D.outline360(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 1.0f, 0x1FFFFFF, new Outline360Range[0]);
        Render2D.circle(-20.0f, 1.0f, 1.0f, 0x1FFFFFF);
        Render2D.pickerHue(-20.0f, 0.0f, 2.0f, 2.0f, 0.003921569f);
        Render2D.pickerAlpha(-20.0f, 0.0f, 2.0f, 2.0f, 0x1FFFFFF, 0.003921569f);
        Render2D.zippy(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0x1FFFFFF);
        Render2D.halftoneRect(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 0x1FFFFFF, 0x1FFFFFF, 1.0f, 2.0f);
        Render2D.ripple(-20.0f, 0.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0x1FFFFFF, 0x1FFFFFF);
        Render2D.shimmer(-20.0f, 0.0f, 2.0f, 2.0f, 0.5f, 0.35f, 0.5f, 0x1FFFFFF);
        Fonts.KIMIKO.msdf("pjri", -20.0f, 0.0f, 6.5f, 0x1FFFFFF);
        Fonts.EVENT_ICONS.msdf("\ue104\ue106\ue105", -20.0f, 0.0f, 8.0f, 0x1FFFFFF);
        Fonts.MONTSERRAT_MEDIUM.draw("Ag", -20.0f, 0.0f, 7.0f, 0x1FFFFFF);
        Fonts.MONTSERRAT_MEDIUM.fade("Ag", -20.0f, 0.0f, 5.5f, 0x1FFFFFF, 0.0f, 10.0f, 5.0f, 1.0f, 1.0f);
        Fonts.MONTSERRAT_MEDIUM.shimmer("Ag", -20.0f, 0.0f, 7.0f, 0x1FFFFFF, 0.5f, 0.35f, 0.5f, 0.003921569f);
        GifRenderer.draw(drawContext, -20.0f, 0.0f, 2.0f, 2.0f, 1.0f, "kimiko:gif/kity.gif", 0.003921569f);
        UI.INSTANCE.warmupRender();
        Render2D.flush();
    }
}

