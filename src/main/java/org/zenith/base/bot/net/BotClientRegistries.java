package org.zenith.base.bot.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registry.PendingTagLoad;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryLoader.ElementsAndTags;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.SerializableRegistries.SerializedRegistryEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer.Serialized;
import net.minecraft.resource.ResourceFactory;

final class BotClientRegistries {
   public final Map<RegistryKey<? extends Registry<?>>, List<SerializedRegistryEntry>> dynamicRegistries = new HashMap<>();
   public final Map<RegistryKey<? extends Registry<?>>, Serialized> tags = new HashMap<>();

   void putDynamicRegistry(RegistryKey<? extends Registry<?>> var1, List<SerializedRegistryEntry> var2) {
      this.dynamicRegistries.computeIfAbsent(var1, var0 -> new ArrayList<>()).addAll(var2);
   }

   void putTags(Map<RegistryKey<? extends Registry<?>>, Serialized> var1) {
      this.tags.putAll(var1);
   }

   Immutable createRegistryManager(ResourceFactory var1, Immutable var2) {
      if (this.dynamicRegistries.isEmpty()) {
         return var2;
      }

      CombinedDynamicRegistries combineddynamicregistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
      Immutable immutable = combineddynamicregistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
      Map<RegistryKey<? extends Registry<?>>, ElementsAndTags> hashmap = new HashMap<>();
      this.dynamicRegistries.forEach((var1x, var2x) -> hashmap.put((RegistryKey<? extends Registry<?>>)var1x, new ElementsAndTags(var2x, Serialized.NONE)));
      ArrayList arraylist = new ArrayList();
      this.tags
         .forEach(
            (var3x, var4x) -> {
               if (!var4x.isEmpty()) {
                  if (SerializableRegistries.isSynced(var3x)) {
                     hashmap.compute(
                        (RegistryKey<? extends Registry<?>>)var3x, (var1xx, var2xx) -> new ElementsAndTags(var2xx != null ? var2xx.elements() : List.of(), var4x)
                     );
                  } else {
                     arraylist.add(startTagReload(immutable, var3x, var4x));
                  }
               }
            }
         );
      List list = TagGroupLoader.collectRegistries(immutable, arraylist);
      Immutable immutable1 = RegistryLoader.loadFromNetwork(hashmap, var1, list, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
      return combineddynamicregistries.with(ClientDynamicRegistryType.REMOTE, new Immutable[]{immutable1}).getCombinedRegistryManager().toImmutable();
   }

   public static <T> PendingTagLoad<T> startTagReload(Immutable var0, RegistryKey<? extends Registry<? extends T>> var1, Serialized var2) {
      Registry registry = var0.getOrThrow(var1);
      return registry.startTagReload(var2.toRegistryTags(registry));
   }
}
