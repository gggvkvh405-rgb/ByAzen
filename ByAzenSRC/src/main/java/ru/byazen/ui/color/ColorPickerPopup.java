/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.ui.color;

import java.util.List;
import org.joml.Matrix4f;
import ru.byazen.misc.ColorPicker;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.PopupHeader;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.FloatingPanel;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.ToggleIndicatorRenderer;

public final class ColorPickerPopup
extends FloatingPanel {
    private static final float WIDTH = 125.0f;
    private static final float PADDING = 5.0f;
    private static final float SWATCH_SIZE = 7.0f;
    private static final float SWATCH_GAP = 2.0f;
    private final ColorSetting setting;
    private final PopupHeader header;
    private final ColorPicker picker;
    private final List<ToggleIndicatorRenderer> swatches = List.of(new ToggleIndicatorRenderer(), new ToggleIndicatorRenderer());

    public ColorPickerPopup(ColorSetting setting) {
        super(new GuiBounds(0.0f, 0.0f, 125.0f, 172.5f));
        this.setting = setting;
        this.header = new PopupHeader(new GuiBounds(5.0f, 5.0f, 115.0f, 0.0f), "\u0412\u044b\u0431\u043e\u0440 \u0446\u0432\u0435\u0442\u0430", "Z", setting.getDisplayName());
        this.picker = new ColorPicker(new GuiBounds(5.0f, 0.0f, 115.0f, 77.0f), setting);
        this.addChild(this.picker);
    }

    @Override
    public void update() {
        this.picker.update();
    }

    @Override
    protected void updateLayout() {
        float labelHeight = FontRegistry.font4.process4(this.getValueLabel(), 5.0f);
        float contentY = Math.max(5.0f + this.header.getFloatType2(), 5.0f + labelHeight + 10.0f) + 5.0f;
        float pickerHeight = this.picker.getFloatType2();
        this.picker.getBounds().setPosition(5.0f, contentY);
        this.picker.getBounds().setSize(115.0f, pickerHeight);
        this.getBounds().setSize(125.0f, contentY + pickerHeight + 12.5f);
    }

    @Override
    protected void renderPanel(float delta, Matrix4f matrix, GuiDrawApi renderer) {
        this.header.BlockHitResult(matrix, renderer);
        this.renderValue(matrix, renderer);
        this.picker.render(delta, matrix);
    }

    private void renderValue(Matrix4f matrix, GuiDrawApi renderer) {
        if (!this.setting.isDoubleColorMode()) {
            String value = this.setting.getDisplayText();
            float width = FontRegistry.font4.process3(value, 5.0f);
            float height = FontRegistry.font4.process4(value, 5.0f);
            float x = 120.0f - width;
            float swatchY = 5.0f + height + 3.0f;
            FontRegistry.font4.process2(matrix, renderer, value, x, 5.0f, 5.0f, this.setting.getColor());
            this.swatches.getFirst().process(matrix, renderer, new GuiBounds(113.0f, swatchY, 7.0f, 7.0f), this.setting.getColor(), true);
            return;
        }
        String first = this.setting.getPrimaryHex();
        String separator = ", ";
        String second = this.setting.getSecondaryHex();
        float firstWidth = FontRegistry.font4.process3(first, 5.0f);
        float separatorWidth = FontRegistry.font4.process3(separator, 5.0f);
        float totalWidth = firstWidth + separatorWidth + FontRegistry.font4.process3(second, 5.0f);
        float x = 120.0f - totalWidth;
        float swatchY = 5.0f + FontRegistry.font4.process4(first, 5.0f) + 3.0f;
        FontRegistry.font4.process2(matrix, renderer, first, x, 5.0f, 5.0f, this.setting.getPrimaryColor());
        FontRegistry.font4.process2(matrix, renderer, separator, x + firstWidth, 5.0f, 5.0f, this.setting.getPrimaryColor());
        FontRegistry.font4.process2(matrix, renderer, second, x + firstWidth + separatorWidth, 5.0f, 5.0f, this.setting.getSecondaryColor());
        int[] colors = new int[]{this.setting.getPrimaryColor(), this.setting.getSecondaryColor()};
        float swatchesWidth = (float)colors.length * 7.0f + (float)(colors.length - 1) * 2.0f;
        float swatchX = 120.0f - swatchesWidth;
        for (int index = 0; index < colors.length; ++index) {
            this.swatches.get(index).process(matrix, renderer, new GuiBounds(swatchX + (float)index * 9.0f, swatchY, 7.0f, 7.0f), colors[index], true);
        }
    }

    private String getValueLabel() {
        return this.setting.isDoubleColorMode() ? this.setting.getPrimaryHex() + ", " + this.setting.getSecondaryHex() : this.setting.getDisplayText();
    }
}

