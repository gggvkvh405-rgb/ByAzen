package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoCodesListGet(UUID CraftingExecutor) implements ProtocolMessage {
   public ProtoCodesListGet {
      Objects.requireNonNull(CraftingExecutor, "configId");
   }

   @Override
   public String type() {
      return "config.codes.list.get";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("configId", this.CraftingExecutor.toString());
   }

   public UUID PermissionListCodec() {
      return this.CraftingExecutor;
   }
}
