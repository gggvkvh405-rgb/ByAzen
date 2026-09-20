package org.zenith.core;

import java.util.List;
import java.util.UUID;

public record CloudBadgesDto(UUID ShapeRenderer, List<CloudBadgeDto> ShaderWrapper, long LineShader) implements CloudResponse {
   public CloudBadgesDto {
      ShaderWrapper = List.copyOf(ShaderWrapper);
   }

   @Override
   public String type() {
      return "friends.added";
   }

   public UUID Event05() {
      return this.ShapeRenderer;
   }

   public List<CloudBadgeDto> SettingGroup() {
      return this.ShaderWrapper;
   }

   public long RenderTickEvent() {
      return this.LineShader;
   }
}
