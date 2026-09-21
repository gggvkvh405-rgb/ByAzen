package rtx.byazen.utils.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;

/**
 * Палитра мини-карты (идея №71 из IDEAS.md): мягкие природные цвета вместо случайных.
 * Каждый блок получает свой узнаваемый оттенок, а рельеф читается за счёт освещения по высоте и склонам.
 */
public final class MinimapColors {

    private static final int UNKNOWN = 0x7C7F86;

    private MinimapColors() {
    }

    public static int base(BlockState state) {
        if (state == null || state.isAir()) {
            return 0x0E1016;
        }
        if (state.isOf(Blocks.WATER)) {
            return 0x2F6FD0;
        }
        if (state.isOf(Blocks.LAVA)) {
            return 0xE4622A;
        }
        if (state.isOf(Blocks.ICE) || state.isOf(Blocks.PACKED_ICE) || state.isOf(Blocks.BLUE_ICE)) {
            return 0xAED7F2;
        }
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.POWDER_SNOW)) {
            return 0xEEF4FA;
        }
        if (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.MOSS_BLOCK)) {
            return 0x5C9B4A;
        }
        if (state.isOf(Blocks.SAND) || state.isOf(Blocks.SANDSTONE) || state.isOf(Blocks.RED_SAND) || state.isOf(Blocks.RED_SANDSTONE)) {
            return 0xD8C48A;
        }
        if (state.isIn(BlockTags.LEAVES)) {
            return 0x3E7A38;
        }
        if (state.isIn(BlockTags.LOGS)) {
            return 0x6B4B2A;
        }
        if (state.isIn(BlockTags.PLANKS)) {
            return 0xA4773F;
        }
        if (state.isOf(Blocks.DIRT) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.PODZOL) || state.isOf(Blocks.FARMLAND)
                || state.isOf(Blocks.MUD) || state.isOf(Blocks.SOUL_SAND) || state.isOf(Blocks.CLAY) || state.isOf(Blocks.GRAVEL)) {
            return 0x8A6A48;
        }
        if (state.isOf(Blocks.STONE) || state.isOf(Blocks.COBBLESTONE) || state.isOf(Blocks.DEEPSLATE) || state.isOf(Blocks.TUFF)) {
            return 0x8E9095;
        }
        if (state.isOf(Blocks.ANDESITE) || state.isOf(Blocks.DIORITE) || state.isOf(Blocks.GRANITE) || state.isOf(Blocks.BASALT)) {
            return 0xA2A4A8;
        }
        if (state.isOf(Blocks.NETHERRACK)) {
            return 0x8C3A34;
        }
        if (state.isOf(Blocks.END_STONE)) {
            return 0xD9D6A8;
        }
        if (state.isOf(Blocks.OBSIDIAN)) {
            return 0x3A2E5A;
        }
        String key = state.getBlock().getTranslationKey();
        if (key.contains("_ore")) {
            return 0x9A8F7A;
        }
        if (key.contains("flower") || key.contains("tulip") || key.contains("daisy") || key.contains("rose")) {
            return 0x6FA84A;
        }
        return UNKNOWN;
    }

    /** Ореол для руды на мини-карте: дорогие блоки чуть подсвечиваются. */
    public static boolean isOre(BlockState state) {
        return state != null && !state.isAir() && state.getBlock().getTranslationKey().contains("_ore");
    }

    /** Освещение по высоте и наклону: рельеф становится объёмным. */
    public static int shade(int rgb, float factor) {
        float f = Math.max(0.55f, Math.min(1.45f, factor));
        int r = Math.round((float)(rgb >> 16 & 0xFF) * f);
        int g = Math.round((float)(rgb >> 8 & 0xFF) * f);
        int b = Math.round((float)(rgb & 0xFF) * f);
        return MinimapColors.clamp(r) << 16 | MinimapColors.clamp(g) << 8 | MinimapColors.clamp(b) | 0xFF000000;
    }

    public static int withAlpha(int rgb, int alpha) {
        return rgb & 0xFFFFFF | Math.max(0, Math.min(255, alpha)) << 24;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static int mix(int from, int to, float factor) {
        float f = Math.max(0.0f, Math.min(1.0f, factor));
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        return MinimapColors.clamp(r) << 16 | MinimapColors.clamp(g) << 8 | MinimapColors.clamp(b);
    }
}
