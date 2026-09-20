package org.zenith.base.filemanager.impl.way;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.math.BlockPos;

public final class WayAdapter implements JsonDeserializer<Way>, JsonSerializer<Way> {
   public JsonElement serialize(Way var1, Type var2, JsonSerializationContext var3) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", var1.name());
      jsonobject.addProperty("server", var1.server());
      JsonObject jsonobject1 = new JsonObject();
      BlockPos blockpos = var1.pos();
      jsonobject1.addProperty("x", blockpos.getX());
      jsonobject1.addProperty("y", blockpos.getY());
      jsonobject1.addProperty("z", blockpos.getZ());
      jsonobject.add("pos", jsonobject1);
      return jsonobject;
   }

   public Way deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject jsonobject = var1.getAsJsonObject();
      String s = getString(jsonobject, "name", "");
      String s1 = getString(jsonobject, "server", "");
      JsonObject jsonobject1 = jsonobject.getAsJsonObject("pos");
      if (jsonobject1 == null) {
         throw new JsonParseException("Missing 'pos' for Way");
      }

      int i = getInt(jsonobject1, "x");
      int j = getInt(jsonobject1, "y");
      int k = getInt(jsonobject1, "z");
      return new Way(s, new BlockPos(i, j, k), s1);
   }

   public static String getString(JsonObject var0, String var1, String var2) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && !jsonelement.isJsonNull() ? jsonelement.getAsString() : var2;
   }

   public static int getInt(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      if (jsonelement != null && !jsonelement.isJsonNull()) {
         return jsonelement.getAsInt();
      } else {
         throw new JsonParseException("Missing int: " + var1);
      }
   }
}
