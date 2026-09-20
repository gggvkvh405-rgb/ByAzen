package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoFriendAccept(UUID AHHelper) implements ProtocolMessage {
   public ProtoFriendAccept {
      Objects.requireNonNull(AHHelper, "requestId");
   }

   @Override
   public String type() {
      return "friends.request.accept";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("requestId", this.AHHelper.toString());
   }

   public UUID Event05() {
      return this.AHHelper;
   }
}
