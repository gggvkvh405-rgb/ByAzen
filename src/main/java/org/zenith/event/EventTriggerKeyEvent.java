package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.MinecraftClient;
import org.zenith.core.ClientProvider;

public final class EventTriggerKeyEvent implements Event {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final int int94;
   public final int keyCode;

   public boolean is(int var1) {
      return var1 == this.keyCode;
   }

   public boolean ItemRegistry(int var1) {
      return this.on23(var1, ClientProvider.minecraftClient3.currentScreen == null);
   }

   public boolean on23(int var1, boolean var2) {
      return this.keyCode == var1 && this.int94 == 1 && var2;
   }

   public boolean ItemSpec(int var1) {
      return this.UiAnimation(var1, ClientProvider.minecraftClient3.currentScreen == null);
   }

   public boolean UiAnimation(int var1, boolean var2) {
      return this.keyCode == var1 && this.int94 == 0 && var2;
   }

   public int TridentAimbot() {
      return this.int94;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public EventTriggerKeyEvent(int var1, int var2) {
      this.int94 = var1;
      this.keyCode = var2;
   }
}
