package rtx.byazen.api.events.impl.network;
import net.minecraft.network.packet.Packet;
import rtx.byazen.api.events.CancellableEvent;

public final class PacketSendEvent
extends CancellableEvent {
    private Packet<?> packet;

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public <T extends Packet<?>> T getPacketAs(Class<T> clazz) {
        return (T)(clazz.isInstance(this.packet) ? this.packet : null);
    }
}

