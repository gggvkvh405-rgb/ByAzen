package org.zenith.client.screens.nlgui.elements;

import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class GuiFriendRowElement extends Element {
   public static final float HEIGHT = 28.0F;
   public static final float REMOVE_HEIGHT = 13.0F;
   public static final Easing LINEAR = (var0, var1, var2, var3) -> var3 == 0.0F ? var1 + var2 : var1 + var2 * (var0 / var3);
   public final UiAnimation visibleAnimation = new UiAnimation(120L, 1.0F, LINEAR);
   public final UiAnimation removeHoverAnimation = new UiAnimation(100L, 0.0F, LINEAR);
   public boolean touched;
   public boolean targetVisible = true;
   public float order;
   public CornerRadiusF removeBounds = new CornerRadiusF(0.0F, 0.0F, 0.0F, 0.0F);
   protected CornerRadiusF bounds = new CornerRadiusF(0.0F, 0.0F, 0.0F, 0.0F);

   public abstract String key();

   public abstract boolean isCloud();

   public abstract String getCloudUid();

   public abstract String getLocalName();

   @Override
   public float getHeight() {
      return 28.0F;
   }

   @Override
   public float getWidth() {
      return this.bounds.width();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public abstract float render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8);

   public final void beginSync() {
      this.touched = false;
      this.targetVisible = false;
   }

   public final void markPresent(float var1) {
      this.touched = true;
      this.targetVisible = true;
      this.order = var1;
   }

   public final boolean shouldRemoveAfterSync() {
      this.visibleAnimation.on23(this.targetVisible ? 1.0F : 0.0F);
      return !this.targetVisible && this.visibleAnimation.CancellableEvent() <= 0.001F;
   }

   protected final float updateVisible() {
      return this.visibleAnimation.on23(this.targetVisible ? 1.0F : 0.0F);
   }

   protected final void updateBounds(float var1, float var2, float var3) {
      this.bounds = new CornerRadiusF(var1, var2, var3, 28.0F);
      this.removeBounds = new CornerRadiusF(var1 + Math.max(0.0F, var3 - 13.0F - 6.0F), var2 + 7.5F, 13.0F, 13.0F);
   }

   public CornerRadiusF getRemoveBounds() {
      return this.removeBounds;
   }

   public CornerRadiusF getBounds() {
      return this.bounds;
   }

   protected void setRemoveBounds(CornerRadiusF var1) {
      this.removeBounds = var1;
   }

   public UiAnimation getRemoveHoverAnimation() {
      return this.removeHoverAnimation;
   }

   public UiAnimation getVisibleAnimation() {
      return this.visibleAnimation;
   }

   public boolean isTargetVisible() {
      return this.targetVisible;
   }

   public void setTargetVisible(boolean var1) {
      this.targetVisible = var1;
   }

   public float getOrder() {
      return this.order;
   }

   public boolean isTouched() {
      return this.touched;
   }
}
