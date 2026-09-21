/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public interface NativeMemoryAllocator {
    public void free(long var1);

    public long process(long var1, long var3, long var5);

    public long allocate(long var1);
}

