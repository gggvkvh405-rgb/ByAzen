package org.zenith.core;

import org.zenith.module.misc.AutoCraft;

public record WaypointData(WaypointKind zClass047Var159, String string117, String string118, String string119) {
   public static WaypointData call100() {
      return new WaypointData(null, "", "", "");
   }

   public boolean call038() {
      return this.zClass047Var159 == null;
   }

   public String Easing(AutoCraft var1) {
      if (this.zClass047Var159 == WaypointKind.val189) {
         return "Нажмите ПКМ по хранилищу с " + var1.EventRenderScreenHook(this.string119) + " для автокрафта";
      } else if (this.zClass047Var159 == WaypointKind.val190) {
         return "Нажмите ПКМ по сундуку склада для автокрафта";
      } else {
         return this.zClass047Var159 == WaypointKind.val191 ? "Нажмите ПКМ по верстаку для автокрафта" : "Нажмите ПКМ по блоку для привязки";
      }
   }

   public WaypointKind call079() {
      return this.zClass047Var159;
   }

   public String call061() {
      return this.string117;
   }

   public String call062() {
      return this.string118;
   }

   public String double127() {
      return this.string119;
   }
}
