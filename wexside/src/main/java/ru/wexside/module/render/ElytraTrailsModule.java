/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.lwjgl.opengl.GL46
 */
package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.lwjgl.opengl.GL46;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;

public final class ElytraTrailsModule
extends Module
implements ConfigSerializable {
    private static final double MIN_DISTANCE = 3.0E-5;
    private static final int MIN_POINTS = 4;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final List<TrailPoint> trail = new ArrayList<TrailPoint>();

    public ElytraTrailsModule(EventBus eventBus) {
        super(eventBus, "elytra_trails", "Elytra Trails", "\u0421\u043b\u0435\u0434 \u0437\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u043c \u043f\u0440\u0438 \u043f\u043e\u043b\u0451\u0442\u0435 \u043d\u0430 \u044d\u043b\u0438\u0442\u0440\u0430\u0445", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0441\u043b\u0435\u0434 \u043f\u0440\u0438 \u043f\u043e\u043b\u0451\u0442\u0435 \u043d\u0430 \u044d\u043b\u0438\u0442\u0440\u0430\u0445").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u0441\u043b\u0435\u0434\u0430").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::onWorldRender);
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.trail.clear();
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null || !client.field_1724.method_6101()) {
            this.trail.clear();
            return;
        }
        class_243 pos = client.field_1724.method_30950(event.getFloatType());
        if (this.trail.isEmpty()) {
            this.trail.add(new TrailPoint(pos, pos, false));
        } else {
            TrailPoint last = this.trail.getLast();
            if (pos.method_1025(last.to) > 3.0E-5) {
                this.trail.add(new TrailPoint(last.to, pos, true));
            }
        }
        this.trail.removeIf(TrailPoint::expired);
        this.renderTrail(event);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderTrail(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || !client.field_1724.method_6128() || client.field_1690.method_31044().method_31034() || this.trail.size() < 4) {
            return;
        }
        boolean cullEnabled = GL46.glIsEnabled((int)2884);
        boolean depthMask = GL46.glGetBoolean((int)2930);
        GL46.glDisable((int)2884);
        GL46.glDepthMask((boolean)false);
        try {
            class_4184 camera = client.field_1773.method_19418();
            class_243 cameraPos = camera.method_71156();
            float[] rgba = this.colorComponents(this.color.getColor());
            Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
            class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
            for (int i = 1; i < this.trail.size() - 2; ++i) {
                TrailPoint prev = this.trail.get(Math.max(0, i - 1));
                TrailPoint current = this.trail.get(i);
                TrailPoint next = this.trail.get(i + 1);
                TrailPoint after = this.trail.get(Math.min(this.trail.size() - 1, i + 2));
                if (!current.valid || !next.valid || current.from.method_1025(current.to) <= 1.0E-7 || next.from.method_1025(next.to) <= 1.0E-7) continue;
                TrailVertex left = this.buildVertex(current, prev, next, i, cameraPos, event.getFloatType(), rgba, 0);
                TrailVertex right = this.buildVertex(next, current, after, i + 1, cameraPos, event.getFloatType(), rgba, 1);
                if (left.alpha <= 0.001f || right.alpha <= 0.001f) continue;
                this.emitRibbon(consumer, matrix, left, right);
            }
            class_12249.method_76023().method_60895(consumer.method_60800());
        }
        finally {
            GL46.glDepthMask((boolean)depthMask);
            if (cullEnabled) {
                GL46.glEnable((int)2884);
            } else {
                GL46.glDisable((int)2884);
            }
        }
    }

    private TrailVertex buildVertex(TrailPoint point, TrailPoint prev, TrailPoint next, int index, class_243 cameraPos, float tickDelta, float[] rgba, int layer) {
        float alpha;
        float blue;
        float green;
        float red;
        float width;
        class_243 pos = point.lerp(tickDelta);
        class_243 prevPos = prev.lerp(tickDelta);
        class_243 nextPos = next.lerp(tickDelta);
        class_243 tangent = nextPos.method_1020(prevPos);
        if (tangent.method_1027() < 1.0E-5) {
            tangent = point.to.method_1020(point.from);
        }
        if (tangent.method_1027() < 1.0E-5) {
            tangent = new class_243(0.0, 0.0, 1.0);
        }
        tangent = tangent.method_1029();
        class_243 side = new class_243(-tangent.field_1350, 0.0, tangent.field_1352);
        side = side.method_1027() < 1.0E-5 ? new class_243(1.0, 0.0, 0.0) : side.method_1029();
        class_243 relative = pos.method_1020(cameraPos);
        float progress = point.progress();
        float fade = point.fade();
        float time = class_310.method_1551().field_1687.method_75260();
        float wave = class_3532.method_15374((double)(time * 0.15f + (float)index * 0.5f)) * 0.15f * fade;
        if (layer == 0) {
            width = 0.2f + 0.2f * progress + wave * 0.1f;
            float dim = progress * 0.3f;
            red = rgba[0] * (1.0f - dim);
            green = rgba[1] * (1.0f - dim);
            blue = rgba[2] * (1.0f - dim);
            alpha = fade * rgba[3];
        } else if (layer == 1) {
            width = 0.2f + (class_3532.method_15374((double)(time * 0.2f)) * 0.1f + 0.1f) * fade + wave * 0.1f;
            float boost = progress * 0.2f;
            red = class_3532.method_15363((float)(rgba[0] + boost), (float)0.0f, (float)1.0f);
            green = class_3532.method_15363((float)(rgba[1] + boost), (float)0.0f, (float)1.0f);
            blue = class_3532.method_15363((float)(rgba[2] + boost), (float)0.0f, (float)1.0f);
            alpha = fade * rgba[3];
        } else {
            width = 0.1f + wave * 0.05f;
            red = rgba[0];
            green = rgba[1];
            blue = rgba[2];
            alpha = class_3532.method_15363((float)(fade * rgba[3] * 0.3f), (float)0.0f, (float)0.3f);
        }
        return new TrailVertex((float)relative.field_1352, (float)relative.field_1351, (float)relative.field_1350, side, Math.max(width, 0.001f), red, green, blue, class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f));
    }

    private void emitRibbon(class_287 consumer, Matrix4f matrix, TrailVertex left, TrailVertex right) {
        class_243 a = left.side.method_1021((double)(-left.width));
        class_243 b = left.side.method_1021((double)left.width);
        class_243 c = right.side.method_1021((double)right.width);
        class_243 d = right.side.method_1021((double)(-right.width));
        this.vertex(consumer, matrix, left.x + (float)a.field_1352, left.y + (float)a.field_1351, left.z + (float)a.field_1350, left.red, left.green, left.blue, left.alpha);
        this.vertex(consumer, matrix, left.x + (float)b.field_1352, left.y + (float)b.field_1351, left.z + (float)b.field_1350, left.red, left.green, left.blue, left.alpha);
        this.vertex(consumer, matrix, right.x + (float)c.field_1352, right.y + (float)c.field_1351, right.z + (float)c.field_1350, right.red, right.green, right.blue, right.alpha);
        this.vertex(consumer, matrix, right.x + (float)d.field_1352, right.y + (float)d.field_1351, right.z + (float)d.field_1350, right.red, right.green, right.blue, right.alpha);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_22915(red, green, blue, alpha);
    }

    private float[] colorComponents(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >>> 24 & 0xFF) / 255.0f};
    }

    static final class TrailPoint {
        final class_243 from;
        final class_243 to;
        final boolean valid;

        TrailPoint(class_243 from, class_243 to, boolean valid) {
            this.from = from;
            this.to = to;
            this.valid = valid;
        }

        class_243 lerp(float tickDelta) {
            return new class_243(class_3532.method_16436((double)tickDelta, (double)this.from.field_1352, (double)this.to.field_1352), class_3532.method_16436((double)tickDelta, (double)this.from.field_1351, (double)this.to.field_1351), class_3532.method_16436((double)tickDelta, (double)this.from.field_1350, (double)this.to.field_1350));
        }

        float progress() {
            return class_3532.method_15363((float)((float)this.from.method_1022(this.to) * 4.0f), (float)0.0f, (float)1.0f);
        }

        float fade() {
            return class_3532.method_15363((float)(1.0f - this.progress()), (float)0.0f, (float)1.0f);
        }

        boolean expired() {
            return !this.valid;
        }
    }

    static final class TrailVertex {
        final float x;
        final float y;
        final float z;
        final class_243 side;
        final float width;
        final float red;
        final float green;
        final float blue;
        final float alpha;

        TrailVertex(float x, float y, float z, class_243 side, float width, float red, float green, float blue, float alpha) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
            this.width = width;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }
}

