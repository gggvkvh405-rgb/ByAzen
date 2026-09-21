/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1542
 *  net.minecraft.class_1747
 *  net.minecraft.class_1767
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2248
 *  net.minecraft.class_243
 *  net.minecraft.class_2480
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_7923
 *  net.minecraft.class_9288
 *  net.minecraft.class_9334
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 */
package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_7923;
import net.minecraft.class_9288;
import net.minecraft.class_9334;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.ItemHoverEvent;
import ru.wexside.event.TooltipRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class ShulkerViewerModule
extends Module
implements ConfigSerializable {
    private static final float PANEL_WIDTH = 172.0f;
    private static final int DEFAULT_TINT = 9068428;
    private static final int COLUMNS = 9;
    private static volatile ShulkerViewerModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting onGround;
    private final BooleanSetting onGroundTexture;
    private final BindSetting viewKey;
    private final ItemIconCache icons = new ItemIconCache();
    private class_1799 hoveredShulker;

    public ShulkerViewerModule(EventBus eventBus) {
        super(eventBus, "shulker_viewer", "Shulker Viewer", "\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u043c\u043e\u0433\u043e \u0448\u0430\u043b\u043a\u0435\u0440\u0430 ", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u043c\u043e\u0433\u043e \u0448\u0430\u043b\u043a\u0435\u0440\u043e\u0432").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.onGround = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("OnGround").id("on_ground").description("\u041f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0448\u0430\u043b\u043a\u0435\u0440\u043e\u0432, \u043b\u0435\u0436\u0430\u0449\u0438\u0445 \u0432 \u043c\u0438\u0440\u0435")).build();
        this.registerSetting(this.onGround);
        this.onGroundTexture = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Texture").id("on_ground_texture").description("\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u043d\u0430\u044f \u0440\u0430\u043c\u043a\u0430 \u043f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 \u043d\u0430 \u0437\u0435\u043c\u043b\u0435").aliases("texture", "\u0442\u0435\u043a\u0441\u0442\u0443\u0440\u0430").visibleWhen(this.onGround::isEnabled)).build();
        this.registerSetting(this.onGroundTexture);
        this.viewKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("View key").id("view_key").description("\u0423\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0439 \u043a\u043b\u0430\u0432\u0438\u0448\u0443, \u0447\u0442\u043e\u0431\u044b \u0443\u0432\u0438\u0434\u0435\u0442\u044c \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u043c\u043e\u0435 \u043d\u0430\u0432\u0435\u0434\u0451\u043d\u043d\u043e\u0433\u043e \u0448\u0430\u043b\u043a\u0435\u0440\u0430").aliases("view key", "\u043a\u043b\u0430\u0432\u0438\u0448\u0430 \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430")).build();
        this.registerSetting(this.viewKey);
    }

    @Override
    protected void initialize() {
        this.listen(ItemHoverEvent.class, this::onItemHover);
        this.listen(TooltipRenderEvent.class, this::onTooltip);
        this.listen(HudRenderEvent.class, this::onHudRender);
        this.listen(WorldSessionEvent.class, event -> this.icons.update3());
    }

    public static boolean compute(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return false;
        }
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        String path = id == null ? "" : id.method_12832();
        return path.contains("shulker_box");
    }

    public static String getString() {
        ShulkerViewerModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled() || module.viewKey.getBindInput().isUnbound()) {
            return null;
        }
        return module.viewKey.getKeyDisplayName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHudRender(HudRenderEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.onGround.isEnabled()) {
            this.icons.update3();
            return;
        }
        class_310 client = class_310.method_1551();
        class_243 cameraPos = client.field_1773.method_19418().method_71156();
        if (client.field_1687 == null || client.field_1724 == null || cameraPos == null) {
            return;
        }
        ArrayList<GroundPreview> previews = new ArrayList<GroundPreview>();
        for (class_1297 entity : client.field_1687.method_18112()) {
            Vector2f screen;
            List<class_1799> contents;
            class_9288 container;
            class_1542 itemEntity;
            class_1799 stack;
            class_1767 color;
            if (!(entity instanceof class_1542)) continue;
            itemEntity = (class_1542)entity;
            stack = itemEntity.method_6983();
            color = this.shulkerColor(stack);
            if (color == null && !this.isShulkerBlock(stack)) continue;
            if (cameraPos.method_1025(entity.method_73189()) > 1024.0) continue;
            container = (class_9288)stack.method_58694(class_9334.field_49622);
            if (container == null) continue;
            contents = container.method_57489().toList();
            if (contents.isEmpty()) continue;
            screen = this.worldToScreen(entity.method_23317(), entity.method_23318() + (double)entity.method_17682() / 2.0, entity.method_23321());
            if (screen == null) continue;
            previews.add(new GroundPreview(screen.x, screen.y, this.brighten(color), contents));
        }
        if (previews.isEmpty()) {
            return;
        }
        float scale = client.method_22683().method_4495();
        this.icons.update2();
        for (GroundPreview preview : previews) {
            for (class_1799 stack : preview.stacks) {
                preview.icons.add(stack.method_7960() ? null : this.icons.process(stack));
            }
        }
        ArrayList<BakedIconEntry> commands = new ArrayList<BakedIconEntry>();
        this.icons.process2(scale, commands);
        if (!commands.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(commands);
        }
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        Matrix4f matrix = new Matrix4f().scale(scale);
        renderer.begin();
        try {
            for (GroundPreview preview : previews) {
                this.drawPanel(renderer, matrix, preview);
            }
            for (GroundPreview preview : previews) {
                this.drawIcons(renderer, matrix, preview);
            }
        }
        finally {
            renderer.end();
        }
        this.icons.update();
    }

    private void onItemHover(ItemHoverEvent event) {
        class_1799 stack = event.getStack();
        if (!this.enabledSetting.isEnabled() || !ShulkerViewerModule.compute(stack)) {
            return;
        }
        this.hoveredShulker = stack;
        if (this.viewKey.isPressed()) {
            event.update();
        }
    }

    private void onTooltip(TooltipRenderEvent event) {
        class_1799 stack = this.hoveredShulker;
        this.hoveredShulker = null;
        if (!this.enabledSetting.isEnabled() || stack == null || !this.viewKey.isPressed()) {
            return;
        }
        class_9288 container = (class_9288)stack.method_58694(class_9334.field_49622);
        if (container == null) {
            return;
        }
        this.drawTooltip(event.getDrawContext(), event.getIntType2(), event.getIntType(), stack, container.method_57489().toList());
    }

    private void drawTooltip(class_332 context, int mouseX, int mouseY, class_1799 stack, List<class_1799> contents) {
        class_310 client = class_310.method_1551();
        int rows = Math.max(3, (contents.size() + 9 - 1) / 9);
        int slots = rows * 9;
        int width = 174;
        int height = rows * 18 + 12;
        int screenWidth = context.method_51421();
        int screenHeight = context.method_51443();
        int x = mouseX + 8;
        int y = mouseY + 8;
        if (x + width > screenWidth) {
            x = mouseX - 8 - width;
        }
        if (y + height > screenHeight) {
            y = Math.max(0, screenHeight - height);
        }
        int tint = this.itemTint(stack);
        int fill = 0xE6000000 | this.scaleRgb(tint, 0.35f);
        int slotFill = 0x33FFFFFF;
        context.method_25294(x, y, x + width, y + height, fill);
        context.method_25294(x, y, x + width, y + 1, 0xFF000000 | tint);
        context.method_25294(x, y + height - 1, x + width, y + height, 0xFF000000 | tint);
        context.method_25294(x, y, x + 1, y + height, 0xFF000000 | tint);
        context.method_25294(x + width - 1, y, x + width, y + height, 0xFF000000 | tint);
        for (int i = 0; i < slots; ++i) {
            class_1799 item;
            int slotX = x + 6 + i % 9 * 18;
            int slotY = y + 6 + i / 9 * 18;
            context.method_25294(slotX, slotY, slotX + 16, slotY + 16, slotFill);
            if (i >= contents.size() || (item = contents.get(i)).method_7960()) continue;
            context.method_51427(item, slotX, slotY);
            context.method_51431(client.field_1772, item, slotX, slotY);
        }
    }

    private void drawPanel(GuiDrawApi renderer, Matrix4f matrix, GroundPreview preview) {
        int rows = Math.max(3, (preview.stacks.size() + 9 - 1) / 9);
        float x = preview.x - 86.0f;
        float y = preview.y + 6.0f;
        float height = rows * 18 + 10;
        renderer.fillRectangle(matrix, x, y, 172.0f, height, -1072689136);
        int border = 0xFF000000 | preview.tint & 0xFFFFFF;
        renderer.fillRectangle(matrix, x, y, 172.0f, 1.0f, border);
        renderer.fillRectangle(matrix, x, y + height - 1.0f, 172.0f, 1.0f, border);
        renderer.fillRectangle(matrix, x, y, 1.0f, height, border);
        renderer.fillRectangle(matrix, x + 172.0f - 1.0f, y, 1.0f, height, border);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < 9; ++column) {
                renderer.fillRectangle(matrix, x + 6.0f + (float)(18 * column), y + 6.0f + (float)(18 * row), 16.0f, 16.0f, 0x33FFFFFF);
            }
        }
    }

    private void drawIcons(GuiDrawApi renderer, Matrix4f matrix, GroundPreview preview) {
        float x = preview.x - 86.0f;
        float y = preview.y + 6.0f;
        for (int i = 0; i < preview.stacks.size(); ++i) {
            Object icon = preview.icons.get(i);
            if (icon == null) continue;
            float slotX = x + 6.0f + (float)(18 * (i % 9));
            float slotY = y + 6.0f + (float)(18 * (i / 9));
            this.icons.process3(renderer, matrix, icon, slotX, slotY, 16.0f);
        }
    }

    private class_1767 shulkerColor(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return null;
        }
        class_1792 item = stack.method_7909();
        if (!(item instanceof class_1747)) {
            return null;
        }
        class_1747 blockItem = (class_1747)item;
        class_2248 class_22482 = blockItem.method_7711();
        if (!(class_22482 instanceof class_2480)) {
            return null;
        }
        class_2480 shulker = (class_2480)class_22482;
        return shulker.method_10528();
    }

    private boolean isShulkerBlock(class_1799 stack) {
        class_1747 blockItem;
        if (stack == null || stack.method_7960()) {
            return false;
        }
        class_1792 item = stack.method_7909();
        return item instanceof class_1747 && (blockItem = (class_1747)item).method_7711() instanceof class_2480;
    }

    private int itemTint(class_1799 stack) {
        class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
        String path = id == null ? "" : id.method_12832();
        for (class_1767 color : class_1767.values()) {
            if (!path.startsWith(color.method_15434() + "_")) continue;
            return color.method_16357() & 0xFFFFFF;
        }
        return 9068428;
    }

    private int brighten(class_1767 color) {
        int rgb = color != null ? color.method_7787() & 0xFFFFFF : 9068428;
        int red = Math.min(255, (rgb >> 16 & 0xFF) + 38);
        int green = Math.min(255, (rgb >> 8 & 0xFF) + 38);
        int blue = Math.min(255, (rgb & 0xFF) + 38);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private int scaleRgb(int rgb, float factor) {
        int red = Math.round((float)(rgb >> 16 & 0xFF) * factor);
        int green = Math.round((float)(rgb >> 8 & 0xFF) * factor);
        int blue = Math.round((float)(rgb & 0xFF) * factor);
        return red << 16 | green << 8 | blue;
    }

    private Vector2f worldToScreen(double x, double y, double z) {
        return RenderProjection.project(new class_243(x, y, z));
    }

    private static final class GroundPreview {
        final float x;
        final float y;
        final int tint;
        final List<class_1799> stacks;
        final List<Object> icons = new ArrayList<Object>();

        GroundPreview(float x, float y, int tint, List<class_1799> contents) {
            this.x = x;
            this.y = y;
            this.tint = tint;
            this.stacks = contents;
        }
    }
}

