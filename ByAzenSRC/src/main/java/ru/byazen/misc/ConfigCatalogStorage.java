/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.io.IOException;
import java.util.List;
import ru.byazen.misc.ConfigFileEntry;

public interface ConfigCatalogStorage {
    public List<ConfigFileEntry> serializeEntries() throws IOException;

    public void applyEntries(List<ConfigFileEntry> var1) throws IOException;
}

