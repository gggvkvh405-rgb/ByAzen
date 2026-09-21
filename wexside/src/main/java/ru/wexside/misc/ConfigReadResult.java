/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package ru.wexside.misc;

import com.google.gson.JsonObject;

public record ConfigReadResult(JsonObject json, boolean needsMigration) {
}

