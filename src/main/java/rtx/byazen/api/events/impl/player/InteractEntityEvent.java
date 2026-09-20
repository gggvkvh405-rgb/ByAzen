package rtx.byazen.api.events.impl.player;
import net.minecraft.entity.Entity;
import rtx.byazen.api.events.CancellableEvent;

public final class InteractEntityEvent
extends CancellableEvent {
    private final Entity entity;

    public InteractEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

