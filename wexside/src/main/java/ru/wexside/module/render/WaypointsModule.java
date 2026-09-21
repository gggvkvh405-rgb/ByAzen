/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 */
package ru.wexside.module.render;

import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;

public final class WaypointsModule
extends Module
implements ConfigSerializable {
    private static final String CLOCK_ICON = "h";
    private static final String DISTANCE_ICON = "\u0429";
    private static final String ELLIPSIS = "...";
    private static final float CARD_HEIGHT = 30.0f;
    private static final float CARD_RADIUS = 8.0f;
    private static final float MIN_WIDTH = 80.0f;
    private static final float MAX_WIDTH = 120.0f;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u0442 \u043c\u0435\u0442\u043a\u0438 \u0432 \u043c\u0438\u0440\u0435").withKeybind().toggle()).build();

    public WaypointsModule(EventBus eventBus) {
        super(eventBus, "waypoints", "Waypoints", "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u0442 \u043c\u0435\u0442\u043a\u0438 \u0432 \u043c\u0438\u0440\u0435", ModuleCategory.valueOf("RENDER"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(HudRenderEvent.class, this::onHudRender);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHudRender(HudRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        WaypointStore store = WexSideClient.getWaypointStore();
        class_243 cameraPos = client.field_1773.method_19418().method_71156();
        if (player == null || client.field_1687 == null || store == null || cameraPos == null) {
            return;
        }
        List<Waypoint> waypoints = store.getWaypoints();
        if (waypoints.isEmpty()) {
            return;
        }
        int titleColor = ThemeColors.textPrimary();
        int nameColor = ThemeColors.textPrimary();
        int accent = ThemeColors.textSecondary();
        int chip = ThemeColors.separator();
        float scale = client.method_22683().method_4495();
        float inverseScale = 2.0f / scale;
        Matrix4f matrix = new Matrix4f().scale(scale);
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        class_243 playerPos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
        renderer.begin();
        try {
            for (Waypoint waypoint : waypoints) {
                class_243 world = new class_243((double)waypoint.x() + 0.5, (double)waypoint.y() + 0.5, (double)waypoint.z() + 0.5);
                Vector2f screen = this.worldToScreen(world);
                if (screen == null) continue;
                String name = waypoint.name();
                String kind = String.valueOf((Object)waypoint.type()).toLowerCase().contains("event") ? "Event" : "Waypoint";
                int meters = (int)playerPos.method_1022(world);
                String distance = meters + "m";
                float nameWidth = FontRegistry.font4.process3(name, 6.5f);
                float distanceWidth = FontRegistry.font5.process3(distance, 5.5f);
                float chipWidth = 18.5f + distanceWidth;
                float contentWidth = 5.0f + nameWidth + 6.0f + chipWidth;
                float width = Math.max(80.0f, Math.min(120.0f, contentWidth));
                float x = screen.x - width / 2.0f;
                float y = screen.y - 15.0f;
                float nameBudget = width - 5.0f - 6.0f - chipWidth;
                String clipped = this.clip(FontRegistry.font4, name, 6.5f, nameBudget);
                renderer.drawRoundedRectangle(matrix, x, y, width, 30.0f, 8.0f, chip);
                FontRegistry.font7.process2(matrix, renderer, kind, x + 5.0f, y + 4.75f, 6.75f, titleColor);
                FontRegistry.font3.process5(matrix, renderer, CLOCK_ICON, x + width - 11.0f, y + 4.0f, 6.0f, titleColor);
                FontRegistry.font4.process2(matrix, renderer, clipped, x + 5.0f, y + 30.0f - 12.5f, 6.5f, nameColor);
                renderer.drawRoundedOutline(matrix, x + width - 18.5f - distanceWidth, y + 30.0f - 14.0f, 13.5f + distanceWidth, 10.0f, 6.0f, inverseScale, chip);
                FontRegistry.font3.process5(matrix, renderer, DISTANCE_ICON, x + width - 15.0f - distanceWidth, y + 30.0f - 11.0f, 4.0f, accent);
                FontRegistry.font5.process2(matrix, renderer, distance, x + width - 8.0f - distanceWidth, y + 30.0f - 12.5f, 5.5f, accent);
            }
        }
        finally {
            renderer.end();
        }
    }

    private String clip(MsdfFontRenderer font, String text, float size, float maxWidth) {
        int end;
        if (text == null || text.isEmpty() || maxWidth <= 0.0f) {
            return "";
        }
        if (font.process3(text, size) <= maxWidth) {
            return text;
        }
        float ellipsisWidth = font.process3(ELLIPSIS, size);
        if (maxWidth <= ellipsisWidth) {
            return ELLIPSIS;
        }
        for (end = text.length(); end > 0 && font.process3(text.substring(0, end), size) + ellipsisWidth > maxWidth; --end) {
        }
        return text.substring(0, end) + ELLIPSIS;
    }

    private Vector2f worldToScreen(class_243 world) {
        return RenderProjection.project(world);
    }
}

