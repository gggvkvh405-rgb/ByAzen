package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.entity.passive.WolfVariants;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.c2s.config.AcceptCodeOfConductC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.CodeOfConductS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SerializableRegistries.SerializedRegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;

public final class BotConfigHandler extends BotCommonHandler implements ClientConfigurationPacketListener {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final GameProfile profile;
   public final Immutable receivedRegistries;
   public final BotClientRegistries clientRegistries = new BotClientRegistries();
   public final Map<RegistryKey<? extends Registry<?>>, Integer> receivedRegistryEntries = new HashMap<>();
   public FeatureSet enabledFeatures;
   public ClientDataPackManager dataPackManager;

   public BotConfigHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.receivedRegistries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
   }

   public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
      this.receivedRegistryEntries.merge(packet.registry(), packet.entries().size(), Integer::sum);
   }

   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putTags(packet.getGroups());
   }

   public void onFeatures(FeaturesS2CPacket packet) {
      this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(packet.features());
   }

   public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.dataPackManager == null) {
         this.dataPackManager = new ClientDataPackManager();
      }

      this.sendPacket(new SelectKnownPacksC2SPacket(this.dataPackManager.getCommonKnownPacks(packet.knownPacks())));
   }

   public void onResetChat(ResetChatS2CPacket packet) {
   }

   public void onReady(ReadyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         this.installMissingWolfVariantFallback();

         Immutable immutable;
         try {
            immutable = this.openClientDataPack(var1 -> this.clientRegistries.createRegistryManager(var1, this.receivedRegistries));
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Bot {} registry loading failed", this.client.getName(), runtimeexception);
            this.connection.disconnect(Text.literal("registry loading failed"));
            return;
         }

         BotConnectionState botconnectionstate = new BotConnectionState(
            this.profile, immutable, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
         );
         BotPlayHandler botplayhandler = new BotPlayHandler(this.client, this.connection, botconnectionstate);
         this.client.setPlayHandler(botplayhandler);
         this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(immutable)), botplayhandler);
         this.connection.send(ReadyC2SPacket.INSTANCE);
         this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(immutable), () -> true));
      }
   }

   @Override
   public void onCodeOfConduct(CodeOfConductS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.sendPacket(AcceptCodeOfConductC2SPacket.INSTANCE);
   }

   public <T> T openClientDataPack(Function<ResourceFactory, T> var1) {
      if (this.dataPackManager == null) {
         return var1.apply(ResourceFactory.MISSING);
      }

      LifecycledResourceManager lifecycledresourcemanager = this.dataPackManager.createResourceManager();

      Object object;
      try {
         object = var1.apply(lifecycledresourcemanager);
      } catch (Throwable var7) {
         if (lifecycledresourcemanager != null) {
            try {
               lifecycledresourcemanager.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (lifecycledresourcemanager != null) {
         lifecycledresourcemanager.close();
      }

      return (T)object;
   }

   public void installMissingWolfVariantFallback() {
      if (this.receivedRegistryEntries.getOrDefault(RegistryKeys.WOLF_VARIANT, 0) <= 0) {
         List<RegistryKey<WolfVariant>> list = List.of(
            WolfVariants.PALE,
            WolfVariants.SPOTTED,
            WolfVariants.SNOWY,
            WolfVariants.BLACK,
            WolfVariants.ASHEN,
            WolfVariants.RUSTY,
            WolfVariants.WOODS,
            WolfVariants.CHESTNUT,
            WolfVariants.STRIPED
         );
         List<SerializedRegistryEntry> list1 = list.stream().map(var0 -> new SerializedRegistryEntry(var0.getValue(), Optional.empty())).toList();
         this.clientRegistries.putDynamicRegistry(RegistryKeys.WOLF_VARIANT, list1);
         if (this.dataPackManager == null) {
            this.dataPackManager = new ClientDataPackManager();
            this.dataPackManager.getCommonKnownPacks(List.of(VanillaResourcePackProvider.VANILLA_ID));
         }

         LOGGER.debug("Bot {} installed wolf_variant registry fallback ({} entries)", this.client.getName(), list1.size());
      }
   }

   public void tick() {
   }

   public NetworkPhase getPhase() {
      return NetworkPhase.CONFIGURATION;
   }
}
