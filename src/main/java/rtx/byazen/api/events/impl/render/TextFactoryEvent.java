package rtx.byazen.api.events.impl.render;

import rtx.byazen.api.events.Event;

public final class TextFactoryEvent extends Event {
    private String text;

    public TextFactoryEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
