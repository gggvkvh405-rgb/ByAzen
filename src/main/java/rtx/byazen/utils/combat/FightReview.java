package rtx.byazen.utils.combat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.DeathScreenEvent;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.player.TotemPopEvent;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.scripts.ScriptEngine;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Разбор боя после смерти (идея №179 из IDEAS.md).
 * <p>
 * Клиент держит в памяти последние полторы минуты боя: как падало здоровье, чем и от чего, сколько
 * ударов нанесли вы, были ли тотемы. После смерти из этого собирается понятный разбор: главная
 * причина, самая большая потеря HP, состояние брони и эффектов и что сделать иначе в следующий раз.
 */
public final class FightReview {

    private static final FightReview INSTANCE = new FightReview();
    private static final int HIT_LIMIT = 200;
    private static final int SAMPLE_LIMIT = 240;
    private static final int REVIEW_LIMIT = 8;
    private static final long COMBAT_WINDOW = 90000L;

    private final List<FightReview.Hit> hits = new ArrayList<FightReview.Hit>();
    private final List<float[]> health = new ArrayList<float[]>();
    private final List<String> totems = new ArrayList<String>();
    private final List<FightReview.Report> reviews = new ArrayList<FightReview.Report>();
    private boolean subscribed;
    private boolean loaded;
    private float lastHealth = -1.0f;
    private long lastDamage;
    private long lastHeal;

    private FightReview() {
    }

    public static FightReview get() {
        return INSTANCE;
    }

    /** Одно событие боя: урон или удар. */
    public record Hit(long time, double amount, String source, boolean outgoing, String target) {
    }

    /** Готовый разбор смерти. */
    public static final class Report {
        public final long time;
        public final String world;
        public final String coords;
        public final double totalDamage;
        public final double biggestHit;
        public final String biggestSource;
        public final int ourHits;
        public final int totemsUsed;
        public final long battleMs;
        public final List<String> facts = new ArrayList<String>();
        public final List<String> advice = new ArrayList<String>();

        Report(long time, String world, String coords, double totalDamage, double biggestHit, String biggestSource,
                int ourHits, int totemsUsed, long battleMs) {
            this.time = time;
            this.world = world;
            this.coords = coords;
            this.totalDamage = totalDamage;
            this.biggestHit = biggestHit;
            this.biggestSource = biggestSource;
            this.ourHits = ourHits;
            this.totemsUsed = totemsUsed;
            this.battleMs = battleMs;
        }

        public String verdict() {
            if (this.biggestHit >= 20.0) {
                return "погибли от одного очень сильного удара";
            }
            if (this.totalDamage >= 40.0 && this.battleMs < 8000L) {
                return "бой вышел слишком быстрым: не хватило времени среагировать";
            }
            if (this.totemsUsed > 0) {
                return "тотем спасал, но не хватило запаса";
            }
            if (this.ourHits <= 2) {
                return "почти не отвечали ударом на удар";
            }
            return "бой был долгим — здоровья не хватило к концу";
        }

        public String text() {
            StringBuilder builder = new StringBuilder();
            builder.append("Разбор боя ByAzen\n");
            builder.append("Мир: ").append(this.world).append(", координаты: ").append(this.coords).append('\n');
            builder.append(String.format(Locale.ROOT, "Урона получено: %.1f · самый сильный удар: %.1f (%s)%n",
                    this.totalDamage, this.biggestHit, this.biggestSource));
            builder.append("Ваших ударов: ").append(this.ourHits).append(" · тотемов: ").append(this.totemsUsed)
                    .append(String.format(Locale.ROOT, " · длительность боя: %.1f с%n", this.battleMs / 1000.0));
            builder.append("Итог: ").append(this.verdict()).append('\n');
            for (String fact : this.facts) {
                builder.append("* ").append(fact).append('\n');
            }
            builder.append("Что делать иначе:\n");
            for (String tip : this.advice) {
                builder.append("→ ").append(tip).append('\n');
            }
            return builder.toString();
        }
    }

    public void ensure() {
        this.load();
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

    public List<Report> reviews() {
        this.load();
        return new ArrayList<Report>(this.reviews);
    }

    public Report last() {
        this.load();
        return this.reviews.isEmpty() ? null : this.reviews.get(0);
    }

    /** Сколько секунд прошло с последнего урона — нужно для «в бою ли я». */
    public static long sinceLastDamage() {
        return INSTANCE.lastDamage == 0L ? Long.MAX_VALUE : System.currentTimeMillis() - INSTANCE.lastDamage;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        long now = System.currentTimeMillis();
        float current = client.player.getHealth();
        if (this.lastHealth < 0.0f) {
            this.lastHealth = current;
            return;
        }
        if (current < this.lastHealth - 0.01f) {
            double damage = this.lastHealth - current;
            this.record(new Hit(now, damage, FightReview.guessSource(client), false, ""));
            this.lastDamage = now;
            ScriptEngine.CombatClock.hurt();
        }
        else if (current > this.lastHealth + 0.01f) {
            this.lastHeal = now;
        }
        this.lastHealth = current;
        synchronized (this.health) {
            this.health.add(new float[]{now, current});
            while (this.health.size() > SAMPLE_LIMIT) {
                this.health.remove(0);
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        Entity target = attackEntityEvent.getTarget();
        if (target == null) {
            return;
        }
        this.record(new Hit(System.currentTimeMillis(), 0.0, "вы ударили", true, FightReview.nameOf(target)));
    }

    @EventHandler
    public void onTotem(TotemPopEvent totemPopEvent) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || totemPopEvent.getEntity() != client.player) {
            return;
        }
        String text = "тотем " + (totemPopEvent.isEnchanted() ? "(зачарованный)" : "");
        this.totems.add(text);
        this.record(new Hit(System.currentTimeMillis(), 0.0, "спас " + text, false, ""));
    }

    @EventHandler
    public void onDeath(DeathScreenEvent deathScreenEvent) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        Report report = this.build(client);
        this.reviews.add(0, report);
        while (this.reviews.size() > REVIEW_LIMIT) {
            this.reviews.remove(this.reviews.size() - 1);
        }
        this.save();
        NotificationsModule.notify("Разбор боя готов: " + report.verdict(), 4000L);
        if (rtx.byazen.api.modules.impl.Utils.FightReviewModule.wantsChat()) {
            rtx.byazen.utils.chat.ChatMessage.send("§bРазбор боя: §f" + report.verdict());
            int shown = 0;
            for (String line : report.text().split("\n")) {
                if (!line.startsWith("→") && !line.startsWith("*")) {
                    continue;
                }
                rtx.byazen.utils.chat.ChatMessage.send("§7" + line);
                if (!rtx.byazen.api.modules.impl.Utils.FightReviewModule.wantsFull() && ++shown >= 3) {
                    rtx.byazen.utils.chat.ChatMessage.send("§8…полный разбор — кнопкой «Последний разбор» в модуле Fight Review");
                    break;
                }
            }
        }
        WebBridge.pushEvent("fight", "Разбор боя: " + report.verdict());
        ClientLog.info("разбор боя: " + report.verdict());
        this.hits.clear();
        this.totems.clear();
    }

    private void record(Hit hit) {
        synchronized (this.hits) {
            this.hits.add(hit);
            while (this.hits.size() > HIT_LIMIT) {
                this.hits.remove(0);
            }
        }
    }

    /** Причина урона по состоянию игрока: точный источник сервер не сообщает. */
    private static String guessSource(MinecraftClient client) {
        if (client.player.isOnFire()) {
            return "огонь или лава";
        }
        if (client.player.fallDistance > 3.0f) {
            return "падение";
        }
        Entity nearest = FightReview.nearestEntity(client, 5.0);
        if (nearest != null) {
            return FightReview.nameOf(nearest);
        }
        if (client.world.getTimeOfDay() % 24000L > 13000L) {
            return "мобы или окружение";
        }
        return "окружение";
    }

    private static Entity nearestEntity(MinecraftClient client, double radius) {
        Entity best = null;
        double bestDistance = Double.MAX_VALUE;
        try {
            for (Entity entity : client.world.getEntities()) {
                if (entity == client.player) {
                    continue;
                }
                double distance = client.player.squaredDistanceTo(entity);
                if (distance <= radius * radius && distance < bestDistance) {
                    bestDistance = distance;
                    best = entity;
                }
            }
        }
        catch (Throwable ignored) {
        }
        return best;
    }

    private static String nameOf(Entity entity) {
        try {
            return entity.getName().getString();
        }
        catch (Throwable throwable) {
            return entity.getType() == null ? "сущность" : "сущность";
        }
    }

    private Report build(MinecraftClient client) {
        long now = System.currentTimeMillis();
        List<Hit> incoming;
        synchronized (this.hits) {
            incoming = new ArrayList<Hit>(this.hits);
        }
        double total = 0.0;
        double biggest = 0.0;
        String biggestSource = "—";
        int ourHits = 0;
        long first = now;
        Map<String, Double> bySource = new LinkedHashMap<String, Double>();
        for (Hit hit : incoming) {
            if (hit.outgoing()) {
                ++ourHits;
                continue;
            }
            if (hit.amount() <= 0.0) {
                continue;
            }
            total += hit.amount();
            bySource.merge(hit.source(), hit.amount(), Double::sum);
            if (hit.amount() > biggest) {
                biggest = hit.amount();
                biggestSource = hit.source();
            }
            first = Math.min(first, hit.time());
        }
        long battle = Math.max(0L, now - first);
        Report report = new Report(now,
                client.world.getRegistryKey().getValue().getPath(),
                Math.round(client.player.getX()) + " " + Math.round(client.player.getY()) + " " + Math.round(client.player.getZ()),
                total, biggest, biggestSource, ourHits, this.totems.size(),
                Math.min(battle, COMBAT_WINDOW));

        report.facts.add(String.format(Locale.ROOT, "здоровье перед смертью: %.1f из %.1f",
                client.player.getHealth(), client.player.getMaxHealth()));
        report.facts.add("эффекты: " + FightReview.effects(client));
        report.facts.add("броня: " + FightReview.armor(client));
        report.facts.add("голод: " + FightReview.hunger(client));
        report.facts.add(String.format(Locale.ROOT, "в бою участвовали %.1f с, урон шёл в основном от: %s",
                battle / 1000.0, FightReview.topSource(bySource)));
        if (!this.totems.isEmpty()) {
            report.facts.add("тотемы: " + String.join(", ", this.totems));
        }

        if (biggest >= 20.0) {
            report.advice.add("держите дистанцию с источников большого урона: " + biggestSource);
        }
        if (ourHits <= 2) {
            report.advice.add("отвечайте ударами: за бой вы почти не попали по противнику");
        }
        if (now - this.lastHeal > 8000L) {
            report.advice.add("последние 8 секунд вы не лечились — держите золотое яблоко или зелье под рукой");
        }
        if (FightReview.armorScore(client) < 8) {
            report.advice.add("часть брони отсутствует или изношена: почините её до следующего боя");
        }
        if (FightReview.potionCount(client) == 0) {
            report.advice.add("не было ни одного зелья: скорость, сила или сопротивление сильно меняют бой");
        }
        if (total / Math.max(1.0, battle / 1000.0) > 6.0) {
            report.advice.add("урон приходил очень плотно — отступайте раньше, не дожидаясь низкого HP");
        }
        report.advice.add("загляните в «Почему лагает» и «Профайлер»: часто смерть идёт от просадки кадров");
        return report;
    }

    private static String topSource(Map<String, Double> bySource) {
        String best = "—";
        double bestValue = 0.0;
        for (Map.Entry<String, Double> entry : bySource.entrySet()) {
            if (entry.getValue() > bestValue) {
                bestValue = entry.getValue();
                best = entry.getKey();
            }
        }
        return bestValue <= 0.0 ? best : String.format(Locale.ROOT, "%s (%.0f)", best, bestValue);
    }

    private static String effects(MinecraftClient client) {
        try {
            List<String> names = new ArrayList<String>();
            for (StatusEffectInstance instance : client.player.getStatusEffects()) {
                names.add(instance.getTranslationKey().replace("effect.minecraft.", "").replace("effect.", ""));
            }
            return names.isEmpty() ? "нет" : String.join(", ", names);
        }
        catch (Throwable throwable) {
            return "неизвестно";
        }
    }

    private static String armor(MinecraftClient client) {
        StringBuilder builder = new StringBuilder();
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (stack == null || stack.isEmpty()) {
                builder.append("— ");
                continue;
            }
            int max = stack.getMaxDamage();
            int damage = stack.getDamage();
            int percent = max <= 0 ? 100 : Math.max(0, 100 - damage * 100 / max);
            builder.append(percent).append("% ");
        }
        return builder.toString().trim();
    }

    private static int armorScore(MinecraftClient client) {
        int score = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (stack != null && !stack.isEmpty()) {
                score += 3;
            }
        }
        return score;
    }

    private static int potionCount(MinecraftClient client) {
        try {
            return client.player.getStatusEffects().size();
        }
        catch (Throwable throwable) {
            return 1;
        }
    }

    private static String hunger(MinecraftClient client) {
        try {
            Object manager = client.player.getClass().getMethod("getHungerManager").invoke(client.player);
            Object level = manager.getClass().getMethod("getFoodLevel").invoke(manager);
            return String.valueOf(((Number)level).intValue());
        }
        catch (Throwable throwable) {
            return "неизвестно";
        }
    }

    private void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("fight_reviews");
            JsonArray array = root.getAsJsonArray("items");
            if (array == null) {
                return;
            }
            for (int i = 0; i < array.size() && i < REVIEW_LIMIT; ++i) {
                JsonObject object = array.get(i).getAsJsonObject();
                Report report = new Report(object.get("time").getAsLong(),
                        object.has("world") ? object.get("world").getAsString() : "мир",
                        object.has("coords") ? object.get("coords").getAsString() : "—",
                        object.has("damage") ? object.get("damage").getAsDouble() : 0.0,
                        object.has("biggest") ? object.get("biggest").getAsDouble() : 0.0,
                        object.has("source") ? object.get("source").getAsString() : "—",
                        object.has("hits") ? object.get("hits").getAsInt() : 0,
                        object.has("totems") ? object.get("totems").getAsInt() : 0,
                        object.has("battle") ? object.get("battle").getAsLong() : 0L);
                if (object.has("facts")) {
                    for (var element : object.getAsJsonArray("facts")) {
                        report.facts.add(element.getAsString());
                    }
                }
                if (object.has("advice")) {
                    for (var element : object.getAsJsonArray("advice")) {
                        report.advice.add(element.getAsString());
                    }
                }
                this.reviews.add(report);
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("не удалось прочитать разборы боя: " + throwable.getClass().getSimpleName());
        }
    }

    private void save() {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        for (Report report : this.reviews) {
            JsonObject object = new JsonObject();
            object.addProperty("time", report.time);
            object.addProperty("world", report.world);
            object.addProperty("coords", report.coords);
            object.addProperty("damage", report.totalDamage);
            object.addProperty("biggest", report.biggestHit);
            object.addProperty("source", report.biggestSource);
            object.addProperty("hits", report.ourHits);
            object.addProperty("totems", report.totemsUsed);
            object.addProperty("battle", report.battleMs);
            JsonArray facts = new JsonArray();
            report.facts.forEach(facts::add);
            object.add("facts", facts);
            JsonArray advice = new JsonArray();
            report.advice.forEach(advice::add);
            object.add("advice", advice);
            array.add(object);
        }
        root.add("items", array);
        RepositoryStorage.write("fight_reviews", root);
    }
}
