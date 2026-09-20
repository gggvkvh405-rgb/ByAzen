package rtx.kimiko.api.events.impl.module;
import rtx.kimiko.api.events.Event;
import rtx.kimiko.api.modules.Module;

public final class ModuleToggleEvent
extends Event {
    private final Module module;
    private final boolean enabled;

    public ModuleToggleEvent(Module module, boolean bl) {
        this.module = module;
        this.enabled = bl;
    }

    public Module getModule() {
        return this.module;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

