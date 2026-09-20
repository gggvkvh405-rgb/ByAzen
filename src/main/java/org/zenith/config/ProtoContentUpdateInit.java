package org.zenith.config;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class ProtoContentUpdateInit implements ProtocolMessage {
   public final UUID ItemFilterRules;
   public final long IntPair;
   public final String AutoCraftHelper;
   public final int WaypointData;
   public final String TextLibrary;

   public ProtoContentUpdateInit(UUID var1, long var2, String var4, int var5, String var6) {
      Objects.requireNonNull(var1, "configId");
      var4 = ConfigJsonUtil.ItemSpec(var4, "fileName");
      var6 = ConfigJsonUtil.ItemSpec(var6, "sha256");
      if (var2 >= 1L && var5 > 0) {
         this.ItemFilterRules = var1;
         this.IntPair = var2;
         this.AutoCraftHelper = var4;
         this.WaypointData = var5;
         this.TextLibrary = var6;
      } else {
         throw new IllegalArgumentException("Invalid config content update metadata");
      }
   }

   @Override
   public String type() {
      return "config.content.update.init";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("configId", this.ItemFilterRules.toString());
      jsonobject.addProperty("expectedVersion", this.IntPair);
      jsonobject.addProperty("fileName", this.AutoCraftHelper);
      jsonobject.addProperty("sizeBytes", this.WaypointData);
      jsonobject.addProperty("sha256", this.TextLibrary);
      return jsonobject;
   }

   public UUID PermissionListCodec() {
      return this.ItemFilterRules;
   }

   public long RotationPredictiveStrategy() {
      return this.IntPair;
   }

   public String RotationSmoothStrategy() {
      return this.AutoCraftHelper;
   }

   public int RotationBurstStrategy() {
      return this.WaypointData;
   }

   public String RotationSnapStrategy() {
      return this.TextLibrary;
   }
}
