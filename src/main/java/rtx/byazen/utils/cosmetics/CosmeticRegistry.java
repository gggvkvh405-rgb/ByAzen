package rtx.byazen.utils.cosmetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * Каталог всей косметики клиента (идеи №121 и №122 из IDEAS.md).
 * <p>
 * Здесь лежит полный список предметов: базовые аксессуары, доступные сразу, и три набора ByAzen
 * «Зима», «Неон» и «Космос» — по десять предметов в каждом. Наборы сезонные: в свой сезон весь набор
 * можно забрать бесплатно, в остальное время предметы приходят выдачей от LiteApi.
 */
public final class CosmeticRegistry {

    /** Набор: имя, вкус (стиль частиц), сезон и предметы. */
    public static final class Set {
        public final String flavor;
        public final String name;
        public final String description;
        public final int[] months;
        public final int color;
        public final int accent;
        public final List<Cosmetic> items;

        Set(String flavor, String name, String description, int[] months, int color, int accent, List<Cosmetic> items) {
            this.flavor = flavor;
            this.name = name;
            this.description = description;
            this.months = months;
            this.color = color & 0xFFFFFF;
            this.accent = accent & 0xFFFFFF;
            this.items = Collections.unmodifiableList(items);
        }

        public boolean has(Cosmetic cosmetic) {
            return cosmetic != null && this.flavor.equals(cosmetic.flavor);
        }
    }

    private static final List<Cosmetic> BASE = new ArrayList<Cosmetic>();
    private static final List<Set> SETS = new ArrayList<Set>();
    private static final List<Cosmetic> ALL = new ArrayList<Cosmetic>();
    private static final Map<String, Cosmetic> BY_ID = new LinkedHashMap<String, Cosmetic>();

    private CosmeticRegistry() {
    }

    private static Cosmetic add(List<Cosmetic> target, Cosmetic cosmetic) {
        target.add(cosmetic);
        ALL.add(cosmetic);
        BY_ID.put(cosmetic.id, cosmetic);
        return cosmetic;
    }

    private static Cosmetic item(String id, String name, String slot, String shape, String flavor, String description,
                                 int color, int accent, Item icon, boolean free) {
        return new Cosmetic(id, name, slot, shape, flavor, description, color, accent, icon, free);
    }

    static {
        // --- базовые аксессуары: доступны сразу, это «первый комплект» любого игрока
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_hat", "Цилиндр", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HAT, "",
                "Строгий цилиндр: держится ровно и слегка покачивается при ходьбе.", 0x2B2F3A, 0x8FA8FF, Items.LEATHER_HELMET, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_crown", "Корона", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_CROWN, "",
                "Золотая корона с самоцветами и мягким свечением по зубцам.", 0xE8C15A, 0xFFF0B8, Items.GOLDEN_HELMET, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_horns", "Рога", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HORNS, "",
                "Пара изогнутых рогов: растут из висков и повторяют наклон головы.", 0x8E3B3B, 0xFF9A6B, Items.GOAT_HORN, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_halo", "Нимб", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HALO, "",
                "Тёплый светящийся круг над головой: медленно вращается.", 0xFFE9A8, 0xFFF6D8, Items.GLOWSTONE_DUST, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_ears_cat", "Кошачьи уши", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_CAT, "",
                "Мягкие кошачьи ушки: подрагивают, когда вы прыгаете.", 0x3A3F4C, 0xFFA8C8, Items.RABBIT_FOOT, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_ears_fox", "Лисьи уши", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_FOX, "",
                "Большие лисьи уши с белыми кончиками и живой физикой.", 0xE0763C, 0xFFE2C4, Items.SWEET_BERRIES, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_mask", "Маска", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_MASK, "",
                "Гладкая маска на пол-лица с прорезями для глаз.", 0x1F2430, 0x8FA8FF, Items.CARVED_PUMPKIN, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_glasses", "Очки", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_GLASSES, "",
                "Круглые очки со стеклянным блеском и тонкой оправой.", 0x9AD7FF, 0xE6F6FF, Items.GLASS_PANE, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_scarf", "Шарф", Cosmetic.SLOT_NECK, Cosmetic.SHAPE_SCARF, "",
                "Длинный шарф: концы развеваются при беге и ветре.", 0xB03A48, 0xFFB4A8, Items.RED_WOOL, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_tail", "Хвост", Cosmetic.SLOT_TAIL, Cosmetic.SHAPE_TAIL, "",
                "Пушистый хвост на пружинной физике: ходит из стороны в сторону.", 0x6B4A32, 0xFFD9A8, Items.STRING, true));
        CosmeticRegistry.add(BASE, CosmeticRegistry.item("base_aura", "Аура", Cosmetic.SLOT_AURA, Cosmetic.SHAPE_AURA, "",
                "Светящееся кольцо у ног и тихие искры вокруг игрока.", 0x8FA8FF, 0xC7D6FF, Items.AMETHYST_SHARD, true));

        // --- набор «Зима»
        ArrayList<Cosmetic> winter = new ArrayList<Cosmetic>();
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_hat", "Снежный цилиндр", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HAT, Cosmetic.FLAVOR_SNOW,
                "Цилиндр цвета свежего снега с искрящейся лентой.", 0xE9F4FF, 0xFFFFFF, Items.LEATHER_HELMET, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_crown", "Корона зимы", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_CROWN, Cosmetic.FLAVOR_SNOW,
                "Ледяная корона: зубцы светятся морозным светом.", 0xCFE9FF, 0xFFFFFF, Items.GOLDEN_HELMET, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_halo", "Морозный нимб", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HALO, Cosmetic.FLAVOR_SNOW,
                "Нимб изо льда: кружится и оставляет снежинки.", 0xE7F6FF, 0xBFE4FF, Items.GLOWSTONE_DUST, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_ears_cat", "Снежные ушки", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_CAT, Cosmetic.FLAVOR_SNOW,
                "Белые кошачьи ушки с пушистой подкладкой.", 0xDCEBFA, 0xFFFFFF, Items.RABBIT_FOOT, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_ears_fox", "Белый лис", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_FOX, Cosmetic.FLAVOR_SNOW,
                "Уши полярного лиса: белые с голубым отливом.", 0xF2F7FF, 0xBFE4FF, Items.SWEET_BERRIES, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_mask", "Ледяная маска", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_MASK, Cosmetic.FLAVOR_SNOW,
                "Маска из прозрачного льда с морозным узором.", 0xA8D2F0, 0xFFFFFF, Items.CARVED_PUMPKIN, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_glasses", "Иней на очках", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_GLASSES, Cosmetic.FLAVOR_SNOW,
                "Очки, покрытые инеем: по стеклу бегут снежинки.", 0xCDE9FF, 0xFFFFFF, Items.GLASS_PANE, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_scarf", "Снежный шарф", Cosmetic.SLOT_NECK, Cosmetic.SHAPE_SCARF, Cosmetic.FLAVOR_SNOW,
                "Длинный шарф с вязаным узором, развевается на ветру.", 0xE6F1FF, 0xBFE4FF, Items.RED_WOOL, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_tail", "Пушистый хвост", Cosmetic.SLOT_TAIL, Cosmetic.SHAPE_TAIL, Cosmetic.FLAVOR_SNOW,
                "Тёплый пушистый хвост, оставляющий снежный след.", 0xBBD6EE, 0xFFFFFF, Items.STRING, false));
        CosmeticRegistry.add(winter, CosmeticRegistry.item("winter_aura", "Вьюга", Cosmetic.SLOT_AURA, Cosmetic.SHAPE_AURA, Cosmetic.FLAVOR_SNOW,
                "Вокруг игрока кружатся снежинки и мягко светится кольцо.", 0xDFF1FF, 0xFFFFFF, Items.AMETHYST_SHARD, false));
        SETS.add(new Set(Cosmetic.FLAVOR_SNOW, "Зима", "Морозный набор: белые аксессуары, снежинки и ледяное свечение.",
                new int[]{12, 1, 2}, 0xBFE4FF, 0xFFFFFF, winter));

        // --- набор «Неон»
        ArrayList<Cosmetic> neon = new ArrayList<Cosmetic>();
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_hat", "Неоновый цилиндр", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HAT, Cosmetic.FLAVOR_NEON,
                "Тёмный цилиндр с яркой неоновой лентой по кругу.", 0x140A1E, 0x39FF88, Items.LEATHER_HELMET, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_crown", "Неоновая корона", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_CROWN, Cosmetic.FLAVOR_NEON,
                "Корона с пылающими розовыми зубцами.", 0x1A0F28, 0xFF3DF0, Items.GOLDEN_HELMET, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_horns", "Неоновые рога", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HORNS, Cosmetic.FLAVOR_NEON,
                "Рога из светящихся трубок: холодный голубой свет.", 0x12081C, 0x3DF0FF, Items.GOAT_HORN, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_halo", "Неоновый нимб", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HALO, Cosmetic.FLAVOR_NEON,
                "Быстрый неоновый круг: оставляет светящийся след.", 0x2A0F33, 0x39FF88, Items.GLOWSTONE_DUST, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_ears_cat", "Неоновые ушки", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_CAT, Cosmetic.FLAVOR_NEON,
                "Ушки с розовым неоновым контуром.", 0x1B0F26, 0xFF5BE0, Items.RABBIT_FOOT, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_ears_fox", "Кибер-лис", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_FOX, Cosmetic.FLAVOR_NEON,
                "Уши киберлиса с бирюзовой подсветкой.", 0x20102B, 0x3DF0FF, Items.SWEET_BERRIES, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_mask", "Неоновая маска", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_MASK, Cosmetic.FLAVOR_NEON,
                "Маска с изумрудными светящимися прорезями.", 0x0E0716, 0x39FFD0, Items.CARVED_PUMPKIN, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_glasses", "Неоновые очки", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_GLASSES, Cosmetic.FLAVOR_NEON,
                "Очки со светящейся зелёной оправой.", 0x140A1E, 0x7BFF5B, Items.GLASS_PANE, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_scarf", "Неоновый шарф", Cosmetic.SLOT_NECK, Cosmetic.SHAPE_SCARF, Cosmetic.FLAVOR_NEON,
                "Шарф из светящихся волокон, развевается ярче в движении.", 0x180C22, 0xFF3DF0, Items.RED_WOOL, false));
        CosmeticRegistry.add(neon, CosmeticRegistry.item("neon_aura", "Неоновая аура", Cosmetic.SLOT_AURA, Cosmetic.SHAPE_AURA, Cosmetic.FLAVOR_NEON,
                "Пульсирующее кольцо и зелёные искры вокруг игрока.", 0x250A33, 0x39FF88, Items.AMETHYST_SHARD, false));
        SETS.add(new Set(Cosmetic.FLAVOR_NEON, "Неон", "Ночной набор: тёмная основа и яркое неоновое свечение с искрами.",
                new int[]{6, 7, 8}, 0x39FF88, 0xFF3DF0, neon));

        // --- набор «Космос»
        ArrayList<Cosmetic> space = new ArrayList<Cosmetic>();
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_hat", "Шляпа астронавта", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HAT, Cosmetic.FLAVOR_SPACE,
                "Шляпа с картой звёзд и сиреневой подсветкой.", 0x232A4D, 0x9F7BFF, Items.LEATHER_HELMET, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_crown", "Корона созвездий", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_CROWN, Cosmetic.FLAVOR_SPACE,
                "Корона с золотыми звёздами по зубцам.", 0x2A2F5E, 0xFFE07B, Items.GOLDEN_HELMET, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_horns", "Рога кометы", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HORNS, Cosmetic.FLAVOR_SPACE,
                "Рога со шлейфом из звёздной пыли.", 0x262C52, 0xC7A8FF, Items.GOAT_HORN, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_halo", "Орбита", Cosmetic.SLOT_HEAD, Cosmetic.SHAPE_HALO, Cosmetic.FLAVOR_SPACE,
                "Нимб-орбита с крошечной планетой.", 0x1E2447, 0x7BD1FF, Items.GLOWSTONE_DUST, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_ears_cat", "Уши луны", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_CAT, Cosmetic.FLAVOR_SPACE,
                "Ушки цвета лунной пыли с серебристым краем.", 0x232A4D, 0xE6E9FF, Items.RABBIT_FOOT, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_ears_fox", "Уши лиса-звезды", Cosmetic.SLOT_EARS, Cosmetic.SHAPE_EARS_FOX, Cosmetic.FLAVOR_SPACE,
                "Уши с золотистым свечением, как у звезды.", 0x2B3160, 0xFFB86B, Items.SWEET_BERRIES, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_mask", "Маска пустоты", Cosmetic.SLOT_FACE, Cosmetic.SHAPE_MASK, Cosmetic.FLAVOR_SPACE,
                "Тёмная маска с фиолетовыми прорезями.", 0x141833, 0x8F7BFF, Items.CARVED_PUMPKIN, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_scarf", "Шарф галактики", Cosmetic.SLOT_NECK, Cosmetic.SHAPE_SCARF, Cosmetic.FLAVOR_SPACE,
                "Шарф с переливом галактики и звёздами по ткани.", 0x2C2F63, 0xB06BFF, Items.RED_WOOL, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_tail", "Хвост кометы", Cosmetic.SLOT_TAIL, Cosmetic.SHAPE_TAIL, Cosmetic.FLAVOR_SPACE,
                "Хвост со шлейфом из светящихся искр.", 0x232A4D, 0x9FE8FF, Items.STRING, false));
        CosmeticRegistry.add(space, CosmeticRegistry.item("space_aura", "Звёздная пыль", Cosmetic.SLOT_AURA, Cosmetic.SHAPE_AURA, Cosmetic.FLAVOR_SPACE,
                "Кольцо звёздной пыли и медленно падающие искры.", 0x2A2F5E, 0xC7A8FF, Items.AMETHYST_SHARD, false));
        SETS.add(new Set(Cosmetic.FLAVOR_SPACE, "Космос", "Набор глубокого космоса: сиреневые тона, звёзды и светящиеся искры.",
                new int[]{9, 10, 11}, 0x9F7BFF, 0xC7A8FF, space));
    }

    public static List<Cosmetic> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static List<Cosmetic> base() {
        return Collections.unmodifiableList(BASE);
    }

    public static List<Set> sets() {
        return Collections.unmodifiableList(SETS);
    }

    public static Set setOf(String flavor) {
        for (Set set : SETS) {
            if (set.flavor.equalsIgnoreCase(flavor)) {
                return set;
            }
        }
        return null;
    }

    public static Cosmetic byId(String id) {
        return id == null ? null : BY_ID.get(id.toLowerCase(Locale.ROOT));
    }

    /** Предметы конкретного набора по его стилю; пустой стиль — базовые аксессуары. */
    public static List<Cosmetic> ofFlavor(String flavor) {
        String needle = flavor == null ? "" : flavor.toLowerCase(Locale.ROOT);
        ArrayList<Cosmetic> list = new ArrayList<Cosmetic>();
        for (Cosmetic cosmetic : ALL) {
            if (cosmetic.flavor.toLowerCase(Locale.ROOT).equals(needle)) {
                list.add(cosmetic);
            }
        }
        return list;
    }

    /** Идёт ли сейчас сезон набора: месяцы заданы списком. */
    public static boolean seasonNow(String flavor, int month) {
        Set set = CosmeticRegistry.setOf(flavor);
        if (set == null) {
            return false;
        }
        for (int value : set.months) {
            if (value == month) {
                return true;
            }
        }
        return false;
    }

    /** Название сезона набора для подсказки в каталоге. */
    public static String seasonText(String flavor) {
        Set set = CosmeticRegistry.setOf(flavor);
        if (set == null) {
            return "";
        }
        String[] names = {"", "январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};
        StringBuilder builder = new StringBuilder();
        for (int value : set.months) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value >= 1 && value <= 12 ? names[value] : String.valueOf(value));
        }
        return "сезон: " + builder;
    }
}
