package rtx.byazen.utils.chat;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Авто-перевод чата (идея №180 из IDEAS.md).
 * <p>
 * Перевод идёт встроенным словарём (около двухсот самых частых слов и фраз из чата и игры), который
 * можно расширять своими парами: клиент не отправляет ваши сообщения в интернет и работает без
 * внешних сервисов. Если слов в словаре мало, перевод не показывается — лучше промолчать, чем выдать
 * бессмыслицу. Для исходящих сообщений есть отдельная команда {@code .tr}.
 */
public final class ChatTranslate {

    public enum Direction {
        AUTO, RU_TO_EN, EN_TO_RU
    }

    /** Пары «русское слово|английское слово». */
    private static final String[] DICTIONARY = {
            "привет|hi", "здравствуйте|hello", "здравствуй|hello", "пока|bye", "спасибо|thanks", "пожалуйста|please",
            "извини|sorry", "извините|sorry", "да|yes", "нет|no", "может|maybe", "конечно|sure", "хорошо|ok",
            "плохо|bad", "нормально|fine", "приветствую|greetings", "доброе|good", "утро|morning", "вечер|evening",
            "ночь|night", "день|day", "друг|friend", "друзья|friends", "игрок|player", "игроки|players", "сервер|server",
            "мир|world", "игра|game", "играть|play", "играем|playing", "где|where", "кто|who", "что|what", "как|how",
            "почему|why", "когда|when", "сколько|how much", "помоги|help", "помогите|help me", "помощь|help",
            "подожди|wait", "иду|coming", "бегу|running", "стою|standing", "здесь|here", "там|there", "тут|here",
            "рядом|near", "далеко|far", "быстро|fast", "медленно|slow", "осторожно|careful", "опасно|dangerous",
            "мобы|mobs", "моб|mob", "зомби|zombie", "скелет|skeleton", "крипер|creeper", "паук|spider", "эндермен|enderman",
            "игрок рядом|player nearby", "убей|kill", "убили|killed", "умер|died", "смерть|death", "жив|alive",
            "здоровье|health", "хп|hp", "голод|hunger", "еда|food", "есть|eat", "пить|drink", "зелье|potion",
            "меч|sword", "броня|armor", "щит|shield", "лук|bow", "стрела|arrow", "блок|block", "блоки|blocks",
            "камень|stone", "дерево|wood", "железо|iron", "золото|gold", "алмаз|diamond", "алмазы|diamonds",
            "незерит|netherite", "руда|ore", "копать|dig", "копаю|digging", "шахта|mine", "пещера|cave", "вода|water",
            "лава|lava", "огонь|fire", "дом|home", "база|base", "спавн|spawn", "портал|portal", "ад|nether",
            "край|end", "координаты|coordinates", "коорд|coords", "дальше|further", "назад|back", "вперёд|forward",
            "вверх|up", "вниз|down", "налево|left", "направо|right", "север|north", "юг|south", "запад|west",
            "восток|east", "торговля|trade", "продам|selling", "куплю|buying", "цена|price", "обмен|trade",
            "деньги|money", "монеты|coins", "вещи|items", "предмет|item", "инвентарь|inventory", "сундук|chest",
            "шалкер|shulker", "элитра|elytra", "лошадь|horse", "лодка|boat", "телега|minecart", "поршень|piston",
            "редстоун|redstone", "механизм|contraption", "зачарование|enchant", "наковальня|anvil", "стол|table",
            "кровать|bed", "спать|sleep", "респавн|respawn", "телепорт|teleport", "тп|tp", "прыгай|jump",
            "сиди|sit", "жди|wait", "иди|go", "беги|run", "стоп|stop", "стой|stop", "хватит|enough", "начали|start",
            "готов|ready", "готовы|ready", "ясно|got it", "понял|understood", "поняла|understood", "не понял|didn't get it",
            "кто здесь|who is here", "есть кто|anyone here", "люди|people", "онлайн|online", "оффлайн|offline",
            "привет всем|hi all", "всем привет|hi everyone", "добрый день|good afternoon", "спокойной ночи|good night",
            "до связи|see you", "увидимся|see you", "пока всем|bye all", "хорошей игры|have fun", "удачи|good luck",
            "молодец|well done", "круто|cool", "класс|great", "ужас|awful", "больно|painful", "чиню|repairing",
            "строю|building", "строительство|building", "ферма|farm", "грядка|farmland", "урожай|harvest",
            "красиво|beautiful", "нравится|like", "не нравится|dislike", "смешно|funny", "тихо|quiet", "громко|loud",
            "фпс|fps", "лаги|lag", "лагает|lagging", "тормозит|lagging", "оптимизация|optimization", "шейдеры|shaders",
            "ресурспак|resourcepack", "мод|mod", "моды|mods", "версия|version", "клиент|client", "чит|cheat",
            "бан|ban", "кик|kick", "мьют|mute", "варн|warn", "админ|admin", "модератор|moderator", "правила|rules",
            "нарушение|violation", "разреши|allow", "запрещено|forbidden", "спс|thx", "пж|pls", "ок|ok", "норм|fine",
            "го|go", "гоу|go", "лол|lol", "ржу|lol", "ахах|haha", "хаха|haha", "ура|yay", "ой|oops", "упс|oops"
    };

    private static final Map<String, String> RU_TO_EN = new LinkedHashMap<String, String>();
    private static final Map<String, String> EN_TO_RU = new LinkedHashMap<String, String>();
    private static final Map<String, String> USER_RU_TO_EN = new LinkedHashMap<String, String>();
    private static boolean loaded;

    private ChatTranslate() {
    }

    public record Result(String text, int translated, int total, Direction direction) {
        public float coverage() {
            return this.total == 0 ? 0.0f : (float)this.translated / (float)this.total;
        }

        public String label() {
            return this.direction == Direction.RU_TO_EN ? "рус → англ" : "англ → рус";
        }
    }

    static {
        for (String pair : DICTIONARY) {
            int index = pair.indexOf(124);
            if (index <= 0) {
                continue;
            }
            String ru = pair.substring(0, index).trim().toLowerCase(Locale.ROOT);
            String en = pair.substring(index + 1).trim().toLowerCase(Locale.ROOT);
            RU_TO_EN.put(ru, en);
            EN_TO_RU.putIfAbsent(en, ru);
        }
    }

    /** Сколько слов знает словарь. */
    public static int size() {
        ChatTranslate.load();
        return RU_TO_EN.size() + USER_RU_TO_EN.size();
    }

    public static String summary() {
        return "словарь: " + RU_TO_EN.size() + " встроенных пар + " + USER_RU_TO_EN.size() + " своих";
    }

    public static void addPair(String ru, String en) {
        ChatTranslate.load();
        String russian = ru.trim().toLowerCase(Locale.ROOT);
        String english = en.trim().toLowerCase(Locale.ROOT);
        if (russian.isEmpty() || english.isEmpty()) {
            return;
        }
        USER_RU_TO_EN.put(russian, english);
        EN_TO_RU.putIfAbsent(english, russian);
        ChatTranslate.save();
    }

    public static List<String> userPairs() {
        ChatTranslate.load();
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : USER_RU_TO_EN.entrySet()) {
            list.add(entry.getKey() + " = " + entry.getValue());
        }
        return list;
    }

    /** Направление по письменности: кириллицы больше — значит русский. */
    public static Direction detect(String text) {
        int cyrillic = 0;
        int latin = 0;
        for (char symbol : text.toCharArray()) {
            if (symbol >= 'а' && symbol <= 'я' || symbol == 'ё') {
                ++cyrillic;
            }
            else if (symbol >= 'a' && symbol <= 'z' || symbol >= 'A' && symbol <= 'Z') {
                ++latin;
            }
        }
        if (cyrillic == 0 && latin == 0) {
            return Direction.AUTO;
        }
        return cyrillic >= latin ? Direction.RU_TO_EN : Direction.EN_TO_RU;
    }

    /** Переводит текст пословно. Возвращает {@code null}, если уверенности мало. */
    public static Result translate(String text, Direction direction, float minCoverage) {
        ChatTranslate.load();
        if (text == null || text.isBlank()) {
            return null;
        }
        Direction actual = direction == Direction.AUTO ? ChatTranslate.detect(text) : direction;
        if (actual == Direction.AUTO) {
            return null;
        }
        Map<String, String> dictionary = actual == Direction.RU_TO_EN ? RU_TO_EN : EN_TO_RU;
        StringBuilder builder = new StringBuilder();
        int words = 0;
        int translated = 0;
        for (String piece : text.split("\\s+")) {
            String clean = piece.replaceAll("^[^\\p{L}]+", "").replaceAll("[^\\p{L}]+$", "");
            if (clean.isEmpty()) {
                builder.append(piece).append(' ');
                continue;
            }
            ++words;
            String lower = clean.toLowerCase(Locale.ROOT);
            String hit = dictionary.get(lower);
            if (hit == null) {
                hit = dictionary.get(lower.replace("ё", "е"));
            }
            if (hit == null) {
                builder.append(piece).append(' ');
                continue;
            }
            ++translated;
            builder.append(piece.replace(clean, hit)).append(' ');
        }
        if (words == 0) {
            return null;
        }
        Result result = new Result(builder.toString().trim(), translated, words, actual);
        return result.coverage() >= minCoverage ? result : null;
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("chat_dictionary");
            if (!root.has("pairs")) {
                return;
            }
            for (String key : root.getAsJsonObject("pairs").keySet()) {
                String value = root.getAsJsonObject("pairs").get(key).getAsString();
                USER_RU_TO_EN.put(key, value);
                EN_TO_RU.putIfAbsent(value, key);
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("словарь перевода: " + throwable.getClass().getSimpleName());
        }
    }

    private static void save() {
        try {
            JsonObject pairs = new JsonObject();
            for (Map.Entry<String, String> entry : USER_RU_TO_EN.entrySet()) {
                pairs.addProperty(entry.getKey(), entry.getValue());
            }
            JsonObject root = new JsonObject();
            root.add("pairs", pairs);
            RepositoryStorage.write("chat_dictionary", root);
        }
        catch (Throwable ignored) {
        }
    }
}
