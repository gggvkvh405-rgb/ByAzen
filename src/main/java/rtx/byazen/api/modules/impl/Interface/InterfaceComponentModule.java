package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;

public abstract class InterfaceComponentModule
extends Module {
    protected InterfaceComponentModule(String string, String string2) {
        super(string, string2, Category.DISPLAY);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }
}
