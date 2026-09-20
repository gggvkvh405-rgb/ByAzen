package org.zenith.core;

import org.zenith.module.misc.AutoCraft;

public class TextLibrary {
   public final String string93;
   public String string27 = "";
   public String string28 = "";
   public final String[] val104 = new String[9];
   public final String[] val212 = new String[9];
   public int int361 = 0;

   public TextLibrary(String var1) {
      this.string93 = var1 == null ? "" : var1;

      for (int i = 0; i < 9; i++) {
         this.val104[i] = "";
         this.val212[i] = "";
      }
   }

   public static TextLibrary call208() {
      return new TextLibrary("");
   }

   public boolean call038() {
      return this.string93.isBlank();
   }

   public String string112() {
      return this.string93;
   }

   public String float275() {
      return this.string27;
   }

   public String boolean178() {
      return this.string28;
   }

   public int call408() {
      return this.int361;
   }

   public String EventMouseScrollHook(int var1) {
      return this.val104[var1];
   }

   public String EventInteractBlock(int var1) {
      return this.val212[var1];
   }

   public boolean call011() {
      if (this.string27 != null && !this.string27.isBlank()) {
         int i = 0;

         for (int j = 0; j < 9; j++) {
            if (this.val104[j] != null && !this.val104[j].isBlank()) {
               i++;
            }
         }

         return i >= 2;
      } else {
         return false;
      }
   }

   public void on23(String var1, String var2, String[] var3, String[] var4, int var5) {
      this.string27 = var1 == null ? "" : var1;
      this.string28 = var2 == null ? "" : var2;
      this.int361 = Math.max(0, var5);

      for (int i = 0; i < 9; i++) {
         this.val104[i] = var3 != null && i < var3.length && var3[i] != null ? var3[i] : "";
         this.val212[i] = var4 != null && i < var4.length && var4[i] != null ? var4[i] : "";
      }
   }

   public void call039() {
      this.string27 = "";
      this.string28 = "";
      this.int361 = 0;

      for (int i = 0; i < 9; i++) {
         this.val104[i] = "";
         this.val212[i] = "";
      }
   }

   public String Easing(AutoCraft var1) {
      return !this.call011()
         ? "AutoCraft: craft item in crafting table to add preset"
         : "AutoCraft: take crafted item to save " + var1.ProtocolMessage(this.string27, this.string28);
   }
}
