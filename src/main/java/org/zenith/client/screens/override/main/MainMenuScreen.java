package org.zenith.client.screens.override.main;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.button.ButtonScreen;
import org.zenith.client.screens.override.button.DefaultButton;
import org.zenith.core.ClientProvider;
import org.zenith.core.MenuScreenId;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.HudDrawContext;

public class MainMenuScreen extends Screen implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float BUTTON_WIDTH = 108.0F;
   public static final float EXIT_BUTTON_WIDTH = 48.0F;
   public static final float BUTTON_HEIGHT = 20.0F;
   public static final float BUTTON_GAP = 4.0F;
   public static final float EXIT_PADDING = 12.0F;
   public final MainMenuBlurRenderer blurRenderer = new MainMenuBlurRenderer();
   public final List<DefaultButton> buttons = new ArrayList<>();

   public MainMenuScreen() {
      super(Text.literal("MainMenu"));
      this.buttons.add(new DefaultButton("Singleplayer", "K", 108.0F, 20.0F, () -> minecraftClient3.setScreen(new SelectWorldScreen(this)), this.blurRenderer));
      this.buttons.add(new DefaultButton("Multiplayer", "J", 108.0F, 20.0F, () -> {
         Object object = minecraftClient3.options.skipMultiplayerWarning ? new MultiplayerScreen(new TitleScreen()) : new MultiplayerWarningScreen(new TitleScreen());
         minecraftClient3.setScreen((Screen)object);
      }, this.blurRenderer));
      this.buttons.add(new DefaultButton("AltManager", "a", 108.0F, 20.0F, () -> {}, this.blurRenderer));
      this.buttons
         .add(
            new DefaultButton(
               "Options", "F", 108.0F, 20.0F, () -> minecraftClient3.setScreen(new OptionsScreen(this, minecraftClient3.options)), this.blurRenderer
            )
         );
      this.buttons.add(new DefaultButton("Exit", "b", 48.0F, 20.0F, minecraftClient3::scheduleStop, this.blurRenderer));
   }

   protected void init() {
      ZenithClient.on23().StringCodec().init();
      super.init();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      HudDrawContext ililll1lli1i11l11l111i1l1 = HudDrawContext.of(context, mouseX, mouseY, delta);
      this.renderTop(ililll1lli1i11l11l111i1l1, ililll1lli1i11l11l111i1l1.getMouseX(), ililll1lli1i11l11l111i1l1.getMouseY());
   }

   @EventTarget
   public void on476(EventRenderScreenHook var1) {
   }

   public void renderTop(HudDrawContext var1, float var2, float var3) {
      Window window = minecraftClient3.getWindow();
      float f = window.getScaledWidth();
      float f1 = window.getScaledHeight();
      var1.drawTexture(ZenithClient.on23("menu/background.png"), 0.0F, 0.0F, f, f1, ArgbColor.var11934);
      ZenithClient.on23().StringCodec().renderParticles(var1);
      this.blurRenderer.capture(var1, 12.0F);
      Font font = Fonts.ICONS.getFont(20.0F);
      float f2 = font.height();
      String s = "5";
      float f3 = this.buttons.stream().map(ButtonScreen::getHeight).reduce(0.0F, Float::sum) + 4.0F * (this.buttons.size() - 2) + 12.0F;
      float f4 = (f1 - f3) / 2.0F + 20.0F;
      float f5 = (f - font.width(s)) / 2.0F;
      float f6 = f4 - 32.0F - f2;
      var1.drawText(font, s, f5, f6, ZenithClient.on23().TextScanner().getColorCycleIconGradient());

      for (int i = 0; i < this.buttons.size(); i++) {
         DefaultButton defaultbutton = this.buttons.get(i);
         float f7 = (f - defaultbutton.getWidth()) / 2.0F;
         defaultbutton.render(var1, var2, var3, f7, f4);
         f4 += defaultbutton.getHeight() + (i == this.buttons.size() - 2 ? 12.0F : 4.0F);
      }
   }

   public void removed() {
      this.blurRenderer.close();
      super.removed();
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
   }

   public void renderInGameBackground(DrawContext context) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      for (DefaultButton defaultbutton : this.buttons) {
         defaultbutton.onMouseClicked(mouseX, mouseY, MenuScreenId.Event37(button));
      }

      return super.mouseClicked(new net.minecraft.client.gui.Click(mouseX, mouseY, new net.minecraft.client.input.MouseInput(button, 0)), false);
   }

   @Override
   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }
}
