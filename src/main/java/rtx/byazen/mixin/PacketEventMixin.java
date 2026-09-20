package rtx.byazen.mixin;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.network.PacketEvent;
import rtx.byazen.api.events.impl.network.PacketEvent.Direction;
import rtx.byazen.api.events.impl.network.PacketReceiveEvent;
import rtx.byazen.api.events.impl.network.PacketSendEvent;
import rtx.byazen.utils.network.Network;

@Mixin(net.minecraft.network.ClientConnection.class)

public abstract class PacketEventMixin {
    @Inject(method="send", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_onPacketSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent unified;
        PacketSendEvent sendEvent;
        EventBus bus = EventBus.get();
        if (bus.hasListeners(PacketSendEvent.class) && (sendEvent = bus.post(new PacketSendEvent(packet))).isCancelled()) {
            ci.cancel();
            return;
        }
        if (bus.hasListeners(PacketEvent.class) && (unified = bus.post(new PacketEvent(packet, PacketEvent.Direction.SEND))).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method="channelRead0", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_onPacketReceive(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        PacketEvent unified;
        PacketReceiveEvent receiveEvent;
        EventBus bus = EventBus.get();
        if (bus.hasListeners(PacketReceiveEvent.class) && (receiveEvent = bus.post(new PacketReceiveEvent(packet))).isCancelled()) {
            ci.cancel();
            return;
        }
        if (bus.hasListeners(PacketEvent.class) && (unified = bus.post(new PacketEvent(packet, PacketEvent.Direction.RECEIVE))).isCancelled()) {
            ci.cancel();
            return;
        }
        Network.handlePacket(packet);
    }
}

