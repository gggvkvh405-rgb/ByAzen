package org.zenith.client.screens.nlgui.elements;

import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiFriendRequestElement extends Element {
   public static final float HEIGHT = 28.0F;
   public static final Easing LINEAR = (var0, var1, var2, var3) -> var3 == 0.0F ? var1 + var2 : var1 + var2 * (var0 / var3);
   public final UiAnimation visibleAnimation = new UiAnimation(120L, 1.0F, LINEAR);
   public final String uid;
   public String role;
   public boolean touched;
   public boolean targetVisible = true;
   public CornerRadiusF acceptBounds = new CornerRadiusF(0.0F, 0.0F, 0.0F, 0.0F);
   public CornerRadiusF declineBounds = new CornerRadiusF(0.0F, 0.0F, 0.0F, 0.0F);

   public GuiFriendRequestElement(String var1, String var2) {
      this.uid = var1 == null ? "" : var1;
      this.role = var2 == null ? "" : var2;
   }

   public String key() {
      return this.uid;
   }

   public void beginSync() {
      this.touched = false;
      this.targetVisible = false;
   }

   public void syncFromRequest(String var1) {
      this.role = var1 == null ? "" : var1;
      this.touched = true;
      this.targetVisible = true;
   }

   public boolean shouldRemoveAfterSync() {
      this.visibleAnimation.on23(this.targetVisible ? 1.0F : 0.0F);
      return !this.targetVisible && this.visibleAnimation.CancellableEvent() <= 0.001F;
   }

   public float render(HudDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      float f = 13.0F;
      this.acceptBounds = new CornerRadiusF(var2 + Math.max(0.0F, var4 - f * 2.0F - 10.0F), var3 + 7.5F, f, f);
      this.declineBounds = new CornerRadiusF(var2 + Math.max(0.0F, var4 - f - 5.0F), var3 + 7.5F, f, f);
      this.visibleAnimation.on23(this.targetVisible ? 1.0F : 0.0F);
      return 28.0F;
   }

   @Override
   public String getName() {
      return this.uid;
   }

   @Override
   public float getHeight() {
      return 28.0F;
   }

   @Override
   public float getWidth() {
      return 0.0F;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public void setRole(String var1) {
      this.role = var1;
   }

   public void setTouched(boolean var1) {
      this.touched = var1;
   }

   public void setTargetVisible(boolean var1) {
      this.targetVisible = var1;
   }

   public UiAnimation getVisibleAnimation() {
      return this.visibleAnimation;
   }

   public String getUid() {
      return this.uid;
   }

   public String getRole() {
      return this.role;
   }

   public boolean isTouched() {
      return this.touched;
   }

   public boolean isTargetVisible() {
      return this.targetVisible;
   }

   public CornerRadiusF getAcceptBounds() {
      return this.acceptBounds;
   }

   public CornerRadiusF getDeclineBounds() {
      return this.declineBounds;
   }
}
