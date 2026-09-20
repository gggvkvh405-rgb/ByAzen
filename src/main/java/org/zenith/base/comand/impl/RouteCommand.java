package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.rotation.Rotation;

public final class RouteCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int STUCK_TICKS_BEFORE_REPATH = 40;
   public static final float MAX_YAW_STEP = 35.0F;
   public static final float MOVE_YAW_TOLERANCE = 55.0F;
   public Pathfinder.Path route;
   public BlockPos destination;
   public BlockPos nextBlock;
   public Vec3d lastPosition;
   public int stuckTicks;

   public RouteCommand() {
      super("route");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Использование: .route <x> <y> <z> или .route stop");
         return 1;
      });
      var1.then(literal("stop").executes(var1x -> {
         this.stop();
         StyledTextBuilder.RefreshCacheEvent("Маршрут остановлен");
         return 1;
      }));
      var1.then(
         arg("x", IntegerArgumentType.integer()).then(arg("y", IntegerArgumentType.integer()).then(arg("z", IntegerArgumentType.integer()).executes(var1x -> {
            int i = IntegerArgumentType.getInteger(var1x, "x");
            int j = IntegerArgumentType.getInteger(var1x, "y");
            int k = IntegerArgumentType.getInteger(var1x, "z");
            this.start(new BlockPos(i, j, k));
            return 1;
         })))
      );
   }

   public void start(BlockPos var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         ClientPlayerEntity clientplayerentity = minecraftClient3.player;
         Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = this.findRoute(clientplayerentity, var1);
         if (l1liiliiiil1i_liil11l111liil1ll == null) {
            this.stop();
            StyledTextBuilder.AimPolicyRotationStrategy("Не удалось построить путь до " + var1.getX() + " " + var1.getY() + " " + var1.getZ());
         } else {
            this.route = l1liiliiiil1i_liil11l111liil1ll;
            this.destination = var1.toImmutable();
            this.nextBlock = null;
            this.lastPosition = clientplayerentity.getEntityPos();
            this.stuckTicks = 0;
            StyledTextBuilder.RefreshCacheEvent(
               "Иду к " + var1.getX() + " " + var1.getY() + " " + var1.getZ() + " (" + this.route.var04().size() + " блоков)"
            );
         }
      } else {
         StyledTextBuilder.AimPolicyRotationStrategy("Сначала зайди в мир");
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (this.route != null) {
         ClientPlayerEntity clientplayerentity = minecraftClient3.player;
         if (clientplayerentity != null && minecraftClient3.world != null && this.destination != null) {
            this.nextBlock = this.route.CloudRouter(clientplayerentity.getEntityPos());
            if (this.nextBlock == null) {
               this.stop();
               StyledTextBuilder.RefreshCacheEvent("Маршрут завершён");
            } else {
               this.rotateTo(clientplayerentity, this.nextBlock);
               this.updateStuckState(clientplayerentity);
            }
         } else {
            this.stop();
         }
      }
   }

   @EventTarget
   public void onMoveInput(MovementInputEvent var1) {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      if (this.route != null && this.nextBlock != null && clientplayerentity != null) {
         var1.NoSlow();
         float f = this.yawTo(clientplayerentity, this.nextBlock);
         float f1 = Math.abs(MathHelper.wrapDegrees(f - clientplayerentity.getYaw()));
         if (f1 <= 55.0F) {
            var1.ItemSpec(true);
         }

         int i = MathHelper.floor(clientplayerentity.getY() + 0.01);
         boolean flag = this.nextBlock.getY() > i || clientplayerentity.horizontalCollision;
         var1.EnchantItemSpec(flag);
      }
   }

   public void rotateTo(ClientPlayerEntity var1, BlockPos var2) {
      float f = this.yawTo(var1, var2);
      float f1 = MathHelper.wrapDegrees(f - var1.getYaw());
      var1.setYaw(var1.getYaw() + MathHelper.clamp(f1, -35.0F, 35.0F));
   }

   public float yawTo(ClientPlayerEntity var1, BlockPos var2) {
      Vec3d vec3d = Pathfinder.EventInteractBlock(var2);
      Vec3d vec3d1 = new Vec3d(vec3d.x, var1.getEyeY(), vec3d.z);
      return Rotation.ItemServiceBase(vec3d1, var1.getEyePos()).GrimGlide();
   }

   public void updateStuckState(ClientPlayerEntity var1) {
      Vec3d vec3d = var1.getEntityPos();
      if (this.lastPosition != null && vec3d.squaredDistanceTo(this.lastPosition) < 4.0E-4) {
         this.stuckTicks++;
      } else {
         this.stuckTicks = 0;
      }

      this.lastPosition = vec3d;
      if (this.stuckTicks >= 40 && var1.isOnGround()) {
         Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = this.findRoute(var1, this.destination);
         if (l1liiliiiil1i_liil11l111liil1ll == null) {
            this.stop();
            StyledTextBuilder.AimPolicyRotationStrategy("Не удалось перестроить маршрут");
         } else {
            this.route = l1liiliiiil1i_liil11l111liil1ll;
            this.nextBlock = null;
            this.stuckTicks = 0;
            StyledTextBuilder.RotationLegitStrategy("Маршрут перестроен");
         }
      }
   }

   public Pathfinder.Path findRoute(ClientPlayerEntity var1, BlockPos var2) {
      return Pathfinder.ItemRegistry(var1.getBlockPos(), var2).orElse(null);
   }

   public void stop() {
      this.route = null;
      this.destination = null;
      this.nextBlock = null;
      this.lastPosition = null;
      this.stuckTicks = 0;
   }
}
