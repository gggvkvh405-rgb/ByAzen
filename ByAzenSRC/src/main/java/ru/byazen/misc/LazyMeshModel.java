/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.byazen.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.byazen.misc.MeshModel;
import ru.byazen.render.model.BuiltInMesh;
import ru.byazen.render.model.MeshProgramCache;

public final class LazyMeshModel {
    private boolean failed;
    private MeshModel meshModel;
    private final BuiltInMesh mesh;
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyMeshModel.class);

    private LazyMeshModel(BuiltInMesh mesh) {
        this.mesh = mesh;
    }

    public boolean load() {
        if (this.meshModel != null) {
            return true;
        }
        if (this.failed) {
            return false;
        }
        try {
            this.meshModel = MeshProgramCache.get(this.mesh);
            return true;
        }
        catch (Throwable throwable) {
            this.failed = true;
            LOGGER.error("Failed to load model program {}", (Object)(this.mesh != null ? this.mesh.name() : "null"), (Object)throwable);
            return false;
        }
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public MeshModel getMeshModel() {
        this.load();
        return this.meshModel;
    }

    public static LazyMeshModel create(BuiltInMesh mesh) {
        LazyMeshModel model = new LazyMeshModel(mesh);
        model.load();
        return model;
    }

    public boolean isLoaded() {
        return this.load();
    }
}

