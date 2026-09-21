/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package ru.byazen.misc;

import com.google.gson.Gson;
import java.io.File;
import ru.byazen.misc.ConfigStore;
import ru.byazen.misc.FileBackedStore;

public abstract class JsonConfigStore
extends FileBackedStore
implements ConfigStore {
    public final Gson gson2;

    public JsonConfigStore(File file, Gson gson3) {
        super(file);
        this.gson2 = gson3;
    }
}

