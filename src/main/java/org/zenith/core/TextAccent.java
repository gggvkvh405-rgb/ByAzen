package org.zenith.core;

import org.zenith.util.ArgbColor;

public enum TextAccent {
   call013("WARN", new ArgbColor(247, 206, 59)),
   call417("ERROR", new ArgbColor(242, 79, 68)),
   call002("INFO", new ArgbColor(87, 126, 255));

   public final String string54;
   public final ArgbColor var11930;

   public String call271() {
      return this.string54;
   }

   public ArgbColor getColor() {
      return this.var11930;
   }

   TextAccent(String var3, ArgbColor var4) {
      this.string54 = var3;
      this.var11930 = var4;
   }
}
