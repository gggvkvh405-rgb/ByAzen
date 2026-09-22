package rtx.byazen.utils.cosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.utils.math.MathUtils;
import rtx.byazen.utils.render.pipeline.ClientPipelines;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;

/**
 * Отрисовка надетой косметики (идеи №121, №122, №124 и №130 из IDEAS.md).
 * <p>
 * Модель берётся из {@link CosmeticModels} и переносится на игрока: шляпа и корона сидят на голове
 * с учётом наклона, уши и маска следуют за лицом, шарф и хвост висят на пружинах, у ног светится
 * аура. Смещение и размер берутся из настроек модуля, поэтому любой аксессуар подгоняется под свой
 * скин. Кроме моделей здесь живут искры: у каждого набора свой характер — снежинки, неон, звёздная
 * пыль.
 */
public final class CosmeticRenderer {

    /** Как сейчас расположены аксессуары: сдвиг, размер и частицы. */
    public static final class Layout {

        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public float scale = 1.0f;
        public boolean particles = true;
        public float particleAmount = 1.0f;
        public boolean otherPlayers = true;

        public Layout copy() {
            Layout layout = new Layout();
            layout.offsetX = this.offsetX;
            layout.offsetY = this.offsetY;
            layout.offsetZ = this.offsetZ;
            layout.scale = this.scale;
            layout.particles = this.particles;
            layout.particleAmount = this.particleAmount;
            layout.otherPlayers = this.otherPlayers;
            return layout;
        }
    }

    private static Layout layout = new Layout();
    private static final Map<Integer, PlayerState> STATES = new HashMap<Integer, PlayerState>();

    private CosmeticRenderer() {
    }

    public static void setLayout(Layout value) {
        layout = value == null ? new Layout() : value.copy();
    }

    public static Layout layout() {
        return layout.copy();
    }

    /** Одна искра косметики. */
    private static final class Spark {

        Vec3d pos;
        Vec3d velocity;
        float life;
        float maxLife;
        float size;
        int color;

        Spark(Vec3d pos, Vec3d velocity, float life, float size, int color) {
            this.pos = pos;
            this.velocity = velocity;
            this.life = life;
            this.maxLife = life;
            this.size = size;
            this.color = color;
        }
    }

    /** Состояние отрисовки одного игрока: физика шарфа и хвоста плюс искры. */
    private static final class PlayerState {

        CosmeticPhysics scarf = new CosmeticPhysics(7, 1.05f);
        CosmeticPhysics tail = new CosmeticPhysics(6, 0.85f);
        final List<Spark> sparks = new ArrayList<Spark>();
        float phase;
        float randomness;

        void ensure(String headKey, String neckKey, String tailKey) {
            if (!headKey.equals(this.headKey)) {
                this.headKey = headKey;
                this.scarf = new CosmeticPhysics(7, 1.05f);
                this.scarfActive = false;
            }
            if (!neckKey.equals(this.neckKey)) {
                this.neckKey = neckKey;
                this.scarfActive = false;
            }
            if (!tailKey.equals(this.tailKey)) {
                this.tailKey = tailKey;
                this.tailActive = false;
            }
        }

        String headKey = "";
        String neckKey = "";
        String tailKey = "";
        boolean scarfActive;
        boolean tailActive;
    }

    /** Точка входа: вызывается из модуля косметики каждый кадр мира. */
    public static void render(WorldRenderEvent event, MinecraftClient client) {
        if (event == null || client == null || client.world == null || client.player == null) {
            return;
        }
        Camera camera = event.getCamera() != null ? event.getCamera() : client.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }
        Vec3d cameraPos = camera.getCameraPos();
        Quaternionf cameraRotation = camera.getRotation();
        MatrixStack stack = event.getStack();
        float partial = (float)event.getPartialTicks();
        Layout active = layout;

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer consumer = immediate.getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);
        MatrixStack.Entry entry = stack.peek();
        ArrayList<BloomRenderer.Point> glow = new ArrayList<BloomRenderer.Point>();
        long now = System.currentTimeMillis();

        for (Entity entity : client.world.getPlayers()) {
            if (!(entity instanceof PlayerEntity)) {
                continue;
            }
            PlayerEntity player = (PlayerEntity)entity;
            if (player.isInvisible() || player.isSpectator()
                    || player == client.player && client.options.getPerspective() == Perspective.FIRST_PERSON) {
                continue;
            }
            if (player != client.player && !active.otherPlayers) {
                continue;
            }
            if (player.squaredDistanceTo(cameraPos.x, cameraPos.y, cameraPos.z) > 4096.0) {
                continue;
            }
            CosmeticRenderer.drawPlayer(consumer, entry, cameraPos, glow, player, partial, active, now);
        }
        if (active.particles && !glow.isEmpty()) {
            BloomRenderer.render(glow, stack, cameraPos, cameraRotation, 0.085f, 150.0f, 1.4f, true);
        }
    }

    /** Сбрасывает состояние: вызывается при выключении модуля. */
    public static void clear() {
        STATES.clear();
    }

    private static PlayerState state(PlayerEntity player) {
        return STATES.computeIfAbsent(player.getId(), id -> new PlayerState());
    }

    private static void drawPlayer(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera,
                                   List<BloomRenderer.Point> glow, PlayerEntity player, float partial, Layout layout,
                                   long now) {
        Cosmetic head = Cosmetics.equippedCosmetic(Cosmetic.SLOT_HEAD);
        Cosmetic ears = Cosmetics.equippedCosmetic(Cosmetic.SLOT_EARS);
        Cosmetic face = Cosmetics.equippedCosmetic(Cosmetic.SLOT_FACE);
        Cosmetic neck = Cosmetics.equippedCosmetic(Cosmetic.SLOT_NECK);
        Cosmetic tail = Cosmetics.equippedCosmetic(Cosmetic.SLOT_TAIL);
        Cosmetic aura = Cosmetics.equippedCosmetic(Cosmetic.SLOT_AURA);
        if (head == null && ears == null && face == null && neck == null && tail == null && aura == null) {
            return;
        }

        PlayerState state = CosmeticRenderer.state(player);
        state.ensure(head == null ? "" : head.id, neck == null ? "" : neck.id, tail == null ? "" : tail.id);
        Vec3d base = MathUtils.interpolate(player, partial);
        float yaw = player.getYaw();
        float pitch = player.getLerpedPitch(partial);
        float height = player.getHeight();
        float scale = Math.max(0.4f, layout.scale);
        float time = (float)now / 1000.0f + (float)player.getId() * 0.37f;

        Vec3d forward = CosmeticRenderer.forward(yaw);
        Vec3d right = CosmeticRenderer.right(yaw);
        Vec3d up = new Vec3d(0.0, 1.0, 0.0);
        Vec3d headUp = CosmeticRenderer.tilt(up, forward, -pitch);
        Vec3d faceForward = CosmeticRenderer.tilt(forward, up, pitch);

        double headY = base.y + (double)height * 0.92;
        Vec3d headCenter = new Vec3d(base.x, headY, base.z)
                .add(right.multiply((double)layout.offsetX * 0.35))
                .add(up.multiply((double)layout.offsetY * 0.35))
                .add(forward.multiply(0.02 + (double)layout.offsetZ * 0.35));

        if (head != null) {
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticModels.accessory(head, scale, time).moved(headCenter, right, headUp, faceForward));
        }
        if (ears != null) {
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticModels.accessory(ears, scale, time).moved(headCenter, right, headUp, faceForward));
        }
        if (face != null) {
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticModels.accessory(face, scale, time).moved(headCenter, right, headUp, faceForward));
        }

        Vec3d wind = player.getVelocity().multiply(0.15);
        if (neck != null) {
            Vec3d anchor = new Vec3d(base.x, base.y + (double)height * 0.82, base.z)
                    .add(right.multiply((double)layout.offsetX * 1.4))
                    .add(forward.multiply(0.06 + (double)layout.offsetZ * 1.4))
                    .add(up.multiply((double)layout.offsetY * 1.4));
            Vec3d rest = new Vec3d(0.0, -1.0, 0.0).add(faceForward.multiply(-0.42))
                    .add(right.multiply(player.getVelocity().horizontalLength() > 0.08 ? -0.30 : 0.0));
            state.scarf.update(anchor, rest, wind, 0.0026f, 0.87f);
            state.scarfActive = true;
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticRenderer.strand(state.scarf, 0.085f * scale, 0.030f * scale, neck, camera));
            CosmeticRenderer.spawn(state, neck, state.scarf.point(state.scarf.count() - 1), layout, 1.0f);
        }
        if (tail != null) {
            Vec3d anchor = new Vec3d(base.x, base.y + (double)height * 0.44, base.z)
                    .add(forward.multiply(-0.20 - (double)layout.offsetZ * 1.2))
                    .add(right.multiply((double)layout.offsetX * 1.2))
                    .add(up.multiply((double)layout.offsetY * 1.2));
            Vec3d rest = new Vec3d(0.0, -1.0, 0.0).add(faceForward.multiply(-0.5));
            state.tail.update(anchor, rest, wind.multiply(1.4), 0.0030f, 0.89f);
            state.tailActive = true;
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticRenderer.strand(state.tail, 0.10f * scale, 0.028f * scale, tail, camera));
            CosmeticRenderer.spawn(state, tail, state.tail.point(state.tail.count() - 1), layout, 0.8f);
        }
        if (aura != null) {
            state.phase += 0.012f;
            Vec3d ringCenter = new Vec3d(base.x, base.y + 0.04 + (double)layout.offsetY * 0.6, base.z);
            CosmeticRenderer.emit(consumer, entry, camera,
                    CosmeticModels.accessory(aura, scale * 0.95f, (float)now / 1000.0f)
                            .moved(ringCenter, right, up, forward));
            CosmeticRenderer.spawn(state, aura, ringCenter, layout, 0.7f);
        }

        CosmeticRenderer.stepSparks(state);
        for (Spark spark : state.sparks) {
            float life = spark.life / spark.maxLife;
            glow.add(new BloomRenderer.Point(spark.pos, spark.size * (0.45f + life * 0.75f), spark.color,
                    (float)((double)spark.pos.x + (double)spark.pos.z)));
        }
    }

    /** Лента через физические точки: сторона всегда развёрнута к камере. */
    private static CosmeticMesh strand(CosmeticPhysics strand, float startWidth, float endWidth, Cosmetic cosmetic,
                                       Vec3d camera) {
        int count = strand.count();
        Vec3d[] spine = new Vec3d[count];
        float[] widths = new float[count];
        for (int i = 0; i < count; ++i) {
            spine[i] = strand.point(i);
            float t = (float)i / (float)(count - 1);
            widths[i] = MathHelper.lerp(t, startWidth, endWidth);
        }
        CosmeticMesh mesh = new CosmeticMesh();
        mesh.ribbon(spine, widths, cosmetic.color, cosmetic.accent, 246, camera);
        return mesh;
    }

    private static void emit(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, CosmeticMesh mesh) {
        if (mesh == null || mesh.isEmpty()) {
            return;
        }
        for (CosmeticMesh.Tri tri : mesh.triangles()) {
            CosmeticRenderer.vertex(consumer, entry, camera, tri.a, tri.ca);
            CosmeticRenderer.vertex(consumer, entry, camera, tri.b, tri.cb);
            CosmeticRenderer.vertex(consumer, entry, camera, tri.c, tri.cc);
        }
    }

    private static void vertex(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, Vec3d point, int color) {
        consumer.vertex(entry, (float)(point.x - camera.x), (float)(point.y - camera.y), (float)(point.z - camera.z))
                .color(color);
    }

    /** Искры конкретного предмета: у каждого набора свой характер. */
    private static void spawn(PlayerState state, Cosmetic cosmetic, Vec3d origin, Layout layout, float rate) {
        if (!layout.particles || cosmetic == null) {
            return;
        }
        state.randomness += 0.02f * rate;
        int budget = (int)Math.max(1.0f, 1.0f + layout.particleAmount * 2.0f * rate);
        if (state.sparks.size() > 110) {
            return;
        }
        java.util.Random random = new java.util.Random((long)(state.randomness * 1000.0f) + (long)cosmetic.id.hashCode());
        for (int i = 0; i < budget; ++i) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = 0.16 + random.nextDouble() * 0.30;
            Vec3d offset = new Vec3d(Math.cos(angle) * radius, random.nextDouble() * 0.12, Math.sin(angle) * radius);
            Vec3d velocity;
            float life;
            float size;
            int color;
            if (cosmetic.snow()) {
                velocity = new Vec3d((random.nextDouble() - 0.5) * 0.012, -0.020 - random.nextDouble() * 0.012,
                        (random.nextDouble() - 0.5) * 0.012);
                life = 1.1f + random.nextFloat() * 0.7f;
                size = 0.7f;
                color = CosmeticRenderer.mix(cosmetic, 0.35f + random.nextFloat() * 0.3f);
            } else if (cosmetic.neon()) {
                velocity = new Vec3d((random.nextDouble() - 0.5) * 0.02, 0.012 + random.nextDouble() * 0.02,
                        (random.nextDouble() - 0.5) * 0.02);
                life = 0.55f + random.nextFloat() * 0.35f;
                size = 0.6f;
                color = CosmeticRenderer.mix(cosmetic, random.nextFloat());
            } else if (cosmetic.space()) {
                velocity = new Vec3d((random.nextDouble() - 0.5) * 0.014, 0.006 + random.nextDouble() * 0.01,
                        (random.nextDouble() - 0.5) * 0.014);
                life = 1.4f + random.nextFloat();
                size = 0.55f;
                color = CosmeticRenderer.mix(cosmetic, 0.25f + random.nextFloat() * 0.5f);
            } else {
                velocity = new Vec3d(0.0, 0.014, 0.0);
                life = 0.8f + random.nextFloat() * 0.4f;
                size = 0.5f;
                color = CosmeticRenderer.mix(cosmetic, 0.4f + random.nextFloat() * 0.4f);
            }
            state.sparks.add(new Spark(origin.add(offset), velocity, life, size, color));
        }
    }

    private static int mix(Cosmetic cosmetic, float t) {
        return BloomRenderer.mix(cosmetic.color, cosmetic.accent, MathHelper.clamp(t, 0.0f, 1.0f)) & 0xFFFFFF;
    }

    private static void stepSparks(PlayerState state) {
        for (int i = state.sparks.size() - 1; i >= 0; --i) {
            Spark spark = state.sparks.get(i);
            spark.life -= 0.05f;
            if (spark.life <= 0.0f) {
                state.sparks.remove(i);
                continue;
            }
            spark.pos = spark.pos.add(spark.velocity);
        }
        if (state.sparks.size() > 160) {
            state.sparks.subList(0, state.sparks.size() - 160).clear();
        }
    }

    static Vec3d forward(float yaw) {
        double radians = Math.toRadians((double)yaw);
        return new Vec3d(-Math.sin(radians), 0.0, Math.cos(radians));
    }

    static Vec3d right(float yaw) {
        double radians = Math.toRadians((double)yaw);
        return new Vec3d(-Math.cos(radians), 0.0, -Math.sin(radians));
    }

    static Vec3d tilt(Vec3d axis, Vec3d other, float pitchDegrees) {
        float radians = (float)Math.toRadians((double)pitchDegrees);
        return axis.multiply((double)MathHelper.cos(radians)).add(other.multiply((double)MathHelper.sin(radians))).normalize();
    }
}
