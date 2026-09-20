package org.zenith.base.bot.client;

import java.util.Locale;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.util.StopWatch;
import org.zenith.util.WorldUtils;

public final class BotRctService {
   public static final int SOLO_MIN_ANARCHY = 1;
   public static final int DUO_MIN_ANARCHY = 18;
   public static final int DUO_MAX_ANARCHY = 38;
   public static final int TRIO_MIN_ANARCHY = 39;
   public static final int TRIO_MAX_ANARCHY = 57;
   public static final int CLAN_MIN_ANARCHY = 58;
   public static final int CLAN_MAX_ANARCHY = 74;
   public static final long COMMAND_INTERVAL_MS = 1500L;
   public static final long LOBBY_CONFIRM_MS = 1500L;
   public static final long MENU_CLICK_INTERVAL_MS = 500L;
   public static final long JOIN_TIMEOUT_MS = 10000L;
   public static final int MENU_CLICK_RETRY_LIMIT = 20;
   public static final String LITE_MENU_TITLE = "лайт анархии";
   public final BotClient client;
   public final StopWatch stopWatch = new StopWatch();
   public int anarchy;
   public boolean lobby;
   public boolean joining;
   public boolean hubCommandSent;
   public long lobbyDetectedAt;
   public boolean waitingForMenuUpdate;
   public int menuClickAttempts;
   public int clickedSyncId = -1;
   public int clickedInventorySize = -1;

   public BotRctService(BotClient var1) {
      this.client = var1;
   }

   public boolean isActive() {
      return this.anarchy != 0;
   }

   public int getTargetAnarchy() {
      return this.anarchy;
   }

   public boolean isHolyWorldHere() {
      BotPlayHandler botplayhandler = this.client.getPlayHandler();
      return botplayhandler != null && WorldUtils.TrajectoryDataset(botplayhandler.getBrand());
   }

   public int currentAnarchyHere() {
      return WorldUtils.UiAnimation(this.client.getWorld());
   }

   public void reconnect(int var1) {
      if (var1 >= 1 && var1 <= 74) {
         this.anarchy = var1;
         this.beginLobbyPhase();
      } else {
         this.client.systemMessage("RCT: неверный лайт " + var1);
      }
   }

   public void stop() {
      this.anarchy = 0;
      this.lobby = false;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
   }

   public void onGameMessage(Text var1) {
      if (this.anarchy != 0) {
         String s = var1.getString().toLowerCase(Locale.ROOT);
         if (!s.contains("хаб") && s.contains("не удалось")) {
            this.client.systemMessage("RCT: на анархию " + this.anarchy + " нельзя зайти");
            this.stop();
         }
      }
   }

   public void tick(BotWorld var1, BotPlayer var2) {
      if (this.anarchy != 0) {
         BotPlayHandler botplayhandler = this.client.getPlayHandler();
         if (botplayhandler != null && var1 != null && var2 != null) {
            if (!this.isHolyWorldHere()) {
               this.stop();
            } else {
               int i = this.currentAnarchyHere();
               if (this.lobby) {
                  this.resetMenuUpdate();
                  if (!this.hubCommandSent) {
                     if (this.stopWatch.ServiceException(1500.0)) {
                        botplayhandler.sendCommand("hub");
                        this.hubCommandSent = true;
                        this.lobbyDetectedAt = 0L;
                     }
                  } else if (i == -1) {
                     if (this.lobbyDetectedAt == 0L) {
                        this.lobbyDetectedAt = System.currentTimeMillis();
                     }

                     if (System.currentTimeMillis() - this.lobbyDetectedAt >= 1500L) {
                        this.lobby = false;
                        this.hubCommandSent = false;
                        this.lobbyDetectedAt = 0L;
                        this.stopWatch.reset();
                     }
                  } else {
                     this.lobbyDetectedAt = 0L;
                     if (this.stopWatch.ServiceException(1500.0)) {
                        botplayhandler.sendCommand("hub");
                     }
                  }
               } else if (i == this.anarchy) {
                  this.closeLiteMenu(var2, botplayhandler);
                  this.stop();
               } else if (i != -1) {
                  this.closeLiteMenu(var2, botplayhandler);
                  this.beginLobbyPhase();
               } else if (this.joining) {
                  this.resetMenuUpdate();
                  if (this.stopWatch.BotFeatureRegistry(10000.0)) {
                     this.beginLobbyPhase();
                  }
               } else {
                  GenericContainerScreenHandler genericcontainerscreenhandler = this.getLiteMenu(var2, botplayhandler);
                  if (genericcontainerscreenhandler != null) {
                     if (this.waitingForMenuUpdate && this.hasMenuUpdated(genericcontainerscreenhandler)) {
                        this.resetMenuUpdate();
                     }

                     if (this.stopWatch.ServiceException(500.0)) {
                        boolean flag = genericcontainerscreenhandler.getInventory().size() < 10;
                        int[] aint = this.getAnarchyMenuSlots(this.anarchy);
                        if (flag) {
                           if (this.menuClickAttempts >= 20) {
                              this.restartLiteMenu(var2, botplayhandler);
                              return;
                           }

                           botplayhandler.getInteractionManager().clickSlot(genericcontainerscreenhandler.syncId, aint[0], 0, SlotActionType.PICKUP, var2);
                           this.rememberMenuClick(genericcontainerscreenhandler);
                        } else {
                           this.resetMenuUpdate();
                           botplayhandler.getInteractionManager()
                              .clickSlot(genericcontainerscreenhandler.syncId, 18 + this.anarchy - aint[1], 0, SlotActionType.PICKUP, var2);
                           var2.closeHandledScreen();
                           this.joining = true;
                           this.stopWatch.reset();
                        }
                     }
                  } else {
                     this.resetMenuUpdate();
                     if (this.stopWatch.ServiceException(1500.0)) {
                        botplayhandler.sendCommand("lite");
                     }
                  }
               }
            }
         }
      }
   }

   public void rememberMenuClick(GenericContainerScreenHandler var1) {
      this.waitingForMenuUpdate = true;
      this.menuClickAttempts++;
      this.clickedSyncId = var1.syncId;
      this.clickedInventorySize = var1.getInventory().size();
   }

   public boolean hasMenuUpdated(GenericContainerScreenHandler var1) {
      return var1.syncId != this.clickedSyncId || var1.getInventory().size() != this.clickedInventorySize;
   }

   public void restartLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      this.resetMenuUpdate();
      var1.closeHandledScreen();
      var2.sendCommand("lite");
      this.stopWatch.reset();
   }

   public void resetMenuUpdate() {
      this.waitingForMenuUpdate = false;
      this.menuClickAttempts = 0;
      this.clickedSyncId = -1;
      this.clickedInventorySize = -1;
   }

   public void beginLobbyPhase() {
      this.lobby = true;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
      this.stopWatch.EventInjectHandleInputEvents(1500L);
   }

   public GenericContainerScreenHandler getLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      Text text = var2.getCurrentScreenTitle();
      if (text != null && text.getString().toLowerCase(Locale.ROOT).contains("лайт анархии")) {
         return var1.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler ? genericcontainerscreenhandler : null;
      } else {
         return null;
      }
   }

   public void closeLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      if (this.getLiteMenu(var1, var2) != null) {
         var1.closeHandledScreen();
      }
   }

   public int[] getAnarchyMenuSlots(int var1) {
      if (var1 >= 18 && var1 <= 38) {
         return new int[]{1, 18};
      } else if (var1 >= 39 && var1 <= 57) {
         return new int[]{2, 39};
      } else {
         return var1 >= 58 && var1 <= 74 ? new int[]{3, 58} : new int[]{0, 1};
      }
   }
}
