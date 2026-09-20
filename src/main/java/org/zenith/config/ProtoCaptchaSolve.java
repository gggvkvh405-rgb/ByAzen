package org.zenith.config;

import com.google.gson.JsonObject;

public class ProtoCaptchaSolve implements ProtocolMessage {
   public final String FastBreak;
   public final int FreeCam;
   public final int InventorySetting;

   public ProtoCaptchaSolve(String var1, int var2, int var3) {
      var1 = ConfigJsonUtil.ItemSpec(var1, "imageBase64");
      if (var2 < 0 || var3 < 0 || var3 > 0 && var2 > var3) {
         throw new IllegalArgumentException("Invalid captcha length bounds");
      }

      this.FastBreak = var1;
      this.FreeCam = var2;
      this.InventorySetting = var3;
   }

   @Override
   public String type() {
      return "captcha.solve";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("imageBase64", this.FastBreak);
      if (this.FreeCam > 0) {
         jsonobject.addProperty("minLength", this.FreeCam);
      }

      if (this.InventorySetting > 0) {
         jsonobject.addProperty("maxLength", this.InventorySetting);
      }

      return jsonobject;
   }

   public String RotationManager() {
      return this.FastBreak;
   }

   public int RotationTask() {
      return this.FreeCam;
   }

   public int maxLength() {
      return this.InventorySetting;
   }
}
