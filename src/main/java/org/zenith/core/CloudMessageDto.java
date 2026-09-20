package org.zenith.core;

public record CloudMessageDto(String string51, String string52, boolean boolean112, Long long110) implements CloudResponse {
   public CloudMessageDto(String var1, String var2, boolean var3) {
      this(var1, var2, var3, null);
   }

   @Override
   public String type() {
      return "system.error";
   }

   public String PlayerStateService() {
      return this.string51;
   }

   public String message() {
      return this.string52;
   }

   public boolean PetManager() {
      return this.boolean112;
   }

   public Long TrapTp() {
      return this.long110;
   }
}
