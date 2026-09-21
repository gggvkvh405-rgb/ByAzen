/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public record TargetChange<T>(T previous, T current, boolean changed, boolean disappearing) {
}

