/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector4f
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import ru.byazen.event.TotemPopEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.LazyMeshModel;
import ru.byazen.misc.SpriteAtlasRegion;
import ru.byazen.misc.TotemEffectRenderer;
import ru.byazen.misc.ByAzenHitParticles;
import ru.byazen.render.ParticleBillboardRenderer;
import ru.byazen.render.RenderCamera;
import ru.byazen.render.model.BuiltInMesh;
import ru.byazen.render.model.MeshBuilder;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.InlineMesh;
import ru.byazen.util.InlineMeshRenderer;
import ru.byazen.util.ModelRenderOptions;
import ru.byazen.util.TotemEffectSettings;

public final class TotemHologramRenderer
implements TotemEffectRenderer {
    private final List<TotemHologram> holograms;
    private MeshParticleTemplate meshParticleTemplate;
    static final float value2 = 1.65f;
    private final SpriteAtlasRegion spriteAtlasRegion;
    static final float value3 = 0.05f;
    static final double value4 = 0.5;
    private final Random random;
    private List<InlineMesh> values3;
    static final float value5 = 0.7f;
    static final int slot = 500;
    private List<InlineMesh> values4;
    private double value6;
    private static final InlineMeshRenderer HOLOGRAM_RENDERER = new InlineMeshRenderer("totem-hologram");
    private final TotemEffectSettings totemEffectSettings;
    private List<InlineMesh> values5;
    static final float getColorChannel8 = 15.0f;
    static final float value7 = 0.3f;
    static final float value8 = 0.5f;
    static final double value9 = 10000.0;
    static final float value10 = 1.0f;
    private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.PLAYER);
    static final long member13120 = 1399L;

    public TotemHologramRenderer(TotemEffectSettings totemEffectSettings) {
        this.holograms = new ArrayList<TotemHologram>();
        this.spriteAtlasRegion = ByAzenHitParticles.LIGHT.getSpriteAtlasRegion();
        this.random = new Random();
        this.totemEffectSettings = totemEffectSettings;
    }

    @Override
    public void renderWorld(WorldRenderEvent floatTypeEvent2) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null || RenderCamera.position() == null) {
            this.update2();
            return;
        }
        if (this.holograms.isEmpty()) {
            return;
        }
        if (!this.isActive()) {
            return;
        }
        long l = System.currentTimeMillis();
        for (int i = this.holograms.size() - 1; i >= 0; --i) {
            TotemHologram iliIllIll2 = this.holograms.get(i);
            if (this.process3(mc, iliIllIll2, l)) {
                this.holograms.remove(i);
                continue;
            }
            this.process(floatTypeEvent2, mc, iliIllIll2, l);
        }
    }

    @Override
    public void setTotemPopEvent(TotemPopEvent lIiillIliIEvent) {
        class_310 mc = class_310.method_1551();
        if (lIiillIliIEvent.getEntity() == null || mc.field_1724 == null) {
            return;
        }
        class_243 vec = new class_243(lIiillIliIEvent.getEntity().method_23317(), lIiillIliIEvent.getEntity().method_23318() + (double)lIiillIliIEvent.getEntity().method_17682() * 0.5, lIiillIliIEvent.getEntity().method_23321());
        class_243 vec2 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318() + (double)mc.field_1724.method_17682() * 0.5, mc.field_1724.method_23321());
        class_243 vec3 = vec2.method_1020(vec);
        if (vec3.method_1027() <= 1.0E-6) {
            vec3 = new class_243(0.0, 0.0, 1.0);
        }
        this.holograms.add(new TotemHologram(vec.method_1019(vec3.method_1029().method_1021(0.5)), System.currentTimeMillis(), lIiillIliIEvent));
        this.update();
    }

    private void process(WorldRenderEvent floatTypeEvent2, class_310 mc, TotemHologram hologram, long l) {
        class_243 vec = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318() + (double)mc.field_1724.method_17682(), mc.field_1724.method_23321());
        double d = vec.field_1352 - hologram.position.field_1352;
        double d2 = vec.field_1351 - hologram.position.field_1351;
        double d3 = vec.field_1350 - hologram.position.field_1350;
        float f = (float)Math.toDegrees(Math.atan2(d, d3));
        float f2 = (float)Math.toDegrees(-Math.atan2(d2, Math.sqrt(d * d + d3 * d3)));
        double d4 = hologram.progress(l);
        float f3 = this.process9((float)d4);
        float f4 = (float)((l - hologram.startedAt) % 3600L) / 10.0f;
        float f5 = (float)(1.0 + Math.sin(f4 * 0.05f) * 0.5);
        float f6 = (float)Math.sin((double)f3 * Math.PI * 6.0) * 0.35f;
        float f7 = (float)(0.2 * (1.0 - (double)f3));
        float f8 = f4 * 2.0f;
        float f9 = (float)Math.sin(d4 * Math.PI * 8.0) * 7.0f;
        float f10 = (this.random.nextFloat() - 0.5f) * 2.0f;
        float f11 = 1.65f * (float)(d4 < 0.5 ? 2.0 * d4 * (1.2 - d4) : 1.0 - (d4 - 0.5) * 2.0);
        class_243 vec2 = RenderCamera.position();
        class_4587 matrices2 = new class_4587();
        matrices2.method_22904(hologram.position.field_1352 - vec2.field_1352, hologram.position.field_1351 - vec2.field_1351, hologram.position.field_1350 - vec2.field_1350);
        matrices2.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(f - 25.0f));
        matrices2.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(f2));
        matrices2.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(f4 * f5));
        matrices2.method_46416(0.0f, f6, 0.0f);
        matrices2.method_22904(Math.sin(Math.toRadians(f8)) * (double)f7, 0.0, Math.cos(Math.toRadians(f8)) * (double)f7);
        matrices2.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(f9 + f10));
        matrices2.method_22905(f11, f11, f11);
        if (d4 < (double)0.3f) {
            matrices2.method_46416((this.random.nextFloat() - 0.5f) * 0.05f, (this.random.nextFloat() - 0.5f) * 0.05f, (this.random.nextFloat() - 0.5f) * 0.05f);
        }
        Matrix4f matrix4f = new Matrix4f((Matrix4fc)matrices2.method_23760().method_23761());
        float f12 = d4 < (double)0.3f ? 0.0f : class_3532.method_15363((float)((float)((d4 - (double)0.3f) / 0.19999998807907104)), (float)0.0f, (float)1.0f);
        float f13 = d4 >= 0.5 ? class_3532.method_15363((float)((float)((d4 - 0.5) / 0.19999998807907104)), (float)0.0f, (float)1.0f) : 0.0f;
        float f14 = d4 >= (double)0.7f ? class_3532.method_15363((float)((float)((d4 - (double)0.7f) / (double)0.3f)), (float)0.0f, (float)1.0f) : 0.0f;
        float f15 = d4 >= 0.5 ? 1.0f - f13 : 1.0f;
        float f16 = d4 >= 0.5 ? f13 * (1.0f - f14) : 0.0f;
        int n = this.totemEffectSettings.getIntType3();
        if (f15 > 0.0f) {
            if (d4 < (double)0.3f) {
                HOLOGRAM_RENDERER.process11(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), class_243.field_1353, this.values4, null, null, this.process10(n, f15 * 2.5f), List.of(matrix4f), ModelRenderOptions.getDefaultRenderOptions().process10(true));
            } else {
                Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(1.0f * f12, 0.0f, 0.0f).rotateZ((float)Math.toRadians(15.0f * f12));
                Matrix4f matrix4f3 = new Matrix4f((Matrix4fc)matrix4f).translate(-1.0f * f12, 0.0f, 0.0f).rotateZ((float)Math.toRadians(-15.0f * f12));
                if (!this.values3.isEmpty()) {
                    HOLOGRAM_RENDERER.process11(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), class_243.field_1353, this.values3, null, null, this.process10(n, f15 * 2.5f), List.of(matrix4f2), ModelRenderOptions.getDefaultRenderOptions().process10(true));
                }
                if (!this.values5.isEmpty()) {
                    HOLOGRAM_RENDERER.process11(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), class_243.field_1353, this.values5, null, null, this.process10(n, f15), List.of(matrix4f3), ModelRenderOptions.getDefaultRenderOptions().process10(true));
                }
                if (f16 > 0.0f) {
                    hologram.initializeParticles(this.meshParticleTemplate, this.value6);
                    class_243 vec3 = hologram.attractionPoint();
                    float f17 = 0.022f * (2.0f * (1.0f - f14));
                    float f18 = f14 * 2.0f + f13 * 0.2f;
                    float f19 = f13 + f14;
                    this.process7(hologram.startedAt, hologram.leftParticles, matrix4f3, vec3, n, f16, f18, f19, f14, f17, 0);
                    this.process7(hologram.startedAt, hologram.rightParticles, matrix4f2, vec3, n, f16, f18, f19, f14, f17, hologram.leftParticles.length);
                }
            }
        }
    }

    private boolean process2(float[] fArray, int n, int n2, int n3, double d, boolean bl) {
        return this.process8(fArray[n * 3], d, bl) && this.process8(fArray[n2 * 3], d, bl) && this.process8(fArray[n3 * 3], d, bl);
    }

    private boolean process3(class_310 mc, TotemHologram hologram, long l) {
        if (l - hologram.startedAt > 1399L) {
            return true;
        }
        class_243 vec = mc.field_1724 != null ? new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321()) : class_243.field_1353;
        return vec.method_1025(hologram.position) > 10000.0;
    }

    private List<InlineMesh> process4(List<InlineMesh> list, double d, boolean bl) {
        ArrayList<InlineMesh> arrayList = new ArrayList<InlineMesh>();
        for (InlineMesh inlineMesh : list) {
            int n;
            int[] nArray = inlineMesh.getIntType2();
            if (nArray == null || nArray.length == 0) continue;
            ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
            float[] fArray = inlineMesh.getFloatType();
            int n2 = 0;
            while (n2 + 2 < nArray.length) {
                n = nArray[n2];
                int n3 = nArray[n2 + 1];
                int n4 = nArray[n2 + 2];
                if (this.process2(fArray, n, n3, n4, d, bl)) {
                    arrayList2.add(n);
                    arrayList2.add(n3);
                    arrayList2.add(n4);
                }
                n2 += 3;
            }
            if (arrayList2.isEmpty()) continue;
            int[] nArray2 = new int[arrayList2.size()];
            for (n = 0; n < arrayList2.size(); ++n) {
                nArray2[n] = (Integer)arrayList2.get(n);
            }
            arrayList.add(MeshBuilder.create(inlineMesh.getFloatType(), inlineMesh.getFloatType3(), inlineMesh.getFloatType2(), nArray2, inlineMesh.getFloatType4()));
        }
        return arrayList;
    }

    private class_243 process5(Matrix4f matrix4f, double d, double d2, double d3) {
        Vector4f vector4f = new Vector4f((float)d, (float)d2, (float)d3, 1.0f);
        matrix4f.transform(vector4f);
        return new class_243((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
    }

    private double process6(List<InlineMesh> list) {
        double d = Double.POSITIVE_INFINITY;
        double d2 = Double.NEGATIVE_INFINITY;
        for (InlineMesh inlineMesh : list) {
            float[] fArray = inlineMesh.getFloatType();
            int n = 0;
            while (n + 2 < fArray.length) {
                d = Math.min(d, (double)fArray[n]);
                d2 = Math.max(d2, (double)fArray[n]);
                n += 3;
            }
        }
        return (d + d2) * 0.5;
    }

    private void update() {
        int n = this.holograms.size() - 12;
        if (n > 0) {
            this.holograms.subList(0, n).clear();
        }
    }

    private void process7(long l, MeshParticle[] iIililiIIIArray, Matrix4f matrix4f, class_243 vec, int n, float f, float f2, float f3, float f4, float f5, int n2) {
        if (iIililiIIIArray.length == 0 || f5 <= 0.001f) {
            return;
        }
        class_243 vec2 = RenderCamera.position();
        for (int i = 0; i < iIililiIIIArray.length; ++i) {
            class_243 vec3;
            MeshParticle particle = iIililiIIIArray[i];
            Random random = new Random(l + (long)n2 + (long)i);
            double d = particle.position.field_1352 + particle.direction.field_1352 * (double)f2 + (double)((random.nextFloat() - 0.5f) * 0.5f * f3);
            double d2 = particle.position.field_1351 + particle.direction.field_1351 * (double)f2 + (double)((random.nextFloat() - 0.5f) * 0.5f * f3) - (double)(f4 * 2.0f);
            double d3 = particle.position.field_1350 + particle.direction.field_1350 * (double)f2 + (double)((random.nextFloat() - 0.5f) * 0.5f * f3);
            class_243 vec4 = this.process5(matrix4f, d, d2, d3).method_1031(vec2.field_1352, vec2.field_1351, vec2.field_1350);
            if (vec != null && (vec3 = vec.method_1020(vec4)).method_1027() > 1.0E-6) {
                vec4 = vec4.method_1019(vec3.method_1029().method_1021((double)(f4 * 2.0f)));
            }
            ParticleBillboardRenderer.draw(vec4.field_1352, vec4.field_1351, vec4.field_1350, f5, f5, this.process10(n, f), ByAzenHitParticles.getParticleTexture(), false, 0.0f, this.spriteAtlasRegion.minU(), this.spriteAtlasRegion.minV(), this.spriteAtlasRegion.maxU(), this.spriteAtlasRegion.maxV());
        }
    }

    private boolean isActive() {
        if (!this.lazyMeshModel.isLoaded() || this.lazyMeshModel.getMeshModel() == null || this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
            return false;
        }
        if (this.values4 != null) {
            return true;
        }
        this.values4 = List.copyOf(this.lazyMeshModel.getMeshModel().getList());
        this.value6 = this.process6(this.values4);
        this.values5 = this.process4(this.values4, this.value6, false);
        this.values3 = this.process4(this.values4, this.value6, true);
        this.meshParticleTemplate = MeshParticleTemplate.from(this.values4);
        return true;
    }

    private boolean process8(float f, double d, boolean bl) {
        return bl ? (double)f >= d : (double)f < d;
    }

    private float process9(float f) {
        float f2 = class_3532.method_15363((float)f, (float)0.0f, (float)1.0f);
        return f2 < 0.5f ? 4.0f * f2 * f2 * f2 : 1.0f - (float)Math.pow(-2.0f * f2 + 2.0f, 3.0) * 0.5f;
    }

    private int process10(int n, float f) {
        int n2 = class_3532.method_15340((int)((int)((float)(n >>> 24 & 0xFF) * f)), (int)0, (int)255);
        return ColorUtils.withAlpha(n, (float)n2);
    }

    @Override
    public void update2() {
        this.holograms.clear();
    }

    private static final class TotemHologram {
        private final class_243 position;
        private final long startedAt;
        private final TotemPopEvent event;
        private MeshParticle[] leftParticles = new MeshParticle[0];
        private MeshParticle[] rightParticles = new MeshParticle[0];
        private boolean particlesInitialized;

        private TotemHologram(class_243 position, long startedAt, TotemPopEvent event) {
            this.position = position;
            this.startedAt = startedAt;
            this.event = event;
        }

        private double progress(long now) {
            return class_3532.method_15350((double)((double)(now - this.startedAt) / 1399.0), (double)0.0, (double)1.0);
        }

        private void initializeParticles(MeshParticleTemplate template, double splitX) {
            if (this.particlesInitialized || template == null) {
                return;
            }
            this.leftParticles = template.leftOf(splitX);
            this.rightParticles = template.rightOf(splitX);
            this.particlesInitialized = true;
        }

        private class_243 attractionPoint() {
            if (this.event.getEntity() == null) {
                return this.position;
            }
            return this.event.getEntity().method_30950(1.0f).method_1031(0.0, (double)this.event.getEntity().method_17682() * 0.5, 0.0);
        }
    }

    private record MeshParticleTemplate(List<MeshParticle> particles) {
        private static MeshParticleTemplate from(List<InlineMesh> meshes) {
            ArrayList<MeshParticle> particles = new ArrayList<MeshParticle>();
            for (InlineMesh mesh : meshes) {
                float[] positions = mesh.getFloatType();
                float[] normals = mesh.getFloatType3();
                int index = 0;
                while (index + 2 < positions.length) {
                    class_243 position = new class_243((double)positions[index], (double)positions[index + 1], (double)positions[index + 2]);
                    class_243 direction = normals != null && index + 2 < normals.length ? new class_243((double)normals[index], (double)normals[index + 1], (double)normals[index + 2]) : (position.method_1027() > 1.0E-7 ? position.method_1029() : class_243.field_1353);
                    particles.add(new MeshParticle(position, direction));
                    index += 3;
                }
            }
            if (particles.size() > 500) {
                int step = Math.max(1, particles.size() / 500);
                ArrayList<MeshParticle> sampled = new ArrayList<MeshParticle>(500);
                for (int index = 0; index < particles.size() && sampled.size() < 500; index += step) {
                    sampled.add((MeshParticle)particles.get(index));
                }
                particles = sampled;
            }
            return new MeshParticleTemplate(List.copyOf(particles));
        }

        private MeshParticle[] leftOf(double splitX) {
            return (MeshParticle[])this.particles.stream().filter(particle -> particle.position().field_1352 < splitX).toArray(MeshParticle[]::new);
        }

        private MeshParticle[] rightOf(double splitX) {
            return (MeshParticle[])this.particles.stream().filter(particle -> particle.position().field_1352 >= splitX).toArray(MeshParticle[]::new);
        }
    }

    private record MeshParticle(class_243 position, class_243 direction) {
    }
}

