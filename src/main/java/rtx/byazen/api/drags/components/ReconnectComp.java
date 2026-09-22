package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.AutoReconnect;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Виджет переподключения (идея №119 из IDEAS.md): обратный отсчёт до возврата на сервер, полоска
 * прогресса, место в очереди и номер попытки. Появляется сам, когда связь с сервером обрывается.
 */
public final class ReconnectComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-bold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float WIDTH = 156.0f;
    private static final float HEIGHT = 44.0f;
    private static final float PAD = 8.0f;

    private float alpha;
    private long lastFrameNs;

    public ReconnectComp() {
        super("reconnect", 5.0f, 250.0f);
    }

    private static AutoReconnect module() {
        return ModuleManager.get().get(AutoReconnect.class);
    }

    @Override
    public String displayName() {
        return "Переподключение";
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
        return AutoReconnect.counting();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        AutoReconnect module = ReconnectComp.module();
        if (module != null) {
            list.add(new SliderSetting(module.delay));
            list.add(new BoolSetting(module.backoff));
            list.add(new SliderSetting(module.maxAttempts));
            list.add(new BoolSetting(module.notify));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        AutoReconnect module = ReconnectComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((this.isInteractive() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        int seconds = Math.max(0, (int)Math.ceil(AutoReconnect.secondsLeft()));
        int queue = AutoReconnect.queuePlace();
        float progress = Math.max(0.0f, Math.min(1.0f, AutoReconnect.progress()));

        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, WIDTH, HEIGHT, 6.0f, a);
        Render2D.msdfText(FONT_TITLE, "Переподключение", x + PAD, y + PAD, 7.0f, ClientAccent.accent(240.0f * a));

        String secondsText = seconds + " с";
        float secondsWidth = Render2D.msdfWidth(FONT_TITLE, secondsText, 11.0f);
        Render2D.msdfText(FONT_TITLE, secondsText, x + WIDTH - PAD - secondsWidth, y + PAD - 2.5f, 11.0f,
                ColorUtil.rgba(240, 245, 255, Math.round(248.0f * a)));

        String target = AutoReconnect.target();
        Render2D.msdfText(FONT_TEXT, target.isEmpty() ? "сервер" : target, x + PAD, y + PAD + 9.5f, 6.0f,
                ColorUtil.rgba(186, 194, 208, Math.round(190.0f * a)));

        String caption = queue > 0
                ? "Очередь " + queue + (AutoReconnect.queueSize() > 0 ? " из " + AutoReconnect.queueSize() : "") + " · попытка " + AutoReconnect.attemptCount()
                : "Попытка " + AutoReconnect.attemptCount() + (AutoReconnect.lastReason().isEmpty() ? "" : " · " + AutoReconnect.lastReason());
        float captionMax = WIDTH - PAD * 2.0f;
        Render2D.msdfText(FONT_TEXT, ReconnectComp.fit(FONT_TEXT, caption, captionMax, 5.8f), x + PAD, y + PAD + 18.0f, 5.8f,
                ColorUtil.rgba(176, 184, 198, Math.round(170.0f * a)));

        float barY = y + HEIGHT - PAD - 5.0f;
        float barW = WIDTH - PAD * 2.0f;
        Render2D.rect(x + PAD, barY, barW, 4.0f, 2.0f, ColorUtil.rgba(255, 255, 255, Math.round(12.0f * a)));
        if (progress > 0.001f) {
            AccentGradient.fillVertical(x + PAD, barY, barW * progress, 4.0f, 0.0f, 235.0f * a);
        }
        Render2D.msdfText(FONT_TEXT, "Возврат на сервер", x + PAD, barY - 8.0f, 5.4f,
                ColorUtil.rgba(150, 158, 172, Math.round(150.0f * a)));
        Render2D.flush();
    }

    /** Обрезает подпись по ширине, добавляя многоточие. */
    private static String fit(String font, String text, float maxWidth, float size) {
        if (text == null || text.isEmpty() || Render2D.msdfWidth(font, text, size) <= maxWidth) {
            return text == null ? "" : text;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            char symbol = text.charAt(i);
            String candidate = builder.toString() + symbol + "…";
            if (Render2D.msdfWidth(font, candidate, size) > maxWidth) {
                break;
            }
            builder.append(symbol);
        }
        return builder + "…";
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNs == 0L ? 0.016f : (float)(now - this.lastFrameNs) / 1.0E9f;
        this.lastFrameNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }
}
