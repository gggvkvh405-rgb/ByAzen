package org.zenith.core;

public class AvatarTile {
   public final int int186;
   public int int187 = 0;

   public AvatarTile(int var1) {
      this.int186 = var1;
   }

   public void reset() {
      this.int187 = 0;
   }

   public boolean call152() {
      return ++this.int187 > 4;
   }
}
