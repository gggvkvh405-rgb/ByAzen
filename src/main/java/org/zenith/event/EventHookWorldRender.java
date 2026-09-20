package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.util.math.MatrixStack;

public final class EventHookWorldRender implements Event {
   public final MatrixStack matrixStack;
   public final float getVar125;

   public MatrixStack ClanUpgrade() {
      return this.matrixStack;
   }

   public float CropFarmer() {
      return this.getVar125;
   }

   public EventHookWorldRender(MatrixStack var1, float var2) {
      this.matrixStack = var1;
      this.getVar125 = var2;
   }
}
