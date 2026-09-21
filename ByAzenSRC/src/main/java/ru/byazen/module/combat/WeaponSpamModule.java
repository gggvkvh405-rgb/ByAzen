/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1753
 *  net.minecraft.class_1764
 *  net.minecraft.class_1799
 *  net.minecraft.class_1835
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1835;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public final class WeaponSpamModule
extends Module
implements ConfigSerializable {
    private static final String BOW = "Bow";
    private static final String CROSSBOW = "Crossbow";
    private static final String TRIDENT = "Trident";
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0424\u043b\u0443\u0434\u0438\u0442 \u0432\u044b\u0441\u0442\u0440\u0435\u043b\u0430\u043c\u0438 \u0438\u0437 \u043e\u0440\u0443\u0436\u0438\u044f").withKeybind().toggle()).build();
    private final MultiSelectSetting weapons;
    private final NumberSetting delay;

    public WeaponSpamModule(EventBus eventBus) {
        super(eventBus, "weapon_spam", "Weapon Spam", "\u0424\u043b\u0443\u0434\u0438\u0442 \u0432\u044b\u0441\u0442\u0440\u0435\u043b\u0430\u043c\u0438 \u0438\u0437 \u043e\u0440\u0443\u0436\u0438\u044f", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting weaponSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(BOW, CROSSBOW, TRIDENT).selectAll(false).optionListEnabled(false).name("Weapons").id("weapons").description("\u0422\u0438\u043f\u044b \u043e\u0440\u0443\u0436\u0438\u044f")).build();
        weaponSetting.setOptions(new String[0]);
        this.weapons = weaponSetting;
        this.registerSetting(weaponSetting);
        this.delay = ((NumberSettingBuilder)NumberSetting.builder().range(3.0, 20.0).defaultValue(5.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Delay").id("delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043d\u0430 \u043b\u0443\u043a (\u0442\u0438\u043a\u0438)").visibleWhen(() -> this.weapons.getSelectedOptions().contains(BOW))).build();
        this.registerSetting(this.delay);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || client.field_1761 == null) {
            return;
        }
        if (!player.method_6115()) {
            return;
        }
        class_1799 stack = player.method_6030();
        List<String> selected = this.weapons.getSelectedOptions();
        int useTime = player.method_6048();
        if (selected.contains(BOW) && stack.method_7909() instanceof class_1753 && useTime >= this.delay.getIntValue()) {
            client.field_1761.method_2897((class_1657)player);
            return;
        }
        if (selected.contains(CROSSBOW) && stack.method_7909() instanceof class_1764) {
            if (useTime >= class_1764.method_7775((class_1799)stack, (class_1309)player) && !class_1764.method_7781((class_1799)stack)) {
                client.field_1761.method_2897((class_1657)player);
            }
            return;
        }
        if (selected.contains(TRIDENT) && stack.method_7909() instanceof class_1835 && useTime >= 10) {
            client.field_1761.method_2897((class_1657)player);
        }
    }
}

