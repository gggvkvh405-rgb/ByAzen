package rtx.byazen.api.ui;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Utils.HandbookModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.help.ModuleHelp;
import rtx.byazen.utils.help.TutorialState;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Интерактивный туториал при первом запуске (идея №188 из IDEAS.md).
 * <p>
 * Семь коротких шагов: знакомство, модули и клавиши, поиск «где найти», хаб, диагностика,
 * обновления и финал. Каждый шаг умеет не только рассказать, но и сразу открыть нужное окно —
 * справочник, хаб, диагностику — или показать поиск модуля в действии.
 */
public final class TutorialScreen
extends BaseScreen {

    private static final String FONT = "montserrat-medium";
    private static final String FONT_BOLD = "montserrat-bold";
    private static final float W = 600.0f;
    private static final float H = 340.0f;
    private static final float PAD = 16.0f;
    private static final float RAIL = 150.0f;
    private static final float STEP_H = 26.0f;
    private static final String[] BOTTOM = {"Назад", "Дальше", "Пропустить", "Шпаргалка"};

    private int step;
    private float animation;
    private String status = "";
    private long statusUntil;

    public TutorialScreen() {
        super(Text.literal("Тур по ByAzen"));
        this.step = TutorialState.get().index();
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new TutorialScreen());
        }
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float contentX() {
        return TutorialScreen.panelX() + RAIL + 14.0f;
    }

    private static float contentW() {
        return W - RAIL - 30.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float a = Math.min(1.0f, this.animation += (1.0f - this.animation) * 0.25f);
        float x = TutorialScreen.panelX();
        float y = TutorialScreen.panelY();
        List<TutorialState.Step> steps = TutorialState.steps();
        TutorialState.Step current = TutorialState.step(this.step);

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Тур по ByAzen", x + PAD, y + 12.0f, 10.0f, TutorialScreen.ink(a));
        Render2D.msdfText(FONT, TutorialState.progressText(), x + PAD + 132.0f, y + 15.0f, 6.8f, TutorialScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, TutorialScreen.sub(a));

        for (int i = 0; i < steps.size(); ++i) {
            float rowY = y + 40.0f + (float)i * STEP_H;
            boolean active = i == this.step;
            boolean passed = i < this.step;
            Render2D.rect(x + PAD - 4.0f, rowY, RAIL, STEP_H - 4.0f, 6.0f,
                    active ? ClientAccent.accentFill(34.0f * a) : TutorialScreen.field(a * 0.6f));
            Render2D.rect(x + PAD, rowY + 8.0f, 8.0f, 8.0f, 4.0f,
                    passed ? ClientAccent.accent(a) : TutorialScreen.field(a));
            Render2D.msdfText(active ? FONT_BOLD : FONT, TutorialScreen.trim(steps.get(i).title(), 20),
                    x + PAD + 14.0f, rowY + 8.0f, 6.8f, active ? TutorialScreen.ink(a) : TutorialScreen.sub(a));
        }
        Render2D.msdfText(FONT, TutorialScreen.trim(TutorialState.firstLaunch() ? "первый запуск" : "тур можно открыть снова",
                24), x + PAD, y + H - 46.0f, 6.2f, TutorialScreen.sub(a * 0.85f));

        if (current != null) {
            Render2D.msdfText(FONT_BOLD, TutorialScreen.trim(current.title(), 40), TutorialScreen.contentX(),
                    y + 44.0f, 10.0f, TutorialScreen.ink(a));
            int line = 0;
            for (String part : current.body().split("\n")) {
                Render2D.msdfText(FONT, TutorialScreen.trim(part, 62), TutorialScreen.contentX(),
                        y + 66.0f + (float)line * 15.0f, 7.0f, TutorialScreen.sub(a));
                ++line;
            }
            float actionY = y + 66.0f + (float)Math.max(3, line) * 15.0f + 8.0f;
            float actionW = Math.max(120.0f, Render2D.msdfWidth(FONT_BOLD, current.actionLabel(), 7.2f) + 22.0f);
            boolean hovered = TutorialScreen.inside(mouseX, mouseY, TutorialScreen.contentX(), actionY, actionW, 22.0f);
            Render2D.rect(TutorialScreen.contentX(), actionY, actionW, 22.0f, 6.0f,
                    hovered ? ClientAccent.accentFill(60.0f * a) : ClientAccent.accentFill(38.0f * a));
            Render2D.msdfText(FONT_BOLD, current.actionLabel(), TutorialScreen.contentX() + 11.0f, actionY + 7.0f, 7.2f,
                    ClientAccent.accentBright(a));
            Render2D.msdfText(FONT, "шаг " + (this.step + 1) + " из " + steps.size() + " · " + TutorialState.progressText(),
                    TutorialScreen.contentX(), actionY + 30.0f, 6.2f, TutorialScreen.sub(a * 0.8f));
        }

        float by = y + H - 30.0f;
        float bx = x + PAD;
        for (String label : BOTTOM) {
            float w = Math.max(80.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 18.0f);
            boolean hovered = TutorialScreen.inside(mouseX, mouseY, bx, by, w, 20.0f);
            Render2D.rect(bx, by, w, 20.0f, 6.0f, hovered ? TutorialScreen.hover(a) : TutorialScreen.field(a));
            Render2D.msdfText(FONT_BOLD, label, bx + 9.0f, by + 6.0f, 7.0f, TutorialScreen.ink(a));
            bx += w + 6.0f;
        }
        if (System.currentTimeMillis() < this.statusUntil) {
            Render2D.msdfText(FONT, TutorialScreen.trim(this.status, 92), x + PAD, y + H - 42.0f, 6.4f,
                    ClientAccent.accent(a));
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = TutorialScreen.panelX();
        float y = TutorialScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 26.0f) {
            this.close();
            return true;
        }
        List<TutorialState.Step> steps = TutorialState.steps();
        for (int i = 0; i < steps.size(); ++i) {
            float rowY = y + 40.0f + (float)i * STEP_H;
            if (TutorialScreen.inside(mouseX, mouseY, x + PAD - 4.0f, rowY, RAIL, STEP_H - 4.0f)) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.45f, 1.2f);
                this.step = i;
                TutorialState.goTo(i);
                return true;
            }
        }
        TutorialState.Step current = TutorialState.step(this.step);
        if (current != null) {
            int line = 0;
            for (String ignored : current.body().split("\n")) {
                ++line;
            }
            float actionY = y + 66.0f + (float)Math.max(3, line) * 15.0f + 8.0f;
            float actionW = Math.max(120.0f, Render2D.msdfWidth(FONT_BOLD, current.actionLabel(), 7.2f) + 22.0f);
            if (TutorialScreen.inside(mouseX, mouseY, TutorialScreen.contentX(), actionY, actionW, 22.0f)) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
                this.runAction(current);
                return true;
            }
        }
        float by = y + H - 30.0f;
        float bx = x + PAD;
        for (int i = 0; i < BOTTOM.length; ++i) {
            float w = Math.max(80.0f, Render2D.msdfWidth(FONT_BOLD, BOTTOM[i], 7.0f) + 18.0f);
            if (TutorialScreen.inside(mouseX, mouseY, bx, by, w, 20.0f)) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
                this.bottom(i);
                return true;
            }
            bx += w + 6.0f;
        }
        return super.mouseClicked(click, doubled);
    }

    private void bottom(int index) {
        switch (index) {
            case 0: {
                TutorialState.previous();
                this.step = TutorialState.get().index();
                break;
            }
            case 1: {
                if (this.step >= TutorialState.size() - 1) {
                    this.finish();
                    return;
                }
                TutorialState.next();
                this.step = TutorialState.get().index();
                break;
            }
            case 2: {
                TutorialState.finish();
                this.say("Тур можно открыть заново из модуля Tutorial");
                this.close();
                break;
            }
            default: {
                this.copy(TutorialState.cheatSheet(), "Шпаргалка скопирована в буфер");
            }
        }
    }

    private void runAction(TutorialState.Step step) {
        switch (step.action()) {
            case "hub0": {
                ByAzenHubScreen.open(0);
                break;
            }
            case "handbook": {
                HandbookModule.openScreen();
                break;
            }
            case "finddemo": {
                this.say(ModuleHelp.locate("звук"));
                break;
            }
            case "hub2": {
                ByAzenHubScreen.open(2);
                break;
            }
            case "diag": {
                DiagnosticsScreen.open(0);
                break;
            }
            case "cheat": {
                this.copy(TutorialState.cheatSheet(), "Шпаргалка скопирована в буфер");
                break;
            }
            default: {
                this.finish();
            }
        }
    }

    private void finish() {
        TutorialState.finish();
        this.say("Готово! Тур всегда доступен в модуле Tutorial");
        this.close();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_RIGHT) {
            TutorialState.next();
            this.step = TutorialState.get().index();
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_LEFT) {
            TutorialState.previous();
            this.step = TutorialState.get().index();
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

    private void copy(String text, String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && text != null) {
            client.keyboard.setClipboard(text);
        }
        this.say(message);
    }

    private void say(String text) {
        this.status = text == null ? "" : text;
        this.statusUntil = System.currentTimeMillis() + 4000L;
    }

    private static boolean inside(float mouseX, float mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return TutorialScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return TutorialScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return TutorialScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return TutorialScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
