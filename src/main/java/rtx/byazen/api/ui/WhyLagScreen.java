package rtx.byazen.api.ui;

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
import rtx.byazen.utils.compat.MixinAudit;
import rtx.byazen.utils.perf.LagReport;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.startup.EnvCheck;

/**
 * Окно «Почему лагает» (идея №170 из IDEAS.md): причины просадок простыми словами.
 * <p>
 * Никаких графиков и цифр без объяснения: каждая строка отвечает на вопрос «это плохо?» и говорит,
 * что делать. Слева полоска важности: зелёная — всё хорошо, жёлтая — стоит посмотреть, красная —
 * вероятная причина. Кнопки внизу обновляют данные, открывают профайлер и копируют отчёт.
 */
public final class WhyLagScreen
extends BaseScreen {

    private static final float W = 560.0f;
    private static final float H = 330.0f;
    private static final float PAD = 16.0f;
    private static final float ROW_H = 34.0f;
    private static final float LIST_H = 214.0f;
    private static final String FONT = "montserrat-medium";
    private static final String FONT_BOLD = "montserrat-bold";
    private static final String[] BUTTONS = {"Обновить", "Профайлер", "Окружение", "Копировать", "Закрыть"};

    private List<LagReport.Row> rows = LagReport.rows();
    private float animation;
    private long refreshedAt = System.currentTimeMillis();
    private String status = "";
    private long statusUntil;

    public WhyLagScreen() {
        super(Text.literal("Почему лагает"));
    }

    /** Открывает окно (модуль «Почему лагает» и команда diag рендер). */
    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new WhyLagScreen());
        }
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        if (System.currentTimeMillis() - this.refreshedAt > 1000L) {
            this.rows = LagReport.rows();
            this.refreshedAt = System.currentTimeMillis();
        }
        float a = Math.min(1.0f, this.animation += (1.0f - this.animation) * 0.25f);
        float x = WhyLagScreen.panelX();
        float y = WhyLagScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Почему лагает", x + PAD, y + PAD, 11.0f, WhyLagScreen.ink(a));
        String summary = LagReport.summary();
        Render2D.msdfText(FONT, summary, x + PAD, y + PAD + 16.0f, 7.4f, ClientAccent.accent(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 12.0f, 9.0f, WhyLagScreen.sub(a));

        float rowY = y + 50.0f;
        for (LagReport.Row row : this.rows) {
            if (rowY + ROW_H > y + 50.0f + LIST_H) {
                break;
            }
            Render2D.rect(x + PAD, rowY, W - PAD * 2.0f, ROW_H - 4.0f, 7.0f, WhyLagScreen.field(a));
            Render2D.rect(x + PAD + 6.0f, rowY + 7.0f, 3.0f, ROW_H - 18.0f, 1.5f, WhyLagScreen.severity(row.severity(), a));
            Render2D.msdfText(FONT_BOLD, row.title(), x + PAD + 16.0f, rowY + 6.0f, 7.6f, WhyLagScreen.ink(a));
            Render2D.msdfText(FONT, WhyLagScreen.trim(row.value(), 44), x + PAD + 16.0f, rowY + 17.0f, 6.8f,
                    ClientAccent.accentBright(a));
            Render2D.msdfText(FONT, WhyLagScreen.trim(row.verdict(), 62), x + W - PAD - 12.0f
                    - Render2D.msdfWidth(FONT, WhyLagScreen.trim(row.verdict(), 62), 6.8f), rowY + 17.0f, 6.8f,
                    WhyLagScreen.sub(a));
            rowY += ROW_H;
        }

        float by = y + H - 32.0f;
        float bx = x + PAD;
        for (String label : BUTTONS) {
            float w = Math.max(84.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 18.0f);
            boolean hovered = (float)mouseX >= bx && (float)mouseX <= bx + w && (float)mouseY >= by && (float)mouseY <= by + 20.0f;
            Render2D.rect(bx, by, w, 20.0f, 6.0f, hovered ? WhyLagScreen.hover(a) : WhyLagScreen.field(a));
            Render2D.msdfText(FONT_BOLD, label, bx + 9.0f, by + 6.0f, 7.0f, WhyLagScreen.ink(a));
            bx += w + 6.0f;
        }
        if (System.currentTimeMillis() < this.statusUntil) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - 44.0f, 6.8f, ClientAccent.accent(a));
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = WhyLagScreen.panelX();
        float y = WhyLagScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 26.0f) {
            this.close();
            return true;
        }
        float by = y + H - 32.0f;
        float bx = x + PAD;
        for (int i = 0; i < BUTTONS.length; ++i) {
            float w = Math.max(84.0f, Render2D.msdfWidth(FONT_BOLD, BUTTONS[i], 7.0f) + 18.0f);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= by && mouseY <= by + 20.0f) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
                this.action(i);
                return true;
            }
            bx += w + 6.0f;
        }
        return super.mouseClicked(click, doubled);
    }

    private void action(int index) {
        switch (index) {
            case 0: {
                this.rows = LagReport.rows();
                this.refreshedAt = System.currentTimeMillis();
                this.say("Данные обновлены");
                break;
            }
            case 1: {
                DiagnosticsScreen.open(DiagnosticsScreen.TAB_PROFILE);
                break;
            }
            case 2: {
                ChatMessage.send("§bОкружение: " + EnvCheck.summary() + " · миксины: " + MixinAudit.summary());
                this.say("Сводка окружения — в чате");
                break;
            }
            case 3: {
                StringBuilder builder = new StringBuilder();
                for (String line : LagReport.report()) {
                    builder.append(line).append('\n');
                }
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.keyboard.setClipboard(builder.toString());
                }
                this.say("Отчёт скопирован");
                break;
            }
            default: {
                this.close();
            }
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    public void close() {
        SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
        BaseScreen.beginClosingOverlay(this);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }

    private void say(String text) {
        this.status = text;
        this.statusUntil = System.currentTimeMillis() + 2600L;
    }

    private static int severity(int severity, float alpha) {
        if (severity >= 2) {
            return WhyLagScreen.rgba(232, 92, 92, 240.0f * alpha);
        }
        return severity == 1 ? WhyLagScreen.rgba(230, 186, 92, 240.0f * alpha) : WhyLagScreen.rgba(98, 208, 138, 240.0f * alpha);
    }

    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return WhyLagScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return WhyLagScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return WhyLagScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return WhyLagScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
