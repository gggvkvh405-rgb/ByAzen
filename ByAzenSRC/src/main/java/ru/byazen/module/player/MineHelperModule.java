/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 */
package ru.byazen.module.player;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_12249;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.HudRenderEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.BakedIconEntry;
import ru.byazen.misc.ElapsedTimer;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.render.BakedItemIcon;
import ru.byazen.render.ItemIconCache;
import ru.byazen.render.RenderProjection;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.GuiDrawApi;

public class MineHelperModule
extends Module
implements ConfigSerializable {
    private static final int SCAN_SLICES = 2;
    private static final long SCAN_INTERVAL_MS = 1000L;
    private static final Map<class_2248, OreGroup> ORES = new HashMap<class_2248, OreGroup>();
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting ores;
    private final NumberSetting scanRadius;
    private final BooleanSetting renderMaterial;
    private final ItemIconCache iconCache = new ItemIconCache();
    private final ElapsedTimer scanTimer = new ElapsedTimer();
    private List<OreHit> scanBuffer;
    private List<OreHit> found = List.of();
    private class_2338 scanOrigin;
    private int scanOffset;
    private int scanExtent;

    public MineHelperModule(EventBus eventBus) {
        super(eventBus, "mine_helper", "Mine Helper", "\u0421\u043a\u0430\u043d\u0438\u0440\u0443\u0435\u0442 \u0442\u0435\u0440\u0440\u0438\u0442\u043e\u0440\u0438\u044e \u0432 \u043f\u043e\u0438\u0441\u043a\u0435 \u0440\u0443\u0434", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting oresSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Diamond", "Emerald", "Gold", "Iron", "Debris", "Lapis", "Quartz", "Redstone", "Coal").selectAll(true).optionListEnabled(false).name("Ores").id("ores").description("\u041a\u0430\u043a\u0438\u0435 \u0440\u0443\u0434\u044b \u0438\u0441\u043a\u0430\u0442\u044c").aliases("ores", "\u0440\u0443\u0434\u044b")).build();
        oresSetting.setOptions(new String[]{"Diamond", "Emerald", "Gold", "Iron", "Debris", "Lapis", "Quartz", "Redstone", "Coal"});
        this.ores = oresSetting;
        this.registerSetting(oresSetting);
        this.scanRadius = ((NumberSettingBuilder)NumberSetting.builder().range(5.0, 30.0).defaultValue(15.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Scan Radius").id("scan_radius").description("\u0420\u0430\u0434\u0438\u0443\u0441 \u0441\u043a\u0430\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f").aliases("scan radius", "\u0440\u0430\u0434\u0438\u0443\u0441")).build();
        this.registerSetting(this.scanRadius);
        this.renderMaterial = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Render Material").id("render_material").description("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0432\u044b\u043f\u0430\u0434\u0430\u0435\u043c\u044b\u0439 \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b \u043d\u0430\u0434 \u0440\u0443\u0434\u043e\u0439").aliases("render material", "\u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b")).build();
        this.registerSetting(this.renderMaterial);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::onWorldRender);
        this.listen(HudRenderEvent.class, event -> this.onHudRender());
    }

    private List<OreHit> visibleOres() {
        this.scanSlice();
        return this.found;
    }

    private void drawBox(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
        double minX = box.field_1323;
        double minY = box.field_1322;
        double minZ = box.field_1321;
        double maxX = box.field_1320;
        double maxY = box.field_1325;
        double maxZ = box.field_1324;
        this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, maxX, minY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, minY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, minX, minY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, minY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, maxY, minZ, maxX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, maxY, maxZ, minX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, minX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, maxY, minZ, color);
        this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    private void drawLine(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = color >>> 24 & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        consumer.method_22918((Matrix4fc)matrix, (float)(x1 - cameraPos.field_1352), (float)(y1 - cameraPos.field_1351), (float)(z1 - cameraPos.field_1350)).method_1336(red, green, blue, alpha);
        consumer.method_22918((Matrix4fc)matrix, (float)(x2 - cameraPos.field_1352), (float)(y2 - cameraPos.field_1351), (float)(z2 - cameraPos.field_1350)).method_1336(red, green, blue, alpha);
    }

    private void onWorldRender(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        if (!this.enabledSetting.isEnabled() || client.field_1687 == null || client.field_1724 == null || cameraPos == null) {
            return;
        }
        List<String> selected = this.ores.getSelectedOptions();
        ArrayList<EspBox> boxes = new ArrayList<EspBox>();
        for (OreHit hit : this.visibleOres()) {
            class_238 box = this.blockBox(hit.pos());
            if (!selected.contains(hit.group().name()) || !this.inView(box, cameraPos)) continue;
            boxes.add(new EspBox(box, hit.group().color()));
        }
        if (boxes.isEmpty()) {
            return;
        }
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (EspBox box : boxes) {
            this.drawBox(consumer, matrix, cameraPos, box.box(), box.color());
        }
        class_12249.method_76015().method_60895(consumer.method_60800());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onHudRender() {
        if (!this.enabledSetting.isEnabled() || !this.renderMaterial.isEnabled()) {
            this.iconCache.update3();
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        if (client.field_1687 == null || player == null || cameraPos == null) {
            return;
        }
        List<String> selected = this.ores.getSelectedOptions();
        Matrix4f projection = RenderProjection.viewProjectionMatrix();
        this.iconCache.update2();
        ArrayList<ScreenIcon> icons = new ArrayList<ScreenIcon>();
        for (OreHit hit : this.visibleOres()) {
            Vector2f screen;
            if (!selected.contains(hit.group().name())) continue;
            class_2338 pos = hit.pos();
            double x = (double)pos.method_10263() + 0.5;
            double y = (double)pos.method_10264() + 0.5;
            double z = (double)pos.method_10260() + 0.5;
            class_238 box = this.blockBox(pos);
            if (!this.inView(box, cameraPos) || (screen = this.worldToScreen(x, y, z, projection)) == null) continue;
            float size = this.iconSize(player.method_5649(x, y, z));
            icons.add(new ScreenIcon(screen.x, screen.y, size, hit.group().icon()));
        }
        if (icons.isEmpty()) {
            this.iconCache.update();
            return;
        }
        float scale = client.method_22683().method_4495();
        ArrayList<BakedItemIcon> bakedIcons = new ArrayList<BakedItemIcon>();
        for (ScreenIcon icon : icons) {
            bakedIcons.add(this.iconCache.process(icon.stack()));
        }
        ArrayList<BakedIconEntry> commands = new ArrayList<BakedIconEntry>();
        this.iconCache.process2(scale, commands);
        if (!commands.isEmpty()) {
            ByAzenClient.getRenderPipeline2().setList(commands);
        }
        GuiDrawApi renderer = ByAzenClient.getHudRenderer();
        Matrix4f scaled = new Matrix4f().scale(scale);
        renderer.begin();
        try {
            for (int index = 0; index < icons.size(); ++index) {
                ScreenIcon icon = (ScreenIcon)icons.get(index);
                this.iconCache.process3(renderer, scaled, bakedIcons.get(index), icon.x() - icon.size() / 2.0f, icon.y() - icon.size() / 2.0f, icon.size());
            }
        }
        finally {
            renderer.end();
        }
        this.iconCache.update();
    }

    private float iconSize(double squaredDistance) {
        double distance = Math.max(Math.sqrt(squaredDistance), 4.0);
        return class_3532.method_15363((float)((float)(104.0 / distance)), (float)5.0f, (float)22.0f);
    }

    private static void registerOre(String name, int color, class_1792 icon, class_2248 ... blocks) {
        OreGroup group = new OreGroup(name, color | 0xFF000000, new class_1799((class_1935)icon));
        for (class_2248 block : blocks) {
            ORES.put(block, group);
        }
    }

    private void scanSlice() {
        class_310 client = class_310.method_1551();
        class_638 world = client.field_1687;
        class_746 player = client.field_1724;
        if (world == null || player == null) {
            return;
        }
        if (this.scanOrigin == null) {
            if (!this.scanTimer.process(1000L)) {
                return;
            }
            this.scanOrigin = player.method_24515();
            this.scanExtent = this.scanRadius.getIntValue();
            this.scanOffset = -this.scanExtent;
            this.scanBuffer = new ArrayList<OreHit>();
        }
        class_2338.class_2339 mutable = new class_2338.class_2339();
        int slices = 0;
        while (slices < 2 && this.scanOffset <= this.scanExtent) {
            int x = this.scanOrigin.method_10263() + this.scanOffset;
            for (int yOff = -this.scanExtent; yOff <= this.scanExtent; ++yOff) {
                for (int zOff = -this.scanExtent; zOff <= this.scanExtent; ++zOff) {
                    mutable.method_10103(x, this.scanOrigin.method_10264() + yOff, this.scanOrigin.method_10260() + zOff);
                    OreGroup group = ORES.get(world.method_8320((class_2338)mutable).method_26204());
                    if (group == null) continue;
                    this.scanBuffer.add(new OreHit(mutable.method_10062(), group));
                }
            }
            ++slices;
            ++this.scanOffset;
        }
        if (this.scanOffset > this.scanExtent) {
            this.found = this.scanBuffer;
            this.scanBuffer = null;
            this.scanOrigin = null;
            this.scanTimer.update();
        }
    }

    private class_238 blockBox(class_2338 pos) {
        return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1.0, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 1.0);
    }

    private boolean inView(class_238 box, class_243 cameraPos) {
        return cameraPos.method_1025(box.method_1005()) < 4096.0;
    }

    private Vector2f worldToScreen(double x, double y, double z, Matrix4f matrix) {
        return RenderProjection.project(x, y, z, matrix);
    }

    static {
        MineHelperModule.registerOre("Diamond", 4910553, class_1802.field_8477, class_2246.field_10442, class_2246.field_29029);
        MineHelperModule.registerOre("Emerald", 1564002, class_1802.field_8687, class_2246.field_10013, class_2246.field_29220);
        MineHelperModule.registerOre("Gold", 16576075, class_1802.field_8695, class_2246.field_10571, class_2246.field_29026, class_2246.field_23077);
        MineHelperModule.registerOre("Iron", 14200723, class_1802.field_8620, class_2246.field_10212, class_2246.field_29027);
        MineHelperModule.registerOre("Debris", 6637376, class_1802.field_22021, class_2246.field_22109);
        MineHelperModule.registerOre("Lapis", 1395125, class_1802.field_8759, class_2246.field_10090, class_2246.field_29028);
        MineHelperModule.registerOre("Quartz", 15130844, class_1802.field_8155, class_2246.field_10213);
        MineHelperModule.registerOre("Redstone", 0xFF0000, class_1802.field_8725, class_2246.field_10080, class_2246.field_29030);
        MineHelperModule.registerOre("Coal", 0x363636, class_1802.field_8713, class_2246.field_10418, class_2246.field_29219);
    }

    private record OreHit(class_2338 pos, OreGroup group) {
    }

    private record OreGroup(String name, int color, class_1799 icon) {
    }

    private record EspBox(class_238 box, int color) {
    }

    private record ScreenIcon(float x, float y, float size, class_1799 stack) {
    }
}

