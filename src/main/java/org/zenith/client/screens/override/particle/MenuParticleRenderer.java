package org.zenith.client.screens.override.particle;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;
import org.zenith.core.RenderHook;
import org.zenith.event.EventWindowSizeChanged;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class MenuParticleRenderer implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final List<MenuParticleRenderer_MenuParticle> particles = new ArrayList<>();

   public MenuParticleRenderer() {
      for (int i = 0; i < 100; i++) {
         this.particles.add(new MenuParticleRenderer_MenuParticle(this, 1920.0F, 1080.0F, true));
      }

      EventManager.register(this);
   }

   @EventTarget
   public void on48(EventWindowSizeChanged var1) {
      this.init();
   }

   public void init() {
      this.particles.clear();

      for (int i = 0; i < 100; i++) {
         this.particles
            .add(
               new MenuParticleRenderer_MenuParticle(this, minecraftClient3.getWindow().getScaledWidth(), minecraftClient3.getWindow().getScaledHeight(), true)
            );
      }
   }

   public static void renderBackgroundAndParticles(HudDrawContext var0) {
      Window window = minecraftClient3.getWindow();
      float f = window.getScaledWidth();
      float f1 = window.getScaledHeight();
      var0.drawRoundedTexture(ZenithClient.on23("menu/background.png"), 0.0F, 0.0F, f, f1, CornerRadius.var159, ArgbColor.var11934.EventHookWorldRender(255));
      ZenithClient.on23().StringCodec().renderParticles(var0, getScreenFadeProgress());
   }

   public static float getScreenFadeProgress() {
      Screen screen = minecraftClient3.currentScreen;
      if (screen == null) {
         return 1.0F;
      }

      RenderHook li11l1lilili1l = (RenderHook)screen;
      long i = li11l1lilili1l.zenithDLC_callGetStartTime();
      long j = System.currentTimeMillis();
      float f = (float)(j - i) / 200.0F;
      return Math.max(0.0F, Math.min(1.0F, f));
   }

   public void renderParticles(HudDrawContext var1) {
      this.renderParticles(var1, 1.0F);
   }

   public void renderParticles(HudDrawContext var1, float var2) {
      Window window = minecraftClient3.getWindow();
      float f = window.getScaledWidth();
      float f1 = window.getScaledHeight();

      for (MenuParticleRenderer_MenuParticle menuparticlerenderer_menuparticle : this.particles) {
         menuparticlerenderer_menuparticle.update(f, f1, var1.getDelta());
         int i = (int)(menuparticlerenderer_menuparticle.alpha * 180.0F);
         var1.drawTexture(
            ZenithClient.on23("textures/glow.png"),
            menuparticlerenderer_menuparticle.x,
            menuparticlerenderer_menuparticle.y,
            menuparticlerenderer_menuparticle.size,
            menuparticlerenderer_menuparticle.size,
            new ArgbColor(255, 255, 255, (int)(i * var2))
         );
      }
   }
}
