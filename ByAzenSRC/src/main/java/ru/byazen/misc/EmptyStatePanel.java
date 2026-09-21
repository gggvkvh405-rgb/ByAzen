/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import java.util.List;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.GuiDrawApi;

public final class EmptyStatePanel
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private final float value2;
    private final String string2;
    private final float value3;
    private final float value4;
    private final List<String> messageLines;

    public EmptyStatePanel(List<String> list, float f, float f2) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f));
        this.value2 = 6.5f;
        this.value3 = 9.0f;
        this.string2 = "a";
        this.messageLines = list;
        this.value = f;
        this.value4 = f2;
    }

    public EmptyStatePanel() {
        this(List.of("\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", "\u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u0432\u0432\u0435\u0441\u0442\u0438 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430", "\u0432 \u043f\u043e\u0438\u0441\u043a\u0435 \u0438\u043d\u0430\u0447\u0435"));
    }

    public EmptyStatePanel(List<String> list) {
        this(list, 16.0f, 6.0f);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        float f2 = bounds2.getX() + bounds2.getWidth() / 2.0f;
        float f3 = bounds2.getY() + (bounds2.getHeight() - this.value - this.value4 - (float)this.messageLines.size() * 9.0f) / 2.0f;
        FontRegistry.font3.process5(matrix4f, drawApi, "a", f2 - FontRegistry.font3.process3("a", this.value) / 2.0f, f3, this.value, ThemeColors.textSecondary());
        f3 += this.value + this.value4;
        for (String string : this.messageLines) {
            float f4 = FontRegistry.font2.process3(string, 6.5f);
            float f5 = FontRegistry.font2.process4(string, 6.5f);
            FontRegistry.font2.process2(matrix4f, drawApi, string, f2 - f4 / 2.0f, f3 + (9.0f - f5) / 2.0f, 6.5f, ThemeColors.textSecondary());
            f3 += 9.0f;
        }
        return bounds2.getY() + bounds2.getHeight();
    }
}

