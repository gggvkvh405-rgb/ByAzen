package rtx.byazen.api.events.impl.render;
import rtx.byazen.api.events.Event;
import rtx.byazen.utils.render.underhand.UnderHand2D;

public final class UnderHandRenderEvent
extends Event {
    private final UnderHand2D ctx;

    public UnderHandRenderEvent(UnderHand2D underHand2D) {
        this.ctx = underHand2D;
    }

    public UnderHand2D ctx() {
        return this.ctx;
    }
}

