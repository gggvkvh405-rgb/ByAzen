package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Visuals.particles.FadeParticle;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleCollision;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleColors;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleRenderer;
import rtx.byazen.api.modules.impl.Visuals.particles.ParticleTexturePicker;
import rtx.byazen.api.modules.impl.Visuals.particles.WorldParticleUtil;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.NumberSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.render.render2d.ClientPalette;

public final class HitParticles
extends Module {
    private static final int MAX_PARTICLES = 1000;
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private static final double TWIST_TURN_PER_TICK = 0.09;
    private static final String DIR_RANDOM = "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u043e";
    private static final String DIR_FROM_TARGET = "\u041e\u0442 \u0446\u0435\u043b\u0438";
    private static final String DIR_TO_TARGET = "\u041a \u0446\u0435\u043b\u0438";
    private static final String DIR_TWIST = "\u0417\u0430\u043a\u0440\u0443\u0447\u0438\u0432\u0430\u043d\u0438\u0435";
    private final SeparatorSetting appearanceSeparator = this.register(new SeparatorSetting("\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434"));
    private final ModeSetting display = this.register(new ModeSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", "\u041a\u0430\u043a\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b \u0447\u0430\u0441\u0442\u0438\u0446 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c.", "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u0441\u0451", ParticleTexturePicker.modeOptions()));
    private final NumberSetting size = this.register(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", "\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446.", 50.0, 15.0, 56.0, 1.0));
    private final BooleanSetting scaleWithAlpha = this.register(new BooleanSetting("\u0421\u043a\u0435\u0439\u043b", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0441\u043b\u0435\u0434\u0443\u0435\u0442 \u0435\u0451 \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438.", true));
    private final SeparatorSetting spawnSeparator = this.register(new SeparatorSetting("\u041f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435"));
    private final NumberSetting count = this.register(new NumberSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", "\u0427\u0430\u0441\u0442\u0438\u0446 \u0437\u0430 \u043e\u0434\u0438\u043d \u0443\u0434\u0430\u0440.", 3.0, 3.0, 56.0, 1.0));
    private final NumberSetting spread = this.register(new NumberSetting("\u0414\u043e\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u0435 \u043a \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u0438", "\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 \u0440\u0430\u0437\u0431\u0440\u043e\u0441 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0432\u043e\u043a\u0440\u0443\u0433 \u0446\u0435\u043b\u0438.", 2.0, 0.5, 6.0, 0.5));
    private final ModeSetting spawnDirection = this.register(new ModeSetting("\u041f\u0440\u0435\u0441\u0435\u0442 \u043f\u043e\u043b\u0451\u0442\u0430", "\u041a\u0443\u0434\u0430 \u0438 \u043a\u0430\u043a \u0440\u0430\u0437\u043b\u0435\u0442\u0430\u044e\u0442\u0441\u044f \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0443\u0434\u0430\u0440\u0430.", "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u043e", "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u043e", "\u041e\u0442 \u0446\u0435\u043b\u0438", "\u041a \u0446\u0435\u043b\u0438", "\u0417\u0430\u043a\u0440\u0443\u0447\u0438\u0432\u0430\u043d\u0438\u0435"));
    private final NumberSetting lifetime = this.register(new NumberSetting("\u0412\u0440\u0435\u043c\u044f \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u043e\u0432\u0430\u043d\u0438\u044f", "\u0412\u0440\u0435\u043c\u044f \u043f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0438 \u0443\u0433\u0430\u0441\u0430\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432 \u043c\u0441.", 1500.0, 350.0, 2000.0, 25.0));
    private final SeparatorSetting motionSeparator = this.register(new SeparatorSetting("\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435"));
    private final NumberSetting speed = this.register(new NumberSetting("\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u0430\u044f \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c", "\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u0430\u044f \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446.", 0.3, 0.05, 1.0, 0.05));
    private final NumberSetting gravity = this.register(new NumberSetting("\u0413\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u044f", "\u041a\u043e\u044d\u0444\u0444\u0438\u0446\u0438\u0435\u043d\u0442 \u0433\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u0438 \u0447\u0430\u0441\u0442\u0438\u0446, \u043c\u0438\u043d\u0443\u0441 \u2014 \u0432\u0432\u0435\u0440\u0445.", 0.2, -0.5, 1.0, 0.05));
    private final ModeSetting collideMode = this.register(new ModeSetting("\u041f\u0440\u0438 \u0441\u0442\u043e\u043b\u043a\u043d\u043e\u0432\u0435\u043d\u0438\u0438", "\u041f\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u0435 \u043f\u0440\u0438 \u0441\u0442\u043e\u043b\u043a\u043d\u043e\u0432\u0435\u043d\u0438\u0438 \u0441 \u0431\u043b\u043e\u043a\u043e\u043c.", "\u041e\u0442\u0441\u043a\u043e\u043a", ParticleCollision.MODES));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446.", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446.", new Color(255, 255, 255, 255)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)));
    private final List<FadeParticle> particles = new ArrayList<FadeParticle>();
    private final Random random = new Random();
    private final ParticleRenderer renderer = new ParticleRenderer();

    public HitParticles() {
        super("Hit Particles", "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u043a\u0440\u0430\u0441\u0438\u0432\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u043e\u0442 \u0443\u0434\u0430\u0440\u043e\u0432.", Category.VISUALS);
        this.useSecondColor.visibleWhen(() -> this.colorMode.is("\u0421\u0432\u043e\u0439"));
        this.customColor.visibleWhen(() -> this.colorMode.is("\u0421\u0432\u043e\u0439"));
        this.customSecondColor.visibleWhen(() -> this.colorMode.is("\u0421\u0432\u043e\u0439") && this.useSecondColor.getValue());
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        if (this.mc.world == null || this.mc.player == null) {
            this.particles.clear();
            return;
        }
        long l = System.currentTimeMillis();
        this.particles.removeIf(fadeParticle -> fadeParticle.isDead(l));
        float f = this.gravity.getFloat();
        String string = this.collideMode.getValue();
        for (FadeParticle fadeParticle2 : this.particles) {
            this.stepParticle(fadeParticle2, f, string);
        }
    }

    @Override
    protected void onEnable() {
        this.particles.clear();
    }

    @EventHandler
    private void onAttack(AttackEntityEvent attackEntityEvent) {
        if (!this.isEnabled() || this.mc.world == null || this.mc.player == null) {
            return;
        }
        Entity entity = attackEntityEvent.getTarget();
        if (entity == null) {
            return;
        }
        int n = this.count.getInt();
        float f = this.lifetime.getFloat();
        float f2 = this.spread.getFloat();
        float f3 = this.speed.getFloat();
        Vec3d vec3d = entity.getEyePos().add(0.0, (double)(-entity.getStandingEyeHeight() / 2.0f), 0.0);
        float f4 = this.spawnDirection.is(DIR_TWIST) ? (this.random.nextBoolean() ? 1.0f : -1.0f) : 0.0f;
        for (int i = 0; i < n && this.particles.size() < 1000; ++i) {
            Vec3d vec3d2 = WorldParticleUtil.randomSpawnPosition((MinecraftClient)(Object)this.mc, (Random)(Object)this.random, (float)f2, (Vec3d)vec3d);
            if (vec3d2 == null) continue;
            double d = this.randomRange((double)f3 / 1.5, (double)f3 * 1.5);
            Vec3d vec3d3 = this.initialMotion(vec3d, vec3d2, d, f4);
            float f5 = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            FadeParticle fadeParticle = new FadeParticle(vec3d2, vec3d3, (float)this.randomRange(0.0, 180.0), f, ParticleTexturePicker.pick(this.display, this.random), f5);
            fadeParticle.twist = f4;
            this.particles.add(fadeParticle);
        }
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.particles.isEmpty() || this.mc.world == null || this.mc.player == null || this.mc.gameRenderer == null) {
            return;
        }
        long l = System.currentTimeMillis();
        MatrixStack matrixStack = worldRenderEvent.getStack();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        Vec3d vec3d = this.mc.gameRenderer.getCamera().getCameraPos();
        Quaternionf quaternionf = this.mc.gameRenderer.getCamera().getRotation();
        Vector3fc vector3fc = this.mc.gameRenderer.getCamera().getHorizontalPlane();
        float f = MathHelper.clamp((float)worldRenderEvent.getPartialTicks(), (float)0.0f, (float)1.0f);
        float f2 = this.size.getFloat() * 0.012f;
        boolean bl = this.scaleWithAlpha.getValue();
        this.renderer.clear();
        for (FadeParticle fadeParticle : this.particles) {
            float f3 = fadeParticle.alpha01(l);
            if (f3 <= 0.004f) continue;
            Vec3d vec3d2 = fadeParticle.renderPos(f).add(0.0, 0.2, 0.0);
            double d = vec3d2.x - vec3d.x;
            double d2 = vec3d2.y - vec3d.y;
            double d3 = vec3d2.z - vec3d.z;
            if (d * (double)vector3fc.x() + d2 * (double)vector3fc.y() + d3 * (double)vector3fc.z() < (double)(-f2 * 2.0f)) continue;
            int n = this.particleColor(fadeParticle, f3);
            float f4 = bl ? f2 * f3 : f2;
            this.renderer.drawTexture(matrixStack, immediate, fadeParticle.texture, vec3d2, vec3d, quaternionf, f4, fadeParticle.rotationDeg, n, true);
        }
        this.renderer.flush(immediate);
    }

    private void stepParticle(FadeParticle fadeParticle, float f, String string) {
        boolean bl;
        fadeParticle.beginStep();
        if (fadeParticle.twist != 0.0f && !fadeParticle.holdCollide) {
            double d = 0.09 * (double)fadeParticle.twist;
            double d2 = Math.cos(d);
            double d3 = Math.sin(d);
            double d4 = fadeParticle.motionX * d2 - fadeParticle.motionZ * d3;
            double d5 = fadeParticle.motionX * d3 + fadeParticle.motionZ * d2;
            fadeParticle.motionX = d4;
            fadeParticle.motionZ = d5;
        }
        fadeParticle.motionY -= (double)f / 17.5;
        if (f > 0.0f && fadeParticle.motionY > 0.0) {
            fadeParticle.motionY /= 1.0 + (double)f * 0.05;
        }
        if (!(bl = ParticleCollision.apply(this.mc, fadeParticle, string, f))) {
            fadeParticle.posX += fadeParticle.motionX;
            fadeParticle.posY += fadeParticle.motionY;
            fadeParticle.posZ += fadeParticle.motionZ;
        }
    }

    private int particleColor(FadeParticle fadeParticle, float f) {
        int n;
        int n2;
        int n3 = (int)(fadeParticle.gradientT * 360.0f);
        if (this.colorMode.is("\u0420\u0430\u0434\u0443\u0433\u0430")) {
            return ParticleColors.rainbow((int)8, (int)n3, (float)1.0f, (float)1.0f, (float)f);
        }
        if (this.colorMode.is("\u041a\u043b\u0438\u0435\u043d\u0442")) {
            int[] nArray = ClientPalette.colors();
            if (nArray != null && nArray.length >= 2) {
                return ColorUtil.multAlpha(ParticleColors.paletteFade((int)8, (int)n3, (int[])nArray), f);
            }
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            if (interfaceModule != null) {
                n2 = interfaceModule.clientPrimaryColorOpaque();
                n = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n2;
            } else {
                n = n2 = -1;
            }
        } else {
            n2 = this.customColor.getColor();
            int n4 = n = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : this.customColor.getColor();
        }
        if (n2 == n) {
            return ColorUtil.multAlpha(n2, f);
        }
        return ColorUtil.multAlpha(ParticleColors.fade((int)8, (int)n3, (int)n2, (int)n), f);
    }

    private double randomRange(double d, double d2) {
        return d + this.random.nextDouble() * (d2 - d);
    }

    private Vec3d horizontalDir(Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(vec3d.x, 0.0, vec3d.z);
        if (vec3d2.lengthSquared() < 1.0E-4) {
            double d = this.randomRange(0.0, Math.PI * 2);
            return new Vec3d(Math.cos(d), 0.0, Math.sin(d));
        }
        return vec3d2.normalize();
    }

    private Vec3d initialMotion(Vec3d vec3d, Vec3d vec3d2, double d, float f) {
        if (this.spawnDirection.is(DIR_FROM_TARGET)) {
            Vec3d vec3d3 = vec3d2.subtract(vec3d);
            if (vec3d3.lengthSquared() < 1.0E-4) {
                return this.randomUnitDirection().multiply(d);
            }
            return vec3d3.normalize().multiply(d);
        }
        if (this.spawnDirection.is(DIR_TO_TARGET)) {
            Vec3d vec3d4 = vec3d.subtract(vec3d2);
            if (vec3d4.lengthSquared() < 1.0E-4) {
                return this.randomUnitDirection().multiply(d);
            }
            return vec3d4.normalize().multiply(d);
        }
        if (this.spawnDirection.is(DIR_TWIST)) {
            Vec3d vec3d5 = this.horizontalDir(vec3d2.subtract(vec3d));
            Vec3d vec3d6 = new Vec3d(-vec3d5.z * (double)f, 0.0, vec3d5.x * (double)f);
            Vec3d vec3d7 = new Vec3d(vec3d6.x * 0.85 + vec3d5.x * 0.25, this.randomRange(0.15, 0.45), vec3d6.z * 0.85 + vec3d5.z * 0.25).normalize();
            return vec3d7.multiply(d);
        }
        return this.randomUnitDirection().multiply(d);
    }

    private Vec3d randomUnitDirection() {
        double d = this.randomRange(0.0, Math.PI * 2);
        double d2 = this.randomRange(-0.35, 0.75);
        double d3 = Math.sqrt(Math.max(0.0, 1.0 - d2 * d2));
        return new Vec3d(Math.cos(d) * d3, d2, Math.sin(d) * d3);
    }
}

