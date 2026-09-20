package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoStateUpdate(JsonObject AutoBrewing) implements ProtocolMessage {
   public ProtoStateUpdate {
      AutoBrewing = ConfigJsonUtil.Easing(AutoBrewing, "state");
   }

   @Override
   public String type() {
      return "player.state.update";
   }

   @Override
   public JsonObject TaskQueue() {
      return this.AutoBrewing.deepCopy();
   }

   public JsonObject MenuEaseE() {
      return this.AutoBrewing;
   }
}
