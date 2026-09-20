package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;

public final class SelfTag
extends Module {
    public SelfTag() {
        super("Self Tag", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u0443\u044e \u0442\u0430\u0431\u043b\u0438\u0447\u043a\u0443 \u0441 \u043d\u0438\u043a\u043e\u043c \u043d\u0430\u0434 \u0432\u0430\u043c\u0438 (\u043e\u0442 3-\u0433\u043e \u043b\u0438\u0446\u0430).", Category.VISUALS);
    }

    public static boolean active() {
        SelfTag selfTag = ModuleManager.get().get(SelfTag.class);
        return selfTag != null && selfTag.isEnabled();
    }
}

