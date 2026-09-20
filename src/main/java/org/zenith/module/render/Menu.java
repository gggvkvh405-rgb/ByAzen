package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.zenith.ZenithClient;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen_ElementsType;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.utility.render.display.base.HudDrawContext;

@ModuleInfo(name = "Menu", category = Category.RENDER, description = "Меню чита")
public final class Menu extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Menu menu = new Menu();
   public final ModeSetting animation2 = new ModeSetting(
      "module.menu.animation", "module.menu.animation.desc", "module.menu.animation.normal", "module.menu.animation.blur"
   );
   public final NumberSetting closeAnimationSpeed = new NumberSetting(
      "module.menu.closeAnimationSpeed", 300.0F, 100.0F, 500.0F, 10.0F, "module.menu.closeAnimationSpeed.desc", "ms"
   );
   public final NumberSetting elementSwitchSpeed = new NumberSetting(
      "module.menu.elementSwitchSpeed", 250.0F, 100.0F, 500.0F, 10.0F, "module.menu.elementSwitchSpeed.desc", "ms"
   );
   public final BooleanSetting moveGui = new BooleanSetting("module.menu.moveGui", "module.menu.moveGui.desc", false);
   public boolean boolean123;
   public float float144;
   public float float145;
   public Screen screen2;

   public Menu() {
      this.setKeyCode(344);
   }

   @Override
   public void onEnable() {
      if (minecraftClient3.mouse == null) {
         this.setEnabled(false);
      } else {
         this.screen2 = minecraftClient3.currentScreen;
         if (minecraftClient3.currentScreen != ZenithClient.on23().NbtEditor()) {
            NLMenuScreen nlmenuscreen = ZenithClient.on23().NbtEditor();
            nlmenuscreen.initialize();
            boolean flag = this.screen2 instanceof BotControlScreen;
            nlmenuscreen.getGuiModulePanel().setBotOnly(false);
            nlmenuscreen.getGuiModulePanel().setBotContext(flag ? ((BotControlScreen)this.screen2).getBotName() : null);
            if (flag) {
               nlmenuscreen.resetSearch();
               nlmenuscreen.setType(NLMenuScreen_ElementsType.CATEGORY);
            }

            minecraftClient3.setScreen(nlmenuscreen);
            ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent);
            super.onEnable();
         }
      }
   }

   @Override
   public void onDisable() {
      if (minecraftClient3.currentScreen == ZenithClient.on23().NbtEditor()) {
         this.setEnabled(true);
      } else {
         super.onDisable();
      }
   }

   @Override
   public void setKeyCode(int var1) {
      if (var1 != -1) {
         super.setKeyCode(var1);
      }
   }

   @EventTarget(3)
   public void ColorAnimator(EventRenderScreenHook var1) {
      HudDrawContext ililll1lli1i11l11l111i1l1 = var1.WarpFarm();
      if (this.screen2 != null) {
         this.screen2.render(ililll1lli1i11l11l111i1l1, 0, 0, minecraftClient3.getRenderTickCounter().getTickProgress(false));
      }

      ZenithClient.on23().NbtEditor().renderTop(ililll1lli1i11l11l111i1l1, ililll1lli1i11l11l111i1l1.getMouseX(), ililll1lli1i11l11l111i1l1.getMouseY());
      if (ZenithClient.on23().NbtEditor().isFinish()) {
         if (this.screen2 instanceof BotControlScreen) {
            Screen screen = this.screen2;
            this.screen2 = null;
            if (minecraftClient3.currentScreen == ZenithClient.on23().NbtEditor() || minecraftClient3.currentScreen == null) {
               minecraftClient3.setScreen(screen);
            }
         }

         this.toggle();
      }
   }

   public boolean int467() {
      return this.animation2.is(1);
   }

   public long int468() {
      return (long)this.closeAnimationSpeed.getCurrent();
   }

   public long int469() {
      return (long)this.elementSwitchSpeed.getCurrent();
   }

   public long int470() {
      return (long)this.elementSwitchSpeed.getCurrent();
   }

   public boolean float376() {
      return this.moveGui.isEnabled();
   }

   public float VelocityChangeEvent(float var1) {
      return this.boolean123 ? this.float144 : var1;
   }

   public float CrosshairTargetUpdateEvent(float var1) {
      return this.boolean123 ? this.float145 : var1;
   }

   public void CloudPoller(float var1, float var2) {
      this.float144 = var1;
      this.float145 = var2;
      this.boolean123 = true;
   }

   @Override
   public JsonObject save() {
      JsonObject jsonobject = super.save();
      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.addProperty("saved", this.boolean123);
      jsonobject1.addProperty("x", this.float144);
      jsonobject1.addProperty("y", this.float145);
      jsonobject.add("GuiPosition", jsonobject1);
      return jsonobject;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("GuiPosition") && var1.get("GuiPosition").isJsonObject()) {
         JsonObject jsonobject = var1.getAsJsonObject("GuiPosition");
         if (jsonobject.has("saved")) {
            this.boolean123 = jsonobject.get("saved").getAsBoolean();
         }

         if (jsonobject.has("x")) {
            this.float144 = jsonobject.get("x").getAsFloat();
         }

         if (jsonobject.has("y")) {
            this.float145 = jsonobject.get("y").getAsFloat();
         }
      }
   }
}
