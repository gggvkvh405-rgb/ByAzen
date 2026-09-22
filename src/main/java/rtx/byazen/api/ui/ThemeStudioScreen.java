package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ThemeCodes;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Редактор темы и код темы (идеи №154 и №155 из IDEAS.md).
 * <p>
 * Слева — палитры: цвета темы и оттенки из обложки играющего трека. В центре — поле подбора цвета,
 * где цвет берётся движением мыши. Справа — живой предпросмотр карточки и кнопки кода: скопировать
 * свою тему или применить чужую из буфера обмена.
 */
public final class ThemeStudioScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 620.0f;
    private static final float H = 340.0f;
    private static final float PAD = 14.0f;
    private static final float SWATCH = 22.0f;
    private static final float FIELD = 176.0f;

    private final List<Integer> palette = new ArrayList<Integer>();
    private float animation;
    private long lastFrameNanos;
    private float hue;
    private float saturation;
    private float brightness;
    private boolean second;
    private boolean dragging;
    private int lastPreview = -1;
    private String status = "";
    private long statusUntil;

    public ThemeStudioScreen() {
        super(Text.literal("Редактор темы ByAzen"));
        this.loadThemePalette();
        this.syncFromModule();
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private InterfaceModule module() {
        return InterfaceModule.getInstance();
    }

    private void loadThemePalette() {
        this.palette.clear();
        int[] blended = ClientAccent.blendedClientPalette();
        if (blended != null) {
            for (int color : blended) {
                this.palette.add(color & 0xFFFFFF);
            }
        }
        InterfaceModule interfaceModule = this.module();
        if (interfaceModule != null) {
            this.palette.add(interfaceModule.rectColor.getColor() & 0xFFFFFF);
            this.palette.add(interfaceModule.rectSecondColor.getColor() & 0xFFFFFF);
        }
        int[] cover = ThemeStudioScreen.coverPalette();
        if (cover != null) {
            for (int color : cover) {
                this.palette.add(color & 0xFFFFFF);
            }
        }
    }

    private void loadCoverPalette() {
        int[] cover = ThemeStudioScreen.coverPalette();
        if (cover == null) {
            this.status = "Сейчас ничего не играет или у трека нет обложки";
            this.statusUntil = System.currentTimeMillis() + 3000L;
            return;
        }
        this.palette.clear();
        for (int color : cover) {
            this.palette.add(color & 0xFFFFFF);
        }
        this.status = "Палитра взята из обложки трека: " + cover.length + " оттенков";
        this.statusUntil = System.currentTimeMillis() + 3000L;
    }

    private static int[] coverPalette() {
        try {
            MusicEngine engine = MusicEngine.get();
            if (engine == null) {
                return null;
            }
            MusicTrack track = engine.current();
            if (track == null || track.coverUrl() == null || track.coverUrl().isBlank()) {
                return null;
            }
            return MusicCovers.paletteFor(track.coverUrl());
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.pollDrag();
        float delta = this.deltaSeconds();
        this.animation += (1.0f - this.animation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.animation));
        float x = ThemeStudioScreen.panelX();
        float y = ThemeStudioScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Редактор темы", x + PAD, y + PAD, 11.0f, ThemeStudioScreen.ink(a));
        Render2D.msdfText(FONT, this.second ? "подбираем второй цвет" : "подбираем основной цвет",
                x + PAD + Render2D.msdfWidth(FONT_BOLD, "Редактор темы", 11.0f) + 8.0f, y + PAD + 3.0f, 7.4f,
                ThemeStudioScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, ThemeStudioScreen.sub(a * 0.9f));

        // палитра слева
        Render2D.msdfText(FONT_BOLD, "Палитра", x + PAD, y + 42.0f, 8.0f, ThemeStudioScreen.ink(a * 0.9f));
        float swatchX = x + PAD;
        float swatchY = y + 56.0f;
        int perRow = 6;
        for (int i = 0; i < this.palette.size() && i < 24; ++i) {
            float sx = swatchX + (float)(i % perRow) * (SWATCH + 4.0f);
            float sy = swatchY + (float)(i / perRow) * (SWATCH + 4.0f);
            Render2D.rect(sx, sy, SWATCH, SWATCH, 5.0f, 0xFF000000 | this.palette.get(i));
            Render2D.outline(sx, sy, SWATCH, SWATCH, 5.0f, 1.0f, ThemeStudioScreen.rgba(255, 255, 255, 26.0f * a));
        }
        this.button(a, x + PAD, y + 200.0f, 132.0f, "Цвета из трека", mouseX, mouseY, 0);
        this.button(a, x + PAD, y + 228.0f, 132.0f, "Цвета темы", mouseX, mouseY, 1);
        this.button(a, x + PAD, y + 256.0f, 132.0f, this.second ? "Меняем 2-й цвет" : "Меняем 1-й цвет", mouseX, mouseY, 2);

        // поле подбора цвета
        float fx = x + 168.0f;
        float fy = y + 56.0f;
        this.picker(drawContext, a, fx, fy);
        this.updateHsb();

        // предпросмотр
        float px = x + 368.0f;
        float py = y + 56.0f;
        Render2D.msdfText(FONT_BOLD, "Предпросмотр", px, py - 14.0f, 8.0f, ThemeStudioScreen.ink(a * 0.9f));
        int first = this.module() == null ? 0xFF5B8CFF : this.module().rectColor.getColor() & 0xFFFFFF;
        int secondColor = this.module() == null ? 0xFFFF6FA8 : this.module().rectSecondColor.getColor() & 0xFFFFFF;
        Render2D.rect(px, py, 232.0f, 92.0f, 10.0f, ThemeStudioScreen.rgba(10, 12, 18, 235.0f * a));
        RectUtil.drawClientRect(px, py, 232.0f, 92.0f, 10.0f, a);
        Render2D.rect(px + 10.0f, py + 12.0f, 60.0f, 62.0f, 8.0f, ThemeStudioScreen.rgba(255, 255, 255, 16.0f * a));
        Render2D.msdfText(FONT_BOLD, "ByAzen", px + 80.0f, py + 16.0f, 10.0f, ThemeStudioScreen.ink(a));
        Render2D.msdfText(FONT, "модуль · включён", px + 80.0f, py + 32.0f, 6.8f, ThemeStudioScreen.sub(a));
        Render2D.rect(px + 80.0f, py + 44.0f, 140.0f, 6.0f, 3.0f, 0xFF000000 | first);
        Render2D.rect(px + 80.0f, py + 56.0f, 140.0f, 6.0f, 3.0f, 0xFF000000 | secondColor);
        Render2D.rect(px + 80.0f, py + 70.0f, 44.0f, 14.0f, 7.0f, ThemeStudioScreen.rgba(255, 255, 255, 22.0f * a));

        this.button(a, px, py + 108.0f, 112.0f, "Копировать код", mouseX, mouseY, 3);
        this.button(a, px + 120.0f, py + 108.0f, 112.0f, "Код из буфера", mouseX, mouseY, 4);
        this.button(a, px, py + 136.0f, 112.0f, "Сбросить цвет", mouseX, mouseY, 5);
        this.button(a, px + 120.0f, py + 136.0f, 112.0f, "Радуга", mouseX, mouseY, 6);

        if (System.currentTimeMillis() < this.statusUntil && !this.status.isEmpty()) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - PAD - 8.0f, 7.0f, ClientAccent.accent(a));
        }
    }

    private void picker(DrawContext drawContext, float a, float x, float y) {
        Render2D.rect(x, y, FIELD, FIELD, 8.0f, ThemeStudioScreen.rgba(255, 255, 255, 12.0f * a));
        // квадрат насыщенность/яркость для выбранного тона
        Render2D.pushScissor(drawContext, x, y, FIELD, FIELD);
        int stepsX = 24;
        int stepsY = 16;
        float cellW = FIELD / (float)stepsX;
        float cellH = FIELD / (float)stepsY;
        for (int ix = 0; ix < stepsX; ++ix) {
            for (int iy = 0; iy < stepsY; ++iy) {
                float s = ((float)ix + 0.5f) / (float)stepsX;
                float v = 1.0f - ((float)iy + 0.5f) / (float)stepsY;
                int color = 0xFF000000 | ThemeStudioScreen.hsv(this.hue, s, v);
                Render2D.rect(x + (float)ix * cellW, y + (float)iy * cellH, cellW + 0.6f, cellH + 0.6f, 0.0f, color);
            }
        }
        Render2D.popScissor(drawContext);
        Render2D.outline(x, y, FIELD, FIELD, 8.0f, 1.0f, ThemeStudioScreen.rgba(255, 255, 255, 30.0f * a));
        float cursorX = x + this.saturation * FIELD;
        float cursorY = y + (1.0f - this.brightness) * FIELD;
        Render2D.circleOutline(cursorX, cursorY, 4.0f, 1.6f, ThemeStudioScreen.rgba(20, 22, 28, 220.0f * a));
        Render2D.circleOutline(cursorX, cursorY, 6.0f, 1.4f, ThemeStudioScreen.rgba(255, 255, 255, 210.0f * a));
        // полоса тона
        float stripY = y + FIELD + 12.0f;
        int strips = 36;
        float stripW = FIELD / (float)strips;
        for (int i = 0; i < strips; ++i) {
            int color = 0xFF000000 | ThemeStudioScreen.hsv(((float)i + 0.5f) / (float)strips * 360.0f, 0.92f, 0.95f);
            Render2D.rect(x + (float)i * stripW, stripY, stripW + 0.6f, 10.0f, 0.0f, color);
        }
        float hueX = x + this.hue / 360.0f * FIELD;
        Render2D.rect(hueX - 1.0f, stripY - 2.0f, 2.0f, 14.0f, 1.0f, ThemeStudioScreen.rgba(255, 255, 255, 230.0f * a));
        Render2D.msdfText(FONT, "тон", x, stripY + 14.0f, 6.4f, ThemeStudioScreen.sub(a * 0.8f));
    }

    private void updateHsb() {
        int color = 0xFF000000 | ThemeStudioScreen.hsv(this.hue, this.saturation, this.brightness);
        this.previewColor(color);
    }

    private void previewColor(int color) {
        InterfaceModule interfaceModule = this.module();
        if (interfaceModule == null || (color & 0xFFFFFF) == this.lastPreview) {
            return;
        }
        this.lastPreview = color & 0xFFFFFF;
        if (this.second) {
            interfaceModule.rectSecondColor.setColor(color & 0xFFFFFF);
            interfaceModule.rectUseSecondColor.setValue(true);
        }
        else {
            interfaceModule.rectColor.setColor(color & 0xFFFFFF);
        }
        interfaceModule.clientColorMode.selected(InterfaceModule.CLIENT_COLOR_CUSTOM);
    }

    private void commit() {
        rtx.byazen.api.config.ConfigManager.markDirty();
        ChatMessage.send("Тема обновлена: основной " + ThemeStudioScreen.hex(this.module() == null ? 0 : this.module().rectColor.getColor())
                + ", второй " + ThemeStudioScreen.hex(this.module() == null ? 0 : this.module().rectSecondColor.getColor()));
    }

    private void button(float a, float x, float y, float w, String label, int mouseX, int mouseY, int index) {
        boolean hovered = (float)mouseX >= x && (float)mouseX <= x + w && (float)mouseY >= y && (float)mouseY <= y + 22.0f;
        Render2D.rect(x, y, w, 22.0f, 6.0f, hovered ? ThemeStudioScreen.hover(a) : ThemeStudioScreen.field(a));
        Render2D.msdfText(FONT_BOLD, label, x + 8.0f, y + 7.0f, 6.8f, ThemeStudioScreen.ink(a));
    }

    private int buttonAt(float mouseX, float mouseY) {
        float x = ThemeStudioScreen.panelX();
        float y = ThemeStudioScreen.panelY();
        float[][] rects = {
                {x + PAD, y + 200.0f, 132.0f},
                {x + PAD, y + 228.0f, 132.0f},
                {x + PAD, y + 256.0f, 132.0f},
                {x + 368.0f, y + 164.0f, 112.0f},
                {x + 488.0f, y + 164.0f, 112.0f},
                {x + 368.0f, y + 192.0f, 112.0f},
                {x + 488.0f, y + 192.0f, 112.0f},
        };
        for (int i = 0; i < rects.length; ++i) {
            if (mouseX >= rects[i][0] && mouseX <= rects[i][0] + rects[i][2] && mouseY >= rects[i][1] && mouseY <= rects[i][1] + 22.0f) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = ThemeStudioScreen.panelX();
        float y = ThemeStudioScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
            this.commit();
            this.closeScreen();
            return true;
        }
        float fx = x + 168.0f;
        float fy = y + 56.0f;
        if (click.button() == 0 && mouseX >= fx && mouseX <= fx + FIELD && mouseY >= fy && mouseY <= fy + FIELD) {
            this.dragging = true;
            this.pick(mouseX, mouseY);
            return true;
        }
        if (click.button() == 0 && mouseX >= fx && mouseX <= fx + FIELD && mouseY >= fy + FIELD + 8.0f && mouseY <= fy + FIELD + 26.0f) {
            this.hue = Math.max(0.0f, Math.min(359.0f, (mouseX - fx) / FIELD * 360.0f));
            this.previewColor(0xFF000000 | ThemeStudioScreen.hsv(this.hue, this.saturation, this.brightness));
            return true;
        }
        switch (this.buttonAt(mouseX, mouseY)) {
            case 0: {
                this.loadCoverPalette();
                break;
            }
            case 1: {
                this.loadThemePalette();
                this.status = "палитра обновлена";
                this.statusUntil = System.currentTimeMillis() + 2200L;
                break;
            }
            case 2: {
                this.second = !this.second;
                this.syncFromModule();
                break;
            }
            case 3: {
                String code = ThemeCodes.export();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && !code.isEmpty()) {
                    client.keyboard.setClipboard(code);
                }
                this.status = code.isEmpty() ? "код не собрался" : ("код темы скопирован (" + code.length() + " символов)");
                this.statusUntil = System.currentTimeMillis() + 3000L;
                break;
            }
            case 4: {
                MinecraftClient client = MinecraftClient.getInstance();
                String code = client == null ? "" : client.keyboard.getClipboard();
                String name = ThemeCodes.apply(code);
                this.status = name.isEmpty() ? "в буфере нет кода темы ByAzen" : ("тема применена: " + name);
                this.statusUntil = System.currentTimeMillis() + 3000L;
                break;
            }
            case 5: {
                InterfaceModule interfaceModule = this.module();
                if (interfaceModule != null) {
                    if (this.second) {
                        interfaceModule.rectSecondColor.setColor(interfaceModule.rectSecondColor.getDefaultColor() & 0xFFFFFF);
                    }
                    else {
                        interfaceModule.rectColor.setColor(interfaceModule.rectColor.getDefaultColor() & 0xFFFFFF);
                    }
                    this.syncFromModule();
                }
                this.status = "цвет сброшен к стандартному";
                this.statusUntil = System.currentTimeMillis() + 2500L;
                break;
            }
            case 6: {
                InterfaceModule interfaceModule = this.module();
                if (interfaceModule != null) {
                    interfaceModule.clientColorMode.selected(InterfaceModule.CLIENT_COLOR_RAINBOW);
                }
                this.status = "включена радуга";
                this.statusUntil = System.currentTimeMillis() + 2500L;
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    private void pick(float mouseX, float mouseY) {
        float fx = ThemeStudioScreen.panelX() + 168.0f;
        float fy = ThemeStudioScreen.panelY() + 56.0f;
        this.saturation = Math.max(0.0f, Math.min(1.0f, (mouseX - fx) / FIELD));
        this.brightness = Math.max(0.0f, Math.min(1.0f, 1.0f - (mouseY - fy) / FIELD));
        this.previewColor(0xFF000000 | ThemeStudioScreen.hsv(this.hue, this.saturation, this.brightness));
    }

    private void syncFromModule() {
        InterfaceModule interfaceModule = this.module();
        if (interfaceModule == null) {
            return;
        }
        int color = (this.second ? interfaceModule.rectSecondColor.getColor() : interfaceModule.rectColor.getColor()) & 0xFFFFFF;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;
        this.brightness = max;
        this.saturation = max <= 0.0f ? 0.0f : delta / max;
        if (delta <= 0.0f) {
            this.hue = 0.0f;
        }
        else if (max == red) {
            this.hue = 60.0f * (((green - blue) / delta) % 6.0f);
        }
        else if (max == green) {
            this.hue = 60.0f * ((blue - red) / delta + 2.0f);
        }
        else {
            this.hue = 60.0f * ((red - green) / delta + 4.0f);
        }
        if (this.hue < 0.0f) {
            this.hue += 360.0f;
        }
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (this.dragging) {
            this.dragging = false;
            this.commit();
        }
        return super.mouseReleased(click);
    }

    /** Ведение пипетки: у экранов этой версии нет mouseDragged, поэтому опрашиваем кнопку мыши сами. */
    private void pollDrag() {
        if (!this.dragging) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            this.dragging = false;
            return;
        }
        if (GLFW.glfwGetMouseButton(client.getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
            this.pick(Position.mouseX(), Position.mouseY());
        }
        else {
            this.dragging = false;
            this.commit();
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.commit();
            this.closeScreen();
            return true;
        }
        if (key == GLFW.GLFW_KEY_TAB) {
            this.second = !this.second;
            this.syncFromModule();
            return true;
        }
        return super.keyPressed(input);
    }

    private void closeScreen() {
        BaseScreen.beginClosingOverlay(this);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    /** Тон/насыщенность/яркость в цвет: hue 0..360, saturation и value 0..1. */
    private static int hsv(float hue, float saturation, float value) {
        float h = (hue % 360.0f + 360.0f) % 360.0f / 60.0f;
        float s = Math.max(0.0f, Math.min(1.0f, saturation));
        float v = Math.max(0.0f, Math.min(1.0f, value));
        int sector = (int)Math.floor((double)h);
        float fraction = h - (float)sector;
        float p = v * (1.0f - s);
        float q = v * (1.0f - s * fraction);
        float t = v * (1.0f - s * (1.0f - fraction));
        float red;
        float green;
        float blue;
        switch (sector % 6) {
            case 0: { red = v; green = t; blue = p; break; }
            case 1: { red = q; green = v; blue = p; break; }
            case 2: { red = p; green = v; blue = t; break; }
            case 3: { red = p; green = q; blue = v; break; }
            case 4: { red = t; green = p; blue = v; break; }
            default: { red = v; green = p; blue = q; break; }
        }
        return (int)(red * 255.0f) << 16 | (int)(green * 255.0f) << 8 | (int)(blue * 255.0f);
    }

    private static String hex(int color) {
        return "#" + String.format("%06X", color & 0xFFFFFF);
    }

    private static int ink(float alpha) {
        return ThemeStudioScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return ThemeStudioScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return ThemeStudioScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return ThemeStudioScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
