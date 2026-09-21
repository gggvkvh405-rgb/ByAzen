/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  org.joml.Matrix3x2fStack
 */
package ru.byazen.module.hud;

import java.util.Objects;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.joml.Matrix3x2fStack;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.HandledScreenAccessor;
import ru.byazen.misc.NativeHandle;
import ru.byazen.misc.TransitionAnimation;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.Easing;

public final class AnimateModule
extends Module
implements ConfigSerializable {
    static volatile AnimateModule animateModule2;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting targets;
    private final BooleanSetting bounce;
    private final NumberSetting duration;
    private final NumberSetting inputDuration;
    private final TransitionAnimation containerAnimation;
    private class_437 closingScreen;
    private class_437 openingScreen;
    private boolean chatOpen;
    private boolean renderingClosingScreen;
    private long tabAnimationStartMs;

    public AnimateModule(EventBus eventBus) {
        super(eventBus, "animate", "Animate", "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f \u044d\u043a\u0440\u0430\u043d\u043e\u0432", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        animateModule2 = this;
        this.containerAnimation = new TransitionAnimation(Easing.LINEAR);
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0410\u043d\u0438\u043c\u0438\u0440\u0443\u0435\u0442 \u044d\u043a\u0440\u0430\u043d\u044b").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Container", "Chat", "Tab").selectAll(true).optionListEnabled(false).name("Targets").id("targets").description("\u0427\u0442\u043e \u0430\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u0442\u044c")).build();
        targetsSetting.setOptions(new String[]{"Container", "Chat", "Tab"});
        this.targets = targetsSetting;
        this.registerSetting(targetsSetting);
        this.bounce = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Bounce").id("bounce").description("\u041e\u0442\u0441\u043a\u043e\u043a").aliases("bounce", "\u043e\u0442\u0441\u043a\u043e\u043a").visibleWhen(() -> this.targets.getSelectedOptions().contains("Container"))).build();
        this.registerSetting(this.bounce);
        this.duration = ((NumberSettingBuilder)NumberSetting.builder().range(50.0, 300.0).defaultValue(250.0).multiplier(1.0).precision(0).animationSpeed(20.0f).markers(50.0).snapTo(50.0).name("Duration").id("duration").description("\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438")).build();
        this.registerSetting(this.duration);
        this.inputDuration = ((NumberSettingBuilder)NumberSetting.builder().range(50.0, 300.0).defaultValue(170.0).multiplier(1.0).precision(0).animationSpeed(20.0f).markers(50.0).snapTo(10.0).name("Input Duration").id("input_duration").description("\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u043f\u043e\u043b\u044f \u0432\u0432\u043e\u0434\u0430 \u0447\u0430\u0442\u0430").aliases("input duration", "\u043f\u043e\u043b\u0435 \u0432\u0432\u043e\u0434\u0430").visibleWhen(() -> this.targets.getSelectedOptions().contains("Chat"))).build();
        this.registerSetting(this.inputDuration);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled() || !this.isTargetEnabled("Container")) {
            this.openingScreen = null;
            this.closingScreen = null;
            this.containerAnimation.finish();
            return;
        }
        double step = 50.0 / Math.max(50.0, this.duration.getValue());
        class_310 client = class_310.method_1551();
        class_437 currentScreen = client.field_1755;
        if (currentScreen instanceof class_465) {
            if (this.openingScreen != currentScreen) {
                this.openingScreen = currentScreen;
                this.containerAnimation.reset();
            }
            this.closingScreen = null;
            this.containerAnimation.advance(true, step);
        } else if (currentScreen == null) {
            if (this.openingScreen != null) {
                this.closingScreen = this.openingScreen;
                this.openingScreen = null;
            }
            if (this.closingScreen != null) {
                this.containerAnimation.advance(false, step);
                if (this.containerAnimation.isAtStart()) {
                    this.closingScreen = null;
                }
            }
        } else {
            this.openingScreen = null;
            this.closingScreen = null;
        }
    }

    private boolean isTargetEnabled(String target) {
        return this.targets.getSelectedOptions().contains(target);
    }

    public static boolean compute2(class_437 screen) {
        AnimateModule module = animateModule2;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        Objects.requireNonNull(module);
        if (!module.isTargetEnabled("Container")) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1755 != screen || !(screen instanceof class_465)) {
            return false;
        }
        if (module.openingScreen != screen) {
            module.openingScreen = screen;
            module.closingScreen = null;
            module.containerAnimation.reset();
        }
        return true;
    }

    public static float compute3(int height) {
        AnimateModule module = animateModule2;
        if (module == null || !module.enabledSetting.isEnabled() || !module.isTargetEnabled("Tab")) {
            return 0.0f;
        }
        float durationMs = (float)module.duration.getValue();
        if (durationMs <= 0.0f) {
            return 0.0f;
        }
        float progress = Math.min((float)(System.currentTimeMillis() - module.tabAnimationStartMs) / durationMs, 1.0f);
        float eased = Easing.EASE_OUT_CUBIC.apply(progress);
        return -(1.0f - eased) * (float)height * 0.4f;
    }

    public static float compute4(float width, NativeHandle chatHud) {
        if (!AnimateModule.isEnabled2()) {
            return width;
        }
        float durationMs = AnimateModule.getFloatType2();
        if (durationMs <= 0.0f) {
            return width;
        }
        float elapsed = System.currentTimeMillis() - chatHud.getLongType();
        float progress = Math.min(elapsed / durationMs, 1.0f);
        return Easing.LINEAR.apply(width * progress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onHudRender(class_332 context) {
        class_437 screen;
        AnimateModule module = animateModule2;
        if (module == null || !module.enabledSetting.isEnabled() || !module.isTargetEnabled("Container")) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (module.closingScreen == null && module.openingScreen != null && client.field_1755 == null) {
            module.closingScreen = module.openingScreen;
            module.openingScreen = null;
        }
        if ((screen = module.closingScreen) == null || client.field_1755 != null || !(screen instanceof class_465)) {
            return;
        }
        class_465 handledScreen = (class_465)screen;
        class_1041 window = client.method_22683();
        int mouseX = (int)client.field_1729.method_68879(window);
        int mouseY = (int)client.field_1729.method_68883(window);
        float tickDelta = client.method_61966().method_60637(true);
        float fade = AnimateModule.compute5(module);
        if (fade > 0.0f) {
            int topColor = (int)(192.0f * fade) << 24 | 0x101010;
            int bottomColor = (int)(208.0f * fade) << 24 | 0x101010;
            context.method_25296(0, 0, handledScreen.field_22789, handledScreen.field_22790, topColor, bottomColor);
        }
        Matrix3x2fStack matrices = context.method_51448();
        matrices.pushMatrix();
        module.renderingClosingScreen = true;
        try {
            AnimateModule.handle(matrices, handledScreen.field_22789, handledScreen.field_22790);
            ((HandledScreenAccessor)handledScreen).drawContainerBackground(context, tickDelta, mouseX, mouseY);
            handledScreen.method_25394(context, mouseX, mouseY, tickDelta);
        }
        catch (RuntimeException ignored) {
            module.closingScreen = null;
        }
        finally {
            module.renderingClosingScreen = false;
            matrices.popMatrix();
        }
    }

    private boolean isAnimatingContainer() {
        if (!this.enabledSetting.isEnabled()) {
            return false;
        }
        class_310 client = class_310.method_1551();
        return this.openingScreen != null && client.field_1755 == this.openingScreen || this.renderingClosingScreen;
    }

    public static float getFloatType() {
        AnimateModule module = animateModule2;
        return module == null ? 0.0f : (float)module.inputDuration.getValue();
    }

    public static void onBooleanType(boolean open) {
        AnimateModule module = animateModule2;
        if (module == null) {
            return;
        }
        if (open && !module.chatOpen) {
            module.tabAnimationStartMs = System.currentTimeMillis();
        }
        module.chatOpen = open;
    }

    public static void handle(Matrix3x2fStack matrices, float width, float height) {
        AnimateModule module = animateModule2;
        if (module == null) {
            return;
        }
        float scale = module.getContainerScale();
        matrices.translate(width / 2.0f, height / 2.0f);
        matrices.scale(scale, scale);
        matrices.translate(-width / 2.0f, -height / 2.0f);
    }

    public static float getFloatType2() {
        AnimateModule module = animateModule2;
        return module == null ? 0.0f : (float)module.duration.getValue();
    }

    private static float compute5(AnimateModule module) {
        float progress = module.containerAnimation.getPrimaryProgress();
        return Math.max(0.0f, Math.min(1.0f, progress));
    }

    public static int compute6(int x) {
        AnimateModule module = animateModule2;
        if (module == null || !module.isAnimatingContainer()) {
            return x;
        }
        float center = (float)class_310.method_1551().method_22683().method_4486() / 2.0f;
        float scale = Math.max(0.001f, module.getContainerScale());
        return Math.round(center + ((float)x - center) / scale);
    }

    public static int compute7(int y) {
        AnimateModule module = animateModule2;
        if (module == null || !module.isAnimatingContainer()) {
            return y;
        }
        float center = (float)class_310.method_1551().method_22683().method_4502() / 2.0f;
        float scale = Math.max(0.001f, module.getContainerScale());
        return Math.round(center + ((float)y - center) / scale);
    }

    private float getContainerScale() {
        float progress = this.containerAnimation.getPrimaryProgress();
        if (this.closingScreen != null) {
            return 1.0f - Easing.EASE_OUT_CUBIC.apply(1.0f - progress);
        }
        if (this.bounce.isEnabled()) {
            return Easing.EASE_OUT_BACK.apply(progress);
        }
        return Easing.EASE_OUT_CUBIC.apply(progress);
    }

    public static boolean isEnabled2() {
        AnimateModule module = animateModule2;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        Objects.requireNonNull(module);
        return module.isTargetEnabled("Chat");
    }

    public static float getFloatType4() {
        AnimateModule module = animateModule2;
        return module != null && module.isAnimatingContainer() ? module.getContainerScale() : 1.0f;
    }
}

