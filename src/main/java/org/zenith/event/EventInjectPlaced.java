package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EventInjectPlaced implements Event {
   public final BlockPos pos;
   public final BlockState state;

   public EventInjectPlaced(BlockPos var1, BlockState var2) {
      this.pos = var1;
      this.state = var2;
   }

   public BlockState NoFriendDamage() {
      return this.state;
   }

   public BlockPos NameProtect() {
      return this.pos;
   }
}
