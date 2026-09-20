package org.zenith.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;

public class ProtoWatchSet implements ProtocolMessage {
   public final List<String> AutoWarden;

   public ProtoWatchSet(List<String> var1) {
      var1 = var1 == null ? null : List.copyOf(var1);
      this.AutoWarden = var1;
   }

   public ProtoWatchSet() {
      this(null);
   }

   @Override
   public String type() {
      return "player.watch.set";
   }

   @Override
   public JsonObject TaskQueue() {
      JsonObject jsonobject = new JsonObject();
      if (this.AutoWarden != null) {
         JsonArray jsonarray = new JsonArray();
         this.AutoWarden.forEach(jsonarray::add);
         jsonobject.add("userIds", jsonarray);
      }

      return jsonobject;
   }

   public List<String> MenuEaseC() {
      return this.AutoWarden;
   }
}
