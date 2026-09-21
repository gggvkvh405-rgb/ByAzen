/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public record ScaleSettings(double minimum, double maximum, int precision, boolean markersEnabled, double markerStep, double snapStep) {
    public boolean hasSnapStep() {
        return this.snapStep > 0.0;
    }

    public boolean hasMarkers() {
        return this.markersEnabled || this.markerStep > 0.0;
    }

    public boolean hasMarkerSpacing() {
        return this.markerStep > 0.0;
    }
}

