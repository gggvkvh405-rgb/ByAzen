package org.zenith.client.screens.override.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.button.ServerButton;
import org.zenith.core.MenuScreenId;
import org.zenith.core.ServerTheme;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class MenuMultiPlayerScreen extends Screen {
   public final Screen parent;
   public final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
   public final List<ServerButton> serverButtons = new CopyOnWriteArrayList<>();
   public ServerList serverList;
   public float scrollY = 0.0F;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public MenuMultiPlayerScreen(Screen var1) {
      super(Text.literal("Multiplayer"));
      this.parent = var1;
   }

   protected void init() {
      ZenithClient.on23().StringCodec().init();
      this.serverList = new ServerList(minecraftClient3);
      this.serverList.loadFile();
      this.clearButtons();
      float f = 260.0F;
      float f1 = 30.0F;

      for (int i = 0; i < this.serverList.size(); i++) {
         this.serverButtons.add(new ServerButton(this, this.serverList.get(i), f, f1));
      }
   }

   public void clearButtons() {
      for (ServerButton serverbutton : this.serverButtons) {
         serverbutton.close();
      }

      this.serverButtons.clear();
   }

   public void tick() {
      this.serverListPinger.tick();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      HudDrawContext ililll1lli1i11l11l111i1l1 = HudDrawContext.of(context, mouseX, mouseY, delta);
      this.renderBackgroundAndParticles(ililll1lli1i11l11l111i1l1);
      this.renderServerList(ililll1lli1i11l11l111i1l1, mouseX, mouseY);
   }

   public void renderBackgroundAndParticles(HudDrawContext var1) {
      Window window = minecraftClient3.getWindow();
      float f = window.getScaledWidth();
      float f1 = window.getScaledHeight();
      var1.drawTexture(ZenithClient.on23("menu/background.png"), 0.0F, 0.0F, f, f1, ArgbColor.var11934);
      ZenithClient.on23().StringCodec().renderParticles(var1);
   }

   public void renderServerList(HudDrawContext var1, float var2, float var3) {
      Window window = minecraftClient3.getWindow();
      float f = window.getScaledWidth();
      float f1 = window.getScaledHeight();
      ServerTheme illiii11lll1lil11i = ServerTheme.var111;
      float f2 = 265.0F;
      float f3 = 209.0F;
      float f4 = (f - f2) / 2.0F;
      float f5 = (f1 - f3) / 2.0F;
      float f6 = 4.0F;
      float f7 = this.serverButtons.size() * (30.0F + f6);
      float f8 = Math.max(0.0F, f7 - f3);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -f8) {
         this.scrollY = -f8;
      }

      float f9 = f5 + this.scrollY;
      var1.enableScissor((int)f4, (int)f5, (int)(f4 + f2), (int)(f5 + f3));

      for (ServerButton serverbutton : this.serverButtons) {
         if (f9 + serverbutton.getHeight() > f5 && f9 < f5 + f3) {
            serverbutton.render(var1, var2, var3, f4 + (f2 - serverbutton.getWidth()) / 2.0F, f9);
         }

         f9 += serverbutton.getHeight() + f6;
      }

      var1.disableScissor();
      if (f7 > f3) {
         float f13 = 1.0F;
         float f14 = f4 + f2 + 4.0F - f13 - 2.0F;
         float f10 = f3 * (f3 / f7);
         float f11 = f5 + -this.scrollY / f7 * f3;
         var1.drawRoundedRect(f14, f5, f13, f3, CornerRadius.var159, illiii11lll1lil11i.string128());
         var1.drawRoundedRect(f14, f11, f13, f10, CornerRadius.var159, illiii11lll1lil11i.file6());
      }

      Font font = Fonts.ICONS.getFont(20.0F);
      float f15 = font.height();
      String s = "J";
      float f16 = (f - font.width(s)) / 2.0F;
      float f12 = f5 - 32.0F - f15;
      var1.drawText(font, s, f16, f12, ZenithClient.on23().TextScanner().getColorCycleIcon().VelocityChangeEvent());
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      for (ServerButton serverbutton : this.serverButtons) {
         serverbutton.onMouseClicked(mouseX, mouseY, MenuScreenId.Event37(button));
      }

      return true;
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scrollY += (float)(verticalAmount * 15.0);
      float f = Math.max(0, this.serverButtons.size() * 30);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -f) {
         this.scrollY = -f;
      }

      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public void close() {
      minecraftClient3.setScreen(this.parent);
   }

   public void removed() {
      this.clearButtons();
      this.serverListPinger.cancel();
   }

   public MultiplayerServerListPinger getServerListPinger() {
      return this.serverListPinger;
   }
}
