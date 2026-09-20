package rtx.kimiko.api.drags.components;
import java.util.ArrayList;
import java.util.List;
import rtx.kimiko.api.drags.DragSystem;
import rtx.kimiko.api.drags.components.ListHudComp;
import rtx.kimiko.api.drags.components.ListHudComp.Row;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.ModuleManager;
import rtx.kimiko.api.modules.impl.Interface.ClickGui;
import rtx.kimiko.api.modules.impl.Interface.HotKeysModule;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.key.KeyBind;
import rtx.kimiko.utils.math.MathUtils;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class HotKeysComp
extends ListHudComp {
    private static final String ICON_FONT = "kimiko";
    private static final int ICON_COLOR = -3355444;
    private static final String EXAMPLE_NAME = "Example bind";
    private static final String EXAMPLE_KEY = "[B]";

    public HotKeysComp() {
        super("hotkeys", "Hotkeys", HotKeysModule.class, 5.0f, 33.0f);
    }

    @Override
    protected String headerIconGlyph() {
        return "u";
    }

    private static ListHudComp.IconDrawer categoryIcon(Category category) {
        if (category == null) {
            return null;
        }
        String string = String.valueOf(HotKeysComp.iconChar(category));
        return (drawContext, f, f2, f3, f4) -> {
            float f5 = Math.max(0.0f, Math.min(1.0f, f4));
            if (f5 <= 0.003921569f) {
                return;
            }
            float[] fArray = Render2D.msdfBounds(ICON_FONT, string, f3);
            if (fArray == null || fArray.length < 4) {
                return;
            }
            float f6 = f + (f3 - (fArray[2] - fArray[0])) * 0.5f - fArray[0];
            float f7 = f2 + (f3 - (fArray[3] - fArray[1])) * 0.5f - fArray[1];
            Render2D.msdfText(ICON_FONT, string, f6, f7, f3, ColorUtil.multAlpha(-3355444, f5));
        };
    }

    @Override
    protected List<ListHudComp.Row> collectRows() {
        ArrayList<ListHudComp.Row> arrayList = new ArrayList<ListHudComp.Row>();
        for (Module module : ModuleManager.get().getAll()) {
            if (!HotKeysComp.isActiveBind(module)) continue;
            arrayList.add(new ListHudComp.Row((Object)module, module.getName(), HotKeysComp.keyText(module.getBind()), HotKeysComp.categoryIcon(module.getCategory())));
        }
        if (arrayList.isEmpty() && DragSystem.get().isDragModeActive()) {
            arrayList.add(new ListHudComp.Row((Object)"preview", EXAMPLE_NAME, EXAMPLE_KEY, HotKeysComp.categoryIcon(Category.VISUALS)));
        }
        return arrayList;
    }

    @Override
    protected float iconSlotSize() {
        return 6.0f;
    }

    private static boolean isActiveBind(Module module) {
        if (module == null) {
            return false;
        }
        if (module instanceof ClickGui) {
            return false;
        }
        KeyBind keyBind = module.getBind();
        return keyBind != null && keyBind.isBound() && module.isEnabled();
    }

    private static char iconChar(Category category) {
        if (category == null) return 'p';
        return switch (category) {
            case VISUALS -> 'r';
            case DISPLAY -> 'i';
            case UTILS -> 'j';
            case EVENTS -> 'p';
            case THEMES -> 'B';
        };
    }

    private static String keyText(KeyBind keyBind) {
        return "[" + MathUtils.shortBind((KeyBind)keyBind) + "]";
    }
}

