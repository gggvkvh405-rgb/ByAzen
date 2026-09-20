package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.util.math.Vector2f;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.setting.TextSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiStringSetting extends GuiSetting<TextSetting> {
   public final UiAnimation focusAnimation = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public CornerRadiusF bounds;
   public SearchBox textBox;

   public GuiStringSetting(TextSetting var1) {
      super(166.0F, var1);
   }

   public GuiStringSetting(TextSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3) && var5 == MenuScreenId.call004) {
         this.getBox().VelocityChangeEvent(true);
         return true;
      }

      if (!this.getBox().onMouseClicked(var1, var3, var5)) {
         this.setting.setValueSafe(this.getBox().getText());
      }

      return false;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
         SearchBox i1lil1lliilli1lli1l = this.getBox();
         if (!i1lil1lliilli1lli1l.isSelected()) {
            i1lil1lliilli1lli1l.HudHotbarPanel(this.setting.getValue());
            i1lil1lliilli1lli1l.EventRender(this.setting.getValue().length());
         }

         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
         float f = this.width / 2.0F - GuiStyle.PADDING.intValue();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
         this.drawDefault(
            var1,
            var2,
            var3,
            "M",
            this.setting.getName(),
            this.setting.getDescription(),
            font,
            font1,
            var4,
            var5,
            f,
            i11ii1llliilllii1i1,
            i11ii1llliilllii1i11,
            i11ii1llliilllii1i12
         );
         float f1 = this.width / 2.0F;
         float f2 = this.getHeight();
         this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
         this.focusAnimation.on23(i1lil1lliilli1lli1l.isSelected() || this.bounds.PotionItemBuilder(var2, var3));
         var1.drawRoundedRect(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            0.1F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldBorder()
               .getColor()
               .Easing(zenithstyle.getPrimaryColor().getColor().SprintStateEvent(0.5F), this.focusAnimation.CancellableEvent())
               .SprintStateEvent(var6)
         );
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.3F);
         i1lil1lliilli1lli1l.on23(font2);
         i1lil1lliilli1lli1l.setWidth(f1 - GuiStyle.PADDING.intValue() * 2.0F);
         i1lil1lliilli1lli1l.EventItemRenderHook(this.setting.getValidator().getMaxLength());
         i1lil1lliilli1lli1l.EventInjectPlaced(this.setting.isSecret());
         i1lil1lliilli1lli1l.HudInventoryPanel(ZenithClient.on23().Easing().translate(this.setting.getEmptyText()));
         i1lil1lliilli1lli1l.on23(
            var1,
            this.bounds.x() + GuiStyle.PADDING.intValue(),
            this.bounds.y() + (f2 - font2.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor().SprintStateEvent(var6),
            zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6)
         );
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      SearchBox i1lil1lliilli1lli1l = this.getBox();
      if (i1lil1lliilli1lli1l.isSelected()) {
         if (var1 == 257) {
            this.setting.setValueSafe(i1lil1lliilli1lli1l.getText());
            i1lil1lliilli1lli1l.VelocityChangeEvent(false);
            return true;
         }

         if (var1 == 256) {
            i1lil1lliilli1lli1l.VelocityChangeEvent(false);
            return true;
         }
      }

      return i1lil1lliilli1lli1l.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.getBox().charTyped(var1, var2);
   }

   @Override
   public float getHeight() {
      return 14.0F;
   }

   public SearchBox getBox() {
      if (this.textBox == null) {
         this.textBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), this.setting.getEmptyText(), 40.0F);
      }

      return this.textBox;
   }
}
