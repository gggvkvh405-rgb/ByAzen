package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;

public record BotConnectionState(
   GameProfile profile,
   Immutable receivedRegistries,
   FeatureSet enabledFeatures,
   String brand,
   Map<Identifier, byte[]> serverCookies,
   Map<String, String> customReportDetails,
   ServerLinks serverLinks
) {
}
