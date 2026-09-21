/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;

public final class TracersModule
extends Module
implements ConfigSerializable {
    private static final double LOOK_DISTANCE = 75.0;
    private static volatile TracersModule instance;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting types;
    private final ColorSetting playersColor;
    private final ColorSetting entitiesColor;
    private final ColorSetting friendsColor;
    private final NumberSetting width;
    private final List<TracerLine> lines = new ArrayList<TracerLine>();

    public TracersModule(EventBus eventBus) {
        super(eventBus, "tracers", "Tracers", "\u041b\u0438\u043d\u0438\u0438 \u0434\u043e \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041b\u0438\u043d\u0438\u0438 \u0434\u043e \u043e\u0431\u044a\u0435\u043a\u0442\u0430 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting typesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Players", "Entities", "Friends").selectAll(false).optionListEnabled(false).name("Types").id("types").description("\u0412\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438")).build();
        typesSetting.setOptions(new String[0]);
        this.types = typesSetting;
        this.registerSetting(typesSetting);
        ColorSetting players = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Players Color").id("players_color").description("\u0426\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0439 \u0434\u043e \u0438\u0433\u0440\u043e\u043a\u043e\u0432").visibleWhen(() -> this.hasType("Players"))).build();
        this.applyPalette(players);
        this.playersColor = players;
        this.registerSetting(players);
        ColorSetting entities = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Entities Color").id("entities_color").description("\u0426\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0439 \u0434\u043e \u043c\u043e\u0431\u043e\u0432").visibleWhen(() -> this.hasType("Entities"))).build();
        this.applyPalette(entities);
        this.entitiesColor = entities;
        this.registerSetting(entities);
        ColorSetting friends = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Friends Color").id("friends_color").description("\u0426\u0432\u0435\u0442 \u043b\u0438\u043d\u0438\u0439 \u0434\u043e \u0434\u0440\u0443\u0437\u0435\u0439").visibleWhen(() -> this.hasType("Friends"))).build();
        this.applyPalette(friends);
        this.friendsColor = friends;
        this.registerSetting(friends);
        this.width = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 2.0).defaultValue(1.0).multiplier(1.0).precision(1).animationSpeed(20.0f).snapTo(0.5).name("Width").id("width").description("\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0439").aliases("width", "\u0442\u043e\u043b\u0449\u0438\u043d\u0430")).build();
        this.registerSetting(this.width);
    }

    @Override
    protected void initialize() {
        this.listen(WorldRenderEvent.class, this::collectLines);
    }

    public static void tick2() {
        TracersModule module = instance;
        if (module != null) {
            module.drawLines();
        }
    }

    private void collectLines(WorldRenderEvent event) {
        this.lines.clear();
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            return;
        }
        class_243 cameraPos = client.field_1773.method_19418().method_71156();
        if (cameraPos == null) {
            return;
        }
        boolean players = this.hasType("Players");
        boolean entities = this.hasType("Entities");
        boolean friends = this.hasType("Friends");
        if (!(players || entities || friends)) {
            return;
        }
        int playerColor = this.playersColor.getColor(0.0f);
        int entityColor = this.entitiesColor.getColor(0.0f);
        int friendColor = this.friendsColor.getColor(0.0f);
        float tickDelta = event.getFloatType();
        for (class_1297 entity : client.field_1687.method_18112()) {
            Integer color = this.colorFor(entity, players, entities, friends, playerColor, entityColor, friendColor);
            if (color == null) continue;
            class_243 offset = entity.method_30950(tickDelta).method_1031(0.0, (double)(entity.method_17682() / 2.0f), 0.0).method_1020(cameraPos);
            this.lines.add(new TracerLine(offset, color));
        }
    }

    private void drawLines() {
        if (this.lines.isEmpty()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            this.lines.clear();
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        class_243 look = class_243.method_1030((float)camera.method_19329(), (float)camera.method_19330()).method_1021(75.0);
        Matrix4f matrix = new Matrix4f();
        class_287 consumer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (TracerLine line : this.lines) {
            class_243 delta = line.offset.method_1020(look);
            if (delta.method_1027() < 1.0E-4) continue;
            class_243 normal = delta.method_1029();
            int[] rgba = ColorUtils.unpackRgba(line.color);
            consumer.method_22918((Matrix4fc)matrix, (float)look.field_1352, (float)look.field_1351, (float)look.field_1350).method_1336(rgba[0], rgba[1], rgba[2], rgba[3]);
            consumer.method_22918((Matrix4fc)matrix, (float)line.offset.field_1352, (float)line.offset.field_1351, (float)line.offset.field_1350).method_1336(rgba[0], rgba[1], rgba[2], rgba[3]);
        }
        class_12249.method_76015().method_60895(consumer.method_60800());
        this.lines.clear();
    }

    private Integer colorFor(class_1297 entity, boolean players, boolean entities, boolean friends, int playerColor, int entityColor, int friendColor) {
        class_310 client = class_310.method_1551();
        if (entity == null || entity == client.field_1724 || entity == client.method_1560() || !entity.method_5805()) {
            return null;
        }
        if (entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            if (this.isFriend(player)) {
                return friends ? Integer.valueOf(friendColor) : null;
            }
            return players ? Integer.valueOf(playerColor) : null;
        }
        if (entity instanceof class_1309) {
            return entities ? Integer.valueOf(entityColor) : null;
        }
        return null;
    }

    private boolean isFriend(class_1657 player) {
        FriendList friends = WexSideClient.getFriends();
        return friends != null && friends.contains(player.method_5477().getString());
    }

    private boolean hasType(String type) {
        return this.types.getSelectedOptions().contains(type);
    }

    private void applyPalette(ColorSetting setting) {
        setting.setPrimaryColor(0, -11753627);
        setting.setPrimaryColor(1, -1543135);
        setting.setPrimaryColor(2, -9279489);
        setting.setPrimaryColor(3, -46001);
        setting.setPrimaryColor(4, -13218);
        setting.setPrimaryColor(5, -10582785);
        setting.setPrimaryColor(6, -2732032);
    }

    private record TracerLine(class_243 offset, int color) {
    }
}

