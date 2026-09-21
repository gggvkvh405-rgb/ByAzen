/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.util;

import java.util.Objects;
import ru.wexside.misc.ModelSurfaceMode;

public final class ModelRenderOptions {
    private final int process;
    private static final float value = 2.0f;
    private final boolean getIntType;
    private final boolean enabled;
    private final float value2;
    private static final ModelRenderOptions modelRenderOptions2 = new ModelRenderOptions(false, false, false, -1, 2.0f, true, ModelSurfaceMode.SOLID);
    private final ModelSurfaceMode modelSurfaceMode;
    private static final ModelRenderOptions modelRenderOptions3 = new ModelRenderOptions(true, true, false, -1, 2.0f, true, ModelSurfaceMode.SOLID);
    private final boolean enabled2;
    private final boolean enabled3;

    public ModelRenderOptions(boolean bl, boolean bl2, boolean bl3, int n, float f, boolean bl4, ModelSurfaceMode modelSurfaceMode) {
        this.enabled3 = bl;
        this.getIntType = bl2;
        this.enabled2 = bl3;
        this.process = n;
        this.value2 = f;
        this.enabled = bl4;
        this.modelSurfaceMode = modelSurfaceMode;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ModelRenderOptions)) {
            return false;
        }
        ModelRenderOptions modelRenderOptions2 = (ModelRenderOptions)object;
        return this.enabled3 == modelRenderOptions2.enabled3 && this.getIntType == modelRenderOptions2.getIntType && this.enabled2 == modelRenderOptions2.enabled2 && this.process == modelRenderOptions2.process && Float.compare(this.value2, modelRenderOptions2.value2) == 0 && this.enabled == modelRenderOptions2.enabled && Objects.equals((Object)this.modelSurfaceMode, (Object)modelRenderOptions2.modelSurfaceMode);
    }

    public String toString() {
        String string = String.valueOf((Object)this.modelSurfaceMode);
        boolean bl = this.enabled;
        float f = this.value2;
        int n = this.process;
        boolean bl2 = this.enabled2;
        boolean bl3 = this.getIntType;
        boolean bl4 = this.enabled3;
        return "ModelRenderOptions[outlineEnabled=" + bl4 + ", outlineThroughFill=" + bl3 + ", outlineUsesModelColor=" + bl2 + ", outlineColor=" + n + ", outlineLineWidth=" + f + ", depthTestEnabled=" + bl + ", surfaceEffect=" + string + "]";
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.enabled3, this.getIntType, this.enabled2, this.process, Float.valueOf(this.value2), this.enabled, this.modelSurfaceMode});
    }

    public float getFloatType() {
        return Math.max(1.0f, this.value2);
    }

    public boolean isActive() {
        return this.getIntType;
    }

    public boolean isActive2() {
        return this.enabled;
    }

    public boolean isActive3() {
        return this.enabled3;
    }

    public ModelRenderOptions process2(boolean bl) {
        return new ModelRenderOptions(bl, this.getIntType, this.enabled2, this.process, this.value2, this.enabled, this.modelSurfaceMode);
    }

    public boolean isActive4() {
        return this.modelSurfaceMode != ModelSurfaceMode.SOLID;
    }

    public boolean isActive5() {
        return this.enabled2;
    }

    public ModelRenderOptions process3(ModelSurfaceMode modelSurfaceMode) {
        return new ModelRenderOptions(this.enabled3, this.getIntType, this.enabled2, this.process, this.value2, this.enabled, modelSurfaceMode != null ? modelSurfaceMode : ModelSurfaceMode.SOLID);
    }

    public ModelRenderOptions getModelRenderOptions() {
        return new ModelRenderOptions(this.enabled3, this.getIntType, true, this.process, this.value2, this.enabled, this.modelSurfaceMode);
    }

    public float getFloatType2() {
        return this.value2;
    }

    public int getIntType2() {
        return this.process;
    }

    public static ModelRenderOptions process4(boolean bl) {
        return bl ? ModelRenderOptions.getAlternateRenderOptions() : ModelRenderOptions.getDefaultRenderOptions();
    }

    public ModelRenderOptions process5(boolean bl) {
        return new ModelRenderOptions(this.enabled3, bl, this.enabled2, this.process, this.value2, this.enabled, this.modelSurfaceMode);
    }

    public static ModelRenderOptions process6(int n) {
        return new ModelRenderOptions(true, true, false, n, 2.0f, true, ModelSurfaceMode.SOLID);
    }

    public static ModelRenderOptions getDefaultRenderOptions() {
        return modelRenderOptions2;
    }

    public ModelRenderOptions process7(float f) {
        return new ModelRenderOptions(this.enabled3, this.getIntType, this.enabled2, this.process, f, this.enabled, this.modelSurfaceMode);
    }

    public int process8(int n) {
        return this.enabled2 ? n : this.process;
    }

    public ModelRenderOptions process9(int n) {
        return new ModelRenderOptions(this.enabled3, this.getIntType, false, n, this.value2, this.enabled, this.modelSurfaceMode);
    }

    public static ModelRenderOptions getAlternateRenderOptions() {
        return modelRenderOptions3;
    }

    public ModelRenderOptions process10(boolean bl) {
        return new ModelRenderOptions(this.enabled3, this.getIntType, this.enabled2, this.process, this.value2, bl, this.modelSurfaceMode);
    }

    public ModelSurfaceMode getModelSurfaceMode() {
        return this.modelSurfaceMode;
    }
}

