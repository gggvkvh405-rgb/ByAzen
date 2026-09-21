/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.ConfigStore;

public interface ConfigStoreRegistry {
    public <T extends ConfigStore> T getStore(Class<T> var1);

    public void registerStore(ConfigStore var1);
}

