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
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.MovementController;
import org.zenith.core.PacketDispatcher;
import org.zenith.event.MovementSpeedEvent;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventMotion;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PacketEvent;
import org.zenith.event.SprintStateEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.MovementUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TimerSpeed;

@ModuleInfo(name = "Speed", description = "speed", category = Category.MOVEMENT)
public final class Speed extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Speed speed11 = new Speed();
   public static final int int302 = 16;
   public final ModeSetting mode13 = new ModeSetting(
      "module.speed.mode",
      "module.speed.mode.desc",
      "module.speed.mode.holyWorld",
      "module.speed.mode.grimOld",
      "module.speed.mode.reallyWorld",
      "module.speed.mode.grimElytra"
   );
   public final NumberSetting collisionRadius = new NumberSetting(
      "module.speed.collisionRadius", 0.3F, 0.0F, 1.0F, 0.01F, "module.speed.collisionRadius.desc", "b", this::call130
   );
   public final NumberSetting speed6 = new NumberSetting("module.speed.speed", 0.1F, 0.0F, 0.5F, 0.01F, "module.speed.speed.desc", "x", this::call130);
   public final BooleanSetting onlyAura = new BooleanSetting("module.speed.onlyAura", "module.speed.onlyAura.desc", true, this::call130);
   public int ticks = 0;
   public int int303 = 0;
   public double double100;
   boolean val207 = false;
   int val103 = 0;

   @Override
   public void onEnable() {
      this.val103 = 0;
      this.val207 = false;
      this.ticks = 0;
      this.int303 = 0;
      TimerSpeed.BotWorldJoinEvent(1.0F);
      if (this.call016()
         && minecraftClient3.player != null
         && !minecraftClient3.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         this.call131();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      TimerSpeed.BotWorldJoinEvent(1.0F);
      if (this.call016()
         && minecraftClient3.player != null
         && minecraftClient3.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         this.call131();
      }

      super.onDisable();
   }

   @EventTarget
   public void UiAnimation(SprintStateEvent var1) {
      if (this.val207) {
         var1.PotionItemBuilder(false);
         this.val207 = false;
      }
   }

   @EventTarget(4)
   public void ItemSpec(EventTick var1) {
      if (this.val103 > 0) {
         this.val103--;
      }

      if (this.call130() && (Aura.aura.isEnabled() || !this.onlyAura.isEnabled())) {
         this.PotionItemBuilder(null);
      }
   }

   @EventTarget
   public void on23(MovementSpeedEvent var1) {
      if (this.call030() && minecraftClient3.player != null) {
         TimerSpeed.BotWorldJoinEvent(1.7F);
         if (this.ticks > 3) {
            double d0 = 0.03;
            if (this.ticks % 2 == 0) {
               minecraftClient3.player.addVelocity(0.0, 0.03F, 0.0);
               d0 = minecraftClient3.player.isOnGround() ? 0.085 : 0.03;
            }

            if (this.call101()) {
               double d1 = MovementUtils.NbtItemSpec(
                  minecraftClient3.player.getYaw(),
                  minecraftClient3.player.input.getMovementInput().y,
                  minecraftClient3.player.input.getMovementInput().x
               );
               minecraftClient3.player.addVelocity(-Math.sin(d1) * d0, 0.0, Math.cos(d1) * d0);
            }
         }

         this.ticks++;
      }
   }

   @EventTarget
   public void onMoveInput(MovementInputEvent var1) {
      if (this.call030() && minecraftClient3.player != null) {
         if (minecraftClient3.player.verticalCollision) {
            this.int303++;
         } else {
            this.int303 = 0;
         }

         if (this.int303 >= 1) {
            minecraftClient3.player.jump();
         }
      }
   }

   @EventTarget
   public void on23(EventMotion var1) {
      if (this.call030() && minecraftClient3.player != null && minecraftClient3.getNetworkHandler() != null && this.ticks % 2 == 0) {
         TimerSpeed.BotWorldJoinEvent(0.3F);
         PacketDispatcher.SimpleItemBuilder(new ClientCommandC2SPacket(minecraftClient3.player, Mode.START_FALL_FLYING));
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (this.call030() && var1.ItemScroller() instanceof PlayerPositionLookS2CPacket) {
         if (this.ticks % 2 == 1) {
            this.ticks++;
         }

         TimerSpeed.BotWorldJoinEvent(1.0F);
      }
   }

   public void PotionItemBuilder(MovementInputEvent var1) {
      boolean flag = minecraftClient3.player.isSprinting();
      if (flag) {
         minecraftClient3.player.setSprinting(false);
      }

      MovementController il11i11i111i1i1l1il = MovementController.TargetAcquireEvent(1);
      if (flag) {
         minecraftClient3.player.setSprinting(true);
      }

      Box box = il11i11i111i1i1l1il.box9
         .expand(this.collisionRadius.getCurrent(), this.collisionRadius.getCurrent(), this.collisionRadius.getCurrent());
      Entity object = null;
      if (!this.onlyAura.isEnabled()) {
         if (Aura.aura.zClass054() == null) {
            for (Entity entity : minecraftClient3.world.getEntities()) {
               if (entity != minecraftClient3.player
                  && (entity instanceof LivingEntity || entity instanceof BoatEntity)
                  && box.intersects(entity.getBoundingBox())) {
                  object = entity;
                  break;
               }
            }
         }
      } else {
         if (Aura.aura.zClass054() == null) {
            return;
         }

         if (box.intersects(Aura.aura.zClass054().getBoundingBox())) {
            object = Aura.aura.zClass054();
         }
      }

      if (object instanceof PlayerEntity) {
         Vec3d vec3d = il11i11i111i1i1l1il.TriggerBot;
         Vec3d vec3d1 = (Backtrack.reachV3.isEnabled() && Backtrack.reachV3.float272() != null
               ? object.dimensions.getBoxAt(Backtrack.reachV3.float272())
               : (object instanceof PlayerEntity ? MovementController.ColorAnimator((PlayerEntity)object, 2).box9 : object.getBoundingBox()))
            .getCenter();
         double d0 = vec3d1.x - vec3d.x;
         double d1 = vec3d1.z - vec3d.z;
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         double d3 = this.speed6.getCurrent();
         double d4 = 0.0;
         double d5 = Math.max(0.0, d2 - d4);
         double d6 = Math.min(d3, d5);
         double[] adouble = new double[]{d0 * d6, d1 * d6};
         minecraftClient3.player.addVelocity(adouble[0], 0.0, adouble[1]);
         if (this.mode13.is(0)) {
            if (minecraftClient3.player.lastSprinting) {
               minecraftClient3.player.addVelocity(-adouble[0], 0.0, -adouble[1]);
            }

            minecraftClient3.player.setSprinting(false);
            minecraftClient3.options.sprintKey.setPressed(false);
            this.val207 = true;
         }

         if (minecraftClient3.player.getBoundingBox().intersects(object.getBoundingBox())) {
            this.val103 = 3;
            return;
         }
      } else {
         this.val103 = 0;
      }
   }

   public boolean call030() {
      return this.mode13.is(2);
   }

   @EventTarget
   public void SimpleItemBuilder(EventTick var1) {
      if (this.call016() && minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.call101()) {
            minecraftClient3.player.setSprinting(true);
         }

         if (!minecraftClient3.player.isOnGround()
            && !minecraftClient3.player.isGliding()
            && minecraftClient3.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            PacketDispatcher.SimpleItemBuilder(new ClientCommandC2SPacket(minecraftClient3.player, Mode.START_FALL_FLYING));
            minecraftClient3.player.startGliding();
         }

         if (minecraftClient3.player.isOnGround()) {
            this.ticks = 16;
            this.double100 = minecraftClient3.player.getY();
         }
      }
   }

   @EventTarget
   public void SimpleItemBuilder(RotationUpdateStartEvent var1) {
      if (this.call016()
         && minecraftClient3.player != null
         && minecraftClient3.world != null
         && this.call101()
         && !minecraftClient3.player.isOnGround()
         && minecraftClient3.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
         float f = (float)Math.toDegrees(
            MovementUtils.NbtItemSpec(
               minecraftClient3.player.getYaw(), minecraftClient3.player.input.getMovementInput().y, minecraftClient3.player.input.getMovementInput().x
            )
         );
         if (this.ticks > 0) {
            double d0 = Math.abs(minecraftClient3.player.getY() - this.double100);
            double d1 = Math.max(0.0, 1.0 - d0);
            float f1 = 75.0F;
            Rotation ililiiili1ll1li11 = new Rotation(f, f1);
            val003.CloudRouter().on23(new RotationTask(ililiiili1ll1li11, () -> ililiiili1ll1li11, val001.HudPreviewItem()), 2, this);
         }
      }
   }

   public boolean call016() {
      return this.mode13.is(3);
   }

   public boolean call130() {
      return this.mode13.is(0) || this.mode13.is(1);
   }

   public void call131() {
      if (minecraftClient3.player != null) {
         Slot slot;
         if (minecraftClient3.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            slot = ScreenUtils.on23(
               List.of(
                  Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE
               ),
               Comparator.comparingInt(var0 -> var0.id)
            );
         } else {
            slot = ScreenUtils.on23(
               minecraftClient3.player.playerScreenHandler, Items.ELYTRA, Comparator.comparingInt(var0 -> var0.id), var0 -> true
            );
         }

         if (slot != null) {
            ScreenUtils.on23(slot, 6, true, false);
         }
      }
   }

   public boolean call101() {
      return minecraftClient3.player.input.getMovementInput().y != 0.0F || minecraftClient3.player.input.getMovementInput().x != 0.0F;
   }

   public int call170() {
      return this.val103;
   }
}
