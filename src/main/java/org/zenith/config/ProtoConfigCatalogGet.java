package org.zenith.config;

import com.google.gson.JsonObject;

public class ProtoConfigCatalogGet implements ProtocolMessage {
   public final String PathTeleport;
   public final int PvpSafe;
   public final int ServerHelper;

   public ProtoConfigCatalogGet(String var1, int var2, int var3) {
      var1 = ConfigJsonUtil.ItemSpec(var1, "scope");
      this.PathTeleport = var1;
      this.PvpSafe = var2;
      this.ServerHelper = var3;
   }

   @Override
   public String type() {
      return "config.catalog.get";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("scope", this.PathTeleport);
      jsonobject.addProperty("offset", this.PvpSafe);
      jsonobject.addProperty("limit", this.ServerHelper);
      return jsonobject;
   }

   public String GmmModel() {
      return this.PathTeleport;
   }

   public int MotorIntentModel() {
      return this.PvpSafe;
   }

   public int PermissionListsStore() {
      return this.ServerHelper;
   }
}
