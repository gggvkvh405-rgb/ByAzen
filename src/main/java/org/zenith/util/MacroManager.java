package org.zenith.util;


import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import org.zenith.core.ClientProvider;
import org.zenith.event.EventTriggerKeyEvent;

public class MacroManager extends Item<MacroManager.ItemDescriptor> implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public MacroManager() {
      super("macros", "", new MacroListTypeToken().getType(), ArrayList::new);
      EventManager.register(this);
   }

   @Override
   protected Gson createGson() {
      return new GsonBuilder().registerTypeAdapter(MacroManager.ItemDescriptor.class, new MacroManager.JsonAdapter()).setPrettyPrinting().create();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void on23(MacroManager.ItemDescriptor var1) {
      this.items.add(var1);
   }

   public void on23(String var1, int var2, String var3) {
      this.items.add(new MacroManager.ItemDescriptor(var1, var2, var3));
   }

   public void BotRespawnEvent(String var1) {
      this.items.removeIf(var1xx -> var1xx.name().equalsIgnoreCase(var1));
   }

   public MacroManager.ItemDescriptor BotTickEvent(String var1) {
      return this.items.stream().filter(var1xx -> var1xx.name().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public boolean VelocityChangeEvent(String var1) {
      return this.BotTickEvent(var1) != null;
   }

   public void clear() {
      this.items.clear();
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (minecraftClient3.player != null) {
         for (MacroManager.ItemDescriptor ilii1111lllilllilllii_ii1il11l111ii11iil : this.items) {
            if (var1.ItemRegistry(ilii1111lllilllilllii_ii1il11l111ii11iil.TargetESP())) {
               this.UiAnimation(ilii1111lllilllilllii_ii1il11l111ii11iil);
            }
         }
      }
   }

   public void UiAnimation(MacroManager.ItemDescriptor var1) {
      if (var1.TotemParticles().startsWith("/")) {
         minecraftClient3.player.networkHandler.sendChatCommand(var1.TotemParticles().substring(1));
      } else {
         minecraftClient3.player.networkHandler.sendChatMessage(var1.TotemParticles());
      }
   }


   public static class JsonAdapter implements JsonDeserializer<ItemDescriptor>, JsonSerializer<ItemDescriptor> {
      public JsonElement serialize(ItemDescriptor var1, Type var2, JsonSerializationContext var3) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("name", var1.name());
         jsonobject.addProperty("key", var1.TargetESP());
         jsonobject.addProperty("command", var1.TotemParticles());
         return jsonobject;
      }

      public ItemDescriptor deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject jsonobject = var1.getAsJsonObject();
         String s = getString(jsonobject, "name", "");
         int i = getInt(jsonobject, "key");
         String s1 = getString(jsonobject, "command", "");
         return new ItemDescriptor(s, i, s1);
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
            throw new JsonParseException("Missing key " + var1);
         }
      }
   }

   public static class ItemDescriptor {
      @SerializedName("name")
      public final String string31;
      @SerializedName("key")
      public final int int128;
      @SerializedName("command")
      public final String string32;

      public ItemDescriptor(String var1, int var2, String var3) {
         this.string31 = var1;
         this.int128 = var2;
         this.string32 = var3;
      }

      public String name() {
         return this.string31;
      }

      public int TargetESP() {
         return this.int128;
      }

      public String TotemParticles() {
         return this.string32;
      }

      public Text toText() {
         String s = this.string31.replace("\"", "\\\"");
         MutableText mutabletext = Text.literal(" " + this.string31).formatted(new Formatting[]{Formatting.GOLD, Formatting.BOLD});
         MutableText mutabletext1 = Text.literal(" [  " + InputUtil.fromKeyCode(new KeyInput(this.int128, 0, 0)).getLocalizedText().getString() + "  ]")
            .formatted(Formatting.AQUA);
         MutableText mutabletext2 = Text.literal(" -> " + this.string32).formatted(Formatting.GRAY);
         MutableText mutabletext3 = Text.literal("  [Удалить]")
            .formatted(new Formatting[]{Formatting.RED, Formatting.BOLD})
            .styled(
               var2x -> var2x.withHoverEvent(new HoverEvent.ShowText(Text.literal("Подставить команду удаления макроса " + this.string31)))
                  .withClickEvent(new ClickEvent.SuggestCommand(".macro remove " + s))
            );
         return Text.empty().append(mutabletext).append(mutabletext1).append(mutabletext2).append(mutabletext3);
      }
   }

   private static final class MacroListTypeToken extends TypeToken<ArrayList<ItemDescriptor>> {
   }
}
