package org.zenith.client.screens.override.main.component;

import java.util.ArrayList;
import java.util.List;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.layout.MainMenuLayout;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class MenuNavigationBar {
   public static final float PADDING = MainMenuLayout.px(6.0F);
   public static final float GAP = MainMenuLayout.px(8.0F);
   public static final float BUTTON_SIZE = MainMenuLayout.px(42.0F);
   public static final float ACTIVE_HORIZONTAL_PADDING = 12.0F;
   public static final float ACTIVE_CONTENT_GAP = 4.0F;
   public final List<MenuNavigationBar_Entry> entries = new ArrayList<>();

   public MenuNavigationBar(List<MenuNavigationBar_Item> var1) {
      for (MenuNavigationBar_Item menunavigationbar_item : var1) {
         this.entries.add(new MenuNavigationBar_Entry(menunavigationbar_item));
      }
   }

   public void render(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, int var5, ZenithStyle var6) {
      for (int i = 0; i < this.entries.size(); i++) {
         this.entries.get(i).activity.on23(i == var5);
      }

      float f5 = GAP * (this.entries.size() - 1);

      for (MenuNavigationBar_Entry menunavigationbar_entry : this.entries) {
         f5 += this.lerp(BUTTON_SIZE, this.getActiveWidth(menunavigationbar_entry.item), menunavigationbar_entry.activity.CancellableEvent());
      }

      float f6 = var4.x() + (var4.width() - f5) / 2.0F;
      float f7 = var4.y() + PADDING;
      float f = var4.height() - PADDING * 2.0F;
      ArgbColor i11ii1llliilllii1i1 = var6.getPrimaryColor().getColor();

      for (MenuNavigationBar_Entry menunavigationbar_entry1 : this.entries) {
         float f1 = menunavigationbar_entry1.activity.CancellableEvent();
         float f2 = this.getActiveWidth(menunavigationbar_entry1.item);
         float f3 = this.lerp(BUTTON_SIZE, f2, f1);
         menunavigationbar_entry1.bounds.setX(f6);
         menunavigationbar_entry1.bounds.setY(f7);
         menunavigationbar_entry1.bounds.setWidth(f3);
         menunavigationbar_entry1.bounds.setHeight(f);
         menunavigationbar_entry1.hover.on23(menunavigationbar_entry1.bounds.PotionItemBuilder(var2, var3));
         float f4 = menunavigationbar_entry1.hover.CancellableEvent();
         ArgbColor i11ii1llliilllii1i11 = var6.getFieldSurfaceBackground().getColor();
         ArgbColor i11ii1llliilllii1i12 = i11ii1llliilllii1i11.Easing(i11ii1llliilllii1i1, f1);
         ArgbColor i11ii1llliilllii1i13 = var6.getFieldBorder().getColor().Easing(var6.getFieldBorder().getColor().SprintStateEvent(0.0F), f1);
         var1.drawRoundedRect(f6, f7, f3, f, CornerRadius.MovementInputEvent(3.0F), i11ii1llliilllii1i12);
         var1.drawRoundedBorder(f6, f7, f3, f, -0.1F, CornerRadius.MovementInputEvent(3.0F), i11ii1llliilllii1i13);
         this.renderEntry(var1, menunavigationbar_entry1.item, f6, f7, f3, f, f2, f1, f4, i11ii1llliilllii1i1, var6);
         f6 += f3 + GAP;
      }
   }

   public void renderEntry(
      HudDrawContext var1,
      MenuNavigationBar_Item var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      ArgbColor var10,
      ZenithStyle var11
   ) {
      Font font = Fonts.NEW_ICONS.getFont(6.0F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(6.0F);
      float f = font.width(var2.icon());
      float f1 = var3 + (BUTTON_SIZE - f) / 2.0F;
      float f2 = var3 + 12.0F;
      float f3 = this.lerp(f1, f2, var8);
      float f4 = var3 + BUTTON_SIZE + 4.0F;
      float f5 = var3 + var7 - 12.0F - font1.width(var2.label());
      float f6 = this.lerp(f4, f5, var8);
      ArgbColor i11ii1llliilllii1i1 = var11.getTextTertiary().getColor().Easing(var10, var9);
      ArgbColor i11ii1llliilllii1i11 = i11ii1llliilllii1i1.Easing(var11.getTextEnable().getColor(), var8);
      var1.enableScissor(var3, var4, var3 + var5, var4 + var6);
      var1.drawText(font, var2.icon(), f3, var4 + (var6 - font.height()) / 2.0F, i11ii1llliilllii1i11);
      var1.drawText(font1, var2.label(), f6, var4 + (var6 - font1.height()) / 2.0F, var11.getTextEnable().getColor());
      var1.disableScissor();
   }

   public float getActiveWidth(MenuNavigationBar_Item var1) {
      Font font = Fonts.NEW_ICONS.getFont(6.0F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(6.0F);
      return 12.0F + font.width(var1.icon()) + 4.0F + font1.width(var1.label()) + 12.0F;
   }

   public float lerp(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }

   public boolean mouseClicked(double var1, double var3) {
      for (MenuNavigationBar_Entry menunavigationbar_entry : this.entries) {
         if (menunavigationbar_entry.bounds.PotionItemBuilder(var1, var3)) {
            menunavigationbar_entry.item.action().run();
            return true;
         }
      }

      return false;
   }
}
