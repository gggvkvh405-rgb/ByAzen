package rtx.byazen.api.events.impl.player;
import net.minecraft.entity.player.PlayerEntity;
import rtx.byazen.api.events.CancellableEvent;

public final class JumpEvent
extends CancellableEvent {
    private final PlayerEntity player;

    public JumpEvent(PlayerEntity playerEntity) {
        this.player = playerEntity;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}

