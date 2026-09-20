package rtx.kimiko.api.modules.impl.Interface;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.utils.key.KeyBind;

public final class ClickGui
extends Module {
    public ClickGui() {
        super("ClickGui", "\u041e\u0442\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u043a\u043b\u0438\u043a-\u043c\u0435\u043d\u044e \u043a\u043b\u0438\u0435\u043d\u0442\u0430.", Category.DISPLAY);
        this.setBind(KeyBind.keyboard(344));
    }
}

