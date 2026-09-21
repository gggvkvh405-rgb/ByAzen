/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package ru.byazen.misc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import ru.byazen.misc.ConfigReadResult;
import ru.byazen.misc.ConfigStore;
import ru.byazen.misc.EncryptedConfigIO;
import ru.byazen.misc.JsonConfigStore;

public final class BlockEspConfigStore
extends JsonConfigStore
implements ConfigStore {
    private final Map<String, Integer> field20 = new LinkedHashMap<String, Integer>();
    static final int slot = -1;

    public BlockEspConfigStore(File file, Gson gson2) {
        super(file, gson2);
    }

    @Override
    public void load() {
        ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
        JsonObject json2 = config.json();
        if (json2.has("magic") && "byazen".equals(json2.get("magic").getAsString())) {
            this.field20.clear();
            if (json2.has("blocks") && json2.get("blocks").isJsonObject()) {
                for (Map.Entry entry : json2.get("blocks").getAsJsonObject().entrySet()) {
                    if (((JsonElement)entry.getValue()).isJsonNull()) continue;
                    this.field20.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsInt());
                }
            }
            if (config.needsMigration()) {
                try {
                    this.save();
                }
                catch (IOException exception) {
                    throw new IllegalStateException("Failed to migrate keybind configuration", exception);
                }
            }
        }
    }

    public Map<String, Integer> getMap() {
        return this.field20;
    }

    @Override
    public void save() throws IOException {
        JsonObject json2 = new JsonObject();
        json2.addProperty("magic", "byazen");
        json2.addProperty("version", (Number)2);
        JsonObject json3 = new JsonObject();
        for (Map.Entry<String, Integer> entry : this.field20.entrySet()) {
            json3.addProperty(entry.getKey(), (Number)entry.getValue());
        }
        json2.add("blocks", (JsonElement)json3);
        EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
    }
}

