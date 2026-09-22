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
import rtx.byazen.utils.config.CloudConfigs;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Версии настроек (идея №151 из IDEAS.md).
 * <p>
 * Окно истории конфига: список версий с датой, размером и подписью. Версию можно откатить, удалить
 * или выгрузить коротким кодом, чтобы перенести настройки на другой компьютер.
 */
public final class CloudConfigsScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 520.0f;
    private static final float H = 300.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 28.0f;
    private static final int VISIBLE = 7;

    private final List<CloudConfigs.Version> versions = new ArrayList<CloudConfigs.Version>();
    private int selected;
    private int scroll;
    private float animation;
    private long lastFrameNanos;
    private String status = "";
    private long statusUntil;

    public CloudConfigsScreen() {
        super(Text.literal("Версии настроек ByAzen"));
        this.versions.addAll(CloudConfigs.versions());
        this.selected = this.versions.isEmpty() ? -1 : 0;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float listX() {
        return CloudConfigsScreen.panelX() + PAD;
    }

    private static float listY() {
        return CloudConfigsScreen.panelY() + PAD + 38.0f;
    }

    private static float listW() {
        return 300.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.animation += (1.0f - this.animation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.animation));
        float x = CloudConfigsScreen.panelX();
        float y = CloudConfigsScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Версии настроек", x + PAD, y + PAD, 11.0f, CloudConfigsScreen.ink(a));
        Render2D.msdfText(FONT, this.versions.size() + " версий · откат одним кликом", x + PAD + Render2D.msdfWidth(FONT_BOLD, "Версии настроек", 11.0f) + 8.0f,
                y + PAD + 3.0f, 7.4f, CloudConfigsScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, CloudConfigsScreen.sub(a * 0.9f));

        if (this.versions.isEmpty()) {
            Render2D.msdfText(FONT, "Сохранённых версий пока нет — нажмите «Сохранить» на панели справа",
                    CloudConfigsScreen.listX() + 4.0f, CloudConfigsScreen.listY() + 10.0f, 7.4f, CloudConfigsScreen.sub(a * 0.9f));
        }
        else {
            Render2D.pushScissor(drawContext, CloudConfigsScreen.listX(), CloudConfigsScreen.listY(), listW(),
                    (float)VISIBLE * ROW_H + 4.0f);
            float rowY = CloudConfigsScreen.listY() - (float)this.scroll;
            for (int i = 0; i < this.versions.size(); ++i) {
                CloudConfigs.Version version = this.versions.get(i);
                boolean active = i == this.selected;
                boolean hovered = mouseX >= CloudConfigsScreen.listX() && mouseX <= CloudConfigsScreen.listX() + listW()
                        && (float)mouseY >= rowY && (float)mouseY <= rowY + ROW_H - 2.0f;
                Render2D.rect(CloudConfigsScreen.listX(), rowY, listW(), ROW_H - 2.0f, 6.0f,
                        active ? CloudConfigsScreen.selected(a) : hovered ? CloudConfigsScreen.hover(a) : CloudConfigsScreen.field(a));
                Render2D.circle(CloudConfigsScreen.listX() + 12.0f, rowY + 12.0f, 3.2f,
                        active ? ClientAccent.accent(a) : CloudConfigsScreen.sub(a * 0.6f));
                Render2D.msdfText(FONT_BOLD, version.timeText(), CloudConfigsScreen.listX() + 24.0f, rowY + 5.0f, 8.4f, CloudConfigsScreen.ink(a));
                Render2D.msdfText(FONT, CloudConfigsScreen.label(version) + " · " + version.sizeText() + " · " + version.hash,
                        CloudConfigsScreen.listX() + 24.0f, rowY + 16.0f, 6.4f, CloudConfigsScreen.sub(a * 0.85f));
                rowY += ROW_H;
            }
            Render2D.popScissor(drawContext);
        }

        float bx = CloudConfigsScreen.panelX() + 332.0f;
        float by = CloudConfigsScreen.listY();
        this.button(drawContext, a, bx, by, 168.0f, 22.0f, "Откатить выбранную", mouseX, mouseY, 0);
        this.button(drawContext, a, bx, by + 28.0f, 168.0f, 22.0f, "Скопировать код", mouseX, mouseY, 1);
        this.button(drawContext, a, bx, by + 56.0f, 168.0f, 22.0f, "Сохранить версию", mouseX, mouseY, 2);
        this.button(drawContext, a, bx, by + 84.0f, 168.0f, 22.0f, "Удалить", mouseX, mouseY, 3);
        Render2D.msdfText(FONT, "Версии лежат рядом с конфигом:", bx, by + 118.0f, 6.4f, CloudConfigsScreen.sub(a * 0.8f));
        Render2D.msdfText(FONT, "config-versions/", bx, by + 128.0f, 6.4f, CloudConfigsScreen.sub(a * 0.6f));

        if (System.currentTimeMillis() < this.statusUntil && !this.status.isEmpty()) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - PAD - 8.0f, 7.0f, ClientAccent.accent(a));
        }
    }

    private void button(DrawContext drawContext, float a, float x, float y, float w, float h, String label,
                        int mouseX, int mouseY, int index) {
        boolean hovered = (float)mouseX >= x && (float)mouseX <= x + w && (float)mouseY >= y && (float)mouseY <= y + h;
        Render2D.rect(x, y, w, h, 6.0f, hovered ? CloudConfigsScreen.hover(a) : CloudConfigsScreen.field(a));
        Render2D.msdfText(FONT_BOLD, label, x + 8.0f, y + 7.0f, 7.2f, CloudConfigsScreen.ink(a));
    }

    private int buttonAt(float mouseX, float mouseY) {
        float bx = CloudConfigsScreen.panelX() + 332.0f;
        float by = CloudConfigsScreen.listY();
        for (int i = 0; i < 4; ++i) {
            float y = by + (float)i * 28.0f;
            if (mouseX >= bx && mouseX <= bx + 168.0f && mouseY >= y && mouseY <= y + 22.0f) {
                return i;
            }
        }
        return -1;
    }

    private static String label(CloudConfigs.Version version) {
        String name = version.name;
        int dash = name.lastIndexOf('-');
        return dash > 0 ? name.substring(0, dash) : name;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = CloudConfigsScreen.panelX();
        float y = CloudConfigsScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
            this.closeScreen();
            return true;
        }
        if (click.button() == 0 && mouseX >= CloudConfigsScreen.listX() && mouseX <= CloudConfigsScreen.listX() + listW()) {
            float rowY = CloudConfigsScreen.listY() - (float)this.scroll;
            for (int i = 0; i < this.versions.size(); ++i) {
                if (mouseY >= rowY && mouseY <= rowY + ROW_H - 2.0f) {
                    this.selected = i;
                    return true;
                }
                rowY += ROW_H;
            }
        }
        int index = this.buttonAt(mouseX, mouseY);
        switch (index) {
            case 0: {
                this.restore();
                break;
            }
            case 1: {
                this.copy();
                break;
            }
            case 2: {
                CloudConfigs.Version version = CloudConfigs.snapshot("вручную");
                this.versions.clear();
                this.versions.addAll(CloudConfigs.versions());
                this.selected = 0;
                this.status = version == null ? "не удалось сохранить" : "версия сохранена";
                this.statusUntil = System.currentTimeMillis() + 2500L;
                SoundManager.playSound(SoundManager.MODULE_ENABLE, 0.6f, 1.0f);
                break;
            }
            case 3: {
                this.delete();
                break;
            }
            default:
                break;
        }
        return true;
    }

    private void restore() {
        CloudConfigs.Version version = this.current();
        if (version == null) {
            return;
        }
        int applied = CloudConfigs.restore(version);
        this.status = applied > 0 ? ("откатились к " + version.timeText() + ": модулей " + applied) : "не удалось применить версию";
        this.statusUntil = System.currentTimeMillis() + 3000L;
        this.versions.clear();
        this.versions.addAll(CloudConfigs.versions());
        ChatMessage.send(this.status);
    }

    private void copy() {
        CloudConfigs.Version version = this.current();
        if (version == null) {
            return;
        }
        String code = CloudConfigs.exportCode(version);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && !code.isEmpty()) {
            client.keyboard.setClipboard(code);
        }
        this.status = code.isEmpty() ? "код не собрался" : ("код скопирован: " + code.length() + " символов");
        this.statusUntil = System.currentTimeMillis() + 3000L;
    }

    private void delete() {
        CloudConfigs.Version version = this.current();
        if (version == null || !CloudConfigs.delete(version)) {
            return;
        }
        this.versions.remove(this.selected);
        this.selected = this.versions.isEmpty() ? -1 : Math.min(this.selected, this.versions.size() - 1);
        this.status = "версия удалена";
        this.statusUntil = System.currentTimeMillis() + 2500L;
    }

    private CloudConfigs.Version current() {
        return this.selected >= 0 && this.selected < this.versions.size() ? this.versions.get(this.selected) : null;
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int max = Math.max(0, (int)((float)this.versions.size() * ROW_H - (float)VISIBLE * ROW_H));
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
        if (key == GLFW.GLFW_KEY_DOWN && this.selected + 1 < this.versions.size()) {
            ++this.selected;
            return true;
        }
        if (key == GLFW.GLFW_KEY_UP && this.selected > 0) {
            --this.selected;
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER) {
            this.restore();
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
        return CloudConfigsScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return CloudConfigsScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return CloudConfigsScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return CloudConfigsScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int selected(float alpha) {
        return CloudConfigsScreen.rgba(255, 255, 255, 34.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
