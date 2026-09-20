package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.SliderSetting;

public final class AutoCommands
extends Module {
    private static final String FIX_ALL = "/fix all";
    private static final String HEAL = "/heal";
    private static final long STAGGER_MS = 10000L;
    private final MultiSelectSetting commands = this.register(new MultiSelectSetting("\u041a\u043e\u043c\u0430\u043d\u0434\u044b", "\u041a\u0430\u043a\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438.").value("/fix all", "/heal").selected("/fix all", "/heal"));
    private final SliderSetting cooldown = this.register(new SliderSetting("\u041a\u0443\u043b\u0434\u0430\u0443\u043d", "\u041a\u0430\u043a \u0447\u0430\u0441\u0442\u043e \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u0442\u044c \u043a\u0430\u0436\u0434\u0443\u044e \u043a\u043e\u043c\u0430\u043d\u0434\u0443 (\u0441\u0435\u043a\u0443\u043d\u0434\u044b).").setValue(60.0f).range(60, 360).increment(1));
    private List<String> scheduled = Collections.emptyList();
    private long[] nextAt = new long[0];

    public AutoCommands() {
        super("Auto Commands", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043f\u043e \u043a\u0443\u043b\u0434\u0430\u0443\u043d\u0443, \u043f\u043e \u043e\u0447\u0435\u0440\u0435\u0434\u0438.", Category.UTILS);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.player.networkHandler == null || this.mc.world == null || this.mc.currentScreen != null) {
            return;
        }
        long l = System.currentTimeMillis();
        ArrayList<String> arrayList = new ArrayList<String>(this.commands.getSelected());
        if (!arrayList.equals(this.scheduled)) {
            this.scheduled = arrayList;
            this.nextAt = new long[arrayList.size()];
            for (int i = 0; i < arrayList.size(); ++i) {
                this.nextAt[i] = l + (long)i * 10000L;
            }
        }
        if (this.scheduled.isEmpty()) {
            return;
        }
        long l2 = (long)this.cooldown.getInt() * 1000L;
        for (int i = 0; i < this.scheduled.size(); ++i) {
            if (l < this.nextAt[i]) continue;
            this.send(this.scheduled.get(i));
            this.nextAt[i] = l + l2;
        }
    }

    @Override
    protected void onEnable() {
        this.scheduled = Collections.emptyList();
        this.nextAt = new long[0];
    }

    private void send(String string) {
        String string2 = string.startsWith("/") ? string.substring(1) : string;
        if (!(string2 = string2.trim()).isEmpty()) {
            this.mc.player.networkHandler.sendChatCommand(string2);
        }
    }
}

