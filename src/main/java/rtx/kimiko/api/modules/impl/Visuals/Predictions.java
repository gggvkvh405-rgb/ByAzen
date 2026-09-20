package rtx.kimiko.api.modules.impl.Visuals;
import rtx.kimiko.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import rtx.kimiko.api.drags.Position;
import rtx.kimiko.api.events.impl.render.HudRenderEvent;
import rtx.kimiko.api.events.impl.render.WorldRenderEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ColorSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;
import rtx.kimiko.utils.render.others.WorldVertex;
import rtx.kimiko.utils.render.pipeline.ClientPipelines;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.render.render2d.Render2DCoordinateSpace;
import rtx.kimiko.utils.render.render2d.shape.BuiltShape;

public final class Predictions
extends Module {
    private static final int MAX_PARTICLES = 650;
    private static final int MAX_SIMULATION_TICKS = 300;
    private static final long PARTICLE_SPAWN_INTERVAL_MS = 42L;
    private static final float MARKER_RADIUS = 10.5f;
    private static final float MARKER_POINTER_HEIGHT = 7.0f;
    private static final String TEXTURE_GLOW = "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435";
    private static final String TEXTURE_STAR = "\u0417\u0432\u0435\u0437\u0434\u0430";
    private static final String TEXTURE_HEART = "\u0421\u0435\u0440\u0434\u0446\u0435";
    private static final String TEXTURE_LIGHTNING = "\u041c\u043e\u043b\u043d\u0438\u044f";
    private static final String TEXTURE_TRIANGLE = "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a";
    private static final String TEXTURE_CROSS = "\u041a\u0440\u0435\u0441\u0442";
    private static final String TEXTURE_CROWN = "\u041a\u043e\u0440\u043e\u043d\u0430";
    private static final String TEXTURE_DOLLAR = "\u0414\u043e\u043b\u043b\u0430\u0440";
    private static final String TEXTURE_RHOMBUS = "\u0420\u043e\u043c\u0431";
    private static final String TEXTURE_SNOWFLAKE = "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0430";
    private static final String TEXTURE_SKULL = "\u0427\u0435\u0440\u0435\u043f";
    private static final String TEXTURE_CUBE = "\u041a\u0443\u0431";
    private static final String TEXTURE_SQUARE = "\u041a\u0432\u0430\u0434\u0440\u0430\u0442";
    private static final String TEXTURE_RANDOM = "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u043e";
    private static final Identifier PARTICLE_GLOW = Predictions.texture("glow.png");
    private static final Identifier PARTICLE_STAR = Predictions.texture("star.png");
    private static final Identifier PARTICLE_HEART = Predictions.texture("heart.png");
    private static final Identifier PARTICLE_LIGHTNING = Predictions.texture("lightning.png");
    private static final Identifier PARTICLE_TRIANGLE = Predictions.texture("triangle.png");
    private static final Identifier PARTICLE_CROSS = Predictions.texture("cross.png");
    private static final Identifier PARTICLE_CROWN = Predictions.texture("crown.png");
    private static final Identifier PARTICLE_DOLLAR = Predictions.texture("dollar.png");
    private static final Identifier PARTICLE_SQUARE = Predictions.texture("square.png");
    private static final Identifier PARTICLE_SNOWFLAKE = Predictions.texture("snowflake.png");
    private static final Identifier PARTICLE_SKULL = Predictions.texture("skull.png");
    private static final Identifier PARTICLE_CUBE = Predictions.texture("cube.png");
    private static final Predictions.ProjectileProfile PROFILE_PEARL = new Predictions.ProjectileProfile(0.99, 0.03);
    private static final Predictions.ProjectileProfile PROFILE_ITEM = new Predictions.ProjectileProfile(0.98, 0.04);
    private static final Predictions.ProjectileProfile PROFILE_ARROW = new Predictions.ProjectileProfile(0.99, 0.05);
    private static final String RENDER_TRAJECTORY = "\u0422\u0440\u0430\u0435\u043a\u0442\u043e\u0440\u0438\u044f";
    private static final String RENDER_PARTICLES = "\u0427\u0430\u0441\u0442\u0438\u0446\u044b";
    private static final String MARKER_PEARL = "\u041f\u0451\u0440\u043b";
    private static final String MARKER_SNOWBALL = "\u0421\u043d\u0435\u0436\u043e\u043a";
    private static final String MARKER_ARROW = "\u0421\u0442\u0440\u0435\u043b\u0430";
    private static final String MARKER_TRIDENT = "\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446";
    private static final String MARKER_ITEM = "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String[] PARTICLE_TEXTURE_MODES = new String[]{"\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u0417\u0432\u0435\u0437\u0434\u0430", "\u0421\u0435\u0440\u0434\u0446\u0435", "\u041c\u043e\u043b\u043d\u0438\u044f", "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a", "\u041a\u0440\u0435\u0441\u0442", "\u041a\u043e\u0440\u043e\u043d\u0430", "\u0414\u043e\u043b\u043b\u0430\u0440", "\u0420\u043e\u043c\u0431", "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0430", "\u0427\u0435\u0440\u0435\u043f", "\u041a\u0443\u0431", "\u041a\u0432\u0430\u0434\u0440\u0430\u0442", "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u043e"};
    private final SeparatorSetting generalSeparator = this.register(new SeparatorSetting("\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435"));
    private final MultiSelectSetting targets = this.register(new MultiSelectSetting("\u0426\u0435\u043b\u0438", "\u041e\u0431\u044a\u0435\u043a\u0442\u044b, \u043e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u043c\u044b\u0435 \u043f\u0440\u0435\u0434\u0441\u043a\u0430\u0437\u0430\u043d\u0438\u044f\u043c\u0438.").value(Predictions.ProjectileTarget.PEARLS.settingName(), Predictions.ProjectileTarget.ARROWS.settingName(), Predictions.ProjectileTarget.TRIDENTS.settingName(), Predictions.ProjectileTarget.ITEMS.settingName()).selected(Predictions.ProjectileTarget.PEARLS.settingName(), Predictions.ProjectileTarget.ARROWS.settingName(), Predictions.ProjectileTarget.TRIDENTS.settingName(), Predictions.ProjectileTarget.ITEMS.settingName()));
    private final MultiSelectSetting render = this.register(new MultiSelectSetting("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0435", "\u0412\u0438\u0437\u0443\u0430\u043b \u043f\u0440\u0435\u0434\u0441\u043a\u0430\u0437\u0430\u043d\u0438\u0439.").value("\u0422\u0440\u0430\u0435\u043a\u0442\u043e\u0440\u0438\u044f", "\u0427\u0430\u0441\u0442\u0438\u0446\u044b").selected("\u0422\u0440\u0430\u0435\u043a\u0442\u043e\u0440\u0438\u044f", "\u0427\u0430\u0441\u0442\u0438\u0446\u044b"));
    private final BooleanSetting landingMarker = this.register(new BooleanSetting("\u041c\u0435\u0442\u043a\u0430 \u043f\u0440\u0438\u0437\u0435\u043c\u043b\u0435\u043d\u0438\u044f", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0438 \u0441\u0442\u0440\u0435\u043b\u043a\u0443 \u0432 \u0442\u043e\u0447\u043a\u0435 \u043f\u0430\u0434\u0435\u043d\u0438\u044f.", true));
    private final MultiSelectSetting markerItems = this.register(new MultiSelectSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0432 \u043c\u0435\u0442\u043a\u0435", "\u0422\u0438\u043f\u044b \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u043c. \u041e\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435 \u0440\u0438\u0441\u0443\u044e\u0442\u0441\u044f \u043a\u0432\u0430\u0434\u0440\u0430\u0442\u043e\u043c.").value("\u041f\u0451\u0440\u043b", "\u0421\u043d\u0435\u0436\u043e\u043a", "\u0421\u0442\u0440\u0435\u043b\u0430", "\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b").selected("\u041f\u0451\u0440\u043b", "\u0421\u043d\u0435\u0436\u043e\u043a", "\u0421\u0442\u0440\u0435\u043b\u0430", "\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b"));
    private final NumberSetting range = this.register(new NumberSetting("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c", "\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u0430\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u043f\u0440\u0435\u0434\u0441\u043a\u0430\u0437\u0430\u043d\u0438\u0439.", 128.0, 16.0, 256.0, 1.0));
    private final SeparatorSetting particlesSeparator = this.register(new SeparatorSetting("\u0427\u0430\u0441\u0442\u0438\u0446\u044b"));
    private final ModeSetting particleTexture = this.register(new ModeSetting("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0434\u043b\u044f \u0447\u0430\u0441\u0442\u0438\u0446 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432.", "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", PARTICLE_TEXTURE_MODES));
    private final NumberSetting particleCount = this.register(new NumberSetting("\u041a\u043e\u043b-\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446", "\u0427\u0430\u0441\u0442\u0438\u0446 \u043d\u0430 \u043a\u0430\u0436\u0434\u044b\u0439 \u0438\u043c\u043f\u0443\u043b\u044c\u0441 \u0434\u0432\u0438\u0436\u0443\u0449\u0435\u0433\u043e\u0441\u044f \u0441\u043d\u0430\u0440\u044f\u0434\u0430.", 4.0, 1.0, 10.0, 1.0));
    private final NumberSetting particleSize = this.register(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0420\u0430\u0437\u043c\u0435\u0440 \u043a\u0432\u0430\u0434\u0430 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0441\u043d\u0430\u0440\u044f\u0434\u0430.", 0.23, 0.05, 0.75, 0.01));
    private final NumberSetting particleSpeed = this.register(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446", "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u0430\u0437\u043b\u0451\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446 \u0441\u043d\u0430\u0440\u044f\u0434\u0430.", 0.3, 0.05, 1.5, 0.01));
    private final NumberSetting particleLifetime = this.register(new NumberSetting("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0441\u043d\u0430\u0440\u044f\u0434\u0430 \u0432 \u043c\u0438\u043b\u043b\u0438\u0441\u0435\u043a\u0443\u043d\u0434\u0430\u0445.", 900.0, 250.0, 2500.0, 50.0));
    private final BooleanSetting randomParticleColor = this.register(new BooleanSetting("\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442 \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0435 \u0446\u0432\u0435\u0442\u0430 \u0447\u0430\u0441\u0442\u0438\u0446.", false));
    private final BooleanSetting particleWhiteCenter = this.register(new BooleanSetting("\u0411\u0435\u043b\u044b\u0439 \u0446\u0435\u043d\u0442\u0440", "\u0420\u0438\u0441\u0443\u0435\u0442 \u044f\u0440\u043a\u0438\u0439 \u0446\u0435\u043d\u0442\u0440 \u0432\u043d\u0443\u0442\u0440\u0438 \u0447\u0430\u0441\u0442\u0438\u0446 \u0441\u043d\u0430\u0440\u044f\u0434\u0430.", true));
    private final BooleanSetting particleSpin = this.register(new BooleanSetting("\u0412\u0440\u0430\u0449\u0435\u043d\u0438\u0435 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0412\u0440\u0430\u0449\u0430\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0441\u043d\u0430\u0440\u044f\u0434\u0430.", true));
    private final SeparatorSetting colorsSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442\u0430"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a \u0446\u0432\u0435\u0442\u0430 \u043b\u0438\u043d\u0438\u0438, \u043c\u0435\u0442\u043a\u0438 \u0438 \u0447\u0430\u0441\u0442\u0438\u0446.", "\u0421\u0432\u043e\u0439", "\u0421\u0432\u043e\u0439", "\u041a\u043b\u0438\u0435\u043d\u0442"));
    private final ColorSetting lineColor = this.register(new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0438", "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0438 \u043f\u0440\u0435\u0434\u0441\u043a\u0430\u0437\u0430\u043d\u0438\u044f.", new Color(127, 242, 255, 230)).visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM)));
    private final ColorSetting particleColor = this.register(new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0441\u043b\u0435\u0434\u0430 \u0431\u0440\u043e\u0448\u0435\u043d\u043d\u043e\u0433\u043e \u043e\u0431\u044a\u0435\u043a\u0442\u0430.", new Color(185, 125, 255, 215)));
    private final Random random = new Random();
    private final List<PredictionParticle> particles = new ArrayList<PredictionParticle>();
    private final List<Vec3d> simulationPoints = new ArrayList<Vec3d>(301);
    private final Map<Integer, ProjectilePath> projectilePaths = new HashMap<Integer, ProjectilePath>();
    private final Set<Integer> liveProjectileIds = new HashSet<Integer>();
    private final Map<Integer, Long> lastParticleSpawnMs = new HashMap<Integer, Long>();
    private final Set<RenderLayer> usedParticleRenderTypes = new HashSet<RenderLayer>();
    private final Map<Integer, LandingInterpolation> landingInterpolations = new HashMap<Integer, LandingInterpolation>();
    private final List<LandingMarker> landingMarkers = new ArrayList<LandingMarker>();
    private final Vector4f projectionScratch = new Vector4f();
    private long lastParticleUpdateMs;

    public Predictions() {
        super("Predict", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0442\u0440\u0430\u0435\u043a\u0442\u043e\u0440\u0438\u044e \u0438 \u043c\u0435\u0441\u0442\u043e \u043f\u0430\u0434\u0435\u043d\u0438\u044f \u043b\u0435\u0442\u044f\u0449\u0438\u0445 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432.", Category.VISUALS);
        this.particleTexture.visibleWhen(this::particlesEnabled);
        this.particleCount.visibleWhen(this::particlesEnabled);
        this.particleSize.visibleWhen(this::particlesEnabled);
        this.particleSpeed.visibleWhen(this::particlesEnabled);
        this.particleLifetime.visibleWhen(this::particlesEnabled);
        this.randomParticleColor.visibleWhen(this::particlesEnabled);
        this.particleWhiteCenter.visibleWhen(this::particlesEnabled);
        this.particleSpin.visibleWhen(this::particlesEnabled);
        this.markerItems.visible(this.landingMarker::getValue);
        this.particleColor.visibleWhen(() -> this.particlesEnabled() && !this.randomParticleColor.getValue() && this.colorMode.is(COLOR_CUSTOM));
    }

    private boolean isFinite(Vec3d vec3d) {
        return vec3d != null && Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z);
    }

    private static Identifier texture(String string) {
        return Identifier.of((String)"kimiko", (String)("textures/features/predictions/particles/" + string));
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.lastParticleSpawnMs.clear();
        this.usedParticleRenderTypes.clear();
        this.projectilePaths.clear();
        this.landingInterpolations.clear();
        this.landingMarkers.clear();
        this.lastParticleUpdateMs = 0L;
    }

    private boolean isInRange(Entity entity) {
        float f = this.range.getFloat();
        return f <= 0.0f || this.mc.player == null || this.mc.player.squaredDistanceTo(entity) <= (double)(f * f);
    }

    private Predictions.ProjectileTarget targetFor(Entity entity) {
        ItemEntity itemEntity;
        if (entity instanceof EnderPearlEntity || entity instanceof SnowballEntity || entity instanceof EggEntity) {
            return Predictions.ProjectileTarget.PEARLS;
        }
        if (entity instanceof TridentEntity) {
            return Predictions.ProjectileTarget.TRIDENTS;
        }
        if (entity instanceof PersistentProjectileEntity) {
            return Predictions.ProjectileTarget.ARROWS;
        }
        if (entity instanceof ItemEntity && !(itemEntity = (ItemEntity)entity).getStack().isEmpty()) {
            return Predictions.ProjectileTarget.ITEMS;
        }
        return null;
    }

    private void drawPath(VertexConsumerProvider.Immediate immediate, MatrixStack matrixStack, Vec3d vec3d, List<Vec3d> list, int n, int n2) {
        if (list.size() - n < 2) {
            return;
        }
        VertexConsumer vertexConsumer = immediate.getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);
        MatrixStack.Entry entry = matrixStack.peek();
        int n3 = Math.max(0, n);
        Vec3d vec3d2 = list.get(n3);
        Vec3d vec3d3 = this.pathOffset(list, n3, vec3d, null);
        double d = 0.0;
        int n4 = ColorUtil.multAlpha(n2, 0.0f);
        for (int i = n3 + 1; i < list.size(); ++i) {
            Vec3d vec3d4 = list.get(i);
            Vec3d vec3d5 = this.pathOffset(list, i, vec3d, vec3d3);
            float f = MathHelper.clamp((float)((float)((d += vec3d2.distanceTo(vec3d4)) / 6.0)), (float)0.0f, (float)1.0f);
            float f2 = f * f * (3.0f - 2.0f * f);
            int n5 = ColorUtil.multAlpha(n2, f2);
            if (ColorUtil.alpha(n4) > 0 || ColorUtil.alpha(n5) > 0) {
                this.emitRibbonSegment(vertexConsumer, entry, vec3d2, vec3d4, vec3d, vec3d3, vec3d5, n4, n5);
            }
            vec3d2 = vec3d4;
            vec3d3 = vec3d5;
            n4 = n5;
        }
        immediate.draw(ClientPipelines.WORLD_PARTICLES_COLOR);
    }

    private void drawPath(VertexConsumerProvider.Immediate immediate, MatrixStack matrixStack, Vec3d vec3d, List<Vec3d> list, int n) {
        this.drawPath(immediate, matrixStack, vec3d, list, 0, n);
    }

    private Predictions.ProjectileProfile profileFor(Predictions.ProjectileTarget projectileTarget) {
        return switch (projectileTarget.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> PROFILE_PEARL;
            case 3 -> PROFILE_ITEM;
            case 1, 2 -> PROFILE_ARROW;
        };
    }

    private float frameDelta(long l) {
        if (this.lastParticleUpdateMs == 0L) {
            this.lastParticleUpdateMs = l;
            return 1.0f;
        }
        long l2 = Math.max(1L, Math.min(50L, l - this.lastParticleUpdateMs));
        this.lastParticleUpdateMs = l;
        return MathHelper.clamp((float)((float)l2 / 16.6667f), (float)0.25f, (float)3.0f);
    }

    private Vec3d pathOffset(List<Vec3d> list, int n, Vec3d vec3d, Vec3d vec3d2) {
        Vec3d vec3d3;
        Vec3d vec3d4 = list.get(Math.min(list.size() - 1, n + 1)).subtract(list.get(Math.max(0, n - 1))).normalize();
        Vec3d vec3d5 = vec3d4.crossProduct(vec3d3 = list.get(n).subtract(vec3d).normalize());
        if (vec3d5.lengthSquared() < 0.0025 && (vec3d5 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0))).lengthSquared() < 1.0E-8) {
            vec3d5 = vec3d4.crossProduct(new Vec3d(1.0, 0.0, 0.0));
        }
        if (vec3d2 != null && vec3d5.dotProduct(vec3d2) < 0.0) {
            vec3d5 = vec3d5.multiply(-1.0);
        }
        double d = list.get(n).distanceTo(vec3d);
        double d2 = Math.max(0.006, Math.min(0.14, d * 0.00115));
        return vec3d5.normalize().multiply(d2);
    }

    private void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry, double d, double d2, double d3, int n) {
        vertexConsumer.vertex(entry, (float)d, (float)d2, (float)d3).color(n);
    }

    private boolean simulate(Vec3d vec3d, Vec3d vec3d2, Predictions.ProjectileProfile projectileProfile, int n, Entity entity, double d, List<Vec3d> list) {
        list.clear();
        list.add(vec3d);
        Vec3d vec3d3 = vec3d;
        Vec3d vec3d4 = vec3d2;
        boolean bl = false;
        for (int i = 0; i < n; ++i) {
            Vec3d vec3d5 = vec3d3.add(vec3d4);
            BlockHitResult blockHitResult = this.mc.world.raycast(new RaycastContext(vec3d3, vec3d5, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
            if (blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS) {
                list.add(blockHitResult.getPos());
                bl = true;
                break;
            }
            list.add(vec3d5);
            vec3d3 = vec3d5;
            vec3d4 = vec3d4.multiply(projectileProfile.drag()).add(0.0, -projectileProfile.gravity(), 0.0);
            if (vec3d3.y < -128.0 || vec3d3.squaredDistanceTo(vec3d) > d) break;
        }
        return bl;
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        this.landingMarkers.clear();
        if (this.mc.player == null || this.mc.world == null) {
            this.particles.clear();
            this.lastParticleSpawnMs.clear();
            this.projectilePaths.clear();
            this.landingInterpolations.clear();
            return;
        }
        boolean bl = this.render.isSelected(RENDER_TRAJECTORY);
        boolean bl2 = this.render.isSelected(RENDER_PARTICLES);
        boolean bl3 = this.landingMarker.getValue();
        if (!bl3) {
            this.landingInterpolations.clear();
        }
        if (!bl && !bl2) {
            this.particles.clear();
            this.lastParticleSpawnMs.clear();
            return;
        }
        long l = System.currentTimeMillis();
        float f = this.frameDelta(l);
        double d = this.simulationLimitSqr();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        Camera camera = worldRenderEvent.getCamera() == null ? this.mc.gameRenderer.getCamera() : worldRenderEvent.getCamera();
        Vec3d vec3d = camera.getCameraPos();
        int n2 = this.trajectoryColor();
        for (Entity entity : this.mc.world.getEntities()) {
            Vec3d vec3d2;
            Predictions.ProjectileTarget projectileTarget = this.targetFor(entity);
            if (projectileTarget == null || !this.targets.isSelected(projectileTarget.settingName()) || !this.isInRange(entity) || !this.isOwnedByPlayer(entity) || !this.shouldRenderLive(entity, vec3d2 = entity.getVelocity())) continue;
            Predictions.ProjectileProfile projectileProfile = this.profileFor(projectileTarget);
            Vec3d vec3d3 = entity.getLerpedPos(worldRenderEvent.getPartialTicks());
            if (bl && this.mc.player.canSee(entity)) {
                ProjectilePath projectilePath = this.projectilePathFor(entity, projectileProfile, d);
                this.liveProjectileIds.add(entity.getId());
                this.simulationPoints.clear();
                this.simulationPoints.add(vec3d3);
                int n3 = vec3d3.squaredDistanceTo((Vec3d)projectilePath.points().getFirst()) < 1.0E-8 ? 1 : 0;
                this.simulationPoints.addAll(projectilePath.points().subList(n3, projectilePath.points().size()));
                this.drawPath(immediate, worldRenderEvent.getStack(), vec3d, this.simulationPoints, n2);
                this.addLandingMarker(worldRenderEvent, vec3d, entity.getId(), entity.age, projectilePath.points(), projectilePath.hit() && bl3, this.markerVisualFor(entity), n2);
            }
            if (!bl2) continue;
            this.spawnProjectileParticles(entity, vec3d3, vec3d2, l);
        }
        if (bl) {
            this.projectilePaths.keySet().removeIf(n -> !this.liveProjectileIds.contains(n));
            this.landingInterpolations.keySet().removeIf(n -> !this.liveProjectileIds.contains(n));
            this.liveProjectileIds.clear();
        } else {
            this.projectilePaths.clear();
            this.landingInterpolations.clear();
        }
        if (bl2) {
            this.updateParticles(f, l);
            this.drawParticles(worldRenderEvent, immediate, vec3d, camera.getRotation(), l);
            this.lastParticleSpawnMs.entrySet().removeIf(entry -> l - (Long)entry.getValue() > 2000L);
        } else {
            this.particles.clear();
            this.lastParticleSpawnMs.clear();
        }
    }

    @EventHandler
    private void onHudRender(HudRenderEvent hudRenderEvent) {
        if (this.landingMarkers.isEmpty()) {
            return;
        }
        DrawContext drawContext = hudRenderEvent.getGraphics();
        Render2D.beginFrame(drawContext);
        for (LandingMarker landingMarker : this.landingMarkers) {
            this.drawMarkerBackground(landingMarker);
        }
        Render2D.flush();
        for (LandingMarker landingMarker : this.landingMarkers) {
            this.drawMarkerItem(drawContext, landingMarker);
        }
    }

    private double randomRange(double d, double d2) {
        return d + (d2 - d) * this.random.nextDouble();
    }

    private void drawParticles(WorldRenderEvent worldRenderEvent, VertexConsumerProvider.Immediate immediate, Vec3d vec3d, Quaternionf quaternionf, long l) {
        if (this.particles.isEmpty() || this.mc.gameRenderer == null) {
            return;
        }
        MatrixStack matrixStack = worldRenderEvent.getStack();
        this.usedParticleRenderTypes.clear();
        for (PredictionParticle predictionParticle : this.particles) {
            this.renderParticle(matrixStack, immediate, vec3d, quaternionf, predictionParticle, l);
        }
        for (RenderLayer renderLayer : this.usedParticleRenderTypes) {
            immediate.draw(renderLayer);
        }
        this.usedParticleRenderTypes.clear();
    }

    private MarkerVisual markerVisualFor(Entity entity) {
        if (entity instanceof EnderPearlEntity) {
            return new MarkerVisual(Items.ENDER_PEARL.getDefaultStack(), MarkerKind.PEARL);
        }
        if (entity instanceof SnowballEntity) {
            return new MarkerVisual(Items.SNOWBALL.getDefaultStack(), MarkerKind.SNOWBALL);
        }
        if (entity instanceof EggEntity) {
            return new MarkerVisual(Items.EGG.getDefaultStack(), MarkerKind.ITEM);
        }
        if (entity instanceof TridentEntity) {
            return new MarkerVisual(Items.TRIDENT.getDefaultStack(), MarkerKind.TRIDENT);
        }
        if (entity instanceof PersistentProjectileEntity) {
            return new MarkerVisual(Items.ARROW.getDefaultStack(), MarkerKind.ARROW);
        }
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            return new MarkerVisual(itemEntity.getStack().copyWithCount(1), MarkerKind.ITEM);
        }
        return null;
    }

    private ProjectilePath projectilePathFor(Entity entity, Predictions.ProjectileProfile projectileProfile, double d) {
        ProjectilePath projectilePath = this.projectilePaths.get(entity.getId());
        if (projectilePath == null || projectilePath.tick() != entity.age) {
            boolean bl = this.simulate(entity.getEntityPos(), entity.getVelocity(), projectileProfile, 300, entity, d, this.simulationPoints);
            projectilePath = new ProjectilePath(new ArrayList<Vec3d>(this.simulationPoints), bl, entity.age);
            this.projectilePaths.put(entity.getId(), projectilePath);
        }
        return projectilePath;
    }

    private void updateParticles(float f, long l) {
        if (this.particles.isEmpty()) {
            return;
        }
        this.particles.removeIf(predictionParticle -> predictionParticle.dead(l));
        for (PredictionParticle predictionParticle2 : this.particles) {
            predictionParticle2.update(f);
        }
    }

    private void drawBillboard(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, Identifier identifier, Vec3d vec3d, Vec3d vec3d2, Quaternionf quaternionf, float f, float f2, int n) {
        if (identifier == null || f <= 0.001f || ColorUtil.alpha(n) <= 0) {
            return;
        }
        RenderLayer renderLayer = RenderLayers.entityTranslucentEmissive(identifier);
        this.usedParticleRenderTypes.add(renderLayer);
        VertexConsumer vertexConsumer = immediate.getBuffer(renderLayer);
        matrixStack.push();
        matrixStack.translate(vec3d.x - vec3d2.x, vec3d.y - vec3d2.y, vec3d.z - vec3d2.z);
        matrixStack.multiply((Quaternionfc)quaternionf);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f2));
        MatrixStack.Entry entry = matrixStack.peek();
        float f3 = f * 0.5f;
        WorldVertex.textured((VertexConsumer)vertexConsumer, (MatrixStack.Entry)entry, (float)(-f3), (float)(-f3), (float)0.0f, (float)0.0f, (float)0.0f, (int)n);
        WorldVertex.textured((VertexConsumer)vertexConsumer, (MatrixStack.Entry)entry, (float)f3, (float)(-f3), (float)0.0f, (float)1.0f, (float)0.0f, (int)n);
        WorldVertex.textured((VertexConsumer)vertexConsumer, (MatrixStack.Entry)entry, (float)f3, (float)f3, (float)0.0f, (float)1.0f, (float)1.0f, (int)n);
        WorldVertex.textured((VertexConsumer)vertexConsumer, (MatrixStack.Entry)entry, (float)(-f3), (float)f3, (float)0.0f, (float)0.0f, (float)1.0f, (int)n);
        matrixStack.pop();
    }

    private Identifier particleTexture() {
        String string = this.particleTexture.getValue();
        if (TEXTURE_RANDOM.equals(string)) {
            string = PARTICLE_TEXTURE_MODES[this.random.nextInt(PARTICLE_TEXTURE_MODES.length - 1)];
        }
        return switch (string) {
            case TEXTURE_STAR -> PARTICLE_STAR;
            case TEXTURE_HEART -> PARTICLE_HEART;
            case TEXTURE_LIGHTNING -> PARTICLE_LIGHTNING;
            case TEXTURE_TRIANGLE -> PARTICLE_TRIANGLE;
            case TEXTURE_CROSS -> PARTICLE_CROSS;
            case TEXTURE_CROWN -> PARTICLE_CROWN;
            case TEXTURE_DOLLAR -> PARTICLE_DOLLAR;
            case TEXTURE_RHOMBUS, TEXTURE_SQUARE -> PARTICLE_SQUARE;
            case TEXTURE_SNOWFLAKE -> PARTICLE_SNOWFLAKE;
            case TEXTURE_SKULL -> PARTICLE_SKULL;
            case TEXTURE_CUBE -> PARTICLE_CUBE;
            default -> PARTICLE_GLOW;
        };
    }

    private int trajectoryColor() {
        return this.colorMode.is(COLOR_CLIENT) ? ClientAccent.accent(230.0f) : this.lineColor.getValue();
    }

    private void addLandingMarker(WorldRenderEvent worldRenderEvent, Vec3d vec3d, int n, int n2, List<Vec3d> list, boolean bl, MarkerVisual markerVisual, int n3) {
        if (!bl || list.isEmpty()) {
            return;
        }
        Vec3d vec3d2 = list.getLast();
        LandingInterpolation landingInterpolation = this.landingInterpolations.get(n);
        if (landingInterpolation == null) {
            landingInterpolation = new LandingInterpolation(vec3d2, vec3d2, n2);
        } else if (landingInterpolation.tick() != n2) {
            landingInterpolation = new LandingInterpolation(landingInterpolation.current(), vec3d2, n2);
        }
        this.landingInterpolations.put(n, landingInterpolation);
        float f = MathHelper.clamp((float)worldRenderEvent.getPartialTicks(), (float)0.0f, (float)1.0f);
        Vec3d vec3d3 = landingInterpolation.previous().lerp(landingInterpolation.current(), (double)f).subtract(vec3d);
        Vector4f vector4f = this.projectionScratch.set((float)vec3d3.x, (float)vec3d3.y, (float)vec3d3.z, 1.0f);
        worldRenderEvent.getPositionMatrix().transform(vector4f);
        worldRenderEvent.getProjectionMatrix().transform(vector4f);
        if (vector4f.w <= 1.0E-4f) {
            return;
        }
        float f2 = Position.screenWidth();
        float f3 = Position.screenHeight();
        float f4 = (vector4f.x / vector4f.w * 0.5f + 0.5f) * f2;
        float f5 = (1.0f - (vector4f.y / vector4f.w * 0.5f + 0.5f)) * f3;
        float f6 = 21.0f;
        if (!Float.isFinite(f4) || !Float.isFinite(f5) || f4 < -f6 || f4 > f2 + f6 || f5 < -f6 || f5 > f3 + f6) {
            return;
        }
        ItemStack itemStack = markerVisual != null && this.markerItems.isSelected(markerVisual.kind().settingName()) ? markerVisual.icon().copyWithCount(1) : ItemStack.EMPTY;
        this.landingMarkers.add(new LandingMarker(f4, f5, itemStack, n3));
    }

    private void emitRibbonSegment(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3, Vec3d vec3d4, Vec3d vec3d5, int n, int n2) {
        Vec3d vec3d6 = vec3d.subtract(vec3d3);
        Vec3d vec3d7 = vec3d2.subtract(vec3d3);
        this.vertex(vertexConsumer, entry, vec3d6.x - vec3d4.x, vec3d6.y - vec3d4.y, vec3d6.z - vec3d4.z, n);
        this.vertex(vertexConsumer, entry, vec3d6.x + vec3d4.x, vec3d6.y + vec3d4.y, vec3d6.z + vec3d4.z, n);
        this.vertex(vertexConsumer, entry, vec3d7.x + vec3d5.x, vec3d7.y + vec3d5.y, vec3d7.z + vec3d5.z, n2);
        this.vertex(vertexConsumer, entry, vec3d7.x - vec3d5.x, vec3d7.y - vec3d5.y, vec3d7.z - vec3d5.z, n2);
    }

    private boolean particlesEnabled() {
        return this.render.isSelected(RENDER_PARTICLES);
    }

    private void drawMarkerItem(DrawContext drawContext, LandingMarker landingMarker) {
        if (landingMarker.icon().isEmpty()) {
            return;
        }
        float f = landingMarker.y() - 10.5f - 7.0f + 1.5f;
        drawContext.getMatrices().pushMatrix();
        Render2DCoordinateSpace.applyGuiScaleIndependence((Matrix3x2f)drawContext.getMatrices());
        drawContext.getMatrices().translate(landingMarker.x(), f);
        drawContext.getMatrices().scale(0.5f, 0.5f);
        drawContext.getMatrices().translate(-8.0f, -8.0f);
        drawContext.drawItem(landingMarker.icon(), 0, 0);
        drawContext.getMatrices().popMatrix();
    }

    private boolean isOwnedByPlayer(Entity entity) {
        Entity entity2;
        if (this.mc.player == null) {
            return false;
        }
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            entity2 = itemEntity.getOwner();
        } else if (entity instanceof ProjectileEntity) {
            ProjectileEntity projectileEntity = (ProjectileEntity)entity;
            entity2 = projectileEntity.getOwner();
        } else {
            return false;
        }
        return entity2 == this.mc.player;
    }

    private double simulationLimitSqr() {
        double d = this.range.getFloat();
        return d * d * 4.0;
    }

    private int particleBaseColor() {
        if (this.randomParticleColor.getValue()) {
            return RainbowLut.sample((int)((int)(this.random.nextFloat() * 360.0f)), (float)0.78f, (float)1.0f) | 0xFF000000;
        }
        return this.colorMode.is(COLOR_CLIENT) ? ClientAccent.accent(215.0f) : this.particleColor.getValue();
    }

    private void renderParticle(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, Vec3d vec3d, Quaternionf quaternionf, PredictionParticle predictionParticle, long l) {
        float f = predictionParticle.alpha(l);
        if (f <= 0.01f) {
            return;
        }
        float f2 = this.particleSize.getFloat() * predictionParticle.scale() * (0.55f + f * 0.45f);
        int n = ColorUtil.multAlpha(predictionParticle.color(), f);
        float f3 = this.particleSpin.getValue() ? predictionParticle.rotation() : 3.15f;
        this.drawBillboard(matrixStack, immediate, predictionParticle.texture(), predictionParticle.position(), vec3d, quaternionf, f2, f3, n);
        if (this.particleWhiteCenter.getValue()) {
            this.drawBillboard(matrixStack, immediate, predictionParticle.texture(), predictionParticle.position(), vec3d, quaternionf, f2 * 0.42f, f3, ColorUtil.rgba(255, 255, 255, Math.round(230.0f * f)));
        }
    }

    private boolean isGroundedItem(ItemEntity itemEntity, Vec3d vec3d) {
        Vec3d vec3d2;
        if (itemEntity.isOnGround()) {
            return true;
        }
        if (this.mc.world == null) {
            return false;
        }
        Box box = itemEntity.getBoundingBox();
        Vec3d vec3d3 = new Vec3d(itemEntity.getX(), box.minY + 0.08, itemEntity.getZ());
        BlockHitResult blockHitResult = this.mc.world.raycast(new RaycastContext(vec3d3, vec3d2 = new Vec3d(itemEntity.getX(), box.minY - 0.16, itemEntity.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)itemEntity));
        boolean bl = blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS;
        return bl && vec3d.y > -0.08;
    }

    private boolean shouldRenderLive(Entity entity, Vec3d vec3d) {
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            return !this.isGroundedItem(itemEntity, vec3d) && vec3d.lengthSquared() > 1.0E-5;
        }
        return vec3d.lengthSquared() > 1.0E-5;
    }

    private void spawnProjectileParticles(Entity entity, Vec3d vec3d, Vec3d vec3d2, long l) {
        int n;
        long l2 = this.lastParticleSpawnMs.getOrDefault(entity.getId(), 0L);
        if (l - l2 < 42L) {
            return;
        }
        this.lastParticleSpawnMs.put(entity.getId(), l);
        int n2 = Math.max(1, Math.round(this.particleCount.getFloat()));
        double d = Math.max(0.12, (double)entity.getWidth() * 0.8);
        double d2 = Math.max(0.12, (double)entity.getHeight());
        double d3 = (double)this.particleSpeed.getFloat() / 18.0;
        for (n = 0; n < n2; ++n) {
            Vec3d vec3d3 = vec3d.add(this.randomRange(-d, d), this.random.nextDouble() * d2, this.randomRange(-d, d));
            Vec3d vec3d4 = new Vec3d(this.randomRange(-d3, d3), this.randomRange(-d3, d3), this.randomRange(-d3, d3)).add(vec3d2.multiply(-0.018));
            this.particles.add(new PredictionParticle(this.particleTexture(), vec3d3, vec3d4, l, (long)Math.round(this.particleLifetime.getFloat()), this.particleBaseColor(), this.random.nextFloat() * 360.0f, (float)this.randomRange(-5.0, 5.0), 0.78f + this.random.nextFloat() * 0.5f));
        }
        n = this.particles.size() - 650;
        if (n > 0) {
            this.particles.subList(0, n).clear();
        }
    }

    private void drawMarkerBackground(LandingMarker landingMarker) {
        float f = landingMarker.x();
        float f2 = landingMarker.y() - 10.5f - 7.0f + 1.5f;
        float f3 = f2 + 10.5f - 1.0f;
        int n = ColorUtil.withAlpha(landingMarker.color(), 255);
        Render2D.shape(BuiltShape.downwardTriangle((float)(f - 5.5f), (float)f3, (float)11.0f, (float)7.0f, (int)n));
        Render2D.circle(f, f2, 10.5f, ColorUtil.rgba(7, 10, 14, 255));
        Render2D.circleOutline(f, f2, 10.5f, 1.5f, n);
        if (landingMarker.icon().isEmpty()) {
            Render2D.rect(f - 2.0f, f2 - 2.0f, 4.0f, 4.0f, n);
        }
    }


    public static record ProjectileProfile(double drag, double gravity) {
    }
    
        public static enum ProjectileTarget {
        PEARLS("\u041f\u0451\u0440\u043b\u044b"),
        ARROWS("\u0421\u0442\u0440\u0435\u043b\u044b"),
        TRIDENTS("\u0422\u0440\u0435\u0437\u0443\u0431\u0446\u044b"),
        ITEMS("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b");
    
        private final String settingName;
    
        private ProjectileTarget(String settingName) {
            this.settingName = settingName;
        }
    
        private String settingName() {
            return this.settingName;
        }
    }


    public static final class PredictionParticle {
        private final Identifier texture;
        private Vec3d position;
        private final Vec3d velocity;
        private final long spawnMs;
        private final long lifetimeMs;
        private final int color;
        private float rotation;
        private final float rotSpeed;
        private final float scale;

        public PredictionParticle(Identifier texture, Vec3d position, Vec3d velocity, long spawnMs, long lifetimeMs, int color, float rotation, float rotSpeed, float scale) {
            this.texture = texture;
            this.position = position;
            this.velocity = velocity;
            this.spawnMs = spawnMs;
            this.lifetimeMs = lifetimeMs;
            this.color = color;
            this.rotation = rotation;
            this.rotSpeed = rotSpeed;
            this.scale = scale;
        }

        public boolean dead(long now) {
            return now - this.spawnMs >= this.lifetimeMs;
        }

        public void update(float tickDelta) {
            this.position = this.position.add(this.velocity.multiply(tickDelta));
            this.rotation += this.rotSpeed * tickDelta;
        }

        public float alpha(long now) {
            long elapsed = now - this.spawnMs;
            if (elapsed >= this.lifetimeMs) return 0.0f;
            return 1.0f - (float) elapsed / (float) this.lifetimeMs;
        }

        public float scale() { return this.scale; }
        public int color() { return this.color; }
        public float rotation() { return this.rotation; }
        public Identifier texture() { return this.texture; }
        public Vec3d position() { return this.position; }
    }

    public static record ProjectilePath(List<Vec3d> points, boolean hit, int tick) {}
    public static record LandingInterpolation(Vec3d previous, Vec3d current, int tick) {}
    public static record LandingMarker(float x, float y, ItemStack icon, int color) {}

    public enum MarkerKind {
        PEARL("Перл"),
        SNOWBALL("Снежок"),
        ITEM("Предмет"),
        TRIDENT("Трезубец"),
        ARROW("Стрела");

        private final String settingName;
        MarkerKind(String settingName) { this.settingName = settingName; }
        public String settingName() { return this.settingName; }
    }

    public static record MarkerVisual(ItemStack icon, MarkerKind kind) {}
}