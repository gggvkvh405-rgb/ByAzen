/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_1680
 *  net.minecraft.class_1792
 */
package ru.byazen.misc;

import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_1680;
import net.minecraft.class_1792;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.setting.ColorSetting;

public interface ConsumableEspEffect {
    default public void process(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
        this.process5(floatTypeEvent2, player, colorSetting);
    }

    default public void process2(WorldRenderEvent floatTypeEvent2, class_1680 snowballEntity, ColorSetting colorSetting) {
        this.process4(floatTypeEvent2, snowballEntity);
    }

    public void update();

    default public void setSet(Set<UUID> set) {
    }

    default public void setUUID(UUID uUID) {
    }

    public boolean process3(class_1792 var1);

    default public void process4(WorldRenderEvent floatTypeEvent2, class_1680 snowballEntity) {
    }

    public void process5(WorldRenderEvent var1, class_1657 var2, ColorSetting var3);
}

