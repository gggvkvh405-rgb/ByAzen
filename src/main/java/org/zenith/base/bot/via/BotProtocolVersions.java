package org.zenith.base.bot.via;

import java.util.List;
import net.minecraft.SharedConstants;

public final class BotProtocolVersions {
   public static final int NATIVE = -1;
   public static final List<BotProtocolVersions_Entry> ENTRIES = List.of(
      new BotProtocolVersions_Entry(-1, SharedConstants.getGameVersion().name()),
      new BotProtocolVersions_Entry(768, "1.21.3"),
      new BotProtocolVersions_Entry(767, "1.21.1"),
      new BotProtocolVersions_Entry(766, "1.20.6"),
      new BotProtocolVersions_Entry(765, "1.20.4"),
      new BotProtocolVersions_Entry(764, "1.20.2"),
      new BotProtocolVersions_Entry(763, "1.20.1"),
      new BotProtocolVersions_Entry(762, "1.19.4"),
      new BotProtocolVersions_Entry(761, "1.19.3"),
      new BotProtocolVersions_Entry(760, "1.19.2"),
      new BotProtocolVersions_Entry(759, "1.19"),
      new BotProtocolVersions_Entry(758, "1.18.2"),
      new BotProtocolVersions_Entry(757, "1.18.1"),
      new BotProtocolVersions_Entry(756, "1.17.1"),
      new BotProtocolVersions_Entry(755, "1.17"),
      new BotProtocolVersions_Entry(754, "1.16.5"),
      new BotProtocolVersions_Entry(753, "1.16.3"),
      new BotProtocolVersions_Entry(751, "1.16.2"),
      new BotProtocolVersions_Entry(736, "1.16.1"),
      new BotProtocolVersions_Entry(578, "1.15.2"),
      new BotProtocolVersions_Entry(498, "1.14.4"),
      new BotProtocolVersions_Entry(477, "1.14")
   );

   public static String label(int var0) {
      for (BotProtocolVersions_Entry botprotocolversions_entry : ENTRIES) {
         if (botprotocolversions_entry.protocol() == var0) {
            return botprotocolversions_entry.label();
         }
      }

      return var0 <= 0 ? ENTRIES.get(0).label() : "#" + var0;
   }
}
