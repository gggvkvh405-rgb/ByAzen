package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Калькулятор координат Nether ↔ Overworld (идея №107 из IDEAS.md).
 * <p>
 * {@code coords} — координаты игрока и их эквивалент в другом измерении,
 * {@code coords <x> <y> <z>} — пересчёт указанных координат, {@code coords nether|overworld} — направление.
 */
public final class CoordsCommand
extends Command {

    public CoordsCommand() {
        super("coords", "Калькулятор координат Overworld ↔ Nether", "coord", "calc");
    }

    @Override
    public void execute(String label, String[] args) {
        int x;
        int y;
        int z;
        String world;
        boolean toNether;
        if (args.length >= 3) {
            try {
                x = (int) Double.parseDouble(args[0].replace(',', '.'));
                y = (int) Double.parseDouble(args[1].replace(',', '.'));
                z = (int) Double.parseDouble(args[2].replace(',', '.'));
            }
            catch (NumberFormatException numberFormatException) {
                this.logDirect("Не могу разобрать координаты. Пример: coords 120 64 -340", Formatting.RED);
                return;
            }
            boolean netherKeyword = args.length > 3 && CoordsCommand.isNetherWord(args[3]);
            boolean overworldKeyword = args.length > 3 && CoordsCommand.isOverworldWord(args[3]);
            world = this.currentWorld();
            toNether = netherKeyword || (!overworldKeyword && !this.isNetherWorld(world));
        }
        else {
            if (this.mc.player == null) {
                this.logDirect("Координаты игрока недоступны: нет мира.", Formatting.RED);
                return;
            }
            x = (int) Math.round(this.mc.player.getX());
            y = (int) Math.round(this.mc.player.getY());
            z = (int) Math.round(this.mc.player.getZ());
            world = this.currentWorld();
            toNether = !this.isNetherWorld(world);
        }
        if (!this.mc.isInSingleplayer() && args.length >= 3) {
            this.logDirect("Подсказка: коэффициент 8 работает на серверах, где Nether связан с Overworld.", Formatting.GRAY);
        }
        double factor = toNether ? 0.125 : 8.0;
        int targetX = (int) Math.round((double) x * factor);
        int targetZ = (int) Math.round((double) z * factor);
        String from = toNether ? "Overworld" : "Nether";
        String to = toNether ? "Nether" : "Overworld";
        String source = x + " " + y + " " + z;
        String target = targetX + " " + y + " " + targetZ;

        ChatMessage.brandmessage("Координаты " + from + " → " + to + ":");
        ChatMessage.brandmessage(net.minecraft.text.Text.literal("  " + source + "  →  " + target)
                .styled(style -> style.withClickEvent(new net.minecraft.text.ClickEvent.CopyToClipboard(target))));
        if (this.mc.player != null && args.length >= 3) {
            double dx = (double) x - this.mc.player.getX();
            double dz = (double) z - this.mc.player.getZ();
            ChatMessage.brandmessage("До указанной точки по прямой: " + Math.round(Math.sqrt(dx * dx + dz * dz)) + " блоков.");
        }
    }

    private String currentWorld() {
        if (this.mc.world == null) {
            return "";
        }
        return this.mc.world.getRegistryKey().getValue().getPath().toLowerCase(Locale.ROOT);
    }

    private boolean isNetherWorld(String world) {
        return world.contains("nether") || world.contains("hell") || world.contains("ad");
    }

    private static boolean isNetherWord(String word) {
        String value = word.toLowerCase(Locale.ROOT);
        return value.equals("nether") || value.equals("ад") || value.equals("n");
    }

    private static boolean isOverworldWord(String word) {
        String value = word.toLowerCase(Locale.ROOT);
        return value.equals("overworld") || value.equals("овер") || value.equals("верх") || value.equals("o");
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Пересчитывает координаты между Overworld и Nether (коэффициент 8).",
                "Нажмите на результат в чате, чтобы скопировать координаты.",
                "", "Использование:",
                "> coords",
                "> coords 100 64 -200",
                "> coords 100 64 -200 nether");
    }

    @Override
    public Stream<String> tabComplete(String label, String[] args) {
        if (args.length == 4) {
            return Stream.of("nether", "overworld").filter(value -> value.startsWith(args[3].toLowerCase(Locale.ROOT)));
        }
        return Stream.empty();
    }
}
