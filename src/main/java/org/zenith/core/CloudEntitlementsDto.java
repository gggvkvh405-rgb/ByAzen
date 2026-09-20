package org.zenith.core;

import java.util.UUID;

public record CloudEntitlementsDto(
   UUID Menu, UUID NoRender, String Particles, String ShaderHand, long SwingAnimation, String TargetESP, Long TotemParticles, Long TotemPop
) {
   public UUID InventoryCodec() {
      return this.Menu;
   }

   public UUID PermissionListCodec() {
      return this.NoRender;
   }

   public String PlayerStateService() {
      return this.Particles;
   }

   public String ThemeColorCycler() {
      return this.ShaderHand;
   }

   public long RenderTickEvent() {
      return this.SwingAnimation;
   }

   public String EmoteRegistry() {
      return this.TargetESP;
   }

   public Long UserdataManager() {
      return this.TotemParticles;
   }

   public Long ArmorHud() {
      return this.TotemPop;
   }
}
