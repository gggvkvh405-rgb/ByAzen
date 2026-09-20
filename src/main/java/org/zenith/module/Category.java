package org.zenith.module;

import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

public enum Category {
   COMBAT("Combat", "3"),
   MOVEMENT("Movement", "4"),
   MISC("Misc", "5"),
   RENDER("Visuals", "6"),
   PLAYER("PvE", "7"),
   THEMES("Themes", "G");

   public final String string13;
   public final String string14;

   Category(String var3, String var4) {
      this.string13 = var3;
      this.string14 = var4;
   }

   public String getIcon() {
      return this.string14;
   }

   public String getName() {
      return this.string13;
   }
}
