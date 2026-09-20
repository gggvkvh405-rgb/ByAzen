package rtx.byazen.api.modules.impl.Visuals;
import java.awt.Color;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.color.ColorUtil;
import rtx.byazen.utils.inventory.SlotCategory;
import rtx.byazen.utils.inventory.SlotItem;
import rtx.byazen.utils.render.render2d.Render2D;

public class ItemHighlight
extends Module {
    private static ItemHighlight instance;
    private final Map<SlotItem, ColorSetting> itemColors = new EnumMap<SlotItem, ColorSetting>(SlotItem.class);
    private final Map<Item, ItemHighlight.ItemEntry> itemEntries = new LinkedHashMap<Item, ItemHighlight.ItemEntry>();
    private final SliderSetting opacity = this.register(new SliderSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", "\u041d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0444\u043e\u043d\u0430.").range(0, 100).increment(1).setValue(80.0f));

    public ItemHighlight() {
        super("ItemHighlight", "\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0444\u043e\u043d \u044f\u0447\u0435\u0435\u043a \u0434\u043b\u044f \u043d\u0443\u0436\u043d\u044b\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432.", Category.VISUALS);
        instance = this;
        for (SlotItem slotItem : SlotItem.values()) {
            ColorSetting colorSetting = this.register(new ColorSetting(slotItem.getDisplayName(), "\u0426\u0432\u0435\u0442 \u0444\u043e\u043d\u0430 \u0434\u043b\u044f \u00ab" + slotItem.getDisplayName() + "\u00bb.", new Color(slotItem.getDefaultColor() | 0xFF000000, true)));
            this.itemColors.put(slotItem, colorSetting);
        }
        this.register(new SeparatorSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b"));
        this.addItem(Items.ENDER_PEARL, "\u042d\u043d\u0434\u0435\u0440 \u043f\u0451\u0440\u043b", new Color(0, 150, 0));
        this.addItem(Items.SNOWBALL, "\u0421\u043d\u0435\u0436\u043e\u043a", new Color(100, 200, 255));
        this.addItem(Items.NETHERITE_SCRAP, "\u0422\u0440\u0430\u043f\u043a\u0430", new Color(200, 180, 150));
        this.addItem(Items.LANTERN, "\u0421\u0432\u0435\u0442\u0438\u043b\u044c\u043d\u0438\u043a", new Color(255, 150, 0));
        this.addItem(Items.FIRE_CHARGE, "\u0412\u0437\u0440\u044b\u0432\u043d\u0430\u044f \u0442", new Color(255, 0, 0));
        this.addItem(Items.TOTEM_OF_UNDYING, "\u0422\u043e\u0442\u0435\u043c", new Color(255, 200, 0));
        this.addItem(Items.CROSSBOW, "\u0410\u0440\u0431\u0430\u043b\u0435\u0442", new Color(150, 100, 50));
        this.addItem(Items.NETHERITE_SWORD, "\u041d\u0435\u0437\u0435\u0440\u0438\u0442\u043e\u0432\u044b\u0439 \u043c\u0435\u0447", new Color(50, 50, 50));
        this.addItem(Items.CHORUS_FRUIT, "\u0425\u043e\u0440\u0443\u0441", new Color(200, 100, 200));
        this.addItem(Items.SUGAR, "\u0421\u0430\u0445\u0430\u0440", new Color(255, 255, 255));
        this.addItem(Items.PHANTOM_MEMBRANE, "\u041c\u0435\u043c\u0431\u0440\u0430\u043d\u0430", new Color(200, 180, 150));
        this.addItem(Items.ENDER_EYE, "\u041e\u043a\u043e \u044d\u043d\u0434\u0435\u0440\u0430", new Color(100, 0, 200));
        this.addItem(Items.DRIED_KELP, "\u041b\u0430\u043c\u0438\u043d\u0430\u0440\u0438\u044f", new Color(100, 150, 50));
        this.addItem(Items.EXPERIENCE_BOTTLE, "\u041f\u0443\u0437\u044b\u0440\u0435\u043a \u043e\u043f\u044b\u0442\u0430", new Color(0, 200, 100));
        this.addItem(Items.GOLDEN_APPLE, "\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", new Color(255, 200, 0));
        this.addItem(Items.ENCHANTED_GOLDEN_APPLE, "\u0427\u0430\u0440. \u044f\u0431\u043b\u043e\u043a\u043e", new Color(255, 150, 0));
    }

    public static ItemHighlight getInstance() {
        ItemHighlight itemHighlight = ModuleManager.get().get(ItemHighlight.class);
        return itemHighlight != null ? itemHighlight : instance;
    }

    public int backgroundFor(ItemStack itemStack, boolean bl) {
        if (!this.isEnabled() || itemStack == null || itemStack.isEmpty()) {
            return 0;
        }
        int n = this.potionBackground(itemStack);
        if (n != 0) {
            return n;
        }
        ItemHighlight.ItemEntry itemEntry = this.itemEntries.get(itemStack.getItem());
        if (itemEntry == null || !itemEntry.enabled().getValue()) {
            return 0;
        }
        int n2 = itemEntry.color().getColorOpaque() & 0xFFFFFF;
        return this.withOpacity(n2, itemEntry.color().getAlpha());
    }

    public void drawSlotBackground(DrawContext drawContext, int n, int n2, int n3) {
        if (drawContext == null || n3 == 0) {
            return;
        }
        drawContext.fill(n, n2, n + 16, n2 + 16, n3);
    }

    private void addItem(Item item, String string, Color color) {
        BooleanSetting booleanSetting = this.register(new BooleanSetting(string, "\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u00ab" + string + "\u00bb.", true));
        ColorSetting colorSetting = this.register(new ColorSetting(string + " \u0446\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u0444\u043e\u043d\u0430 \u0434\u043b\u044f \u00ab" + string + "\u00bb.", new Color(color.getRGB() | 0xFF000000, true)).visibleWhen(() -> booleanSetting.getValue()));
        this.itemEntries.put(item, new ItemHighlight.ItemEntry(booleanSetting, colorSetting));
    }

    private int potionBackground(ItemStack itemStack) {
        float f;
        int n;
        SlotCategory slotCategory = SlotCategory.classify((ItemStack)itemStack);
        if (slotCategory == null) {
            return 0;
        }
        SlotItem slotItem = SlotItem.match(itemStack, slotCategory.getItemGroup());
        if (slotItem == null) {
            return 0;
        }
        ColorSetting colorSetting = this.itemColors.get((Object)slotItem);
        if (colorSetting != null) {
            n = colorSetting.getColorOpaque() & 0xFFFFFF;
            f = colorSetting.getAlpha();
        } else {
            n = slotCategory.getDefaultColor() & 0xFFFFFF;
            f = 1.0f;
        }
        return this.withOpacity(n, f);
    }

    private int withOpacity(int n, float f) {
        float f2 = Math.max(0.0f, Math.min(1.0f, this.opacity.getFloat() / 100.0f));
        int n2 = Math.round(255.0f * f * f2);
        if (n2 <= 0) {
            return 0;
        }
        return n2 << 24 | n;
    }

    public void drawRoundedSlotBackground(float f, float f2, float f3, int n, float f4) {
        if (n == 0 || f3 <= 0.0f || f4 <= 0.0f) {
            return;
        }
        InterfaceModule interfaceModule = InterfaceModule.getInstance();
        float f5 = interfaceModule == null ? 2.0f : interfaceModule.rectCornerRadius.getFloat();
        f5 = Math.min(f5, f3 * 0.5f);
        this.drawRoundedSlotBackground(f, f2, f3, n, f4, f5, f5, f5, f5);
    }

    public void drawRoundedSlotBackground(float f, float f2, float f3, int n, float f4, float f5, float f6, float f7, float f8) {
        if (n == 0 || f3 <= 0.0f || f4 <= 0.0f) {
            return;
        }
        float f9 = f3 * 0.5f;
        Render2D.rect(f, f2, f3, f3, Math.min(f5, f9), Math.min(f6, f9), Math.min(f7, f9), Math.min(f8, f9), ColorUtil.multAlpha(n, f4));
    }


    public static record ItemEntry(BooleanSetting enabled, ColorSetting color) {
    }
}

