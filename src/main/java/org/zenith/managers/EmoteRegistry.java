package org.zenith.managers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.zenith.ZenithClient;
import org.zenith.config.CosmeticManager;
import org.zenith.config.EmoteLoopMode;

public final class EmoteRegistry {
   public static List<String> map56() {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill == null
         ? List.of()
         : illlillllllliili1li11i11lill.AutoTool().ids().stream().filter(EmoteRegistry::Event37).toList();
   }

   public static Collection<EmoteMetadata> set19() {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill == null
         ? List.of()
         : illlillllllliili1li11i11lill.AutoTool().all().stream().filter(var0x -> Event37(var0x.id())).toList();
   }

   public static Optional<EmoteMetadata> Event18Ext5(String var0) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill != null && Event37(var0) ? illlillllllliili1li11i11lill.AutoTool().find(var0) : Optional.empty();
   }

   public static boolean Event05(String var0) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill != null && illlillllllliili1li11i11lill.AutoTool().find(var0).isPresent();
   }

   public static boolean BotChatEvent(String var0) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill != null && Event37(var0) && illlillllllliili1li11i11lill.BotChatEvent(var0);
   }

   public static boolean on23(UUID var0, String var1, int var2) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill != null && illlillllllliili1li11i11lill.on23(var0, var1, var2, EmoteLoopMode.call009);
   }

   public static void on23(UUID var0, String var1, long var2, int var4) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      if (illlillllllliili1li11i11lill != null) {
         illlillllllliili1li11i11lill.on23(var0, var1, var2, var4);
      }
   }

   public static void ItemSpec(UUID var0) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      if (illlillllllliili1li11i11lill != null) {
         illlillllllliili1li11i11lill.ItemSpec(var0);
      }
   }

   public static boolean TextScanner(UUID var0) {
      CosmeticManager illlillllllliili1li11i11lill = set20();
      return illlillllllliili1li11i11lill != null && illlillllllliili1li11i11lill.TextScanner(var0);
   }

   public static CosmeticManager set20() {
      ZenithClient ii1il11l111ii11iil = ZenithClient.on23();
      return ii1il11l111ii11iil == null ? null : ii1il11l111ii11iil.SimpleItemBuilder();
   }

   public static boolean Event37(String var0) {
      ZenithClient ii1il11l111ii11iil = ZenithClient.on23();
      return ii1il11l111ii11iil != null && ii1il11l111ii11iil.getCloudClient() != null && ii1il11l111ii11iil.getCloudClient().EnchantItemSpec(var0);
   }
}
