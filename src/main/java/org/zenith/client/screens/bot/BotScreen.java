package org.zenith.client.screens.bot;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.base.bot.BotAvatarCache;
import org.zenith.base.bot.ServerIconCache;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.client.HeadlessBots_ProxyPingSnapshot;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.via.BotProtocolVersions;
import org.zenith.base.bot.via.BotProtocolVersions_Entry;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Font;
import org.zenith.base.font.FontData_MetricsData;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.hud.SearchBox;
import org.zenith.hud.SearchBox;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class BotScreen extends CustomScreen {
   public static final float leftPanelWidth = 240.0F;
   public static final float rightPanelWidth = 240.0F;
   public static final float panelWidth = 480.0F;
   public static final float panelHeight = 320.0F;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(10.0F);
   public static final CornerRadius leftPanelRadius = CornerRadius.BotTickEvent(10.0F, 10.0F);
   public static final CornerRadius rightPanelRadius = CornerRadius.VelocityChangeEvent(10.0F, 10.0F);
   public static final long screenOpenAnimationDuration = 300L;
   public static final long screenCloseAnimationDuration = 220L;
   public static final float screenAnimationEpsilon = 0.001F;
   public static final float screenStartScale = 0.88F;
   public static final float screenStartOffsetY = 10.0F;
   public static final long popupAnimationDuration = 180L;
   public static final float shadowOffsetX = 0.0F;
   public static final float shadowOffsetY = 16.0F;
   public static final float shadowBlur = 32.0F;
   public static final float shadowSpread = -16.0F;
   public static final ArgbColor shadowColor = new ArgbColor(12, 12, 12, 20);
   public static final float blurRadius = 32.0F;
   public static final ArgbColor blurColor = new ArgbColor(255, 255, 255, 255);
   public static final ArgbColor leftColor = new ArgbColor(14, 14, 16, 122);
   public static final float headerOffset = 4.0F;
   public static final float headerWidth = 232.0F;
   public static final float headerHeight = 23.0F;
   public static CornerRadius headerRadius = CornerRadius.MovementInputEvent(4.0F);
   public static final ArgbColor headerColor = new ArgbColor(14, 14, 16, 122);
   public static final float addButtonWidth = 23.0F;
   public static final ArgbColor addButtonColor = new ArgbColor(14, 14, 16, 61);
   public static final float searchSplitGap = 4.0F;
   public static final float searchHalfWidth = 114.0F;
   public static final float rowGap = 4.0F;
   public static final float searchOffsetY = 31.0F;
   public static final float listRowWidth = 227.0F;
   public static final float listStartY = 58.0F;
   public static final float listRowGap = 2.0F;
   public static final float listRowStride = 25.0F;
   public static final float listViewportHeight = 211.0F;
   public static final float bulkActionTabsOffsetY = 275.0F;
   public static final float bulkActionModeHeight = 14.0F;
   public static final float bulkActionModeButtonSize = 14.0F;
   public static final float bulkActionModeGap = 3.0F;
   public static final CornerRadius bulkActionModeRadius = CornerRadius.MovementInputEvent(4.0F);
   public static final float bulkActionPayloadOffsetY = 292.0F;
   public static final float bulkActionRunWidth = 64.0F;
   public static final int botNameMinLength = 3;
   public static final int botNameMaxLength = 16;
   public static final int rctMinAnarchy = 1;
   public static final int rctMaxAnarchy = 66;
   public static final float listScrollStep = 25.0F;
   public static final float scrollbarGap = 4.0F;
   public static final float scrollbarWidth = 1.0F;
   public static final float scrollbarMinThumb = 16.0F;
   public static final CornerRadius scrollbarRadius = CornerRadius.MovementInputEvent(0.5F);
   public static final ArgbColor scrollbarTrackColor = new ArgbColor(255, 255, 255, 5);
   public static final ArgbColor scrollbarThumbColor = new ArgbColor(255, 255, 255, 10);
   public static final float listActionWidth = 64.0F;
   public static final float serverIconSize = 7.0F;
   public static final float serverIconGap = 4.0F;
   public static final CornerRadius serverIconRadius = CornerRadius.MovementInputEvent(2.0F);
   public static final ArgbColor listColor = new ArgbColor(14, 14, 16, 61);
   public static final float avatarSize = 15.0F;
   public static final CornerRadius avatarRadius = CornerRadius.MovementInputEvent(1.9565217F);
   public static final float rightHeaderWidth = 232.0F;
   public static final float rightHeaderPanelWidth = 157.0F;
   public static final float rightHeaderPanelHeight = 23.0F;
   public static final float rightHeaderPanelX = 319.0F;
   public static final float screenGap = 12.0F;
   public static final float screenOffsetY = 39.0F;
   public static final float screenWidth = 232.0F;
   public static final float screenHeight = 130.5F;
   public static final float screenOffsetX = 244.0F;
   public static final float chatGap = 4.0F;
   public static final float chatHeight = 107.5F;
   public static final float chatPadding = 4.0F;
   public static final float chatLineGap = 1.0F;
   public static final float chatTextOffset = 8.0F;
   public static final float chatScrollStep = 14.0F;
   public static final long chatAppearDuration = 260L;
   public static final float inputGap = 4.0F;
   public static final float inputHeight = 23.0F;
   public static final float inputButtonWidth = 48.0F;
   public static final ArgbColor inputEmptyColor = new ArgbColor(255, 255, 255, 184);
   public static final Font nickFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font timeFont = Fonts.REGULAR.getFont(5.0F);
   public static final Font botIconFont = Fonts.NEW_ICONS.getFont(7.0F);
   public static final String botIcon = "P";
   public static final String addIcon = "D";
   public static final Font connectIconFont = Fonts.NEW_ICONS.getFont(6.0F);
   public static final String connectIcon = "Z";
   public static final String controlIcon = "P";
   public static final String offlineIcon = "]";
   public static final String sendIcon = "d";
   public static final String rctIcon = "7";
   public static final String disconnectIcon = "]";
   public static final ArgbColor disconnectColor = new ArgbColor(255, 99, 99, 255);
   public static final Font roleFont = Fonts.MEDIUM.getFont(5.0F);
   public static final CornerRadius roleBadgeRadius = CornerRadius.MovementInputEvent(3.0F);
   public static final Font moduleMenuFont = Fonts.MEDIUM.getFont(5.0F);
   public static final float moduleMenuWidth = 118.0F;
   public static final float moduleMenuHeaderHeight = 18.0F;
   public static final float moduleMenuRowHeight = 20.0F;
   public static final float moduleMenuPadding = 4.0F;
   public static final float moduleMenuGap = 3.0F;
   public static final CornerRadius moduleMenuRadius = CornerRadius.MovementInputEvent(6.0F);
   public static final CornerRadius moduleMenuRowRadius = CornerRadius.MovementInputEvent(4.0F);
   public static final Font deleteIconFont = Fonts.NEW_ICONS.getFont(8.0F);
   public static final Font proxyDeleteIconFont = Fonts.NEW_ICONS.getFont(4.0F);
   public static final String deleteIcon = "2";
   public static final ArgbColor deleteColor = new ArgbColor(255, 64, 64, 255);
   public static final ArgbColor deleteDarkColor = new ArgbColor(18, 18, 20, 255);
   public static final float deleteDarken = 0.62F;
   public static final float connectIconGap = 4.0F;
   public static final float botIconGap = 3.0F;
   public static final String proxyIcon = "Z";
   public static final float proxyDotSize = 4.0F;
   public static final CornerRadius proxyDotRadius = CornerRadius.MovementInputEvent(2.0F);
   public static final float proxyPanelSpace = 4.0F;
   public static final float proxyPanelSpaceHalf = 2.0F;
   public static final float proxyPanelSpaceDouble = 8.0F;
   public static final float proxyPanelWidth = 156.0F;
   public static final float proxyPanelHeight = 222.0F;
   public static final float proxyPanelGap = 8.0F;
   public static final float proxyPanelButtonSize = 18.0F;
   public static final float proxyPanelCloseHitSize = 14.0F;
   public static final float proxyPanelPadding = 4.0F;
   public static final float proxyPanelHeaderHeight = 27.0F;
   public static final float proxyPanelRowHeight = 23.0F;
   public static final float proxyPanelRowGap = 2.0F;
   public static final float proxyPanelInputGap = 4.0F;
   public static final float proxyPanelInputHeight = 18.0F;
   public static final float proxyPanelInputButtonSize = 18.0F;
   public static final float proxyPanelInputAfterGap = 4.0F;
   public static final float proxyPanelInputOffsetY = 31.0F;
   public static final float proxyPanelListOffsetY = 53.0F;
   public static final float proxyPanelListHeight = 161.0F;
   public static final float proxyPanelDeleteWidth = 18.0F;
   public static final float proxyPanelDeleteGap = 2.0F;
   public static final float proxyPanelScrollbarGap = 4.0F;
   public static final float proxyPanelStatusPadX = 4.0F;
   public static final float proxyPanelStatusInnerGap = 4.0F;
   public static final float proxyPanelStatusSeparatorWidth = 0.5F;
   public static final float proxyPanelStatusSeparatorHeight = 6.0F;
   public static final float proxyPanelScrollStep = 25.0F;
   public static final long proxyPanelAnimationDuration = 220L;
   public static final CornerRadius proxyPanelRadius = CornerRadius.MovementInputEvent(10.0F);
   public static final CornerRadius proxyPanelButtonRadius = CornerRadius.MovementInputEvent(5.0F);
   public static final ArgbColor proxyPingOkColor = new ArgbColor(88, 220, 166, 255);
   public static final ArgbColor proxyPingSlowColor = new ArgbColor(255, 206, 112, 255);
   public static final ArgbColor proxyPingBadColor = new ArgbColor(255, 99, 99, 255);
   public static final float chatLineStepCache = lineHeight(nickFont) + 1.0F;
   public static final float chatTimeTopOffsetCache = nickFont.height() - timeFont.height();
   public static final float chatTimeColumnWidthCache = timeFont.width("00:00:00");
   public static final float statusPadding = 8.0F;
   public static final float statusLineGap = 4.0F;
   public static final Font scoreboardFont = Fonts.NEW_MEDIUM.getFont(5.0F);
   public static final float scoreboardRowGap = 1.5F;
   public static final Font versionFont = Fonts.MEDIUM.getFont(5.0F);
   public static final float versionChipWidth = 34.0F;
   public static final float versionChipGap = 4.0F;
   public static final float versionMenuWidth = 72.0F;
   public static final float versionMenuRowHeight = 14.0F;
   public static final float versionMenuRowGap = 2.0F;
   public static final float versionMenuPadding = 4.0F;
   public static final int versionMenuVisibleRows = 9;
   public final UiAnimation screenAnimation = new UiAnimation(300L, 0.0F, Easing.EventInjectHandleInputEvents);
   public final UiAnimation proxyPanelAnimation = new UiAnimation(220L, 0.0F, Easing.StopUsingItemEvent);
   public final UiAnimation moduleMenuAnimation = new UiAnimation(180L, 0.0F, Easing.PreventActionEvent);
   public final UiAnimation versionMenuAnimation = new UiAnimation(180L, 0.0F, Easing.PreventActionEvent);
   public final Map<String, BotScreen_RowAnim> rowAnims = new HashMap<>();
   public final Map<String, UiAnimation> proxyHoverAnims = new HashMap<>();
   public final Map<ChatMessage, UiAnimation> chatAnims = new IdentityHashMap<>();
   public final Map<ChatMessage, List<Text>> wrapCache = new IdentityHashMap<>();
   public BotClient chatAnimClient;
   public boolean closing = false;
   public static boolean savedProxyPanelOpen = false;
   public boolean proxyPanelOpen = savedProxyPanelOpen;
   public String selectedName;
   public final LinkedHashSet<String> selectedNames = new LinkedHashSet<>();
   public float chatScroll;
   public float listScroll;
   public float proxyPanelScroll;
   public final SearchBox chatInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Message...", 0.0F);
   public final SearchBox connectInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Connect to...", 0.0F);
   public final SearchBox addInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Add...", 0.0F);
   public final SearchBox searchInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Search...", 0.0F);
   public final SearchBox bulkActionInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Value...", 0.0F);
   public final SearchBox proxyManagerInput = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "Proxy...", 0.0F);
   public BotScreen_BulkAction bulkAction = BotScreen_BulkAction.CONNECT;
   public String moduleMenuBotName;
   public float moduleMenuX;
   public float moduleMenuY;
   public float connectButtonWidth;
   public boolean versionMenuOpen;
   public float versionMenuScroll;
   public BotWorldView previewView;
   public BotClient previewClient;
   public static final Map<String, String> serverLabelCache = new HashMap<>();
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   protected void init() {
      super.init();
      this.closing = false;
      this.screenAnimation.on23(300L);
      this.screenAnimation.on23(Easing.EventInjectHandleInputEvents);
      this.screenAnimation.setValue(0.0F);
      this.screenAnimation.on23(1.0F);
      this.moduleMenuAnimation.setValue(this.moduleMenuBotName == null ? 0.0F : 1.0F);
      this.versionMenuAnimation.setValue(this.versionMenuOpen ? 1.0F : 0.0F);
      this.connectButtonWidth = 23.0F;
      this.chatInput.HudInventoryPanel(tr("module.bot.message"));
      this.connectInput.HudInventoryPanel(tr("module.bot.connectTo"));
      this.connectInput.on23(SearchBox.MatchMode.val180);
      if (this.connectInput.getText().isBlank()) {
         ServerInfo serverinfo = minecraftClient3.getCurrentServerEntry();
         if (serverinfo != null && serverinfo.address != null && !serverinfo.address.isBlank()) {
            this.connectInput.HudHotbarPanel(serverinfo.address.trim());
            this.connectInput.EventRender(this.connectInput.getText().length());
         }
      }

      this.addInput.HudInventoryPanel(tr("module.bot.add"));
      this.addInput.on23(SearchBox.MatchMode.val296);
      this.addInput.EventItemRenderHook(16);
      this.searchInput.HudInventoryPanel(tr("module.bot.search"));
      this.bulkActionInput.EventItemRenderHook(256);
      this.proxyManagerInput.HudInventoryPanel(tr("module.bot.proxy"));
      this.proxyManagerInput.EventItemRenderHook(256);
      this.proxyManagerInput.on23(SearchBox.MatchMode.val178);
      if (this.selectedName == null) {
         List<String> list = HeadlessBots.allNames();
         if (!list.isEmpty()) {
            this.selectedName = list.get(0);
         }
      }

      if (this.selectedName != null && this.selectedNames.isEmpty()) {
         this.selectedNames.add(this.selectedName);
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3) {
   }

   public void renderTop(HudDrawContext var1, float var2, float var3) {
      float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
      float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
      float f2 = this.screenAnimation.on23(this.closing ? 0.0F : 1.0F);
      if (this.closing && this.screenAnimation.isDone() && f2 <= 0.001F) {
         minecraftClient3.setScreen(null);
      } else {
         float f3 = clamp01(f2);
         float f4 = f + 240.0F;
         float f5 = f1 + 160.0F;
         float f6 = this.closing ? f3 : f2;
         float f7 = 0.88F + 0.120000005F * f6;
         float f8 = 10.0F * (1.0F - f3);
         float f9 = (var2 - f4) / f7 + f4;
         float f10 = (var3 - f5 - f8) / f7 + f5;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f4, f5 + f8);
         var1.getMatrices().scale(f7, f7);
         var1.getMatrices().translate(-f4, -f5);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         this.renderProxyPanel(var1, f, f1, f3, f9, f10);
         this.renderPanels(var1, f, f1, f3, f9, f10);
         this.renderProxyPanelButton(var1, f, f1, f3, f9, f10);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   public void renderPanels(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         ServerIconCache.tick();
         ShapeRenderer.on23(var1.getMatrices(), var2, var3, 480.0F, 320.0F, 0.0F, 16.0F, 32.0F, -16.0F, panelRadius, shadowColor.SprintStateEvent(var4));
         this.renderPanelBlur(var1, var2, var3, var4);
         var1.drawRoundedRect(var2, var3, 240.0F, 320.0F, leftPanelRadius, leftColor.SprintStateEvent(var4));
         var1.drawRoundedRect(var2 + 240.0F, var3, 240.0F, 320.0F, rightPanelRadius, zenithstyle.getRightBackground().getColor().SprintStateEvent(var4));
         var1.drawRoundedRect(var2 + 4.0F, var3 + 4.0F, 232.0F, 23.0F, headerRadius, headerColor.SprintStateEvent(var4));
         float f = var3 + 31.0F;
         float f1 = centeredTextY(nickFont, f, 23.0F);
         float f2 = var2 + 4.0F;
         var1.drawRoundedRect(f2, f, 114.0F, 23.0F, headerRadius, headerColor.SprintStateEvent(var4));
         float f3 = f2 + 114.0F - 23.0F;
         var1.drawRoundedRect(f3, f, 23.0F, 23.0F, headerRadius, addButtonColor.SprintStateEvent(var4));
         float f4 = botIconFont.width("D");
         var1.drawText(botIconFont, "D", f3 + (23.0F - f4) / 2.0F, centeredTextY(botIconFont, f, 23.0F), inputEmptyColor.SprintStateEvent(var4));
         this.addInput.setWidth(75.0F);
         this.addInput.on23(var1, f2 + 8.0F, f1, ArgbColor.var11934.SprintStateEvent(var4), inputEmptyColor.SprintStateEvent(var4));
         float f5 = f2 + 114.0F + 4.0F;
         var1.drawRoundedRect(f5, f, 114.0F, 23.0F, headerRadius, headerColor.SprintStateEvent(var4));
         this.searchInput.setWidth(98.0F);
         this.searchInput.on23(var1, f5 + 8.0F, f1, ArgbColor.var11934.SprintStateEvent(var4), inputEmptyColor.SprintStateEvent(var4));
         List<String> list = HeadlessBots.allNames();
         this.refreshSelection(list);
         if (!this.rowAnims.isEmpty()) {
            Iterator<String> iterator = this.rowAnims.keySet().iterator();

            while (iterator.hasNext()) {
               String s = iterator.next();
               boolean flag = false;

               for (String s1 : list) {
                  if (s1.equals(s)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  iterator.remove();
               }
            }
         }

         List<String> list1 = this.filterBots(list);
         float f25 = var3 + 58.0F;
         float f26 = f25 + 211.0F;
         this.listScroll = Math.max(0.0F, Math.min(this.listScroll, listMaxScroll(list1.size())));
         var1.enableScissor(var2 + 4.0F, f25, var2 + 4.0F + 227.0F, f26);

         for (int i = 0; i < list1.size(); i++) {
            this.renderListRow(var1, list1.get(i), i, var2, f25, f26, var4, var5, var6, zenithstyle);
         }

         var1.disableScissor();
         float f27 = var2 + 4.0F + 227.0F + 4.0F;
         var1.drawRoundedRect(f27, f25, 1.0F, 211.0F, scrollbarRadius, scrollbarTrackColor.SprintStateEvent(var4));
         float f28 = listContentHeight(list1.size());
         float f6 = f28 <= 211.0F ? 211.0F : Math.max(16.0F, 44521.0F / f28);
         float f7 = listMaxScroll(list1.size());
         float f8 = f25 + (f7 > 0.0F ? this.listScroll / f7 : 0.0F) * (211.0F - f6);
         var1.drawRoundedRect(f27, f8, 1.0F, f6, scrollbarRadius, scrollbarThumbColor.SprintStateEvent(var4));
         this.renderBulkActionTabs(var1, var2, var3 + 275.0F, var4, zenithstyle);
         this.renderBulkActionPayload(var1, var2, var3 + 292.0F, var4);
         var1.drawRoundedRect(
            var2 + 240.0F + 4.0F, var3 + 4.0F, 232.0F, 23.0F, headerRadius, zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var4)
         );
         var1.drawRoundedRect(
            var2 + 240.0F + 4.0F, var3 + 4.0F, 23.0F, 23.0F, headerRadius, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var4)
         );
         boolean flag1 = this.selectedNames.size() > 1;
         if (flag1) {
            float f9 = botIconFont.width("P");
            var1.drawText(
               botIconFont,
               "P",
               var2 + 240.0F + 4.0F + (23.0F - f9) / 2.0F,
               centeredTextY(botIconFont, var3 + 4.0F, 23.0F),
               zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var4)
            );
            var1.drawText(
               nickFont,
               tr("module.bot.selected") + ": " + this.selectedNames.size(),
               var2 + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, var3 + 4.0F, 23.0F),
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var4)
            );
         } else if (this.selectedName != null) {
            Identifier identifier = BotAvatarCache.prewarm(this.selectedName);
            if (identifier != null) {
               float f10 = var2 + 240.0F + 4.0F + 4.0F;
               float f11 = var3 + 4.0F + 4.0F;
               ShapeRenderer.on23(var1.getMatrices(), identifier, f10, f11, 15.0F, 15.0F, avatarRadius, ArgbColor.var11934.SprintStateEvent(var4));
            }

            var1.drawText(
               nickFont,
               this.selectedName,
               var2 + 240.0F + 4.0F + 23.0F + 8.0F,
               centeredTextY(nickFont, var3 + 4.0F, 23.0F),
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var4)
            );
         }

         float f29 = nickFont.width("Bot");
         float f30 = botIconFont.width("P");
         float f31 = var2 + 4.0F + (232.0F - f30 - 3.0F - f29) / 2.0F;
         var1.drawText(botIconFont, "P", f31, centeredTextY(botIconFont, var3 + 4.0F, 23.0F), zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var4));
         var1.drawText(nickFont, "Bot", f31 + f30 + 3.0F, centeredTextY(nickFont, var3 + 4.0F, 23.0F), ArgbColor.var11934.SprintStateEvent(var4));
         BotClient botclient = this.selectedClient();
         boolean flag2 = botclient != null && botclient.isJoined();
         float f12 = var2 + 319.0F;
         float f13 = var3 + 4.0F;
         float f14 = f12 + 157.0F - this.connectButtonWidth;
         float f15 = f14 - 4.0F - 34.0F;
         this.connectInput.setWidth(157.0F - this.connectButtonWidth - 34.0F - 4.0F - 15.0F - 8.0F);
         this.connectInput
            .on23(
               var1,
               f12 + 15.0F,
               centeredTextY(nickFont, f13, 23.0F),
               zenithstyle.getTextEnable().getColor().SprintStateEvent(var4),
               zenithstyle.getTextSecondary().getColor().SprintStateEvent(var4)
            );
         var1.drawRoundedRect(f14, f13, this.connectButtonWidth, 23.0F, headerRadius, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var4));
         String s2 = flag2 ? "P" : "Z";
         float f16 = connectIconFont.width(s2);
         var1.drawText(
            connectIconFont,
            s2,
            f14 + (this.connectButtonWidth - f16) / 2.0F,
            centeredTextY(connectIconFont, f13, 23.0F) + 0.5F,
            (flag2 ? zenithstyle.getPrimaryColor().getColor() : zenithstyle.getTextSecondary().getColor()).SprintStateEvent(var4)
         );
         var1.drawRoundedRect(
            f15,
            f13,
            34.0F,
            23.0F,
            headerRadius,
            (this.versionMenuOpen ? zenithstyle.getPrimaryColor().getColor().EventHookWorldRender(42) : zenithstyle.getPanelLeftBackground().getColor())
               .SprintStateEvent(var4)
         );
         String s3 = fitText(versionFont, BotProtocolVersions.label(this.selectedProtocolVersion()), 28.0F);
         ArgbColor i11ii1llliilllii1i1 = this.selectedProtocolVersion() > 0
            ? zenithstyle.getPrimaryColor().getColor()
            : zenithstyle.getTextSecondary().getColor();
         var1.drawText(
            versionFont, s3, f15 + (34.0F - versionFont.width(s3)) / 2.0F, centeredTextY(versionFont, f13, 23.0F), i11ii1llliilllii1i1.SprintStateEvent(var4)
         );
         float f17 = var2 + 244.0F;
         float f18 = var3 + 39.0F + 130.5F + 4.0F;
         float f19 = f18 + 107.5F + 4.0F;
         if (flag2) {
            var1.drawRoundedRect(
               var2 + 244.0F, var3 + 39.0F, 232.0F, 130.5F, headerRadius, zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var4)
            );
            this.renderPreview(var1, botclient, var2 + 244.0F, var3 + 39.0F, var4);
            this.renderStatusPanel(var1, botclient, var2 + 244.0F, var3 + 39.0F, var4, zenithstyle);
            var1.drawRoundedRect(f17, f18, 232.0F, 107.5F, headerRadius, zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var4));
         } else {
            this.closePreview();
            float f20 = 242.0F;
            var1.drawRoundedRect(f17, var3 + 39.0F, 232.0F, f20, headerRadius, zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var4));
            String s4 = tr("module.bot.noConnection");
            float f21 = nickFont.width(s4);
            var1.drawText(
               nickFont,
               s4,
               f17 + (232.0F - f21) / 2.0F,
               centeredTextY(nickFont, var3 + 39.0F, f20),
               zenithstyle.getTextSecondary().getColor().SprintStateEvent(var4)
            );
         }

         if (flag2) {
            float f32 = f17 + 232.0F - 48.0F;
            this.chatInput.setWidth(168.0F);
            this.chatInput
               .on23(
                  var1,
                  f17 + 8.0F,
                  centeredTextY(nickFont, f19, 23.0F),
                  zenithstyle.getTextEnable().getColor().SprintStateEvent(var4),
                  zenithstyle.getTextSecondary().getColor().SprintStateEvent(var4)
               );
            var1.drawRoundedRect(f32, f19, 48.0F, 23.0F, headerRadius, zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var4));
            String s5 = tr("module.bot.send");
            float f33 = nickFont.width(s5);
            float f22 = connectIconFont.width("d");
            float f23 = 2.0F;
            float f24 = f32 + (48.0F - f22 - f23 - f33) / 2.0F;
            var1.drawText(
               connectIconFont, "d", f24, centeredTextY(connectIconFont, f19, 23.0F), zenithstyle.getTextSecondary().getColor().SprintStateEvent(var4)
            );
            var1.drawText(nickFont, s5, f24 + f22 + f23, centeredTextY(nickFont, f19, 23.0F), zenithstyle.getTextSecondary().getColor().SprintStateEvent(var4));
         } else {
            this.chatInput.VelocityChangeEvent(false);
         }

         if (flag2) {
            this.renderChatMessages(var1, botclient, f17, f18, var4, zenithstyle);
         }

         this.renderModuleMenu(var1, var4, var5, var6, zenithstyle);
         this.renderVersionMenu(var1, var2, var3, var4, var5, var6, zenithstyle);
      }
   }

   public void renderStatusPanel(HudDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      float f = var3 + 8.0F;
      float f1 = var4 + 8.0F;
      float f2 = lineHeight(nickFont) + 4.0F;
      var1.drawText(nickFont, var2.getName(), f, f1, var6.getTextEnable().getColor().SprintStateEvent(var5));
      String s = var2.getPhase().name();
      float f3 = roleFont.width(s);
      float f4 = f3 + 8.0F;
      float f5 = var3 + 232.0F - 8.0F - f4;
      var1.drawRoundedRect(f5, f1 - 1.5F, f4, 10.0F, roleBadgeRadius, var6.getPrimaryColor().getColor().EventHookWorldRender(34).SprintStateEvent(var5));
      var1.drawText(roleFont, s, f5 + 4.0F, f1, var6.getPrimaryColor().getColor().SprintStateEvent(var5));
      f1 += f2 + 4.0F;
      this.renderBotScoreboard(var1, var2, var3, var4, var5, var6);
   }

   public void renderBotScoreboard(HudDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      BotWorld botworld = var2.getWorld();
      if (botworld != null) {
         Scoreboard scoreboard = botworld.getScoreboard();
         ScoreboardObjective scoreboardobjective = null;
         Team team = scoreboard.getScoreHolderTeam(var2.getName());
         if (team != null) {
            ScoreboardDisplaySlot scoreboarddisplayslot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
            if (scoreboarddisplayslot != null) {
               scoreboardobjective = scoreboard.getObjectiveForSlot(scoreboarddisplayslot);
            }
         }

         if (scoreboardobjective == null) {
            scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
         }

         if (scoreboardobjective != null) {
            float f3 = lineHeight(scoreboardFont) + 1.5F;
            float f = var4 + 8.0F + 12.0F;
            float f1 = var4 + 130.5F - 8.0F;
            int i = Math.max(0, (int)((f1 - f - f3) / f3));
            if (i > 0) {
               List<Text> arraylist = new ArrayList<>();
               List<ScoreboardEntry> arraylist1 = new ArrayList<>(scoreboard.getScoreboardEntries(scoreboardobjective));
               arraylist1.removeIf(ScoreboardEntry::hidden);
               arraylist1.sort(InGameHud.SCOREBOARD_ENTRY_COMPARATOR);

               for (ScoreboardEntry scoreboardentry : arraylist1) {
                  if (arraylist.size() >= i) {
                     break;
                  }

                  arraylist.add(Team.decorateName(scoreboard.getScoreHolderTeam(scoreboardentry.owner()), scoreboardentry.name()));
               }

               if (!arraylist.isEmpty()) {
                  float f4 = var3 + 232.0F - 8.0F;
                  Text text1 = scoreboardobjective.getDisplayName();
                  var1.drawText(scoreboardFont, text1, f4 - scoreboardFont.width(text1), f, var6.getTextEnable().getColor().SprintStateEvent(var5).call001());
                  float f2 = f + f3 + 1.5F;

                  for (Text text : arraylist) {
                     var1.drawText(scoreboardFont, text, f4 - scoreboardFont.width(text), f2, var6.getTextEnable().getColor().SprintStateEvent(var5).call001());
                     f2 += f3;
                  }
               }
            }
         }
      }
   }

   public List<String[]> statusLines(BotClient var1) {
      List<String[]> arraylist = new ArrayList<>();
      arraylist.add(new String[]{"Server", var1.getAddress()});
      BotPlayer botplayer = var1.getPlayer();
      if (botplayer != null) {
         arraylist.add(new String[]{"Health", String.format(Locale.ROOT, "%.1f", botplayer.getHealth())});
         arraylist.add(new String[]{"Pos", (int)botplayer.getX() + " " + (int)botplayer.getY() + " " + (int)botplayer.getZ()});
      }

      BotWorld botworld = var1.getWorld();
      if (botworld != null) {
         arraylist.add(new String[]{"World", botworld.getRegistryKey().getValue().getPath()});
      }

      arraylist.add(new String[]{"RCT", var1.getRct().isActive() ? "active" : "idle"});
      StringBuilder stringbuilder = new StringBuilder();

      for (BotModule botmodule : var1.getModules().getModules()) {
         if (botmodule.isEnabled()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(botmodule.getName());
         }
      }

      arraylist.add(new String[]{"Modules", stringbuilder.length() == 0 ? "-" : stringbuilder.toString()});
      return arraylist;
   }

   public void renderPreview(HudDrawContext var1, BotClient var2, float var3, float var4, float var5) {
      if (this.previewClient != var2) {
         this.closePreview();
         this.previewClient = var2;
         this.previewView = new BotWorldView(var2);
      }

      double d0 = minecraftClient3.getWindow().getScaleFactor();
      int i = (int)(232.0 * d0);
      int j = (int)(130.5 * d0);
      var1.draw();
      if (this.previewView.renderToFbo(i, j)) {
         ShapeRenderer.on23(
            var1.getMatrices(),
            this.previewView.getColorAttachment(),
            var3,
            var4,
            232.0F,
            130.5F,
            headerRadius,
            ArgbColor.var11934.SprintStateEvent(var5),
            0.0F,
            1.0F,
            1.0F,
            0.0F
         );
         var1.drawRoundedRect(var3, var4, 232.0F, 130.5F, headerRadius, new ArgbColor(10, 10, 14, 150).SprintStateEvent(var5));
      }
   }

   public void closePreview() {
      if (this.previewView != null) {
         this.previewView.close();
         this.previewView = null;
      }

      this.previewClient = null;
   }

   public void removed() {
      this.closePreview();
      ServerIconCache.clear();
      BotAvatarCache.clear();
      super.removed();
   }

   public void renderPanelBlur(HudDrawContext var1, float var2, float var3, float var4) {
      ShapeRenderer.on23(var1.getMatrices(), var2, var3, 480.0F, 320.0F, 32.0F, panelRadius, blurColor.SprintStateEvent(var4), true, false);
   }

   public void renderProxyPanel(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = this.proxyPanelAnimation.on23(this.proxyPanelOpen ? 1.0F : 0.0F);
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null && f > 0.001F) {
         float f1 = proxyPanelRenderX(var2, f);
         float f2 = proxyPanelY(var3);
         float f3 = var4 * f;
         ShapeRenderer.ColorAnimator(var1.getMatrices(), f1, f2 + 8.0F, 156.0F, 222.0F, 16.0F, proxyPanelRadius, shadowColor.SprintStateEvent(f3));
         ShapeRenderer.on23(var1.getMatrices(), f1, f2, 156.0F, 222.0F, 32.0F, proxyPanelRadius, blurColor.SprintStateEvent(f3), true, false);
         var1.drawRoundedRect(f1, f2, 156.0F, 222.0F, proxyPanelRadius, zenithstyle.getRightBackground().getColor().SprintStateEvent(f3));
         var1.drawRoundedRect(f1, f2, 156.0F, 27.0F, CornerRadius.Event29(10.0F), zenithstyle.getHeaderDisableBackground().getColor().SprintStateEvent(f3));
         float f4 = connectIconFont.width("Z");
         float f5 = f1 + 4.0F + 4.0F;
         var1.drawText(connectIconFont, "Z", f5, centeredTextY(connectIconFont, f2, 27.0F), zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f3));
         var1.drawText(
            nickFont,
            tr("module.bot.proxyPanel"),
            f5 + f4 + 4.0F,
            centeredTextY(nickFont, f2, 27.0F),
            zenithstyle.getTextEnable().getColor().SprintStateEvent(f3)
         );
         float f6 = proxyPanelCloseHitX(f1);
         float f7 = proxyPanelCloseHitY(f2);
         boolean flag = var5 >= f6 && var5 <= f6 + 14.0F && var6 >= f7 && var6 <= f7 + 14.0F;
         float f8 = proxyDeleteIconFont.width("2");
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getTextSecondary().getColor(), flag ? 0.9F : 0.0F);
         var1.drawText(
            proxyDeleteIconFont, "2", f6 + (14.0F - f8) / 2.0F, f7 + (14.0F - proxyDeleteIconFont.height()) / 2.0F, i11ii1llliilllii1i1.SprintStateEvent(f3)
         );
         this.renderProxyPanelInput(var1, zenithstyle, f1, f2 + 31.0F, f3);
         this.renderProxyPanelList(var1, zenithstyle, f1, f2 + 53.0F, f3, var5, var6);
      }
   }

   public void renderProxyPanelButton(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = this.proxyPanelAnimation.CancellableEvent();
         float f1 = proxyPanelButtonX(var2);
         float f2 = proxyPanelButtonY(var3);
         boolean flag = var5 >= f1 && var5 <= f1 + 18.0F && var6 >= f2 && var6 <= f2 + 18.0F;
         ArgbColor i11ii1llliilllii1i1 = (this.proxyPanelOpen
               ? zenithstyle.getHeaderDisableBackground().getColor()
               : zenithstyle.getDisableActiveBg().getColor())
            .Easing(zenithstyle.getPrimaryColor().getColor(), !flag && !this.proxyPanelOpen ? 0.0F : 0.18F);
         var1.drawRoundedRect(f1, f2, 18.0F, 18.0F, proxyPanelButtonRadius, i11ii1llliilllii1i1.SprintStateEvent(var4));
         float f3 = connectIconFont.width("Z");
         var1.drawText(
            connectIconFont,
            "Z",
            f1 + (18.0F - f3) / 2.0F,
            centeredTextY(connectIconFont, f2, 18.0F),
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), f > 0.01F ? 1.0F : 0.0F).SprintStateEvent(var4)
         );
      }
   }

   public void renderProxyPanelInput(HudDrawContext var1, ZenithStyle var2, float var3, float var4, float var5) {
      float f = var3 + 4.0F;
      float f1 = 126.0F;
      var1.drawRoundedRect(f, var4, f1, 18.0F, headerRadius, var2.getHeaderHudBackground().getColor().SprintStateEvent(var5));
      this.proxyManagerInput.setWidth(f1 - 16.0F);
      this.proxyManagerInput
         .on23(
            var1,
            f + 8.0F,
            centeredTextY(nickFont, var4, 18.0F),
            var2.getTextEnable().getColor().SprintStateEvent(var5),
            var2.getTextSecondary().getColor().SprintStateEvent(var5)
         );
      float f2 = f + f1 + 4.0F;
      var1.drawRoundedRect(f2, var4, 18.0F, 18.0F, headerRadius, var2.getPrimaryColor().getColor().SprintStateEvent(0.25F).SprintStateEvent(var5));
      float f3 = botIconFont.width("D");
      var1.drawText(
         botIconFont, "D", f2 + (18.0F - f3) / 2.0F + 1.0F, centeredTextY(botIconFont, var4, 18.0F), var2.getPrimaryColor().getColor().SprintStateEvent(var5)
      );
   }

   public void renderProxyPanelList(HudDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, float var7) {
      List<String> list = HeadlessBots.getProxyPool();
      this.pruneProxyHoverAnimations(list);
      int i = list.size() + 1;
      this.proxyPanelScroll = Math.max(0.0F, Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(i)));
      float f = var3 + 4.0F;
      float f1 = proxyPanelRowWidth();
      float f2 = var4 + 161.0F;
      var1.enableScissor(f, var4, f + f1, f2);
      float f3 = var4 - this.proxyPanelScroll;
      this.renderProxyPanelRow(
         var1, proxyNoneKey(), tr("module.bot.proxyNone"), this.noProxyCount(), this.selectionHasNoProxy(), false, f, f3, f1, var4, f2, var5, var6, var7, var2
      );
      f3 += 25.0F;

      for (String s : list) {
         this.renderProxyPanelRow(var1, s, s, HeadlessBots.getProxyUseCount(s), this.selectionUsesProxy(s), true, f, f3, f1, var4, f2, var5, var6, var7, var2);
         f3 += 25.0F;
      }

      var1.disableScissor();
      float f6 = proxyPanelScrollbarX(var3);
      var1.drawRoundedRect(f6, var4, 1.0F, 161.0F, scrollbarRadius, var2.getDisableActiveBg().getColor().SprintStateEvent(var5 * 0.65F));
      if (proxyPanelMaxScroll(i) > 0.0F) {
         float f7 = proxyPanelContentHeight(i);
         float f4 = Math.max(16.0F, 25921.0F / f7);
         float f5 = var4 + this.proxyPanelScroll / proxyPanelMaxScroll(i) * (161.0F - f4);
         var1.drawRoundedRect(f6, f5, 1.0F, f4, scrollbarRadius, var2.getPrimaryColor().getColor().SprintStateEvent(var5));
      }
   }

   public void renderProxyPanelRow(
      HudDrawContext var1,
      String var2,
      String var3,
      int var4,
      boolean var5,
      boolean var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      ZenithStyle var15
   ) {
      boolean flag = var14 >= var10 && var14 <= var11 && var13 >= var7 && var13 <= var7 + var9 && var14 >= var8 && var14 <= var8 + 23.0F;
      boolean flag1 = var6 && flag && var13 >= var7 + var9 - 18.0F;
      UiAnimation l1i1illlili = this.proxyHoverAnims.computeIfAbsent(var2, var0 -> new UiAnimation(150L, 0.0F, Easing.PreventActionEvent));
      float f = l1i1illlili.on23(flag ? 1.0F : 0.0F);
      ArgbColor i11ii1llliilllii1i1 = var15.getHeaderDisableBackground()
         .getColor()
         .Easing(var15.getSurfaceEnableBackground().getColor(), var5 ? 0.9F : f * 0.45F)
         .SprintStateEvent(var12);
      var1.drawRoundedRect(var7, var8, var9, 23.0F, headerRadius, i11ii1llliilllii1i1);
      if (var5) {
         float f1 = var7 + 4.0F;
         float f2 = var8 + 4.0F;
         float f3 = 15.0F;
         var1.drawRoundedRect(f1, f2, 3.0F, f3, scrollbarRadius, var15.getDisableActiveBg().getColor().SprintStateEvent(var12));
         var1.drawRoundedRect(f1, f2, 2.0F, f3, scrollbarRadius, var15.getPrimaryColor().getColor().SprintStateEvent(var12));
      }

      String s2 = String.valueOf(var4);
      float f14 = roleFont.width(s2);
      HeadlessBots_ProxyPingSnapshot headlessbots_proxypingsnapshot = var6 ? HeadlessBots.getProxyPing(var2) : null;
      String s = headlessbots_proxypingsnapshot == null ? "" : headlessbots_proxypingsnapshot.label();
      float f4 = headlessbots_proxypingsnapshot == null ? 0.0F : roleFont.width(s);
      float f5 = 8.0F + f14;
      if (var6) {
         f5 += f4 + 8.0F + 0.5F;
      }

      float f6 = var6 ? 20.0F : 8.0F;
      float f7 = var7 + var9 - f5 - f6;
      float f8 = 10.0F;
      float f9 = var8 + (23.0F - f8) / 2.0F;
      float f10 = var7 + 8.0F + 2.0F + f;
      String s1 = fitText(nickFont, var3, Math.max(12.0F, f7 - f10 - 4.0F));
      var1.drawText(nickFont, s1, f10, centeredTextY(nickFont, var8, 23.0F), var15.getTextEnable().getColor().SprintStateEvent(var12));
      var1.drawRoundedRect(f7, f9, f5, f8, roleBadgeRadius, var15.getPrimaryColor().getColor().EventHookWorldRender(34).SprintStateEvent(var12));
      if (var6) {
         var1.drawText(
            roleFont, s, f7 + 4.0F, centeredTextY(roleFont, var8, 23.0F), proxyPingColor(headlessbots_proxypingsnapshot, var15).SprintStateEvent(var12)
         );
         float f11 = f7 + 4.0F + f4 + 4.0F;
         float f12 = var8 + 8.5F;
         var1.drawRect(f11, f12, 0.5F, 6.0F, var15.getDisableActiveBg().getColor().SprintStateEvent(var12));
      }

      var1.drawText(roleFont, s2, f7 + f5 - 4.0F - f14, centeredTextY(roleFont, f9, f8), var15.getPrimaryColor().getColor().SprintStateEvent(var12));
      if (var6) {
         float f15 = proxyDeleteIconFont.width("2");
         float f16 = var7 + var9 - f15 - 8.0F;
         float f13 = var8 + (23.0F - proxyDeleteIconFont.height()) / 2.0F;
         ArgbColor i11ii1llliilllii1i11 = var15.getTextTertiary().getColor().Easing(var15.getTextSecondary().getColor(), flag1 ? 0.9F : f * 0.35F);
         var1.drawText(proxyDeleteIconFont, "2", f16, f13, i11ii1llliilllii1i11.SprintStateEvent(var12));
      }
   }

   public static float proxyPanelRenderX(float var0, float var1) {
      return var0 - 8.0F - 156.0F * var1;
   }

   public static float proxyPanelListWidth() {
      return 148.0F;
   }

   public static float proxyPanelRowWidth() {
      return proxyPanelListWidth() - 1.0F - 4.0F;
   }

   public static float proxyPanelScrollbarX(float var0) {
      return var0 + 156.0F - 4.0F - 1.0F;
   }

   public static float proxyPanelY(float var0) {
      return var0;
   }

   public static float proxyPanelButtonX(float var0) {
      return var0 + 4.0F + 2.5F;
   }

   public static float proxyPanelButtonY(float var0) {
      return var0 + 4.0F + 2.5F;
   }

   public static float proxyPanelCloseHitX(float var0) {
      return var0 + 156.0F - 4.0F - 14.0F;
   }

   public static float proxyPanelCloseHitY(float var0) {
      return var0 + 6.5F;
   }

   public static float proxyPanelContentHeight(int var0) {
      return var0 <= 0 ? 0.0F : var0 * 25.0F - 2.0F;
   }

   public static float proxyPanelMaxScroll(int var0) {
      return Math.max(0.0F, proxyPanelContentHeight(var0) - 161.0F);
   }

   public static ArgbColor proxyPingColor(HeadlessBots_ProxyPingSnapshot var0, ZenithStyle var1) {
      if (var0 == null) {
         return var1.getTextTertiary().getColor();
      }

      return switch (var0.status()) {
         case OK -> var0.latencyMs() <= 750 ? proxyPingOkColor : proxyPingSlowColor;
         case FAILED -> proxyPingBadColor;
         case CHECKING, UNKNOWN -> var1.getTextTertiary().getColor();
      };
   }

   public static String proxyNoneKey() {
      return "__none__";
   }

   public int noProxyCount() {
      int i = 0;

      for (String s : HeadlessBots.allNames()) {
         if (!HeadlessBots.hasProxy(s)) {
            i++;
         }
      }

      return i;
   }

   public boolean selectionHasNoProxy() {
      List<String> list = this.selectionNames();
      if (list.isEmpty()) {
         return false;
      }

      for (String s : list) {
         if (HeadlessBots.hasProxy(s)) {
            return false;
         }
      }

      return true;
   }

   public boolean selectionUsesProxy(String var1) {
      List<String> list = this.selectionNames();
      if (!list.isEmpty() && var1 != null && !var1.isBlank()) {
         for (String s : list) {
            String s1 = HeadlessBots.getProxy(s);
            if (s1 == null || !s1.equalsIgnoreCase(var1)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void pruneProxyHoverAnimations(List<String> var1) {
      HashSet hashset = new HashSet<>(var1);
      hashset.add(proxyNoneKey());
      this.proxyHoverAnims.keySet().removeIf(var1x -> !hashset.contains(var1x));
   }

   public void renderBulkActionTabs(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      float f = var2 + 4.0F;
      var1.drawRoundedRect(f, var3 - 3.0F, 232.0F, 1.0F, scrollbarRadius, scrollbarThumbColor.SprintStateEvent(var4));
      float f1 = connectIconFont.width(this.bulkAction.icon);
      float f2 = 2.0F;
      var1.drawText(
         connectIconFont, this.bulkAction.icon, f + 3.0F, centeredTextY(connectIconFont, var3, 14.0F), var5.getPrimaryColor().getColor().SprintStateEvent(var4)
      );
      var1.drawText(nickFont, tr(this.bulkAction.tabKey), f + 3.0F + f1 + f2, centeredTextY(nickFont, var3, 14.0F), ArgbColor.var11934.SprintStateEvent(var4));
      float f3 = bulkActionModeStartX(var2);

      for (BotScreen_BulkAction botscreen_bulkaction : BotScreen_BulkAction.values()) {
         boolean flag = botscreen_bulkaction == this.bulkAction;
         var1.drawRoundedRect(f3, var3, 14.0F, 14.0F, bulkActionModeRadius, (flag ? headerColor : addButtonColor).SprintStateEvent(var4));
         float f4 = connectIconFont.width(botscreen_bulkaction.icon);
         var1.drawText(
            connectIconFont,
            botscreen_bulkaction.icon,
            f3 + (14.0F - f4) / 2.0F,
            centeredTextY(connectIconFont, var3, 14.0F),
            (flag ? var5.getPrimaryColor().getColor() : inputEmptyColor).SprintStateEvent(var4)
         );
         f3 += 17.0F;
      }
   }

   public static float bulkActionModeStartX(float var0) {
      float f = BotScreen_BulkAction.values().length * 14.0F + (BotScreen_BulkAction.values().length - 1) * 3.0F;
      return var0 + 4.0F + 232.0F - f;
   }

   public void focusTextBox(SearchBox var1, double var2, double var4) {
      var1.onMouseClicked(var2, var4, MenuScreenId.call004);
      if (!var1.isSelected()) {
         var1.VelocityChangeEvent(true);
         var1.CrosshairTargetUpdateEvent(false);
      }
   }

   public void renderBulkActionPayload(HudDrawContext var1, float var2, float var3, float var4) {
      float f = var2 + 4.0F;
      this.bulkActionInput.HudInventoryPanel(tr(this.bulkAction.placeholderKey));
      this.syncBulkActionInputRules();
      float f1 = 164.0F;
      var1.drawRoundedRect(f, var3, f1, 23.0F, headerRadius, headerColor.SprintStateEvent(var4));
      this.bulkActionInput.setWidth(f1 - 16.0F);
      this.bulkActionInput
         .on23(var1, f + 8.0F, centeredTextY(nickFont, var3, 23.0F), ArgbColor.var11934.SprintStateEvent(var4), inputEmptyColor.SprintStateEvent(var4));
      float f2 = f + f1 + 4.0F;
      var1.drawRoundedRect(f2, var3, 64.0F, 23.0F, headerRadius, addButtonColor.SprintStateEvent(var4));
      this.drawCenteredIconText(var1, this.bulkAction.icon, tr(this.bulkAction.buttonKey), f2, var3, 64.0F, 23.0F, inputEmptyColor.SprintStateEvent(var4));
   }

   public void syncBulkActionInputRules() {
      switch (this.bulkAction) {
         case CONNECT:
            this.bulkActionInput.on23(SearchBox.MatchMode.val180);
            this.bulkActionInput.EventItemRenderHook(256);
            break;
         case CHAT:
            this.bulkActionInput.on23(SearchBox.MatchMode.val178);
            this.bulkActionInput.EventItemRenderHook(256);
            break;
         case RCT:
            this.bulkActionInput.on23(SearchBox.MatchMode.val297);
            this.bulkActionInput.EventItemRenderHook(2);
      }
   }

   public void drawCenteredIconText(HudDrawContext var1, String var2, String var3, float var4, float var5, float var6, float var7, ArgbColor var8) {
      float f = connectIconFont.width(var2);
      float f1 = nickFont.width(var3);
      float f2 = 2.0F;
      float f3 = f + f2 + f1;
      if (f3 <= var6 - 6.0F) {
         float f4 = var4 + (var6 - f3) / 2.0F;
         var1.drawText(connectIconFont, var2, f4, centeredTextY(connectIconFont, var5, var7), var8);
         var1.drawText(nickFont, var3, f4 + f + f2, centeredTextY(nickFont, var5, var7), var8);
      } else if (f1 <= var6 - 8.0F) {
         var1.drawText(nickFont, var3, var4 + (var6 - f1) / 2.0F, centeredTextY(nickFont, var5, var7), var8);
      } else {
         var1.drawText(connectIconFont, var2, var4 + (var6 - f) / 2.0F, centeredTextY(connectIconFont, var5, var7), var8);
      }
   }

   public void renderListRow(
      HudDrawContext var1, String var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      BotScreen_RowAnim botscreen_rowanim = this.rowAnims.computeIfAbsent(var2, var0 -> new BotScreen_RowAnim());
      BotClient botclient = HeadlessBots.get(var2);
      boolean flag = botclient != null && botclient.isJoined();
      float f = var5 + var3 * 25.0F - this.listScroll;
      boolean flag1 = this.isMultiSelectDown() || this.isShiftDown();
      boolean flag2 = var9 >= var5 && var9 <= var6 && var8 >= var4 + 4.0F && var8 <= var4 + 4.0F + 227.0F && var9 >= f && var9 <= f + 23.0F;
      float f1 = botscreen_rowanim.appear.on23(1.0F);
      float f2 = botscreen_rowanim.hover.on23(flag2 ? 1.0F : 0.0F);
      float f3 = var7 * f1;
      float f4 = f + (1.0F - f1) * 6.0F;
      float f5 = var4 + 4.0F;
      var1.drawRoundedRect(f5, f4, 227.0F, 23.0F, headerRadius, listColor.SprintStateEvent(f3));
      float f6 = botscreen_rowanim.select.on23(this.selectedNames.contains(var2) ? 1.0F : 0.0F);
      var1.drawRoundedRect(f5, f4, 23.0F, 23.0F, headerRadius, listColor.SprintStateEvent(f3));
      float f7 = f5 + 227.0F - 64.0F;
      var1.drawRoundedRect(f7, f4, 64.0F, 23.0F, headerRadius, listColor.SprintStateEvent(f3));
      if (flag) {
         boolean flag3 = !flag1 && var8 >= f7 && var8 <= f7 + 64.0F && var9 >= f4 && var9 <= f4 + 23.0F && var9 >= var5 && var9 <= var6;
         float f8 = botscreen_rowanim.disc.on23(flag3 ? 1.0F : 0.0F);
         float f9 = 23.0F;
         var1.enableScissor(f5, f4, f5 + 227.0F, f4 + 23.0F);
         if (f8 < 0.999F) {
            String s = botclient.getAddress();
            String s1 = serverLabel(s);
            Identifier identifier = ServerIconCache.get(s);
            float f10 = nickFont.width(s1);
            float f11 = f10 + (identifier != null ? 11.0F : 0.0F);
            float f12 = f7 + (64.0F - f11) / 2.0F;
            float f13 = f4 - f8 * f9;
            float f14 = f3 * (1.0F - f8);
            if (identifier != null) {
               float f15 = f13 + 8.0F;
               ShapeRenderer.on23(var1.getMatrices(), identifier, f12, f15, 7.0F, 7.0F, serverIconRadius, ArgbColor.var11934.SprintStateEvent(f14));
               f12 += 11.0F;
            }

            var1.drawText(nickFont, s1, f12, centeredTextY(nickFont, f13, 23.0F), ArgbColor.var11934.SprintStateEvent(f14));
         }

         if (f8 > 0.001F) {
            String s3 = tr("module.bot.disconnect");
            float f24 = connectIconFont.width("]");
            float f29 = nickFont.width(s3);
            float f30 = 2.0F;
            float f31 = f7 + (64.0F - f24 - f30 - f29) / 2.0F;
            float f32 = f4 + (1.0F - f8) * f9;
            float f33 = f3 * f8;
            var1.drawText(connectIconFont, "]", f31, centeredTextY(connectIconFont, f32, 23.0F), disconnectColor.SprintStateEvent(f33));
            var1.drawText(nickFont, s3, f31 + f24 + f30, centeredTextY(nickFont, f32, 23.0F), disconnectColor.SprintStateEvent(f33));
         }

         var1.disableScissor();
      } else {
         String s2 = "Offline";
         float f16 = connectIconFont.width("]");
         float f18 = nickFont.width(s2);
         float f19 = 2.0F;
         float f25 = f7 + (64.0F - f16 - f19 - f18) / 2.0F;
         var1.drawText(connectIconFont, "]", f25, centeredTextY(connectIconFont, f4, 23.0F), inputEmptyColor.SprintStateEvent(f3));
         var1.drawText(nickFont, s2, f25 + f16 + f19, centeredTextY(nickFont, f4, 23.0F), inputEmptyColor.SprintStateEvent(f3));
      }

      boolean flag4 = !flag1 && var8 >= f5 && var8 <= f5 + 23.0F && var9 >= f4 && var9 <= f4 + 23.0F && var9 >= var5 && var9 <= var6;
      float f17 = botscreen_rowanim.delete.on23(flag4 ? 1.0F : 0.0F);
      Identifier identifier1 = BotAvatarCache.prewarm(var2);
      if (identifier1 != null) {
         float f20 = f5 + 4.0F;
         float f26 = f4 + 4.0F;
         ArgbColor i11ii1llliilllii1i1 = f17 > 0.0F ? ArgbColor.var11934.Easing(deleteDarkColor, 0.62F * f17) : ArgbColor.var11934;
         ShapeRenderer.on23(var1.getMatrices(), identifier1, f20, f26, 15.0F, 15.0F, avatarRadius, i11ii1llliilllii1i1.SprintStateEvent(f3));
      }

      if (f17 > 0.001F) {
         float f21 = deleteIconFont.width("2");
         var1.drawText(
            deleteIconFont, "2", f5 + (23.0F - f21) / 2.0F - 0.5F, centeredTextY(deleteIconFont, f4, 23.0F) + 0.5F, deleteColor.SprintStateEvent(f3 * f17)
         );
      }

      if (f17 < 0.5F && HeadlessBots.hasProxy(var2)) {
         float f22 = f5 + 23.0F - 4.0F - 4.0F;
         float f27 = f4 + 4.0F;
         var1.drawRoundedRect(f22, f27, 4.0F, 4.0F, proxyDotRadius, var10.getPrimaryColor().getColor().SprintStateEvent(f3 * (1.0F - f17 * 2.0F)));
      }

      float f23 = f5 + 23.0F + 6.0F + f2 * 2.0F;
      float f28 = centeredTextY(nickFont, f4, 23.0F);
      ArgbColor i11ii1llliilllii1i11 = f6 > 0.001F ? ArgbColor.var11934.Easing(var10.getPrimaryColor().getColor(), f6) : ArgbColor.var11934;
      var1.drawText(nickFont, var2, f23, f28, i11ii1llliilllii1i11.SprintStateEvent(f3));
      this.renderRowModuleBadges(var1, var2, f23 + nickFont.width(var2) + 6.0F, f7 - 5.0F, f4, f3, var10);
   }

   public void renderRowModuleBadges(HudDrawContext var1, String var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      float f = var3;
      float f1 = var5 + 6.5F;
      int i = 0;

      for (String s : BotModuleManager.supportedModuleNames()) {
         if (this.isBotModuleEnabled(var2, s)) {
            String s1 = this.shortModuleName(s);
            float f2 = moduleMenuFont.width(s1) + 7.0F;
            if (f + f2 > var4 || i >= 2) {
               break;
            }

            var1.drawRoundedRect(f, f1, f2, 10.0F, roleBadgeRadius, var7.getPrimaryColor().getColor().EventHookWorldRender(34).SprintStateEvent(var6));
            var1.drawText(moduleMenuFont, s1, f + 3.5F, centeredTextY(moduleMenuFont, f1, 10.0F), var7.getPrimaryColor().getColor().SprintStateEvent(var6));
            f += f2 + 3.0F;
            i++;
         }
      }
   }

   public void renderModuleMenu(HudDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      String s = this.moduleMenuBotName();
      List<String> list = BotModuleManager.supportedModuleNames();
      if (s != null && !list.isEmpty()) {
         float f = this.moduleMenuAnimation.on23(1.0F);
         float f1 = var2 * clamp01(f);
         float f2 = this.moduleMenuY + (1.0F - f) * 6.0F;
         float f3 = 26.0F + list.size() * 20.0F + Math.max(0, list.size() - 1) * 3.0F;
         ShapeRenderer.ColorAnimator(var1.getMatrices(), this.moduleMenuX, f2 + 5.0F, 118.0F, f3, 18.0F, moduleMenuRadius, shadowColor.SprintStateEvent(f1));
         ShapeRenderer.on23(var1.getMatrices(), this.moduleMenuX, f2, 118.0F, f3, 16.0F, moduleMenuRadius, blurColor.SprintStateEvent(f1), true, false);
         var1.drawRoundedRect(this.moduleMenuX, f2, 118.0F, f3, moduleMenuRadius, var5.getRightBackground().getColor().SprintStateEvent(f1));
         float f4 = f2 + 4.0F;
         var1.drawText(
            moduleMenuFont,
            fitText(moduleMenuFont, s, 110.0F),
            this.moduleMenuX + 4.0F,
            centeredTextY(moduleMenuFont, f4, 18.0F),
            var5.getTextEnable().getColor().SprintStateEvent(f1)
         );
         float f5 = f4 + 18.0F;

         for (String s1 : list) {
            boolean flag = this.isBotModuleEnabled(s, s1);
            boolean flag1 = var3 >= this.moduleMenuX + 4.0F && var3 <= this.moduleMenuX + 118.0F - 4.0F && var4 >= f5 && var4 <= f5 + 20.0F;
            ArgbColor i11ii1llliilllii1i1 = (flag ? var5.getPrimaryColor().getColor().EventHookWorldRender(42) : var5.getSurfaceDisableBackground().getColor())
               .Easing(var5.getPrimaryColor().getColor(), flag1 ? 0.12F : 0.0F);
            var1.drawRoundedRect(this.moduleMenuX + 4.0F, f5, 110.0F, 20.0F, moduleMenuRowRadius, i11ii1llliilllii1i1.SprintStateEvent(f1));
            ArgbColor i11ii1llliilllii1i11 = flag ? var5.getPrimaryColor().getColor() : var5.getTextSecondary().getColor();
            var1.drawText(
               moduleMenuFont,
               fitText(moduleMenuFont, s1, 85.0F),
               this.moduleMenuX + 4.0F + 6.0F,
               centeredTextY(moduleMenuFont, f5, 20.0F),
               i11ii1llliilllii1i11.SprintStateEvent(f1)
            );
            float f6 = 6.0F;
            float f7 = this.moduleMenuX + 118.0F - 4.0F - 10.0F;
            float f8 = f5 + (20.0F - f6) / 2.0F;
            var1.drawRoundedRect(
               f7,
               f8,
               f6,
               f6,
               CornerRadius.MovementInputEvent(f6 / 2.0F),
               (flag ? var5.getPrimaryColor().getColor() : var5.getDisableActiveBg().getColor()).SprintStateEvent(f1)
            );
            f5 += 23.0F;
         }
      } else {
         this.moduleMenuBotName = null;
      }
   }

   public boolean isBotModuleEnabled(String var1, String var2) {
      BotClient botclient = HeadlessBots.get(var1);
      if (botclient != null) {
         BotModule botmodule = botclient.getModules().getModule(var2);
         if (botmodule != null) {
            return botmodule.isEnabled();
         }
      }

      return HeadlessBots.isModuleEnabledInProfile(var1, var2);
   }

   public String shortModuleName(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String s = var1.replace("BotAuto", "A").replace("Auto", "A").replace("Bot", "");
         return s.length() <= 6 ? s : s.substring(0, 6);
      } else {
         return "";
      }
   }

   public String moduleMenuBotName() {
      if (this.moduleMenuBotName == null) {
         return null;
      }

      for (String s : HeadlessBots.allNames()) {
         if (s.equalsIgnoreCase(this.moduleMenuBotName)) {
            return s;
         }
      }

      return null;
   }

   public void openModuleMenu(String var1, float var2, float var3) {
      if (var1 == null) {
         this.closeModuleMenu();
      } else {
         this.versionMenuOpen = false;
         this.moduleMenuAnimation.setValue(0.0F);
         this.moduleMenuBotName = var1;
         this.moduleMenuX = var2 + 227.0F + 3.0F;
         this.moduleMenuY = var3;
      }
   }

   public void closeModuleMenu() {
      this.moduleMenuBotName = null;
   }

   public boolean handleModuleMenuClick(double var1, double var3) {
      String s = this.moduleMenuBotName();
      List<String> list = BotModuleManager.supportedModuleNames();
      if (s != null && !list.isEmpty()) {
         float f = 26.0F + list.size() * 20.0F + Math.max(0, list.size() - 1) * 3.0F;
         boolean flag = var1 >= this.moduleMenuX && var1 <= this.moduleMenuX + 118.0F && var3 >= this.moduleMenuY && var3 <= this.moduleMenuY + f;
         if (!flag) {
            this.closeModuleMenu();
            return false;
         }

         float f1 = this.moduleMenuY + 4.0F + 18.0F;

         for (String s1 : list) {
            if (var1 >= this.moduleMenuX + 4.0F && var1 <= this.moduleMenuX + 118.0F - 4.0F && var3 >= f1 && var3 <= f1 + 20.0F) {
               boolean flag1 = this.isBotModuleEnabled(s, s1);
               HeadlessBots.setModuleEnabled(s, s1, !flag1);
               return true;
            }

            f1 += 23.0F;
         }

         return true;
      } else {
         this.closeModuleMenu();
         return false;
      }
   }

   public int selectedProtocolVersion() {
      return this.selectedName == null ? -1 : HeadlessBots.getProtocolVersion(this.selectedName);
   }

   public void applyVersionToSelection(int var1) {
      Set<String> linkedhashset = new LinkedHashSet<>(this.selectedNames);
      if (this.selectedName != null) {
         linkedhashset.add(this.selectedName);
      }

      for (String s : linkedhashset) {
         HeadlessBots.setProtocolVersion(s, var1);
      }
   }

   public float versionChipLeftX(float var1) {
      return var1 + 319.0F + 157.0F - this.connectButtonWidth - 4.0F - 34.0F;
   }

   public float versionMenuX(float var1) {
      return Math.min(this.versionChipLeftX(var1), var1 + 480.0F - 4.0F - 72.0F);
   }

   public float versionMenuY(float var1) {
      return var1 + 4.0F + 23.0F + 3.0F;
   }

   public float versionMenuViewportHeight() {
      int i = Math.min(9, BotProtocolVersions.ENTRIES.size());
      return i * 14.0F + Math.max(0, i - 1) * 2.0F;
   }

   public float versionMenuMaxScroll() {
      float f = 16.0F;
      float f1 = BotProtocolVersions.ENTRIES.size() * f - 2.0F;
      return Math.max(0.0F, f1 - this.versionMenuViewportHeight());
   }

   public void renderVersionMenu(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      if (this.versionMenuOpen) {
         if (this.selectedName == null) {
            this.versionMenuOpen = false;
         } else {
            float f = this.versionMenuX(var2);
            float f1 = this.versionMenuAnimation.on23(1.0F);
            float f2 = var4 * clamp01(f1);
            float f3 = this.versionMenuY(var3) + (1.0F - f1) * 6.0F;
            float f4 = this.versionMenuViewportHeight();
            float f5 = 8.0F + f4;
            ShapeRenderer.ColorAnimator(var1.getMatrices(), f, f3 + 5.0F, 72.0F, f5, 18.0F, moduleMenuRadius, shadowColor.SprintStateEvent(f2));
            ShapeRenderer.on23(var1.getMatrices(), f, f3, 72.0F, f5, 16.0F, moduleMenuRadius, blurColor.SprintStateEvent(f2), true, false);
            var1.drawRoundedRect(f, f3, 72.0F, f5, moduleMenuRadius, var7.getRightBackground().getColor().SprintStateEvent(f2));
            this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuScroll, this.versionMenuMaxScroll()));
            float f6 = 16.0F;
            float f7 = f3 + 4.0F;
            int i = this.selectedProtocolVersion();
            var1.enableScissor(f, f7, f + 72.0F, f7 + f4);
            float f8 = f7 - this.versionMenuScroll;

            for (BotProtocolVersions_Entry botprotocolversions_entry : BotProtocolVersions.ENTRIES) {
               if (f8 + 14.0F >= f7 && f8 <= f7 + f4) {
                  boolean flag = botprotocolversions_entry.protocol() == i;
                  boolean flag1 = var5 >= f + 4.0F && var5 <= f + 72.0F - 4.0F && var6 >= Math.max(f8, f7) && var6 <= Math.min(f8 + 14.0F, f7 + f4);
                  ArgbColor i11ii1llliilllii1i1 = (flag
                        ? var7.getPrimaryColor().getColor().EventHookWorldRender(42)
                        : var7.getSurfaceDisableBackground().getColor())
                     .Easing(var7.getPrimaryColor().getColor(), flag1 ? 0.12F : 0.0F);
                  var1.drawRoundedRect(f + 4.0F, f8, 64.0F, 14.0F, moduleMenuRowRadius, i11ii1llliilllii1i1.SprintStateEvent(f2));
                  ArgbColor i11ii1llliilllii1i11 = flag ? var7.getPrimaryColor().getColor() : var7.getTextSecondary().getColor();
                  var1.drawText(
                     versionFont,
                     botprotocolversions_entry.label(),
                     f + 4.0F + 5.0F,
                     centeredTextY(versionFont, f8, 14.0F),
                     i11ii1llliilllii1i11.SprintStateEvent(f2)
                  );
               }

               f8 += f6;
            }

            var1.disableScissor();
         }
      }
   }

   public boolean handleVersionMenuClick(double var1, double var3, float var5, float var6) {
      if (!this.versionMenuOpen) {
         return false;
      }

      float f = this.versionMenuX(var5);
      float f1 = this.versionMenuY(var6);
      float f2 = this.versionMenuViewportHeight();
      float f3 = 8.0F + f2;
      boolean flag = var1 >= f && var1 <= f + 72.0F && var3 >= f1 && var3 <= f1 + f3;
      if (!flag) {
         this.versionMenuOpen = false;
         return false;
      }

      float f4 = 16.0F;
      float f5 = (float)var3 - (f1 + 4.0F) + this.versionMenuScroll;
      int i = (int)Math.floor(f5 / f4);
      List<BotProtocolVersions_Entry> list = BotProtocolVersions.ENTRIES;
      if (i >= 0 && i < list.size() && f5 - i * f4 <= 14.0F) {
         this.applyVersionToSelection(list.get(i).protocol());
         this.versionMenuOpen = false;
      }

      return true;
   }

   public void renderChatMessages(HudDrawContext var1, BotClient var2, float var3, float var4, float var5, ZenithStyle var6) {
      if (var2 != null) {
         ChatMessage[] achatmessage = var2.getChatLog().toArray(new ChatMessage[0]);
         if (var2 != this.chatAnimClient) {
            this.chatAnimClient = var2;
            this.chatAnims.clear();
            this.wrapCache.clear();

            for (ChatMessage chatmessage : achatmessage) {
               this.chatAnims.put(chatmessage, new UiAnimation(260L, 1.0F, Easing.PreventActionEvent));
            }
         }

         Iterator<ChatMessage> iterator = this.chatAnims.keySet().iterator();

         while (iterator.hasNext()) {
            ChatMessage chatmessage3 = iterator.next();
            boolean flag = false;

            for (ChatMessage chatmessage1 : achatmessage) {
               if (chatmessage1 == chatmessage3) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               iterator.remove();
               this.wrapCache.remove(chatmessage3);
            }
         }

         if (achatmessage.length > 0) {
            float f1 = chatLineStepCache;
            float f2 = chatTimeTopOffsetCache;
            float f3 = var3 + 8.0F + chatTimeColumnWidthCache + 8.0F;
            float f4 = var3 + 232.0F - 8.0F - f3;
            ArrayList<BotScreen_ChatLine> arraylist = new ArrayList<>(achatmessage.length);

            for (ChatMessage chatmessage2 : achatmessage) {
               UiAnimation l1i1illlili = this.chatAnims.computeIfAbsent(chatmessage2, var0 -> new UiAnimation(260L, 0.0F, Easing.PreventActionEvent));
               float f = l1i1illlili.on23(1.0F);
               List<Text> list = this.wrapCache.get(chatmessage2);
               if (list == null) {
                  list = wrapText(chatmessage2.text(), nickFont, f4);
                  this.wrapCache.put(chatmessage2, list);
               }

               for (int i = 0; i < list.size(); i++) {
                  arraylist.add(new BotScreen_ChatLine(i == 0 ? chatmessage2.time() : null, list.get(i), f));
               }
            }

            float f5 = 99.5F;
            float f6 = 0.0F;

            for (BotScreen_ChatLine botscreen_chatline : arraylist) {
               f6 += f1 * botscreen_chatline.grow();
            }

            float f7 = Math.max(0.0F, f6 - f5);
            this.chatScroll = Math.max(0.0F, Math.min(this.chatScroll, f7));
            float f8 = var4 + 107.5F - 4.0F - f6 + this.chatScroll;
            var1.enableScissor(var3, var4, var3 + 232.0F, var4 + 107.5F);

            for (BotScreen_ChatLine botscreen_chatline1 : arraylist) {
               float f9 = botscreen_chatline1.grow();
               if (f8 + f1 > var4 && f8 < var4 + 107.5F) {
                  float f10 = var5 * f9;
                  if (botscreen_chatline1.time() != null) {
                     var1.drawText(timeFont, botscreen_chatline1.time(), var3 + 8.0F, f8 + f2, var6.getTextTertiary().getColor().SprintStateEvent(f10));
                  }

                  var1.drawText(nickFont, botscreen_chatline1.text(), f3, f8, var6.getTextEnable().getColor().SprintStateEvent(f10).call001());
               }

               f8 += f1 * f9;
            }

            var1.disableScissor();
         }
      }
   }

   public static String tr(String var0) {
      return ZenithClient.on23().Easing().translate(var0);
   }

   public static float centeredTextY(Font var0, float var1, float var2) {
      FontData_MetricsData fontdata_metricsdata = var0.getFont().getMetrics();
      float f = (fontdata_metricsdata.ascender() - fontdata_metricsdata.descender()) * var0.getSize();
      return var1 + (var2 - f) / 2.0F;
   }

   public static float clamp01(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   public static float lineHeight(Font var0) {
      FontData_MetricsData fontdata_metricsdata = var0.getFont().getMetrics();
      return (fontdata_metricsdata.ascender() - fontdata_metricsdata.descender()) * var0.getSize();
   }

   public static String fitText(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty() && !(var0.width(var1) <= var2)) {
         String s = "...";
         int i = var1.length();

         while (i > 0 && var0.width(var1.substring(0, i) + s) > var2) {
            i--;
         }

         return i <= 0 ? s : var1.substring(0, i) + s;
      } else {
         return var1 == null ? "" : var1;
      }
   }

   public static float listContentHeight(int var0) {
      return var0 <= 0 ? 0.0F : var0 * 25.0F - 2.0F;
   }

   public static float listMaxScroll(int var0) {
      return Math.max(0.0F, listContentHeight(var0) - 211.0F);
   }

   public List<String> filterBots(List<String> var1) {
      String s = this.searchInput.getText().trim();
      if (s.isEmpty()) {
         return var1;
      }

      String s1 = s.toLowerCase(Locale.ROOT);
      List<String> arraylist = new ArrayList<>();

      for (String s2 : var1) {
         if (s2.toLowerCase(Locale.ROOT).contains(s1)) {
            arraylist.add(s2);
         }
      }

      return arraylist;
   }

   public List<String> visibleBots() {
      return this.filterBots(HeadlessBots.allNames());
   }

   public BotClient selectedClient() {
      return this.selectedName == null ? null : HeadlessBots.get(this.selectedName);
   }

   public static String serverLabel(String var0) {
      if (var0 != null && !"No Server".equalsIgnoreCase(var0)) {
         String s = serverLabelCache.get(var0);
         if (s != null) {
            return s;
         }

         String s1 = computeServerLabel(var0);
         serverLabelCache.put(var0, s1);
         return s1;
      } else {
         return "";
      }
   }

   public static String computeServerLabel(String var0) {
      int i = var0.indexOf(58);
      String s = i > 0 ? var0.substring(0, i) : var0;
      String[] astring = s.split("\\.");
      if (astring.length < 2) {
         return s;
      }

      for (String s1 : astring) {
         if (!s1.chars().allMatch(Character::isDigit)) {
            return astring[astring.length - 2];
         }
      }

      return s;
   }

   public boolean isSearch() {
      return this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.proxyManagerInput.isSelected()
         || this.chatInput.isSelected()
         || this.connectInput.isSelected();
   }

   public static List<Text> wrapText(Text var0, Font var1, float var2) {
      List<Text> arraylist = new ArrayList<>();
      ArrayList arraylist1 = new ArrayList();
      float f = 0.0F;
      int i = -1;

      for (BotScreen_StyledTextFragment botscreen_styledtextfragment : splitTextToGlyphs(var0)) {
         if (botscreen_styledtextfragment.isLineBreak()) {
            addWrappedLine(arraylist, arraylist1);
            arraylist1.clear();
            f = 0.0F;
            i = -1;
         } else if (!arraylist1.isEmpty() || !botscreen_styledtextfragment.isWhitespace()) {
            arraylist1.add(botscreen_styledtextfragment);
            f += botscreen_styledtextfragment.width(var1);
            if (botscreen_styledtextfragment.isWhitespace()) {
               i = arraylist1.size() - 1;
            }

            if (var2 > 0.0F && f > var2 && arraylist1.size() > 1) {
               if (i > 0) {
                  addWrappedLine(arraylist, arraylist1.subList(0, i));
                  arraylist1 = new ArrayList(arraylist1.subList(i + 1, arraylist1.size()));
                  trimLeadingWhitespace(arraylist1);
               } else {
                  BotScreen_StyledTextFragment botscreen_styledtextfragment1 = (BotScreen_StyledTextFragment)arraylist1.remove(arraylist1.size() - 1);
                  addWrappedLine(arraylist, arraylist1);
                  arraylist1 = new ArrayList();
                  if (!botscreen_styledtextfragment1.isWhitespace()) {
                     arraylist1.add(botscreen_styledtextfragment1);
                  }
               }

               f = textWidth(arraylist1, var1);
               i = lastWhitespace(arraylist1);
            }
         }
      }

      addWrappedLine(arraylist, arraylist1);
      return arraylist;
   }

   public static List<BotScreen_StyledTextFragment> splitTextToGlyphs(Text var0) {
      List<BotScreen_StyledTextFragment> arraylist = new ArrayList<>();
      var0.visit((var1x, var2) -> {
         int i = 0;

         while (i < var2.length()) {
            int j = var2.codePointAt(i);
            int k = i + Character.charCount(j);
            arraylist.add(new BotScreen_StyledTextFragment(var2.substring(i, k), var1x));
            i = k;
         }

         return Optional.empty();
      }, Style.EMPTY);
      return arraylist;
   }

   public static void addWrappedLine(List<Text> var0, List<BotScreen_StyledTextFragment> var1) {
      ArrayList arraylist = new ArrayList<>(var1);
      trimTrailingWhitespace(arraylist);
      if (!arraylist.isEmpty()) {
         var0.add(toText(arraylist));
      }
   }

   public static Text toText(List<BotScreen_StyledTextFragment> var0) {
      MutableText mutabletext = Text.empty();

      for (BotScreen_StyledTextFragment botscreen_styledtextfragment : var0) {
         mutabletext.append(Text.literal(botscreen_styledtextfragment.value()).setStyle(botscreen_styledtextfragment.style()));
      }

      return mutabletext;
   }

   public static float textWidth(List<BotScreen_StyledTextFragment> var0, Font var1) {
      float f = 0.0F;

      for (BotScreen_StyledTextFragment botscreen_styledtextfragment : var0) {
         f += botscreen_styledtextfragment.width(var1);
      }

      return f;
   }

   public static int lastWhitespace(List<BotScreen_StyledTextFragment> var0) {
      for (int i = var0.size() - 1; i >= 0; i--) {
         if (var0.get(i).isWhitespace()) {
            return i;
         }
      }

      return -1;
   }

   public static void trimLeadingWhitespace(List<BotScreen_StyledTextFragment> var0) {
      while (!var0.isEmpty() && var0.get(0).isWhitespace()) {
         var0.remove(0);
      }
   }

   public static void trimTrailingWhitespace(List<BotScreen_StyledTextFragment> var0) {
      while (!var0.isEmpty() && var0.get(var0.size() - 1).isWhitespace()) {
         var0.remove(var0.size() - 1);
      }
   }

   public void sendChatMessage() {
      String s = this.chatInput.getText();
      BotClient botclient = this.selectedClient();
      if (botclient != null && !s.isBlank()) {
         botclient.sendChat(s);
      }

      this.chatInput.HudHotbarPanel("");
      this.chatInput.EventRender(0);
   }

   public static boolean isValidBotName(String var0) {
      int i = var0 == null ? 0 : var0.trim().length();
      return i >= 3 && i <= 16;
   }

   public void addBotFromSearch() {
      String s = this.addInput.getText().trim();
      if (isValidBotName(s) && HeadlessBots.add(s)) {
         this.addInput.HudHotbarPanel("");
         this.addInput.EventRender(0);
         this.selectOnly(s);
         this.listScroll = 0.0F;
      }
   }

   public List<String> selectionNames() {
      List<String> arraylist = new ArrayList<>(this.selectedNames);
      if (arraylist.isEmpty() && this.selectedName != null) {
         arraylist.add(this.selectedName);
      }

      return arraylist;
   }

   public void applyProxyToSelection(String var1) {
      String s = var1 == null ? "" : var1.trim();
      if (!s.isBlank()) {
         HeadlessBots.addProxy(s);
      }

      for (String s1 : this.selectionNames()) {
         HeadlessBots.setProxy(s1, s);
      }

      this.proxyManagerInput.VelocityChangeEvent(false);
   }

   public void addProxyFromManager() {
      String s = this.proxyManagerInput.getText().trim();
      if (!s.isBlank()) {
         HeadlessBots.addProxy(s);
         this.proxyManagerInput.HudHotbarPanel("");
         this.proxyManagerInput.EventRender(0);
         this.proxyPanelScroll = 0.0F;
      }
   }

   public boolean handleProxyPanelClick(double var1, double var3, float var5, float var6) {
      float f = proxyPanelButtonX(var5);
      float f1 = proxyPanelButtonY(var6);
      if (var1 >= f && var1 <= f + 18.0F && var3 >= f1 && var3 <= f1 + 18.0F) {
         this.proxyPanelOpen = !this.proxyPanelOpen;
         savedProxyPanelOpen = this.proxyPanelOpen;
         this.proxyManagerInput.VelocityChangeEvent(false);
         return true;
      }

      float f2 = this.proxyPanelAnimation.CancellableEvent();
      if (!this.proxyPanelOpen && f2 <= 0.001F) {
         return false;
      }

      float f3 = proxyPanelRenderX(var5, Math.max(f2, this.proxyPanelOpen ? 1.0F : 0.0F));
      float f4 = proxyPanelY(var6);
      if (!(var1 < f3) && !(var1 > f3 + 156.0F) && !(var3 < f4) && !(var3 > f4 + 222.0F)) {
         float f5 = proxyPanelCloseHitX(f3);
         float f6 = proxyPanelCloseHitY(f4);
         if (var1 >= f5 && var1 <= f5 + 14.0F && var3 >= f6 && var3 <= f6 + 14.0F) {
            this.proxyPanelOpen = false;
            savedProxyPanelOpen = false;
            this.proxyManagerInput.VelocityChangeEvent(false);
            return true;
         } else {
            this.handleProxyPanelBodyClick(var1, var3, f3, f4);
            return true;
         }
      } else {
         return false;
      }
   }

   public void handleProxyPanelBodyClick(double var1, double var3, float var5, float var6) {
      float f = var6 + 31.0F;
      float f1 = var5 + 4.0F;
      float f2 = 126.0F;
      float f3 = f1 + f2 + 4.0F;
      if (!(var3 >= f) || !(var3 <= f + 18.0F)) {
         float f4 = var6 + 53.0F;
         float f5 = proxyPanelRowWidth();
         if (!(var1 < f1) && !(var1 > f1 + f5) && !(var3 < f4) && !(var3 > f4 + 161.0F)) {
            float f6 = (float)(var3 - f4 + this.proxyPanelScroll);
            float f7 = 25.0F;
            int i = (int)(f6 / f7);
            if (i >= 0) {
               if (f6 - i * f7 > 23.0F) {
                  this.proxyManagerInput.VelocityChangeEvent(false);
               } else if (i == 0) {
                  this.applyProxyToSelection("");
               } else {
                  List<String> list = HeadlessBots.getProxyPool();
                  int j = i - 1;
                  if (j < list.size()) {
                     String s = list.get(j);
                     float f8 = f1 + f5 - 18.0F;
                     if (var1 >= f8 && var1 <= f8 + 18.0F) {
                        if (HeadlessBots.removeProxy(s)) {
                           this.proxyHoverAnims.remove(s);
                           this.proxyPanelScroll = Math.min(this.proxyPanelScroll, proxyPanelMaxScroll(HeadlessBots.getProxyPool().size() + 1));
                        }

                        this.proxyManagerInput.VelocityChangeEvent(false);
                     } else {
                        this.applyProxyToSelection(s);
                     }
                  }
               }
            }
         } else {
            this.proxyManagerInput.VelocityChangeEvent(false);
         }
      } else if (var1 >= f3 && var1 <= f3 + 18.0F) {
         this.addProxyFromManager();
         this.proxyManagerInput.VelocityChangeEvent(false);
      } else if (var1 >= f1 && var1 <= f1 + f2) {
         this.focusTextBox(this.proxyManagerInput, var1, var3);
         this.clearMainInputFocus();
      } else {
         this.proxyManagerInput.VelocityChangeEvent(false);
      }
   }

   public void clearMainInputFocus() {
      this.chatInput.VelocityChangeEvent(false);
      this.connectInput.VelocityChangeEvent(false);
      this.addInput.VelocityChangeEvent(false);
      this.searchInput.VelocityChangeEvent(false);
      this.bulkActionInput.VelocityChangeEvent(false);
   }

   public boolean isMultiSelectDown() {
      var window = minecraftClient3.getWindow();
      return InputUtil.isKeyPressed(window, 341) || InputUtil.isKeyPressed(window, 345) || InputUtil.isKeyPressed(window, 343) || InputUtil.isKeyPressed(window, 347);
   }

   public boolean isShiftDown() {
      var window = minecraftClient3.getWindow();
      return InputUtil.isKeyPressed(window, 340) || InputUtil.isKeyPressed(window, 344);
   }

   public void selectOnly(String var1) {
      this.selectedNames.clear();
      if (var1 != null) {
         this.selectedNames.add(var1);
      }

      this.selectedName = var1;
   }

   public void toggleSelection(String var1) {
      if (this.selectedNames.contains(var1)) {
         this.selectedNames.remove(var1);
         if (this.selectedName != null && this.selectedName.equals(var1)) {
            this.selectedName = null;
            this.chatScroll = 0.0F;
            Iterator<String> iterator = this.selectedNames.iterator();
            if (iterator.hasNext()) {
               String s = iterator.next();
               this.selectedName = s;
            }
         }
      } else {
         this.selectedNames.add(var1);
         this.selectedName = var1;
         this.chatScroll = 0.0F;
      }
   }

   public void rangeSelect(List<String> var1, int var2) {
      int i = -1;
      if (this.selectedName != null) {
         for (int j = 0; j < var1.size(); j++) {
            if (var1.get(j).equalsIgnoreCase(this.selectedName)) {
               i = j;
               break;
            }
         }
      }

      if (i < 0) {
         this.selectOnly(var1.get(var2));
         this.chatScroll = 0.0F;
      } else {
         this.selectedNames.clear();

         for (int k = Math.min(i, var2); k <= Math.max(i, var2); k++) {
            this.selectedNames.add(var1.get(k));
         }
      }
   }

   public void selectAllVisible() {
      List<String> list = this.visibleBots();
      if (!list.isEmpty()) {
         this.selectedNames.clear();
         this.selectedNames.addAll(list);
         if (this.selectedName == null || !this.selectedNames.contains(this.selectedName)) {
            this.selectedName = list.get(0);
            this.chatScroll = 0.0F;
         }
      }
   }

   public void refreshSelection(List<String> var1) {
      if (this.selectedName != null) {
         for (String s : var1) {
            if (s.equalsIgnoreCase(this.selectedName)) {
               if (!s.equals(this.selectedName)) {
                  this.selectedName = s;
               }

               return;
            }
         }

         this.selectedName = null;
      }

      for (String s2 : this.selectedNames) {
         for (String s1 : var1) {
            if (s1.equalsIgnoreCase(s2)) {
               this.selectedName = s1;
               return;
            }
         }
      }
   }

   public boolean anyInputSelected() {
      return this.chatInput.isSelected()
         || this.connectInput.isSelected()
         || this.addInput.isSelected()
         || this.searchInput.isSelected()
         || this.bulkActionInput.isSelected()
         || this.proxyManagerInput.isSelected();
   }

   public List<String> offlineSelectedNames() {
      List<String> arraylist = new ArrayList<>();

      for (String s : this.selectedNames) {
         if (!HeadlessBots.isOnline(s)) {
            arraylist.add(s);
         }
      }

      if (arraylist.isEmpty() && this.selectedName != null && !HeadlessBots.isOnline(this.selectedName)) {
         arraylist.add(this.selectedName);
      }

      return arraylist;
   }

   public void doConnect() {
      String s = this.connectInput.getText().trim();
      if (s.isBlank()) {
         ServerInfo serverinfo = minecraftClient3.getCurrentServerEntry();
         if (serverinfo != null && serverinfo.address != null) {
            s = serverinfo.address.trim();
         }
      }

      if (s.isBlank()) {
         s = HeadlessBots.getLastAddress();
      }

      if (!s.isBlank()) {
         ServerAddress serveraddress = ServerAddress.parse(s);
         String s1 = serveraddress.getAddress();
         int i = serveraddress.getPort();
         List<String> list = this.offlineSelectedNames();
         if (!list.isEmpty()) {
            if (list.size() == 1) {
               HeadlessBots.connect(list.get(0), s1, i);
            } else {
               HeadlessBots.connectAll(list, s1, i);
            }

            this.connectInput.HudHotbarPanel("");
            this.connectInput.EventRender(0);
            this.connectInput.VelocityChangeEvent(false);
         }
      }
   }

   public void executeBulkAction() {
      switch (this.bulkAction) {
         case CONNECT:
            this.doBulkConnect();
            break;
         case CHAT:
            this.doBulkChat();
            break;
         case RCT:
            this.doBulkRct();
      }
   }

   public void doBulkRct() {
      int i = this.parseRctAnarchy();
      if (i >= 0) {
         for (BotClient botclient : HeadlessBots.all()) {
            if (botclient.isJoined()) {
               botclient.getRct().reconnect(i);
            }
         }

         this.bulkActionInput.VelocityChangeEvent(false);
      }
   }

   public int parseRctAnarchy() {
      String s = this.bulkActionInput.getText().trim();
      if (s.isEmpty()) {
         return -1;
      }

      try {
         int i = Integer.parseInt(s);
         return i >= 1 && i <= 66 ? i : -1;
      } catch (NumberFormatException numberformatexception) {
         return -1;
      }
   }

   public void doBulkConnect() {
      String s = this.bulkActionInput.getText().trim();
      if (s.isBlank()) {
         ServerInfo serverinfo = minecraftClient3.getCurrentServerEntry();
         if (serverinfo != null && serverinfo.address != null) {
            s = serverinfo.address.trim();
         }
      }

      if (s.isBlank()) {
         s = HeadlessBots.getLastAddress();
      }

      if (!s.isBlank()) {
         List<String> list = this.offlineBotNames();
         if (!list.isEmpty()) {
            ServerAddress serveraddress = ServerAddress.parse(s);
            HeadlessBots.connectAll(list, serveraddress.getAddress(), serveraddress.getPort());
            this.bulkActionInput.HudHotbarPanel("");
            this.bulkActionInput.EventRender(0);
            this.bulkActionInput.VelocityChangeEvent(false);
         }
      }
   }

   public void doBulkChat() {
      String s = this.bulkActionInput.getText();
      if (!s.isBlank()) {
         for (BotClient botclient : HeadlessBots.all()) {
            if (botclient.isJoined()) {
               botclient.sendChat(s);
            }
         }

         this.bulkActionInput.HudHotbarPanel("");
         this.bulkActionInput.EventRender(0);
         this.bulkActionInput.VelocityChangeEvent(false);
      }
   }

   public List<String> offlineBotNames() {
      List<String> arraylist = new ArrayList<>();

      for (String s : HeadlessBots.allNames()) {
         if (!HeadlessBots.isOnline(s)) {
            arraylist.add(s);
         }
      }

      return arraylist;
   }

   @Override
   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (var5 == MenuScreenId.call111) {
            float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
            float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
            List<String> list = this.visibleBots();
            float f2 = f1 + 58.0F;
            float f3 = f2 + 211.0F;

            for (int i = 0; i < list.size(); i++) {
               float f4 = f2 + i * 25.0F - this.listScroll;
               if (var3 >= f2 && var3 <= f3 && var1 >= f + 4.0F && var1 <= f + 4.0F + 227.0F && var3 >= f4 && var3 <= f4 + 23.0F) {
                  this.openModuleMenu(list.get(i), f + 4.0F, f4);
                  return;
               }
            }

            this.closeModuleMenu();
         }

         if (var5 == MenuScreenId.call004) {
            float f14 = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
            float f15 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
            if (this.handleProxyPanelClick(var1, var3, f14, f15)) {
               return;
            }

            this.proxyManagerInput.VelocityChangeEvent(false);
            if (this.handleModuleMenuClick(var1, var3)) {
               return;
            }

            if (this.handleVersionMenuClick(var1, var3, f14, f15)) {
               return;
            }

            float f16 = f15 + 31.0F;
            boolean flag6 = var3 >= f16 && var3 <= f16 + 23.0F;
            float f17 = f14 + 4.0F;
            float f18 = f17 + 114.0F - 23.0F;
            boolean flag7 = flag6 && var1 >= f17 && var1 <= f17 + 114.0F;
            if (flag7 && var1 >= f18) {
               this.bulkActionInput.VelocityChangeEvent(false);
               this.addBotFromSearch();
               return;
            }

            boolean flag = flag7 && var1 < f18;
            if (flag) {
               this.focusTextBox(this.addInput, var1, var3);
               this.chatInput.VelocityChangeEvent(false);
               this.connectInput.VelocityChangeEvent(false);
               this.searchInput.VelocityChangeEvent(false);
               this.bulkActionInput.VelocityChangeEvent(false);
               return;
            }

            this.addInput.VelocityChangeEvent(false);
            float f5 = f17 + 114.0F + 4.0F;
            boolean flag1 = flag6 && var1 >= f5 && var1 <= f5 + 114.0F;
            if (flag1) {
               this.focusTextBox(this.searchInput, var1, var3);
               this.chatInput.VelocityChangeEvent(false);
               this.connectInput.VelocityChangeEvent(false);
               this.addInput.VelocityChangeEvent(false);
               this.bulkActionInput.VelocityChangeEvent(false);
               return;
            }

            this.searchInput.VelocityChangeEvent(false);
            float f6 = f15 + 275.0F;
            boolean flag2 = var3 >= f6 && var3 <= f6 + 14.0F;
            float f7 = bulkActionModeStartX(f14);
            float f8 = f14 + 4.0F + 232.0F;
            if (flag2 && var1 >= f7 && var1 <= f8) {
               for (BotScreen_BulkAction botscreen_bulkaction : BotScreen_BulkAction.values()) {
                  if (var1 >= f7 && var1 <= f7 + 14.0F) {
                     if (this.bulkAction != botscreen_bulkaction) {
                        this.bulkActionInput.HudHotbarPanel("");
                        this.bulkActionInput.EventRender(0);
                     }

                     this.bulkAction = botscreen_bulkaction;
                     this.syncBulkActionInputRules();
                     this.clearMainInputFocus();
                     this.bulkActionInput.VelocityChangeEvent(true);
                     this.bulkActionInput.CrosshairTargetUpdateEvent(false);
                     return;
                  }

                  f7 += 17.0F;
               }
            }

            float f19 = f15 + 292.0F;
            boolean flag8 = var3 >= f19 && var3 <= f19 + 23.0F;
            if (flag8 && var1 >= f14 + 4.0F && var1 <= f14 + 4.0F + 232.0F) {
               float f20 = f14 + 4.0F;
               float f21 = 164.0F;
               float f9 = f20 + f21 + 4.0F;
               if (var1 >= f9) {
                  this.executeBulkAction();
                  return;
               }

               boolean flag3 = var1 < f20 + f21;
               if (flag3) {
                  this.clearMainInputFocus();
                  this.focusTextBox(this.bulkActionInput, var1, var3);
               } else {
                  this.bulkActionInput.VelocityChangeEvent(false);
               }

               if (flag3) {
                  return;
               }
            }

            if (flag8 && var1 >= f14 + 4.0F && var1 <= f14 + 4.0F + 232.0F) {
               this.chatInput.VelocityChangeEvent(false);
               this.connectInput.VelocityChangeEvent(false);
               this.addInput.VelocityChangeEvent(false);
               this.searchInput.VelocityChangeEvent(false);
               this.bulkActionInput.VelocityChangeEvent(false);
               return;
            }

            this.bulkActionInput.VelocityChangeEvent(false);
            List<String> list1 = this.visibleBots();
            float f22 = f15 + 58.0F;
            float f23 = f22 + 211.0F;

            for (int j = 0; j < list1.size(); j++) {
               float f10 = f22 + j * 25.0F - this.listScroll;
               if (var3 >= f22 && var3 <= f23 && var1 >= f14 + 4.0F && var1 <= f14 + 4.0F + 227.0F && var3 >= f10 && var3 <= f10 + 23.0F) {
                  String s = list1.get(j);
                  if (this.isMultiSelectDown()) {
                     this.toggleSelection(s);
                     return;
                  }

                  if (this.isShiftDown()) {
                     this.rangeSelect(list1, j);
                     return;
                  }

                  if (var1 <= f14 + 4.0F + 23.0F) {
                     HeadlessBots.remove(s);
                     this.rowAnims.remove(s);
                     this.selectedNames.remove(s);
                     if (s.equals(this.selectedName)) {
                        this.chatScroll = 0.0F;
                        this.selectedName = null;
                        Iterator<String> iterator = this.selectedNames.iterator();
                        if (iterator.hasNext()) {
                           String s1 = iterator.next();
                           this.selectedName = s1;
                        }

                        if (this.selectedName == null && this.selectedNames.isEmpty()) {
                           List<String> list2 = HeadlessBots.allNames();
                           if (!list2.isEmpty()) {
                              this.selectOnly(list2.get(0));
                           }
                        }
                     }

                     return;
                  }

                  if (HeadlessBots.isOnline(s) && var1 >= f14 + 4.0F + 227.0F - 64.0F) {
                     HeadlessBots.disconnect(s);
                     return;
                  }

                  this.selectOnly(s);
                  this.chatScroll = 0.0F;
                  return;
               }
            }

            float f24 = f14 + 319.0F;
            float f25 = f15 + 4.0F;
            boolean flag9 = var1 >= f24 && var1 <= f24 + 157.0F && var3 >= f25 && var3 <= f25 + 23.0F;
            float f26 = f24 + 157.0F - this.connectButtonWidth;
            if (flag9 && var1 >= f26) {
               BotClient botclient1 = this.selectedClient();
               if (botclient1 != null && botclient1.isJoined()) {
                  minecraftClient3.setScreen(new BotControlScreen(botclient1.getName()));
               } else {
                  this.doConnect();
               }

               return;
            }

            float f27 = f26 - 4.0F - 34.0F;
            if (flag9 && var1 >= f27) {
               this.versionMenuOpen = !this.versionMenuOpen;
               if (this.versionMenuOpen) {
                  this.versionMenuAnimation.setValue(0.0F);
               }

               this.versionMenuScroll = 0.0F;
               this.clearMainInputFocus();
               return;
            }

            boolean flag4 = flag9 && var1 < f27;
            if (flag4) {
               this.focusTextBox(this.connectInput, var1, var3);
               this.chatInput.VelocityChangeEvent(false);
               return;
            }

            this.connectInput.VelocityChangeEvent(false);
            BotClient botclient = this.selectedClient();
            if (botclient != null && botclient.isJoined()) {
               float f11 = f14 + 244.0F;
               float f12 = f15 + 39.0F + 130.5F + 4.0F + 107.5F + 4.0F;
               float f13 = f11 + 232.0F - 48.0F;
               if (var1 >= f13 && var1 <= f13 + 48.0F && var3 >= f12 && var3 <= f12 + 23.0F) {
                  this.sendChatMessage();
                  return;
               }

               boolean flag5 = var1 >= f11 && var1 <= f11 + 232.0F && var3 >= f12 && var3 <= f12 + 23.0F;
               if (flag5) {
                  this.focusTextBox(this.chatInput, var1, var3);
                  return;
               }

               this.chatInput.VelocityChangeEvent(false);
            }
         }

         super.onMouseClicked(var1, var3, var5);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.closing && this.screenAnimation.isDone()) {
         if (this.proxyManagerInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.addProxyFromManager();
               return true;
            }

            if (keyCode == 256) {
               this.proxyManagerInput.VelocityChangeEvent(false);
               return true;
            }

            if (this.proxyManagerInput.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         if (this.chatInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.sendChatMessage();
               return true;
            }

            if (this.chatInput.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         if (this.connectInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.doConnect();
               return true;
            }

            if (this.connectInput.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         if (this.addInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.addBotFromSearch();
               return true;
            }

            if (this.addInput.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         if (this.searchInput.isSelected() && this.searchInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }

         if (this.bulkActionInput.isSelected()) {
            if (keyCode == 257 || keyCode == 335) {
               this.executeBulkAction();
               return true;
            }

            if (keyCode == 256) {
               this.bulkActionInput.VelocityChangeEvent(false);
               return true;
            }

            if (this.bulkActionInput.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         if (keyCode == 65 && (modifiers & 10) != 0 && !this.anyInputSelected()) {
            this.selectAllVisible();
            return true;
         } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      } else {
         return false;
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.closing || !this.screenAnimation.isDone()) {
         return false;
      } else if (this.chatInput.charTyped(chr, modifiers)) {
         return true;
      } else if (this.connectInput.charTyped(chr, modifiers)) {
         return true;
      } else if (this.addInput.charTyped(chr, modifiers)) {
         return true;
      } else if (this.searchInput.charTyped(chr, modifiers)) {
         return true;
      } else if (this.bulkActionInput.charTyped(chr, modifiers)) {
         return true;
      } else {
         return this.proxyManagerInput.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && this.screenAnimation.isDone()) {
         float f = (minecraftClient3.getWindow().getScaledWidth() - 480.0F) / 2.0F;
         float f1 = (minecraftClient3.getWindow().getScaledHeight() - 320.0F) / 2.0F;
         if (this.versionMenuOpen) {
            float f2 = this.versionMenuX(f);
            float f3 = this.versionMenuY(f1);
            float f4 = 8.0F + this.versionMenuViewportHeight();
            if (mouseX >= f2 && mouseX <= f2 + 72.0F && mouseY >= f3 && mouseY <= f3 + f4) {
               float f14 = 16.0F;
               this.versionMenuScroll = Math.max(0.0F, Math.min(this.versionMenuMaxScroll(), this.versionMenuScroll - (float)verticalAmount * f14));
               return true;
            }
         }

         float f8 = this.proxyPanelAnimation.CancellableEvent();
         if (this.proxyPanelOpen || f8 > 0.001F) {
            float f9 = proxyPanelRenderX(f, Math.max(f8, this.proxyPanelOpen ? 1.0F : 0.0F));
            float f11 = proxyPanelY(f1);
            float f5 = f11 + 53.0F;
            if (mouseX >= f9 + 4.0F && mouseX <= f9 + 4.0F + proxyPanelListWidth() && mouseY >= f5 && mouseY <= f5 + 161.0F) {
               int i = HeadlessBots.getProxyPool().size() + 1;
               float f15 = proxyPanelMaxScroll(i);
               this.proxyPanelScroll = Math.max(0.0F, Math.min(f15, this.proxyPanelScroll - (float)verticalAmount * 25.0F));
               return true;
            }
         }

         float f10 = f + 244.0F;
         float f12 = f1 + 39.0F + 130.5F + 4.0F;
         if (mouseX >= f10 && mouseX <= f10 + 232.0F && mouseY >= f12 && mouseY <= f12 + 107.5F) {
            this.chatScroll = Math.max(0.0F, this.chatScroll + (float)verticalAmount * 14.0F);
            return true;
         } else {
            float f13 = f1 + 58.0F;
            float f6 = f13 + 211.0F;
            if (mouseX >= f + 4.0F && mouseX <= f + 4.0F + 227.0F && mouseY >= f13 && mouseY <= f6) {
               float f7 = listMaxScroll(this.visibleBots().size());
               this.listScroll = Math.max(0.0F, Math.min(f7, this.listScroll - (float)verticalAmount * 25.0F));
               return true;
            } else {
               return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
         }
      } else {
         return false;
      }
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public boolean shouldPause() {
      return false;
   }

   public String getSelectedBotName() {
      return this.selectedName;
   }

   public void close() {
      if (!this.closing) {
         this.screenAnimation.on23(220L);
         this.screenAnimation.on23(Easing.ModuleToggleEvent);
         this.closing = true;
      }
   }
}
