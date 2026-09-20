package org.zenith.client.screens.nlgui.elements;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.CloudConfigDetailsDto;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.PollMode;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiCloudConfigElement extends InterfaceElement {
   public static final float WIDTH = 120.0F;
   public static final float CARD_PADDING = 8.0F;
   public static final float PREVIEW_WIDTH = 104.0F;
   public static final float PREVIEW_HEIGHT = 58.5F;
   public static final float TITLE_ROW_HEIGHT = 7.0F;
   public static final float AUTHOR_ROW_HEIGHT = 7.0F;
   public static final float HEADER_HEIGHT = 100.5F;
   public static final float BODY_HEIGHT = 71.0F;
   public static final float HEIGHT = 171.5F;
   public static final float DESCRIPTION_LINE_HEIGHT = 7.0F;
   public static final int MAX_DESCRIPTION_LINES = 2;
   public static final float INFO_ROW_HEIGHT = 7.0F;
   public static final float INFO_ROW_GAP = 6.0F;
   public static final DateTimeFormatter CREATED_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");
   public final CloudConfigDetailsDto entry;
   public final BiConsumer<GuiCloudConfigElement, PollMode> loadAction;
   public final Consumer<GuiCloudConfigElement> likeAction;
   public final UiAnimation hoverAnimation = new UiAnimation(180L, Easing.CloseScreenEvent);
   public final UiAnimation heartAnimation = new UiAnimation(180L, Easing.CloseScreenEvent);
   public final UiAnimation deleteAnimation = new UiAnimation(180L, Easing.CloseScreenEvent);
   public final UiAnimation actionAnimation = new UiAnimation(180L, Easing.CloseScreenEvent);
   public final UiAnimation expandedAnimation = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final UiAnimation loadButtonAnimation = new UiAnimation(220L, Easing.CloseScreenEvent);
   public final UiAnimation previewFadeAnimation = new UiAnimation(180L, Easing.StopUsingItemEvent);
   public final UiAnimation layoutXAnimation = new UiAnimation(280L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation layoutYAnimation = new UiAnimation(280L, 0.0F, Easing.StopUsingItemEvent);
   public final ModeSetting loadMode = new ModeSetting(
      "gui.configelement.mode", "", "gui.configelement.mode.all", "gui.configelement.mode.ignoreBinds", "gui.configelement.mode.onlyThemes"
   );
   public final GuiModeSetting guiLoadMode = new GuiModeSetting(this.loadMode, 120.0F - GuiStyle.PADDING.intValue() * 4.0F);
   public CornerRadiusF bounds;
   public CornerRadiusF heartBounds;
   public CornerRadiusF deleteBounds;
   public CornerRadiusF actionBounds;
   public CornerRadiusF popupBounds;
   public CornerRadiusF popupExitBounds;
   public CornerRadiusF loadButtonBounds;
   public boolean downloading;
   public boolean liking;
   public boolean deleting;
   public String actionLabel;
   public boolean liked;
   public boolean expanded;
   public long likeCount;
   public Consumer<GuiCloudConfigElement> editAction;
   public Consumer<GuiCloudConfigElement> deleteAction;
   public boolean layoutInitialized;

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = this.expandedAnimation.on23(this.expanded ? 1.0F : 0.0F);
      if (!(f <= 0.001F) && this.bounds != null) {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            float f1 = 120.0F;
            float f2 = 26.0F;
            float f3 = GuiStyle.PADDING.intValue() * 5.0F + 28.0F;
            float f4 = f2 + f3;
            float f5 = this.bounds.x();
            float f6 = this.actionBounds == null
               ? this.bounds.y() + this.bounds.height() + GuiStyle.PADDING.intValue()
               : this.actionBounds.y() + this.actionBounds.height() + GuiStyle.PADDING.intValue();
            this.popupBounds = new CornerRadiusF(f5, f6, f1, f4);
            var6 *= f;
            var1.pushMatrix();
            var1.getMatrices().translate(f5 + f1 / 2.0F, f6);
            var1.getMatrices().scale(f, f);
            var1.getMatrices().translate(-(f5 + f1 / 2.0F), -f6);
            ShapeRenderer.ItemSpec(
               var1.getMatrices(),
               f5,
               f6,
               f1,
               f4,
               12.0F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f5,
               f6,
               f1,
               f2,
               CornerRadius.BotPacketEvent(GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue()),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f5,
               f6 + f2,
               f1,
               f3,
               CornerRadius.BotRespawnEvent(GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue()),
               zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6)
            );
            Font font = Fonts.NEW_ICONS.getFont(5.0F);
            Font font1 = Fonts.NEW_MEDIUM.getFont(5.4F);
            float f7 = f5 + GuiStyle.PADDING.intValue() * 2.0F;
            var1.drawText(font, "n", f7, f6 + (f2 - font.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6));
            var1.drawText(
               font1,
               this.translate("gui.configelement.loadType"),
               f7 + font.width("n") + GuiStyle.PADDING.intValue() / 2.0F,
               f6 + (f2 - font1.height()) / 2.0F,
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var6)
            );
            Font font2 = Fonts.NEW_ICONS.getFont(4.0F);
            float f8 = f5 + f1 - font2.width("2") - GuiStyle.PADDING.intValue() * 2.0F;
            float f9 = f6 + GuiStyle.PADDING.intValue() + font2.height();
            this.popupExitBounds = new CornerRadiusF(f8, f9, 5.0F, 5.0F);
            var1.drawText(font2, "2", f8, f9, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
            float f10 = f5 + GuiStyle.PADDING.intValue() * 2.0F;
            float f11 = f6 + f2 + GuiStyle.PADDING.intValue();
            this.guiLoadMode.render(var1, var2, var3, f10, f11, var6);
            float f12 = f11 + this.guiLoadMode.getAnimHeight() + GuiStyle.PADDING.intValue() * 2.0F;
            this.loadButtonBounds = new CornerRadiusF(f10, f12, this.guiLoadMode.getWidth(), 14.0F);
            float f13 = this.loadButtonAnimation
               .on23(this.loadButtonBounds.PotionItemBuilder(var2, var3) && !this.guiLoadMode.contains(var2, var3) ? 1.0F : 0.0F);
            var1.drawRoundedRect(
               this.loadButtonBounds.x(),
               this.loadButtonBounds.y(),
               this.loadButtonBounds.width(),
               this.loadButtonBounds.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldSurfaceBackground().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f13 * 0.15F).SprintStateEvent(var6)
            );
            var1.drawRoundedBorder(
               this.loadButtonBounds.x(),
               this.loadButtonBounds.y(),
               this.loadButtonBounds.width(),
               this.loadButtonBounds.height(),
               0.1F,
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
               zenithstyle.getFieldBorder().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f13 * 0.35F).SprintStateEvent(var6)
            );
            Font font3 = Fonts.NEW_ICONS.getFont(5.0F);
            Font font4 = Fonts.NEW_MEDIUM.getFont(5.3F);
            String s = this.translate("gui.configelemen.load");
            float f14 = font3.width("a") + GuiStyle.PADDING.intValue() / 2.0F + font4.width(s);
            float f15 = this.loadButtonBounds.x() + (this.loadButtonBounds.width() - f14) / 2.0F;
            var1.drawText(
               font3,
               "a",
               f15,
               this.loadButtonBounds.y() + (this.loadButtonBounds.height() - font3.height()) / 2.0F + 0.28F,
               zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f13).SprintStateEvent(var6)
            );
            var1.drawText(
               font4,
               s,
               f15 + font3.width("a") + GuiStyle.PADDING.intValue() / 2.0F,
               this.loadButtonBounds.y() + (this.loadButtonBounds.height() - font4.height()) / 2.0F,
               zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f13).SprintStateEvent(var6)
            );
            this.guiLoadMode.renderPriority(var1, var2, var3, f10, f11, var6, 1.0F);
            var1.popMatrix();
         }
      }
   }

   public void renderCardHeader(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var1.drawRoundedRect(
         var2,
         var3,
         120.0F,
         100.5F,
         CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
         var8.getHeaderDisableBackground().getColor().SprintStateEvent(var4)
      );
      float f = var2 + 8.0F;
      float f1 = var3 + 8.0F;
      CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 4.0F);
      Identifier identifier = CloudPreviewTextureCache.get(this.entry.HudHotbarPanel().HudClockPanel());
      float f2 = this.previewFadeAnimation.on23(identifier == null ? 0.0F : 1.0F);
      if (identifier == null || f2 < 0.999F) {
         float f3 = identifier == null ? 1.0F : 1.0F - f2;
         var1.drawRoundedRect(f, f1, 104.0F, 58.5F, ii1il11l111ii11iil, var8.getFieldSurfaceBackground().getColor().SprintStateEvent(var4 * f3));
         Font font = Fonts.NEW_ICONS.getFont(8.0F);
         var1.drawText(
            font,
            "9",
            f + (104.0F - font.width("9")) / 2.0F,
            f1 + (58.5F - font.height()) / 2.0F,
            var8.getTextTertiary().getColor().SprintStateEvent(var4 * f3)
         );
      }

      if (identifier != null) {
         var1.drawRoundedTexture(identifier, f, f1, 104.0F, 58.5F, ii1il11l111ii11iil, ArgbColor.var11934.SprintStateEvent(var4 * f2));
      }

      float f4 = f1 + 58.5F + 8.0F;
      this.renderHeaderLine(var1, var2, f4, var4, var5, var6, var8);
      this.renderAuthor(var1, var2 + 8.0F, f4 + 7.0F + 4.0F, var4, var7, var8);
   }

   public void renderCardBody(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_REGULAR.getFont(5.5F);
      float f = var2 + 8.0F;
      float f1 = var3 + 8.0F;
      String s = this.entry.HudHotbarPanel().description();
      boolean flag = s != null && !s.isBlank();
      List<String> list = wrap(font, flag ? s : "No description provided.");
      ArgbColor i11ii1llliilllii1i1 = flag ? var5.getTextSecondary().getColor() : var5.getTextTertiary().getColor();

      for (int i = 0; i < list.size(); i++) {
         var1.drawText(font, list.get(i), f, f1 + i * 7.0F, i11ii1llliilllii1i1.SprintStateEvent(var4));
      }

      float f2 = f1 + 14.0F + 8.0F;
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.5F);
      this.renderInfoRow(var1, f, f2, "Servers", this.serverLabel(), var4, var5, font1, font2);
      f2 += 13.0F;
      this.renderInfoRow(var1, f, f2, "Updated", relative(this.entry.HudHotbarPanel().HudTargetPanel()), var4, var5, font1, font2);
      f2 += 13.0F;
      this.renderInfoRow(var1, f, f2, "Date Created", this.createdDate(), var4, var5, font1, font2);
   }

   public GuiCloudConfigElement(CloudConfigDetailsDto var1, BiConsumer<GuiCloudConfigElement, PollMode> var2, Consumer<GuiCloudConfigElement> var3) {
      this.entry = Objects.requireNonNull(var1, "entry");
      this.loadAction = Objects.requireNonNull(var2, "loadAction");
      this.likeAction = Objects.requireNonNull(var3, "likeAction");
      this.liked = var1.HudArmorPanel();
      this.likeCount = var1.HudSelectedItemPanel();
      this.heartAnimation.setValue(this.liked ? 1.0F : 0.0F);
   }

   public CloudConfigDetailsDto getEntry() {
      return this.entry;
   }

   public long getLikeCount() {
      return this.likeCount;
   }

   public void prefetchPreview() {
      CloudPreviewTextureCache.prefetch(this.entry.HudHotbarPanel().HudClockPanel());
   }

   public void updateLayoutTarget(float var1, float var2) {
      if (!this.layoutInitialized) {
         this.layoutXAnimation.setValue(var1);
         this.layoutYAnimation.setValue(var2);
         this.layoutInitialized = true;
      } else {
         this.layoutXAnimation.on23(var1);
         this.layoutYAnimation.on23(var2);
      }
   }

   public float getAnimatedLayoutX() {
      return this.layoutXAnimation.CancellableEvent();
   }

   public float getAnimatedLayoutY() {
      return this.layoutYAnimation.CancellableEvent();
   }

   public void setDownloading(boolean var1) {
      this.downloading = var1;
      if (var1) {
         this.actionLabel = null;
      }
   }

   public void setLiking(boolean var1) {
      this.liking = var1;
   }

   public void setDeleting(boolean var1) {
      this.deleting = var1;
   }

   public void applyLikeResult(boolean var1, long var2) {
      this.liked = var1;
      this.likeCount = Math.max(0L, var2);
      this.liking = false;
   }

   public void setActionLabel(String var1) {
      this.actionLabel = var1;
   }

   public void setEditAction(Consumer<GuiCloudConfigElement> var1) {
      this.editAction = var1;
   }

   public void setDeleteAction(Consumer<GuiCloudConfigElement> var1) {
      this.deleteAction = var1;
   }

   @Override
   public String getName() {
      return this.entry.HudHotbarPanel().name();
   }

   @Override
   public float getHeight() {
      return 171.5F;
   }

   @Override
   public float getWidth() {
      return 120.0F;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.bounds = new CornerRadiusF(var4, var5, 120.0F, 171.5F);
         float f = this.hoverAnimation.on23(this.bounds.PotionItemBuilder(var2, var3) ? 1.0F : 0.0F);
         float f1 = this.heartAnimation.on23(this.liked ? 1.0F : 0.0F);
         float f2 = this.deleteAnimation.on23(this.deleteBounds != null && this.deleteBounds.PotionItemBuilder(var2, var3) ? 1.0F : 0.0F);
         float f3 = this.actionAnimation
            .on23(
               !this.isLoaded() && !this.downloading && !this.expanded
                  ? (this.actionBounds != null && this.actionBounds.PotionItemBuilder(var2, var3) ? 1.0F : 0.0F)
                  : 1.0F
            );
         var1.drawRoundedRect(
            var4,
            var5,
            120.0F,
            171.5F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().Easing(zenithstyle.getSurfaceEnableBackground().getColor(), f * 0.08F).SprintStateEvent(var6)
         );
         this.renderCardHeader(var1, var4, var5, var6, f1, f2, f3, zenithstyle);
         this.renderCardBody(var1, var4, var5 + 100.5F, var6, zenithstyle);
      }
   }

   public void renderHeaderLine(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(4.5F);
      String s = String.valueOf(this.likeCount);
      float f = 9.5F + font1.width(s);
      float f1 = var2 + 120.0F - 8.0F;
      if (this.deleteAction != null) {
         this.deleteBounds = new CornerRadiusF(f1 - 7.0F, var3, 7.0F, 7.0F);
         f1 = this.deleteBounds.x() - GuiStyle.PADDING.intValue();
      } else {
         this.deleteBounds = null;
      }

      this.heartBounds = new CornerRadiusF(f1 - f, var3, f, 7.0F);
      float f2 = var2 + 8.0F;
      float f3 = Math.max(12.0F, this.heartBounds.x() - 4.0F - f2);
      var1.drawText(
         font, trimToWidth(font, this.getName(), f3), f2, var3 + (7.0F - font.height()) / 2.0F, var7.getTextEnable().getColor().SprintStateEvent(var4)
      );
      this.renderFavorite(var1, var4, var5, var7, font1, s);
      if (this.deleteBounds != null) {
         this.renderDelete(var1, var4, var6, var7);
      }
   }

   public void renderAuthor(HudDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font font2 = Fonts.NEW_ICONS.getFont(4.8F);
      float f = 7.0F;
      ShapeRenderer.on23(
         var1.getMatrices(),
         ZenithClient.on23("icons/avatar.png"),
         var2,
         var3,
         f,
         f,
         CornerRadius.MovementInputEvent(2.0F),
         ArgbColor.var11934.SprintStateEvent(var4)
      );
      String s = this.downloading
         ? "Loading..."
         : (this.actionLabel != null && !this.actionLabel.isBlank() ? this.actionLabel : this.translate("gui.configelemen.load"));
      float f1 = font2.width("n") + GuiStyle.PADDING.intValue() / 2.0F + font1.width(s);
      float f2 = var2 + 104.0F - f1;
      this.actionBounds = new CornerRadiusF(f2 - 1.0F, var3 - 1.0F, f1 + 2.0F, 9.0F);
      String s1 = this.entry.HudElementValue() == null ? "Unknown" : this.entry.HudElementValue().HudInventoryPanel();
      float f3 = var2 + f + 2.0F;
      float f4 = Math.max(8.0F, f2 - GuiStyle.PADDING.intValue() - f3);
      var1.drawText(font, trimToWidth(font, s1, f4), f3, var3 + (7.0F - font.height()) / 2.0F, var6.getTextSecondary().getColor().SprintStateEvent(var4));
      var1.drawText(
         font2,
         "n",
         f2,
         var3 + (7.0F - font2.height()) / 2.0F + 0.4F,
         var6.getTextTertiary().getColor().Easing(var6.getPrimaryColor().getColor(), var5).SprintStateEvent(var4)
      );
      var1.drawText(
         font1,
         s,
         f2 + font2.width("n") + GuiStyle.PADDING.intValue() / 2.0F,
         var3 + (7.0F - font1.height()) / 2.0F,
         var6.getTextSecondary().getColor().Easing(var6.getTextEnable().getColor(), var5).SprintStateEvent(var4)
      );
   }

   public void renderInfoRow(HudDrawContext var1, float var2, float var3, String var4, String var5, float var6, ZenithStyle var7, Font var8, Font var9) {
      var1.drawText(var8, var4, var2, var3 + (7.0F - var8.height()) / 2.0F, var7.getTextEnable().getColor().SprintStateEvent(var6));
      float f = 104.0F - var8.width(var4) - 8.0F;
      String s = trimToWidth(var9, var5, f - 4.0F);
      float f1 = var9.width(s) + 4.0F;
      float f2 = var2 + 104.0F - f1;
      var1.drawRoundedRect(f2, var3, f1, 7.0F, CornerRadius.MovementInputEvent(2.0F), var7.getDisableActiveBg().getColor().SprintStateEvent(var6));
      var1.drawText(var9, s, f2 + 2.0F, var3 + (7.0F - var9.height()) / 2.0F, var7.getTextTertiary().getColor().SprintStateEvent(var6));
   }

   public void renderFavorite(HudDrawContext var1, float var2, float var3, ZenithStyle var4, Font var5, String var6) {
      var1.drawRoundedRect(
         this.heartBounds.x(),
         this.heartBounds.y(),
         this.heartBounds.width(),
         this.heartBounds.height(),
         CornerRadius.MovementInputEvent(2.0F),
         var4.getDisableActiveBg().getColor().Easing(var4.getHeartActiveBg().getColor(), var3).SprintStateEvent(var2)
      );
      var1.drawText(
         Fonts.NEW_ICONS.getFont(4.5F),
         "U",
         this.heartBounds.x() + 2.0F,
         this.heartBounds.y() + 1.2F,
         ArgbColor.var11941.Easing(var4.getTextTertiary().getColor(), 1.0F - var3).SprintStateEvent(var2)
      );
      var1.drawText(
         Fonts.NEW_ICONS.getFont(4.0F),
         "V",
         this.heartBounds.x() + 2.05F,
         this.heartBounds.y() + 1.35F,
         ArgbColor.var11941.Easing(var4.getHeartIcon().getColor(), var3).SprintStateEvent(var2)
      );
      var1.drawText(
         var5,
         var6,
         this.heartBounds.x() + 7.5F,
         this.heartBounds.y() + (this.heartBounds.height() - var5.height()) / 2.0F,
         var4.getTextTertiary().getColor().Easing(var4.getHeartIcon().getColor(), var3).SprintStateEvent(var2)
      );
   }

   public void renderDelete(HudDrawContext var1, float var2, float var3, ZenithStyle var4) {
      var1.drawRoundedRect(
         this.deleteBounds.x(),
         this.deleteBounds.y(),
         this.deleteBounds.width(),
         this.deleteBounds.height(),
         CornerRadius.MovementInputEvent(1.5F),
         var4.getDisableActiveBg().getColor().SprintStateEvent(var2)
      );
      Font font = Fonts.NEW_ICONS.getFont(4.3F);
      var1.drawText(
         font,
         "[",
         this.deleteBounds.x() + (this.deleteBounds.width() - font.width("[")) / 2.0F,
         this.deleteBounds.y() + 1.5F,
         var4.getTextTertiary().getColor().Easing(var4.getPrimaryColor().getColor(), var3).SprintStateEvent(var2)
      );
   }

   public String serverLabel() {
      String s = this.entry.HudHotbarPanel().RotationLegitStrategy();
      return s != null && !s.isBlank() && !"unknown".equalsIgnoreCase(s) ? s : "Universal / All";
   }

   public String createdDate() {
      return CREATED_DATE_FORMAT.format(Instant.ofEpochMilli(this.entry.HudHotbarPanel().RenderTickEvent()).atZone(ZoneId.systemDefault()));
   }

   public boolean isLoaded() {
      return "Loaded".equalsIgnoreCase(this.actionLabel);
   }

   public static String relative(long var0) {
      long i = Math.max(0L, (System.currentTimeMillis() - var0) / 1000L);
      if (i < 60L) {
         return "Just now";
      } else if (i < 3600L) {
         return ago(Math.max(1L, i / 60L), "minute");
      } else if (i < 86400L) {
         return ago(i / 3600L, "hour");
      } else {
         return i < 2592000L ? ago(i / 86400L, "day") : ago(Math.max(1L, i / 2592000L), "month");
      }
   }

   public static String ago(long var0, String var2) {
      return var0 + " " + var2 + (var0 == 1L ? "" : "s") + " ago";
   }

   public static List<String> wrap(Font var0, String var1) {
      List<String> arraylist = new ArrayList<>();
      if (var1 != null && !var1.isBlank()) {
         StringBuilder stringbuilder = new StringBuilder();

         for (String s : var1.trim().split("\\s+")) {
            String s1 = stringbuilder.isEmpty() ? s : stringbuilder + " " + s;
            if (var0.width(s1) <= 104.0F) {
               stringbuilder.setLength(0);
               stringbuilder.append(s1);
            } else {
               if (!stringbuilder.isEmpty()) {
                  arraylist.add(stringbuilder.toString());
                  stringbuilder.setLength(0);
               }

               if (arraylist.size() == 2) {
                  break;
               }

               stringbuilder.append(s);
            }
         }

         if (!stringbuilder.isEmpty() && arraylist.size() < 2) {
            arraylist.add(stringbuilder.toString());
         }

         if (arraylist.size() == 2) {
            arraylist.set(1, trimToWidth(var0, arraylist.get(1), 104.0F));
         }

         return arraylist;
      } else {
         return arraylist;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds == null || !this.bounds.PotionItemBuilder(var1, var3)) {
         return false;
      }

      if (var5 == MenuScreenId.call111) {
         if (this.editAction == null) {
            return false;
         }

         this.editAction.accept(this);
         return true;
      } else {
         if (var5 != MenuScreenId.call004) {
            return false;
         }

         if (this.heartBounds != null && this.heartBounds.PotionItemBuilder(var1, var3)) {
            if (this.liking) {
               return true;
            }

            this.liking = true;
            this.likeAction.accept(this);
            return true;
         } else if (this.deleteBounds != null && this.deleteBounds.PotionItemBuilder(var1, var3)) {
            if (this.deleting) {
               return true;
            }

            this.deleting = true;
            this.deleteAction.accept(this);
            return true;
         } else {
            if (this.actionBounds == null || !this.actionBounds.PotionItemBuilder(var1, var3)) {
               return false;
            }

            if (this.downloading) {
               return true;
            }

            this.expanded = !this.expanded;
            return true;
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.expanded || this.popupBounds == null) {
         return false;
      } else if (this.guiLoadMode.onMousePriorityClicked(var1, var3, var5)) {
         return true;
      } else if (!this.popupBounds.PotionItemBuilder(var1, var3)) {
         this.expanded = false;
         return false;
      } else if (this.popupExitBounds != null && this.popupExitBounds.on23(var1, var3, 2.0F)) {
         this.expanded = false;
         return true;
      } else if (this.guiLoadMode.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (var5 == MenuScreenId.call004 && this.loadButtonBounds != null && this.loadButtonBounds.PotionItemBuilder(var1, var3)) {
         this.loadByMode();
         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return this.expanded && this.guiLoadMode.onMousePriorityScroll(var1, var3, var5, var7);
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      this.guiLoadMode.onMouseReleased(var1, var3, var5);
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.guiLoadMode.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.guiLoadMode.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public void loadByMode() {
      String s = this.loadMode.get();

      PollMode liill1llill11i11il_ii1il11l111ii11iil = switch (s) {
         case "gui.configelement.mode.ignoreBinds" -> PollMode.call137;
         case "gui.configelement.mode.onlyThemes" -> PollMode.getThis3;
         default -> PollMode.call107;
      };
      this.downloading = true;
      this.actionLabel = null;
      this.expanded = false;
      this.loadAction.accept(this, liill1llill11i11il_ii1il11l111ii11iil);
   }

   public String translate(String var1) {
      return ZenithClient.on23().Easing().translate(var1);
   }

   public static String trimToWidth(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty() && !(var0.width(var1) <= var2)) {
         String s = "...";
         int i = var1.length();

         while (i > 0 && var0.width(var1.substring(0, i)) + var0.width(s) > var2) {
            i--;
         }

         return i == 0 ? s : var1.substring(0, i) + s;
      } else {
         return var1 == null ? "" : var1;
      }
   }
}
