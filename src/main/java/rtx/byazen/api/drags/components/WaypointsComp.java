package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.WaypointsModule;
import rtx.byazen.api.nav.Waypoint;
import rtx.byazen.api.nav.WaypointStore;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Список путевых точек (идея №72 из IDEAS.md): ближайшие точки с расстоянием и координатами,
 * активная цель подсвечена. Всё рисуется векторно, по-клиентски, без пиксельных иконок.
 */
public final class WaypointsComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-semibold";
    private static final String FONT_NAME = "montserrat-semibold";
    private static final String FONT_SMALL = "montserrat-medium";
    private static final float TITLE_SIZE = 7.0f;
    private static final float NAME_SIZE = 6.6f;
    private static final float INFO_SIZE = 5.8f;
    private static final float PAD_X = 8.0f;
    private static final float PAD_Y = 5.0f;
    private static final float ROW = 11.0f;

    private float currentWidth = 118.0f;
    private float currentHeight = 34.0f;
    private float alpha;
    private long lastNs;

    public WaypointsComp() {
        super("waypoints", 5.0f, 128.0f);
    }

    private static WaypointsModule module() {
        return ModuleManager.get().get(WaypointsModule.class);
    }

    @Override
    public String displayName() {
        return Lang.t("Путевые точки", "Waypoints");
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return this.currentHeight;
    }

    @Override
    public boolean isInteractive() {
        WaypointsModule module = WaypointsComp.module();
        return module != null && module.listEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> settings = super.buildHudSettings();
        WaypointsModule module = WaypointsComp.module();
        if (module != null) {
            settings.add(new BoolSetting(module.listInHud));
            settings.add(new BoolSetting(module.beams));
            settings.add(new BoolSetting(module.marker));
            settings.add(new BoolSetting(module.announce));
        }
        return settings;
    }

    @Override
    protected void render(DrawContext drawContext) {
        WaypointsModule module = WaypointsComp.module();
        if (module == null) {
            return;
        }
        boolean visible = module.listEnabled();
        float delta = this.deltaSeconds();
        this.alpha += ((visible ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        List<Waypoint> points = WaypointStore.get().nearby(module.listSize.getValue() <= 0.0f ? 8 : (int)module.listSize.getValue());
        float a = this.alpha;
        String title = Lang.t("Точки", "Waypoints");

        float nameWidth = 0.0f;
        for (Waypoint point : points) {
            nameWidth = Math.max(nameWidth, Render2D.msdfWidth(FONT_NAME, point.name(), NAME_SIZE) + 34.0f);
        }
        float width = Math.max(Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE) + 34.0f, nameWidth) + PAD_X * 2.0f;
        width = Math.max(112.0f, Math.min(240.0f, width));
        float height = PAD_Y * 2.0f + TITLE_SIZE + 4.0f + (points.isEmpty() ? INFO_SIZE : (float)points.size() * ROW);
        this.currentWidth = width;
        this.currentHeight = height;

        float x = this.getX();
        float y = this.getY();
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, width, height, 7.0f, a);
        Render2D.circle(x + PAD_X + 2.2f, y + PAD_Y + 3.4f, 2.4f, ClientAccent.accent(240.0f * a));
        Render2D.msdfText(FONT_TITLE, title, x + PAD_X + 8.0f, y + PAD_Y, TITLE_SIZE, ClientAccent.accent(240.0f * a));
        Render2D.msdfText(FONT_SMALL, Integer.toString(points.size()), x + width - PAD_X - 6.0f, y + PAD_Y + 1.0f, INFO_SIZE,
                ClientAccent.accentSoft(190.0f * a));
        String activeName = WaypointStore.get().activeName();
        if (points.isEmpty()) {
            Render2D.msdfText(FONT_SMALL, Lang.t("Пока пусто: .waypoint add <имя>", "Empty: .waypoint add <name>"),
                    x + PAD_X, y + PAD_Y + TITLE_SIZE + 4.0f, INFO_SIZE, WaypointsComp.color(190, 196, 210, 170.0f * a));
        }
        else {
            float rowY = y + PAD_Y + TITLE_SIZE + 4.0f;
            for (Waypoint point : points) {
                boolean active = point.name().equalsIgnoreCase(activeName);
                float dotX = x + PAD_X + 2.2f;
                float dotY = rowY + NAME_SIZE * 0.5f + 1.0f;
                Render2D.circle(dotX, dotY, active ? 3.0f : 2.4f, active ? ClientAccent.accentOpaque(250.0f * a) : (point.color() & 0xFFFFFF | WaypointsComp.alpha(235.0f * a) << 24));
                if (active) {
                    Render2D.circleOutline(dotX, dotY, 4.6f, 1.0f, ClientAccent.accentSoft(200.0f * a));
                }
                double meters = WaypointStore.playerPos() == null ? 0.0 : point.distanceTo(WaypointStore.playerPos());
                String info = Math.round(meters) + " \u043c";
                float infoWidth = Render2D.msdfWidth(FONT_SMALL, info, INFO_SIZE);
                Render2D.msdfText(FONT_SMALL, info, x + width - PAD_X - infoWidth, rowY + 0.6f, INFO_SIZE,
                        WaypointsComp.color(196, 204, 220, 190.0f * a));
                float nameMax = width - PAD_X * 2.0f - 12.0f - infoWidth - 6.0f;
                Render2D.msdfText(FONT_NAME, WaypointsComp.trim(point.name(), FONT_NAME, NAME_SIZE, nameMax),
                        x + PAD_X + 8.0f, rowY, NAME_SIZE,
                        active ? ClientAccent.accentBright(245.0f * a) : WaypointsComp.color(236, 240, 248, 225.0f * a));
                rowY += ROW;
            }
        }
        Render2D.flush();
    }

    private static String trim(String text, String font, float size, float maxWidth) {
        String result = text;
        while (result.length() > 1 && Render2D.msdfWidth(font, result + "…", size) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result.length() == text.length() ? text : result + "…";
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastNs == 0L ? 0.016f : (float)(now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private static int alpha(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }

    private static int color(int r, int g, int b, float alpha) {
        return WaypointsComp.alpha(alpha) << 24 | r << 16 | g << 8 | b;
    }
}
