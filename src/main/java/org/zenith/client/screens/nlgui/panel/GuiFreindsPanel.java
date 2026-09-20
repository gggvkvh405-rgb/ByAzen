package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.FriendSkinResolver;
import org.zenith.client.screens.nlgui.elements.GuiCloudFriendElement;
import org.zenith.client.screens.nlgui.elements.GuiFriendRequestElement;
import org.zenith.client.screens.nlgui.elements.GuiFriendRowElement;
import org.zenith.client.screens.nlgui.elements.GuiLocalFriendElement;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiStringSetting;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.CloudApiClient;
import org.zenith.core.CloudUserProfile;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ModuleSnapshotDto;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
import org.zenith.setting.TextSetting;
import org.zenith.setting.TextSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiFreindsPanel extends ElementPanel {
   public static final float HEADER_HEIGHT = 23.0F;
   public static float ITEM_GAP;
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public static final float POPUP_WIDTH = 128.0F;
   public static final float POPUP_HEADER_HEIGHT = 26.0F;
   public final UiAnimation tabSwitchAnimation = new UiAnimation(220L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation headerPopupAnimation = new UiAnimation(220L, 0.0F, Easing.StopUsingItemEvent);
   public final List<GuiFriendRowElement> friendElements = new ArrayList<>();
   public final List<GuiFriendRequestElement> requestElements = new ArrayList<>();
   public final List<GuiFreindsPanel_RequestActionBounds> requestActionBounds = new ArrayList<>();
   public final ModeSetting addFriendModeSetting;
   public final TextSetting addFriendTextSetting;
   public final GuiModeSetting guiAddFriendMode;
   public final GuiStringSetting guiAddFriendText;
   public final UiAnimation submitHoverAnimation = new UiAnimation(220L, 0.0F, Easing.CloseScreenEvent);
   public CornerRadiusF allFriendsTabBounds;
   public CornerRadiusF requestsTabBounds;
   public CornerRadiusF scissorBounds;
   public CornerRadiusF headerAddButtonBounds;
   public CornerRadiusF headerPopupBounds;
   public CornerRadiusF headerPopupCloseBounds;
   public CornerRadiusF headerSubmitBounds;
   public float rightDrawerProgress = 0.0F;
   public float scroll = 0.0F;
   public float scrollTarget = 0.0F;
   public boolean headerPopupExpanded = false;
   public GuiFreindsPanel_HeaderTab activeTab = GuiFreindsPanel_HeaderTab.ALL_FREINDS;

   @Override
   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f = var5 + GuiStyle.PADDING.intValue() * 3.0F;
         float f1 = var6 + (23.0F - font.height()) / 2.0F;
         String s = "8";
         var1.drawText(font1, s, f, var6 + (23.0F - font1.height()) / 2.0F - 0.2F, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var4));
         var1.drawText(font, "Friends", f + font1.width(s) + GuiStyle.PADDING.intValue(), f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(var4));
         Font font2 = Fonts.NEW_MEDIUM.getFont(4.8F);
         String s1 = "All Freinds";
         String s2 = "Requests";
         float f2 = GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = font2.width(s2);
         float f5 = font2.width(s1);
         float f6 = var5 + var7 - f2 - f4;
         float f7 = f6 - f3;
         float f8 = f7 - f3 - f5;
         float f9 = var6 + (23.0F - font2.height()) / 2.0F;
         this.allFriendsTabBounds = new CornerRadiusF(f8 - 2.0F, var6 + 1.0F, f5 + 4.0F, 21.0F);
         this.requestsTabBounds = new CornerRadiusF(f6 - 2.0F, var6 + 1.0F, f4 + 4.0F, 21.0F);
         var1.drawText(
            font2,
            s1,
            f8,
            f9,
            (this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS ? zenithstyle.getTextEnable().getColor() : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var4)
         );
         var1.drawRect(f7, f9, 0.5F, 6.0F, zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var4));
         var1.drawText(
            font2,
            s2,
            f6,
            f9,
            (this.activeTab == GuiFreindsPanel_HeaderTab.REQUESTS ? zenithstyle.getTextEnable().getColor() : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var4)
         );
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (var5 != MenuScreenId.call004) {
         return false;
      }

      if (this.allFriendsTabBounds != null && this.allFriendsTabBounds.PotionItemBuilder(var1, var3)) {
         this.activeTab = GuiFreindsPanel_HeaderTab.ALL_FREINDS;
         this.scrollTarget = 0.0F;
         return true;
      }

      if (this.requestsTabBounds != null && this.requestsTabBounds.PotionItemBuilder(var1, var3)) {
         this.activeTab = GuiFreindsPanel_HeaderTab.REQUESTS;
         this.scrollTarget = 0.0F;
         return true;
      }

      if (this.activeTab == GuiFreindsPanel_HeaderTab.REQUESTS) {
         CloudApiClient l1i1iil111il1l1lx = this.getCloudClient();
         if (l1i1iil111il1l1lx == null) {
            return false;
         }

         for (GuiFreindsPanel_RequestActionBounds guifreindspanel_requestactionbounds : this.requestActionBounds) {
            if (guifreindspanel_requestactionbounds.acceptBounds.PotionItemBuilder(var1, var3)) {
               l1i1iil111il1l1lx.NbtEditor(guifreindspanel_requestactionbounds.uid);
               return true;
            }

            if (guifreindspanel_requestactionbounds.declineBounds.PotionItemBuilder(var1, var3)) {
               l1i1iil111il1l1lx.PotionItemBuilder(guifreindspanel_requestactionbounds.uid);
               return true;
            }
         }
      }

      if (this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS) {
         for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
            CornerRadiusF l11liliill1iii1 = guifriendrowelement.getRemoveBounds();
            if (l11liliill1iii1 != null && l11liliill1iii1.PotionItemBuilder(var1, var3)) {
               if (guifriendrowelement.isCloud()) {
                  CloudApiClient l1i1iil111il1l1l = this.getCloudClient();
                  if (l1i1iil111il1l1l != null) {
                     l1i1iil111il1l1l.ItemServiceBase(guifriendrowelement.getCloudUid());
                  }
               } else {
                  ZenithClient.on23().MediaTrackInfo().ItemServiceBase(guifriendrowelement.getLocalName());
                  ZenithClient.on23().MediaTrackInfo().save();
               }

               return true;
            }
         }

         for (GuiFriendRowElement guifriendrowelement1 : this.friendElements) {
            if (guifriendrowelement1.getBounds() != null && guifriendrowelement1.getBounds().PotionItemBuilder(var1, var3)) {
               if (guifriendrowelement1 instanceof GuiCloudFriendElement guicloudfriendelement
                  && guicloudfriendelement.onMousePriorityClicked(var1, var3, var5)) {
                  return true;
               }

               if (guifriendrowelement1.onMouseClicked(var1, var3, var5)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void renderHeaderPopup(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = 128.0F;
         float f1 = f - GuiStyle.PADDING.intValue() * 4.0F;
         float f2 = GuiStyle.PADDING.intValue()
            + this.guiAddFriendMode.getHeight()
            + GuiStyle.PADDING.intValue()
            + this.guiAddFriendText.getHeight()
            + GuiStyle.PADDING.intValue() * 2.0F
            + 14.0F
            + GuiStyle.PADDING.intValue();
         float f3 = 26.0F + f2;
         float f4 = var5 - (f + GuiStyle.PADDING.intValue()) * (1.0F - var2);
         this.headerPopupBounds = new CornerRadiusF(f4, var6, f, f3);
         var1.enableScissor(var5 - GuiStyle.PADDING.intValue() * 3.0F, var6, var5 + f + GuiStyle.PADDING.intValue() * 4.0F, var6 + f3);
         if (ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F) {
            ShapeRenderer.on23(
               var1.getMatrices(),
               f4,
               var6,
               f,
               f3,
               ZenithClient.on23().NbtEditor().getBlurPower(),
               CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
               ArgbColor.var11934.SprintStateEvent(var2),
               true,
               false
            );
         }

         var1.drawRoundedRect(
            f4, var6, f, 26.0F, CornerRadius.Event29(GuiStyle.ROUND.intValue()), zenithstyle.getRightBackground().getColor().SprintStateEvent(var2)
         );
         var1.drawRoundedRect(
            f4,
            var6 + 26.0F,
            f,
            f3 - 26.0F,
            CornerRadius.RotationUpdateStartEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var2)
         );
         Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(4.5F);
         var1.drawText(
            font1,
            "{",
            f4 + GuiStyle.PADDING.intValue() * 2.0F,
            var6 + (26.0F - font1.height()) / 2.0F,
            zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var2)
         );
         var1.drawText(
            font,
            "Add friend",
            f4 + GuiStyle.PADDING.intValue() * 2.0F + font1.width("a") + GuiStyle.PADDING.intValue() / 2.0F,
            var6 + (26.0F - font.height()) / 2.0F,
            zenithstyle.getTextEnable().getColor().SprintStateEvent(var2)
         );
         float f5 = f4 + f - font1.width("2") - GuiStyle.PADDING.intValue() * 2.0F;
         float f6 = var6 + (26.0F - font1.height()) / 2.0F;
         this.headerPopupCloseBounds = new CornerRadiusF(f5, f6, font1.width("2"), font1.height());
         var1.drawText(font1, "2", f5, f6, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var2));
         float f7 = f4 + GuiStyle.PADDING.intValue() * 2.0F;
         float f8 = var6 + 26.0F + GuiStyle.PADDING.intValue();
         this.guiAddFriendMode.render(var1, var3, var4, f7, f8, var2);
         float f9 = f8 + this.guiAddFriendMode.getAnimHeight() + GuiStyle.PADDING.intValue();
         this.guiAddFriendText.render(var1, var3, var4, f7, f9, var2);
         float f10 = f9 + this.guiAddFriendText.getAnimHeight() + GuiStyle.PADDING.intValue() * 2.0F;
         this.headerSubmitBounds = new CornerRadiusF(f7, f10, f1, 14.0F);
         float f11 = this.submitHoverAnimation
            .on23(this.headerSubmitBounds.PotionItemBuilder(var3, var4) && !this.guiAddFriendMode.contains(var3, var4) ? 1.0F : 0.0F);
         var1.drawRoundedRect(
            this.headerSubmitBounds.x(),
            this.headerSubmitBounds.y(),
            this.headerSubmitBounds.width(),
            this.headerSubmitBounds.height(),
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f11 * 0.15F).SprintStateEvent(var2)
         );
         var1.drawRoundedBorder(
            this.headerSubmitBounds.x(),
            this.headerSubmitBounds.y(),
            this.headerSubmitBounds.width(),
            this.headerSubmitBounds.height(),
            0.1F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue() / 2.0F),
            zenithstyle.getFieldBorder().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f11 * 0.35F).SprintStateEvent(var2)
         );
         Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
         Font font3 = Fonts.NEW_MEDIUM.getFont(5.3F);
         String s = this.addFriendModeSetting.is(0) ? "Add cloud" : "Add local";
         float f12 = font2.width("{") + GuiStyle.PADDING.intValue() / 2.0F + font3.width(s);
         float f13 = this.headerSubmitBounds.x() + (this.headerSubmitBounds.width() - f12) / 2.0F;
         float f14 = this.headerSubmitBounds.y() + (this.headerSubmitBounds.height() - font2.height()) / 2.0F + 0.28F;
         float f15 = this.headerSubmitBounds.y() + (this.headerSubmitBounds.height() - font3.height()) / 2.0F;
         var1.drawText(
            font2, "{", f13, f14, zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f11).SprintStateEvent(var2)
         );
         var1.drawText(
            font3,
            s,
            f13 + font2.width("{") + GuiStyle.PADDING.intValue() / 2.0F,
            f15,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f11).SprintStateEvent(var2)
         );
         var1.disableScissor();
      }
   }

   public void syncFriendElements() {
      for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
         guifriendrowelement.beginSync();
      }

      List<CloudUserProfile> list = ZenithClient.on23().MediaTrackInfo().ShaderHand();
      float f = 0.0F;

      for (CloudUserProfile li1ilil1i11ii111l11l : list) {
         String s = "cloud:" + li1ilil1i11ii111l11l.id();
         Object object = this.findFriendRowByKey(s);
         if (object == null) {
            object = new GuiCloudFriendElement(li1ilil1i11ii111l11l);
            this.friendElements.add((GuiFriendRowElement)object);
         }

         ((GuiFriendRowElement)object).markPresent(f++);
      }

      for (GuiFreindsPanel_GuiLocalFreind guifreindspanel_guilocalfreind : this.getLocalFreinds()) {
         String s1 = "local:" + guifreindspanel_guilocalfreind.name;
         Object object1 = this.findFriendRowByKey(s1);
         if (object1 == null) {
            object1 = new GuiLocalFriendElement(guifreindspanel_guilocalfreind.name);
            this.friendElements.add((GuiFriendRowElement)object1);
         }

         if (object1 instanceof GuiLocalFriendElement guilocalfriendelement) {
            guilocalfriendelement.syncLocal(f++);
         }
      }

      ArrayList arraylist = new ArrayList();

      for (GuiFriendRowElement guifriendrowelement1 : this.friendElements) {
         if (guifriendrowelement1.shouldRemoveAfterSync()) {
            arraylist.add(guifriendrowelement1);
         }
      }

      this.friendElements.removeAll(arraylist);
   }

   public void submitAddFriend() {
      String s = this.addFriendTextSetting.getValue() == null ? "" : this.addFriendTextSetting.getValue().trim();
      if (!s.isEmpty()) {
         if (this.addFriendModeSetting.is(0)) {
            CloudApiClient l1i1iil111il1l1l = this.getCloudClient();
            if (l1i1iil111il1l1l != null) {
               l1i1iil111il1l1l.SimpleItemBuilder(s);
            }
         } else {
            ZenithClient.on23().MediaTrackInfo().add(s);
            ZenithClient.on23().MediaTrackInfo().save();
         }

         this.addFriendTextSetting.setValue("");
      }
   }

   public float getAllFriendsContentHeight() {
      float f = 0.0F;
      int i = 0;

      for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
         float f1 = guifriendrowelement.getVisibleAnimation().CancellableEvent();
         if (!(f1 <= 0.02F) || guifriendrowelement.isTargetVisible()) {
            float f2 = guifriendrowelement.isTargetVisible() ? 1.0F : f1;
            f += (guifriendrowelement.getHeight() + ITEM_GAP) * f2;
            i++;
         }
      }

      return i > 0 ? f - ITEM_GAP : 0.0F;
   }

   public float getRequestsContentHeight() {
      float f = 0.0F;
      int i = 0;

      for (GuiFriendRequestElement guifriendrequestelement : this.requestElements) {
         float f1 = guifriendrequestelement.getVisibleAnimation().CancellableEvent();
         if (!(f1 <= 0.02F) || guifriendrequestelement.isTargetVisible()) {
            float f2 = guifriendrequestelement.isTargetVisible() ? 1.0F : f1;
            f += (guifriendrequestelement.getHeight() + ITEM_GAP) * f2;
            i++;
         }
      }

      return i > 0 ? f - ITEM_GAP : 0.0F;
   }

   @Override
   public boolean isRender() {
      return this.isRightDrawerOpen();
   }

   @Override
   public void renderRightPanel(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      this.rightDrawerProgress = this.headerPopupAnimation.on23(this.headerPopupExpanded ? 1.0F : 0.0F) * var4 * var7;
      if (!(this.rightDrawerProgress <= 0.01F)) {
         this.renderHeaderPopup(var1, this.rightDrawerProgress, var2, var3, var5, var6);
      }
   }

   @Override
   public void closeRightDrawer() {
      this.headerPopupExpanded = false;
   }

   @Override
   public CornerRadiusF getRightPanelBlurBounds(float var1, float var2, float var3, float var4) {
      float f = this.getRightPanelBlurProgress(var3, var4);
      if (f <= 0.01F) {
         return null;
      }

      float f1 = 128.0F;
      float f2 = GuiStyle.PADDING.intValue()
         + this.guiAddFriendMode.getHeight()
         + GuiStyle.PADDING.intValue()
         + this.guiAddFriendText.getHeight()
         + GuiStyle.PADDING.intValue() * 2.0F
         + 14.0F
         + GuiStyle.PADDING.intValue();
      float f3 = 26.0F + f2;
      float f4 = var1 - (f1 + GuiStyle.PADDING.intValue()) * (1.0F - f);
      return new CornerRadiusF(f4, var2, f1, f3);
   }

   @Override
   public float getRightPanelBlurProgress(float var1, float var2) {
      return this.rightDrawerProgress;
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.requestActionBounds.clear();
         this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 295.5F - GuiStyle.PADDING.intValue() * 2.0F);
         float f = this.tabSwitchAnimation.on23(this.activeTab == GuiFreindsPanel_HeaderTab.REQUESTS ? 1.0F : 0.0F);
         float f1 = f < 0.5F ? this.getAllFriendsContentHeight() : this.getRequestsContentHeight();
         this.clampScroll(f1 + GuiStyle.PADDING.intValue(), this.scissorBounds.height());
         this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
         var1.enableScissor(var5, var6, var5 + this.scissorBounds.width(), var6 + this.scissorBounds.height());
         float f2 = (1.0F - f) * var4;
         float f3 = f * var4;
         if (f2 > 0.01F) {
            this.renderAllFriends(var1, f2, var2, var3, var5, var6 - 14.0F * f, zenithstyle);
         }

         if (f3 > 0.01F) {
            this.renderRequests(var1, f3, var5, var6 + 14.0F * (1.0F - f), zenithstyle);
         }

         var1.disableScissor();
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = this.tabSwitchAnimation.CancellableEvent();
      float f1 = (1.0F - f) * var4;
      if (f1 > 0.01F) {
         this.renderAllFriendsPriority(var1, f1, var2, var3, var5, var6 - 14.0F * f);
      }

      if (this.rightDrawerProgress > 0.01F && this.headerPopupBounds != null) {
         float f2 = this.headerPopupBounds.x() + GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = this.headerPopupBounds.y() + 26.0F + GuiStyle.PADDING.intValue();
         this.guiAddFriendMode.renderPriority(var1, var2, var3, f2, f3, this.rightDrawerProgress, 1.0F);
      }
   }

   @Override
   public float getButtonWidth() {
      return 23.0F + GuiStyle.PADDING.intValue();
   }

   @Override
   public void renderHeaderButtons(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = 23.0F;
         float f1 = var5 + var7 - f;
         this.headerAddButtonBounds = new CornerRadiusF(f1, var6, f, f);
         float f2 = this.rightDrawerProgress;
         var1.drawRoundedRect(
            f1,
            var6,
            f,
            f,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getPanelLeftBackground().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f2).SprintStateEvent(var4 * var8)
         );
         Font font = Fonts.NEW_ICONS.getFont(5.0F);
         var1.drawText(
            font,
            "{",
            f1 + (f - font.width("{")) / 2.0F,
            var6 + (f - font.height()) / 2.0F,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), f2).SprintStateEvent(var4 * var8)
         );
      }
   }

   @Override
   public boolean onHeaderButtonsClicked(double var1, double var3, MenuScreenId var5) {
      if (this.headerPopupExpanded && this.guiAddFriendMode.onMousePriorityClicked(var1, var3, var5)) {
         return true;
      } else if (var5 != MenuScreenId.call004) {
         return false;
      } else if (this.headerAddButtonBounds != null && this.headerAddButtonBounds.PotionItemBuilder(var1, var3)) {
         this.headerPopupExpanded = !this.headerPopupExpanded;
         return true;
      } else if (!this.headerPopupExpanded) {
         return false;
      } else if (this.headerPopupBounds != null && !this.headerPopupBounds.PotionItemBuilder(var1, var3)) {
         this.headerPopupExpanded = false;
         return false;
      } else if (this.headerPopupCloseBounds != null && this.headerPopupCloseBounds.on23(var1, var3, 2.0F)) {
         this.headerPopupExpanded = false;
         return true;
      } else if (this.guiAddFriendMode.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.guiAddFriendText.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.headerSubmitBounds != null && this.headerSubmitBounds.PotionItemBuilder(var1, var3)) {
         this.submitAddFriend();
         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.headerPopupExpanded && this.guiAddFriendMode.keyPressed(var1, var2, var3)) {
         return true;
      }

      if (this.headerPopupExpanded && this.guiAddFriendText.keyPressed(var1, var2, var3)) {
         return true;
      }

      if (this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS) {
         for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
            if (guifriendrowelement instanceof GuiCloudFriendElement guicloudfriendelement
               && guicloudfriendelement.hasSettings()
               && guicloudfriendelement.keyPressed(var1, var2, var3)) {
               return true;
            }
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.headerPopupExpanded && this.guiAddFriendMode.charTyped(var1, var2)) {
         return true;
      }

      if (this.headerPopupExpanded && this.guiAddFriendText.charTyped(var1, var2)) {
         return true;
      }

      if (this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS) {
         for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
            if (guifriendrowelement instanceof GuiCloudFriendElement guicloudfriendelement
               && guicloudfriendelement.hasSettings()
               && guicloudfriendelement.charTyped(var1, var2)) {
               return true;
            }
         }
      }

      return super.charTyped(var1, var2);
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS) {
         for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
            if (guifriendrowelement instanceof GuiCloudFriendElement guicloudfriendelement) {
               guicloudfriendelement.onMouseReleased(var1, var3, var5);
            }
         }
      }

      return super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.headerPopupExpanded && this.guiAddFriendMode.onMousePriorityScroll(var1, var3, var5, var7)) {
         return true;
      }

      if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3) && this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS) {
         for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
            if (guifriendrowelement.getBounds() != null
               && guifriendrowelement.getBounds().PotionItemBuilder(var1, var3)
               && guifriendrowelement instanceof GuiCloudFriendElement guicloudfriendelement
               && guicloudfriendelement.hasSettings()
               && guicloudfriendelement.onMousePriorityScroll(var1, var3, var5, var7)) {
               return true;
            }
         }
      }

      if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3)) {
         float f = this.scissorBounds.height();
         float f1 = this.activeTab == GuiFreindsPanel_HeaderTab.ALL_FREINDS ? this.getAllFriendsContentHeight() : this.getRequestsContentHeight();
         if (f1 + GuiStyle.PADDING.intValue() <= f) {
            return false;
         }

         this.scrollTarget += (float)(var7 * 22.0);
         this.clampScroll(f1 + GuiStyle.PADDING.intValue(), f);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public List<? extends Element> getElements() {
      return List.of();
   }

   @Override
   public void close() {
      FriendSkinResolver.clearExternalCache();
      this.requestActionBounds.clear();
      this.scroll = 0.0F;
      this.scrollTarget = 0.0F;
      this.headerPopupExpanded = false;
      this.headerPopupAnimation.UiAnimation(0.0F);
   }

   @Override
   public boolean isRightDrawerOpen() {
      return this.headerPopupExpanded || this.headerPopupAnimation.CancellableEvent() > 0.01F;
   }

   public GuiFreindsPanel() {
      this.addFriendModeSetting = new ModeSetting("gui.friendspanel.mode", "", "gui.friendspanel.mode.cloud", "gui.friendspanel.mode.local");
      this.addFriendTextSetting = new TextSetting(
         "gui.friendspanel.target",
         "",
         "",
         "uid_or_name",
         TextSetting.Validator.on23(40, var0 -> var0.chars().allMatch(var0x -> Character.isLetterOrDigit(var0x) || var0x == 95 || var0x == 45 || var0x == 46))
      );
      float f = 128.0F - GuiStyle.PADDING.intValue() * 4.0F;
      this.guiAddFriendMode = new GuiModeSetting(this.addFriendModeSetting, f);
      this.guiAddFriendText = new GuiStringSetting(this.addFriendTextSetting, f);
   }

   @Override
   public void tick() {
      this.syncFriendElements();
      this.syncRequestElements();
   }

   public void clampScroll(float var1, float var2) {
      float f = var1 > var2 ? var2 - var1 : 0.0F;
      this.scrollTarget = Math.max(f, Math.min(0.0F, this.scrollTarget));
      if (this.scroll > 0.0F) {
         this.scroll = 0.0F;
      }
   }

   public List<GuiFreindsPanel_GuiLocalFreind> getLocalFreinds() {
      Collection<String> collection = ZenithClient.on23().MediaTrackInfo().getItems();
      if (collection != null && !collection.isEmpty()) {
         List<GuiFreindsPanel_GuiLocalFreind> arraylist = new ArrayList<>(collection.size());

         for (String s : collection) {
            if (s != null && !s.isBlank()) {
               arraylist.add(new GuiFreindsPanel_GuiLocalFreind(s.trim()));
            }
         }

         arraylist.sort(Comparator.comparing(var0 -> var0.name.toLowerCase(Locale.ROOT)));
         return arraylist;
      } else {
         return List.of();
      }
   }

   public CloudApiClient getCloudClient() {
      return ZenithClient.on23().getCloudClient();
   }

   public void syncRequestElements() {
      for (GuiFriendRequestElement guifriendrequestelement : this.requestElements) {
         guifriendrequestelement.beginSync();
      }

      List<ModuleSnapshotDto> snapshots = this.getCloudClient() != null ? this.getCloudClient().StopUsingItemEvent() : List.of();
      for (ModuleSnapshotDto l1iiiil1lii1iliiill1 : snapshots) {
         String s = "request:" + l1iiiil1lii1iliiill1.Event37();
         GuiFriendRequestElement guifriendrequestelement1 = this.findRequestRowByKey(s);
         if (guifriendrequestelement1 == null) {
            guifriendrequestelement1 = new GuiFriendRequestElement(l1iiiil1lii1iliiill1.Event37(), l1iiiil1lii1iliiill1.EventUpdateHealth());
            this.requestElements.add(guifriendrequestelement1);
         }

         guifriendrequestelement1.syncFromRequest(l1iiiil1lii1iliiill1.EventUpdateHealth());
      }

      ArrayList arraylist = new ArrayList();

      for (GuiFriendRequestElement guifriendrequestelement2 : this.requestElements) {
         if (guifriendrequestelement2.shouldRemoveAfterSync()) {
            arraylist.add(guifriendrequestelement2);
         }
      }

      this.requestElements.removeAll(arraylist);
   }

   public GuiFriendRowElement findFriendRowByKey(String var1) {
      for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
         if (guifriendrowelement.key().equals(var1)) {
            return guifriendrowelement;
         }
      }

      return null;
   }

   public GuiFriendRequestElement findRequestRowByKey(String var1) {
      for (GuiFriendRequestElement guifriendrequestelement : this.requestElements) {
         if (guifriendrequestelement.key().equals(var1)) {
            return guifriendrequestelement;
         }
      }

      return null;
   }

   public void renderAllFriends(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      float f = var5 + GuiStyle.PADDING.intValue();
      float f1 = var6 + this.scroll;
      float f2 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
      this.friendElements.sort((var0, var1x) -> Float.compare(var0.getOrder(), var1x.getOrder()));

      for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
         float f3 = guifriendrowelement.render(var1, var3, var4, f, f1, f2, var2, var7);
         if (!(f3 <= 0.02F)) {
            f1 += (guifriendrowelement.getHeight() + ITEM_GAP) * guifriendrowelement.getVisibleAnimation().CancellableEvent();
         }
      }
   }

   public void renderAllFriendsPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = var5 + GuiStyle.PADDING.intValue();
      float f1 = var6 + this.scroll;
      this.friendElements.sort((var0, var1x) -> Float.compare(var0.getOrder(), var1x.getOrder()));

      for (GuiFriendRowElement guifriendrowelement : this.friendElements) {
         float f2 = guifriendrowelement.getVisibleAnimation().CancellableEvent();
         if (!(f2 <= 0.02F)) {
            if (guifriendrowelement instanceof GuiCloudFriendElement guicloudfriendelement && guicloudfriendelement.hasSettings()) {
               guicloudfriendelement.renderPriority(var1, var3, var4, f, f1, var2);
            }

            f1 += (guifriendrowelement.getHeight() + ITEM_GAP) * f2;
         }
      }
   }

   public void renderRequests(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
      float f = var3 + GuiStyle.PADDING.intValue();
      float f1 = var4 + this.scroll;
      float f2 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
      if (this.requestElements.isEmpty()) {
         var1.drawText(font, "No requests in this session", f + 4.0F, f1 + 6.0F, var5.getTextSecondary().getColor().SprintStateEvent(var2));
      } else {
         for (GuiFriendRequestElement guifriendrequestelement : this.requestElements) {
            float f3 = guifriendrequestelement.render(var1, f, f1, f2, var2, var5);
            if (!(f3 <= 0.02F)) {
               CornerRadiusF l11liliill1iii1x = guifriendrequestelement.getAcceptBounds();
               l11liliill1iii1x = guifriendrequestelement.getDeclineBounds();
               if (l11liliill1iii1x != null && l11liliill1iii1x != null) {
                  this.requestActionBounds.add(new GuiFreindsPanel_RequestActionBounds(guifriendrequestelement.getUid(), l11liliill1iii1x, l11liliill1iii1x));
               }

               f1 += (guifriendrequestelement.getHeight() + ITEM_GAP) * guifriendrequestelement.getVisibleAnimation().CancellableEvent();
            }
         }
      }
   }
}
