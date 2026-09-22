package rtx.byazen.utils.handbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Оффлайн-справочник клиента (идея №106 из IDEAS.md).
 * <p>
 * Разделы, которые нужны в игре: команды клиента, назначенные клавиши, координаты и порталы, зелья,
 * зачарования, тайминги и мелочи выживания. Всё хранится прямо в клиенте, поэтому справочник
 * открывается мгновенно и без интернета, а координаты он ещё и считает: напишите «1240 -380» — и
 * получите пересчёт между Нижним и Верхним миром, расстояние и направление.
 */
public final class Handbook {

    private static final Pattern NUMBERS = Pattern.compile("(-?\\d{1,7})\\s*[ ,;:xX*]*\\s*(-?\\d{1,7})");

    private Handbook() {
    }

    /** Раздел справочника: заголовок, короткая подсказка и строки. */
    public static final class Section {

        public final String title;
        public final String hint;
        public final List<String> lines;

        public Section(String title, String hint, List<String> lines) {
            this.title = title;
            this.hint = hint;
            this.lines = lines;
        }
    }

    /** Базовые разделы справочника без данных клиента. */
    public static List<Section> build() {
        ArrayList<Section> sections = new ArrayList<Section>();
        sections.add(new Section("Координаты и порталы",
                "Введите «X Z» в поиске — пересчитаю между Нижним и Верхним миром",
                Arrays.asList(
                        "Нижний мир → Верхний мир: умножить на 8",
                        "Верхний мир → Нижний мир: разделить на 8 (дробное округляется)",
                        "Близкие точки: на 1 блок в Нижнем приходится 8 блоков в Верхнем",
                        "Портал связывается, если точки в пределах 128 блоков в Верхнем мире",
                        "Привязка портала ищется в радиусе 16 блоков по горизонтали и 128 по вертикали",
                        "Точка возрождения: кровать или якорь возрождения; якорь работает в Нижнем мире",
                        "Магнитный камень: компас указывает на него, если он заряжен",
                        "Координаты из чата: клиент умеет разбирать «X Y Z» и подставлять в путевые точки")));
        sections.add(new Section("Зелья",
                "Что работает и сколько длится",
                Arrays.asList(
                        "Скорость II: 1:30 — сахар + 8 минут на огненном порошке",
                        "Сила II: 1:30 — адский нарост, кремень и вода",
                        "Регенерация II: 0:22 — слёзы гаста, продолжительность растёт зельем черепахи",
                        "Огнестойкость: 3:00 — магма слизень; продление до 8:00 на огненном порошке",
                        "Ночное зрение: 3:00 — золотая морковь",
                        "Подводное дыхание: 3:00 — иглобрюх",
                        "Невидимость: 3:00 — ночное зрение + ферментированный глаз паука",
                        "Прыжок II: 1:30 — кроличья лапка",
                        "Слабость: 1:30 — ферментированный глаз паука; помогает против атакующих",
                        "Медленное падение: 1:30 — «Замедленное падение» + мембрана фантома",
                        "Урон II: 0:22 — зелье вреда; в Верхнем мире тушится слёзами гаста",
                        "Метка: используйте стрелы с эффектами — они дешевле зелий в бою")));
        sections.add(new Section("Зачарования",
                "Максимальные уровни и что с чем сочетается",
                Arrays.asList(
                        "Острота V — на книге до IV в наковальне, максимально V",
                        "Прочность III — лучший выбор для инструментов",
                        "Эффективность V — кирка, топор, лопата",
                        "Удача III — руды и редкие дропы",
                        "Шелковое касание I — несовместимо с Удачей",
                        "Защита IV — на все четыре части брони",
                        "Прочность III + Починка I — вечная кирка",
                        "Небесная кара V / Бич членистоногих V / Разящий клинок V — одно из трёх",
                        "Быстрая перезарядка III и Бесконечность I — лук (Бесконечность несовместима с Починкой)",
                        "Мультивыстрел I — арбалет; Пронзание IV — тоже арбалет",
                        "Тягуньи III — трезубец; Верность III — несовместима с Тягуньями",
                        "Подводная ходьба III — сапоги, помогает по дну",
                        "Душевная скорость III — быстрее по земле душ",
                        "Прыгучесть IV — сапоги, несовместима с Душевной скоростью")));
        sections.add(new Section("Тайминги",
                "Сколько ждать в механизмах и на фермах",
                Arrays.asList(
                        "Редстоун-задержка: 1 такт = 0,1 сек, 1 секунда = 10 тактов",
                        "Повторитель: 1–4 такта, настраивается правой кнопкой",
                        "Наблюдатель: импульс 1 такт при обновлении блока",
                        "Рост пшеницы: 5–25 минут без костной муки",
                        "Костная мука: ускоряет рост мгновенно, но не бесконечно",
                        "Печь: 10 секунд на предмет, топливо — 8 предметов на уголь",
                        "Плавка в бластовой печи: 5 секунд на предмет, опыт как у печи, но больше",
                        "Выращивание дерева: 1–2 дня игровых (примерно 20–40 минут реального времени)",
                        "Рыбалка: 5–30 секунд на поклёвку, зачарование Приманка III ускоряет",
                        "Ночная смена мобов: с наступлением темноты, закат начинается на 12 000 тактов",
                        "Спавн в радиусе 24–128 блоков от игрока, при свете 7 и ниже",
                        "Сетка моба: 20 тактов = 1 секунда на сервере на 20 TPS")));
        sections.add(new Section("Выживание",
                "Мелочи, которые хорошо знать",
                Arrays.asList(
                        "Ведро воды гасит огонь и позволяет спуститься без шума",
                        "Кровать в Нижнем мире взрывается — используйте вместо неё якорь возрождения",
                        "Лодка на льду едет быстро и безопасно; на лаве работает только вагонетка",
                        "Ходьба по земле душ замедляет без Душевной скорости",
                        "Фейерверк с ракетой III даёт самый большой подъём на элитрах",
                        "Тотем бессмертия спасает от смерти; держите его в руке или в левой руке",
                        "Золотые яблоки лечат мгновенно; зачарованные — ещё и защиту и регенерацию",
                        "Молоко снимает любой эффект, в том числе вред от зелий",
                        "Стрелы можно крафтить с эффектами: 8 стрел + 1 зелье = 8 стрел с эффектом",
                        "Торговля с крестьянами даёт дешёвые изумруды и еду перед походом",
                        "Шалкеровые коробки сохраняют содержимое и место в инвентаре",
                        "Эндер-сундук доступен из любого места — держите в нём запас еды и блоков",
                        "Компас в Нижнем мире не работает, но карта-восстановление спасает",
                        "Снежный голем гасит лаву и мешает гастам")));
        return sections;
    }

    /** Раздел с командами клиента: собирается из текущего префикса и списка команд. */
    public static Section commands(String prefix, List<String> commands) {
        ArrayList<String> lines = new ArrayList<String>();
        String clean = prefix == null || prefix.isEmpty() ? "." : prefix;
        lines.add("Все команды клиента работают с префиксом «" + clean + "»");
        lines.add("Полный список открывает команда " + clean + "help");
        for (String command : commands) {
            lines.add(clean + command);
        }
        return new Section("Команды клиента", "Команды вводятся в чат игры, регистр не важен", lines);
    }

    /** Раздел с назначенными клавишами. */
    public static Section keys(List<String> binds) {
        ArrayList<String> lines = new ArrayList<String>();
        if (binds.isEmpty()) {
            lines.add("Пока ни одной клавиши не назначено");
            lines.add("Назначайте клавиши в настройках модуля — тогда они появятся здесь");
        }
        else {
            lines.addAll(binds);
            lines.add("Клавиши можно менять в настройках каждого модуля");
        }
        return new Section("Клавиши", "Что нажатие делает прямо сейчас", lines);
    }

    /** Пересчёт координат по строке запроса. Пустой список — строка не похожа на координаты. */
    public static List<String> coordinates(String query) {
        ArrayList<String> lines = new ArrayList<String>();
        if (query == null) {
            return lines;
        }
        Matcher matcher = NUMBERS.matcher(query.trim());
        if (!matcher.find()) {
            return lines;
        }
        int x;
        int z;
        try {
            x = Integer.parseInt(matcher.group(1));
            z = Integer.parseInt(matcher.group(2));
        }
        catch (Throwable throwable) {
            return lines;
        }
        int overworldX = x * 8;
        int overworldZ = z * 8;
        int netherX = Math.round(x / 8.0f);
        int netherZ = Math.round(z / 8.0f);
        lines.add("Введено: X " + x + ", Z " + z);
        lines.add("Если это Нижний мир → в Верхнем: X " + overworldX + ", Z " + overworldZ);
        lines.add("Если это Верхний мир → в Нижнем: X " + netherX + ", Z " + netherZ);
        lines.add("Расстояние от нуля в Верхнем мире: " + Math.round(Math.sqrt((double)(overworldX * overworldX + overworldZ * overworldZ))) + " блоков");
        lines.add("Расстояние от нуля в Нижнем мире: " + Math.round(Math.sqrt((double)(netherX * netherX + netherZ * netherZ))) + " блоков");
        lines.add("Направление от нуля: " + Handbook.direction(x, z));
        lines.add("Пешком в Верхнем мире (4,3 блока/с): примерно " + Handbook.walkTime(Math.sqrt((double)(overworldX * overworldX + overworldZ * overworldZ))));
        lines.add("На лошади (14 блока/с): примерно " + Handbook.walkTime(Math.sqrt((double)(overworldX * overworldX + overworldZ * overworldZ)) / 14.0 * 4.3));
        lines.add("Постройте портал в Нижнем мире около X " + netherX + ", Z " + netherZ + " — он свяжется с этой точкой");
        return lines;
    }

    /** Направление в градусах и словами: куда смотреть, чтобы идти к нулю. */
    public static String direction(double x, double z) {
        double angle = Math.toDegrees(Math.atan2(-x, z));
        if (angle < 0.0) {
            angle += 360.0;
        }
        String word;
        if (angle < 22.5 || angle >= 337.5) {
            word = "юг";
        }
        else if (angle < 67.5) {
            word = "юго-восток";
        }
        else if (angle < 112.5) {
            word = "восток";
        }
        else if (angle < 157.5) {
            word = "северо-восток";
        }
        else if (angle < 202.5) {
            word = "север";
        }
        else if (angle < 247.5) {
            word = "северо-запад";
        }
        else if (angle < 292.5) {
            word = "запад";
        }
        else {
            word = "юго-запад";
        }
        return word + String.format(Locale.ROOT, " (%.0f°)", angle);
    }

    /** Время в пути для расстояния: «2 мин 14 с». */
    public static String walkTime(double blocks) {
        double seconds = blocks / 4.3;
        if (seconds < 60.0) {
            return Math.round(seconds) + " с";
        }
        int minutes = (int)(seconds / 60.0);
        int rest = (int)Math.round(seconds - (double)minutes * 60.0);
        return minutes + " мин " + rest + " с";
    }

    /** Поиск по всем разделам: возвращает строки, в которых встречается запрос. */
    public static List<String> find(List<Section> sections, String query) {
        ArrayList<String> result = new ArrayList<String>();
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            return result;
        }
        for (Section section : sections) {
            for (String line : section.lines) {
                if (line.toLowerCase(Locale.ROOT).contains(needle)) {
                    result.add("[" + section.title + "] " + line);
                }
            }
        }
        return result;
    }
}
