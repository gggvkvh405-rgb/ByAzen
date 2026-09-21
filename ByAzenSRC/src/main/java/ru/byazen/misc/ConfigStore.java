/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.io.IOException;

public interface ConfigStore {
    public void load();

    public void save() throws IOException;
}

