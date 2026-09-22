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
import rtx.byazen.api.modules.impl.Utils.CacheLimitsModule;
import rtx.byazen.api.modules.impl.Utils.ConfigJournalModule;
import rtx.byazen.api.modules.impl.Utils.ModDetectModule;
import rtx.byazen.api.modules.impl.Utils.PrivateDataModule;
import rtx.byazen.api.modules.impl.Utils.RenderProfilerModule;
import rtx.byazen.api.modules.impl.Utils.StatsExportModule;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.compat.ModEnv;
import rtx.byazen.utils.config.ChangeLog;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.perf.ModuleProfiler;
import rtx.byazen.utils.render.cache.CacheManager;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.security.PrivateData;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.startup.LazyTasks;
import rtx.byazen.utils.stats.StatsExport;

/**
 * Окно «Диагностика» (идеи №159, №161, №163, №164, №165, №166 из IDEAS.md).
 * <p>
 * Одно окно на все вопросы «что происходит с клиентом»: дневник изменений настроек, нагрузка модулей,
 * кэши, отложенные задачи запуска, окружение из соседних модов, хранилище личных данных и выгрузка
 * статистики. Каждая вкладка — список строк с подписями и значением, снизу кнопки действий.
 */
public final class DiagnosticsScreen
extends BaseScreen {

    public static final int TAB_JOURNAL = 0;
    public static final int TAB_PROFILE = 1;
    public static final int TAB_CACHE = 2;
    public static final int TAB_START = 3;
    public static final int TAB_MODS = 4;
    public static final int TAB_VAULT = 5;
    public static final int TAB_STATS = 6;

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final String[] TAB_LABELS = {"Журнал", "Профиль", "Кэш", "Запуск", "Моды", "Хранилище", "Статистика"};
    private static final String[] TAB_NOTES = {
            "Дневник настроек: что и когда менялось",
            "Нагрузка модулей по времени на вызов",
            "Кэш картинок, обложек и анимаций",
            "Отложенная подготовка интеграций",
            "Соседние моды: шейдеры, оптимизаторы",
            "Личные данные под ключом шифрования",
            "Статистика сессии: игра, бои, музыка",
    };
    private static final String[][] TAB_BUTTONS = {
            {"Скопировать", "Очистить", "Обновить"},
            {"Сбросить окно", "Список в чат", "Сбросить счётчики"},
            {"Очистить всё", "Пределы по умолчанию", "Сводка"},
            {"Выполнить отложенное", "Сводка"},
            {"Список в чат", "Приглушить визуалы", "Пересканировать"},
            {"Закрыть под ключом", "Открыть хранилище", "Проверить"},
            {"Выгрузить файлы", "Скопировать markdown"},
    };

    private static final float W = 640.0f;
    private static final float H = 392.0f;
    private static final float PAD = 14.0f;
    private static final float RAIL = 150.0f;
    private static final float ROW_H = 26.0f;
    private static final int VISIBLE = 8;

    /** Строка содержимого: подпись, значение, пояснение. */
    private static final class Line {

        final String label;
        final String value;
        final String note;
        final int color;

        Line(String label, String value, String note, int color) {
            this.label = label;
            this.value = value;
            this.note = note;
            this.color = color;
        }
    }

    private int tab;
    private int scroll;
    private float animation;
    private long lastFrameNanos;
    private String status = "";
    private long statusUntil;
    private List<Line> cache = new ArrayList<Line>();
    private int cacheTab = -1;
    private long cacheAt;

    public DiagnosticsScreen(int tab) {
        super(Text.literal("Диагностика ByAzen"));
        this.tab = Math.max(0, Math.min(TAB_LABELS.length - 1, tab));
    }

    /** Открывает окно на нужной вкладке. */
    public static void open(int tab) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new DiagnosticsScreen(tab));
        }
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float railX() {
        return DiagnosticsScreen.panelX() + 8.0f;
    }

    private static float railY() {
        return DiagnosticsScreen.panelY() + 46.0f;
    }

    private static float contentX() {
        return DiagnosticsScreen.panelX() + RAIL + 12.0f;
    }

    private static float contentY() {
        return DiagnosticsScreen.panelY() + 46.0f;
    }

    private static float contentW() {
        return W - RAIL - 34.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.animation += (1.0f - this.animation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.animation));
        float x = DiagnosticsScreen.panelX();
        float y = DiagnosticsScreen.panelY();
        List<Line> lines = this.lines();

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Диагностика", x + PAD, y + PAD, 11.0f, DiagnosticsScreen.ink(a));
        String note = TAB_NOTES[this.tab];
        Render2D.msdfText(FONT, note, x + PAD + Render2D.msdfWidth(FONT_BOLD, "Диагностика", 11.0f) + 8.0f, y + PAD + 3.0f, 7.2f,
                DiagnosticsScreen.sub(a * 0.95f));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, DiagnosticsScreen.sub(a * 0.9f));

        this.renderRail(a, mouseX, mouseY);
        Render2D.rect(DiagnosticsScreen.contentX() - 6.0f, DiagnosticsScreen.contentY() - 6.0f, contentW() + 12.0f,
                H - 46.0f - 30.0f, 10.0f, DiagnosticsScreen.field(a * 0.45f));

        if (lines.isEmpty()) {
            Render2D.msdfText(FONT, this.emptyText(), DiagnosticsScreen.contentX(), DiagnosticsScreen.contentY() + 6.0f, 7.4f,
                    DiagnosticsScreen.sub(a * 0.9f));
        }
        else {
            Render2D.pushScissor(drawContext, DiagnosticsScreen.contentX(), DiagnosticsScreen.contentY(), contentW(),
                    (float)VISIBLE * ROW_H + 4.0f);
            float rowY = DiagnosticsScreen.contentY() - (float)this.scroll;
            for (int i = 0; i < lines.size(); ++i) {
                DiagnosticsScreen.Line line = lines.get(i);
                boolean hovered = (float)mouseX >= DiagnosticsScreen.contentX() && (float)mouseX <= DiagnosticsScreen.contentX() + contentW()
                        && (float)mouseY >= rowY && (float)mouseY <= rowY + ROW_H - 3.0f;
                Render2D.rect(DiagnosticsScreen.contentX(), rowY, contentW(), ROW_H - 3.0f, 6.0f,
                        hovered ? DiagnosticsScreen.hover(a) : DiagnosticsScreen.field(a * 0.8f));
                Render2D.circle(DiagnosticsScreen.contentX() + 9.0f, rowY + 11.0f, 2.8f,
                        line.color == 0 ? ClientAccent.accent(a) : line.color);
                Render2D.msdfText(FONT_BOLD, DiagnosticsScreen.trim(line.label, 34), DiagnosticsScreen.contentX() + 18.0f, rowY + 4.0f, 7.6f,
                        DiagnosticsScreen.ink(a));
                if (line.value != null && !line.value.isEmpty()) {
                    Render2D.msdfText(FONT_BOLD, line.value, DiagnosticsScreen.contentX() + contentW() - 10.0f
                            - Render2D.msdfWidth(FONT_BOLD, line.value, 7.6f), rowY + 4.0f, 7.6f, ClientAccent.accentBright(a));
                }
                if (line.note != null && !line.note.isEmpty()) {
                    Render2D.msdfText(FONT, DiagnosticsScreen.trim(line.note, 62), DiagnosticsScreen.contentX() + 18.0f, rowY + 15.0f, 6.2f,
                            DiagnosticsScreen.sub(a * 0.85f));
                }
                rowY += ROW_H;
            }
            Render2D.popScissor(drawContext);
        }

        this.renderButtons(a, mouseX, mouseY);
        if (System.currentTimeMillis() < this.statusUntil && !this.status.isEmpty()) {
            Render2D.msdfText(FONT, this.status, x + PAD, y + H - PAD - 6.0f, 6.8f, ClientAccent.accent(a));
        }
        else {
            Render2D.msdfText(FONT, "Строк: " + lines.size() + " · колесо мыши листает список", x + PAD, y + H - PAD - 6.0f, 6.8f,
                    DiagnosticsScreen.sub(a * 0.75f));
        }
    }

    private void renderRail(float a, int mouseX, int mouseY) {
        for (int i = 0; i < TAB_LABELS.length; ++i) {
            float ty = DiagnosticsScreen.railY() + (float)i * 30.0f;
            boolean active = i == this.tab;
            boolean hovered = (float)mouseX >= railX() && (float)mouseX <= railX() + RAIL - 8.0f
                    && (float)mouseY >= ty && (float)mouseY <= ty + 26.0f;
            Render2D.rect(railX(), ty, RAIL - 8.0f, 26.0f, 7.0f,
                    active ? DiagnosticsScreen.selected(a) : hovered ? DiagnosticsScreen.hover(a) : DiagnosticsScreen.field(a * 0.7f));
            if (active) {
                Render2D.rect(railX(), ty + 5.0f, 2.6f, 16.0f, 1.3f, ClientAccent.accent(a));
            }
            Render2D.msdfText(FONT_BOLD, TAB_LABELS[i], DiagnosticsScreen.railX() + 12.0f, ty + 8.0f, 7.8f,
                    active ? DiagnosticsScreen.ink(a) : DiagnosticsScreen.sub(a * 0.95f));
            Render2D.msdfText(FONT, this.railBadge(i), DiagnosticsScreen.railX() + RAIL - 42.0f, ty + 9.0f, 6.4f,
                    DiagnosticsScreen.sub(a * 0.7f));
        }
    }

    private String railBadge(int index) {
        switch (index) {
            case TAB_JOURNAL: {
                return String.valueOf(ChangeLog.count());
            }
            case TAB_PROFILE: {
                return String.valueOf(ModuleProfiler.measured());
            }
            case TAB_CACHE: {
                return String.valueOf(CacheManager.totalEntries());
            }
            case TAB_START: {
                return String.valueOf(LazyTasks.pending());
            }
            case TAB_MODS: {
                return String.valueOf(ModEnv.presentRows().size());
            }
            case TAB_VAULT: {
                return PrivateData.vaultExists() ? "закрыто" : String.valueOf(PrivateData.stores().size());
            }
            default: {
                return ClientLog.count(ClientLog.Level.WARN) + "!";
            }
        }
    }

    private void renderButtons(float a, int mouseX, int mouseY) {
        float by = DiagnosticsScreen.panelY() + H - 34.0f;
        float bx = DiagnosticsScreen.contentX();
        for (int i = 0; i < TAB_BUTTONS[this.tab].length; ++i) {
            String label = TAB_BUTTONS[this.tab][i];
            float w = Math.max(96.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 20.0f);
            boolean hovered = (float)mouseX >= bx && (float)mouseX <= bx + w && (float)mouseY >= by && (float)mouseY <= by + 22.0f;
            Render2D.rect(bx, by, w, 22.0f, 6.0f, hovered ? DiagnosticsScreen.hover(a) : DiagnosticsScreen.field(a));
            Render2D.msdfText(FONT_BOLD, label, bx + 10.0f, by + 7.0f, 7.0f, DiagnosticsScreen.ink(a));
            bx += w + 8.0f;
        }
    }

    private boolean buttonClicked(float mouseX, float mouseY) {
        float by = DiagnosticsScreen.panelY() + H - 34.0f;
        float bx = DiagnosticsScreen.contentX();
        for (int i = 0; i < TAB_BUTTONS[this.tab].length; ++i) {
            String label = TAB_BUTTONS[this.tab][i];
            float w = Math.max(96.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 20.0f);
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= by && mouseY <= by + 22.0f) {
                this.action(i);
                return true;
            }
            bx += w + 8.0f;
        }
        return false;
    }

    private void action(int index) {
        SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
        switch (this.tab) {
            case TAB_JOURNAL: {
                if (index == 0) {
                    ConfigJournalModule.copy();
                    this.say("Журнал скопирован в буфер");
                }
                else if (index == 1) {
                    ConfigJournalModule.clear();
                    this.say("Журнал очищен");
                }
                else {
                    this.say("Обновлено: записей " + ChangeLog.count());
                }
                break;
            }
            case TAB_PROFILE: {
                if (index == 0) {
                    ModuleProfiler.reset();
                    this.say("Окно замера сброшено");
                }
                else if (index == 1) {
                    RenderProfilerModule.report();
                    this.say("Список выведен в чат");
                }
                else {
                    ModuleProfiler.setEnabled(false);
                    ModuleProfiler.reset();
                    ModuleProfiler.setEnabled(true);
                    this.say("Счётчики профилировщика обнулены");
                }
                break;
            }
            case TAB_CACHE: {
                if (index == 0) {
                    CacheManager.clearAll();
                    this.say("Кэши очищены: " + CacheManager.summary());
                }
                else if (index == 1) {
                    CacheLimitsModule.resetLimits();
                    this.say("Пределы по умолчанию: " + CacheManager.summary());
                }
                else {
                    this.say(CacheManager.summary());
                }
                break;
            }
            case TAB_START: {
                if (index == 0) {
                    long[] result = LazyTasks.runAll();
                    this.say("Выполнено задач: " + result[0] + " за " + result[1] + " мс");
                }
                else {
                    this.say(LazyTasks.report());
                }
                break;
            }
            case TAB_MODS: {
                if (index == 0) {
                    ChatMessage.send("Окружение: " + ModEnv.summary());
                    this.say("Отправлено в чат");
                }
                else if (index == 1) {
                    this.say(ModDetectModule.calmHeavy());
                }
                else {
                    ModEnv.rescan();
                    this.say("Пересканировано: " + ModEnv.summary());
                }
                break;
            }
            case TAB_VAULT: {
                if (index == 0) {
                    PrivateDataModule.lock();
                    this.say(PrivateData.vaultExists() ? "Данные закрыты: " + PrivateData.verify() : "Закрыть не удалось");
                }
                else if (index == 1) {
                    PrivateDataModule.unlock();
                    this.say("Хранилище открыто, файлы возвращены");
                }
                else {
                    this.say(PrivateData.verify());
                }
                break;
            }
            default: {
                if (index == 0) {
                    StatsExportModule.writeFiles();
                    this.say("Файлы статистики выгружены в exports/");
                }
                else {
                    StatsExportModule.copyReport();
                    this.say("Отчёт скопирован в буфер");
                }
                break;
            }
        }
        this.cacheTab = -1;
    }

    private void say(String text) {
        this.status = text;
        this.statusUntil = System.currentTimeMillis() + 4000L;
    }

    private String emptyText() {
        switch (this.tab) {
            case TAB_JOURNAL: {
                return "Пока пусто: журнал начнёт наполняться, как только вы что-то поменяете";
            }
            case TAB_PROFILE: {
                return "Включите модуль «Render Profiler», чтобы увидеть нагрузку модулей";
            }
            case TAB_CACHE: {
                return "Кэши пусты — картинки подгрузятся по мере надобности";
            }
            case TAB_START: {
                return "Отложенных задач нет: всё подготовлено";
            }
            case TAB_MODS: {
                return "Рядом с игрой только ванильный Minecraft";
            }
            case TAB_VAULT: {
                return "Открытых файлов нет — либо всё уже закрыто ключом";
            }
            default: {
                return "Данных пока нет: поиграйте и выгрузите статистику";
            }
        }
    }

    /** Список строк текущей вкладки; данные собираются не чаще раза в секунду. */
    private List<Line> lines() {
        long now = System.currentTimeMillis();
        if (this.cacheTab == this.tab && now - this.cacheAt < 1000L) {
            return this.cache;
        }
        this.cacheTab = this.tab;
        this.cacheAt = now;
        this.cache = new ArrayList<Line>();
        switch (this.tab) {
            case TAB_JOURNAL: {
                for (ChangeLog.Entry entry : ChangeLog.entries()) {
                    this.cache.add(new DiagnosticsScreen.Line(entry.timeText() + "  " + entry.module(), "",
                            entry.setting() + " = " + ChangeLog.shortValue(entry.value()), 0));
                    if (this.cache.size() >= ConfigJournalModule.depth()) {
                        break;
                    }
                }
                break;
            }
            case TAB_PROFILE: {
                this.cache.add(new DiagnosticsScreen.Line("Всего модулями", String.format(java.util.Locale.ROOT, "%.1f мс", ModuleProfiler.windowMillis()),
                        "окно " + ModuleProfiler.windowAgeMs() / 1000L + " с · модулей в замере: " + ModuleProfiler.measured(), 0));
                for (ModuleProfiler.Row row : ModuleProfiler.snapshot()) {
                    this.cache.add(new DiagnosticsScreen.Line(row.name, row.microsText(),
                            row.shareText() + " работы · вызовов " + row.callsText(), 0));
                    if (this.cache.size() >= 40) {
                        break;
                    }
                }
                break;
            }
            case TAB_CACHE: {
                for (CacheManager.Row row : CacheManager.rows()) {
                    this.cache.add(new DiagnosticsScreen.Line(row.label, row.sizeText(), row.bytesText(), 0));
                }
                break;
            }
            case TAB_START: {
                this.cache.add(new DiagnosticsScreen.Line("Итог", LazyTasks.pending() + " ждут", LazyTasks.report(), 0));
                for (LazyTasks.Task task : LazyTasks.tasks()) {
                    this.cache.add(new DiagnosticsScreen.Line(task.name, task.done() ? "готово" : "ждёт",
                            task.done() ? task.tookMs() + " мс" : "выполнится при входе в мир", 0));
                }
                break;
            }
            case TAB_MODS: {
                for (ModEnv.Row row : ModEnv.presentRows()) {
                    this.cache.add(new DiagnosticsScreen.Line(row.label, row.version.isEmpty() ? "найден" : row.version, row.note, 0));
                }
                this.cache.add(new DiagnosticsScreen.Line("Наборы шейдеров", String.valueOf(ModEnv.shaderPackCount()),
                        ModEnv.shaderPackCount() > 0 ? "шейдеры дорогие — следите за тяжёлыми визуалами" : "наборов не найдено", 0));
                break;
            }
            case TAB_VAULT: {
                this.cache.add(new DiagnosticsScreen.Line("Хранилище", PrivateData.vaultExists() ? "закрыто" : "нет",
                        PrivateData.verify(), 0));
                for (PrivateData.Store store : PrivateData.stores()) {
                    this.cache.add(new DiagnosticsScreen.Line(store.label, store.sizeText(), store.path.getFileName().toString(), 0));
                }
                break;
            }
            default: {
                for (String line : StatsExport.build().markdown.split("\n")) {
                    String text = line.trim();
                    if (text.isEmpty()) {
                        continue;
                    }
                    this.cache.add(new DiagnosticsScreen.Line(text, "", "", 0));
                    if (this.cache.size() >= 40) {
                        break;
                    }
                }
                break;
            }
        }
        return this.cache;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = DiagnosticsScreen.panelX();
        float y = DiagnosticsScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            this.close();
            return true;
        }
        for (int i = 0; i < TAB_LABELS.length; ++i) {
            float ty = DiagnosticsScreen.railY() + (float)i * 30.0f;
            if (mouseX >= railX() && mouseX <= railX() + RAIL - 8.0f && mouseY >= ty && mouseY <= ty + 26.0f) {
                this.tab = i;
                this.scroll = 0;
                this.cacheTab = -1;
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.5f, 1.1f);
                return true;
            }
        }
        if (this.buttonClicked(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int lines = this.lines().size();
        int max = Math.max(0, (int)((float)lines * ROW_H - (float)VISIBLE * ROW_H));
        this.scroll = Math.max(0, Math.min(max, this.scroll - (int)(verticalAmount * 24.0)));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (key == GLFW.GLFW_KEY_TAB) {
            this.tab = (this.tab + 1) % TAB_LABELS.length;
            this.scroll = 0;
            this.cacheTab = -1;
            return true;
        }
        return super.keyPressed(input);
    }

    private void close() {
        SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
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

    /** Обрезает длинную строку по числу символов, чтобы не выезжала из панели. */
    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return DiagnosticsScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return DiagnosticsScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return DiagnosticsScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return DiagnosticsScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int selected(float alpha) {
        return DiagnosticsScreen.rgba(255, 255, 255, 34.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
