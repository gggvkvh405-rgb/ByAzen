package rtx.kimiko.api.modules;

public enum Category {
    VISUALS("Visuals"),
    DISPLAY("Display"),
    UTILS("Utils"),
    EVENTS("Events"),
    THEMES("Themes");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return this.displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
