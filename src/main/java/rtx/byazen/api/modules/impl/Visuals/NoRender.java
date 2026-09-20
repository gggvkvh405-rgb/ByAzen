package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.MultiModeSetting;

public class NoRender
extends Module {
    public static final String FIRE = "\u041e\u0433\u043e\u043d\u044c";
    public static final String ENTITY_FIRE = "\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044f\u0445";
    public static final String CAMERA_SHAKE = "\u0422\u0440\u044f\u0441\u043a\u0430 \u043a\u0430\u043c\u0435\u0440\u044b";
    public static final String VIEW_BOBBING = "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435 \u043a\u0430\u043c\u0435\u0440\u044b";
    public static final String FOV_DYNAMIC = "\u0414\u0438\u043d\u0430\u043c\u0438\u043a\u0430 \u043f\u043e\u043b\u044f \u0437\u0440\u0435\u043d\u0438\u044f";
    public static final String SCOREBOARD = "\u0422\u0430\u0431\u043b\u0438\u0446\u0430 \u0441\u0447\u0451\u0442\u0430";
    public static final String BOSS_BAR = "\u041f\u043e\u043b\u043e\u0441\u0430 \u0431\u043e\u0441\u0441\u0430";
    public static final String GLOW = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435";
    private static NoRender instance;
    private final MultiModeSetting elements = this.register(new MultiModeSetting("\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b", "\u041a\u0430\u043a\u0438\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u044b \u0440\u0435\u043d\u0434\u0435\u0440\u0430 \u0441\u043a\u0440\u044b\u0432\u0430\u0442\u044c.", new String[]{"\u041e\u0433\u043e\u043d\u044c", "\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044f\u0445", "\u0422\u0440\u044f\u0441\u043a\u0430 \u043a\u0430\u043c\u0435\u0440\u044b", "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435 \u043a\u0430\u043c\u0435\u0440\u044b", "\u0414\u0438\u043d\u0430\u043c\u0438\u043a\u0430 \u043f\u043e\u043b\u044f \u0437\u0440\u0435\u043d\u0438\u044f", "\u0422\u0430\u0431\u043b\u0438\u0446\u0430 \u0441\u0447\u0451\u0442\u0430", "\u041f\u043e\u043b\u043e\u0441\u0430 \u0431\u043e\u0441\u0441\u0430", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435"}, "\u041e\u0433\u043e\u043d\u044c", "\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044f\u0445", "\u0422\u0440\u044f\u0441\u043a\u0430 \u043a\u0430\u043c\u0435\u0440\u044b"));

    public NoRender() {
        super("No Render", "\u0421\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0432\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b.", Category.VISUALS);
        instance = this;
    }

    public static NoRender getInstance() {
        return instance;
    }

    public static boolean isActive(String string) {
        if (instance == null || !instance.isEnabled()) {
            return false;
        }
        instance.ensureSelectedDefaults();
        return NoRender.instance.elements.isSelected(string);
    }

    private void ensureSelectedDefaults() {
        if (this.elements.getSelected().isEmpty()) {
            this.elements.selected(FIRE, ENTITY_FIRE, CAMERA_SHAKE);
        }
    }
}

