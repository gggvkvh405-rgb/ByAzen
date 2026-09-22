package rtx.byazen.utils.scripts;

import java.util.ArrayList;
import java.util.List;

/**
 * «Магазин» готовых правил (идея №177 из IDEAS.md).
 * <p>
 * Ничего не скачивается из сети: наборы лежат прямо в клиенте, их можно посмотреть, установить
 * одной кнопкой в папку правил и потом править руками. Такой «магазин» честно работает офлайн.
 */
public final class ScriptCatalog {

    private ScriptCatalog() {
    }

    public record Item(String title, String note, String file, String text) {
    }

    private static final List<Item> ITEMS = new ArrayList<Item>();

    static {
        ScriptCatalog.add("Мало здоровья", "Напоминает съесть что-нибудь, когда HP ниже 8", "мало-hp.az", """
                rule "Мало здоровья"
                when health < 8
                do notify "Мало HP — съешь яблоко или выпей зелье"
                do sound
                cooldown 25
                end
                """);
        ScriptCatalog.add("Мало здоровья в бою", "Кричит громче, если вы в бою", "мало-hp-бой.az", """
                rule "Срочно лечись"
                when health < 6
                and in_combat
                do chat "HP на исходе — отходи и лечись!"
                do notify "HP на исходе"
                cooldown 8
                end
                """);
        ScriptCatalog.add("Голод на нуле", "Напоминает поесть до того, как начнёт отнимать HP", "голод.az", """
                rule "Пора поесть"
                when hunger < 6
                do notify "Голод кончается — поешь"
                cooldown 60
                end
                """);
        ScriptCatalog.add("Наступила ночь", "Сообщает о ночи и предлагает поставить факелы", "ночь.az", """
                rule "Наступила ночь"
                when night == 1
                do notify "Ночь: мобы скоро пойдут за вами"
                cooldown 300
                end
                """);
        ScriptCatalog.add("Пошёл дождь", "Тихое напоминание про лёд и воду", "дождь.az", """
                rule "Дождь"
                when rain == 1
                do chat "Дождь: вода поднимается, будь осторожнее"
                cooldown 600
                end
                """);
        ScriptCatalog.add("Горишь!", "Сообщение, когда вы загорелись", "огонь.az", """
                rule "Горишь"
                when on_fire == 1
                do notify "Ты горишь — к воде!"
                do sound
                cooldown 10
                end
                """);
        ScriptCatalog.add("Просадка FPS", "Меняет музыку на что-то спокойное и пишет причину", "fps.az", """
                rule "Просадка FPS"
                when fps < 30
                do notify "FPS ниже 30 — стоит понизить дальность"
                cooldown 120
                end
                """);
        ScriptCatalog.add("Броня просит ремонта", "Напоминает про наковальню, когда броня истёрлась", "броня.az", """
                rule "Броня истёрлась"
                when armor < 12
                do notify "Броня почти кончилась — отремонтируй"
                cooldown 180
                end
                """);
        ScriptCatalog.add("Ночью тише", "Останавливает музыку на ночь, чтобы слышать мобов", "ночь-тихо.az", """
                rule "Ночью тише"
                when night == 1
                do music_stop
                cooldown 300
                end
                """);
        ScriptCatalog.add("Ты в небе", "Уведомление на большой высоте", "высота.az", """
                rule "Высоко в небе"
                when y > 250
                do notify "Высоко: падение будет долгим"
                cooldown 60
                end
                """);
    }

    private static void add(String title, String note, String file, String text) {
        ITEMS.add(new Item(title, note, file, text));
    }

    public static List<Item> items() {
        return ITEMS;
    }

    /** Устанавливает набор в папку правил: заменяет одноимённый файл. */
    public static String install(String file) {
        for (Item item : ITEMS) {
            if (!item.file().equalsIgnoreCase(file)) {
                continue;
            }
            ScriptEngine.ParseResult result = ScriptEngine.parse(item.text());
            if (!result.ok()) {
                return "Набор «" + item.title() + "» не разобрался: " + result.error();
            }
            boolean saved = ScriptStore.write(item.file(), item.text());
            if (!saved) {
                return "Не удалось записать файл правила";
            }
            ScriptStore.reload();
            return "Установлено: " + item.title() + " · " + ScriptStore.summary();
        }
        return "Такого набора нет";
    }

    /** Устанавливает все наборы, которых ещё нет в папке. */
    public static int installMissing() {
        int installed = 0;
        for (Item item : ITEMS) {
            if (ScriptStore.files().contains(item.file())) {
                continue;
            }
            if (ScriptStore.write(item.file(), item.text())) {
                ++installed;
            }
        }
        ScriptStore.reload();
        return installed;
    }

    /** Текст набора для предпросмотра в окне. */
    public static String preview(String file) {
        for (Item item : ITEMS) {
            if (item.file().equalsIgnoreCase(file)) {
                return item.text();
            }
        }
        return "";
    }
}
