package rtx.byazen.utils.vote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Голосование за фичи внутри клиента (идея №198 из IDEAS.md).
 * <p>
 * Список пожеланий живёт в клиенте: можно отдать один голос за то, что хочется увидеть следующим,
 * написать своё пожелание и обменяться «кодом голосов» с друзьями — счёт сложится. Текст с итогами
 * копируется одной кнопкой, чтобы отправить его в Discord или в обсуждения проекта.
 */
public final class FeatureVote {

    /** Пожелание с голосами. */
    public record Wish(String id, String title, String note, int votes, boolean mine) {
    }

    private static final FeatureVote INSTANCE = new FeatureVote();
    private static final List<Wish> WISHES = new ArrayList<Wish>();
    private static final Map<String, Integer> TALLY = new LinkedHashMap<String, Integer>();
    private static final List<String> OWN = new ArrayList<String>();
    private static final int MAX_OWN = 20;

    private static String myVote = "";
    private boolean loaded;

    static {
        FeatureVote.add("minimap2", "Мини-карта 2.0", "метки, дороги и общий вид сервера");
        FeatureVote.add("cosmetics_more", "Больше наборов косметики", "сезонные наборы, плащи, шляпы");
        FeatureVote.add("pet_cosmetics", "Косметика для питомца", "окрасы, шапки и аксессуары");
        FeatureVote.add("music_share", "Общий плейлист с друзьями", "слушать одни треки в партии");
        FeatureVote.add("ui_themes", "Свои темы интерфейса", "готовые палитры и импорт из файла");
        FeatureVote.add("hud_cloud", "Синхронизация HUD между ПК", "один HUD на всех компьютерах");
        FeatureVote.add("screenshots_cloud", "Облако скриншотов", "хранить снимки и делиться ссылкой");
        FeatureVote.add("voice_chat", "Голосовой чат с друзьями", "по согласию, через свой сервер");
        FeatureVote.add("quests_daily", "Ежедневные задания", "задания за вход и игру");
        FeatureVote.add("achievements_more", "Ещё достижения", "редкие и скрытые награды");
        FeatureVote.add("mod_docs", "Документация по модулям", "описания и примеры в одном файле");
        FeatureVote.add("mobile_app", "Мобильное приложение ByAzen", "статистика и плеер на телефоне");
    }

    private FeatureVote() {
    }

    private static void add(String id, String title, String note) {
        WISHES.add(new Wish(id, title, note, 0, false));
    }

    public static List<Wish> wishes() {
        FeatureVote.load();
        ArrayList<Wish> list = new ArrayList<Wish>();
        for (Wish wish : WISHES) {
            list.add(new Wish(wish.id(), wish.title(), wish.note(),
                    TALLY.getOrDefault(wish.id(), 0), wish.id().equals(myVote)));
        }
        return list;
    }

    public static String myVote() {
        FeatureVote.load();
        return myVote;
    }

    public static List<String> own() {
        FeatureVote.load();
        return new ArrayList<String>(OWN);
    }

    /** Голос за пожелание: один голос, всегда можно поменять. */
    public static String vote(String id) {
        FeatureVote.load();
        Wish target = null;
        String needle = id == null ? "" : id.trim().toLowerCase(Locale.ROOT);
        for (Wish wish : WISHES) {
            if (wish.id().equalsIgnoreCase(needle) || wish.title().toLowerCase(Locale.ROOT).contains(needle)) {
                target = wish;
                break;
            }
        }
        if (target == null) {
            return "Такого пожелания нет — список в хабе на вкладке «Фичи»";
        }
        if (!myVote.isEmpty()) {
            TALLY.put(myVote, Math.max(0, TALLY.getOrDefault(myVote, 1) - 1));
        }
        myVote = target.id();
        TALLY.merge(myVote, 1, Integer::sum);
        INSTANCE.save();
        NotificationsModule.notify("§bГолос за фичу: §f" + target.title(), 3500L);
        WebBridge.pushEvent("feature_vote", "Голос: " + target.title());
        return "Голос за «" + target.title() + "» учтён · " + FeatureVote.summary();
    }

    /** Своё пожелание: попадёт в список и в код голосов. */
    public static String propose(String text) {
        FeatureVote.load();
        String idea = text == null ? "" : text.trim();
        if (idea.length() < 3) {
            return "Опишите пожелание хотя бы в трёх словах";
        }
        if (idea.length() > 90) {
            idea = idea.substring(0, 90);
        }
        if (OWN.contains(idea)) {
            return "Такое пожелание уже записано";
        }
        OWN.add(0, idea);
        while (OWN.size() > MAX_OWN) {
            OWN.remove(OWN.size() - 1);
        }
        INSTANCE.save();
        ChatMessage.send("§bПожелание записано: §f" + idea + " §7(отправить — кнопка «Текст для отправки»)");
        WebBridge.pushEvent("feature_vote", "Пожелание: " + idea);
        return "Пожелание записано";
    }

    public static List<String> leaderboard() {
        ArrayList<Wish> list = new ArrayList<Wish>(FeatureVote.wishes());
        list.sort((left, right) -> Integer.compare(right.votes(), left.votes()));
        ArrayList<String> rows = new ArrayList<String>();
        int place = 0;
        for (Wish wish : list) {
            ++place;
            rows.add("§7" + place + ". " + (wish.mine() ? "§b" : "§f") + wish.title() + " §8— " + wish.votes()
                    + (wish.mine() ? " §b· ваш голос" : ""));
        }
        for (String idea : OWN) {
            rows.add("§8 своё: §7" + idea);
        }
        return rows;
    }

    public static String summary() {
        FeatureVote.load();
        Wish best = FeatureVote.best();
        String lead = best == null ? "голосов пока нет" : "впереди «" + best.title() + "» (" + best.votes() + ")";
        String own = myVote.isEmpty() ? "ваш голос не отдан" : "ваш голос: " + FeatureVote.nameOf(myVote);
        return own + " · " + lead + " · своих пожеланий: " + OWN.size();
    }

    private static Wish best() {
        Wish best = null;
        for (Wish wish : FeatureVote.wishes()) {
            if (best == null || wish.votes() > best.votes()) {
                best = wish;
            }
        }
        return best;
    }

    private static String nameOf(String id) {
        for (Wish wish : WISHES) {
            if (wish.id().equals(id)) {
                return wish.title();
            }
        }
        return id;
    }

    /** Код голосов для друзей: голос, счёт и пожелания. */
    public static String code() {
        FeatureVote.load();
        JsonObject root = new JsonObject();
        root.addProperty("vote", myVote);
        JsonObject tally = new JsonObject();
        for (Map.Entry<String, Integer> entry : TALLY.entrySet()) {
            tally.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("tally", tally);
        JsonArray ideas = new JsonArray();
        OWN.forEach(ideas::add);
        root.add("own", ideas);
        String raw = root.toString();
        return "BZFEAT1:" + Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Складывает голоса друзей и их пожелания. */
    public static String applyCode(String code) {
        FeatureVote.load();
        String text = code == null ? "" : code.trim();
        int index = text.indexOf("BZFEAT1:");
        if (index < 0) {
            return "Это не код голосов за фичи (нужен BZFEAT1:…)";
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(text.substring(index + 8).trim()),
                    StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
            int merged = 0;
            if (root.has("tally")) {
                for (String key : root.getAsJsonObject("tally").keySet()) {
                    int value = root.getAsJsonObject("tally").get(key).getAsInt();
                    if (value > 0) {
                        TALLY.merge(key, value, Integer::sum);
                        ++merged;
                    }
                }
            }
            int added = 0;
            if (root.has("own")) {
                for (var element : root.getAsJsonArray("own")) {
                    String idea = element.getAsString();
                    if (!OWN.contains(idea) && OWN.size() < MAX_OWN) {
                        OWN.add(idea);
                        ++added;
                    }
                }
            }
            INSTANCE.save();
            return "Голоса друзей приняты: позиций " + merged + ", новых пожеланий " + added;
        }
        catch (Throwable throwable) {
            return "Код не читается — проверьте, что он скопирован целиком";
        }
    }

    /** Текст для отправки в Discord или обсуждения проекта. */
    public static String sendText() {
        FeatureVote.load();
        StringBuilder builder = new StringBuilder("Голосование за фичи ByAzen\n");
        ArrayList<Wish> list = new ArrayList<Wish>(FeatureVote.wishes());
        list.sort((left, right) -> Integer.compare(right.votes(), left.votes()));
        for (Wish wish : list) {
            builder.append(wish.votes()).append(" — ").append(wish.title())
                    .append(" (").append(wish.note()).append(")\n");
        }
        for (String idea : OWN) {
            builder.append("своё пожелание: ").append(idea).append('\n');
        }
        return builder.toString();
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("feature_votes");
            if (root.has("vote")) {
                myVote = root.get("vote").getAsString();
            }
            if (root.has("tally")) {
                for (String key : root.getAsJsonObject("tally").keySet()) {
                    TALLY.put(key, root.getAsJsonObject("tally").get(key).getAsInt());
                }
            }
            if (root.has("own")) {
                for (var element : root.getAsJsonArray("own")) {
                    OWN.add(element.getAsString());
                }
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("голосование за фичи: не удалось прочитать файл");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("vote", myVote);
            JsonObject tally = new JsonObject();
            for (Map.Entry<String, Integer> entry : TALLY.entrySet()) {
                tally.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("tally", tally);
            JsonArray ideas = new JsonArray();
            OWN.forEach(ideas::add);
            root.add("own", ideas);
            RepositoryStorage.write("feature_votes", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("голосование за фичи: не удалось сохранить файл");
        }
    }
}

