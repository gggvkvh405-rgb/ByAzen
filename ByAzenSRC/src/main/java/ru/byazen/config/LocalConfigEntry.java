/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.config;

import java.io.File;
import ru.byazen.misc.TextureResource;

public record LocalConfigEntry(String name, String author, String updatedAt, String server, File file, TextureResource avatar) {
}

