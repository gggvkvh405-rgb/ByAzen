package rtx.byazen.api.modules.impl.Visuals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleConstants;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexture;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexturePicker;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.world.SimpleParticles;

/**
 * Трейлы 2.0 (идея №58 из IDEAS.md).
 * <p>
 * След тянется за тем, что реально движется: за игроком на элитрах вьётся завиток, за стрелами и
 * трезубцами — шлейф, за брошенными снарядами и предметами тянется мягкая пыль. Частицы рисуются
 * сглаженными билбордами и плавно гаснут, положение считается по пройденному пути, поэтому след
 * не рассыпается на медленных скоростях.
 */
public final class TrailsPlus
extends Module {

    private final SeparatorSetting whoSeparator = this.register(new SeparatorSetting("За кем след"));
    public final BooleanSetting elytra = this.register(new BooleanSetting("Завиток от элитр", "Вить спираль за игроком на элитрах.", true));
    public final BooleanSetting arrows = this.register(new BooleanSetting("Шлейф стрел и трезубцев", "Тянуть след за стрелами и трезубцами.", true));
    public final BooleanSetting thrown = this.register(new BooleanSetting("Шлейф снарядов", "След за снежками, жемчугом и зельями.", true));
    public final BooleanSetting items = this.register(new BooleanSetting("След предметов", "Пыль за летящими предметами.", false));

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final SliderSetting density = this.register(new SliderSetting("Плотность", "Сколько частиц на блок пройденного пути.").range(1.0f, 30.0f).increment(1.0f).setValue(8.0f));
    public final SliderSetting lifetime = this.register(new SliderSetting("Время жизни, мс", "Как долго живёт частица следа.").range(120.0f, 1200.0f).increment(20.0f).setValue(380.0f));
    public final SliderSetting size = this.register(new SliderSetting("Размер, %", "Размер частиц следа.").range(30.0f, 200.0f).increment(5.0f).setValue(80.0f));
    public final SliderSetting spread = this.register(new SliderSetting("Разброс", "Насколько широко частицы расходятся от линии движения.").range(0.0f, 100.0f).increment(5.0f).setValue(30.0f));
    public final ModeSetting texture = this.register(new ModeSetting("Частица", "Текстура частиц следа.", "Отображать всё", ParticleTexturePicker.modeOptions()));

    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("Цвета"));
    public final ColorSetting elytraColor = this.register(new ColorSetting("Цвет элитр", "Оттенок завитка от элитр.", new java.awt.Color(150, 200, 255, 190)));
    public final ColorSetting arrowColor = this.register(new ColorSetting("Цвет стрел", "Оттенок шлейфа стрел и трезубцев.", new java.awt.Color(255, 238, 190, 200)));
    public final ColorSetting thrownColor = this.register(new ColorSetting("Цвет снарядов", "Оттенок шлейфа брошенных снарядов.", new java.awt.Color(190, 255, 215, 190)));
    public final ColorSetting itemColor = this.register(new ColorSetting("Цвет предметов", "Оттенок пыли за предметами.", new java.awt.Color(255, 215, 160, 150)));

    private final SimpleParticles particles = new SimpleParticles();
    private final Map<Integer, Vec3d> lastPositions = new HashMap<Integer, Vec3d>();
    private final Random random = new Random();
    private double spin;

    public TrailsPlus() {
        super("Trails 2.0", "Завиток за элитрами, шлейф стрел, снарядов и предметов.", Category.VISUALS);
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.lastPositions.clear();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            this.lastPositions.clear();
            return;
        }
        if (this.lastPositions.size() > 512) {
            this.lastPositions.clear();
        }
        double density = (double)this.density.getValue();
        Vec3d playerPos = new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ());
        for (Entity entity : this.mc.world.getEntities()) {
            int kind = this.kindOf(entity);
            if (kind < 0 || entity.squaredDistanceTo(playerPos.x, playerPos.y, playerPos.z) > 2304.0) {
                continue;
            }
            Vec3d current = new Vec3d(entity.getX(), entity.getY() + (double)entity.getHeight() * 0.5, entity.getZ());
            Vec3d previous = this.lastPositions.put(entity.getId(), current);
            if (previous == null) {
                continue;
            }
            double distance = current.distanceTo(previous);
            if (distance < 1.0E-4) {
                continue;
            }
            double expected = distance * density;
            int count = (int)Math.floor(expected);
            if (this.random.nextDouble() < expected - (double)count) {
                ++count;
            }
            if (count > 8) {
                count = 8;
            }
            for (int i = 0; i < count; ++i) {
                double part = count <= 1 ? 1.0 : (double)i / (double)(count - 1);
                Vec3d spawn = previous.add(current.subtract(previous).multiply(part));
                this.spawn(entity, kind, spawn);
            }
        }
    }

    private void spawn(Entity entity, int kind, Vec3d where) {
        double spread = (double)this.spread.getValue() / 100.0;
        Vec3d motion;
        if (kind == 0) {
            Vec3d velocity = entity.getVelocity();
            double length = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
            Vec3d side = length < 1.0E-4
                    ? new Vec3d(1.0, 0.0, 0.0)
                    : new Vec3d(-velocity.z / length, 0.0, velocity.x / length);
            this.spin += 0.42;
            double radius = 0.14 + spread * 0.5;
            where = where.add(side.multiply(Math.cos(this.spin) * radius))
                    .add(0.0, Math.sin(this.spin) * radius * 0.7, 0.0);
            motion = new Vec3d(side.x * 0.004 * (double)(this.random.nextBoolean() ? 1 : -1),
                    0.002, side.z * 0.004 * (double)(this.random.nextBoolean() ? 1 : -1));
        }
        else {
            double jitter = 0.012 + spread * 0.03;
            motion = new Vec3d((this.random.nextDouble() - 0.5) * jitter,
                    (this.random.nextDouble() - 0.5) * jitter * 0.6,
                    (this.random.nextDouble() - 0.5) * jitter);
        }
        int color = this.colorFor(kind);
        float life = this.lifetime.getValue() * (0.72f + this.random.nextFloat() * 0.6f);
        float gravity = kind == 1 ? 0.0025f : (kind == 2 ? 0.0018f : (kind == 3 ? 0.002f : 0.0f));
        float drag = kind == 1 ? 0.10f : (kind == 3 ? 0.08f : 0.05f);
        this.particles.spawn(where, motion, life, this.random.nextFloat() * 360.0f,
                this.textureFor(), color, gravity, drag);
    }

    private int kindOf(Entity entity) {
        if (entity == null || entity.isRemoved()) {
            return -1;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            return player.isGliding() && this.elytra.getValue() ? 0 : -1;
        }
        if (entity instanceof PersistentProjectileEntity) {
            return this.arrows.getValue() ? 1 : -1;
        }
        if (entity instanceof ProjectileEntity) {
            return this.thrown.getValue() ? 2 : -1;
        }
        if (entity instanceof ItemEntity) {
            return this.items.getValue() ? 3 : -1;
        }
        return -1;
    }

    private int colorFor(int kind) {
        switch (kind) {
            case 0: {
                return this.elytraColor.getValue();
            }
            case 1: {
                return this.arrowColor.getValue();
            }
            case 2: {
                return this.thrownColor.getValue();
            }
        }
        return this.itemColor.getValue();
    }

    private Identifier textureFor() {
        String mode = this.texture.getValue();
        if (ParticleTexturePicker.SHOW_ALL.equals(mode)) {
            return ParticleConstants.TEXTURES[this.random.nextInt(ParticleConstants.TEXTURES.length)].id();
        }
        for (ParticleTexture particleTexture : ParticleConstants.TEXTURES) {
            if (particleTexture.mode().equals(mode)) {
                return particleTexture.id();
            }
        }
        return ParticleConstants.TEXTURES[0].id();
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
                this.size.getValue() * 0.011f, true);
    }
}
