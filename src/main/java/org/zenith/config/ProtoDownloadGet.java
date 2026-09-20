package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoDownloadGet(UUID PricedItem) implements ProtocolMessage {
   public ProtoDownloadGet {
      Objects.requireNonNull(PricedItem, "configId");
   }

   @Override
   public String type() {
      return "config.download.get";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("configId", this.PricedItem.toString());
   }

   public UUID PermissionListCodec() {
      return this.PricedItem;
   }
}
