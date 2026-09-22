package rtx.byazen.api.ui;

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
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.mods.ModsIndex;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Менеджер модов (идея №100 из IDEAS.md).
 * <p>
 * Четыре вкладки: список загруженных модов, клавиши с указанием мода-владельца, поиск по содержимому
 * jar-файлов (по названию кнопки сразу понятно, откуда она) и конфликты — повторяющиеся клавиши и
 * идентификаторы.
 */
public final class ModsScreen
extends BaseScreen {

    private static final float W = 640.0f;
    private static final float H = 404.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 16.0f;
    private static final float FIELD_H = 18.0f;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final String[] TABS = {"Моды", "Клавиши", "Поиск", "Конфликты"};

    private final Screen parent;
    private final SearchField search = new SearchField().icon("").placeholder("мод, кнопка или надпись");
    private final List<String> rows = new ArrayList<String>();
    private final List<String> details = new ArrayList<String>();

    private float appear;
    private float delta = 0.016f;
    private long lastNs;
    private float scroll;
    private float scrollTarget;
    private int tab;
    private int hovered = -1;
    private long lastRebuildMs;
    private String footer = "";

    public ModsScreen(Screen parent) {
        super(Text.literal(Lang.t("Менеджер модов", "Mod manager")));
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

    private float listTop() {
        return ModsScreen.panelY() + 86.0f;
    }

    private float listHeight() {
        return ModsScreen.panelY() + H - 30.0f - this.listTop();
    }

    /** Пересобирает список строк выбранной вкладки. */
    private void rebuild() {
        String query = this.search.getText() == null ? "" : this.search.getText().trim();
        long now = System.currentTimeMillis();
        if (now - this.lastRebuildMs < 350L) {
            return;
        }
        this.lastRebuildMs = now;
        this.rows.clear();
        this.details.clear();
        switch (this.tab) {
            case 0: {
                List<ModsIndex.Mod> mods = ModsIndex.list();
                for (ModsIndex.Mod mod : mods) {
                    if (!query.isEmpty() && !mod.display().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                            && !mod.id.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    this.rows.add(mod.display() + "   " + mod.version);
                    this.details.add(mod.id + " · " + mod.sizeText() + (mod.authors.isEmpty() ? "" : " · " + mod.authors));
                }
                this.footer = "Модов: " + mods.size() + " · " + ModsIndex.summary();
                break;
            }
            case 1: {
                List<ModsIndex.KeyEntry> keys = ModsIndex.keys();
                for (ModsIndex.KeyEntry entry : keys) {
                    if (!query.isEmpty() && !entry.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    this.rows.add(entry.name + "   →   " + entry.bound);
                    this.details.add(entry.mod.isEmpty() ? entry.category : entry.mod);
                }
                this.footer = "Клавиш: " + keys.size() + " · в колонке справа видно мод, добавивший действие";
                break;
            }
            case 2: {
                if (query.isEmpty()) {
                    this.rows.add("Впишите слово — найду надписи внутри модов");
                    this.details.add("Поиск идёт по языковым файлам и описаниям внутри jar");
                    this.footer = "Поиск по содержимому модов";
                    break;
                }
                List<ModsIndex.Hit> hits = ModsIndex.search(query, true);
                for (ModsIndex.Hit hit : hits) {
                    this.rows.add(hit.mod.display() + "   " + hit.mod.version);
                    this.details.add(hit.where);
                }
                this.footer = "Найдено модов: " + hits.size();
                break;
            }
            default: {
                List<String> conflicts = ModsIndex.keyConflicts();
                for (String line : conflicts) {
                    this.rows.add("клавиша " + line);
                    this.details.add("одно и то же сочетание у нескольких действий");
                }
                for (ModsIndex.Conflict conflict : ModsIndex.conflicts()) {
                    this.rows.add(conflict.what);
                    this.details.add(conflict.who);
                }
                this.footer = conflicts.isEmpty() ? "Конфликтов клавиш не найдено" : "Конфликтов клавиш: " + conflicts.size();
                break;
            }
        }
        if (this.rows.isEmpty()) {
            this.rows.add("Ничего не найдено");
            this.details.add(query.isEmpty() ? "Список пуст" : "Попробуйте другое слово");
        }
    }

    private int indexAt(float mouseX, float mouseY) {
        float top = this.listTop();
        if (mouseX < ModsScreen.panelX() + PAD || mouseY < top || mouseY > top + this.listHeight()) {
            return -1;
        }
        int index = (int)((mouseY - top + this.scroll) / ROW_H);
        return index >= 0 && index < this.rows.size() ? index : -1;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = ModsScreen.panelX();
        float y = ModsScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, ModsScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, "Менеджер модов", x + PAD, y + 20.0f, 12.0f, ModsScreen.rgba(255, 255, 255, 245.0f * a));
        Render2D.msdfText(FONT_TEXT, "Что загружено, кто занял клавишу, откуда кнопка и что конфликтует",
                x + PAD, y + 34.0f, 5.8f, ModsScreen.rgba(196, 202, 214, 150.0f * a));
        this.drawClose(x, y, mx, my, a);

        float tabY = y + 48.0f;
        float tabWidth = 96.0f;
        for (int i = 0; i < TABS.length; ++i) {
            float tabX = x + PAD + (tabWidth + 6.0f) * (float)i;
            boolean selected = this.tab == i;
            boolean hot = this.hover(mx, my, tabX, tabY, tabWidth, FIELD_H);
            Render2D.rect(tabX, tabY, tabWidth, FIELD_H, 6.0f, selected
                    ? ClientAccent.accentSoft(170.0f * a)
                    : ModsScreen.rgba(255, 255, 255, (hot ? 28.0f : 14.0f) * a));
            float textWidth = Render2D.msdfWidth(FONT_SEMI, TABS[i], 5.8f);
            Render2D.msdfText(FONT_SEMI, TABS[i], tabX + (tabWidth - textWidth) * 0.5f, tabY + 5.6f, 5.8f,
                    selected ? ModsScreen.rgba(12, 14, 20, 240.0f * a) : ModsScreen.rgba(220, 226, 238, 230.0f * a));
        }
        if (this.tab != 0) {
            this.search.render(drawContext, x + PAD + (tabWidth + 6.0f) * 4.0f, tabY, W - PAD * 2.0f - (tabWidth + 6.0f) * 4.0f,
                    FIELD_H, a, mx, my, this.delta);
        }

        this.rebuild();
        float left = x + PAD;
        float top = this.listTop();
        Render2D.rect(left, top, W - PAD * 2.0f, this.listHeight(), 8.0f, ModsScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.pushScissor(drawContext, left, top, W - PAD * 2.0f, this.listHeight());
        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.delta * 14.0f);
        this.hovered = this.indexAt(mx, my);
        for (int i = 0; i < this.rows.size(); ++i) {
            float rowY = top + 6.0f + (float)i * ROW_H - this.scroll;
            if (rowY + ROW_H < top || rowY > top + this.listHeight()) {
                continue;
            }
            boolean hot = i == this.hovered;
            Render2D.rect(left + 5.0f, rowY, W - PAD * 2.0f - 10.0f, ROW_H - 2.0f, 5.0f,
                    ModsScreen.rgba(255, 255, 255, (hot ? 22.0f : 8.0f) * a));
            Render2D.msdfText(FONT_SEMI, ModsScreen.trim(this.rows.get(i), 62), left + 11.0f, rowY + 4.4f, 5.9f,
                    ModsScreen.rgba(232, 238, 248, 235.0f * a));
            String detail = this.details.size() > i ? this.details.get(i) : "";
            Render2D.msdfText(FONT_TEXT, ModsScreen.trim(detail, 34), left + W - PAD * 2.0f - 240.0f, rowY + 4.6f, 5.5f,
                    ClientAccent.accentSoft(200.0f * a));
        }
        Render2D.popScissor(drawContext);
        Render2D.msdfText(FONT_TEXT, ModsScreen.trim(this.footer, 110), x + PAD, y + H - PAD - 4.0f, 5.8f,
                ClientAccent.accentSoft(220.0f * a));
    }

    private void drawClose(float x, float y, float mx, float my, float a) {
        float size = 18.0f;
        float closeX = x + W - PAD - size;
        float closeY = y + 14.0f;
        boolean hot = this.hover(mx, my, closeX, closeY, size, size);
        Render2D.rect(closeX, closeY, size, size, 6.0f, ModsScreen.rgba(255, 255, 255, (hot ? 26.0f : 10.0f) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : ModsScreen.rgba(210, 214, 224, 190.0f * a);
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
        float x = ModsScreen.panelX();
        float y = ModsScreen.panelY();
        if (this.tab != 0 && this.search.mouseClicked(mx, my, click.button())) {
            return true;
        }
        if (click.button() == 0 && this.hover(mx, my, x + W - PAD - 18.0f, y + 14.0f, 18.0f, 18.0f)) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        float tabY = y + 48.0f;
        float tabWidth = 96.0f;
        for (int i = 0; i < TABS.length; ++i) {
            float tabX = x + PAD + (tabWidth + 6.0f) * (float)i;
            if (this.hover(mx, my, tabX, tabY, tabWidth, FIELD_H)) {
                this.tab = i;
                this.scroll = 0.0f;
                this.scrollTarget = 0.0f;
                this.lastRebuildMs = 0L;
                Sounds.play("select_category");
                return true;
            }
        }
        int index = this.indexAt(mx, my);
        if (index >= 0 && index < this.rows.size() && index < this.details.size()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.keyboard.setClipboard(this.rows.get(index) + " — " + this.details.get(index));
            }
            this.footer = "Строка скопирована в буфер обмена";
            Sounds.play("select_category");
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float maxScroll = Math.max(0.0f, (float)this.rows.size() * ROW_H - this.listHeight() + 12.0f);
        this.scrollTarget = Math.max(0.0f, Math.min(maxScroll, this.scrollTarget - (float)verticalAmount * 24.0f));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256 || input.key() == 27) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (input.key() == 258) {
            this.tab = (this.tab + 1) % TABS.length;
            this.lastRebuildMs = 0L;
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
