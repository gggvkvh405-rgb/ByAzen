package org.zenith.core;

public record CloudMediaEntryDto(MediaTrackInfo JumpCircle) implements CloudResponse {
   @Override
   public String type() {
      return "chat.message.received";
   }

   public MediaTrackInfo SoundManager() {
      return this.JumpCircle;
   }
}
