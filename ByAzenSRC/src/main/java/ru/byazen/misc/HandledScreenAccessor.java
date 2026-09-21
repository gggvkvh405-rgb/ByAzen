/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1735
 *  net.minecraft.class_332
 */
package ru.byazen.misc;

import net.minecraft.class_1735;
import net.minecraft.class_332;

public interface HandledScreenAccessor {
    public int getContainerX();

    public int getContainerY();

    public int getContainerWidth();

    public int getContainerHeight();

    public class_1735 getFocusedSlot();

    public void drawContainerBackground(class_332 var1, float var2, int var3, int var4);
}

