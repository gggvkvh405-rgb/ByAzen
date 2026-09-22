package rtx.byazen.api.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.cosmetics.Cosmetic;
import rtx.byazen.utils.cosmetics.CosmeticGrants;
import rtx.byazen.utils.cosmetics.CosmeticPreview;
import rtx.byazen.utils.cosmetics.CosmeticRegistry;
import rtx.byazen.utils.cosmetics.CosmeticSounds;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Каталог косметики ByAzen (идеи №121 и №122 из IDEAS.md).
 * <p>
 * Слева наборы и фильтры, в центре список предметов, справа живое 3D-превью: модель можно крутить
 * мышью, а это ровно та модель, что появится на игроке. Здесь же надевают и снимают аксессуары,
 * кладут их в избранное, забирают сезонный набор и применяют код выдачи от LiteApi.
 */
public final class CosmeticCatalogScreen
extends BaseScreen {

    private static final String FONT_BOLD = "montserrat-bold";
    private static final String FONT = "montserrat-medium";
    private static final float W = 566.0f;
    private static final float H = 306.0f;
    private static final float PAD = 14.0f;
    private static final float ROW_H = 26.0f;
    private static final String[] SETS = {"all", "base", Cosmetic.FLAVOR_SNOW, Cosmetic.FLAVOR_NEON, Cosmetic.FLAVOR_SPACE, "fav"};
    private static final String[] SET_NAMES = {"Все", "Базовые", "Зима", "Неон", "Космос", "Избранное"};

    private final List<Cosmetic> items = new ArrayList<Cosmetic>();
    private String setFilter = "all";
    private int selected;
    private int scroll;
    private float yaw = 24.0f;
    private float pitch = 12.0f;
    private float zoom = 1.0f;
    private float openAnimation;
    private long lastFrameNanos;
    private boolean dragging;
    private float lastMouseX;
    private float lastMouseY;
    private final StringBuilder code = new StringBuilder();
    private boolean codeMode;
    private String status = "";
    private long statusUntil;

    public CosmeticCatalogScreen() {
        super(Text.literal("Косметика ByAzen"));
        this.rebuild();
    }

    private void rebuild() {
        this.items.clear();
        for (Cosmetic cosmetic : CosmeticRegistry.all()) {
            if (this.matches(cosmetic)) {
                this.items.add(cosmetic);
            }
        }
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll()));
        this.selected = this.items.isEmpty() ? -1 : Math.max(0, Math.min(this.selected, this.items.size() - 1));
    }

    private boolean matches(Cosmetic cosmetic) {
        if ("all".equals(this.setFilter)) {
            return true;
        }
        if ("base".equals(this.setFilter)) {
            return !cosmetic.inSet();
        }
        if ("fav".equals(this.setFilter)) {
            return Cosmetics.favorite(cosmetic);
        }
        return this.setFilter.equals(cosmetic.flavor);
    }

    private Cosmetic current() {
        return this.selected >= 0 && this.selected < this.items.size() ? this.items.get(this.selected) : null;
    }

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private static float listX() {
        return CosmeticCatalogScreen.panelX() + PAD + 78.0f;
    }

    private static float listY() {
        return CosmeticCatalogScreen.panelY() + PAD + 44.0f;
    }

    private static float listW() {
        return 190.0f;
    }

    private static float listH() {
        return H - PAD * 2.0f - 68.0f;
    }

    private static float previewX() {
        return CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW() + 12.0f;
    }

    private static float previewY() {
        return CosmeticCatalogScreen.panelY() + PAD + 44.0f;
    }

    private static float previewW() {
        return CosmeticCatalogScreen.panelX() + W - PAD - CosmeticCatalogScreen.previewX();
    }

    private static float previewH() {
        return CosmeticCatalogScreen.listH();
    }

    private int rowsVisible() {
        return (int)(CosmeticCatalogScreen.listH() / ROW_H);
    }

    private int maxScroll() {
        return Math.max(0, this.items.size() - this.rowsVisible());
    }

    private int rowAt(float mouseX, float mouseY) {
        if (mouseX < CosmeticCatalogScreen.listX() || mouseX > CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW()) {
            return -1;
        }
        if (mouseY < CosmeticCatalogScreen.listY() || mouseY > CosmeticCatalogScreen.listY() + CosmeticCatalogScreen.listH()) {
            return -1;
        }
        int row = (int)((mouseY - CosmeticCatalogScreen.listY()) / ROW_H);
        int index = this.scroll + row;
        return index >= 0 && index < this.items.size() ? index : -1;
    }

    private int setAt(float mouseX, float mouseY) {
        float x = CosmeticCatalogScreen.panelX() + PAD;
        float y = CosmeticCatalogScreen.panelY() + PAD + 44.0f;
        for (int i = 0; i < SETS.length; ++i) {
            float rowY = y + (float)i * 20.0f;
            if (mouseX >= x && mouseX <= x + 70.0f && mouseY >= rowY && mouseY <= rowY + 18.0f) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float delta = this.deltaSeconds();
        this.openAnimation += (1.0f - this.openAnimation) * Math.min(1.0f, delta * 9.0f);
        float a = Math.max(0.0f, Math.min(1.0f, this.openAnimation));
        float x = CosmeticCatalogScreen.panelX();
        float y = CosmeticCatalogScreen.panelY();
        this.updateDrag(delta);

        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));

        Render2D.msdfText(FONT_BOLD, "Косметика ByAzen", x + PAD, y + PAD, 11.0f, CosmeticCatalogScreen.ink(a));
        int owned = Cosmetics.ownedCount();
        String counter = owned + " из " + Cosmetics.totalCount() + " открыто";
        Render2D.msdfText(FONT, counter, x + PAD + Render2D.msdfWidth(FONT_BOLD, "Косметика ByAzen", 11.0f) + 8.0f, y + PAD + 3.0f,
                7.6f, CosmeticCatalogScreen.sub(a));
        Render2D.msdfText(FONT_BOLD, "✕", x + W - PAD - 4.0f, y + 10.0f, 9.0f, CosmeticCatalogScreen.sub(a * 0.9f));

        // фильтры по наборам
        for (int i = 0; i < SETS.length; ++i) {
            float rowY = CosmeticCatalogScreen.panelY() + PAD + 44.0f + (float)i * 20.0f;
            boolean active = this.setFilter.equals(SETS[i]);
            boolean hovered = mouseX >= x + PAD && mouseX <= x + PAD + 70.0f && mouseY >= rowY && mouseY <= rowY + 18.0f;
            int background = active ? CosmeticCatalogScreen.selected(a) : hovered ? CosmeticCatalogScreen.hover(a)
                    : CosmeticCatalogScreen.field(a);
            Render2D.rect(x + PAD, rowY, 70.0f, 18.0f, 5.0f, background);
            String label = SET_NAMES[i];
            if (!"all".equals(SETS[i]) && !"base".equals(SETS[i]) && !"fav".equals(SETS[i])) {
                label = label + " " + Cosmetics.ownedInSet(SETS[i]) + "/10";
            }
            int color = active ? CosmeticCatalogScreen.ink(a) : CosmeticCatalogScreen.sub(a * 0.95f);
            Render2D.msdfText(FONT, label, x + PAD + 6.0f, rowY + 5.0f, 6.8f, color);
        }
        String season = "Сезон: " + CosmeticCatalogScreen.seasonLine();
        Render2D.msdfText(FONT, season, x + PAD, CosmeticCatalogScreen.panelY() + PAD + 44.0f + (float)SETS.length * 20.0f + 6.0f,
                6.5f, CosmeticCatalogScreen.sub(a * 0.8f));

        // список предметов
        Render2D.rect(CosmeticCatalogScreen.listX(), CosmeticCatalogScreen.listY(), CosmeticCatalogScreen.listW(),
                CosmeticCatalogScreen.listH(), 8.0f, CosmeticCatalogScreen.listBackground(a));
        Render2D.pushScissor(drawContext, CosmeticCatalogScreen.listX(), CosmeticCatalogScreen.listY(),
                CosmeticCatalogScreen.listW(), CosmeticCatalogScreen.listH());
        for (int row = 0; row < this.rowsVisible(); ++row) {
            int index = this.scroll + row;
            if (index >= this.items.size()) {
                break;
            }
            Cosmetic cosmetic = this.items.get(index);
            float rowY = CosmeticCatalogScreen.listY() + (float)row * ROW_H;
            boolean isSelected = index == this.selected;
            boolean hovered = index == this.rowAt(mouseX, mouseY);
            if (isSelected) {
                RectUtil.drawClientRect(CosmeticCatalogScreen.listX() + 1.0f, rowY, CosmeticCatalogScreen.listW() - 3.0f, ROW_H - 2.0f, 5.0f, a * 0.95f);
            } else if (hovered) {
                Render2D.rect(CosmeticCatalogScreen.listX() + 1.0f, rowY, CosmeticCatalogScreen.listW() - 3.0f, ROW_H - 2.0f, 5.0f,
                        CosmeticCatalogScreen.hover(a));
            }
            boolean available = Cosmetics.available(cosmetic);
            drawContext.getMatrices().pushMatrix();
            drawContext.getMatrices().translate(CosmeticCatalogScreen.listX() + 6.0f, rowY + 6.0f);
            drawContext.getMatrices().scale(0.85f, 0.85f);
            drawContext.drawItem(new ItemStack(cosmetic.icon), 0, 0);
            drawContext.getMatrices().popMatrix();
            if (!available) {
                Render2D.rect(CosmeticCatalogScreen.listX() + 6.0f, rowY + 6.0f, 14.0f, 14.0f, 2.0f, CosmeticCatalogScreen.lock(a));
            }
            int nameColor = isSelected ? CosmeticCatalogScreen.ink(a) : available ? CosmeticCatalogScreen.sub(a * 0.96f)
                    : CosmeticCatalogScreen.locked(a);
            Render2D.msdfText(FONT, cosmetic.name, CosmeticCatalogScreen.listX() + 26.0f, rowY + 5.0f, 7.0f, nameColor);
            String tags = cosmetic.slotName() + " · " + cosmetic.setNameText();
            Render2D.msdfText(FONT, tags, CosmeticCatalogScreen.listX() + 26.0f, rowY + 15.0f, 6.0f, CosmeticCatalogScreen.sub(a * 0.7f));
            if (Cosmetics.equipped(cosmetic)) {
                Render2D.circleOutline(CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW() - 14.0f, rowY + 13.0f, 5.0f, 1.2f,
                        ClientAccent.accent(Math.round(210.0f * a)));
                Render2D.circle(CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW() - 14.0f, rowY + 13.0f, 2.4f, 8.0f,
                        ClientAccent.accent(Math.round(240.0f * a)));
            } else if (Cosmetics.favorite(cosmetic)) {
                Render2D.circle(CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW() - 14.0f, rowY + 13.0f, 2.2f, 8.0f,
                        CosmeticCatalogScreen.sub(a * 0.85f));
            }
        }
        Render2D.popScissor(drawContext);
        if (this.maxScroll() > 0) {
            float barH = Math.max(16.0f, CosmeticCatalogScreen.listH() * (float)this.rowsVisible() / (float)this.items.size());
            float barY = CosmeticCatalogScreen.listY() + (CosmeticCatalogScreen.listH() - barH) * ((float)this.scroll / (float)this.maxScroll());
            Render2D.rect(CosmeticCatalogScreen.listX() + CosmeticCatalogScreen.listW() - 3.0f, barY, 2.0f, barH, 1.0f,
                    ClientAccent.accent(Math.round(160.0f * a)));
        }

        this.renderPreview(drawContext, a, x, y);
    }

    private void renderPreview(DrawContext drawContext, float a, float panelX, float panelY) {
        float px = CosmeticCatalogScreen.previewX();
        float py = CosmeticCatalogScreen.previewY();
        float pw = CosmeticCatalogScreen.previewW();
        float ph = CosmeticCatalogScreen.previewH();
        Render2D.rect(px, py, pw, ph, 10.0f, CosmeticCatalogScreen.listBackground(a));
        for (int ring = 4; ring >= 1; --ring) {
            float inset = (float)ring * 6.0f;
            Render2D.rect(px + inset, py + inset, pw - inset * 2.0f, ph - inset * 2.0f, 8.0f,
                    ClientAccent.accent(Math.max(2.0f, 14.0f * a / (float)ring)));
        }
        Cosmetic cosmetic = this.current();
        if (cosmetic == null) {
            Render2D.msdfText(FONT, "Ничего не найдено", px + 12.0f, py + 12.0f, 8.0f, CosmeticCatalogScreen.sub(a));
            return;
        }
        Render2D.pushScissor(drawContext, px + 1.0f, py + 1.0f, pw - 2.0f, ph - 64.0f);
        CosmeticPreview.draw(cosmetic, px + pw * 0.5f, py + (ph - 68.0f) * 0.5f + 6.0f, 74.0f * this.zoom,
                this.yaw, this.pitch, (float)System.currentTimeMillis() / 1000.0f, a);
        Render2D.popScissor(drawContext);

        Render2D.msdfText(FONT_BOLD, cosmetic.name, px + 12.0f, py + ph - 58.0f, 10.0f, CosmeticCatalogScreen.ink(a));
        String slot = cosmetic.slotName() + " · " + cosmetic.setNameText() + (cosmetic.inSet() ? " · " + Cosmetics.setState(cosmetic.flavor) : "");
        Render2D.msdfText(FONT, slot, px + 12.0f, py + ph - 45.0f, 6.6f, CosmeticCatalogScreen.sub(a * 0.9f));
        Render2D.msdfText(FONT, cosmetic.description, px + 12.0f, py + ph - 34.0f, 6.6f, CosmeticCatalogScreen.sub(a * 0.8f));

        // кнопки действий
        float buttonY = py + ph - 18.0f;
        boolean available = Cosmetics.available(cosmetic);
        boolean equipped = Cosmetics.equipped(cosmetic);
        this.button(px + 12.0f, buttonY, 78.0f, equipped ? "Снять" : available ? "Надеть" : "Закрыто",
                equipped, available, a);
        float favoriteX = px + 12.0f + 84.0f;
        this.button(favoriteX, buttonY, 74.0f, Cosmetics.favorite(cosmetic) ? "★ В избранном" : "☆ В избранное", false, true, a);
        float claimX = favoriteX + 80.0f;
        if (cosmetic.inSet() && Cosmetics.seasonNow(cosmetic.flavor) && Cosmetics.ownedInSet(cosmetic.flavor) < 10) {
            this.button(claimX, buttonY, 96.0f, "Забрать набор", false, true, a);
        } else {
            this.button(claimX, buttonY, 96.0f, "Код выдачи", false, true, a);
        }
        String status = System.currentTimeMillis() < this.statusUntil ? this.status : "ЛКМ — выбрать · тяни в превью — поворот · колесо — размер";
        Render2D.msdfText(FONT, status, px + 12.0f, py + 8.0f, 6.4f,
                System.currentTimeMillis() < this.statusUntil ? CosmeticCatalogScreen.accentPulse(a) : CosmeticCatalogScreen.sub(a * 0.75f));
        if (this.codeMode) {
            Render2D.rect(CosmeticCatalogScreen.listX(), CosmeticCatalogScreen.panelY() + H - PAD - 20.0f,
                    CosmeticCatalogScreen.listW() + 12.0f + CosmeticCatalogScreen.previewW(), 20.0f, 5.0f,
                    CosmeticCatalogScreen.field(a));
            String text = this.code.length() == 0 ? "Вставь код BZGS1:… или JSON выдачи и нажми Enter" : this.code.toString();
            Render2D.msdfText(FONT, text, CosmeticCatalogScreen.listX() + 6.0f, CosmeticCatalogScreen.panelY() + H - PAD - 15.0f, 7.0f,
                    this.code.length() == 0 ? CosmeticCatalogScreen.sub(a * 0.8f) : CosmeticCatalogScreen.ink(a));
        }
    }

    private void button(float x, float y, float width, String label, boolean active, boolean enabled, float a) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        boolean hovered = enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 14.0f;
        if (active) {
            RectUtil.drawClientRect(x, y, width, 14.0f, 4.0f, a * 0.95f);
        } else {
            Render2D.rect(x, y, width, 14.0f, 4.0f, hovered ? CosmeticCatalogScreen.hover(a) : CosmeticCatalogScreen.field(a));
        }
        int color = enabled ? CosmeticCatalogScreen.ink(a) : CosmeticCatalogScreen.locked(a);
        Render2D.msdfText(FONT, label, x + 6.0f, y + 4.0f, 6.6f, color);
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        return delta;
    }

    /** Вращение превью: кнопка мыши опрашивается напрямую, потому что у Click нет «зажатия». */
    private void updateDrag(float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }
        long handle = client.getWindow().getHandle();
        boolean pressed = GLFW.glfwGetMouseButton(handle, 0) == 1;
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        boolean inside = mouseX >= CosmeticCatalogScreen.previewX() && mouseX <= CosmeticCatalogScreen.previewX() + CosmeticCatalogScreen.previewW()
                && mouseY >= CosmeticCatalogScreen.previewY() && mouseY <= CosmeticCatalogScreen.previewY() + CosmeticCatalogScreen.previewH() - 64.0f;
        if (pressed && inside) {
            if (this.dragging) {
                this.yaw += (mouseX - this.lastMouseX) * 0.42f;
                this.pitch = Math.max(-78.0f, Math.min(78.0f, this.pitch + (mouseY - this.lastMouseY) * 0.36f));
            }
            this.dragging = true;
        } else if (!pressed) {
            this.dragging = false;
        }
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        if (!this.dragging) {
            this.yaw += delta * 8.0f;
        }
    }

    private void setStatus(String text) {
        this.status = text;
        this.statusUntil = System.currentTimeMillis() + 2600L;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = CosmeticCatalogScreen.panelX();
        float y = CosmeticCatalogScreen.panelY();
        if (mouseX >= x + W - PAD - 12.0f && mouseX <= x + W - PAD + 6.0f && mouseY >= y + 8.0f && mouseY <= y + 24.0f) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        int setIndex = this.setAt(mouseX, mouseY);
        if (setIndex >= 0) {
            this.setFilter = SETS[setIndex];
            this.rebuild();
            Sounds.play("select_category");
            return true;
        }
        int row = this.rowAt(mouseX, mouseY);
        if (row >= 0) {
            this.selected = row;
            Sounds.play("select_category");
            return true;
        }
        Cosmetic cosmetic = this.current();
        if (cosmetic == null) {
            return true;
        }
        float px = CosmeticCatalogScreen.previewX();
        float py = CosmeticCatalogScreen.previewY();
        float pw = CosmeticCatalogScreen.previewW();
        float ph = CosmeticCatalogScreen.previewH();
        float buttonY = py + ph - 18.0f;
        if (this.hit(mouseX, mouseY, px + 12.0f, buttonY, 78.0f)) {
            this.toggleEquip(cosmetic);
            return true;
        }
        float favoriteX = px + 12.0f + 84.0f;
        if (this.hit(mouseX, mouseY, favoriteX, buttonY, 74.0f)) {
            boolean added = Cosmetics.toggleFavorite(cosmetic);
            CosmeticSounds.play("cosmetic_favorite", 0.75f);
            this.setStatus(added ? "Добавлено в избранное" : "Убрано из избранного");
            this.rebuild();
            return true;
        }
        float claimX = favoriteX + 80.0f;
        if (this.hit(mouseX, mouseY, claimX, buttonY, 96.0f)) {
            if (cosmetic.inSet() && Cosmetics.seasonNow(cosmetic.flavor) && Cosmetics.ownedInSet(cosmetic.flavor) < 10) {
                int granted = Cosmetics.claimSet(cosmetic.flavor);
                CosmeticSounds.playSet(cosmetic.flavor, 0.9f);
                this.setStatus("Набор «" + cosmetic.setNameText() + "»: забрано предметов — " + granted);
                this.rebuild();
            } else {
                this.codeMode = !this.codeMode;
                Sounds.play("settings_open");
                this.setStatus("Код выдачи: " + CosmeticGrants.example());
            }
            return true;
        }
        if (mouseX >= px && mouseX <= px + pw && mouseY >= py + ph - 26.0f && mouseY <= py + ph) {
            this.toggleEquip(cosmetic);
            return true;
        }
        if (mouseX < x || mouseX > x + W || mouseY < y || mouseY > y + H) {
            Sounds.play("settings_close");
            this.close();
        }
        return true;
    }

    private boolean hit(float mouseX, float mouseY, float x, float y, float width) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 14.0f;
    }

    private void toggleEquip(Cosmetic cosmetic) {
        if (!Cosmetics.available(cosmetic)) {
            this.setStatus("Предмет закрыт: он из набора «" + cosmetic.setNameText() + "» и выдаётся по коду");
            Sounds.play("command_error");
            return;
        }
        boolean equipped = Cosmetics.toggle(cosmetic);
        CosmeticSounds.playEquipped(equipped, 0.85f);
        this.setStatus(equipped ? "Надето: " + cosmetic.name : "Снято: " + cosmetic.name);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0.0) {
            return true;
        }
        boolean overPreview = mouseX >= CosmeticCatalogScreen.previewX() && mouseX <= CosmeticCatalogScreen.previewX() + CosmeticCatalogScreen.previewW()
                && mouseY >= CosmeticCatalogScreen.previewY() && mouseY <= CosmeticCatalogScreen.previewY() + CosmeticCatalogScreen.previewH();
        if (overPreview) {
            this.zoom = Math.max(0.55f, Math.min(1.9f, this.zoom + (verticalAmount > 0.0 ? 0.08f : -0.08f)));
            Sounds.play("slider");
            return true;
        }
        this.scroll = Math.max(0, Math.min(this.maxScroll(), this.scroll - (verticalAmount > 0.0 ? 1 : -1)));
        Sounds.play("slider");
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (this.codeMode) {
            if (key == 259 && this.code.length() > 0) {
                this.code.setLength(this.code.length() - 1);
                Sounds.play("search_typing");
                return true;
            }
            if (key == 257 || key == 335) {
                MinecraftClient client = MinecraftClient.getInstance();
                String text = this.code.toString();
                if (text.isBlank() && client != null) {
                    text = client.keyboard.getClipboard();
                }
                int applied = CosmeticGrants.applyCode(text);
                if (applied > 0) {
                    CosmeticSounds.play("cosmetic_claim_set", 0.9f);
                    this.setStatus("Выдача применена: новых предметов — " + applied);
                } else {
                    Sounds.play("command_error");
                    this.setStatus("Код не подошёл. Ожидается BZGS1:… или JSON выдачи");
                }
                this.code.setLength(0);
                this.codeMode = false;
                this.rebuild();
                return true;
            }
            if (key == 256) {
                this.codeMode = false;
                return true;
            }
            return true;
        }
        if (key == 256) {
            Sounds.play("settings_close");
            this.close();
            return true;
        }
        if (key == 264) {
            this.selected = Math.min(this.items.size() - 1, this.selected + 1);
            this.scroll = Math.max(0, Math.min(this.maxScroll(), this.selected - this.rowsVisible() + 1));
            Sounds.play("slider");
            return true;
        }
        if (key == 265) {
            this.selected = Math.max(0, this.selected - 1);
            this.scroll = Math.min(this.scroll, this.selected);
            Sounds.play("slider");
            return true;
        }
        if (key == 257 || key == 335) {
            this.toggleEquip(this.current());
            return true;
        }
        if (key == 70) {
            Cosmetic cosmetic = this.current();
            if (cosmetic != null) {
                Cosmetics.toggleFavorite(cosmetic);
                CosmeticSounds.play("cosmetic_favorite", 0.75f);
                this.rebuild();
            }
            return true;
        }
        if (key == 67 && CosmeticCatalogScreen.ctrlDown()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.keyboard.setClipboard(CosmeticGrants.exportCode());
                this.setStatus("Код со своими предметами скопирован в буфер");
                Sounds.play("module_settings_close");
            }
            return true;
        }
        if (key == 65 && CosmeticCatalogScreen.ctrlDown()) {
            this.codeMode = true;
            this.code.setLength(0);
            this.setStatus("Вставь код (Ctrl+V в поле) и нажми Enter");
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.codeMode) {
            String typed = new String(Character.toChars(input.codepoint()));
            if (this.code.length() < 4096) {
                this.code.append(typed);
            }
            return true;
        }
        return true;
    }

    private static boolean ctrlDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        long handle = client.getWindow().getHandle();
        return GLFW.glfwGetKey(handle, 341) == 1 || GLFW.glfwGetKey(handle, 345) == 1;
    }

    private static String seasonLine() {
        StringBuilder builder = new StringBuilder();
        for (CosmeticRegistry.Set set : CosmeticRegistry.sets()) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(set.name).append(Cosmetics.seasonNow(set.flavor) ? " идёт" : " закрыт");
        }
        return builder.toString();
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    private static int ink(float alpha) {
        return CosmeticCatalogScreen.rgba(233, 238, 248, 240.0f * alpha);
    }

    private static int sub(float alpha) {
        return CosmeticCatalogScreen.rgba(168, 176, 194, 230.0f * alpha);
    }

    private static int locked(float alpha) {
        return CosmeticCatalogScreen.rgba(126, 132, 148, 210.0f * alpha);
    }

    private static int field(float alpha) {
        return CosmeticCatalogScreen.rgba(16, 18, 24, 190.0f * alpha);
    }

    private static int listBackground(float alpha) {
        return CosmeticCatalogScreen.rgba(14, 16, 21, 165.0f * alpha);
    }

    private static int hover(float alpha) {
        return CosmeticCatalogScreen.rgba(255, 255, 255, 24.0f * alpha);
    }

    private static int selected(float alpha) {
        return CosmeticCatalogScreen.rgba(255, 255, 255, 40.0f * alpha);
    }

    private static int lock(float alpha) {
        return CosmeticCatalogScreen.rgba(8, 9, 12, 170.0f * alpha);
    }

    private static int accentPulse(float alpha) {
        float wave = 0.6f + 0.4f * (float)Math.sin((double)System.currentTimeMillis() / 420.0);
        return ClientAccent.accent(Math.round(200.0f * alpha * wave));
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(null);
        }
    }
}
