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
import rtx.byazen.utils.cosmetics.Cosmetic;
import rtx.byazen.utils.cosmetics.CosmeticRegistry;
import rtx.byazen.utils.chat.ChatHistory;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Голосование за следующую косметику (идея №186 из IDEAS.md).
 * <p>
 * Внутри клиента есть список кандидатов (по одному предмету из каждого набора и несколько базовых),
 * за них можно голосовать. Голос хранится локально, а кнопка «Код голоса» собирает его в короткую
 * строку: обменявшись кодами в чате или Discord, группа суммирует голоса — так видно, какой предмет
 * стоит доделать следующим. Плюс можно предложить свою идею, чтобы не потерять её.
 */
public final class CosmeticVote {

    /** Кандидат: что предлагаем и сколько голосов. */
    public record Candidate(String id, String name, String flavor, int votes, boolean mine) {
    }

    private static final CosmeticVote INSTANCE = new CosmeticVote();
    private static final List<Cosmetic> CANDIDATES = new ArrayList<Cosmetic>();
    private static final Map<String, Integer> VOTES = new LinkedHashMap<String, Integer>();
    private static final List<String> PROPOSALS = new ArrayList<String>();
    private static final int MAX_PROPOSALS = 20;
    private static String myVote = "";
    private boolean loaded;

    private CosmeticVote() {
    }

    private static void ensureCandidates() {
        if (!CANDIDATES.isEmpty()) {
            return;
        }
        for (CosmeticRegistry.Set set : CosmeticRegistry.sets()) {
            if (!set.items.isEmpty()) {
                CANDIDATES.add(set.items.get(0));
            }
        }
        int added = 0;
        for (Cosmetic cosmetic : CosmeticRegistry.base()) {
            if (added >= 4) {
                break;
            }
            CANDIDATES.add(cosmetic);
            ++added;
        }
    }

    public static List<Candidate> candidates() {
        CosmeticVote.ensureCandidates();
        CosmeticVote.load();
        ArrayList<Candidate> list = new ArrayList<Candidate>();
        for (Cosmetic cosmetic : CANDIDATES) {
            list.add(new Candidate(cosmetic.id, cosmetic.name, cosmetic.flavor,
                    VOTES.getOrDefault(cosmetic.id, 0), cosmetic.id.equals(myVote)));
        }
        return list;
    }

    public static String myVote() {
        CosmeticVote.load();
        return myVote;
    }

    /** Голос за предмет: у вас один голос, он всегда можно переменить. */
    public static String vote(String id) {
        CosmeticVote.ensureCandidates();
        CosmeticVote.load();
        String target = id == null ? "" : id.trim().toLowerCase(Locale.ROOT);
        if (target.indexOf(':') < 0) {
            for (Cosmetic cosmetic : CANDIDATES) {
                if (cosmetic.id.equalsIgnoreCase(target) || cosmetic.name.equalsIgnoreCase(target)) {
                    target = cosmetic.id;
                    break;
                }
            }
        }
        Cosmetic cosmetic = CosmeticRegistry.byId(target);
        if (cosmetic == null) {
            for (Cosmetic candidate : CANDIDATES) {
                if (candidate.id.equals(target)) {
                    cosmetic = candidate;
                    break;
                }
            }
        }
        if (cosmetic == null) {
            return "Такого кандидата нет — список в хабе, вкладка «Голосование»";
        }
        if (!myVote.isEmpty()) {
            VOTES.put(myVote, Math.max(0, VOTES.getOrDefault(myVote, 1) - 1));
        }
        myVote = cosmetic.id;
        VOTES.merge(myVote, 1, Integer::sum);
        INSTANCE.save();
        NotificationsModule.notify("§bГолос: §f" + cosmetic.name, 3000L);
        WebBridge.pushEvent("vote", "Голос: " + cosmetic.name);
        return "Голос за «" + cosmetic.name + "» учтён · " + CosmeticVote.summary();
    }

    /** Итоги: кто впереди. */
    public static List<String> leaderboard() {
        CosmeticVote.ensureCandidates();
        CosmeticVote.load();
        ArrayList<Candidate> list = new ArrayList<Candidate>(CosmeticVote.candidates());
        list.sort((left, right) -> Integer.compare(right.votes(), left.votes()));
        ArrayList<String> rows = new ArrayList<String>();
        int place = 0;
        for (Candidate candidate : list) {
            ++place;
            rows.add("§7" + place + ". " + (candidate.mine() ? "§b" : "§f") + candidate.name()
                    + " §8(" + candidate.flavor() + ") §7— " + candidate.votes()
                    + (candidate.mine() ? " §b· ваш голос" : ""));
        }
        return rows;
    }

    /** Предложить свою идею косметики: её видно в списке и в коде голоса. */
    public static String propose(String text) {
        CosmeticVote.load();
        String idea = text == null ? "" : text.trim();
        if (idea.length() < 3) {
            return "Опишите идею хотя бы в трёх словах";
        }
        if (idea.length() > 80) {
            idea = idea.substring(0, 80);
        }
        if (PROPOSALS.contains(idea)) {
            return "Такая идея уже записана";
        }
        PROPOSALS.add(0, idea);
        while (PROPOSALS.size() > MAX_PROPOSALS) {
            PROPOSALS.remove(PROPOSALS.size() - 1);
        }
        INSTANCE.save();
        ChatMessage.send("§bИдея записана: §f" + idea + " §7(её можно отправить в коде голоса)");
        WebBridge.pushEvent("vote", "Идея: " + idea);
        return "Идея записана";
    }

    public static List<String> proposals() {
        CosmeticVote.load();
        return new ArrayList<String>(PROPOSALS);
    }

    public static String summary() {
        CosmeticVote.load();
        Candidate best = null;
        for (Candidate candidate : CosmeticVote.candidates()) {
            if (best == null || candidate.votes() > best.votes()) {
                best = candidate;
            }
        }
        String lead = best == null || best.votes() == 0 ? "голосов пока нет" : "впереди «" + best.name() + "» (" + best.votes() + ")";
        return (myVote.isEmpty() ? "ваш голос не отдан" : "ваш голос: " + CosmeticVote.nameOf(myVote)) + " · " + lead + " · идей: " + PROPOSALS.size();
    }

    private static String nameOf(String id) {
        Cosmetic cosmetic = CosmeticRegistry.byId(id);
        return cosmetic == null ? id : cosmetic.name;
    }

    /** Код голоса: ваш голос, итоги и идеи — можно отправить друзьям. */
    public static String code() {
        CosmeticVote.load();
        JsonObject root = new JsonObject();
        root.addProperty("vote", myVote);
        JsonObject tally = new JsonObject();
        for (Map.Entry<String, Integer> entry : VOTES.entrySet()) {
            tally.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("tally", tally);
        JsonArray ideas = new JsonArray();
        PROPOSALS.forEach(ideas::add);
        root.add("ideas", ideas);
        String raw = root.toString();
        return "BZVOTE1:" + Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Принимает код друга: складывает голоса и идеи (счёт группы). */
    public static String applyCode(String code) {
        CosmeticVote.load();
        String text = code == null ? "" : code.trim();
        int index = text.indexOf("BZVOTE1:");
        if (index < 0) {
            return "Это не код голосования (нужен BZVOTE1:…)";
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(text.substring(index + 8).trim()), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
            int merged = 0;
            if (root.has("tally")) {
                for (String key : root.getAsJsonObject("tally").keySet()) {
                    int value = root.getAsJsonObject("tally").get(key).getAsInt();
                    if (value > 0) {
                        VOTES.merge(key, value, Integer::sum);
                        ++merged;
                    }
                }
            }
            int ideas = 0;
            if (root.has("ideas")) {
                for (var element : root.getAsJsonArray("ideas")) {
                    String idea = element.getAsString();
                    if (!PROPOSALS.contains(idea) && PROPOSALS.size() < MAX_PROPOSALS) {
                        PROPOSALS.add(idea);
                        ++ideas;
                    }
                }
            }
            INSTANCE.save();
            return "Голоса друзей приняты: позиций " + merged + ", новых идей " + ideas + " · " + CosmeticVote.summary();
        }
        catch (Throwable throwable) {
            return "Код не читается — проверьте, что он скопирован целиком";
        }
    }

    /** Рекомендация для хаба: что предлагаем доделать следующим. */
    public static String advice() {
        Candidate best = null;
        for (Candidate candidate : CosmeticVote.candidates()) {
            if (best == null || candidate.votes() > best.votes()) {
                best = candidate;
            }
        }
        if (best == null || best.votes() == 0) {
            return "Проголосуйте — и следующий набор соберём по вашему выбору";
        }
        return "По голосам следующий в работе: «" + best.name() + "» из набора " + best.flavor();
    }

    /** Часто ли в чате говорят про косметику: подсказка, что тема живая. */
    public static int chatMentions() {
        int mentions = 0;
        for (String line : ChatHistory.entries()) {
            String lower = line == null ? "" : line.toLowerCase(Locale.ROOT);
            if (lower.contains("космет") || lower.contains("скин") || lower.contains("шапк") || lower.contains("cape")
                    || lower.contains("плащ")) {
                ++mentions;
            }
        }
        return mentions;
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("cosmetic_vote");
            if (root.has("vote")) {
                myVote = root.get("vote").getAsString();
            }
            if (root.has("tally")) {
                for (String key : root.getAsJsonObject("tally").keySet()) {
                    VOTES.put(key, root.getAsJsonObject("tally").get(key).getAsInt());
                }
            }
            if (root.has("ideas")) {
                for (var element : root.getAsJsonArray("ideas")) {
                    PROPOSALS.add(element.getAsString());
                }
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("голосование: не удалось прочитать файл");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("vote", myVote);
            JsonObject tally = new JsonObject();
            for (Map.Entry<String, Integer> entry : VOTES.entrySet()) {
                tally.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("tally", tally);
            JsonArray ideas = new JsonArray();
            PROPOSALS.forEach(ideas::add);
            root.add("ideas", ideas);
            RepositoryStorage.write("cosmetic_vote", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("голосование: не удалось сохранить файл");
        }
    }
}
