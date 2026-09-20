package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoInventoryUpdate(JsonObject AutoMine) implements ProtocolMessage {
   public ProtoInventoryUpdate {
      AutoMine = ConfigJsonUtil.Easing(AutoMine, "inventory");
   }

   @Override
   public String type() {
      return "player.inventory.update";
   }

   @Override
   public JsonObject TaskQueue() {
      return this.AutoMine.deepCopy();
   }

   public JsonObject MenuEaseA() {
      return this.AutoMine;
   }
}
