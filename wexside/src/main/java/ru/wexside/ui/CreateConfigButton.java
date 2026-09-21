/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.ui;

import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class CreateConfigButton
extends GuiElement {
    private final BooleanSupplier enabled;
    private final Runnable action;

    public CreateConfigButton(GuiBounds bounds, BooleanSupplier enabled, Runnable action) {
        super(bounds);
        this.enabled = enabled;
        this.action = action;
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.getBounds().contains(mouseX, mouseY)) {
            return false;
        }
        if (this.enabled.getAsBoolean()) {
            this.action.run();
        }
        return true;
    }

    @Override
    public float render(float delta, Matrix4f matrix) {
        GuiBounds bounds = this.getBounds();
        GuiDrawApi renderer = WexSideClient.getGuiRenderer();
        int color = this.enabled.getAsBoolean() ? ThemeColors.accent() : ThemeColors.borderPrimary();
        renderer.drawRoundedOutline(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 6.0f, 0.75f, ThemeColors.withHoverOverlay(ThemeColors.notificationOutline()));
        String label = "\u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044e";
        float width = FontRegistry.font4.process3(label, 5.5f);
        float height = FontRegistry.font4.process4(label, 5.5f);
        FontRegistry.font4.process2(matrix, renderer, label, bounds.getX() + (bounds.getWidth() - width) / 2.0f, bounds.getY() + (bounds.getHeight() - height) / 2.0f, 5.5f, color);
        return bounds.getY() + bounds.getHeight();
    }
}

