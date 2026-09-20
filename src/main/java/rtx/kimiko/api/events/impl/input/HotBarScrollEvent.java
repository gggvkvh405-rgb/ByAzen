package rtx.kimiko.api.events.impl.input;
import rtx.kimiko.api.events.CancellableEvent;

public final class HotBarScrollEvent
extends CancellableEvent {
    private double vertical;

    public HotBarScrollEvent(double d) {
        this.vertical = d;
    }

    public double getVertical() {
        return this.vertical;
    }

    public void setVertical(double d) {
        this.vertical = d;
    }
}

