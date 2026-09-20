package org.zenith.utility.render.display.base;

import org.zenith.core.MenuScreenId;

public abstract class AnimationCurve {
   protected float x;
   protected float y;
   protected float width;
   protected float height;

   protected AnimationCurve(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   protected AnimationCurve() {
      this(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void Easing(HudDrawContext var1) {
      this.ItemRegistry(var1);
      this.ColorAnimator(var1);
   }

   protected abstract void ColorAnimator(HudDrawContext var1);

   public void supplier3() {
   }

   public void ItemRegistry(HudDrawContext var1) {
   }

   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
   }

   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
   }

   public void UiAnimation(int var1, int var2, int var3) {
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }

   public void Easing(double var1, double var3, double var5, double var7) {
   }

   public void CrosshairTargetUpdateEvent(float var1, float var2) {
      this.x = var1;
      this.y = var2;
   }

   public void EmoteMetadata(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public boolean DataChangedEvent(float var1, float var2) {
      return RenderMathUtils.on23(this.x, this.y, this.width, this.height, var1, var2);
   }

   public boolean isHovered(double var1, double var3) {
      return RenderMathUtils.on23(this.x, this.y, this.width, this.height, var1, var3);
   }

   public boolean ItemSpec(HudDrawContext var1) {
      return this.DataChangedEvent(var1.getMouseX(), var1.getMouseY());
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public void setX(float var1) {
      this.x = var1;
   }

   public void setY(float var1) {
      this.y = var1;
   }

   public void setWidth(float var1) {
      this.width = var1;
   }

   public void setHeight(float var1) {
      this.height = var1;
   }
}
