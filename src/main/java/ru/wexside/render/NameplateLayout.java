/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_5250
 */
package ru.wexside.render;

import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_5250;
import ru.wexside.item.ItemBadge;
import ru.wexside.render.BakedItemIcon;

public final class NameplateLayout {
    public final float centerX;
    public final float baselineY;
    public final class_5250 label;
    public final float fontSize;
    public final float labelWidth;
    public boolean friend;
    public boolean showEnchantments;
    public List<class_1799> equipment;
    public List<BakedItemIcon> equipmentIcons;
    public ItemBadge badge;

    public NameplateLayout(float centerX, float baselineY, class_5250 label, float fontSize, float labelWidth) {
        this.centerX = centerX;
        this.baselineY = baselineY;
        this.label = label;
        this.fontSize = fontSize;
        this.labelWidth = labelWidth;
    }
}

