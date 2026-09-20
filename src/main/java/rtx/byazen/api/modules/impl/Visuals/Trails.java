package rtx.byazen.api.modules.impl.Visuals;
import rtx.byazen.api.events.EventHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.color.RainbowLut;
import rtx.byazen.utils.math.MathUtils;
import rtx.byazen.utils.render.pipeline.ClientPipelines;
import rtx.byazen.utils.render.post.scarglass.ScarGlassRenderer;
import rtx.byazen.utils.render.render2d.ClientPalette;

public class Trails
extends Module {
    private static final Identifier GLOW_TEXTURE;
    private static final int CLIENT_COLOR_FIRST;
    private static final int CLIENT_COLOR_SECOND;
    private static final int DARK_SECOND_COLOR;
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final String MODE_NORMAL = "\u041e\u0431\u044b\u0447\u043d\u044b\u0439";
    private static final String MODE_DUPLICATES = "\u041e\u0441\u043a\u043e\u043b\u043a\u0438";
    private static final String SPAWN_SHARDS = "\u041e\u0441\u043a\u043e\u043b\u043a\u0438";
    private static final String SPAWN_RIBBONS = "\u041b\u0438\u043d\u0438\u0438";
    private static final String SPAWN_BOTH = "\u041e\u0431\u0430";
    private static final float SPARK_COUNT = 5.0f;
    private static final long FADE_IN_MS = 80L;
    private static final double LENGTH_TO_MS = 8.0;
    private static final double MIN_ADD_DISTANCE_SQ = 0.01;
    private static final int MAX_POINTS = 512;
    private final SeparatorSetting generalSeparator = this.register(new SeparatorSetting("\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435"));
    private final ModeSetting mode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0420\u0435\u0436\u0438\u043c \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u0441\u043b\u0435\u0434\u0430.", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041e\u0441\u043a\u043e\u043b\u043a\u0438"));
    private final SliderSetting length = this.register(new SliderSetting("\u0414\u043b\u0438\u043d\u0430", "\u0414\u043b\u0438\u043d\u0430 \u0445\u0432\u043e\u0441\u0442\u0430 (\u041e\u0431\u044b\u0447\u043d\u044b\u0439) / \u0432\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432.").range(15.0f, 25.0f).increment(1.0f).setValue(20.0f));
    private final SliderSetting size = this.register(new SliderSetting("\u0420\u0430\u0437\u043c\u0435\u0440", "\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u043f\u0440\u0430\u0439\u0442\u0430 \u0445\u0432\u043e\u0441\u0442\u0430.").range(0.3f, 1.0f).increment(0.05f).setValue(0.55f).visible(() -> !this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SeparatorSetting shardSeparator = this.register(new SeparatorSetting("\u041e\u0441\u043a\u043e\u043b\u043a\u0438").visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final ModeSetting dupSpawn = this.register(new ModeSetting("\u0427\u0442\u043e \u0441\u043f\u0430\u0432\u043d\u0438\u0442\u044c", "\u0427\u0442\u043e \u0440\u043e\u0436\u0434\u0430\u0442\u044c \u043d\u0430 \u0432\u0441\u043f\u044b\u0448\u043a\u0443: \u043e\u0441\u043a\u043e\u043b\u043a\u0438, \u043b\u0438\u043d\u0438\u0438 \u0438\u043b\u0438 \u043e\u0431\u0430.", "\u041e\u0441\u043a\u043e\u043b\u043a\u0438", "\u041e\u0441\u043a\u043e\u043b\u043a\u0438", "\u041b\u0438\u043d\u0438\u0438", "\u041e\u0431\u0430"));
    private final SliderSetting dupBlur = this.register(new SliderSetting("\u0414\u043b\u0438\u043d\u0430 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432", "\u0414\u043b\u0438\u043d\u0430 \u044d\u043d\u0435\u0440\u0433\u0435\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0445 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432.").range(0.0f, 8.0f).increment(0.25f).setValue(3.0f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SliderSetting dupDensity = this.register(new SliderSetting("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", "\u041a\u0430\u043a \u0447\u0430\u0441\u0442\u043e \u0440\u043e\u0436\u0434\u0430\u044e\u0442\u0441\u044f \u0432\u0441\u043f\u044b\u0448\u043a\u0438. \u0412\u044b\u0448\u0435 = \u043f\u043b\u043e\u0442\u043d\u0435\u0435 \u043f\u043e\u0442\u043e\u043a, \u043d\u043e \u0434\u043e\u0440\u043e\u0436\u0435 \u043f\u043e FPS (\u0433\u043b\u0430\u0432\u043d\u044b\u0439 \u0440\u044b\u0447\u0430\u0433 \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438).").range(1.0f, 10.0f).increment(1.0f).setValue(6.0f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SliderSetting dupAlpha = this.register(new SliderSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", "\u0412\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c \u0434\u0443\u0431\u043b\u0438\u043a\u0430\u0442\u043e\u0432.").range(0.1f, 1.0f).increment(0.05f).setValue(0.7f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SliderSetting dupTint = this.register(new SliderSetting("\u041f\u043e\u0434\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u043d\u0438\u0435", "\u0421\u0438\u043b\u0430 \u043f\u043e\u0434\u043a\u0440\u0430\u0441\u043a\u0438 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432 \u0446\u0432\u0435\u0442\u043e\u043c (0 = \u0431\u0435\u043b\u044b\u0435, 100 = \u043f\u043e\u043b\u043d\u044b\u0439 \u0446\u0432\u0435\u0442).").range(0.0f, 100.0f).increment(1.0f).setValue(100.0f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SeparatorSetting dupGlassSeparator = this.register(new SeparatorSetting("\u0421\u0442\u0435\u043a\u043b\u043e").visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SliderSetting dupDistort = this.register(new SliderSetting("\u0418\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u0435", "\u0421\u0438\u043b\u0430 \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u043e\u0433\u043e \u0438\u0441\u043a\u0440\u0438\u0432\u043b\u0435\u043d\u0438\u044f \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0430 \u0441\u043a\u0432\u043e\u0437\u044c \u0434\u0443\u0431\u043b\u0438\u043a\u0430\u0442\u044b.").range(0.0f, 3.0f).increment(0.1f).setValue(1.0f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SliderSetting dupReflect = this.register(new SliderSetting("\u041e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u0435", "\u0421\u0438\u043b\u0430 \u043e\u0442\u0440\u0430\u0436\u0435\u043d\u0438\u044f \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0430 (\u0441\u0442\u0435\u043a\u043b\u043e).").range(0.0f, 1.0f).increment(0.05f).setValue(0.4f).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SeparatorSetting sparkSeparator = this.register(new SeparatorSetting("\u0421\u043f\u0430\u0440\u043a\u0438").visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final BooleanSetting dupSparks = this.register(new BooleanSetting("\u0421\u043f\u0430\u0440\u043a\u0438", "\u0421\u043f\u0430\u0432\u043d\u0438\u0442\u044c \u0440\u043e\u0439 \u0438\u0441\u043a\u0440 \u0432\u043e\u043a\u0440\u0443\u0433 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432.", true).visible(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")));
    private final SeparatorSetting colorSeparator = this.register(new SeparatorSetting("\u0426\u0432\u0435\u0442"));
    private final ModeSetting colorMode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430 \u0445\u0432\u043e\u0441\u0442\u0430.", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439"));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0432\u0442\u043e\u0440\u043e\u0439 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442.", false));
    private final ColorSetting customColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442 \u0445\u0432\u043e\u0441\u0442\u0430.", new Color(255, 255, 255, 255)));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("\u0426\u0432\u0435\u0442 2", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u0445\u0432\u043e\u0441\u0442\u0430.", new Color(ColorUtil.lerpColor(-1, DARK_SECOND_COLOR, 0.7f), true)));
    private final List<TailPoint> points = new ArrayList<TailPoint>();
    private Vec3d lastAdded;
    private static final double SCAR_SPAWN_DIST = 0.2;
    private static final double[][] BODY_CUBOIDS;
    private static final float[][] SHARD_FACES;
    private static final float SHARD_TOTAL_AREA;
    private static final float TAU = (float)Math.PI * 2;
    private static final int TRIG_BITS = 13;
    private static final int TRIG_SIZE = 8192;
    private static final int TRIG_MASK = 8191;
    private static final int TRIG_QUARTER = 2048;
    private static final float TRIG_SCALE = 1303.7972f;
    private static final float[] SIN_LUT;
    private static final float[] BEAM_BOW;
    private static final long SCAR_MIN_INTERVAL_MS = 12L;
    private final List<ScarBurst> bursts = new ArrayList<ScarBurst>();
    private final float[] scarGlassData = new float[1164];
    private final Trails.ColorCtx colorCtx = new Trails.ColorCtx(this);
    private final Matrix4f scarViewProj = new Matrix4f();
    private final Vector4f scarProjScratch = new Vector4f();
    private Vec3d lastBurstPos;
    private long lastBurstMs;
    private long burstSeq;
    private static final Trails.Shard[] EMPTY_SHARDS;
    private static final Trails.Ribbon[] EMPTY_RIBBONS;
    private static final Trails.Spark[] EMPTY_SPARKS;

    public Trails() {
        super("Trails", "\u041e\u0441\u0442\u0430\u0432\u043b\u044f\u0435\u0442 \u0441\u043b\u0435\u0434 \u0437\u0430 \u0432\u0430\u043c\u0438 (\u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0438\u043b\u0438 \u044d\u043d\u0435\u0440\u0433\u0435\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u043e\u0441\u043a\u043e\u043b\u043a\u0438).", Category.VISUALS);
        this.dupSpawn.visibleWhen(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438"));
        this.dupSparks.visibleWhen(() -> this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438"));
        this.colorMode.visibleWhen(this::colorVisible);
        this.useSecondColor.visibleWhen(() -> this.colorVisible() && this.colorMode.is(COLOR_CUSTOM));
        this.customColor.visibleWhen(() -> this.colorVisible() && this.colorMode.is(COLOR_CUSTOM));
        this.customSecondColor.visibleWhen(() -> this.colorVisible() && this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue());
    }

    static {
        int n;
        GLOW_TEXTURE = Trails.id("textures/particle/glow.png");
        CLIENT_COLOR_FIRST = ColorUtil.rgba(127, 242, 255, 255);
        CLIENT_COLOR_SECOND = ColorUtil.rgba(255, 50, 150, 255);
        DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
        BODY_CUBOIDS = new double[][]{{-0.25, 1.22, -0.25, 0.25, 1.72, 0.25}, {-0.25, 0.72, -0.125, 0.25, 1.22, 0.125}, {-0.43, 0.72, -0.105, -0.25, 1.22, 0.105}, {0.25, 0.72, -0.105, 0.43, 1.22, 0.105}, {-0.24, 0.0, -0.105, -0.02, 0.72, 0.105}, {0.02, 0.0, -0.105, 0.24, 0.72, 0.105}};
        ArrayList<float[]> arrayList = new ArrayList<float[]>();
        for (double[] dArray : BODY_CUBOIDS) {
            float f = (float)dArray[0];
            float f2 = (float)dArray[1];
            float f3 = (float)dArray[2];
            float f4 = (float)dArray[3];
            float f5 = (float)dArray[4];
            float f6 = (float)dArray[5];
            float f7 = f4 - f;
            float f8 = f5 - f2;
            float f9 = f6 - f3;
            arrayList.add(Trails.shardFace(f, f2, f3, 0.0f, f8, 0.0f, 0.0f, 0.0f, f9, -1.0f, 0.0f, 0.0f));
            arrayList.add(Trails.shardFace(f4, f2, f3, 0.0f, f8, 0.0f, 0.0f, 0.0f, f9, 1.0f, 0.0f, 0.0f));
            arrayList.add(Trails.shardFace(f, f2, f3, f7, 0.0f, 0.0f, 0.0f, 0.0f, f9, 0.0f, -1.0f, 0.0f));
            arrayList.add(Trails.shardFace(f, f5, f3, f7, 0.0f, 0.0f, 0.0f, 0.0f, f9, 0.0f, 1.0f, 0.0f));
            arrayList.add(Trails.shardFace(f, f2, f3, f7, 0.0f, 0.0f, 0.0f, f8, 0.0f, 0.0f, 0.0f, -1.0f));
            arrayList.add(Trails.shardFace(f, f2, f6, f7, 0.0f, 0.0f, 0.0f, f8, 0.0f, 0.0f, 0.0f, 1.0f));
        }
        SHARD_FACES = arrayList.toArray(new float[0][]);
        float f = 0.0f;
        for (float[] fArray : SHARD_FACES) {
            f += fArray[12];
        }
        SHARD_TOTAL_AREA = f;
        SIN_LUT = new float[8192];
        for (n = 0; n < 8192; ++n) {
            Trails.SIN_LUT[n] = (float)Math.sin((double)n * (Math.PI * 2) / 8192.0);
        }
        BEAM_BOW = new float[4];
        for (n = 0; n <= 3; ++n) {
            f = (float)n / 3.0f;
            Trails.BEAM_BOW[n] = (float)Math.sin((double)f * Math.PI);
        }
        EMPTY_SHARDS = new Trails.Shard[0];
        EMPTY_RIBBONS = new Trails.Ribbon[0];
        EMPTY_SPARKS = new Trails.Spark[0];
    }

    private static Identifier id(String string) {
        return Identifier.of((String)"byazen", (String)string);
    }

    private static int rainbow(int n, int n2, float f, float f2, float f3) {
        int n3 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        int n4 = RainbowLut.sample((int)n3, (float)f, (float)f2);
        return ColorUtil.rgba(n4 >>> 16 & 0xFF, n4 >>> 8 & 0xFF, n4 & 0xFF, Math.round(MathHelper.clamp((float)f3, (float)0.0f, (float)1.0f) * 255.0f));
    }

    private static int fade(int n, int n2, int n3, int n4) {
        int n5 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        n5 = n5 >= 180 ? 360 - n5 : n5;
        return ColorUtil.lerpColor(n3, n4, (float)n5 / 180.0f);
    }

    @Override
    protected void onDisable() {
        this.points.clear();
        this.bursts.clear();
        ScarGlassRenderer.clear();
        this.lastAdded = null;
        this.lastBurstPos = null;
        this.lastBurstMs = 0L;
    }

    private static float[] shardFace(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12) {
        float f13 = f5 * f9 - f6 * f8;
        float f14 = f6 * f7 - f4 * f9;
        float f15 = f4 * f8 - f5 * f7;
        float f16 = (float)Math.sqrt(f13 * f13 + f14 * f14 + f15 * f15);
        return new float[]{f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f16};
    }

    private void spawnBurst() {
        if (this.mc.player == null) {
            return;
        }
        Vec3d vec3d = MathUtils.interpolate((Entity)(Object)this.mc.player);
        if (!(Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z))) {
            return;
        }
        long l = System.currentTimeMillis();
        boolean bl = this.lastBurstPos == null || this.lastBurstPos.distanceTo(vec3d) >= 0.2;
        long l2 = Math.max(12L, (long)(200.0f / Math.max(1.0f, this.dupDensity.getFloat())));
        if (bl && l - this.lastBurstMs >= l2) {
            double d;
            double d2;
            double d3;
            this.lastBurstMs = l;
            if (this.lastBurstPos != null) {
                d3 = vec3d.x - this.lastBurstPos.x;
                d2 = vec3d.y - this.lastBurstPos.y;
                d = vec3d.z - this.lastBurstPos.z;
            } else {
                Vec3d vec3d2 = this.mc.player.getVelocity();
                d3 = vec3d2.x;
                d2 = vec3d2.y;
                d = vec3d2.z;
            }
            double d4 = Math.sqrt(d3 * d3 + d * d);
            float f = d4 > 1.0E-4 ? (float)(d3 / d4) : 1.0f;
            float f2 = d4 > 1.0E-4 ? (float)(d / d4) : 0.0f;
            float f3 = (float)MathHelper.clamp((double)(d2 / Math.max(d4, 0.05)), (double)-0.6, (double)0.6);
            this.bursts.add(new ScarBurst(vec3d.x, vec3d.y, vec3d.z, System.currentTimeMillis(), this.burstSeq++, f, f3, f2, this.mc.player.bodyYaw, (float)d4));
            this.lastBurstPos = vec3d;
            if (this.bursts.size() > 64) {
                this.bursts.remove(0);
            }
        }
    }

    private static void quad(VertexConsumer vertexConsumer, MatrixStack.Entry entry, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n) {
        vertexConsumer.vertex(entry, f - f4 - f7, f2 - f5 - f8, f3 - f6 - f9).color(n);
        vertexConsumer.vertex(entry, f - f4 + f7, f2 - f5 + f8, f3 - f6 + f9).color(n);
        vertexConsumer.vertex(entry, f + f4 + f7, f2 + f5 + f8, f3 + f6 + f9).color(n);
        vertexConsumer.vertex(entry, f + f4 - f7, f2 + f5 - f8, f3 + f6 - f9).color(n);
    }

    private int getColorBase(int n, float f) {
        int n2;
        int n3;
        if (this.colorMode.is(COLOR_RAINBOW)) {
            return Trails.rainbow(8, n, 1.0f, 1.0f, f);
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            int[] nArray = ClientPalette.colors();
            if (nArray != null && nArray.length >= 2) {
                return ColorUtil.multAlpha(Trails.paletteFade(8, n, nArray), f);
            }
            InterfaceModule interfaceModule = InterfaceModule.getInstance();
            if (interfaceModule != null) {
                n3 = interfaceModule.clientPrimaryColorOpaque();
                n2 = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : n3;
            } else {
                n3 = Trails.getClientColor();
                n2 = ColorUtil.lerpColor(n3, DARK_SECOND_COLOR, 0.7f);
            }
        } else {
            n3 = this.customColor.getColor();
            int n4 = n2 = this.useSecondColor.getValue() ? this.customSecondColor.getColor() : this.customColor.getColor();
        }
        if (n3 == n2) {
            return ColorUtil.multAlpha(n3, f);
        }
        return ColorUtil.multAlpha(Trails.fade(8, n, n3, n2), f);
    }

    private boolean colorVisible() {
        return this.mode.is(MODE_NORMAL) || this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438");
    }

    private static void projectScarSegments(float[] fArray, int n, Matrix4f matrix4f, Vector4f vector4f, float f, float f2) {
        for (int i = 0; i < n; ++i) {
            int n2 = 12 + i * 12;
            float f3 = fArray[n2 + 3];
            float f4 = fArray[n2 + 7];
            vector4f.set(fArray[n2], fArray[n2 + 1], fArray[n2 + 2], 1.0f);
            matrix4f.transform(vector4f);
            boolean bl = vector4f.w > 1.0E-4f;
            float f5 = 0.0f;
            float f6 = 0.0f;
            float f7 = 0.0f;
            if (bl) {
                float f8 = 1.0f / vector4f.w;
                f5 = vector4f.x * f8 * 0.5f + 0.5f;
                f6 = vector4f.y * f8 * 0.5f + 0.5f;
                f7 = Trails.linDepth(vector4f.z * f8 * 0.5f + 0.5f, f, f2);
            }
            vector4f.set(fArray[n2 + 4], fArray[n2 + 5], fArray[n2 + 6], 1.0f);
            matrix4f.transform(vector4f);
            boolean bl2 = vector4f.w > 1.0E-4f;
            float f9 = 0.0f;
            float f10 = 0.0f;
            float f11 = 0.0f;
            if (bl2) {
                float f12 = 1.0f / vector4f.w;
                f9 = vector4f.x * f12 * 0.5f + 0.5f;
                f10 = vector4f.y * f12 * 0.5f + 0.5f;
                f11 = Trails.linDepth(vector4f.z * f12 * 0.5f + 0.5f, f, f2);
            }
            if (!bl || !bl2) {
                fArray[n2 + 7] = 0.0f;
                continue;
            }
            fArray[n2] = f5;
            fArray[n2 + 1] = f6;
            fArray[n2 + 2] = f7;
            fArray[n2 + 3] = f3;
            fArray[n2 + 4] = f9;
            fArray[n2 + 5] = f10;
            fArray[n2 + 6] = f11;
            fArray[n2 + 7] = f4;
        }
    }

    private void renderScarBursts3D(WorldRenderEvent worldRenderEvent) {
        float f;
        float f2;
        ScarBurst scarBurst;
        long l = System.currentTimeMillis();
        long l2 = (long)(this.length.getFloat() * 50.0f);
        for (int i = this.bursts.size() - 1; i >= 0; --i) {
            if (l - this.bursts.get((int)i).spawnMs <= l2) continue;
            this.bursts.remove(i);
        }
        if (this.bursts.isEmpty() || worldRenderEvent.getCamera() == null) {
            return;
        }
        Vec3d vec3d = worldRenderEvent.getCamera().getCameraPos();
        double d = vec3d.x;
        double d2 = vec3d.y;
        double d3 = vec3d.z;
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        RenderLayer renderLayer = ClientPipelines.WORLD_PARTICLES_GLOW;
        VertexConsumer vertexConsumer = immediate.getBuffer(renderLayer);
        MatrixStack.Entry entry = worldRenderEvent.getStack().peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        float f3 = matrix4f.m00();
        float f4 = matrix4f.m10();
        float f5 = matrix4f.m20();
        float f6 = matrix4f.m01();
        float f7 = matrix4f.m11();
        float f8 = matrix4f.m21();
        Trails.ColorCtx colorCtx = this.colorCtx;
        colorCtx.update(l);
        float f9 = this.dupAlpha.getFloat();
        float f10 = 0.5f + this.dupBlur.getFloat() * 0.03f;
        boolean bl = this.dupSparks.getValue();
        float f11 = 5.0f;
        float f12 = (float)((double)(l % 100000L) / 1000.0);
        float f13 = f12 * ((float)Math.PI * 2);
        int n = this.dupSpawn.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438") || this.dupSpawn.is(SPAWN_BOTH) ? 1 : 0;
        int n2 = this.dupSpawn.is(SPAWN_RIBBONS) || this.dupSpawn.is(SPAWN_BOTH) ? 1 : 0;
        int n3 = 0;
        boolean bl2 = this.dupDistort.getFloat() > 0.001f || this.dupReflect.getFloat() > 0.001f;
        float[] fArray = bl2 ? this.scarGlassData : null;
        int n4 = this.bursts.size();
        for (int i = 0; i < n4; ++i) {
            int n5;
            float f14;
            float f15;
            float f16;
            float f17;
            float f18;
            float f19;
            Object[] objectArray;
            scarBurst = this.bursts.get(i);
            f2 = (float)(l - scarBurst.spawnMs) / (float)l2;
            f = MathHelper.clamp((float)(f2 / 0.15f), (float)0.0f, (float)1.0f) * (1.0f - MathHelper.clamp((float)((f2 - 0.55f) / 0.45f), (float)0.0f, (float)1.0f));
            if (f <= 0.01f) continue;
            scarBurst.build();
            float f20 = scarBurst.dirX;
            float f21 = scarBurst.dirZ;
            float f22 = f2 * f2 * 0.5f;
            float f23 = (float)(scarBurst.x - d) - f20 * f22;
            float f24 = (float)(scarBurst.y - d2);
            float f25 = (float)(scarBurst.z - d3) - f21 * f22;
            if (n > 0) {
                objectArray = scarBurst.ensureShards(n);
                for (int j = 0; j < n; ++j) {
                    Object object = objectArray[j];
                    float f26 = f23 + ((Trails.Shard)object).ox;
                    f19 = f24 + ((Trails.Shard)object).oy;
                    f18 = f25 + ((Trails.Shard)object).oz;
                    f17 = (float)Math.sqrt(f26 * f26 + f19 * f19 + f18 * f18);
                    f16 = MathHelper.clamp((float)((f17 - f10 - 0.2f) / 1.0f), (float)0.0f, (float)1.0f);
                    if (f16 <= 0.0f) continue;
                    f15 = f10 * ((Trails.Shard)object).sizeMul;
                    f14 = MathHelper.clamp((float)(f * f9 * f16 * ((Trails.Shard)object).heroMul), (float)0.0f, (float)1.0f);
                    n5 = colorCtx.color(((Trails.Shard)object).colorIndex, f14);
                    n3 = Trails.emitBeam(vertexConsumer, entry, (Trails.Shard)object, f26, f19, f18, f15, n5, fArray, n3, f14, f * f16);
                    if (!bl) continue;
                    int n6 = Math.max(1, Math.round(f11 * ((Trails.Shard)object).sparkNBase));
                    Trails.Spark[] sparkArray = scarBurst.ensureSparks((Trails.Shard)object, n6);
                    for (int k = 0; k < n6; ++k) {
                        this.emitSpark(vertexConsumer, entry, sparkArray[k], colorCtx, (int)scarBurst.baseHue, f3, f4, f5, f6, f7, f8, f26, f19, f18, f20, f21, f2, f12, f13, f9, f * f16);
                    }
                }
            }
            if (n2 <= 0) continue;
            objectArray = scarBurst.ensureRibbons(n2);
            float f27 = MathHelper.clamp((float)(f2 / 0.3f), (float)0.0f, (float)1.0f);
            for (int j = 0; j < n2; ++j) {
                Object object = objectArray[j];
                f19 = f24 + ((Trails.Ribbon)object).royOff;
                f18 = f10 * ((Trails.Ribbon)object).lenFactor * ((Trails.Ribbon)object).speedScale * f27;
                f17 = ((Trails.Ribbon)object).halfWBase * (0.5f + 0.5f * f27);
                f16 = (float)Math.sqrt(f23 * f23 + f19 * f19 + f25 * f25);
                f15 = MathHelper.clamp((float)((f16 - f18 - 0.3f) / 1.0f), (float)0.0f, (float)1.0f);
                if (f15 <= 0.0f) continue;
                f14 = MathHelper.clamp((float)(f * f9 * 0.5f * f15), (float)0.0f, (float)1.0f);
                n5 = Trails.applyTint(colorCtx.color(((Trails.Ribbon)object).colorIndex, f14), 0.85f);
                n3 = Trails.emitRibbon(vertexConsumer, entry, (Trails.Ribbon)object, f23, f19, f25, f18, f17, n5, fArray, n3, f14, f * f15);
            }
        }
        if (n3 > 0) {
            Framebuffer framebuffer = this.mc.getFramebuffer();
            Matrix4f matrix4f2 = worldRenderEvent.getPositionMatrix();
            Matrix4f projMatrix = worldRenderEvent.getProjectionMatrix();
            if (framebuffer != null && matrix4f2 != null && projMatrix != null) {
                f2 = (float)framebuffer.textureWidth / (float)Math.max(1, framebuffer.textureHeight);
                f = this.mc.options != null ? Math.max(192.0f, (float)(this.mc.options.getViewDistance().getValue() + 1) * 16.0f) : 256.0f;
                fArray[0] = n3;
                fArray[1] = f2;
                fArray[2] = (float)((double)(System.currentTimeMillis() % 100000L) / 1000.0);
                fArray[3] = 0.01f + this.dupDistort.getFloat() * 0.006f;
                fArray[4] = 0.6f;
                fArray[5] = this.dupReflect.getFloat();
                fArray[6] = 0.2f;
                fArray[7] = 0.105000004f / f10;
                fArray[8] = 1.0f;
                fArray[9] = 0.05f;
                fArray[10] = f;
                fArray[11] = 0.0f;
                Trails.projectScarSegments(fArray, n3, this.scarViewProj.set((Matrix4fc)projMatrix).mul((Matrix4fc)matrix4f2), this.scarProjScratch, 0.05f, f);
                ScarGlassRenderer.apply((Framebuffer)framebuffer, (float[])fArray);
            }
        }
        immediate.draw(renderLayer);
    }

    private static float hashf(long seed) {
        seed = (seed ^ (seed >>> 30)) * 0xbf58476d1ce4e5b9L;
        seed = (seed ^ (seed >>> 27)) * 0x94d049bb133111ebL;
        seed = seed ^ (seed >>> 31);
        return (float)((seed & 0xFFFFFF) / (double)0x1000000);
    }

    private static void sparkQuad(VertexConsumer vertexConsumer, MatrixStack.Entry entry, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, int n) {
        float f12 = f * f10;
        float f13 = f2 * f10;
        float f14 = f3 * f10;
        float f15 = f4 * f11;
        float f16 = f5 * f11;
        float f17 = f6 * f11;
        vertexConsumer.vertex(entry, f7 - f12 - f15, f8 - f13 - f16, f9 - f14 - f17).color(n);
        vertexConsumer.vertex(entry, f7 - f12 + f15, f8 - f13 + f16, f9 - f14 + f17).color(n);
        vertexConsumer.vertex(entry, f7 + f12 + f15, f8 + f13 + f16, f9 + f14 + f17).color(n);
        vertexConsumer.vertex(entry, f7 + f12 - f15, f8 + f13 - f16, f9 + f14 - f17).color(n);
    }

    private int getColor(int n, float f) {
        int n2 = this.getColorBase(n, f);
        if (this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")) {
            n2 = Trails.applyTint(n2, MathHelper.clamp((float)(this.dupTint.getFloat() / 100.0f), (float)0.0f, (float)1.0f));
        }
        return n2;
    }

    private static int emitRibbon(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Trails.Ribbon ribbon, float f, float f2, float f3, float f4, float f5, int n, float[] fArray, int n2, float f6, float f7) {
        int n3 = 14;
        float f8 = ribbon.bdx;
        float f9 = ribbon.bdy;
        float f10 = ribbon.bdz;
        float f11 = ribbon.e1x;
        float f12 = ribbon.e1z;
        float[] fArray2 = ribbon.sVals;
        float[] fArray3 = ribbon.swayOff;
        float[] fArray4 = ribbon.liftOff;
        float[] fArray5 = ribbon.taper;
        float f13 = 0.0f;
        float f14 = 0.0f;
        float f15 = 0.0f;
        float f16 = 0.0f;
        boolean bl = false;
        float f17 = 0.0f;
        float f18 = 0.0f;
        float f19 = 0.0f;
        float f20 = 0.0f;
        boolean bl2 = false;
        for (int i = 0; i <= 14; ++i) {
            float f21 = fArray2[i] * f4;
            float f22 = fArray3[i];
            float f23 = fArray4[i];
            float f24 = f + f8 * f21 + f11 * f22;
            float f25 = f2 + f9 * f21 + f23;
            float f26 = f3 + f10 * f21 + f12 * f22;
            float f27 = fArray5[i];
            if (bl) {
                float f28 = (f13 + f24) * 0.5f;
                float f29 = (f14 + f25) * 0.5f;
                float f30 = (f15 + f26) * 0.5f;
                float f31 = f24 - f13;
                float f32 = f25 - f14;
                float f33 = f26 - f15;
                float f34 = (float)Math.sqrt(f31 * f31 + f32 * f32 + f33 * f33);
                f34 = f34 > 1.0E-5f ? f34 : 1.0f;
                f31 /= f34;
                f32 /= f34;
                f33 /= f34;
                float f35 = (float)Math.sqrt(f28 * f28 + f29 * f29 + f30 * f30);
                f35 = f35 > 1.0E-5f ? f35 : 1.0f;
                float f36 = f28 / f35;
                float f37 = f29 / f35;
                float f38 = f30 / f35;
                float f39 = f32 * f38 - f33 * f37;
                float f40 = f33 * f36 - f31 * f38;
                float f41 = f31 * f37 - f32 * f36;
                float f42 = (float)Math.sqrt(f39 * f39 + f40 * f40 + f41 * f41);
                f42 = f42 > 1.0E-5f ? f42 : 1.0f;
                float f43 = f5 * Math.max(0.05f, f16) / f42;
                float f44 = f5 * Math.max(0.05f, f27) / f42;
                float f45 = f39 * f43;
                float f46 = f40 * f43;
                float f47 = f41 * f43;
                float f48 = f39 * f44;
                float f49 = f40 * f44;
                float f50 = f41 * f44;
                float f51 = (float)(i - 1) / 14.0f;
                float f52 = (float)i / 14.0f;
                vertexConsumer.vertex(entry, f13 - f45, f14 - f46, f15 - f47).color(n);
                vertexConsumer.vertex(entry, f13 + f45, f14 + f46, f15 + f47).color(n);
                vertexConsumer.vertex(entry, f24 + f48, f25 + f49, f26 + f50).color(n);
                vertexConsumer.vertex(entry, f24 - f48, f25 - f49, f26 - f50).color(n);
            }
            if (fArray != null && (i == 0 || i == 7 || i == 14)) {
                if (bl2 && n2 < 96) {
                    int n4 = 12 + n2 * 12;
                    fArray[n4] = f17;
                    fArray[n4 + 1] = f18;
                    fArray[n4 + 2] = f19;
                    fArray[n4 + 3] = f6;
                    fArray[n4 + 4] = f24;
                    fArray[n4 + 5] = f25;
                    fArray[n4 + 6] = f26;
                    fArray[n4 + 7] = f7;
                    fArray[n4 + 8] = f20;
                    fArray[n4 + 9] = f27;
                    fArray[n4 + 10] = 0.0f;
                    fArray[n4 + 11] = 0.0f;
                    ++n2;
                }
                f17 = f24;
                f18 = f25;
                f19 = f26;
                f20 = f27;
                bl2 = true;
            }
            f13 = f24;
            f14 = f25;
            f15 = f26;
            f16 = f27;
            bl = true;
        }
        return n2;
    }

    private static float linDepth(float z, float near, float far) {
        return (2.0f * near) / (far + near - z * (far - near));
    }

    private static void emitBeamUv(VertexConsumer vertexConsumer, MatrixStack.Entry entry, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, int n) {
        vertexConsumer.vertex(entry, f - f4 - f7, f2 - f5 - f8, f3 - f6 - f9).color(n);
        vertexConsumer.vertex(entry, f - f4 + f7, f2 - f5 + f8, f3 - f6 + f9).color(n);
        vertexConsumer.vertex(entry, f + f4 + f7, f2 + f5 + f8, f3 + f6 + f9).color(n);
        vertexConsumer.vertex(entry, f + f4 - f7, f2 + f5 - f8, f3 + f6 - f9).color(n);
    }

    private static float fcos(float f) {
        return SIN_LUT[(int)Math.floor(f * 1303.7972f) + 2048 & 0x1FFF];
    }

    private void addPoint(long l) {
        Vec3d vec3d = MathUtils.interpolate((Entity)(Object)this.mc.player).add(0.0, (double)this.mc.player.getHeight() * 0.5, 0.0);
        if (!(Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z))) {
            return;
        }
        if (this.lastAdded != null && this.lastAdded.squaredDistanceTo(vec3d) < 0.01) {
            return;
        }
        this.points.add(new TailPoint(vec3d, l));
        this.lastAdded = vec3d;
        if (this.points.size() > 512) {
            this.points.subList(0, this.points.size() - 512).clear();
        }
    }

    private static int applyTint(int n, float f) {
        if (f >= 0.999f) {
            return n;
        }
        int n2 = n >>> 24 & 0xFF;
        int n3 = n >>> 16 & 0xFF;
        int n4 = n >>> 8 & 0xFF;
        int n5 = n & 0xFF;
        float f2 = 1.0f - f;
        n3 = Math.round((float)n3 + (float)(255 - n3) * f2);
        n4 = Math.round((float)n4 + (float)(255 - n4) * f2);
        n5 = Math.round((float)n5 + (float)(255 - n5) * f2);
        return n2 << 24 | n3 << 16 | n4 << 8 | n5;
    }

    private static float fsin(float f) {
        return SIN_LUT[(int)Math.floor(f * 1303.7972f) & 0x1FFF];
    }

    private static int emitBeam(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Trails.Shard shard, float f, float f2, float f3, float f4, int n, float[] fArray, int n2, float f5, float f6) {
        int n3 = 3;
        float f7 = shard.beamE1x;
        float f8 = shard.beamE1z;
        float f9 = shard.bdx;
        float f10 = shard.bdy;
        float f11 = shard.bdz;
        float f12 = shard.curveX;
        float f13 = shard.curveY;
        float f14 = shard.curveZ;
        float f15 = shard.wd;
        float f16 = 0.0f;
        float f17 = 0.0f;
        float f18 = 0.0f;
        for (int i = 0; i <= 3; ++i) {
            float f19 = (float)i / 3.0f;
            float f20 = (f19 - 0.5f) * 2.0f * f4;
            float f21 = BEAM_BOW[i];
            float f22 = f + f9 * f20 + f12 * f21;
            float f23 = f2 + f10 * f20 + f13 * f21;
            float f24 = f3 + f11 * f20 + f14 * f21;
            if (i > 0) {
                float f25 = (f16 + f22) * 0.5f;
                float f26 = (f17 + f23) * 0.5f;
                float f27 = (f18 + f24) * 0.5f;
                float f28 = (f22 - f16) * 0.5f;
                float f29 = (f23 - f17) * 0.5f;
                float f30 = (f24 - f18) * 0.5f;
                float f31 = (float)(i - 1) / 3.0f;
                float f32 = (float)i / 3.0f;
                Trails.emitBeamUv(vertexConsumer, entry, f25, f26, f27, f28, f29, f30, f7 * f15, 0.0f, f8 * f15, f31, f32, n);
                Trails.emitBeamUv(vertexConsumer, entry, f25, f26, f27, f28, f29, f30, 0.0f, f15, 0.0f, f31, f32, n);
                if (fArray != null && n2 < 96) {
                    int n4 = 12 + n2 * 12;
                    fArray[n4] = f16;
                    fArray[n4 + 1] = f17;
                    fArray[n4 + 2] = f18;
                    fArray[n4 + 3] = f5;
                    fArray[n4 + 4] = f22;
                    fArray[n4 + 5] = f23;
                    fArray[n4 + 6] = f24;
                    fArray[n4 + 7] = f6;
                    fArray[n4 + 8] = BEAM_BOW[i - 1];
                    fArray[n4 + 9] = BEAM_BOW[i];
                    fArray[n4 + 10] = 0.0f;
                    fArray[n4 + 11] = 0.0f;
                    ++n2;
                }
            }
            f16 = f22;
            f17 = f23;
            f18 = f24;
        }
        return n2;
    }

    private void emitSpark(VertexConsumer vertexConsumer, MatrixStack.Entry entry, Trails.Spark spark, Trails.ColorCtx colorCtx, int n, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16) {
        float f17;
        float f18;
        float f19 = spark.orbRBase * (0.85f + f12 * 0.25f);
        float f20 = f13 * spark.speed + spark.phase;
        float f21 = Trails.fcos(f20);
        float f22 = Trails.fsin(f20);
        float f23 = (spark.px0 * f21 + spark.qx * f22) * f19;
        float f24 = (spark.py0 * f21 + spark.qy * f22) * f19;
        float f25 = (spark.pz0 * f21 + spark.qz * f22) * f19;
        float f26 = f7 + f23 + Trails.fsin(f14 * spark.hf + spark.phx) * spark.hAmp;
        float f27 = f8 + f24 + Trails.fsin(f14 * spark.hf * 1.3f + spark.phy) * spark.hAmp;
        float f28 = f9 + f25 + Trails.fcos(f14 * spark.hf * 0.85f + spark.phz) * spark.hAmp;
        float f29 = f12 * f12 * 0.55f;
        f26 -= f10 * f29;
        f28 -= f11 * f29;
        float f30 = MathHelper.clamp((float)((f12 - spark.born) / 0.1f), (float)0.0f, (float)1.0f);
        float f31 = MathHelper.clamp((float)(f16 * f15 * f30 * (f18 = 1.0f - MathHelper.clamp((float)((f12 - (spark.lifeEnd - 0.2f)) / 0.2f), (float)0.0f, (float)1.0f)) * (f17 = 0.82f + 0.18f * Trails.fsin(f14 * spark.shimFreq + spark.shimPhase))), (float)0.0f, (float)1.0f);
        if (f31 <= 0.02f) {
            return;
        }
        int n2 = colorCtx.color(n + spark.hueOff, f31);
        int n3 = ColorUtil.multAlpha(Trails.applyTint(n2, 0.55f), 0.36f);
        int n4 = ColorUtil.multAlpha(n2, 0.5f);
        float f32 = spark.size;
        Trails.sparkQuad(vertexConsumer, entry, f, f2, f3, f4, f5, f6, f26, f27, f28, f32 * 3.4f, f32 * 3.4f, n3);
        Trails.sparkQuad(vertexConsumer, entry, f, f2, f3, f4, f5, f6, f26, f27, f28, f32 * 1.9f, f32 * 1.9f, n3);
        Trails.sparkQuad(vertexConsumer, entry, f, f2, f3, f4, f5, f6, f26, f27, f28, f32, f32, n2);
        float f33 = spark.cr;
        float f34 = spark.sr;
        float f35 = f * f33 + f4 * f34;
        float f36 = f2 * f33 + f5 * f34;
        float f37 = f3 * f33 + f6 * f34;
        float f38 = -f * f34 + f4 * f33;
        float f39 = -f2 * f34 + f5 * f33;
        float f40 = -f3 * f34 + f6 * f33;
        Trails.sparkQuad(vertexConsumer, entry, f35, f36, f37, f38, f39, f40, f26, f27, f28, f32 * 1.5f, f32 * 0.2f, n4);
        Trails.sparkQuad(vertexConsumer, entry, f35, f36, f37, f38, f39, f40, f26, f27, f28, f32 * 0.2f, f32 * 1.5f, n4);
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            this.points.clear();
            this.lastAdded = null;
            return;
        }
        long l = System.currentTimeMillis();
        long l2 = Math.max(100L, Math.round((double)this.length.getFloat() * 60.0));
        this.addPoint(l);
        if (this.mode.is("\u041e\u0441\u043a\u043e\u043b\u043a\u0438")) {
            this.spawnBurst();
            this.renderScarBursts3D(worldRenderEvent);
            return;
        }
        Vec3d vec3d = worldRenderEvent.getCamera() == null ? this.mc.gameRenderer.getCamera().getCameraPos() : worldRenderEvent.getCamera().getCameraPos();
        Quaternionf quaternionf = this.mc.gameRenderer.getCamera().getRotation();
        float f = this.size.getFloat() * 0.5f;
        Vector3f vector3f = quaternionf.transform(new Vector3f(1.0f, 0.0f, 0.0f));
        Vector3f vector3f2 = quaternionf.transform(new Vector3f(0.0f, 1.0f, 0.0f));
        float f2 = vector3f.x() * f;
        float f3 = vector3f.y() * f;
        float f4 = vector3f.z() * f;
        float f5 = vector3f2.x() * f;
        float f6 = vector3f2.y() * f;
        float f7 = vector3f2.z() * f;
        MatrixStack matrixStack = worldRenderEvent.getStack();
        VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        RenderLayer renderLayer = ClientPipelines.WORLD_PARTICLES_GLOW;
        VertexConsumer vertexConsumer = immediate.getBuffer(renderLayer);
        MatrixStack.Entry entry = matrixStack.peek();
        boolean bl = false;

        // Clean expired points
        for (int i = this.points.size() - 1; i >= 0; --i) {
            if (l - this.points.get(i).spawnMs > l2) {
                this.points.remove(i);
            }
        }

        // Draw solid 3D ribbon connecting points
        if (this.points.size() >= 2) {
            float ribbonHeight = this.size.getFloat() * 0.8f;
            for (int i = 0; i < this.points.size() - 1; i++) {
                TailPoint p1 = this.points.get(i);
                TailPoint p2 = this.points.get(i + 1);
                long age1 = l - p1.spawnMs;
                long age2 = l - p2.spawnMs;
                if (age1 > l2 || age2 > l2) continue;

                float prog1 = MathHelper.clamp((float) age1 / (float) l2, 0.0f, 1.0f);
                float prog2 = MathHelper.clamp((float) age2 / (float) l2, 0.0f, 1.0f);
                float alpha1 = (1.0f - prog1) * Math.min((float) age1 / 80.0f, 1.0f);
                float alpha2 = (1.0f - prog2) * Math.min((float) age2 / 80.0f, 1.0f);

                int col1 = this.getColor(i, alpha1);
                int col2 = this.getColor(i + 1, alpha2);

                float x1 = (float) (p1.pos.x - vec3d.x);
                float y1 = (float) (p1.pos.y - vec3d.y);
                float z1 = (float) (p1.pos.z - vec3d.z);

                float x2 = (float) (p2.pos.x - vec3d.x);
                float y2 = (float) (p2.pos.y - vec3d.y);
                float z2 = (float) (p2.pos.z - vec3d.z);

                // Two-sided vertical ribbon
                vertexConsumer.vertex(entry, x1, y1 - ribbonHeight * 0.5f, z1).color(col1);
                vertexConsumer.vertex(entry, x1, y1 + ribbonHeight * 0.5f, z1).color(col1);
                vertexConsumer.vertex(entry, x2, y2 + ribbonHeight * 0.5f, z2).color(col2);
                vertexConsumer.vertex(entry, x2, y2 - ribbonHeight * 0.5f, z2).color(col2);

                // Reverse side
                vertexConsumer.vertex(entry, x2, y2 - ribbonHeight * 0.5f, z2).color(col2);
                vertexConsumer.vertex(entry, x2, y2 + ribbonHeight * 0.5f, z2).color(col2);
                vertexConsumer.vertex(entry, x1, y1 + ribbonHeight * 0.5f, z1).color(col1);
                vertexConsumer.vertex(entry, x1, y1 - ribbonHeight * 0.5f, z1).color(col1);
                bl = true;
            }
        }

        // Draw billboards along points
        for (int i = 0; i < this.points.size(); ++i) {
            TailPoint tailPoint = this.points.get(i);
            long l3 = l - tailPoint.spawnMs;
            if (l3 > l2) continue;
            float f8 = MathHelper.clamp((float)((float)l3 / (float)l2), 0.0f, 1.0f);
            float f9 = Math.min((float)l3 / 80.0f, 1.0f);
            float f10 = f9 * (1.0f - f8);
            if (f10 > 0.003921569f) {
                int n2 = this.getColor(i, f10);
                float f11 = (float)(tailPoint.pos.x - vec3d.x);
                float f12 = (float)(tailPoint.pos.y - vec3d.y);
                float f13 = (float)(tailPoint.pos.z - vec3d.z);
                Trails.quad(vertexConsumer, entry, f11, f12, f13, f2, f3, f4, f5, f6, f7, n2);
                bl = true;
            }
        }
        if (bl) {
            immediate.draw(renderLayer);
        }
    }

    private static int paletteFade(int n, int n2, int[] nArray) {
        int n3 = nArray.length;
        int n4 = (int)((System.currentTimeMillis() / (long)Math.max(1, n) + (long)n2) % 360L);
        float f = (float)n4 / 360.0f * (float)n3;
        int n5 = (int)f % n3;
        int n6 = (n5 + 1) % n3;
        int n7 = nArray[n5] | 0xFF000000;
        int n8 = nArray[n6] | 0xFF000000;
        return ColorUtil.lerpColor(n7, n8, f - (float)Math.floor(f)) | 0xFF000000;
    }

    private static int getClientColor() {
        float f = (MathHelper.sin((double)((float)System.currentTimeMillis() / 520.0f)) + 1.0f) / 2.0f;
        return ColorUtil.lerpColor(CLIENT_COLOR_FIRST, CLIENT_COLOR_SECOND, f);
    }


    public static final class ColorCtx {
        private int mode;
        private int[] table;
        private int[] palette;
        private int first;
        private int second;
        private int wrapOff;
        private float tintFactor;
        private boolean tintEnabled;
        final /* synthetic */ Trails this$0;
    
        private int base(int n) {
            int n2 = Math.floorMod(this.wrapOff + n, 360);
            switch (this.mode) {
                case 0: {
                    return this.table[n2];
                }
                case 1: {
                    int n3 = this.palette.length;
                    float f = (float)n2 / 360.0f * (float)n3;
                    int n4 = (int)f % n3;
                    int n5 = (n4 + 1) % n3;
                    int n6 = this.palette[n4] | 0xFF000000;
                    int n7 = this.palette[n5] | 0xFF000000;
                    return ColorUtil.lerpColor(n6, n7, f - (float)Math.floor(f)) | 0xFF000000;
                }
            }
            if (this.first == this.second) {
                return this.first;
            }
            int n8 = n2 >= 180 ? 360 - n2 : n2;
            return ColorUtil.lerpColor(this.first, this.second, (float)n8 / 180.0f);
        }
    
        private ColorCtx(Trails trails) {
            this.this$0 = trails;
        }
    
        void update(long l) {
            this.tintFactor = MathHelper.clamp((float)(this.this$0.dupTint.getFloat() / 100.0f), (float)0.0f, (float)1.0f);
            this.tintEnabled = this.tintFactor < 0.999f;
            this.wrapOff = (int)(l / 8L % 360L);
            if (this.this$0.colorMode.is("\u0420\u0430\u0434\u0443\u0433\u0430")) {
                this.mode = 0;
                this.table = RainbowLut.table((float)1.0f, (float)1.0f);
            } else if (this.this$0.colorMode.is("\u041a\u043b\u0438\u0435\u043d\u0442")) {
                int[] nArray = ClientPalette.colors();
                if (nArray != null && nArray.length >= 2) {
                    this.mode = 1;
                    this.palette = nArray;
                } else {
                    InterfaceModule interfaceModule = InterfaceModule.getInstance();
                    if (interfaceModule != null) {
                        this.first = interfaceModule.clientPrimaryColorOpaque();
                        this.second = interfaceModule.usesSecondClientColor() ? interfaceModule.clientSecondaryColorOpaque() : this.first;
                    } else {
                        this.first = Trails.getClientColor();
                        this.second = ColorUtil.lerpColor(this.first, Trails.DARK_SECOND_COLOR, 0.7f);
                    }
                    this.mode = 2;
                }
            } else {
                this.first = this.this$0.customColor.getColor();
                this.second = this.this$0.useSecondColor.getValue() ? this.this$0.customSecondColor.getColor() : this.first;
                this.mode = 2;
            }
        }
    
        int color(int n, float f) {
            int n2 = ColorUtil.multAlpha(this.base(n), f);
            if (this.tintEnabled) {
                n2 = Trails.applyTint(n2, this.tintFactor);
            }
            return n2;
        }
    }
    
        public static final class Ribbon {
        float royOff;
        float lenFactor;
        float speedScale;
        float halfWBase;
        float bdx;
        float bdy;
        float bdz;
        float e1x;
        float e1z;
        int colorIndex;
        float[] sVals;
        float[] swayOff;
        float[] liftOff;
        float[] taper;
    
        private Ribbon() {
        }
    }
    
        public static final class Shard {
        float ox;
        float oy;
        float oz;
        float bdx;
        float bdy;
        float bdz;
        float beamE1x;
        float beamE1z;
        float curveX;
        float curveY;
        float curveZ;
        float wd;
        float sizeMul;
        float heroMul;
        int colorIndex;
        long sparkSeed;
        float sparkNBase;
        Trails.Spark[] sparks;
        int sparkCount;
    }
    
        public static final class Spark {
        float px0;
        float py0;
        float pz0;
        float qx;
        float qy;
        float qz;
        float orbRBase;
        float speed;
        float phase;
        float hf;
        float hAmp;
        float phx;
        float phy;
        float phz;
        float born;
        float lifeEnd;
        float shimFreq;
        float shimPhase;
        float size;
        int hueOff;
        float cr;
        float sr;
    
        public Spark() {
        }
    }

    public static record TailPoint(Vec3d pos, long spawnMs) {}

    public static final class ScarBurst {
        public double x, y, z;
        public long spawnMs;
        public long seq;
        public float baseHue;
        public float dirX, dirZ;
        public Trails.Shard[] shards = EMPTY_SHARDS;
        public Trails.Ribbon[] ribbons = EMPTY_RIBBONS;

        public ScarBurst(double x, double y, double z, long spawnMs, long seq, float hue, float dirX, float dirZ, float yaw, float dist) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.spawnMs = spawnMs;
            this.seq = seq;
            this.baseHue = hue;
            this.dirX = dirX;
            this.dirZ = dirZ;
        }

        public void build() {}

        public Trails.Shard[] ensureShards(int n) {
            if (this.shards == EMPTY_SHARDS || this.shards.length != n) {
                this.shards = new Trails.Shard[n];
                for (int i = 0; i < n; i++) this.shards[i] = new Trails.Shard();
            }
            return this.shards;
        }

        public Trails.Ribbon[] ensureRibbons(int n) {
            if (this.ribbons == EMPTY_RIBBONS || this.ribbons.length != n) {
                this.ribbons = new Trails.Ribbon[n];
                for (int i = 0; i < n; i++) this.ribbons[i] = new Trails.Ribbon();
            }
            return this.ribbons;
        }

        public Trails.Spark[] ensureSparks(Trails.Shard shard, int n) {
            if (shard.sparks == null || shard.sparks.length != n) {
                shard.sparks = new Trails.Spark[n];
                for (int i = 0; i < n; i++) shard.sparks[i] = new Trails.Spark();
            }
            return shard.sparks;
        }
    }
}

