/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Arrays;
import ru.byazen.misc.ClasspathResource;
import ru.byazen.misc.TextureResource;

public enum ArrowStyle {
    TRIANGLE("Triangle", "/assets/byazen/textures/arrows/triangle.png"),
    CLASSIC("Classic", "/assets/byazen/textures/arrows/old_triangle.png");

    private final String displayName;
    private final TextureResource texture;

    private ArrowStyle(String displayName, String resourcePath) {
        this.displayName = displayName;
        this.texture = new TextureResource(new ClasspathResource(resourcePath)).wrapT(33071).wrapS(33071);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public TextureResource getTexture() {
        return this.texture;
    }

    public static String[] displayNames() {
        return (String[])Arrays.stream(ArrowStyle.values()).map(ArrowStyle::getDisplayName).toArray(String[]::new);
    }

    public static ArrowStyle fromDisplayName(String displayName) {
        return Arrays.stream(ArrowStyle.values()).filter(style -> style.displayName.equalsIgnoreCase(displayName)).findFirst().orElse(CLASSIC);
    }
}

