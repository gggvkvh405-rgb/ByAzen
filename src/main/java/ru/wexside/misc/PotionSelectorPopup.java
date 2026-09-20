/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.PotionEditorState;
import ru.wexside.misc.PotionSearchField;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.PotionOptionRow;
import ru.wexside.util.ClippedContentRenderer;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.PotionPresetController;
import ru.wexside.util.ScrollController;
import ru.wexside.util.Scrollbar;

public final class PotionSelectorPopup
extends PopupPanel
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private String string2;
    private final PotionEditorState potionEditorState;
    private final ClippedContentRenderer clippedContentRenderer = new ClippedContentRenderer(0.0f, 14.0f, 14.0f, false);
    private final float value2;
    private final Consumer<PotionCatalogEntry> consumer;
    public static final float value3 = 150.0f;
    private final float value4;
    private final PotionSearchField potionSearchField;
    private final String string3;
    private final String string4;
    private final float value5;
    private final ItemIconRenderer itemIconRenderer;
    private final float value6;
    private final float value7;
    private final String string5;
    private final float value8;
    private final float value9;
    private final String string6;
    static final float value10 = 1.0f;
    public static final float value11 = 115.5f;
    private final PotionPresetController potionPresetController2;
    private final float value12;
    static final float value13 = 106.0f;
    private final float value14;
    private final ScrollController scrollController = new ScrollController(18.0f, 30.0f);
    private final float value15;
    private final Scrollbar scrollbar = new Scrollbar();
    private final List<PotionOptionRow> optionRows = new ArrayList<PotionOptionRow>();
    private final float value16;

    public PotionSelectorPopup(PotionPresetController potionPresetController2, PotionEditorState potionEditorState, ItemIconRenderer itemIconRenderer, Consumer<PotionCatalogEntry> consumer) {
        super(new GuiBounds(0.0f, 0.0f, 115.5f, 150.0f));
        this.value2 = 5.0f;
        this.value15 = 3.0f;
        this.value5 = 44.0f;
        this.value14 = 5.5f;
        this.value = 6.0f;
        this.value8 = 1.5f;
        this.value12 = 12.5f;
        this.value6 = 7.0f;
        this.value4 = 26.0f;
        this.value16 = 2.0f;
        this.value9 = 3.5f;
        this.value7 = 6.0f;
        this.string5 = "\u0412\u0430\u0448\u0438 \u0437\u0435\u043b\u044c\u044f";
        this.string4 = "\u0422\u0435\u043a\u0443\u0449\u0438\u0435 \u0437\u0435\u043b\u044c\u044f";
        this.string3 = "u";
        this.string6 = "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e";
        this.potionPresetController2 = potionPresetController2;
        this.potionEditorState = potionEditorState;
        this.itemIconRenderer = itemIconRenderer;
        this.consumer = consumer;
        this.potionSearchField = new PotionSearchField(potionEditorState);
        this.addChild(this.potionSearchField);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        if (this.isActive2() && this.getBounds().contains(n, n2)) {
            this.scrollController.scrollByWheel(d, this.getFloatType3());
        }
    }

    @Override
    public void update() {
        this.potionSearchField.update();
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        int n4;
        if (!this.isActive2() || !this.getBounds().contains(n, n2)) {
            return false;
        }
        int n5 = (int)((float)n - this.getBounds().getX());
        if (this.potionSearchField.onMousePressed(n5, n4 = (int)((float)n2 - this.getBounds().getY()), n3)) {
            return true;
        }
        if (this.scrollbar.onMousePressed(n5, n4, n3)) {
            return true;
        }
        if ((float)n4 >= 44.0f && (float)n4 <= 44.0f + this.getFloatType3()) {
            for (PotionOptionRow row : this.optionRows) {
                if (!row.onMousePressed(n5, n4, n3)) continue;
                return true;
            }
        }
        return true;
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        this.potionSearchField.onMouseReleased((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
    }

    @Override
    public boolean onCharTyped(char c) {
        return this.potionSearchField.onCharTyped(c);
    }

    @Override
    public void update2() {
        this.potionSearchField.update2();
        super.update2();
    }

    @Override
    public boolean onKeyPressed(int n) {
        return this.potionSearchField.onKeyPressed(n);
    }

    public void update3() {
        this.string2 = this.potionEditorState.getSearchQuery();
        this.optionRows.clear();
        this.optionRows.clear();
        this.addChild(this.potionSearchField);
        String string = this.string2 == null ? "" : this.string2.trim().toLowerCase(Locale.ROOT);
        for (PotionCatalogEntry potionCatalogEntry : this.potionPresetController2.getCatalogSortedByAvailability()) {
            if (!string.isEmpty() && !potionCatalogEntry.getDisplayName().toLowerCase(Locale.ROOT).contains(string)) continue;
            PotionOptionRow row = new PotionOptionRow(potionCatalogEntry, this.potionPresetController2, this.itemIconRenderer, 106.0f, this.consumer);
            this.optionRows.add(row);
            this.addChild(row);
        }
        this.scrollController.scrollTo(0.0f, this.getFloatType3());
    }

    public void update4() {
        if (!this.isActive()) {
            return;
        }
        float f = this.getFloatType3();
        float f2 = 44.0f + this.scrollController.getOffset();
        for (PotionOptionRow row : this.optionRows) {
            if (f2 + 14.0f >= 44.0f && f2 <= 44.0f + f) {
                row.prepareIcon();
            }
            f2 += 15.0f;
        }
    }

    @Override
    public void setBounds(GuiBounds bounds2) {
        this.getBounds().setPosition(bounds2.getX() + bounds2.getWidth() + 2.0f, bounds2.getY());
    }

    private float getFloatType() {
        return this.getLastMouseY() - this.getBounds().getY();
    }

    private float getFloatType2() {
        return this.getLastMouseX() - this.getBounds().getX();
    }

    private float getFloatType3() {
        return 101.0f;
    }

    protected float getFloatType4() {
        return 5.0f;
    }

    @Override
    protected void updateLayout() {
        this.potionSearchField.getBounds().setPosition(5.0f, 26.0f);
        if (this.string2 == null || !this.string2.equals(this.potionEditorState.getSearchQuery())) {
            this.update3();
        }
    }

    @Override
    protected void renderPopup(float f, Matrix4f matrix4f2, GuiDrawApi drawApi) {
        drawApi.drawRoundedRectangleOutlined(matrix4f2, 0.0f, 0.0f, 115.5f, 150.0f, this.getFloatType4(), 1.0f, ColorUtils.withAlpha(-1, 0.0f), ThemeColors.borderPrimary());
        int n = ThemeColors.accent();
        FontRegistry.font4.process2(matrix4f2, drawApi, "\u0412\u0430\u0448\u0438 \u0437\u0435\u043b\u044c\u044f", 5.0f, 5.0f, 5.5f, n);
        float f2 = 5.0f + FontRegistry.font4.process3("\u0412\u0430\u0448\u0438 \u0437\u0435\u043b\u044c\u044f", 5.5f) + 1.5f;
        float f3 = 5.0f + FontRegistry.font4.process4("\u0412\u0430\u0448\u0438 \u0437\u0435\u043b\u044c\u044f", 5.5f) / 2.0f;
        FontRegistry.font3.process5(matrix4f2, drawApi, "u", f2, f3 - FontRegistry.font3.process14("u".charAt(0), 0.0f, 6.0f), 6.0f, n);
        FontRegistry.font4.process2(matrix4f2, drawApi, "\u0422\u0435\u043a\u0443\u0449\u0438\u0435 \u0437\u0435\u043b\u044c\u044f", 5.0f, 12.5f, 7.0f, ThemeColors.textPrimary());
        this.potionSearchField.render(f, matrix4f2);
        float f4 = this.getFloatType3();
        if (this.optionRows.isEmpty()) {
            FontRegistry.font2.process2(matrix4f2, drawApi, "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", 3.0f, 44.0f + (f4 - FontRegistry.font2.process4("\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", 6.0f)) / 2.0f, 6.0f, ThemeColors.textPlaceholder());
            return;
        }
        float f5 = (float)this.optionRows.size() * 15.0f - 1.0f;
        this.scrollController.update(f4, f5);
        this.clippedContentRenderer.render(drawApi, matrix4f2, 3.0f, 44.0f, 106.0f, f4, this.scrollController.getOffset(), this.scrollController.getMinimumOffset(f4), matrix4f -> {
            float rowY = 44.0f + this.scrollController.getOffset();
            for (PotionOptionRow row : this.optionRows) {
                row.getBounds().setPosition(3.0f, rowY);
                if (rowY + 14.0f >= 44.0f && rowY <= 44.0f + f4) {
                    row.render(f, (Matrix4f)matrix4f);
                }
                rowY += 15.0f;
            }
        });
        this.scrollController.setContentHeight(f4, f5);
        this.scrollbar.process(drawApi, matrix4f2, 112.5f, 44.0f, f4, this.scrollController, this.getFloatType2(), this.getFloatType());
    }
}

