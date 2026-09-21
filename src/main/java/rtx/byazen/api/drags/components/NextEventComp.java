package rtx.byazen.api.drags.components;

import java.awt.Color;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.liteapi.LiteApiEvents;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NextEventModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.lang.Lang;

/**
 * Плашка ближайшего ивента сервера (идея №37 из IDEAS.md): название, подробности и отсчёт
 * времени до начала. Данные приходят по LiteApi. Рендер полностью векторный.
 */
public final class NextEventComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float TITLE_SIZE = 7.2f;
    private static final float DETAIL_SIZE = 5.8f;
    private static final float PAD_X = 8.0f;
    private static final float PAD_Y = 4.5f;
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private float currentWidth = 120.0f;
    private float currentHeight = 22.0f;
    private float alpha;
    private long lastNs;

    public NextEventComp() {
        super("next_event", 5.0f, 160.0f);
    }

    private static NextEventModule module() {
        return ModuleManager.get().get(NextEventModule.class);
    }

    @Override
    public String displayName() {
        return Lang.t("Следующий ивент", "Next event");
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
        NextEventModule module = NextEventComp.module();
        return module != null && module.isEnabled() && module.showWidget.getValue();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> settings = super.buildHudSettings();
        NextEventModule module = NextEventComp.module();
        if (module != null) {
            settings.add(new BoolSetting(module.showWidget));
            settings.add(new BoolSetting(module.showTime));
            settings.add(new BoolSetting(module.showDetail));
            settings.add(new BoolSetting(module.hideUnknown));
        }
        return settings;
    }

    @Override
    protected void render(DrawContext drawContext) {
        NextEventModule module = NextEventComp.module();
        if (module == null) {
            return;
        }
        LiteApiEvents.Snapshot event = LiteApiEvents.current();
        boolean visible = module.isEnabled() && module.showWidget.getValue() && (event != null || !module.hideUnknown.getValue());
        float delta = this.deltaSeconds();
        this.alpha += ((visible ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 8.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        String title = event == null ? Lang.t("Ивент неизвестен", "Event unknown") : Lang.t("Следующий: ", "Next: ") + event.name();
        String detail = NextEventComp.detail(event, module);
        float titleWidth = Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE);
        float detailWidth = detail.isBlank() ? 0.0f : Render2D.msdfWidth(FONT_TEXT, detail, DETAIL_SIZE);
        float iconSize = 9.0f;
        float width = Math.max(titleWidth, detailWidth) + PAD_X * 2.0f + iconSize + 4.0f;
        float height = detail.isBlank() ? 15.0f : 24.0f;
        this.currentWidth = width;
        this.currentHeight = height;

        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        boolean soon = event != null && LiteApiEvents.secondsLeft() >= 0L && LiteApiEvents.secondsLeft() <= 300L;
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, width, height, 7.0f, a);
        Render2D.outline(x, y, width, height, 7.0f, 0.8f,
                ClientAccent.accentSoft((soon ? 110.0f : 50.0f) * a));
        rtx.byazen.utils.render.icons.IconLibrary.draw(soon ? "\u041c\u043e\u043b\u043d\u0438\u044f" : "\u0427\u0430\u0441\u044b",
                x + PAD_X, y + (height - iconSize) * 0.5f, iconSize, ClientAccent.accent(235.0f * a));
        float textX = x + PAD_X + iconSize + 4.0f;
        Render2D.msdfText(FONT_TITLE, title, textX, y + 3.4f, TITLE_SIZE, NextEventComp.color(255, 255, 255, 240.0f * a));
        if (!detail.isBlank()) {
            Render2D.msdfText(FONT_TEXT, detail, textX, y + 13.0f, DETAIL_SIZE,
                    soon ? ClientAccent.accentBright(215.0f * a) : NextEventComp.color(206, 212, 226, 170.0f * a));
        }
        Render2D.flush();
    }

    private static String detail(LiteApiEvents.Snapshot event, NextEventModule module) {
        if (event == null) {
            return Lang.t("Сервер пока не сообщил (LiteApi)", "Server has not reported yet (LiteApi)");
        }
        StringBuilder builder = new StringBuilder();
        if (module.showTime.getValue()) {
            long left = LiteApiEvents.secondsLeft();
            if (left >= 0L) {
                String hours = Lang.t(" ч ", " h ");
                String minutes = Lang.t(" мин ", " min ");
                String seconds = Lang.t(" с", " s");
                if (left >= 3600L) {
                    builder.append(left / 3600L).append(hours).append((left % 3600L) / 60L).append(minutes);
                }
                else if (left >= 60L) {
                    builder.append(left / 60L).append(minutes).append(left % 60L).append(seconds);
                }
                else {
                    builder.append(left).append(seconds);
                }
            }
            else if (event.atMs() > 0L) {
                builder.append("в ").append(CLOCK.format(Instant.ofEpochMilli(event.atMs())));
            }
        }
        if (module.showDetail.getValue() && event.detail() != null && !event.detail().isBlank()) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append(event.detail());
        }
        if (builder.length() == 0 && event.atMs() > 0L) {
            builder.append(CLOCK.format(Instant.ofEpochMilli(event.atMs())));
        }
        return builder.toString();
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastNs == 0L ? 0.016f : (float) (now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private static int color(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return new Color(r, g, b, a).getRGB();
    }
}
