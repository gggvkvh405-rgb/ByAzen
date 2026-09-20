package org.zenith.addon.internal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.zenith.ZenithClient;
import org.zenith.addon.api.frontend.ModuleAccess;
import org.zenith.addon.api.frontend.ModuleCatalog;

final class ZenithModuleCatalog implements ModuleCatalog {
   public List<? extends ModuleAccess> modules() {
      return ZenithClient.on23().ColorAnimator() == null
         ? Collections.emptyList()
         : ZenithClient.on23().ColorAnimator().PacketDispatcher().stream().map(ZenithModuleAccess::new).toList();
   }

   public Optional<? extends ModuleAccess> find(String var1) {
      return var1 != null && ZenithClient.on23().ColorAnimator() != null
         ? ZenithClient.on23()
            .ColorAnimator()
            .PacketDispatcher()
            .stream()
            .filter(var1xx -> var1xx.getId().equals(var1))
            .findFirst()
            .map(ZenithModuleAccess::new)
         : Optional.empty();
   }
}
