package org.zenith.base.bot.via;

import com.viaversion.vialoader.netty.ViaEncoder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

final class BotViaEncoder extends ViaEncoder {
   BotViaEncoder(UserConnection var1) {
      super(var1);
   }

   @Override
   protected void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      super.encode(var1, var2, var3);
      ProtocolPipeline protocolpipeline = this.connection.getProtocolInfo().getPipeline();
      if (protocolpipeline.hasNonBaseProtocols() && !protocolpipeline.contains(BotViaCompatibilityProtocol.class)) {
         protocolpipeline.add(BotViaCompatibilityProtocol.INSTANCE);
      }
   }
}
