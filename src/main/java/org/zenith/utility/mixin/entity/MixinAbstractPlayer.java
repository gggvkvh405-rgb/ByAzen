package org.zenith.utility.mixin.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.ZenithClient;
import org.zenith.core.ClickFxController;
import org.zenith.core.ClickFxPoint;
import org.zenith.core.ClickFxState;
import org.zenith.core.TranslationKey;
import org.zenith.module.render.Cape;

@Mixin(PlayerEntity.class)
public abstract class MixinAbstractPlayer implements TranslationKey {
   @Unique
   public final ClickFxController zenith_stickSimulation = new ClickFxController();
   @Unique
   public double zenith_prevX;
   @Unique
   public double zenith_prevY;
   @Unique
   public double zenith_prevZ;
   @Unique
   public boolean zenith_initialized = false;
   @Unique
   public boolean zenith_wasOnGround = true;
   @Unique
   public float zenith_jumpImpulse = 0.0F;

   @Override
   public void zenith_simulate() {
      PlayerEntity playerentity = (PlayerEntity)(Object)this;
      this.zenith_stickSimulation.AttackEntityEvent(ClickFxController.string69());
      TranslationKey.on23(playerentity.getId(), this.zenith_stickSimulation);
      if (!this.zenith_initialized) {
         this.zenith_prevX = playerentity.getX();
         this.zenith_prevY = playerentity.getY();
         this.zenith_prevZ = playerentity.getZ();
         this.zenith_initialized = true;
      } else {
         double d0 = playerentity.getX() - this.zenith_prevX;
         double d1 = playerentity.getY() - this.zenith_prevY;
         double d2 = playerentity.getZ() - this.zenith_prevZ;
         double d3 = playerentity.getVelocity().y;
         boolean flag = playerentity.isOnGround();
         if (this.zenith_wasOnGround && !flag && d3 > 0.0) {
            this.zenith_jumpImpulse = 10.0F;
         }

         float f;
         if (d3 < -0.01) {
            f = (float)Math.abs(d3);
         } else {
            f = 0.0F;
         }

         this.zenith_wasOnGround = flag;
         float f1 = (float)Math.toRadians(playerentity.bodyYaw);
         float f2 = MathHelper.sin(f1);
         float f3 = MathHelper.cos(f1);
         double d4 = d0 * f3 + d2 * f2;
         double d5 = -d0 * f2 + d2 * f3;
         float f4 = Cape.cape.int398();
         if (this.zenith_jumpImpulse > 0.1F) {
            for (int i = 1; i < this.zenith_stickSimulation.list70.size(); i++) {
               ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = this.zenith_stickSimulation.list70.get(i);
               float f5 = (float)i / this.zenith_stickSimulation.list70.size();
               l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var165.y = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var165.y
                  - this.zenith_jumpImpulse * f5 * (0.2F + f4 * 0.3F);
               l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var165.x = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var165.x
                  + this.zenith_jumpImpulse * f5 * (0.15F + f4 * 0.2F);
            }

            this.zenith_jumpImpulse *= 0.6F;
         }

         if (f > 0.01F) {
            float f7 = f * f4 * 20.0F;

            for (int j = 1; j < this.zenith_stickSimulation.list70.size(); j++) {
               ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iil = this.zenith_stickSimulation.list70.get(j);
               float f6 = (float)j / this.zenith_stickSimulation.list70.size();
               l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var165.y -= f7 * f6;
               l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var165.x += f7 * f6 * 0.6F;
            }
         }

         float f8 = Cape.cape.boolean106();
         float f9 = 2.0F + f4 * 4.0F;
         ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1 = new ClickFxPoint((float)(d5 * f9), (float)(d1 * f9 * 0.8F), (float)(d4 * f9));
         this.zenith_stickSimulation.UiAnimation(l111lliil1ilill1l1i11li_illi1l1l1);
         this.zenith_stickSimulation.BotWorldJoinEvent(playerentity.isSneaking());
         this.zenith_stickSimulation.EventTriggerKeyEvent(25.0F * f8);
         this.zenith_stickSimulation.int380 = (int)(10.0F + Cape.cape.boolean107() * 20.0F);
         this.zenith_stickSimulation.call229();
         this.zenith_prevX = playerentity.getX();
         this.zenith_prevY = playerentity.getY();
         this.zenith_prevZ = playerentity.getZ();
      }
   }

   @Inject(method = "tick", at = @At("TAIL"))
   public void onTick(CallbackInfo var1) {
      try {
         PlayerEntity playerentity = (PlayerEntity)(Object)this;
         if (Cape.cape.isEnabled() && (playerentity == MinecraftClient.getInstance().player || ZenithClient.on23().MediaTrackInfo().UiAnimation(playerentity))) {
            this.zenith_simulate();
         }
      } catch (Exception exception) {
         System.out.println("Paster dayn v2");
         exception.printStackTrace();
      }
   }
}
