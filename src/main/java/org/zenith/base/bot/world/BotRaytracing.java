package org.zenith.base.bot.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.rotation.Rotation;

public final class BotRaytracing {
   public static BlockHitResult rayTracePos(BotWorld var0, BotPlayer var1, Vec3d var2, Rotation var3, double var4, Predicate<BlockHitResult> var6) {
      Vec3d vec3d = var3.int202();
      Vec3d vec3d1 = var2.add(vec3d.x * var4, vec3d.y * var4, vec3d.z * var4);
      RaycastContext raycastcontext = new RaycastContext(var2, vec3d1, ShapeType.OUTLINE, FluidHandling.NONE, var1);
      return (BlockHitResult)BlockView.raycast(
         raycastcontext.getStart(),
         raycastcontext.getEnd(),
         raycastcontext,
         (var2x, var3x) -> {
            BlockState blockstate = var0.getBlockState(var3x);
            FluidState fluidstate = var0.getFluidState(var3x);
            Vec3d vec3d2 = var2x.getStart();
            Vec3d vec3d3 = var2x.getEnd();
            VoxelShape voxelshape = var2x.getBlockShape(blockstate, var0, var3x);
            BlockHitResult blockhitresult = var0.raycastBlock(vec3d2, vec3d3, var3x, voxelshape, blockstate);
            VoxelShape voxelshape1 = var2x.getFluidShape(fluidstate, var0, var3x);
            BlockHitResult blockhitresult1 = voxelshape1.raycast(vec3d2, vec3d3, var3x);
            double d0 = blockhitresult == null ? Double.MAX_VALUE : vec3d2.squaredDistanceTo(blockhitresult.getPos());
            double d1 = blockhitresult1 == null ? Double.MAX_VALUE : vec3d2.squaredDistanceTo(blockhitresult1.getPos());
            BlockHitResult blockhitresult2 = d0 <= d1 ? blockhitresult : blockhitresult1;
            return !var6.test(blockhitresult2) ? null : blockhitresult2;
         },
         var0x -> {
            Vec3d vec3d2 = var0x.getStart().subtract(var0x.getEnd());
            return BlockHitResult.createMissed(
               var0x.getEnd(),
               Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z),
               BlockPos.ofFloored(var0x.getEnd())
            );
         }
      );
   }
}
