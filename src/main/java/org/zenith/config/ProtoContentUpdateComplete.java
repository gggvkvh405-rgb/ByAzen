package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoContentUpdateComplete(UUID BlockPosEntry) implements ProtocolMessage {
   public ProtoContentUpdateComplete {
      Objects.requireNonNull(BlockPosEntry, "uploadId");
   }

   @Override
   public String type() {
      return "config.content.update.complete";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("uploadId", this.BlockPosEntry.toString());
   }

   public UUID GameCoordinator() {
      return this.BlockPosEntry;
   }
}
