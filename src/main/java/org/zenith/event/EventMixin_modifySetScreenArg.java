package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.gui.screen.Screen;

public class EventMixin_modifySetScreenArg implements Event {
   public Screen screen;

   public EventMixin_modifySetScreenArg(Screen var1) {
      this.screen = var1;
   }

   public Screen AutoCraftHelper() {
      return this.screen;
   }

   public void on23(Screen var1) {
      this.screen = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (var1 instanceof EventMixin_modifySetScreenArg llli1iilli1ii1) {
         if (!llli1iilli1ii1.canEqual(this)) {
            return false;
         }

         Screen screen = this.AutoCraftHelper();
         Screen screen1 = llli1iilli1ii1.AutoCraftHelper();
         return screen == null ? screen1 == null : screen.equals(screen1);
      } else {
         return false;
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EventMixin_modifySetScreenArg;
   }

   @Override
   public int hashCode() {
      byte b0 = 59;
      byte b1 = 1;
      Screen screen = this.AutoCraftHelper();
      return b1 * 59 + (screen == null ? 43 : screen.hashCode());
   }

   @Override
   public String toString() {
      return "EventSetScreen(screen=" + this.AutoCraftHelper() + ")";
   }
}
