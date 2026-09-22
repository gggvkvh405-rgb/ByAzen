package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.compat.ModEnv;
import rtx.byazen.utils.logs.ClientLog;

/**
 * Детект Iris / OptiFine / Sodium и соседей (идея №166 из IDEAS.md).
 * <p>
 * Клиент смотрит, что установлено рядом, и предупреждает там, где это важно: с шейдерами тяжёлые
 * визуалы дороже, с OptiFine может двоиться свечение, с оптимизаторами меняется рендер мира. Если
 * включить «Беречь визуалы», клиент при найденных шейдерах сам приглушит тяжёлые эффекты.
 */
public final class ModDetectModule
extends Module {

    private static final String[] HEAVY_VISUALS = {"Footprints", "Cosmetics 3D", "Custom Particles", "Trail", "World Particles"};

    private final SeparatorSetting group = this.register(new SeparatorSetting("Окружение"));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат при входе", "Один раз сообщить, что стоит рядом.").setValue(true));
    private final BooleanSetting protectHeavy = this.register(new BooleanSetting("Беречь визуалы", "При найденных шейдерах выключить самые дорогие эффекты.").setValue(false));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть список", "Кто найден и что с этим делать — в окне «Диагностика».").label("Открыть").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_MODS)));
    private final ButtonSetting report = this.register(new ButtonSetting("Список в чат", "Вывести найденное окружение в чат.").label("В чат").onClick(ModDetectModule::report));

    private boolean reported;
    private boolean handled;

    public ModDetectModule() {
        super("Mod Detect", "Находит Iris, OptiFine, Sodium и другие моды рядом и предупреждает о конфликтах.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @EventHandler
    public void onHud(HudRenderEvent event) {
        if (this.reported && this.handled) {
            return;
        }
        if (!this.reported && this.tellInChat.getValue()) {
            this.reported = true;
            ChatMessage.send("Окружение: " + ModEnv.summary());
            for (String note : ModEnv.notes()) {
                ChatMessage.send("• " + note);
            }
            ClientLog.info("окружение: " + ModEnv.summary());
        }
        this.reported = true;
        if (!this.handled) {
            this.handled = true;
            if (this.protectHeavy.getValue() && ModEnv.shadersPossible() && ModEnv.shaderPackCount() > 0) {
                this.calmHeavyVisuals();
            }
        }
    }

    /** Приглушает дорогие эффекты вручную (кнопка в окне «Диагностика»). */
    public static String calmHeavy() {
        ModDetectModule module = rtx.byazen.api.modules.ModuleManager.get().get(ModDetectModule.class);
        if (module == null) {
            return "модуль не найден";
        }
        int disabled = module.calmHeavyVisuals();
        return "Приглушено эффектов: " + disabled;
    }

    private int calmHeavyVisuals() {
        int disabled = 0;
        for (String name : HEAVY_VISUALS) {
            Module module = ModuleManager.get().findByName(name);
            if (module != null && module.isEnabled()) {
                module.disable();
                ++disabled;
                ClientLog.warn("с шейдерами выключен дорогой модуль: " + module.getDisplayName());
            }
        }
        if (disabled > 0) {
            ChatMessage.send("Приглушено дорогих эффектов: " + disabled);
        }
        return disabled;
    }

    private static void report() {
        ChatMessage.send("Окружение: " + ModEnv.summary());
        List<ModEnv.Row> rows = ModEnv.presentRows();
        if (rows.isEmpty()) {
            ChatMessage.send("• рядом только ванильный Minecraft");
            return;
        }
        for (ModEnv.Row row : rows) {
            ChatMessage.send("• " + row.label + (row.version.isEmpty() ? "" : " " + row.version) + " — " + row.note);
        }
    }
}
