package org.zenith.base.bot;

import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.ServerInfo;

final class ServerIconCache_Entry {
   final ServerInfo info;
   final WorldIcon icon;
   byte[] lastFavicon;

   ServerIconCache_Entry(ServerInfo var1, WorldIcon var2) {
      this.info = var1;
      this.icon = var2;
   }
}
