package org.zenith.core;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zenith.rotation.Rotation;
import org.zenith.util.RaycastUtils;

public final class BlockFinder {
   public static final RegionBounds zClass005Var159 = new RegionBounds(43, 61, 73, 81, 38, 56);
   public static final RegionBounds zClass005Var1592 = new RegionBounds(43, 61, 70, 81, 74, 92);
   public static final Set<Block> set18 = Set.of(
      Blocks.COAL_ORE,
      Blocks.DEEPSLATE_COAL_ORE,
      Blocks.IRON_ORE,
      Blocks.DEEPSLATE_IRON_ORE,
      Blocks.COPPER_ORE,
      Blocks.DEEPSLATE_COPPER_ORE,
      Blocks.GOLD_ORE,
      Blocks.DEEPSLATE_GOLD_ORE,
      Blocks.REDSTONE_ORE,
      Blocks.DEEPSLATE_REDSTONE_ORE,
      Blocks.EMERALD_ORE,
      Blocks.DEEPSLATE_EMERALD_ORE,
      Blocks.LAPIS_ORE,
      Blocks.DEEPSLATE_LAPIS_ORE,
      Blocks.DIAMOND_ORE,
      Blocks.DEEPSLATE_DIAMOND_ORE,
      Blocks.NETHER_GOLD_ORE,
      Blocks.NETHER_QUARTZ_ORE,
      Blocks.ANCIENT_DEBRIS,
      Blocks.CLAY
   );

   public static RegionBounds BotPacketEvent(boolean var0) {
      return var0 ? zClass005Var1592 : zClass005Var159;
   }

   public static boolean ItemRegistry(Block var0) {
      return set18.contains(var0);
   }

   public static boolean on23(RegionBounds var0, BlockPos var1) {
      return var1.getX() >= var0.call019()
         && var1.getX() <= var0.call021()
         && var1.getY() >= var0.minY()
         && var1.getY() <= var0.call069()
         && var1.getZ() >= var0.call020()
         && var1.getZ() <= var0.call022();
   }

   public static boolean on23(RegionBounds var0, Vec3d var1) {
      return var1.x >= var0.call019()
         && var1.x <= var0.call021() + 1.0
         && var1.z >= var0.call020()
         && var1.z <= var0.call022() + 1.0;
   }

   public static boolean UiAnimation(RegionBounds var0, BlockPos var1) {
      return var1.getX() >= var0.call019()
         && var1.getX() <= var0.call021()
         && var1.getY() >= var0.minY() - 3
         && var1.getY() <= var0.call069() + 3
         && var1.getZ() >= var0.call020()
         && var1.getZ() <= var0.call022();
   }

   public static boolean on23(RegionBounds var0, World var1) {
      int i = var0.call019() - 4;
      int j = var0.call021() + 4;
      int k = var0.call020() - 4;
      int l = var0.call022() + 4;
      int i1 = (var0.call019() + var0.call021()) / 2;
      int j1 = (var0.call020() + var0.call022()) / 2;
      return var1.isChunkLoaded(new BlockPos(i, var0.minY(), k))
         && var1.isChunkLoaded(new BlockPos(i, var0.minY(), l))
         && var1.isChunkLoaded(new BlockPos(j, var0.minY(), k))
         && var1.isChunkLoaded(new BlockPos(j, var0.minY(), l))
         && var1.isChunkLoaded(new BlockPos(i1, var0.minY(), j1));
   }

   public static double on23(Vec3d var0, BlockPos var1) {
      double d0 = Math.max(var1.getX(), Math.min(var1.getX() + 1.0, var0.x));
      double d1 = Math.max(var1.getY(), Math.min(var1.getY() + 1.0, var0.y));
      double d2 = Math.max(var1.getZ(), Math.min(var1.getZ() + 1.0, var0.z));
      return var0.squaredDistanceTo(d0, d1, d2);
   }

   public static BlockHitResult on23(BlockPos var0, double var1, Vec3d var3) {
      Vec3d vec3d = var0.toCenterPos();
      double d0 = 0.49;
      Vec3d[] avec3d = new Vec3d[]{
         vec3d,
         vec3d.add(d0, 0.0, 0.0),
         vec3d.add(-d0, 0.0, 0.0),
         vec3d.add(0.0, d0, 0.0),
         vec3d.add(0.0, -d0, 0.0),
         vec3d.add(0.0, 0.0, d0),
         vec3d.add(0.0, 0.0, -d0)
      };

      for (Vec3d vec3d1 : avec3d) {
         BlockHitResult blockhitresult = RaycastUtils.on23(var3, var1, Rotation.ItemServiceBase(vec3d1, var3), false);
         if (blockhitresult != null && blockhitresult.getType() != Type.MISS && blockhitresult.getBlockPos().equals(var0)) {
            return blockhitresult;
         }
      }

      return null;
   }

   static boolean on23(BlockPos var0, Vec3d var1, Vec3d var2, double var3) {
      Vec3d vec3d = var0.toCenterPos().subtract(var1);
      double d0 = vec3d.x * var2.x + vec3d.z * var2.z;
      return d0 > 0.0 && d0 <= var3 + 0.75 && on23(var0, var1, var2) <= 1.2;
   }

   static boolean UiAnimation(BlockPos var0, Vec3d var1, Vec3d var2, double var3) {
      Vec3d vec3d = var0.toCenterPos().subtract(var1);
      double d0 = vec3d.x * var2.x + vec3d.z * var2.z;
      return d0 > -0.35 && d0 <= Math.min(var3 + 0.75, 4.25) && on23(var0, var1, var2) <= 2.25;
   }

   public static BlockPos on23(BlockPos var0, List<BlockPos> var1, Vec3d var2, Vec3d var3) {
      Vec3d vec3d = var0.toCenterPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x - var3.x, 0.0, vec3d.z - var3.z);
      double d0 = vec3d1.lengthSquared();
      if (d0 < 0.01) {
         return null;
      }

      double d1 = Math.sqrt(d0);
      Vec3d vec3d2 = vec3d1.multiply(1.0 / d1);
      return var1.stream()
         .filter(var1x -> !var1x.equals(var0))
         .filter(var4x -> UiAnimation(var4x, var3, vec3d2, d1))
         .min(Comparator.<BlockPos>comparingDouble(var1x -> on23(var2, var1x)).thenComparingDouble(var2x -> on23(var2x, var3, vec3d2)))
         .orElse(null);
   }

   public static BlockPos on23(BlockPos var0, List<BlockPos> var1, Vec3d var2, Vec3d var3, int var4) {
      if (var0.getY() != var4) {
         return null;
      }

      Vec3d vec3d = var0.toCenterPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x - var3.x, 0.0, vec3d.z - var3.z);
      double d0 = vec3d1.lengthSquared();
      if (d0 < 0.01) {
         return null;
      }

      double d1 = Math.sqrt(d0);
      Vec3d vec3d2 = vec3d1.multiply(1.0 / d1);
      return var1.stream()
         .filter(var1x -> var1x.getY() == var0.getY())
         .filter(var4x -> on23(var4x, var3, vec3d2, d1))
         .min(Comparator.<BlockPos>comparingDouble(var2x -> on23(var2x, var3, vec3d2)).thenComparingDouble(var1x -> on23(var2, var1x)))
         .orElse(null);
   }

   static double on23(BlockPos var0, Vec3d var1, Vec3d var2) {
      Vec3d vec3d = var0.toCenterPos().subtract(var1);
      double d0 = vec3d.x * var2.x + vec3d.z * var2.z;
      double d1 = var1.x + var2.x * d0;
      double d2 = var1.z + var2.z * d0;
      double d3 = var0.getX() + 0.5 - d1;
      double d4 = var0.getZ() + 0.5 - d2;
      return d3 * d3 + d4 * d4;
   }
}
