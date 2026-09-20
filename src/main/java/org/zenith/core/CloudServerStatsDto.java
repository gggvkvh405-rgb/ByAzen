package org.zenith.core;

import java.util.List;

public record CloudServerStatsDto(
   String BotFollowEntity, long BotEntity, long SpinMarker, long BotGotoEntity, int PositionProvider, long ParticleRenderer, List<String> ParticleTextures
) implements CloudResponse {
   public CloudServerStatsDto {
      ParticleTextures = List.copyOf(ParticleTextures);
   }

   @Override
   public String type() {
      return "connection.welcome";
   }

   public String serverVersion() {
      return this.BotFollowEntity;
   }

   public long Module() {
      return this.BotEntity;
   }

   public long ModuleInfo() {
      return this.SpinMarker;
   }

   public long Setting() {
      return this.BotGotoEntity;
   }

   public int BooleanSetting() {
      return this.PositionProvider;
   }

   public long ButtonSetting() {
      return this.ParticleRenderer;
   }

   public List<String> MenuEaseB() {
      return this.ParticleTextures;
   }
}
