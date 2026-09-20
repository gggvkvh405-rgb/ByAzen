package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;

public final class BotLoginHandler implements ClientLoginPacketListener {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final BotClient client;
   public final BotConnection connection;
   public final Map<Identifier, byte[]> serverCookies = new HashMap<>();

   public BotLoginHandler(BotClient var1, BotConnection var2) {
      this.client = var1;
      this.connection = var2;
   }

   public void onHello(LoginHelloS2CPacket packet) {
      if (packet.needsAuthentication()) {
         this.connection.disconnect(Text.literal("Online-mode server is not supported by bots"));
      } else {
         Cipher cipher;
         Cipher cipher1;
         LoginKeyC2SPacket loginkeyc2spacket;
         try {
            SecretKey secretkey = NetworkEncryptionUtils.generateSecretKey();
            PublicKey publickey = packet.getPublicKey();
            cipher = NetworkEncryptionUtils.cipherFromKey(2, secretkey);
            cipher1 = NetworkEncryptionUtils.cipherFromKey(1, secretkey);
            loginkeyc2spacket = new LoginKeyC2SPacket(secretkey, publickey, packet.getNonce());
         } catch (Exception exception) {
            throw new IllegalStateException("Protocol error", exception);
         }

         this.connection.send(loginkeyc2spacket, PacketCallbacks.always(() -> this.connection.setupEncryption(cipher, cipher1)));
      }
   }

   public void onSuccess(LoginSuccessS2CPacket packet) {
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         GameProfile gameprofile = packet.profile();
         this.client.setPhase(BotPhase.CONFIGURATION);
         LOGGER.debug("Bot {} login success, profileId={}", this.client.getName(), gameprofile.id());
         BotConnectionState botconnectionstate = new BotConnectionState(
            gameprofile, ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(), FeatureFlags.DEFAULT_ENABLED_FEATURES, null, this.serverCookies, Map.of(), ServerLinks.EMPTY
         );
         BotConfigHandler botconfighandler = new BotConfigHandler(this.client, this.connection, botconnectionstate);
         this.connection.transitionInbound(ConfigurationStates.S2C, botconfighandler);
         this.connection.send(EnterConfigurationC2SPacket.INSTANCE);
         this.connection.transitionOutbound(ConfigurationStates.C2S);
         this.connection.send(new CustomPayloadC2SPacket(new BrandCustomPayload(this.client.getConfig().brand())));
         this.connection.send(new ClientOptionsC2SPacket(SyncedClientOptions.createDefault()));
      }
   }

   public void onDisconnect(LoginDisconnectS2CPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   public void onCompression(LoginCompressionS2CPacket packet) {
      this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
   }

   public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
      this.connection.send(new LoginQueryResponseC2SPacket(packet.queryId(), null));
   }

   public void onCookieRequest(CookieRequestS2CPacket packet) {
      this.connection.send(new CookieResponseC2SPacket(packet.key(), this.serverCookies.get(packet.key())));
   }

   public void onDisconnected(DisconnectionInfo info) {
      this.client.onConnectionTerminated(info);
   }

   public boolean isConnectionOpen() {
      return this.connection.isOpen();
   }
}
