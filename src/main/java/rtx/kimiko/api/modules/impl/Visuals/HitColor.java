package rtx.kimiko.api.modules.impl.Visuals;
import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;

public final class HitColor
extends Module {
    private static HitColor instance;
    private static final Map<LivingEntityRenderState, Integer> renderTints;
    private final ColorSetting color = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0443\u0440\u043e\u043d\u0430.", new Color(255, 110, 110, 255)));

    public HitColor() {
        super("Hit Color", "\u041c\u0435\u043d\u044f\u0435\u0442 \u0446\u0432\u0435\u0442 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0438 \u0443\u0440\u043e\u043d\u0430.", Category.VISUALS);
        instance = this;
    }

    static {
        renderTints = Collections.synchronizedMap(new WeakHashMap());
    }

    private static boolean isActive() {
        return instance != null && instance.isEnabled();
    }

    public static int color() {
        return instance != null ? HitColor.instance.color.getColor() : -1;
    }

    public static boolean shouldTint(LivingEntity livingEntity) {
        return HitColor.isActive() && livingEntity instanceof PlayerEntity && livingEntity.hurtTime > 0;
    }

    public static Integer tintFor(LivingEntityRenderState livingEntityRenderState) {
        return HitColor.isActive() ? renderTints.get(livingEntityRenderState) : null;
    }

    @Override
    protected void onDisable() {
        renderTints.clear();
    }

    public static void captureTint(LivingEntityRenderState livingEntityRenderState, LivingEntity livingEntity) {
        if (!HitColor.isActive()) {
            return;
        }
        if (HitColor.shouldTint(livingEntity)) {
            renderTints.put(livingEntityRenderState, HitColor.color());
        } else {
            renderTints.remove(livingEntityRenderState);
        }
    }
}

