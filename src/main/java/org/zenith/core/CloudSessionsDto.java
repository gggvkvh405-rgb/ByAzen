package org.zenith.core;

import java.util.List;
import java.util.UUID;

public record CloudSessionsDto(UUID ClickFxController, List<CloudEntitlementsDto> CryptoUtils) implements CloudResponse {
   public CloudSessionsDto {
      CryptoUtils = List.copyOf(CryptoUtils);
   }

   @Override
   public String type() {
      return "config.codes";
   }

   public UUID PermissionListCodec() {
      return this.ClickFxController;
   }

   public List<CloudEntitlementsDto> HudInfoBoxPrimary() {
      return this.CryptoUtils;
   }
}
