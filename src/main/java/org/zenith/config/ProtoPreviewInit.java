package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class ProtoPreviewInit implements ProtocolMessage {
   public final UUID GrimGlide;
   public final int GuiWalk;
   public final String NoDelay;

   public ProtoPreviewInit(UUID var1, int var2, String var3) {
      Objects.requireNonNull(var1, "configId");
      var3 = ConfigJsonUtil.ItemSpec(var3, "sha256");
      if (var2 <= 0) {
         throw new IllegalArgumentException("sizeBytes must be positive");
      }

      this.GrimGlide = var1;
      this.GuiWalk = var2;
      this.NoDelay = var3;
   }

   @Override
   public String type() {
      return "config.preview.init";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("configId", this.GrimGlide.toString());
      jsonobject.addProperty("sizeBytes", this.GuiWalk);
      jsonobject.addProperty("sha256", this.NoDelay);
      return jsonobject;
   }

   public UUID PermissionListCodec() {
      return this.GrimGlide;
   }

   public int RotationBurstStrategy() {
      return this.GuiWalk;
   }

   public String RotationSnapStrategy() {
      return this.NoDelay;
   }
}
