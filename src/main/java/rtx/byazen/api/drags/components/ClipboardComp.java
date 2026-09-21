package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.impl.Utils.ClipboardModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.api.drags.Position;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Виджет «недавние» (идея №39 из IDEAS.md): последние записи буфера обмена, клик возвращает запись обратно.
 */
public final class ClipboardComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-bold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float TITLE_SIZE = 7.0f;
    private static final float ROW_SIZE = 6.2f;
    private static final float PAD_X = 9.0f;
    private static final float PAD_Y = 6.0f;
    private static final float ROW_STEP = 9.0f;

    private float currentWidth = 150.0f;
    private float currentHeight = 26.0f;
    private float alpha;
    private long lastFrameNs;
    private String copied = "";
    private float copiedTime;

    public ClipboardComp() {
        super("clipboard_recent", 5.0f, 120.0f);
    }

    private static ClipboardModule module() {
        return ClipboardModule.instance();
    }

    @Override
    public String displayName() {
        return "Недавние";
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
        ClipboardModule module = ClipboardComp.module();
        return module != null && module.isEnabled() && module.showWidget.getValue();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        ClipboardModule module = ClipboardComp.module();
        if (module != null) {
            list.add(new BoolSetting(module.showWidget));
            list.add(new SliderSetting(module.limit));
            list.add(new BoolSetting(module.skipShort));
            list.add(new BoolSetting(module.chatNotice));
        }
        return list;
    }

    /** Клик по строке возвращает запись в буфер обмена. */
    public boolean click(int button, boolean onlyButtons) {
        ClipboardModule module = ClipboardComp.module();
        if (module == null || !module.isEnabled() || !module.showWidget.getValue() || button != 0) {
            return false;
        }
        List<String> entries = module.entries();
        if (entries.isEmpty()) {
            return false;
        }
        float x = this.getX();
        float y = this.getY();
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        if (mouseX < x || mouseX > x + this.currentWidth || mouseY < y || mouseY > y + this.currentHeight) {
            return false;
        }
        int index = (int) ((mouseY - (y + PAD_Y + ROW_STEP)) / ROW_STEP);
        if (index < 0 || index >= entries.size()) {
            return false;
        }
        String value = entries.get(index);
        module.copy(value);
        this.copied = ClipboardModule.shorten(value);
        this.copiedTime = 1.4f;
        return true;
    }

    private static int rgba(int r, int g, int b, float alpha) {
        return rtx.byazen.utils.color.ColorUtil.rgba(r, g, b, Math.round(Math.max(0.0f, Math.min(255.0f, alpha))));
    }

    @Override
    protected void render(DrawContext drawContext) {
        ClipboardModule module = ClipboardComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((this.isInteractive() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        List<String> entries = module.entries();
        if (entries.isEmpty()) {
            return;
        }
        this.copiedTime = Math.max(0.0f, this.copiedTime - delta);
        float width = 150.0f;
        float height = PAD_Y * 2.0f + TITLE_SIZE + 3.0f + (float) entries.size() * ROW_STEP;
        this.currentWidth = width;
        this.currentHeight = height;
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;

        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, width, height, 6.0f, a);
        Render2D.msdfText(FONT_TITLE, "Недавние", x + PAD_X, y + PAD_Y, TITLE_SIZE, ClientAccent.accent(240.0f * a));
        float rowY = y + PAD_Y + TITLE_SIZE + 3.0f;
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        for (int i = 0; i < entries.size(); ++i) {
            boolean hot = mouseX >= x && mouseX <= x + width && mouseY >= rowY && mouseY <= rowY + ROW_STEP;
            if (hot) {
                Render2D.rect(x + 4.0f, rowY - 1.0f, width - 8.0f, ROW_STEP, 4.0f, ClientAccent.accentSoft(28.0f * a));
            }
            Render2D.msdfText(FONT_TEXT, ClipboardModule.shorten(entries.get(i)), x + PAD_X, rowY + 1.0f, ROW_SIZE,
                    ClipboardComp.rgba(226, 231, 240, (hot ? 250.0f : 205.0f) * a));
            rowY += ROW_STEP;
        }
        if (this.copiedTime > 0.0f) {
            String hint = "Скопировано: " + this.copied;
            float hintWidth = Render2D.msdfWidth(FONT_TEXT, hint, 5.6f);
            AccentGradient.fillHorizontal(x + (width - hintWidth - 12.0f) * 0.5f, y + height - 12.0f,
                    hintWidth + 12.0f, 11.0f, 5.0f, 150.0f * a * Math.min(1.0f, this.copiedTime));
            Render2D.msdfText(FONT_TEXT, hint, x + (width - hintWidth) * 0.5f, y + height - 11.0f, 5.6f,
                    ClipboardComp.rgba(255, 255, 255, 240.0f * a * Math.min(1.0f, this.copiedTime)));
        }
        Render2D.flush();
    }
}
