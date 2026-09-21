package rtx.byazen.api.drags.components;

import java.time.LocalDateTime;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.ClockModule;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Часы и дата в HUD: скруглённая плашка, сглаженный шрифт, плавное появление.
 * Идея №36 из IDEAS.md.
 */
public final class ClockComp
extends Draggable {

    private static final String FONT_TIME = "montserrat-bold";
    private static final String FONT_DATE = "montserrat-medium";
    private static final float TIME_SIZE = 13.0f;
    private static final float DATE_SIZE = 6.0f;
    private static final float PAD_X = 9.0f;
    private static final float PAD_Y = 5.0f;
    private static final float LINE_GAP = 2.0f;
    private static final String[] MONTHS = {
            "янв", "фев", "мар", "апр", "мая", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"
    };
    private static final String[] WEEKDAYS = {
            "пн", "вт", "ср", "чт", "пт", "сб", "вс"
    };

    private float currentWidth = 64.0f;
    private float currentHeight = 22.0f;
    private float alpha;
    private long lastFrameNs;

    public ClockComp() {
        super("clock", 5.0f, 5.0f);
    }

    private static ClockModule module() {
        return ModuleManager.get().get(ClockModule.class);
    }

    @Override
    public String displayName() {
        return "Часы";
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
        ClockModule module = ClockComp.module();
        return module != null && module.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        ClockModule module = ClockComp.module();
        if (module != null) {
            list.add(new BoolSetting(module.h24));
            list.add(new BoolSetting(module.seconds));
            list.add(new BoolSetting(module.showDate));
        }
        return list;
    }

    @Override
    protected void render(DrawContext drawContext) {
        ClockModule module = ClockComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        this.alpha += ((module.isEnabled() ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        String time = ClockComp.time(now, module.h24.getValue(), module.seconds.getValue());
        String date = ClockComp.date(now);
        boolean withDate = module.showDate.getValue();

        float timeWidth = Render2D.msdfWidth(FONT_TIME, time, TIME_SIZE);
        float dateWidth = withDate ? Render2D.msdfWidth(FONT_DATE, date, DATE_SIZE) : 0.0f;
        float width = Math.max(timeWidth, dateWidth) + PAD_X * 2.0f;
        float height = withDate ? PAD_Y * 2.0f + TIME_SIZE + LINE_GAP + DATE_SIZE - 2.0f : TIME_SIZE + PAD_Y * 2.0f - 2.0f;
        this.currentWidth = width;
        this.currentHeight = height;

        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, width, height, 6.0f, a);
        Render2D.msdfText(FONT_TIME, time, x + (width - timeWidth) * 0.5f, y + PAD_Y, TIME_SIZE, ClientAccent.accent(242.0f * a));
        if (withDate) {
            Render2D.msdfText(FONT_DATE, date, x + (width - dateWidth) * 0.5f, y + PAD_Y + TIME_SIZE + LINE_GAP, DATE_SIZE,
                    ClientAccent.accentSoft(190.0f * a));
        }
        Render2D.flush();
    }

    private static String time(LocalDateTime now, boolean h24, boolean seconds) {
        int hour = now.getHour();
        if (!h24) {
            hour = hour % 12;
            if (hour == 0) {
                hour = 12;
            }
        }
        String base = ClockComp.two(hour) + ":" + ClockComp.two(now.getMinute());
        if (seconds) {
            base = base + ":" + ClockComp.two(now.getSecond());
        }
        if (!h24) {
            base = base + (now.getHour() < 12 ? " AM" : " PM");
        }
        return base;
    }

    private static String two(int value) {
        return (value < 10 ? "0" : "") + value;
    }

    private static String date(LocalDateTime now) {
        return now.getDayOfMonth() + " " + MONTHS[now.getMonthValue() - 1] + ", " + WEEKDAYS[now.getDayOfWeek().getValue() - 1];
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNs == 0L ? 0.016f : (float) (now - this.lastFrameNs) / 1.0E9f;
        this.lastFrameNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }
}
