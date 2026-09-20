package org.zenith.module.movement;

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
import java.util.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import org.zenith.ZenithClient;
import org.zenith.core.MovementController;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "ElytraTarget", description = "", category = Category.MOVEMENT)
public final class ElytraTarget extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ElytraTarget elytraTarget = new ElytraTarget();
   public PlayerEntity playerEntity;
   public final CooldownTimer zClass06727 = new CooldownTimer();
   public final BooleanSetting autoFWSetting = new BooleanSetting("module.elytraTarget.autoFWSetting", "module.elytraTarget.autoFWSetting.desc", true);
   public final NumberSetting predictSetting2 = new NumberSetting(
      "module.elytraTarget.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.elytraTarget.predictSetting.desc", "t"
   );
   public final BooleanSetting notScipBox = new BooleanSetting("module.elytraTarget.notScipBox", "module.elytraTarget.notScipBox.desc", true);
   public Box box2 = null;

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   public void ColorAnimator(EventHookWorldRender var1) {
      if (this.box2 != null && this.predictSetting2.getCurrent() != 0.0F) {
         WorldRender.on23(this.box2, ZenithClient.on23().TextScanner().getClientColor(0).call001(), 1.0F);
      }
   }

   @EventTarget
   public void ItemRegistry(RotationUpdateStartEvent var1) {
      this.playerEntity = this.int152();
      if (this.playerEntity == null && Aura.aura.zClass054() instanceof PlayerEntity playerentity) {
         this.playerEntity = playerentity;
      }

      if (this.playerEntity != null && minecraftClient3.player.isGliding()) {
         this.box2 = this.NbtItemSpec(this.playerEntity);
         Rotation ililiiili1ll1li11 = RotationMath.Event08(
            this.box2
               .getCenter()
               .subtract(
                  minecraftClient3.player
                     .getEntityPos()
                     .add(minecraftClient3.player.getVelocity())
                     .add(0.0, minecraftClient3.player.getEyeHeight(minecraftClient3.player.getPose()), 0.0)
               )
         );
         val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(Aura.aura.zClass088(), ililiiili1ll1li11), Aura.aura.zClass088()), 10, this);
         if (this.autoFWSetting.isEnabled() && !ElytraMotion.elytraMotion.call126()) {
            double d0 = Math.hypot(
                  minecraftClient3.player.getY() - minecraftClient3.player.lastY,
                  Math.hypot(
                     minecraftClient3.player.getX() - minecraftClient3.player.lastX,
                     minecraftClient3.player.getZ() - minecraftClient3.player.lastZ
                  )
               )
               * 20.0;
            if (this.zClass06727.EventModifyMouseRotationInput(400L)) {
               ScreenUtils.on23(Items.FIREWORK_ROCKET, Hand.OFF_HAND);
               this.zClass06727.reset();
            }
         }
      } else {
         this.box2 = null;
      }
   }

   @EventTarget
   public void UiAnimation(PacketEvent var1) {
      if (var1.ItemScroller() instanceof PlayerInteractEntityC2SPacket) {
      }
   }

   public Box NbtItemSpec(LivingEntity var1) {
      return var1 instanceof PlayerEntity playerentity
         ? MovementController.ColorAnimator(playerentity, (int)this.predictSetting2.getCurrent()).box9
         : var1.getBoundingBox();
   }

   public PlayerEntity int152() {
      return minecraftClient3.world
         .getPlayers()
         .stream()
         .filter(
            var0 -> var0 != minecraftClient3.player
               && minecraftClient3.player.canSee(var0)
               && var0.isGliding()
               && !ZenithClient.on23().MediaTrackInfo().UiAnimation(var0)
         )
         .min(Comparator.comparingDouble(var0 -> minecraftClient3.player.distanceTo(var0) - (var0 == Aura.aura.var11813() ? 100 : 0)))
         .orElse(null);
   }

   public boolean EnchantItemSpec(LivingEntity var1) {
      return false;
   }

   public BooleanSetting call085() {
      return this.notScipBox;
   }

   public Box call084() {
      return this.box2;
   }
}
