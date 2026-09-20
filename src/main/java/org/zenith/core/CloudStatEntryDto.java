package org.zenith.core;

import com.google.gson.JsonObject;
import java.util.Objects;

public record CloudStatEntryDto(String string46, long long108, JsonObject jsonObject3) {
   public CloudStatEntryDto {
      Objects.requireNonNull(string46, "userId");
      jsonObject3 = jsonObject3.deepCopy();
   }

   public String userId() {
      return this.string46;
   }

   public long EventRender() {
      return this.long108;
   }

   public JsonObject MenuEaseA() {
      return this.jsonObject3;
   }
}
