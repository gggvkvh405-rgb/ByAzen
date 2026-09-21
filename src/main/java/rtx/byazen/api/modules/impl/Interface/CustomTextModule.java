package rtx.byazen.api.modules.impl.Interface;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.TextSetting;

/**
 * Свой текст в HUD с плейсхолдерами. Идея №27 из IDEAS.md.
 * <p>
 * Поддерживаются: %fps% %ping% %x% %y% %z% %coords% %world% %player% %speed% %memory% %time% %date%.
 */
public final class CustomTextModule
extends InterfaceComponentModule {

    public final SeparatorSetting content = this.register(new SeparatorSetting("Текст"));
    public final TextSetting text = this.register(new TextSetting("Строки", "Можно несколько строк (перенос по |). Поддерживаются плейсхолдеры.")
            .setPlaceholder("%fps% FPS · %ping% мс|X: %x% Y: %y% Z: %z%")
            .lengthBounds(0, 220));
    public final SliderSetting size = this.register(new SliderSetting("Размер", "Размер текста в HUD.").range(5.0f, 12.0f).increment(0.2f).setValue(7.2f));
    public final BooleanSetting background = this.register(new BooleanSetting("Фон", "Рисовать скруглённую подложку под текстом.", true));
    public final BooleanSetting shadow = this.register(new BooleanSetting("Тень", "Лёгкая тень для читаемости на любом фоне.", true));

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public CustomTextModule() {
        super("Custom Text", "Свой текст в HUD с плейсхолдерами: %fps%, %ping%, %coords%, %world%, %speed%, %memory%, %time%, %date%.");
        this.text.setText("%fps% FPS · %ping% мс|X: %x% Y: %y% Z: %z%");
    }

    /** Подстановка плейсхолдеров в строку. */
    public static String resolve(String template) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        MinecraftClient client = MinecraftClient.getInstance();
        String result = template;
        result = CustomTextModule.replace(result, "%fps%", client == null ? "0" : Integer.toString(client.getCurrentFps()));
        result = CustomTextModule.replace(result, "%ping%", Integer.toString(CustomTextModule.ping(client)));
        result = CustomTextModule.replace(result, "%player%", CustomTextModule.player(client));
        result = CustomTextModule.replace(result, "%memory%", CustomTextModule.memory());
        result = CustomTextModule.replace(result, "%time%", java.time.LocalTime.now().withNano(0).toString());
        result = CustomTextModule.replace(result, "%date%", java.time.LocalDate.now().toString());
        if (client != null && client.player != null) {
            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();
            double speed = Math.sqrt(client.player.getVelocity().x * client.player.getVelocity().x
                    + client.player.getVelocity().z * client.player.getVelocity().z) * 20.0;
            result = CustomTextModule.replace(result, "%x%", Long.toString(Math.round(x)));
            result = CustomTextModule.replace(result, "%y%", Long.toString(Math.round(y)));
            result = CustomTextModule.replace(result, "%z%", Long.toString(Math.round(z)));
            result = CustomTextModule.replace(result, "%coords%", Math.round(x) + " " + Math.round(y) + " " + Math.round(z));
            result = CustomTextModule.replace(result, "%speed%", String.format(Locale.ROOT, "%.1f", speed));
        }
        if (client != null && client.world != null) {
            String world = client.world.getRegistryKey().getValue().getPath();
            result = CustomTextModule.replace(result, "%world%", world);
        }
        return result;
    }

    private static String replace(String source, String key, String value) {
        return source.contains(key) ? source.replace(key, value) : source;
    }

    private static int ping(MinecraftClient client) {
        try {
            if (client == null || client.player == null || client.getNetworkHandler() == null) {
                return 0;
            }
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            return entry == null ? 0 : Math.max(0, entry.getLatency());
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    private static String player(MinecraftClient client) {
        return client != null && client.getSession() != null && client.getSession().getUsername() != null
                ? client.getSession().getUsername() : "";
    }

    private static String memory() {
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
        return used + " МБ";
    }
}
