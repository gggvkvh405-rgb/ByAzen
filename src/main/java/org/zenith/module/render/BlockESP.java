package org.zenith.module.render;

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
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.StringListSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "BlockESP", category = Category.RENDER, description = "РџРѕРґСЃРІРµС‡РёРІР°РµС‚ Р±Р»РѕРєРё")
public final class BlockESP extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final Set<BlockPos> set7 = ConcurrentHashMap.newKeySet();
   public final ExecutorService executorService2 = Executors.newSingleThreadExecutor(var0 -> {
      Thread thread = new Thread(var0, "zenith-block-esp-search");
      thread.setDaemon(true);
      return thread;
   });
   public final StringListSetting itemSelectSetting = new StringListSetting(
      "module.blockESP.itemSelectSetting", "module.blockESP.itemSelectSetting.desc", new ArrayList<>(), () -> true
   );
   public final ModeSetting mode4 = new ModeSetting("module.blockESP.mode", "module.blockESP.mode.desc");
   public final ModeSetting.Option modeSetting3Var15932 = new ModeSetting.Option(this.mode4, "module.blockESP.onlyUpdate").int210();
   public final ModeSetting.Option modeSetting3Var15933 = new ModeSetting.Option(this.mode4, "module.blockESP.all");
   public final NumberSetting range2 = new NumberSetting(
      "module.blockESP.range", 80.0F, 1.0F, 128.0F, 2.0F, "module.blockESP.range.desc", "b", this.modeSetting3Var15933::isSelected, null
   );
   public final NumberSetting time3 = new NumberSetting(
      "module.blockESP.time", 4.0F, 0.0F, 100.0F, 5.0F, "module.blockESP.time.desc", "s", this.modeSetting3Var15933::isSelected, null
   );
   public final BooleanSetting tracers = new BooleanSetting("module.blockESP.tracers", "module.blockESP.tracers.desc", false);
   public final CooldownTimer zClass06717 = new CooldownTimer();
   public volatile boolean val059;
   public volatile int val090;
   public static final BlockESP blockESP = new BlockESP();

   @EventTarget
   public void NbtEditor(EventHookWorldRender var1) {
      if (minecraftClient3.world != null) {
         for (BlockPos blockpos : this.set7) {
            Block block = minecraftClient3.world.getBlockState(blockpos).getBlock();
            if (this.itemSelectSetting.Easing(block)) {
               if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
                  this.on23(blockpos, Color.cyan.getRGB());
               } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
                  this.on23(blockpos, -10496);
               } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
                  this.on23(blockpos, -16711859);
               } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
                  this.on23(blockpos, -2763307);
               } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
                  this.on23(blockpos, -65536);
               } else if (block == Blocks.ANCIENT_DEBRIS) {
                  this.on23(blockpos, -1);
               } else {
                  MapColor mapcolor = block.getDefaultMapColor();
                  if (mapcolor != null) {
                     this.on23(blockpos, new Color(mapcolor.color).getRGB());
                  }
               }
            }
         }
      }
   }

   public Set<BlockPos> EventDead(int var1) {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      ClientWorld clientworld = minecraftClient3.world;
      if (clientplayerentity != null && clientworld != null) {
         int i = (int)this.range2.getCurrent();
         int j = (int)Math.floor(clientplayerentity.getX() - i);
         int k = (int)Math.ceil(clientplayerentity.getX() + i);
         int l = clientworld.getBottomY() + 1;
         int i1 = clientworld.getTopYInclusive();
         int j1 = (int)Math.floor(clientplayerentity.getZ() - i);
         int k1 = (int)Math.ceil(clientplayerentity.getZ() + i);
         Set<BlockPos> hashset = new HashSet<>();
         Mutable mutable = new Mutable();

         for (int l1 = j; l1 <= k && var1 == this.val090; l1++) {
            for (int i2 = j1; i2 <= k1 && var1 == this.val090; i2++) {
               if (clientworld.isChunkLoaded(l1 >> 4, i2 >> 4)) {
                  for (int j2 = l; j2 <= i1; j2++) {
                     mutable.set(l1, j2, i2);
                     Block block = clientworld.getBlockState(mutable).getBlock();
                     if (this.ColorAnimator(block)) {
                        hashset.add(mutable.toImmutable());
                     }
                  }
               }
            }
         }

         return hashset;
      } else {
         return Collections.emptySet();
      }
   }

   public BlockESP() {
      this.itemSelectSetting.on23(Blocks.DIAMOND_ORE);
   }

   @Override
   public void onEnable() {
      this.zClass06717.EventMixin_modifySetScreenArg(0L);
      this.var1186();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.var1186();
      this.set7.clear();
      super.onDisable();
   }

   @EventTarget
   public void ItemSpec(EventTick var1) {
      if (minecraftClient3.player == null || minecraftClient3.world == null) {
         this.var1186();
      } else if (this.modeSetting3Var15932.isSelected()) {
         this.var1186();
      } else if (!this.val059 && this.zClass06717.EventEntityCollision(this.time3.getCurrent() * 1000.0F)) {
         this.int424();
         this.zClass06717.reset();
      }
   }

   @EventTarget
   public void UiAnimation(PacketEvent var1) {
      if (var1.Arrows()) {
         if (var1.ItemScroller() instanceof ChunkDeltaUpdateS2CPacket chunkdeltaupdates2cpacket) {
            chunkdeltaupdates2cpacket.visitUpdates((var1x, var2x) -> this.on23(var1x.toImmutable(), var2x.getBlock()));
         }

         if (var1.ItemScroller() instanceof BlockUpdateS2CPacket blockupdates2cpacket) {
            this.on23(blockupdates2cpacket.getPos().toImmutable(), blockupdates2cpacket.getState().getBlock());
         }
      }
   }

   public void on23(BlockPos var1, Block var2) {
      if (this.ColorAnimator(var2)) {
         this.set7.add(var1);
      } else {
         this.set7.remove(var1);
      }
   }

   public void on23(BlockPos var1, int var2) {
      WorldRender.on23(new Box(var1), var2, 1.0F);
      if (this.tracers.isEnabled()) {
         Vec3d vec3d = new Vec3d(0.0, 0.0, 75.0)
            .rotateX(-((float)Math.toRadians(minecraftClient3.gameRenderer.getCamera().getPitch())))
            .rotateY(-((float)Math.toRadians(minecraftClient3.gameRenderer.getCamera().getYaw())))
            .add(FreeCam.freeCam.var1357());
         WorldRender.on23(vec3d, var1.toCenterPos(), var2, 1.0F, false);
      }
   }

   public boolean ColorAnimator(Block var1) {
      return !(var1 instanceof AirBlock) && this.itemSelectSetting.Easing(var1);
   }

   public void int424() {
      if (minecraftClient3.player != null && minecraftClient3.world != null && !this.val059) {
         this.val059 = true;
         int i = this.val090;
         CompletableFuture.<Set<BlockPos>>supplyAsync(() -> this.EventDead(i), this.executorService2)
            .thenAccept(var2 -> minecraftClient3.execute(() -> this.on23(var2, i)))
            .exceptionally(var2 -> {
               minecraftClient3.execute(() -> this.HotbarInputEvent(i));
               return null;
            });
      }
   }

   public void on23(Set<BlockPos> var1, int var2) {
      if (var2 == this.val090) {
         if (this.isEnabled() && minecraftClient3.world != null && !this.modeSetting3Var15932.isSelected()) {
            this.set7.clear();
            this.set7.addAll(var1);
            this.val059 = false;
         } else {
            this.val059 = false;
         }
      }
   }

   public void HotbarInputEvent(int var1) {
      if (var1 == this.val090) {
         this.val059 = false;
      }
   }

   public void var1186() {
      this.val090++;
      this.val059 = false;
   }
}
