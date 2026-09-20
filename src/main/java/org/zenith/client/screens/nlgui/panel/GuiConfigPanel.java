package org.zenith.client.screens.nlgui.panel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.cloud.CloudPreviewTextureCache;
import org.zenith.client.screens.nlgui.cloud.ConfigPreviewImage;
import org.zenith.client.screens.nlgui.elements.GuiCloudConfigElement;
import org.zenith.client.screens.nlgui.elements.GuiConfigElement;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.CloudApiClient;
import org.zenith.core.CloudConfigDetailsDto;
import org.zenith.core.CloudPoller;
import org.zenith.core.CloudSessionExtDto;
import org.zenith.core.CloudUserDto;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ModuleStateStore;
import org.zenith.core.PollMode;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.module.render.Menu;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.BooleanSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiConfigPanel extends ElementPanel {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float HEADER_HEIGHT = 23.0F;
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public static final int COLUMNS = 3;
   public static final long CONFIG_SYNC_INTERVAL_MS = 700L;
   public static final long CATALOG_REFRESH_INTERVAL_MS = 15000L;
   public static final int CATALOG_PAGE_SIZE = 25;
   public static final int MAX_DESCRIPTION_LENGTH = 512;
   public static final String UNIVERSAL_SERVER_TAG = "Universal / All";
   public static final String[] SERVER_TAGS = new String[]{"Universal / All", "Funtime", "HolyWorld", "ReallyWorld", "Other"};
   public static final float CREATE_WIDTH = 128.0F;
   public static final float CREATE_HEADER_HEIGHT = 29.0F;
   public static final float CREATE_INPUTS_HEIGHT = 68.0F;
   public static final float CREATE_PREVIEW_HEIGHT = 88.0F;
   public static final float CREATE_DESCRIPTION_HEIGHT = 61.0F;
   public static final float CREATE_VISIBILITY_HEIGHT = 54.0F;
   public static final float CREATE_DATA_HEIGHT = 54.0F;
   public static final float CREATE_ACTIONS_HEIGHT = 54.0F;
   public static final float CREATE_HEIGHT = 408.0F;
   public final List<GuiConfigElement> localElements = new ArrayList<>();
   public final List<GuiCloudConfigElement> libraryCloudElements = new ArrayList<>();
   public final List<GuiCloudConfigElement> publicElements = new ArrayList<>();
   public final Map<String, byte[]> cloudConfigCache = new HashMap<>();
   public final UiAnimation categoryAnimation = new UiAnimation(220L, 1.0F, Easing.StopUsingItemEvent);
   public final UiAnimation createDrawerAnimation = new UiAnimation(220L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation createHoverAnimation = new UiAnimation(180L, 0.0F, Easing.CloseScreenEvent);
   public final UiAnimation cancelHoverAnimation = new UiAnimation(180L, 0.0F, Easing.CloseScreenEvent);
   public final UiAnimation serverTagExpandedAnimation = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public SearchBox createNameBox;
   public SearchBox createDescriptionBox;
   public GuiBooleanSetting includeVisualsSetting;
   public GuiBooleanSetting includeOtherSetting;
   public GuiBooleanSetting includeBindsSetting;
   public CornerRadiusF localTabBounds;
   public CornerRadiusF libraryTabBounds;
   public CornerRadiusF publicTabBounds;
   public CornerRadiusF scissorBounds;
   public CornerRadiusF headerCreateButtonBounds;
   public CornerRadiusF createDrawerBounds;
   public CornerRadiusF createCloseBounds;
   public CornerRadiusF createSubmitBounds;
   public CornerRadiusF createCancelBounds;
   public CornerRadiusF previewPickBounds;
   public CornerRadiusF previewRemoveBounds;
   public CornerRadiusF descriptionFieldBounds;
   public CornerRadiusF serverTagBounds;
   public CornerRadiusF serverTagOptionsBounds;
   public final CornerRadiusF[] serverTagOptionBounds = new CornerRadiusF[SERVER_TAGS.length];
   public final CornerRadiusF[] visibilityBounds = new CornerRadiusF[3];
   public float scroll;
   public float scrollTarget;
   public float createDrawerProgress;
   public long lastConfigSyncAt;
   public long lastLibraryRefreshAt;
   public long lastCatalogRefreshAt;
   public String lastConfigSignature = "";
   public String createVisibility = "PRIVATE";
   public String createServerTag = "Universal / All";
   public String createStatus = "";
   public byte[] createPreviewPng;
   public String createPreviewStatus = "";
   public boolean previewPicking;
   public boolean serverTagExpanded;
   public CloudUserDto editing;
   public boolean editPreviewCleared;
   public String libraryStatus = "";
   public String publicStatus = "";
   public boolean createDrawerExpanded;
   public boolean creating;
   public boolean libraryLoading;
   public boolean publicLoading;
   public boolean libraryHasMore;
   public boolean publicHasMore;
   public Integer libraryNextOffset;
   public Integer publicNextOffset;
   public GuiConfigPanel_ConfigCategory currentCategory = GuiConfigPanel_ConfigCategory.LOCAL;
   public GuiConfigPanel_ConfigCategory lastCategory;

   public CloudSessionExtDto remotePreview() {
      return this.editing != null && !this.editPreviewCleared ? this.editing.HudClockPanel() : null;
   }

   public void editCloudConfig(GuiCloudConfigElement var1) {
      this.ensureCreateControls();
      CloudUserDto l1i1li1i11_l1iiiil1lii1iliiill1 = var1.getEntry().HudHotbarPanel();
      this.editing = l1i1li1i11_l1iiiil1lii1iliiill1;
      this.editPreviewCleared = false;
      this.createPreviewPng = null;
      this.createPreviewStatus = "";
      CloudPreviewTextureCache.clearDraft();
      this.createNameBox.HudHotbarPanel(l1i1li1i11_l1iiiil1lii1iliiill1.name());
      this.createServerTag = this.serverTagForEdit(l1i1li1i11_l1iiiil1lii1iliiill1.RotationLegitStrategy());
      this.createDescriptionBox.HudHotbarPanel(l1i1li1i11_l1iiiil1lii1iliiill1.description() == null ? "" : l1i1li1i11_l1iiiil1lii1iliiill1.description());
      this.createVisibility = l1i1li1i11_l1iiiil1lii1iliiill1.AimPolicyRotationStrategy();
      this.createStatus = "";
      this.createDrawerExpanded = true;
   }

   public void moveCursorToEndIfFocused(SearchBox var1, boolean var2) {
      if (var2 && !var1.isEmpty()) {
         var1.EventRender(var1.getText().length());
      }
   }

   public String serverTagForEdit(String var1) {
      if (var1 != null) {
         for (String s : SERVER_TAGS) {
            if (s.equalsIgnoreCase(var1)) {
               return s;
            }
         }
      }

      return "Universal / All";
   }

   public static String sha256Hex(byte[] var0) {
      try {
         byte[] abyte = MessageDigest.getInstance("SHA-256").digest(var0);
         StringBuilder stringbuilder = new StringBuilder(abyte.length * 2);

         for (byte b0 : abyte) {
            stringbuilder.append(Character.forDigit(b0 >> 4 & 15, 16));
            stringbuilder.append(Character.forDigit(b0 & 15, 16));
         }

         return stringbuilder.toString();
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         throw new IllegalStateException("SHA-256 is required", nosuchalgorithmexception);
      }
   }

   public void pickPreviewImage() {
      if (!this.previewPicking) {
         this.previewPicking = true;
         this.createPreviewStatus = "";
         ConfigPreviewImage.pick().whenComplete((var1, var2) -> minecraftClient3.execute(() -> {
            this.previewPicking = false;
            if (var2 != null) {
               this.createPreviewStatus = failureMessage(var2, "Could not read image");
            } else if (var1 != null) {
               this.createPreviewPng = var1;
               this.createPreviewStatus = "";
            }
         }));
      }
   }

   public void resetCreateForm() {
      this.createNameBox.HudHotbarPanel("");
      this.createServerTag = "Universal / All";
      this.createDescriptionBox.HudHotbarPanel("");
      this.editing = null;
      this.clearSelectedPreview();
   }

   public void clearSelectedPreview() {
      this.createPreviewPng = null;
      this.createPreviewStatus = "";
      this.editPreviewCleared = true;
      CloudPreviewTextureCache.clearDraft();
   }

   public boolean isOwnedByViewer(CloudConfigDetailsDto var1) {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l == null) {
         return false;
      }

      String s = var1.HudHotbarPanel().HudEffectIcons();
      return s != null && s.equals(Long.toString(l1i1iil111il1l1l.RefreshCacheEvent()));
   }

   @Override
   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      this.ensureCreateControls();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var5 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() * 2.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         String s = "9";
         float f1 = var6 + (23.0F - font.height()) / 2.0F;
         float f2 = var6 + (23.0F - font1.height()) / 2.0F - 0.1F;
         var1.drawText(font1, s, f, f2, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var4));
         var1.drawText(font, "Configs", f + font1.width(s) + GuiStyle.PADDING.intValue(), f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(var4));
         Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
         String s1 = GuiConfigPanel_ConfigCategory.LOCAL.label;
         String s2 = GuiConfigPanel_ConfigCategory.LIBRARY.label;
         String s3 = GuiConfigPanel_ConfigCategory.PUBLIC_HUB.label;
         float f3 = GuiStyle.PADDING.intValue() * 2.0F;
         float f4 = GuiStyle.PADDING.intValue();
         float f5 = font2.width(s3);
         float f6 = font2.width(s2);
         float f7 = font2.width(s1);
         float f8 = var5 + var7 - f3 - f5;
         float f9 = f8 - f4;
         float f10 = f9 - f4 - f6;
         float f11 = f10 - f4;
         float f12 = f11 - f4 - f7;
         float f13 = var6 + (23.0F - font2.height()) / 2.0F;
         this.localTabBounds = new CornerRadiusF(f12 - 2.0F, var6 + 1.0F, f7 + 4.0F, 21.0F);
         this.libraryTabBounds = new CornerRadiusF(f10 - 2.0F, var6 + 1.0F, f6 + 4.0F, 21.0F);
         this.publicTabBounds = new CornerRadiusF(f8 - 2.0F, var6 + 1.0F, f5 + 4.0F, 21.0F);
         var1.drawText(
            font2,
            s1,
            f12,
            f13,
            (this.currentCategory == GuiConfigPanel_ConfigCategory.LOCAL ? zenithstyle.getTextEnable().getColor() : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var4)
         );
         var1.drawRect(f11, f13, 0.5F, 6.0F, zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var4));
         var1.drawText(
            font2,
            s2,
            f10,
            f13,
            (this.currentCategory == GuiConfigPanel_ConfigCategory.LIBRARY ? zenithstyle.getTextEnable().getColor() : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var4)
         );
         var1.drawRect(f9, f13, 0.5F, 6.0F, zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var4));
         var1.drawText(
            font2,
            s3,
            f8,
            f13,
            (this.currentCategory == GuiConfigPanel_ConfigCategory.PUBLIC_HUB
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var4)
         );
      }
   }

   public void renderCategory(GuiConfigPanel_ConfigCategory var1, HudDrawContext var2, int var3, int var4, float var5, float var6, float var7, boolean var8) {
      List<? extends InterfaceElement> list = this.getCategoryElements(var1);
      if (list.isEmpty()) {
         if (!var8) {
            this.renderEmptyState(var2, var5, var6, var7, var1);
         }
      } else {
         float f = var6 + GuiStyle.PADDING.intValue();
         float f1 = var7 + this.scroll;
         float f2 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3 * 2.0F) / 3.0F;
         float[] afloat = new float[3];
         new CornerRadiusF(
            this.scissorBounds.x(), this.scissorBounds.y() - this.scissorBounds.height() / 2.0F, this.scissorBounds.width(), this.scissorBounds.height() * 2.0F
         );
         int i = 0;

         for (InterfaceElement interfaceelement : list) {
            int j = this.shortestColumn(afloat);
            float f5 = j * (f4 + f3);
            float f6 = afloat[j];
            float f7 = f + f5;
            float f8 = f1 + f6;
            float f9 = f7;
            float f10 = f8;
            GuiCloudConfigElement guicloudconfigelement = interfaceelement instanceof GuiCloudConfigElement guicloudconfigelement1
               ? guicloudconfigelement1
               : null;
            if (guicloudconfigelement != null) {
               guicloudconfigelement.updateLayoutTarget(f5, f6);
               f9 = f + guicloudconfigelement.getAnimatedLayoutX();
               f10 = f1 + guicloudconfigelement.getAnimatedLayoutY();
            }

            if (var8) {
               interfaceelement.renderPriority(var2, var3, var4, f9, f10, var5);
            } else {
               CornerRadiusF l11liliill1iii1x = new CornerRadiusF(f7, f8, interfaceelement.getWidth(), interfaceelement.getHeight());
               CornerRadiusF l11liliill1iii1xx = new CornerRadiusF(f9, f10, interfaceelement.getWidth(), interfaceelement.getHeight());
               if (guicloudconfigelement != null && (l11liliill1iii1xx.on23(l11liliill1iii1x) || l11liliill1iii1xx.on23(l11liliill1iii1xx))) {
                  guicloudconfigelement.prefetchPreview();
               }

               if (this.scissorBounds.on23(l11liliill1iii1x) || this.scissorBounds.on23(l11liliill1iii1xx)) {
                  interfaceelement.render(var2, var3, var4, f9, f10, var5, i + j);
               }
            }

            afloat[j] += interfaceelement.getHeight() + f3;
            i++;
         }
      }
   }

   @Override
   public void renderHeaderButtons(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = 23.0F;
         float f1 = var5 + var7 - f;
         this.headerCreateButtonBounds = new CornerRadiusF(f1, var6, f, f);
         var1.drawRoundedRect(
            f1,
            var6,
            f,
            f,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getPanelLeftBackground()
               .getColor()
               .Easing(zenithstyle.getPrimaryColor().getColor(), this.createDrawerProgress)
               .SprintStateEvent(var4 * var8)
         );
         Font font = Fonts.NEW_MEDIUM.getFont(8.0F);
         var1.drawText(
            font,
            "+",
            f1 + (f - font.width("+")) / 2.0F,
            var6 + (f - font.height()) / 2.0F - 0.2F,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.createDrawerProgress).SprintStateEvent(var4 * var8)
         );
      }
   }

   @Override
   public boolean onHeaderButtonsClicked(double var1, double var3, MenuScreenId var5) {
      this.ensureCreateControls();
      if (var5 != MenuScreenId.call004) {
         return false;
      }

      if (this.headerCreateButtonBounds != null && this.headerCreateButtonBounds.PotionItemBuilder(var1, var3)) {
         boolean flag2 = !this.createDrawerExpanded || this.editing != null;
         if (flag2) {
            this.resetCreateForm();
            this.editPreviewCleared = false;
            this.createStatus = "";
            this.createDrawerExpanded = true;
         } else {
            this.closeRightDrawer();
         }

         return true;
      } else {
         if (!this.createDrawerExpanded) {
            return false;
         }

         if (this.createDrawerBounds != null && !this.createDrawerBounds.PotionItemBuilder(var1, var3)) {
            this.closeRightDrawer();
            return false;
         }

         if (this.createCloseBounds != null && this.createCloseBounds.on23(var1, var3, 2.0F)) {
            this.closeRightDrawer();
            return true;
         }

         if (this.serverTagExpanded && this.selectServerTag(var1, var3)) {
            return true;
         }

         if (this.serverTagExpanded && this.serverTagOptionsBounds != null && this.serverTagOptionsBounds.PotionItemBuilder(var1, var3)) {
            return true;
         }

         if (this.serverTagBounds != null && this.serverTagBounds.PotionItemBuilder(var1, var3)) {
            this.serverTagExpanded = !this.serverTagExpanded;
            return true;
         }

         this.serverTagExpanded = false;
         boolean flag = this.createNameBox.onMouseClicked(var1, var3, var5);
         boolean flag1 = this.createDescriptionBox.onMouseClicked(var1, var3, var5);
         if (this.descriptionFieldBounds != null && this.descriptionFieldBounds.PotionItemBuilder(var1, var3)) {
            this.createDescriptionBox.VelocityChangeEvent(true);
            flag1 = true;
         }

         this.moveCursorToEndIfFocused(this.createNameBox, flag);
         this.moveCursorToEndIfFocused(this.createDescriptionBox, flag1);
         if (!flag && !flag1) {
            if (this.previewRemoveBounds != null && this.previewRemoveBounds.PotionItemBuilder(var1, var3)) {
               this.clearSelectedPreview();
               return true;
            }

            if (this.previewPickBounds != null && this.previewPickBounds.PotionItemBuilder(var1, var3)) {
               this.pickPreviewImage();
               return true;
            }

            String[] astring = new String[]{"PRIVATE", "FRIENDS", "PUBLIC"};

            for (int i = 0; i < this.visibilityBounds.length; i++) {
               if (this.visibilityBounds[i] != null && this.visibilityBounds[i].PotionItemBuilder(var1, var3)) {
                  this.createVisibility = astring[i];
                  return true;
               }
            }

            if (this.includeVisualsSetting.onMouseClicked(var1, var3, var5)
               || this.includeOtherSetting.onMouseClicked(var1, var3, var5)
               || this.includeBindsSetting.onMouseClicked(var1, var3, var5)) {
               return true;
            } else if (this.createSubmitBounds != null && this.createSubmitBounds.PotionItemBuilder(var1, var3)) {
               this.submitCreateConfig();
               return true;
            } else if (this.createCancelBounds != null && this.createCancelBounds.PotionItemBuilder(var1, var3)) {
               this.closeRightDrawer();
               return true;
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }

   public void renderCreateDrawer(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.ensureCreateControls();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var5 - (128.0F + GuiStyle.PADDING.intValue()) * (1.0F - var4);
         float f1 = this.drawerHeight();
         this.createDrawerBounds = new CornerRadiusF(f, var6, 128.0F, f1);
         var1.enableScissor(var5 - GuiStyle.PADDING.intValue() * 3.0F, var6, var5 + 128.0F + GuiStyle.PADDING.intValue() * 4.0F, var6 + f1);
         if (ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F) {
            ShapeRenderer.on23(
               var1.getMatrices(),
               f,
               var6,
               128.0F,
               f1,
               ZenithClient.on23().NbtEditor().getBlurPower(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
               ArgbColor.var11934.SprintStateEvent(var4),
               true,
               false
            );
         }

         float f2 = var6 + 29.0F;
         float f3 = f2 + 68.0F;
         float f4 = f3 + 88.0F;
         float f5 = f4 + 61.0F;
         float f6 = f5 + 54.0F;
         float f7 = f6 + 54.0F;
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getLeftBackground().getColor().SprintStateEvent(var4);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getRightBackground().getColor().SprintStateEvent(var4);
         var1.drawRoundedRect(f, var6, 128.0F, 29.0F, CornerRadius.Event29(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i11);
         var1.drawRect(f, f2, 128.0F, 68.0F, i11ii1llliilllii1i1);
         var1.drawRect(f, f3, 128.0F, 88.0F, i11ii1llliilllii1i11);
         var1.drawRect(f, f4, 128.0F, 61.0F, i11ii1llliilllii1i1);
         var1.drawRect(f, f5, 128.0F, 54.0F, i11ii1llliilllii1i11);
         var1.drawRect(f, f6, 128.0F, 54.0F, i11ii1llliilllii1i1);
         var1.drawRoundedRect(f, f7, 128.0F, 54.0F, CornerRadius.RotationUpdateStartEvent(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i11);
         this.renderCreateHeader(var1, var4, f, var6, zenithstyle);
         float f8 = f2 + 8.0F;
         this.renderCreateField(var1, var4, f, f8, zenithstyle, "Config Name", this.createNameBox);
         this.renderServerTagField(var1, var4, f, f8 + 28.0F, zenithstyle);
         this.renderPreviewPicker(var1, var2, var3, var4, f, f3, zenithstyle);
         this.renderDescription(var1, var4, f, f4, zenithstyle);
         this.renderVisibility(var1, var4, f, f5, zenithstyle);
         this.renderIncludedData(var1, var2, var3, var4, f, f6, zenithstyle);
         this.renderCreateActions(var1, var2, var3, var4, f, f7, zenithstyle);
         this.renderServerTagOptions(var1, var2, var3, var4, zenithstyle);
         var1.disableScissor();
      }
   }

   public void renderServerTagField(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.3F);
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = var4 + 7.0F;
      float f3 = 17.0F;
      this.serverTagBounds = new CornerRadiusF(f, f2, f1, f3);
      var1.drawText(font, "Server Tag", f, var4, var5.getTextTertiary().getColor().SprintStateEvent(var2));
      var1.drawRoundedRect(
         f, f2, f1, f3, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F), var5.getFieldSurfaceBackground().getColor().SprintStateEvent(var2)
      );
      var1.drawRoundedBorder(
         f, f2, f1, f3, 0.5F, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F), var5.getFieldBorder().getColor().SprintStateEvent(var2)
      );
      var1.drawText(
         font1,
         this.createServerTag,
         f + GuiStyle.PADDING.intValue() * 2.0F,
         f2 + (f3 - font1.height()) / 2.0F,
         var5.getTextEnable().getColor().SprintStateEvent(var2)
      );
      Font font2 = Fonts.NEW_ICONS.getFont(4.5F);
      String s = this.serverTagExpanded ? ">" : "v";
      var1.drawText(
         font2,
         s,
         f + f1 - font2.width(s) - GuiStyle.PADDING.intValue() * 2.0F,
         f2 + (f3 - font2.height()) / 2.0F,
         var5.getPrimaryColor().getColor().SprintStateEvent(var2)
      );
   }

   public void renderServerTagOptions(HudDrawContext var1, int var2, int var3, float var4, ZenithStyle var5) {
      float f = this.serverTagExpandedAnimation.on23(this.serverTagExpanded ? 1.0F : 0.0F);
      if (!(f <= 0.01F) && this.serverTagBounds != null) {
         float f1 = 12.0F;
         float f2 = GuiStyle.PADDING.intValue() / 2.0F;
         float f3 = this.serverTagBounds.x();
         float f4 = this.serverTagBounds.y() + this.serverTagBounds.height() + GuiStyle.PADDING.intValue();
         float f5 = this.serverTagBounds.width();
         float f6 = f2 + SERVER_TAGS.length * (f1 + f2);
         this.serverTagOptionsBounds = new CornerRadiusF(f3, f4, f5, f6);
         CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F);
         float f7 = var4 * f;
         var1.pushMatrix();
         var1.getMatrices().translate(f3 + f5 / 2.0F, f4);
         var1.getMatrices().scale(f, f);
         var1.getMatrices().translate(-(f3 + f5 / 2.0F), -f4);
         ShapeRenderer.ItemSpec(var1.getMatrices(), f3, f4, f5, f6, 12.0F, ii1il11l111ii11iil, ArgbColor.var11934.SprintStateEvent(f7));
         var1.drawRoundedRect(f3, f4, f5, f6, ii1il11l111ii11iil, var5.getLeftBackground().getColor().SprintStateEvent(f7));
         var1.drawRoundedBorder(f3, f4, f5, f6, 0.5F, ii1il11l111ii11iil, var5.getFieldBorder().getColor().SprintStateEvent(f7));
         Font font = Fonts.NEW_MEDIUM.getFont(5.3F);
         Font font1 = Fonts.NEW_ICONS.getFont(5.0F);
         float f8 = f4 + f2;

         for (int i = 0; i < SERVER_TAGS.length; i++) {
            String s = SERVER_TAGS[i];
            CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f3 + f2, f8, f5 - f2 * 2.0F, f1);
            this.serverTagOptionBounds[i] = l11liliill1iii1;
            boolean flag = s.equals(this.createServerTag);
            boolean flag1 = l11liliill1iii1.PotionItemBuilder(var2, var3);
            float f9 = flag ? 1.0F : (flag1 ? 0.5F : 0.0F);
            var1.drawRoundedRect(
               l11liliill1iii1.x(),
               l11liliill1iii1.y(),
               l11liliill1iii1.width(),
               l11liliill1iii1.height(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 4.0F),
               ArgbColor.var11941.Easing(var5.getFieldSurfaceBackground().getColor(), f9).SprintStateEvent(f7)
            );
            var1.drawText(
               font,
               s,
               l11liliill1iii1.x() + GuiStyle.PADDING.intValue(),
               l11liliill1iii1.y() + (l11liliill1iii1.height() - font.height()) / 2.0F,
               var5.getTextSecondary().getColor().Easing(var5.getTextEnable().getColor(), f9).SprintStateEvent(f7)
            );
            if (flag) {
               var1.drawText(
                  font1,
                  "<",
                  l11liliill1iii1.x() + l11liliill1iii1.width() - font1.width("<") - GuiStyle.PADDING.intValue(),
                  l11liliill1iii1.y() + (l11liliill1iii1.height() - font1.height()) / 2.0F,
                  var5.getPrimaryColor().getColor().SprintStateEvent(f7)
               );
            }

            f8 += f1 + f2;
         }

         var1.popMatrix();
      } else {
         this.serverTagOptionsBounds = null;
         Arrays.fill(this.serverTagOptionBounds, null);
      }
   }

   public void renderPreviewPicker(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, ZenithStyle var7) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font1 = Fonts.NEW_REGULAR.getFont(4.3F);
      float f = var5 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = var6 + 8.0F;
      float f3 = var6 + 17.0F;
      float f4 = 63.0F;
      boolean flag = this.createPreviewPng != null || this.remotePreview() != null;
      boolean flag1 = this.serverTagExpanded || this.serverTagExpandedAnimation.CancellableEvent() > 0.01F;
      boolean flag2 = !this.previewPicking && !flag1 && var2 >= f && var2 <= f + f1 && var3 >= f3 && var3 <= f3 + f4;
      var1.drawText(font, "Preview Image", f, f2, var7.getTextTertiary().getColor().SprintStateEvent(var4));
      this.previewPickBounds = new CornerRadiusF(f, f3, f1, f4);
      CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F);
      var1.drawRoundedRect(
         f,
         f3,
         f1,
         f4,
         ii1il11l111ii11iil,
         var7.getFieldSurfaceBackground().getColor().Easing(var7.getPrimaryColor().getColor(), flag2 && !flag ? 0.1F : 0.0F).SprintStateEvent(var4)
      );
      Identifier identifier = this.createPreviewPng != null
         ? CloudPreviewTextureCache.draft(this.createPreviewPng)
         : CloudPreviewTextureCache.get(this.remotePreview());
      if (identifier != null) {
         var1.drawRoundedTexture(
            identifier, f, f3, f1, f4, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 4.0F), ArgbColor.var11934.SprintStateEvent(var4)
         );
      } else {
         Font font2 = Fonts.NEW_MEDIUM.getFont(8.0F);
         String s = this.previewPicking ? "..." : "+";
         var1.drawText(
            font2,
            s,
            f + (f1 - font2.width(s)) / 2.0F,
            f3 + (f4 - font2.height()) / 2.0F - (flag ? 0.0F : 1.0F),
            var7.getTextTertiary().getColor().SprintStateEvent(var4)
         );
         if (!this.createPreviewStatus.isBlank()) {
            String s1 = trimToWidth(font1, this.createPreviewStatus, f1 - GuiStyle.PADDING.intValue() * 2.0F);
            var1.drawText(
               font1, s1, f + (f1 - font1.width(s1)) / 2.0F, f3 + f4 - font1.height() - 3.0F, var7.getPrimaryColor().getColor().SprintStateEvent(var4)
            );
         }
      }

      var1.drawRoundedBorder(f, f3, f1, f4, 0.5F, ii1il11l111ii11iil, var7.getFieldBorder().getColor().SprintStateEvent(var4));
      if (flag) {
         Font font3 = Fonts.NEW_MEDIUM.getFont(4.5F);
         float f7 = font3.width("Remove") + GuiStyle.PADDING.intValue() * 2.0F;
         float f8 = font3.height() + 4.0F;
         float f5 = f + f1 - f7 - 3.0F;
         float f6 = f3 + 3.0F;
         this.previewRemoveBounds = new CornerRadiusF(f5, f6, f7, f8);
         boolean flag3 = this.previewRemoveBounds.PotionItemBuilder(var2, var3);
         var1.drawRoundedRect(
            f5, f6, f7, f8, CornerRadius.MovementInputEvent(3.0F), var7.getRightBackground().getColor().SprintStateEvent(var4 * (flag3 ? 0.95F : 0.72F))
         );
         var1.drawText(
            font3, "Remove", f5 + GuiStyle.PADDING.intValue(), f6 + (f8 - font3.height()) / 2.0F, var7.getTextSecondary().getColor().SprintStateEvent(var4)
         );
      } else {
         this.previewRemoveBounds = null;
      }
   }

   public void renderDescription(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = var4 + 8.0F;
      float f3 = var4 + 15.0F;
      float f4 = 38.0F;
      CornerRadius ii1il11l111ii11iil = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F);
      this.descriptionFieldBounds = new CornerRadiusF(f, f3, f1, f4);
      var1.drawText(font, "Description", f, f2, var5.getTextTertiary().getColor().SprintStateEvent(var2));
      var1.drawRoundedRect(f, f3, f1, f4, ii1il11l111ii11iil, var5.getFieldSurfaceBackground().getColor().SprintStateEvent(var2));
      var1.drawRoundedBorder(f, f3, f1, f4, 0.5F, ii1il11l111ii11iil, var5.getFieldBorder().getColor().SprintStateEvent(var2));
      this.createDescriptionBox.on23(font1);
      this.createDescriptionBox.setWidth(f1 - GuiStyle.PADDING.intValue() * 4.0F);
      this.createDescriptionBox.setHeight(f4 - 10.0F);
      this.createDescriptionBox
         .on23(
            var1,
            f + GuiStyle.PADDING.intValue() * 2.0F,
            f3 + 5.0F,
            var5.getTextEnable().getColor().SprintStateEvent(var2),
            var5.getTextSecondary().getColor().SprintStateEvent(var2)
         );
   }

   public void renderVisibility(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = var4 + 17.0F;
      var1.drawText(font, "Visibility", f, var4 + 8.0F, var5.getTextTertiary().getColor().SprintStateEvent(var2));
      String[] astring = new String[]{"PRIVATE", "FRIENDS", "PUBLIC"};
      String[] astring1 = new String[]{"Private", "Friends", "Public"};

      for (int i = 0; i < astring1.length; i++) {
         this.visibilityBounds[i] = new CornerRadiusF(f, f2, f1, 9.0F);
         boolean flag = astring[i].equals(this.createVisibility);
         float f3 = 7.0F;
         float f4 = f + f1 - f3;
         float f5 = f2 + 1.0F;
         var1.drawRoundedRect(
            f4,
            f5,
            f3,
            f3,
            CornerRadius.MovementInputEvent(f3 / 2.5F),
            (flag ? var5.getPrimaryColor().getColor() : var5.getFieldSurfaceBackground().getColor()).SprintStateEvent(var2)
         );
         if (flag) {
            var1.drawRoundedRect(
               f4 + 2.0F, f5 + 2.0F, 3.0F, 3.0F, CornerRadius.MovementInputEvent(f3 / 4.0F), var5.getTextEnable().getColor().SprintStateEvent(var2)
            );
         } else {
            var1.drawRoundedBorder(f4, f5, f3, f3, 0.3F, CornerRadius.MovementInputEvent(f3 / 2.0F), var5.getFieldBorder().getColor().SprintStateEvent(var2));
         }

         var1.drawText(
            font1,
            astring1[i],
            f,
            f2 + (9.0F - font1.height()) / 2.0F,
            (flag ? var5.getTextEnable().getColor() : var5.getTextSecondary().getColor()).SprintStateEvent(var2)
         );
         f2 += 11.0F;
      }
   }

   public void renderIncludedData(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, ZenithStyle var7) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      float f = var5 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = var6 + 17.0F;
      var1.drawText(font, "Data Included", f, var6 + 8.0F, var7.getTextTertiary().getColor().SprintStateEvent(var4));
      boolean flag = ZenithClient.on23().NbtEditor().isRenderIcon();
      ZenithClient.on23().NbtEditor().setRenderIcon(false);

      try {
         this.includeVisualsSetting.render(var1, var2, var3, f, f1, var4);
         f1 += this.includeVisualsSetting.getAnimHeight() + GuiStyle.PADDING.intValue();
         this.includeOtherSetting.render(var1, var2, var3, f, f1, var4);
         f1 += this.includeOtherSetting.getAnimHeight() + GuiStyle.PADDING.intValue();
         this.includeBindsSetting.render(var1, var2, var3, f, f1, var4);
      } finally {
         ZenithClient.on23().NbtEditor().setRenderIcon(flag);
      }
   }

   public void renderCreateActions(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, ZenithStyle var7) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = var5 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = 17.0F;
      float f3 = var6 + 8.0F;
      float f4 = f3 + f2 + GuiStyle.PADDING.intValue();
      this.createSubmitBounds = new CornerRadiusF(f, f3, f1, f2);
      this.createCancelBounds = new CornerRadiusF(f, f4, f1, f2);
      float f5 = this.createHoverAnimation.on23(this.createSubmitBounds.PotionItemBuilder(var2, var3) && !this.creating ? 1.0F : 0.0F);
      float f6 = this.cancelHoverAnimation.on23(this.createCancelBounds.PotionItemBuilder(var2, var3) ? 1.0F : 0.0F);
      var1.drawRoundedRect(
         this.createSubmitBounds.x(),
         f3,
         f1,
         f2,
         CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
         var7.getPrimaryColor().getColor().Easing(var7.getTextEnable().getColor(), f5 * 0.12F).SprintStateEvent(var4 * (this.creating ? 0.65F : 1.0F))
      );
      var1.drawRoundedRect(
         this.createCancelBounds.x(),
         f4,
         f1,
         f2,
         CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
         var7.getFieldSurfaceBackground().getColor().Easing(var7.getPrimaryColor().getColor(), f6 * 0.12F).SprintStateEvent(var4)
      );
      var1.drawRoundedBorder(
         this.createCancelBounds.x(),
         f4,
         f1,
         f2,
         0.5F,
         CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
         var7.getFieldBorder().getColor().SprintStateEvent(var4)
      );
      String s = this.creating ? "Saving..." : (this.editing == null ? "Create & Save" : "Save Changes");
      String s1 = "Cancel";
      var1.drawText(
         font,
         s,
         this.createSubmitBounds.x() + (f1 - font.width(s)) / 2.0F,
         f3 + (f2 - font.height()) / 2.0F,
         var7.getTextEnable().getColor().SprintStateEvent(var4)
      );
      var1.drawText(
         font,
         s1,
         this.createCancelBounds.x() + (f1 - font.width(s1)) / 2.0F,
         f4 + (f2 - font.height()) / 2.0F,
         var7.getTextSecondary().getColor().Easing(var7.getTextEnable().getColor(), f6).SprintStateEvent(var4)
      );
   }

   public void submitCreateConfig() {
      this.ensureCreateControls();
      if (!this.creating) {
         if (this.editing != null) {
            this.submitEdit();
         } else {
            String s = sanitizeConfigName(this.createNameBox.getText());
            if (s.isBlank()) {
               this.createStatus = "Enter a config name";
            } else {
               CloudPoller liill1llill11i11il = ZenithClient.on23().TradeGuardService();
               if (liill1llill11i11il.CloudPoller(s) != null) {
                  this.createStatus = "A config with this name already exists";
               } else if (!liill1llill11i11il.on23(
                  s,
                  this.includeVisualsSetting.getSetting().isEnabled(),
                  this.includeOtherSetting.getSetting().isEnabled(),
                  this.includeBindsSetting.getSetting().isEnabled()
               )) {
                  this.createStatus = "Could not save the config";
               } else {
                  this.rebuildElements();
                  ModuleStateStore illlll11i11i1illi1l1ii1i111 = liill1llill11i11il.CloudPoller(s);
                  CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
                  if (illlll11i11i1illi1l1ii1i111 != null && l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected()) {
                     try {
                        byte[] abyte = Files.readAllBytes(illlll11i11i1illi1l1ii1i111.getFile().toPath());
                        byte[] abyte1 = this.createPreviewPng;
                        this.creating = true;
                        this.createStatus = "Uploading...";
                        l1i1iil111il1l1l.on23(
                              s, s + "." + "Zenith".toLowerCase(Locale.ROOT), this.serverTag(), this.createVisibility, this.description(), 0, abyte
                           )
                           .thenCompose(
                              var2x -> abyte1 == null
                                 ? CompletableFuture.completedFuture(var2x)
                                 : l1i1iil111il1l1l.on23(var2x.PermissionListCodec(), abyte1).thenApply(var1xx -> var2x)
                           )
                           .whenComplete((var1x, var2x) -> minecraftClient3.execute(() -> {
                              this.creating = false;
                              if (var2x != null) {
                                 this.createStatus = "Saved locally; " + failureMessage(var2x, "upload failed");
                              } else {
                                 this.createStatus = "Saved to cloud library";
                                 this.resetCreateForm();
                                 this.refreshLibraryCatalog(true);
                                 if ("PUBLIC".equals(this.createVisibility)) {
                                    this.refreshPublicCatalog(true);
                                 }

                                 this.closeRightDrawer();
                              }
                           }));
                     } catch (Exception exception) {
                        this.createStatus = "Saved locally; upload failed";
                     }
                  } else {
                     this.createStatus = "Saved locally";
                     this.createNameBox.HudHotbarPanel("");
                     this.closeRightDrawer();
                  }
               }
            }
         }
      }
   }

   public void submitEdit() {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected()) {
         String s = sanitizeConfigName(this.createNameBox.getText());
         if (s.isBlank()) {
            this.createStatus = "Enter a config name";
         } else {
            CloudPoller liill1llill11i11il = ZenithClient.on23().TradeGuardService();
            if (!liill1llill11i11il.on23(
               s,
               this.includeVisualsSetting.getSetting().isEnabled(),
               this.includeOtherSetting.getSetting().isEnabled(),
               this.includeBindsSetting.getSetting().isEnabled()
            )) {
               this.createStatus = "Could not save the config locally";
            } else {
               this.rebuildElements();
               ModuleStateStore illlll11i11i1illi1l1ii1i111 = liill1llill11i11il.CloudPoller(s);
               if (illlll11i11i1illi1l1ii1i111 == null) {
                  this.createStatus = "Could not read the saved config";
               } else {
                  byte[] abyte;
                  try {
                     abyte = Files.readAllBytes(illlll11i11i1illi1l1ii1i111.getFile().toPath());
                  } catch (Exception exception) {
                     this.createStatus = "Could not read the saved config";
                     return;
                  }

                  CloudUserDto l1i1li1i11_l1iiiil1lii1iliiill1 = this.editing;
                  byte[] abyte1 = this.createPreviewPng;
                  boolean flag = abyte1 == null && this.editPreviewCleared && l1i1li1i11_l1iiiil1lii1iliiill1.HudClockPanel() != null;
                  byte[] abyte2 = sha256Hex(abyte).equals(l1i1li1i11_l1iiiil1lii1iliiill1.RotationSnapStrategy()) ? null : abyte;
                  this.creating = true;
                  this.createStatus = "Saving...";
                  String s1 = this.createDescriptionBox.getText() == null ? "" : this.createDescriptionBox.getText().trim();
                  String s2 = s + "." + "Zenith".toLowerCase(Locale.ROOT);
                  l1i1iil111il1l1l.on23(
                        l1i1li1i11_l1iiiil1lii1iliiill1.PermissionListCodec(),
                        l1i1li1i11_l1iiiil1lii1iliiill1.HudScoreboard(),
                        s,
                        this.serverTag(),
                        s1,
                        this.createVisibility
                     )
                     .thenCompose(
                        var4x -> abyte2 == null
                           ? CompletableFuture.completedFuture(var4x)
                           : l1i1iil111il1l1l.on23(l1i1li1i11_l1iiiil1lii1iliiill1.PermissionListCodec(), var4x.HudScoreboard(), s2, abyte2)
                     )
                     .thenCompose(
                        var4x -> {
                           if (abyte1 != null) {
                              return l1i1iil111il1l1l.on23(l1i1li1i11_l1iiiil1lii1iliiill1.PermissionListCodec(), abyte1).thenApply(var1xx -> var4x);
                           } else {
                              return flag
                                 ? l1i1iil111il1l1l.UiAnimation(l1i1li1i11_l1iiiil1lii1iliiill1.PermissionListCodec())
                                 : CompletableFuture.completedFuture(var4x);
                           }
                        }
                     )
                     .whenComplete((var1x, var2x) -> minecraftClient3.execute(() -> {
                        this.creating = false;
                        if (var2x != null) {
                           this.createStatus = failureMessage(var2x, "could not save changes");
                        } else {
                           this.createStatus = "Saved";
                           this.resetCreateForm();
                           this.refreshLibraryCatalog(true);
                           this.refreshPublicCatalog(true);
                           this.closeRightDrawer();
                        }
                     }));
               }
            }
         }
      } else {
         this.createStatus = "Cloud is offline";
      }
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.syncConfigs();
      if (this.currentCategory == GuiConfigPanel_ConfigCategory.LIBRARY) {
         this.refreshLibraryCatalog(false);
      } else if (this.currentCategory == GuiConfigPanel_ConfigCategory.PUBLIC_HUB) {
         this.refreshPublicCatalog(false);
      }

      this.categoryAnimation.on23(Menu.menu.int470());
      this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 295.5F - GuiStyle.PADDING.intValue() * 2.0F);
      float f = this.getContentHeight(this.getCategoryElements(this.currentCategory));
      this.clampScroll(f + GuiStyle.PADDING.intValue(), this.scissorBounds.height());
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f1 = this.categoryAnimation.on23(1.0F);
      if (this.categoryAnimation.isDone()) {
         this.lastCategory = null;
      }

      float f2 = Math.max(0.0F, Math.min(1.0F, (0.5F - f1) / 0.5F));
      float f3 = Math.max(0.0F, Math.min(1.0F, (f1 - 0.3F) / 0.7F));
      var1.enableScissor(var5, var6, var5 + this.scissorBounds.width(), var6 + this.scissorBounds.height());
      if (this.lastCategory != null && f2 > 0.0F) {
         this.renderCategory(this.lastCategory, var1, var2, var3, var4 * f2, var5, var6 - 14.0F * (1.0F - f2), false);
      }

      if (f3 > 0.0F) {
         this.renderCategory(this.currentCategory, var1, var2, var3, var4 * f3, var5, var6 + 14.0F * (1.0F - f3), false);
      }

      var1.disableScissor();
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = Math.max(0.0F, Math.min(1.0F, (this.categoryAnimation.CancellableEvent() - 0.3F) / 0.7F));
      if (f > 0.0F) {
         this.renderCategory(this.currentCategory, var1, var2, var3, var4 * f, var5, var6 + 14.0F * (1.0F - f), true);
      }
   }

   public void renderEmptyState(HudDrawContext var1, float var2, float var3, float var4, GuiConfigPanel_ConfigCategory var5) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.0F);
         String s;
         String s1;
         if (var5 == GuiConfigPanel_ConfigCategory.PUBLIC_HUB) {
            s = this.publicLoading ? "Loading Public Hub..." : "No public configs found";
            s1 = this.publicStatus.isBlank() ? "Cloud catalog is empty" : this.publicStatus;
         } else if (var5 == GuiConfigPanel_ConfigCategory.LIBRARY) {
            s = this.libraryLoading ? "Loading Libary..." : "No library configs found";
            s1 = this.libraryStatus.isBlank() ? "Cloud library is empty" : this.libraryStatus;
         } else {
            s = "No local configs found";
            s1 = "Use + to save the current settings";
         }

         float f = var3 + 188.0F;
         float f1 = var4 + this.scissorBounds.height() / 2.0F;
         var1.drawText(font, s, f - font.width(s) / 2.0F, f1 - font.height(), zenithstyle.getTextEnable().getColor().SprintStateEvent(var2));
         var1.drawText(font1, s1, f - font1.width(s1) / 2.0F, f1 + 2.0F, zenithstyle.getTextSecondary().getColor().SprintStateEvent(var2));
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.scissorBounds == null) {
         return false;
      }

      List<? extends InterfaceElement> list = this.getCategoryElements(this.currentCategory);

      for (InterfaceElement interfaceelement : list) {
         if (interfaceelement.mouseScrolled(var1, var3, var5, var7)) {
            return true;
         }
      }

      if (this.scissorBounds.PotionItemBuilder(var1, var3) && !list.isEmpty()) {
         float f = this.getContentHeight(list) + GuiStyle.PADDING.intValue();
         if (f <= this.scissorBounds.height()) {
            return false;
         }

         this.scrollTarget += (float)var7 * 22.0F;
         this.clampScroll(f, this.scissorBounds.height());
         if (this.scrollTarget <= this.scissorBounds.height() - f + 12.0F) {
            if (this.currentCategory == GuiConfigPanel_ConfigCategory.PUBLIC_HUB && this.publicHasMore && this.publicNextOffset != null) {
               this.requestPublicPage(this.publicNextOffset, true);
            } else if (this.currentCategory == GuiConfigPanel_ConfigCategory.LIBRARY && this.libraryHasMore && this.libraryNextOffset != null) {
               this.requestLibraryPage(this.libraryNextOffset, true);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 == MenuScreenId.call004) {
         if (this.localTabBounds != null && this.localTabBounds.PotionItemBuilder(var1, var3)) {
            this.setCategory(GuiConfigPanel_ConfigCategory.LOCAL);
            return true;
         }

         if (this.libraryTabBounds != null && this.libraryTabBounds.PotionItemBuilder(var1, var3)) {
            this.setCategory(GuiConfigPanel_ConfigCategory.LIBRARY);
            return true;
         }

         if (this.publicTabBounds != null && this.publicTabBounds.PotionItemBuilder(var1, var3)) {
            this.setCategory(GuiConfigPanel_ConfigCategory.PUBLIC_HUB);
            return true;
         }
      }

      if (this.scissorBounds == null) {
         return false;
      }

      for (InterfaceElement interfaceelement : this.getCategoryElements(this.currentCategory)) {
         if (interfaceelement.onMousePriorityClicked(var1, var3, var5)) {
            return true;
         }
      }

      if (this.categoryAnimation.isDone() && this.scissorBounds.PotionItemBuilder(var1, var3)) {
         for (InterfaceElement interfaceelement1 : this.getCategoryElements(this.currentCategory)) {
            if (interfaceelement1.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (InterfaceElement interfaceelement : this.getCategoryElements(this.currentCategory)) {
         interfaceelement.onMouseReleased(var1, var3, var5);
      }

      return super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      this.ensureCreateControls();
      if (!this.createDrawerExpanded || !this.createNameBox.keyPressed(var1, var2, var3) && !this.createDescriptionBox.keyPressed(var1, var2, var3)) {
         for (InterfaceElement interfaceelement : this.getCategoryElements(this.currentCategory)) {
            if (interfaceelement.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         return super.keyPressed(var1, var2, var3);
      } else {
         return true;
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      this.ensureCreateControls();
      if (!this.createDrawerExpanded || !this.createNameBox.charTyped(var1, var2) && !this.createDescriptionBox.charTyped(var1, var2)) {
         for (InterfaceElement interfaceelement : this.getCategoryElements(this.currentCategory)) {
            if (interfaceelement.charTyped(var1, var2)) {
               return true;
            }
         }

         return super.charTyped(var1, var2);
      } else {
         return true;
      }
   }

   @Override
   public List<? extends Element> getElements() {
      return this.getCategoryElements(this.currentCategory);
   }

   @Override
   public void close() {
      this.closeRightDrawer();
      this.categoryAnimation.UiAnimation(1.0F);
      this.lastCategory = null;
      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
   }

   @Override
   public void tick() {
      this.syncConfigs();
      if (this.currentCategory == GuiConfigPanel_ConfigCategory.LIBRARY) {
         this.refreshLibraryCatalog(false);
      } else if (this.currentCategory == GuiConfigPanel_ConfigCategory.PUBLIC_HUB) {
         this.refreshPublicCatalog(false);
      }
   }

   @Override
   public float getButtonWidth() {
      return 23.0F + GuiStyle.PADDING.intValue();
   }

   @Override
   public boolean isRightDrawerOpen() {
      return this.createDrawerExpanded || this.createDrawerAnimation.CancellableEvent() > 0.01F;
   }

   @Override
   public boolean isRender() {
      return this.isRightDrawerOpen();
   }

   @Override
   public void closeRightDrawer() {
      this.createDrawerExpanded = false;
      this.serverTagExpanded = false;
      this.editing = null;
      this.editPreviewCleared = false;
      if (this.createNameBox != null) {
         this.createNameBox.VelocityChangeEvent(false);
         this.createDescriptionBox.VelocityChangeEvent(false);
      }
   }

   @Override
   public void renderRightPanel(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      this.createDrawerProgress = this.createDrawerAnimation.on23(this.createDrawerExpanded ? 1.0F : 0.0F) * var4 * var7;
      if (this.createDrawerProgress > 0.01F) {
         this.renderCreateDrawer(var1, var2, var3, this.createDrawerProgress, var5, var6);
      }
   }

   @Override
   public CornerRadiusF getRightPanelBlurBounds(float var1, float var2, float var3, float var4) {
      if (this.createDrawerProgress <= 0.01F) {
         return null;
      }

      float f = var1 - (128.0F + GuiStyle.PADDING.intValue()) * (1.0F - this.createDrawerProgress);
      return new CornerRadiusF(f, var2, 128.0F, this.drawerHeight());
   }

   @Override
   public float getRightPanelBlurProgress(float var1, float var2) {
      return this.createDrawerProgress;
   }

   public float drawerHeight() {
      return 408.0F;
   }

   public void renderCreateHeader(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font font1 = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = var4 + 8.0F;
      var1.drawText(font, this.editing == null ? "Create Config" : "Edit Config", f, f1, var5.getTextEnable().getColor().SprintStateEvent(var2));
      String s = this.editing == null ? "Save current settings as a new preset." : "Update the presentation of this config.";
      String s1 = this.createStatus.isBlank() ? s : this.createStatus;
      s1 = trimToWidth(font1, s1, 128.0F - GuiStyle.PADDING.intValue() * 4.0F);
      var1.drawText(
         font1,
         s1,
         f,
         f1 + font.height() + 2.0F,
         (this.createStatus.isBlank() ? var5.getTextSecondary().getColor() : var5.getPrimaryColor().getColor()).SprintStateEvent(var2)
      );
      float f2 = var3 + 128.0F - font2.width("2") - 8.0F;
      this.createCloseBounds = new CornerRadiusF(f2, f1, font2.width("2"), font2.height());
      var1.drawText(font2, "2", f2, f1, var5.getTextTertiary().getColor().SprintStateEvent(var2));
   }

   public void renderCreateField(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5, String var6, SearchBox var7) {
      Font font = Fonts.NEW_REGULAR.getFont(4.5F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float f = var3 + GuiStyle.PADDING.intValue() * 2.0F;
      float f1 = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      float f2 = var4 + 7.0F;
      float f3 = 17.0F;
      var1.drawRoundedRect(
         f, f2, f1, f3, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F), var5.getFieldSurfaceBackground().getColor().SprintStateEvent(var2)
      );
      var1.drawRoundedBorder(
         f, f2, f1, f3, 0.5F, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F), var5.getFieldBorder().getColor().SprintStateEvent(var2)
      );
      var1.drawText(font, var6, f, var4, var5.getTextTertiary().getColor().SprintStateEvent(var2));
      var7.on23(font1);
      var7.setWidth(f1 - GuiStyle.PADDING.intValue() * 4.0F);
      var7.on23(
         var1,
         f + GuiStyle.PADDING.intValue() * 2.0F,
         f2 + (f3 - font1.height()) / 2.0F,
         var5.getTextEnable().getColor().SprintStateEvent(var2),
         var5.getTextSecondary().getColor().SprintStateEvent(var2)
      );
   }

   public boolean selectServerTag(double var1, double var3) {
      for (int i = 0; i < this.serverTagOptionBounds.length; i++) {
         CornerRadiusF l11liliill1iii1 = this.serverTagOptionBounds[i];
         if (l11liliill1iii1 != null && l11liliill1iii1.PotionItemBuilder(var1, var3)) {
            this.createServerTag = SERVER_TAGS[i];
            this.serverTagExpanded = false;
            return true;
         }
      }

      return false;
   }

   public void setCategory(GuiConfigPanel_ConfigCategory var1) {
      if (this.currentCategory != var1 && this.categoryAnimation.isDone()) {
         this.lastCategory = this.currentCategory;
         this.currentCategory = var1;
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.categoryAnimation.UiAnimation(0.0F);
         this.categoryAnimation.Easing(1.0F);
         if (var1 == GuiConfigPanel_ConfigCategory.PUBLIC_HUB) {
            this.refreshPublicCatalog(true);
         } else if (var1 == GuiConfigPanel_ConfigCategory.LIBRARY) {
            this.refreshLibraryCatalog(true);
         }
      }
   }

   private List<? extends InterfaceElement> getCategoryElements(GuiConfigPanel_ConfigCategory var1) {
      return switch (var1) {
         case LOCAL -> this.getFilteredLocalElements();
         case LIBRARY -> this.getFilteredCloudElements(this.libraryCloudElements);
         case PUBLIC_HUB -> this.getFilteredCloudElements(this.publicElements);
      };
   }

   public List<GuiConfigElement> getFilteredLocalElements() {
      String s = this.searchQuery();
      List<GuiConfigElement> list = s.isEmpty()
         ? this.localElements
         : this.localElements.stream().filter(var1x -> var1x.getName().toLowerCase(Locale.ROOT).contains(s)).toList();
      return this.getPriorityOrdered(list);
   }

   public List<GuiCloudConfigElement> getFilteredCloudElements(List<GuiCloudConfigElement> var1) {
      if (var1.isEmpty()) {
         return Collections.emptyList();
      }

      String s = this.searchQuery();
      return s.isEmpty()
         ? var1
         : var1.stream()
            .filter(
               var1x -> var1x.getName().toLowerCase(Locale.ROOT).contains(s)
                  || var1x.getEntry().HudElementValue() != null && var1x.getEntry().HudElementValue().HudInventoryPanel().toLowerCase(Locale.ROOT).contains(s)
            )
            .toList();
   }

   public String searchQuery() {
      String s = ZenithClient.on23().NbtEditor().getSearchValue();
      return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
   }

   public List<GuiConfigElement> getPriorityOrdered(List<GuiConfigElement> var1) {
      List<GuiConfigElement> arraylist = new ArrayList<>(var1.size());

      for (GuiConfigElement guiconfigelement : var1) {
         if (guiconfigelement.isPriority()) {
            arraylist.add(guiconfigelement);
         }
      }

      for (GuiConfigElement guiconfigelement1 : var1) {
         if (!guiconfigelement1.isPriority()) {
            arraylist.add(guiconfigelement1);
         }
      }

      return arraylist;
   }

   public float getContentHeight(List<? extends InterfaceElement> var1) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      float[] afloat = new float[3];

      for (InterfaceElement interfaceelement : var1) {
         int i = this.shortestColumn(afloat);
         afloat[i] += interfaceelement.getHeight() + GuiStyle.PADDING.intValue();
      }

      float f1 = 0.0F;

      for (float f : afloat) {
         f1 = Math.max(f1, f);
      }

      return Math.max(0.0F, f1 - GuiStyle.PADDING.intValue());
   }

   public int shortestColumn(float[] var1) {
      int i = 0;

      for (int j = 1; j < var1.length; j++) {
         if (var1[j] < var1[i]) {
            i = j;
         }
      }

      return i;
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float f = var2 - var1;
         this.scrollTarget = Math.max(f, Math.min(0.0F, this.scrollTarget));
         this.scroll = Math.max(f, Math.min(0.0F, this.scroll));
      }
   }

   public void rebuildElements() {
      HashMap hashmap = new HashMap();

      for (GuiConfigElement guiconfigelement : this.localElements) {
         hashmap.put(guiconfigelement.getName().toLowerCase(Locale.ROOT), guiconfigelement);
      }

      ArrayList arraylist = new ArrayList();
      ArrayList arraylist1 = new ArrayList();

      for (String s : ZenithClient.on23().TradeGuardService().AutoAuth()) {
         String s1 = s.replace("." + "Zenith".toLowerCase(Locale.ROOT), "").trim();
         if (!s1.isEmpty()) {
            ModuleStateStore illlll11i11i1illi1l1ii1i111 = ZenithClient.on23().TradeGuardService().CloudPoller(s1);
            if (illlll11i11i1illi1l1ii1i111 != null) {
               String s2 = s1.toLowerCase(Locale.ROOT);
               arraylist1.add(s2);
               GuiConfigElement guiconfigelement1 = (GuiConfigElement)hashmap.get(s2);
               arraylist.add(guiconfigelement1 != null ? guiconfigelement1 : new GuiConfigElement(illlll11i11i1illi1l1ii1i111, this::rebuildElements));
            }
         }
      }

      this.localElements.clear();
      this.localElements.addAll(arraylist);
      Collections.sort(arraylist1);
      this.lastConfigSignature = String.join("|", arraylist1);
   }

   public void syncConfigs() {
      long i = System.currentTimeMillis();
      if (i - this.lastConfigSyncAt >= 700L) {
         this.lastConfigSyncAt = i;
         ArrayList arraylist = new ArrayList();

         for (String s : ZenithClient.on23().TradeGuardService().AutoAuth()) {
            String s1 = s.replace("." + "Zenith".toLowerCase(Locale.ROOT), "").trim();
            if (!s1.isEmpty()) {
               arraylist.add(s1.toLowerCase(Locale.ROOT));
            }
         }

         Collections.sort(arraylist);
         String s2 = String.join("|", arraylist);
         if (!s2.equals(this.lastConfigSignature)) {
            this.rebuildElements();
         }
      }
   }

   public void refreshPublicCatalog(boolean var1) {
      long i = System.currentTimeMillis();
      if (!this.publicLoading && (var1 || i - this.lastCatalogRefreshAt >= 15000L)) {
         this.requestPublicPage(0, false);
      }
   }

   public void refreshLibraryCatalog(boolean var1) {
      long i = System.currentTimeMillis();
      if (!this.libraryLoading && (var1 || i - this.lastLibraryRefreshAt >= 15000L)) {
         this.requestLibraryPage(0, false);
      }
   }

   public void requestLibraryPage(int var1, boolean var2) {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l == null || !l1i1iil111il1l1l.isConnected()) {
         this.libraryStatus = "Cloud is unavailable";
         this.libraryLoading = false;
         this.lastLibraryRefreshAt = System.currentTimeMillis();
      } else if (!this.libraryLoading) {
         this.libraryLoading = true;
         this.libraryStatus = "";
         l1i1iil111il1l1l.on23("LIBRARY", var1, 25)
            .whenComplete(
               (var2xx, var3x) -> minecraftClient3.execute(
                  () -> {
                     this.libraryLoading = false;
                     this.lastLibraryRefreshAt = System.currentTimeMillis();
                     if (var3x == null && var2xx != null) {
                        if (!var2) {
                           this.libraryCloudElements.clear();
                        }

                        for (CloudConfigDetailsDto l1i1li1i11_lill1il1lll111l11l1i1lllli : var2xx.configs()) {
                           if (l1i1li1i11_lill1il1lll111l11l1i1lllli != null && l1i1li1i11_lill1il1lll111l11l1i1lllli.HudHotbarPanel() != null) {
                              GuiCloudConfigElement guicloudconfigelement = new GuiCloudConfigElement(
                                 l1i1li1i11_lill1il1lll111l11l1i1lllli, this::loadCloudConfig, this::toggleLike
                              );
                              if (this.isOwnedByViewer(l1i1li1i11_lill1il1lll111l11l1i1lllli)) {
                                 guicloudconfigelement.setEditAction(this::editCloudConfig);
                                 guicloudconfigelement.setDeleteAction(this::deleteCloudConfig);
                              }

                              this.libraryCloudElements.add(guicloudconfigelement);
                           }
                        }

                        this.sortCloudElements(this.libraryCloudElements);
                        this.libraryHasMore = var2xx.hasMore();
                        this.libraryNextOffset = var2xx.HudElementMessage();
                     } else {
                        this.libraryStatus = failureMessage(var3x, "Unable to load cloud library");
                     }
                  }
               )
            );
      }
   }

   public void requestPublicPage(int var1, boolean var2) {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l == null || !l1i1iil111il1l1l.isConnected()) {
         this.publicStatus = "Cloud is unavailable";
         this.publicLoading = false;
         this.lastCatalogRefreshAt = System.currentTimeMillis();
      } else if (!this.publicLoading) {
         this.publicLoading = true;
         this.publicStatus = "";
         l1i1iil111il1l1l.on23("PUBLIC", var1, 25)
            .whenComplete(
               (var2xx, var3x) -> minecraftClient3.execute(
                  () -> {
                     this.publicLoading = false;
                     this.lastCatalogRefreshAt = System.currentTimeMillis();
                     if (var3x == null && var2xx != null) {
                        if (!var2) {
                           this.publicElements.clear();
                        }

                        for (CloudConfigDetailsDto l1i1li1i11_lill1il1lll111l11l1i1lllli : var2xx.configs()) {
                           if (l1i1li1i11_lill1il1lll111l11l1i1lllli != null && l1i1li1i11_lill1il1lll111l11l1i1lllli.HudHotbarPanel() != null) {
                              GuiCloudConfigElement guicloudconfigelement = new GuiCloudConfigElement(
                                 l1i1li1i11_lill1il1lll111l11l1i1lllli, this::loadCloudConfig, this::toggleLike
                              );
                              if (this.isOwnedByViewer(l1i1li1i11_lill1il1lll111l11l1i1lllli)) {
                                 guicloudconfigelement.setEditAction(this::editCloudConfig);
                                 guicloudconfigelement.setDeleteAction(this::deleteCloudConfig);
                              }

                              this.publicElements.add(guicloudconfigelement);
                           }
                        }

                        this.sortCloudElements(this.publicElements);
                        this.publicHasMore = var2xx.hasMore();
                        this.publicNextOffset = var2xx.HudElementMessage();
                     } else {
                        this.publicStatus = failureMessage(var3x, "Unable to load Public Hub");
                     }
                  }
               )
            );
      }
   }

   public void loadCloudConfig(GuiCloudConfigElement var1, PollMode var2) {
      String s = this.cloudConfigCacheKey(var1);
      byte[] abyte = this.cloudConfigCache.get(s);
      if (abyte != null) {
         this.applyCloudConfig(var1, abyte, var2);
      } else {
         CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
         if (l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected()) {
            l1i1iil111il1l1l.on23(var1.getEntry().HudHotbarPanel().PermissionListCodec()).whenComplete((var4x, var5x) -> minecraftClient3.execute(() -> {
               if (var5x == null && var4x != null && var4x.length != 0) {
                  this.cloudConfigCache.put(s, var4x);
                  this.applyCloudConfig(var1, var4x, var2);
               } else {
                  var1.setDownloading(false);
                  var1.setActionLabel("Retry");
                  this.setCatalogStatus(var1, failureMessage(var5x, "Download failed"));
               }
            }));
         } else {
            var1.setDownloading(false);
            var1.setActionLabel("Retry");
            this.setCatalogStatus(var1, "Cloud is unavailable");
         }
      }
   }

   public void applyCloudConfig(GuiCloudConfigElement var1, byte[] var2, PollMode var3) {
      boolean flag = ZenithClient.on23().TradeGuardService().on23(var2, var3);
      var1.setDownloading(false);
      var1.setActionLabel(flag ? "Loaded" : "Retry");
      this.setCatalogStatus(var1, flag ? "Cloud config loaded" : "Could not load cloud config");
   }

   public String cloudConfigCacheKey(GuiCloudConfigElement var1) {
      String s = var1.getEntry().HudHotbarPanel().RotationSnapStrategy();
      return s != null && !s.isBlank() ? s : var1.getEntry().HudHotbarPanel().PermissionListCodec().toString();
   }

   public void toggleLike(GuiCloudConfigElement var1) {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected()) {
         l1i1iil111il1l1l.Easing(var1.getEntry().HudHotbarPanel().PermissionListCodec()).whenComplete((var2x, var3) -> minecraftClient3.execute(() -> {
            if (var3 == null && var2x != null) {
               var1.applyLikeResult(var2x.HudArmorPanel(), var2x.HudSelectedItemPanel());
               this.sortCloudElements(this.libraryCloudElements);
               this.sortCloudElements(this.publicElements);
            } else {
               var1.setLiking(false);
               this.setCatalogStatus(var1, failureMessage(var3, "Like failed"));
            }
         }));
      } else {
         var1.setLiking(false);
         this.setCatalogStatus(var1, "Cloud is unavailable");
      }
   }

   public void sortCloudElements(List<GuiCloudConfigElement> var1) {
      var1.sort(
         Comparator.comparingLong(GuiCloudConfigElement::getLikeCount)
            .reversed()
            .thenComparing(Comparator.<GuiCloudConfigElement>comparingLong(var0 -> var0.getEntry().HudHotbarPanel().HudTargetPanel()).reversed())
            .thenComparing(GuiCloudConfigElement::getName, String.CASE_INSENSITIVE_ORDER)
      );
   }

   public void deleteCloudConfig(GuiCloudConfigElement var1) {
      CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
      if (l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected()) {
         l1i1iil111il1l1l.ItemRegistry(var1.getEntry().HudHotbarPanel().PermissionListCodec()).whenComplete((var2x, var3) -> minecraftClient3.execute(() -> {
            if (var3 == null && var2x != null && var2x.HudMediaPanel()) {
               this.libraryCloudElements.remove(var1);
               this.publicElements.remove(var1);
               this.libraryStatus = "Config deleted";
               this.publicStatus = "Config deleted";
               this.refreshLibraryCatalog(true);
               this.refreshPublicCatalog(true);
            } else {
               var1.setDeleting(false);
               this.setCatalogStatus(var1, failureMessage(var3, "Delete failed"));
            }
         }));
      } else {
         var1.setDeleting(false);
         this.setCatalogStatus(var1, "Cloud is unavailable");
      }
   }

   public void setCatalogStatus(GuiCloudConfigElement var1, String var2) {
      if (this.libraryCloudElements.contains(var1)) {
         this.libraryStatus = var2;
      }

      if (this.publicElements.contains(var1)) {
         this.publicStatus = var2;
      }
   }

   public String saveDownloadedConfig(String var1, byte[] var2) throws Exception {
      if (var2 != null && var2.length != 0) {
         String s = sanitizeConfigName(var1);
         if (s.isBlank()) {
            s = "cloud_config";
         }

         Path path = CloudPoller.file7.toPath();
         Files.createDirectories(path);
         String s1 = s;
         int i = 2;

         Path path1;
         for (path1 = path.resolve(s + "." + "Zenith".toLowerCase(Locale.ROOT));
            Files.exists(path1);
            path1 = path.resolve(s1 + "." + "Zenith".toLowerCase(Locale.ROOT))
         ) {
            s1 = s + "_" + i++;
         }

         Files.write(path1, var2, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
         return s1;
      } else {
         throw new IllegalArgumentException("Downloaded config is empty");
      }
   }

   public void ensureCreateControls() {
      if (this.createNameBox == null) {
         this.createNameBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "Enter config name...", 60.0F);
         this.createNameBox.on23(SearchBox.MatchMode.val129);
         this.createNameBox.on23(SearchBox.SearchScope.val298);
         this.createNameBox.EventItemRenderHook(28);
         this.createDescriptionBox = new SearchBox(
            new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.5F), "Briefly describe your configuration features and keybinds...", 60.0F
         );
         this.createDescriptionBox.EventItemRenderHook(512);
         this.createDescriptionBox.DataChangedEvent(true);
         float f = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
         this.includeVisualsSetting = new GuiBooleanSetting(new BooleanSetting("Visuals", true), f);
         this.includeOtherSetting = new GuiBooleanSetting(new BooleanSetting("Other", true), f);
         this.includeBindsSetting = new GuiBooleanSetting(new BooleanSetting("Binds", true), f);
      }
   }

   public String serverTag() {
      return "Universal / All".equals(this.createServerTag) ? "unknown" : this.createServerTag;
   }

   public String description() {
      String s = this.createDescriptionBox.getText();
      return s != null && !s.isBlank() ? s.trim() : null;
   }

   public static String sanitizeConfigName(String var0) {
      if (var0 == null) {
         return "";
      }

      String s = var0.trim().replace("." + "Zenith".toLowerCase(Locale.ROOT), "");
      s = s.replaceAll("[^A-Za-z0-9_-]", "_");
      if (s.length() > 28) {
         s = s.substring(0, 28);
      }

      return s;
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

   public static String failureMessage(Throwable var0, String var1) {
      Throwable throwable = var0;

      while (throwable != null && throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      return throwable != null && throwable.getMessage() != null && !throwable.getMessage().isBlank() ? throwable.getMessage() : var1;
   }
}
