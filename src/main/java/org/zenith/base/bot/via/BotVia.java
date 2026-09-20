package org.zenith.base.bot.via;

import com.mojang.logging.LogUtils;
import com.viaversion.vialoader.ViaLoader;
import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.vialoader.impl.viaversion.VLCommandHandler;
import com.viaversion.vialoader.impl.viaversion.VLInjector;
import com.viaversion.vialoader.netty.ViaDecoder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public final class BotVia {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final int NATIVE_PROTOCOL = SharedConstants.getGameVersion().protocolVersion();
   static final AttributeKey<Integer> TARGET_PROTOCOL = AttributeKey.valueOf("zenith-via-target-protocol");
   public static final String VIA_ENCODER_NAME = "via-encoder";
   public static final String VIA_DECODER_NAME = "via-decoder";
   public static boolean initialized;
   public static boolean failed;

   public static boolean isTranslationNeeded(int var0) {
      return var0 > 0 && var0 != NATIVE_PROTOCOL;
   }

   public static synchronized boolean ensureInitialized() {
      if (initialized) {
         return true;
      }

      if (failed) {
         return false;
      }

      try {
         if (isViaLoaded()) {
            installVersionProvider();
            LOGGER.info("Bot ViaVersion: attached to existing Via platform '{}'", Via.getPlatform().getPlatformName());
         } else {
            ViaLoader.init(
               new ViaVersionPlatformImpl(FabricLoader.getInstance().getConfigDir().resolve("zenith-via").toFile()),
               new BotViaLoader(),
               new BotViaInjector(),
               new VLCommandHandler()
            );
            LOGGER.info("Bot ViaVersion initialized (native protocol {})", NATIVE_PROTOCOL);
         }

         initialized = true;
         return true;
      } catch (Throwable throwable) {
         failed = true;
         LOGGER.error("Failed to initialize ViaVersion for bots", throwable);
         return false;
      }
   }

   public static boolean isViaLoaded() {
      try {
         return Via.getManager() != null;
      } catch (Throwable throwable) {
         return false;
      }
   }

   public static void installVersionProvider() {
      ViaProviders viaproviders = Via.getManager().getProviders();
      VersionProvider versionprovider = viaproviders.get(VersionProvider.class);
      if (!(versionprovider instanceof BotViaVersionProvider)) {
         viaproviders.use(VersionProvider.class, new BotViaVersionProvider(versionprovider));
      }
   }

   public static void injectPipeline(Channel var0, int var1) {
      var0.attr(TARGET_PROTOCOL).set(var1);
      UserConnectionImpl userconnectionimpl = new UserConnectionImpl(var0, true);
      new ProtocolPipelineImpl(userconnectionimpl);
      ChannelPipeline channelpipeline = var0.pipeline();
      channelpipeline.addBefore("encoder", "via-encoder", new BotViaEncoder(userconnectionimpl));
      channelpipeline.addBefore("inbound_config", "via-decoder", new ViaDecoder(userconnectionimpl));
   }

   static Integer targetProtocol(Channel var0) {
      return (Integer)var0.attr(TARGET_PROTOCOL).get();
   }

   private static final class BotViaInjector extends VLInjector {
      @Override
      public String getEncoderName() {
         return VIA_ENCODER_NAME;
      }

      @Override
      public String getDecoderName() {
         return VIA_DECODER_NAME;
      }
   }
}
