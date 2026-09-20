package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class ProtoConfigCodesCreate implements ProtocolMessage {
   public final UUID TridentAimbot;
   public final int ContainerScanner;

   public ProtoConfigCodesCreate(UUID var1, int var2) {
      Objects.requireNonNull(var1, "configId");
      if (var2 < 1) {
         throw new IllegalArgumentException("count must be positive");
      }

      this.TridentAimbot = var1;
      this.ContainerScanner = var2;
   }

   @Override
   public String type() {
      return "config.codes.create";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = ConfigJsonUtil.ItemRegistry("configId", this.TridentAimbot.toString());
      jsonobject.addProperty("count", this.ContainerScanner);
      return jsonobject;
   }

   public UUID PermissionListCodec() {
      return this.TridentAimbot;
   }

   public int count() {
      return this.ContainerScanner;
   }
}
