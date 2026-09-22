package rtx.byazen.utils.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.sounds.SoundManager;

/**
 * Правила клиента — свой маленький язык (идея №177 из IDEAS.md).
 * <p>
 * Полноценные Lua/JS в клиент не встраиваются: сторонний интерпретатор нельзя безопасно тащить в
 * игру, поэтому здесь собственный мини-язык правил без доступа к файлам, сети и рефлексии.
 * Правило описывается словами:
 * <pre>
 *   rule "Мало здоровья"
 *   when health &lt; 8
 *   and in_combat
 *   do notify "Мало HP — съешь яблоко"
 *   do sound
 *   cooldown 20
 *   end
 * </pre>
 * Условия читаются из игры (HP, голод, FPS, высота, бой, огонь, ночь, дождь, режим), действия —
 * только из белого списка: уведомление, звук, сообщение в чат, включить/выключить модуль, музыка.
 * Проверка правил идёт раз в полсекунды, у каждого правила свой откат по времени.
 */
public final class ScriptEngine {

    private static final ScriptEngine INSTANCE = new ScriptEngine();
    public static final int MAX_RULES = 40;
    public static final int MAX_LINE = 200;

    private final List<ScriptEngine.Rule> rules = new ArrayList<ScriptEngine.Rule>();
    private boolean subscribed;
    private int ticks;

    private ScriptEngine() {
    }

    public static ScriptEngine get() {
        return INSTANCE;
    }

    /** Одно условие правила. */
    public static final class Condition {
        public final String key;
        public final String operator;
        public final double value;

        Condition(String key, String operator, double value) {
            this.key = key;
            this.operator = operator;
            this.value = value;
        }
    }

    /** Одно действие правила. */
    public static final class Action {
        public final String type;
        public final String argument;

        Action(String type, String argument) {
            this.type = type;
            this.argument = argument;
        }
    }

    /** Готовое правило. */
    public static final class Rule {
        public final String name;
        public final List<Condition> conditions = new ArrayList<Condition>();
        public final List<Action> actions = new ArrayList<Action>();
        public boolean enabled = true;
        public long cooldownMs = 15000L;
        public long lastRun;

        public Rule(String name) {
            this.name = name;
        }

        public String describe() {
            StringBuilder builder = new StringBuilder();
            for (Condition condition : this.conditions) {
                if (builder.length() > 0) {
                    builder.append(" и ");
                }
                builder.append(ScriptEngine.conditionText(condition));
            }
            return builder.toString();
        }
    }

    /** Итог разбора: правило или понятная ошибка с номером строки. */
    public record ParseResult(Rule rule, String error, int line) {
        public boolean ok() {
            return this.rule != null;
        }
    }

    public static ParseResult parse(String text) {
        if (text == null || text.isBlank()) {
            return new ParseResult(null, "пустой текст правила", 0);
        }
        String name = null;
        Rule rule = null;
        String[] lines = text.split("\r?\n");
        for (int i = 0; i < lines.length; ++i) {
            String raw = lines[i];
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            if (line.length() > MAX_LINE) {
                return new ParseResult(null, "строка " + (i + 1) + " длиннее " + MAX_LINE + " символов", i + 1);
            }
            String lower = line.toLowerCase(Locale.ROOT);
            if (lower.startsWith("rule ")) {
                name = ScriptEngine.unquote(line.substring(5).trim());
                if (name.isEmpty()) {
                    name = "правило " + (i + 1);
                }
                rule = new Rule(name);
                continue;
            }
            if (rule == null) {
                return new ParseResult(null, "строка " + (i + 1) + ": правило должно начинаться со слова rule", i + 1);
            }
            if (lower.equals("end") || lower.equals("конец")) {
                if (rule.conditions.isEmpty() && rule.actions.isEmpty()) {
                    return new ParseResult(null, "правило «" + rule.name + "» пустое", i + 1);
                }
                return new ParseResult(rule, null, 0);
            }
            if (lower.startsWith("cooldown ") || lower.startsWith("откат ")) {
                String value = line.substring(line.indexOf(32) + 1).trim();
                try {
                    rule.cooldownMs = Math.max(1000L, Math.min(600000L, (long)(Double.parseDouble(value) * 1000.0)));
                }
                catch (Throwable throwable) {
                    return new ParseResult(null, "строка " + (i + 1) + ": откат должен быть числом секунд", i + 1);
                }
                continue;
            }
            if (lower.startsWith("when ") || lower.startsWith("if ") || lower.startsWith("and ") || lower.startsWith("если ")) {
                String body = line.substring(line.indexOf(32) + 1).trim();
                for (String piece : body.split("\\s+(?:and|и)\\s+")) {
                    Condition condition = ScriptEngine.parseCondition(piece.trim());
                    if (condition == null) {
                        return new ParseResult(null, "строка " + (i + 1) + ": не понял условие «" + piece.trim() + "»", i + 1);
                    }
                    rule.conditions.add(condition);
                }
                continue;
            }
            if (lower.startsWith("do ") || lower.startsWith("сделать ")) {
                String body = line.substring(line.indexOf(32) + 1).trim();
                Action action = ScriptEngine.parseAction(body);
                if (action == null) {
                    return new ParseResult(null, "строка " + (i + 1) + ": недопустимое действие «" + body + "»", i + 1);
                }
                rule.actions.add(action);
                continue;
            }
            return new ParseResult(null, "строка " + (i + 1) + ": не знаю команду «" + line + "»", i + 1);
        }
        if (rule == null) {
            return new ParseResult(null, "в тексте нет ни одного правила", 0);
        }
        return new ParseResult(rule, null, 0);
    }

    private static Condition parseCondition(String piece) {
        if (piece.isEmpty()) {
            return null;
        }
        for (String operator : new String[]{"<=", ">=", "==", "!=", "<", ">", "="}) {
            int index = piece.indexOf(operator);
            if (index <= 0) {
                continue;
            }
            String key = piece.substring(0, index).trim().toLowerCase(Locale.ROOT);
            String rawValue = piece.substring(index + operator.length()).trim();
            String op = operator.equals("=") ? "==" : operator;
            if (!ScriptEngine.isKnownKey(key)) {
                return null;
            }
            if (ScriptEngine.isBooleanKey(key)) {
                return new Condition(key, op, ScriptEngine.truth(rawValue) ? 1.0 : 0.0);
            }
            try {
                return new Condition(key, op, Double.parseDouble(rawValue.replace(",", ".")));
            }
            catch (Throwable throwable) {
                return null;
            }
        }
        String key = piece.toLowerCase(Locale.ROOT);
        if (ScriptEngine.isBooleanKey(key)) {
            return new Condition(key, "==", 1.0);
        }
        return null;
    }

    private static Action parseAction(String body) {
        String lower = body.toLowerCase(Locale.ROOT);
        if (lower.equals("sound") || lower.equals("звук")) {
            return new Action("sound", "");
        }
        if (lower.equals("music_next") || lower.equals("следующий_трек")) {
            return new Action("music_next", "");
        }
        if (lower.equals("music_stop") || lower.equals("стоп_музыка")) {
            return new Action("music_stop", "");
        }
        for (String verb : new String[]{"notify", "chat", "log", "сообщить", "чат", "запись"}) {
            if (!lower.startsWith(verb + " ")) {
                continue;
            }
            String argument = ScriptEngine.unquote(body.substring(verb.length()).trim());
            String type = switch (verb) {
                case "сообщить" -> "notify";
                case "чат" -> "chat";
                case "запись" -> "log";
                default -> verb;
            };
            return new Action(type, argument);
        }
        for (String verb : new String[]{"enable", "disable", "toggle", "включить", "выключить", "переключить"}) {
            if (!lower.startsWith(verb + " ")) {
                continue;
            }
            String argument = ScriptEngine.unquote(body.substring(verb.length()).trim());
            String type = switch (verb) {
                case "включить" -> "enable";
                case "выключить" -> "disable";
                case "переключить" -> "toggle";
                default -> verb;
            };
            return new Action(type, argument);
        }
        return null;
    }

    private static boolean isKnownKey(String key) {
        return switch (key) {
            case "health", "hp", "здоровье", "hunger", "food", "голод", "fps", "кадры", "y", "высота",
                 "armor", "броня", "in_combat", "в_бою", "on_fire", "в_огне", "night", "ночь", "rain", "дождь",
                 "sneaking", "присел", "hurt" -> true;
            default -> false;
        };
    }

    private static boolean isBooleanKey(String key) {
        return switch (key) {
            case "in_combat", "в_бою", "on_fire", "в_огне", "night", "ночь", "rain", "дождь", "sneaking", "присел", "hurt" -> true;
            default -> false;
        };
    }

    private static boolean truth(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        return text.equals("true") || text.equals("да") || text.equals("1") || text.equals("включено");
    }

    private static String unquote(String text) {
        String value = text.trim();
        if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String conditionText(Condition condition) {
        return condition.key + " " + condition.operator + " " + (condition.value == Math.rint(condition.value)
                ? String.valueOf((long)condition.value) : String.format(Locale.ROOT, "%.1f", condition.value));
    }

    // ------------------------------------------------------------------ работа правил

    public List<Rule> rules() {
        return this.rules;
    }

    public void clear() {
        this.rules.clear();
    }

    public boolean add(Rule rule) {
        if (rule == null || this.rules.size() >= MAX_RULES) {
            return false;
        }
        for (Rule existing : this.rules) {
            if (existing.name.equalsIgnoreCase(rule.name)) {
                existing.enabled = rule.enabled;
                existing.cooldownMs = rule.cooldownMs;
                existing.conditions.clear();
                existing.conditions.addAll(rule.conditions);
                existing.actions.clear();
                existing.actions.addAll(rule.actions);
                return true;
            }
        }
        this.rules.add(rule);
        return true;
    }

    public Rule find(String name) {
        for (Rule rule : this.rules) {
            if (rule.name.equalsIgnoreCase(name)) {
                return rule;
            }
        }
        return null;
    }

    public boolean remove(String name) {
        Rule rule = this.find(name);
        return rule != null && this.rules.remove(rule);
    }

    public void ensureSubscribed() {
        if (this.subscribed) {
            return;
        }
        this.subscribed = true;
        try {
            EventBus.get().subscribe(this);
        }
        catch (Throwable ignored) {
        }
    }

    /** Проверка правил раз в полсекунды: чаще смысла нет, а нагрузку это создаёт. */
    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || ++this.ticks % 10 != 0) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        long now = System.currentTimeMillis();
        for (Rule rule : new ArrayList<Rule>(this.rules)) {
            if (!rule.enabled || now - rule.lastRun < rule.cooldownMs) {
                continue;
            }
            if (!ScriptEngine.matches(rule, client)) {
                continue;
            }
            rule.lastRun = now;
            ScriptEngine.execute(rule, client);
        }
    }

    private static boolean matches(Rule rule, MinecraftClient client) {
        for (Condition condition : rule.conditions) {
            double actual = ScriptEngine.value(condition.key, client);
            boolean ok = switch (condition.operator) {
                case "<" -> actual < condition.value;
                case "<=" -> actual <= condition.value;
                case ">" -> actual > condition.value;
                case ">=" -> actual >= condition.value;
                case "!=" -> actual != condition.value;
                default -> Math.abs(actual - condition.value) < 0.0001;
            };
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    /** Голод читаем осторожно: имя метода отличается между версиями, поэтому через отражение. */
    private static double hunger(MinecraftClient client) {
        try {
            Object manager = client.player.getClass().getMethod("getHungerManager").invoke(client.player);
            Object level = manager.getClass().getMethod("getFoodLevel").invoke(manager);
            return ((Number)level).doubleValue();
        }
        catch (Throwable throwable) {
            return 20.0;
        }
    }

    private static double value(String key, MinecraftClient client) {
        return switch (key) {
            case "health", "hp", "здоровье" -> client.player.getHealth();
            case "hunger", "food", "голод" -> ScriptEngine.hunger(client);
            case "fps", "кадры" -> client.getCurrentFps();
            case "y", "высота" -> client.player.getY();
            case "armor", "броня" -> client.player.getArmor();
            case "in_combat", "в_бою" -> System.currentTimeMillis() - CombatClock.lastHurt < 5000L ? 1.0 : 0.0;
            case "hurt" -> System.currentTimeMillis() - CombatClock.lastHurt < 1000L ? 1.0 : 0.0;
            case "on_fire", "в_огне" -> client.player.isOnFire() ? 1.0 : 0.0;
            case "night", "ночь" -> client.world.getTimeOfDay() % 24000L > 13000L ? 1.0 : 0.0;
            case "rain", "дождь" -> client.world.isRaining() ? 1.0 : 0.0;
            case "sneaking", "присел" -> client.player.isSneaking() ? 1.0 : 0.0;
            default -> 0.0;
        };
    }

    private static void execute(Rule rule, MinecraftClient client) {
        for (Action action : rule.actions) {
            try {
                switch (action.type) {
                    case "notify": {
                        NotificationsModule.notify(rule.name + ": " + action.argument, 3000L);
                        break;
                    }
                    case "chat": {
                        ChatMessage.send("§b[" + rule.name + "] §f" + action.argument);
                        break;
                    }
                    case "log": {
                        rtx.byazen.utils.logs.ClientLog.info("правило «" + rule.name + "»: " + action.argument);
                        break;
                    }
                    case "sound": {
                        SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.7f, 1.0f);
                        break;
                    }
                    case "music_next": {
                        rtx.byazen.api.music.MusicEngine.get().next();
                        break;
                    }
                    case "music_stop": {
                        rtx.byazen.api.music.MusicEngine.get().stop();
                        break;
                    }
                    case "enable":
                    case "disable":
                    case "toggle": {
                        Module module = ModuleManager.get().findByName(action.argument);
                        if (module == null) {
                            break;
                        }
                        boolean target = switch (action.type) {
                            case "enable" -> true;
                            case "disable" -> false;
                            default -> !module.isEnabled();
                        };
                        if (module.isEnabled() != target) {
                            module.toggle();
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            catch (Throwable throwable) {
                rtx.byazen.utils.logs.ClientLog.error("правило «" + rule.name + "»: действие не выполнено — " + throwable.getClass().getSimpleName());
            }
        }
        rtx.byazen.utils.web.WebBridge.pushEvent("rule", "Сработало правило: " + rule.name);
    }

    /** Отметка «меня ранили» — нужна условиям боя. */
    public static final class CombatClock {
        static volatile long lastHurt;

        private CombatClock() {
        }

        public static void hurt() {
            CombatClock.lastHurt = System.currentTimeMillis();
        }

        public static long sinceHurt() {
            return System.currentTimeMillis() - CombatClock.lastHurt;
        }
    }
}
