package org.zenith.core;

import java.util.UUID;

public record CloudPairDto(UUID ShaderFog, UUID ShaderPostProcess, boolean TranslationKey) implements CloudResponse {
   @Override
   public String type() {
      return "config.code.revoked";
   }

   public UUID PermissionListCodec() {
      return this.ShaderFog;
   }

   public UUID InventoryCodec() {
      return this.ShaderPostProcess;
   }

   public boolean KeybindsHud() {
      return this.TranslationKey;
   }
}
