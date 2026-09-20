package rtx.kimiko.api.events.impl.game;
import net.minecraft.client.gui.screen.Screen;
import rtx.kimiko.api.events.CancellableEvent;

public final class CloseScreenEvent
extends CancellableEvent {
    private final Screen screen;

    public CloseScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return this.screen;
    }
}

