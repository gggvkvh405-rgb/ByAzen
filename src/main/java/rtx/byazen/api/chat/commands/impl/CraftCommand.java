package rtx.byazen.api.chat.commands.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Калькулятор крафта (идея №105 из IDEAS.md): считает, сколько базовых ресурсов нужно на N предметов,
 * например «сколько нужно блоков на 5 шалкеров».
 * <p>
 * Использование: {@code craft shulker 5}, {@code craft hopper 64}.
 */
public final class CraftCommand
extends Command {

    private static final Map<String, CraftCommand.Recipe> RECIPES = new LinkedHashMap<String, CraftCommand.Recipe>();

    static {
        CraftCommand.recipe("доски", 4, "planks", "бревно", 1);
        CraftCommand.recipe("палка", 4, "stick", "доски", 2);
        CraftCommand.recipe("сундук", 1, "chest", "доски", 8);
        CraftCommand.recipe("печь", 1, "furnace", "булыжник", 8);
        CraftCommand.recipe("факел", 4, "torch", "палка", 1, "уголь", 1);
        CraftCommand.recipe("верёвка", 3, "string", "паутина", 1);
        CraftCommand.recipe("лестница", 3, "ladder", "палка", 7);
        CraftCommand.recipe("рельсы", 16, "rail", "железный слиток", 6, "палка", 1);
        CraftCommand.recipe("книга", 1, "book", "бумага", 3, "кожа", 1);
        CraftCommand.recipe("книжная полка", 1, "bookshelf", "доски", 6, "книга", 3);
        CraftCommand.recipe("бумага", 3, "paper", "сахарный тростник", 3);
        CraftCommand.recipe("сундук-ловушка", 1, "trapped_chest", "сундук", 1, "натяжной крюк", 1);
        CraftCommand.recipe("воронка", 1, "hopper", "железный слиток", 5, "сундук", 1);
        CraftCommand.recipe("поршень", 1, "piston", "доски", 3, "булыжник", 4, "железный слиток", 1, "редстоун", 1);
        CraftCommand.recipe("липкий поршень", 1, "sticky_piston", "поршень", 1, "слизь", 1);
        CraftCommand.recipe("наблюдатель", 1, "observer", "кварц", 1, "булыжник", 6, "редстоун", 2);
        CraftCommand.recipe("динамит", 1, "tnt", "порох", 5, "песок", 4);
        CraftCommand.recipe("зельеварка", 1, "brewing_stand", "булыжник", 3, "огненный стержень", 1);
        CraftCommand.recipe("наковальня", 1, "anvil", "блок железа", 3, "железный слиток", 4);
        CraftCommand.recipe("блок железа", 1, "iron_block", "железный слиток", 9);
        CraftCommand.recipe("блок алмаза", 1, "diamond_block", "алмаз", 9);
        CraftCommand.recipe("блок золота", 1, "gold_block", "золотой слиток", 9);
        CraftCommand.recipe("блок угля", 1, "coal_block", "уголь", 9);
        CraftCommand.recipe("эндер-сундук", 1, "ender_chest", "обсидиан", 8, "глаз эндера", 1);
        CraftCommand.recipe("шалкер", 1, "shulker_box", "панцирь шалкера", 1, "сундук", 2);
        CraftCommand.recipe("маяк", 1, "beacon", "стекло", 5, "звезда Незера", 1, "обсидиан", 3);
        CraftCommand.recipe("стекло", 1, "glass", "песок", 1);
        CraftCommand.recipe("камень", 1, "stone", "булыжник", 1);
    }

    public CraftCommand() {
        super("craft", "Калькулятор крафта: сколько ресурсов нужно", "recipe");
    }

    private static void recipe(String name, int output, String id, Object ... pairs) {
        ArrayList<CraftCommand.Ingredient> ingredients = new ArrayList<CraftCommand.Ingredient>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            ingredients.add(new CraftCommand.Ingredient((String) pairs[i], (Integer) pairs[i + 1]));
        }
        RECIPES.put(name.toLowerCase(Locale.ROOT), new CraftCommand.Recipe(name, id, output, ingredients));
    }

    private static CraftCommand.Recipe find(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String key = name.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
        CraftCommand.Recipe recipe = RECIPES.get(key);
        if (recipe != null) {
            return recipe;
        }
        for (CraftCommand.Recipe candidate : RECIPES.values()) {
            if (candidate.id.equalsIgnoreCase(key.replace(' ', '_'))) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public void execute(String label, String[] args) {
        if (args.length == 0) {
            this.usage();
            this.logDirect("Доступно: " + String.join(", ", RECIPES.keySet()), Formatting.GRAY);
            return;
        }
        StringBuilder nameBuilder = new StringBuilder();
        int amount = 1;
        int end = args.length;
        try {
            amount = Math.max(1, Integer.parseInt(args[args.length - 1]));
            end = args.length - 1;
        }
        catch (NumberFormatException numberFormatException) {
            amount = 1;
        }
        for (int i = 0; i < end; ++i) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(' ');
            }
            nameBuilder.append(args[i]);
        }
        CraftCommand.Recipe recipe = CraftCommand.find(nameBuilder.toString());
        if (recipe == null) {
            this.logDirect("Не знаю рецепт «" + nameBuilder + "». Напишите craft, чтобы увидеть список.", Formatting.RED);
            return;
        }
        Map<String, Integer> base = new LinkedHashMap<String, Integer>();
        Map<String, Integer> steps = new LinkedHashMap<String, Integer>();
        CraftCommand.expand(recipe, amount, base, steps, 0);

        ChatMessage.brandmessage(recipe.name + " × " + amount + " — план крафта:");
        for (Map.Entry<String, Integer> entry : steps.entrySet()) {
            ChatMessage.brandmessage(net.minecraft.text.Text.literal("  " + entry.getKey() + " — скрафтить " + entry.getValue() + " раз")
                    .formatted(Formatting.GRAY));
        }
        ChatMessage.brandmessage("Итого базовые ресурсы:");
        for (Map.Entry<String, Integer> entry : base.entrySet()) {
            CraftMessage.line("  " + entry.getKey() + " × " + entry.getValue());
        }
    }

    private static void expand(CraftCommand.Recipe recipe, int amount, Map<String, Integer> base, Map<String, Integer> steps, int depth) {
        int crafts = (int) Math.ceil((double) amount / (double) recipe.output);
        steps.merge(recipe.name, crafts, Integer::sum);
        for (CraftCommand.Ingredient ingredient : recipe.ingredients) {
            int needed = ingredient.count * crafts;
            CraftCommand.Recipe nested = depth < 4 ? CraftCommand.find(ingredient.name) : null;
            if (nested == null) {
                base.merge(ingredient.name, needed, Integer::sum);
                continue;
            }
            CraftCommand.expand(nested, needed, base, steps, depth + 1);
        }
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Считает, сколько ресурсов нужно на N предметов.",
                "", "Использование:",
                "> craft shulker 5",
                "> craft hopper 64",
                "> craft — список доступных рецептов");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length == 1) {
            return RECIPES.values().stream().map(recipe -> recipe.id).filter(id -> id.startsWith(args[0].toLowerCase(Locale.ROOT)));
        }
        return Stream.empty();
    }

    private static final class Recipe {
        private final String name;
        private final String id;
        private final int output;
        private final List<CraftCommand.Ingredient> ingredients;

        private Recipe(String name, String id, int output, List<CraftCommand.Ingredient> ingredients) {
            this.name = name;
            this.id = id;
            this.output = output;
            this.ingredients = ingredients;
        }
    }

    private static final class Ingredient {
        private final String name;
        private final int count;

        private Ingredient(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    /** Мелочь, чтобы не тянуть ChatMessage в статический контекст. */
    private static final class CraftMessage {
        private static void line(String text) {
            ChatMessage.brandmessage(net.minecraft.text.Text.literal(text).formatted(Formatting.WHITE));
        }
    }
}
