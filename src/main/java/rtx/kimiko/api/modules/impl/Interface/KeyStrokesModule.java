package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.events.EventHandler;
import java.util.ArrayDeque;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent;
import rtx.kimiko.api.events.impl.input.MouseButtonEvent.Action;
import rtx.kimiko.api.modules.impl.Interface.InterfaceComponentModule;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;

public final class KeyStrokesModule
extends InterfaceComponentModule {
    private static final long CPS_WINDOW_MS = 1000L;
    public final BooleanSetting showMouse = this.register(new BooleanSetting("\u041a\u043d\u043e\u043f\u043a\u0438 \u043c\u044b\u0448\u0438", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u041b\u041a\u041c/\u041f\u041a\u041c.", true));
    public final BooleanSetting showCps = this.register(new BooleanSetting("CPS", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043a\u043b\u0438\u043a\u0438 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443 \u043d\u0430 \u043a\u043d\u043e\u043f\u043a\u0430\u0445 \u043c\u044b\u0448\u0438.", true));
    public final BooleanSetting showSpace = this.register(new BooleanSetting("\u041f\u0440\u043e\u0431\u0435\u043b", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043f\u043e\u043b\u043e\u0441\u043a\u0443 \u043f\u0440\u043e\u0431\u0435\u043b\u0430.", true));
    private final ArrayDeque<Long> leftClicks = new ArrayDeque();
    private final ArrayDeque<Long> rightClicks = new ArrayDeque();

    public KeyStrokesModule() {
        super("KeyStrokes", "\u041d\u0430\u0436\u0430\u0442\u0438\u044f \u043a\u043b\u0430\u0432\u0438\u0448 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f \u0438 \u043a\u043b\u0438\u043a\u043e\u0432: \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u0435\u043c\u044b\u0439 \u0431\u043b\u043e\u043a.");
        this.showCps.visibleWhen(this.showMouse::getValue);
    }

    public int leftCps() {
        return KeyStrokesModule.prune(this.leftClicks);
    }

    public int rightCps() {
        return KeyStrokesModule.prune(this.rightClicks);
    }

    private static int prune(ArrayDeque<Long> arrayDeque) {
        long l = System.currentTimeMillis() - 1000L;
        while (!arrayDeque.isEmpty() && arrayDeque.peekFirst() < l) {
            arrayDeque.pollFirst();
        }
        return arrayDeque.size();
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.action != MouseButtonEvent.Action.PRESS || this.mc.currentScreen != null) {
            return;
        }
        long l = System.currentTimeMillis();
        if (mouseButtonEvent.button == 0) {
            this.leftClicks.addLast(l);
        } else if (mouseButtonEvent.button == 1) {
            this.rightClicks.addLast(l);
        }
    }
}

