package rtx.byazen.api.chat.irc;
import java.util.Locale;
import net.minecraft.text.Text;
import rtx.byazen.utils.string.chat.helper.TextHelper;

public enum IrcPrefix {
    NONE("none", "", "", false),
    PRISM("prism", "\u041f\u0440\u0438\u0437\u043c\u0430", "astolfo", false),
    VECTOR("vector", "\u0412\u0435\u043a\u0442\u043e\u0440", "red_blue", false),
    SUNSET("sunset", "\u0417\u0430\u043a\u0430\u0442", "orange_magenta", false),
    MAGMA("magma", "\u041c\u0430\u0433\u043c\u0430", "red_orange", false),
    GARNET("garnet", "\u0413\u0440\u0430\u043d\u0430\u0442", "purple_bright_pink", false),
    RIME("rime", "\u0418\u043d\u0435\u0439", "turquoise_blue", false),
    PHOENIX("phoenix", "\u0424\u0435\u043d\u0438\u043a\u0441", "bright_red", false),
    MALACHITE("malachite", "\u041c\u0430\u043b\u0430\u0445\u0438\u0442", "dark_green_bright_green", false),
    NOCTURNE("nocturne", "\u041d\u043e\u043a\u0442\u044e\u0440\u043d", "black_light_purple", false),
    GRIN("grin", "(\uff61\u25d5\u203f\u203f\u25d5\uff61)", "yellow_cyan", true),
    QUARTZ("quartz", "\u041a\u0432\u0430\u0440\u0446", "cyan_orange_fade", false),
    AMBER("amber", "\u042f\u043d\u0442\u0430\u0440\u044c", "orange_white", false),
    ETHER("ether", "\u042d\u0444\u0438\u0440", "blue_green_fade", false),
    NEBULA("nebula", "\u041d\u0435\u0431\u0443\u043b\u0430", "purple_red_fade", false),
    CHIMERA("chimera", "\u0425\u0438\u043c\u0435\u0440\u0430", "green_purple", false),
    ECLIPSE("eclipse", "\u0417\u0430\u0442\u043c\u0435\u043d\u0438\u0435", "white_black", false),
    EMBER("ember", "\u0423\u0433\u043e\u043b\u0451\u043a", "dark_red_bright_red", false),
    CARDINAL("cardinal", "\u041a\u0430\u0440\u0434\u0438\u043d\u0430\u043b", "dark_red", false),
    SAKURA("sakura", "\u0421\u0430\u043a\u0443\u0440\u0430", "pink_dark_pink", false),
    BLAZE("blaze", "\u041f\u043b\u0430\u043c\u044f", "red_white", false);

    private final String id;
    private final String display;
    private final String gradient;
    private final boolean bold;

    private IrcPrefix(String id, String display, String gradient, boolean bold) {
        this.id = id;
        this.display = display;
        this.gradient = gradient;
        this.bold = bold;
    }

    public Text component() {
        if (this.isNone()) {
            return Text.empty();
        }
        return TextHelper.applyPredefinedGradient(this.display + " ", this.gradient, this.bold);
    }

    public String id() {
        return this.id;
    }

    public String display() {
        return this.display;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public static IrcPrefix fromId(String string) {
        if (string == null || string.isBlank()) {
            return NONE;
        }
        String string2 = string.trim().toLowerCase(Locale.ROOT);
        for (IrcPrefix ircPrefix : IrcPrefix.values()) {
            if (!ircPrefix.id.equals(string2) && !ircPrefix.display.toLowerCase(Locale.ROOT).equals(string2)) continue;
            return ircPrefix;
        }
        return NONE;
    }

    public static String[] ids() {
        IrcPrefix[] ircPrefixArray = IrcPrefix.values();
        String[] stringArray = new String[ircPrefixArray.length];
        for (int i = 0; i < ircPrefixArray.length; ++i) {
            stringArray[i] = ircPrefixArray[i].id;
        }
        return stringArray;
    }
}

