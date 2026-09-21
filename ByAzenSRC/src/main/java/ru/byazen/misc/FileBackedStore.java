/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.io.File;
import ru.byazen.misc.ConfigStore;

public abstract class FileBackedStore
implements ConfigStore {
    public final File file;

    public FileBackedStore(File file) {
        this.file = file;
    }
}

