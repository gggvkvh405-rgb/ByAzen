package org.zenith.core;

public record ModuleSnapshotDto(String AutoTotem, String Backtrack, long Blink) {
   public String Event37() {
      return this.AutoTotem;
   }

   public String EventUpdateHealth() {
      return this.Backtrack;
   }

   public long EventRender() {
      return this.Blink;
   }
}
