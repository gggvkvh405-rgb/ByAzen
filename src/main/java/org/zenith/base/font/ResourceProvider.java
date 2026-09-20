package org.zenith.base.font;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;

public final class ResourceProvider implements ClientProvider {
   public static final ResourceManager RESOURCE_MANAGER = MinecraftClient.getInstance().getResourceManager();
   public static final Gson GSON = new Gson();

   public static Identifier getShaderIdentifier(String var0) {
      return ZenithClient.on23("core/" + var0);
   }

   public static <T> T fromJsonToInstance(Identifier var0, Class<T> var1) {
      return (T)GSON.fromJson(toString(var0), var1);
   }

   public static String toString(Identifier var0) {
      return toString(var0, "\n");
   }

   public static String toString(Identifier var0, String var1) {
      try (
         InputStream inputstream = RESOURCE_MANAGER.open(var0);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
      ) {
         return bufferedreader.lines().collect(Collectors.joining(var1));
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }
   }
}
