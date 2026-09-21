/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.NavigationState;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.NavigationEntry;
import ru.byazen.util.ClientMenuContent;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.NavigationSection;

public final class NavigationContent
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private float value = 1.0f;
    private final NavigationState navigationState;
    private final ClientMenuContent clientMenuContent;

    public NavigationContent(GuiBounds bounds2, ClientMenuContent clientMenuContent, NavigationState navigationState) {
        super(bounds2);
        this.clientMenuContent = clientMenuContent;
        this.navigationState = navigationState;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        return false;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        float f2 = this.navigationState.isActive2() ? 0.0f : 1.0f;
        this.value = FrameInterpolator.lerpTowards(this.value, f2, 45.0f);
        if (this.value <= 0.01f) {
            return bounds2.getY() + bounds2.getHeight();
        }
        String string = this.navigationState.string4();
        if (string == null) {
            return bounds2.getY() + bounds2.getHeight();
        }
        NavigationEntry navigationEntry = this.clientMenuContent.process10(string);
        NavigationSection navigationSection = this.clientMenuContent.process4(string);
        if (navigationEntry == null || navigationSection == null) {
            return bounds2.getY() + bounds2.getHeight();
        }
        float f3 = 5.9f;
        float f4 = 6.0f;
        int n = (int)(255.0f * this.value);
        int n2 = ColorUtils.withAlpha(ThemeColors.textMuted(), (float)n);
        int n3 = ColorUtils.withAlpha(ThemeColors.textPrimary(), (float)n);
        String string2 = navigationSection.getString2();
        String string3 = string2 + " / ";
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        FontRegistry.font2.process2(matrix4f, drawApi, string3, bounds2.getX(), bounds2.getY(), f3, n2);
        float f5 = bounds2.getX() + FontRegistry.font2.process3(string3, f3);
        String icon = navigationEntry.getIcon();
        FontRegistry.font3.process5(matrix4f, drawApi, icon, f5 + 1.0f, bounds2.getY() + 0.5f, f4, n3);
        FontRegistry.font2.process2(matrix4f, drawApi, navigationEntry.getDisplayName(), f5 += FontRegistry.font3.process3(icon, f4) + 3.0f, bounds2.getY(), f3, n3);
        return bounds2.getY() + bounds2.getHeight();
    }
}

