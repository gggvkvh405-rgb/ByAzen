package org.zenith.client.screens.nlgui.elements;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.cosmetics.CosmeticAvatarImageCache;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class CosmeticElement extends InterfaceElement {
   public static final float CARD_HEIGHT = 79.0F;
   public static final float CARD_HEADER_HEIGHT = 23.0F;
   public static final int COLUMNS = 3;
   public final String name;
   public final String relativePath;
   public final Path path;
   public final Supplier<Path> selectedPathSupplier;
   public final Consumer<Path> onSelect;
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public CornerRadiusF bounds;

   public CosmeticElement(String var1, String var2, Path var3, Supplier<Path> var4, Consumer<Path> var5) {
      this.name = var1;
      this.relativePath = var2;
      this.path = var3;
      this.selectedPathSupplier = var4;
      this.onSelect = var5;
      if (this.isSelected()) {
         this.animationEnable.setValue(1.0F);
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   public String getRelativePath() {
      return this.relativePath;
   }

   public Path getPath() {
      return this.path;
   }

   @Override
   public float getHeight() {
      return 79.0F;
   }

   @Override
   public float getWidth() {
      float f = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = GuiStyle.PADDING.intValue();
      return (f - f1 * 2.0F) / 3.0F;
   }

   public boolean isSelected() {
      Path pathx = this.selectedPathSupplier != null ? this.selectedPathSupplier.get() : null;
      return pathx != null && this.path != null ? this.path.toAbsolutePath().normalize().equals(pathx.toAbsolutePath().normalize()) : false;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = this.getWidth();
         this.bounds = new CornerRadiusF(var4, var5, f, 79.0F);
         boolean flag = this.isSelected();
         this.animationEnable.on23(flag);
         float f1 = this.animationEnable.CancellableEvent();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getPrimaryColor().getColor();
         ArgbColor i11ii1llliilllii1i12 = i11ii1llliilllii1i11.SprintStateEvent(var6 * (flag ? 1.0F : 0.8F));
         ArgbColor i11ii1llliilllii1i13 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i14 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i15 = zenithstyle.getHeaderDisableBackground()
            .getColor()
            .Easing(zenithstyle.getSurfaceEnableBackground().getColor(), f1)
            .SprintStateEvent(var6);
         var1.drawRoundedRectBatched(var4, var5, f, 79.0F, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i1);
         var1.flushRoundedRects();
         float f2 = 40.0F;
         float f3 = 40.0F;
         float f4 = var4 + (f - f2) / 2.0F;
         float f5 = var5 + (56.0F - f3) / 2.0F;
         Identifier identifier = CosmeticAvatarImageCache.getAvatarTextureId(this.path);
         if (identifier != null) {
            ShapeRenderer.on23(
               var1.getMatrices(),
               identifier,
               f4,
               f5,
               f2,
               f3,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 1.5F),
               ArgbColor.var11934.SprintStateEvent(var6)
            );
         } else {
            String s = "No preview";
            Font font = Fonts.NEW_REGULAR.getFont(4.7F);
            float f6 = f4 + (f2 - font.width(s)) / 2.0F;
            float f7 = f5 + (f3 - font.height()) / 2.0F;
            var1.drawText(font, s, f6, f7, i11ii1llliilllii1i14);
         }

         float f18 = var5 + 79.0F - 23.0F;
         var1.drawRoundedRect(var4, f18, f, 23.0F, CornerRadius.RotationUpdateStartEvent(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i15);
         Font font1 = Fonts.NEW_MEDIUM.getFont(5.2F);
         String s2 = flag ? "A" : "N";
         Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
         float f8 = var4 + GuiStyle.PADDING.intValue() * 1.4F;
         float f9 = f18 + (23.0F - font2.height()) / 2.0F - 0.2F;
         var1.drawText(font2, s2, f8, f9, i11ii1llliilllii1i12);
         float f10 = f8 + font2.width(s2) + GuiStyle.PADDING.intValue();
         float f11 = f18 + (23.0F - font1.height()) / 2.0F - 0.2F;
         float f12 = f / 2.0F;
         String s1 = trimToWidth(font1, this.name, f12);
         var1.drawText(font1, s1, f10, f11, i11ii1llliilllii1i13);
         float f13 = 12.0F;
         float f14 = 7.0F;
         float f15 = f18 + GuiStyle.PADDING * 2;
         float f16 = var4 + f - GuiStyle.PADDING * 2 - f13;
         var1.drawRoundedRectBatched(
            f16,
            f15,
            f13,
            f14,
            CornerRadius.MovementInputEvent(2.5F),
            zenithstyle.getDisableActiveBg().getColor().Easing(i11ii1llliilllii1i11, f1).SprintStateEvent(var6)
         );
         float f17 = MathHelper.lerp(f1, 1.0F, f13 - 1.0F - 5.0F);
         var1.drawRoundedRectBatched(
            f16 + f17,
            f15 + 1.0F,
            5.0F,
            5.0F,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f1).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 != MenuScreenId.call004 || this.bounds == null || this.onSelect == null) {
         return false;
      } else if (this.bounds.PotionItemBuilder(var1, var3)) {
         this.onSelect.accept(this.isSelected() ? null : this.path);
         return true;
      } else {
         return false;
      }
   }

   public static String trimToWidth(Font var0, String var1, float var2) {
      if (var1 != null && var1.contains("/")) {
         var1 = var1.substring(0, var1.indexOf("/"));
      }

      if (var1 != null && var1.contains("(")) {
         var1 = var1.substring(0, var1.indexOf("("));
      }

      if (var1 != null && !var1.isEmpty() && !(var0.width(var1) <= var2)) {
         String s = "...";
         float f = var0.width(s);
         if (f > var2) {
            return "";
         }

         int i = var1.length();

         while (i > 0 && var0.width(var1.substring(0, i)) + f > var2) {
            i--;
         }

         return i <= 0 ? s : var1.substring(0, i) + s;
      } else {
         return var1 == null ? "" : var1;
      }
   }
}
