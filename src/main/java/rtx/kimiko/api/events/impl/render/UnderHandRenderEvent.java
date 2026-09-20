package rtx.kimiko.api.events.impl.render;
import rtx.kimiko.api.events.Event;
import rtx.kimiko.utils.render.underhand.UnderHand2D;

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

