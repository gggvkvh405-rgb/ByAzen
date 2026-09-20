package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public record ProtoLikeToggle(UUID HeldItemWatcher) implements ProtocolMessage {
   public ProtoLikeToggle {
      Objects.requireNonNull(HeldItemWatcher, "configId");
   }

   @Override
   public String type() {
      return "config.like.toggle";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("configId", this.HeldItemWatcher.toString());
   }

   public UUID PermissionListCodec() {
      return this.HeldItemWatcher;
   }
}
