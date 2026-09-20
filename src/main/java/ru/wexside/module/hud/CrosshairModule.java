/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_239
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3966
 *  net.minecraft.class_408
 *  net.minecraft.class_5498
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 */
package ru.wexside.module.hud;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_408;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.TransitionAnimation;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.Easing;
import ru.wexside.util.GuiDrawApi;

public final class CrosshairModule
extends Module
implements ConfigSerializable {
    private static volatile CrosshairModule instance;
    private static final float OUTLINE_SIZE = 1.0f;
    private static final float ATTACK_WAVE_SCALE = 5.0f;
    private final BooleanSetting enabledSetting;
    private final ColorSetting color;
    private final BooleanSetting rayTrace;
    private final ColorSetting hoverColor;
    private final BooleanSetting tLike;
    private final BooleanSetting dot;
    private final BooleanSetting displayCooldown;
    private final NumberSetting length;
    private final NumberSetting gap;
    private final TransitionAnimation hoverAnimation;

    public CrosshairModule(EventBus eventBus) {
        super(eventBus, "crosshair", "Crosshair", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        instance = this;
        this.hoverAnimation = new TransitionAnimation(Easing.LINEAR);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        ColorSetting crosshairColor = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").aliases("color", "\u0446\u0432\u0435\u0442")).build();
        this.applyPalette(crosshairColor);
        this.color = crosshairColor;
        this.registerSetting(crosshairColor);
        this.rayTrace = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("RayTrace").id("ray_trace").description("\u041c\u0435\u043d\u044f\u0435\u0442 \u0446\u0432\u0435\u0442 \u043f\u0440\u0438 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0438 \u043d\u0430 Entity").aliases("raytrace", "\u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0435")).build();
        this.registerSetting(this.rayTrace);
        ColorSetting hover = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("RayTrace Color").id("hover_color").description("\u0426\u0432\u0435\u0442 \u043f\u0440\u0438 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0438 \u043d\u0430 Entity").aliases("hover color", "\u0446\u0432\u0435\u0442 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u044f").visibleWhen(this.rayTrace::isEnabled)).build();
        this.applyPalette(hover);
        this.hoverColor = hover;
        this.registerSetting(hover);
        this.tLike = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("T-Like").id("t_like").description("\u0424\u043e\u0440\u043c\u0430 \u043f\u0440\u0438\u0446\u0435\u043b\u0430 T-\u043e\u0431\u0440\u0430\u0437\u043d\u0430\u044f").aliases("tlike", "\u0442-\u043e\u0431\u0440\u0430\u0437\u043d\u044b\u0439")).build();
        this.registerSetting(this.tLike);
        this.dot = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Dot").id("dot").description("\u0422\u043e\u0447\u043a\u0430 \u0432 \u0446\u0435\u043d\u0442\u0440\u0435 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").aliases("dot", "\u0442\u043e\u0447\u043a\u0430")).build();
        this.registerSetting(this.dot);
        this.displayCooldown = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Display Cooldown").id("display_cooldown").description("\u041f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u043a\u0430 \u0430\u0442\u0430\u043a\u0438 \u043d\u0430 \u043f\u0440\u0438\u0446\u0435\u043b\u0435")).build();
        this.registerSetting(this.displayCooldown);
        this.length = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 8.0).defaultValue(4.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Length").id("length").description("\u0414\u043b\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0439 \u043f\u0440\u0438\u0446\u0435\u043b\u0430").aliases("length", "\u0434\u043b\u0438\u043d\u0430")).build();
        this.registerSetting(this.length);
        this.gap = ((NumberSettingBuilder)NumberSetting.builder().range(1.0, 12.0).defaultValue(3.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Gap").id("gap").description("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u043c\u0435\u0436\u0434\u0443 \u043b\u0438\u043d\u0438\u044f\u043c\u0438 \u043f\u0440\u0438\u0446\u0435\u043b\u0430")).build();
        this.registerSetting(this.gap);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(HudRenderEvent.class, event -> this.onRender());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled()) {
            this.hoverAnimation.setActive(false);
            return;
        }
        this.hoverAnimation.setActive(this.isHoveringLivingEntity());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onRender() {
        if (!this.enabledSetting.isEnabled() || CrosshairModule.shouldHideCustomCrosshair()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1690 == null || client.field_1690.method_31044() != class_5498.field_26664) {
            return;
        }
        float scale = client.method_22683().method_4495();
        float centerX = (float)client.method_22683().method_4486() / 2.0f;
        float centerY = (float)client.method_22683().method_4502() / 2.0f;
        float lineLength = (float)this.length.getValue();
        float cooldownExpansion = 0.0f;
        if (this.displayCooldown.isEnabled()) {
            float tickDelta = client.method_61966().method_60637(true);
            float cooldown = player.method_7261(tickDelta);
            cooldownExpansion = class_3532.method_15374((double)(cooldown * cooldown * (float)Math.PI)) * 5.0f;
        }
        float lineGap = (float)this.gap.getValue() + cooldownExpansion;
        int rgb = this.color.getColor(0.0f);
        float hoverMix = this.hoverAnimation.getPrimaryProgress();
        if (hoverMix > 0.0f && this.rayTrace.isEnabled()) {
            rgb = ColorUtils.lerp(rgb, this.hoverColor.getColor(1.0f), hoverMix);
        }
        int outline = ColorUtils.lightContrastColor;
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        Matrix4f matrix = new Matrix4f().scale(scale);
        renderer.begin();
        try {
            if (this.dot.isEnabled()) {
                this.drawQuad(renderer, matrix, centerX - 1.0f, centerY - 1.0f, centerX + 1.0f, centerY + 1.0f, outline);
                this.drawQuad(renderer, matrix, centerX - 0.5f, centerY - 0.5f, centerX + 0.5f, centerY + 0.5f, rgb);
            }
            if (!this.tLike.isEnabled()) {
                this.drawLine(renderer, matrix, centerX, centerY - lineGap - lineLength, centerX, centerY - lineGap, rgb, outline);
            }
            this.drawLine(renderer, matrix, centerX, centerY + lineGap, centerX, centerY + lineGap + lineLength, rgb, outline);
            this.drawLine(renderer, matrix, centerX - lineGap - lineLength, centerY, centerX - lineGap, centerY, rgb, outline);
            this.drawLine(renderer, matrix, centerX + lineGap, centerY, centerX + lineGap + lineLength, centerY, rgb, outline);
        }
        finally {
            renderer.end();
        }
    }

    private boolean isHoveringLivingEntity() {
        class_239 hit = class_310.method_1551().field_1765;
        if (!(hit instanceof class_3966)) {
            return false;
        }
        class_3966 entityHit = (class_3966)hit;
        class_1297 entity = entityHit.method_17782();
        return entity instanceof class_1309;
    }

    private static boolean shouldHideCustomCrosshair() {
        return class_310.method_1551().field_1755 instanceof class_408;
    }

    public static boolean isEnabled3() {
        if (CrosshairModule.shouldHideCustomCrosshair()) {
            return true;
        }
        CrosshairModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        class_310 client = class_310.method_1551();
        return client.field_1690 != null && client.field_1690.method_31044() == class_5498.field_26664;
    }

    private void drawQuad(GuiDrawApi renderer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        renderer.fillRectangle(matrix, x1, y1, x2 - x1, y2 - y1, color);
    }

    private void drawLine(GuiDrawApi renderer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color, int outline) {
        this.drawQuad(renderer, matrix, x1 - 0.5f, y1 - 0.5f, x2 + 0.5f, y2 + 0.5f, outline);
        this.drawQuad(renderer, matrix, x1, y1, x2, y2, color);
    }

    private void applyPalette(ColorSetting colorSetting) {
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
    }
}

