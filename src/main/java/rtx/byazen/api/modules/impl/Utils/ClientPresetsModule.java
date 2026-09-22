package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ClientPresets;

/**
 * Готовые пресеты клиента (идея №200 из IDEAS.md): «Анархия», «PvP», «Выживание», «Стример», «Минимум».
 */
public final class ClientPresetsModule
extends Module {

    public final ModeSetting preset = this.register(new ModeSetting("Пресет",
            "Набор под задачу: включает нужное, выключает лишнее, ставит графику.",
            "Выживание", "Анархия", "PvP", "Выживание", "Стример", "Минимум"));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Применить"));
    private final ButtonSetting apply = this.register(new ButtonSetting("Применить пресет",
            "Включить и выключить модули по выбранному набору.").label("Применить")
            .onClick(ClientPresetsModule::applyNow));
    private final ButtonSetting plan = this.register(new ButtonSetting("Показать план",
            "Что именно изменит выбранный пресет — до применения.").label("План")
            .onClick(ClientPresetsModule::printPlan));
    private final ButtonSetting list = this.register(new ButtonSetting("Все пресеты",
            "Список наборов и какая графика к ним идёт.").label("Список")
            .onClick(ClientPresetsModule::printAll));

    private final ButtonSetting shareCode = this.register(new ButtonSetting("Код пресета",
            "Скопировать короткий код — друг вставит его у себя и получит такой же набор.").label("Код")
            .onClick(ClientPresetsModule::copyCode));
    private final ButtonSetting acceptCode = this.register(new ButtonSetting("Принять код",
            "Взять код пресета из буфера обмена и применить его.").label("Принять")
            .onClick(ClientPresetsModule::acceptCode));

    public ClientPresetsModule() {
        super("Client Presets", "Пресеты клиента в один клик: Анархия, PvP, Выживание, Стример, Минимум.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§b" + ClientPresets.summary());
    }

    private static ClientPresetsModule self() {
        if (rtx.byazen.api.modules.ModuleManager.get() == null) {
            return null;
        }
        return rtx.byazen.api.modules.ModuleManager.get().get(ClientPresetsModule.class);
    }

    private static void applyNow() {
        ClientPresetsModule module = ClientPresetsModule.self();
        if (module == null) {
            return;
        }
        ChatMessage.send("§b" + ClientPresets.apply(module.preset.getValue()));
    }

    private static void printPlan() {
        ClientPresetsModule module = ClientPresetsModule.self();
        if (module == null) {
            return;
        }
        for (String line : ClientPresets.plan(module.preset.getValue())) {
            ChatMessage.send(line);
        }
    }

    private static void copyCode() {
        ClientPresetsModule module = ClientPresetsModule.self();
        if (module == null) {
            return;
        }
        String code = ClientPresets.shareCode(module.preset.getValue());
        if (code.isEmpty()) {
            ChatMessage.send("§cПресет не найден");
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(code);
        }
        ChatMessage.send("§bКод пресета скопирован — отправьте его другу");
    }

    private static void acceptCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        String code = client == null ? "" : client.keyboard.getClipboard();
        ChatMessage.send("§b" + ClientPresets.applyCode(code));
    }

    private static void printAll() {
        for (String line : ClientPresets.rows()) {
            ChatMessage.send(line);
        }
    }
}

