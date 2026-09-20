package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoConfigDelete(UUID I11IType) implements ProtocolMessage {
   public ProtoConfigDelete {
      Objects.requireNonNull(I11IType, "configId");
   }

   @Override
   public String type() {
      return "config.delete";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("configId", this.I11IType.toString());
   }

   public UUID PermissionListCodec() {
      return this.I11IType;
   }
}
