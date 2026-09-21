package ru.wexside.ui;

import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public final class HudEditorScreen
extends class_437 {
    private static final int ESC_KEYCODE = 256;

    public HudEditorScreen() {
        super((class_2561)class_2561.method_43473());
    }

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean method_25404(class_11908 input) {
        if (input.comp_4795() == ESC_KEYCODE) {
            this.method_25419();
            return true;
        }
        return super.method_25404(input);
    }

    @Override
    public boolean method_25421() {
        return false;
    }
}
