package org.zenith.config;

import com.google.gson.JsonObject;

public class ProtoConfigPublish implements ProtocolMessage {
   public final String NoSlow;
   public final String NoSweetSlow;
   public final String NoWeb;
   public final int ShulkerJump;
   public final String SlimeFlight;
   public final String Speed;
   public final String Spider;
   public final int Strafe;

   public ProtoConfigPublish(String var1, String var2, String var3, int var4, String var5, String var6, String var7, int var8) {
      var1 = ConfigJsonUtil.ItemSpec(var1, "name");
      var2 = ConfigJsonUtil.ItemSpec(var2, "fileName");
      var3 = ConfigJsonUtil.ItemSpec(var3, "serverAddress");
      var5 = ConfigJsonUtil.ItemSpec(var5, "sha256");
      var6 = ConfigJsonUtil.ItemSpec(var6, "visibility");
      var7 = var7 != null && !var7.isBlank() ? var7.strip() : null;
      if (var4 <= 0) {
         throw new IllegalArgumentException("sizeBytes must be positive");
      }

      if (var8 < 0) {
         throw new IllegalArgumentException("initialCodeCount must not be negative");
      }

      this.NoSlow = var1;
      this.NoSweetSlow = var2;
      this.NoWeb = var3;
      this.ShulkerJump = var4;
      this.SlimeFlight = var5;
      this.Speed = var6;
      this.Spider = var7;
      this.Strafe = var8;
   }

   public ProtoConfigPublish(String var1, String var2, String var3, int var4, String var5, String var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, null, var7);
   }

   public ProtoConfigPublish(String var1, int var2, String var3, String var4) {
      this(var1, ConfigJsonUtil.ConfigJsonUtil(var1), "unknown", var2, var3, var4, null, 0);
   }

   @Override
   public String type() {
      return "config.upload.init";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.NoSlow);
      jsonobject.addProperty("fileName", this.NoSweetSlow);
      jsonobject.addProperty("serverAddress", this.NoWeb);
      jsonobject.addProperty("sizeBytes", this.ShulkerJump);
      jsonobject.addProperty("sha256", this.SlimeFlight);
      jsonobject.addProperty("visibility", this.Speed);
      ConfigJsonUtil.on23(jsonobject, "description", this.Spider);
      jsonobject.addProperty("initialCodeCount", this.Strafe);
      return jsonobject;
   }

   public String name() {
      return this.NoSlow;
   }

   public String RotationSmoothStrategy() {
      return this.NoSweetSlow;
   }

   public String RotationLegitStrategy() {
      return this.NoWeb;
   }

   public int RotationBurstStrategy() {
      return this.ShulkerJump;
   }

   public String RotationSnapStrategy() {
      return this.SlimeFlight;
   }

   public String AimPolicyRotationStrategy() {
      return this.Speed;
   }

   public String description() {
      return this.Spider;
   }

   public int RotationBotStrategy() {
      return this.Strafe;
   }
}
