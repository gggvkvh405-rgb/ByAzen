package org.zenith.core;

public record CloudMediaDto(MediaTrackInfo Interface) implements CloudResponse {
   @Override
   public String type() {
      return "chat.message.accepted";
   }

   public MediaTrackInfo SoundManager() {
      return this.Interface;
   }
}
