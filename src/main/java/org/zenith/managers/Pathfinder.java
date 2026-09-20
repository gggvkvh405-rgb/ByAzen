package org.zenith.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public final class Pathfinder {
   public static final double double145 = Math.sqrt(2.0);
   public static final int[][] val520 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   public static final int[][] val521 = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static Optional<Pathfinder.Path> ItemRegistry(BlockPos var0, BlockPos var1) {
      return minecraftClient3.world == null ? Optional.empty() : on23(minecraftClient3.world, var0, var1, Pathfinder.PathOptions.zClass073Var1652);
   }

   public static Optional<Pathfinder.Path> on23(BlockPos var0, BlockPos var1, Pathfinder.PathOptions var2) {
      return minecraftClient3.world == null ? Optional.empty() : on23(minecraftClient3.world, var0, var1, var2);
   }

   public static Optional<Pathfinder.Path> on23(World var0, BlockPos var1, BlockPos var2, Pathfinder.PathOptions var3) {
      if (var0 != null && var1 != null && var2 != null && var3 != null) {
         BlockPos blockpos = var1.toImmutable();
         BlockPos blockpos1 = var2.toImmutable();
         boolean flag = var0.isChunkLoaded(blockpos1.getX() >> 4, blockpos1.getZ() >> 4);
         if (!UiAnimation(var0, blockpos)
            && !UiAnimation(var0, blockpos1)
            && var0.isChunkLoaded(blockpos.getX() >> 4, blockpos.getZ() >> 4)
            && (var3.double162() != Pathfinder.NodeType.val197 || flag)) {
            BlockPos blockpos2 = Easing(var0, blockpos, var3.double160());
            BlockPos blockpos3 = var3.double159() == 0 && flag ? Easing(var0, blockpos1, var3.double160()) : blockpos1;
            if (blockpos3 == null && var3.double162() == Pathfinder.NodeType.val317) {
               blockpos3 = blockpos1;
            }

            if (blockpos2 == null || blockpos3 == null) {
               return Optional.empty();
            }

            if (on23(blockpos2, blockpos3, var3.double159())) {
               return Optional.of(new Pathfinder.Path(List.of(blockpos2)));
            }

            HashMap hashmap = new HashMap();
            PriorityQueue priorityqueue = new PriorityQueue<>(
               Comparator.comparingDouble(Pathfinder.TimedNode::double163).thenComparingLong(Pathfinder.TimedNode::double164)
            );
            Pathfinder.SearchNode l1liiliiiil1i_ii1il11l111ii11iilxxx = new Pathfinder.SearchNode(
               blockpos2, null, 0.0, UiAnimation(blockpos2, blockpos3, var3.double159())
            );
            Pathfinder.SearchNode l1liiliiiil1i_ii1il11l111ii11iilx = l1liiliiiil1i_ii1il11l111ii11iilxxx;
            hashmap.put(blockpos2, l1liiliiiil1i_ii1il11l111ii11iilxxx);
            long i = 0L;
            priorityqueue.add(new Pathfinder.TimedNode(l1liiliiiil1i_ii1il11l111ii11iilxxx, l1liiliiiil1i_ii1il11l111ii11iilxxx.double50, i++));
            int j = 0;

            while (!priorityqueue.isEmpty() && j < var3.float139()) {
               Pathfinder.TimedNode l1liiliiiil1i_Var160 = (Pathfinder.TimedNode)priorityqueue.poll();
               Pathfinder.SearchNode l1liiliiiil1i_ii1il11l111ii11iilxx = l1liiliiiil1i_Var160.int465();
               if (!l1liiliiiil1i_ii1il11l111ii11iilxx.closed && l1liiliiiil1i_Var160.double163() == l1liiliiiil1i_ii1il11l111ii11iilxx.double50) {
                  l1liiliiiil1i_ii1il11l111ii11iilxx.closed = true;
                  j++;
                  if (on23(l1liiliiiil1i_ii1il11l111ii11iilxx, l1liiliiiil1i_ii1il11l111ii11iilx)) {
                     l1liiliiiil1i_ii1il11l111ii11iilx = l1liiliiiil1i_ii1il11l111ii11iilxx;
                  }

                  if (on23(l1liiliiiil1i_ii1il11l111ii11iilxx.blockPos26, blockpos3, var3.double159())) {
                     return Optional.of(new Pathfinder.Path(on23(l1liiliiiil1i_ii1il11l111ii11iilxx)));
                  }

                  for (Pathfinder.NodeDistance l1liiliiiil1i_l1iil11li : UiAnimation(var0, l1liiliiiil1i_ii1il11l111ii11iilxx.blockPos26, blockpos2, var3)) {
                     l1liiliiiil1i_ii1il11l111ii11iilxxx = (Pathfinder.SearchNode)hashmap.get(l1liiliiiil1i_l1iil11li.zClass095Var165());
                     double d0 = l1liiliiiil1i_ii1il11l111ii11iilxx.double49 + l1liiliiiil1i_l1iil11li.call031();
                     if (l1liiliiiil1i_ii1il11l111ii11iilxxx == null || !(d0 >= l1liiliiiil1i_ii1il11l111ii11iilxxx.double49)) {
                        if (l1liiliiiil1i_ii1il11l111ii11iilxxx == null) {
                           l1liiliiiil1i_ii1il11l111ii11iilxxx = new Pathfinder.SearchNode(
                              l1liiliiiil1i_l1iil11li.zClass095Var165(),
                              l1liiliiiil1i_ii1il11l111ii11iilxx,
                              d0,
                              UiAnimation(l1liiliiiil1i_l1iil11li.zClass095Var165(), blockpos3, var3.double159())
                           );
                           hashmap.put(l1liiliiiil1i_l1iil11li.zClass095Var165(), l1liiliiiil1i_ii1il11l111ii11iilxxx);
                        } else {
                           l1liiliiiil1i_ii1il11l111ii11iilxxx.zClass073Var159 = l1liiliiiil1i_ii1il11l111ii11iilxx;
                           l1liiliiiil1i_ii1il11l111ii11iilxxx.double49 = d0;
                           l1liiliiiil1i_ii1il11l111ii11iilxxx.double50 = d0 + l1liiliiiil1i_ii1il11l111ii11iilxxx.double48;
                           l1liiliiiil1i_ii1il11l111ii11iilxxx.closed = false;
                        }

                        if (on23(l1liiliiiil1i_ii1il11l111ii11iilxxx, l1liiliiiil1i_ii1il11l111ii11iilx)) {
                           l1liiliiiil1i_ii1il11l111ii11iilx = l1liiliiiil1i_ii1il11l111ii11iilxxx;
                        }

                        priorityqueue.add(new Pathfinder.TimedNode(l1liiliiiil1i_ii1il11l111ii11iilxxx, l1liiliiiil1i_ii1il11l111ii11iilxxx.double50, i++));
                     }
                  }
               }
            }

            return var3.double162() == Pathfinder.NodeType.val317 ? Optional.of(new Pathfinder.Path(on23(l1liiliiiil1i_ii1il11l111ii11iilx))) : Optional.empty();
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static BlockPos ItemSpec(BlockPos var0, BlockPos var1) {
      return ItemRegistry(var0, var1).map(var1x -> var1x.EventTriggerKeyEvent(var0)).orElse(null);
   }

   public static Vec3d EventInteractBlock(BlockPos var0) {
      return new Vec3d(var0.getX() + 0.5, var0.getY() + 0.15, var0.getZ() + 0.5);
   }

   public static boolean on23(World var0, BlockPos var1, boolean var2) {
      return var0 != null
         && var1 != null
         && !UiAnimation(var0, var1)
         && var0.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4)
         && UiAnimation(var0, var1, var2);
   }

   public static List<Pathfinder.NodeDistance> UiAnimation(World var0, BlockPos var1, BlockPos var2, Pathfinder.PathOptions var3) {
      List<Pathfinder.NodeDistance> arraylist = new ArrayList<>(var3.double161() ? 8 : 4);
      on23(var0, var1, var2, var3, val520, arraylist);
      if (var3.double161()) {
         on23(var0, var1, var2, var3, val521, arraylist);
      }

      return arraylist;
   }

   public static void on23(World var0, BlockPos var1, BlockPos var2, Pathfinder.PathOptions var3, int[][] var4, List<Pathfinder.NodeDistance> var5) {
      for (int[] aint : var4) {
         int i = aint[0];
         int j = aint[1];
         boolean flag = i != 0 && j != 0;
         double d0 = flag ? double145 : 1.0;
         if (!flag || on23(var0, var1, i, j, var3.double160())) {
            BlockPos blockpos = var1.add(i, 0, j);
            if (Easing(var0, blockpos, var2, var3) && UiAnimation(var0, blockpos, var3.double160())) {
               var5.add(new Pathfinder.NodeDistance(blockpos.toImmutable(), d0));
            } else {
               BlockPos blockpos1 = var1.add(i, 1, j);
               if (Easing(var0, blockpos1, var2, var3)
                  && ColorAnimator(var0, var1.up(2), var3.double160())
                  && UiAnimation(var0, blockpos1, var3.double160())) {
                  var5.add(new Pathfinder.NodeDistance(blockpos1.toImmutable(), d0 + 0.65));
               } else if (ColorAnimator(var0, blockpos, var3.double160()) && ColorAnimator(var0, blockpos.up(), var3.double160())) {
                  for (int k = 1; k <= var3.float141(); k++) {
                     BlockPos blockpos2 = var1.add(i, -k, j);
                     if (!Easing(var0, blockpos2, var2, var3)) {
                        break;
                     }

                     if (UiAnimation(var0, blockpos2, var3.double160())) {
                        var5.add(new Pathfinder.NodeDistance(blockpos2.toImmutable(), d0 + 0.2 + k * 0.15));
                        break;
                     }

                     if (!ColorAnimator(var0, blockpos2, var3.double160())) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean Easing(World var0, BlockPos var1, BlockPos var2, Pathfinder.PathOptions var3) {
      if (!UiAnimation(var0, var1) && var0.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4)) {
         int i = var1.getX() - var2.getX();
         int j = var1.getZ() - var2.getZ();
         return Math.max(Math.abs(i), Math.abs(j)) <= var3.float140();
      } else {
         return false;
      }
   }

   public static boolean on23(World var0, BlockPos var1, int var2, int var3, boolean var4) {
      BlockPos blockpos = var1.add(var2, 0, 0);
      BlockPos blockpos1 = var1.add(0, 0, var3);
      return ColorAnimator(var0, blockpos, var4)
         && ColorAnimator(var0, blockpos.up(), var4)
         && ColorAnimator(var0, blockpos1, var4)
         && ColorAnimator(var0, blockpos1.up(), var4);
   }

   public static boolean UiAnimation(World var0, BlockPos var1, boolean var2) {
      if (!ColorAnimator(var0, var1, var2) || !ColorAnimator(var0, var1.up(), var2)) {
         return false;
      }

      if (var2 && !var0.getFluidState(var1).isEmpty()) {
         return true;
      }

      BlockPos blockpos = var1.down();
      BlockState blockstate = var0.getBlockState(blockpos);
      if (ItemRegistry(blockstate)) {
         return false;
      }

      VoxelShape voxelshape = blockstate.getCollisionShape(var0, blockpos);
      if (voxelshape.isEmpty()) {
         return false;
      }

      double d0 = voxelshape.getMax(Axis.Y);
      return d0 >= 0.499 && d0 <= 1.001;
   }

   public static BlockPos Easing(World var0, BlockPos var1, boolean var2) {
      if (UiAnimation(var0, var1, var2)) {
         return var1.toImmutable();
      }

      BlockPos blockpos = var1.up();
      return !UiAnimation(var0, blockpos) && UiAnimation(var0, blockpos, var2) ? blockpos.toImmutable() : null;
   }

   public static boolean ColorAnimator(World var0, BlockPos var1, boolean var2) {
      BlockState blockstate = var0.getBlockState(var1);
      if (!ItemRegistry(blockstate) && blockstate.getCollisionShape(var0, var1).isEmpty()) {
         FluidState fluidstate = blockstate.getFluidState();
         return fluidstate.isEmpty() || var2;
      } else {
         return false;
      }
   }

   public static boolean ItemRegistry(BlockState var0) {
      return var0.isOf(Blocks.LAVA)
         || var0.isOf(Blocks.FIRE)
         || var0.isOf(Blocks.SOUL_FIRE)
         || var0.isOf(Blocks.CACTUS)
         || var0.isOf(Blocks.MAGMA_BLOCK)
         || var0.isOf(Blocks.CAMPFIRE)
         || var0.isOf(Blocks.SOUL_CAMPFIRE)
         || var0.isOf(Blocks.SWEET_BERRY_BUSH)
         || var0.isOf(Blocks.COBWEB)
         || var0.isOf(Blocks.POWDER_SNOW);
   }

   public static boolean UiAnimation(World var0, BlockPos var1) {
      return var1.getY() < var0.getBottomY() || var1.getY() > var0.getTopYInclusive() - 1;
   }

   public static boolean on23(BlockPos var0, BlockPos var1, int var2) {
      return var0.getX() == var1.getX()
         && var0.getZ() == var1.getZ()
         && Math.abs(var0.getY() - var1.getY()) <= var2;
   }

   public static double UiAnimation(BlockPos var0, BlockPos var1, int var2) {
      int i = Math.abs(var0.getX() - var1.getX());
      int j = Math.abs(var0.getZ() - var1.getZ());
      int k = Math.min(i, j);
      int l = Math.max(i, j) - k;
      int i1 = var1.getY() - var0.getY();
      int j1 = Math.max(0, Math.abs(i1) - var2);
      double d0 = i1 > 0 ? j1 * 0.65 : j1 * 0.15;
      return k * double145 + l + d0;
   }

   public static boolean on23(Pathfinder.SearchNode var0, Pathfinder.SearchNode var1) {
      int i = Double.compare(var0.double48, var1.double48);
      return i < 0 || i == 0 && var0.double49 < var1.double49;
   }

   public static List<BlockPos> on23(Pathfinder.SearchNode var0) {
      List<BlockPos> arraylist = new ArrayList<>();

      for (Pathfinder.SearchNode l1liiliiiil1i_ii1il11l111ii11iil = var0;
         l1liiliiiil1i_ii1il11l111ii11iil != null;
         l1liiliiiil1i_ii1il11l111ii11iil = l1liiliiiil1i_ii1il11l111ii11iil.zClass073Var159
      ) {
         arraylist.add(l1liiliiiil1i_ii1il11l111ii11iil.blockPos26);
      }

      Collections.reverse(arraylist);
      return arraylist;
   }


   public record NodeDistance(BlockPos blockPos27, double double51) {
      public BlockPos zClass095Var165() {
         return this.blockPos27;
      }

      public double call031() {
         return this.double51;
      }
   }

   public enum NodeType {
      val197,
      val317;
   }

   public static final class SearchNode {
      public final BlockPos blockPos26;
      public final double double48;
      public SearchNode zClass073Var159;
      public double double49;
      public double double50;
      public boolean closed;

      public SearchNode(BlockPos var1, SearchNode var2, double var3, double var5) {
         this.blockPos26 = var1;
         this.zClass073Var159 = var2;
         this.double49 = var3;
         this.double48 = var5;
         this.double50 = var3 + var5;
      }
   }

   public record TimedNode(SearchNode zClass073Var1592, double double52, long long115) {
      public SearchNode int465() {
         return this.zClass073Var1592;
      }

      public double double163() {
         return this.double52;
      }

      public long double164() {
         return this.long115;
      }
   }

   public record PathOptions(int int173, int int174, int int175, int int176, boolean boolean116, boolean boolean117, NodeType zClass073Var143) {
      public static final PathOptions zClass073Var1652 = new PathOptions(50000, 192, 3, 0, false, true, NodeType.val197);

      public PathOptions(int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
         this(var1, var2, var3, var4, var5, var6, NodeType.val197);
      }

      public PathOptions(int var1, int var2, int var3, boolean var4, boolean var5) {
         this(var1, var2, var3, 0, var4, var5, NodeType.val197);
      }

      public PathOptions {
         if (int173 <= 0) {
            throw new IllegalArgumentException("maxVisitedNodes must be positive");
         }

         if (int174 <= 0) {
            throw new IllegalArgumentException("maxRange must be positive");
         }

         if (int175 < 0) {
            throw new IllegalArgumentException("maxFallDistance cannot be negative");
         }

         if (int176 < 0) {
            throw new IllegalArgumentException("goalYTolerance cannot be negative");
         }

         if (zClass073Var143 == null) {
            throw new IllegalArgumentException("pathMode cannot be null");
         }
      }

      public int float139() {
         return this.int173;
      }

      public int float140() {
         return this.int174;
      }

      public int float141() {
         return this.int175;
      }

      public int double159() {
         return this.int176;
      }

      public boolean double160() {
         return this.boolean116;
      }

      public boolean double161() {
         return this.boolean117;
      }

      public NodeType double162() {
         return this.zClass073Var143;
      }
   }

   public static final class Path {
      public final List<BlockPos> list54;
      public int int177;

      public Path(List<BlockPos> var1) {
         this.list54 = List.copyOf(var1);
         this.int177 = Math.min(1, var1.size());
      }

      public List<BlockPos> var04() {
         return this.list54;
      }

      public BlockPos random11() {
         return this.list54.getLast();
      }

      public boolean ImageEncoder() {
         return this.int177 >= this.list54.size();
      }

      public BlockPos EventTriggerKeyEvent(BlockPos var1) {
         for (int i = this.int177; i < this.list54.size(); i++) {
            if (this.list54.get(i).equals(var1)) {
               this.int177 = i + 1;
               break;
            }
         }

         return this.ImageEncoder() ? null : this.list54.get(this.int177);
      }

      public BlockPos CloudRouter(Vec3d var1) {
         while (!this.ImageEncoder()) {
            BlockPos blockpos = this.list54.get(this.int177);
            double d0 = var1.x - (blockpos.getX() + 0.5);
            double d1 = var1.z - (blockpos.getZ() + 0.5);
            if (!(d0 * d0 + d1 * d1 > 0.2025) && !(Math.abs(var1.y - blockpos.getY()) > 0.75)) {
               this.int177++;
               continue;
            }
            break;
         }

         return this.ImageEncoder() ? null : this.list54.get(this.int177);
      }

      public Vec3d EventInjectHandleInputEvents(BlockPos var1) {
         BlockPos blockpos = this.EventTriggerKeyEvent(var1);
         return blockpos == null ? null : Pathfinder.EventInteractBlock(blockpos);
      }

      public Vec3d ProtocolMessage(Vec3d var1) {
         BlockPos blockpos = this.CloudRouter(var1);
         return blockpos == null ? null : Pathfinder.EventInteractBlock(blockpos);
      }

      public void reset() {
         this.int177 = Math.min(1, this.list54.size());
      }
   }
}
