package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Visuals.CustomSwords;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Редактор оружия ByAzen (идея №65 из IDEAS.md).
 * <p>
 * Большое живое превью выбранной модели, список всех моделей с поиском по названию и мгновенный
 * выбор по клику. Слева — каталог, справа — превью на мягкой подложке со свечением акцента.
 */
public final class SwordPreviewScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 420.0f;
    private static final float H = 236.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 14.0f;
    private static final int COLS = 2;

    private final List<String> matches = new ArrayList<String>();
    private final StringBuilder filter = new StringBuilder();
    private int scroll;
    private int previewIndex;
    private float openAnimation;
    private long lastFrameNanos;

    public SwordPreviewScreen() {
        super(Text.literal("Оружие ByAzen"));
        this.rebuild();
        this.previewIndex = 0;
        this.syncPreview();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static CustomSwords module() {
        return CustomSwords.getInstance();
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private void rebuild() {
        this.matches.clear();
        String needle = this.filter.toString().trim().toLowerCase();
        for (String name : CustomSwords.names()) {
            if (needle.isEmpty() || name.toLowerCase().contains(needle)) {
                this.matches.add(name);
            }
        }
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll()));
        this.previewIndex = this.matches.isEmpty() ? -1 : Math.max(0, Math.min(this.previewIndex, this.matches.size() - 1));
    }

    private int rowsVisible() {
        return (int)((H - PAD * 2.0f - 26.0f) / ROW_H);
    }

    private int maxScroll() {
        int rows = (int)Math.ceil((double)this.matches.size() / (double)COLS);
        return Math.max(0, rows - this.rowsVisible());
    }

    private void syncPreview() {
        CustomSwords module = SwordPreviewScreen.module();
        if (module != null && this.previewIndex >= 0 && this.previewIndex < this.matches.size()) {
            module.selectByDisplay(this.matches.get(this.previewIndex));
        }
    }

    private int indexAt(float mouseX, float mouseY) {
        float x = SwordPreviewScreen.panelX() + PAD;
        float y = SwordPreviewScreen.panelY() + PAD + 26.0f;
        float areaW = W * 0.54f - PAD * 1.5f;
        float columnW = areaW / (float)COLS;
        for (int row = 0; row < this.rowsVisible(); ++row) {
            for (int column = 0; column < COLS; ++column) {
                float cellX = x + (float)column * columnW;
                float cellY = y + (float)row * ROW_H;
                if (mouseX >= cellX && mouseX <= cellX + columnW - 2.0f && mouseY >= cellY && mouseY <= cellY + ROW_H - 2.0f) {
                    int index = (this.scroll + row) * COLS + column;
                    return index < this.matches.size() ? index : -1;
                }
            }
        }
        return -1;
    }

    private static ItemStack previewStack() {
        return new ItemStack(Items.DIAMOND_SWORD);
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.openAnimation += (1.0f - this.openAnimation) * Math.min(1.0f, delta * 10.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.openAnimation));
        float x = SwordPreviewScreen.panelX();
        float y = SwordPreviewScreen.panelY();
        float alpha = a;
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, alpha, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, alpha);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * alpha));
        Render2D.msdfText(FONT_BOLD, "Оружие ByAzen", x + PAD, y + PAD, 11.0f, SwordPreviewScreen.ink(alpha));
        String counter = this.matches.size() + " моделей";
        Render2D.msdfText(FONT, counter, x + W - PAD - Render2D.msdfWidth(FONT, counter, 7.5f), y + PAD + 3.0f, 7.5f, SwordPreviewScreen.sub(alpha));
        String close = "✕";
        Render2D.msdfText(FONT_BOLD, close, x + W - PAD - 4.0f, y + 10.0f, 9.0f, SwordPreviewScreen.sub(alpha * 0.9f));

        // поле поиска
        float searchW = W - PAD * 2.0f;
        Render2D.rect(x + PAD, y + PAD + 16.0f, searchW, 16.0f, 5.0f, SwordPreviewScreen.field(alpha));
        String needle = this.filter.length() == 0 ? "Поиск по названию…" : this.filter.toString();
        int needleColor = this.filter.length() == 0 ? SwordPreviewScreen.sub(alpha * 0.8f) : SwordPreviewScreen.ink(alpha);
        Render2D.msdfText(FONT, needle, x + PAD + 6.0f, y + PAD + 21.0f, 8.0f, needleColor);
        Render2D.msdfText(FONT, "|", x + PAD + 6.0f + Render2D.msdfWidth(FONT, needle, 8.0f), y + PAD + 21.0f, 8.0f,
                SwordPreviewScreen.accentPulse(alpha));

        // список моделей
        float listX = x + PAD;
        float listY = y + PAD + 38.0f;
        float listW = W * 0.54f - PAD * 1.5f;
        float listH = H - PAD * 2.0f - 38.0f;
        Render2D.rect(listX, listY, listW, listH, 8.0f, SwordPreviewScreen.listBackground(alpha));
        float columnW = listW / (float)COLS;
        Render2D.pushScissor(drawContext, listX, listY, listW, listH);
        for (int row = 0; row < this.rowsVisible(); ++row) {
            for (int column = 0; column < COLS; ++column) {
                int index = (this.scroll + row) * COLS + column;
                if (index >= this.matches.size()) {
                    continue;
                }
                float cellX = listX + (float)column * columnW;
                float cellY = listY + (float)row * ROW_H;
                boolean selected = index == this.previewIndex;
                boolean hovered = index == this.indexAt(mouseX, mouseY);
                if (selected) {
                    RectUtil.drawClientRect(cellX + 1.0f, cellY, columnW - 3.0f, ROW_H - 2.0f, 4.0f, alpha * 0.95f);
                }
                else if (hovered) {
                    Render2D.rect(cellX + 1.0f, cellY, columnW - 3.0f, ROW_H - 2.0f, 4.0f, SwordPreviewScreen.hover(alpha));
                }
                String name = this.matches.get(index);
                int color = selected ? SwordPreviewScreen.ink(alpha) : SwordPreviewScreen.sub(alpha * 0.95f);
                Render2D.msdfText(FONT, name, cellX + 6.0f, cellY + 4.0f, 6.6f, color);
            }
        }
        Render2D.popScissor(drawContext);
        if (this.maxScroll() > 0) {
            float trackH = listH;
            float barH = Math.max(18.0f, trackH * (float)this.rowsVisible() / (float)(this.rowsVisible() + this.maxScroll()));
            float barY = listY + (trackH - barH) * ((float)this.scroll / (float)this.maxScroll());
            Render2D.rect(listX + listW - 3.0f, barY, 2.0f, barH, 1.0f, SwordPreviewScreen.accentPulse(alpha * 0.55f));
        }

        // превью
        float prevX = x + W * 0.54f + PAD * 0.5f;
        float prevW = W - (prevX - x) - PAD;
        float prevY = y + PAD + 4.0f;
        float prevH = H - PAD * 2.0f - 30.0f;
        Render2D.rect(prevX, prevY, prevW, prevH, 10.0f, SwordPreviewScreen.listBackground(alpha));
        for (int ring = 4; ring >= 1; --ring) {
            float inset = (float)ring * 6.0f;
            Render2D.rect(prevX + inset, prevY + inset, prevW - inset * 2.0f, prevH - inset * 2.0f, 8.0f,
                    SwordPreviewScreen.glow(alpha, (float)ring));
        }
        String selectedName = this.previewIndex >= 0 && this.previewIndex < this.matches.size()
                ? this.matches.get(this.previewIndex)
                : "—";
        float bob = (float)Math.sin((double)System.nanoTime() / 9.0E8) * 2.0f;
        float iconScale = 6.4f;
        float centerX = prevX + prevW * 0.5f;
        float centerY = prevY + prevH * 0.5f + bob;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(centerX, centerY);
        drawContext.getMatrices().scale(iconScale, iconScale);
        drawContext.getMatrices().translate(-8.0f, -8.0f);
        drawContext.drawItem(SwordPreviewScreen.previewStack(), 0, 0);
        drawContext.getMatrices().popMatrix();
        Render2D.msdfText(FONT_BOLD, selectedName, centerX - Render2D.msdfWidth(FONT_BOLD, selectedName, 9.0f) * 0.5f,
                prevY + prevH + 5.0f, 9.0f, SwordPreviewScreen.ink(alpha));
        String hint = "ЛКМ — выбрать · колесо — листать · Esc — закрыть";
        Render2D.msdfText(FONT, hint, x + PAD, y + H - PAD + 1.0f, 6.4f, SwordPreviewScreen.sub(alpha * 0.75f));
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    private static int ink(float alpha) {
        return SwordPreviewScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return SwordPreviewScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return SwordPreviewScreen.rgba(16, 18, 24, 190.0f * alpha);
    }

    private static int listBackground(float alpha) {
        return SwordPreviewScreen.rgba(14, 16, 21, 160.0f * alpha);
    }

    private static int hover(float alpha) {
        return SwordPreviewScreen.rgba(255, 255, 255, 22.0f * alpha);
    }

    private static int glow(float alpha, float ring) {
        return ClientAccent.accent(Math.max(3.0f, 16.0f * alpha / ring));
    }

    private static int accentPulse(float alpha) {
        float wave = 0.6f + 0.4f * (float)Math.sin((double)System.currentTimeMillis() / 420.0);
        return ClientAccent.accent(Math.round(190.0f * alpha * wave));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = SwordPreviewScreen.panelX();
        float y = SwordPreviewScreen.panelY();
        if (mx >= x + W - PAD - 12.0f && mx <= x + W - PAD + 6.0f && my >= y + 8.0f && my <= y + 24.0f) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        int index = this.indexAt(mx, my);
        if (index >= 0) {
            this.previewIndex = index;
            this.syncPreview();
            Sounds.play("select_category");
            return true;
        }
        if (mx < x || mx > x + W || my < y || my > y + H) {
            Sounds.play("settings_close");
            this.close();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount != 0.0) {
            this.scroll = Math.max(0, Math.min(this.maxScroll(), this.scroll - (verticalAmount > 0.0 ? 1 : -1)));
            Sounds.play("slider");
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == 256) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (key == 259) {
            if (this.filter.length() > 0) {
                this.filter.setLength(this.filter.length() - 1);
                this.rebuild();
                Sounds.play("search_typing");
            }
            return true;
        }
        if (key == 264) {
            this.previewIndex = Math.min(this.matches.size() - 1, this.previewIndex + 1);
            this.scroll = Math.max(0, Math.min(this.maxScroll(), this.previewIndex / COLS - this.rowsVisible() + 1));
            this.syncPreview();
            return true;
        }
        if (key == 265) {
            this.previewIndex = Math.max(0, this.previewIndex - 1);
            this.scroll = Math.min(this.scroll, this.previewIndex / COLS);
            this.syncPreview();
            return true;
        }
        if (key == 257 || key == 335) {
            this.syncPreview();
            Sounds.play("module_settings_close");
            this.close();
            return true;
        }
        if (key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32 || key == 45) {
            if (this.filter.length() < 24) {
                this.filter.append((char)Character.toLowerCase(key));
                this.rebuild();
                Sounds.play("search_typing");
            }
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return true;
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }
}
