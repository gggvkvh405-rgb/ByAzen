/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.render.model;

import ru.byazen.misc.ArrowMeshDefinition;
import ru.byazen.misc.CubeMeshDefinition;
import ru.byazen.misc.CylinderMeshDefinition;
import ru.byazen.misc.MarkerMeshDefinition;
import ru.byazen.misc.MeshDefinition;
import ru.byazen.misc.PlayerMeshDefinition;
import ru.byazen.misc.SkullMeshDefinition;
import ru.byazen.misc.SwordMeshDefinition;
import ru.byazen.misc.TotemMeshDefinition;
import ru.byazen.misc.TridentMeshDefinition;

public enum BuiltInMesh {
    CUBE(new CubeMeshDefinition()),
    ARROW(new ArrowMeshDefinition()),
    PLAYER(new PlayerMeshDefinition()),
    TOTEM(new TotemMeshDefinition()),
    TRIDENT(new TridentMeshDefinition()),
    SKULL(new SkullMeshDefinition()),
    SWORD(new SwordMeshDefinition()),
    CYLINDER(new CylinderMeshDefinition()),
    MARKER(new MarkerMeshDefinition());

    private final MeshDefinition definition;

    private BuiltInMesh(MeshDefinition definition) {
        this.definition = definition;
    }

    public MeshDefinition definition() {
        return this.definition;
    }
}

