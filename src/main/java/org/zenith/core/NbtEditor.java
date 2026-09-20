package org.zenith.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class NbtEditor extends ItemServiceBase {
   public final Map<String, String> StaffList;

   public NbtEditor(ItemStack var1, String var2, String var3, SoundCueKind var4) {
      super(var1, var2, var3, var4);
      this.StaffList = new HashMap<>();
   }

   public NbtEditor(ItemStack var1, String var2, SoundCueKind var3, Map<String, String> var4) {
      super(var1, var2, var3);
      this.StaffList = var4;
   }

   public NbtEditor(ItemStack var1, String var2, SoundCueKind var3, String var4, String var5) {
      super(var1, var2, var3);
      this.StaffList = new HashMap<>();
      this.StaffList.put(var4, var5);
   }

   public NbtEditor(ItemStack var1, String var2, String var3, SoundCueKind var4, String var5, String var6) {
      super(var1, var2, var3, var4);
      this.StaffList = new HashMap<>();
      this.StaffList.put(var5, var6);
   }

   @Override
   public boolean UiAnimation(ItemStack var1) {
      if (!super.UiAnimation(var1)) {
         return false;
      }

      NbtComponent nbtcomponent = (NbtComponent)var1.get(DataComponentTypes.CUSTOM_DATA);
      if (nbtcomponent == null) {
         return false;
      }

      NbtCompound nbtcompound = nbtcomponent.copyNbt();

      for (Entry<String, String> entry : this.StaffList.entrySet()) {
         String s = entry.getKey();
         String s1 = entry.getValue();
         if (!nbtcompound.contains(s)) {
            return false;
         }

         if (on23(s, s1)) {
            if (!on23(nbtcompound, s, s1)) {
               return false;
            }
         } else {
            String s2 = nbtcompound.get(s).toString().replaceAll(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]", "");
            String s3 = s1.replaceAll(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]", "");
            if (!s2.contains(s3)) {
               return false;
            }

            boolean flag = s3.startsWith("{") || s3.startsWith("[");
            if (!flag && s2.matches(".*\".*\".*")) {
               String s4 = "\"" + s3 + "\"";
               if (!s2.contains(s4)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static boolean on23(String var0, String var1) {
      return "custom_potion_effects".equals(var0) && var1.startsWith("minecraft:") && !var1.contains("{") && !var1.contains("[");
   }

   public static boolean on23(NbtCompound var0, String var1, String var2) {
      String s = var2;
      int i = -1;
      int j = var2.indexOf(64);
      if (j > 0) {
         s = var2.substring(0, j);

         try {
            i = Integer.parseInt(var2.substring(j + 1));
         } catch (NumberFormatException numberformatexception) {
            i = -1;
         }
      }

      if (var0.getList(var1).isEmpty()) {
         return var0.get(var1).toString().contains(s);
      }

      String s1 = Easing(s);
      NbtList nbtlist = var0.getListOrEmpty(var1);
      boolean flag = false;

      for (int k = 0; k < nbtlist.size(); k++) {
         NbtCompound nbtcompound = nbtlist.getCompoundOrEmpty(k);
         if (!s1.equals(Easing(nbtcompound.getString("id").orElse("")))) {
            return false;
         }

         if (i < 0 || on23(nbtcompound) >= i) {
            flag = true;
         }
      }

      return flag;
   }

   public static String Easing(String var0) {
      return var0 != null && var0.startsWith("minecraft:") ? var0.substring("minecraft:".length()) : String.valueOf(var0);
   }

   public static int on23(NbtCompound var0) {
      return var0.get("amplifier") instanceof AbstractNbtNumber abstractnbtnumber ? abstractnbtnumber.intValue() : 0;
   }
}
