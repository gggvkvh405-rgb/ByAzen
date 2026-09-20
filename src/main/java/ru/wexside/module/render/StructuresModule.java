/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2637
 *  net.minecraft.class_2673
 *  net.minecraft.class_2675
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 */
package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2637;
import net.minecraft.class_2673;
import net.minecraft.class_2675;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.notification.ItemNotification;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class StructuresModule
extends Module
implements ConfigSerializable {
    private static final int TIMER_COLOR = -45747;
    private static final int NAME_COLOR = -1710619;
    private static final double MAX_DISTANCE_SQ = 2500.0;
    private static final long SETTLE_NS = 150000000L;
    private static final long DEFAULT_DURATION_MS = 30000L;
    private static final float CARD_HEIGHT = 16.0f;
    private static final float TITLE_SIZE = 7.0f;
    private static final float ICON_SIZE = 11.0f;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting world;
    private final ItemIconCache icons = new ItemIconCache();
    private final List<StructureMarker> markers = new ArrayList<StructureMarker>();
    private final Set<class_2338> pendingBlocks = new HashSet<class_2338>();
    private final List<WorldEventHit> pendingEvents = new ArrayList<WorldEventHit>();
    private volatile ParticleBurst pendingBurst;
    private volatile long burstStartedAt;

    public StructuresModule(EventBus eventBus) {
        super(eventBus, "structures", "Structures", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0432\u0440\u0435\u043c\u044f \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u043f\u043e\u0441\u0442\u0440\u043e\u0435\u043a (\u0442\u0440\u0430\u043f\u043a\u0438/\u043f\u043b\u0430\u0441\u0442\u044b)", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0422\u0430\u0439\u043c\u0435\u0440 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0442\u0440\u0430\u043f\u043e\u043a \u0438 \u043f\u043b\u0430\u0441\u0442\u043e\u0432 \u0432 \u043c\u0438\u0440\u0435").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.world = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("World").id("world").description("\u0420\u0438\u0441\u043e\u0432\u0430\u0442\u044c \u0442\u0430\u0439\u043c\u0435\u0440 \u043d\u0430 \u0441\u0430\u043c\u043e\u043c \u0441\u0442\u0440\u043e\u0435\u043d\u0438\u0438")).build();
        this.registerSetting(this.world);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onPacket);
        this.listen(ClientTickEvent.class, this::onTick);
        this.listen(HudRenderEvent.class, this::onHudRender);
        this.listen(WorldSessionEvent.class, event -> this.reset());
    }

    private void onPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2675) {
            class_2675 particle = (class_2675)packet;
            class_2338 pos2 = class_2338.method_49637((double)(particle.method_11544() + 0.5), (double)(particle.method_11547() + 0.5), (double)(particle.method_11546() + 0.5)).method_10062();
            if (this.pendingBurst == null) {
                this.burstStartedAt = System.nanoTime();
            }
            this.pendingBurst = new ParticleBurst(pos2, particle.method_11551());
            return;
        }
        if (packet instanceof class_2637) {
            class_2637 delta = (class_2637)packet;
            if (this.pendingBurst != null) {
                delta.method_30621((pos, state) -> {
                    if (!state.method_27852(class_2246.field_10124)) {
                        this.pendingBlocks.add(pos.method_10062());
                    }
                });
            }
            return;
        }
        if (packet instanceof class_2673) {
            class_2673 worldEvent = (class_2673)packet;
            if (this.pendingBurst != null) {
                this.pendingEvents.add(new WorldEventHit(worldEvent.method_11531(), worldEvent.method_11532()));
            }
        }
    }

    private void onTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        ParticleBurst burst = this.pendingBurst;
        if (burst != null && System.nanoTime() - this.burstStartedAt >= 150000000L) {
            HashSet<class_2338> blocks = new HashSet<class_2338>(this.pendingBlocks);
            ArrayList<WorldEventHit> extras = new ArrayList<WorldEventHit>(this.pendingEvents);
            this.pendingBlocks.clear();
            this.pendingEvents.clear();
            this.pendingBurst = null;
            this.finishBurst(burst, blocks, extras);
        }
        this.markers.removeIf(StructureMarker::expired);
    }

    private void reset() {
        this.pendingBurst = null;
        this.pendingBlocks.clear();
        this.pendingEvents.clear();
        this.markers.clear();
        this.icons.update3();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHudRender(HudRenderEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.world.isEnabled()) {
            this.icons.update3();
            return;
        }
        class_310 client = class_310.method_1551();
        class_243 cameraPos = client.field_1773.method_19418().method_71156();
        if (client.field_1724 == null || client.field_1687 == null || cameraPos == null || this.markers.isEmpty()) {
            return;
        }
        ArrayList<ScreenMarker> onScreen = new ArrayList<ScreenMarker>();
        for (StructureMarker structureMarker : this.markers) {
            Vector2f screen;
            double dz;
            double dy;
            double dx;
            if (structureMarker.expired() || (dx = structureMarker.x - cameraPos.field_1352) * dx + (dy = structureMarker.y - cameraPos.field_1351) * dy + (dz = structureMarker.z - cameraPos.field_1350) * dz > 2500.0 || (screen = this.worldToScreen(structureMarker.x, structureMarker.y, structureMarker.z)) == null) continue;
            onScreen.add(new ScreenMarker(screen.x, screen.y, structureMarker));
        }
        if (onScreen.isEmpty()) {
            return;
        }
        float scale = client.method_22683().method_4495();
        this.icons.update2();
        for (ScreenMarker screenMarker : onScreen) {
            screenMarker.icon = this.icons.process(screenMarker.marker.icon);
        }
        ArrayList<BakedIconEntry> arrayList = new ArrayList<BakedIconEntry>();
        this.icons.process2(scale, arrayList);
        if (!arrayList.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(arrayList);
        }
        Matrix4f matrix = new Matrix4f().scale(scale);
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        renderer.begin();
        try {
            for (ScreenMarker screenMarker : onScreen) {
                this.drawMarker(renderer, matrix, screenMarker);
            }
        }
        finally {
            renderer.end();
        }
        this.icons.update();
    }

    private void finishBurst(ParticleBurst burst, Set<class_2338> blocks, List<WorldEventHit> extras) {
        if (class_310.method_1551().field_1687 == null) {
            return;
        }
        class_243 center = this.centroid(blocks);
        if (center == null) {
            return;
        }
        class_1799 icon = new class_1799((class_1935)class_1802.field_20384);
        String name = extras.isEmpty() ? "Trap" : "Plast";
        this.markers.add(new StructureMarker(center.field_1352, center.field_1351, center.field_1350, name, icon, System.currentTimeMillis() + 30000L));
        NotificationCenter overlays = WexSideClient.getNotificationCenter();
        if (overlays != null) {
            overlays.push(new ItemNotification(icon));
        }
    }

    private void drawMarker(GuiDrawApi renderer, Matrix4f matrix, ScreenMarker screenMarker) {
        StructureMarker marker = screenMarker.marker;
        String name = marker.name;
        int seconds = marker.remainingSeconds();
        String timer = seconds + "\u0441";
        float nameWidth = FontRegistry.font2.process3(name + " ", 7.0f);
        float timerWidth = FontRegistry.font2.process3(timer, 7.0f);
        float width = 19.0f + nameWidth + timerWidth + 5.0f;
        float x = screenMarker.x - width / 2.0f;
        float y = screenMarker.y - 8.0f;
        renderer.drawRoundedRectangle(matrix, x, y, width, 16.0f, 8.0f, ThemeColors.separator());
        float iconX = x + 5.0f;
        float iconY = screenMarker.y - 5.5f;
        this.icons.process3(renderer, matrix, screenMarker.icon, iconX, iconY, 11.0f);
        float textX = iconX + 11.0f + 3.0f;
        float textY = screenMarker.y - 3.5f;
        FontRegistry.font2.process2(matrix, renderer, name + " ", textX, textY, 7.0f, -1710619);
        FontRegistry.font2.process2(matrix, renderer, timer, textX + nameWidth, textY, 7.0f, -45747);
    }

    private class_243 centroid(Set<class_2338> blocks) {
        if (blocks.isEmpty()) {
            return null;
        }
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        for (class_2338 pos : blocks) {
            x += (double)pos.method_10263() + 0.5;
            y += (double)pos.method_10264() + 0.5;
            z += (double)pos.method_10260() + 0.5;
        }
        int count = blocks.size();
        return new class_243(x / (double)count, y / (double)count, z / (double)count);
    }

    private Vector2f worldToScreen(double x, double y, double z) {
        return RenderProjection.project(new class_243(x, y, z));
    }

    private static final class ParticleBurst {
        final class_2338 pos;
        final Object parameters;

        ParticleBurst(class_2338 pos, Object parameters) {
            this.pos = pos;
            this.parameters = parameters;
        }
    }

    private record WorldEventHit(class_2338 pos, int eventId) {
    }

    private static final class StructureMarker {
        final double x;
        final double y;
        final double z;
        final String name;
        final class_1799 icon;
        final long expiresAt;

        StructureMarker(double x, double y, double z, String name, class_1799 icon, long expiresAt) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
            this.icon = icon;
            this.expiresAt = expiresAt;
        }

        boolean expired() {
            return System.currentTimeMillis() >= this.expiresAt;
        }

        int remainingSeconds() {
            return (int)Math.max(0L, (this.expiresAt - System.currentTimeMillis()) / 1000L);
        }
    }

    private static final class ScreenMarker {
        final float x;
        final float y;
        final StructureMarker marker;
        Object icon;

        ScreenMarker(float x, float y, StructureMarker marker) {
            this.x = x;
            this.y = y;
            this.marker = marker;
        }
    }
}

