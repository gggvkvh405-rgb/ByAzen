/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.ui;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.PotionCatalogEntry;
import ru.byazen.misc.ThemeColors;
import ru.byazen.render.BakedItemIcon;
import ru.byazen.render.ItemIconRenderer;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.PotionPresetController;

public final class PotionOptionRow
extends GuiElement {
    private final PotionCatalogEntry potion;
    private final PotionPresetController potionService;
    private final ItemIconRenderer icons;
    private final Consumer<PotionCatalogEntry> onSelected;
    private BakedItemIcon icon;

    public PotionOptionRow(PotionCatalogEntry potion, PotionPresetController potionService, ItemIconRenderer icons, float width, Consumer<PotionCatalogEntry> onSelected) {
        super(new GuiBounds(0.0f, 0.0f, width, 14.0f));
        this.potion = potion;
        this.potionService = potionService;
        this.icons = icons;
        this.onSelected = onSelected;
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.getBounds().contains(mouseX, mouseY)) {
            return false;
        }
        this.onSelected.accept(this.potion);
        return true;
    }

    public void prepareIcon() {
        this.icon = this.icons.process(this.potionService.resolveStack(this.potion));
    }

    @Override
    public float render(float delta, Matrix4f matrix) {
        this.prepareIcon();
        GuiDrawApi renderer = ByAzenClient.getGuiRenderer();
        GuiBounds bounds = this.getBounds();
        int background = this.potionService.isAvailable(this.potion) ? ThemeColors.formatFieldFill() : ThemeColors.borderPrimary();
        renderer.drawRoundedRectangle(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 4.0f, background);
        if (this.icon != null) {
            this.icons.process2(renderer, matrix, this.icon, bounds.getX() + 2.0f, bounds.getY() + 2.0f, 10.0f, -1);
        }
        FontRegistry.font2.process2(matrix, renderer, this.potion.getDisplayName(), bounds.getX() + 15.0f, bounds.getY() + 4.0f, 6.0f, ThemeColors.textPrimary());
        return bounds.getY() + bounds.getHeight();
    }
}

