/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.List;
import ru.byazen.util.InlineMesh;

public class MeshModel {
    private final List<InlineMesh> meshes;
    private final String string2;

    public MeshModel(String string, List<InlineMesh> list) {
        this.string2 = string;
        this.meshes = list;
    }

    public String getString() {
        return this.string2;
    }

    public List<InlineMesh> getList() {
        return this.meshes;
    }
}

