package org.zenith.client.screens.nlgui.panel.api;

import java.util.List;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class ElementPanel extends Panel {
   public abstract void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7);

   public abstract void close();

   public abstract List<? extends Element> getElements();

   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
   }

   public float getButtonWidth() {
      return 0.0F;
   }

   public void renderHeaderButtons(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public boolean onHeaderButtonsClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return false;
   }

   public boolean isRightDrawerOpen() {
      return false;
   }

   public void closeRightDrawer() {
   }

   public boolean isRender() {
      return false;
   }

   public void tick() {
   }

   public void renderRightPanel(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
   }

   public CornerRadiusF getRightPanelBlurBounds(float var1, float var2, float var3, float var4) {
      return null;
   }

   public float getRightPanelBlurProgress(float var1, float var2) {
      return var1 * var2;
   }
}
