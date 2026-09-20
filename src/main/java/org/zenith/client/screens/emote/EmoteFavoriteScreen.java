package org.zenith.client.screens.emote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.EmotePlayback;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.managers.EmoteMetadata;
import org.zenith.managers.EmoteRegistry;
import org.zenith.module.misc.Emotes;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class EmoteFavoriteScreen extends Screen {
   public static final int PREVIEW_ANIMATION_PRIORITY = 2000;
   public static final int COLUMNS = 3;
   public static final float GAP = 5.0F;
   public static final float PREVIEW_GAP = 12.0F;
   public static final float CARD_HEIGHT = 72.0F;
   public static final float SCROLL_SPEED = 38.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public final Emotes owner;
   public final int favoriteSlot;
   public final List<EmoteMetadata> allEmotes;
   public final SearchBox searchBox;
   public final UiAnimation openAnimation = new UiAnimation(280L, 0.0F, Easing.EventInjectHandleInputEvents);
   public final UiAnimation contentAnimation = new UiAnimation(220L, 0.0F, Easing.EventTriggerKeyEvent);
   public final UiAnimation searchAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation closeAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation scrollBarAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final Map<String, UiAnimation> cardAnimations = new HashMap<>();
   public final PlayerPreview3D playerPreview = new PlayerPreview3D(0.0F, 0.0F, 0.0F, 0.0F);
   public final AnimationContainer<EmotePlayback> previewAnimation = new AnimationContainer(null);
   public List<EmoteMetadata> filteredEmotes;
   public ClientPlayerEntity previewAnimationEntity;
   public EmotePlayback previewEmotePlayer;
   public String previewEmoteId;
   public String previousSearch = "";
   public float scroll;
   public float scrollTarget;
   public float interactiveScale = 1.0F;
   public boolean closing;
   public boolean draggingScrollBar;
   public float scrollBarDragOffset;

   public EmoteFavoriteScreen(Emotes var1, int var2) {
      super(Text.literal("Favorite emote"));
      this.owner = var1;
      this.favoriteSlot = var2;
      this.allEmotes = EmoteRegistry.set19().stream().sorted(Comparator.comparing(EmoteMetadata::displayName, String.CASE_INSENSITIVE_ORDER)).toList();
      this.filteredEmotes = this.allEmotes;
      this.searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), "Поиск эмоции...", 180.0F);
      this.searchBox.EventItemRenderHook(80);

      for (EmoteMetadata li1ll1i111l1l1iilli1111il : this.allEmotes) {
         this.cardAnimations.put(li1ll1i111l1l1iilli1111il.id(), new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent));
      }
   }

   protected void init() {
      this.openAnimation.setValue(0.0F);
      this.openAnimation.on23(1.0F);
      this.restartContentAnimation();
      this.closing = false;
      this.searchBox.VelocityChangeEvent(false);
   }

   public void tick() {
      super.tick();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      float f = this.openAnimation.on23(this.closing ? 0.0F : 1.0F);
      float f1 = Math.clamp(f, 0.0F, 1.0F);
      if (this.closing && f1 <= 0.001F) {
         this.finishClose();
      } else {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            context.fill(
               0, 0, this.width, this.height, zenithstyle.getRightBackground().getColor().EventHookWorldRender((int)(145.0F * f1)).call001()
            );
            HudDrawContext ililll1lli1i11l11l111i1l1 = HudDrawContext.of(context, mouseX, mouseY, delta);
            EmoteFavoriteScreen_Layout emotefavoritescreen_layout = this.layout();
            this.updateFilter();
            this.interactiveScale = 0.9F + 0.1F * f;
            float f2 = this.transformX(mouseX, emotefavoritescreen_layout);
            float f3 = this.transformY(mouseY, emotefavoritescreen_layout);
            ililll1lli1i11l11l111i1l1.getMatrices().pushMatrix();
            ililll1lli1i11l11l111i1l1.getMatrices().translate(emotefavoritescreen_layout.centerX(), emotefavoritescreen_layout.centerY());
            ililll1lli1i11l11l111i1l1.getMatrices().scale(this.interactiveScale, this.interactiveScale);
            ililll1lli1i11l11l111i1l1.getMatrices().translate(-emotefavoritescreen_layout.centerX(), -emotefavoritescreen_layout.centerY());
            ArgbColor i11ii1llliilllii1i1 = zenithstyle.getLeftBackground().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i11 = zenithstyle.getRightBackground().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i12 = zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i13 = zenithstyle.getSurfaceEnableBackground().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i14 = zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i15 = zenithstyle.getTextEnable().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i16 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i17 = zenithstyle.getTextTertiary().getColor().SprintStateEvent(f1);
            ArgbColor i11ii1llliilllii1i18 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f1);
            float f4 = ZenithClient.on23().NbtEditor().getBlurPower();
            if (f4 != 0.0F) {
               ShapeRenderer.on23(
                  ililll1lli1i11l11l111i1l1.getMatrices(),
                  emotefavoritescreen_layout.panelX,
                  emotefavoritescreen_layout.panelY,
                  emotefavoritescreen_layout.panelWidth,
                  emotefavoritescreen_layout.panelHeight,
                  f4,
                  CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
                  ArgbColor.var11934.SprintStateEvent(f1),
                  true,
                  false
               );
            }

            ililll1lli1i11l11l111i1l1.drawRoundedRect(
               emotefavoritescreen_layout.panelX,
               emotefavoritescreen_layout.panelY,
               emotefavoritescreen_layout.panelWidth,
               emotefavoritescreen_layout.panelHeight,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
               i11ii1llliilllii1i1
            );
            ililll1lli1i11l11l111i1l1.drawRoundedRect(
               emotefavoritescreen_layout.panelX + 6.0F,
               emotefavoritescreen_layout.panelY + 6.0F,
               emotefavoritescreen_layout.panelWidth - 12.0F,
               37.0F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() - 1.0F),
               i11ii1llliilllii1i11
            );
            this.renderHeader(
               ililll1lli1i11l11l111i1l1,
               emotefavoritescreen_layout,
               f2,
               f3,
               zenithstyle,
               i11ii1llliilllii1i12,
               i11ii1llliilllii1i13,
               i11ii1llliilllii1i15,
               i11ii1llliilllii1i16,
               f1
            );
            this.renderCards(
               ililll1lli1i11l11l111i1l1,
               emotefavoritescreen_layout,
               f2,
               f3,
               i11ii1llliilllii1i12,
               i11ii1llliilllii1i13,
               i11ii1llliilllii1i14,
               i11ii1llliilllii1i15,
               i11ii1llliilllii1i17,
               i11ii1llliilllii1i18,
               f1
            );
            this.renderScrollBar(ililll1lli1i11l11l111i1l1, emotefavoritescreen_layout, f2, f3, zenithstyle, f1);
            this.renderPlayerPreview(context, ililll1lli1i11l11l111i1l1, emotefavoritescreen_layout, f2, f3, i11ii1llliilllii1i12, f1);
            ililll1lli1i11l11l111i1l1.getMatrices().popMatrix();
         }
      }
   }

   public void renderPlayerPreview(DrawContext var1, HudDrawContext var2, EmoteFavoriteScreen_Layout var3, float var4, float var5, ArgbColor var6, float var7) {
      var2.drawRoundedRect(var3.previewX, var3.gridY, var3.previewWidth, var3.gridHeight, CornerRadius.MovementInputEvent(6.0F), var6);
      if (this.client != null && this.client.player != null) {
         this.playerPreview.setBounds(var3.previewX, var3.gridY, var3.previewWidth, var3.gridHeight);
         var2.enableScissor((int)var3.previewX, (int)var3.gridY, (int)(var3.previewX + var3.previewWidth), (int)(var3.gridY + var3.gridHeight));
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, var7);

         try {
            if (this.previewEmotePlayer == null) {
               this.detachPreviewAnimationLayer();
               this.playerPreview.render(var1, this.client.player, var4, var5);
            } else {
               this.attachPreviewAnimation(this.client.player);
               this.previewAnimation.setAnim(this.previewEmotePlayer);

               try {
                  this.playerPreview.render(var1, this.client.player, var4, var5);
               } finally {
                  this.previewAnimation.setAnim(null);
               }
            }
         } finally {
            org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            var2.disableScissor();
         }
      }
   }

   public void renderHeader(
      HudDrawContext var1,
      EmoteFavoriteScreen_Layout var2,
      float var3,
      float var4,
      ZenithStyle var5,
      ArgbColor var6,
      ArgbColor var7,
      ArgbColor var8,
      ArgbColor var9,
      float var10
   ) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.8F);
      String s = "Выберите эмоцию для этого слота";
      var1.drawText(font, s, var2.contentX, var2.panelY + (37.0F - font.height()) / 2.0F + 6.0F, var8);
      boolean flag = inside(var3, var4, var2.closeX, var2.closeY, 18.0F, 18.0F);
      float f = this.closeAnimation.on23(flag ? 1.0F : 0.0F);
      var1.drawRoundedRect(var2.closeX, var2.closeY, 18.0F, 18.0F, CornerRadius.MovementInputEvent(5.0F), var6.Easing(var7, f));
      Font font1 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
      var1.drawText(font1, "×", var2.closeX + (18.0F - font1.width("×")) / 2.0F, var2.closeY + (18.0F - font1.height()) / 2.0F - 0.5F, var8);
      float f1 = this.searchAnimation.on23(this.searchBox.isSelected() ? 1.0F : 0.0F);
      ArgbColor i11ii1llliilllii1i1 = var5.getFieldSurfaceBackground()
         .getColor()
         .SprintStateEvent(var10)
         .Easing(var5.getPanelLeftBackground().getColor().SprintStateEvent(var10), f1 * 0.35F);
      var1.drawRoundedRect(var2.searchX, var2.searchY, var2.searchWidth, 20.0F, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i1);
      this.searchBox.setWidth(var2.searchWidth - 23.0F);
      this.searchBox.on23(var1, var2.searchX + 7.0F, var2.searchY + (20.0F - this.searchBox.call050().height()) / 2.0F, var8, var9);
      Font font2 = Fonts.NEW_ICONS.getFont(4.5F);
      var1.drawText(font2, "S", var2.searchX + var2.searchWidth - font2.width("S") - 7.0F, var2.searchY + (20.0F - font2.height()) / 2.0F, var9);
   }

   public void renderCards(
      HudDrawContext var1,
      EmoteFavoriteScreen_Layout var2,
      float var3,
      float var4,
      ArgbColor var5,
      ArgbColor var6,
      ArgbColor var7,
      ArgbColor var8,
      ArgbColor var9,
      ArgbColor var10,
      float var11
   ) {
      float f = this.contentHeight();
      this.clampScroll(f, var2.gridHeight);
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f1 = Math.clamp(this.contentAnimation.on23(1.0F), 0.0F, 1.0F);
      String s = this.owner.BotRespawnEvent(this.favoriteSlot);
      Font font = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font font1 = Fonts.NEW_REGULAR.getFont(4.3F);
      var1.enableScissor((int)var2.gridX, (int)var2.gridY, (int)(var2.gridX + var2.gridWidth), (int)(var2.gridY + var2.gridHeight));
      int i = 0;
      EmoteMetadata li1ll1i111l1l1iilli1111ily = null;

      for (int j = 0; j < this.filteredEmotes.size(); j++) {
         EmoteMetadata li1ll1i111l1l1iilli1111ilx = this.filteredEmotes.get(j);
         int k = j % 3;
         int l = j / 3;
         float f2 = var2.gridX + k * (var2.cardWidth + 5.0F);
         float f3 = var2.gridY + l * 77.0F + this.scroll;
         if (!(f3 + 72.0F < var2.gridY) && !(f3 > var2.gridY + var2.gridHeight)) {
            boolean flag = inside(var3, var4, var2.gridX, var2.gridY, var2.gridWidth, var2.gridHeight) && inside(var3, var4, f2, f3, var2.cardWidth, 72.0F);
            if (flag) {
               li1ll1i111l1l1iilli1111ily = li1ll1i111l1l1iilli1111ilx;
            }

            boolean flag1 = li1ll1i111l1l1iilli1111ilx.id().equals(s);
            float f4 = this.cardAnimations.get(li1ll1i111l1l1iilli1111ilx.id()).on23(flag ? 1.0F : 0.0F);
            float f5 = Math.clamp(f1 * 1.25F - i * 0.035F, 0.0F, 1.0F);
            float f6 = f3 + (1.0F - f5) * 8.0F;
            float f7 = var11 * f5;
            i++;
            ArgbColor i11ii1llliilllii1i1 = var5.SprintStateEvent(f5).Easing(var6.SprintStateEvent(f5), f4);
            if (flag1) {
               i11ii1llliilllii1i1 = i11ii1llliilllii1i1.Easing(var7.SprintStateEvent(f5), 0.75F);
            }

            var1.drawRoundedRect(f2, f6, var2.cardWidth, 72.0F, CornerRadius.MovementInputEvent(6.0F), i11ii1llliilllii1i1);
            float f8 = 39.0F + f4 * 3.0F;
            float f9 = f2 + (var2.cardWidth - f8) / 2.0F;
            float f10 = f6 + 5.0F - f4;
            var1.drawTexture(li1ll1i111l1l1iilli1111ilx.icon(), f9, f10, f8, f8, ArgbColor.var11934.SprintStateEvent(f7));
            String s1 = fit(font, li1ll1i111l1l1iilli1111ilx.displayName(), var2.cardWidth - 8.0F);
            var1.drawText(font, s1, f2 + (var2.cardWidth - font.width(s1)) / 2.0F, f6 + 72.0F - 17.0F, var8.SprintStateEvent(f5));
            String s2 = li1ll1i111l1l1iilli1111ilx.id().startsWith("spemotes/") ? "SPEmotes" : "Zenith";
            ArgbColor i11ii1llliilllii1i11 = flag1 ? var10.SprintStateEvent(f5) : var9.SprintStateEvent(f5);
            var1.drawText(font1, s2, f2 + (var2.cardWidth - font1.width(s2)) / 2.0F, f6 + 72.0F - 8.0F, i11ii1llliilllii1i11);
         }
      }

      if (li1ll1i111l1l1iilli1111ily != null) {
         this.updatePreviewEmote(li1ll1i111l1l1iilli1111ily);
      }

      if (this.filteredEmotes.isEmpty()) {
         Font font2 = Fonts.NEW_MEDIUM.getFont(5.5F);
         String s3 = "Ничего не найдено";
         var1.drawText(font2, s3, var2.gridX + (var2.gridWidth - font2.width(s3)) / 2.0F, var2.gridY + (var2.gridHeight - font2.height()) / 2.0F, var9);
      }

      var1.disableScissor();
   }

   public void renderScrollBar(HudDrawContext var1, EmoteFavoriteScreen_Layout var2, float var3, float var4, ZenithStyle var5, float var6) {
      EmoteFavoriteScreen_ScrollBarMetrics emotefavoritescreen_scrollbarmetrics = this.scrollBarMetrics(var2);
      if (emotefavoritescreen_scrollbarmetrics != null) {
         boolean flag = inside(var3, var4, var2.scrollBarX - 2.0F, var2.gridY, 7.0F, var2.gridHeight);
         float f = this.scrollBarAnimation.on23(!flag && !this.draggingScrollBar ? 0.0F : 1.0F);
         float f1 = 3.0F + f;
         float f2 = var2.scrollBarX - f / 2.0F;
         ArgbColor i11ii1llliilllii1i1 = var5.getFieldSurfaceBackground().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = var5.getTextTertiary()
            .getColor()
            .SprintStateEvent(var6)
            .Easing(var5.getPrimaryColor().getColor().SprintStateEvent(var6), f * 0.35F);
         var1.drawRoundedRect(f2, var2.gridY, f1, var2.gridHeight, CornerRadius.MovementInputEvent(f1 / 2.0F), i11ii1llliilllii1i1);
         var1.drawRoundedRect(
            f2,
            emotefavoritescreen_scrollbarmetrics.thumbY(),
            f1,
            emotefavoritescreen_scrollbarmetrics.thumbHeight(),
            CornerRadius.MovementInputEvent(f1 / 2.0F),
            i11ii1llliilllii1i11
         );
      }
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.closing) {
         return true;
      }

      EmoteFavoriteScreen_Layout emotefavoritescreen_layout = this.layout();
      mouseX = this.transformX(mouseX, emotefavoritescreen_layout);
      mouseY = this.transformY(mouseY, emotefavoritescreen_layout);
      if (inside(mouseX, mouseY, emotefavoritescreen_layout.closeX, emotefavoritescreen_layout.closeY, 18.0F, 18.0F)) {
         this.close();
         return true;
      }

      if (this.searchBox.onMouseClicked(mouseX, mouseY, MenuScreenId.Event37(button))) {
         return true;
      }

      if (this.playerPreview.onMouseClicked(mouseX, mouseY, button)) {
         return true;
      }

      EmoteFavoriteScreen_ScrollBarMetrics emotefavoritescreen_scrollbarmetrics = this.scrollBarMetrics(emotefavoritescreen_layout);
      if (button == 0
         && emotefavoritescreen_scrollbarmetrics != null
         && inside(mouseX, mouseY, emotefavoritescreen_layout.scrollBarX - 2.0F, emotefavoritescreen_layout.gridY, 7.0F, emotefavoritescreen_layout.gridHeight)
         )
       {
         this.draggingScrollBar = true;
         if (inside(
            mouseX,
            mouseY,
            emotefavoritescreen_layout.scrollBarX - 2.0F,
            emotefavoritescreen_scrollbarmetrics.thumbY(),
            7.0F,
            emotefavoritescreen_scrollbarmetrics.thumbHeight()
         )) {
            this.scrollBarDragOffset = (float)mouseY - emotefavoritescreen_scrollbarmetrics.thumbY();
         } else {
            this.scrollBarDragOffset = emotefavoritescreen_scrollbarmetrics.thumbHeight() / 2.0F;
            this.updateDraggedScrollBar(mouseY, emotefavoritescreen_layout, emotefavoritescreen_scrollbarmetrics);
         }

         return true;
      } else if (button == 0
         && inside(
            mouseX,
            mouseY,
            emotefavoritescreen_layout.gridX,
            emotefavoritescreen_layout.gridY,
            emotefavoritescreen_layout.gridWidth,
            emotefavoritescreen_layout.gridHeight
         )) {
         int i = this.cardAt(mouseX, mouseY, emotefavoritescreen_layout);
         if (i >= 0 && i < this.filteredEmotes.size()) {
            this.owner.UiAnimation(this.favoriteSlot, this.filteredEmotes.get(i).id());
            this.close();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseDragged(Click click, double deltaX, double deltaY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), deltaX, deltaY);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      EmoteFavoriteScreen_Layout emotefavoritescreen_layout = this.layout();
      double d0 = this.transformX(mouseX, emotefavoritescreen_layout);
      double d1 = this.transformY(mouseY, emotefavoritescreen_layout);
      if (this.playerPreview.onMouseDragged(d0, d1, button, deltaX, deltaY)) {
         return true;
      }

      if (this.draggingScrollBar && button == 0) {
         mouseY = d1;
         EmoteFavoriteScreen_ScrollBarMetrics emotefavoritescreen_scrollbarmetrics = this.scrollBarMetrics(emotefavoritescreen_layout);
         if (emotefavoritescreen_scrollbarmetrics != null) {
            this.updateDraggedScrollBar(d1, emotefavoritescreen_layout, emotefavoritescreen_scrollbarmetrics);
            return true;
         }
      }

      return super.mouseDragged(new Click(mouseX, mouseY, new MouseInput(button, 0)), deltaX, deltaY);
   }

   @Override
   public boolean mouseReleased(Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.playerPreview.onMouseReleased(mouseX, mouseY, button)) {
         return true;
      } else if (button == 0 && this.draggingScrollBar) {
         this.draggingScrollBar = false;
         return true;
      } else {
         return super.mouseReleased(new Click(mouseX, mouseY, new MouseInput(button, 0)));
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && verticalAmount != 0.0) {
         EmoteFavoriteScreen_Layout emotefavoritescreen_layout = this.layout();
         mouseX = this.transformX(mouseX, emotefavoritescreen_layout);
         mouseY = this.transformY(mouseY, emotefavoritescreen_layout);
         if (this.playerPreview.onMouseScrolled(mouseX, mouseY, verticalAmount)) {
            return true;
         }

         if (!inside(
            mouseX,
            mouseY,
            emotefavoritescreen_layout.gridX,
            emotefavoritescreen_layout.gridY,
            emotefavoritescreen_layout.gridWidth,
            emotefavoritescreen_layout.gridHeight
         )) {
            return false;
         }

         float f = this.contentHeight();
         if (f <= emotefavoritescreen_layout.gridHeight) {
            return false;
         }

         this.scrollTarget += (float)verticalAmount * 38.0F;
         this.clampScroll(f, emotefavoritescreen_layout.gridHeight);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.closing) {
         return true;
      } else if (keyCode == 256) {
         this.close();
         return true;
      } else {
         return this.searchBox.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(new KeyInput(keyCode, scanCode, modifiers));
      }
   }

   @Override
   public boolean charTyped(CharInput input) {
      String value = input.asString();
      return !value.isEmpty() && this.charTyped(value.charAt(0), input.modifiers());
   }

   public boolean charTyped(char chr, int modifiers) {
      return this.closing ? true : this.searchBox.charTyped(chr, modifiers) || super.charTyped(new CharInput(chr, modifiers));
   }

   public void close() {
      if (!this.closing) {
         this.closing = true;
         this.draggingScrollBar = false;
         this.searchBox.VelocityChangeEvent(false);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   public void removed() {
      this.detachPreviewAnimation();
      super.removed();
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public void updateFilter() {
      String s = this.searchBox.getText().trim().toLowerCase(Locale.ROOT);
      if (!s.equals(this.previousSearch)) {
         this.previousSearch = s;
         this.filteredEmotes = s.isEmpty()
            ? this.allEmotes
            : this.allEmotes
               .stream()
               .filter(
                  var1x -> var1x.displayName().toLowerCase(Locale.ROOT).contains(s)
                     || var1x.id().toLowerCase(Locale.ROOT).contains(s)
                     || var1x.author().toLowerCase(Locale.ROOT).contains(s)
               )
               .toList();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.draggingScrollBar = false;
         this.restartContentAnimation();
      }
   }

   public int cardAt(double var1, double var3, EmoteFavoriteScreen_Layout var5) {
      for (int i = 0; i < this.filteredEmotes.size(); i++) {
         int j = i % 3;
         int k = i / 3;
         float f = var5.gridX + j * (var5.cardWidth + 5.0F);
         float f1 = var5.gridY + k * 77.0F + this.scroll;
         if (inside(var1, var3, f, f1, var5.cardWidth, 72.0F)) {
            return i;
         }
      }

      return -1;
   }

   public float contentHeight() {
      if (this.filteredEmotes.isEmpty()) {
         return 0.0F;
      }

      int i = (this.filteredEmotes.size() + 3 - 1) / 3;
      return i * 72.0F + Math.max(0, i - 1) * 5.0F;
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
      } else {
         float f = var2 - var1;
         this.scrollTarget = Math.clamp(this.scrollTarget, f, 0.0F);
         this.scroll = Math.clamp(this.scroll, f, 0.0F);
      }
   }

   public EmoteFavoriteScreen_ScrollBarMetrics scrollBarMetrics(EmoteFavoriteScreen_Layout var1) {
      float f = this.contentHeight();
      if (f <= var1.gridHeight) {
         return null;
      }

      float f1 = var1.gridHeight - f;
      float f2 = Math.max(24.0F, var1.gridHeight * var1.gridHeight / f);
      f2 = Math.min(var1.gridHeight, f2);
      float f3 = var1.gridHeight - f2;
      float f4 = Math.clamp(this.scroll / f1, 0.0F, 1.0F);
      return new EmoteFavoriteScreen_ScrollBarMetrics(var1.gridY + f3 * f4, f2, f3, f1);
   }

   public void updateDraggedScrollBar(double var1, EmoteFavoriteScreen_Layout var3, EmoteFavoriteScreen_ScrollBarMetrics var4) {
      float f = Math.clamp((float)var1 - this.scrollBarDragOffset, var3.gridY, var3.gridY + var4.travel());
      float f1 = var4.travel() <= 0.0F ? 0.0F : (f - var3.gridY) / var4.travel();
      this.scroll = var4.minimum() * f1;
      this.scrollTarget = this.scroll;
   }

   public void restartContentAnimation() {
      this.contentAnimation.setValue(0.0F);
      this.contentAnimation.on23(1.0F);
   }

   public void updatePreviewEmote(EmoteMetadata var1) {
      String s = var1 == null ? null : var1.id();
      if (!Objects.equals(this.previewEmoteId, s) || this.previewEmotePlayer != null && !this.previewEmotePlayer.isActive()) {
         if (this.previewEmotePlayer != null) {
            this.previewEmotePlayer.stop();
         }

         this.previewEmoteId = s;
         this.previewEmotePlayer = var1 == null || this.client == null || this.client.player == null
            ? null
            : new EmotePlayback(this.client.player, var1, 0);
         if (var1 == null) {
            this.detachPreviewAnimationLayer();
         }
      }
   }

   public void attachPreviewAnimation(ClientPlayerEntity var1) {
      if (this.previewAnimationEntity != var1) {
         this.detachPreviewAnimationLayer();
         PlayerAnimationAccess.getPlayerAnimManager(var1).addAnimLayer(PREVIEW_ANIMATION_PRIORITY, this.previewAnimation);
         this.previewAnimationEntity = var1;
      }
   }

   public void detachPreviewAnimationLayer() {
      this.previewAnimation.setAnim(null);
      if (this.previewAnimationEntity != null) {
         PlayerAnimationAccess.getPlayerAnimManager(this.previewAnimationEntity).removeLayer(this.previewAnimation);
         this.previewAnimationEntity = null;
      }
   }

   public void detachPreviewAnimation() {
      this.detachPreviewAnimationLayer();
      if (this.previewEmotePlayer != null) {
         this.previewEmotePlayer.stop();
      }

      this.previewEmotePlayer = null;
      this.previewEmoteId = null;
   }

   public void finishClose() {
      if (this.client != null && this.client.currentScreen == this) {
         this.client.setScreen(null);
      }
   }

   public EmoteFavoriteScreen_Layout layout() {
      float f = Math.min(this.width - 30.0F, 520.0F);
      float f1 = Math.min(this.height - 24.0F, 360.0F);
      float f2 = (this.width - f) / 2.0F;
      float f3 = (this.height - f1) / 2.0F;
      float f4 = f2 + 14.0F;
      float f5 = f - 28.0F;
      float f6 = Math.min(190.0F, f5 * 0.4F);
      float f7 = f2 + f - 14.0F - f6;
      float f8 = f3 + 14.5F;
      float f9 = f3 + 51.0F;
      float f10 = f3 + f1 - 14.0F - f9;
      float f11 = f5 * 0.39F;
      float f12 = f5 - f11 - 12.0F;
      float f13 = f4 + f12 + 3.0F;
      float f14 = f4 + f12 + 12.0F;
      float f15 = (f12 - 10.0F) / 3.0F;
      return new EmoteFavoriteScreen_Layout(f2, f3, f, f1, f4, f7, f8, f6, f4, f9, f12, f10, f15, f13, f14, f11, f7 - 24.0F, f3 + 15.5F);
   }

   public float transformX(double var1, EmoteFavoriteScreen_Layout var3) {
      return (float)((var1 - var3.centerX()) / this.interactiveScale + var3.centerX());
   }

   public float transformY(double var1, EmoteFavoriteScreen_Layout var3) {
      return (float)((var1 - var3.centerY()) / this.interactiveScale + var3.centerY());
   }

   public static String fit(Font var0, String var1, float var2) {
      if (var0.width(var1) <= var2) {
         return var1;
      }

      String s = "…";
      int i = var1.length();

      while (i > 0 && var0.width(var1.substring(0, i) + s) > var2) {
         i--;
      }

      return var1.substring(0, i) + s;
   }

   public static boolean inside(double var0, double var2, float var4, float var5, float var6, float var7) {
      return var0 >= var4 && var2 >= var5 && var0 <= var4 + var6 && var2 <= var5 + var7;
   }
}
