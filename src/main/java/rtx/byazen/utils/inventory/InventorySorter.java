package rtx.byazen.utils.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Сортировка инвентаря (идея №69 из IDEAS.md).
 * <p>
 * Раскладывает предметы по категориям, имени или количеству теми же действиями, что и игрок руками:
 * берёт предмет, меняет местами и кладёт обратно. Очередь выполняется постепенно — один-два обмена
 * за тик, чтобы сервер успевал подтверждать действия и ничего не рассинхронизировалось.
 */
public final class InventorySorter {

    public static final String MODE_CATEGORY = "Категории";
    public static final String MODE_NAME = "Имя";
    public static final String MODE_COUNT = "Количество";

    private static final int FIRST_MAIN_SLOT = 9;
    private static final int LAST_MAIN_SLOT = 35;
    private static final int FIRST_HOTBAR_SLOT = 36;
    private static final int LAST_HOTBAR_SLOT = 44;

    private static final List<Click> queue = new ArrayList<Click>();
    private static final List<Integer> model = new ArrayList<Integer>();
    private static int totalSteps;
    private static int doneSteps;

    private InventorySorter() {
    }

    public static boolean isActive() {
        return !queue.isEmpty();
    }

    public static float progress() {
        return totalSteps <= 0 ? 1.0f : (float)doneSteps / (float)totalSteps;
    }

    public static void cancel() {
        queue.clear();
        model.clear();
        totalSteps = 0;
        doneSteps = 0;
    }

    /** Готовит план обменов для текущего содержимого инвентаря. */
    public static void plan(PlayerScreenHandler handler, String mode, boolean withHotbar) {
        InventorySorter.cancel();
        if (handler == null) {
            return;
        }
        List<Integer> slots = new ArrayList<Integer>();
        for (int i = FIRST_MAIN_SLOT; i <= LAST_MAIN_SLOT; ++i) {
            slots.add(i);
        }
        if (withHotbar) {
            for (int i = FIRST_HOTBAR_SLOT; i <= LAST_HOTBAR_SLOT; ++i) {
                slots.add(i);
            }
        }
        ArrayList<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < slots.size(); ++i) {
            order.add(i);
        }
        final List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < slots.size(); ++i) {
            stacks.add(InventorySorter.stackAt(handler, slots.get(i)));
        }
        order.sort((left, right) -> InventorySorter.compare(stacks.get(left), stacks.get(right), mode));
        // модель текущего состояния: индекс позиции -> исходный индекс предмета
        ArrayList<Integer> arrangement = new ArrayList<Integer>();
        for (int i = 0; i < slots.size(); ++i) {
            arrangement.add(i);
        }
        for (int position = 0; position < order.size(); ++position) {
            int wanted = order.get(position);
            int currentPosition = arrangement.indexOf(wanted);
            if (currentPosition == position || currentPosition < 0) {
                continue;
            }
            InventorySorter.addSwap(handler, slots.get(position), slots.get(currentPosition));
            int tmp = arrangement.get(position);
            arrangement.set(position, arrangement.get(currentPosition));
            arrangement.set(currentPosition, tmp);
        }
        totalSteps = queue.size();
        doneSteps = 0;
    }

    private static ItemStack stackAt(PlayerScreenHandler handler, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= handler.slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = handler.slots.get(slotIndex);
        ItemStack stack = slot == null ? ItemStack.EMPTY : slot.getStack();
        return stack == null ? ItemStack.EMPTY : stack;
    }

    private static void addSwap(PlayerScreenHandler handler, int first, int second) {
        // обмен местами через три обычных клика мышью
        queue.add(new Click(first, 0));
        queue.add(new Click(second, 0));
        queue.add(new Click(first, 0));
    }

    private static int compare(ItemStack left, ItemStack right, String mode) {
        boolean leftEmpty = left == null || left.isEmpty();
        boolean rightEmpty = right == null || right.isEmpty();
        if (leftEmpty && rightEmpty) {
            return 0;
        }
        if (leftEmpty) {
            return 1;
        }
        if (rightEmpty) {
            return -1;
        }
        if (MODE_COUNT.equals(mode)) {
            int byCount = Integer.compare(right.getCount(), left.getCount());
            return byCount != 0 ? byCount : InventorySorter.nameOf(left).compareTo(InventorySorter.nameOf(right));
        }
        if (MODE_NAME.equals(mode)) {
            return InventorySorter.nameOf(left).compareTo(InventorySorter.nameOf(right));
        }
        int byGroup = Integer.compare(InventorySorter.groupOf(left), InventorySorter.groupOf(right));
        if (byGroup != 0) {
            return byGroup;
        }
        int byCount = Integer.compare(right.getCount(), left.getCount());
        return byCount != 0 ? byCount : InventorySorter.nameOf(left).compareTo(InventorySorter.nameOf(right));
    }

    private static String nameOf(ItemStack stack) {
        String id = Registries.ITEM.getId(stack.getItem()).getPath();
        return id == null ? "" : id;
    }

    /** Группа предмета для сортировки по категориям: чем меньше число, тем выше в списке. */
    public static int groupOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 99;
        }
        String id = InventorySorter.nameOf(stack);
        if (InventorySorter.containsAny(id, "sword", "axe", "bow", "crossbow", "trident", "mace", "arrow", "shield")) {
            return 0;
        }
        if (InventorySorter.containsAny(id, "pickaxe", "shovel", "hoe", "shears", "flint_and_steel", "fishing_rod", "bucket", "elytra")) {
            return 1;
        }
        if (InventorySorter.containsAny(id, "helmet", "chestplate", "leggings", "boots", "horse_armor")) {
            return 2;
        }
        if (InventorySorter.containsAny(id, "potion", "apple", "bread", "cooked", "beef", "porkchop", "chicken", "mutton", "cod", "salmon", "carrot", "potato", "beetroot", "melon", "berry", "kelp", "cookie", "cake", "stew", "honey", "stew", "mushroom_stew", "suspicious")) {
            return 3;
        }
        if (InventorySorter.containsAny(id, "torch", "lantern", "campfire", "candle")) {
            return 4;
        }
        if (InventorySorter.containsAny(id, "redstone", "repeater", "comparator", "observer", "piston", "hopper", "dropper", "dispenser", "lever", "button", "pressure_plate", "tripwire")) {
            return 5;
        }
        if (InventorySorter.containsAny(id, "_log", "_planks", "_wood", "stone", "dirt", "sand", "gravel", "glass", "brick", "concrete", "terracotta", "wool", "cobble", "deepslate", "netherrack", "end_stone", "obsidian", "stairs", "slab", "fence", "wall", "door", "trapdoor")) {
            return 6;
        }
        if (InventorySorter.containsAny(id, "shulker_box", "chest", "barrel", "ender_chest", "crafting_table", "furnace", "anvil", "enchanting", "brewing", "cauldron", "grindstone", "loom", "stonecutter", "smithing", "cartography", "fletching", "composter", "lectern", "beacon")) {
            return 7;
        }
        if (InventorySorter.containsAny(id, "diamond", "emerald", "gold_ingot", "iron_ingot", "netherite", "coal", "lapis", "quartz", "amethyst", "copper_ingot", "raw_", "_ingot", "_nugget", "scrap")) {
            return 8;
        }
        return 9;
    }

    private static boolean containsAny(String id, String... tokens) {
        for (String token : tokens) {
            if (id.contains(token)) {
                return true;
            }
        }
        return false;
    }

    /** Выполняет часть очереди: два клика за вызов. */
    public static void tick() {
        if (queue.isEmpty()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client == null ? null : client.player;
        if (player == null || client.interactionManager == null) {
            InventorySorter.cancel();
            return;
        }
        if (!(player.currentScreenHandler instanceof PlayerScreenHandler)) {
            InventorySorter.cancel();
            return;
        }
        int steps = 0;
        while (!queue.isEmpty() && steps < 2) {
            Click click = queue.remove(0);
            client.interactionManager.clickSlot(player.currentScreenHandler.syncId, click.slot, click.button, SlotActionType.PICKUP, (PlayerEntity)(Object)player);
            ++doneSteps;
            ++steps;
        }
        if (queue.isEmpty()) {
            InventorySorter.cancel();
        }
    }

    /** Один клик очереди. */
    private static final class Click {

        private final int slot;
        private final int button;

        private Click(int slot, int button) {
            this.slot = slot;
            this.button = button;
        }
    }
}
