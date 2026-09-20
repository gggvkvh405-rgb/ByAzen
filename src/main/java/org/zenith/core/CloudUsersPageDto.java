package org.zenith.core;

import java.util.List;

public record CloudUsersPageDto(int TimerSpeed, List<CloudUserDto> BaritoneBridge, boolean Pathfinder, Integer AimUtils) implements CloudResponse {
   public CloudUsersPageDto {
      BaritoneBridge = List.copyOf(BaritoneBridge);
   }

   @Override
   public String type() {
      return "config.list";
   }

   public int MotorIntentModel() {
      return this.TimerSpeed;
   }

   public List<CloudUserDto> configs() {
      return this.BaritoneBridge;
   }

   public boolean hasMore() {
      return this.Pathfinder;
   }

   public Integer HudElementMessage() {
      return this.AimUtils;
   }
}
