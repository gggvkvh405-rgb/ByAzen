package rtx.byazen.api.drags.components;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * HUD-виджет статистики сессии (идея №112 из IDEAS.md): время, дистанция, блоки, убийства.
 */
public final class SessionStatsComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-bold";
    private static final String FONT_LINE = "montserrat-medium";
    private static final float TITLE_SIZE = 7.4f;
    private static final float LINE_SIZE = 6.4f;
    private static final float PAD_X = 8.0f;
    private static final float PAD_Y = 5.0f;
    private static final float LINE_HEIGHT = 9.0f;
    private static final float GAP = 3.0f;

    private float currentWidth = 120.0f;
    private float currentHeight = 46.0f;
    private float alpha;
    private long lastNs;

    public SessionStatsComp() {
        super("session_stats", 5.0f, 200.0f);
    }

    private static SessionStatsModule module() {
        return ModuleManager.get().get(SessionStatsModule.class);
    }

    @Override
    public String displayName() {
        return "Статистика сессии";
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
        SessionStatsModule module = SessionStatsComp.module();
        return module != null && module.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        SessionStatsModule module = SessionStatsComp.module();
        if (module != null) {
            list.add(new BoolSetting(module.showTime));
            list.add(new BoolSetting(module.showDistance));
            list.add(new BoolSetting(module.showBlocks));
            list.add(new BoolSetting(module.showKills));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        SessionStatsModule module = SessionStatsComp.module();
        if (module == null) {
            return;
        }
        float delta = this.delta();
        this.alpha += ((module.isEnabled() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        int lines = 0;
        if (module.showTime.getValue()) {
            ++lines;
        }
        if (module.showDistance.getValue()) {
            ++lines;
        }
        if (module.showBlocks.getValue()) {
            ++lines;
        }
        if (module.showKills.getValue()) {
            ++lines;
        }
        if (lines == 0) {
            return;
        }
        String title = "Сессия";
        float width = Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE);
        for (int i = 0; i < lines; ++i) {
            width = Math.max(width, Render2D.msdfWidth(FONT_LINE, this.lineText(module, i), LINE_SIZE));
        }
        float boxWidth = width + PAD_X * 2.0f;
        float boxHeight = PAD_Y * 2.0f + TITLE_SIZE + GAP + (float) lines * LINE_HEIGHT - 2.0f;
        this.currentWidth = boxWidth;
        this.currentHeight = boxHeight;

        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, boxWidth, boxHeight, 7.0f, a);
        Render2D.msdfText(FONT_TITLE, title, x + PAD_X, y + PAD_Y, TITLE_SIZE,
                rtx.byazen.api.ui.theme.ClientAccent.accentSoft(230.0f * a));
        int color = SessionStatsComp.rgba(228, 233, 242, 220.0f * a);
        for (int i = 0; i < lines; ++i) {
            Render2D.msdfText(FONT_LINE, this.lineText(module, i), x + PAD_X, y + PAD_Y + TITLE_SIZE + GAP + (float) i * LINE_HEIGHT,
                    LINE_SIZE, color);
        }
        Render2D.flush();
    }

    private String lineText(SessionStatsModule module, int index) {
        if (module.showTime.getValue() && index == 0) {
            return "Время: " + SessionStatsModule.formatTime(module.playtimeMs());
        }
        if (module.showDistance.getValue()) {
            if (index == (module.showTime.getValue() ? 1 : 0)) {
                return "Пройдено: " + SessionStatsModule.formatDistance(module.distance());
            }
        }
        if (module.showBlocks.getValue()) {
            int position = (module.showTime.getValue() ? 1 : 0) + (module.showDistance.getValue() ? 1 : 0);
            if (index == position) {
                return "Блоков: " + module.blocks();
            }
        }
        if (module.showKills.getValue()) {
            int position = (module.showTime.getValue() ? 1 : 0) + (module.showDistance.getValue() ? 1 : 0)
                    + (module.showBlocks.getValue() ? 1 : 0);
            if (index == position) {
                return "Убито: " + module.kills();
            }
        }
        return "";
    }

    private float delta() {
        long now = System.nanoTime();
        float delta = this.lastNs == 0L ? 0.016f : (float) (now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        return a << 24 | r << 16 | g << 8 | b;
    }
}
