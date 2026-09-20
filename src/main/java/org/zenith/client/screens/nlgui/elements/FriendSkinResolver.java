package org.zenith.client.screens.nlgui.elements;

import net.minecraft.util.Identifier;

public final class FriendSkinResolver {
   public static final Identifier DEFAULT_SKIN = Identifier.of("minecraft", "textures/entity/player/wide/steve.png");

   public static Identifier resolveSkin(String var0) {
      return DEFAULT_SKIN;
   }

   public static void clearExternalCache() {
   }
}
