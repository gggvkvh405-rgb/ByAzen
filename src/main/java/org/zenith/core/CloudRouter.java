package org.zenith.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class CloudRouter {
   public final Predicate<BotFeaturesDto> ContainerHelper;
   public final Map<Class<? extends CloudResponse>, CloudRouteHandler<?>> Debug = new HashMap<>();

   public CloudRouter(Predicate<BotFeaturesDto> var1) {
      this.ContainerHelper = Objects.requireNonNull(var1, "beforeDispatch");
   }

   public <T extends CloudResponse> CloudRouter on23(Class<T> var1, BiConsumer<BotFeaturesDto, T> var2) {
      Objects.requireNonNull(var1, "packetType");
      Objects.requireNonNull(var2, "handler");
      if (this.Debug.putIfAbsent(var1, new CloudRouteHandler<>(var1, var2)) != null) {
         throw new IllegalStateException("Handler is already registered for " + var1.getName());
      } else {
         return this;
      }
   }

   public void InventoryUtils(BotFeaturesDto var1) {
      Objects.requireNonNull(var1, "envelope");
      if (this.ContainerHelper.test(var1)) {
         CloudRouteHandler ililii1l1li1i1l1iii1lllli1111_ii1il11l111ii11iil = this.Debug.get(var1.BotActivity().getClass());
         if (ililii1l1li1i1l1iii1lllli1111_ii1il11l111ii11iil != null) {
            ililii1l1li1i1l1iii1lllli1111_ii1il11l111ii11iil.BotFeatureRegistry(var1);
         } else if (!var1.MenuEaseF()) {
            throw new ServiceException("UNHANDLED_PACKET", "No handler registered for " + var1.type(), false);
         }
      }
   }
}
