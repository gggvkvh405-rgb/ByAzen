package org.zenith.base.bot.via;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;

final class BotViaLoader implements ViaPlatformLoader {
   @Override
   public void load() {
      Via.getManager().getProviders().use(VersionProvider.class, new BotViaVersionProvider(null));
   }

   @Override
   public void unload() {
   }
}
