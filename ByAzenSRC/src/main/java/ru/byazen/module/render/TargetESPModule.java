/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 */
package ru.byazen.module.render;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import ru.byazen.ByAzenClient;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.event.WorldSessionEvent;
import ru.byazen.misc.TargetEspEffect;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.module.combat.AttackAuraModule;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ColorSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.util.AuraTargetEspRenderer;
import ru.byazen.util.CylinderTargetEspRenderer;
import ru.byazen.util.MarkerTargetEspRenderer;
import ru.byazen.util.SkullTargetEspRenderer;
import ru.byazen.util.SwordTargetEspRenderer;

public final class TargetESPModule
extends Module
implements ConfigSerializable {
    private static final String SWORD = "Sword";
    private static volatile TargetESPModule instance;
    private final BooleanSetting enabledSetting;
    private final ModeSetting mode;
    private final ColorSetting color;
    private final BooleanSetting useAttackAuraTarget;
    private final Map<String, TargetEspEffect> renderers;
    private WorldRenderEvent pendingWorldRender;
    private AttackAuraModule attackAura;
    private class_1309 lastTarget;
    private String lastMode;

    public TargetESPModule(EventBus eventBus) {
        super(eventBus, "target_esp", "Target ESP", "\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u0430\u0442\u0430\u043a\u0443\u0435\u043c\u043e\u0439 \u0446\u0435\u043b\u0438", ModuleCategory.valueOf("RENDER"), new String[0]);
        instance = this;
        this.renderers = this.createRenderers();
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0443 \u0446\u0435\u043b\u0438").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options(SWORD, "Cylinder", "Marker", "Skull", "Aura").defaultOption(SWORD).name("Mode").id("mode").description("\u0421\u0442\u0438\u043b\u044c \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438")).build();
        this.registerSetting(this.mode);
        ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color").id("color").description("\u0426\u0432\u0435\u0442 Target ESP")).build();
        colorSetting.setPrimaryColor(0, -11753627);
        colorSetting.setPrimaryColor(1, -1543135);
        colorSetting.setPrimaryColor(2, -9279489);
        colorSetting.setPrimaryColor(3, -46001);
        colorSetting.setPrimaryColor(4, -13218);
        colorSetting.setPrimaryColor(5, -10582785);
        colorSetting.setPrimaryColor(6, -2732032);
        this.color = colorSetting;
        this.registerSetting(colorSetting);
        this.useAttackAuraTarget = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Attack Aura only").id("use_attack_aura_target").description("\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438 Attack Aura")).build();
        this.registerSetting(this.useAttackAuraTarget);
    }

    @Override
    protected void initialize() {
        this.listen(EntityAttackEvent.class, this::onAttack);
        this.listen(WorldRenderEvent.class, this::onWorldRender);
        this.listen(WorldSessionEvent.class, event -> this.resetRenderers());
    }

    public static void tick3() {
        TargetESPModule module = instance;
        if (module != null) {
            module.flushPendingRender();
        }
    }

    private void onAttack(EntityAttackEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_1297 class_12972 = event.getEntity();
        if (class_12972 instanceof class_1309) {
            class_1309 livingEntity;
            this.lastTarget = livingEntity = (class_1309)class_12972;
        }
        this.currentRenderer().setEntityAttackEvent(event);
    }

    private void onWorldRender(WorldRenderEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.resetRenderers();
            this.pendingWorldRender = null;
            return;
        }
        if (this.currentRenderer().isActive()) {
            this.pendingWorldRender = event;
        } else {
            this.pendingWorldRender = null;
            this.render(event);
        }
    }

    private void flushPendingRender() {
        WorldRenderEvent event = this.pendingWorldRender;
        this.pendingWorldRender = null;
        if (event == null || !this.enabledSetting.isEnabled()) {
            return;
        }
        this.render(event);
    }

    private void render(WorldRenderEvent event) {
        this.syncMode();
        this.auraTarget();
        this.currentRenderer().setWorldRenderEvent(event);
    }

    private void syncMode() {
        String selected = this.mode.getSelectedOption();
        if (selected == null) {
            return;
        }
        if (this.lastMode == null) {
            this.lastMode = selected;
            return;
        }
        if (!selected.equals(this.lastMode)) {
            this.resetRenderers();
            this.lastMode = selected;
        }
    }

    private void resetRenderers() {
        this.renderers.values().forEach(TargetEspEffect::update);
        this.lastTarget = null;
    }

    private TargetEspEffect currentRenderer() {
        return this.renderers.getOrDefault(this.mode.getSelectedOption(), this.renderers.get(SWORD));
    }

    private Map<String, TargetEspEffect> createRenderers() {
        LinkedHashMap<String, TargetEspEffect> map = new LinkedHashMap<String, TargetEspEffect>();
        map.put(SWORD, new SwordTargetEspRenderer());
        map.put("Cylinder", new CylinderTargetEspRenderer());
        map.put("Marker", new MarkerTargetEspRenderer());
        map.put("Skull", new SkullTargetEspRenderer());
        map.put("Aura", new AuraTargetEspRenderer());
        return map;
    }

    private AttackAuraModule attackAura() {
        if (this.attackAura != null) {
            return this.attackAura;
        }
        if (ByAzenClient.getInstance() == null || ByAzenClient.getInstance().getModuleManager() == null) {
            return null;
        }
        this.attackAura = ByAzenClient.getInstance().getModuleManager().getModule(AttackAuraModule.class);
        return this.attackAura;
    }

    private class_1309 auraTarget() {
        if (!this.useAttackAuraTarget.isEnabled()) {
            return null;
        }
        AttackAuraModule module = this.attackAura();
        return module != null ? module.getLivingEntity() : null;
    }

    public static TargetESPModule getInstance() {
        return instance;
    }

    public class_1309 getCurrentTarget() {
        class_1309 target;
        class_1309 aura = this.auraTarget();
        class_1309 class_13092 = target = aura != null ? aura : this.lastTarget;
        if (target != null && (!target.method_5805() || target.method_31481())) {
            this.lastTarget = null;
            return null;
        }
        return target;
    }

    public int getPrimaryColor() {
        return this.color.getColor(0.0f);
    }

    public int getSecondaryColor() {
        return this.color.getColor(0.5f);
    }
}

