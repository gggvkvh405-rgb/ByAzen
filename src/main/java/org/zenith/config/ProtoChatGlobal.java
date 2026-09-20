package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoChatGlobal(String ItemUseController) implements ProtocolMessage {
   public ProtoChatGlobal {
      ItemUseController = ConfigJsonUtil.CloudResponse(ItemUseController);
   }

   @Override
   public String type() {
      return "chat.global.send";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("text", this.ItemUseController);
   }

   public String text() {
      return this.ItemUseController;
   }
}
