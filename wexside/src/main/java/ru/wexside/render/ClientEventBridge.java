/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.class_1282
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_8143
 *  net.minecraft.class_9779
 */
package ru.wexside.render;

import java.io.IOException;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_8143;
import net.minecraft.class_9779;
import ru.wexside.WexSideClient;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.DamageEvent;
import ru.wexside.event.DamageType;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ConfigManager;
import ru.wexside.module.hud.AnimateModule;
import ru.wexside.module.misc.AutoConfigSaveModule;
import ru.wexside.module.render.BlockOverlayModule;
import ru.wexside.module.render.ColorCorrectionModule;
import ru.wexside.module.render.TargetESPModule;
import ru.wexside.module.render.TracersModule;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.render.RenderFrameState;
import ru.wexside.ui.WexsideScreen;
import ru.wexside.util.ClientClock;

public final class ClientEventBridge {
    private static class_332 pendingHudContext;
    private static class_9779 pendingHudTickCounter;

    private ClientEventBridge() {
    }

    public static void register(EventBus eventBus) {
        eventBus.subscribe(IncomingPacketEvent.class, event -> ClientEventBridge.onIncomingPacket(eventBus, event));
        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientEventBridge.onEndTick(eventBus, client));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ClientEventBridge.saveActiveConfig());
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            RenderFrameClock.advanceFrame();
            pendingHudContext = context;
            pendingHudTickCounter = tickCounter;
        });
        WorldRenderEvents.END_EXTRACTION.register(context -> RenderFrameState.update(context.camera().method_71156(), context.cullProjectionMatrix(), context.viewMatrix()));
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> !BlockOverlayModule.isEnabled());
        WorldRenderEvents.END_MAIN.register(context -> {
            eventBus.post(new WorldRenderEvent(context.matrices(), ClientClock.tickDelta()));
            TargetESPModule.tick3();
            TracersModule.tick2();
            ColorCorrectionModule.tick();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> eventBus.post(new WorldSessionEvent(WorldSessionEvent.Change.JOINED)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            RenderFrameState.clear();
            eventBus.post(new WorldSessionEvent(WorldSessionEvent.Change.DISCONNECTED));
        });
    }

    public static void renderHudAfterVanillaGui() {
        class_332 context = pendingHudContext;
        class_9779 tickCounter = pendingHudTickCounter;
        pendingHudContext = null;
        pendingHudTickCounter = null;
        EventBus eventBus = WexSideClient.getEventBus();
        if (eventBus != null && context != null && tickCounter != null) {
            AnimateModule.onHudRender(context);
            if (!(class_310.method_1551().field_1755 instanceof WexsideScreen)) {
                eventBus.post(new HudRenderEvent(context, tickCounter));
            }
        }
    }

    private static void onIncomingPacket(EventBus eventBus, IncomingPacketEvent event) {
        class_8143 damage;
        class_1297 entity;
        class_2663 status;
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return;
        }
        class_2596<?> class_25962 = event.getPacket();
        if (class_25962 instanceof class_2663 && (status = (class_2663)class_25962).method_11470() == 35) {
            entity = status.method_11469((class_1937)client.field_1687);
            if (entity != null) {
                eventBus.post(new TotemPopEvent(entity, new class_243(entity.method_23317(), entity.method_23318() + (double)entity.method_17682() * 0.5, entity.method_23321()), System.currentTimeMillis()));
            }
            return;
        }
        if (client.field_1724 != null && event.getPacket() instanceof class_8143 && (damage = (class_8143)event.getPacket()).comp_1267() == client.field_1724.method_5628()) {
            class_1282 source = damage.method_49071((class_1937)client.field_1687);
            DamageType type = source.method_5526() != null && source.method_5529() != null && source.method_5526() != source.method_5529() ? DamageType.PROJECTILE : (source.method_5529() != null ? DamageType.DIRECT : DamageType.ENVIRONMENTAL);
            eventBus.post(new DamageEvent(type));
        }
    }

    private static void onEndTick(EventBus eventBus, class_310 client) {
        ClientClock.advanceTick();
        if (client.field_1724 != null && client.field_1687 != null) {
            eventBus.post(new ClientTickEvent());
        }
    }

    private static void saveActiveConfig() {
        if (!AutoConfigSaveModule.isActive()) {
            return;
        }
        ConfigManager configManager = WexSideClient.getConfigManager();
        if (configManager == null || configManager.hasPendingImportedEntries()) {
            return;
        }
        String profile = configManager.getCurrentProfileName();
        if (profile == null || profile.isBlank()) {
            profile = "default";
        }
        try {
            configManager.saveProfile(profile);
        }
        catch (IOException exception) {
            WexSideClient.getInstance().getLogger().warn("Unable to auto-save profile {}", (Object)profile, (Object)exception);
        }
    }
}

