package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Яркость и ночное зрение (идея №73 из IDEAS.md).
 * <p>
 * Яркость мира поднимается плавно, отдельным ползунком, не трогая ванильную настройку гаммы:
 * можно поставить «видно в шахте», а на скриншотах останется привычная картинка, если выбрать
 * «плавный переход» и умеренную силу. Ничего не выделяется, кроме света: блоки не подсвечиваются.
 */
public final class NightVision
extends Module {

    private static final float MIN_GAMMA = 0.02f;

    public final SeparatorSetting main = this.register(new SeparatorSetting("Яркость"));
    public final SliderSetting strength = this.register(new SliderSetting("Сила яркости", "Насколько светлее делать мир. 1.0 — как ванильная гамма, дальше видно в шахтах.")
            .range(0.5f, 12.0f).increment(0.1f).setValue(4.0f));
    public final BooleanSetting smooth = this.register(new BooleanSetting("Плавный переход", "Яркость включается и выключается мягко, без щелчка.", true));
    public final BooleanSetting respectPotion = this.register(new BooleanSetting("Уважать зелье", "Если на вас зелье ночного зрения — не перебивать его ванильный вид.", true));
    public final SeparatorSetting limitsSeparator = this.register(new SeparatorSetting("Ограничения"));
    public final BooleanSetting nightOnly = this.register(new BooleanSetting("Только ночью", "Поднимать яркость только когда мир погружается в темноту.", false));
    public final BooleanSetting notInNether = this.register(new BooleanSetting("Не в аду и краю", "Оставить ад и край с их привычным светом.", false));
    public final SliderSetting fadeSpeed = this.register(new SliderSetting("Скорость перехода", "Как быстро яркость доходит до нужного значения.").range(0.5f, 6.0f).increment(0.1f).setValue(2.0f).visible(this.smooth::getValue));

    private static NightVision instance;

    private float current;
    private float applied;

    public NightVision() {
        super("Night Vision", "Ярче видно ночью и в шахтах: плавный переход, без правки ванильной гаммы.", Category.VISUALS);
        instance = this;
    }

    /** Модуль яркости, если он создан клиентом. */
    public static NightVision getInstance() {
        NightVision local = instance;
        return local != null ? local : (instance = ModuleManager.get().get(NightVision.class));
    }

    @Override
    protected void onDisable() {
        this.current = 0.0f;
        this.applied = 0.0f;
    }

    /** Текущая яркость для миксина света: -1 означает «не вмешиваться». */
    public static float gammaOverride() {
        NightVision module = NightVision.getInstance();
        if (module == null || !module.isEnabled()) {
            return -1.0f;
        }
        if (module.applied <= 0.001f) {
            return -1.0f;
        }
        return Math.max(MIN_GAMMA, module.applied);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            this.current = 0.0f;
            this.applied = 0.0f;
            return;
        }
        boolean allowed = true;
        if (this.respectPotion.getValue() && client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            allowed = false;
        }
        if (this.nightOnly.getValue() && !NightVision.isDark(client)) {
            allowed = false;
        }
        if (this.notInNether.getValue()) {
            String dimension = client.world.getRegistryKey().getValue().getPath();
            if (dimension.contains("nether") || dimension.contains("the_end")) {
                allowed = false;
            }
        }
        float target = allowed ? this.strength.getValue() : 0.0f;
        if (this.smooth.getValue()) {
            float speed = this.fadeSpeed.getValue();
            float step = 0.06f * speed;
            if (this.current < target) {
                this.current = Math.min(target, this.current + step);
            }
            else if (this.current > target) {
                this.current = Math.max(target, this.current - step);
            }
        }
        else {
            this.current = target;
        }
        this.applied = this.current;
    }

    private static boolean isDark(MinecraftClient client) {
        long time = client.world.getTimeOfDay() % 24000L;
        return time > 12800L || time < 3000L;
    }
}
