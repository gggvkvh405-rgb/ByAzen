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
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.achievements.Achievements;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.help.ModuleHelp;
import rtx.byazen.utils.missions.CoopMissions;
import rtx.byazen.utils.vote.FeatureVote;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.season.SeasonEvents;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.vote.CosmeticVote;

/**
 * Хаб ByAzen: миссии, достижения, события и голосование (идеи №183–186 из IDEAS.md).
 * <p>
 * Одно окно вместо четырёх: слева вкладки, справа строки, снизу — кнопки текущей вкладки.
 * Всё считается локально, секретов здесь нет: прогресс миссий, профиль достижений, сезонные
 * события и голоса за косметику хранятся в конфиге клиента.
 */
public final class ByAzenHubScreen
extends BaseScreen {

    private static final String FONT = "montserrat-medium";
    private static final String FONT_BOLD = "montserrat-bold";
    private static final float W = 600.0f;
    private static final float H = 340.0f;
    private static final float PAD = 14.0f;
    private static final float RAIL = 132.0f;
    private static final float ROW_H = 20.0f;
    private static final int VISIBLE = 10;
    private static final String[] TABS = {"Миссии", "Достижения", "События", "Голосование", "Профиль",
            "Фичи", "Гайд"};
    private static final String[] NOTES = {
            "Кооп-миссии для игры с друзьями",
            "Что вы уже сделали в клиенте",
            "Сезон идёт: палитра и награда",
            "Какая косметика будет следующей",
            "Ваш профиль и статистика",
            "За какие фичи голосуют игроки",
            "Документация и «как это работает»"
    };
    private static final String[][] BUTTONS = {
            {"В чат", "Код для друзей", "Принять код", "Сбросить"},
            {"В чат", "Скопировать профиль", "Сбросить"},
            {"Забрать награду", "Включить палитру", "В чат"},
            {"Голосовать", "Код голоса", "Принять код", "Идея из буфера"},
            {"Скопировать профиль", "В чат"},
            {"Голосовать", "Код голосов", "Принять код", "Пожелание из буфера"},
            {"Открыть тур", "Инструкция строки", "Все модули в чат"}
    };

    private static boolean openedOnce;
    private int tab;
    private int scroll;
    private int selected;
    private float animation;
    private String status = "";
    private long statusUntil;
    private List<String> rows = new ArrayList<String>();
    private long refreshedAt;
    private int refreshTab = -1;

    public ByAzenHubScreen(int tab) {
        super(Text.literal("Хаб ByAzen"));
        this.tab = Math.max(0, Math.min(TABS.length - 1, tab));
    }

    /** Открывает хаб на нужной вкладке. */
    public static void open(int tab) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ByAzenHubScreen(tab));
        }
    }

    /** Открывался ли хаб за эту сессию: достижение «Всё по полочкам». */
    public static boolean opened() {
        return openedOnce;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float contentX() {
        return ByAzenHubScreen.panelX() + RAIL + 10.0f;
    }

    private static float contentY() {
        return ByAzenHubScreen.panelY() + 58.0f;
    }

    private static float contentW() {
        return W - RAIL - 24.0f;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        openedOnce = true;
        float a = Math.min(1.0f, this.animation += (1.0f - this.animation) * 0.25f);
        float x = ByAzenHubScreen.panelX();
        float y = ByAzenHubScreen.panelY();
        if (this.refreshTab != this.tab || System.currentTimeMillis() - this.refreshedAt > 1500L) {
            this.refreshTab = this.tab;
            this.refreshedAt = System.currentTimeMillis();
            this.rows = this.buildRows();
        }

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Хаб ByAzen", x + PAD, y + 12.0f, 10.0f, ByAzenHubScreen.ink(a));
        Render2D.msdfText(FONT, NOTES[this.tab], x + PAD + 108.0f, y + 15.0f, 6.8f, ByAzenHubScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, ByAzenHubScreen.sub(a));

        for (int i = 0; i < TABS.length; ++i) {
            float ty = y + 38.0f + (float)i * 30.0f;
            boolean active = i == this.tab;
            Render2D.rect(x + PAD - 4.0f, ty, RAIL, 26.0f, 7.0f,
                    active ? ClientAccent.accentFill(34.0f * a) : ByAzenHubScreen.field(a * 0.6f));
            if (active) {
                Render2D.rect(x + PAD - 4.0f, ty + 5.0f, 2.6f, 16.0f, 1.3f, ClientAccent.accent(a));
            }
            Render2D.msdfText(active ? FONT_BOLD : FONT, TABS[i], x + PAD + 6.0f, ty + 9.0f, 7.4f,
                    active ? ByAzenHubScreen.ink(a) : ByAzenHubScreen.sub(a));
            Render2D.msdfText(FONT, ByAzenHubScreen.trim(this.badge(i), 20), x + PAD + 6.0f, ty + 17.0f, 5.8f,
                    ByAzenHubScreen.sub(a * 0.8f));
        }

        float top = ByAzenHubScreen.contentY();
        Render2D.rect(ByAzenHubScreen.contentX() - 6.0f, top - 6.0f, ByAzenHubScreen.contentW() + 12.0f,
                ROW_H * (float)VISIBLE + 10.0f, 8.0f, ByAzenHubScreen.field(a * 0.5f));
        for (int i = 0; i < VISIBLE; ++i) {
            int index = this.scroll + i;
            if (index >= this.rows.size()) {
                break;
            }
            float rowY = top + (float)i * ROW_H;
            boolean selectedRow = this.tab == 3 && index == this.selected;
            if (selectedRow) {
                Render2D.rect(ByAzenHubScreen.contentX() - 4.0f, rowY - 2.0f, ByAzenHubScreen.contentW() + 8.0f, ROW_H - 2.0f,
                        5.0f, ClientAccent.accentFill(30.0f * a));
            }
            Render2D.msdfText(FONT, ByAzenHubScreen.trim(this.rows.get(index), 108), ByAzenHubScreen.contentX(),
                    rowY + 4.0f, 7.0f, selectedRow ? ByAzenHubScreen.ink(a) : ByAzenHubScreen.sub(a));
        }
        if (this.rows.size() > VISIBLE) {
            Render2D.msdfText(FONT, "▲", ByAzenHubScreen.contentX() + ByAzenHubScreen.contentW() - 12.0f, top + 4.0f, 7.0f,
                    this.scroll > 0 ? ByAzenHubScreen.ink(a) : ByAzenHubScreen.sub(a * 0.4f));
            Render2D.msdfText(FONT, "▼", ByAzenHubScreen.contentX() + ByAzenHubScreen.contentW() - 12.0f,
                    top + ROW_H * (float)VISIBLE - 14.0f, 7.0f,
                    this.scroll + VISIBLE < this.rows.size() ? ByAzenHubScreen.ink(a) : ByAzenHubScreen.sub(a * 0.4f));
        }

        String[] buttons = BUTTONS[this.tab];
        float by = y + H - 30.0f;
        float bx = x + PAD;
        for (String label : buttons) {
            float w = Math.max(80.0f, Render2D.msdfWidth(FONT_BOLD, label, 7.0f) + 18.0f);
            boolean hovered = (float)mouseX >= bx && (float)mouseX <= bx + w && (float)mouseY >= by && (float)mouseY <= by + 20.0f;
            Render2D.rect(bx, by, w, 20.0f, 6.0f, hovered ? ByAzenHubScreen.hover(a) : ByAzenHubScreen.field(a));
            Render2D.msdfText(FONT_BOLD, ByAzenHubScreen.trim(label, 22), bx + 9.0f, by + 6.0f, 7.0f, ByAzenHubScreen.ink(a));
            bx += w + 6.0f;
        }
        if (System.currentTimeMillis() < this.statusUntil) {
            Render2D.msdfText(FONT, ByAzenHubScreen.trim(this.status, 96), x + PAD, y + H - 42.0f, 6.6f, ClientAccent.accent(a));
        }
        else {
            Render2D.msdfText(FONT, ByAzenHubScreen.trim(this.hint(), 96), x + PAD, y + H - 42.0f, 6.6f, ByAzenHubScreen.sub(a));
        }
    }

    private String badge(int tab) {
        switch (tab) {
            case 0: {
                return CoopMissions.doneCount() + " из " + CoopMissions.list().size();
            }
            case 1: {
                return "уровень " + Achievements.level();
            }
            case 2: {
                SeasonEvents.Event event = SeasonEvents.current();
                return event == null ? "сезон не идёт" : event.title();
            }
            case 3: {
                return CosmeticVote.myVote().isEmpty() ? "голос не отдан" : "голос отдан";
            }
            case 4: {
                return Achievements.points() + " очков";
            }
            case 5: {
                return FeatureVote.myVote().isEmpty() ? "голос не отдан" : "ваш голос учтён";
            }
            default: {
                return ModuleManager.get() == null ? "гайд" : ModuleManager.get().getAll().size() + " модулей";
            }
        }
    }

    private String hint() {
        switch (this.tab) {
            case 0: {
                return "Миссии считаются в игре; кодом можно сложить прогресс с друзьями";
            }
            case 1: {
                return "Достижения открываются сами — за игру и использование функций";
            }
            case 2: {
                return "Награда сезона забирается один раз: весь набор косметики";
            }
            case 3: {
                return "Выберите строку и нажмите «Голосовать» — голос можно переменить";
            }
            case 4: {
                return "Профиль можно скопировать и отправить друзьям";
            }
            case 5: {
                return "Голос один и его можно поменять; свои пожелания уходят в код голосов";
            }
            default: {
                return "Выберите модуль и нажмите «Инструкция строки» — как это работает";
            }
        }
    }

    private List<String> buildRows() {
        switch (this.tab) {
            case 0: {
                return CoopMissions.rows();
            }
            case 1: {
                return Achievements.rows();
            }
            case 2: {
                return SeasonEvents.rows();
            }
            case 3: {
                ArrayList<String> list = new ArrayList<String>(CosmeticVote.leaderboard());
                list.addAll(CosmeticVote.proposals().isEmpty() ? List.of("§8Идей пока нет — добавьте свою")
                        : CosmeticVote.proposals());
                return list;
            }
            case 4: {
                return this.profileRows();
            }
            case 5: {
                return FeatureVote.leaderboard();
            }
            default: {
                return this.guideRows();
            }
        }
    }

    private List<String> profileRows() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("§b" + Achievements.profile());
        list.add("§7" + CoopMissions.summary());
        list.add("§7" + SeasonEvents.summary());
        list.add("§7" + CosmeticVote.summary());
        list.add("§7" + rtx.byazen.utils.profiles.BuildTier.summary());
        list.add("§7" + FeatureVote.summary());
        list.add("§8Профиль хранится только у вас — наружу ничего не уходит");
        return list;
    }

    /** Документация: каждый модуль одной строкой, что он делает и как называется для чата. */
    private List<String> guideRows() {
        ArrayList<String> list = new ArrayList<String>();
        if (ModuleManager.get() == null) {
            return list;
        }
        for (Module module : ModuleManager.get().getAll()) {
            String description = module.getDescription() == null ? "" : module.getDescription();
            if (description.length() > 78) {
                description = description.substring(0, 77) + "…";
            }
            String bind = module.getBind() != null && module.getBind().isBound()
                    ? " §8[" + module.getBind().getDisplayName() + "]" : "";
            list.add("§b" + module.getDisplayName() + bind + " §7— " + description
                    + " §8· " + module.getCategory().getDisplayName());
        }
        list.add("§8Команды: .find <слово> — где найти, .how <модуль> — как это работает");
        return list;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = ByAzenHubScreen.panelX();
        float y = ByAzenHubScreen.panelY();
        if (mouseX >= x + W - PAD - 14.0f && mouseX <= x + W - PAD && mouseY >= y + 8.0f && mouseY <= y + 26.0f) {
            this.close();
            return true;
        }
        for (int i = 0; i < TABS.length; ++i) {
            float ty = y + 38.0f + (float)i * 30.0f;
            if (mouseX >= x + PAD - 4.0f && mouseX <= x + PAD - 4.0f + RAIL && mouseY >= ty && mouseY <= ty + 26.0f) {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.5f, 1.1f);
                this.tab = i;
                this.scroll = 0;
                this.selected = 0;
                return true;
            }
        }
        float top = ByAzenHubScreen.contentY();
        for (int i = 0; i < VISIBLE; ++i) {
            int index = this.scroll + i;
            if (index >= this.rows.size()) {
                break;
            }
            float rowY = top + (float)i * ROW_H;
            if (mouseX >= ByAzenHubScreen.contentX() - 4.0f && mouseX <= ByAzenHubScreen.contentX() + ByAzenHubScreen.contentW()
                    && mouseY >= rowY - 2.0f && mouseY <= rowY + ROW_H - 4.0f) {
                if (this.tab == 3) {
                    this.selected = index;
                    SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.4f, 1.3f);
                }
                return true;
            }
        }
        if (this.rows.size() > VISIBLE) {
            if (mouseX >= ByAzenHubScreen.contentX() + ByAzenHubScreen.contentW() - 18.0f) {
                if (mouseY <= top + ROW_H * (float)VISIBLE * 0.5f && this.scroll > 0) {
                    --this.scroll;
                    return true;
                }
                if (mouseY > top + ROW_H * (float)VISIBLE * 0.5f && this.scroll + VISIBLE < this.rows.size()) {
                    ++this.scroll;
                    return true;
                }
            }
        }
        String[] buttons = BUTTONS[this.tab];
        float by = y + H - 30.0f;
        float bx = x + PAD;
        for (int i = 0; i < buttons.length; ++i) {
            float w = Math.max(80.0f, Render2D.msdfWidth(FONT_BOLD, buttons[i], 7.0f) + 18.0f);
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
        switch (this.tab) {
            case 0: {
                if (index == 0) {
                    for (String line : CoopMissions.rows()) {
                        ChatMessage.send("§7" + line);
                    }
                    this.say("Список миссий — в чате");
                }
                else if (index == 1) {
                    this.copy(CoopMissions.code(), "Код для друзей скопирован");
                }
                else if (index == 2) {
                    String code = this.paste();
                    this.say(CoopMissions.applyCode(code));
                }
                else {
                    CoopMissions.reset();
                    this.say("Прогресс миссий сброшен");
                }
                break;
            }
            case 1: {
                if (index == 0) {
                    for (String line : Achievements.rows()) {
                        ChatMessage.send("§7" + line);
                    }
                    this.say("Достижения — в чате");
                }
                else if (index == 1) {
                    this.copy(Achievements.shareText(), "Профиль достижений скопирован");
                }
                else {
                    Achievements.reset();
                    this.say("Достижения сброшены (можно заработать заново)");
                }
                break;
            }
            case 2: {
                if (index == 0) {
                    this.say(SeasonEvents.claim());
                }
                else if (index == 1) {
                    this.say(SeasonEvents.apply(null));
                }
                else {
                    for (String line : SeasonEvents.rows()) {
                        ChatMessage.send("§7" + line);
                    }
                    this.say("События — в чате");
                }
                break;
            }
            case 3: {
                if (index == 0) {
                    List<CosmeticVote.Candidate> candidates = CosmeticVote.candidates();
                    if (this.selected < 0 || this.selected >= candidates.size()) {
                        this.say("Выберите строку кандидата");
                        break;
                    }
                    this.say(CosmeticVote.vote(candidates.get(this.selected).id()));
                }
                else if (index == 1) {
                    this.copy(CosmeticVote.code(), "Код голоса скопирован");
                }
                else if (index == 2) {
                    this.say(CosmeticVote.applyCode(this.paste()));
                }
                else {
                    String idea = this.paste();
                    if (idea == null || idea.isBlank()) {
                        this.say("Скопируйте свою идею в буфер и нажмите ещё раз");
                        break;
                    }
                    CosmeticVote.propose(idea);
                    this.say("Идея записана");
                }
                break;
            }
            case 5: {
                if (index == 0) {
                    java.util.List<FeatureVote.Wish> wishes = FeatureVote.wishes();
                    if (this.selected < 0 || this.selected >= wishes.size()) {
                        this.say("Выберите строку пожелания");
                        break;
                    }
                    this.say(FeatureVote.vote(wishes.get(this.selected).id()));
                }
                else if (index == 1) {
                    this.copy(FeatureVote.code(), "Код голосов скопирован");
                }
                else if (index == 2) {
                    this.say(FeatureVote.applyCode(this.paste()));
                }
                else {
                    String idea = this.paste();
                    if (idea == null || idea.isBlank()) {
                        this.say("Скопируйте пожелание в буфер и нажмите ещё раз");
                        break;
                    }
                    this.say(FeatureVote.propose(idea));
                }
                break;
            }
            case 6: {
                if (index == 0) {
                    TutorialScreen.open();
                }
                else if (index == 1) {
                    Module module = this.selectedModule();
                    if (module == null) {
                        this.say("Выберите модуль в списке слева");
                        break;
                    }
                    for (String line : ModuleHelp.full(module)) {
                        ChatMessage.send(line);
                    }
                    this.say("Инструкция по «" + module.getDisplayName() + "» — в чате");
                }
                else {
                    for (String line : ModuleHelp.allModules()) {
                        ChatMessage.send(line);
                    }
                    this.say("Список модулей — в чате");
                }
                break;
            }
            default: {
                if (index == 0) {
                    StringBuilder builder = new StringBuilder("Профиль ByAzen\n");
                    for (String line : this.profileRows()) {
                        builder.append(line.replaceAll("§.", "")).append('\n');
                    }
                    this.copy(builder.toString(), "Профиль скопирован");
                }
                else {
                    for (String line : this.profileRows()) {
                        ChatMessage.send(line.startsWith("§") ? line : "§7" + line);
                    }
                    this.say("Профиль — в чате");
                }
            }
        }
    }

    /** Модуль по выбранной строке гайда. */
    private Module selectedModule() {
        if (ModuleManager.get() == null) {
            return null;
        }
        List<Module> modules = ModuleManager.get().getAll();
        if (this.selected < 0 || this.selected >= modules.size()) {
            return null;
        }
        return modules.get(this.selected);
    }

    private String paste() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client == null ? "" : client.keyboard.getClipboard();
    }

    private void copy(String text, String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && text != null) {
            client.keyboard.setClipboard(text);
        }
        this.say(message);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_DOWN) {
            this.scroll = Math.min(Math.max(0, this.rows.size() - VISIBLE), this.scroll + 1);
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_UP) {
            this.scroll = Math.max(0, this.scroll - 1);
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
        this.statusUntil = System.currentTimeMillis() + 4000L;
        if (this.statusUntil > 0L) {
            this.refreshedAt = 0L;
        }
    }

    private static String trim(String text, int max) {
        String value = text == null ? "" : text;
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static int ink(float alpha) {
        return ByAzenHubScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return ByAzenHubScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int field(float alpha) {
        return ByAzenHubScreen.rgba(255, 255, 255, 14.0f * alpha);
    }

    private static int hover(float alpha) {
        return ByAzenHubScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | r << 16 | g << 8 | b;
    }
}
