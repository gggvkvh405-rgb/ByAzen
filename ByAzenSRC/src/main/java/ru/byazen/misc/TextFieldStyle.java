/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public interface TextFieldStyle {
    public float longType();

    public int getIntType();

    public int getIntType2();

    public float getFloatType();

    public float getFloatType2();

    default public float getFloatType3() {
        return 1.0f;
    }

    default public float getFloatType4() {
        return 4.0f;
    }
}

