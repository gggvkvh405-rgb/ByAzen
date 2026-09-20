package org.zenith.client.screens.nlgui.elements.api;

import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class InterfaceElement extends Element {
   @Override
   public abstract float getWidth();

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
   }

   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return super.charTyped(var1, var2);
   }
}
