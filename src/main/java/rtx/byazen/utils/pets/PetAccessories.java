package rtx.byazen.utils.pets;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.impl.Visuals.CustomPet;
import rtx.byazen.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;
import rtx.byazen.utils.cosmetics.CosmeticMesh;
import rtx.byazen.utils.math.MathUtils;
import rtx.byazen.utils.render.pipeline.ClientPipelines;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;

/**
 * Аксессуары питомца (идея №127 из IDEAS.md).
 * <p>
 * Бант на груди, шляпа или корона на голове, шарф вокруг шеи, очки на морде и кольцо настроения у лап.
 * Всё рисуется кодом из тех же гладких лент и колец, что и косметика игрока, поэтому аксессуар не
 * спорит с моделью питомца и остаётся красивым при любом размере.
 */
public final class PetAccessories {

    private static final Map<Integer, Float> PHASE = new HashMap<Integer, Float>();

    private PetAccessories() {
    }

    private static int rgba(int rgb, int a) {
        return Math.max(0, Math.min(255, a)) << 24 | rgb & 0xFFFFFF;
    }

    /** Рисует оформление питомца; вызывается модулем оформления каждый кадр мира. */
    public static void render(WorldRenderEvent event, MinecraftClient client) {
        if (event == null || client == null || client.player == null) {
            return;
        }
        CustomPetEntity pet = PetAccessories.localPet();
        if (pet == null || pet.isInvisible()) {
            return;
        }
        Camera camera = event.getCamera() != null ? event.getCamera() : client.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }
        MatrixStack.Entry entry = event.getStack().peek();
        Vec3d cameraPos = camera.getCameraPos();
        float partial = (float)event.getPartialTicks();
        VertexConsumer consumer = client.getBufferBuilders().getEntityVertexConsumers()
                .getBuffer(ClientPipelines.WORLD_PARTICLES_COLOR);

        Vec3d base = MathUtils.interpolate(pet, partial);
        float yaw = pet.getLerpedYaw(partial);
        float scale = PetStyleData.getScale();
        float height = pet.getHeight();
        Vec3d forward = PetAccessories.forward(yaw);
        Vec3d right = PetAccessories.right(yaw);
        Vec3d up = new Vec3d(0.0, 1.0, 0.0);
        float time = (float)System.currentTimeMillis() / 1000.0f;
        float mood = (float)PetStyleData.getMood() / 100.0f;
        float phase = PHASE.getOrDefault(pet.getId(), 0.0f) + 0.014f;
        PHASE.put(pet.getId(), phase);

        int body = PetAccessories.rgba(PetStyleData.getColor(), 244);
        int accent = PetAccessories.rgba(PetStyleData.getAccent(), 250);
        CosmeticMesh mesh = new CosmeticMesh();
        String accessory = PetStyleData.getAccessory();

        if (PetStyleData.ACCESSORY_BOW.equals(accessory)) {
            Vec3d chest = new Vec3d(base.x, base.y + (double)height * 0.60, base.z).add(forward.multiply(0.16 * (double)scale));
            for (int side = -1; side <= 1; side += 2) {
                Vec3d outer = chest.add(right.multiply((double)side * 0.10 * (double)scale)).add(up.multiply(0.045 * (double)scale));
                Vec3d low = chest.add(right.multiply((double)side * 0.085 * (double)scale)).subtract(up.multiply(0.045 * (double)scale));
                mesh.tri(chest.add(right.multiply((double)side * 0.015 * (double)scale)), low, outer, body, body, accent);
                mesh.tri(chest.add(right.multiply((double)side * 0.015 * (double)scale)), outer,
                        chest.add(right.multiply((double)side * 0.015 * (double)scale)).add(up.multiply(0.09 * (double)scale)),
                        body, accent, body);
            }
        } else if (PetStyleData.ACCESSORY_HAT.equals(accessory) || PetStyleData.ACCESSORY_CROWN.equals(accessory)) {
            boolean crown = PetStyleData.ACCESSORY_CROWN.equals(accessory);
            Vec3d head = new Vec3d(base.x, base.y + (double)height * 0.94, base.z);
            float radius = (crown ? 0.10f : 0.12f) * scale;
            float hatHeight = (crown ? 0.09f : 0.19f) * scale;
            mesh.cylinder(head, right, forward, up, radius, (double)hatHeight, 16, body, accent);
            mesh.annulus(head, right, forward, radius, radius * (crown ? 1.45f : 1.85f), 16, accent, body);
            if (crown) {
                for (int i = 0; i < 5; ++i) {
                    double angle = (double)i / 5.0 * Math.PI * 2.0;
                    Vec3d side = right.multiply(Math.cos(angle)).add(forward.multiply(Math.sin(angle)));
                    Vec3d foot = head.add(side.multiply((double)radius * 1.15)).add(up.multiply((double)hatHeight));
                    mesh.tri(foot, foot.add(side.multiply(0.035 * (double)scale)),
                            head.add(side.multiply((double)radius * 1.2)).add(up.multiply((double)(hatHeight + 0.12f * scale))),
                            accent, accent, body);
                }
            }
        } else if (PetStyleData.ACCESSORY_SCARF.equals(accessory)) {
            Vec3d neck = new Vec3d(base.x, base.y + (double)height * 0.74, base.z);
            mesh.annulus(neck, right, forward, 0.11f * scale, 0.16f * scale, 18, body, accent);
            Vec3d tip = neck.add(forward.multiply(-0.20 * (double)scale)).add(up.multiply(-0.24 * (double)scale));
            Vec3d[] spine = new Vec3d[4];
            float[] widths = new float[4];
            for (int i = 0; i < spine.length; ++i) {
                float t = (float)i / (float)(spine.length - 1);
                spine[i] = neck.add(tip.subtract(neck).multiply((double)t))
                        .add(right.multiply((double)(0.05f * scale * (float)Math.sin((double)t * 3.2 + (double)time))));
                widths[i] = (0.055f - 0.012f * t) * scale;
            }
            mesh.ribbonSided(spine, widths, PetStyleData.getColor(), PetStyleData.getAccent(), 240, right);
        } else if (PetStyleData.ACCESSORY_GLASSES.equals(accessory)) {
            Vec3d face = new Vec3d(base.x, base.y + (double)height * 0.86, base.z).add(forward.multiply(0.14 * (double)scale));
            for (int side = -1; side <= 1; side += 2) {
                Vec3d center = face.add(right.multiply((double)side * 0.07 * (double)scale));
                mesh.annulus(center, right, up, 0.034f * scale, 0.05f * scale, 14, body, body);
                mesh.annulus(center, right, up, 0.0f, 0.033f * scale, 14, PetAccessories.rgba(PetStyleData.getAccent(), 150),
                        PetAccessories.rgba(PetStyleData.getAccent(), 150));
            }
            mesh.quad(face.add(right.multiply(-0.03 * (double)scale)).add(up.multiply(0.035 * (double)scale)),
                    face.add(right.multiply(0.03 * (double)scale)).add(up.multiply(0.035 * (double)scale)),
                    face.add(right.multiply(0.03 * (double)scale)).add(up.multiply(0.024 * (double)scale)),
                    face.add(right.multiply(-0.03 * (double)scale)).add(up.multiply(0.024 * (double)scale)),
                    body, body, body, body);
        }

        // кольцо настроения: чем веселее питомец, тем ярче
        float ringRadius = (0.32f + 0.045f * (float)Math.sin((double)phase * 2.2)) * scale;
        mesh.annulus(new Vec3d(base.x, base.y + 0.02, base.z), right, forward, ringRadius, ringRadius + 0.07f * scale, 20,
                PetAccessories.rgba(PetStyleData.getAccent(), Math.round(50.0f + 110.0f * mood)),
                PetAccessories.rgba(PetStyleData.getColor(), Math.round(20.0f + 70.0f * mood)));
        PetAccessories.emit(consumer, entry, cameraPos, mesh);

        BloomRenderer.Point point = new BloomRenderer.Point(new Vec3d(base.x, base.y + (double)height * 0.55, base.z),
                0.5f + 0.5f * mood, PetStyleData.getAccent(), phase);
        java.util.ArrayList<BloomRenderer.Point> points = new java.util.ArrayList<BloomRenderer.Point>();
        points.add(point);
        BloomRenderer.render(points, event.getStack(), cameraPos, camera.getRotation(), 0.07f, 110.0f + 90.0f * mood, 1.6f, true);
    }

    /** Ограничивающая рамка для подписи имени: экранные координаты считает NamePlate. */
    public static Vec3d nameAnchor(MinecraftClient client, float partial) {
        CustomPetEntity pet = PetAccessories.localPet();
        if (pet == null || client == null) {
            return null;
        }
        Vec3d base = MathUtils.interpolate(pet, partial);
        return new Vec3d(base.x, base.y + (double)pet.getHeight() * PetStyleData.getScale() + 0.42, base.z);
    }

    private static CustomPetEntity localPet() {
        try {
            return CustomPet.getInstance().localPet();
        }
        catch (Throwable throwable) {
            return null;
        }
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

    private static Vec3d forward(float yaw) {
        double radians = Math.toRadians((double)yaw);
        return new Vec3d(-Math.sin(radians), 0.0, Math.cos(radians));
    }

    private static Vec3d right(float yaw) {
        double radians = Math.toRadians((double)yaw);
        return new Vec3d(-Math.cos(radians), 0.0, -Math.sin(radians));
    }
}
