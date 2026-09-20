package org.zenith.core;

public enum MenuScreenId {
   call004(0),
   call111(1),
   call470(2),
   call498(3),
   call499(4),
   call500(5),
   call501(6);

   public final int int381;

   public static MenuScreenId Event37(int var0) {
      for (MenuScreenId ll1lil1ii1iil1l : values()) {
         if (ll1lil1ii1iil1l.int203() == var0) {
            return ll1lil1ii1iil1l;
         }
      }

      return call004;
   }

   MenuScreenId(int var3) {
      this.int381 = var3;
   }

   public int int203() {
      return this.int381;
   }
}
