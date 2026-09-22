package rtx.byazen.utils.profiles;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.render.cache.CacheManager;
import rtx.byazen.utils.web.WebBridge;

/**
 * Сборки Lite и Full (идея №187 из IDEAS.md).
 * <p>
 * Полная сборка везёт всё; Lite-сборка собирается отдельным jar-ом (CI кладёт её в артефакты) и
 * помечена файлом {@code byazen-tier.txt} внутри jar. При запуске Lite-сборки клиент сам предлагает
 * облегчённый профиль: тяжёлые визуалы выключаются, кэши картинок и анимаций урезаются, а окна и
 * утилиты остаются на месте. Профиль можно применить или отменить в любой момент.
 */
public final class BuildTier {

    /** Тяжёлый модуль и объяснение, почему он в списке. */
    public record Heavy(String module, String why) {
    }

    private static final List<Heavy> HEAVY = new ArrayList<Heavy>();
    private static String tier;

    static {
        BuildTier.add("Cosmetics 3D", "рисует объёмную косметику каждый кадр");
        BuildTier.add("Kill Effect 3D", "тяжёлые эффекты смерти");
        BuildTier.add("Footprints", "следы с частицами");
        BuildTier.add("Animated Capes", "физика плаща");
        BuildTier.add("Pet Style", "своя модель питомца");
        BuildTier.add("Custom Particles", "дополнительные частицы");
        BuildTier.add("Bloom", "постобработка свечения");
        BuildTier.add("Emotions", "эмоции персонажа");
        BuildTier.add("Kill FX", "эффекты убийств");
        BuildTier.add("Death Effects", "эффекты смерти");
        BuildTier.add("Hit Particles", "частицы ударов");
        BuildTier.add("China Hat", "лишняя геометрия на голове");
    }

    private BuildTier() {
    }

    private static void add(String module, String why) {
        HEAVY.add(new Heavy(module, why));
    }

    public static List<Heavy> heavy() {
        return new ArrayList<Heavy>(HEAVY);
    }

    /** Как собрана эта сборка: full или lite. */
    public static String tier() {
        if (tier != null) {
            return tier;
        }
        tier = "full";
        try (InputStream stream = BuildTier.class.getClassLoader().getResourceAsStream("byazen-tier.txt")) {
            if (stream != null) {
                String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8).trim().toLowerCase();
                if (text.contains("lite")) {
                    tier = "lite";
                }
            }
        }
        catch (Throwable throwable) {
            ClientLog.debug("сборка: маркер tier не найден");
        }
        return tier;
    }

    public static boolean lite() {
        return BuildTier.tier().equals("lite");
    }

    public static String label() {
        return BuildTier.lite() ? "ByAzen Lite (облегчённая сборка)" : "ByAzen Full (полная сборка)";
    }

    public static String summary() {
        int enabledHeavy = 0;
        ModuleManager manager = ModuleManager.get();
        for (Heavy heavy : HEAVY) {
            Module module = manager == null ? null : manager.findByName(heavy.module());
            if (module != null && module.isEnabled()) {
                ++enabledHeavy;
            }
        }
        return BuildTier.label() + " · тяжёлых модулей включено " + enabledHeavy + " из " + HEAVY.size()
                + " · кэш GIF " + CacheManager.limit("gifs") + ", картинок " + CacheManager.limit("images");
    }

    /** Облегчённый профиль: выключает тяжёлые визуалы и урезает кэши. */
    public static int applyLite() {
        ModuleManager manager = ModuleManager.get();
        int disabled = 0;
        if (manager != null) {
            for (Heavy heavy : HEAVY) {
                Module module = manager.findByName(heavy.module());
                if (module != null && module.isEnabled()) {
                    module.setEnabled(false);
                    ++disabled;
                }
            }
        }
        CacheManager.setLimit("gifs", 8);
        CacheManager.setLimit("images", 32);
        CacheManager.setLimit("covers", 16);
        NotificationsModule.notify("§bОблегчённый профиль включён: выключено модулей " + disabled, 4000L);
        ChatMessage.send("§bПрофиль Lite: §7выключено " + disabled + " тяжёлых модулей, кэши урезаны. "
                + "Вернуть обратно — кнопка «Профиль Full» в модуле Build Tier");
        WebBridge.pushEvent("tier", "Включён профиль Lite (" + disabled + ")");
        ClientLog.info("сборка: включён профиль Lite, выключено модулей " + disabled);
        return disabled;
    }

    /** Полный профиль: возвращает кэши и подсказывает, что включить. */
    public static String applyFull() {
        CacheManager.setLimit("gifs", 24);
        CacheManager.setLimit("images", 72);
        CacheManager.setLimit("covers", 48);
        ChatMessage.send("§bПрофиль Full: §7кэши вернулись к обычным. Тяжёлые визуалы включайте по вкусу — "
                + "список в модуле Build Tier");
        WebBridge.pushEvent("tier", "Включён профиль Full");
        return "Профиль Full применён";
    }

    /** Подсказка при первом запуске Lite-сборки. */
    public static void hintIfLite() {
        if (!BuildTier.lite()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        NotificationsModule.notify("§bЭто Lite-сборка ByAzen", 5000L);
        ChatMessage.send("§7Lite-сборка: тяжёлые визуалы по умолчанию выключены. Применить профиль — модуль §fBuild Tier");
    }

    /** Строки для окна и хаба. */
    public static List<String> rows() {
        ArrayList<String> rows = new ArrayList<String>();
        rows.add("§7Сборка: " + BuildTier.summary());
        for (Heavy heavy : HEAVY) {
            Module module = ModuleManager.get() == null ? null : ModuleManager.get().findByName(heavy.module());
            boolean exists = module != null;
            boolean enabled = exists && module.isEnabled();
            rows.add((enabled ? "§c● " : "§7○ ") + heavy.module() + " §8— " + heavy.why()
                    + (exists ? "" : " §8(модуль не найден)"));
        }
        rows.add("§7Профиль Lite: " + (BuildTier.lite() ? "эта сборка уже Lite" : "доступен по кнопке"));
        return rows;
    }
}
