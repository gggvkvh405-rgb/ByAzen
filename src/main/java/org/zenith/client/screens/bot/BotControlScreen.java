package org.zenith.client.screens.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.ChatMessage;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.view.BotPlayerGuiRender;
import org.zenith.base.bot.view.BotRemoteControl;
import org.zenith.base.bot.view.BotWorldView;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.module.render.Menu;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.font.Fonts;
import org.zenith.core.MenuScreenId;
import org.zenith.hud.SearchBox;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.game.other.render.CustomScreen;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class BotControlScreen extends CustomScreen {
   public static final Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
   public static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
   public static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_left");
   public static final Identifier HEART_CONTAINER_TEXTURE = Identifier.ofVanilla("hud/heart/container");
   public static final Identifier HEART_FULL_TEXTURE = Identifier.ofVanilla("hud/heart/full");
   public static final Identifier HEART_HALF_TEXTURE = Identifier.ofVanilla("hud/heart/half");
   public static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
   public static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");
   public static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
   public static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/armor_empty");
   public static final Identifier ARMOR_FULL_TEXTURE = Identifier.ofVanilla("hud/armor_full");
   public static final Identifier ARMOR_HALF_TEXTURE = Identifier.ofVanilla("hud/armor_half");
   public static final Identifier AIR_TEXTURE = Identifier.ofVanilla("hud/air");
   public static final Identifier AIR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/air_empty");
   public static final Identifier XP_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
   public static final Identifier XP_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");
   public static final Identifier INVENTORY_TEXTURE = Identifier.ofVanilla("textures/gui/container/inventory.png");
   public static final Identifier GENERIC_54_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
   public static final Identifier SHULKER_BOX_TEXTURE = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
   public static final Identifier HOPPER_TEXTURE = Identifier.ofVanilla("textures/gui/container/hopper.png");
   public static final Identifier DISPENSER_TEXTURE = Identifier.ofVanilla("textures/gui/container/dispenser.png");
   public static final Identifier FURNACE_TEXTURE = Identifier.ofVanilla("textures/gui/container/furnace.png");
   public static final Identifier BLAST_FURNACE_TEXTURE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");
   public static final Identifier SMOKER_TEXTURE = Identifier.ofVanilla("textures/gui/container/smoker.png");
   public static final Identifier CRAFTING_TABLE_TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");
   public static final Identifier SLOT_HIGHLIGHT_BACK_TEXTURE = Identifier.ofVanilla("container/slot_highlight_back");
   public static final Identifier SLOT_HIGHLIGHT_FRONT_TEXTURE = Identifier.ofVanilla("container/slot_highlight_front");
   public final String botName;
   public BotClient boundClient;
   public BotRemoteControl control;
   public BotWorldView view;
   public boolean inventoryOpen;
   public boolean cursorLocked;
   public double lastMouseX = Double.NaN;
   public double lastMouseY = Double.NaN;
   public Slot hoveredSlotCache;
   public int wheelSlot = -1;
   public static final int CHAT_WIDTH = 320;
   public static final int CHAT_LINE_HEIGHT = 9;
   public static final long CHAT_VISIBLE_MS = 10000L;
   public final SearchBox chatField = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(6.0F), "", 0.0F);
   public final BotChatInputSuggestor chatSuggestor;
   public String originalChatText = "";
   public int chatFirstChar;
   public int messageHistoryIndex;
   public String chatLastMessage = "";
   public boolean chatOpen;
   public boolean suppressNextChar;
   public int chatScrollLines;
   public int chatTotalLines;
   public final Map<ChatMessage, BotControlScreen_CachedChat> chatCache = new IdentityHashMap<>();
   public boolean chatSeeded;
   public boolean keyForward;
   public boolean keyBack;
   public boolean keyLeft;
   public boolean keyRight;
   public boolean keyJump;
   public boolean keySneak;
   public boolean keySprint;
   public final double[] directMouseX = new double[1];
   public final double[] directMouseY = new double[1];
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public String getBotName() {
      return this.botName;
   }

   public BotControlScreen(String var1) {
      this.botName = var1;
      this.chatSuggestor = new BotChatInputSuggestor(
         this,
         this.chatField,
         minecraftClient3.textRenderer,
         () -> this.boundClient != null ? this.boundClient.getPlayHandler() : null,
         this::chatCharacterX,
         () -> this.height - 16
      );
   }

   public BotClient ensureBound() {
      BotClient botclient = HeadlessBots.get(this.botName);
      if (botclient != this.boundClient) {
         this.unbind();
         this.boundClient = botclient;
         if (botclient != null) {
            this.control = new BotRemoteControl(botclient);
            this.control.attach();
            this.view = new BotWorldView(botclient);
         }
      }

      return botclient;
   }

   public void unbind() {
      if (this.control != null) {
         this.control.detach();
         this.control = null;
      }

      if (this.view != null) {
         this.view.close();
         this.view = null;
      }

      this.boundClient = null;
      this.closeChat();
      this.chatCache.clear();
      this.chatSeeded = false;
   }

   public void removed() {
      this.setCursorLocked(false);
      this.unbind();
      super.removed();
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3) {
      BotClient botclient = this.ensureBound();
      BotPlayer botplayer = botclient != null ? botclient.getPlayer() : null;
      boolean flag = botclient != null && botclient.isJoined() && botplayer != null;
      ScreenHandler screenhandler = flag ? this.currentUiHandler(botclient, botplayer) : null;
      boolean flag1 = screenhandler != null;
      boolean flag2 = minecraftClient3.currentScreen != this;
      if (this.chatOpen && (!flag || flag1)) {
         this.closeChat();
      }

      boolean flag3 = !flag2 && flag && !flag1 && !this.chatOpen;
      if (this.control != null) {
         this.control.setUiScreenOpen(!flag3);
      }

      this.setCursorLocked(flag3);
      if (flag3) {
         this.clearMainPlayerInput();
         this.pollDirectMovement();
      } else {
         this.clearMovementKeys();
      }

      var1.drawRoundedRect(0.0F, 0.0F, this.width, this.height, CornerRadius.var159, new ArgbColor(10, 10, 14, 255));
      if (!flag) {
         String s1 = "Бот не в игре";
         var1.drawText(
            minecraftClient3.textRenderer, s1, (this.width - minecraftClient3.textRenderer.getWidth(s1)) / 2, this.height / 2 - 4, -1, true
         );
         String s2 = "Esc — назад";
         var1.drawText(
            minecraftClient3.textRenderer, s2, (this.width - minecraftClient3.textRenderer.getWidth(s2)) / 2, this.height / 2 + 8, -6643542, true
         );
      } else {
         int i = minecraftClient3.getWindow().getFramebufferWidth();
         int j = minecraftClient3.getWindow().getFramebufferHeight();
         boolean flag4 = this.view != null && this.view.renderToFbo(i, j);
         if (flag4) {
            ShapeRenderer.on23(
               var1.getMatrices(),
               this.view.getColorAttachment(),
               0.0F,
               0.0F,
               this.width,
               this.height,
               CornerRadius.var159,
               ArgbColor.var11934,
               0.0F,
               1.0F,
               1.0F,
               0.0F
            );
         } else {
            String s = "Загрузка мира бота...";
            var1.drawText(
               minecraftClient3.textRenderer, s, (this.width - minecraftClient3.textRenderer.getWidth(s)) / 2, this.height / 2 - 4, -1, true
            );
         }

         this.renderHud(var1, botclient, botplayer);
         this.renderScoreboardSidebar(var1, botclient);
         var1.draw();
         this.renderChat(var1, botclient);
         var1.draw();
         if (flag1) {
            this.renderContainer(var1, botplayer, screenhandler, (int)var2, (int)var3);
            var1.draw();
         } else {
            this.hoveredSlotCache = null;
            this.renderCrosshair(var1);
         }
      }
   }

   public ScreenHandler currentUiHandler(BotClient var1, BotPlayer var2) {
      BotPlayHandler botplayhandler = var1.getPlayHandler();
      if (botplayhandler != null && botplayhandler.hasOpenScreen()) {
         this.inventoryOpen = false;
         return var2.currentScreenHandler;
      } else {
         return this.inventoryOpen ? var2.playerScreenHandler : null;
      }
   }

   public void renderCrosshair(HudDrawContext var1) {
      int i = this.width / 2;
      int j = this.height / 2;
      int k = -855638017;
      var1.fill(i - 5, j, i + 4, j + 1, k);
      var1.fill(i, j - 5, i + 1, j + 4, k);
   }

   public void renderChat(HudDrawContext var1, BotClient var2) {
      List<ChatMessage> list = var2.getChatLog();
      long i = System.currentTimeMillis();
      this.syncChatCache(list, i);
      ArrayList arraylist = new ArrayList(list.size());
      this.chatTotalLines = 0;

      for (ChatMessage chatmessage : list) {
         BotControlScreen_CachedChat botcontrolscreen_cachedchat = this.chatCache.get(chatmessage);
         if (botcontrolscreen_cachedchat != null) {
            arraylist.add(botcontrolscreen_cachedchat);
            this.chatTotalLines = this.chatTotalLines + botcontrolscreen_cachedchat.lines().size();
         }
      }

      int l1 = this.visibleChatLines();
      this.chatScrollLines = MathHelper.clamp(this.chatScrollLines, 0, Math.max(0, this.chatTotalLines - l1));
      int i2 = this.chatOpen ? this.chatScrollLines : 0;
      int j2 = this.height - 52;
      byte b0 = 4;
      int j = 0;

      label55:
      for (int k = arraylist.size() - 1; k >= 0 && j < l1; k--) {
         BotControlScreen_CachedChat botcontrolscreen_cachedchat1 = (BotControlScreen_CachedChat)arraylist.get(k);
         float f = 1.0F;
         if (!this.chatOpen) {
            long l = i - botcontrolscreen_cachedchat1.seenAt();
            if (l >= 10000L) {
               break;
            }

            float f1 = (float)(10000L - l) / 10000.0F * 10.0F;
            f = MathHelper.clamp(f1, 0.0F, 1.0F);
            f *= f;
         }

         List<OrderedText> list1 = botcontrolscreen_cachedchat1.lines();

         for (int i1 = list1.size() - 1; i1 >= 0; i1--) {
            if (i2 > 0) {
               i2--;
            } else {
               int k2 = j2 - j * 9;
               int j1 = (int)(f * 128.0F);
               int k1 = (int)(f * 255.0F);
               if (k1 > 8) {
                  var1.fill(b0 - 2, k2 - 1, b0 + 320 + 2, k2 + 9 - 1, j1 << 24);
                  var1.drawText(minecraftClient3.textRenderer, list1.get(i1), b0, k2, k1 << 24 | 16777215, true);
               }

               if (++j >= l1) {
                  break label55;
               }
            }
         }
      }

      if (this.chatOpen) {
         this.renderChatField(var1);
      }
   }

   public void renderChatField(HudDrawContext var1) {
      this.clampChatFieldScroll();
      this.chatSuggestor.update(this.originalChatText);
      String s = this.chatField.getText();
      int i = this.height - 16;
      var1.drawRoundedRect(2.0F, i, this.width - 2, 14.0F, CornerRadius.var159, new ArgbColor(Integer.MIN_VALUE));
      byte b0 = 6;
      int j = i + 3;
      int k = MathHelper.clamp(this.chatField.var14348(), 0, s.length());
      String s1 = minecraftClient3.textRenderer.trimToWidth(s.substring(this.chatFirstChar), this.chatInnerWidth());
      if (this.chatField.float263() && !s.isEmpty()) {
         var1.fill(b0 - 1, i + 1, b0 + minecraftClient3.textRenderer.getWidth(s1) + 1, i + 13, -2139062144);
      }

      if (!s1.isEmpty()) {
         OrderedText orderedtext = this.chatSuggestor.provideRenderText(s1, this.chatFirstChar);
         var1.drawText(minecraftClient3.textRenderer, orderedtext, b0, j, -2039584, true);
      }

      String s2 = this.chatSuggestor.getSuggestion();
      boolean flag = this.chatFirstChar + s1.length() >= s.length();
      int l = b0 + minecraftClient3.textRenderer.getWidth(s1);
      if (s2 != null && !s2.isEmpty() && k == s.length() && flag) {
         var1.drawText(minecraftClient3.textRenderer, minecraftClient3.textRenderer.trimToWidth(s2, this.width - 6 - l), l, j, -8355712, true);
      }

      if (System.currentTimeMillis() / 300L % 2L == 0L) {
         int i1 = MathHelper.clamp(k - this.chatFirstChar, 0, s1.length());
         int j1 = b0 + minecraftClient3.textRenderer.getWidth(s1.substring(0, i1));
         if (k == s.length()) {
            var1.drawText(minecraftClient3.textRenderer, "_", j1, j, -2039584, true);
         } else {
            var1.fill(j1, j - 1, j1 + 1, j + 10, -3092272);
         }
      }

      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(0.0F, 0.0F);
      this.chatSuggestor.render(var1, var1.getMouseX(), var1.getMouseY());
      var1.getMatrices().popMatrix();
   }

   public int chatInnerWidth() {
      return this.width - 12;
   }

   public void clampChatFieldScroll() {
      String s = this.chatField.getText();
      int i = MathHelper.clamp(this.chatField.var14348(), 0, s.length());
      this.chatFirstChar = MathHelper.clamp(this.chatFirstChar, 0, s.length());
      if (i < this.chatFirstChar) {
         this.chatFirstChar = i;
      }

      while (this.chatFirstChar < i && minecraftClient3.textRenderer.getWidth(s.substring(this.chatFirstChar, i)) > this.chatInnerWidth()) {
         this.chatFirstChar++;
      }
   }

   public int chatCharacterX(int var1) {
      String s = this.chatField.getText();
      int i = MathHelper.clamp(var1, this.chatFirstChar, s.length());
      return 6 + minecraftClient3.textRenderer.getWidth(s.substring(this.chatFirstChar, i));
   }

   public void syncChatCache(List<ChatMessage> var1, long var2) {
      if (!this.chatSeeded) {
         this.chatSeeded = true;

         for (ChatMessage chatmessage : var1) {
            this.chatCache.put(chatmessage, new BotControlScreen_CachedChat(0L, this.wrapChatLines(chatmessage)));
         }
      }

      for (ChatMessage chatmessage1 : var1) {
         this.chatCache.computeIfAbsent(chatmessage1, var3 -> new BotControlScreen_CachedChat(var2, this.wrapChatLines(var3)));
      }

      if (this.chatCache.size() > var1.size()) {
         Set set = Collections.newSetFromMap(new IdentityHashMap());
         set.addAll(var1);
         this.chatCache.keySet().retainAll(set);
      }
   }

   public List<OrderedText> wrapChatLines(ChatMessage var1) {
      return ChatMessages.breakRenderedChatMessageLines(var1.text(), 312, minecraftClient3.textRenderer);
   }

   public int visibleChatLines() {
      return this.chatOpen ? MathHelper.clamp((this.height - 100) / 9, 4, 20) : 12;
   }

   public void openChat(String var1) {
      this.chatOpen = true;
      this.suppressNextChar = true;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.EventItemRenderHook(256);
      this.chatField.HudHotbarPanel(var1);
      this.chatField.EventRender(var1.length());
      this.chatField.VelocityChangeEvent(true);
      this.chatField.CrosshairTargetUpdateEvent(false);
      this.originalChatText = var1;
      this.chatLastMessage = "";
      BotClient botclient = this.boundClient;
      this.messageHistoryIndex = botclient != null ? botclient.getSentMessages().size() : 0;
      this.chatSuggestor.reset(var1);
      this.clearMovementKeys();
   }

   public void closeChat() {
      this.chatOpen = false;
      this.suppressNextChar = false;
      this.chatScrollLines = 0;
      this.chatFirstChar = 0;
      this.chatField.HudHotbarPanel("");
      this.chatField.EventRender(0);
      this.chatField.VelocityChangeEvent(false);
      this.originalChatText = "";
      this.chatSuggestor.setWindowActive(false);
      this.chatSuggestor.clearWindow();
   }

   public void setChatFromHistory(int var1) {
      BotClient botclient = this.boundClient;
      if (botclient != null) {
         List<String> list = botclient.getSentMessages();
         int i = MathHelper.clamp(this.messageHistoryIndex + var1, 0, list.size());
         if (i != this.messageHistoryIndex) {
            if (i == list.size()) {
               this.messageHistoryIndex = i;
               this.chatField.HudHotbarPanel(this.chatLastMessage);
               this.chatField.EventRender(this.chatLastMessage.length());
            } else {
               if (this.messageHistoryIndex == list.size()) {
                  this.chatLastMessage = this.chatField.getText();
               }

               String s = list.get(i);
               this.chatField.HudHotbarPanel(s);
               this.chatField.EventRender(s.length());
               this.chatSuggestor.update(this.originalChatText);
               this.chatSuggestor.setWindowActive(false);
               this.messageHistoryIndex = i;
            }
         }
      }
   }

   public void clearMovementKeys() {
      this.keyForward = this.keyBack = this.keyLeft = this.keyRight = this.keyJump = this.keySneak = this.keySprint = false;
      if (this.control != null) {
         this.control.setMovement(false, false, false, false, false, false, false);
      }
   }

   public void renderHud(HudDrawContext var1, BotClient var2, BotPlayer var3) {
      int i = this.width / 2;
      int j = this.height;
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, i - 91, j - 22, 182, 22);
      int k = var3.getInventory().selectedSlot;
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, i - 91 - 1 + k * 20, j - 22 - 1, 24, 23);
      ItemStack itemstack = var3.getOffHandStack();
      if (!itemstack.isEmpty()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29, j - 23, 29, 24);
      }

      int l = j - 16 - 3;

      for (int i1 = 0; i1 < 9; i1++) {
         ItemStack itemstack1 = (ItemStack)var3.getInventory().main.get(i1);
         int j1 = i - 90 + i1 * 20 + 2;
         this.drawStack(var1, itemstack1, j1, l);
      }

      if (!itemstack.isEmpty()) {
         this.drawStack(var1, itemstack, i - 91 - 26, l);
      }

      int j4 = i - 91;
      int k4 = i + 91;
      int l4 = j - 39;
      int k1 = MathHelper.ceil(var3.getHealth());
      int l1 = MathHelper.ceil(var3.getMaxHealth() / 2.0F);

      for (int i2 = 0; i2 < Math.min(l1, 10); i2++) {
         int j2 = j4 + i2 * 8;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_CONTAINER_TEXTURE, j2, l4, 9, 9);
         if (i2 * 2 + 1 < k1) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_FULL_TEXTURE, j2, l4, 9, 9);
         } else if (i2 * 2 + 1 == k1) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HEART_HALF_TEXTURE, j2, l4, 9, 9);
         }
      }

      int i5 = var3.getArmor();
      if (i5 > 0) {
         int j5 = l4 - 10;

         for (int k2 = 0; k2 < 10; k2++) {
            int l2 = j4 + k2 * 8;
            if (k2 * 2 + 1 < i5) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_FULL_TEXTURE, l2, j5, 9, 9);
            } else if (k2 * 2 + 1 == i5) {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_HALF_TEXTURE, l2, j5, 9, 9);
            } else {
               var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_TEXTURE, l2, j5, 9, 9);
            }
         }
      }

      int k5 = var3.getHungerManager().getFoodLevel();

      for (int l5 = 0; l5 < 10; l5++) {
         int j6 = k4 - l5 * 8 - 9;
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_EMPTY_TEXTURE, j6, l4, 9, 9);
         if (l5 * 2 + 1 < k5) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_FULL_TEXTURE, j6, l4, 9, 9);
         } else if (l5 * 2 + 1 == k5) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FOOD_HALF_TEXTURE, j6, l4, 9, 9);
         }
      }

      int i6 = var3.getMaxAir();
      int k6 = Math.min(var3.getAir(), i6);
      if (var3.isSubmergedInWater() || k6 < i6) {
         int i3 = l4 - 10;
         int j3 = MathHelper.ceil((k6 - 2.0) * 10.0 / i6);
         int k3 = MathHelper.ceil(k6 * 10.0 / i6) - j3;

         for (int l3 = 0; l3 < j3 + k3; l3++) {
            var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, l3 < j3 ? AIR_TEXTURE : AIR_EMPTY_TEXTURE, k4 - l3 * 8 - 9, i3, 9, 9);
         }
      }

      int l6 = j - 32 + 3;
      var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_BACKGROUND_TEXTURE, j4, l6, 182, 5);
      int i7 = (int)(var3.experienceProgress * 183.0F);
      if (i7 > 0) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, j4, l6, i7, 5);
      }

      if (var3.experienceLevel > 0) {
         String s = String.valueOf(var3.experienceLevel);
         int j7 = (this.width - minecraftClient3.textRenderer.getWidth(s)) / 2;
         int i4 = j - 31 - 4;
         var1.drawText(minecraftClient3.textRenderer, s, j7 + 1, i4, -16777216, false);
         var1.drawText(minecraftClient3.textRenderer, s, j7 - 1, i4, -16777216, false);
         var1.drawText(minecraftClient3.textRenderer, s, j7, i4 + 1, -16777216, false);
         var1.drawText(minecraftClient3.textRenderer, s, j7, i4 - 1, -16777216, false);
         var1.drawText(minecraftClient3.textRenderer, s, j7, i4, -8323296, false);
      }

      String s1 = var2.getName() + "  §7" + (int)var3.getX() + " " + (int)var3.getY() + " " + (int)var3.getZ();
      var1.drawText(minecraftClient3.textRenderer, s1, 6, 6, -1, true);
      String s2 = "Esc — выход";
      var1.drawText(minecraftClient3.textRenderer, s2, 6, 17, -6643542, true);
      BotRemoteControl botremotecontrol = this.control;
      String s3 = "§eCTRL4 §7tick "
         + var2.getTickCounter()
         + " / control "
         + (botremotecontrol != null ? botremotecontrol.getUpdateCount() : -1L)
         + " / loaded "
         + var3.isLoaded()
         + " / ui "
         + (botremotecontrol != null && botremotecontrol.uiScreenOpen)
         + " / serverUi "
         + (var2.getPlayHandler() != null && var2.getPlayHandler().hasOpenScreen())
         + " / applied "
         + (botremotecontrol != null && botremotecontrol.wasMovementActive)
         + " / input "
         + (this.keyForward ? 'W' : '-')
         + (this.keyBack ? 'S' : '-')
         + (this.keyLeft ? 'A' : '-')
         + (this.keyRight ? 'D' : '-');
      var1.drawText(minecraftClient3.textRenderer, s3, 6, 28, -1, true);
   }

   public void renderScoreboardSidebar(HudDrawContext var1, BotClient var2) {
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
            NumberFormat numberformat = scoreboardobjective.getNumberFormatOr(StyledNumberFormat.RED);
            List<ScoreboardEntry> object = new ArrayList<>(scoreboard.getScoreboardEntries(scoreboardobjective));
            object.removeIf(ScoreboardEntry::hidden);
            object.sort(InGameHud.SCOREBOARD_ENTRY_COMPARATOR);
            if (object.size() > 15) {
               object = object.subList(0, 15);
            }

            List<BotControlScreen_1SidebarRow> arraylist = new ArrayList<>(object.size());

            for (ScoreboardEntry scoreboardentry : object) {
               MutableText mutabletext = Team.decorateName(scoreboard.getScoreHolderTeam(scoreboardentry.owner()), scoreboardentry.name());
               MutableText mutabletext1 = scoreboardentry.formatted(numberformat);
               arraylist.add(new BotControlScreen_1SidebarRow(mutabletext, mutabletext1, minecraftClient3.textRenderer.getWidth(mutabletext1)));
            }

            Text text = scoreboardobjective.getDisplayName();
            int k1 = minecraftClient3.textRenderer.getWidth(text);
            int l1 = k1;
            int i2 = minecraftClient3.textRenderer.getWidth(": ");

            for (BotControlScreen_1SidebarRow botcontrolscreen_1sidebarrow : arraylist) {
               l1 = Math.max(
                  l1,
                  minecraftClient3.textRenderer.getWidth(botcontrolscreen_1sidebarrow.name())
                     + (botcontrolscreen_1sidebarrow.scoreWidth() > 0 ? i2 + botcontrolscreen_1sidebarrow.scoreWidth() : 0)
               );
            }

            byte b0 = 9;
            int j2 = arraylist.size() * b0;
            int i = this.height / 2 + j2 / 3;
            int j = this.width - 3;
            int k = j - l1;
            int l = i - j2;
            var1.fill(k - 2, l - b0 - 1, j + 2, l - 1, 1711276032);
            var1.fill(k - 2, l - 1, j + 2, i, 1275068416);
            var1.drawText(minecraftClient3.textRenderer, text, k + l1 / 2 - k1 / 2, l - b0, -1, false);

            for (int i1 = 0; i1 < arraylist.size(); i1++) {
               BotControlScreen_1SidebarRow botcontrolscreen_1sidebarrow1 = arraylist.get(i1);
               int j1 = l + i1 * b0;
               var1.drawText(minecraftClient3.textRenderer, botcontrolscreen_1sidebarrow1.name(), k, j1, -1, false);
               if (botcontrolscreen_1sidebarrow1.scoreWidth() > 0) {
                  var1.drawText(
                     minecraftClient3.textRenderer, botcontrolscreen_1sidebarrow1.score(), j - botcontrolscreen_1sidebarrow1.scoreWidth(), j1, -1, false
                  );
               }
            }
         }
      }
   }

   public void drawStack(HudDrawContext var1, ItemStack var2, int var3, int var4) {
      if (!var2.isEmpty()) {
         var1.drawItemWithoutEntity(var2, var3, var4);
         var1.drawStackOverlay(minecraftClient3.textRenderer, var2, var3, var4);
      }
   }

   public BotControlScreen_ContainerLayout layoutFor(BotPlayer var1, ScreenHandler var2) {
      if (var2 == var1.playerScreenHandler) {
         return new BotControlScreen_ContainerLayout(INVENTORY_TEXTURE, 176, 166, 0, 97, false, false);
      } else if (var2 instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         int k = genericcontainerscreenhandler.getRows();
         return new BotControlScreen_ContainerLayout(GENERIC_54_TEXTURE, 176, 114 + k * 18, k, 8, false, true);
      } else {
         if (var2 instanceof ShulkerBoxScreenHandler) {
            return new BotControlScreen_ContainerLayout(SHULKER_BOX_TEXTURE, 176, 166, 0, 8, false, true);
         }

         if (var2 instanceof HopperScreenHandler) {
            return new BotControlScreen_ContainerLayout(HOPPER_TEXTURE, 176, 133, 0, 8, false, true);
         }

         if (var2 instanceof Generic3x3ContainerScreenHandler) {
            return new BotControlScreen_ContainerLayout(DISPENSER_TEXTURE, 176, 166, 0, 8, true, true);
         }

         if (var2 instanceof AbstractFurnaceScreenHandler) {
            Identifier identifier = var2 instanceof BlastFurnaceScreenHandler ? BLAST_FURNACE_TEXTURE : (var2 instanceof SmokerScreenHandler ? SMOKER_TEXTURE : FURNACE_TEXTURE);
            return new BotControlScreen_ContainerLayout(identifier, 176, 166, 0, 8, true, true);
         }

         if (var2 instanceof CraftingScreenHandler) {
            return new BotControlScreen_ContainerLayout(CRAFTING_TABLE_TEXTURE, 176, 166, 0, 29, false, true);
         }

         int i = 160;
         int j = 142;

         for (Slot slot : var2.slots) {
            i = Math.max(i, slot.x);
            j = Math.max(j, slot.y);
         }

         return new BotControlScreen_ContainerLayout(null, Math.max(176, i + 16 + 8), j + 16 + 8, 0, 8, false, false);
      }
   }

   public void renderContainer(HudDrawContext var1, BotPlayer var2, ScreenHandler var3, int var4, int var5) {
      var1.drawRoundedRect(0.0F, 0.0F, this.width, this.height, CornerRadius.var159, new ArgbColor(0, 0, 0, 120));
      BotControlScreen_ContainerLayout botcontrolscreen_containerlayout = this.layoutFor(var2, var3);
      int i = (this.width - botcontrolscreen_containerlayout.width()) / 2;
      int j = (this.height - botcontrolscreen_containerlayout.height()) / 2;
      if (botcontrolscreen_containerlayout.texture() == null) {
         this.drawSyntheticPanel(var1, i, j, botcontrolscreen_containerlayout.width(), botcontrolscreen_containerlayout.height());

         for (Slot slot : var3.slots) {
            this.drawSlotFrame(var1, i + slot.x, j + slot.y);
         }
      } else if (botcontrolscreen_containerlayout.chestRows() > 0) {
         var1.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            botcontrolscreen_containerlayout.texture(),
            i,
            j,
            0.0F,
            0.0F,
            botcontrolscreen_containerlayout.width(),
            botcontrolscreen_containerlayout.chestRows() * 18 + 17,
            256,
            256
         );
         var1.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            botcontrolscreen_containerlayout.texture(),
            i,
            j + botcontrolscreen_containerlayout.chestRows() * 18 + 17,
            0.0F,
            126.0F,
            botcontrolscreen_containerlayout.width(),
            96,
            256,
            256
         );
      } else {
         var1.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            botcontrolscreen_containerlayout.texture(),
            i,
            j,
            0.0F,
            0.0F,
            botcontrolscreen_containerlayout.width(),
            botcontrolscreen_containerlayout.height(),
            256,
            256
         );
      }

      if (var3 == var2.playerScreenHandler) {
         BotClient botclient = this.boundClient;
         BotPlayerGuiRender.drawEntity(
            var1, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, var4, var5, var2, botclient != null ? botclient.getPlayHandler() : null
         );
      }

      Slot slot2 = this.hoveredSlot(var2, var3, var4, var5);
      this.hoveredSlotCache = slot2;
      if (slot2 != null && slot2.canBeHighlighted()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_TEXTURE, i + slot2.x - 4, j + slot2.y - 4, 24, 24);
      }

      for (Slot slot1 : var3.slots) {
         if (slot1.isEnabled()) {
            this.drawStack(var1, slot1.getStack(), i + slot1.x, j + slot1.y);
         }
      }

      if (slot2 != null && slot2.canBeHighlighted()) {
         var1.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_TEXTURE, i + slot2.x - 4, j + slot2.y - 4, 24, 24);
      }

      Text text = this.containerTitle(var2, var3);
      int k = botcontrolscreen_containerlayout.centerTitle()
         ? (botcontrolscreen_containerlayout.width() - minecraftClient3.textRenderer.getWidth(text)) / 2
         : botcontrolscreen_containerlayout.titleX();
      var1.drawText(minecraftClient3.textRenderer, text, i + k, j + 6, 4210752, false);
      if (botcontrolscreen_containerlayout.playerInventoryLabel()) {
         var1.drawText(
            minecraftClient3.textRenderer,
            Text.translatable("container.inventory"),
            i + 8,
            j + botcontrolscreen_containerlayout.height() - 94,
            4210752,
            false
         );
      }

      ItemStack itemstack = var3.getCursorStack();
      if (!itemstack.isEmpty()) {
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(0.0F, 0.0F);
         this.drawStack(var1, itemstack, var4 - 8, var5 - 8);
         var1.getMatrices().popMatrix();
      }

      if (slot2 != null && slot2.hasStack() && itemstack.isEmpty()) {
         ItemStack itemstack1 = slot2.getStack();
         List<Text> list = itemstack1.getTooltip(
            TooltipContext.create(var2.getWorld()), var2, minecraftClient3.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC
         );
         var1.drawTooltip(
            minecraftClient3.textRenderer, list, itemstack1.getTooltipData(), var4, var5, (Identifier)itemstack1.get(DataComponentTypes.TOOLTIP_STYLE)
         );
      }
   }

   public void drawSyntheticPanel(HudDrawContext var1, int var2, int var3, int var4, int var5) {
      var1.fill(var2 - 1, var3 - 1, var2 + var4 + 1, var3 + var5 + 1, -16777216);
      var1.fill(var2, var3, var2 + var4, var3 + var5, -3750202);
      var1.fill(var2, var3, var2 + var4 - 1, var3 + 1, -1);
      var1.fill(var2, var3 + 1, var2 + 1, var3 + var5 - 1, -1);
      var1.fill(var2 + 1, var3 + var5 - 1, var2 + var4, var3 + var5, -11184811);
      var1.fill(var2 + var4 - 1, var3 + 1, var2 + var4, var3 + var5 - 1, -11184811);
   }

   public void drawSlotFrame(HudDrawContext var1, int var2, int var3) {
      var1.fill(var2 - 1, var3 - 1, var2 + 17, var3 + 17, -7631989);
      var1.fill(var2 - 1, var3 - 1, var2 + 16, var3, -13158601);
      var1.fill(var2 - 1, var3, var2, var3 + 16, -13158601);
      var1.fill(var2, var3 + 16, var2 + 17, var3 + 17, -1);
      var1.fill(var2 + 16, var3, var2 + 17, var3 + 16, -1);
   }

   public Text containerTitle(BotPlayer var1, ScreenHandler var2) {
      if (var2 == var1.playerScreenHandler) {
         return Text.translatable("container.crafting");
      }

      BotClient botclient = this.boundClient;
      BotPlayHandler botplayhandler = botclient != null ? botclient.getPlayHandler() : null;
      Text text = botplayhandler != null ? botplayhandler.getCurrentScreenTitle() : null;
      return (Text)(text != null ? text : Text.literal("Контейнер"));
   }

   public Slot hoveredSlot(BotPlayer var1, ScreenHandler var2, int var3, int var4) {
      BotControlScreen_ContainerLayout botcontrolscreen_containerlayout = this.layoutFor(var1, var2);
      int i = (this.width - botcontrolscreen_containerlayout.width()) / 2;
      int j = (this.height - botcontrolscreen_containerlayout.height()) / 2;

      for (Slot slot : var2.slots) {
         if (slot.isEnabled()) {
            int k = var3 - i;
            int l = var4 - j;
            if (k >= slot.x - 1 && k < slot.x + 17 && l >= slot.y - 1 && l < slot.y + 17) {
               return slot;
            }
         }
      }

      return null;
   }

   public boolean isClickOutsideBounds(BotPlayer var1, ScreenHandler var2, double var3, double var5) {
      BotControlScreen_ContainerLayout botcontrolscreen_containerlayout = this.layoutFor(var1, var2);
      int i = (this.width - botcontrolscreen_containerlayout.width()) / 2;
      int j = (this.height - botcontrolscreen_containerlayout.height()) / 2;
      return var3 < i || var5 < j || var3 >= i + botcontrolscreen_containerlayout.width() || var5 >= j + botcontrolscreen_containerlayout.height();
   }

   public boolean uiOpen() {
      BotClient botclient = this.boundClient;
      BotPlayer botplayer = botclient != null ? botclient.getPlayer() : null;
      return botclient != null && botplayer != null ? this.currentUiHandler(botclient, botplayer) != null : false;
   }

   public void mouseMoved(double mouseX, double mouseY) {
      this.updateDirectMouse(mouseX, mouseY);
      super.mouseMoved(mouseX, mouseY);
   }

   public void pollDirectMovement() {
      var window = minecraftClient3.getWindow();
      boolean flag = InputUtil.isKeyPressed(window, 87);
      boolean flag1 = InputUtil.isKeyPressed(window, 83);
      boolean flag2 = InputUtil.isKeyPressed(window, 65);
      boolean flag3 = InputUtil.isKeyPressed(window, 68);
      boolean flag4 = InputUtil.isKeyPressed(window, 32);
      boolean flag5 = InputUtil.isKeyPressed(window, 340) || InputUtil.isKeyPressed(window, 344);
      boolean flag6 = InputUtil.isKeyPressed(window, 341) || InputUtil.isKeyPressed(window, 345);
      this.keyForward = flag;
      this.keyBack = flag1;
      this.keyLeft = flag2;
      this.keyRight = flag3;
      this.keyJump = flag4;
      this.keySneak = flag5;
      this.keySprint = flag6;
      if (this.control != null) {
         this.control.setMovement(flag, flag1, flag2, flag3, flag4, flag5, flag6);
      }
   }

   public void pollDirectMouse() {
      long i = minecraftClient3.getWindow().getHandle();
      GLFW.glfwGetCursorPos(i, this.directMouseX, this.directMouseY);
      this.updateDirectMouse(this.directMouseX[0], this.directMouseY[0]);
   }

   public void updateDirectMouse(double var1, double var3) {
      if (this.cursorLocked && this.control != null) {
         if (!Double.isNaN(this.lastMouseX)) {
            double d0 = var1 - this.lastMouseX;
            double d1 = var3 - this.lastMouseY;
            if (Math.abs(d0) <= 500.0 && Math.abs(d1) <= 500.0) {
               double d2 = (Double)minecraftClient3.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
               double d3 = d2 * d2 * d2 * 8.0 * minecraftClient3.getWindow().getScaleFactor();
               boolean flag = (Boolean)minecraftClient3.options.getInvertMouseY().getValue();
               this.control.turn(d0 * d3, d1 * d3 * (flag ? -1.0 : 1.0));
            }
         }

         this.lastMouseX = var1;
         this.lastMouseY = var3;
      } else {
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
      }
   }

   public void clearMainPlayerInput() {
      minecraftClient3.options.forwardKey.setPressed(false);
      minecraftClient3.options.backKey.setPressed(false);
      minecraftClient3.options.leftKey.setPressed(false);
      minecraftClient3.options.rightKey.setPressed(false);
      minecraftClient3.options.jumpKey.setPressed(false);
      minecraftClient3.options.sneakKey.setPressed(false);
      minecraftClient3.options.sprintKey.setPressed(false);
      if (minecraftClient3.player != null && minecraftClient3.player.input != null) {
         minecraftClient3.player.input.playerInput = PlayerInput.DEFAULT;
      }
   }

   public static boolean isControlContextActive() {
      return minecraftClient3.currentScreen instanceof BotControlScreen
         || minecraftClient3.currentScreen instanceof NLMenuScreen && Menu.menu.screen2 instanceof BotControlScreen;
   }

   @Override
   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.chatOpen) {
         this.chatSuggestor.mouseClicked(var1, var3);
      } else if (this.control != null) {
         BotClient botclient = this.boundClient;
         BotPlayer botplayer = botclient != null ? botclient.getPlayer() : null;
         if (botplayer != null) {
            ScreenHandler screenhandler = this.currentUiHandler(botclient, botplayer);
            if (screenhandler != null) {
               int i = var5 == MenuScreenId.call111 ? 1 : 0;
               Slot slot = this.hoveredSlot(botplayer, screenhandler, (int)var1, (int)var3);
               if (slot != null) {
                  SlotActionType slotactiontype = hasShiftDown() ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP;
                  this.control.clickSlot(slot.id, i, slotactiontype);
               } else if (!screenhandler.getCursorStack().isEmpty() && this.isClickOutsideBounds(botplayer, screenhandler, var1, var3)) {
                  this.control.clickSlot(64537, i, SlotActionType.PICKUP);
               }
            } else if (var5 == MenuScreenId.call004) {
               this.control.pressAttack();
            } else if (var5 == MenuScreenId.call111) {
               this.control.pressUse();
            }
         }
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (this.control != null) {
         if (var5 == MenuScreenId.call004) {
            this.control.releaseAttack();
         } else if (var5 == MenuScreenId.call111) {
            this.control.releaseUse();
         }
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      BotClient botclient = this.boundClient;
      boolean flag = botclient != null && botclient.getPlayHandler() != null && botclient.getPlayHandler().hasOpenScreen();
      if (this.chatOpen) {
         if (this.chatSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }

         if (keyCode == 256) {
            this.closeChat();
            return true;
         }

         if (keyCode == 257 || keyCode == 335) {
            String s = this.chatField.getText().trim();
            if (!s.isEmpty() && botclient != null) {
               botclient.addSentMessage(s);
               botclient.sendChat(s);
            }

            this.closeChat();
            return true;
         } else if (keyCode == 265) {
            this.setChatFromHistory(-1);
            return true;
         } else if (keyCode == 264) {
            this.setChatFromHistory(1);
            return true;
         } else if (keyCode == 266) {
            this.chatScrollLines = MathHelper.clamp(
               this.chatScrollLines + this.visibleChatLines() - 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else if (keyCode == 267) {
            this.chatScrollLines = MathHelper.clamp(
               this.chatScrollLines - this.visibleChatLines() + 1, 0, Math.max(0, this.chatTotalLines - this.visibleChatLines())
            );
            return true;
         } else {
            this.chatField.keyPressed(keyCode, scanCode, modifiers);
            return true;
         }
      } else if (keyCode == 256) {
         if (flag && this.control != null) {
            this.control.closeScreen();
            return true;
         }

         if (this.inventoryOpen) {
            this.inventoryOpen = false;
            if (this.control != null) {
               this.control.closeScreen();
            }

            return true;
         } else {
            minecraftClient3.setScreen(new BotScreen());
            return true;
         }
      } else if (keyCode == 69) {
         if (this.control != null) {
            if (flag) {
               this.control.closeScreen();
            } else if (this.inventoryOpen) {
               this.inventoryOpen = false;
               this.control.closeScreen();
            } else {
               this.inventoryOpen = true;
            }
         }

         return true;
      } else if (keyCode == 70 && this.control != null && this.uiOpen()) {
         BotPlayer botplayer = botclient != null ? botclient.getPlayer() : null;
         ScreenHandler screenhandler = botplayer != null ? this.currentUiHandler(botclient, botplayer) : null;
         Slot slot = this.hoveredSlotCache;
         if (screenhandler != null && slot != null && screenhandler.getCursorStack().isEmpty()) {
            this.control.clickSlot(slot.id, 40, SlotActionType.SWAP);
         }

         return true;
      } else {
         if (this.control != null && !this.uiOpen()) {
            if (keyCode >= 49 && keyCode <= 57) {
               this.wheelSlot = keyCode - 49;
               this.control.requestSlot(this.wheelSlot);
               return true;
            }

            if (keyCode == 81) {
               this.control.requestDrop(hasControlDown());
               return true;
            }

            if (keyCode == 70) {
               this.control.requestSwapHands();
               return true;
            }

            if (keyCode == 84) {
               this.openChat("");
               return true;
            }

            if (keyCode == 47) {
               this.openChat("/");
               return true;
            }
         }

         return this.updateMovementKey(keyCode, true) ? true : super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.suppressNextChar) {
         this.suppressNextChar = false;
         return true;
      } else if (this.chatOpen) {
         this.chatField.charTyped(chr, modifiers);
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.updateMovementKey(keyCode, false) ? true : super.keyReleased(keyCode, scanCode, modifiers);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.chatOpen && this.chatSuggestor.mouseScrolled(verticalAmount, mouseX, mouseY)) {
         return true;
      }

      if (this.chatOpen && verticalAmount != 0.0) {
         int k = Math.max(0, this.chatTotalLines - this.visibleChatLines());
         this.chatScrollLines = MathHelper.clamp(this.chatScrollLines + (verticalAmount > 0.0 ? 1 : -1), 0, k);
         return true;
      }

      if (this.control != null && verticalAmount != 0.0 && !this.uiOpen()) {
         BotClient botclient = this.boundClient;
         BotPlayer botplayer = botclient != null ? botclient.getPlayer() : null;
         if (botplayer != null) {
            int i = botplayer.getInventory().getSelectedSlot();
            int j = this.wheelSlot >= 0 && this.wheelSlot != i ? this.wheelSlot : i;
            this.wheelSlot = Math.floorMod(j - (int)Math.signum(verticalAmount), 9);
            this.control.requestSlot(this.wheelSlot);
            return true;
         }
      }

      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean updateMovementKey(int var1, boolean var2) {
      switch (var1) {
         case 32:
            this.keyJump = var2;
            break;
         case 65:
            this.keyLeft = var2;
            break;
         case 68:
            this.keyRight = var2;
            break;
         case 83:
            this.keyBack = var2;
            break;
         case 87:
            this.keyForward = var2;
            break;
         case 340:
            this.keySneak = var2;
            break;
         case 341:
            this.keySprint = var2;
            break;
         default:
            return false;
      }

      if (this.control != null) {
         this.control.setMovement(this.keyForward, this.keyBack, this.keyLeft, this.keyRight, this.keyJump, this.keySneak, this.keySprint);
      }

      return true;
   }

   public void setCursorLocked(boolean var1) {
      if (this.cursorLocked != var1) {
         this.cursorLocked = var1;
         this.lastMouseX = Double.NaN;
         this.lastMouseY = Double.NaN;
         double d0 = minecraftClient3.getWindow().getWidth() / 2.0;
         double d1 = minecraftClient3.getWindow().getHeight() / 2.0;
         InputUtil.setCursorParameters(minecraftClient3.getWindow(), var1 ? 212995 : 212993, d0, d1);
      }
   }

   private static boolean hasShiftDown() {
      var window = minecraftClient3.getWindow();
      return InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
   }

   private static boolean hasControlDown() {
      var window = minecraftClient3.getWindow();
      return InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
