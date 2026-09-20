package org.zenith.config;

import org.zenith.managers.EmoteMetadata;

record EmoteSyncState(EmoteMetadata var153, int int139, long long97, long long98, EmoteLoopMode var15Var160) {
   public EmoteMetadata EventTracker() {
      return this.var153;
   }

   public int Emotes() {
      return this.int139;
   }

   public long FakePlayer() {
      return this.long97;
   }

   public long ContainerHelper() {
      return this.long98;
   }

   public EmoteLoopMode FastBreak() {
      return this.var15Var160;
   }
}
