package org.zenith.client.screens.nlgui.elements;

import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.CloudUserProfile;
import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiCloudFriendElement extends GuiFriendRowElement {
   public final CloudUserProfile friend;

   public GuiCloudFriendElement(CloudUserProfile var1) {
      this.friend = var1;
   }

   public boolean hasSettings() {
      return false;
   }

   @Override
   public String key() {
      return "cloud:" + this.getCloudUid();
   }

   @Override
   public boolean isCloud() {
      return true;
   }

   @Override
   public String getCloudUid() {
      return this.friend == null ? "" : this.friend.id();
   }

   @Override
   public String getLocalName() {
      return this.friend == null ? "" : this.friend.username();
   }

   @Override
   public String getName() {
      return this.getLocalName();
   }

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      return false;
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return false;
   }

   @Override
   public float render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      this.updateBounds(var4, var5, var6);
      this.updateVisible();
      return 28.0F;
   }
}
