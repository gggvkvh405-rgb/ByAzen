package org.zenith.client.screens.nlgui;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.panel.ClientPanel;
import org.zenith.client.screens.nlgui.panel.CosmeticElementPanel;
import org.zenith.client.screens.nlgui.panel.GeneralPanel;
import org.zenith.client.screens.nlgui.panel.GuiConfigPanel;
import org.zenith.client.screens.nlgui.panel.GuiFreindsPanel;
import org.zenith.client.screens.nlgui.panel.GuiModulePanel;
import org.zenith.client.screens.nlgui.panel.InterfacePanel;
import org.zenith.client.screens.nlgui.panel.InterfacePanel_InterfaceCategory;
import org.zenith.client.screens.nlgui.panel.MiscPanel;
import org.zenith.client.screens.nlgui.panel.ProfilePanel;
import org.zenith.client.screens.nlgui.panel.ScriptsPanel;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.ClientSession;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.HudElement;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.module.render.Menu;
import org.zenith.render.RenderCommandQueue;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.BlurHudDrawContext;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class NLMenuScreen extends Screen {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final UiAnimation animationClose = new UiAnimation(300L, 0.0F, Easing.EventInjectHandleInputEvents);
   public GeneralPanel generalPanel;
   public ProfilePanel profilePanel;
   public ClientPanel clientPanel;
   public MiscPanel miscPanel;
   public GuiModulePanel guiModulePanel;
   public GuiFreindsPanel guiFreindsPanel;
   public InterfacePanel interfacePanel;
   public GuiConfigPanel guiConfigPanel;
   public CosmeticElementPanel cosmeticElementPanel;
   public ScriptsPanel scriptsPanel;
   public NLMenuScreen_ElementsType type = NLMenuScreen_ElementsType.CATEGORY;
   public boolean closing = false;
   public static final float widthPanel = 480.0F;
   public static final float heightPanel = 320.0F;
   public static final float leftWidthPanel = 104.0F;
   public static final float rightWidthPanel = 376.0F;
   public float x;
   public float y;
   public float rawX;
   public float rawY;
   public boolean init = false;
   public SearchBox searchField;
   public CornerRadiusF profileBounds;
   public final UiAnimation profileHoverAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public CornerRadiusF clientBounds;
   public CornerRadiusF dragBounds;
   public boolean draggingGui;
   public boolean restoreClientPanelAfterRightPanel = false;
   float alpha = 0.0F;
   public final UiAnimation setTypeAnimation = new UiAnimation(220L, 1.0F, Easing.CloseScreenEvent);
   public NLMenuScreen_ElementsType prevType;

   public void renderTop(HudDrawContext var1, int var2, int var3) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         RenderCommandQueue.map44();

         try {
            this.renderTopDeferred(BlurHudDrawContext.TextScanner(var1), var2, var3, zenithstyle);
         } finally {
            RenderCommandQueue.finish();
         }
      }
   }

   public void renderTopDeferred(HudDrawContext var1, int var2, int var3, ZenithStyle var4) {
      float f = GuiStyle.ROUND.intValue();
      this.animationClose.on23(Menu.menu.int468());
      this.setTypeAnimation.on23(Menu.menu.int469());
      float f1 = this.animationClose.on23(this.closing ? 0.0F : 1.0F);
      float f2 = minecraftClient3.getWindow().getScaledWidth() / 2.0F - 240.0F;
      float f3 = minecraftClient3.getWindow().getScaledHeight() / 1.9F - 160.0F;
      this.rawX = Menu.menu.float376() ? Menu.menu.VelocityChangeEvent(f2) : f2;
      this.rawY = Menu.menu.float376() ? Menu.menu.CrosshairTargetUpdateEvent(f3) : f3;
      this.x = this.clampRenderPosition(this.rawX, 480.0F, minecraftClient3.getWindow().getScaledWidth());
      this.y = this.clampRenderPosition(this.rawY, 320.0F, minecraftClient3.getWindow().getScaledHeight());
      float f4 = this.getGuiScale();
      float f5 = this.x + 240.0F;
      float f6 = this.y + 160.0F;
      var2 = (int)((var2 - f5) / f4 + f5);
      var3 = (int)((var3 - f6) / f4 + f6);
      var1.pushMatrix();
      var1.getMatrices().translate(f5, f6);
      var1.getMatrices().scale(f4, f4);
      var1.getMatrices().translate(-f5, -f6);
      if (this.profilePanel.isRender()) {
         this.profilePanel.render(var1, var2, var3, f1, this.x - 128.0F - GuiStyle.PADDING * 2, this.y + 320.0F - 190.0F);
      }

      float f7 = this.x + 480.0F + GuiStyle.PADDING * 2;
      float f8 = this.y;
      ElementPanel elementpanel = this.type.getPanelSupplier().get();
      ElementPanel elementpanel1 = this.prevType != null ? this.prevType.getPanelSupplier().get() : null;
      float f9 = this.setTypeAnimation.CancellableEvent();
      boolean flag = elementpanel.isRender() && f9 > 0.001F;
      boolean flag1 = elementpanel1 != null && elementpanel1.isRender() && 1.0F - f9 > 0.001F;
      this.updateClientPanelVisibilityForRightPanels(flag || flag1);
      if (this.clientPanel.isRender()) {
         this.clientPanel.render(var1, var2, var3, f1, f7, f8);
      }

      if (elementpanel1 != null && elementpanel1.isRender()) {
         elementpanel1.renderRightPanel(var1, var2, var3, f1 * (1.0F - f9), f7, f8, 1.0F - f9);
      }

      if (elementpanel.isRender()) {
         elementpanel.renderRightPanel(var1, var2, var3, f1 * f9, f7, f8, f9);
      }

      float f10 = this.getBlurPower();
      if (f10 != 0.0F) {
         ShapeRenderer.on23(
            var1.getMatrices(), this.x, this.y, 480.0F, 320.0F, f10, CornerRadius.MovementInputEvent(f), ArgbColor.var11934.SprintStateEvent(f1), true, false
         );
      }

      ArgbColor i11ii1llliilllii1i1 = var4.getLeftBackground().getColor().SprintStateEvent(f1);
      ArgbColor i11ii1llliilllii1i11 = var4.getRightBackground().getColor().SprintStateEvent(f1);
      ArgbColor i11ii1llliilllii1i12 = var4.getPanelLeftBackground().getColor().SprintStateEvent(f1);
      ArgbColor i11ii1llliilllii1i13 = var4.getPrimaryColor().getColor().SprintStateEvent(f1);
      ArgbColor i11ii1llliilllii1i14 = var4.getTextEnable().getColor().SprintStateEvent(f1);
      ArgbColor i11ii1llliilllii1i15 = var4.getTextTertiary().getColor().SprintStateEvent(f1);
      var1.drawRoundedRect(this.x, this.y, 104.0F, 320.0F, CornerRadius.BotTickEvent(f, f), i11ii1llliilllii1i1);
      var1.drawRoundedRect(this.x + 104.0F, this.y, 376.0F, 320.0F, CornerRadius.VelocityChangeEvent(f, f), i11ii1llliilllii1i11);
      float f11 = GuiStyle.PADDING.intValue();
      float f12 = 96.0F;
      float f13 = this.y + f11;
      float f14 = this.x + f11;
      float f15 = f13 + 23.0F + f11;
      float f16 = this.y + 320.0F - f11 - 30.0F;
      this.profileBounds = new CornerRadiusF(f14, f16, f12, 30.0F);
      float f17 = this.profileHoverAnimation.on23(this.profileBounds.PotionItemBuilder(var2, var3) ? 1.0F : 0.0F);
      var1.drawRoundedRectBatched(f14, f13, f12, 23.0F, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i12);
      var1.drawRoundedRectBatched(
         f14, f16, f12, 30.0F, CornerRadius.MovementInputEvent(f), i11ii1llliilllii1i12.Easing(var4.getPrimaryColor().getColor(), f17 * 0.15F)
      );
      var1.flushRoundedRects();
      String s = "5";
      Font font = Fonts.ICONS.getFont(6.0F);
      Font font1 = Fonts.NEW_MEDIUM.getFont(6.0F);
      float f18 = font.width(s);
      float f19 = font1.width("Zenith");
      float f20 = f18 + f11 + f19;
      float f21 = f14 + (f12 - f20) / 2.0F;
      var1.drawText(font, s, f21, f13 + (23.0F - font.height()) / 2.0F - 0.2F, i11ii1llliilllii1i13);
      var1.drawText(font1, "Zenith", f21 + f18 + f11, f13 + (23.0F - font1.height()) / 2.0F, i11ii1llliilllii1i14);
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.2F);
      font = Fonts.NEW_REGULAR.getFont(4.5F);
      float f25 = this.x + f11 * 3.0F;
      f18 = 14.0F;
      f19 = f16 + f11 * 2.0F;
      ShapeRenderer.on23(
         var1.getMatrices(),
         ZenithClient.on23("icons/avatar.png"),
         f25,
         f19,
         f18,
         f18,
         CornerRadius.MovementInputEvent(4.0F),
         ArgbColor.var11934.SprintStateEvent(f1)
      );
      f20 = f25 + f18 + f11;
      var1.drawText(font3, ClientSession.DISPLAY_CREDIT_FIRST_LINE, f20, f19 + 1.0F, i11ii1llliilllii1i14);
      var1.drawText(font, ClientSession.DISPLAY_CREDIT_SECOND_LINE, f20, f19 + f18 - font.height() - 1.0F, i11ii1llliilllii1i15);
      if (this.setTypeAnimation.isDone() && this.prevType != null) {
         this.prevType.getPanelSupplier().get().close();
         this.prevType = null;
      } else {
         this.setTypeAnimation.on23(1.0F);
      }

      float f23 = 241.0F;
      float f24 = this.x + 104.0F + GuiStyle.PADDING.intValue();
      float f26 = 23.0F;
      f18 = this.x + 480.0F - GuiStyle.PADDING.intValue() - f26;
      this.dragBounds = new CornerRadiusF(this.x, this.y, 480.0F, 23.0F);
      f19 = elementpanel.getButtonWidth();
      f20 = elementpanel1 != null ? elementpanel1.getButtonWidth() : 0.0F;
      f21 = f20 * (1.0F - f9) + f19 * f9;
      float f22 = f23 - f21;
      if (f22 < 60.0F) {
         f22 = 60.0F;
      }

      var1.drawRoundedRectBatched(f24, f13, f22, 23.0F, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()), i11ii1llliilllii1i12);
      this.clientBounds = new CornerRadiusF(f18, f13, f26, f26);
      var1.drawRoundedRectBatched(
         f18,
         f13,
         f26,
         f26,
         CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
         var4.getPanelLeftBackground().getColor().Easing(var4.getPrimaryColor().getColor(), this.clientPanel.getAnimationProgress()).SprintStateEvent(f1)
      );
      var1.flushRoundedRects();
      if (elementpanel1 != null) {
         elementpanel1.renderHeader(var1, var2, var3, f1 * (1.0F - f9), this.x + 104.0F, f13, f22);
      }

      elementpanel.renderHeader(var1, var2, var3, f1 * f9, this.x + 104.0F, f13, f22);
      this.renderSearh(var1, var2, var3, f24 + f22 + GuiStyle.PADDING.intValue(), f13, f1);
      Font font2 = Fonts.NEW_ICONS.getFont(5.0F);
      var1.drawText(
         font2,
         "k",
         f18 + (f26 - font2.width("k")) / 2.0F,
         f13 + (f26 - font2.height()) / 2.0F,
         var4.getTextSecondary().getColor().Easing(var4.getTextEnable().getColor(), this.clientPanel.getAnimationProgress()).SprintStateEvent(f1)
      );
      if (elementpanel1 != null && f20 > 0.01F) {
         elementpanel1.renderHeaderButtons(var1, var2, var3, f1 * (1.0F - f9), f18 - GuiStyle.PADDING.intValue() - f20, f13, f20, 1.0F - f9);
      }

      if (f19 > 0.01F) {
         elementpanel.renderHeaderButtons(var1, var2, var3, f1 * f9, f18 - GuiStyle.PADDING.intValue() - f19, f13, f19, f9);
      }

      this.generalPanel.render(var1, var2, var3, f1, f14, f15);
      this.miscPanel.render(var1, var2, var3, f1, f14, f15 + 112.0F + f11);
      if (elementpanel1 != null) {
         elementpanel1.render(var1, var2, var3, f1 * (1.0F - f9), this.x + 104.0F, f15);
      }

      elementpanel.render(var1, var2, var3, f1 * f9, this.x + 104.0F, f15);
      MsdfRenderer.flushBatch();
      ZenithClient.on23().ModuleStateStore().UiAnimation(var1);
      if (elementpanel1 != null) {
         elementpanel1.renderPriority(var1, var2, var3, f1 * (1.0F - f9), this.x + 104.0F, f15);
      }

      elementpanel.renderPriority(var1, var2, var3, f1 * f9, this.x + 104.0F, f15);
      if (!this.closing && this.prevType != null) {
         this.renderElementSwapBlur(var1, this.x + 104.0F, f15, 376.0F, 295.5F - GuiStyle.PADDING.intValue() * 2.0F, 1.0F - f9);
      }

      this.renderMenuBlurLayers(var1, f1, elementpanel, elementpanel1, f9, f7, f8, f);
      var1.popMatrix();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      float f = this.getGuiScale();
      float f1 = this.x + 240.0F;
      float f2 = this.y + 160.0F;
      mouseX = (int)((mouseX - f1) / f + f1);
      mouseY = (int)((mouseY - f2) / f + f2);
      MenuScreenId ll1lil1ii1iil1l = MenuScreenId.Event37(button);
      if (this.searchField.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
         return true;
      }

      try {
         ElementPanel elementpanel = this.getType().getPanelSupplier().get();
         if (elementpanel.onHeaderButtonsClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
            return true;
         }

         if (Menu.menu.float376() && button == 2 && this.dragBounds != null && this.dragBounds.PotionItemBuilder(mouseX, mouseY)) {
            this.draggingGui = true;
            return true;
         }

         if (elementpanel.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
            return true;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      if (this.profileBounds != null && this.profileBounds.PotionItemBuilder(mouseX, mouseY)) {
         this.profilePanel.toggleExpanded();
      }

      if (this.profilePanel.isRender() && this.profilePanel.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
         return true;
      }

      if (this.clientBounds != null && this.clientBounds.PotionItemBuilder(mouseX, mouseY)) {
         ElementPanel elementpanel1 = this.getType().getPanelSupplier().get();
         if (elementpanel1.isRender() || elementpanel1.isRightDrawerOpen()) {
            elementpanel1.closeRightDrawer();
            if (this.prevType != null) {
               this.prevType.getPanelSupplier().get().closeRightDrawer();
            }
         }

         boolean flag = this.clientPanel.isExpanded();
         this.clientPanel.toggleExpanded();
         if (!flag && this.clientPanel.isExpanded()) {
            elementpanel1.closeRightDrawer();
            if (this.prevType != null) {
               this.prevType.getPanelSupplier().get().closeRightDrawer();
            }
         }

         return true;
      } else if (this.clientPanel.isRender() && this.clientPanel.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
         return true;
      } else if (this.generalPanel.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
         this.resetSearch();
         return true;
      } else if (this.miscPanel.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l)) {
         this.resetSearch();
         return true;
      } else {
         return super.mouseClicked(new net.minecraft.client.gui.Click(mouseX, mouseY, new net.minecraft.client.input.MouseInput(button, 0)), false);
      }
   }

   public NLMenuScreen() {
      super(Text.literal("NLMenu"));
   }

   protected void init() {
      this.animationClose.setValue(0.0F);
      this.animationClose.on23(1.0F);
      this.ensurePanelsInitialized();
      this.closing = false;
      if (this.searchField == null) {
         this.searchField = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "Search for elements...", 100.0F);
         this.searchField.on23(SearchBox.MatchMode.val179);
         this.searchField.on23(SearchBox.SearchScope.val298);
      }
   }

   public void renderMenuBlurLayers(HudDrawContext var1, float var2, ElementPanel var3, ElementPanel var4, float var5, float var6, float var7, float var8) {
      if (Menu.menu.int467()) {
         float f = this.getBlurPower();
         if (f != 0.0F && this.closing) {
            this.drawBlurLayer(var1, this.x, this.y, 480.0F, 320.0F, var8, var2, f);
            if (this.profilePanel.isRender()) {
               float f1 = this.profilePanel.getAnimationProgress();
               float f2 = this.x - 128.0F - GuiStyle.PADDING.intValue() * 2.0F + (128.0F + GuiStyle.PADDING.intValue()) * (1.0F - f1);
               this.drawBlurLayer(var1, f2, this.y + 320.0F - 190.0F, 128.0F, 190.0F, GuiStyle.ROUND.intValue(), var2, f);
            }

            if (this.clientPanel.isRender()) {
               float f3 = this.clientPanel.getAnimationProgress();
               float f4 = var6 - (128.0F + GuiStyle.PADDING.intValue()) * (1.0F - f3);
               this.drawBlurLayer(var1, f4, var7, 128.0F, 227.0F, GuiStyle.ROUND.intValue(), var2, f);
            }

            this.drawRightPanelBlur(var1, var4, var2 * (1.0F - var5), 1.0F - var5, var6, var7, var2, f);
            this.drawRightPanelBlur(var1, var3, var2 * var5, var5, var6, var7, var2, f);
         }
      }
   }

   public void drawRightPanelBlur(HudDrawContext var1, ElementPanel var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var2 != null && var2.isRender()) {
         CornerRadiusF l11liliill1iii1 = var2.getRightPanelBlurBounds(var5, var6, var3, var4);
         if (l11liliill1iii1 != null) {
            this.drawBlurLayer(
               var1, l11liliill1iii1.x(), l11liliill1iii1.y(), l11liliill1iii1.width(), l11liliill1iii1.height(), GuiStyle.ROUND.intValue(), var7, var8
            );
         }
      }
   }

   public void drawBlurLayer(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float f = this.getBlurLayerAlpha(var7);
      if (!(f <= 0.001F)) {
         ShapeRenderer.ItemSpec(
            var1.getMatrices(), var2, var3, var4, var5, var8, CornerRadius.MovementInputEvent(var6), ArgbColor.var11934.SprintStateEvent(f)
         );
      }
   }

   public void renderElementSwapBlur(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (Menu.menu.int467()) {
         float f = this.getBlurPower();
         if (f != 0.0F) {
            this.drawBlurLayer(var1, var2, var3, var4, var5, GuiStyle.ROUND.intValue(), var6, f);
         }
      }
   }

   public float getBlurLayerAlpha(float var1) {
      float f = MathHelper.clamp(var1, 0.0F, 1.0F);
      if (f >= 0.75F) {
         float f2 = (1.0F - f) / 0.25F;
         return f2 * f2 * (3.0F - 2.0F * f2);
      }

      if (f >= 0.3F) {
         return 1.0F;
      }

      float f1 = f / 0.3F;
      return (float)Math.sqrt(f1);
   }

   public void renderSearh(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = 96.0F;
         float f1 = 23.0F;
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f,
            f1,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(5.0F);
         this.searchField.on23(font);
         this.searchField.setWidth(f - GuiStyle.PADDING * 6);
         this.searchField
            .on23(
               var1,
               var4 + GuiStyle.PADDING * 2,
               var5 + (f1 - font.height()) / 2.0F,
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var6),
               zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6)
            );
         var1.drawText(
            font1, "S", var4 + f - font1.width("S") - GuiStyle.PADDING * 2, var5 + (f1 - font1.height()) / 2.0F, zenithstyle.getTextSecondary().getColor(var6)
         );
      }
   }

   public void resetSearch() {
      if (this.searchField != null) {
         this.searchField.HudHotbarPanel("");
         this.searchField.VelocityChangeEvent(false);
         this.searchField.EventRender(0);
      }
   }

   public String getSearchValue() {
      return this.searchField == null ? "" : this.searchField.getText();
   }

   public void openHudElementSettings(HudElement var1) {
      if (var1 != null) {
         this.ensurePanelsInitialized();
         this.resetSearch();
         this.setType(NLMenuScreen_ElementsType.INTERFACE);
         this.interfacePanel.openHudElementSettings(var1);
      }
   }

   public ClientPanel getClientPanel() {
      return this.clientPanel;
   }

   public void updateClientPanelVisibilityForRightPanels(boolean var1) {
      if (var1) {
         if (this.clientPanel.isExpanded()) {
            this.clientPanel.setExpanded(false);
            this.restoreClientPanelAfterRightPanel = true;
         }
      } else if (this.restoreClientPanelAfterRightPanel) {
         this.clientPanel.setExpanded(true);
         this.restoreClientPanelAfterRightPanel = false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float f = this.getGuiScale();
      float f1 = this.x + 240.0F;
      float f2 = this.y + 160.0F;
      mouseX = (int)((mouseX - f1) / f + f1);
      mouseY = (int)((mouseY - f2) / f + f2);
      this.type.getPanelSupplier().get().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      }

      if (this.getType().getPanelSupplier().get().keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      }

      if (keyCode == 256 && !this.closing) {
         this.closing = true;
         this.mouseReleased(0.0, 0.0, 0);
         this.mouseReleased(0.0, 0.0, 0);
         this.mouseReleased(0.0, 0.0, 0);
         ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent2);
      }

      return super.keyPressed(new net.minecraft.client.input.KeyInput(keyCode, scanCode, modifiers));
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.searchField.charTyped(chr, modifiers)) {
         return true;
      } else {
         return this.getType().getPanelSupplier().get().charTyped(chr, modifiers)
            || super.charTyped(new net.minecraft.client.input.CharInput(chr, modifiers));
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 2) {
         this.draggingGui = false;
      }

      float f = this.getGuiScale();
      float f1 = this.x + 240.0F;
      float f2 = this.y + 160.0F;
      mouseX = (int)((mouseX - f1) / f + f1);
      mouseY = (int)((mouseY - f2) / f + f2);
      if (this.getType().getPanelSupplier().get().onMouseReleased(mouseX, mouseY, MenuScreenId.Event37(button))) {
         return true;
      } else {
         return this.clientPanel.onMouseReleased(mouseX, mouseY, MenuScreenId.Event37(button))
            || super.mouseReleased(new net.minecraft.client.gui.Click(mouseX, mouseY, new net.minecraft.client.input.MouseInput(button, 0)));
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (Menu.menu.float376() && this.draggingGui && button == 2) {
         this.rawX += (float)deltaX;
         this.rawY += (float)deltaY;
         Menu.menu.CloudPoller(this.rawX, this.rawY);
         return true;
      } else {
         return this.getType().getPanelSupplier().get().onMouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            ? true
            : super.mouseDragged(new net.minecraft.client.gui.Click(mouseX, mouseY, new net.minecraft.client.input.MouseInput(button, 0)), deltaX, deltaY);
      }
   }

   @Override
   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   @Override
   public boolean mouseReleased(net.minecraft.client.gui.Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   @Override
   public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), deltaX, deltaY);
   }

   @Override
   public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   @Override
   public boolean charTyped(net.minecraft.client.input.CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public void tick() {
      if (this.closing && this.animationClose.CancellableEvent() == 0.0F) {
         this.close();
      }

      this.getType().getPanelSupplier().get().tick();
      super.tick();
   }

   public void removed() {
      this.closing = true;
      this.draggingGui = false;
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      super.removed();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public boolean isRenderHud() {
      return minecraftClient3.currentScreen == this
         && !this.closing
         && this.getType() == NLMenuScreen_ElementsType.INTERFACE
         && this.interfacePanel.getCurrentCategory() == InterfacePanel_InterfaceCategory.HUD;
   }

   public float clampRenderPosition(float var1, float var2, float var3) {
      float f = Math.max(0.0F, var3 - var2);
      return MathHelper.clamp(var1, 0.0F, f);
   }

   public boolean isFinish() {
      return this.animationClose.CancellableEvent() == 0.0F && this.closing;
   }

   public void initialize() {
      this.ensurePanelsInitialized();
   }

   public boolean isShortMode() {
      return !this.clientPanel.getRenderDescription().getSetting().isEnabled();
   }

   public void setRenderIcon(boolean var1) {
      this.clientPanel.getRenderIcon().getSetting().setEnabled(var1);
   }

   public boolean isRenderIcon() {
      return this.clientPanel.getRenderIcon().getSetting().isEnabled();
   }

   public float getGuiScale() {
      if (this.clientPanel == null) {
         return 1.0F;
      } else {
         float f = this.clientPanel.getGuiScale().getApplayValue() / 100.0F;
         float f1 = 0.35F;
         float f2 = 100.0F;
         float f3 = minecraftClient3.getWindow().getScaledWidth();
         float f4 = minecraftClient3.getWindow().getScaledHeight();
         float f5 = f3 / (480.0F + f2);
         float f6 = f4 / (320.0F + f2);
         float f7 = Math.min(f5, f6);
         if (Float.isNaN(f) || Float.isInfinite(f)) {
            return 1.0F;
         } else if (f7 <= 0.0F) {
            return f1;
         } else {
            return f7 < f1 ? f7 : MathHelper.clamp(f, f1, f7);
         }
      }
   }

   public void safe(JsonObject var1) {
      this.ensurePanelsInitialized();
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("type", this.type.name());
      JsonObject jsonobject1 = new JsonObject();
      this.profilePanel.safe(jsonobject1);
      jsonobject.add("profilePanel", jsonobject1);
      JsonObject jsonobject2 = new JsonObject();
      this.clientPanel.safe(jsonobject2);
      jsonobject.add("clientPanel", jsonobject2);
      var1.add("NLMenuScreen", jsonobject);
   }

   public void load(JsonObject var1) {
      this.ensurePanelsInitialized();
      if (var1.has("NLMenuScreen") && var1.get("NLMenuScreen").isJsonObject()) {
         JsonObject jsonobject = var1.getAsJsonObject("NLMenuScreen");
         if (jsonobject.has("type")) {
            try {
               this.setType(NLMenuScreen_ElementsType.valueOf(jsonobject.get("type").getAsString()));
            } catch (IllegalArgumentException var4) {
            }
         }

         if (jsonobject.has("profilePanel") && jsonobject.get("profilePanel").isJsonObject()) {
            this.profilePanel.load(jsonobject.getAsJsonObject("profilePanel"));
         }

         if (jsonobject.has("clientPanel") && jsonobject.get("clientPanel").isJsonObject()) {
            this.clientPanel.load(jsonobject.getAsJsonObject("clientPanel"));
         }
      }
   }

   public void ensurePanelsInitialized() {
      if (!this.init) {
         this.guiModulePanel = new GuiModulePanel();
         this.guiFreindsPanel = new GuiFreindsPanel();
         this.interfacePanel = new InterfacePanel();
         this.guiConfigPanel = new GuiConfigPanel();
         this.cosmeticElementPanel = new CosmeticElementPanel();
         this.scriptsPanel = new ScriptsPanel();
         this.generalPanel = new GeneralPanel();
         this.profilePanel = new ProfilePanel();
         this.clientPanel = new ClientPanel();
         this.miscPanel = new MiscPanel();
         this.init = true;
      }
   }

   public float getBlurPower() {
      return this.clientPanel.getBlurStrength().getSetting().getCurrent();
   }

   public boolean isElementSwapBlurActive() {
      return this.prevType != null
         || this.guiModulePanel != null && this.guiModulePanel.isCategorySwitching()
         || this.scriptsPanel != null && this.scriptsPanel.isAddonSwitching();
   }

   public void setType(NLMenuScreen_ElementsType var1) {
      if (var1 != this.type) {
         this.setTypeAnimation.UiAnimation(0.0F);
         this.setTypeAnimation.on23(1.0F);
         this.prevType = this.type;
         this.type = var1;
      }
   }

   public boolean isSearch() {
      return this.searchField != null && this.searchField.isSelected();
   }

   public GuiModulePanel getGuiModulePanel() {
      return this.guiModulePanel;
   }

   public GuiFreindsPanel getGuiFreindsPanel() {
      return this.guiFreindsPanel;
   }

   public InterfacePanel getInterfacePanel() {
      return this.interfacePanel;
   }

   public GuiConfigPanel getGuiConfigPanel() {
      return this.guiConfigPanel;
   }

   public CosmeticElementPanel getCosmeticElementPanel() {
      return this.cosmeticElementPanel;
   }

   public ScriptsPanel getScriptsPanel() {
      return this.scriptsPanel;
   }

   public NLMenuScreen_ElementsType getType() {
      return this.type;
   }

   public boolean isClosing() {
      return this.closing;
   }
}
