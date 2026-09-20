package org.zenith.base.bot.via;

import com.mojang.logging.LogUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import java.util.Objects;
import org.slf4j.Logger;

final class BotViaVersionProvider extends BaseVersionProvider {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final VersionProvider delegate;

   BotViaVersionProvider(VersionProvider var1) {
      this.delegate = var1;
   }

   @Override
   public ProtocolVersion getClosestServerProtocol(UserConnection var1) throws Exception {
      Integer integer = var1.isClientSide() ? BotVia.targetProtocol(var1.getChannel()) : null;
      if (integer != null) {
         ProtocolInfo protocolinfo = Objects.requireNonNull(var1.getProtocolInfo());
         if (!ProtocolVersion.isRegistered(integer)) {
            LOGGER.warn("Bot ViaVersion: protocol {} is not registered, using native", integer);
            return protocolinfo.protocolVersion();
         } else {
            ProtocolVersion protocolversion = ProtocolVersion.getProtocol(integer);
            boolean flag = protocolversion.equals(protocolinfo.protocolVersion())
               || Via.getManager().getProtocolManager().getProtocolPath(protocolinfo.protocolVersion(), protocolversion) != null;
            if (!flag) {
               LOGGER.warn("Bot ViaVersion: no protocol path {} -> {}, using native", protocolinfo.protocolVersion(), protocolversion);
               return protocolinfo.protocolVersion();
            } else {
               LOGGER.info("Bot ViaVersion: translating {} -> {}", protocolinfo.protocolVersion(), protocolversion);
               return protocolversion;
            }
         }
      } else if (this.delegate != null) {
         return this.delegate.getClosestServerProtocol(var1);
      } else {
         return var1.isClientSide() ? Objects.requireNonNull(var1.getProtocolInfo()).protocolVersion() : super.getClosestServerProtocol(var1);
      }
   }
}
