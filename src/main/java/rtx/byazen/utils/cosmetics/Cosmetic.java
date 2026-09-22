package rtx.byazen.utils.cosmetics;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * Один предмет косметики (идея №121 из IDEAS.md).
 * <p>
 * Описание предмета полностью данными: где он сидит (слот), какой формы (shape — по ней строится
 * модель), из какого набора, какого цвета и что за частицы его сопровождают. Благодаря этому каталог
 * и рендер не знают друг о друге: рендер смотрит на форму и цвет, каталог — на название и набор.
 */
public final class Cosmetic {

    /** Слоты: на голове, на ушах, на лице, на шее, хвост и аура вокруг игрока. */
    public static final String SLOT_HEAD = "head";
    public static final String SLOT_EARS = "ears";
    public static final String SLOT_FACE = "face";
    public static final String SLOT_NECK = "neck";
    public static final String SLOT_TAIL = "tail";
    public static final String SLOT_AURA = "aura";

    /** Формы моделей: по ним рендер строит геометрию. */
    public static final String SHAPE_HAT = "hat";
    public static final String SHAPE_CROWN = "crown";
    public static final String SHAPE_HORNS = "horns";
    public static final String SHAPE_HALO = "halo";
    public static final String SHAPE_EARS_CAT = "ears_cat";
    public static final String SHAPE_EARS_FOX = "ears_fox";
    public static final String SHAPE_MASK = "mask";
    public static final String SHAPE_GLASSES = "glasses";
    public static final String SHAPE_SCARF = "scarf";
    public static final String SHAPE_TAIL = "tail";
    public static final String SHAPE_AURA = "aura";
    public static final String SHAPE_BANDANA = "bandana";

    /** Сопровождение набора: снежинки, неон, звёзды. */
    public static final String FLAVOR_NONE = "";
    public static final String FLAVOR_SNOW = "snow";
    public static final String FLAVOR_NEON = "neon";
    public static final String FLAVOR_SPACE = "space";

    public final String id;
    public final String name;
    public final String slot;
    public final String shape;
    public final String setName;
    public final String flavor;
    public final String description;
    public final int color;
    public final int accent;
    public final Item icon;
    public final boolean free;

    public Cosmetic(String id, String name, String slot, String shape, String setName, String flavor,
                    String description, int color, int accent, Item icon, boolean free) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.shape = shape;
        this.setName = setName == null ? "" : setName;
        this.flavor = flavor == null ? FLAVOR_NONE : flavor;
        this.description = description == null ? "" : description;
        this.color = color & 0xFFFFFF;
        this.accent = accent & 0xFFFFFF;
        this.icon = icon == null ? Items.LEATHER_HELMET : icon;
        this.free = free;
    }

    public boolean inSet() {
        return !this.setName.isEmpty();
    }

    /** Человеческое название слота для каталога. */
    public String slotName() {
        switch (this.slot) {
            case SLOT_HEAD:
                return "Голова";
            case SLOT_EARS:
                return "Уши";
            case SLOT_FACE:
                return "Лицо";
            case SLOT_NECK:
                return "Шея";
            case SLOT_TAIL:
                return "Хвост";
            case SLOT_AURA:
                return "Аура";
            default:
                return "Аксессуар";
        }
    }

    /** Название набора по-русски. */
    public String setNameText() {
        if (FLAVOR_SNOW.equals(this.flavor)) {
            return "Зима";
        }
        if (FLAVOR_NEON.equals(this.flavor)) {
            return "Неон";
        }
        if (FLAVOR_SPACE.equals(this.flavor)) {
            return "Космос";
        }
        return "Базовый";
    }

    public boolean snow() {
        return FLAVOR_SNOW.equals(this.flavor);
    }

    public boolean neon() {
        return FLAVOR_NEON.equals(this.flavor);
    }

    public boolean space() {
        return FLAVOR_SPACE.equals(this.flavor);
    }
}
