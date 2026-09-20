package rtx.kimiko.api.modules.settings.impl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import rtx.kimiko.api.modules.settings.Setting;

public class SelectSetting
extends Setting {
    private List<String> options = Collections.emptyList();
    private String selected = "";
    private String defaultSelected = "";
    private boolean defaultCaptured;

    public SelectSetting(String string, String string2) {
        super(string, string2);
    }

    public SelectSetting value(String ... stringArray) {
        this.options = Arrays.asList(stringArray);
        String string = this.selected = this.options.isEmpty() ? "" : this.options.get(0);
        if (!this.defaultCaptured) {
            this.defaultSelected = this.selected;
        }
        return this;
    }

    public String getValue() {
        return this.selected;
    }

    public boolean is(String string) {
        return this.selected.equals(string);
    }

    public SelectSetting visible(Supplier<Boolean> supplier) {
        this.setVisibilityCondition(supplier);
        return this;
    }

    private void captureDefault() {
        if (!this.defaultCaptured) {
            this.defaultSelected = this.selected;
            this.defaultCaptured = true;
        }
    }

    public SelectSetting setSelected(String string) {
        return this.selected(string);
    }

    public String getSelected() {
        return this.selected;
    }

    public String getDefaultSelected() {
        return this.defaultSelected;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(this.options);
    }

    public boolean isSelected(String string) {
        return this.is(string);
    }

    public SelectSetting selected(String string) {
        if (this.options.contains(string)) {
            boolean bl = !this.selected.equals(string);
            this.selected = string;
            this.captureDefault();
            if (bl) {
                this.notifyChanged();
            }
        }
        return this;
    }
}

