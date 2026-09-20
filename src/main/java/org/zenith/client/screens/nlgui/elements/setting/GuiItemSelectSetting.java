package org.zenith.client.screens.nlgui.elements.setting;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import org.zenith.hud.ScrollHandler;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.StringListSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiItemSelectSetting extends GuiSetting<StringListSetting> {
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public SearchBox searchBox;
   public final ScrollHandler scrollHandler = new ScrollHandler();
   public boolean sortBySelected = false;
   public CornerRadiusF bounds;
   public CornerRadiusF rectBounds;
   public CornerRadiusF exitBounds;
   public CornerRadiusF searchBounds;
   public CornerRadiusF sortBounds;
   public CornerRadiusF listScissorBounds;
   public final Map<Block, CornerRadiusF> itemBounds = new HashMap<>();
   public boolean expanded;

   public GuiItemSelectSetting(StringListSetting var1) {
      this(var1, 166.0F);
   }

   public GuiItemSelectSetting(StringListSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.on23(var1, var3, 2.0F)) {
         this.expanded = !this.expanded;
         return true;
      }

      if (this.expanded && this.rectBounds != null && this.rectBounds.PotionItemBuilder(var1, var3)) {
         return true;
      }

      this.expanded = false;
      return false;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
         float f = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
         this.drawDefault(
            var1,
            var2,
            var3,
            "i",
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
         float f1 = 6.0F;
         float f2 = 6.0F;
         this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
         var1.drawRoundedRect(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            -0.5F,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
         );
         Font font2 = Fonts.NEW_ICONS.getFont(5.5F);
         var1.drawText(
            font2,
            "v",
            this.bounds.x() + (f1 - font2.width("v")) / 2.0F,
            this.bounds.y() + (f2 - font2.height()) / 2.0F,
            zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.animationExpanded.on23(this.expanded);
      if (!(this.animationExpanded.CancellableEvent() <= 0.0F) && this.bounds != null) {
         if (this.searchBox == null) {
            this.searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), "Search...", 0.0F);
            this.searchBox.on23(SearchBox.MatchMode.val179);
         }

         float f = GuiStyle.PADDING.intValue() * 2.0F;
         float f1 = f * 2.0F + this.getHeight();
         float f2 = 17.0F;
         float f3 = 115.0F;
         float f4 = 126.0F;
         float f5 = f * 2.0F;
         float f6 = f1 + f + f2 + f5 + f3;
         float f7 = this.bounds.x() + GuiStyle.PADDING.intValue() * 2.0F;
         float f8 = this.bounds.y() - f6 / 2.0F;
         this.rectBounds = new CornerRadiusF(f7, f8, f4, f6);
         var6 *= this.animationExpanded.CancellableEvent();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f7, this.bounds.y());
         var1.getMatrices().scale(this.animationExpanded.CancellableEvent(), this.animationExpanded.CancellableEvent());
         var1.getMatrices().translate(-f7, -this.bounds.y());
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            float popupCorner = GuiStyle.ROUND.intValue() / 2.0F;
            CornerRadius popupRadius = CornerRadius.MovementInputEvent(popupCorner);
            ShapeRenderer.ItemSpec(
               var1.getMatrices(),
               f7,
               f8,
               f4,
               f6,
               12.0F,
               popupRadius,
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f7,
               f8,
               f4,
               f1,
               CornerRadius.BotPacketEvent(popupCorner, popupCorner),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedRect(f7, f8 + f1, f4, f + f2 + f5, CornerRadius.var159, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6));
            var1.drawRoundedRect(
               f7,
               f8 + f1 + f2 + f5,
               f4,
               f3 + f,
               CornerRadius.BotRespawnEvent(popupCorner, popupCorner),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            Font font = Fonts.NEW_ICONS.getFont(4.0F);
            float f9 = f7 + f4 - font.width("2") - f;
            float f10 = f8 + f + font.height();
            this.exitBounds = new CornerRadiusF(f9, f10, 5.0F, 5.0F);
            var1.drawText(font, "2", f9, f10, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
            Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_REGULAR.getFont(5.3F);
            float f11 = f4 / 1.4F - GuiStyle.PADDING.intValue();
            ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
            this.drawDefault(
               var1,
               var2,
               var3,
               "i",
               this.setting.getName(),
               this.setting.getDescription(),
               font1,
               font2,
               f7 + f,
               f8 + f,
               f11,
               i11ii1llliilllii1i1,
               i11ii1llliilllii1i11,
               i11ii1llliilllii1i12
            );
            float f12 = f8 + f1 + f;
            this.searchBounds = new CornerRadiusF(f7 + f, f12, f4 - f * 2.0F, f2);
            var1.drawRoundedRect(
               this.searchBounds.x(),
               this.searchBounds.y(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedBorder(
               this.searchBounds.x(),
               this.searchBounds.y(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               0.1F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
            );
            float f13 = 14.0F;
            float f14 = this.searchBounds.width() - f * 2.0F - f13 - f;
            this.searchBox.setWidth(f14);
            this.searchBox.EventItemRenderHook(35);
            this.searchBox
               .on23(
                  var1,
                  this.searchBounds.x() + f,
                  this.searchBounds.y() + (f2 - Fonts.NEW_MEDIUM.getFont(5.3F).height()) / 2.0F,
                  zenithstyle.getTextEnable().getColor().SprintStateEvent(var6),
                  zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6)
               );
            this.sortBounds = new CornerRadiusF(this.searchBounds.x() + this.searchBounds.width() - f - f13, f12 + (f2 - f13) / 2.0F, f13, f13);
            var1.drawRoundedRect(
               this.sortBounds.x(),
               this.sortBounds.y(),
               f13,
               f13,
               CornerRadius.MovementInputEvent(2.0F),
               zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
            );
            Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
            var1.drawText(
               font3,
               "W",
               this.sortBounds.x() + (f13 - font3.width("W")) / 2.0F,
               this.sortBounds.y() + (f13 - font3.height()) / 2.0F,
               zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
            );
            List<Block> list = this.getFilteredAndSortedBlocks();
            float f15 = 13.0F;
            float f16 = f15 + GuiStyle.PADDING.intValue();
            float f17 = list.size() * f16;
            this.scrollHandler.ProtocolMessage(Math.max(0.0F, f17 - f3));
            this.scrollHandler.update();
            float f18 = f12 + f2 + f5;
            this.listScissorBounds = new CornerRadiusF(f7, f18, f4, f3 - f);
            float f19 = f18 - (float)this.scrollHandler.float260();
            float f20 = f7 + f;
            float f21 = 105.0F;
            this.itemBounds.clear();
            var1.enableScissor((int)f7, (int)f18, (int)(f7 + f4), (int)(f18 + f3));
            Font font4 = Fonts.NEW_MEDIUM.getFont(5.3F);
            float f22 = 8.0F;
            float f23 = 0.5F;
            int i = 0;
            ArgbColor i11ii1llliilllii1i13 = zenithstyle.getFieldSurfaceBackground().getColor(var6);
            ArgbColor i11ii1llliilllii1i14 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(0.25F * var6);
            ArgbColor i11ii1llliilllii1i15 = zenithstyle.getFieldBorder().getColor(var6);

            for (Block block : list) {
               if (f19 + i * f16 < f18 - f16) {
                  i++;
               } else {
                  float f24 = f19 + i * f16;
                  if (f24 > f18 + f3) {
                     break;
                  }

                  boolean flag = this.setting.Easing(block);
                  CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f20, f24, f21, f15);
                  this.itemBounds.put(block, l11liliill1iii1);
                  var1.drawRoundedRect(
                     l11liliill1iii1.x(),
                     l11liliill1iii1.y(),
                     l11liliill1iii1.width(),
                     l11liliill1iii1.height(),
                     CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
                     flag ? i11ii1llliilllii1i14 : i11ii1llliilllii1i13
                  );
                  var1.drawRoundedBorder(
                     l11liliill1iii1.x(),
                     l11liliill1iii1.y(),
                     l11liliill1iii1.width(),
                     l11liliill1iii1.height(),
                     0.1F,
                     CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
                     i11ii1llliilllii1i15
                  );
                  var1.getMatrices().pushMatrix();
                  var1.getMatrices().translate(f20 + 4.0F, f24 + (f15 - f22) / 2.0F);
                  var1.getMatrices().scale(f23, f23);
                  var1.drawItem(block.asItem().getDefaultStack(), 0, 0);
                  var1.getMatrices().popMatrix();
                  String s = block.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
                  if (!s.isEmpty()) {
                     s = s.substring(0, 1).toUpperCase() + s.substring(1);
                  }

                  float f25 = f20 + GuiStyle.PADDING.intValue() + f22 + GuiStyle.PADDING.intValue() / 2.0F;
                  var1.drawText(
                     font4,
                     s,
                     f25,
                     f24 + (f15 - font4.height()) / 2.0F,
                     (flag ? zenithstyle.getTextEnable() : zenithstyle.getTextSecondary()).getColor().SprintStateEvent(var6)
                  );
                  i++;
               }
            }

            var1.disableScissor();
            float f26 = 1.5F;
            float f27 = f7 + f4 - f - f26;
            float f28 = f3 - f / 2.0F;
            if (this.scrollHandler.float261() > 0.0) {
               var1.drawRoundedRect(
                  f27, f18, f26, f28, CornerRadius.MovementInputEvent(0.5F), zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
               );
               float f29 = (float)(this.scrollHandler.float260() / this.scrollHandler.float261());
               float f30 = Math.max(f28 * (f28 / (float)(f28 + this.scrollHandler.float261())), 12.0F);
               float f31 = Math.max(1.0F, f28 - f30);
               float f32 = f18 + f31 * f29;
               f32 = Math.min(f18 + f28 - f30, f32);
               var1.drawRoundedRect(f27, f32, f26, f30, CornerRadius.MovementInputEvent(0.5F), zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6));
            }

            var1.getMatrices().popMatrix();
         }
      }
   }

   public List<Block> getFilteredAndSortedBlocks() {
      String s = this.searchBox != null && !this.searchBox.isEmpty() ? this.searchBox.getText().toLowerCase().trim() : "";
      Stream<Block> stream = getAllBlocks().filter(var0 -> var0 != Blocks.AIR);
      if (!s.isEmpty()) {
         stream = stream.filter(var1x -> {
            String s1 = var1x.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
            return s1.toLowerCase().contains(s);
         });
      }

      List<Block> arraylist = new ArrayList<>(stream.toList());
      if (this.sortBySelected || s.isEmpty()) {
         arraylist.sort((var1x, var2x) -> {
            boolean flag = this.setting.Easing(var1x);
            boolean flag1 = this.setting.Easing(var2x);
            return Boolean.compare(!flag, !flag1);
         });
      }

      return arraylist;
   }

   public static Stream<Block> getAllBlocks() {
      return Stream.of(Blocks.class.getDeclaredFields())
         .filter(var0 -> Modifier.isStatic(var0.getModifiers()))
         .filter(var0 -> Modifier.isPublic(var0.getModifiers()))
         .filter(var0 -> Block.class.isAssignableFrom(var0.getType()))
         .map(var0 -> {
            try {
               return (Block)var0.get(null);
            } catch (IllegalAccessException illegalaccessexception) {
               throw new RuntimeException(illegalaccessexception);
            }
         });
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.expanded || this.rectBounds == null) {
         return false;
      }

      if (!this.rectBounds.PotionItemBuilder(var1, var3)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.VelocityChangeEvent(false);
         }

         return false;
      } else if (this.exitBounds != null && this.exitBounds.on23(var1, var3, 2.0F)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.VelocityChangeEvent(false);
         }

         return true;
      } else if (this.searchBounds != null && this.searchBounds.PotionItemBuilder(var1, var3)) {
         if (this.searchBox != null) {
            this.searchBox.VelocityChangeEvent(true);
         }

         return true;
      } else {
         if (this.searchBox != null) {
            this.searchBox.VelocityChangeEvent(false);
         }

         if (this.sortBounds != null && this.sortBounds.on23(var1, var3, 2.0F)) {
            this.sortBySelected = !this.sortBySelected;
            this.scrollHandler.AnalyticsTracker(0.0);
            return true;
         }

         if (this.listScissorBounds != null && this.listScissorBounds.PotionItemBuilder(var1, var3) && var5.int203() == 0) {
            for (Entry<Block, CornerRadiusF> entry : this.itemBounds.entrySet()) {
               if (entry.getValue().PotionItemBuilder(var1, var3)) {
                  if (this.setting.Easing(entry.getKey())) {
                     this.setting.UiAnimation(entry.getKey());
                  } else {
                     this.setting.on23(entry.getKey());
                  }

                  return true;
               }
            }
         }

         return true;
      }
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      if (!this.expanded) {
         return false;
      }

      if (this.listScissorBounds != null && this.listScissorBounds.PotionItemBuilder(var1, var3)) {
         this.scrollHandler.CloudRouter(var7 * 10.0);
         return true;
      }

      this.expanded = false;
      if (this.searchBox != null) {
         this.searchBox.VelocityChangeEvent(false);
      }

      return false;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.expanded && this.searchBox != null && this.searchBox.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.expanded && this.searchBox != null && this.searchBox.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }
}
