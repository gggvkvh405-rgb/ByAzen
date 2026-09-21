/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public final class RaycastMode {
    public static final RaycastMode VISIBLE = new RaycastMode();
    public static final RaycastMode THROUGH_WALLS = new RaycastMode();
    public static final RaycastMode raycastMode = VISIBLE;
    public static final RaycastMode raycastMode2 = THROUGH_WALLS;

    private RaycastMode() {
    }
}

