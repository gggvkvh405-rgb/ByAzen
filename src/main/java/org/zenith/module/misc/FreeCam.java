package org.zenith.module.misc;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.base.font.Fonts;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventPosHook;
import org.zenith.event.HudRenderEvent;
import org.zenith.event.MovementInputEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.NumberSetting;
import org.zenith.util.MathUtils;
import org.zenith.util.MovementUtils;

@ModuleInfo(name = "FreeCam", description = "Свободный обзор камеры летать можно", category = Category.MISC)
public final class FreeCam extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final FreeCam freeCam = new FreeCam();
   public final NumberSetting speedSetting = new NumberSetting("module.freeCam.speedSetting", 2.0F, 0.5F, 5.0F, 0.5F, "module.freeCam.speedSetting.desc", "x");
   public Vec3d TriggerBot;
   public Vec3d ModeSetting;

   public Vec3d var1357() {
      return this.isEnabled() ? this.TriggerBot : minecraftClient3.getCameraEntity().getEyePos();
   }

   @Override
   public void onEnable() {
      this.ModeSetting = this.TriggerBot = new Vec3d(minecraftClient3.getEntityRenderDispatcher().camera.getCameraPos().toVector3f());
      super.onEnable();
   }

   @Override
   public void onDisable() {
      minecraftClient3.options.setPerspective(Perspective.FIRST_PERSON);
      super.onDisable();
   }

   @EventTarget
   public void ColorAnimator(EventHookWorldRender var1) {
      WorldRender.on23(
         minecraftClient3.player
            .getBoundingBox()
            .offset(MathUtils.CloudResponse(minecraftClient3.player).subtract(minecraftClient3.player.getEntityPos())),
         val003.TextScanner().getClientColor(90).call001(),
         1.0F
      );
   }

   @EventTarget
   public void Easing(HudRenderEvent var1) {
      String s = MathUtils.round((float)this.TriggerBot.x)
         + "   "
         + MathUtils.round((float)this.TriggerBot.y)
         + "   "
         + MathUtils.round((float)this.TriggerBot.z);
      float f = Fonts.MEDIUM.getWidth(s, 7.0F);
      var1.Bot()
         .drawText(
            Fonts.MEDIUM.getFont(7.0F),
            s,
            minecraftClient3.getWindow().getScaledWidth() / 2.0F - f / 2.0F,
            10.0F,
            ZenithClient.on23().TextScanner().getCurrentStyle().getTextEnable().getColor()
         );
   }

   @EventTarget
   public void TextScanner(MovementInputEvent var1) {
      float f = this.speedSetting.getCurrent();
      double[] adouble = MovementUtils.FileLogger(f);
      this.ModeSetting = this.TriggerBot;
      this.TriggerBot = this.TriggerBot.add(adouble[0], var1.NoSweetSlow().jump() ? f : (var1.NoSweetSlow().sneak() ? -f : 0.0), adouble[1]);
      var1.NoSlow();
   }

   @EventTarget
   public void on23(EventPosHook var1) {
      if (this.ModeSetting != null && this.TriggerBot != null) {
         var1.UiAnimation(MathUtils.NbtEditor(this.ModeSetting, this.TriggerBot));
         minecraftClient3.options.setPerspective(Perspective.THIRD_PERSON_BACK);
      }
   }
}
