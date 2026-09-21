/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1839
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 */
package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class DurationVisualiserModule
extends Module
implements ConfigSerializable {
    private static final float ICON_SIZE = 20.0f;
    private static final float ICON_HALF = 10.0f;
    private static final float RING_RADIUS = 10.0f;
    private static final float RING_WIDTH = 3.0f;
    private static final float LABEL_SIZE = 7.0f;
    private static final float LABEL_OFFSET = 14.0f;
    private static final int BACKDROP_COLOR = 0x40000000;
    private static final int RING_COLOR = -1;
    private static final double TRACK_DISTANCE_SQ = 1024.0;
    private final BooleanSetting enabledSetting;
    private final ItemIconCache iconCache = new ItemIconCache();
    private final Map<class_1657, UseTracker> trackers = new HashMap<class_1657, UseTracker>();

    public DurationVisualiserModule(EventBus eventBus) {
        super(eventBus, "duration_visualiser", "Duration Visualiser", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0432\u0440\u0435\u043c\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u0443 \u0441\u043e\u0441\u0435\u0434\u043d\u0438\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u0432\u0440\u0435\u043c\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(HudRenderEvent.class, this::onHudRender);
        this.listen(WorldSessionEvent.class, event -> {
            this.trackers.clear();
            this.iconCache.update3();
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHudRender(HudRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            if (!this.trackers.isEmpty()) {
                this.trackers.clear();
            }
            this.iconCache.update3();
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        this.updateTrackers(client.field_1724);
        Matrix4f viewProjection = RenderProjection.viewProjectionMatrix();
        float scale = client.method_22683().method_4495();
        Matrix4f guiMatrix = new Matrix4f().scale(scale);
        long now = System.currentTimeMillis();
        ArrayList<HudEntry> entries = new ArrayList<HudEntry>();
        Iterator<Map.Entry<class_1657, UseTracker>> iterator = this.trackers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<class_1657, UseTracker> entry = iterator.next();
            class_1657 player = entry.getKey();
            UseTracker tracker = entry.getValue();
            long remaining = tracker.expiresAt - now;
            if (remaining <= 0L || !player.method_6115()) {
                iterator.remove();
                continue;
            }
            Vector2f screen = RenderProjection.projectEntityCenter((class_1297)player, viewProjection);
            if (screen == null) continue;
            entries.add(new HudEntry(screen.x, screen.y, remaining, tracker.totalDuration, player.method_6030()));
        }
        if (entries.isEmpty()) {
            return;
        }
        this.iconCache.update2();
        for (HudEntry hudEntry : entries) {
            hudEntry.icon = this.iconCache.process(hudEntry.stack);
        }
        ArrayList<BakedIconEntry> bakes = new ArrayList<BakedIconEntry>();
        this.iconCache.process2(scale, bakes);
        if (!bakes.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(bakes);
        }
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        renderer.begin();
        try {
            for (HudEntry hudEntry : entries) {
                float x = hudEntry.x;
                float y = hudEntry.y;
                renderer.drawRoundedRectangleBordered(guiMatrix, x - 10.0f, y - 10.0f, 20.0f, 20.0f, 20.0f * scale, 0.0f, 0x40000000);
                if (hudEntry.icon != null) {
                    this.iconCache.process3(renderer, guiMatrix, hudEntry.icon, x - 5.6f, y - 5.6f, 11.2f);
                }
                float progress = (float)hudEntry.remaining / (float)hudEntry.totalDuration;
                renderer.drawCircle(guiMatrix, x, y, 0.0f, progress * 360.0f, 3.0f, 10.0f, -1);
                String label = String.format(Locale.US, "%.1f \u0441\u0435\u043a.", Float.valueOf((float)hudEntry.remaining / 1000.0f));
                float width = FontRegistry.font6.process3(label, 7.0f);
                FontRegistry.font6.process2(guiMatrix, renderer, label, x - width / 2.0f, y + 14.0f, 7.0f, -1);
            }
        }
        finally {
            renderer.end();
        }
        this.iconCache.update();
    }

    private void updateTrackers(class_746 local) {
        class_310 client = class_310.method_1551();
        for (class_1657 player : client.field_1687.method_18456()) {
            if (player.method_5858((class_1297)local) > 1024.0 || !player.method_6115()) {
                this.trackers.remove(player);
                continue;
            }
            class_1799 stack = player.method_6030();
            class_1839 action = stack.method_7976();
            if (stack.method_7960() || action != class_1839.field_8950 && action != class_1839.field_8946) {
                this.trackers.remove(player);
                continue;
            }
            int maxUseTime = stack.method_7935((class_1309)player);
            int useTime = player.method_6048();
            if (useTime != maxUseTime - 1) continue;
            long duration = (long)maxUseTime * 50L;
            this.trackers.put(player, new UseTracker(System.currentTimeMillis() + duration, duration));
        }
    }

    static final class UseTracker {
        final long expiresAt;
        final long totalDuration;

        UseTracker(long expiresAt, long totalDuration) {
            this.expiresAt = expiresAt;
            this.totalDuration = totalDuration;
        }
    }

    static final class HudEntry {
        final float x;
        final float y;
        final long remaining;
        final long totalDuration;
        final class_1799 stack;
        BakedItemIcon icon;

        HudEntry(float x, float y, long remaining, long totalDuration, class_1799 stack) {
            this.x = x;
            this.y = y;
            this.remaining = remaining;
            this.totalDuration = totalDuration;
            this.stack = stack;
        }
    }
}

