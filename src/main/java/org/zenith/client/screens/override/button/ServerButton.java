package org.zenith.client.screens.override.button;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.server.MenuMultiPlayerScreen;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ServerTheme;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.util.TextReplaceUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class ServerButton extends ButtonScreen {
   public final ServerInfo serverInfo;
   public final WorldIcon icon;
   public final UiAnimation hoverAnimation = new UiAnimation(250L, Easing.CloseScreenEvent);
   public final MenuMultiPlayerScreen multiplayerScreen;
   public static final ThreadPoolExecutor PINGER_EXECUTOR = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
   public byte[] lastFavicon;
   public String pingText = "...";
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public ServerButton(MenuMultiPlayerScreen var1, ServerInfo var2, float var3, float var4) {
      super(var3, var4);
      this.multiplayerScreen = var1;
      this.serverInfo = var2;
      this.icon = WorldIcon.forServer(minecraftClient3.getTextureManager(), var2.address);
      this.pingServer();
   }

   public void pingServer() {
      this.serverInfo.setStatus(Status.PINGING);
      PINGER_EXECUTOR.submit(() -> {
         try {
            this.multiplayerScreen.getServerListPinger().add(this.serverInfo, () -> {
               this.pingText = this.serverInfo.ping + "ms";
               this.updateFavicon();
            }, () -> {}, NetworkingBackend.remote(minecraftClient3.options.shouldUseNativeTransport()));
         } catch (Exception exception) {
            this.serverInfo.setStatus(Status.UNREACHABLE);
            this.pingText = "Error";
         }
      });
   }

   public void updateFavicon() {
      byte[] abyte = this.serverInfo.getFavicon();
      if (!Arrays.equals(abyte, this.lastFavicon) && abyte != null) {
         try {
            this.icon.load(NativeImage.read(abyte));
            this.lastFavicon = abyte;
         } catch (Exception var3) {
         }
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      super.render(var1, var2, var3, var4, var5);
      ServerTheme illiii11lll1lil11i = ServerTheme.var111;
      this.hoverAnimation.on23(this.bounds.PotionItemBuilder(var2, var3));
      var1.drawRoundedRect(
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         CornerRadius.MovementInputEvent(6.0F),
         illiii11lll1lil11i.string128().Easing(illiii11lll1lil11i.string31(), this.hoverAnimation.CancellableEvent())
      );
      var1.drawRoundedBorder(var4, var5, this.getWidth(), this.getHeight(), -0.1F, CornerRadius.MovementInputEvent(4.0F), illiii11lll1lil11i.file6());
      this.updateFavicon();
      float f = 14.0F;
      float f1 = (this.getHeight() - f) / 2.0F;
      Identifier identifier = this.icon.getTextureId();
      ShapeRenderer.on23(var1.getMatrices(), identifier, var4 + f1, var5 + f1, f, f, CornerRadius.MovementInputEvent(2.0F), ArgbColor.var11934);
      Font font = Fonts.MEDIUM.getFont(8.0F);
      Font font1 = Fonts.MEDIUM.getFont(7.0F);
      float f2 = var4 + f1 + f + 6.0F;
      var1.drawText(font, this.serverInfo.name, f2, var5 + f1 - 1.0F, ArgbColor.var11934);
      Text text = TextReplaceUtils.CloudApiClient(
         this.serverInfo.label != null ? this.serverInfo.label : Text.of(this.serverInfo.address)
      );
      var1.enableScissor((int)f2, 0, (int)(f2 + 170.0F), minecraftClient3.getWindow().getScaledHeight());
      var1.drawText(font1, text, f2, var5 + f1 + f - font1.height(), illiii11lll1lil11i.map40().call001());
      var1.disableScissor();
      String s = this.serverInfo.playerCountLabel != null ? this.serverInfo.playerCountLabel.getString() : "0/0";
      float f3 = font1.width(this.pingText);
      var1.drawText(font1, this.pingText, var4 + this.getWidth() - f3 - 5.0F, var5 + f1 - 1.0F, illiii11lll1lil11i.map39());
      var1.drawText(font1, s, var4 + this.getWidth() - font1.width(s) - 5.0F, var5 + f1 + 10.0F, illiii11lll1lil11i.map39());
      ShapeRenderer.on23(
         var1.getMatrices(),
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         0.1F,
         15.0F,
         ArgbColor.var11941.Easing(ZenithClient.on23().TextScanner().getCurrentStyle().getPrimaryColor().getColor(), this.hoverAnimation.CancellableEvent()),
         CornerRadius.MovementInputEvent(4.0F)
      );
   }

   @Override
   public void onClick(double var1, double var3, MenuScreenId var5) {
      if (var5 == MenuScreenId.call004) {
         ServerAddress serveraddress = ServerAddress.parse(this.serverInfo.address);
         ConnectScreen.connect(minecraftClient3.currentScreen, minecraftClient3, serveraddress, this.serverInfo, false, null);
      }
   }

   public void close() {
      this.icon.close();
   }
}
