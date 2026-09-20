package org.zenith.utility.render.display.base;

import net.minecraft.client.gui.DrawContext;

public class HudDrawContext extends CustomDrawContext {
   public final int mouseX;
   public final int mouseY;
   public final float delta;

   protected HudDrawContext(DrawContext var1, int var2, int var3, float var4) {
      super(var1);
      this.mouseX = var2;
      this.mouseY = var3;
      this.delta = var4;
   }

   public static HudDrawContext of(DrawContext var0, int var1, int var2, float var3) {
      return new HudDrawContext(var0, var1, var2, var3);
   }

   public int getMouseX() {
      return this.mouseX;
   }

   public int getMouseY() {
      return this.mouseY;
   }

   public float getDelta() {
      return this.delta;
   }
}
