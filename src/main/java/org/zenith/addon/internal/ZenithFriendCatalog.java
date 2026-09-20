package org.zenith.addon.internal;

import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.addon.api.frontend.FriendCatalog;
import org.zenith.core.FriendStore;

final class ZenithFriendCatalog implements FriendCatalog {
   public List<String> friends() {
      FriendStore lilii1lililli1liil1iiiill1l = this.manager();
      return lilii1lililli1liil1iiiill1l != null && lilii1lililli1liil1iiiill1l.getItems() != null
         ? lilii1lililli1liil1iiiill1l.getItems()
            .stream()
            .filter(var0 -> var0 != null && !var0.isBlank())
            .map(String::trim)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList()
         : List.of();
   }

   public boolean isFriend(String var1) {
      FriendStore lilii1lililli1liil1iiiill1l = this.manager();
      String s = normalize(var1);
      return lilii1lililli1liil1iiiill1l != null && s != null && lilii1lililli1liil1iiiill1l.isFriend(s);
   }

   public boolean add(String var1) {
      FriendStore lilii1lililli1liil1iiiill1l = this.manager();
      String s = normalize(var1);
      if (lilii1lililli1liil1iiiill1l != null && s != null && findLocal(lilii1lililli1liil1iiiill1l, s) == null) {
         lilii1lililli1liil1iiiill1l.add(s);
         lilii1lililli1liil1iiiill1l.save();
         return true;
      } else {
         return false;
      }
   }

   public boolean remove(String var1) {
      FriendStore lilii1lililli1liil1iiiill1l = this.manager();
      String s = normalize(var1);
      if (lilii1lililli1liil1iiiill1l != null && s != null) {
         String s1 = findLocal(lilii1lililli1liil1iiiill1l, s);
         if (s1 == null) {
            return false;
         }

         lilii1lililli1liil1iiiill1l.ItemServiceBase(s1);
         lilii1lililli1liil1iiiill1l.save();
         return true;
      } else {
         return false;
      }
   }

   public FriendStore manager() {
      return ZenithClient.on23().MediaTrackInfo();
   }

   public static String findLocal(FriendStore var0, String var1) {
      return var0.getItems() == null
         ? null
         : var0.getItems().stream().filter(var1xx -> var1xx != null && var1xx.equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public static String normalize(String var0) {
      if (var0 == null) {
         return null;
      }

      String s = var0.trim();
      return !s.isEmpty() && s.length() <= 64 ? s : null;
   }
}
