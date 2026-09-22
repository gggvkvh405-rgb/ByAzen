package rtx.byazen.utils.cosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.utils.render.pipeline.ClientPipelines;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;

/**
 * Следы под ногами (идея №130 из IDEAS.md).
 * <p>
 * Перед каждым шагом на земле остаётся мягкая светящаяся площадка: она чуть развёрнута по
 * направлению движения, чередуется левая-правая и медленно растворяется. Плюс тонкое кольцо и
 * блик — вместе это даёт аккуратный след, который хорошо смотрится на скриншотах. Хранится всё
 * в коротком кольцевом буфере, поэтому даже длинная прогулка не копит мусор.
 */
public final class FootprintTrail {

    /** Один отпечаток: где стоит, куда смотрел игрок и когда появился. */
    public static final class Step {

        public final Vec3d pos;
        public final float yaw;
        public final long time;
        public final boolean left;
        public final int color;

        Step(Vec3d pos, float yaw, long time, boolean left, int color) {
            this.pos = pos;
            this.yaw = yaw;
            this.time = time;
            this.left = left;
            this.color = color;
        }
    }

    private static final class Trail {

        final List<Step> steps = new ArrayList<Step>();
        Vec3d last = Vec3d.ZERO;
        boolean left;
    }

    private static final Map<Integer, Trail> TRAILS = new HashMap<Integer, Trail>();
    private static final int LIMIT = 220;

    private FootprintTrail() {
    }

    public static void clear() {
        TRAILS.clear();
    }

    /**
     * Добавляет отпечаток, если игрок прошёл достаточное расстояние по земле.
     *
     * @param spacing расстояние между следами в блоках
     */
    public static void update(Entity entity, long now, double spacing, int color) {
        if (entity == null) {
            return;
        }
        Trail trail = TRAILS.computeIfAbsent(entity.getId(), id -> new Trail());
        Vec3d pos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        boolean busy = entity instanceof PlayerEntity
                && (((PlayerEntity)entity).isGliding() || ((PlayerEntity)entity).isSwimming());
        if (!entity.isOnGround() || busy) {
            trail.last = pos;
            return;
        }
        double dx = pos.x - trail.last.x;
        double dz = pos.z - trail.last.z;
        double gap = Math.max(0.12, spacing);
        if (!trail.steps.isEmpty() && dx * dx + dz * dz < gap * gap) {
            return;
        }
        trail.left = !trail.left;
        float yaw = entity.getYaw();
        double rad = Math.toRadians(yaw);
        // правое плечо относительно направления взгляда
        double rightX = Math.cos(rad);
        double rightZ = Math.sin(rad);
        double side = trail.left ? 0.17 : -0.17;
        Vec3d step = new Vec3d(pos.x + rightX * side, pos.y + 0.02, pos.z + rightZ * side);
        trail.last = pos;
        trail.steps.add(new Step(step, yaw, now, trail.left, color));
        while (trail.steps.size() > LIMIT) {
            trail.steps.remove(0);
        }
    }

    /** Рисует следы всех игроков и затухает по возрасту. */
    public static void render(WorldRenderEvent event, MinecraftClient client, long lifetimeMs, float size,
                              boolean glow, boolean others, int color) {
        if (event == null || client == null || client.player == null || TRAILS.isEmpty()) {
            return;
        }
        Camera camera = event.getCamera() != null ? event.getCamera() : client.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }
        Vec3d cameraPos = camera.getCameraPos();
        double reach = 4096.0;
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer consumer = immediate.getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);
        MatrixStack.Entry entry = event.getStack().peek();
        ArrayList<BloomRenderer.Point> bloom = glow ? new ArrayList<BloomRenderer.Point>() : null;
        long now = System.currentTimeMillis();
        float life = Math.max(400.0f, lifetimeMs);

        for (Map.Entry<Integer, Trail> entrySet : TRAILS.entrySet()) {
            Trail trail = entrySet.getValue();
            Entity owner = FootprintTrail.owner(client, entrySet.getKey());
            if (owner == null) {
                continue;
            }
            if (owner != client.player && !others) {
                continue;
            }
            Iterator<Step> iterator = trail.steps.iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                float age = (float)(now - step.time);
                if (age > life) {
                    iterator.remove();
                    continue;
                }
                if (step.pos.squaredDistanceTo(cameraPos) > reach) {
                    continue;
                }
                float fade = 1.0f - age / life;
                int tint = FootprintTrail.fade(step.color != 0 ? step.color : color, fade * 0.55f);
                FootprintTrail.emit(consumer, entry, cameraPos, FootprintTrail.pad(step, size * 0.34f, size * 0.62f, tint),
                        FootprintTrail.pad(step, size * 0.22f, size * 0.44f, FootprintTrail.fade(tint, 0.75f)));
                if (bloom != null && fade > 0.15f) {
                    bloom.add(new BloomRenderer.Point(step.pos.add(0.0, 0.05, 0.0), size * 0.5f * fade,
                            FootprintTrail.fade(step.color != 0 ? step.color : color, fade), (float)step.time * 0.001f));
                }
            }
        }
        if (bloom != null && !bloom.isEmpty()) {
            BloomRenderer.render(bloom, event.getStack(), cameraPos, camera.getRotation(), 0.08f, 140.0f, 1.25f, true);
        }
    }

    private static Entity owner(MinecraftClient client, int id) {
        if (client.world == null) {
            return null;
        }
        return client.world.getEntityById(id);
    }

    /** Плоская овальная площадка, развёрнутая по направлению шага. */
    private static CosmeticMesh pad(Step step, float width, float length, int color) {
        CosmeticMesh mesh = new CosmeticMesh();
        float rad = (float)Math.toRadians(step.yaw);
        float cos = (float)Math.cos(rad);
        float sin = (float)Math.sin(rad);
        int segments = 10;
        Vec3d previous = null;
        for (int i = 0; i <= segments; i++) {
            double angle = Math.PI * 2.0 * i / segments;
            double ox = Math.cos(angle) * width;
            double oz = Math.sin(angle) * length;
            Vec3d point = new Vec3d(step.pos.x + ox * cos - oz * sin, step.pos.y, step.pos.z + ox * sin + oz * cos);
            if (previous != null) {
                mesh.tri(step.pos, previous, point, color, color, color);
            }
            previous = point;
        }
        return mesh;
    }

    private static void emit(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, CosmeticMesh first,
                             CosmeticMesh second) {
        FootprintTrail.emit(consumer, entry, camera, first);
        FootprintTrail.emit(consumer, entry, camera, second);
    }

    private static void emit(VertexConsumer consumer, MatrixStack.Entry entry, Vec3d camera, CosmeticMesh mesh) {
        if (mesh == null || mesh.isEmpty()) {
            return;
        }
        for (CosmeticMesh.Tri tri : mesh.triangles()) {
            consumer.vertex(entry, (float)(tri.a.x - camera.x), (float)(tri.a.y - camera.y), (float)(tri.a.z - camera.z)).color(tri.ca);
            consumer.vertex(entry, (float)(tri.b.x - camera.x), (float)(tri.b.y - camera.y), (float)(tri.b.z - camera.z)).color(tri.cb);
            consumer.vertex(entry, (float)(tri.c.x - camera.x), (float)(tri.c.y - camera.y), (float)(tri.c.z - camera.z)).color(tri.cc);
        }
    }

    private static int fade(int color, float amount) {
        int alpha = (int)((float)((color >>> 24) & 0xFF) * MathHelper.clamp(amount, 0.0f, 1.0f));
        return alpha << 24 | color & 0xFFFFFF;
    }
}
