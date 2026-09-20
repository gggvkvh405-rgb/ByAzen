package org.zenith.client.screens.autocraft;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.ItemFilterRules;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.module.misc.AutoCraft;
import org.zenith.module.render.Menu;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;
import org.zenith.utility.render.display.base.RenderMathUtils;

public class AutoCraftEditorScreen extends CustomScreen {
   public static final float panelWidth = 480.0F;
   public static final float panelHeight = 320.0F;
   public static final float topHeight = 284.0F;
   public static final float bottomHeight = 36.0F;
   public static final ArgbColor topColor = GuiStyle.RIGHT_BACKGROUND;
   public static final ArgbColor bottomColor = GuiStyle.LEFT_BACKGROUND;
   public static final ArgbColor blurDesignTint = new ArgbColor(12, 12, 12, 20);
   public static final ArgbColor panelInnerColor61 = GuiStyle.FIELD_SURFACE_BACKGROUND;
   public static final ArgbColor panelInnerColor122 = GuiStyle.PANEL_LEFT_BACKGROUND;
   public static final ArgbColor settingsButtonIdleColor = GuiStyle.PANEL_LEFT_BACKGROUND;
   public static final ArgbColor settingsButtonHoverColor = GuiStyle.DISABLE_ACTIVE_BG;
   public static final ArgbColor settingsButtonActiveColor = GuiStyle.PRIMARY_COLOR;
   public static final ArgbColor itemTextColor = GuiStyle.TEXT_SECONDARY;
   public static final ArgbColor countColor = GuiStyle.TEXT_TERTIARY;
   public static final ArgbColor searchEmptySelected = ArgbColor.var11941;
   public static final ArgbColor searchEmptyUnselected = GuiStyle.TEXT_SECONDARY;
   public static final ArgbColor arrowColor = GuiStyle.FIELD_BORDER;
   public static final ArgbColor scrollTrackColor = GuiStyle.FIELD_SURFACE_BACKGROUND;
   public static final ArgbColor scrollThumbColor = GuiStyle.FIELD_BORDER;
   public static final ArgbColor deleteTextColor = GuiStyle.TEXT_SECONDARY;
   public static final CornerRadius topRadius = new CornerRadius(GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue(), 0.0F, 0.0F);
   public static final CornerRadius bottomRadius = new CornerRadius(0.0F, 0.0F, GuiStyle.ROUND.intValue(), GuiStyle.ROUND.intValue());
   public static final CornerRadius radius8 = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue());
   public static final CornerRadius radius10 = CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue());
   public static final CornerRadius radius1 = CornerRadius.MovementInputEvent(1.0F);
   public static final CornerRadius radius128 = CornerRadius.MovementInputEvent(128.0F);
   public static final Font font6 = Fonts.NEW_MEDIUM.getFont(6.0F);
   public static final Font font55 = Fonts.NEW_MEDIUM.getFont(5.5F);
   public static final Font font5 = Fonts.NEW_REGULAR.getFont(5.0F);
   public static final Font font47 = Fonts.NEW_MEDIUM.getFont(4.7F);
   public static final Font font4 = Fonts.NEW_MEDIUM.getFont(4.0F);
   public static final Font iconFont6 = Fonts.NEW_ICONS.getFont(6.0F);
   public static final Font iconFont7 = Fonts.NEW_ICONS.getFont(7.0F);
   public static final float settingsPanelOffsetX = 10.0F;
   public static final float settingsPanelWidth = 128.0F;
   public static final float settingsHeaderWidth = 128.0F;
   public static final float settingsHeaderHeight = 23.0F;
   public static final float settingsPanelPadding = 8.0F;
   public static final float settingsControlGap = 6.0F;
   public static final long ribbonAppearDurationMs = 160L;
   public static final long selectionSaveDelayMs = 650L;
   public static final float ribbonAppearEpsilon = 0.001F;
   public static final float ribbonClickAppearThreshold = 0.5F;
   public static final float ribbonShiftSnapEpsilon = 0.05F;
   public static final float ribbonShiftStepFactor = 0.25F;
   public static final float ribbonShiftMinStep = 0.5F;
   public static final float ribbonPadding = 4.0F;
   public static final float ribbonItemHeight = 23.0F;
   public static final float ribbonItemGap = 4.0F;
   public static final float ribbonIconSize = 8.0F;
   public static final float ribbonIconLeftPadding = 4.0F;
   public static final float ribbonTextGapFromIcon = 4.0F;
   public static final float ribbonIconVisibilityThreshold = 12.0F;
   public static final float ribbonTextVisibilityThreshold = 24.0F;
   public static final ItemStack craftingTableStack = Items.CRAFTING_TABLE.getDefaultStack();
   public static final ItemStack chestStack = Items.CHEST.getDefaultStack();
   public final AutoCraft module;
   public String searchQuery = "";
   public float bottomPresetRibbonScrollX = 0.0F;
   public float bottomPresetRibbonMaxScrollX = 0.0F;
   public boolean settingsPanelVisible = false;
   public final UiAnimation screenOpenCloseAnimation = new UiAnimation(200L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation settingsPanelRevealAnimation = new UiAnimation(220L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation settingsButtonHoverAnimation = new UiAnimation(140L, 0.0F, Easing.HotbarInputEvent);
   public final UiAnimation settingsButtonActiveAnimation = new UiAnimation(180L, 0.0F, Easing.HotbarInputEvent);
   public boolean closing = false;
   public Runnable pendingCloseAction = null;
   public boolean selectionSavePending = false;
   public long selectionSaveAtMs = 0L;
   public SearchBox searchBox;
   public static boolean restoreUiOnNextOpen = false;
   public static String restoreSearchQuery = "";
   public static float restoreRibbonScrollX = 0.0F;
   public static boolean restoreSettingsPanelVisible = false;
   public final List<String> ingredientKeysCache = new ArrayList<>();
   public final List<GuiSetting<?>> settingsPanelControls = new ArrayList<>();
   public final Map<String, Float> ribbonItemCurrentX = new HashMap<>();
   public final Map<String, UiAnimation> ribbonItemAppearAnim = new HashMap<>();
   public String ribbonAnimationProfile = "";
   public boolean ribbonAnimationInitialized = false;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public AutoCraftEditorScreen(AutoCraft var1) {
      this.module = var1;
      this.initSettingsPanelControls();
      if (restoreUiOnNextOpen) {
         this.searchQuery = restoreSearchQuery == null ? "" : restoreSearchQuery;
         this.bottomPresetRibbonScrollX = Math.max(0.0F, restoreRibbonScrollX);
         this.settingsPanelVisible = restoreSettingsPanelVisible;
         SearchBox i1lil1lliilli1lli1l = this.getSearchBox();
         i1lil1lliilli1lli1l.HudHotbarPanel(this.searchQuery);
         i1lil1lliilli1lli1l.EventRender(this.searchQuery.length());
         restoreUiOnNextOpen = false;
      }

      this.screenOpenCloseAnimation.setValue(0.0F);
      this.settingsPanelRevealAnimation.setValue(this.settingsPanelVisible ? 1.0F : 0.0F);
      this.settingsButtonHoverAnimation.setValue(0.0F);
      this.settingsButtonActiveAnimation.setValue(this.settingsPanelVisible ? 1.0F : 0.0F);
      this.ribbonAnimationProfile = this.getRibbonProfileKey();
      this.initializeRibbonAnimationState();
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3) {
      float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
      float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
      float f2 = f1 + 284.0F;
      float f3 = f + 240.0F;
      float f4 = f1 + 160.0F;
      this.screenOpenCloseAnimation.Easing(this.closing ? 0.0F : 1.0F);
      float f5 = this.screenOpenCloseAnimation.EmotePlayback();
      if (this.closing && f5 <= 0.001F) {
         Runnable runnable = this.pendingCloseAction == null ? this::openClickGui : this.pendingCloseAction;
         this.pendingCloseAction = null;
         this.closing = false;
         runnable.run();
      } else {
         float f6 = 0.96F + 0.04F * f5;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f3, f4);
         var1.getMatrices().scale(f6, f6);
         var1.getMatrices().translate(-f3, -f4);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, f5);
         this.settingsPanelRevealAnimation.Easing(this.settingsPanelVisible ? 1.0F : 0.0F);
         this.settingsPanelRevealAnimation.EmotePlayback();
         float f7 = this.getSettingsPanelRevealProgress();
         if (f7 > 0.001F) {
            this.renderSettingsPanel(var1, var2, var3, f7);
         }

         ShapeRenderer.TextScanner(var1.getMatrices(), f, f1, 480.0F, 284.0F, 32.0F, topRadius, ArgbColor.var11934);
         ShapeRenderer.TextScanner(var1.getMatrices(), f, f2, 480.0F, 36.0F, 32.0F, bottomRadius, ArgbColor.var11934);
         var1.drawRoundedRect(f, f1, 480.0F, 284.0F, topRadius, topColor);
         var1.drawRoundedRect(f, f2, 480.0F, 36.0F, bottomRadius, bottomColor);
         var1.drawRoundedRect(f, f1, 480.0F, 284.0F, topRadius, blurDesignTint);
         var1.drawRoundedRect(f, f2, 480.0F, 36.0F, bottomRadius, blurDesignTint);
         this.renderMarkLabelPanel(var1, f, f1);
         this.renderTopItemPanel(var1, f, f1);
         this.renderSearchBar(var1, f, f1);
         this.renderSettingsButton(var1, f, f1, var2, var3);
         this.renderCraftTablePanel(var1, f, f1);
         this.renderChestPanel(var1, f, f1);
         this.renderBottomPresetRibbon(var1, f, f2, 480.0F, 36.0F);
         this.renderCraftGrid(var1, f, f1);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   public void initSettingsPanelControls() {
      this.settingsPanelControls.clear();
      float f = getSettingsControlWidth();
      this.settingsPanelControls.add(new GuiBooleanSetting(this.module.call428(), f));
      this.settingsPanelControls.add(new GuiBooleanSetting(this.module.call429(), f));
      this.settingsPanelControls.add(new GuiBooleanSetting(this.module.double119(), f));
   }

   public void renderSettingsPanel(HudDrawContext var1, float var2, float var3, float var4) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = this.getSettingsPanelAnimatedX(var4);
         float f1 = this.getSettingsPanelY();
         float f2 = this.getSettingsPanelW();
         float f3 = this.getSettingsPanelH();
         ShapeRenderer.ItemSpec(var1.getMatrices(), f, f1, f2, f3, 12.0F, radius10, ArgbColor.var11934.SprintStateEvent(var4));
         var1.drawRoundedRect(f, f1, f2, f3, radius10, zenithstyle.getRightBackground().getColor().SprintStateEvent(var4));
         var1.drawRoundedRect(f, f1, f2, f3, radius10, blurDesignTint);
         var1.drawRoundedRect(f, f1, 128.0F, 23.0F, topRadius, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var4));
         float f4 = f + 8.0F;
         float f5 = f1 + (23.0F - font55.height()) / 2.0F;
         var1.drawText(font55, "Settings", f4, f5, zenithstyle.getTextEnable().getColor().SprintStateEvent(var4));
         float f6 = this.getSettingsContentY();
         float f7 = this.getSettingsContentX(var4);

         for (GuiSetting guisetting : this.settingsPanelControls) {
            this.renderEditorSetting(guisetting, var1, var2, var3, f7, f6, var4);
            f6 += guisetting.getHeight() + 6.0F;
         }

         f6 = this.getSettingsContentY();

         for (GuiSetting guisetting1 : this.settingsPanelControls) {
            this.renderEditorSettingPriority(guisetting1, var1, var2, var3, f7, f6, var4);
            f6 += guisetting1.getHeight() + 6.0F;
         }
      }
   }

   public void renderEditorSetting(GuiSetting<?> var1, HudDrawContext var2, float var3, float var4, float var5, float var6, float var7) {
      Supplier supplier = var1.getSetting().getVisible();
      var1.getSetting().setVisible(() -> true);

      try {
         var1.render(var2, var3, var4, var5, var6, var7);
      } finally {
         var1.getSetting().setVisible(supplier);
      }
   }

   public void renderEditorSettingPriority(GuiSetting<?> var1, HudDrawContext var2, float var3, float var4, float var5, float var6, float var7) {
      Supplier supplier = var1.getSetting().getVisible();
      var1.getSetting().setVisible(() -> true);

      try {
         var1.renderPriority(var2, var3, var4, var5, var6, var7, this.module.isEnabled() ? 1.0F : 0.5F);
      } finally {
         var1.getSetting().setVisible(supplier);
      }
   }

   public void renderMarkLabelPanel(HudDrawContext var1, float var2, float var3) {
      float f = var2 + 4.0F;
      float f1 = var3 + 4.0F;
      float f2 = 63.5F;
      float f3 = 23.0F;
      var1.drawRoundedRect(f, f1, 63.5F, 23.0F, radius8, panelInnerColor122);
      String s = "AutoCraft";
      float f4 = f + (63.5F - font6.width("AutoCraft")) / 2.0F;
      float f5 = f1 + (23.0F - font6.height()) / 2.0F;
      var1.drawText(font6, "AutoCraft", f4, f5, ArgbColor.var11934);
   }

   public void renderTopItemPanel(HudDrawContext var1, float var2, float var3) {
      float f = var2 + 4.0F;
      float f1 = 63.5F;
      float f2 = 245.5F;
      float f3 = 23.0F;
      float f4 = f + 63.5F + 4.0F;
      float f5 = var3 + 4.0F;
      float f6 = 23.0F;
      float f7 = 52.5F;
      float f8 = 23.0F;
      var1.drawRoundedRect(f4, f5, 245.5F, 23.0F, radius8, panelInnerColor61);
      var1.drawRoundedRect(f4, f5, 23.0F, 23.0F, radius8, panelInnerColor61);
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.module.call043();
      if (iiilili1lli1i11lilillliiii1iii != null) {
         boolean flag = iiilili1lli1i11lilillliiii1iii.int395();
         float f9 = f4 + 245.5F - 52.5F;
         if (flag) {
            var1.drawRoundedRect(f9, f5, 52.5F, 23.0F, radius8, panelInnerColor122);
            String s = "Delete";
            float f10 = f9 + (52.5F - font6.width("Delete")) / 2.0F;
            float f11 = f5 + (23.0F - font6.height()) / 2.0F;
            var1.drawText(font6, "Delete", f10, f11, deleteTextColor);
         }

         ItemStack itemstack = iiilili1lli1i11lilillliiii1iii.UiAnimation(this.module);
         if (!itemstack.isEmpty()) {
            float f16 = 8.0F;
            float f17 = 0.5F;
            float f12 = f4 + 7.5F;
            float f13 = f5 + 7.5F;
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(f12, f13);
            var1.getMatrices().scale(0.5F, 0.5F);
            var1.drawItem(itemstack, 0, 0);
            var1.getMatrices().popMatrix();
         }

         Font font = font6;
         String s1 = iiilili1lli1i11lilillliiii1iii.on23(this.module);
         if (s1 == null || s1.isBlank()) {
            s1 = iiilili1lli1i11lilillliiii1iii.getDisplayName();
         }

         if (s1 == null) {
            s1 = "";
         }

         float f18 = f4 + 23.0F + 8.0F;
         float f19 = f5 + (23.0F - font.height()) / 2.0F;
         float f14 = flag ? f9 - 4.0F : f4 + 245.5F - 6.0F;
         float f15 = f14 - f18;
         if (!(f15 <= 0.0F)) {
            if (font.width(s1) > f15) {
               while (!s1.isEmpty() && font.width(s1 + "...") > f15) {
                  s1 = s1.substring(0, s1.length() - 1);
               }

               if (!s1.isEmpty()) {
                  s1 = s1 + "...";
               }
            }

            if (!s1.isEmpty()) {
               var1.drawText(font, s1, f18, f19, ArgbColor.var11934);
            }
         }
      }
   }

   public void renderSearchBar(HudDrawContext var1, float var2, float var3) {
      float f = var2 + 4.0F;
      float f1 = 63.5F;
      float f2 = 245.5F;
      float f3 = f + 63.5F + 4.0F;
      float f4 = f3 + 245.5F + 4.0F;
      float f5 = var3 + 4.0F;
      float f6 = 128.0F;
      float f7 = 23.0F;
      var1.drawRoundedRect(f4, f5, 128.0F, 23.0F, radius8, panelInnerColor122);
      SearchBox i1lil1lliilli1lli1l = this.getSearchBox();
      i1lil1lliilli1lli1l.setWidth(112.0F);
      ArgbColor i11ii1llliilllii1i1 = i1lil1lliilli1lli1l.isSelected() ? searchEmptySelected : searchEmptyUnselected;
      i1lil1lliilli1lli1l.on23(var1, f4 + 8.0F, f5 + (23.0F - i1lil1lliilli1lli1l.call050().height()) / 2.0F, ArgbColor.var11934, i11ii1llliilllii1i1);
   }

   public void renderSettingsButton(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      float f = var2 + 4.0F;
      float f1 = 63.5F;
      float f2 = 245.5F;
      float f3 = f + 63.5F + 4.0F;
      float f4 = 128.0F;
      float f5 = f3 + 245.5F + 4.0F;
      float f6 = var3 + 4.0F;
      float f7 = f5 + 128.0F + 4.0F;
      float f8 = 23.0F;
      boolean flag = RenderMathUtils.on23(f7, f6, 23.0, 23.0, var4, var5);
      this.settingsButtonHoverAnimation.Easing(flag && !this.settingsPanelVisible ? 1.0F : 0.0F);
      float f9 = this.settingsButtonHoverAnimation.EmotePlayback();
      this.settingsButtonActiveAnimation.Easing(this.settingsPanelVisible ? 1.0F : 0.0F);
      float f10 = this.settingsButtonActiveAnimation.EmotePlayback();
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      ArgbColor i11ii1llliilllii1i1 = zenithstyle == null ? settingsButtonActiveColor : zenithstyle.getPrimaryColor().getColor();
      ArgbColor i11ii1llliilllii1i11 = settingsButtonIdleColor.Easing(settingsButtonHoverColor, f9);
      ArgbColor i11ii1llliilllii1i12 = i11ii1llliilllii1i11.Easing(i11ii1llliilllii1i1, f10);
      String s = "F";
      float f11 = f7 + (23.0F - iconFont6.width("F")) / 2.0F;
      float f12 = f6 + (23.0F - iconFont6.height()) / 2.0F - 0.3F;
      var1.drawRoundedRect(f7, f6, 23.0F, 23.0F, radius8, i11ii1llliilllii1i12);
      var1.drawText(iconFont6, "F", f11, f12, ArgbColor.var11934);
   }

   public void renderCraftTablePanel(HudDrawContext var1, float var2, float var3) {
      float f = var3 + 4.0F;
      float f1 = 23.0F;
      float f2 = 234.0F;
      float f3 = 23.0F;
      float f4 = 23.0F;
      float f5 = var2 + 4.0F;
      float f6 = f + 23.0F + 4.0F;
      float f7 = 0.5F;
      float f8 = 8.0F;
      float f9 = f5 + 7.5F;
      float f10 = f6 + 7.5F;
      var1.drawRoundedRect(f5, f6, 234.0F, 23.0F, radius8, panelInnerColor61);
      var1.drawRoundedRect(f5, f6, 23.0F, 23.0F, radius8, panelInnerColor61);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(f9, f10);
      var1.getMatrices().scale(0.5F, 0.5F);
      var1.drawItem(craftingTableStack, 0, 0);
      var1.getMatrices().popMatrix();
      float f11 = f5 + 23.0F + 8.0F;
      float f12 = f6 + (23.0F - font6.height()) / 2.0F;
      var1.drawText(font6, "Crafting Table", f11, f12, ArgbColor.var11934);
   }

   public void renderChestPanel(HudDrawContext var1, float var2, float var3) {
      float f = var3 + 31.0F;
      float f1 = var2 + 4.0F;
      float f2 = 234.0F;
      float f3 = 234.0F;
      float f4 = 23.0F;
      float f5 = 23.0F;
      float f6 = f1 + 234.0F + 4.0F;
      float f7 = 0.5F;
      float f8 = 8.0F;
      float f9 = f6 + 7.5F;
      float f10 = f + 7.5F;
      var1.drawRoundedRect(f6, f, 234.0F, 23.0F, radius8, panelInnerColor61);
      var1.drawRoundedRect(f6, f, 23.0F, 23.0F, radius8, panelInnerColor61);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(f9, f10);
      var1.getMatrices().scale(0.5F, 0.5F);
      var1.drawItem(chestStack, 0, 0);
      var1.getMatrices().popMatrix();
      float f11 = f6 + 23.0F + 8.0F;
      float f12 = f + (23.0F - font6.height()) / 2.0F;
      var1.drawText(font6, "Chest", f11, f12, ArgbColor.var11934);
   }

   public void renderBottomPresetRibbon(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      float f = var2 + 4.0F;
      float f1 = var3 + 4.0F;
      float f2 = var4 - 8.0F;
      float f3 = var5 - 8.0F;
      String s = "Add";
      String s1 = "M";
      boolean flag = "custom".equalsIgnoreCase(this.module.call056());
      AutoCraftEditorScreen_RibbonLayoutData autocrafteditorscreen_ribbonlayoutdata = this.buildRibbonLayoutData(f, f1, flag, true);
      this.bottomPresetRibbonMaxScrollX = Math.max(0.0F, autocrafteditorscreen_ribbonlayoutdata.contentWidth - f2);
      float f4 = Math.max(0.0F, Math.min(this.bottomPresetRibbonScrollX, this.bottomPresetRibbonMaxScrollX));
      if (f4 != this.bottomPresetRibbonScrollX) {
         this.bottomPresetRibbonScrollX = f4;
         autocrafteditorscreen_ribbonlayoutdata = this.buildRibbonLayoutData(f, f1, flag, false);
      } else {
         this.bottomPresetRibbonScrollX = f4;
      }

      var1.enableScissor((int)(f - 4.0F), (int)f1, (int)(f + f2 + 3.0F), (int)(f1 + f3));
      float f5 = 0.5F;
      float f6 = (23.0F - font6.height()) / 2.0F;
      float f7 = 7.5F;

      for (AutoCraftEditorScreen_RibbonItemLayout autocrafteditorscreen_ribbonitemlayout : autocrafteditorscreen_ribbonlayoutdata.items) {
         if (!autocrafteditorscreen_ribbonitemlayout.inFilter) {
            this.renderRibbonItem(var1, autocrafteditorscreen_ribbonitemlayout, 0.5F, 7.5F, f6);
         }
      }

      for (AutoCraftEditorScreen_RibbonItemLayout autocrafteditorscreen_ribbonitemlayout1 : autocrafteditorscreen_ribbonlayoutdata.items) {
         if (autocrafteditorscreen_ribbonitemlayout1.inFilter && autocrafteditorscreen_ribbonitemlayout1.appearProgress >= 0.999F) {
            this.renderRibbonItem(var1, autocrafteditorscreen_ribbonitemlayout1, 0.5F, 7.5F, f6);
         }
      }

      for (AutoCraftEditorScreen_RibbonItemLayout autocrafteditorscreen_ribbonitemlayout2 : autocrafteditorscreen_ribbonlayoutdata.items) {
         if (autocrafteditorscreen_ribbonitemlayout2.inFilter && autocrafteditorscreen_ribbonitemlayout2.appearProgress < 0.999F) {
            this.renderRibbonItem(var1, autocrafteditorscreen_ribbonitemlayout2, 0.5F, 7.5F, f6);
         }
      }

      if (flag) {
         var1.drawRoundedRect(
            autocrafteditorscreen_ribbonlayoutdata.addX,
            autocrafteditorscreen_ribbonlayoutdata.addY,
            autocrafteditorscreen_ribbonlayoutdata.addWidth,
            autocrafteditorscreen_ribbonlayoutdata.addHeight,
            radius8,
            panelInnerColor61
         );
         float f10 = autocrafteditorscreen_ribbonlayoutdata.addX + 4.0F + (8.0F - iconFont7.width("M")) / 2.0F;
         float f11 = autocrafteditorscreen_ribbonlayoutdata.addY + (autocrafteditorscreen_ribbonlayoutdata.addHeight - iconFont7.height()) / 2.0F;
         var1.drawText(iconFont7, "M", f10, f11, itemTextColor);
         float f8 = autocrafteditorscreen_ribbonlayoutdata.addX + 4.0F + 8.0F + 4.0F;
         float f9 = autocrafteditorscreen_ribbonlayoutdata.addY + (autocrafteditorscreen_ribbonlayoutdata.addHeight - font6.height()) / 2.0F;
         var1.drawText(font6, "Add", f8, f9, itemTextColor);
      }

      var1.disableScissor();
      this.renderBottomScrollbar(var1, var2, var3, var5, f2);
   }

   public void renderRibbonItem(HudDrawContext var1, AutoCraftEditorScreen_RibbonItemLayout var2, float var3, float var4, float var5) {
      if (!(var2.width <= 0.001F)) {
         var1.drawRoundedRect(var2.currentX, var2.y, var2.width, var2.height, radius8, panelInnerColor61);
         float f = var2.currentX + var2.width;
         if (!(f <= var2.currentX)) {
            var1.enableScissor((int)var2.currentX, (int)var2.y, (int)Math.ceil(f), (int)Math.ceil(var2.y + var2.height));
            if (var2.width > 12.0F) {
               ItemStack itemstack = var2.preset.UiAnimation(this.module);
               if (!itemstack.isEmpty()) {
                  float f1 = var2.currentX + 4.0F;
                  float f2 = var2.y + var4;
                  var1.getMatrices().pushMatrix();
                  var1.getMatrices().translate(f1, f2);
                  var1.getMatrices().scale(var3, var3);
                  var1.drawItem(itemstack, 0, 0);
                  var1.getMatrices().popMatrix();
               }
            }

            if (var2.width > 24.0F) {
               float f3 = var2.currentX + 4.0F + 8.0F + 4.0F;
               float f4 = var2.y + var5;
               var1.drawText(font6, var2.label, f3, f4, itemTextColor);
            }

            var1.disableScissor();
         }
      }
   }

   public void renderBottomScrollbar(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      float f = Math.max(0.0F, this.bottomPresetRibbonMaxScrollX);
      if (!(f <= 0.001F)) {
         float f1 = var2 + 4.0F;
         float f2 = var3 + var4 - 5.0F;
         float f3 = 472.0F;
         float f4 = 1.0F;
         var1.drawRoundedRect(f1, f2, 472.0F, 1.0F, radius128, scrollTrackColor);
         float f5 = Math.max(0.0F, Math.min(this.bottomPresetRibbonScrollX, f));
         float f6 = var5 + f;
         float f7 = 472.0F * (var5 / f6);
         f7 = Math.max(1.0F, Math.min(472.0F, f7));
         float f8 = 472.0F - f7;
         float f9 = f5 / f;
         float f10 = f1 + f8 * f9;
         var1.drawRoundedRect(f10, f2, f7, 1.0F, radius1, scrollThumbColor);
      }
   }

   public void renderCraftGrid(HudDrawContext var1, float var2, float var3) {
      float f = var2 + 242.0F;
      float f1 = var3 + 31.0F;
      float f2 = 234.0F;
      float f3 = 23.0F;
      float f4 = 36.0F;
      float f5 = 2.0F;
      float f6 = 112.0F;
      float f7 = f + 61.0F - 160.0F;
      float f8 = f1 + 23.0F + 16.0F;
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.module.call043();
      float f9 = 0.75F;
      float f10 = 12.0F;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            float f11 = f7 + j * 38.0F;
            float f12 = f8 + i * 38.0F;
            var1.drawRoundedRect(f11, f12, 36.0F, 36.0F, radius8, panelInnerColor61);
            if (iiilili1lli1i11lilillliiii1iii != null) {
               int k = i * 3 + j;
               String s = iiilili1lli1i11lilillliiii1iii.DataChangedEvent(k);
               if (s != null && !s.isBlank()) {
                  ItemStack itemstack = this.module.GameMessageEvent(s).getDefaultStack();
                  if (!itemstack.isEmpty()) {
                     float f13 = f11 + 12.0F;
                     float f14 = f12 + 12.0F;
                     var1.getMatrices().pushMatrix();
                     var1.getMatrices().translate(f13, f14);
                     var1.getMatrices().scale(0.75F, 0.75F);
                     var1.drawItem(itemstack, 0, 0);
                     var1.getMatrices().popMatrix();
                  }
               }
            }
         }
      }

      String s1 = "A";
      float f19 = f7 + 76.0F;
      float f20 = f8 + 36.0F + 2.0F;
      float f21 = f19 + 36.0F + 12.0F;
      float f22 = 3.0F;
      float f23 = f21 - iconFont7.width("A") / 2.0F + 3.0F;
      float f24 = f20 + (36.0F - iconFont7.height()) / 2.0F;
      var1.drawText(iconFont7, "A", f23, f24, arrowColor);
      float f25 = 12.0F;
      float f26 = f21 + iconFont7.width("A") / 2.0F + 12.0F;
      var1.drawRoundedRect(f26, f20, 36.0F, 36.0F, radius8, panelInnerColor61);
      if (iiilili1lli1i11lilillliiii1iii != null) {
         ItemStack itemstack1 = iiilili1lli1i11lilillliiii1iii.UiAnimation(this.module);
         if (itemstack1.isEmpty() && !iiilili1lli1i11lilillliiii1iii.float275().isBlank()) {
            itemstack1 = this.module.GameMessageEvent(iiilili1lli1i11lilillliiii1iii.float275()).getDefaultStack();
         }

         if (!itemstack1.isEmpty()) {
            float f15 = 0.75F;
            float f16 = 12.0F;
            float f17 = f26 + 12.0F;
            float f18 = f20 + 12.0F;
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(f17, f18);
            var1.getMatrices().scale(0.75F, 0.75F);
            var1.drawItem(itemstack1, 0, 0);
            var1.getMatrices().popMatrix();
         }
      }

      this.renderResourcesList(var1, var2, f8, 36.0F, 2.0F);
   }

   public void renderResourcesList(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.module.call043();
      if (iiilili1lli1i11lilillliiii1iii != null) {
         float f = var3 + var4 * 3.0F + var5 * 2.0F;
         float f1 = var2 + 4.0F;
         float f2 = f + 16.0F;
         float f3 = 472.0F;
         float f4 = 19.0F;
         float f5 = 2.0F;
         List<String> list = this.getUniqueIngredientKeys(iiilili1lli1i11lilillliiii1iii);
         float f6 = font6.height();
         float f7 = font5.height();

         for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            String s1 = iiilili1lli1i11lilillliiii1iii.FriendFilter(s);
            int j = iiilili1lli1i11lilillliiii1iii.CloudApi(s);
            float f8 = f2 + i * 21.0F;
            var1.drawRoundedRect(f1, f8, 472.0F, 19.0F, radius8, panelInnerColor61);
            float f9 = f1 + 6.0F;
            ItemStack itemstack = this.module.GameMessageEvent(s1).getDefaultStack();
            if (!itemstack.isEmpty()) {
               float f10 = 8.0F;
               float f11 = 0.5F;
               float f13 = f8 + 5.5F;
               var1.getMatrices().pushMatrix();
               var1.getMatrices().translate(f9, f13);
               var1.getMatrices().scale(0.5F, 0.5F);
               var1.drawItem(itemstack, 0, 0);
               var1.getMatrices().popMatrix();
               f9 += 12.0F;
            }

            String s2 = iiilili1lli1i11lilillliiii1iii.on23(s, this.module);
            String s3 = "x" + j;
            float f12 = 4.0F;
            float f19 = font5.width(s3);
            float f14 = f1 + 472.0F - 6.0F;
            float f15 = Math.max(0.0F, f14 - f9 - 4.0F - f19);
            if (font6.width(s2) > f15) {
               while (!s2.isEmpty() && font6.width(s2 + "...") > f15) {
                  s2 = s2.substring(0, s2.length() - 1);
               }

               if (!s2.isEmpty()) {
                  s2 = s2 + "...";
               }
            }

            float f16 = f8 + (19.0F - f6) / 2.0F;
            float f17 = f8 + (19.0F - f7) / 2.0F;
            float f18 = f9 + font6.width(s2) + 4.0F;
            var1.drawText(font6, s2, f9, f16, ArgbColor.var11934);
            var1.drawText(font5, s3, f18, f17, countColor);
         }
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.closing) {
         return false;
      } else {
         float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
         float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
         float f2 = f + 4.0F;
         float f3 = f1 + 284.0F + 4.0F;
         float f4 = 472.0F;
         float f5 = 28.0F;
         if (RenderMathUtils.on23(f2, f3, 472.0, 28.0, mouseX, mouseY)) {
            float f6 = (float)((Math.abs(horizontalAmount) > 0.001 ? horizontalAmount : -verticalAmount) * 18.0);
            this.bottomPresetRibbonScrollX += f6;
            this.bottomPresetRibbonScrollX = Math.max(0.0F, Math.min(this.bottomPresetRibbonScrollX, this.bottomPresetRibbonMaxScrollX));
            return true;
         } else {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
         }
      }
   }

   @Override
   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.closing) {
         float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
         float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
         if (!this.handleSettingsPanelClick(var1, var3, var5) && var5 == MenuScreenId.call004) {
            float f2 = f + 4.0F;
            float f3 = 63.5F;
            float f4 = 245.5F;
            float f5 = f2 + 63.5F + 4.0F;
            ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.module.call043();
            if (iiilili1lli1i11lilillliiii1iii != null && iiilili1lli1i11lilillliiii1iii.int395()) {
               float f6 = 52.5F;
               float f7 = 23.0F;
               float f8 = f5 + 245.5F - 52.5F;
               float f9 = f1 + 4.0F;
               if (RenderMathUtils.on23(f8, f9, 52.5, 23.0, var1, var3)) {
                  String s = iiilili1lli1i11lilillliiii1iii.getId();
                  this.module.EventHookWorldRender(s);
                  this.ribbonItemCurrentX.remove(s);
                  this.ribbonItemAppearAnim.remove(s);
                  ZenithClient.on23().TradeGuardService().save();
                  return;
               }
            }

            float f17 = 128.0F;
            float f18 = f5 + 245.5F + 4.0F;
            float f19 = f1 + 4.0F;
            float f20 = 23.0F;
            if (RenderMathUtils.on23(f18, f19, 128.0, 23.0, var1, var3)) {
               this.getSearchBox().VelocityChangeEvent(true);
            } else {
               float f10 = f18 + 128.0F + 4.0F;
               float f11 = f1 + 4.0F;
               float f12 = 23.0F;
               if (RenderMathUtils.on23(f10, f11, 23.0, 23.0, var1, var3)) {
                  this.settingsPanelVisible = !this.settingsPanelVisible;
               } else {
                  float f13 = f + 4.0F;
                  float f14 = f1 + 284.0F + 4.0F;
                  float f15 = 472.0F;
                  float f16 = 28.0F;
                  if (RenderMathUtils.on23(f13, f14, 472.0, 28.0, var1, var3)) {
                     boolean flag = "custom".equalsIgnoreCase(this.module.call056());
                     AutoCraftEditorScreen_RibbonLayoutData autocrafteditorscreen_ribbonlayoutdata = this.buildRibbonLayoutData(f13, f14, flag, false);

                     for (AutoCraftEditorScreen_RibbonItemLayout autocrafteditorscreen_ribbonitemlayout : autocrafteditorscreen_ribbonlayoutdata.items) {
                        if (!(autocrafteditorscreen_ribbonitemlayout.appearProgress <= 0.5F)
                           && RenderMathUtils.on23(
                              autocrafteditorscreen_ribbonitemlayout.currentX,
                              autocrafteditorscreen_ribbonitemlayout.y,
                              autocrafteditorscreen_ribbonitemlayout.width,
                              autocrafteditorscreen_ribbonitemlayout.height,
                              var1,
                              var3
                           )) {
                           this.module.EventItemRenderHook(autocrafteditorscreen_ribbonitemlayout.presetId);
                           this.scheduleSelectionSave();
                           return;
                        }
                     }

                     if (flag
                        && this.isAddLeftEdgeVisibleInRibbon(autocrafteditorscreen_ribbonlayoutdata.addX, f13, 472.0F)
                        && RenderMathUtils.on23(
                           autocrafteditorscreen_ribbonlayoutdata.addX,
                           autocrafteditorscreen_ribbonlayoutdata.addY,
                           autocrafteditorscreen_ribbonlayoutdata.addWidth,
                           autocrafteditorscreen_ribbonlayoutdata.addHeight,
                           var1,
                           var3
                        )) {
                        this.rememberUiStateForNextOpen();
                        this.module.call395();
                        return;
                     }
                  }

                  this.getSearchBox().VelocityChangeEvent(false);
               }
            }
         }
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (!this.closing && this.settingsPanelVisible) {
         for (GuiSetting guisetting : this.settingsPanelControls) {
            guisetting.onMouseReleased(var1, var3, var5);
         }

         this.saveAutoCraftSettings();
      }
   }

   @Override
   public void onMouseDragged(double var1, double var3, MenuScreenId var5, double var6, double var8) {
      if (!this.closing) {
         super.onMouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.closing) {
         return false;
      } else if (this.getSearchBox().charTyped(chr, modifiers)) {
         this.syncSearchFromBox();
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.syncSearchFromBox();
      this.flushSelectionSaveIfReady();
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public boolean shouldPause() {
      return false;
   }

   public void openClickGui() {
      if (!Menu.menu.isEnabled()) {
         Menu.menu.toggle();
      } else {
         minecraftClient3.setScreen(ZenithClient.on23().NbtEditor());
      }
   }

   public void close() {
      if (!this.closing) {
         this.flushSelectionSaveNow();
         this.closing = true;
         this.pendingCloseAction = this::openClickGui;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.closing) {
         return false;
      } else {
         SearchBox i1lil1lliilli1lli1l = this.getSearchBox();
         if (i1lil1lliilli1lli1l.isSelected() && keyCode == 256) {
            i1lil1lliilli1lli1l.VelocityChangeEvent(false);
            return true;
         } else if (i1lil1lliilli1lli1l.keyPressed(keyCode, scanCode, modifiers)) {
            this.syncSearchFromBox();
            return true;
         } else if (keyCode == 256) {
            this.close();
            return true;
         } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      }
   }

   public float getMainPanelX() {
      return (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
   }

   public float getMainPanelY() {
      return (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
   }

   public float getSettingsPanelY() {
      return this.getMainPanelY();
   }

   public float getSettingsPanelW() {
      return 128.0F;
   }

   public float getSettingsPanelH() {
      float f = 0.0F;

      for (int i = 0; i < this.settingsPanelControls.size(); i++) {
         f += this.settingsPanelControls.get(i).getHeight();
         if (i < this.settingsPanelControls.size() - 1) {
            f += 6.0F;
         }
      }

      return 29.0F + f + 8.0F;
   }

   public static float getSettingsControlWidth() {
      return 112.0F;
   }

   public float getSettingsContentX(float var1) {
      return this.getSettingsPanelAnimatedX(var1) + 8.0F;
   }

   public float getSettingsContentY() {
      return this.getSettingsPanelY() + 23.0F + 6.0F;
   }

   public boolean handleSettingsPanelClick(double var1, double var3, MenuScreenId var5) {
      float f = this.getSettingsPanelRevealProgress();
      if (this.settingsPanelVisible && !(f <= 0.0F)) {
         for (GuiSetting guisetting : this.settingsPanelControls) {
            if (guisetting.onMousePriorityClicked(var1, var3, var5)) {
               this.saveAutoCraftSettings();
               return true;
            }
         }

         float f1 = this.getSettingsPanelAnimatedX(f);
         if (!RenderMathUtils.on23(f1, this.getSettingsPanelY(), this.getSettingsPanelW(), this.getSettingsPanelH(), var1, var3)) {
            return false;
         }

         for (GuiSetting guisetting1 : this.settingsPanelControls) {
            if (guisetting1.onMouseClicked(var1, var3, var5)) {
               this.saveAutoCraftSettings();
               return true;
            }
         }

         this.saveAutoCraftSettings();
         return true;
      } else {
         return false;
      }
   }

   public float getSettingsPanelRevealProgress() {
      return Math.max(0.0F, Math.min(1.0F, this.settingsPanelRevealAnimation.CancellableEvent()));
   }

   public float getSettingsPanelAnimatedX(float var1) {
      float f = this.getMainPanelX() + 480.0F + 10.0F;
      float f1 = 138.0F;
      return f - (1.0F - var1) * f1;
   }

   public void saveAutoCraftSettings() {
      ZenithClient.on23().TradeGuardService().save();
   }

   public void scheduleSelectionSave() {
      this.selectionSavePending = true;
      this.selectionSaveAtMs = System.currentTimeMillis() + 650L;
   }

   public void flushSelectionSaveIfReady() {
      if (this.selectionSavePending && System.currentTimeMillis() >= this.selectionSaveAtMs) {
         this.flushSelectionSaveNow();
      }
   }

   public void flushSelectionSaveNow() {
      if (this.selectionSavePending) {
         this.selectionSavePending = false;
         this.saveAutoCraftSettings();
      }
   }

   public AutoCraftEditorScreen_RibbonLayoutData buildRibbonLayoutData(float var1, float var2, boolean var3, boolean var4) {
      this.ensureRibbonAnimationProfile();
      List<ItemFilterRules> list = this.module.EventGetFogColorHook(this.module.call056());
      HashSet hashset = new HashSet();

      for (ItemFilterRules iiilili1lli1i11lilillliiii1iii : list) {
         String s = iiilili1lli1i11lilillliiii1iii.getId();
         if (s != null && !s.isBlank()) {
            hashset.add(s);
         }
      }

      this.ribbonItemAppearAnim.keySet().removeIf(var1x -> !hashset.contains(var1x));
      this.ribbonItemCurrentX.keySet().removeIf(var1x -> !hashset.contains(var1x));
      String s3 = this.getRibbonSearchNeedle();
      float f7 = this.getRibbonItemY(var2);
      float f8 = var1;
      ArrayList arraylist = new ArrayList(list.size());

      for (ItemFilterRules iiilili1lli1i11lilillliiii1iii1 : list) {
         String s1 = iiilili1lli1i11lilillliiii1iii1.getId();
         if (s1 != null && !s1.isBlank()) {
            boolean flag = this.matchesRibbonSearch(iiilili1lli1i11lilillliiii1iii1, s3);
            UiAnimation l1i1illlili = this.ribbonItemAppearAnim.get(s1);
            if (l1i1illlili == null) {
               if (!flag) {
                  continue;
               }

               l1i1illlili = new UiAnimation(160L, 0.0F, Easing.HotbarInputEvent);
               this.ribbonItemAppearAnim.put(s1, l1i1illlili);
            }

            if (var4) {
               l1i1illlili.Easing(flag ? 1.0F : 0.0F);
               l1i1illlili.EmotePlayback();
            }

            float f = this.clampRibbon01(l1i1illlili.CancellableEvent());
            if (!flag && f < 0.001F) {
               this.ribbonItemAppearAnim.remove(s1);
               this.ribbonItemCurrentX.remove(s1);
            } else {
               String s2 = this.resolveRibbonPresetLabel(iiilili1lli1i11lilillliiii1iii1);
               float f1 = this.computeRibbonItemWidth(s2);
               float f2 = f8;
               float f3 = f1 * f;
               float f4 = 4.0F * f;
               f8 += f3 + f4;
               float f5 = this.ribbonItemCurrentX.getOrDefault(s1, f2);
               if (var4) {
                  f5 = this.animateRibbonCurrentX(f5, f2);
               }

               this.ribbonItemCurrentX.put(s1, f5);
               float f6 = f5 - this.bottomPresetRibbonScrollX;
               arraylist.add(new AutoCraftEditorScreen_RibbonItemLayout(iiilili1lli1i11lilillliiii1iii1, s1, s2, f6, f7, f3, 23.0F, f, flag));
            }
         }
      }

      float f9 = Math.max(0.0F, f8 - var1);
      float f10 = 0.0F;
      float f11 = f8 - this.bottomPresetRibbonScrollX;
      if (var3) {
         f10 = this.computeRibbonItemWidth("Add");
         f9 += f10;
      }

      return new AutoCraftEditorScreen_RibbonLayoutData(arraylist, f9, f11, f7, f10, 23.0F);
   }

   public void initializeRibbonAnimationState() {
      this.ribbonItemCurrentX.clear();
      this.ribbonItemAppearAnim.clear();
      List<ItemFilterRules> list = this.module.EventGetFogColorHook(this.module.call056());
      String s = this.getRibbonSearchNeedle();
      boolean flag = minecraftClient3 != null && minecraftClient3.getWindow() != null;
      float f = flag ? this.getMainPanelX() + 4.0F : 0.0F;

      for (ItemFilterRules iiilili1lli1i11lilillliiii1iii : list) {
         String s1 = iiilili1lli1i11lilillliiii1iii.getId();
         if (s1 != null && !s1.isBlank()) {
            boolean flag1 = this.matchesRibbonSearch(iiilili1lli1i11lilillliiii1iii, s);
            UiAnimation l1i1illlili = new UiAnimation(160L, flag1 ? 1.0F : 0.0F, Easing.HotbarInputEvent);
            l1i1illlili.setValue(flag1 ? 1.0F : 0.0F);
            this.ribbonItemAppearAnim.put(s1, l1i1illlili);
            if (flag1 && flag) {
               String s2 = this.resolveRibbonPresetLabel(iiilili1lli1i11lilillliiii1iii);
               float f1 = this.computeRibbonItemWidth(s2);
               this.ribbonItemCurrentX.put(s1, f);
               f += f1 + 4.0F;
            }
         }
      }

      this.ribbonAnimationInitialized = true;
   }

   public void ensureRibbonAnimationProfile() {
      String s = this.getRibbonProfileKey();
      if (!this.ribbonAnimationInitialized || !s.equals(this.ribbonAnimationProfile)) {
         this.ribbonAnimationProfile = s;
         this.initializeRibbonAnimationState();
      }
   }

   public String getRibbonProfileKey() {
      String s = this.module.call056();
      return s == null ? "" : s.toLowerCase(Locale.ROOT);
   }

   public String getRibbonSearchNeedle() {
      return this.searchQuery != null && !this.searchQuery.isBlank() ? this.searchQuery.toLowerCase(Locale.ROOT) : "";
   }

   public boolean matchesRibbonSearch(ItemFilterRules var1, String var2) {
      if (var2.isEmpty()) {
         return true;
      }

      String s = this.resolveRibbonPresetLabel(var1);
      return !s.isBlank() && s.toLowerCase(Locale.ROOT).contains(var2);
   }

   public float animateRibbonCurrentX(float var1, float var2) {
      float f = var2 - var1;
      float f1 = Math.abs(f);
      if (f1 < 0.05F) {
         return var2;
      }

      float f2 = Math.max(0.5F, f1 * 0.25F);
      f2 = Math.min(f2, f1);
      return var1 + Math.signum(f) * f2;
   }

   public float clampRibbon01(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   public String sanitizeSearchInput(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringBuilder stringbuilder = new StringBuilder();

         for (int i = 0; i < var1.length(); i++) {
            char c0 = var1.charAt(i);
            if (!Character.isISOControl(c0)) {
               stringbuilder.append(c0);
            }
         }

         return stringbuilder.toString();
      } else {
         return "";
      }
   }

   public void setSearchQuery(String var1) {
      String s = var1 == null ? "" : this.sanitizeSearchInput(var1);
      if (s.length() > 64) {
         s = s.substring(0, 64);
      }

      if (!s.equals(this.searchQuery)) {
         this.searchQuery = s;
         this.bottomPresetRibbonScrollX = 0.0F;
      }
   }

   public SearchBox getSearchBox() {
      if (this.searchBox == null) {
         this.searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), font6, "Search for item...", 112.0F);
         this.searchBox.EventItemRenderHook(64);
      }

      return this.searchBox;
   }

   public void syncSearchFromBox() {
      SearchBox i1lil1lliilli1lli1l = this.getSearchBox();
      this.setSearchQuery(i1lil1lliilli1lli1l.getText());
      if (!this.searchQuery.equals(i1lil1lliilli1lli1l.getText())) {
         i1lil1lliilli1lli1l.HudHotbarPanel(this.searchQuery);
         i1lil1lliilli1lli1l.EventRender(this.searchQuery.length());
      }
   }

   public void rememberUiStateForNextOpen() {
      restoreUiOnNextOpen = true;
      restoreSearchQuery = this.searchQuery;
      restoreRibbonScrollX = this.bottomPresetRibbonScrollX;
      restoreSettingsPanelVisible = this.settingsPanelVisible;
   }

   public String resolveRibbonPresetLabel(ItemFilterRules var1) {
      String s = var1.on23(this.module);
      if (s == null || s.isBlank()) {
         s = var1.getDisplayName();
      }

      return s == null ? "" : s;
   }

   public float computeRibbonItemWidth(String var1) {
      float f = 8.0F;
      float f1 = 4.0F;
      float f2 = 4.0F;
      float f3 = 6.0F;
      float f4 = 60.5F;
      return Math.max(60.5F, 16.0F + font6.width(var1) + 6.0F);
   }

   public float getRibbonItemY(float var1) {
      return var1;
   }

   public boolean isAddLeftEdgeVisibleInRibbon(float var1, float var2, float var3) {
      return var1 >= var2 && var1 <= var2 + var3;
   }

   public List<String> getUniqueIngredientKeys(ItemFilterRules var1) {
      this.ingredientKeysCache.clear();
      if (var1 == null) {
         return this.ingredientKeysCache;
      }

      for (int i = 0; i < 9; i++) {
         String s = var1.DataChangedEvent(i);
         if (s != null && !s.isBlank()) {
            String s1 = var1.ChatMessageEvent(i);
            if (!this.ingredientKeysCache.contains(s1)) {
               this.ingredientKeysCache.add(s1);
            }
         }
      }

      return this.ingredientKeysCache;
   }
}
