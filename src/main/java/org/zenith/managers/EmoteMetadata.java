package org.zenith.managers;

import com.zigythebird.playeranimcore.animation.Animation;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Identifier;

/** Immutable metadata and animation data for a built-in emote. */
public record EmoteMetadata(
   String id,
   UUID animationUuid,
   String displayName,
   String author,
   Identifier icon,
   Animation animation
) {
   public EmoteMetadata {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(animationUuid, "animationUuid");
      Objects.requireNonNull(displayName, "displayName");
      Objects.requireNonNull(author, "author");
      Objects.requireNonNull(icon, "icon");
      Objects.requireNonNull(animation, "animation");
   }
}
