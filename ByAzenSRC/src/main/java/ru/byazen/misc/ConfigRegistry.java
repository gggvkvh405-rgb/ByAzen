/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.misc.ConfigEntryCodec;
import ru.byazen.misc.ConfigFileEntry;
import ru.byazen.misc.ConfigPersistence;

public class ConfigRegistry
implements ConfigPersistence {
    private final ConfigEntryCodec codec;
    private List<ConfigFileEntry> baselineEntries = new ArrayList<ConfigFileEntry>();
    private final List<ConfigSerializable> registeredEntries = new ArrayList<ConfigSerializable>();

    public ConfigRegistry() {
        this.codec = new ConfigEntryCodec(this.registeredEntries);
    }

    @Override
    public void register(ConfigSerializable entry) {
        if (entry != null && !this.registeredEntries.contains(entry)) {
            this.registeredEntries.add(entry);
        }
    }

    public ConfigEntryCodec getCodec() {
        return this.codec;
    }

    public Set<String> getRegisteredIds() {
        HashSet<String> ids = new HashSet<String>(this.registeredEntries.size());
        for (ConfigSerializable entry : this.registeredEntries) {
            ids.add(entry.getConfigId());
        }
        return ids;
    }

    @Override
    public void applyEntries(List<ConfigFileEntry> entries) throws IOException {
        this.codec.applyEntries(entries);
    }

    @Override
    public List<ConfigFileEntry> serializeEntries() throws IOException {
        return this.codec.serializeEntries();
    }

    public void restoreBaseline() throws IOException {
        this.applyEntries(this.baselineEntries);
    }

    public void captureBaseline() throws IOException {
        this.baselineEntries = List.copyOf(this.serializeEntries());
    }

    public List<ConfigFileEntry> getBaselineEntries() {
        return this.baselineEntries;
    }
}

