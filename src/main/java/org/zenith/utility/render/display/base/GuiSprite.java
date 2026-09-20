package org.zenith.utility.render.display.base;

import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;

public class GuiSprite {
   public Identifier identifier15;

   public GuiSprite(String var1) {
      if (var1.contains(":")) {
         this.identifier15 = Identifier.of(var1);
      } else if (var1.contains("/")) {
         this.identifier15 = ZenithClient.on23(var1);
      } else {
         this.identifier15 = ZenithClient.on23("icons/category/" + var1);
      }
   }

   public Identifier booleanSupplier2() {
      return this.identifier15;
   }
}
