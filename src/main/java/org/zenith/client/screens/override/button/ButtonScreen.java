package org.zenith.client.screens.override.button;

import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.AnimationValue;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class ButtonScreen {
   public AnimationValue bounds;

   protected ButtonScreen(float var1, float var2) {
      this.bounds = new AnimationValue(0.0F, 0.0F, var1, var2);
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      this.bounds.setX(var4);
      this.bounds.setY(var5);
   }

   public float getWidth() {
      return this.bounds.getWidth();
   }

   public float getHeight() {
      return this.bounds.getHeight();
   }

   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds.PotionItemBuilder(var1, var3)) {
         this.onClick(var1, var3, var5);
      }
   }

   public abstract void onClick(double var1, double var3, MenuScreenId var5);

   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
   }

   public void onMouseDragged(double var1, double var3, MenuScreenId var5, double var6, double var8) {
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }
}
