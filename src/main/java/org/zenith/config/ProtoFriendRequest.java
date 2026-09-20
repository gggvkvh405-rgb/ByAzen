package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoFriendRequest(String AppleFarm) implements ProtocolMessage {
   public ProtoFriendRequest {
      AppleFarm = ConfigJsonUtil.ItemSpec(AppleFarm, "targetUserId");
   }

   @Override
   public String type() {
      return "friends.request.create";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("targetUserId", this.AppleFarm);
   }

   public String MenuEaseD() {
      return this.AppleFarm;
   }
}
