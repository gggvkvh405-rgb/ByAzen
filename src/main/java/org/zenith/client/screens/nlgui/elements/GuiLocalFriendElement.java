package org.zenith.client.screens.nlgui.elements;

import java.util.Locale;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiLocalFriendElement extends GuiFriendRowElement {
   public final String name;

   public GuiLocalFriendElement(String var1) {
      this.name = var1 == null ? "" : var1;
   }

   public void syncLocal(float var1) {
      this.markPresent(var1);
   }

   @Override
   public String key() {
      return "local:" + this.name.toLowerCase(Locale.ROOT);
   }

   @Override
   public boolean isCloud() {
      return false;
   }

   @Override
   public String getCloudUid() {
      return "";
   }

   @Override
   public String getLocalName() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public float render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      this.updateBounds(var4, var5, var6);
      this.updateVisible();
      return 28.0F;
   }
}
