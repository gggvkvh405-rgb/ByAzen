package org.zenith.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class EventPushOutOfBlocks extends EventCancellable {
   public PushCollisionType collisionType;

   public PushCollisionType BoatLongJump() {
      return this.collisionType;
   }

   public EventPushOutOfBlocks(PushCollisionType collisionType) {
      this.collisionType = collisionType;
   }
}
