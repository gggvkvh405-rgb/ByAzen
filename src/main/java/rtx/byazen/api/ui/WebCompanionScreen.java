package rtx.byazen.api.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.impl.Utils.MobileCompanionModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.qr.QrCode;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.web.LocalHttp;
import rtx.byazen.utils.web.WebService;

/**
 * Окно мобильного компаньона (идея №175 из IDEAS.md).
 * <p>
 * Показывает QR-код и адрес страницы: телефон в той же сети наводится на код и получает живой экран
 * со статистикой, координатами, здоровьем и управлением плеером. Тут же включается доступ из
 * домашней сети, меняется ключ и копируется ссылка.
 */
public final class WebCompanionScreen
extends BaseScreen {

    private static final float W = 566.0f;
    private static final float H = 356.0f;
    private static final float PAD = 16.0f;
    private static final float QR_SIZE = 168.0f;
    private static final String FONT = "montserrat-medium";
    private static final String FONT_BOLD = "montserrat-bold";
    private static final String[] BUTTONS = {"Запустить", "Стоп", "Новый ключ", "Скопировать", "Дашборд", "Закрыть"};

    private QrCode.Symbol symbol;
    private float animation;
    private long refreshedAt;
    private String status = "";
    private long statusUntil;

    public WebCompanionScreen() {
        super(Text.literal("Мобильный компаньон"));
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new WebCompanionScreen());
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
        if (System.currentTimeMillis() - this.refreshedAt > 800L) {
            this.refreshedAt = System.currentTimeMillis();
            this.rebuildQr();
        }
        float a = Math.min(1.0f, this.animation += (1.0f - this.animation) * 0.25f);
        float x = WebCompanionScreen.panelX();
        float y = WebCompanionScreen.panelY();
        LocalHttp http = LocalHttp.get();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Мобильный компаньон", x + PAD, y + PAD, 11.0f, WebCompanionScreen.ink(a));
        Render2D.msdfText(FONT, "откройте страницу с телефона — статистика и плеер под рукой",
                x + PAD, y + PAD + 16.0f, 7.0f, WebCompanionScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 12.0f, 9.0f, WebCompanionScreen.sub(a));

        float plate = x + PAD;
        float plateY = y + 52.0f;
        Render2D.rect(plate, plateY, QR_SIZE + 24.0f, QR_SIZE + 24.0f, 10.0f, WebCompanionScreen.rgba(245, 247, 252, 246.0f * a));
        if (this.symbol != null) {
            this.drawQr(plate + 12.0f, plateY + 12.0f, QR_SIZE, a);
        }
        else {
            Render2D.msdfText(FONT, "QR появится после запуска сервера", plate + 14.0f, plateY + QR_SIZE * 0.5f, 7.0f,
                    WebCompanionScreen.rgba(90, 96, 112, a));
        }
        String link = http.running() ? http.link() : http.baseUrl();
        Render2D.msdfText(FONT_BOLD, WebCompanionScreen.trim(link, 46), plate, plateY + QR_SIZE + 30.0f, 7.0f, WebCompanionScreen.ink(a));
        Render2D.msdfText(FONT, http.running() ? "QR ведёт на эту ссылку с ключом доступа" : "сервер выключен",
                plate, plateY + QR_SIZE + 42.0f, 6.6f, WebCompanionScreen.sub(a));

        float infoX = x + PAD + QR_SIZE + 44.0f;
        float rowY = y + 56.0f;
        rowY = this.row(infoX, rowY, a, "Состояние", http.running() ? "работает" : "выключен",
                http.running() ? ClientAccent.accentBright(a) : WebCompanionScreen.rgba(232, 92, 92, a));
        rowY = this.row(infoX, rowY, a, "Адрес", http.running() ? (http.lan() ? LocalHttp.lanAddress() : "127.0.0.1") : "—", WebCompanionScreen.ink(a));
        rowY = this.row(infoX, rowY, a, "Порт", String.valueOf(http.running() ? http.port() : MobileCompanionModule.port()), WebCompanionScreen.ink(a));
        rowY = this.row(infoX, rowY, a, "Ключ", http.running() ? http.token() : "—", WebCompanionScreen.ink(a));
        rowY = this.row(infoX, rowY, a, "Доступ из сети", MobileCompanionModule.lanAllowed() ? "да (телефон рядом)" : "только этот ПК", WebCompanionScreen.ink(a));
        rowY = this.row(infoX, rowY, a, "Запросов", String.valueOf(http.requests()), WebCompanionScreen.ink(a));
        Render2D.msdfText(FONT, WebCompanionScreen.trim("как открыть: телефон в той же сети наводится на QR-код или вводит ссылку вручную",
                62), infoX, rowY + 4.0f, 6.6f, WebCompanionScreen.sub(a));
        Render2D.msdfText(FONT, WebCompanionScreen.trim("ключ в ссылке защищает страницу: без него сервер отвечает «нужен токен»",
                62), infoX, rowY + 15.0f, 6.6f, WebCompanionScreen.sub(a));

        float by = y + H - 32.0f;
        float bx = x + PAD;
        for (String label : BUTTONS) {
            float w = Math.max(84.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 18.0f);
            boolean hovered = (float)mouseX >= bx && (float)mouseX <= bx + w && (float)mouseY >= by && (float)mouseY <= by + 20.0f;
            Render2D.rect(bx, by, w, 20.0f, 6.0f, hovered ? WebCompanionScreen.hover(a) : WebCompanionScreen.field(a));
            Render2D.msdfText(FONT_BOLD, label, bx + 9.0f, by + 6.0f, 7.0f, WebCompanionScreen.ink(a));
            bx += w + 6.0f;
        }
        if (System.currentTimeMillis() < this.statusUntil) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - 44.0f, 6.8f, ClientAccent.accent(a));
        }
    }

    private float row(float x, float y, float a, String label, String value, int valueColor) {
        Render2D.rect(x, y, W - (x - WebCompanionScreen.panelX()) - PAD, 22.0f, 6.0f, WebCompanionScreen.field(a));
        Render2D.msdfText(FONT, label, x + 8.0f, y + 7.0f, 6.8f, WebCompanionScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, WebCompanionScreen.trim(value, 30), x + 120.0f, y + 7.0f, 6.8f, valueColor);
        return y + 26.0f;
    }

    private void rebuildQr() {
        try {
            String link = LocalHttp.get().running() ? LocalHttp.get().link() : null;
            this.symbol = link == null ? null : QrCode.encodeText(link);
        }
        catch (Throwable throwable) {
            this.symbol = null;
        }
    }

    /** Полосы вместо точек: так код читается камерой заметно увереннее. */
    private void drawQr(float x, float y, float size, float a) {
        if (this.symbol == null) {
            return;
        }
        int padded = this.symbol.paddedSize();
        float cell = size / (float)padded;
        for (int py = 0; py < padded; ++py) {
            int px = 0;
            while (px < padded) {
                if (!this.symbol.dark(px, py)) {
                    ++px;
                    continue;
                }
                int run = 1;
                while (px + run < padded && this.symbol.dark(px + run, py)) {
                    ++run;
                }
                Render2D.rect(x + (float)px * cell, y + (float)py * cell, (float)run * cell,
                        Math.max(1.0f, cell + 0.35f), 0.0f, WebCompanionScreen.rgba(14, 16, 22, 250.0f * a));
                px += run;
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = WebCompanionScreen.panelX();
        float y = WebCompanionScreen.panelY();
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
        LocalHttp http = LocalHttp.get();
        switch (index) {
            case 0: {
                WebService.refresh();
                this.say(http.running() ? "Сервер запущен" : "Не удалось запустить сервер");
                break;
            }
            case 1: {
                WebService.stop();
                this.say("Сервер остановлен");
                break;
            }
            case 2: {
                this.say("Новый ключ: " + http.rotateToken());
                break;
            }
            case 3: {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.keyboard.setClipboard(http.link());
                }
                this.say("Ссылка скопирована");
                break;
            }
            case 4: {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    client.keyboard.setClipboard(http.baseUrl() + "/dashboard?t=" + http.token());
                }
                this.say("Ссылка на дашборд скопирована (нужен модуль Web Dashboard)");
                break;
            }
            default: {
                this.close();
            }
        }
        this.rebuildQr();
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
        this.statusUntil = System.currentTimeMillis() + 3200L;
    }

    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return WebCompanionScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return WebCompanionScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return WebCompanionScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return WebCompanionScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
