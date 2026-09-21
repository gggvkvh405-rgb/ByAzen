package rtx.byazen.api.ui;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.render.LocalImages;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;
import rtx.byazen.utils.lang.Lang;

/**
 * Галерея скриншотов (идея №45 из IDEAS.md): сетка превью, просмотр на весь экран, удаление,
 * копирование пути и открытие папки - всё прямо в игре, без выхода в проводник.
 * <p>
 * Отрисовка векторная: скруглённые карточки, плавное появление, размытие фона, сглаженный шрифт.
 */
public final class GalleryScreen
extends BaseScreen {

    private static final float W = 452.0f;
    private static final float H = 292.0f;
    private static final float PAD = 14.0f;
    private static final float FIELD_H = 18.0f;
    private static final float CELL_GAP = 8.0f;
    private static final int COLUMNS = 3;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("dd.MM HH:mm").withZone(ZoneId.systemDefault());

    private final Screen parent;
    private final List<Path> files = new ArrayList<Path>();
    private float appear;
    private long lastNs;
    private float delta = 0.016f;
    private float scroll;
    private float scrollTarget;
    private int previewIndex = -1;
    private int hovered = -1;
    private int pendingDelete = -1;
    private String footer = "";

    public GalleryScreen(Screen parent) {
        super(Text.literal(Lang.t("Галерея скриншотов", "Screenshot gallery")));
        this.parent = parent;
        this.reload();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static Path screenshotDirectory() {
        MinecraftClient client = MinecraftClient.getInstance();
        Path root = client == null ? Path.of("screenshots") : client.runDirectory.toPath().resolve("screenshots");
        return root;
    }

    private void reload() {
        this.files.clear();
        Path directory = GalleryScreen.screenshotDirectory();
        if (!Files.isDirectory(directory)) {
            return;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            stream.filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                    .sorted(Comparator.comparingLong(GalleryScreen::modified).reversed())
                    .forEach(this.files::add);
        }
        catch (IOException exception) {
            this.footer = Lang.t("Папка со скриншотами недоступна", "Screenshot folder is unavailable");
        }
    }

    private static long modified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        }
        catch (IOException exception) {
            return 0L;
        }
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(this.parent);
        }
    }

    // ------------------------------------------------------------------ geometry

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float gridTop() {
        return panelY() + 74.0f;
    }

    private float gridHeight() {
        return panelY() + H - 34.0f - this.gridTop();
    }

    private float cellWidth() {
        return (W - PAD * 2.0f - CELL_GAP * (float) (COLUMNS - 1)) / (float) COLUMNS;
    }

    private float cellHeight() {
        return this.cellWidth() * 0.62f;
    }

    private float rowsHeight() {
        int rows = (int) Math.ceil((double) this.files.size() / (double) COLUMNS);
        return (float) rows * (this.cellHeight() + CELL_GAP) - CELL_GAP;
    }

    private float maxScroll() {
        return Math.max(0.0f, this.rowsHeight() - this.gridHeight());
    }

    private int indexAt(float mouseX, float mouseY) {
        float left = panelX() + PAD;
        float top = this.gridTop();
        if (mouseX < left || mouseX > left + W - PAD * 2.0f || mouseY < top || mouseY > top + this.gridHeight()) {
            return -1;
        }
        float localX = mouseX - left;
        float localY = mouseY - top + this.scroll;
        int column = (int) (localX / (this.cellWidth() + CELL_GAP));
        int row = (int) (localY / (this.cellHeight() + CELL_GAP));
        if (column < 0 || column >= COLUMNS) {
            return -1;
        }
        int index = row * COLUMNS + column;
        return index >= 0 && index < this.files.size() ? index : -1;
    }

    // ------------------------------------------------------------------ rendering

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = GalleryScreen.panelX();
        float y = GalleryScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, GalleryScreen.rgba(4, 5, 9, 150.0f * a));
        if (this.previewIndex >= 0) {
            this.renderPreview(drawContext, a, mx, my);
            return;
        }
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, Lang.t("Галерея скриншотов", "Screenshot gallery"), x + PAD, y + 20.0f, 12.0f, GalleryScreen.rgba(255, 255, 255, 245.0f * a));
        String subtitle = this.files.isEmpty()
                ? Lang.t("В папке screenshots пока пусто — сделайте снимок по F2", "The screenshots folder is empty — press F2 in game")
                : Lang.t("Снимков: ", "Shots: ") + this.files.size() + "  •  " + GalleryScreen.screenshotDirectory();
        Render2D.msdfText(FONT_TEXT, GalleryScreen.trim(subtitle, 78), x + PAD, y + 33.0f, 6.0f,
                GalleryScreen.rgba(196, 202, 214, 140.0f * a));
        Render2D.msdfText(FONT_TEXT, Lang.t("ЛКМ — открыть, ПКМ — меню, Ctrl+ЛКМ — открыть папку, колесо — прокрутка", "LMB — open, RMB — menu, Ctrl+LMB — open folder, wheel — scroll"),
                x + PAD, y + 47.0f, 5.6f, GalleryScreen.rgba(170, 176, 190, 120.0f * a));
        float closeSize = 18.0f;
        float closeX = x + W - PAD - closeSize;
        float closeY = y + 14.0f;
        boolean closeHot = mx >= closeX && mx <= closeX + closeSize && my >= closeY && my <= closeY + closeSize;
        Render2D.rect(closeX, closeY, closeSize, closeSize, 6.0f, GalleryScreen.rgba(255, 255, 255, (closeHot ? 26.0f : 10.0f) * a));
        int ink = closeHot ? ClientAccent.accentBright(240.0f * a) : GalleryScreen.rgba(210, 214, 224, 190.0f * a);
        Render2D.line(closeX + 5.4f, closeY + 5.4f, closeX + closeSize - 5.4f, closeY + closeSize - 5.4f, 1.4f, ink);
        Render2D.line(closeX + closeSize - 5.4f, closeY + 5.4f, closeX + 5.4f, closeY + closeSize - 5.4f, 1.4f, ink);

        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.delta * 14.0f);
        this.hovered = this.indexAt(mx, my);
        float left = x + PAD;
        float top = this.gridTop();
        Render2D.pushScissor(drawContext, left, top, W - PAD * 2.0f, this.gridHeight());
        for (int i = 0; i < this.files.size(); ++i) {
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            float cx = left + (float) column * (this.cellWidth() + CELL_GAP);
            float cy = top + (float) row * (this.cellHeight() + CELL_GAP) - this.scroll;
            if (cy + this.cellHeight() < top - 2.0f || cy > top + this.gridHeight() + 2.0f) {
                continue;
            }
            boolean hot = i == this.hovered;
            Render2D.rect(cx - 2.0f, cy - 2.0f, this.cellWidth() + 4.0f, this.cellHeight() + 4.0f, 8.0f,
                    hot ? ClientAccent.accentSoft(40.0f * a) : GalleryScreen.rgba(255, 255, 255, 8.0f * a));
            Path file = this.files.get(i);
            String texture = LocalImages.textureFor(file);
            if (texture != null && Render2D.imageReady(texture)) {
                Render2D.image(texture, cx, cy, this.cellWidth(), this.cellHeight(), 7.0f, GalleryScreen.rgba(255, 255, 255, 255.0f * a));
                Render2D.outline(cx, cy, this.cellWidth(), this.cellHeight(), 7.0f, 0.8f, GalleryScreen.rgba(255, 255, 255, 26.0f * a));
            }
            else {
                Render2D.rect(cx, cy, this.cellWidth(), this.cellHeight(), 7.0f, GalleryScreen.rgba(18, 20, 26, 200.0f * a));
                Render2D.msdfText(FONT_TEXT, Lang.t("загрузка превью…", "loading preview…"), cx + 8.0f, cy + this.cellHeight() * 0.5f - 3.0f, 5.8f,
                        GalleryScreen.rgba(190, 196, 208, 140.0f * a));
            }
            Render2D.msdfText(FONT_SEMI, this.name(file), cx + 2.0f, cy + this.cellHeight() + 4.0f, 5.4f,
                    GalleryScreen.rgba(226, 231, 240, (hot ? 245.0f : 180.0f) * a));
            Render2D.msdfText(FONT_TEXT, STAMP.format(Instant.ofEpochMilli(GalleryScreen.modified(file))),
                    cx + this.cellWidth() - Render2D.msdfWidth(FONT_TEXT, STAMP.format(Instant.ofEpochMilli(GalleryScreen.modified(file))), 5.2f) - 2.0f,
                    cy + this.cellHeight() + 4.0f, 5.2f, GalleryScreen.rgba(176, 182, 196, 150.0f * a));
            if (this.pendingDelete == i) {
                Render2D.rect(cx, cy, this.cellWidth(), this.cellHeight(), 7.0f, GalleryScreen.rgba(120, 20, 30, 190.0f * a));
                Render2D.msdfText(FONT_SEMI, Lang.t("Удалить? ЛКМ — да, ПКМ — нет", "Delete? LMB — yes, RMB — no"), cx + 8.0f, cy + this.cellHeight() * 0.5f - 3.0f, 5.8f,
                        GalleryScreen.rgba(255, 235, 235, 240.0f * a));
            }
        }
        Render2D.popScissor(drawContext);
        if (this.rowsHeight() > this.gridHeight() + 0.5f) {
            float thumbHeight = Math.max(20.0f, this.gridHeight() * this.gridHeight() / this.rowsHeight());
            float thumbY = top + (this.gridHeight() - thumbHeight) * (this.scroll / Math.max(1.0f, this.maxScroll()));
            Render2D.rect(left + W - PAD * 2.0f + 4.0f, thumbY, 3.0f, thumbHeight, 1.5f, ClientAccent.accentSoft(150.0f * a));
        }
        if (!this.footer.isBlank()) {
            Render2D.msdfText(FONT_TEXT, this.footer, x + PAD, y + H - 16.0f, 5.8f, ClientAccent.accentSoft(220.0f * a));
        }
    }

    private void renderPreview(DrawContext drawContext, float a, float mx, float my) {
        Path file = this.files.get(Math.max(0, Math.min(this.files.size() - 1, this.previewIndex)));
        float x = GalleryScreen.panelX();
        float y = GalleryScreen.panelY();
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_SEMI, this.name(file), x + PAD, y + 20.0f, 8.0f, GalleryScreen.rgba(255, 255, 255, 240.0f * a));
        Render2D.msdfText(FONT_TEXT, Lang.t("← → листать • C — копировать путь • O — открыть папку • Delete — удалить • Esc — назад", "← → browse • C — copy path • O — open folder • Delete — remove • Esc — back"),
                x + PAD, y + 31.0f, 5.6f, GalleryScreen.rgba(180, 186, 200, 140.0f * a));
        float imageLeft = x + PAD;
        float imageTop = y + 40.0f;
        float imageWidth = W - PAD * 2.0f;
        float imageHeight = H - 40.0f - 22.0f;
        Render2D.rect(imageLeft, imageTop, imageWidth, imageHeight, 8.0f, GalleryScreen.rgba(8, 9, 12, 200.0f * a));
        String texture = LocalImages.textureFor(file);
        if (texture != null && Render2D.imageReady(texture)) {
            Render2D.image(texture, imageLeft, imageTop, imageWidth, imageHeight, 8.0f,
                    GalleryScreen.rgba(255, 255, 255, 255.0f * a));
        }
        else {
            Render2D.msdfText(FONT_TEXT, Lang.t("Открываем снимок…", "Opening screenshot…"), imageLeft + 10.0f, imageTop + 10.0f, 6.4f,
                    GalleryScreen.rgba(200, 206, 218, 170.0f * a));
        }
        String counter = (this.previewIndex + 1) + " / " + this.files.size();
        Render2D.msdfText(FONT_TEXT, counter, x + W - PAD - Render2D.msdfWidth(FONT_TEXT, counter, 6.0f), y + 20.0f, 6.0f,
                GalleryScreen.rgba(200, 206, 218, 170.0f * a));
        if (this.pendingDelete >= 0) {
            float width = 200.0f;
            float bx = x + (W - width) * 0.5f;
            float by = y + H - 46.0f;
            Render2D.rect(bx, by, width, 20.0f, 7.0f, GalleryScreen.rgba(120, 20, 30, 220.0f * a));
            Render2D.msdfText(FONT_SEMI, Lang.t("Удалить снимок? ЛКМ — да, ПКМ — отмена", "Delete the screenshot? LMB — yes, RMB — cancel"), bx + 8.0f, by + 6.0f, 5.8f,
                    GalleryScreen.rgba(255, 235, 235, 240.0f * a));
        }
    }

    private float updateDelta() {
        long now = System.nanoTime();
        this.delta = this.lastNs == 0L ? 0.016f : (float) (now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, this.delta));
    }

    private String name(Path file) {
        String name = file.getFileName().toString();
        return name.length() > 26 ? name.substring(0, 25) + "…" : name;
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        if (this.previewIndex >= 0) {
            if (this.pendingDelete >= 0) {
                if (click.button() == 0) {
                    this.deleteCurrent();
                }
                else {
                    this.pendingDelete = -1;
                }
                return true;
            }
            if (click.button() == 0) {
                this.previewIndex = -1;
                Sounds.play("settings_close");
            }
            else if (click.button() == 1) {
                this.previewIndex = -1;
            }
            return true;
        }
        float x = GalleryScreen.panelX();
        float y = GalleryScreen.panelY();
        if (click.button() == 0 && mx >= x + W - PAD - 18.0f && mx <= x + W - PAD && my >= y + 14.0f && my <= y + 32.0f) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        int index = this.indexAt(mx, my);
        if (index >= 0 && index < this.files.size()) {
            if (this.pendingDelete == index) {
                if (click.button() == 0) {
                    this.delete(index);
                }
                else {
                    this.pendingDelete = -1;
                }
                return true;
            }
            if (click.button() == 1) {
                this.pendingDelete = index;
                Sounds.play("select_category");
                return true;
            }
            if (GalleryScreen.ctrlHeld() || doubled) {
                this.openFolder();
                return true;
            }
            this.previewIndex = index;
            Sounds.play("module_settings_open");
            return true;
        }
        if (!(mx >= x && mx <= x + W && my >= y && my <= y + H)) {
            this.close();
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.previewIndex >= 0) {
            if (verticalAmount < 0.0) {
                this.step(1);
            }
            else if (verticalAmount > 0.0) {
                this.step(-1);
            }
            return true;
        }
        this.scrollTarget = Math.max(0.0f, Math.min(this.maxScroll(), this.scrollTarget - (float) verticalAmount * 20.0f));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == 256) {
            if (this.previewIndex >= 0) {
                this.previewIndex = -1;
                return true;
            }
            this.close();
            return true;
        }
        if (this.previewIndex >= 0) {
            if (key == 263) {
                this.step(-1);
                return true;
            }
            if (key == 262) {
                this.step(1);
                return true;
            }
            if (key == 67) {
                this.copyPath();
                return true;
            }
            if (key == 79) {
                this.openFolder();
                return true;
            }
            if (key == 261 || key == 259) {
                this.pendingDelete = this.previewIndex;
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return false;
    }

    private void step(int direction) {
        if (this.files.isEmpty()) {
            return;
        }
        this.previewIndex = Math.floorMod(this.previewIndex + direction, this.files.size());
        this.pendingDelete = -1;
        Sounds.play("select_category");
    }

    private void copyPath() {
        Path file = this.files.get(Math.max(0, Math.min(this.files.size() - 1, this.previewIndex)));
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(file.toAbsolutePath().toString());
            this.footer = Lang.t("Путь скопирован", "Path copied");
            NotificationsModule.notify(Lang.t("Путь к снимку скопирован", "Screenshot path copied"), 1600L);
        }
    }

    private void openFolder() {
        Path directory = GalleryScreen.screenshotDirectory();
        try {
            net.minecraft.util.Util.getOperatingSystem().open(directory.toFile());
            this.footer = Lang.t("Открываю папку…", "Opening folder…");
        }
        catch (Throwable throwable) {
            this.footer = Lang.t("Не удалось открыть папку", "Could not open the folder");
        }
    }

    private void deleteCurrent() {
        if (this.previewIndex >= 0 && this.previewIndex < this.files.size()) {
            this.delete(this.previewIndex);
        }
    }

    private void delete(int index) {
        Path file = this.files.get(index);
        try {
            Path trash = file.resolveSibling(file.getFileName().toString() + ".deleted");
            Files.move(file, trash, StandardCopyOption.REPLACE_EXISTING);
            this.files.remove(index);
            this.pendingDelete = -1;
            this.previewIndex = Math.max(0, Math.min(this.previewIndex, this.files.size() - 1));
            if (this.files.isEmpty()) {
                this.previewIndex = -1;
                this.footer = Lang.t("Снимков больше нет", "No screenshots left");
            }
            Sounds.play("gui_close");
            ChatMessage.brandmessage(Lang.t("Снимок перемещён в ", "Screenshot moved to ") + trash.getFileName());
        }
        catch (IOException exception) {
            this.footer = Lang.t("Не удалось удалить: ", "Could not delete: ") + exception.getClass().getSimpleName();
            Sounds.play("command_error");
        }
    }

    /** Ctrl нажат (для «открыть папку» по Ctrl+ЛКМ). */
    private static boolean ctrlHeld() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }
        long handle = client.getWindow().getHandle();
        return org.lwjgl.glfw.GLFW.glfwGetKey(handle, 341) == 1 || org.lwjgl.glfw.GLFW.glfwGetKey(handle, 345) == 1;
    }

    private static String trim(String text, int limit) {
        if (text == null) {
            return "";
        }
        return text.length() <= limit ? text : text.substring(0, limit - 1) + "…";
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        return new Color(r, g, b, a).getRGB();
    }

    /** Небольшая подсказка для HUD: сколько снимков в папке. */
    public static int count() {
        Path directory = GalleryScreen.screenshotDirectory();
        if (!Files.isDirectory(directory)) {
            return 0;
        }
        try (Stream<Path> stream = Files.list(directory)) {
            return (int) stream.filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")).count();
        }
        catch (IOException exception) {
            return 0;
        }
    }
}
