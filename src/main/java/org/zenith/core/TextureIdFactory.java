package org.zenith.core;

import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;

public class TextureIdFactory {
   final Identifier val336;

   public TextureIdFactory(String var1) {
      this.val336 = ZenithClient.on23(this.HudElementMessage(var1));
   }

   public TextureIdFactory(Identifier var1) {
      this.val336 = Identifier.of(var1.getNamespace(), var1.getPath());
   }

   String HudElementMessage(String var1) {
      if (Identifier.isPathValid(var1)) {
         return var1;
      }

      StringBuilder stringbuilder = new StringBuilder();

      for (char c0 : var1.toLowerCase().toCharArray()) {
         if (Identifier.isPathCharacterValid(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   public Identifier var14340() {
      return this.val336;
   }
}
