package rtx.byazen.api.modules.impl.Interface;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.input.KeyPressEvent;
import rtx.byazen.api.events.impl.inventory.ClickSlotEvent;
import rtx.byazen.api.events.impl.inventory.HandledScreenEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.inventory.InventorySorter;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.render.render2d.Render2DCoordinateSpace;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Кастомный инвентарь 2.0 (идея №69 из IDEAS.md).
 * <p>
 * Добавляет к обычному инвентарю то, чего в нём всегда не хватает: раскладку предметов по
 * категориям, подсветку нужной группы, мягкое затемнение лишнего, счётчики предметов и избранные
 * ячейки с акцентной рамкой. Всё рисуется плавными скруглёнными формами поверх ванильных слотов.
 */
public final class InventoryPlus
extends Module {

    private static final String FILTER_ALL = "Всё";
    private static final String FILTER_WEAPONS = "Оружие";
    private static final String FILTER_TOOLS = "Инструменты";
    private static final String FILTER_ARMOR = "Броня";
    private static final String FILTER_FOOD = "Еда";
    private static final String FILTER_BLOCKS = "Блоки";
    private static final String FILTER_RESOURCES = "Ресурсы";
    private static final String FILTER_CONTAINERS = "Контейнеры";

    private static final String FONT = "montserrat-semibold";
    private static final float SLOT_SIZE = 16.0f;

    private final SeparatorSetting sortSeparator = this.register(new SeparatorSetting("Сортировка"));
    public final ModeSetting sortMode = this.register(new ModeSetting("Способ", "Как раскладывать предметы при сортировке.", InventorySorter.MODE_CATEGORY,
            InventorySorter.MODE_CATEGORY, InventorySorter.MODE_NAME, InventorySorter.MODE_COUNT));
    public final BooleanSetting sortHotbar = this.register(new BooleanSetting("Сортирую хотбар", "Раскладывать вместе с ячейками хотбара.", false));
    public final BooleanSetting sortSound = this.register(new BooleanSetting("Звук сортировки", "Короткий щелчок, когда раскладка началась.", true));

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Подсветка"));
    public final ModeSetting filter = this.register(new ModeSetting("Фильтр", "Какую группу предметов подсвечивать.", FILTER_ALL,
            FILTER_ALL, FILTER_WEAPONS, FILTER_TOOLS, FILTER_ARMOR, FILTER_FOOD, FILTER_BLOCKS, FILTER_RESOURCES, FILTER_CONTAINERS));
    public final BooleanSetting dimOthers = this.register(new BooleanSetting("Затемнять лишнее", "Мягко затемнять ячейки, которые не подходят под фильтр.", true));
    public final SliderSetting dimStrength = this.register(new SliderSetting("Сила затемнения, %", "Насколько сильно темнить неподходящие ячейки.", 55.0f, 15.0f, 85.0f, 5.0f)).visible(this.dimOthers::getValue);
    public final ColorSetting markColor = this.register(new ColorSetting("Цвет рамки", "Цвет акцентной рамки вокруг подходящих ячеек.", new java.awt.Color(120, 200, 255, 200)));

    private final SeparatorSetting searchSeparator = this.register(new SeparatorSetting("Поиск"));
    public final BooleanSetting search = this.register(new BooleanSetting("Поиск по названию", "Искать предметы по имени: печатайте прямо в инвентаре, Backspace — стереть.", true));
    public final BooleanSetting searchLatin = this.register(new BooleanSetting("Латинские буквы и цифры", "Печатать в поиск только латиницу, цифры и подчёркивания (как в идентификаторах предметов).", true));

    private final SeparatorSetting extraSeparator = this.register(new SeparatorSetting("Мелочи"));
    public final BooleanSetting counters = this.register(new BooleanSetting("Счётчики предметов", "Писать в углу ячейки, сколько таких предметов всего в инвентаре.", true));
    public final BooleanSetting favorites = this.register(new BooleanSetting("Избранные ячейки", "Alt + ЛКМ помечает ячейку акцентной рамкой.", true));
    public final BooleanSetting hoverHint = this.register(new BooleanSetting("Подсказка на ячейке", "Показывать подсказку под курсором: сколько предметов всего и как добавить в избранное.", true));

    private final Set<Integer> favoriteSlots = new LinkedHashSet<Integer>();
    private final StringBuilder query = new StringBuilder();
    private boolean typing;

    public InventoryPlus() {
        super("InventoryPlus", "Кастомный инвентарь: сортировка, фильтр с подсветкой, счётчики предметов и избранные ячейки.", Category.DISPLAY);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        this.favoriteSlots.clear();
        this.query.setLength(0);
        this.typing = false;
        InventorySorter.cancel();
    }

    public static InventoryPlus getInstance() {
        return ModuleManager.get().get(InventoryPlus.class);
    }

    public static boolean isActive() {
        InventoryPlus module = InventoryPlus.getInstance();
        return module != null && module.isEnabled();
    }

    public static String sortModeName() {
        InventoryPlus module = InventoryPlus.getInstance();
        return module == null ? InventorySorter.MODE_CATEGORY : module.sortMode.getValue();
    }

    public static boolean sortingHotbar() {
        InventoryPlus module = InventoryPlus.getInstance();
        return module != null && module.sortHotbar.getValue();
    }

    /** Запускает раскладку из обычного инвентаря игрока. */
    public static void triggerSort(PlayerScreenHandler handler) {
        if (handler == null) {
            return;
        }
        InventorySorter.plan(handler, InventoryPlus.sortModeName(), InventoryPlus.sortingHotbar());
        InventoryPlus module = InventoryPlus.getInstance();
        if (module != null && module.sortSound.getValue() && InventorySorter.isActive()) {
            Sounds.play("select_category");
        }
    }

    @EventHandler
    public void onKey(KeyPressEvent keyPressEvent) {
        if (!this.isEnabled() || !this.search.getValue() || keyPressEvent.action != KeyPressEvent.Action.PRESS) {
            return;
        }
        if (!(this.mc.currentScreen instanceof HandledScreen<?>)) {
            this.typing = false;
            return;
        }
        int key = keyPressEvent.keyCode;
        if (key == 259) {
            if (this.query.length() > 0) {
                this.query.setLength(this.query.length() - 1);
                this.typing = true;
            }
            return;
        }
        if (key == 256 || key == 257) {
            this.query.setLength(0);
            this.typing = false;
            return;
        }
        if (key == 27) {
            if (this.query.length() > 0) {
                this.query.setLength(0);
                this.typing = false;
                keyPressEvent.cancel();
            }
            return;
        }
        if (!this.searchLatin.getValue()) {
            return;
        }
        char letter = InventoryPlus.letterOf(key);
        if (letter != 0 && this.query.length() < 20) {
            this.query.append(letter);
            this.typing = true;
        }
    }

    private static char letterOf(int key) {
        if (key >= 65 && key <= 90) {
            return (char)(key + 32);
        }
        if (key >= 48 && key <= 57) {
            return (char)key;
        }
        if (key == 32 || key == 45) {
            return '_';
        }
        return (char)0;
    }

    @EventHandler
    public void onClickSlot(ClickSlotEvent clickSlotEvent) {
        if (!this.isEnabled() || !this.favorites.getValue() || this.mc.player == null) {
            return;
        }
        if (!InventoryPlus.altDown() || clickSlotEvent.getActionType() != SlotActionType.PICKUP || clickSlotEvent.getButton() != 0) {
            return;
        }
        int slotId = clickSlotEvent.getSlotId();
        if (slotId < 0 || this.mc.player.currentScreenHandler == null || slotId >= this.mc.player.currentScreenHandler.slots.size()) {
            return;
        }
        if (!(this.mc.player.currentScreenHandler.slots.get(slotId).inventory instanceof PlayerInventory)) {
            return;
        }
        if (!this.favoriteSlots.remove(slotId)) {
            this.favoriteSlots.add(slotId);
        }
        clickSlotEvent.cancel();
        Sounds.play("select_category");
    }

    private static boolean altDown() {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return false;
        }
        long window = client.getWindow().getHandle();
        return GLFW.glfwGetKey(window, 342) == 1 || GLFW.glfwGetKey(window, 346) == 1;
    }

    @EventHandler
    public void onHandledScreen(HandledScreenEvent handledScreenEvent) {
        if (!this.isEnabled() || this.mc.player == null || this.mc.player.currentScreenHandler == null) {
            return;
        }
        DrawContext drawContext = handledScreenEvent.getDrawContext();
        if (drawContext == null) {
            return;
        }
        InventorySorter.tick();
        List<Slot> slots = this.mc.player.currentScreenHandler.slots;
        String needle = this.search.getValue() ? this.query.toString().toLowerCase(Locale.ROOT) : "";
        List<Slot> matched = new ArrayList<Slot>();
        for (Slot slot : slots) {
            if (!(slot.inventory instanceof PlayerInventory)) {
                continue;
            }
            if (this.matches(slot.getStack(), needle)) {
                matched.add(slot);
            }
        }
        float scale = (float)Render2DCoordinateSpace.guiScale() / (float)Render2DCoordinateSpace.designGuiScale();
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(scale);
        Render2D.beginFrame(drawContext);
        this.drawDimming(handledScreenEvent, slots, matched, needle);
        this.drawMarks(handledScreenEvent, matched, slots);
        if (this.counters.getValue()) {
            this.drawCounters(handledScreenEvent, slots);
        }
        if (this.hoverHint.getValue()) {
            this.drawHoverHint(handledScreenEvent, slots);
        }
        if (this.search.getValue() && (this.typing || this.query.length() > 0)) {
            this.drawSearchBar(handledScreenEvent);
        }
        Render2D.flush();
        drawContext.getMatrices().popMatrix();
    }

    private void drawDimming(HandledScreenEvent event, List<Slot> slots, List<Slot> matched, String needle) {
        if (!this.dimOthers.getValue() || needle.length() == 0 && FILTER_ALL.equals(this.filter.getValue())) {
            return;
        }
        float strength = this.dimStrength.getValue() / 100.0f;
        int dim = InventoryPlus.rgba(4, 6, 11, 180.0f * strength);
        for (Slot slot : slots) {
            if (!(slot.inventory instanceof PlayerInventory) || matched.contains(slot)) {
                continue;
            }
            Render2D.rect(event.getX() + (float)slot.x, event.getY() + (float)slot.y, SLOT_SIZE, SLOT_SIZE, 4.0f, dim);
        }
    }

    private void drawMarks(HandledScreenEvent event, List<Slot> matched, List<Slot> slots) {
        boolean filtering = !FILTER_ALL.equals(this.filter.getValue()) || this.query.length() > 0;
        if (filtering) {
            int color = InventoryPlus.withAlpha(this.markColor.getValue(), 130.0f);
            for (Slot slot : matched) {
                this.outlineSlot(event, slot, 1.0f, color);
            }
        }
        if (!this.favorites.getValue() || this.favoriteSlots.isEmpty()) {
            return;
        }
        int accent = InventoryPlus.withAlpha(ClientAccent.accentOpaque(), 235.0f);
        for (int index : this.favoriteSlots) {
            if (index < 0 || index >= slots.size()) {
                continue;
            }
            this.outlineSlot(event, slots.get(index), 1.2f, accent);
        }
    }

    private void outlineSlot(HandledScreenEvent event, Slot slot, float thickness, int color) {
        Render2D.outline(event.getX() + (float)slot.x - 0.5f, event.getY() + (float)slot.y - 0.5f,
                SLOT_SIZE + 1.0f, SLOT_SIZE + 1.0f, 4.0f, thickness, color);
    }

    private void drawCounters(HandledScreenEvent event, List<Slot> slots) {
        for (Slot slot : slots) {
            if (!(slot.inventory instanceof PlayerInventory)) {
                continue;
            }
            ItemStack stack = slot.getStack();
            if (stack == null || stack.isEmpty()) {
                continue;
            }
            int total = InventoryPlus.countOf(this.mc.player, stack);
            if (total <= stack.getCount()) {
                continue;
            }
            String label = Integer.toString(total);
            float width = Render2D.msdfWidth(FONT, label, 4.6f);
            float bx = event.getX() + (float)slot.x + SLOT_SIZE - width - 2.5f;
            float by = event.getY() + (float)slot.y + SLOT_SIZE - 7.0f;
            Render2D.rect(bx - 1.5f, by - 1.0f, width + 3.0f, 6.5f, 3.0f, InventoryPlus.rgba(8, 10, 15, 195.0f));
            Render2D.msdfText(FONT, label, bx, by, 4.6f, InventoryPlus.rgba(226, 233, 245, 240.0f));
        }
    }

    private void drawHoverHint(HandledScreenEvent event, List<Slot> slots) {
        Slot hovered = event.getSlotHover();
        if (hovered == null || !(hovered.inventory instanceof PlayerInventory)) {
            return;
        }
        ItemStack stack = hovered.getStack();
        String text;
        if (stack == null || stack.isEmpty()) {
            text = this.favorites.getValue() ? "Alt + ЛКМ — в избранное" : "";
        } else {
            int total = InventoryPlus.countOf(this.mc.player, stack);
            text = total > 1 ? "Всего: " + total : "";
            if (this.favorites.getValue()) {
                text = text.isEmpty() ? "Alt + ЛКМ — в избранное" : text + "  ·  Alt + ЛКМ — в избранное";
            }
        }
        if (text.isEmpty()) {
            return;
        }
        this.drawHint(event, text);
    }

    private void drawHint(HandledScreenEvent event, String text) {
        float size = 5.4f;
        float width = Render2D.msdfWidth(FONT, text, size) + 9.0f;
        float x = event.getX() + 4.0f;
        float y = event.getY() - 12.0f;
        Render2D.rect(x, y, width, 10.0f, 5.0f, InventoryPlus.rgba(9, 11, 17, 208.0f));
        Render2D.outline(x, y, width, 10.0f, 5.0f, 1.0f, ClientAccent.accentSoft(95.0f));
        Render2D.msdfText(FONT, text, x + 4.5f, y + 2.6f, size, InventoryPlus.rgba(220, 228, 242, 235.0f));
    }

    private void drawSearchBar(HandledScreenEvent event) {
        String text = this.query.length() == 0 ? "Поиск: начните печатать" : "Поиск: " + this.query.toString();
        float size = 5.6f;
        float width = 124.0f;
        float x = Math.max(4.0f, event.getX() - width - 6.0f);
        float y = event.getY();
        Render2D.rect(x, y, width, 13.5f, 6.5f, InventoryPlus.rgba(10, 12, 18, 205.0f));
        Render2D.outline(x, y, width, 13.5f, 6.5f, 1.0f, ClientAccent.accentSoft(115.0f));
        Render2D.msdfText(FONT, text, x + 5.0f, y + 3.8f, size,
                this.query.length() == 0 ? InventoryPlus.rgba(150, 158, 176, 220.0f) : InventoryPlus.rgba(228, 234, 246, 245.0f));
    }

    private boolean matches(ItemStack stack, String needle) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        String activeFilter = this.filter.getValue();
        if (needle.length() > 0) {
            String id = InventoryPlus.idOf(stack);
            String name = stack.getName().getString().toLowerCase(Locale.ROOT);
            if (!id.contains(needle) && !name.contains(needle)) {
                return false;
            }
        }
        if (FILTER_ALL.equals(activeFilter)) {
            return true;
        }
        int group = InventorySorter.groupOf(stack);
        switch (activeFilter) {
            case FILTER_WEAPONS: {
                return group == 0;
            }
            case FILTER_TOOLS: {
                return group == 1;
            }
            case FILTER_ARMOR: {
                return group == 2;
            }
            case FILTER_FOOD: {
                return group == 3;
            }
            case FILTER_BLOCKS: {
                return group == 6;
            }
            case FILTER_RESOURCES: {
                return group == 8;
            }
            case FILTER_CONTAINERS: {
                return group == 7;
            }
            default: {
                return true;
            }
        }
    }

    private static String idOf(ItemStack stack) {
        try {
            return Registries.ITEM.getId(stack.getItem()).getPath();
        }
        catch (Exception exception) {
            return "";
        }
    }

    /** Сколько предметов одного вида лежит в инвентаре игрока. */
    public static int countOf(PlayerEntity player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack other = player.getInventory().getStack(i);
            if (other != null && !other.isEmpty() && other.getItem() == stack.getItem()) {
                total += other.getCount();
            }
        }
        return total;
    }

    private static int rgba(int red, int green, int blue, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    private static int withAlpha(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a << 24 | color & 0xFFFFFF;
    }
}
