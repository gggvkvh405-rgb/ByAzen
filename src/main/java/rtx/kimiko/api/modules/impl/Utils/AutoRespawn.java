package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import rtx.kimiko.api.events.impl.game.DeathScreenEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.SelectSetting;

public final class AutoRespawn
extends Module {
    public final SelectSetting mode = new SelectSetting("\u0420\u0435\u0436\u0438\u043c", "\u0420\u0435\u0436\u0438\u043c \u0432\u043e\u0437\u0440\u043e\u0436\u0434\u0435\u043d\u0438\u044f.").value("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442");

    public AutoRespawn() {
        super("Auto Respawn", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u043e\u0437\u0440\u043e\u0436\u0434\u0430\u0435\u0442 \u043f\u043e\u0441\u043b\u0435 \u0441\u043c\u0435\u0440\u0442\u0438.", Category.UTILS);
        this.register(this.mode);
    }

    @EventHandler
    public void onDeathScreen(DeathScreenEvent deathScreenEvent) {
        if (!this.mode.is("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442")) {
            return;
        }
        if (this.mc.player == null) {
            return;
        }
        this.mc.player.requestRespawn();
        this.mc.setScreenAndRender(null);
    }
}

