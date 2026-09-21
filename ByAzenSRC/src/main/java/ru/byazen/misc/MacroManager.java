/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import ru.byazen.event.EventBus;
import ru.byazen.event.KeyPressedEvent;
import ru.byazen.misc.MacroConfigStore;
import ru.byazen.misc.MacroDefinition;
import ru.byazen.misc.MacroType;

public class MacroManager {
    private final MacroConfigStore store;

    public MacroManager(MacroConfigStore store, EventBus eventBus) {
        this.store = store;
        eventBus.subscribe(KeyPressedEvent.class, this::onKeyPressed);
    }

    public boolean process(String string) {
        if (string == null) {
            return false;
        }
        for (MacroDefinition macroDefinition : this.store.getMacros()) {
            if (!macroDefinition.getName().equalsIgnoreCase(string)) continue;
            return true;
        }
        return false;
    }

    public List<MacroDefinition> getList() {
        return Collections.unmodifiableList(this.store.getMacros());
    }

    public boolean process2(String string) {
        if (string == null) {
            return false;
        }
        boolean bl = this.store.getMacros().removeIf(macroDefinition -> macroDefinition.getName().equalsIgnoreCase(string));
        if (bl) {
            this.update2();
        }
        return bl;
    }

    public void update() {
        if (this.store.getMacros().isEmpty()) {
            return;
        }
        this.store.getMacros().clear();
        this.update2();
    }

    public void process3(String string, String string2, int n, MacroType macroType) {
        this.store.getMacros().add(new MacroDefinition(string, string2, n, macroType));
        this.update2();
    }

    private void executeMacro(class_634 networkHandler, MacroDefinition macro) {
        String string = macro.getMessage();
        if (string == null) {
            return;
        }
        String string2 = string.trim();
        if (string2.isEmpty()) {
            return;
        }
        if (macro.getType() == MacroType.COMMAND) {
            if (string2.startsWith("/")) {
                string2 = string2.substring(1).trim();
            }
            if (string2.isEmpty()) {
                return;
            }
            networkHandler.method_45730(string2);
        } else {
            networkHandler.method_45729(string2);
        }
    }

    private void onKeyPressed(KeyPressedEvent event) {
        class_310 mc = class_310.method_1551();
        class_746 player2 = mc.field_1724;
        if (player2 == null || mc.field_1755 != null) {
            return;
        }
        class_634 networkHandler = player2.field_3944;
        if (networkHandler == null) {
            return;
        }
        int n = event.key();
        for (MacroDefinition macroDefinition : new ArrayList<MacroDefinition>(this.store.getMacros())) {
            if (macroDefinition.getKeyCode() != n) continue;
            this.executeMacro(networkHandler, macroDefinition);
        }
    }

    public void setConsumer(Consumer<MacroDefinition> consumer) {
        for (MacroDefinition macroDefinition : this.store.getMacros()) {
            consumer.accept(macroDefinition);
        }
    }

    private void update2() {
        try {
            this.store.save();
        }
        catch (IOException exception) {
            throw new IllegalStateException("Failed to save macros", exception);
        }
    }
}

