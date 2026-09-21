package rtx.byazen.api.modules.impl.Visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexture;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.world.SimpleParticles;

/**
 * Анимации смерти моба (идея №57 из IDEAS.md).
 * <p>
 * Когда моб или игрок умирает, на его месте разлетаются частицы: цветные искры, мягкое облако и
 * короткая вспышка. Никаких резких текстур — только сглаженные спрайты с прозрачностью.
 */
public final class DeathEffects
extends Module {

    private static final String WHO_MOBS = "Мобы";
    private static final String WHO_ANIMALS = "Животные";
    private static final String WHO_PLAYERS = "Игроки";
    private static final String TEXTURE_RANDOM = "Случайно";

    public final SeparatorSetting main = this.register(new SeparatorSetting("Разлёт"));
    public final SliderSetting count = this.register(new SliderSetting("Количество", "Сколько частиц разлетается при смерти.").range(4.0f, 60.0f).increment(1.0f).setValue(20.0f));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Размер частиц.").range(40.0f, 220.0f).increment(5.0f).setValue(110.0f));
    public final SliderSetting lifetime = this.register(new SliderSetting("Время жизни, мс", "Как долго видны частицы.").range(250.0f, 2200.0f).increment(50.0f).setValue(900.0f));
    public final SliderSetting power = this.register(new SliderSetting("Разлёт, %", "Как сильно разлетаются частицы.").range(20.0f, 200.0f).increment(5.0f).setValue(90.0f));
    public final SliderSetting gravity = this.register(new SliderSetting("Гравитация", "Насколько частицы падают вниз.").range(0.0f, 100.0f).increment(5.0f).setValue(35.0f));
    public final ModeSetting texture = this.register(new ModeSetting("Частица", "Какая текстура у частиц.", TEXTURE_RANDOM, ParticleDeathOptions.options()));

    public final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final BooleanSetting useMobColor = this.register(new BooleanSetting("Цвет моба", "Красить частицы в оттенок, свой для каждого вида моба.", false));
    public final ColorSetting color = this.register(new ColorSetting("Цвет", "Цвет частиц, если цвет моба выключен.", new java.awt.Color(196, 214, 255, 235)).visible(() -> !this.useMobColor.getValue()));
    public final BooleanSetting flash = this.register(new BooleanSetting("Вспышка", "Короткая мягкая вспышка на месте смерти.", true));
    public final ColorSetting flashColor = this.register(new ColorSetting("Цвет вспышки", "Цвет вспышки при смерти.", new java.awt.Color(255, 240, 220, 200)).visible(this.flash::getValue));

    public final SeparatorSetting whoSeparator = this.register(new SeparatorSetting("Кому"));
    public final MultiSelectSetting targets = this.register(new MultiSelectSetting("Цели", "Для кого показывать анимацию смерти.").value(WHO_MOBS, WHO_ANIMALS, WHO_PLAYERS).selected(WHO_MOBS, WHO_ANIMALS));

    private final SimpleParticles particles = new SimpleParticles();
    private final Map<Integer, Vec3d> tracked = new HashMap<Integer, Vec3d>();
    private final Map<Integer, Integer> colors = new HashMap<Integer, Integer>();
    private final Random random = new Random();

    public DeathEffects() {
        super("Death Effects", "Разлёт частиц и мягкая вспышка при смерти моба или игрока.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.tracked.clear();
        this.colors.clear();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) {
            return;
        }
        this.particles.step();
        List<Integer> alive = new ArrayList<Integer>();
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || !DeathEffects.isTarget(entity, this.targets)) {
                continue;
            }
            LivingEntity living = (LivingEntity)entity;
            int id = entity.getId();
            if (living.isAlive() && !living.isRemoved()) {
                this.tracked.put(id, new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
                this.colors.put(id, DeathEffects.tintOf(living));
                alive.add(id);
                continue;
            }
            Vec3d pos = this.tracked.remove(id);
            Integer tint = this.colors.remove(id);
            if (pos != null) {
                this.burst(pos, tint == null ? this.color.getValue() : tint);
            }
        }
        if (this.tracked.size() > 256) {
            this.tracked.keySet().removeIf(id -> !alive.contains(id));
        }
    }

    private void burst(Vec3d pos, int tint) {
        float life = this.lifetime.getValue();
        float spread = this.power.getValue() / 100.0f;
        int amount = (int)this.count.getValue();
        for (int i = 0; i < amount; ++i) {
            Vec3d motion = new Vec3d(
                    (this.random.nextDouble() - 0.5) * 0.22 * (double)spread,
                    this.random.nextDouble() * 0.18 * (double)spread + 0.05,
                    (this.random.nextDouble() - 0.5) * 0.22 * (double)spread);
            this.particles.spawn(pos.add(0.0, 0.35, 0.0), motion, life * (0.7f + this.random.nextFloat() * 0.6f),
                    this.random.nextFloat() * 360.0f, this.pickFor(this.texture.getValue()), DeathEffects.argb(225, tint),
                    this.gravity.getValue() / 100.0f * 0.02f, 0.02f);
        }
        if (this.flash.getValue()) {
            int flashTint = this.flashColor.getValue() & 0xFFFFFF;
            for (int i = 0; i < 4; ++i) {
                this.particles.spawn(pos.add(0.0, 0.4, 0.0), Vec3d.ZERO, 220.0f + (float)i * 40.0f,
                        (float)i * 45.0f, DeathEffects.pick("Точка"), DeathEffects.argb(150 - i * 24, flashTint), 0.0f, 0.0f);
            }
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.particles.size() == 0) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.gameRenderer == null || client.gameRenderer.getCamera() == null) {
            return;
        }
        this.particles.render(worldRenderEvent.getStack(), client.gameRenderer.getCamera().getCameraPos(),
                client.gameRenderer.getCamera().getRotation(), (float)worldRenderEvent.getPartialTicks(),
                this.size.getValue() * 0.012f, true);
    }

    private static boolean isTarget(Entity entity, MultiSelectSetting targets) {
        if (entity instanceof PlayerEntity) {
            return targets.isSelected(WHO_PLAYERS);
        }
        if (entity instanceof PassiveEntity) {
            return targets.isSelected(WHO_ANIMALS);
        }
        return entity instanceof MobEntity && targets.isSelected(WHO_MOBS);
    }

    /** Оттенок конкретного вида моба: стабильный, но у каждого вида свой. */
    private static int tintOf(LivingEntity entity) {
        String key = entity.getType().getTranslationKey();
        int hash = key == null ? 0 : key.hashCode();
        float hue = (float)Math.floorMod(hash, 360) / 360.0f;
        return java.awt.Color.getHSBColor(hue, 0.45f, 1.0f).getRGB() & 0xFFFFFF;
    }

    /** Текстура для частицы: «Случайно» каждый раз выбирает новую из списка. */
    private Identifier pickFor(String mode) {
        if (TEXTURE_RANDOM.equals(mode)) {
            return ParticleConstants.TEXTURES[this.random.nextInt(ParticleConstants.TEXTURES.length)].id();
        }
        return DeathEffects.pick(mode);
    }

    private static Identifier pick(String mode) {
        for (ParticleTexture texture : ParticleConstants.TEXTURES) {
            if (texture.mode().equals(mode)) {
                return texture.id();
            }
        }
        return ParticleConstants.TEXTURES[0].id();
    }

    private static int argb(int alpha, int rgb) {
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | rgb & 0xFFFFFF;
    }

    /** Список текстур частиц с добавленным «Случайно» во главе. */
    private static final class ParticleDeathOptions {
        private static String[] options() {
            String[] modes = new String[ParticleConstants.TEXTURES.length + 1];
            modes[0] = TEXTURE_RANDOM;
            for (int i = 0; i < ParticleConstants.TEXTURES.length; ++i) {
                modes[i + 1] = ParticleConstants.TEXTURES[i].mode();
            }
            return modes;
        }
    }
}
