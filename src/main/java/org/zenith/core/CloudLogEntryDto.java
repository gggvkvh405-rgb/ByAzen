package org.zenith.core;

import com.google.gson.JsonObject;
import java.util.Objects;

public record CloudLogEntryDto(String string45, long long106, JsonObject jsonObject2) {
   public CloudLogEntryDto {
      Objects.requireNonNull(string45, "userId");
      jsonObject2 = jsonObject2.deepCopy();
   }

   public String userId() {
      return this.string45;
   }

   public long EventRender() {
      return this.long106;
   }

   public JsonObject MenuEaseE() {
      return this.jsonObject2;
   }
}
