package rtx.byazen.api.ui;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
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
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.render.LocalImages;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.screenshot.Screenshots;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Аннотатор скриншотов (идея №109 из IDEAS.md).
 * <p>
 * Рисует на снимке стрелки, рамки, круги, линии и подписи: всё векторно и с мягкими краями. Разметка
 * сохраняется отдельной копией в исходном разрешении с антиалиасингом, поэтому исходный файл остаётся
 * нетронутым, а картинка — четкой даже на большом экране.
 */
public final class ScreenshotAnnotatorScreen
extends BaseScreen {

    private static final float W = 620.0f;
    private static final float H = 420.0f;
    private static final float PAD = 14.0f;
    private static final float FIELD_H = 18.0f;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final String[] TOOLS = {"Стрелка", "Рамка", "Круг", "Линия", "Подпись"};
    private static final int[] PALETTE = {
        0xFFF2F4F8, 0xFFFF5A5A, 0xFFFFC24B, 0xFF52E08A, 0xFF4FC3FF, 0xFFC08BFF
    };
    private static final float[] THICKNESS = {2.0f, 4.0f, 7.0f};

    /** Одна фигура разметки: координаты хранятся в пикселях снимка. */
    private static final class Shape {

        int tool;
        float x1;
        float y1;
        float x2;
        float y2;
        int color;
        float thickness;
        String text = "";
    }

    private final Screen parent;
    private final Path source;
    private final SearchField label = new SearchField().icon("").placeholder("текст подписи");
    private final List<Shape> shapes = new ArrayList<Shape>();
    private final List<Shape> redo = new ArrayList<Shape>();

    private BufferedImage image;
    private String texture = "";
    private float imageWidth = 1.0f;
    private float imageHeight = 1.0f;
    private int tool;
    private int colorIndex;
    private int thicknessIndex = 1;
    private Shape draft;
    private boolean drawing;
    private long buttonHeldMs;
    private float appear;
    private float delta = 0.016f;
    private long lastNs;
    private String status = "";

    public ScreenshotAnnotatorScreen(Screen parent, Path source) {
        super(Text.literal(Lang.t("Разметка снимка", "Annotate screenshot")));
        this.parent = parent;
        this.source = source == null ? Screenshots.newest() : source;
        this.load();
    }

    private void load() {
        if (this.source == null) {
            this.status = "Снимков не нашлось — сделайте снимок по F2";
            return;
        }
        try {
            this.image = ImageIO.read(this.source.toFile());
            if (this.image != null) {
                this.imageWidth = this.image.getWidth();
                this.imageHeight = this.image.getHeight();
            }
            this.texture = LocalImages.textureFor(this.source);
            this.status = "Снимок: " + this.source.getFileName() + " · " + (int)this.imageWidth + "×" + (int)this.imageHeight;
        }
        catch (Throwable throwable) {
            this.status = "Снимок не читается: " + throwable.getClass().getSimpleName();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float canvasTop() {
        return ScreenshotAnnotatorScreen.panelY() + 66.0f;
    }

    private float canvasHeight() {
        return ScreenshotAnnotatorScreen.panelY() + H - 92.0f - this.canvasTop();
    }

    private float canvasLeft() {
        return ScreenshotAnnotatorScreen.panelX() + PAD;
    }

    private float canvasWidth() {
        return W - PAD * 2.0f;
    }

    private float scale() {
        if (this.image == null) {
            return 1.0f;
        }
        return Math.min(this.canvasWidth() / this.imageWidth, this.canvasHeight() / this.imageHeight);
    }

    private float drawWidth() {
        return this.imageWidth * this.scale();
    }

    private float drawHeight() {
        return this.imageHeight * this.scale();
    }

    private float drawLeft() {
        return this.canvasLeft() + (this.canvasWidth() - this.drawWidth()) * 0.5f;
    }

    private float drawTop() {
        return this.canvasTop() + (this.canvasHeight() - this.drawHeight()) * 0.5f;
    }

    /** Перевод координат курсора в пиксели снимка. */
    private float toImageX(float screenX) {
        return (screenX - this.drawLeft()) / this.scale();
    }

    private float toImageY(float screenY) {
        return (screenY - this.drawTop()) / this.scale();
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = ScreenshotAnnotatorScreen.panelX();
        float y = ScreenshotAnnotatorScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, ScreenshotAnnotatorScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, "Разметка снимка", x + PAD, y + 20.0f, 12.0f, ScreenshotAnnotatorScreen.rgba(255, 255, 255, 245.0f * a));
        Render2D.msdfText(FONT_TEXT, "Зажмите ЛКМ и ведите — фигура появится на снимке; Ctrl+Z — отмена, Ctrl+S — сохранить",
                x + PAD, y + 34.0f, 5.8f, ScreenshotAnnotatorScreen.rgba(196, 202, 214, 150.0f * a));
        this.drawClose(x, y, mx, my, a);

        float toolY = y + 46.0f;
        float toolWidth = 92.0f;
        for (int i = 0; i < TOOLS.length; ++i) {
            float toolX = x + PAD + (toolWidth + 5.0f) * (float)i;
            boolean selected = this.tool == i;
            boolean hot = this.hover(mx, my, toolX, toolY, toolWidth, FIELD_H);
            Render2D.rect(toolX, toolY, toolWidth, FIELD_H, 6.0f, selected
                    ? ClientAccent.accentSoft(170.0f * a)
                    : ScreenshotAnnotatorScreen.rgba(255, 255, 255, (hot ? 28.0f : 14.0f) * a));
            float textWidth = Render2D.msdfWidth(FONT_SEMI, TOOLS[i], 5.8f);
            Render2D.msdfText(FONT_SEMI, TOOLS[i], toolX + (toolWidth - textWidth) * 0.5f, toolY + 5.6f, 5.8f,
                    selected ? ScreenshotAnnotatorScreen.rgba(12, 14, 20, 240.0f * a) : ScreenshotAnnotatorScreen.rgba(220, 226, 238, 230.0f * a));
        }

        this.updateDrawing(mx, my);

        float left = this.drawLeft();
        float top = this.drawTop();
        Render2D.rect(this.canvasLeft() - 2.0f, this.canvasTop() - 2.0f, this.canvasWidth() + 4.0f, this.canvasHeight() + 4.0f,
                8.0f, ScreenshotAnnotatorScreen.rgba(8, 9, 13, 200.0f * a));
        if (this.image == null) {
            Render2D.msdfText(FONT_TEXT, "Снимок недоступен", this.canvasLeft() + 10.0f, this.canvasTop() + 10.0f, 6.4f,
                    ScreenshotAnnotatorScreen.rgba(226, 170, 170, 230.0f * a));
        }
        else {
            if (this.texture != null && !this.texture.isEmpty() && Render2D.imageReady(this.texture)) {
                Render2D.image(this.texture, left, top, this.drawWidth(), this.drawHeight(), 4.0f,
                        ScreenshotAnnotatorScreen.rgba(255, 255, 255, 255.0f * a));
            }
            else {
                Render2D.rect(left, top, this.drawWidth(), this.drawHeight(), 4.0f, ScreenshotAnnotatorScreen.rgba(24, 26, 34, 255.0f * a));
                Render2D.msdfText(FONT_TEXT, "Загружаю превью…", left + 10.0f, top + 10.0f, 6.4f,
                        ScreenshotAnnotatorScreen.rgba(190, 196, 210, 220.0f * a));
            }
            for (Shape shape : this.shapes) {
                this.drawShape(shape, a);
            }
            if (this.draft != null) {
                this.drawShape(this.draft, a * 0.9f);
            }
            Render2D.outline(left, top, this.drawWidth(), this.drawHeight(), 4.0f, 1.0f, ClientAccent.accentSoft(60.0f * a));
        }

        float bottom = y + H - 56.0f;
        for (int i = 0; i < PALETTE.length; ++i) {
            float swatchX = x + PAD + (float)i * 22.0f;
            boolean selected = this.colorIndex == i;
            boolean hot = this.hover(mx, my, swatchX, bottom, 18.0f, 18.0f);
            Render2D.rect(swatchX, bottom, 18.0f, 18.0f, 5.0f, PALETTE[i]);
            if (selected || hot) {
                Render2D.outline(swatchX - 1.5f, bottom - 1.5f, 21.0f, 21.0f, 6.0f, selected ? 2.0f : 1.0f,
                        selected ? ClientAccent.accentBright(240.0f * a) : ScreenshotAnnotatorScreen.rgba(255, 255, 255, 120.0f * a));
            }
        }
        for (int i = 0; i < THICKNESS.length; ++i) {
            float buttonX = x + PAD + 6.0f * 22.0f + 12.0f + (float)i * 30.0f;
            boolean selected = this.thicknessIndex == i;
            boolean hot = this.hover(mx, my, buttonX, bottom, 26.0f, 18.0f);
            Render2D.rect(buttonX, bottom, 26.0f, 18.0f, 5.0f, selected
                    ? ClientAccent.accentSoft(170.0f * a)
                    : ScreenshotAnnotatorScreen.rgba(255, 255, 255, (hot ? 28.0f : 14.0f) * a));
            Render2D.rect(buttonX + 4.0f, bottom + 9.0f - THICKNESS[i] * 0.5f, 18.0f, THICKNESS[i], THICKNESS[i] * 0.5f,
                    selected ? ScreenshotAnnotatorScreen.rgba(12, 14, 20, 240.0f * a) : ScreenshotAnnotatorScreen.rgba(224, 230, 242, 230.0f * a));
        }
        this.label.render(drawContext, x + PAD + 320.0f, bottom, 130.0f, FIELD_H, a, mx, my, this.delta);
        this.button("Сохранить", x + W - PAD - 84.0f, bottom, 84.0f, this.hover(mx, my, x + W - PAD - 84.0f, bottom, 84.0f, FIELD_H), true, a);

        String hint = this.status.isEmpty() ? "Готово к разметке" : this.status;
        Render2D.msdfText(FONT_TEXT, ScreenshotAnnotatorScreen.trim(hint, 104), x + PAD, y + H - PAD - 14.0f, 5.8f,
                ClientAccent.accentSoft(220.0f * a));
        Render2D.msdfText(FONT_TEXT, "Фигур: " + this.shapes.size() + " · файл сохранится рядом со снимком как «_annotated.png»",
                x + PAD, y + H - PAD - 4.0f, 5.6f, ScreenshotAnnotatorScreen.rgba(168, 176, 192, 200.0f * a));
    }

    /** Опрос кнопки мыши: у экранов игры нет события перетаскивания, поэтому следим за состоянием. */
    private void updateDrawing(float mx, float my) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean inside = this.image != null && mx >= this.drawLeft() && mx <= this.drawLeft() + this.drawWidth()
                && my >= this.drawTop() && my <= this.drawTop() + this.drawHeight();
        boolean down = false;
        if (client != null && client.getWindow() != null) {
            down = org.lwjgl.glfw.GLFW.glfwGetMouseButton(client.getWindow().getHandle(), 0) == 1;
        }
        if (down && !this.drawing && inside) {
            this.drawing = true;
            this.buttonHeldMs = System.currentTimeMillis();
            Shape shape = new Shape();
            shape.tool = this.tool;
            shape.x1 = this.toImageX(mx);
            shape.y1 = this.toImageY(my);
            shape.x2 = shape.x1;
            shape.y2 = shape.y1;
            shape.color = PALETTE[Math.max(0, Math.min(PALETTE.length - 1, this.colorIndex))];
            shape.thickness = THICKNESS[Math.max(0, Math.min(THICKNESS.length - 1, this.thicknessIndex))];
            shape.text = this.label.getText() == null ? "" : this.label.getText().trim();
            this.draft = shape;
        }
        else if (down && this.drawing && this.draft != null) {
            this.draft.x2 = this.toImageX(mx);
            this.draft.y2 = this.toImageY(my);
        }
        else if (!down && this.drawing && this.draft != null) {
            this.drawing = false;
            if (Math.abs(this.draft.x2 - this.draft.x1) > 2.0f || Math.abs(this.draft.y2 - this.draft.y1) > 2.0f
                    || (this.draft.tool == 4 && !this.draft.text.isEmpty())) {
                this.shapes.add(this.draft);
                this.redo.clear();
                Sounds.play("select_category");
            }
            this.draft = null;
        }
    }

    private void drawShape(Shape shape, float a) {
        float sx = this.scale();
        float x1 = this.drawLeft() + shape.x1 * sx;
        float y1 = this.drawTop() + shape.y1 * sx;
        float x2 = this.drawLeft() + shape.x2 * sx;
        float y2 = this.drawTop() + shape.y2 * sx;
        float thickness = Math.max(1.2f, shape.thickness * sx);
        int color = ScreenshotAnnotatorScreen.fade(shape.color, a);
        switch (shape.tool) {
            case 0: {
                Render2D.line(x1, y1, x2, y2, thickness, color);
                double angle = Math.atan2(y2 - y1, x2 - x1);
                float head = Math.max(7.0f, thickness * 3.4f);
                Render2D.line(x2, y2, x2 - (float)(Math.cos(angle - 0.42) * (double)head), y2 - (float)(Math.sin(angle - 0.42) * (double)head), thickness, color);
                Render2D.line(x2, y2, x2 - (float)(Math.cos(angle + 0.42) * (double)head), y2 - (float)(Math.sin(angle + 0.42) * (double)head), thickness, color);
                break;
            }
            case 1: {
                Render2D.outline(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), 4.0f, thickness, color);
                break;
            }
            case 2: {
                float radius = (float)(Math.hypot((double)(x2 - x1), (double)(y2 - y1)));
                Render2D.circleOutline(x1, y1, radius, thickness, color);
                break;
            }
            case 3: {
                Render2D.line(x1, y1, x2, y2, thickness, color);
                break;
            }
            default: {
                if (!shape.text.isEmpty()) {
                    Render2D.rect(x1 - 4.0f, y1 - 3.0f, Render2D.msdfWidth(FONT_SEMI, shape.text, 7.2f) + 8.0f, 13.0f, 3.0f,
                            ScreenshotAnnotatorScreen.rgba(10, 12, 18, 190.0f * a));
                    Render2D.msdfText(FONT_SEMI, shape.text, x1, y1, 7.2f, color);
                }
                break;
            }
        }
    }

    private void drawClose(float x, float y, float mx, float my, float a) {
        float size = 18.0f;
        float closeX = x + W - PAD - size;
        float closeY = y + 14.0f;
        boolean hot = this.hover(mx, my, closeX, closeY, size, size);
        Render2D.rect(closeX, closeY, size, size, 6.0f, ScreenshotAnnotatorScreen.rgba(255, 255, 255, (hot ? 26.0f : 10.0f) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : ScreenshotAnnotatorScreen.rgba(210, 214, 224, 190.0f * a);
        Render2D.line(closeX + 5.4f, closeY + 5.4f, closeX + size - 5.4f, closeY + size - 5.4f, 1.4f, ink);
        Render2D.line(closeX + size - 5.4f, closeY + 5.4f, closeX + 5.4f, closeY + size - 5.4f, 1.4f, ink);
    }

    private void button(String text, float x, float y, float width, boolean hot, boolean accent, float a) {
        Render2D.rect(x, y, width, FIELD_H, 6.0f, accent
                ? ClientAccent.accentSoft((hot ? 215.0f : 180.0f) * a)
                : ScreenshotAnnotatorScreen.rgba(255, 255, 255, (hot ? 30.0f : 16.0f) * a));
        float textWidth = Render2D.msdfWidth(FONT_SEMI, text, 5.8f);
        Render2D.msdfText(FONT_SEMI, text, x + (width - textWidth) * 0.5f, y + 5.6f, 5.8f,
                accent ? ScreenshotAnnotatorScreen.rgba(12, 14, 20, 240.0f * a) : ScreenshotAnnotatorScreen.rgba(222, 228, 240, 230.0f * a));
    }

    private boolean hover(float mx, float my, float x, float y, float width, float height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    /** Сохраняет разметку отдельной копией в исходном разрешении. */
    private void save() {
        if (this.image == null) {
            this.status = "Сохранять нечего: снимок не загружен";
            Sounds.play("settings_close");
            return;
        }
        try {
            Path target = Screenshots.annotated(this.source);
            BufferedImage output = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = output.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.drawImage(this.image, 0, 0, null);
            for (Shape shape : this.shapes) {
                ScreenshotAnnotatorScreen.paint(graphics, shape);
            }
            graphics.dispose();
            ImageIO.write(output, "png", target.toFile());
            this.status = "Сохранено: " + target.getFileName() + " · " + Screenshots.sizeText(target);
            ChatMessage.send("Разметка сохранена: " + target.getFileName());
            Sounds.play("select_category");
        }
        catch (Throwable throwable) {
            this.status = "Не удалось сохранить: " + throwable.getClass().getSimpleName();
            Sounds.play("settings_close");
        }
    }

    private static void paint(Graphics2D graphics, Shape shape) {
        graphics.setColor(new java.awt.Color(shape.color, true));
        graphics.setStroke(new BasicStroke(shape.thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double x1 = shape.x1;
        double y1 = shape.y1;
        double x2 = shape.x2;
        double y2 = shape.y2;
        switch (shape.tool) {
            case 0: {
                graphics.draw(new Line2D.Double(x1, y1, x2, y2));
                double angle = Math.atan2(y2 - y1, x2 - x1);
                double head = Math.max(9.0, shape.thickness * 3.4);
                Path2D path = new Path2D.Double();
                path.moveTo(x2, y2);
                path.lineTo(x2 - Math.cos(angle - 0.42) * head, y2 - Math.sin(angle - 0.42) * head);
                path.moveTo(x2, y2);
                path.lineTo(x2 - Math.cos(angle + 0.42) * head, y2 - Math.sin(angle + 0.42) * head);
                graphics.draw(path);
                break;
            }
            case 1: {
                graphics.draw(new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)));
                break;
            }
            case 2: {
                double radius = Math.hypot(x2 - x1, y2 - y1);
                graphics.draw(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2.0, radius * 2.0));
                break;
            }
            case 3: {
                graphics.draw(new Line2D.Double(x1, y1, x2, y2));
                break;
            }
            default: {
                if (!shape.text.isEmpty()) {
                    float size = Math.max(14.0f, shape.thickness * 4.5f);
                    graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.round(size)));
                    java.awt.FontMetrics metrics = graphics.getFontMetrics();
                    int textWidth = metrics.stringWidth(shape.text);
                    graphics.setColor(new java.awt.Color(10, 12, 18, 190));
                    graphics.fillRoundRect((int)x1 - 5, (int)y1 - metrics.getAscent() - 3, textWidth + 10, metrics.getHeight() + 2, 6, 6);
                    graphics.setColor(new java.awt.Color(shape.color, true));
                    graphics.drawString(shape.text, (float)x1, (float)y1);
                }
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = ScreenshotAnnotatorScreen.panelX();
        float y = ScreenshotAnnotatorScreen.panelY();
        if (this.label.mouseClicked(mx, my, click.button())) {
            return true;
        }
        if (click.button() == 0 && this.hover(mx, my, x + W - PAD - 18.0f, y + 14.0f, 18.0f, 18.0f)) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        float toolY = y + 46.0f;
        float toolWidth = 92.0f;
        for (int i = 0; i < TOOLS.length; ++i) {
            float toolX = x + PAD + (toolWidth + 5.0f) * (float)i;
            if (this.hover(mx, my, toolX, toolY, toolWidth, FIELD_H)) {
                this.tool = i;
                Sounds.play("select_category");
                return true;
            }
        }
        float bottom = y + H - 56.0f;
        for (int i = 0; i < PALETTE.length; ++i) {
            float swatchX = x + PAD + (float)i * 22.0f;
            if (this.hover(mx, my, swatchX, bottom, 18.0f, 18.0f)) {
                this.colorIndex = i;
                Sounds.play("select_category");
                return true;
            }
        }
        for (int i = 0; i < THICKNESS.length; ++i) {
            float buttonX = x + PAD + 6.0f * 22.0f + 12.0f + (float)i * 30.0f;
            if (this.hover(mx, my, buttonX, bottom, 26.0f, 18.0f)) {
                this.thicknessIndex = i;
                Sounds.play("select_category");
                return true;
            }
        }
        if (this.hover(mx, my, x + W - PAD - 84.0f, bottom, 84.0f, FIELD_H)) {
            this.save();
            return true;
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == 256 || key == 27) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        boolean control = ScreenshotAnnotatorScreen.controlDown();
        if (control && key == 90 && !this.shapes.isEmpty()) {
            this.redo.add(this.shapes.remove(this.shapes.size() - 1));
            this.status = "Шаг отменён";
            return true;
        }
        if (control && key == 89 && !this.redo.isEmpty()) {
            this.shapes.add(this.redo.remove(this.redo.size() - 1));
            this.status = "Шаг возвращён";
            return true;
        }
        if (control && key == 83) {
            this.save();
            return true;
        }
        return this.label.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return this.label.charTyped(input);
    }

    private static boolean controlDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && client.getWindow() != null
                && org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), 341) == 1;
    }

    @Override
    public void close() {
        this.label.blur();
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

    private static int fade(int color, float a) {
        int alpha = Math.max(0, Math.min(255, Math.round((float)(color >>> 24) * a)));
        return alpha << 24 | color & 0xFFFFFF;
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
