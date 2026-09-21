/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1796
 *  net.minecraft.class_2960
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package ru.wexside.mixin;

import java.util.Map;
import net.minecraft.class_1796;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={class_1796.class})
public interface ItemCooldownManagerAccessorMixin {
    @Accessor(value="field_8024")
    public Map<class_2960, Object> wexside$getEntries();

    @Accessor(value="field_8025")
    public int wexside$getTick();
}

