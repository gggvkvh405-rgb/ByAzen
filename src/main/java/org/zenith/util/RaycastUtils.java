package org.zenith.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.core.GameService;
import org.zenith.module.combat.Aura;
import org.zenith.rotation.Rotation;

public final class RaycastUtils implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static BlockHitResult on23(double var0, Rotation var2, boolean var3) {
      return on23(Objects.requireNonNull(minecraftClient3.player).getCameraPosVec(1.0F), var0, var2, var3);
   }

   public static BlockHitResult on23(Vec3d var0, double var1, Rotation var3, boolean var4) {
      Entity entity = minecraftClient3.getCameraEntity();
      if (entity == null) {
         return null;
      }

      Vec3d vec3d = var3.int202();
      Vec3d vec3d1 = var0.add(vec3d.x * var1, vec3d.y * var1, vec3d.z * var1);
      ClientWorld clientworld = minecraftClient3.world;
      if (clientworld == null) {
         return null;
      }

      FluidHandling fluidhandling = var4 ? FluidHandling.ANY : FluidHandling.NONE;
      RaycastContext raycastcontext = new RaycastContext(var0, vec3d1, ShapeType.OUTLINE, fluidhandling, entity);
      return clientworld.raycast(raycastcontext);
   }

   public static HitResult on23(double var0, Rotation var2, float var3, boolean var4) {
      Vec3d vec3d = minecraftClient3.getCameraEntity().getCameraPosVec(var3);
      Vec3d vec3d1 = var2.int202();
      Vec3d vec3d2 = vec3d.add(vec3d1.x * var0, vec3d1.y * var0, vec3d1.z * var0);
      return minecraftClient3.world
         .raycast(new RaycastContext(vec3d, vec3d2, ShapeType.OUTLINE, var4 ? FluidHandling.ANY : FluidHandling.NONE, minecraftClient3.getCameraEntity()));
   }

   public static BlockHitResult on23(Vec3d var0, Vec3d var1, ShapeType var2) {
      return on23(var0, var1, var2, minecraftClient3.player);
   }

   public static BlockHitResult on23(Vec3d var0, Vec3d var1, ShapeType var2, Entity var3) {
      return minecraftClient3.world.raycast(new RaycastContext(var0, var1, var2, FluidHandling.NONE, var3));
   }

   public static boolean on23(double var0, Rotation var2, LivingEntity var3) {
      return on23(var0, var2, var1 -> var1 == var3) != null;
   }

   public static EntityHitResult on23(double var0, Rotation var2, Predicate<Entity> var3) {
      Entity entity = minecraftClient3.getCameraEntity();
      if (entity == null) {
         return null;
      }

      Vec3d vec3d = entity.getCameraPosVec(1.0F);
      Vec3d vec3d1 = var2.int202();
      Vec3d vec3d2 = vec3d.add(vec3d1.x * var0, vec3d1.y * var0, vec3d1.z * var0);
      Box box = entity.getBoundingBox().stretch(vec3d1.multiply(var0)).expand(1.0, 1.0, 1.0);
      return ProjectileUtil.raycast(entity, vec3d, vec3d2, box, var1 -> !var1.isSpectator() && var3.test(var1), var0 * var0);
   }

   public static EntityHitResult on23(Rotation var0, Predicate<Entity> var1) {
      Entity entity = minecraftClient3.getCameraEntity();
      if (entity != null && minecraftClient3.world != null) {
         Vec3d vec3d = entity.getCameraPosVec(1.0F);
         Vec3d vec3d1 = var0.int202();
         Entity entity1 = null;
         Vec3d vec3d2 = null;
         double d0 = Double.MAX_VALUE;

         for (Entity entity2 : minecraftClient3.world.getEntities()) {
            if (entity2 != entity && !entity2.isSpectator() && var1.test(entity2)) {
               Box box = entity2.getBoundingBox().expand(entity2.getTargetingMargin());
               Vec3d vec3d3;
               if (box.contains(vec3d)) {
                  vec3d3 = vec3d;
               } else {
                  double d1 = vec3d.distanceTo(box.getCenter()) + box.getLengthX() + box.getLengthY() + box.getLengthZ();
                  Optional<Vec3d> optional = box.raycast(vec3d, vec3d.add(vec3d1.multiply(d1)));
                  if (optional.isEmpty()) {
                     continue;
                  }

                  vec3d3 = optional.get();
               }

               double d2 = vec3d.squaredDistanceTo(vec3d3);
               if (d2 < d0) {
                  entity1 = entity2;
                  vec3d2 = vec3d3;
                  d0 = d2;
               }
            }
         }

         return entity1 == null ? null : new EntityHitResult(entity1, vec3d2);
      } else {
         return null;
      }
   }

   public static boolean on23(Rotation var0, Vec3d var1, Box var2, double var3, boolean var5) {
      double d0 = Math.max(minecraftClient3.player.getBlockInteractionRange(), var3);
      double d1 = MathHelper.square(d0);
      BlockHitResult blockhitresult = on23(var1, var0, d0, Aura.aura::on23);
      double d2 = blockhitresult.getPos().squaredDistanceTo(var1);
      if (var5 && blockhitresult.getType() != Type.MISS) {
         d1 = d2;
         d0 = Math.sqrt(d2);
      }

      Vec3d vec3d = var0.int202();
      Vec3d vec3d1 = var1.add(vec3d.x * d0, vec3d.y * d0, vec3d.z * d0);
      Vec3d vec3d2 = on23(var1, vec3d1, var2, d1);
      return vec3d2 != null && (vec3d2.squaredDistanceTo(var1) < d2 || !var5) && vec3d2.isInRange(var1, var3);
   }

   public static HitResult on23(Vec3d var0, Rotation var1, double var2) {
      Vec3d vec3d = var1.int202();
      Vec3d vec3d1 = var0.add(vec3d.x * var2, vec3d.y * var2, vec3d.z * var2);
      return minecraftClient3.world.raycast(new RaycastContext(var0, vec3d1, ShapeType.OUTLINE, FluidHandling.NONE, minecraftClient3.player));
   }

   public static Vec3d on23(Vec3d var0, Vec3d var1, Box var2, double var3) {
      Vec3d vec3d = null;
      Optional<Vec3d> optional = var2.raycast(var0, var1);
      if (var2.contains(var0)) {
         if (var3 >= 0.0) {
            vec3d = optional.orElse(var0);
         }
      } else if (optional.isPresent()) {
         Vec3d vec3d1 = optional.get();
         double d0 = var0.squaredDistanceTo(vec3d1);
         if (d0 < var3 || var3 == 0.0) {
            vec3d = vec3d1;
         }
      }

      return vec3d;
   }

   public static HitResult ensureTargetInRange(HitResult var0, Vec3d var1, double var2) {
      Vec3d vec3d = var0.getPos();
      if (!vec3d.isInRange(var1, var2)) {
         Vec3d vec3d1 = var0.getPos();
         Direction direction = Direction.getFacing(
            vec3d1.x - var1.x, vec3d1.y - var1.y, vec3d1.z - var1.z
         );
         return BlockHitResult.createMissed(vec3d1, direction, BlockPos.ofFloored(vec3d1));
      } else {
         return var0;
      }
   }

   public static boolean on23(Vec3d var0, double var1, Box var3) {
      Vec3d vec3d = Objects.requireNonNull(minecraftClient3.player).getEyePos();
      return var3.contains(vec3d) || var3.raycast(vec3d, vec3d.add(var0.multiply(var1))).isPresent();
   }

   public static BlockHitResult on23(Vec3d var0, Rotation var1, double var2, Predicate<BlockHitResult> var4) {
      Vec3d vec3d = var1.int202();
      Vec3d vec3d1 = var0.add(vec3d.x * var2, vec3d.y * var2, vec3d.z * var2);
      return on23(new RaycastContext(var0, vec3d1, ShapeType.OUTLINE, FluidHandling.NONE, minecraftClient3.player), var4);
   }

   public static BlockHitResult on23(RaycastContext var0, Predicate<BlockHitResult> var1) {
      return (BlockHitResult)BlockView.raycast(
         var0.getStart(),
         var0.getEnd(),
         var0,
         (var1xx, var2) -> {
            BlockState blockstate = minecraftClient3.world.getBlockState(var2);
            FluidState fluidstate = minecraftClient3.world.getFluidState(var2);
            Vec3d vec3d = var1xx.getStart();
            Vec3d vec3d1 = var1xx.getEnd();
            VoxelShape voxelshape = var1xx.getBlockShape(blockstate, minecraftClient3.world, var2);
            BlockHitResult blockhitresult = minecraftClient3.world.raycastBlock(vec3d, vec3d1, var2, voxelshape, blockstate);
            VoxelShape voxelshape1 = var1xx.getFluidShape(fluidstate, minecraftClient3.world, var2);
            BlockHitResult blockhitresult1 = voxelshape1.raycast(vec3d, vec3d1, var2);
            double d0 = blockhitresult == null ? Double.MAX_VALUE : var1xx.getStart().squaredDistanceTo(blockhitresult.getPos());
            double d1 = blockhitresult1 == null ? Double.MAX_VALUE : var1xx.getStart().squaredDistanceTo(blockhitresult1.getPos());
            if (!var1.test(d0 <= d1 ? blockhitresult : blockhitresult1)) {
               return null;
            } else {
               return d0 <= d1 ? blockhitresult : blockhitresult1;
            }
         },
         var0x -> {
            Vec3d vec3d = var0x.getStart().subtract(var0x.getEnd());
            return BlockHitResult.createMissed(
               var0x.getEnd(),
               Direction.getFacing(vec3d.x, vec3d.y, vec3d.z),
               BlockPos.ofFloored(var0x.getEnd())
            );
         }
      );
   }

   public static BlockHitResult on23(Vec3d var0, Vec3d var1, BlockPos var2, VoxelShape var3, BlockState var4) {
      BlockHitResult blockhitresult = var3.raycast(var0, var1, var2);
      if (blockhitresult != null) {
         BlockHitResult blockhitresult1 = var4.getRaycastShape(minecraftClient3.world, var2).raycast(var0, var1, var2);
         if (blockhitresult1 != null
            && blockhitresult1.getPos().subtract(var0).lengthSquared() < blockhitresult.getPos().subtract(var0).lengthSquared()) {
            return blockhitresult.withSide(blockhitresult1.getSide());
         }
      }

      return blockhitresult;
   }

   public RaycastUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
