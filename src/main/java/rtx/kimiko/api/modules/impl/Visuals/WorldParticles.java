package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.impl.Interface.InterfaceModule;
import rtx.kimiko.api.modules.impl.Visuals.particles.FadeParticle;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleCollision;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleColors;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleRenderer;
import rtx.kimiko.api.modules.impl.Visuals.particles.ParticleTexturePicker;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.render2d.ClientPalette;

public final class WorldParticles
extends Module {
    private static final int MAX_PARTICLES = 1500;
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private final SeparatorSetting appearanceSeparator = this.register(new SeparatorSetting("\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434"));
    private final ModeSetting display = this.register(new ModeSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c", "\u041a\u0430\u043a\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0443\u0440\u044b \u0447\u0430\u0441\u0442\u0438\u0446 \u0440\u0438\u0441\u043e\u0432\u0430\u0442\u044c.", "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u0441\u0451", ParticleTexturePicker.modeOptions()));
    private final NumberSetting size = this.register(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", "\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446.", 150.0, 100.0, 200.0, 10.0));
    private final BooleanSetting scaleWithAlpha = this.register(new BooleanSetting("\u0421\u043a\u0435\u0439\u043b", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0441\u043b\u0435\u0434\u0443\u0435\u0442 \u0435\u0451 \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438.", true));
    private final SeparatorSetting spawnSeparator = this.register(new SeparatorSetting("\u041f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435"));
    private final NumberSetting count = this.register(new NumberSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", "\u041f\u043e\u043f\u044b\u0442\u043e\u043a \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446 \u043a\u0430\u0436\u0434\u044b\u0439 \u0442\u0438\u043a.", 15.0, 1.0, 60.0, 1.0));
    private final NumberSetting range = this.register(new NumberSetting("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u043f\u0430\u0432\u043d\u0430", "\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u044b\u0439 \u0440\u0430\u0437\u0431\u0440\u043e\u0441 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446.", 50.0, 10.0, 50.0, 1.0));
    private final NumberSetting rangeY = this.register(new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u043f\u0430\u0432\u043d\u0430", "\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0432\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u043d\u0430\u0434 \u0432\u0430\u043c\u0438.", 30.0, 0.05, 30.0, 0.05));
    private final NumberSetting lifetime = this.register(new NumberSetting("\u0412\u0440\u0435\u043c\u044f \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u043e\u0432\u0430\u043d\u0438\u044f", "\u0412\u0440\u0435\u043c\u044f \u043f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f \u0438 \u0443\u0433\u0430\u0441\u0430\u043d\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432 \u043c\u0441.", 800.0, 150.0, 1500.0, 10.0));
    private final SeparatorSetting motionSeparator = this.register(new SeparatorSetting("\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435"));
    private final NumberSetting motionPower = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f", "\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438 \u043f\u043e\u043b\u0451\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446.", 1.0, 0.1, 2.0, 0.1));
    private final NumberSetting gravity = this.register(new NumberSetting("\u0421\u0438\u043b\u0430 \u0433\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u0438", "\u041f\u043e\u0441\u0442\u043e\u044f\u043d\u043d\u043e\u0435 \u0443\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u0435 \u0447\u0430\u0441\u0442\u0438\u0446 \u0432\u043d\u0438\u0437 \u0438\u043b\u0438 \u0432\u0432\u0435\u0440\u0445.", 0.0, -10.0, 10.0, 1.0));
    private final NumberSetting inclineX = this.register(new NumberSetting("\u041d\u0430\u043a\u043b\u043e\u043d \u043f\u043e\u043b\u0451\u0442\u0430 \u043f\u043e X", "\u0411\u043e\u043a\u043e\u0432\u043e\u0439 \u0441\u043d\u043e\u0441 \u0447\u0430\u0441\u0442\u0438\u0446 \u043e\u0442\u043d\u043e\u0441\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u0432\u0437\u0433\u043b\u044f\u0434\u0430.", 0.0, -17.5, 17.5, 0.5));
    private final NumberSetting inclineZ = this.register(new NumberSetting("\u041d\u0430\u043a\u043b\u043e\u043d \u043f\u043e\u043b\u0451\u0442\u0430 \u043f\u043e Z", "\u041f\u0440\u043e\u0434\u043e\u043b\u044c\u043d\u044b\u0439 \u0441\u043d\u043e\u0441 \u0447\u0430\u0441\u0442\u0438\u0446 \u043e\u0442\u043d\u043e\u0441\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u0432\u0437\u0433\u043b\u044f\u0434\u0430.", 17.5, -17.5, 17.5, 0.5));
    private final ModeSetting collideMode = this.register(new ModeSetting("\u041f\u0440\u0438 \u0441\u0442\u043e\u043b\u043a\u043d\u043e\u0432\u0435\u043d\u0438\u0438", "\u041f\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u0435 \u043f\u0440\u0438 \u0441\u0442\u043e\u043b\u043a\u043d\u043e\u0432\u0435\u043d\u0438\u0438 \u0441 \u0431\u043b\u043e\u043a\u043e\u043c.", "\u041e\u0442\u0441\u043a\u043e\u043a", ParticleCollision.MODES));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446.", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446.", new Color(255, 255, 255, 255)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)));
    private final List<FadeParticle> particles = new ArrayList<FadeParticle>();
    private final Random random = new Random();
    private final ParticleRenderer renderer = new ParticleRenderer();

    public WorldParticles() {
        super("World Particles", "\u0420\u0435\u043d\u0434\u0435\u0440\u0438\u0442 \u043a\u0440\u0430\u0441\u0438\u0432\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432\u043e\u043a\u0440\u0443\u0433 \u0432\u0430\u0441.", Category.VISUALS);
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
        float f = this.motionPower.getFloat();
        float f2 = this.gravity.getFloat() / 80.0f * 0.05f * f;
        float f3 = MathHelper.clamp((float)(this.gravity.getFloat() / 10.0f), (float)0.0f, (float)1.0f);
        String string = this.collideMode.getValue();
        float f4 = this.cameraYaw();
        double d = Math.sin(Math.toRadians(f4));
        double d2 = -Math.cos(Math.toRadians(f4));
        double d3 = -Math.sin(Math.toRadians(f4 + 90.0f));
        double d4 = Math.cos(Math.toRadians(f4 + 90.0f));
        double d5 = (d * (double)this.inclineZ.getFloat() / 50.0 + d3 * (double)this.inclineX.getFloat() / 50.0) * 0.05 * (double)f;
        double d6 = (d2 * (double)this.inclineZ.getFloat() / 50.0 + d4 * (double)this.inclineX.getFloat() / 50.0) * 0.05 * (double)f;
        for (FadeParticle fadeParticle2 : this.particles) {
            this.stepParticle(fadeParticle2, f, d5, f2, d6, string, f3);
        }
        this.spawnParticles();
    }

    @Override
    protected void onEnable() {
        this.particles.clear();
    }

    private float cameraYaw() {
        float f = this.mc.player.getYaw();
        if (this.mc.options.getPerspective().isFrontView()) {
            f += 180.0f;
        }
        return f;
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
            Vec3d vec3d2 = fadeParticle.renderPos(f);
            int n = this.particleColor(fadeParticle, f3);
            float f4 = bl ? f2 * f3 : f2;
            this.renderer.drawTexture(matrixStack, immediate, fadeParticle.texture, vec3d2, vec3d, quaternionf, f4, fadeParticle.rotationDeg, n, true);
        }
        this.renderer.flush(immediate);
    }

    private void stepParticle(FadeParticle fadeParticle, float f, double d, double d2, double d3, String string, float f2) {
        fadeParticle.beginStep();
        if (!fadeParticle.holdCollide) {
            fadeParticle.posX += fadeParticle.motionX * (double)f;
            fadeParticle.posY += fadeParticle.motionY * (double)f;
            fadeParticle.posZ += fadeParticle.motionZ * (double)f;
            fadeParticle.motionX += d;
            fadeParticle.motionY += d2;
            fadeParticle.motionZ += d3;
        }
        fadeParticle.motionY -= 6.0E-4;
        boolean bl = ParticleCollision.apply(this.mc, fadeParticle, string, f2);
        if (!bl) {
            fadeParticle.posX += fadeParticle.motionX;
            fadeParticle.posY += fadeParticle.motionY;
            fadeParticle.posZ += fadeParticle.motionZ;
        }
    }

    private void spawnParticles() {
        int n = this.count.getInt();
        float f = this.range.getFloat();
        float f2 = Math.max(0.55f, this.rangeY.getFloat());
        float f3 = this.lifetime.getFloat();
        for (int i = 0; i < n && this.particles.size() < 1500; ++i) {
            double d = this.randomRange(-f, f);
            double d2 = this.randomRange(-f, f);
            double d3 = this.randomRange(0.5, f2);
            Vec3d vec3d = this.mc.player.getEntityPos().add(d, d3, d2);
            if (!this.mc.world.getBlockState(BlockPos.ofFloored((Position)vec3d)).isAir() || !this.isInPlayerView(vec3d)) continue;
            Vec3d vec3d2 = new Vec3d(this.randomRange(-0.04, 0.04), this.randomRange(0.0, 0.05), this.randomRange(-0.04, 0.04));
            float f4 = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            this.particles.add(new FadeParticle(vec3d, vec3d2, (float)this.randomRange(0.0, 180.0), f3, ParticleTexturePicker.pick(this.display, this.random), f4));
        }
    }

    private boolean isInPlayerView(Vec3d vec3d) {
        Vec3d vec3d2;
        Vec3d vec3d3 = Vec3d.fromPolar((float)this.mc.player.getPitch(), (float)this.cameraYaw());
        return vec3d3.dotProduct(vec3d2 = vec3d.subtract(this.mc.player.getEntityPos()).normalize()) > 0.1;
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
}

