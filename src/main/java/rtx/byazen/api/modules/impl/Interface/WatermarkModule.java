package rtx.byazen.api.modules.impl.Interface;

import java.awt.Color;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;

/**
 * Водяной знак ByAzen (идея №46 из IDEAS.md): стиль плашки, набор данных, свой цвет и режим «для стрима».
 */
public final class WatermarkModule
extends InterfaceComponentModule {

    public static final String STYLE_PILL = "Плашка";
    public static final String STYLE_MINIMAL = "Минимал";
    public static final String STYLE_LOGO = "Только логотип";
    public static final String POS_CUSTOM = "Своя";
    public static final String POS_TOP_LEFT = "Слева сверху";
    public static final String POS_TOP_CENTER = "По центру сверху";
    public static final String POS_TOP_RIGHT = "Справа сверху";
    public static final String POS_BOTTOM_LEFT = "Слева снизу";
    public static final String POS_BOTTOM_CENTER = "По центру снизу";
    public static final String POS_BOTTOM_RIGHT = "Справа снизу";

    public final SeparatorSetting styleSeparator = this.register(new SeparatorSetting("Стиль"));
    public final ModeSetting style = this.register(new ModeSetting("Вид", "Как выглядит водяной знак.", STYLE_PILL, STYLE_PILL, STYLE_MINIMAL, STYLE_LOGO));
    public final ModeSetting position = this.register(new ModeSetting("Позиция", "Готовое расположение плашки на экране (или просто перетащите её мышью).", POS_CUSTOM, POS_CUSTOM, POS_TOP_LEFT, POS_TOP_CENTER, POS_TOP_RIGHT, POS_BOTTOM_LEFT, POS_BOTTOM_CENTER, POS_BOTTOM_RIGHT));
    public final ModeSetting palette = this.register(new ModeSetting("Цвет", "Цвет водяного знака: как в теме интерфейса или свой.", "Тема", "Тема", "Свой")
            .visibleWhen(() -> !this.style.is(STYLE_LOGO)));
    public final ColorSetting customColor = this.register(new ColorSetting("Свой цвет", "Базовый цвет для плавного градиента водяного знака.", new Color(105, 92, 255))
            .visible(() -> this.palette.is("Свой") && !this.style.is(STYLE_LOGO)));

    public final SeparatorSetting dataSeparator = this.register(new SeparatorSetting("Данные"));
    public final BooleanSetting showNick = this.register(new BooleanSetting("Ник", "Показывать ник игрока в водяном знаке.", true));
    public final BooleanSetting showTime = this.register(new BooleanSetting("Время", "Показывать время в водяном знаке.", true));
    public final BooleanSetting showFps = this.register(new BooleanSetting("FPS", "Показывать текущий FPS.", true));
    public final BooleanSetting showPing = this.register(new BooleanSetting("Пинг", "Показывать задержку до сервера.", false));
    public final BooleanSetting showTps = this.register(new BooleanSetting("TPS", "Показывать тики в секунду (оценка клиента).", false));

    public final SeparatorSetting streamSeparator = this.register(new SeparatorSetting("Стрим"));
    public final BooleanSetting streamSafe = this.register(new BooleanSetting("Прятать ник на стриме", "Если включён Streamer Mode, вместо ника показывается подмена (например Protected).", true));

    public WatermarkModule() {
        super("Watermark", "Водяной знак клиента: ник, время, fps, пинг и tps; три стиля и свой цвет.");
    }

    /** Нужно ли рисовать подложку-плашку. */
    public boolean hasPill() {
        return this.style.is(STYLE_PILL);
    }

    /** Нужно ли рисовать надпись ByAzen. */
    public boolean showBrand() {
        return true;
    }

    /** Нужно ли рисовать блок данных (ник, время, fps и т.д.). */
    public boolean showData() {
        return !this.style.is(STYLE_LOGO);
    }
}
