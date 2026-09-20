package org.zenith.core;

import java.util.Map;

public record CloudSessionDto(String string43, String string44, Map<String, String> map21, long long104) {
   public CloudSessionDto {
      map21 = Map.copyOf(map21);
   }

   public String Backtrack() {
      return this.string43;
   }

   public String url() {
      return this.string44;
   }

   public Map<String, String> Blink() {
      return this.map21;
   }

   public long expiresAt() {
      return this.long104;
   }
}
