package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;
import org.zenith.utility.render.display.base.GradientRadius;

public class ThemeColorCycler {
   public ServerTheme var1113;
   public ServerTheme var1114 = ServerTheme.var111;
   public final UiAnimation var14360;
   public final ColorAnimator isOnGround;
   public final List<ServerTheme> isFalse2 = new ArrayList<>(List.of(ServerTheme.var111, ServerTheme.getRotation, ServerTheme.var1112));
   public int int482 = 0;

   public ThemeColorCycler() {
      EventManager.register(this);
      this.var14360 = new UiAnimation(200L, 1.0F, Easing.CloseScreenEvent);
      this.var14360.UiAnimation(true);
      this.isOnGround = new ColorAnimator(1700L);
   }

   public void on23(ServerTheme var1) {
      if (this.var14360.isDone()) {
         this.var1113 = this.var1114;
         this.var1114 = var1;
         int i = this.isFalse2.indexOf(var1);
         if (i != -1) {
            this.int482 = i;
         }

         this.var14360.reset();
      }
   }

   public void float308() {
      if (this.var14360.isDone()) {
         this.var1113 = this.var1114;
         this.int482 = (this.int482 + 1) % this.isFalse2.size();
         this.var1114 = this.isFalse2.get(this.int482);
         this.var14360.reset();
      }
   }

   public void AttackEntityEvent(String var1) {
      if (var1 != null) {
         ServerTheme illiii11lll1lil11i;
         switch (var1) {
            case "Dark":
               illiii11lll1lil11i = ServerTheme.var111;
               break;
            case "Light":
               illiii11lll1lil11i = ServerTheme.getRotation;
               break;
            case "Custom":
               illiii11lll1lil11i = ServerTheme.var1112;
               break;
            default:
               return;
         }

         this.on23(illiii11lll1lil11i);
      }
   }

   @EventTarget
   public void on474(EventRenderScreenHook var1) {
      this.isOnGround.update();
      this.var14360.on23(1.0F);
   }

   public void on23(List<ArgbColor> var1, ArgbColor var2, ArgbColor var3, ArgbColor var4, ArgbColor var5) {
      var1.set(0, var2);
      var1.set(1, var3);
      var1.set(2, var4);
      var1.set(3, var5);
   }

   public ServerTheme int422() {
      return this.var14360.isDone() ? this.var1114 : this.var1113.on23(this.var1114, this.var14360.CancellableEvent());
   }

   public boolean UiAnimation(ServerTheme var1) {
      return this.var1114 == var1;
   }

   public GradientRadius getClientColor() {
      return GradientRadius.on23(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
   }

   public ArgbColor getClientColor(int var1) {
      return ColorUtils.on23(4, var1, this.int422().getColor(), this.int422().path11());
   }

   public ArgbColor getGlowColor(int var1) {
      return ColorUtils.on23(4, var1, this.int422().type(), this.int422().list88());
   }

   public GradientRadius getGlowColor() {
      return GradientRadius.on23(this.getGlowColor(0), this.getGlowColor(90), this.getGlowColor(180), this.getGlowColor(270));
   }

   public ServerTheme render3() {
      return this.var1113;
   }

   public UiAnimation getEvent11() {
      return this.var14360;
   }

   public ColorAnimator getColorCycleIcon() {
      return this.isOnGround;
   }

   public List<ServerTheme> path13() {
      return this.isFalse2;
   }

   public int var0() {
      return this.int482;
   }
}
