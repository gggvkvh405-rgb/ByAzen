/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.input.BindInput;
import ru.byazen.misc.KeybindBinding;
import ru.byazen.misc.PotionPresetDraft;
import ru.byazen.util.PotionPresetController;

public final class PotionPresetKeybind
extends KeybindBinding {
    private final PotionPresetController potionCombiner;
    private final PotionPresetDraft preset;

    public PotionPresetKeybind(PotionPresetController potionCombiner, PotionPresetDraft preset) {
        super(null, preset.getBindInput());
        this.potionCombiner = potionCombiner;
        this.preset = preset;
    }

    @Override
    public void onReleased() {
        this.potionCombiner.queuePreset(this.preset);
    }

    @Override
    public void setBindInput(BindInput keybind) {
        super.setBindInput(keybind);
        this.preset.setBindInput(this.getBindInput());
    }
}

