/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_12249;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BlockEspStore;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class BlockESPModule
extends Module
implements ConfigSerializable {
    private static final long SCAN_INTERVAL_MS = 1000L;
    private final BooleanSetting enabledSetting;
    private final NumberSetting scanRadius;
    private final ElapsedTimer scanTimer = new ElapsedTimer();
    private List<BlockHit> hits = new ArrayList<BlockHit>();

    public BlockESPModule(EventBus eventBus) {
        super(eventBus, "block_esp", "Block ESP", "\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0437\u0430\u0434\u0430\u043d\u043d\u044b\u0435 \u0431\u043b\u043e\u043a\u0438 \u0432 \u043c\u0438\u0440\u0435", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u043d\u043d\u044b\u0445 \u0431\u043b\u043e\u043a\u043e\u0432.").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.scanRadius = ((NumberSettingBuilder)NumberSetting.builder().range(5.0, 32.0).defaultValue(16.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Scan Radius").id("scan_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u043a\u0430\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f \u0431\u043b\u043e\u043a\u043e\u0432").aliases("scan radius", "\u0440\u0430\u0434\u0438\u0443\u0441")).build();
        this.registerSetting(this.scanRadius);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::onWorldRender);
        this.listen(WorldSessionEvent.class, event -> {
            this.hits = new ArrayList<BlockHit>();
        });
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_638 world = client.field_1687;
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        if (player == null || world == null || cameraPos == null) {
            return;
        }
        List<BlockHit> scanned = this.cachedHits();
        if (scanned.isEmpty()) {
            return;
        }
        ArrayList<VisibleBox> boxes = new ArrayList<VisibleBox>();
        for (BlockHit hit : scanned) {
            class_238 box = this.blockBox(hit.pos());
            if (cameraPos.method_1025(box.method_1005()) > 4096.0) continue;
            boxes.add(new VisibleBox(box, hit.color()));
        }
        if (boxes.isEmpty()) {
            return;
        }
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (VisibleBox box : boxes) {
            this.drawBox(consumer, matrix, cameraPos, box.box(), box.color());
        }
        class_12249.method_76015().method_60895(consumer.method_60800());
    }

    private void drawBox(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
        double minX = box.field_1323;
        double minY = box.field_1322;
        double minZ = box.field_1321;
        double maxX = box.field_1320;
        double maxY = box.field_1325;
        double maxZ = box.field_1324;
        this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, maxX, minY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, minY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, minX, minY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, minY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, maxY, minZ, maxX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, maxY, maxZ, minX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, minX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    private void drawLine(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = color >>> 24 & 0xFF;
        consumer.method_22918((Matrix4fc)matrix, (float)(x1 - cameraPos.field_1352), (float)(y1 - cameraPos.field_1351), (float)(z1 - cameraPos.field_1350)).method_1336(red, green, blue, alpha);
        consumer.method_22918((Matrix4fc)matrix, (float)(x2 - cameraPos.field_1352), (float)(y2 - cameraPos.field_1351), (float)(z2 - cameraPos.field_1350)).method_1336(red, green, blue, alpha);
    }

    private class_238 blockBox(class_2338 pos) {
        return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1.0, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 1.0);
    }

    private Map<class_2248, Integer> watchedBlocks() {
        HashMap<class_2248, Integer> map = new HashMap<class_2248, Integer>();
        BlockEspStore store = WexSideClient.getBlockEspStore();
        if (store == null) {
            return map;
        }
        for (Map.Entry<String, Integer> entry : store.getBlocks().entrySet()) {
            class_2960 id = class_2960.method_12829((String)entry.getKey());
            if (id == null) continue;
            class_7923.field_41175.method_17966(id).ifPresent(block -> map.put((class_2248)block, (Integer)entry.getValue()));
        }
        return map;
    }

    private List<BlockHit> cachedHits() {
        if (this.scanTimer.process(1000L)) {
            this.hits = this.scanHits();
            this.scanTimer.update();
        }
        return this.hits;
    }

    private List<BlockHit> scanHits() {
        ArrayList<BlockHit> found = new ArrayList<BlockHit>();
        class_310 client = class_310.method_1551();
        class_638 world = client.field_1687;
        class_746 player = client.field_1724;
        if (world == null || player == null) {
            return found;
        }
        Map<class_2248, Integer> colors = this.watchedBlocks();
        if (colors.isEmpty()) {
            return found;
        }
        int radius = this.scanRadius.getIntValue();
        class_2338 origin = player.method_24515();
        int minY = Math.max(origin.method_10264() - radius, world.method_31607());
        int maxY = Math.min(origin.method_10264() + radius, world.method_31607() + world.method_31605() - 1);
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    cursor.method_10103(origin.method_10263() + x, y, origin.method_10260() + z);
                    Integer color = colors.get(world.method_8320((class_2338)cursor).method_26204());
                    if (color == null) continue;
                    found.add(new BlockHit(cursor.method_10062(), color));
                }
            }
        }
        return found;
    }

    private record BlockHit(class_2338 pos, int color) {
    }

    private record VisibleBox(class_238 box, int color) {
    }
}

