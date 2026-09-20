package org.zenith.core;

import java.util.Objects;

public record CloudNoticeDto(String Chams) implements CloudResponse {
   public CloudNoticeDto {
      Objects.requireNonNull(Chams, "answer");
   }

   @Override
   public String type() {
      return "captcha.solved";
   }

   public String RemoteEventsPoller() {
      return this.Chams;
   }
}
