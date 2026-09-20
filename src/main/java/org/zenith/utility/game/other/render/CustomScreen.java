package org.zenith.utility.game.other.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.text.Text;
import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class CustomScreen extends Screen {
   protected CustomScreen() {
      super(Text.empty());
   }

   public abstract void render(HudDrawContext var1, float var2, float var3);

   public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
      HudDrawContext ililll1lli1i11l11l111i1l1 = HudDrawContext.of(context, mouseX, mouseY, delta);
      this.render(ililll1lli1i11l11l111i1l1, mouseX, mouseY);
      super.render(context, mouseX, mouseY, delta);
   }

   @Override
   public final boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public final boolean mouseClicked(double mouseX, double mouseY, int button) {
      MenuScreenId ll1lil1ii1iil1l = MenuScreenId.Event37(button);
      this.onMouseClicked(mouseX, mouseY, ll1lil1ii1iil1l);
      return super.mouseClicked(new Click(mouseX, mouseY, new MouseInput(button, 0)), false);
   }

   public void tick() {
   }

   @Override
   public final boolean mouseReleased(Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   public final boolean mouseReleased(double mouseX, double mouseY, int button) {
      MenuScreenId ll1lil1ii1iil1l = MenuScreenId.Event37(button);
      this.onMouseReleased(mouseX, mouseY, ll1lil1ii1iil1l);
      return super.mouseReleased(new Click(mouseX, mouseY, new MouseInput(button, 0)));
   }

   @Override
   public final boolean mouseDragged(Click click, double deltaX, double deltaY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), deltaX, deltaY);
   }

   public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      MenuScreenId ll1lil1ii1iil1l = MenuScreenId.Event37(button);
      this.onMouseDragged(mouseX, mouseY, ll1lil1ii1iil1l, deltaX, deltaY);
      return super.mouseDragged(new Click(mouseX, mouseY, new MouseInput(button, 0)), deltaX, deltaY);
   }

   @Override
   public final boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return super.keyPressed(new KeyInput(keyCode, scanCode, modifiers));
   }

   @Override
   public final boolean keyReleased(KeyInput input) {
      return this.keyReleased(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return super.keyReleased(new KeyInput(keyCode, scanCode, modifiers));
   }

   @Override
   public final boolean charTyped(CharInput input) {
      String value = input.asString();
      return !value.isEmpty() && this.charTyped(value.charAt(0), input.modifiers());
   }

   public boolean charTyped(char character, int modifiers) {
      return super.charTyped(new CharInput(character, modifiers));
   }

   public void onMouseClicked(double var1, double var3, MenuScreenId var5) {
   }

   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
   }

   public void onMouseDragged(double var1, double var3, MenuScreenId var5, double var6, double var8) {
   }
}
