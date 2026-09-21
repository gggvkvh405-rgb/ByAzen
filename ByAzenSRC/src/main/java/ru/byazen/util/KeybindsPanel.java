/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LabeledSegmentedControl;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.ModuleKeybindEntryFactory;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.SettingsListLayout;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.ModuleManager;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.KeybindFilter;
import ru.byazen.util.ModuleKeybindGroup;
import ru.byazen.util.ScrollController;
import ru.byazen.util.Scrollbar;
import ru.byazen.util.TwoColumnLayout;

public final class KeybindsPanel
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final TwoColumnLayout twoColumnLayout;
    private final ScrollController scrollController = new ScrollController(18.0f, 30.0f);
    private final List<ModuleKeybindGroup> moduleGroups;
    private final KeybindFilter keybindFilter;
    private final LabeledSegmentedControl labeledSegmentedControl;
    private final Scrollbar scrollbar = new Scrollbar();

    public KeybindsPanel(GuiBounds bounds2, ModuleManager moduleManager) {
        super(bounds2);
        this.labeledSegmentedControl = new LabeledSegmentedControl("\u041b", "\u0412\u0441\u0435 \u043a\u0435\u0439\u0431\u0438\u043d\u0434\u044b", "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u043a\u0435\u0439\u0431\u0438\u043d\u0434\u044b");
        this.keybindFilter = new KeybindFilter();
        this.moduleGroups = ModuleKeybindEntryFactory.process2(moduleManager);
        this.twoColumnLayout = new TwoColumnLayout(2, 6.0f, 6.0f);
        this.addChild(this.labeledSegmentedControl);
        this.addChild(this.keybindFilter);
        for (ModuleKeybindGroup moduleKeybindGroup : this.moduleGroups) {
            this.addChild(moduleKeybindGroup);
        }
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        if (this.getBounds().contains(n, n2)) {
            this.scrollController.scrollByWheel(d, this.getFloatType());
        }
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (n3 != 0 || !this.getBounds().contains(n, n2)) {
            return false;
        }
        if (this.scrollbar.onMousePressed(n, n2, n3)) {
            return true;
        }
        if (this.labeledSegmentedControl.onMousePressed(n, n2, n3)) {
            return true;
        }
        if (this.keybindFilter.onMousePressed(n, n2, n3)) {
            return true;
        }
        for (ModuleKeybindGroup moduleKeybindGroup : this.getTabs()) {
            if (!moduleKeybindGroup.onMousePressed(n, n2, n3)) continue;
            return true;
        }
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.layoutTabs(bounds2);
        this.labeledSegmentedControl.render(f, matrix4f);
        this.keybindFilter.render(f, matrix4f);
        boolean bl = this.isActive();
        List<ModuleKeybindGroup> list = this.process5(bl);
        SettingsListLayout settingsListLayout = new SettingsListLayout(list, f, bl);
        float f2 = bounds2.getY() + 8.0f + this.labeledSegmentedControl.getBounds().getHeight() + 6.0f;
        float f3 = this.getFloatType();
        this.scrollController.update(f3, this.twoColumnLayout.process2(settingsListLayout) + 8.0f);
        drawApi.beginStencil(1);
        drawApi.drawRoundedRectangleRadii(matrix4f, bounds2.getX() + 1.0f, f2, bounds2.getWidth() - 1.5f, f3 - 0.5f, 10.5f, 0.0f, 0.0f, 0.0f, ColorUtils.rgba(0, 0, 0, 45));
        drawApi.applyStencilMask(1);
        float f4 = bounds2.getX() + 8.0f;
        float f5 = bounds2.getWidth() - 16.0f;
        float f6 = this.twoColumnLayout.process(settingsListLayout, matrix4f, f4, f2, f5, this.scrollController.getOffset(), bounds2.getY() - 1.0f, bounds2.getY() + bounds2.getHeight() + 1.0f);
        drawApi.endStencil();
        this.scrollController.setContentHeight(f3, f6 <= 0.0f ? 0.0f : f6 + 8.0f);
        this.scrollbar.process(drawApi, matrix4f, bounds2.getX() + bounds2.getWidth(), f2, f3, this.scrollController, this.getLastMouseX(), this.getLastMouseY());
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public void update2() {
        this.scrollController.scrollToTop();
    }

    private static boolean process4(ModuleCategory moduleCategory, List<String> list) {
        for (String string : list) {
            if (!moduleCategory.getName().equalsIgnoreCase(string)) continue;
            return true;
        }
        return false;
    }

    private List<ModuleKeybindGroup> getTabs() {
        return this.process5(this.isActive());
    }

    private List<ModuleKeybindGroup> process5(boolean bl) {
        List<String> list = this.keybindFilter.getList();
        boolean bl2 = list != null && !list.isEmpty();
        ArrayList<ModuleKeybindGroup> arrayList = new ArrayList<ModuleKeybindGroup>(this.moduleGroups.size());
        for (ModuleKeybindGroup moduleKeybindGroup : this.moduleGroups) {
            if (bl2 && !KeybindsPanel.process4(moduleKeybindGroup.getModule().getCategory(), list) || !moduleKeybindGroup.process6(bl)) continue;
            arrayList.add(moduleKeybindGroup);
        }
        return arrayList;
    }

    private float getFloatType() {
        return Math.max(0.0f, this.getBounds().getHeight() - 8.0f - this.labeledSegmentedControl.getBounds().getHeight() - 6.0f);
    }

    private void layoutTabs(GuiBounds bounds2) {
        this.labeledSegmentedControl.getBounds().setPosition(bounds2.getX() + 8.0f, bounds2.getY() + 8.0f);
        GuiBounds bounds3 = this.keybindFilter.getBounds();
        bounds3.setPosition(bounds2.getX() + bounds2.getWidth() - 8.0f - bounds3.getWidth(), bounds2.getY() + 8.0f);
    }

    private boolean isActive() {
        return this.labeledSegmentedControl.getIntType2() == 1;
    }
}

