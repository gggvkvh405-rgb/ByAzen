package rtx.byazen.api.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.settings.SettingsSearch;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Палитра «найти настройку» (идея №40 из IDEAS.md): открывается по Ctrl+F в ClickGui.
 * <p>
 * Ввод идёт по названиям модулей, названиям настроек и их описаниям; выбранный результат открывает
 * окно настроек нужного модуля и подсвечивает найденную строку. Отрисовка полностью векторная:
 * скруглённая панель, мягкая тень, анимированные строки, сглаженный шрифт.
 */
public final class SettingsSearchPalette {

    public static final float WIDTH = 320.0f;
    private static final float FIELD_H = 22.0f;
    private static final float ROW_H = 24.0f;
    private static final float ROW_STEP = 26.0f;
    private static final float PAD = 8.0f;
    private static final int MAX_RESULTS = 9;
    private static final String FONT_TITLE = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";

    private final SearchField field = new SearchField();
    private boolean open;
    private float anim;
    private float scroll;
    private long lastQueryAt;
    private String lastQuery = "\u0000";
    private boolean draggingScroll;
    private int hovered = -1;
    private final List<SettingsSearch.Entry> results = new ArrayList<SettingsSearch.Entry>();

    public SettingsSearchPalette() {
        this.field.placeholder("Найти настройку… (например: громкость, HUD, прицел)");
    }

    public boolean isOpen() {
        return this.open || this.anim > 0.01f;
    }

    public boolean isTyping() {
        return this.open && this.field.isTyping();
    }

    public void openPalette() {
        this.open = true;
        this.field.setText("");
        this.field.focus();
        this.results.clear();
        this.scroll = 0.0f;
        this.lastQuery = "\u0000";
        this.lastQueryAt = System.currentTimeMillis();
    }

    public void closePalette() {
        this.open = false;
        this.field.blur();
        this.results.clear();
    }

    // ------------------------------------------------------------------ geometry

    private float panelX() {
        return (Position.screenWidth() - WIDTH) * 0.5f;
    }

    private float panelY() {
        return Math.max(24.0f, Position.screenHeight() * 0.22f);
    }

    private float listHeight() {
        return Math.max(ROW_H + 6.0f, Math.min(MAX_RESULTS, Math.max(1, this.results.size())) * ROW_STEP + 4.0f);
    }

    private int rowAt(float mouseX, float mouseY) {
        float top = this.panelY() + FIELD_H + PAD;
        float left = this.panelX() + PAD;
        float width = WIDTH - PAD * 2.0f;
        if (mouseX < left || mouseX > left + width || mouseY < top || mouseY > top + this.listHeight()) {
            return -1;
        }
        int index = (int) ((mouseY - top + this.scroll) / ROW_STEP);
        return index >= 0 && index < this.results.size() ? index : -1;
    }

    // ------------------------------------------------------------------ rendering

    public void render(DrawContext drawContext, float alpha) {
        this.anim += ((this.open ? 1.0f : 0.0f) - this.anim) * 0.18f;
        if (this.anim <= 0.01f) {
            return;
        }
        float a = this.anim * alpha;
        float x = this.panelX();
        float y = this.panelY() + (1.0f - this.anim) * -10.0f;
        float height = FIELD_H + PAD * 2.0f + this.listHeight();
        Render2D.blur(x, y, WIDTH, height, 10.0f, 12.0f, a, -1);
        Render2D.rect(x, y, WIDTH, height, 10.0f, SettingsSearchPalette.rgba(10, 11, 15, 235.0f * a));
        Render2D.outline(x, y, WIDTH, height, 10.0f, 0.9f, ClientAccent.accentSoft(60.0f * a));
        this.field.render(drawContext, x + PAD, y + PAD, WIDTH - PAD * 2.0f, FIELD_H, a, Position.mouseX(), Position.mouseY(), 0.016f);
        float top = y + PAD + FIELD_H + PAD;
        float left = x + PAD;
        float width = WIDTH - PAD * 2.0f;
        this.hovered = this.rowAt(Position.mouseX(), Position.mouseY());
        if (this.results.isEmpty()) {
            String hint = this.field.getText().isBlank()
                    ? "Начните вводить название настройки или модуля"
                    : "Ничего не найдено";
            Render2D.msdfText(FONT_TEXT, hint, left + 4.0f, top + 6.0f, 6.4f, SettingsSearchPalette.rgba(190, 196, 208, 160.0f * a));
            return;
        }
        Render2D.pushScissor(drawContext, left, top, width, this.listHeight());
        for (int i = 0; i < this.results.size(); ++i) {
            SettingsSearch.Entry entry = this.results.get(i);
            float ry = top + (float) i * ROW_STEP - this.scroll;
            boolean hot = i == this.hovered;
            if (hot) {
                Render2D.rect(left, ry, width, ROW_H, 6.0f, ClientAccent.accentSoft(38.0f * a));
            }
            if (entry.favorite()) {
                Render2D.rect(left + 3.0f, ry + 5.0f, 2.0f, ROW_H - 10.0f, 1.0f, ClientAccent.accent(220.0f * a));
            }
            String badge = entry.settingName() == null ? "МОДУЛЬ" : entry.module().getDisplayName();
            Render2D.msdfText(FONT_TITLE, entry.title(), left + 10.0f, ry + 4.0f, 7.0f,
                    SettingsSearchPalette.rgba(255, 255, 255, 240.0f * a));
            String subtitle = entry.subtitle() == null ? "" : entry.subtitle();
            Render2D.msdfText(FONT_TEXT, SettingsSearchPalette.trim(subtitle, 62), left + 10.0f, ry + 13.5f, 5.9f,
                    SettingsSearchPalette.rgba(192, 198, 210, 155.0f * a));
            float badgeWidth = Render2D.msdfWidth(FONT_TEXT, badge, 5.4f) + 10.0f;
            float badgeX = left + width - badgeWidth - 6.0f;
            Render2D.rect(badgeX, ry + 7.0f, badgeWidth, 10.0f, 5.0f, SettingsSearchPalette.rgba(255, 255, 255, 16.0f * a));
            Render2D.msdfText(FONT_TEXT, badge, badgeX + 5.0f, ry + 12.0f, 5.4f,
                    SettingsSearchPalette.rgba(206, 212, 226, 170.0f * a));
        }
        Render2D.popScissor(drawContext);
        float total = (float) this.results.size() * ROW_STEP;
        if (total > this.listHeight() + 0.5f) {
            float max = total - this.listHeight();
            float thumbHeight = Math.max(18.0f, this.listHeight() * this.listHeight() / total);
            float thumbY = top + (this.listHeight() - thumbHeight) * (this.scroll / Math.max(1.0f, max));
            Render2D.rect(left + width - 2.0f, thumbY, 3.0f, thumbHeight, 1.5f, ClientAccent.accentSoft(140.0f * a));
        }
        Render2D.msdfText(FONT_TEXT, "Enter — открыть, ↑↓ — выбор, Esc — закрыть",
                left + 4.0f, top + this.listHeight() + 3.0f, 5.4f, SettingsSearchPalette.rgba(170, 176, 190, 130.0f * a));
    }

    // ------------------------------------------------------------------ input

    public boolean keyPressed(KeyInput input) {
        if (!this.open) {
            return false;
        }
        int key = input.key();
        if (key == 256) {
            this.closePalette();
            return true;
        }
        if (key == 257 || key == 335) {
            if (this.hovered >= 0 && this.hovered < this.results.size()) {
                this.choose(this.results.get(this.hovered));
            }
            else if (!this.results.isEmpty()) {
                this.choose(this.results.get(0));
            }
            return true;
        }
        if (key == 264 || key == 265) {
            if (!this.results.isEmpty()) {
                int step = key == 264 ? 1 : -1;
                this.hovered = Math.floorMod(this.hovered + step, this.results.size());
                this.keepVisible(this.hovered);
                Sounds.play("select_category");
            }
            return true;
        }
        return this.field.keyPressed(input);
    }

    public boolean charTyped(CharInput input) {
        if (!this.open) {
            return false;
        }
        return this.field.charTyped(input);
    }

    public boolean mouseClicked(Click click) {
        if (!this.open) {
            return false;
        }
        if (this.field.mouseClicked(Position.mouseX(), Position.mouseY(), click.button())) {
            return true;
        }
        int index = this.rowAt(Position.mouseX(), Position.mouseY());
        if (index >= 0 && click.button() == 0) {
            this.choose(this.results.get(index));
            return true;
        }
        float x = this.panelX();
        float y = this.panelY();
        float height = FIELD_H + PAD * 2.0f + this.listHeight();
        if (Position.mouseX() < x || Position.mouseX() > x + WIDTH || Position.mouseY() < y || Position.mouseY() > y + height) {
            this.closePalette();
            return true;
        }
        return true;
    }

    public boolean mouseReleased(int button) {
        this.field.mouseReleased(button);
        this.draggingScroll = false;
        return this.open;
    }

    public boolean mouseScrolled(double amount) {
        if (!this.open) {
            return false;
        }
        float total = (float) this.results.size() * ROW_STEP;
        float max = Math.max(0.0f, total - this.listHeight());
        this.scroll = Math.max(0.0f, Math.min(max, this.scroll - (float) amount * 18.0f));
        return true;
    }

    /** Пересчитывает результаты при изменении строки поиска (с небольшой задержкой). */
    public void tick() {
        if (!this.open) {
            return;
        }
        String query = this.field.getText().trim();
        if (query.equals(this.lastQuery)) {
            return;
        }
        if (System.currentTimeMillis() - this.lastQueryAt < 140L) {
            return;
        }
        this.lastQueryAt = System.currentTimeMillis();
        this.lastQuery = query;
        this.results.clear();
        if (!query.isBlank()) {
            this.results.addAll(SettingsSearch.search(query, MAX_RESULTS));
        }
        this.scroll = 0.0f;
        this.hovered = this.results.isEmpty() ? -1 : 0;
    }

    private void keepVisible(int index) {
        float top = (float) index * ROW_STEP;
        if (top < this.scroll) {
            this.scroll = top;
        }
        else if (top + ROW_H > this.scroll + this.listHeight()) {
            this.scroll = top + ROW_H - this.listHeight();
        }
    }

    private void choose(SettingsSearch.Entry entry) {
        if (entry == null) {
            return;
        }
        Sounds.play("module_settings_open");
        UI.openModuleSettings(entry.module(), entry.settingName());
        this.closePalette();
    }

    private static String trim(String text, int limit) {
        if (text == null || text.length() <= limit) {
            return text == null ? "" : text;
        }
        return text.substring(0, limit - 1) + "…";
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        return new Color(r, g, b, a).getRGB();
    }
}
