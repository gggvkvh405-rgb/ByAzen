package org.zenith.core;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.zenith.managers.EmoteMetadata;

/** A PAL controller that plays one Zenith emote from an optional tick offset. */
public final class EmotePlayback extends PlayerAnimationController {
   private final EmoteMetadata emote;

   public EmotePlayback(AbstractClientPlayerEntity player, EmoteMetadata emote, int startTick) {
      super(player, (controller, state, animationSetter) -> PlayState.STOP);
      this.emote = emote;
      this.triggerAnimation(emote.animation(), Math.max(0, startTick));
   }

   public EmoteMetadata emote() {
      return this.emote;
   }
}
