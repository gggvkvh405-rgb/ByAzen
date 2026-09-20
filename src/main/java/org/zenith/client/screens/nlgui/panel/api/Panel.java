package org.zenith.client.screens.nlgui.panel.api;

import org.zenith.core.ClientProvider;
import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class Panel implements ClientProvider {
   public abstract void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6);

   public abstract boolean onMouseClicked(double var1, double var3, MenuScreenId var5);

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }
}
