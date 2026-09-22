package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.server.ServerFavorites;
import rtx.byazen.utils.server.ServerPinger;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Проверка серверов (идея №99 из IDEAS.md).
 * <p>
 * Список избранных адресов с живым состоянием: пинг полосками, игроки и места, версия, описание.
 * Адрес добавляется в один клик, проверку можно обновить у одного сервера или сразу у всех, а вход
 * на сервер выполняется прямо отсюда.
 */
public final class ServerCheckScreen
extends BaseScreen {

    private static final float W = 580.0f;
    private static final float H = 392.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 42.0f;
    private static final float FIELD_H = 18.0f;
    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";

    private final Screen parent;
    private final SearchField address = new SearchField().icon("").placeholder("play.example.net:25565");
    private final SearchField note = new SearchField().icon("").placeholder("пометка (необязательно)");
    private final List<ServerFavorites.Entry> entries = new ArrayList<ServerFavorites.Entry>();
    private final Map<String, ServerPinger.Status> statuses = new LinkedHashMap<String, ServerPinger.Status>();
    private final List<String> pending = new ArrayList<String>();

    private float appear;
    private float delta = 0.016f;
    private long lastNs;
    private float scroll;
    private float scrollTarget;
    private int hovered = -1;
    private long lastRefreshMs;
    private boolean checking;
    private String footer = "Избранных серверов пока нет — добавьте адрес сверху";

    public ServerCheckScreen(Screen parent) {
        super(Text.literal(Lang.t("Проверка серверов", "Server check")));
        this.parent = parent;
        this.reload();
        this.checkAll();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void reload() {
        long now = System.currentTimeMillis();
        if (now - this.lastRefreshMs < 400L) {
            return;
        }
        this.lastRefreshMs = now;
        List<ServerFavorites.Entry> loaded = ServerFavorites.load();
        this.entries.clear();
        this.entries.addAll(loaded);
        if (this.entries.isEmpty()) {
            this.footer = "Избранных серверов пока нет — добавьте адрес сверху";
        }
    }

    /** Проверяет все адреса: опрос идёт в отдельных потоках, интерфейс не подвисает. */
    private void checkAll() {
        this.pending.clear();
        for (ServerFavorites.Entry entry : this.entries) {
            this.pending.add(entry.address);
        }
        this.checking = !this.pending.isEmpty();
        this.footer = this.pending.isEmpty() ? "Добавьте адрес, чтобы проверить состояние" : "Проверяю сервера…";
        this.startNext();
    }

    private void startNext() {
        if (this.pending.isEmpty()) {
            this.checking = false;
            if (!this.entries.isEmpty()) {
                this.footer = "Проверка завершена: серверов " + this.entries.size();
            }
            return;
        }
        final String address = this.pending.remove(0);
        ServerPinger.pingAsync(address).thenAccept(status -> MinecraftClient.getInstance().execute(() -> {
            this.statuses.put(address, status);
            this.startNext();
        }));
    }

    private void check(String address) {
        this.statuses.remove(address);
        ServerPinger.pingAsync(address).thenAccept(status -> MinecraftClient.getInstance().execute(() -> this.statuses.put(address, status)));
        this.footer = "Проверяю " + ServerPinger.prettyAddress(address) + "…";
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float listTop() {
        return ServerCheckScreen.panelY() + 88.0f;
    }

    private float listHeight() {
        return ServerCheckScreen.panelY() + H - 30.0f - this.listTop();
    }

    private int indexAt(float mouseX, float mouseY) {
        float top = this.listTop();
        if (mouseX < ServerCheckScreen.panelX() + PAD || mouseY < top || mouseY > top + this.listHeight()) {
            return -1;
        }
        int index = (int)((mouseY - top + this.scroll) / ROW_H);
        return index >= 0 && index < this.entries.size() ? index : -1;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        this.delta = this.updateDelta();
        float a = this.appear += (1.0f - this.appear) * Math.min(1.0f, this.delta * 9.0f);
        float x = ServerCheckScreen.panelX();
        float y = ServerCheckScreen.panelY();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, ServerCheckScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        Render2D.msdfText(FONT_TITLE, "Проверка серверов", x + PAD, y + 20.0f, 12.0f, ServerCheckScreen.rgba(255, 255, 255, 245.0f * a));
        Render2D.msdfText(FONT_TEXT, "Пинг, игроки и версия каждого сервера · ЛКМ — войти, ПКМ — убрать, Ctrl+ЛКМ — проверить",
                x + PAD, y + 34.0f, 5.8f, ServerCheckScreen.rgba(196, 202, 214, 150.0f * a));
        this.drawClose(x, y, mx, my, a);

        this.address.render(drawContext, x + PAD, y + 48.0f, 280.0f, FIELD_H, a, mx, my, this.delta);
        this.note.render(drawContext, x + PAD + 288.0f, y + 48.0f, 140.0f, FIELD_H, a, mx, my, this.delta);
        boolean addHot = this.hover(mx, my, x + PAD + 436.0f, y + 48.0f, 46.0f, FIELD_H);
        this.button("Добавить", x + PAD + 436.0f, y + 48.0f, 46.0f, addHot, true, a);
        boolean checkAllHot = this.hover(mx, my, x + PAD + 486.0f, y + 48.0f, 80.0f, FIELD_H);
        this.button("Проверить", x + PAD + 486.0f, y + 48.0f, 80.0f, checkAllHot, false, a);

        this.reload();
        String summary = this.checking ? "Идёт проверка…" : "Серверов: " + this.entries.size()
                + (this.statuses.isEmpty() ? "" : " · проверено: " + this.statuses.size());
        Render2D.msdfText(FONT_TEXT, summary, x + PAD, y + 72.0f, 5.8f, ServerCheckScreen.rgba(180, 188, 204, 200.0f * a));

        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.delta * 14.0f);
        this.hovered = this.indexAt(mx, my);
        float left = x + PAD;
        float top = this.listTop();
        Render2D.rect(left, top, W - PAD * 2.0f, this.listHeight(), 8.0f, ServerCheckScreen.rgba(10, 12, 17, 150.0f * a));
        Render2D.pushScissor(drawContext, left, top, W - PAD * 2.0f, this.listHeight());
        if (this.entries.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, "Список пуст. Впишите адрес сверху и нажмите «Добавить» — клиент сразу его проверит.",
                    left + 12.0f, top + 14.0f, 6.2f, ServerCheckScreen.rgba(170, 176, 190, 210.0f * a));
            Render2D.msdfText(FONT_TEXT, "Избранные сервера хранятся рядом с настройками клиента.",
                    left + 12.0f, top + 28.0f, 6.0f, ServerCheckScreen.rgba(150, 156, 170, 180.0f * a));
        }
        for (int i = 0; i < this.entries.size(); ++i) {
            float rowY = top + (float)i * ROW_H - this.scroll;
            if (rowY + ROW_H < top - 4.0f || rowY > top + this.listHeight() + 4.0f) {
                continue;
            }
            this.drawRow(drawContext, this.entries.get(i), i, left, rowY, i == this.hovered, a, mx, my);
        }
        Render2D.popScissor(drawContext);
        Render2D.msdfText(FONT_TEXT, ServerCheckScreen.trim(this.footer, 96), x + PAD, y + H - PAD - 4.0f, 5.8f,
                ClientAccent.accentSoft(220.0f * a));
    }

    private void drawRow(DrawContext drawContext, ServerFavorites.Entry entry, int index, float left, float rowY, boolean hot, float a, float mx, float my) {
        float width = W - PAD * 2.0f - 10.0f;
        float top = rowY + 2.0f;
        float height = ROW_H - 6.0f;
        Render2D.rect(left + 5.0f, top, width, height, 8.0f,
                ServerCheckScreen.rgba(255, 255, 255, (hot ? 22.0f : 9.0f) * a));
        if (hot) {
            Render2D.outline(left + 5.0f, top, width, height, 8.0f, 1.0f, ClientAccent.accentSoft(120.0f * a));
        }
        Render2D.msdfText(FONT_SEMI, entry.display(), left + 14.0f, top + 7.0f, 8.0f, ServerCheckScreen.rgba(240, 244, 252, 240.0f * a));
        ServerPinger.Status status = this.statuses.get(entry.address);
        if (entry.note != null && !entry.note.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, ServerCheckScreen.trim(entry.note, 34), left + 14.0f, top + 20.0f, 5.6f,
                    ServerCheckScreen.rgba(170, 178, 194, 200.0f * a));
        }
        else if (status != null && !status.motd.isEmpty()) {
            Render2D.msdfText(FONT_TEXT, ServerCheckScreen.trim(status.motd, 46), left + 14.0f, top + 20.0f, 5.6f,
                    ServerCheckScreen.rgba(170, 178, 194, 200.0f * a));
        }
        else {
            Render2D.msdfText(FONT_TEXT, status == null ? "ещё не проверялся" : "описание не указано",
                    left + 14.0f, top + 20.0f, 5.6f, ServerCheckScreen.rgba(150, 156, 170, 180.0f * a));
        }
        float right = left + width - 12.0f;
        if (status == null) {
            Render2D.msdfText(FONT_TEXT, "проверка…", right - 60.0f, top + 15.0f, 5.8f, ServerCheckScreen.rgba(180, 188, 204, 200.0f * a));
            return;
        }
        int bars = status.bars();
        for (int i = 0; i < 5; ++i) {
            float barHeight = 4.0f + (float)i * 2.2f;
            float barX = right - 78.0f + (float)i * 5.0f;
            float barY = top + height - 8.0f - barHeight;
            boolean filled = i < bars;
            int color = bars == 0 ? ServerCheckScreen.rgba(150, 80, 80, 200.0f * a)
                    : (bars >= 4 ? ClientAccent.accentSoft(230.0f * a) : ServerCheckScreen.rgba(232, 196, 120, 220.0f * a));
            Render2D.rect(barX, barY, 3.4f, barHeight, 1.4f, filled ? color : ServerCheckScreen.rgba(255, 255, 255, 34.0f * a));
        }
        Render2D.msdfText(FONT_SEMI, status.summary(), right - 62.0f, top + 5.0f, 6.2f,
                status.online ? ServerCheckScreen.rgba(226, 232, 244, 235.0f * a) : ServerCheckScreen.rgba(226, 170, 170, 230.0f * a));
        Render2D.msdfText(FONT_TEXT, ServerCheckScreen.trim(status.online ? status.versionText() : "нет связи", 22),
                right - 62.0f, top + 18.0f, 5.4f, ServerCheckScreen.rgba(168, 176, 192, 210.0f * a));
    }

    private void drawClose(float x, float y, float mx, float my, float a) {
        float size = 18.0f;
        float closeX = x + W - PAD - size;
        float closeY = y + 14.0f;
        boolean hot = this.hover(mx, my, closeX, closeY, size, size);
        Render2D.rect(closeX, closeY, size, size, 6.0f, ServerCheckScreen.rgba(255, 255, 255, (hot ? 26.0f : 10.0f) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : ServerCheckScreen.rgba(210, 214, 224, 190.0f * a);
        Render2D.line(closeX + 5.4f, closeY + 5.4f, closeX + size - 5.4f, closeY + size - 5.4f, 1.4f, ink);
        Render2D.line(closeX + size - 5.4f, closeY + 5.4f, closeX + 5.4f, closeY + size - 5.4f, 1.4f, ink);
    }

    private void button(String label, float x, float y, float width, boolean hot, boolean accent, float a) {
        Render2D.rect(x, y, width, FIELD_H, 6.0f, accent
                ? ClientAccent.accentSoft((hot ? 215.0f : 180.0f) * a)
                : ServerCheckScreen.rgba(255, 255, 255, (hot ? 30.0f : 16.0f) * a));
        float textWidth = Render2D.msdfWidth(FONT_SEMI, label, 5.8f);
        Render2D.msdfText(FONT_SEMI, label, x + (width - textWidth) * 0.5f, y + 5.6f, 5.8f,
                accent ? ServerCheckScreen.rgba(12, 14, 20, 240.0f * a) : ServerCheckScreen.rgba(222, 228, 240, 230.0f * a));
    }

    private boolean hover(float mx, float my, float x, float y, float width, float height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = ServerCheckScreen.panelX();
        float y = ServerCheckScreen.panelY();
        if (this.address.mouseClicked(mx, my, click.button()) || this.note.mouseClicked(mx, my, click.button())) {
            return true;
        }
        if (click.button() == 0 && this.hover(mx, my, x + W - PAD - 18.0f, y + 14.0f, 18.0f, 18.0f)) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (this.hover(mx, my, x + PAD + 436.0f, y + 48.0f, 46.0f, FIELD_H)) {
            String address = this.address.getText() == null ? "" : this.address.getText().trim();
            if (address.isEmpty()) {
                this.footer = "Сначала впишите адрес сервера";
                Sounds.play("settings_close");
                return true;
            }
            if (ServerFavorites.contains(address)) {
                this.footer = "Этот сервер уже в списке";
                Sounds.play("settings_close");
                return true;
            }
            ServerFavorites.add(address, this.note.getText());
            this.lastRefreshMs = 0L;
            this.reload();
            this.footer = "Сервер добавлен: " + ServerPinger.prettyAddress(address);
            Sounds.play("select_category");
            this.check(ServerPinger.cleanAddress(address));
            return true;
        }
        if (this.hover(mx, my, x + PAD + 486.0f, y + 48.0f, 80.0f, FIELD_H)) {
            this.checkAll();
            Sounds.play("select_category");
            return true;
        }
        int index = this.indexAt(mx, my);
        if (index >= 0 && index < this.entries.size()) {
            ServerFavorites.Entry entry = this.entries.get(index);
            if (click.button() == 1) {
                ServerFavorites.remove(entry.address);
                this.statuses.remove(entry.address);
                this.lastRefreshMs = 0L;
                this.reload();
                this.footer = "Убрано из списка: " + entry.display();
                Sounds.play("settings_close");
            }
            else if (ServerCheckScreen.controlDown()) {
                this.check(entry.address);
                Sounds.play("select_category");
            }
            else {
                this.connect(entry.address);
            }
            return true;
        }
        return true;
    }

    /** Зажат ли Ctrl: так проверяем только один сервер, не выходя из окна. */
    private static boolean controlDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        return org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), 341) == 1
                || org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), 345) == 1;
    }

    private void connect(String address) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        ServerInfo info = new ServerInfo(ServerPinger.prettyAddress(address), ServerPinger.cleanAddress(address), ServerInfo.ServerType.OTHER);
        this.footer = "Подключаюсь к " + ServerPinger.prettyAddress(address) + "…";
        Sounds.play("select_category");
        ConnectScreen.connect(this, client, new ServerAddress(ServerPinger.hostOf(address), ServerPinger.portOf(address)), info, false, null);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float maxScroll = Math.max(0.0f, (float)this.entries.size() * ROW_H - this.listHeight());
        this.scrollTarget = Math.max(0.0f, Math.min(maxScroll, this.scrollTarget - (float)verticalAmount * 26.0f));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == 256 || input.key() == 27) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (input.key() == 257) {
            this.checkAll();
            return true;
        }
        return this.address.keyPressed(input) || this.note.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return this.address.charTyped(input) || this.note.charTyped(input);
    }

    @Override
    public void close() {
        this.address.blur();
        this.note.blur();
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
