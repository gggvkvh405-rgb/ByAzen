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
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ForeignConfigs;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Импорт настроек других клиентов (идея №153 из IDEAS.md).
 * <p>
 * Окно показывает, какие клиенты найдены рядом с игрой и что из них можно взять: сколько клавиш и
 * основных ползунков. Перенос делается одной кнопкой — по одному клиенту или сразу из всех.
 */
public final class ConfigImportScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 520.0f;
    private static final float H = 286.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 30.0f;
    private static final int VISIBLE = 6;

    private final List<ForeignConfigs.Found> found = new ArrayList<ForeignConfigs.Found>();
    private int selected;
    private int scroll;
    private float animation;
    private long lastFrameNanos;
    private String status = "";
    private long statusUntil;

    public ConfigImportScreen() {
        super(Text.literal("Импорт настроек ByAzen"));
        this.found.addAll(ForeignConfigs.detect());
        this.selected = this.found.isEmpty() ? -1 : 0;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float listX() {
        return ConfigImportScreen.panelX() + PAD;
    }

    private static float listY() {
        return ConfigImportScreen.panelY() + PAD + 38.0f;
    }

    private static float listW() {
        return 320.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.animation += (1.0f - this.animation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.animation));
        float x = ConfigImportScreen.panelX();
        float y = ConfigImportScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Импорт настроек", x + PAD, y + PAD, 11.0f, ConfigImportScreen.ink(a));
        Render2D.msdfText(FONT, this.found.isEmpty() ? "клиенты не найдены" : "найдено: " + this.found.size(),
                x + PAD + Render2D.msdfWidth(FONT_BOLD, "Импорт настроек", 11.0f) + 8.0f, y + PAD + 3.0f, 7.4f,
                ConfigImportScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, ConfigImportScreen.sub(a * 0.9f));

        if (this.found.isEmpty()) {
            Render2D.msdfText(FONT, "Рядом с игрой нет папок других клиентов или в них нет знакомых настроек",
                    ConfigImportScreen.listX() + 4.0f, ConfigImportScreen.listY() + 10.0f, 7.2f, ConfigImportScreen.sub(a * 0.9f));
        }
        else {
            Render2D.pushScissor(drawContext, ConfigImportScreen.listX(), ConfigImportScreen.listY(), listW(),
                    (float)VISIBLE * ROW_H + 4.0f);
            float rowY = ConfigImportScreen.listY() - (float)this.scroll;
            for (int i = 0; i < this.found.size(); ++i) {
                ForeignConfigs.Found client = this.found.get(i);
                boolean active = i == this.selected;
                boolean hovered = mouseX >= ConfigImportScreen.listX() && mouseX <= ConfigImportScreen.listX() + listW()
                        && (float)mouseY >= rowY && (float)mouseY <= rowY + ROW_H - 2.0f;
                Render2D.rect(ConfigImportScreen.listX(), rowY, listW(), ROW_H - 2.0f, 6.0f,
                        active ? ConfigImportScreen.selected(a) : hovered ? ConfigImportScreen.hover(a) : ConfigImportScreen.field(a));
                Render2D.msdfText(FONT_BOLD, client.name, ConfigImportScreen.listX() + 10.0f, rowY + 5.0f, 8.4f, ConfigImportScreen.ink(a));
                Render2D.msdfText(FONT, client.summary(), ConfigImportScreen.listX() + 10.0f, rowY + 17.0f, 6.4f,
                        client.readable ? ConfigImportScreen.sub(a * 0.85f) : ConfigImportScreen.warn(a));
                rowY += ROW_H;
            }
            Render2D.popScissor(drawContext);
        }

        float bx = ConfigImportScreen.panelX() + 352.0f;
        float by = ConfigImportScreen.listY();
        this.button(a, bx, by, "Перенести выбранное", mouseX, mouseY, 0);
        this.button(a, bx, by + 28.0f, "Перенести всё", mouseX, mouseY, 1);
        this.button(a, bx, by + 56.0f, "Обновить список", mouseX, mouseY, 2);
        Render2D.msdfText(FONT, "Клавиши раскладываются по", bx, by + 92.0f, 6.2f, ConfigImportScreen.sub(a * 0.8f));
        Render2D.msdfText(FONT, "модулям, FOV и дальность —", bx, by + 101.0f, 6.2f, ConfigImportScreen.sub(a * 0.8f));
        Render2D.msdfText(FONT, "прямо в настройки игры.", bx, by + 110.0f, 6.2f, ConfigImportScreen.sub(a * 0.8f));

        if (System.currentTimeMillis() < this.statusUntil && !this.status.isEmpty()) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - PAD - 8.0f, 7.0f, ClientAccent.accent(a));
        }
    }

    private void button(float a, float x, float y, String label, int mouseX, int mouseY, int index) {
        boolean hovered = (float)mouseX >= x && (float)mouseX <= x + 150.0f && (float)mouseY >= y && (float)mouseY <= y + 22.0f;
        Render2D.rect(x, y, 150.0f, 22.0f, 6.0f, hovered ? ConfigImportScreen.hover(a) : ConfigImportScreen.field(a));
        Render2D.msdfText(FONT_BOLD, label, x + 8.0f, y + 7.0f, 7.0f, ConfigImportScreen.ink(a));
    }

    private int buttonAt(float mouseX, float mouseY) {
        float bx = ConfigImportScreen.panelX() + 352.0f;
        float by = ConfigImportScreen.listY();
        for (int i = 0; i < 3; ++i) {
            float y = by + (float)i * 28.0f;
            if (mouseX >= bx && mouseX <= bx + 150.0f && mouseY >= y && mouseY <= y + 22.0f) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = ConfigImportScreen.panelX();
        float y = ConfigImportScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
            this.closeScreen();
            return true;
        }
        if (click.button() == 0 && mouseX >= ConfigImportScreen.listX() && mouseX <= ConfigImportScreen.listX() + listW()) {
            float rowY = ConfigImportScreen.listY() - (float)this.scroll;
            for (int i = 0; i < this.found.size(); ++i) {
                if (mouseY >= rowY && mouseY <= rowY + ROW_H - 2.0f) {
                    this.selected = i;
                    return true;
                }
                rowY += ROW_H;
            }
        }
        switch (this.buttonAt(mouseX, mouseY)) {
            case 0: {
                this.applySelected();
                break;
            }
            case 1: {
                this.applyAll();
                break;
            }
            case 2: {
                this.found.clear();
                this.found.addAll(ForeignConfigs.detect());
                this.selected = this.found.isEmpty() ? -1 : 0;
                this.status = "список обновлён: найдено " + this.found.size();
                this.statusUntil = System.currentTimeMillis() + 2500L;
                break;
            }
            default:
                break;
        }
        return true;
    }

    private void applySelected() {
        ForeignConfigs.Found client = this.selected >= 0 && this.selected < this.found.size() ? this.found.get(this.selected) : null;
        if (client == null) {
            return;
        }
        ForeignConfigs.Report report = ForeignConfigs.apply(client);
        this.status = client.name + ": клавиш " + report.binds + ", настроек " + report.sliders
                + (report.notes.isEmpty() ? "" : " · " + report.notes.get(0));
        this.statusUntil = System.currentTimeMillis() + 3500L;
        ChatMessage.send("Импорт " + client.name + ": клавиш " + report.binds + ", настроек " + report.sliders);
    }

    private void applyAll() {
        int binds = 0;
        int sliders = 0;
        for (ForeignConfigs.Found client : this.found) {
            ForeignConfigs.Report report = ForeignConfigs.apply(client);
            binds += report.binds;
            sliders += report.sliders;
        }
        this.status = "перенесено: клавиш " + binds + ", настроек " + sliders;
        this.statusUntil = System.currentTimeMillis() + 3500L;
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int max = Math.max(0, (int)((float)this.found.size() * ROW_H - (float)VISIBLE * ROW_H));
        this.scroll = Math.max(0, Math.min(max, this.scroll - (int)(verticalAmount * 22.0)));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.closeScreen();
            return true;
        }
        if (key == GLFW.GLFW_KEY_DOWN && this.selected + 1 < this.found.size()) {
            ++this.selected;
            return true;
        }
        if (key == GLFW.GLFW_KEY_UP && this.selected > 0) {
            --this.selected;
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER) {
            this.applySelected();
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

    private static int ink(float alpha) {
        return ConfigImportScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return ConfigImportScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int warn(float alpha) {
        return ConfigImportScreen.rgba(255, 196, 140, 235.0f * alpha);
    }

    private static int field(float alpha) {
        return ConfigImportScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return ConfigImportScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int selected(float alpha) {
        return ConfigImportScreen.rgba(255, 255, 255, 34.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
