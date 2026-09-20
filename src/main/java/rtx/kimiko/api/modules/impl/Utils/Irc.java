package rtx.kimiko.api.modules.impl.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rtx.kimiko.api.chat.irc.IrcClient;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;

public final class Irc
extends Module {
    public Irc() {
        super("IRC", "\u041e\u0431\u0449\u0438\u0439 \u0447\u0430\u0442 \u043a\u043b\u0438\u0435\u043d\u0442\u0430 \u0447\u0435\u0440\u0435\u0437 \u043b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0439 \u043c\u043e\u0441\u0442. \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 .irc \u0434\u043b\u044f \u043e\u0431\u0449\u0435\u043d\u0438\u044f.", Category.UTILS);
    }

    public static Irc getInstance() {
        return ModuleManager.get().get(Irc.class);
    }

    private void message(String string) {
        if (this.mc.player != null) {
            this.mc.player.sendMessage((Text)IrcClient.brand().append((Text)Text.literal((String)string).formatted(Formatting.GRAY)), false);
        }
    }

    @Override
    protected void onDisable() {
        IrcClient.INSTANCE.stop();
        this.message("\u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u043e");
    }

    @Override
    protected void onEnable() {
        IrcClient.INSTANCE.start();
        this.message("\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435...");
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }
}

