package ru.wexside.module.hud;

import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.ui.HudEditorScreen;
import ru.wexside.ui.WexsideScreen;

public class HudEditorModule
extends Module
implements ConfigSerializable {
    private static volatile HudEditorModule instance;
    private final BooleanSetting enabledSetting;

    public HudEditorModule(EventBus eventBus) {
        super(eventBus, "hud_editor", "HUD Editor", "Редактор позиций HUD: ЛКМ — таскать элементы, ПКМ — выбрать, Esc — выход", ModuleCategory.valueOf("DISPLAY"), "hudedit");
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("Открыть редактор HUD").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.tick());
    }

    private void tick() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        this.enabledSetting.setEnabled(false);
        class_310 client = class_310.method_1551();
        if (client.field_1755 == null || client.field_1755 instanceof WexsideScreen) {
            client.method_1507((class_437)new HudEditorScreen());
        }
    }

    public static boolean isEditorOpen() {
        class_310 client = class_310.method_1551();
        return client != null && client.field_1755 instanceof HudEditorScreen;
    }
}
