package org.zenith.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum I1Type {
   val503,
   val531,
   val532,
   val533,
   val534;

   public final String string22;
   public final List<Item> list40;

   I1Type() {
      this("");
   }

   I1Type(String var3, Item... var4) {
      this.string22 = var3;
      this.list40 = List.of(var4);
   }

   public List<Item> call238() {
      List<Item> arraylist = new ArrayList<>(this.list40.size() + 1);
      arraylist.add(Items.NETHER_WART);
      arraylist.addAll(this.list40);
      return arraylist;
   }

   public static I1Type RotationSmoothStrategy(String var0) {
      for (I1Type i1111lilll1li11iil : values()) {
         if (i1111lilll1li11iil.string22.equals(var0)) {
            return i1111lilll1li11iil;
         }
      }

      return val503;
   }
}
