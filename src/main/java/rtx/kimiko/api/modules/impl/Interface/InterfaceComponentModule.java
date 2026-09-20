package rtx.kimiko.api.modules.impl.Interface;

import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;

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
