package rtx.byazen.api.events.impl.player;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import rtx.byazen.api.events.Event;

/**
 * Событие разрушения блока игроком (идея №64 из IDEAS.md): на нём строятся свои осколки
 * и дополнительная пыль вместо стандартной текстуры разрушения.
 */
public final class BlockBreakEvent
extends Event {

    private final BlockPos pos;
    private final BlockState state;

    public BlockBreakEvent(BlockPos blockPos, BlockState blockState) {
        this.pos = blockPos;
        this.state = blockState;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }
}
