package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import rtx.kimiko.api.config.ConfigManager;
import rtx.kimiko.api.events.impl.module.ModuleToggleEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.sounds.SoundManager;

public final class ClientSounds
extends Module {
    private static ClientSounds instance;
    private final SeparatorSetting soundSeparator = this.register(new SeparatorSetting("\u0417\u0432\u0443\u043a"));
    private final ModeSetting soundType = this.register(new ModeSetting("\u0422\u0438\u043f \u0437\u0432\u0443\u043a\u0430", "\u0422\u0438\u043f \u0437\u0432\u0443\u043a\u0430 \u043a\u043b\u0438\u0435\u043d\u0442\u0430.", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439", "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439"));
    private final NumberSetting volume = this.register(new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0432\u0443\u043a\u043e\u0432 \u0432\u043a\u043b/\u0432\u044b\u043a\u043b \u043c\u043e\u0434\u0443\u043b\u0435\u0439.", 1.0, 0.0, 1.0, 0.05));
    private final NumberSetting pitch = this.register(new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430 \u0442\u043e\u043d\u0430", "\u0412\u044b\u0441\u043e\u0442\u0430 \u0442\u043e\u043d\u0430 \u0437\u0432\u0443\u043a\u0430 \u0432\u043a\u043b/\u0432\u044b\u043a\u043b \u043c\u043e\u0434\u0443\u043b\u0435\u0439.", 1.0, 0.5, 2.0, 0.05));
    private final BooleanSetting moduleToggleSound = this.register(new BooleanSetting("\u0412\u043a\u043b/\u0432\u044b\u043a\u043b \u043c\u043e\u0434\u0443\u043b\u0435\u0439", "\u0417\u0432\u0443\u043a \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u0438 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0443\u043b\u0435\u0439.", true));
    private final SeparatorSetting interfaceSeparator = this.register(new SeparatorSetting("\u0417\u0432\u0443\u043a\u0438 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430"));
    private final NumberSetting interfaceVolume = this.register(new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0432\u0443\u043a\u043e\u0432 \u043c\u0435\u043d\u044e \u0438 UI.", 1.0, 0.0, 1.0, 0.05));
    private final BooleanSetting guiSound = this.register(new BooleanSetting("\u041e\u0442\u043a\u0440\u044b\u0442\u0438\u0435/\u0437\u0430\u043a\u0440\u044b\u0442\u0438\u0435 \u043c\u0435\u043d\u044e", "\u0417\u0432\u0443\u043a \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0435\u043d\u044e.", true));
    private final BooleanSetting categorySound = this.register(new BooleanSetting("\u0421\u043c\u0435\u043d\u0430 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438", "\u0417\u0432\u0443\u043a \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438.", true));
    private final BooleanSetting moduleSettingsSound = this.register(new BooleanSetting("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043c\u043e\u0434\u0443\u043b\u044f", "\u0417\u0432\u0443\u043a \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a \u043c\u043e\u0434\u0443\u043b\u044f.", true));
    private final BooleanSetting dropdownSound = this.register(new BooleanSetting("\u0412\u044b\u043f\u0430\u0434\u0430\u044e\u0449\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u0438", "\u0417\u0432\u0443\u043a \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f \u0432\u044b\u043f\u0430\u0434\u0430\u044e\u0449\u0438\u0445 \u0441\u043f\u0438\u0441\u043a\u043e\u0432.", true));
    private final BooleanSetting sliderSound = this.register(new BooleanSetting("\u041f\u043e\u043b\u0437\u0443\u043d\u043e\u043a", "\u0417\u0432\u0443\u043a \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f \u043f\u043e\u043b\u0437\u0443\u043d\u043a\u0430.", true));
    private final BooleanSetting searchTypingSound = this.register(new BooleanSetting("\u0412\u0432\u043e\u0434 \u0432 \u043f\u043e\u0438\u0441\u043a\u0435", "\u0417\u0432\u0443\u043a \u0432\u0432\u043e\u0434\u0430 \u0442\u0435\u043a\u0441\u0442\u0430 \u0432 \u043f\u043e\u0438\u0441\u043a\u0435.", true));
    private final SeparatorSetting chatSeparator = this.register(new SeparatorSetting("\u0417\u0432\u0443\u043a\u0438 \u0447\u0430\u0442\u0430"));
    private final NumberSetting chatVolume = this.register(new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0447\u0430\u0442\u0430", "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0432\u0443\u043a\u043e\u0432 \u0447\u0430\u0442\u0430 \u0438 \u043a\u043e\u043c\u0430\u043d\u0434.", 1.0, 0.0, 1.0, 0.05));
    private final BooleanSetting commandErrorSound = this.register(new BooleanSetting("\u041e\u0448\u0438\u0431\u043a\u0438 \u043a\u043e\u043c\u0430\u043d\u0434", "\u0417\u0432\u0443\u043a \u043e\u0448\u0438\u0431\u043a\u0438 \u0438\u043b\u0438 \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b.", true));

    public ClientSounds() {
        super("Client Sounds", "\u0417\u0432\u0443\u043a\u0438 \u043a\u043b\u0438\u0435\u043d\u0442\u0430 \u0434\u043b\u044f \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0439 \u043c\u043e\u0434\u0443\u043b\u0435\u0439 \u0438 \u043c\u0435\u043d\u044e.", Category.UTILS);
        instance = this;
    }

    public static ClientSounds getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @EventHandler
    private void onModuleToggle(ModuleToggleEvent moduleToggleEvent) {
        if (ConfigManager.isLoading()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null || moduleToggleEvent.getModule() == this) {
            return;
        }
        this.playToggleSound(moduleToggleEvent.isEnabled());
    }

    public float getVolumeFor(String string) {
        if (string == null) {
            return this.getVolume();
        }
        return switch (string) {
            case "gui_open", "gui_close", "select_category", "module_settings_open", "module_settings_close", "settings_open", "settings_close", "slider", "search_typing" -> this.getInterfaceVolume();
            case "command_error" -> this.getChatVolume();
            default -> this.getVolume();
        };
    }

    public float getInterfaceVolume() {
        return this.interfaceVolume.getFloat();
    }

    private void playToggleSound(boolean bl) {
        if (!this.isEnabled() || !this.moduleToggleSound.getValue()) {
            return;
        }
        float f = this.volume.getFloat();
        float f2 = this.pitch.getFloat();
        SoundManager.playSoundDirect(bl ? SoundManager.TOGGLE1_ON : SoundManager.TOGGLE1_OFF, f, f2);
    }

    public float getChatVolume() {
        return this.chatVolume.getFloat();
    }

    public static boolean isAllowed(String string) {
        ClientSounds clientSounds = instance;
        if (clientSounds == null || string == null) {
            return true;
        }
        if (!clientSounds.isEnabled()) {
            return false;
        }
        return switch (string) {
            case "gui_open", "gui_close" -> clientSounds.guiSound.getValue();
            case "select_category" -> clientSounds.categorySound.getValue();
            case "module_settings_open", "module_settings_close" -> clientSounds.moduleSettingsSound.getValue();
            case "settings_open", "settings_close" -> clientSounds.dropdownSound.getValue();
            case "slider" -> clientSounds.sliderSound.getValue();
            case "search_typing" -> clientSounds.searchTypingSound.getValue();
            case "command_error" -> clientSounds.commandErrorSound.getValue();
            default -> true;
        };
    }

    public float getPitch() {
        return this.pitch.getFloat();
    }

    public float getVolume() {
        return this.volume.getFloat();
    }
}

