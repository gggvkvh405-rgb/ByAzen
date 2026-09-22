package rtx.byazen.api.ui;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Utils.VoiceCommandsModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.voice.VoiceCommands;

/**
 * Окно голосовых команд (идея №181 из IDEAS.md).
 * <p>
 * Список команд и их состояние: обучена или нет. Нажатие на строку — обучение (клиент слушает
 * микрофон около секунды и запоминает форму слова), кнопка «Слушать» — распознать и выполнить.
 * Микрофон включается только на время обучения и распознавания, в фоне ничего не пишется.
 */
public final class VoiceScreen
extends BaseScreen {

    private static final float W = 560.0f;
    private static final float H = 344.0f;
    private static final float PAD = 16.0f;
    private static final float ROW_H = 24.0f;
    private static final String FONT = "montserrat-medium";
    private static final String FONT_BOLD = "montserrat-bold";
    private static final String[] BUTTONS = {"Слушать", "Микрофон", "Забыть всё", "Готово"};

    private float animation;
    private String status = "Нажмите на команду и чётко произнесите слово";
    private long statusUntil;

    public VoiceScreen() {
        super(Text.literal("Голосовые команды"));
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new VoiceScreen());
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
        float a = Math.min(1.0f, this.animation += (1.0f - this.animation) * 0.25f);
        float x = VoiceScreen.panelX();
        float y = VoiceScreen.panelY();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Голосовые команды", x + PAD, y + PAD, 11.0f, VoiceScreen.ink(a));
        Render2D.msdfText(FONT, "микрофон: " + VoiceScreen.trim(VoiceCommands.microphoneSummary(), 44),
                x + PAD, y + PAD + 16.0f, 6.8f, VoiceScreen.sub(a));
        Render2D.msdfText(FONT, VoiceScreen.trim(VoiceCommands.chatModStatus(), 62), x + PAD, y + PAD + 27.0f, 6.4f,
                VoiceScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 12.0f, 9.0f, VoiceScreen.sub(a));

        List<VoiceCommands.Command> commands = VoiceCommands.commands();
        float left = x + PAD;
        float right = x + W * 0.5f + 4.0f;
        float top = y + 62.0f;
        for (int i = 0; i < commands.size(); ++i) {
            VoiceCommands.Command command = commands.get(i);
            float rowX = i % 2 == 0 ? left : right;
            float rowY = top + (float)(i / 2) * ROW_H;
            boolean trained = VoiceCommands.hasTemplate(command.id());
            boolean hovered = (float)mouseX >= rowX && (float)mouseX <= rowX + W * 0.5f - PAD - 6.0f
                    && (float)mouseY >= rowY && (float)mouseY <= rowY + ROW_H - 2.0f;
            Render2D.rect(rowX, rowY, W * 0.5f - PAD - 6.0f, ROW_H - 2.0f, 6.0f,
                    hovered ? VoiceScreen.hover(a) : VoiceScreen.field(a));
            Render2D.rect(rowX + 6.0f, rowY + 6.0f, 3.0f, ROW_H - 14.0f, 1.5f,
                    trained ? VoiceScreen.rgba(108, 208, 138, a) : VoiceScreen.rgba(150, 158, 176, a));
            Render2D.msdfText(FONT_BOLD, command.label(), rowX + 15.0f, rowY + 5.0f, 7.2f, VoiceScreen.ink(a));
            Render2D.msdfText(FONT, trained ? "обучена — нажмите, чтобы переучить" : "нажмите и скажите слово",
                    rowX + 15.0f, rowY + 14.0f, 6.0f, VoiceScreen.sub(a));
        }

        float by = y + H - 32.0f;
        float bx = x + PAD;
        for (String label : BUTTONS) {
            float w = Math.max(84.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 18.0f);
            boolean hovered = (float)mouseX >= bx && (float)mouseX <= bx + w && (float)mouseY >= by && (float)mouseY <= by + 20.0f;
            Render2D.rect(bx, by, w, 20.0f, 6.0f, hovered ? VoiceScreen.hover(a) : VoiceScreen.field(a));
            Render2D.msdfText(FONT_BOLD, label, bx + 9.0f, by + 6.0f, 7.0f, VoiceScreen.ink(a));
            bx += w + 6.0f;
        }
        Render2D.msdfText(FONT, VoiceScreen.trim(this.status, 62), x + PAD, y + H - 44.0f, 6.6f,
                System.currentTimeMillis() < this.statusUntil ? ClientAccent.accent(a) : VoiceScreen.sub(a));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = VoiceScreen.panelX();
        float y = VoiceScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 26.0f) {
            this.close();
            return true;
        }
        List<VoiceCommands.Command> commands = VoiceCommands.commands();
        float left = x + PAD;
        float right = x + W * 0.5f + 4.0f;
        float top = y + 62.0f;
        for (int i = 0; i < commands.size(); ++i) {
            VoiceCommands.Command command = commands.get(i);
            float rowX = i % 2 == 0 ? left : right;
            float rowY = top + (float)(i / 2) * ROW_H;
            if (mouseX >= rowX && mouseX <= rowX + W * 0.5f - PAD - 6.0f && mouseY >= rowY && mouseY <= rowY + ROW_H - 2.0f) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
                this.say("Говорите…");
                String result = VoiceCommands.train(command.id(), 1200);
                this.say(result);
                return true;
            }
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
                this.say("Слушаю…");
                this.say(VoiceCommands.recognizeAndRun(VoiceCommandsModule.threshold()));
                break;
            }
            case 1: {
                this.say(VoiceCommands.microphoneSummary());
                break;
            }
            case 2: {
                VoiceCommands.forgetAll();
                this.say("Все образцы забыты");
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
        this.status = text == null ? "" : text;
        this.statusUntil = System.currentTimeMillis() + 5000L;
    }

    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return VoiceScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return VoiceScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return VoiceScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return VoiceScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
