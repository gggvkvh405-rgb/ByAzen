/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1542
 *  net.minecraft.class_1665
 *  net.minecraft.class_1680
 *  net.minecraft.class_1681
 *  net.minecraft.class_1683
 *  net.minecraft.class_1684
 *  net.minecraft.class_1685
 *  net.minecraft.class_1686
 *  net.minecraft.class_1753
 *  net.minecraft.class_1764
 *  net.minecraft.class_1771
 *  net.minecraft.class_1776
 *  net.minecraft.class_1779
 *  net.minecraft.class_1792
 *  net.minecraft.class_1812
 *  net.minecraft.class_1823
 *  net.minecraft.class_1835
 */
package ru.byazen.util;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1665;
import net.minecraft.class_1680;
import net.minecraft.class_1681;
import net.minecraft.class_1683;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_1686;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1792;
import net.minecraft.class_1812;
import net.minecraft.class_1823;
import net.minecraft.class_1835;

public enum ProjectileType {
    TRIDENT,
    PEARL,
    ARROW,
    CROSSBOW,
    POTION,
    ITEM;


    public static ProjectileType fromItem(class_1792 item) {
        if (item instanceof class_1835) {
            return TRIDENT;
        }
        if (item instanceof class_1776 || item instanceof class_1823 || item instanceof class_1771) {
            return PEARL;
        }
        if (item instanceof class_1753) {
            return ARROW;
        }
        if (item instanceof class_1764) {
            return CROSSBOW;
        }
        if (item instanceof class_1812 || item instanceof class_1779) {
            return POTION;
        }
        return null;
    }

    public static ProjectileType fromEntity(class_1297 entity) {
        if (entity instanceof class_1685) {
            return TRIDENT;
        }
        if (entity instanceof class_1684 || entity instanceof class_1680 || entity instanceof class_1681) {
            return PEARL;
        }
        if (entity instanceof class_1686 || entity instanceof class_1683) {
            return POTION;
        }
        if (entity instanceof class_1665) {
            return ARROW;
        }
        if (entity instanceof class_1542) {
            return ITEM;
        }
        return null;
    }
}

