package org.zenith.addon.internal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.zenith.ZenithClient;
import org.zenith.addon.api.frontend.ConfigCatalog;
import org.zenith.addon.api.frontend.ConfigInfo;
import org.zenith.addon.api.frontend.ConfigLoadMode;
import org.zenith.addon.api.frontend.ConfigSaveOptions;
import org.zenith.core.CloudPoller;
import org.zenith.core.PollMode;

final class ZenithConfigCatalog implements ConfigCatalog {
   public List<ConfigInfo> configs() {
      CloudPoller liill1llill11i11il = this.manager();
      return liill1llill11i11il == null
         ? Collections.emptyList()
         : liill1llill11i11il.AutoAuth()
            .stream()
            .map(this::normalize)
            .filter(var0 -> !var0.isBlank())
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .map(var0 -> new ConfigInfo(var0, false))
            .toList();
   }

   public boolean save(String var1, ConfigSaveOptions var2) {
      CloudPoller liill1llill11i11il = this.manager();
      return liill1llill11i11il != null
         && var2 != null
         && liill1llill11i11il.on23(this.normalize(var1), var2.includeVisuals(), var2.includeOther(), var2.includeBinds());
   }

   public boolean load(String var1, ConfigLoadMode var2) {
      CloudPoller liill1llill11i11il = this.manager();
      if (liill1llill11i11il != null && var2 != null) {
         PollMode liill1llill11i11il_ii1il11l111ii11iil = switch (var2) {
            case ALL -> PollMode.call107;
            case IGNORE_BINDS -> PollMode.call137;
            case ONLY_VISUALS -> PollMode.getThis3;
            default -> throw new MatchException(null, null);
         };
         return liill1llill11i11il.on23(this.normalize(var1), liill1llill11i11il_ii1il11l111ii11iil);
      } else {
         return false;
      }
   }

   public boolean delete(String var1) {
      CloudPoller liill1llill11i11il = this.manager();
      return liill1llill11i11il != null && liill1llill11i11il.EmoteMetadata(this.normalize(var1));
   }

   public boolean rename(String var1, String var2) {
      CloudPoller liill1llill11i11il = this.manager();
      return liill1llill11i11il != null && liill1llill11i11il.NbtItemSpec(this.normalize(var1), this.normalize(var2));
   }

   public CloudPoller manager() {
      return ZenithClient.on23().TradeGuardService();
   }

   public String normalize(String var1) {
      return var1 == null ? "" : var1.replace("." + "Zenith".toLowerCase(Locale.ROOT), "").trim();
   }
}
