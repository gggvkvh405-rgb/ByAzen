package rtx.byazen.api.ui;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Visuals.Graffiti;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.graffiti.GraffitiStore;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Редактор граффити (идея №68 из IDEAS.md).
 * <p>
 * Собран из векторных слоёв: «Фон» и «Штрихи». Кисть рисует сглаженные линии, ластик убирает
 * ненужные, палитра задаёт цвета, а сохранение собирает слои в PNG с антиалиасингом — рисунок
 * получается мягким, без пиксельной лестницы. Готовый PNG тут же можно повесить на стену.
 */
public final class GraffitiEditorScreen
extends BaseScreen {

    private static final float W = 596.0f;
    private static final float H = 348.0f;
    private static final float PAD = 14.0f;
    private static final float CANVAS = 268.0f;
    private static final String FONT = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-regular";

    private static final String[] PALETTE = {
        "#FFFFFF", "#111318", "#FF4D6D", "#FF8A3D", "#FFD166", "#8CFFB0",
        "#38D6FF", "#5B7CFF", "#B07BFF", "#FF7BD5", "#8A8F9C", "#3BA55D"
    };
    private static final float[] BRUSHES = {1.6f, 3.2f, 6.0f, 11.0f};

    private final List<GraffitiStore.Stroke> strokes = new ArrayList<GraffitiStore.Stroke>();
    private final Deque<List<GraffitiStore.Stroke>> history = new ArrayDeque<List<GraffitiStore.Stroke>>();

    private int mode;                 // 0 — кисть, 1 — ластик, 2 — фон
    private int brushIndex = 1;
    private int colorIndex = 6;
    private String background = "#181B22";
    private boolean backgroundVisible = true;
    private boolean strokesVisible = true;
    private boolean painting;
    private float lastCanvasX = -1.0f;
    private float lastCanvasY = -1.0f;
    private String status = "";
    private String nameBuffer = "graffiti";
    private boolean naming;
    private int pictureIndex;

    public GraffitiEditorScreen() {
        super(Text.literal("Граффити"));
        Graffiti module = ModuleManager.get().get(Graffiti.class);
        if (module != null && module.file.getText() != null && !module.file.getText().isBlank()) {
            String value = module.file.getText();
            this.nameBuffer = value.toLowerCase(Locale.ROOT).endsWith(".png")
                    ? value.substring(0, value.length() - 4)
                    : value;
        }
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float canvasX() {
        return GraffitiEditorScreen.panelX() + PAD;
    }

    private float canvasY() {
        return GraffitiEditorScreen.panelY() + 44.0f;
    }

    private float toolsX() {
        return this.canvasX() + CANVAS + 18.0f;
    }

    private float toolsWidth() {
        return W - (CANVAS + PAD * 2.0f + 18.0f);
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float alpha = 1.0f;
        float x = GraffitiEditorScreen.panelX();
        float y = GraffitiEditorScreen.panelY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f,
                GraffitiEditorScreen.rgba(4, 5, 9, 150.0f));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, alpha, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, alpha);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * alpha));
        Render2D.msdfText(FONT, "Редактор граффити", x + PAD, y + 14.0f, 10.0f,
                GraffitiEditorScreen.rgba(236, 241, 250, 246.0f));
        Render2D.msdfText(FONT_TEXT, "Слои: фон и штрихи · сохранение в PNG с мягким сглаживанием", x + PAD, y + 28.0f, 6.0f,
                GraffitiEditorScreen.rgba(158, 166, 184, 220.0f));
        this.drawClose(drawContext, x, y, mouseX, mouseY);
        this.drawCanvas(drawContext);
        this.drawTools(drawContext, mouseX, mouseY, deltaTicks);
        this.pollPaint(mouseX, mouseY);
    }

    private void drawClose(DrawContext drawContext, float x, float y, int mouseX, int mouseY) {
        float size = 18.0f;
        float bx = x + W - PAD - size;
        float by = y + 12.0f;
        boolean hot = (float)mouseX >= bx && (float)mouseX <= bx + size && (float)mouseY >= by && (float)mouseY <= by + size;
        Render2D.rect(bx, by, size, size, 6.0f, GraffitiEditorScreen.rgba(255, 255, 255, hot ? 26.0f : 10.0f));
        int ink = GraffitiEditorScreen.rgba(228, 234, 246, 235.0f);
        Render2D.line(bx + 5.4f, by + 5.4f, bx + size - 5.4f, by + size - 5.4f, 1.4f, ink);
        Render2D.line(bx + size - 5.4f, by + 5.4f, bx + 5.4f, by + size - 5.4f, 1.4f, ink);
    }

    private void drawCanvas(DrawContext drawContext) {
        float cx = this.canvasX();
        float cy = this.canvasY();
        Render2D.pushScissor(drawContext, cx, cy, CANVAS, CANVAS);
        if (this.backgroundVisible && this.background != null) {
            int color = GraffitiEditorScreen.parseColor(this.background, 255);
            Render2D.rect(cx, cy, CANVAS, CANVAS, 10.0f, color);
        }
        else {
            Render2D.rect(cx, cy, CANVAS, CANVAS, 10.0f, GraffitiEditorScreen.rgba(255, 255, 255, 8.0f));
        }
        if (this.strokesVisible) {
            for (GraffitiStore.Stroke stroke : this.strokes) {
                Render2D.line(cx + stroke.x1 * CANVAS, cy + stroke.y1 * CANVAS,
                        cx + stroke.x2 * CANVAS, cy + stroke.y2 * CANVAS,
                        Math.max(1.0f, stroke.width * CANVAS / 128.0f),
                        GraffitiEditorScreen.parseColor(stroke.color, 255));
            }
        }
        Render2D.popScissor(drawContext);
        Render2D.outline(cx - 0.5f, cy - 0.5f, CANVAS + 1.0f, CANVAS + 1.0f, 10.0f, 1.0f,
                ClientAccent.accentSoft(70.0f));
        if (this.mode == 2) {
            Render2D.msdfText(FONT_TEXT, "Режим фона: выберите цвет", cx, cy + CANVAS + 5.0f, 6.0f,
                    GraffitiEditorScreen.rgba(170, 178, 196, 220.0f));
        }
    }

    private void drawTools(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float x = this.toolsX();
        float y = this.canvasY();
        float width = this.toolsWidth();
        Render2D.msdfText(FONT, "Инструмент", x, y, 6.6f, GraffitiEditorScreen.rgba(200, 208, 224, 230.0f));
        String[] modes = {"Кисть", "Ластик", "Фон"};
        float chipWidth = (width - 8.0f) / 3.0f;
        for (int i = 0; i < modes.length; ++i) {
            float bx = x + (chipWidth + 4.0f) * (float)i;
            boolean selected = this.mode == i;
            boolean hot = this.hover(mouseX, mouseY, bx, y + 9.0f, chipWidth, 14.0f);
            Render2D.rect(bx, y + 9.0f, chipWidth, 14.0f, 6.0f, selected
                    ? ClientAccent.accentSoft(150.0f)
                    : GraffitiEditorScreen.rgba(255, 255, 255, hot ? 22.0f : 10.0f));
            float textWidth = Render2D.textWidth(FONT, modes[i], 5.8f);
            Render2D.msdfText(FONT, modes[i], bx + (chipWidth - textWidth) * 0.5f, y + 13.8f, 5.8f,
                    selected ? GraffitiEditorScreen.rgba(10, 12, 18, 240.0f) : GraffitiEditorScreen.rgba(220, 228, 242, 235.0f));
        }

        float paletteY = y + 34.0f;
        Render2D.msdfText(FONT, "Цвета", x, paletteY, 6.6f, GraffitiEditorScreen.rgba(200, 208, 224, 230.0f));
        float swatch = (width - 5.0f * 4.0f) / 6.0f;
        for (int i = 0; i < PALETTE.length; ++i) {
            int column = i % 6;
            int row = i / 6;
            float bx = x + (swatch + 4.0f) * (float)column;
            float by = paletteY + 10.0f + (swatch + 4.0f) * (float)row;
            boolean selected = this.colorIndex == i;
            boolean hot = this.hover(mouseX, mouseY, bx, by, swatch, swatch);
            Render2D.rect(bx, by, swatch, swatch, 5.0f, GraffitiEditorScreen.parseColor(PALETTE[i], 255));
            if (selected) {
                Render2D.outline(bx - 1.0f, by - 1.0f, swatch + 2.0f, swatch + 2.0f, 6.0f, 1.4f,
                        ClientAccent.accentOpaque());
            }
            else if (hot) {
                Render2D.outline(bx - 1.0f, by - 1.0f, swatch + 2.0f, swatch + 2.0f, 6.0f, 1.0f,
                        GraffitiEditorScreen.rgba(255, 255, 255, 90.0f));
            }
        }

        float brushY = paletteY + 10.0f + (swatch + 4.0f) * 2.0f + 8.0f;
        Render2D.msdfText(FONT, "Толщина", x, brushY, 6.6f, GraffitiEditorScreen.rgba(200, 208, 224, 230.0f));
        for (int i = 0; i < BRUSHES.length; ++i) {
            float bx = x + (chipWidth + 4.0f) * (float)i * 0.75f;
            float chipW = chipWidth * 0.75f;
            boolean selected = this.brushIndex == i;
            Render2D.rect(bx, brushY + 9.0f, chipW, 13.0f, 6.0f, selected
                    ? ClientAccent.accentSoft(140.0f)
                    : GraffitiEditorScreen.rgba(255, 255, 255, 12.0f));
            float dotSize = 1.6f + (float)i * 1.7f;
            Render2D.rect(bx + chipW * 0.5f - dotSize * 0.5f, brushY + 15.5f - dotSize * 0.5f, dotSize, dotSize, dotSize * 0.5f,
                    selected ? GraffitiEditorScreen.rgba(10, 12, 18, 240.0f) : GraffitiEditorScreen.rgba(224, 232, 245, 230.0f));
        }

        float layerY = brushY + 30.0f;
        Render2D.msdfText(FONT, "Слои", x, layerY, 6.6f, GraffitiEditorScreen.rgba(200, 208, 224, 230.0f));
        this.drawLayerRow(x, layerY + 10.0f, width, "Фон", this.backgroundVisible, mouseX, mouseY);
        this.drawLayerRow(x, layerY + 28.0f, width, "Штрихи (" + this.strokes.size() + ")", this.strokesVisible, mouseX, mouseY);

        float buttonY = layerY + 48.0f;
        float half = (width - 6.0f) * 0.5f;
        this.drawButton(x, buttonY, half, "Отменить", this.hover(mouseX, mouseY, x, buttonY, half, 16.0f), false);
        this.drawButton(x + half + 6.0f, buttonY, half, "Очистить", this.hover(mouseX, mouseY, x + half + 6.0f, buttonY, half, 16.0f), false);
        this.drawButton(x, buttonY + 20.0f, half, "Сохранить PNG", this.hover(mouseX, mouseY, x, buttonY + 20.0f, half, 16.0f), true);
        this.drawButton(x + half + 6.0f, buttonY + 20.0f, half, "Файлы ‹ ›", this.hover(mouseX, mouseY, x + half + 6.0f, buttonY + 20.0f, half, 16.0f), false);

        float nameY = buttonY + 42.0f;
        Render2D.rect(x, nameY, width, 16.0f, 6.0f, GraffitiEditorScreen.rgba(10, 12, 18, 200.0f));
        Render2D.outline(x, nameY, width, 16.0f, 6.0f, 1.0f,
                this.naming ? ClientAccent.accentSoft(150.0f) : ClientAccent.accentSoft(70.0f));
        String nameText = this.nameBuffer + (this.naming ? "_" : ".png");
        Render2D.msdfText(FONT_TEXT, nameText, x + 5.0f, nameY + 5.0f, 6.0f,
                GraffitiEditorScreen.rgba(228, 234, 246, 240.0f));
        if (!this.status.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, this.status, x, nameY + 22.0f, 5.8f,
                    GraffitiEditorScreen.rgba(160, 214, 180, 235.0f));
        }
    }

    private void drawLayerRow(float x, float y, float width, String label, boolean visible, int mouseX, int mouseY) {
        boolean hot = this.hover(mouseX, mouseY, x, y, width, 15.0f);
        Render2D.rect(x, y, width, 15.0f, 6.0f, GraffitiEditorScreen.rgba(255, 255, 255, hot ? 20.0f : 10.0f));
        Render2D.msdfText(FONT_TEXT, label, x + 6.0f, y + 4.4f, 5.8f,
                GraffitiEditorScreen.rgba(220, 228, 242, visible ? 240.0f : 130.0f));
        float markerX = x + width - 16.0f;
        Render2D.rect(markerX, y + 4.0f, 10.0f, 7.0f, 3.5f,
                visible ? ClientAccent.accentOpaque() : GraffitiEditorScreen.rgba(255, 255, 255, 40.0f));
    }

    private void drawButton(float x, float y, float width, String label, boolean hot, boolean accent) {
        Render2D.rect(x, y, width, 16.0f, 6.0f, accent
                ? ClientAccent.accentSoft(hot ? 200.0f : 165.0f)
                : GraffitiEditorScreen.rgba(255, 255, 255, hot ? 26.0f : 12.0f));
        float textWidth = Render2D.textWidth(FONT, label, 5.8f);
        Render2D.msdfText(FONT, label, x + (width - textWidth) * 0.5f, y + 5.0f, 5.8f,
                accent ? GraffitiEditorScreen.rgba(10, 12, 18, 245.0f) : GraffitiEditorScreen.rgba(222, 230, 244, 235.0f));
    }

    private boolean hover(int mouseX, int mouseY, float x, float y, float width, float height) {
        return (float)mouseX >= x && (float)mouseX <= x + width && (float)mouseY >= y && (float)mouseY <= y + height;
    }

    /** Пока кнопка мыши зажата, кисть ведёт линию — так штрих выходит плавным. */
    private void pollPaint(int mouseX, int mouseY) {
        if (!this.painting) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null
                || GLFW.glfwGetMouseButton(client.getWindow().getHandle(), 0) != 1) {
            this.painting = false;
            this.lastCanvasX = -1.0f;
            return;
        }
        this.paint((float)mouseX, (float)mouseY);
    }

    private void paint(float mouseX, float mouseY) {
        float nx = (mouseX - this.canvasX()) / CANVAS;
        float ny = (mouseY - this.canvasY()) / CANVAS;
        if (nx < 0.0f || nx > 1.0f || ny < 0.0f || ny > 1.0f) {
            return;
        }
        if (this.mode == 1) {
            this.erase(nx, ny);
            return;
        }
        if (this.lastCanvasX < 0.0f) {
            this.lastCanvasX = nx;
            this.lastCanvasY = ny;
            return;
        }
        float dx = nx - this.lastCanvasX;
        float dy = ny - this.lastCanvasY;
        if (dx * dx + dy * dy < 0.00004f) {
            return;
        }
        this.pushHistory();
        this.strokes.add(new GraffitiStore.Stroke(this.lastCanvasX, this.lastCanvasY, nx, ny,
                BRUSHES[this.brushIndex], PALETTE[this.colorIndex]));
        this.lastCanvasX = nx;
        this.lastCanvasY = ny;
    }

    private void erase(float nx, float ny) {
        float threshold = 0.035f + BRUSHES[this.brushIndex] * 0.004f;
        int before = this.strokes.size();
        this.strokes.removeIf(stroke -> GraffitiEditorScreen.distanceToSegment(nx, ny, stroke) < threshold);
        if (this.strokes.size() != before) {
            this.status = "Ластик убрал " + (before - this.strokes.size());
        }
    }

    private static float distanceToSegment(float px, float py, GraffitiStore.Stroke stroke) {
        float dx = stroke.x2 - stroke.x1;
        float dy = stroke.y2 - stroke.y1;
        float lengthSquared = dx * dx + dy * dy;
        if (lengthSquared < 1.0E-6f) {
            return (float)Math.hypot(px - stroke.x1, py - stroke.y1);
        }
        float t = Math.max(0.0f, Math.min(1.0f, ((px - stroke.x1) * dx + (py - stroke.y1) * dy) / lengthSquared));
        return (float)Math.hypot(px - (stroke.x1 + t * dx), py - (stroke.y1 + t * dy));
    }

    private void pushHistory() {
        if (this.history.size() > 24) {
            this.history.removeLast();
        }
        this.history.push(new ArrayList<GraffitiStore.Stroke>(this.strokes));
    }

    private void undo() {
        if (this.history.isEmpty()) {
            return;
        }
        this.strokes.clear();
        this.strokes.addAll(this.history.pop());
        this.status = "Шаг отменён";
        Sounds.play("settings_close");
    }

    private void save() {
        String name = this.nameBuffer.isBlank() ? GraffitiStore.nextName() : this.nameBuffer.trim() + ".png";
        Path path = GraffitiStore.savePng(name, this.backgroundVisible ? this.background : null, this.strokes);
        if (path == null) {
            this.status = "Не удалось сохранить рисунок";
            return;
        }
        Graffiti module = ModuleManager.get().get(Graffiti.class);
        if (module != null) {
            module.file.setText(path.getFileName().toString());
        }
        this.status = "Сохранено: " + path.getFileName();
        Sounds.play("select_category");
    }

    private void cyclePicture() {
        List<Path> files = GraffitiStore.saved();
        if (files.isEmpty()) {
            this.status = "В папке пока нет PNG";
            return;
        }
        this.pictureIndex = (this.pictureIndex + 1) % files.size();
        String name = files.get(this.pictureIndex).getFileName().toString();
        this.nameBuffer = name.toLowerCase(Locale.ROOT).endsWith(".png")
                ? name.substring(0, name.length() - 4)
                : name;
        this.status = "Выбран файл: " + name;
        Sounds.play("select_category");
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = GraffitiEditorScreen.panelX();
        float y = GraffitiEditorScreen.panelY();
        if (click.button() == 1) {
            this.close();
            return true;
        }
        float closeSize = 18.0f;
        if (mx >= x + W - PAD - closeSize && mx <= x + W - PAD && my >= y + 12.0f && my <= y + 12.0f + closeSize) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (mx >= this.canvasX() && mx <= this.canvasX() + CANVAS && my >= this.canvasY() && my <= this.canvasY() + CANVAS) {
            if (this.mode == 2) {
                this.background = PALETTE[this.colorIndex];
                this.backgroundVisible = true;
                Sounds.play("select_category");
                return true;
            }
            this.painting = true;
            this.lastCanvasX = -1.0f;
            this.paint(mx, my);
            return true;
        }
        float tx = this.toolsX();
        float ty = this.canvasY();
        float width = this.toolsWidth();
        float chipWidth = (width - 8.0f) / 3.0f;
        for (int i = 0; i < 3; ++i) {
            float bx = tx + (chipWidth + 4.0f) * (float)i;
            if (this.hover((int)mx, (int)my, bx, ty + 9.0f, chipWidth, 14.0f)) {
                this.mode = i;
                this.painting = false;
                Sounds.play("select_category");
                return true;
            }
        }
        float paletteY = ty + 34.0f;
        float swatch = (width - 5.0f * 4.0f) / 6.0f;
        for (int i = 0; i < PALETTE.length; ++i) {
            float bx = tx + (swatch + 4.0f) * (float)(i % 6);
            float by = paletteY + 10.0f + (swatch + 4.0f) * (float)(i / 6);
            if (this.hover((int)mx, (int)my, bx, by, swatch, swatch)) {
                this.colorIndex = i;
                if (this.mode == 2) {
                    this.background = PALETTE[i];
                }
                Sounds.play("select_category");
                return true;
            }
        }
        float brushY = paletteY + 10.0f + (swatch + 4.0f) * 2.0f + 8.0f;
        for (int i = 0; i < BRUSHES.length; ++i) {
            float bx = tx + (chipWidth + 4.0f) * (float)i * 0.75f;
            if (this.hover((int)mx, (int)my, bx, brushY + 9.0f, chipWidth * 0.75f, 13.0f)) {
                this.brushIndex = i;
                Sounds.play("select_category");
                return true;
            }
        }
        float layerY = brushY + 30.0f;
        if (this.hover((int)mx, (int)my, tx, layerY + 10.0f, width, 15.0f)) {
            this.backgroundVisible = !this.backgroundVisible;
            Sounds.play("select_category");
            return true;
        }
        if (this.hover((int)mx, (int)my, tx, layerY + 28.0f, width, 15.0f)) {
            this.strokesVisible = !this.strokesVisible;
            Sounds.play("select_category");
            return true;
        }
        float buttonY = layerY + 48.0f;
        float half = (width - 6.0f) * 0.5f;
        if (this.hover((int)mx, (int)my, tx, buttonY, half, 16.0f)) {
            this.undo();
            return true;
        }
        if (this.hover((int)mx, (int)my, tx + half + 6.0f, buttonY, half, 16.0f)) {
            if (!this.strokes.isEmpty()) {
                this.pushHistory();
            }
            this.strokes.clear();
            this.status = "Холст очищен";
            Sounds.play("settings_close");
            return true;
        }
        if (this.hover((int)mx, (int)my, tx, buttonY + 20.0f, half, 16.0f)) {
            this.save();
            return true;
        }
        if (this.hover((int)mx, (int)my, tx + half + 6.0f, buttonY + 20.0f, half, 16.0f)) {
            this.cyclePicture();
            return true;
        }
        float nameY = buttonY + 42.0f;
        if (this.hover((int)mx, (int)my, tx, nameY, width, 16.0f)) {
            this.naming = true;
            return true;
        }
        if (!(mx >= x && mx <= x + W && my >= y && my <= y + H)) {
            this.close();
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(Click click) {
        this.painting = false;
        this.lastCanvasX = -1.0f;
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == 256 || key == 27) {
            this.close();
            return true;
        }
        if (key == 257 || key == 335) {
            this.save();
            return true;
        }
        if (key == 259 && this.naming && this.nameBuffer.length() > 0) {
            this.nameBuffer = this.nameBuffer.substring(0, this.nameBuffer.length() - 1);
            return true;
        }
        if (key == 90 && GraffitiEditorScreen.ctrlDown()) {
            this.undo();
            return true;
        }
        if (key == 82 && GraffitiEditorScreen.ctrlDown()) {
            this.cyclePicture();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (!this.naming || this.nameBuffer.length() >= 24) {
            return false;
        }
        int codepoint = input.codepoint();
        if (codepoint < 32) {
            return false;
        }
        char letter = (char)codepoint;
        if (Character.isLetterOrDigit(letter) || letter == '_' || letter == '-') {
            this.nameBuffer = this.nameBuffer + Character.toLowerCase(letter);
            return true;
        }
        return false;
    }

    private static boolean ctrlDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        return GLFW.glfwGetKey(client.getWindow().getHandle(), 341) == 1
                || GLFW.glfwGetKey(client.getWindow().getHandle(), 345) == 1;
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }

    private static int parseColor(String value, int alpha) {
        try {
            int rgb = Integer.parseInt(value.replace("#", ""), 16);
            return alpha << 24 | rgb & 0xFFFFFF;
        }
        catch (Throwable throwable) {
            return alpha << 24 | 0xFFFFFF;
        }
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }
}
