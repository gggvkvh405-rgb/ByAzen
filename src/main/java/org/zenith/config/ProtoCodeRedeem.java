package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoCodeRedeem(String ShulkerPreview) implements ProtocolMessage {
   public ProtoCodeRedeem {
      ShulkerPreview = ConfigJsonUtil.ItemSpec(ShulkerPreview, "code");
   }

   @Override
   public String type() {
      return "config.code.redeem";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("code", this.ShulkerPreview);
   }

   public String PlayerStateService() {
      return this.ShulkerPreview;
   }
}
