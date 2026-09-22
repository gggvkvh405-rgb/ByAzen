package rtx.byazen.api.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.ConfigBackupModule;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.config.ConfigTransfer;
import rtx.byazen.utils.config.ModulePresets;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.qr.QrCode;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Обмен конфигурацией (идея №101 из IDEAS.md).
 * <p>
 * Три вкладки: файл всей конфигурации, QR-код профиля выбранного модуля и импорт. Файл переносит
 * настройки на другое устройство целиком, QR удобно снять камерой телефона, а импорт применяет
 * настройки сразу — перед заменой клиент сам делает резервную копию.
 */
public final class ConfigTransferScreen
extends BaseScreen {

    private static final float W = 640.0f;
    private static final float H = 424.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 18.0f;
    private static final float FIELD_H = 18.0f;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final String[] TABS = {"Файл", "QR профиля", "Импорт"};
    private static final int QR_LIMIT = 2900;

    private final Screen parent;
    private final SearchField search = new SearchField().icon("").placeholder("модуль или код конфигурации");
    private final List<Module> modules = new ArrayList<Module>();
    private final List<Path> exports = new ArrayList<Path>();

    private float appear;
    private float delta = 0.016f;
    private long lastNs;
    private int tab;
    private int selected = -1;
    private float scroll;
    private float scrollTarget;
    private long lastFilesMs;
    private String status = "Готово к обмену: файл переносит всю конфигурацию, QR — профиль модуля";
    private String code = "";
    private int codeBytes;
    private QrCode.Symbol symbol;
    private String symbolCode = "";

    public ConfigTransferScreen(Screen parent) {
        super(Text.literal(Lang.t("Обмен конфигурацией", "Config transfer")));
        this.parent = parent;
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

    private float bodyTop() {
        return ConfigTransferScreen.panelY() + 66.0f;
    }

    private float bodyHeight() {
        return H - (this.bodyTop() - ConfigTransferScreen.panelY()) - 30.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = ConfigTransferScreen.panelX();
        float y = ConfigTransferScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, ConfigTransferScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, "Обмен конфигурацией", x + PAD, y + 20.0f, 12.0f, ConfigTransferScreen.rgba(255, 255, 255, 245.0f * a));
        Render2D.msdfText(FONT_TEXT, "Один файл переносит все настройки · QR-код удобно снять камерой телефона",
                x + PAD, y + 34.0f, 5.8f, ConfigTransferScreen.rgba(196, 202, 214, 150.0f * a));
        this.drawClose(x, y, mx, my, a);

        float tabY = y + 48.0f;
        float tabWidth = 104.0f;
        for (int i = 0; i < TABS.length; ++i) {
            float tabX = x + PAD + (tabWidth + 6.0f) * (float)i;
            boolean active = this.tab == i;
            boolean hot = this.hover(mx, my, tabX, tabY, tabWidth, FIELD_H);
            Render2D.rect(tabX, tabY, tabWidth, FIELD_H, 6.0f, active
                    ? ClientAccent.accentSoft(170.0f * a)
                    : ConfigTransferScreen.rgba(255, 255, 255, (hot ? 28.0f : 14.0f) * a));
            float textWidth = Render2D.msdfWidth(FONT_SEMI, TABS[i], 5.8f);
            Render2D.msdfText(FONT_SEMI, TABS[i], tabX + (tabWidth - textWidth) * 0.5f, tabY + 5.6f, 5.8f,
                    active ? ConfigTransferScreen.rgba(12, 14, 20, 240.0f * a) : ConfigTransferScreen.rgba(220, 226, 238, 230.0f * a));
        }
        if (this.tab != 0) {
            this.search.render(drawContext, x + PAD + (tabWidth + 6.0f) * 3.0f, tabY,
                    W - PAD * 2.0f - (tabWidth + 6.0f) * 3.0f, FIELD_H, a, mx, my, this.delta);
        }

        long now = System.currentTimeMillis();
        if (now - this.lastFilesMs > 400L) {
            this.lastFilesMs = now;
            this.exports.clear();
            this.exports.addAll(ConfigTransfer.files());
        }
        if (this.codeBytes <= 0) {
            this.code = ConfigTransfer.currentCode();
            this.codeBytes = ConfigTransfer.codeBytes(this.code);
        }
        if (this.tab == 0) {
            this.renderFileTab(x, y, mx, my, a);
        }
        else if (this.tab == 1) {
            this.renderQrTab(drawContext, x, y, mx, my, a);
        }
        else {
            this.renderImportTab(x, y, mx, my, a);
        }
        Render2D.msdfText(FONT_TEXT, ConfigTransferScreen.trim(this.status, 108), x + PAD, y + H - PAD - 4.0f, 5.8f,
                ClientAccent.accentSoft(220.0f * a));
    }

    private void renderFileTab(float x, float y, float mx, float my, float a) {
        float left = x + PAD;
        float top = this.bodyTop();
        float height = this.bodyHeight();
        Render2D.rect(left, top, W - PAD * 2.0f, height, 8.0f, ConfigTransferScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.msdfText(FONT_SEMI, "Файл конфигурации", left + 10.0f, top + 10.0f, 8.0f, ConfigTransferScreen.rgba(236, 240, 250, 240.0f * a));
        Render2D.msdfText(FONT_TEXT, "Размер кода: " + ConfigTransfer.sizeText(this.codeBytes)
                + " · сохранённых файлов: " + this.exports.size(), left + 10.0f, top + 24.0f, 6.0f,
                ConfigTransferScreen.rgba(186, 194, 210, 220.0f * a));
        Render2D.msdfText(FONT_TEXT, "Файл с расширением .bycfg лежит рядом с настройками, в папке exports.",
                left + 10.0f, top + 36.0f, 5.8f, ConfigTransferScreen.rgba(160, 168, 186, 215.0f * a));
        float buttonsY = top + 54.0f;
        this.button("Сохранить файл", left + 10.0f, buttonsY, 128.0f, this.hover(mx, my, left + 10.0f, buttonsY, 128.0f, FIELD_H), true, a);
        this.button("Обновить код", left + 146.0f, buttonsY, 128.0f, this.hover(mx, my, left + 146.0f, buttonsY, 128.0f, FIELD_H), false, a);
        this.button("Скопировать код", left + 282.0f, buttonsY, 128.0f, this.hover(mx, my, left + 282.0f, buttonsY, 128.0f, FIELD_H), false, a);
        this.button("Импорт из файла", left + 418.0f, buttonsY, 128.0f, this.hover(mx, my, left + 418.0f, buttonsY, 128.0f, FIELD_H), false, a);
        Render2D.msdfText(FONT_TEXT, "Что входит в файл: настройки модулей, HUD, темы, путевые точки, макросы и история — всё, что лежит рядом с клиентом.",
                left + 10.0f, buttonsY + 30.0f, 5.6f, ConfigTransferScreen.rgba(170, 178, 194, 210.0f * a));
        Render2D.msdfText(FONT_TEXT, "Резервные копии перед импортом делает модуль «Автобэкапы конфигов».",
                left + 10.0f, buttonsY + 41.0f, 5.6f, ConfigTransferScreen.rgba(170, 178, 194, 210.0f * a));
        Render2D.msdfText(FONT_TEXT, "Сохранённые файлы:", left + 10.0f, buttonsY + 58.0f, 5.8f, ConfigTransferScreen.rgba(196, 202, 214, 220.0f * a));
        if (this.exports.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, "пока ничего не сохранено — нажмите «Сохранить файл»",
                    left + 10.0f, buttonsY + 70.0f, 5.6f, ConfigTransferScreen.rgba(150, 158, 176, 210.0f * a));
        }
        for (int i = 0; i < Math.min(6, this.exports.size()); ++i) {
            Render2D.msdfText(FONT_TEXT, "· " + this.exports.get(i).getFileName(), left + 10.0f,
                    buttonsY + 70.0f + (float)i * 11.0f, 5.6f, ConfigTransferScreen.rgba(206, 214, 230, 220.0f * a));
        }
    }

    private void renderQrTab(DrawContext drawContext, float x, float y, float mx, float my, float a) {
        float listWidth = 200.0f;
        float top = this.bodyTop();
        float height = this.bodyHeight();
        this.modules.clear();
        String query = this.search.getText() == null ? "" : this.search.getText().trim().toLowerCase(Locale.ROOT);
        for (Module module : ModuleManager.get().getAll()) {
            if (query.isEmpty() || module.getName().toLowerCase(Locale.ROOT).contains(query)) {
                this.modules.add(module);
            }
        }
        Render2D.rect(x + PAD, top, listWidth, height, 8.0f, ConfigTransferScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.pushScissor(drawContext, x + PAD, top, listWidth, height);
        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.delta * 14.0f);
        for (int i = 0; i < this.modules.size(); ++i) {
            float rowY = top + 6.0f + (float)i * ROW_H - this.scroll;
            if (rowY + ROW_H < top || rowY > top + height) {
                continue;
            }
            boolean active = this.selected == i;
            boolean hot = this.hover(mx, my, x + PAD + 5.0f, rowY, listWidth - 10.0f, ROW_H - 3.0f);
            Render2D.rect(x + PAD + 5.0f, rowY, listWidth - 10.0f, ROW_H - 3.0f, 5.0f, active
                    ? ClientAccent.accentSoft(165.0f * a)
                    : ConfigTransferScreen.rgba(255, 255, 255, (hot ? 24.0f : 8.0f) * a));
            Render2D.msdfText(FONT_SEMI, ConfigTransferScreen.trim(this.modules.get(i).getName(), 24), x + PAD + 11.0f, rowY + 4.6f, 5.9f,
                    active ? ConfigTransferScreen.rgba(12, 14, 20, 240.0f * a) : ConfigTransferScreen.rgba(224, 230, 242, 230.0f * a));
        }
        Render2D.popScissor(drawContext);

        float rightX = x + PAD + listWidth + 10.0f;
        float rightWidth = W - PAD * 2.0f - listWidth - 10.0f;
        Render2D.rect(rightX, top, rightWidth, height, 8.0f, ConfigTransferScreen.rgba(10, 12, 17, 150.0f * a));
        if (this.selected < 0 || this.selected >= this.modules.size()) {
            Render2D.msdfText(FONT_TEXT, "Выберите модуль слева — покажу QR-код его настроек.",
                    rightX + 12.0f, top + 12.0f, 6.0f, ConfigTransferScreen.rgba(180, 188, 204, 220.0f * a));
            Render2D.msdfText(FONT_TEXT, "Так профиль модуля переносится на телефон или отдаётся другу.",
                    rightX + 12.0f, top + 26.0f, 5.8f, ConfigTransferScreen.rgba(158, 166, 184, 200.0f * a));
            return;
        }
        Module module = this.modules.get(this.selected);
        String preset = ModulePresets.export(module);
        if (preset == null || preset.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, "У этого модуля нет настроек для профиля.", rightX + 12.0f, top + 12.0f, 6.0f,
                    ConfigTransferScreen.rgba(226, 180, 180, 225.0f * a));
            return;
        }
        if (!preset.equals(this.symbolCode) || this.symbol == null) {
            this.symbolCode = preset;
            this.symbol = preset.length() > QR_LIMIT ? null : QrCode.encodeText(preset);
        }
        int length = preset.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        Render2D.msdfText(FONT_SEMI, module.getName(), rightX + 12.0f, top + 10.0f, 7.6f, ConfigTransferScreen.rgba(236, 240, 250, 240.0f * a));
        Render2D.msdfText(FONT_TEXT, "Код профиля: " + length + " Б" + (this.symbol != null ? " · QR " + this.symbol.version + "-й версии" : ""),
                rightX + 12.0f, top + 23.0f, 5.8f, ConfigTransferScreen.rgba(180, 188, 204, 220.0f * a));
        float areaTop = top + 36.0f;
        float areaHeight = height - 36.0f - 26.0f;
        if (this.symbol == null) {
            if (length > QR_LIMIT) {
                Render2D.msdfText(FONT_TEXT, "Профиль больше QR-кода (" + length + " Б). Перенесите его", rightX + 12.0f, areaTop + 6.0f, 6.0f,
                        ConfigTransferScreen.rgba(232, 196, 132, 225.0f * a));
                Render2D.msdfText(FONT_TEXT, "файлом конфигурации или текстом кода.", rightX + 12.0f, areaTop + 19.0f, 6.0f,
                        ConfigTransferScreen.rgba(232, 196, 132, 225.0f * a));
            }
            else {
                Render2D.msdfText(FONT_TEXT, "Не удалось построить код профиля.", rightX + 12.0f, areaTop + 6.0f, 6.0f,
                        ConfigTransferScreen.rgba(226, 180, 180, 225.0f * a));
            }
            this.button("Скопировать код", rightX + 12.0f, top + height - 24.0f, 150.0f,
                    this.hover(mx, my, rightX + 12.0f, top + height - 24.0f, 150.0f, FIELD_H), false, a);
            return;
        }
        float side = Math.min(rightWidth - 24.0f, areaHeight);
        float qrX = rightX + (rightWidth - side) * 0.5f;
        float qrY = areaTop;
        Render2D.rect(qrX - 5.0f, qrY - 5.0f, side + 10.0f, side + 10.0f, 8.0f, ConfigTransferScreen.rgba(252, 253, 255, 245.0f * a));
        this.drawQr(qrX, qrY, side);
        Render2D.msdfText(FONT_TEXT, "Наведите камеру телефона: код переносит настройки модуля",
                rightX + 12.0f, top + height - 34.0f, 5.6f, ConfigTransferScreen.rgba(186, 194, 210, 220.0f * a));
        this.button("Скопировать код", rightX + 12.0f, top + height - 24.0f, 150.0f,
                this.hover(mx, my, rightX + 12.0f, top + height - 24.0f, 150.0f, FIELD_H), false, a);
        this.button("Применить профиль", rightX + 170.0f, top + height - 24.0f, 150.0f,
                this.hover(mx, my, rightX + 170.0f, top + height - 24.0f, 150.0f, FIELD_H), true, a);
    }

    /** Рисует модули QR-кода полосами: так строки не размазываются и код уверенно читается камерой. */
    private void drawQr(float x, float y, float size) {
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
                        Math.max(1.0f, cell + 0.35f), 0.0f, ConfigTransferScreen.rgba(14, 16, 22, 250.0f));
                px += run;
            }
        }
    }

    private void renderImportTab(float x, float y, float mx, float my, float a) {
        float left = x + PAD;
        float top = this.bodyTop();
        float height = this.bodyHeight();
        Render2D.rect(left, top, W - PAD * 2.0f, height, 8.0f, ConfigTransferScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.msdfText(FONT_SEMI, "Импорт конфигурации", left + 10.0f, top + 10.0f, 8.0f, ConfigTransferScreen.rgba(236, 240, 250, 240.0f * a));
        Render2D.msdfText(FONT_TEXT, "Вставьте код в поле поиска сверху и нажмите «Импорт из кода»:",
                left + 10.0f, top + 24.0f, 6.0f, ConfigTransferScreen.rgba(186, 194, 210, 220.0f * a));
        String text = this.search.getText() == null ? "" : this.search.getText().trim();
        boolean looks = ConfigTransfer.looksLikeCode(text);
        Render2D.msdfText(FONT_TEXT, looks ? "Код распознан, длина " + text.length() + " символов"
                : (text.isEmpty() ? "Код пока не вставлен" : "Это не похоже на код ByAzen"),
                left + 10.0f, top + 36.0f, 5.8f, looks ? ClientAccent.accentSoft(228.0f * a) : ConfigTransferScreen.rgba(214, 176, 176, 225.0f * a));
        float buttonsY = top + 54.0f;
        this.button("Импорт из кода", left + 10.0f, buttonsY, 150.0f, this.hover(mx, my, left + 10.0f, buttonsY, 150.0f, FIELD_H), true, a);
        this.button("Импорт из файла", left + 168.0f, buttonsY, 170.0f, this.hover(mx, my, left + 168.0f, buttonsY, 170.0f, FIELD_H), false, a);
        this.button("Открыть папку", left + 346.0f, buttonsY, 160.0f, this.hover(mx, my, left + 346.0f, buttonsY, 160.0f, FIELD_H), false, a);
        Render2D.msdfText(FONT_TEXT, "Импорт заменяет настройки клиента и сразу применяет их без перезапуска игры.",
                left + 10.0f, buttonsY + 32.0f, 5.8f, ConfigTransferScreen.rgba(160, 168, 186, 215.0f * a));
        Render2D.msdfText(FONT_TEXT, "Перед заменой клиент делает резервную копию, поэтому вернуть прежние настройки несложно.",
                left + 10.0f, buttonsY + 43.0f, 5.8f, ConfigTransferScreen.rgba(160, 168, 186, 215.0f * a));
    }

    private void button(String label, float x, float y, float width, boolean hot, boolean accent, float a) {
        Render2D.rect(x, y, width, FIELD_H, 6.0f, accent
                ? ClientAccent.accentSoft((hot ? 215.0f : 180.0f) * a)
                : ConfigTransferScreen.rgba(255, 255, 255, (hot ? 30.0f : 16.0f) * a));
        float textWidth = Render2D.msdfWidth(FONT_SEMI, label, 5.8f);
        Render2D.msdfText(FONT_SEMI, label, x + (width - textWidth) * 0.5f, y + 5.6f, 5.8f,
                accent ? ConfigTransferScreen.rgba(12, 14, 20, 240.0f * a) : ConfigTransferScreen.rgba(222, 228, 240, 230.0f * a));
    }

    private void drawClose(float x, float y, float mx, float my, float a) {
        float size = 18.0f;
        float closeX = x + W - PAD - size;
        float closeY = y + 14.0f;
        boolean hot = this.hover(mx, my, closeX, closeY, size, size);
        Render2D.rect(closeX, closeY, size, size, 6.0f, ConfigTransferScreen.rgba(255, 255, 255, (hot ? 26.0f : 10.0f) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : ConfigTransferScreen.rgba(210, 214, 224, 190.0f * a);
        Render2D.line(closeX + 5.4f, closeY + 5.4f, closeX + size - 5.4f, closeY + size - 5.4f, 1.4f, ink);
        Render2D.line(closeX + size - 5.4f, closeY + 5.4f, closeX + 5.4f, closeY + size - 5.4f, 1.4f, ink);
    }

    private boolean hover(float mx, float my, float x, float y, float width, float height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = ConfigTransferScreen.panelX();
        float y = ConfigTransferScreen.panelY();
        if (this.tab != 0 && this.search.mouseClicked(mx, my, click.button())) {
            return true;
        }
        if (click.button() == 0 && this.hover(mx, my, x + W - PAD - 18.0f, y + 14.0f, 18.0f, 18.0f)) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        float tabY = y + 48.0f;
        float tabWidth = 104.0f;
        for (int i = 0; i < TABS.length; ++i) {
            float tabX = x + PAD + (tabWidth + 6.0f) * (float)i;
            if (this.hover(mx, my, tabX, tabY, tabWidth, FIELD_H)) {
                this.tab = i;
                this.scroll = 0.0f;
                this.scrollTarget = 0.0f;
                Sounds.play("select_category");
                return true;
            }
        }
        float left = x + PAD;
        float top = this.bodyTop();
        float height = this.bodyHeight();
        if (this.tab == 0) {
            float buttonsY = top + 54.0f;
            if (this.hover(mx, my, left + 10.0f, buttonsY, 128.0f, FIELD_H)) {
                ConfigTransfer.Result result = ConfigTransfer.exportToFile();
                this.status = result.message;
                this.code = result.code;
                this.codeBytes = ConfigTransfer.codeBytes(this.code);
                this.lastFilesMs = 0L;
                Sounds.play(result.ok ? "select_category" : "settings_close");
                return true;
            }
            if (this.hover(mx, my, left + 146.0f, buttonsY, 128.0f, FIELD_H)) {
                this.code = ConfigTransfer.currentCode();
                this.codeBytes = ConfigTransfer.codeBytes(this.code);
                this.status = "Код обновлён: " + ConfigTransfer.sizeText(this.codeBytes);
                Sounds.play("select_category");
                return true;
            }
            if (this.hover(mx, my, left + 282.0f, buttonsY, 128.0f, FIELD_H)) {
                this.copy(this.code.isEmpty() ? ConfigTransfer.currentCode() : this.code);
                return true;
            }
            if (this.hover(mx, my, left + 418.0f, buttonsY, 128.0f, FIELD_H)) {
                ConfigBackupModule.beforeProfileChange();
                ConfigTransfer.Result result = ConfigTransfer.importLatest();
                this.status = result.message;
                Sounds.play(result.ok ? "select_category" : "settings_close");
                return true;
            }
            return true;
        }
        if (this.tab == 1) {
            float listWidth = 200.0f;
            int index = (int)Math.floor(((double)my - (double)top - 6.0 + (double)this.scroll) / (double)ROW_H);
            if (mx >= left && mx <= left + listWidth && my >= top && my <= top + height
                    && index >= 0 && index < this.modules.size()) {
                this.selected = index;
                this.symbol = null;
                this.symbolCode = "";
                Sounds.play("select_category");
                return true;
            }
            float rightX = left + listWidth + 10.0f;
            if (this.hover(mx, my, rightX + 12.0f, top + height - 24.0f, 150.0f, FIELD_H)) {
                this.copy(this.symbolCode);
                return true;
            }
            if (this.hover(mx, my, rightX + 170.0f, top + height - 24.0f, 150.0f, FIELD_H)) {
                this.applySelected();
                return true;
            }
            return true;
        }
        float buttonsY = top + 54.0f;
        if (this.hover(mx, my, left + 10.0f, buttonsY, 150.0f, FIELD_H)) {
            ConfigBackupModule.beforeProfileChange();
            String text = this.search.getText() == null ? "" : this.search.getText();
            ConfigTransfer.Result result = ConfigTransfer.importCode(text);
            this.status = result.message;
            Sounds.play(result.ok ? "select_category" : "settings_close");
            return true;
        }
        if (this.hover(mx, my, left + 168.0f, buttonsY, 170.0f, FIELD_H)) {
            ConfigBackupModule.beforeProfileChange();
            ConfigTransfer.Result result = ConfigTransfer.importLatest();
            this.status = result.message;
            Sounds.play(result.ok ? "select_category" : "settings_close");
            return true;
        }
        if (this.hover(mx, my, left + 346.0f, buttonsY, 160.0f, FIELD_H)) {
            this.openFolder();
            return true;
        }
        return true;
    }

    private void applySelected() {
        if (this.selected < 0 || this.selected >= this.modules.size() || this.symbolCode.isEmpty()) {
            this.status = "Сначала выберите модуль и дождитесь построения кода";
            return;
        }
        Module module = this.modules.get(this.selected);
        int count = ModulePresets.apply(module, this.symbolCode);
        this.status = count >= 0
                ? "Профиль модуля " + module.getName() + " применён: настроек " + count
                : "Код профиля не подошёл этому модулю";
        Sounds.play(count >= 0 ? "select_category" : "settings_close");
    }

    private void copy(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || text == null || text.isEmpty()) {
            this.status = "Копировать нечего";
            return;
        }
        client.keyboard.setClipboard(text);
        this.status = "Код скопирован в буфер обмена (" + text.length() + " символов)";
        Sounds.play("select_category");
    }

    private void openFolder() {
        try {
            Path folder = ConfigTransfer.directory();
            Files.createDirectories(folder);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(folder.toFile());
                this.status = "Папка экспорта открыта: " + folder;
                return;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.keyboard.setClipboard(folder.toString());
            }
            this.status = "Путь к папке экспорта скопирован: " + folder;
        }
        catch (Throwable throwable) {
            this.status = "Не удалось открыть папку экспорта";
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float maxScroll = Math.max(0.0f, (float)this.modules.size() * ROW_H - this.bodyHeight());
        this.scrollTarget = Math.max(0.0f, Math.min(maxScroll, this.scrollTarget - (float)verticalAmount * 22.0f));
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
        if (key == 258) {
            this.tab = (this.tab + 1) % TABS.length;
            this.scroll = 0.0f;
            this.scrollTarget = 0.0f;
            return true;
        }
        if (key == 257 && this.tab == 2) {
            ConfigBackupModule.beforeProfileChange();
            ConfigTransfer.Result result = ConfigTransfer.importCode(this.search.getText());
            this.status = result.message;
            return true;
        }
        return this.search.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return this.search.charTyped(input);
    }

    @Override
    public void close() {
        this.search.blur();
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
