/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2596
 *  net.minecraft.class_2720
 *  net.minecraft.class_2856
 *  net.minecraft.class_2856$class_2857
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 */
package ru.byazen.module.misc;

import java.util.UUID;
import net.minecraft.class_2596;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_310;
import net.minecraft.class_634;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class SRPSpooferModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();

    public SRPSpooferModule(EventBus eventBus) {
        super(eventBus, "srp_spoofer", "SRP Spoofer", "\u041f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0430\u0435\u0442 \u0440\u0435\u0441\u0443\u0440\u0441-\u043f\u0430\u043a\u0438 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0431\u0435\u0437 \u0438\u0445 \u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0438", ModuleCategory.valueOf("MISC"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (!(packet instanceof class_2720)) {
            return;
        }
        class_2720 resourcePackPacket = (class_2720)packet;
        class_634 networkHandler = class_310.method_1551().method_1562();
        if (networkHandler == null) {
            return;
        }
        UUID packId = resourcePackPacket.comp_2158();
        networkHandler.method_52787((class_2596)new class_2856(packId, class_2856.class_2857.field_13016));
        networkHandler.method_52787((class_2596)new class_2856(packId, class_2856.class_2857.field_47704));
        networkHandler.method_52787((class_2596)new class_2856(packId, class_2856.class_2857.field_13017));
        event.update();
    }
}

