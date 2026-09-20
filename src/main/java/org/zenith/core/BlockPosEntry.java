package org.zenith.core;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;

public class BlockPosEntry {
   public static final BlockPosEntry var12 = new BlockPosEntry(Long.MIN_VALUE);
   public final long long143;

   public BlockPosEntry(long var1) {
      this.long143 = var1;
   }

   public static BlockPosEntry FileLogger(BlockPos var0) {
      return new BlockPosEntry(var0.asLong());
   }

   public boolean isPresent() {
      return this.long143 != Long.MIN_VALUE;
   }

   public BlockPos string108() {
      return BlockPos.fromLong(this.long143);
   }

   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("packedPos", this.long143);
      return jsonobject;
   }

   public static BlockPosEntry TradeGuardService(JsonObject var0) {
      return var0.has("packedPos") ? new BlockPosEntry(var0.get("packedPos").getAsLong()) : var12;
   }
}
