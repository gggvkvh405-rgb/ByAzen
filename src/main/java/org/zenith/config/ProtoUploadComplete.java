package org.zenith.config;

import com.google.gson.JsonObject;

public record ProtoUploadComplete(String NoPush) implements ProtocolMessage {
   public ProtoUploadComplete {
      NoPush = ConfigJsonUtil.ItemSpec(NoPush, "uploadId");
   }

   @Override
   public String type() {
      return "config.upload.complete";
   }

   @Override
   public JsonObject TaskQueue() {
      return ConfigJsonUtil.ItemRegistry("uploadId", this.NoPush);
   }

   public String MotorIntentRotationStrategy() {
      return this.NoPush;
   }
}
