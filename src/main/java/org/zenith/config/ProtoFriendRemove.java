package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoFriendRemove(String WallBypass) implements ProtocolMessage {
   public ProtoFriendRemove {
      WallBypass = ConfigJsonUtil.ItemSpec(WallBypass, "friendUserId");
   }

   @Override
   public String type() {
      return "friends.remove";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("friendUserId", this.WallBypass);
   }

   public String RoundedRectEasing() {
      return this.WallBypass;
   }
}
