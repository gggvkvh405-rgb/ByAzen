package org.zenith.config;

import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.zenith.core.EmotePlayback;

record ActiveEmote(
   AbstractClientPlayerEntity player,
   AnimationContainer<EmotePlayback> layer,
   EmotePlayback playback,
   long sequence
) {
}
