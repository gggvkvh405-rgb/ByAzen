package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.handbook.Handbook;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Оффлайн-справочник (идея №106 из IDEAS.md).
 * <p>
 * Разделы слева, содержимое справа, поиск сверху — работает мгновенно и без интернета. В разделе
 * координат поиск превращается в калькулятор: введите «1240 -380» и получите пересчёт между Нижним и
 * Верхним миром, расстояние, направление и время в пути.
 */
public final class HandbookScreen
extends BaseScreen {

    private static final float W = 620.0f;
    private static final float H = 404.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 12.0f;
    private static final float FIELD_H = 18.0f;
    private static final float LIST_W = 148.0f;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";

    private final Screen parent;
    private final SearchField search = new SearchField().icon("").placeholder("поиск или координаты «X Z»");
    private final List<Handbook.Section> sections = new ArrayList<Handbook.Section>();
    private final List<String> lines = new ArrayList<String>();
    private final List<Integer> lineSections = new ArrayList<Integer>();

    private float appear;
    private float delta = 0.016f;
    private long lastNs;
    private float scroll;
    private float scrollTarget;
    private int selected;
    private long lastRebuildMs;
    private String status = "";

    public HandbookScreen(Screen parent) {
        super(Text.literal(Lang.t("Справочник ByAzen", "ByAzen handbook")));
        this.parent = parent;
        this.sections.addAll(Handbook.build());
        this.rebuildNow();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /** Добавляет раздел, собранный модулем (команды, клавиши). */
    public void addSection(Handbook.Section section) {
        if (section == null) {
            return;
        }
        this.sections.add(0, section);
        this.lastRebuildMs = 0L;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float contentTop() {
        return HandbookScreen.panelY() + 66.0f;
    }

    private float contentHeight() {
        return HandbookScreen.panelY() + H - 30.0f - this.contentTop();
    }

    private float contentLeft() {
        return HandbookScreen.panelX() + PAD + LIST_W + 10.0f;
    }

    private float contentWidth() {
        return W - PAD * 2.0f - LIST_W - 10.0f;
    }

    private void rebuildNow() {
        this.lines.clear();
        this.lineSections.clear();
        String query = this.search.getText() == null ? "" : this.search.getText().trim();
        List<String> coordinates = Handbook.coordinates(query);
        if (!coordinates.isEmpty()) {
            this.lines.addAll(coordinates);
            for (int i = 0; i < coordinates.size(); ++i) {
                this.lineSections.add(-1);
            }
            this.status = "Пересчёт координат готов · " + coordinates.size() + " строк";
            return;
        }
        if (!query.isEmpty()) {
            List<String> found = Handbook.find(this.sections, query);
            for (String line : found) {
                this.lines.add(line);
                this.lineSections.add(-1);
            }
            this.status = found.isEmpty() ? "Ничего не найдено — попробуйте другое слово" : "Найдено строк: " + found.size();
            return;
        }
        if (this.selected >= 0 && this.selected < this.sections.size()) {
            Handbook.Section section = this.sections.get(this.selected);
            for (String line : section.lines) {
                this.lines.add(line);
                this.lineSections.add(this.selected);
            }
            this.status = section.hint;
        }
    }

    private void rebuild() {
        long now = System.currentTimeMillis();
        if (now - this.lastRebuildMs < 300L) {
            return;
        }
        this.lastRebuildMs = now;
        this.rebuildNow();
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = HandbookScreen.panelX();
        float y = HandbookScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, HandbookScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, "Справочник", x + PAD, y + 20.0f, 12.0f, HandbookScreen.rgba(255, 255, 255, 245.0f * a));
        Render2D.msdfText(FONT_TEXT, "Работает без интернета · Tab — следующий раздел, ввод «X Z» — пересчёт координат",
                x + PAD, y + 34.0f, 5.8f, HandbookScreen.rgba(196, 202, 214, 150.0f * a));
        this.drawClose(x, y, mx, my, a);
        this.search.render(drawContext, x + PAD, y + 46.0f, W - PAD * 2.0f, FIELD_H, a, mx, my, this.delta);
        this.rebuild();

        DrawContext context = drawContext;
        float top = this.contentTop();
        float height = this.contentHeight();
        Render2D.rect(x + PAD, top, LIST_W, height, 8.0f, HandbookScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.pushScissor(context, x + PAD, top, LIST_W, height);
        for (int i = 0; i < this.sections.size(); ++i) {
            float rowY = top + 6.0f + (float)i * 18.0f;
            if (rowY > top + height) {
                break;
            }
            boolean selected = this.selected == i;
            boolean hot = this.hover(mx, my, x + PAD + 5.0f, rowY, LIST_W - 10.0f, 16.0f);
            Render2D.rect(x + PAD + 5.0f, rowY, LIST_W - 10.0f, 16.0f, 6.0f, selected
                    ? ClientAccent.accentSoft(165.0f * a)
                    : HandbookScreen.rgba(255, 255, 255, (hot ? 24.0f : 8.0f) * a));
            Render2D.msdfText(FONT_SEMI, HandbookScreen.trim(this.sections.get(i).title, 20), x + PAD + 11.0f, rowY + 4.6f, 5.9f,
                    selected ? HandbookScreen.rgba(12, 14, 20, 240.0f * a) : HandbookScreen.rgba(226, 232, 244, 232.0f * a));
        }
        Render2D.popScissor(context);

        float left = this.contentLeft();
        float width = this.contentWidth();
        Render2D.rect(left, top, width, height, 8.0f, HandbookScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.pushScissor(context, left, top, width - 6.0f, height);
        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.delta * 14.0f);
        for (int i = 0; i < this.lines.size(); ++i) {
            float rowY = top + 8.0f + (float)i * ROW_H - this.scroll;
            if (rowY + ROW_H < top || rowY > top + height) {
                continue;
            }
            String text = this.lines.get(i);
            boolean bullet = !text.startsWith("[");
            int color = bullet ? HandbookScreen.rgba(214, 222, 236, 230.0f * a) : ClientAccent.accentSoft(215.0f * a);
            if (bullet) {
                Render2D.circle(left + 12.0f, rowY + 5.5f, 1.6f, ClientAccent.accentSoft(190.0f * a));
                Render2D.msdfText(FONT_TEXT, HandbookScreen.trim(text, 92), left + 20.0f, rowY + 2.0f, 5.6f, color);
            }
            else {
                Render2D.msdfText(FONT_TEXT, HandbookScreen.trim(text, 96), left + 20.0f, rowY + 2.0f, 5.6f, color);
            }
        }
        Render2D.popScissor(context);

        float content = (float)this.lines.size() * ROW_H + 16.0f;
        if (content > height) {
            float barHeight = Math.max(24.0f, height * height / content);
            float barY = top + (height - barHeight) * Math.min(1.0f, this.scroll / Math.max(1.0f, content - height));
            Render2D.rect(left + width - 4.0f, barY, 3.0f, barHeight, 1.5f, ClientAccent.accentSoft(180.0f * a));
        }
        Render2D.msdfText(FONT_TEXT, HandbookScreen.trim(this.status, 104), x + PAD, y + H - PAD - 4.0f, 5.8f,
                ClientAccent.accentSoft(220.0f * a));
    }

    private void drawClose(float x, float y, float mx, float my, float a) {
        float size = 18.0f;
        float closeX = x + W - PAD - size;
        float closeY = y + 14.0f;
        boolean hot = this.hover(mx, my, closeX, closeY, size, size);
        Render2D.rect(closeX, closeY, size, size, 6.0f, HandbookScreen.rgba(255, 255, 255, (hot ? 26.0f : 10.0f) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : HandbookScreen.rgba(210, 214, 224, 190.0f * a);
        Render2D.line(closeX + 5.4f, closeY + 5.4f, closeX + size - 5.4f, closeY + size - 5.4f, 1.4f, ink);
        Render2D.line(closeX + size - 5.4f, closeY + 5.4f, closeX + 5.4f, closeY + size - 5.4f, 1.4f, ink);
    }

    private boolean hover(float mx, float my, float x, float y, float width, float height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = HandbookScreen.panelX();
        float y = HandbookScreen.panelY();
        if (this.search.mouseClicked(mx, my, click.button())) {
            return true;
        }
        if (click.button() == 0 && this.hover(mx, my, x + W - PAD - 18.0f, y + 14.0f, 18.0f, 18.0f)) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        float top = this.contentTop();
        float height = this.contentHeight();
        if (mx >= x + PAD && mx <= x + PAD + LIST_W && my >= top && my <= top + height) {
            int index = (int)((my - top - 6.0f) / 18.0f);
            if (index >= 0 && index < this.sections.size()) {
                this.selected = index;
                this.search.setText("");
                this.scroll = 0.0f;
                this.scrollTarget = 0.0f;
                this.lastRebuildMs = 0L;
                Sounds.play("select_category");
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float content = (float)this.lines.size() * ROW_H + 16.0f;
        float maxScroll = Math.max(0.0f, content - this.contentHeight());
        this.scrollTarget = Math.max(0.0f, Math.min(maxScroll, this.scrollTarget - (float)verticalAmount * 20.0f));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256 || input.key() == 27) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (input.key() == 258) {
            this.search.setText("");
            this.selected = (this.selected + 1) % Math.max(1, this.sections.size());
            this.scroll = 0.0f;
            this.scrollTarget = 0.0f;
            this.lastRebuildMs = 0L;
            return true;
        }
        return this.search.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return this.search.charTyped(input);
    }

    @Override
    public void close() {
        this.search.blur();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(this.parent);
        }
    }

    private float updateDelta() {
        long now = System.nanoTime();
        if (this.lastNs != 0L) {
            this.delta = Math.min(0.1f, (float)(now - this.lastNs) / 1.0E9f);
        }
        this.lastNs = now;
        return this.delta;
    }

    private static String trim(String text, int limit) {
        if (text == null) {
            return "";
        }
        String clean = text.replace('\n', ' ').trim();
        if (clean.length() <= limit) {
            return clean;
        }
        return clean.substring(0, Math.max(0, limit - 1)) + "…";
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }
}
