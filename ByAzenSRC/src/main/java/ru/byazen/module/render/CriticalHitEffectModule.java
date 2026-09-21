/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4050
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.byazen.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12249;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.ColorUtils;

public final class CriticalHitEffectModule
extends Module
implements ConfigSerializable {
    private static final int MAX_BLOCKS = 420;
    private static final int MAX_GHOSTS = 24;
    private static final int MAX_SPARKS = 260;
    private static final float SPARK_CHANCE = 0.2f;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final NumberSetting lifeTime;
    private final NumberSetting rise;
    private final NumberSetting pulseRadius;
    private final BooleanSetting filled;
    private final BooleanSetting ignoreDepth;
    private final List<CritGhost> ghosts = new ArrayList<CritGhost>();
    private final List<Spark> sparks = new ArrayList<Spark>();
    private long lastSparkTime = Long.MIN_VALUE;

    public CriticalHitEffectModule(EventBus eventBus) {
        super(eventBus, "crit_effect", "CriticalHit Effect", "\u042d\u0444\u0444\u0435\u043a\u0442 \u0434\u0443\u0448\u0438 \u0438 \u0432\u043e\u043b\u043d\u0430 \u043f\u043e \u0431\u043b\u043e\u043a\u0430\u043c \u043f\u0440\u0438 \u043a\u0440\u0438\u0442-\u0443\u0434\u0430\u0440\u0435", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442 \u043f\u0440\u0438 \u043a\u0440\u0438\u0442-\u0443\u0434\u0430\u0440\u0435 \u043f\u043e \u0438\u0433\u0440\u043e\u043a\u0443").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u0434\u0443\u0448\u0438 \u0438 \u0432\u043e\u043b\u043d\u044b").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 30.0).defaultValue(20.0).multiplier(50.0).precision(0).animationSpeed(20.0f).snapTo(5.0).name("Life time").id("life_time").description("\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0441\u043b\u0435\u0434\u0430")).build();
        this.registerSetting(this.lifeTime);
        this.rise = ((NumberSettingBuilder)NumberSetting.builder().range(0.5, 3.0).defaultValue(1.5).multiplier(1.0).precision(1).animationSpeed(20.0f).markers(0.5).snapTo(0.5).name("Rise").id("rise").description("\u0412\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u0434\u044a\u0451\u043c\u0430 \u0434\u0443\u0448\u0438")).build();
        this.registerSetting(this.rise);
        this.pulseRadius = ((NumberSettingBuilder)NumberSetting.builder().range(3.0, 8.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).markers(1.0).name("Pulse radius").id("pulse_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u0432\u043e\u043b\u043d\u044b \u043f\u043e \u0431\u043b\u043e\u043a\u0430\u043c")).build();
        this.registerSetting(this.pulseRadius);
        this.filled = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Filled").id("filled").description("\u0417\u0430\u043b\u0438\u0432\u0430\u0442\u044c \u043c\u043e\u0434\u0435\u043b\u044c \u0434\u0443\u0448\u0438")).build();
        this.registerSetting(this.filled);
        this.ignoreDepth = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Ignore depth").id("ignore_depth").description("\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u044d\u0444\u0444\u0435\u043a\u0442 \u0441\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b")).build();
        this.registerSetting(this.ignoreDepth);
    }

    @Override
    protected void initialize() {
        this.listen(EntityAttackEvent.class, this::onEntityAttack);
        this.listen(WorldRenderEvent.class, this::onWorldRender);
    }

    private void onEntityAttack(EntityAttackEvent event) {
        class_1657 player;
        class_310 client = class_310.method_1551();
        if (!this.enabledSetting.isEnabled() || client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        class_1297 target = event.getEntity();
        if (!(target instanceof class_1657) || (player = (class_1657)target) == client.field_1724 || !player.method_5805() || player.method_31481()) {
            return;
        }
        if (!this.isCriticalHit(client)) {
            return;
        }
        class_243 origin = this.groundPoint(client.field_1687, player);
        this.ghosts.add(new CritGhost(player, origin, this.collectBlocks(client.field_1687, origin), System.currentTimeMillis()));
        this.trimGhosts();
    }

    private void onWorldRender(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        if (!this.enabledSetting.isEnabled()) {
            this.clear();
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        if (client.field_1687 == null || client.field_1724 == null || cameraPos == null) {
            this.clear();
            return;
        }
        if (this.ghosts.isEmpty() && this.sparks.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        this.tickSparks(now);
        boolean drawGhosts = !this.ghosts.isEmpty();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 fill = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        class_287 lines = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        Iterator<CritGhost> iterator = this.ghosts.iterator();
        while (iterator.hasNext()) {
            CritGhost ghost = iterator.next();
            if (ghost.isExpired(now, this.lifetimeMs())) {
                iterator.remove();
                continue;
            }
            this.drawPulse(ghost, now, cameraPos, matrix, fill, lines);
            if (!drawGhosts) continue;
            this.drawSoul(ghost, now, cameraPos, matrix, fill, lines);
        }
        this.drawSparks(cameraPos, matrix, fill);
        class_12249.method_76023().method_60895(fill.method_60800());
        class_12249.method_76015().method_60895(lines.method_60800());
    }

    private void drawPulse(CritGhost ghost, long now, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 lines) {
        if (ghost.blocks.isEmpty()) {
            return;
        }
        float progress = ghost.progress(now, this.lifetimeMs());
        float fade = 1.0f - progress;
        fade *= fade;
        if (fade <= 0.025f) {
            return;
        }
        float eased = this.easeOutCubic(progress);
        double radius = this.pulseRadius.getValue() * (double)eased;
        double band = 0.95f + (1.0f - eased) * 0.22f;
        int base = this.color.getColor();
        int highlight = ColorUtils.lerp(base, -1, 0.38f);
        for (BlockSample sample : ghost.blocks) {
            float ring = 1.0f - (float)class_3532.method_15350((double)(Math.abs(sample.distance - radius) / band), (double)0.0, (double)1.0);
            if (ring <= 0.04f) continue;
            float strength = ring * fade;
            if (strength <= 0.02f) continue;
            if (!sample.sparked && radius + band * 0.45 >= sample.distance && ring > 0.55f) {
                sample.sparked = true;
                if (ThreadLocalRandom.current().nextFloat() < 0.2f) {
                    this.spawnSparks(sample, ghost.origin, strength, highlight);
                }
            }
            int inner = ColorUtils.multiplyAlpha(base, Math.min(1.0f, strength * 0.38f * 0.72f));
            int mid = ColorUtils.multiplyAlpha(highlight, Math.min(1.0f, strength * 0.22f));
            int outer = ColorUtils.multiplyAlpha(highlight, Math.min(1.0f, strength * 0.11f));
            int far = ColorUtils.multiplyAlpha(highlight, Math.min(1.0f, strength * 0.06f));
            int edge = ColorUtils.multiplyAlpha(highlight, Math.min(1.0f, strength));
            double inflate = 0.018 + (double)ring * 0.04;
            for (class_238 local : sample.boxes) {
                class_238 world = local.method_989((double)sample.pos.method_10263(), (double)sample.pos.method_10264(), (double)sample.pos.method_10260());
                this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 2.8), far);
                this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 1.8), outer);
                this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 0.95), mid);
                this.drawFilledBox(fill, matrix, cameraPos, world, inner);
            }
            this.drawBoxOutline(lines, matrix, cameraPos, class_238.method_30048((class_243)class_243.method_24953((class_2382)sample.pos), (double)1.0, (double)1.0, (double)1.0), edge);
        }
    }

    private void drawSoul(CritGhost ghost, long now, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 lines) {
        float progress = ghost.progress(now, this.lifetimeMs());
        float eased = this.easeOutCubic(progress);
        float fade = 1.0f - progress;
        fade *= fade;
        if (fade <= 0.025f) {
            return;
        }
        double lift = this.rise.getValue() * (double)eased;
        class_243 pos = ghost.origin.method_1031(0.0, lift, 0.0);
        int base = this.color.getColor();
        int highlight = ColorUtils.lerp(base, -1, 0.26f);
        float scale = 1.0f + (1.0f - progress) * 0.05f + (float)Math.sin((double)progress * Math.PI) * 0.12f;
        int outlineAlpha = class_3532.method_15340((int)((int)(230.0f * fade)), (int)0, (int)255);
        int fillAlpha = class_3532.method_15340((int)((int)(155.0f * fade)), (int)0, (int)255);
        class_238 box = this.playerBox(ghost, pos, scale);
        if (this.filled.isEnabled() && fillAlpha > 3) {
            this.drawFilledBox(fill, matrix, cameraPos, box.method_1014(0.15), ColorUtils.withAlpha(highlight, (float)class_3532.method_15340((int)((int)((float)fillAlpha * 0.34f)), (int)0, (int)255)));
            this.drawFilledBox(fill, matrix, cameraPos, box.method_1014(0.07), ColorUtils.withAlpha(highlight, (float)class_3532.method_15340((int)((int)((float)fillAlpha * 0.66f)), (int)0, (int)255)));
            this.drawFilledBox(fill, matrix, cameraPos, box, ColorUtils.withAlpha(base, (float)fillAlpha));
        }
        if (outlineAlpha > 3) {
            this.drawBoxOutline(lines, matrix, cameraPos, box, ColorUtils.withAlpha(highlight, (float)outlineAlpha) & 0xFFFFFF | outlineAlpha << 24);
        }
    }

    private void spawnSparks(BlockSample sample, class_243 origin, float strength, int color) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        class_243 away = new class_243(sample.centerX() - origin.field_1352, 0.0, sample.centerZ() - origin.field_1350);
        if (away.method_1027() <= 1.0E-6) {
            away = new class_243(random.nextDouble(-1.0, 1.0), 0.0, random.nextDouble(-1.0, 1.0));
        }
        away = away.method_1029();
        class_243 side = new class_243(-away.field_1350, 0.0, away.field_1352);
        boolean burst = random.nextFloat() < 0.1f;
        int count = burst ? 4 : 2;
        for (int i = 0; i < count; ++i) {
            double along = random.nextDouble(-0.22, 0.22);
            double out = random.nextDouble(-0.12, 0.12);
            class_243 pos = new class_243(sample.centerX() + side.field_1352 * along + away.field_1352 * out, sample.centerY() + random.nextDouble(0.02, 0.1), sample.centerZ() + side.field_1350 * along + away.field_1350 * out);
            class_243 velocity = away.method_1021(random.nextDouble(0.02, 0.1)).method_1019(side.method_1021(random.nextDouble(-0.05, 0.05))).method_1031(0.0, random.nextDouble(0.04, 0.16), 0.0);
            int tint = ColorUtils.lerp(color, -1, random.nextFloat() * 0.45f);
            this.sparks.add(new Spark(pos, velocity, tint, 0.028f + random.nextFloat() * 0.035f, Math.min(1.0f, strength * (0.5f + random.nextFloat() * 0.28f)), 0.18f + random.nextFloat() * 0.22f));
        }
        int overflow = this.sparks.size() - 260;
        if (overflow > 0) {
            this.sparks.subList(0, overflow).clear();
        }
    }

    private void tickSparks(long now) {
        if (this.lastSparkTime == Long.MIN_VALUE) {
            this.lastSparkTime = now;
            return;
        }
        double delta = Math.min((double)(now - this.lastSparkTime) / 1000.0, 0.05);
        this.lastSparkTime = now;
        for (int i = this.sparks.size() - 1; i >= 0; --i) {
            Spark spark = this.sparks.get(i);
            spark.tick(delta);
            if (!spark.isDead()) continue;
            this.sparks.remove(i);
        }
    }

    private void drawSparks(class_243 cameraPos, Matrix4f matrix, class_287 fill) {
        if (this.sparks.isEmpty()) {
            return;
        }
        for (Spark spark : this.sparks) {
            if (spark.life <= 0.02f || spark.size <= 0.004f) continue;
            int tint = ColorUtils.multiplyAlpha(ColorUtils.lerp(spark.color, -1, 0.12f), Math.min(1.0f, spark.life));
            this.drawBillboard(fill, matrix, cameraPos, spark.pos, spark.size, tint);
        }
    }

    private List<BlockSample> collectBlocks(class_638 world, class_243 origin) {
        ArrayList<BlockSample> samples = new ArrayList<BlockSample>();
        if (world == null) {
            return samples;
        }
        int extent = Math.max(2, class_3532.method_15357((double)this.pulseRadius.getValue()) + 2);
        class_2338 center = class_2338.method_49638((class_2374)origin);
        double maxDistance = this.pulseRadius.getValue() + (double)0.95f + 1.0;
        for (int x = -extent; x <= extent; ++x) {
            for (int y = -extent; y <= extent; ++y) {
                for (int z = -extent; z <= extent; ++z) {
                    class_2338 pos = new class_2338(center.method_10263() + x, center.method_10264() + y, center.method_10260() + z);
                    class_2680 state = world.method_8320(pos);
                    class_265 shape = state.method_26218((class_1922)world, pos);
                    if (shape.method_1110() || !this.hasAirNeighbor(world, pos)) continue;
                    List boxes = shape.method_1090();
                    double distance = this.sampleDistance(origin, pos, boxes);
                    if (distance > maxDistance) continue;
                    samples.add(new BlockSample(pos, boxes, distance));
                }
            }
        }
        samples.sort(Comparator.comparingDouble(sample -> sample.distance));
        if (samples.size() > 420) {
            return new ArrayList<BlockSample>(samples.subList(0, 420));
        }
        return samples;
    }

    private class_243 groundPoint(class_638 world, class_1657 player) {
        class_243 pos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
        if (world == null) {
            return pos;
        }
        int x = class_3532.method_15357((double)pos.field_1352);
        int z = class_3532.method_15357((double)pos.field_1350);
        int y = class_3532.method_15357((double)(pos.field_1351 + 0.35));
        for (int i = 0; i <= 8; ++i) {
            class_2338 blockPos = new class_2338(x, y - i, z);
            class_265 shape = world.method_8320(blockPos).method_26218((class_1922)world, blockPos);
            if (shape.method_1110()) continue;
            double top = shape.method_1090().stream().mapToDouble(box -> box.field_1325).max().orElse(1.0);
            return new class_243(pos.field_1352, (double)blockPos.method_10264() + top + 0.008, pos.field_1350);
        }
        return pos;
    }

    private boolean hasAirNeighbor(class_638 world, class_2338 pos) {
        if (world == null) {
            return false;
        }
        for (class_2350 direction : class_2350.values()) {
            class_2338 neighbor = pos.method_10093(direction);
            if (!world.method_8320(neighbor).method_26218((class_1922)world, neighbor).method_1110()) continue;
            return true;
        }
        return false;
    }

    private boolean isCriticalHit(class_310 client) {
        if (client.field_1724 == null) {
            return false;
        }
        return client.field_1724.field_6017 > 0.0 && !client.field_1724.method_24828() && !client.field_1724.method_6101() && !client.field_1724.method_5799() && !client.field_1724.method_5869() && client.field_1724.method_18376() != class_4050.field_18079 && !client.field_1724.method_5624() && !client.field_1724.method_6059(class_1294.field_5919) && client.field_1724.method_7261(0.5f) > 0.9f;
    }

    private double sampleDistance(class_243 origin, class_2338 pos, List<class_238> boxes) {
        double y = (double)pos.method_10264() + boxes.stream().mapToDouble(box -> (box.field_1322 + box.field_1325) * 0.5).average().orElse(0.5);
        double dx = (double)pos.method_10263() + 0.5 - origin.field_1352;
        double dy = y - origin.field_1351;
        double dz = (double)pos.method_10260() + 0.5 - origin.field_1350;
        return Math.sqrt(dx * dx + dz * dz + dy * dy * 0.35);
    }

    private class_238 playerBox(CritGhost ghost, class_243 pos, float scale) {
        double half = 0.3 * (double)scale;
        return new class_238(pos.field_1352 - half, pos.field_1351, pos.field_1350 - half, pos.field_1352 + half, pos.field_1351 + (double)(ghost.height * scale), pos.field_1350 + half);
    }

    private void drawBillboard(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_243 pos, float size, int color) {
        float half = size * 0.5f;
        float x = (float)(pos.field_1352 - cameraPos.field_1352);
        float y = (float)(pos.field_1351 - cameraPos.field_1351);
        float z = (float)(pos.field_1350 - cameraPos.field_1350);
        this.vertex(consumer, matrix, x - half, y - half, z, color);
        this.vertex(consumer, matrix, x + half, y - half, z, color);
        this.vertex(consumer, matrix, x + half, y + half, z, color);
        this.vertex(consumer, matrix, x - half, y + half, z, color);
    }

    private void drawFilledBox(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
        this.quad(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        this.quad(consumer, matrix, cameraPos, box.field_1323, box.field_1325, box.field_1321, box.field_1323, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1321, color);
        this.quad(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        this.quad(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1324, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        this.quad(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        this.quad(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1322, box.field_1324, color);
    }

    private void drawBoxOutline(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, color);
        this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
    }

    private void quad(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int color) {
        this.vertex(consumer, matrix, cameraPos, x1, y1, z1, color);
        this.vertex(consumer, matrix, cameraPos, x2, y2, z2, color);
        this.vertex(consumer, matrix, cameraPos, x3, y3, z3, color);
        this.vertex(consumer, matrix, cameraPos, x4, y4, z4, color);
    }

    private void line(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        this.vertex(consumer, matrix, cameraPos, x1, y1, z1, color);
        this.vertex(consumer, matrix, cameraPos, x2, y2, z2, color);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x, double y, double z, int color) {
        int alpha = color >>> 24 & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        consumer.method_22918((Matrix4fc)matrix, (float)(x - cameraPos.field_1352), (float)(y - cameraPos.field_1351), (float)(z - cameraPos.field_1350)).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha);
    }

    private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
        int alpha = color >>> 24 & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha);
    }

    private float easeOutCubic(float value) {
        float clamped = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        float inverse = 1.0f - clamped;
        return 1.0f - inverse * inverse * inverse;
    }

    private void trimGhosts() {
        int overflow = this.ghosts.size() - 24;
        if (overflow > 0) {
            this.ghosts.subList(0, overflow).clear();
        }
    }

    private void clear() {
        this.ghosts.clear();
        this.sparks.clear();
        this.lastSparkTime = Long.MIN_VALUE;
    }

    private long lifetimeMs() {
        return Math.max(1L, this.lifeTime.getLongValue());
    }

    static final class CritGhost {
        final class_243 origin;
        final float height;
        final List<BlockSample> blocks;
        final long spawnTime;

        CritGhost(class_1657 player, class_243 origin, List<BlockSample> blocks, long spawnTime) {
            this.origin = origin;
            this.height = player.method_17682();
            this.blocks = blocks;
            this.spawnTime = spawnTime;
        }

        boolean isExpired(long now, long lifetime) {
            return now - this.spawnTime >= lifetime;
        }

        float progress(long now, long lifetime) {
            return class_3532.method_15363((float)((float)(now - this.spawnTime) / (float)lifetime), (float)0.0f, (float)1.0f);
        }
    }

    static final class BlockSample {
        final class_2338 pos;
        final List<class_238> boxes;
        final double distance;
        boolean sparked;

        BlockSample(class_2338 pos, List<class_238> boxes, double distance) {
            this.pos = pos;
            this.boxes = boxes;
            this.distance = distance;
        }

        double centerX() {
            return (double)this.pos.method_10263() + 0.5;
        }

        double centerY() {
            return (double)this.pos.method_10264() + 0.5;
        }

        double centerZ() {
            return (double)this.pos.method_10260() + 0.5;
        }
    }

    static final class Spark {
        class_243 pos;
        class_243 velocity;
        final int color;
        float size;
        float life;
        final float fadeRate;

        Spark(class_243 pos, class_243 velocity, int color, float size, float life, float fadeRate) {
            this.pos = pos;
            this.velocity = velocity;
            this.color = color;
            this.size = size;
            this.life = life;
            this.fadeRate = fadeRate;
        }

        void tick(double delta) {
            this.pos = this.pos.method_1019(this.velocity.method_1021(delta * 20.0));
            this.velocity = this.velocity.method_1021(0.92).method_1031(0.0, -0.008 * delta * 20.0, 0.0);
            this.life -= this.fadeRate * (float)delta * 4.0f;
            this.size *= 0.97f;
        }

        boolean isDead() {
            return this.life <= 0.02f || this.size <= 0.004f;
        }
    }
}

