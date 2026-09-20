package rtx.kimiko.api.events.impl.player;
import net.minecraft.entity.player.PlayerEntity;
import rtx.kimiko.api.events.CancellableEvent;

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

