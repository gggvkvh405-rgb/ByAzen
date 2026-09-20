package org.zenith.core;

public record CloudTagDto(String KillEffect) {
   public String ServerTheme() {
      return this.KillEffect;
   }
}
