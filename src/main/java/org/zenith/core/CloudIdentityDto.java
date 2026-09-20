package org.zenith.core;

import java.util.UUID;

public record CloudIdentityDto(UUID TextureIdFactory, String ArgbColor) implements CloudResponse {
   @Override
   public String type() {
      return "friends.request.accepted";
   }

   public UUID Event05() {
      return this.TextureIdFactory;
   }

   public String ThemeColorCycler() {
      return this.ArgbColor;
   }
}
