package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoFriendDecline(UUID AutoLoot) implements ProtocolMessage {
   public ProtoFriendDecline {
      Objects.requireNonNull(AutoLoot, "requestId");
   }

   @Override
   public String type() {
      return "friends.request.decline";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("requestId", this.AutoLoot.toString());
   }

   public UUID Event05() {
      return this.AutoLoot;
   }
}
