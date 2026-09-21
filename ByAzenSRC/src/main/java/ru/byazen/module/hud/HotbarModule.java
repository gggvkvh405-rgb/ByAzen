/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1041
 *  net.minecraft.class_1268
 *  net.minecraft.class_1306
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1934
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 */
package ru.byazen.module.hud;

import java.util.ArrayList;
import net.minecraft.class_1041;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1934;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.HudRenderEvent;
import ru.byazen.misc.BakedIconEntry;
import ru.byazen.misc.HotbarSlotRenderer;
import ru.byazen.misc.ThemeColors;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.render.BakedItemIcon;
import ru.byazen.render.ItemIconCache;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public class HotbarModule
extends Module
implements ConfigSerializable {
    private static final float HOTBAR_WIDTH = 180.0f;
    private static final float SLOT_SIZE = 20.0f;
    private static final float ICON_SIZE = 16.0f;
    private static final float SLOT_DEPTH = 10.0f;
    private static final float BOTTOM_MARGIN = 3.0f;
    private static final float HOTBAR_HEIGHT = 20.0f;
    private static final float OFFHAND_GAP = 4.0f;
    private static final int HOTBAR_SLOTS = 9;
    static volatile HotbarModule hotbarModule2;
    private final BooleanSetting enabledSetting;
    private final HotbarSlotRenderer offhandRenderer;
    private final ItemIconCache iconCache;
    private final HotbarSlotRenderer[] slotRenderers;

    public HotbarModule(EventBus eventBus) {
        super(eventBus, "hotbar", "Hotbar", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u0445\u043e\u0442\u0431\u0430\u0440", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        hotbarModule2 = this;
        this.iconCache = new ItemIconCache();
        this.offhandRenderer = new HotbarSlotRenderer(0);
        this.slotRenderers = new HotbarSlotRenderer[9];
        for (int slot = 0; slot < 9; ++slot) {
            this.slotRenderers[slot] = new HotbarSlotRenderer(slot);
        }
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u0445\u043e\u0442\u0431\u0430\u0440").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(HudRenderEvent.class, event -> this.render());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void render() {
        if (!HotbarModule.isEnabled()) {
            return;
        }
        class_746 player = HotbarModule.getPlayer();
        GuiDrawApi renderer = ByAzenClient.getHudRenderer();
        if (player == null || renderer == null) {
            return;
        }
        class_1661 inventory = player.method_31548();
        int selectedSlot = inventory.method_67532();
        class_1799 offhandStack = player.method_6079();
        boolean hasOffhand = !offhandStack.method_7960();
        class_1268 activeHand = player.method_6115() ? player.method_6058() : null;
        class_1041 window = class_310.method_1551().method_22683();
        float hotbarX = (float)window.method_4486() / 2.0f - 90.0f;
        float hotbarY = (float)window.method_4502() - 3.0f - 20.0f;
        boolean offhandOnLeft = player.method_6068() == class_1306.field_6183;
        float offhandX = offhandOnLeft ? hotbarX - 4.0f - 20.0f : hotbarX + 180.0f + 4.0f;
        this.iconCache.update2();
        BakedItemIcon[] slotIcons = new BakedItemIcon[9];
        for (int slot = 0; slot < 9; ++slot) {
            slotIcons[slot] = this.iconCache.process(inventory.method_5438(slot));
        }
        BakedItemIcon offhandIcon = hasOffhand ? this.iconCache.process(offhandStack) : null;
        ArrayList<BakedIconEntry> queuedDraws = new ArrayList<BakedIconEntry>();
        this.iconCache.process2(2.0f, queuedDraws);
        if (!queuedDraws.isEmpty()) {
            ByAzenClient.getRenderPipeline2().setList(queuedDraws);
        }
        Matrix4f matrix = new Matrix4f().scale(2.0f);
        renderer.begin();
        try {
            this.drawSlotBackground(renderer, matrix, hotbarX, hotbarY, 180.0f);
            for (int slot = 0; slot < 9; ++slot) {
                float slotX = hotbarX + (float)slot * 20.0f;
                this.slotRenderers[slot].process(renderer, matrix, this.iconCache, slotIcons[slot], inventory.method_5438(slot), slotX, hotbarY, 20.0f, 16.0f, 10.0f, slot == selectedSlot, activeHand == class_1268.field_5808 && slot == selectedSlot);
            }
            if (hasOffhand) {
                this.drawSlotBackground(renderer, matrix, offhandX, hotbarY, 20.0f);
                this.offhandRenderer.process(renderer, matrix, this.iconCache, offhandIcon, offhandStack, offhandX, hotbarY, 20.0f, 16.0f, 10.0f, false, activeHand == class_1268.field_5810);
            }
        }
        finally {
            renderer.end();
            this.iconCache.update();
        }
    }

    public static boolean isEnabled() {
        HotbarModule module = hotbarModule2;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        class_310 client = class_310.method_1551();
        class_315 options = client.field_1690;
        if (options == null || options.field_1842) {
            return false;
        }
        if (client.field_1761 != null && client.field_1761.method_2920() == class_1934.field_9219) {
            return false;
        }
        return HotbarModule.getPlayer() != null;
    }

    public static float getFloatType() {
        if (!HotbarModule.isEnabled()) {
            return 0.0f;
        }
        float scale = class_310.method_1551().method_22683().method_4495();
        return 46.0f / scale - 22.0f;
    }

    private static class_746 getPlayer() {
        return class_310.method_1551().field_1724;
    }

    private void drawSlotBackground(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width) {
        renderer.drawRoundedRectangle(matrix, x, y, width, 20.0f, 10.0f, ColorUtils.multiplyAlpha(ThemeColors.visualizerSlot(), 1.0f));
    }
}

