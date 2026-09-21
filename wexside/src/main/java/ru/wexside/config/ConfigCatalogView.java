/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 */
package ru.wexside.config;

import java.io.IOException;
import java.util.List;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.LocalConfigCatalog;
import ru.wexside.config.LocalConfigEntry;
import ru.wexside.misc.ConfigManager;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.TextureResource;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ConfigCatalogView
extends GuiElement {
    private final String title;
    private final LocalConfigCatalog catalog;
    private final StringBuilder profileName = new StringBuilder();
    private float scrollOffset;
    private float targetScrollOffset;
    private int selectedTab;
    private boolean createDialogOpen;
    private boolean serverSpecific;
    private String status = "";
    private long statusExpiresAt;

    public ConfigCatalogView(GuiBounds bounds, String title, LocalConfigCatalog catalog) {
        super(bounds);
        this.title = title;
        this.catalog = catalog;
        this.refresh();
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int button) {
        if (!this.getBounds().contains(mouseX, mouseY)) {
            return false;
        }
        if (this.createDialogOpen) {
            return this.handleCreateDialogClick(mouseX, mouseY, button);
        }
        if (button == 0 && this.tabBounds(0).contains(mouseX, mouseY)) {
            this.selectedTab = 0;
            this.targetScrollOffset = 0.0f;
            return true;
        }
        if (button == 0 && this.tabBounds(1).contains(mouseX, mouseY)) {
            this.selectedTab = 1;
            this.targetScrollOffset = 0.0f;
            return true;
        }
        float toolbarY = this.getBounds().getY();
        if (button == 0 && this.contains(mouseX, mouseY, this.toolbarButtonX(3), toolbarY, 15.0f, 15.0f)) {
            this.saveActiveProfile();
            return true;
        }
        if (button == 0 && this.contains(mouseX, mouseY, this.toolbarButtonX(2), toolbarY, 15.0f, 15.0f)) {
            this.refresh();
            this.showStatus("\u0421\u043f\u0438\u0441\u043e\u043a \u043e\u0431\u043d\u043e\u0432\u043b\u0451\u043d");
            return true;
        }
        if (button == 0 && this.contains(mouseX, mouseY, this.toolbarButtonX(1), toolbarY, 15.0f, 15.0f)) {
            this.openCreateDialog();
            return true;
        }
        if (button == 0 && this.contains(mouseX, mouseY, this.toolbarButtonX(0), toolbarY, 15.0f, 15.0f)) {
            this.openFolder();
            return true;
        }
        int index = this.rowIndexAt(mouseX, mouseY);
        List<LocalConfigEntry> entries = this.visibleEntries();
        if (index < 0 || index >= entries.size()) {
            return true;
        }
        LocalConfigEntry entry = entries.get(index);
        if (button == 0 && this.menuButtonBounds(this.rowBounds(index)).contains(mouseX, mouseY)) {
            this.save(entry);
            return true;
        }
        if (button == 1) {
            this.delete(entry);
            return true;
        }
        if (button == 0) {
            this.load(entry);
        }
        return true;
    }

    @Override
    public void onMouseScroll(int mouseX, int mouseY, double amount) {
        if (!this.getBounds().contains(mouseX, mouseY) || this.createDialogOpen) {
            return;
        }
        this.targetScrollOffset = Math.clamp(this.targetScrollOffset - (float)amount * 18.0f, 0.0f, this.maximumScrollOffset());
    }

    @Override
    public boolean onCharTyped(char character) {
        if (!this.createDialogOpen || Character.isISOControl(character) || this.profileName.length() >= 32) {
            return false;
        }
        if ("\\/:*?\"<>|".indexOf(character) < 0) {
            this.profileName.append(character);
        }
        return true;
    }

    @Override
    public boolean onKeyPressed(int keyCode) {
        if (!this.createDialogOpen) {
            return false;
        }
        if (keyCode == 256) {
            this.createDialogOpen = false;
            return true;
        }
        if (keyCode == 259 && !this.profileName.isEmpty()) {
            this.profileName.deleteCharAt(this.profileName.length() - 1);
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.createProfile();
            return true;
        }
        return false;
    }

    @Override
    public float render(float delta, Matrix4f matrix) {
        this.scrollOffset = FrameInterpolator.lerpTowards(this.scrollOffset, this.targetScrollOffset, 30.0f);
        GuiDrawApi renderer = WexSideClient.getGuiRenderer();
        this.renderTabs(renderer, matrix);
        this.renderToolbar(renderer, matrix);
        this.renderRows(renderer, matrix);
        if (this.createDialogOpen) {
            this.renderCreateDialog(renderer, matrix);
        }
        return this.getBounds().getY() + this.getBounds().getHeight();
    }

    private void renderTabs(GuiDrawApi renderer, Matrix4f matrix) {
        this.renderTab(renderer, matrix, 0, "\u0412\u0441\u0435");
        this.renderTab(renderer, matrix, 1, "\u041b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0435");
    }

    private void renderTab(GuiDrawApi renderer, Matrix4f matrix, int index, String label) {
        GuiBounds tab = this.tabBounds(index);
        boolean active = this.selectedTab == index;
        renderer.drawRoundedRectangle(matrix, tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight(), 6.0f, active ? ThemeColors.backgroundControl() : ThemeColors.backgroundSecondary());
        renderer.drawRoundedOutline(matrix, tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight(), 6.0f, 0.75f, active ? ThemeColors.accent() : ThemeColors.borderPrimary());
        float textWidth = FontRegistry.font4.process3(label, 6.5f);
        float textHeight = FontRegistry.font4.process4(label, 6.5f);
        FontRegistry.font4.process2(matrix, renderer, label, tab.getX() + (tab.getWidth() - textWidth) * 0.5f, tab.getY() + (tab.getHeight() - textHeight) * 0.5f, 6.5f, active ? ThemeColors.accent() : ThemeColors.textSecondary());
    }

    private void renderToolbar(GuiDrawApi renderer, Matrix4f matrix) {
        float y = this.getBounds().getY();
        this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(3), y, "S");
        this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(2), y, "\u0435");
        this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(1), y, "\u042f");
        this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(0), y, "x");
        FontRegistry.font2.process2(matrix, renderer, this.statusText(), this.getBounds().getX() + 8.0f, this.getBounds().getY() + 17.0f, 5.0f, ThemeColors.textMuted());
    }

    private void renderToolbarButton(GuiDrawApi renderer, Matrix4f matrix, float x, float y, String icon) {
        boolean hovered = this.hovered(x, y, 15.0f, 15.0f);
        renderer.drawRoundedRectangle(matrix, x, y, 15.0f, 15.0f, 5.0f, hovered ? ThemeColors.backgroundHover() : ThemeColors.backgroundControl());
        renderer.drawRoundedOutline(matrix, x, y, 15.0f, 15.0f, 5.0f, 0.75f, ThemeColors.borderPrimary());
        float iconWidth = FontRegistry.font3.process3(icon, 6.0f);
        float iconHeight = FontRegistry.font3.process4(icon, 6.0f);
        FontRegistry.font3.process5(matrix, renderer, icon, x + (15.0f - iconWidth) * 0.5f, y + (15.0f - iconHeight) * 0.5f, 6.0f, hovered ? ThemeColors.accent() : ThemeColors.textSecondary());
    }

    private void renderRows(GuiDrawApi renderer, Matrix4f matrix) {
        float contentY = this.getBounds().getY() + 24.0f;
        float contentHeight = Math.max(0.0f, this.getBounds().getHeight() - 24.0f);
        renderer.pushScissor(matrix, this.getBounds().getX(), contentY, this.getBounds().getWidth(), contentHeight);
        List<LocalConfigEntry> entries = this.visibleEntries();
        for (int index = 0; index < entries.size(); ++index) {
            GuiBounds row = this.rowBounds(index);
            if (!(row.getY() + row.getHeight() >= contentY) || !(row.getY() <= contentY + contentHeight)) continue;
            this.renderRow(renderer, matrix, row, entries.get(index));
        }
        renderer.popScissor();
    }

    private void renderRow(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, LocalConfigEntry entry) {
        boolean active = this.isActive(entry);
        boolean hovered = this.hovered(row.getX(), row.getY(), row.getWidth(), row.getHeight());
        renderer.drawRoundedRectangle(matrix, row.getX(), row.getY(), row.getWidth(), row.getHeight(), 11.0f, hovered ? ThemeColors.backgroundHover() : ColorUtils.withAlpha(ThemeColors.backgroundSecondary(), 0.0f));
        renderer.drawRoundedOutline(matrix, row.getX(), row.getY(), row.getWidth(), row.getHeight(), 11.0f, 0.75f, active ? ThemeColors.accent() : ThemeColors.backgroundControl());
        this.renderAvatar(renderer, matrix, row, entry.avatar());
        float textX = row.getX() + 28.0f;
        FontRegistry.font4.process2(matrix, renderer, entry.name(), textX, row.getY() + 6.0f, 6.5f, ThemeColors.textPrimary());
        FontRegistry.font2.process2(matrix, renderer, "\u041b\u043e\u043a\u0430\u043b\u044c\u043d\u044b\u0439  \u2022  " + entry.updatedAt(), textX, row.getY() + 16.5f, 5.5f, ThemeColors.textMuted());
        this.renderStateButton(renderer, matrix, row, active);
        this.renderSaveButton(renderer, matrix, row, active);
    }

    private void renderAvatar(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, TextureResource avatar) {
        float x = row.getX() + 6.0f;
        float y = row.getY() + 6.0f;
        if (avatar != null) {
            int texture = renderer.bindTexture(avatar.getTextureId(), avatar.getWidth(), avatar.getHeight());
            renderer.drawRoundedTextureTinted(matrix, x, y, 18.0f, 18.0f, 12.0f, texture, -1);
            return;
        }
        renderer.drawRoundedRectangle(matrix, x, y, 18.0f, 18.0f, 12.0f, ThemeColors.backgroundControl());
        renderer.drawRoundedOutline(matrix, x, y, 18.0f, 18.0f, 12.0f, 0.75f, ThemeColors.borderPrimary());
        float glyphWidth = FontRegistry.font3.process3("@", 9.0f);
        float glyphHeight = FontRegistry.font3.process4("@", 9.0f);
        FontRegistry.font3.process5(matrix, renderer, "@", x + (18.0f - glyphWidth) * 0.5f, y + (18.0f - glyphHeight) * 0.5f, 9.0f, ThemeColors.textSecondary());
    }

    private void renderStateButton(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, boolean active) {
        float x = row.getX() + row.getWidth() - 42.0f;
        float y = row.getY() + 7.5f;
        renderer.drawRoundedRectangle(matrix, x, y, 15.0f, 15.0f, 9.0f, active ? ThemeColors.accentTint() : ThemeColors.backgroundControl());
        String glyph = active ? "ON" : ">";
        float width = FontRegistry.font6.process3(glyph, 5.0f);
        float height = FontRegistry.font6.process4(glyph, 5.0f);
        FontRegistry.font6.process2(matrix, renderer, glyph, x + (15.0f - width) * 0.5f, y + (15.0f - height) * 0.5f, 5.0f, active ? ThemeColors.accent() : ThemeColors.textSecondary());
    }

    private void renderSaveButton(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, boolean active) {
        GuiBounds button = this.menuButtonBounds(row);
        int background = active ? ThemeColors.accentTint() : ThemeColors.backgroundControl();
        renderer.drawRoundedRectangle(matrix, button.getX(), button.getY(), button.getWidth(), button.getHeight(), 9.0f, background);
        float width = FontRegistry.font6.process3("S", 5.0f);
        float height = FontRegistry.font6.process4("S", 5.0f);
        FontRegistry.font6.process2(matrix, renderer, "S", button.getX() + (button.getWidth() - width) * 0.5f, button.getY() + (button.getHeight() - height) * 0.5f, 5.0f, active ? ThemeColors.accent() : ThemeColors.textSecondary());
    }

    private void renderCreateDialog(GuiDrawApi renderer, Matrix4f matrix) {
        GuiBounds dialog = this.createDialogBounds();
        renderer.drawRoundedRectangle(matrix, this.getBounds().getX(), this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight(), 10.5f, ThemeColors.modalScrim());
        renderer.drawRoundedRectangle(matrix, dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight(), 10.5f, ThemeColors.backgroundPrimary());
        renderer.drawRoundedOutline(matrix, dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight(), 10.5f, 0.75f, ThemeColors.borderStrong());
        FontRegistry.font7.process2(matrix, renderer, "\u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044e", dialog.getX() + 8.0f, dialog.getY() + 8.0f, 8.0f, ThemeColors.textPrimary());
        GuiBounds input = this.createInputBounds();
        renderer.drawRoundedRectangle(matrix, input.getX(), input.getY(), input.getWidth(), input.getHeight(), 6.0f, ThemeColors.formatFieldFill());
        renderer.drawRoundedOutline(matrix, input.getX(), input.getY(), input.getWidth(), input.getHeight(), 6.0f, 0.75f, ThemeColors.borderPrimary());
        String inputText = this.profileName.isEmpty() ? "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435" : this.profileName.toString();
        FontRegistry.font4.process2(matrix, renderer, inputText, input.getX() + 6.0f, input.getY() + 5.0f, 6.5f, this.profileName.isEmpty() ? ThemeColors.textPlaceholder() : ThemeColors.textPrimary());
        GuiBounds serverToggle = this.serverToggleBounds();
        renderer.drawRoundedRectangle(matrix, serverToggle.getX(), serverToggle.getY(), serverToggle.getWidth(), serverToggle.getHeight(), 6.0f, this.serverSpecific ? ThemeColors.accentTint() : ThemeColors.backgroundControl());
        FontRegistry.font4.process2(matrix, renderer, this.serverSpecific ? "\u0414\u043b\u044f \u0442\u0435\u043a\u0443\u0449\u0435\u0433\u043e \u0441\u0435\u0440\u0432\u0435\u0440\u0430" : "\u041e\u0431\u0449\u0438\u0439 \u043a\u043e\u043d\u0444\u0438\u0433", serverToggle.getX() + 6.0f, serverToggle.getY() + 4.0f, 6.0f, this.serverSpecific ? ThemeColors.accent() : ThemeColors.textSecondary());
        GuiBounds create = this.createButtonBounds();
        renderer.drawRoundedRectangle(matrix, create.getX(), create.getY(), create.getWidth(), create.getHeight(), 8.0f, this.profileName.isEmpty() ? ThemeColors.backgroundControl() : ThemeColors.accentTint());
        FontRegistry.font4.process2(matrix, renderer, "ENTER   \u0421\u043e\u0437\u0434\u0430\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044e", create.getX() + 8.0f, create.getY() + 9.0f, 6.0f, this.profileName.isEmpty() ? ThemeColors.textDisabled() : ThemeColors.accent());
    }

    private boolean handleCreateDialogClick(int mouseX, int mouseY, int button) {
        if (button != 0) {
            return true;
        }
        if (this.serverToggleBounds().contains(mouseX, mouseY)) {
            this.serverSpecific = !this.serverSpecific;
            return true;
        }
        if (this.createButtonBounds().contains(mouseX, mouseY)) {
            this.createProfile();
            return true;
        }
        if (!this.createDialogBounds().contains(mouseX, mouseY)) {
            this.createDialogOpen = false;
        }
        return true;
    }

    private void openCreateDialog() {
        this.profileName.setLength(0);
        this.serverSpecific = false;
        this.createDialogOpen = true;
    }

    private void createProfile() {
        String name = this.profileName.toString().trim();
        if (name.isBlank() || name.length() > 32) {
            this.showStatus("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u043e\u0442 1 \u0434\u043e 32 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432");
            return;
        }
        ConfigManager manager = WexSideClient.getConfigManager();
        if (manager == null || manager.profileExists(name)) {
            this.showStatus("\u041a\u043e\u043d\u0444\u0438\u0433 \u0441 \u0442\u0430\u043a\u0438\u043c \u0438\u043c\u0435\u043d\u0435\u043c \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442");
            return;
        }
        try {
            manager.saveProfileWithDisplayName(name, this.serverSpecific ? this.currentServer() : "\u041e\u0431\u0449\u0438\u0439");
            this.createDialogOpen = false;
            this.refresh();
            this.showStatus("\u041a\u043e\u043d\u0444\u0438\u0433 \u0441\u043e\u0437\u0434\u0430\u043d: " + name);
        }
        catch (IOException exception) {
            this.showStatus("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433");
        }
    }

    private void load(LocalConfigEntry entry) {
        ConfigManager manager = WexSideClient.getConfigManager();
        if (manager == null) {
            return;
        }
        try {
            manager.loadProfile(entry.name());
            this.showStatus("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d: " + entry.name());
        }
        catch (RuntimeException exception) {
            this.showStatus("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433");
        }
    }

    private void save(LocalConfigEntry entry) {
        this.save(entry.name());
    }

    private void save(String profileName) {
        ConfigManager manager = WexSideClient.getConfigManager();
        if (manager == null) {
            return;
        }
        try {
            manager.saveProfile(profileName);
            this.refresh();
            this.showStatus("\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d: " + profileName);
        }
        catch (IOException | RuntimeException exception) {
            this.showStatus("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433");
        }
    }

    private void saveActiveProfile() {
        String profileName;
        ConfigManager manager = WexSideClient.getConfigManager();
        String string = profileName = manager == null ? null : manager.getCurrentProfileName();
        if (profileName == null || profileName.isBlank()) {
            this.showStatus("\u0421\u043d\u0430\u0447\u0430\u043b\u0430 \u0441\u043e\u0437\u0434\u0430\u0439\u0442\u0435 \u0438\u043b\u0438 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u0435 \u043a\u043e\u043d\u0444\u0438\u0433");
            return;
        }
        this.save(profileName);
    }

    private void delete(LocalConfigEntry entry) {
        if (this.isActive(entry)) {
            this.showStatus("\u041d\u0435\u043b\u044c\u0437\u044f \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0439 \u043a\u043e\u043d\u0444\u0438\u0433");
            return;
        }
        if (this.catalog.delete(entry)) {
            this.showStatus("\u041a\u043e\u043d\u0444\u0438\u0433 \u0443\u0434\u0430\u043b\u0451\u043d: " + entry.name());
        } else {
            this.showStatus("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043a\u043e\u043d\u0444\u0438\u0433");
        }
    }

    private void openFolder() {
        ConfigManager manager = WexSideClient.getConfigManager();
        if (manager == null) {
            return;
        }
        try {
            manager.openConfigFolder();
        }
        catch (IOException exception) {
            this.showStatus("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432");
        }
    }

    private void refresh() {
        if (this.catalog != null) {
            this.catalog.refresh();
        }
        this.targetScrollOffset = Math.min(this.targetScrollOffset, this.maximumScrollOffset());
    }

    private List<LocalConfigEntry> visibleEntries() {
        return this.catalog == null ? List.of() : this.catalog.entries();
    }

    private GuiBounds tabBounds(int index) {
        return new GuiBounds(this.getBounds().getX() + (float)index * 87.5f, this.getBounds().getY(), 87.5f, 12.0f);
    }

    private GuiBounds rowBounds(int index) {
        return new GuiBounds(this.getBounds().getX() + 8.0f, this.getBounds().getY() + 30.0f + (float)index * 34.0f - this.scrollOffset, this.getBounds().getWidth() - 16.0f, 30.0f);
    }

    private GuiBounds menuButtonBounds(GuiBounds row) {
        return new GuiBounds(row.getX() + row.getWidth() - 22.0f, row.getY() + 7.5f, 15.0f, 15.0f);
    }

    private int rowIndexAt(int mouseX, int mouseY) {
        List<LocalConfigEntry> entries = this.visibleEntries();
        for (int index = 0; index < entries.size(); ++index) {
            if (!this.rowBounds(index).contains(mouseX, mouseY)) continue;
            return index;
        }
        return -1;
    }

    private float maximumScrollOffset() {
        return Math.max(0.0f, (float)this.visibleEntries().size() * 34.0f - Math.max(0.0f, this.getBounds().getHeight() - 30.0f));
    }

    private float toolbarButtonX(int positionFromRight) {
        return this.getBounds().getX() + this.getBounds().getWidth() - 23.0f - (float)positionFromRight * 19.0f;
    }

    private GuiBounds createDialogBounds() {
        return new GuiBounds(this.getBounds().getX() + (this.getBounds().getWidth() - 175.0f) * 0.5f, this.getBounds().getY() + (this.getBounds().getHeight() - 112.0f) * 0.5f, 175.0f, 112.0f);
    }

    private GuiBounds createInputBounds() {
        GuiBounds dialog = this.createDialogBounds();
        return new GuiBounds(dialog.getX() + 8.0f, dialog.getY() + 29.0f, 159.0f, 18.0f);
    }

    private GuiBounds serverToggleBounds() {
        GuiBounds dialog = this.createDialogBounds();
        return new GuiBounds(dialog.getX() + 8.0f, dialog.getY() + 53.0f, 159.0f, 16.0f);
    }

    private GuiBounds createButtonBounds() {
        GuiBounds dialog = this.createDialogBounds();
        return new GuiBounds(dialog.getX() + 8.0f, dialog.getY() + 77.0f, 159.0f, 27.0f);
    }

    private boolean isActive(LocalConfigEntry entry) {
        ConfigManager manager = WexSideClient.getConfigManager();
        return manager != null && entry.name().equals(manager.getCurrentProfileName());
    }

    private String currentServer() {
        class_310 client = class_310.method_1551();
        return client.method_1558() == null ? "\u041e\u0434\u0438\u043d\u043e\u0447\u043d\u0430\u044f \u0438\u0433\u0440\u0430" : client.method_1558().field_3761;
    }

    private boolean hovered(float x, float y, float width, float height) {
        GuiInteractionState interaction = GuiInteractionState.getInstance();
        float mouseX = (float)interaction.getScaledMouseX() - interaction.getRootPanel().getBounds().getX();
        float mouseY = (float)interaction.getScaledMouseY() - interaction.getRootPanel().getBounds().getY();
        return this.contains(mouseX, mouseY, x, y, width, height);
    }

    private boolean contains(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private String statusText() {
        if (!this.status.isBlank() && System.currentTimeMillis() < this.statusExpiresAt) {
            return this.status;
        }
        return this.title;
    }

    private void showStatus(String message) {
        this.status = message;
        this.statusExpiresAt = System.currentTimeMillis() + 3000L;
    }
}

