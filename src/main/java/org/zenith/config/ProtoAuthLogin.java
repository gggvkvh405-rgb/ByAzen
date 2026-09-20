package org.zenith.config;

import com.google.gson.JsonObject;

public class ProtoAuthLogin implements ProtocolMessage {
   public final String FakePlayer;

   public ProtoAuthLogin(String var1) {
      var1 = ConfigJsonUtil.ItemSpec(var1, "accessToken");
      this.FakePlayer = var1;
   }

   @Override
   public String type() {
      return "auth.login";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("accessToken", this.FakePlayer);
      return jsonobject;
   }

   public String RotationEasing() {
      return this.FakePlayer;
   }
}
