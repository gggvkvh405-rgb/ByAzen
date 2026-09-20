package org.zenith.core;

import java.util.function.BiConsumer;

record CloudRouteHandler<T extends CloudResponse>(Class<T> ElytraHelper, BiConsumer<BotFeaturesDto, T> Emotes) {
   public void BotFeatureRegistry(BotFeaturesDto var1) {
      this.Emotes.accept(var1, this.ElytraHelper.cast(var1.BotActivity()));
   }

   public Class<T> HolyWorldClient() {
      return this.ElytraHelper;
   }

   public BiConsumer<BotFeaturesDto, T> RotationQueue() {
      return this.Emotes;
   }
}
