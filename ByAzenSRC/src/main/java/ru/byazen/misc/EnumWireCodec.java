/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Optional;

public interface EnumWireCodec<E> {
    public E process(byte var1);

    public Optional<E> process2(byte var1);
}

