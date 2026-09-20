package org.zenith.core;

public final class ServiceException extends RuntimeException {
   public final String PlayerStateService;
   public final boolean PetManager;

   public ServiceException(String var1, String var2, boolean var3) {
      super(var2);
      this.PlayerStateService = var1;
      this.PetManager = var3;
   }

   public String PlayerStateService() {
      return this.PlayerStateService;
   }

   public boolean PetManager() {
      return this.PetManager;
   }
}
