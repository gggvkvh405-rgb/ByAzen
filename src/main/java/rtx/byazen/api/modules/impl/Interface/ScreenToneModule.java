package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.theme.ScreenTone;

/**
 * Тёмная и светлая тема окон клиента (идея №194 из IDEAS.md).
 */
public final class ScreenToneModule
extends Module {

    public final ModeSetting mode = this.register(new ModeSetting("Тема окон",
            "Тёмная — привычная палитра, светлая — молочные панели с тёмным текстом, авто — светлая днём.",
            "Тёмная", "Тёмная", "Светлая", "Авто"));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Окна"));
    private final ButtonSetting preview = this.register(new ButtonSetting("Посмотреть",
            "Открыть хаб — тема видна на окнах клиента.").label("Открыть хаб")
            .onClick(() -> ByAzenHubScreen.open(0)));

    public ScreenToneModule() {
        super("Screen Tone", "Тёмная и светлая тема окон: хаб, плеер, подсказки, окна клиента.", Category.DISPLAY);
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§b" + ScreenTone.summary());
    }
}

