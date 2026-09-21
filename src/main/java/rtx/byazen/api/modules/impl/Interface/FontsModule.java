package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.render.render2d.font.CustomFonts;

/**
 * Свои шрифты и читаемость интерфейса (идеи №49 и №195 из IDEAS.md).
 * <p>
 * TTF/OTF кладутся в {@code .minecraft/byazen/fonts}. Пиксельные шрифты не подключаются:
 * проверка сглаженности идёт по краям букв (полупрозрачные пиксели), правило «никаких пикселей»
 * соблюдается автоматически.
 */
public final class FontsModule
extends InterfaceComponentModule {

    public final SeparatorSetting main = this.register(new SeparatorSetting("Шрифты"));
    public final ButtonSetting refresh = this.register(new ButtonSetting("Обновить список", "Перечитать папку byazen/fonts. Пиксельные шрифты будут отклонены с пояснением в чате.")
            .label("Обновить").onClick(this::reload));
    public final SelectSetting font = this.register(new SelectSetting("Свой шрифт", "Шрифт TTF/OTF из папки byazen/fonts для интерфейса клиента. Логотип и иконки остаются фирменными.")
            .value(CustomFonts.names()).selected(CustomFonts.NONE));
    public final SliderSetting textScale = this.register(new SliderSetting("Масштаб текста", "Крупнее или мельче все надписи клиента (удобно на 4K и для чтения).")
            .range(0.8f, 1.4f).increment(0.05f).setValue(1.0f));

    private String appliedFont = CustomFonts.NONE;
    private float appliedScale = 1.0f;

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public FontsModule() {
        super("Fonts", "Свой шрифт интерфейса (TTF/OTF) с проверкой на сглаженность и общий масштаб текста.");
    }

    @Override
    protected void onEnable() {
        CustomFonts.refresh();
        this.appliedFont = this.font.getSelected();
        CustomFonts.setOverride(this.appliedFont);
        this.appliedScale = this.textScale.getValue();
        Render2D.setTextScale(this.appliedScale);
    }

    @Override
    protected void onDisable() {
        CustomFonts.setOverride(null);
        Render2D.setTextScale(1.0f);
    }

    public void reload() {
        int loaded = CustomFonts.refresh();
        String previous = this.font.getSelected();
        String[] names = CustomFonts.names();
        this.font.value(names).selected(FontsModule.contains(names, previous) ? previous : CustomFonts.NONE);
        ChatMessage.brandmessage("Шрифтов подключено: " + loaded + (loaded == 0 ? ". Положите .ttf в .minecraft/byazen/fonts" : ""));
        String[] rejected = CustomFonts.rejected();
        for (int i = 0; i < rejected.length && i < 5; ++i) {
            ChatMessage.brandmessage("Отклонён «" + rejected[i] + "»: пиксельный шрифт или не читается");
        }
    }

    private static boolean contains(String[] names, String value) {
        for (String name : names) {
            if (name.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        if (!CustomFonts.scanned()) {
            CustomFonts.refresh();
            String previous = this.font.getSelected();
            String[] names = CustomFonts.names();
            this.font.value(names).selected(FontsModule.contains(names, previous) ? previous : CustomFonts.NONE);
        }
        String selected = this.font.getSelected();
        if (!selected.equals(this.appliedFont)) {
            this.appliedFont = selected;
            CustomFonts.setOverride(selected);
            ChatMessage.brandmessage("Шрифт интерфейса: " + selected);
        }
        float scale = this.textScale.getValue();
        if (Math.abs(scale - this.appliedScale) > 0.001f) {
            this.appliedScale = scale;
            Render2D.setTextScale(scale);
        }
    }

    /** Идентификатор текущего шрифта для команд и отчётов. */
    public String familyId() {
        return CustomFonts.familyId(this.appliedFont);
    }

}
