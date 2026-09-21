/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFutureListener
 *  net.minecraft.class_2535
 *  net.minecraft.class_2547
 *  net.minecraft.class_2596
 *  net.minecraft.class_2598
 *  net.minecraft.class_8697
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.byazen.mixin;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_8697;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.byazen.ByAzenClient;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.event.OutgoingPacketEvent;

@Mixin(value={class_2535.class})
public abstract class ClientConnectionEventMixin {
    @Inject(method={"method_52906"}, at={@At(value="HEAD")}, cancellable=true)
    private void byazen$onPacketSend(class_2596<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo callback) {
        class_2535 connection = (class_2535)((Object)this);
        if (connection.method_36121() != class_2598.field_11942) {
            return;
        }
        EventBus eventBus = ByAzenClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        OutgoingPacketEvent event = new OutgoingPacketEvent(packet);
        eventBus.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_10759"}, at={@At(value="HEAD")}, cancellable=true)
    private static <T extends class_2547> void byazen$onPacketReceive(class_2596<T> packet, class_2547 listener, CallbackInfo callback) {
        if (!(listener instanceof class_8697)) {
            return;
        }
        EventBus eventBus = ByAzenClient.getEventBus();
        if (eventBus == null) {
            return;
        }
        IncomingPacketEvent event = new IncomingPacketEvent(packet);
        eventBus.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
}

