package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.PerfGraphModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Виджет графика FPS (идеи №117 и №118 из IDEAS.md): сглаженная линия, мягкая заливка под ней,
 * средний FPS и «1% low» — по ним сразу видно статтеры.
 */
public final class PerfGraphComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-bold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float WIDTH = 132.0f;
    private static final float HEIGHT = 42.0f;
    private static final float PAD = 7.0f;

    private float alpha;
    private long lastFrameNs;
    private float[] points = new float[32];

    public PerfGraphComp() {
        super("perf_graph", 5.0f, 200.0f);
    }

    private static PerfGraphModule module() {
        return ModuleManager.get().get(PerfGraphModule.class);
    }

    @Override
    public String displayName() {
        return "График FPS";
    }

    @Override
    public float width() {
        return WIDTH;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public boolean isInteractive() {
        PerfGraphModule module = PerfGraphComp.module();
        return module != null && module.isEnabled() && module.showWidget.getValue();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        PerfGraphModule module = PerfGraphComp.module();
        if (module != null) {
            list.add(new BoolSetting(module.showWidget));
            list.add(new SliderSetting(module.window));
            list.add(new BoolSetting(module.showLow));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        PerfGraphModule module = PerfGraphComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((this.isInteractive() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        int samples = module.sampleCount();
        if (samples <= 1) {
            return;
        }
        if (this.points.length != samples) {
            this.points = new float[samples];
        }
        float max = 1.0f;
        for (int i = 0; i < samples; ++i) {
            float value = module.sample(i);
            this.points[i] = value;
            max = Math.max(max, value);
        }
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        float graphX = x + PAD;
        float graphY = y + PAD + 9.0f;
        float graphW = WIDTH - PAD * 2.0f;
        float graphH = HEIGHT - PAD - 9.0f - 8.0f;

        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, WIDTH, HEIGHT, 6.0f, a);
        Render2D.msdfText(FONT_TITLE, "FPS", x + PAD, y + PAD, 7.0f, ClientAccent.accent(240.0f * a));
        String average = String.valueOf(Math.round(module.average()));
        float averageWidth = Render2D.msdfWidth(FONT_TITLE, average, 8.0f);
        Render2D.msdfText(FONT_TITLE, average, x + WIDTH - PAD - averageWidth, y + PAD - 1.0f, 8.0f,
                ColorUtil.rgba(240, 245, 255, Math.round(246.0f * a)));
        if (module.showLow.getValue()) {
            String low = "1% low " + Math.round(module.low());
            float lowWidth = Render2D.msdfWidth(FONT_TEXT, low, 5.6f);
            Render2D.msdfText(FONT_TEXT, low, x + WIDTH - PAD - lowWidth, y + PAD + 8.0f, 5.6f,
                    ColorUtil.rgba(186, 194, 208, Math.round(170.0f * a)));
        }
        Render2D.rect(graphX, graphY, graphW, graphH, 3.0f, ColorUtil.rgba(255, 255, 255, Math.round(8.0f * a)));
        float step = graphW / (float) Math.max(1, samples - 1);
        float previousX = graphX;
        float previousY = graphY + graphH - graphH * Math.min(1.0f, this.points[0] / max);
        for (int i = 1; i < samples; ++i) {
            float pointX = graphX + step * (float) i;
            float pointY = graphY + graphH - graphH * Math.min(1.0f, this.points[i] / max);
            // заливка под линией: тонкие столбики, чтобы график читался на любом фоне
            float columnHeight = graphY + graphH - pointY;
            AccentGradient.fillVertical(pointX - step * 0.5f, pointY, Math.max(1.0f, step + 0.6f), columnHeight, 0.0f, 26.0f * a);
            Render2D.line(previousX, previousY, pointX, pointY, 1.3f, ClientAccent.accent(215.0f * a));
            previousX = pointX;
            previousY = pointY;
        }
        Render2D.flush();
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNs == 0L ? 0.016f : (float) (now - this.lastFrameNs) / 1.0E9f;
        this.lastFrameNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }
}
