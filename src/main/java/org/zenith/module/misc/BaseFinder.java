package org.zenith.module.misc;

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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.zenith.ZenithClient;
import org.zenith.base.font.Fonts;
import org.zenith.core.EffectEngine;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.Translator;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.HudRenderEvent;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.CooldownTimer;
import org.zenith.util.CryptoUtils;

@ModuleInfo(name = "BaseFinder", description = "РёС‰РµС‚ Р±Р°Р·С‹", category = Category.MISC)
public final class BaseFinder extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int81 = 8192;
   public static final int int82 = 4096;
   public static final int int83 = 3072;
   public static final int int84 = 1536;
   public static final BaseFinder baseFinder = new BaseFinder();
   public JsonObject jsonObject;
   public final MultiSelectSetting mods = new MultiSelectSetting(
      "module.baseFinder.mods",
      "module.baseFinder.mods.desc",
      MultiSelectSetting.Option.UiAnimation("module.baseFinder.mods.cave", false),
      MultiSelectSetting.Option.EventImpl("module.baseFinder.mods.bypass"),
      MultiSelectSetting.Option.EventImpl("module.baseFinder.mods.click"),
      MultiSelectSetting.Option.EventImpl("module.baseFinder.mods.packetAnalysis")
   );
   public final NumberSetting range = new NumberSetting("module.baseFinder.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.baseFinder.range.desc", "b");
   public final NumberSetting min = new NumberSetting(
      "module.baseFinder.min", 3.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.min.desc", "x", () -> this.mods.ConfigJsonUtil(0), null
   );
   public final NumberSetting max = new NumberSetting(
      "module.baseFinder.max", 50.0F, 5.0F, 100.0F, 5.0F, "module.baseFinder.max.desc", "x", () -> this.mods.ConfigJsonUtil(0), null
   );
   public final NumberSetting minLength = new NumberSetting(
      "module.baseFinder.minLength", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minLength.desc", "b", () -> this.mods.ConfigJsonUtil(0), null
   );
   public final NumberSetting minWidth = new NumberSetting(
      "module.baseFinder.minWidth", 2.0F, 0.0F, 30.0F, 1.0F, "module.baseFinder.minWidth.desc", "b", () -> this.mods.ConfigJsonUtil(0), null
   );
   public final CooldownTimer zClass06716 = new CooldownTimer();
   public final Set<BlockPos> set4 = ConcurrentHashMap.newKeySet();
   public final Set<BlockPos> set5 = ConcurrentHashMap.newKeySet();
   public final CopyOnWriteArrayList<BaseFinder.ScanResult> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
   public final List<BlockPos> list10 = new ArrayList<>();
   public volatile int val118 = 1;
   public volatile int val089 = 0;
   public volatile long val372 = 0L;
   public volatile boolean val119 = false;
   public BaseFinder.Service baseFinderVar159;
   public BaseFinder.ServiceCore baseFinderVar165;

   @Override
   public void onEnable() {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.zClass06716.EventMixin_modifySetScreenArg(0L);
         this.copyOnWriteArrayList.clear();
         this.set4.clear();
         this.set5.clear();
         this.list10.clear();
         this.baseFinderVar159 = null;
         this.baseFinderVar165 = null;
         this.val119 = false;
         this.val089 = 0;
         this.val118 = 1;
         this.int458();
         this.int454();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      this.baseFinderVar159 = null;
      this.baseFinderVar165 = null;
      this.val119 = false;
      this.set4.clear();
      this.set5.clear();
      this.list10.clear();
      this.jsonObject = null;
      super.onDisable();
   }

   @EventTarget
   public void on23(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.zClass06716.EventModifyMouseRotationInput(1000L)) {
            this.int454();
            this.zClass06716.reset();
         }

         if (this.mods.AnalyticsTracker(1).isEnabled()) {
            this.int455();
         } else {
            this.baseFinderVar159 = null;
            this.set4.clear();
            this.list10.clear();
         }

         if (this.mods.ConfigJsonUtil(0)) {
            this.int456();
         } else {
            this.float364();
            this.set5.clear();
         }

         if (this.mods.ConfigJsonUtil(2)) {
            for (int i = 0; i < 5 && !this.list10.isEmpty(); i++) {
               BlockPos blockpos = this.list10.getFirst();
               EffectEngine.on23(new BlockHitResult(blockpos.toCenterPos(), Direction.UP, blockpos, false), Hand.OFF_HAND);
               StyledTextBuilder.RefreshCacheEvent(this.list10.size() + "|");
               this.list10.removeFirst();
            }
         }
      }
   }

   @EventTarget
   public void ItemSpec(HudRenderEvent var1) {
      if (this.val119 && this.mods.ConfigJsonUtil(0)) {
         double d0 = this.val089 * 100.0 / Math.max(this.val118, 1);
         long i = System.currentTimeMillis();
         long j = i - this.val372;
         long k = (long)(j * ((double)this.val118 / Math.max(this.val089, 1)));
         long l = Math.max(k - j, 0L);
         long i1 = l / 1000L;
         long j1 = i1 / 60L;
         i1 %= 60L;
         String s = String.format("РџРѕРёСЃРє: %.1f%% (%d/%d) | РћСЃС‚Р°Р»РѕСЃСЊ: %02d:%02d", d0, this.val089, this.val118, j1, i1);
         var1.Bot().drawText(Fonts.MEDIUM.getFont(20.0F), s, 0.0F, 0.0F, ArgbColor.var11934);
      }
   }

   @EventTarget
   public void ItemSpec(EventHookWorldRender var1) {
      if (this.mods.ConfigJsonUtil(1)) {
         this.set4.forEach(var1x -> this.on23(var1x, -1));
      }

      if (this.mods.ConfigJsonUtil(0)) {
         this.set5.forEach(var1x -> this.on23(var1x, Color.GREEN.getRGB()));
      }

      this.copyOnWriteArrayList.forEach(var1x -> {
         Block block = var1x.block3;
         if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            this.on23(var1x.blockPos28, Color.cyan.getRGB());
         } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
            this.on23(var1x.blockPos28, -10496);
         } else if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
            this.on23(var1x.blockPos28, -16711859);
         } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            this.on23(var1x.blockPos28, -2763307);
         } else if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
            this.on23(var1x.blockPos28, -65536);
         } else if (block == Blocks.ANCIENT_DEBRIS) {
            this.on23(var1x.blockPos28, -1);
         } else if (block == Blocks.CHEST) {
            this.on23(var1x.blockPos28, Color.ORANGE.getRGB());
         } else {
            MapColor mapcolor = block.getDefaultMapColor();
            if (mapcolor != null) {
               this.on23(var1x.blockPos28, new Color(mapcolor.color).getRGB());
            }
         }
      });
   }

   @EventTarget
   public void UiAnimation(PacketEvent var1) {
      if (this.mods.ConfigJsonUtil(3)) {
      }
   }

   public void int454() {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         BaseFinder.Translation lii1lli1ill1lllilll11ll11i1l_liil11l111liil1ll = this.int457();
         if (this.mods.AnalyticsTracker(1).isEnabled() && this.baseFinderVar159 == null) {
            this.baseFinderVar159 = new BaseFinder.Service(lii1lli1ill1lllilll11ll11i1l_liil11l111liil1ll);
         }

         if (this.mods.ConfigJsonUtil(0) && this.baseFinderVar165 == null) {
            this.baseFinderVar165 = new BaseFinder.ServiceCore(lii1lli1ill1lllilll11ll11i1l_liil11l111liil1ll);
            this.val119 = true;
            this.val372 = System.currentTimeMillis();
            this.val089 = 0;
            this.val118 = Math.max(1, lii1lli1ill1lllilll11ll11i1l_liil11l111liil1ll.float365());
         }
      }
   }

   public void int455() {
      if (this.baseFinderVar159 != null && minecraftClient3.world != null) {
         int i = 0;
         int j = this.mods.ConfigJsonUtil(0) ? 4096 : 8192;
         Mutable mutable = new Mutable();

         BaseFinder.Service lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil;
         for (lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil = this.baseFinderVar159; i < j && !lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.finished; i++) {
            mutable.set(
               lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.x,
               lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.y,
               lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.z
            );
            if (minecraftClient3.world
               .isChunkLoaded(lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.x >> 4, lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.z >> 4)) {
               BlockState blockstate = minecraftClient3.world.getBlockState(mutable);
               if (this.on23(blockstate, mutable)) {
                  lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.set9.add(mutable.toImmutable());
               }
            }

            lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.advance();
         }

         if (lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.finished) {
            Set<BlockPos> set = this.on23(lii1lli1ill1lllilll11ll11i1l_ii1il11l111ii11iil.set9);
            this.set4.clear();
            this.set4.addAll(set);
            this.list10.clear();
            this.list10.addAll(set);
            this.baseFinderVar159 = null;
         }
      }
   }

   public void int456() {
      if (this.baseFinderVar165 != null && minecraftClient3.world != null) {
         int i = 0;
         int j = this.mods.AnalyticsTracker(1).isEnabled() ? 1536 : 3072;

         BaseFinder.ServiceCore lii1lli1ill1lllilll11ll11i1l_illi1l1l1;
         for (lii1lli1ill1lllilll11ll11i1l_illi1l1l1 = this.baseFinderVar165; i < j && !lii1lli1ill1lllilll11ll11i1l_illi1l1l1.finished; i++) {
            BlockPos blockpos = new BlockPos(
               lii1lli1ill1lllilll11ll11i1l_illi1l1l1.x, lii1lli1ill1lllilll11ll11i1l_illi1l1l1.y, lii1lli1ill1lllilll11ll11i1l_illi1l1l1.z
            );
            this.val089++;
            if (minecraftClient3.world.isChunkLoaded(blockpos)
               && !lii1lli1ill1lllilll11ll11i1l_illi1l1l1.set10.contains(blockpos)
               && minecraftClient3.world.isAir(blockpos)) {
               BaseFinder.ScanRequest lii1lli1ill1lllilll11ll11i1l_l1i1illlili = this.on23(
                  blockpos, lii1lli1ill1lllilll11ll11i1l_illi1l1l1.set10, minecraftClient3.world, lii1lli1ill1lllilll11ll11i1l_illi1l1l1.baseFinderVar7
               );
               if (this.on23(lii1lli1ill1lllilll11ll11i1l_l1i1illlili)) {
                  lii1lli1ill1lllilll11ll11i1l_illi1l1l1.set9.addAll(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val236);
                  if (lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val120) {
                     lii1lli1ill1lllilll11ll11i1l_illi1l1l1.set9.addAll(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val171);
                  }
               }
            }

            lii1lli1ill1lllilll11ll11i1l_illi1l1l1.advance();
         }

         if (lii1lli1ill1lllilll11ll11i1l_illi1l1l1.finished) {
            this.set5.clear();
            this.set5.addAll(lii1lli1ill1lllilll11ll11i1l_illi1l1l1.set9);
            this.float364();
         }
      }
   }

   public void float364() {
      this.baseFinderVar165 = null;
      this.val119 = false;
   }

   public BaseFinder.Translation int457() {
      int i = (int)this.range.getCurrent();
      return new BaseFinder.Translation(
         (int)Math.floor(minecraftClient3.player.getX() - i),
         (int)Math.ceil(minecraftClient3.player.getX() + i),
         minecraftClient3.world.getBottomY() + 1,
         minecraftClient3.world.getTopYInclusive(),
         (int)Math.floor(minecraftClient3.player.getZ() - i),
         (int)Math.ceil(minecraftClient3.player.getZ() + i)
      );
   }

   public Set<BlockPos> on23(Set<BlockPos> var1) {
      return !ZenithClient.on23().CloudApiClient().call003() ? var1 : on23(var1, 20).stream().flatMap(Collection::stream).collect(Collectors.toSet());
   }

   public boolean on23(BlockState var1, BlockPos var2) {
      if (ZenithClient.on23().CloudApiClient().call003()) {
         return var1.getBlock() != Blocks.LAVA && minecraftClient3.world.getLightLevel(LightType.BLOCK, var2) > 5;
      } else {
         return minecraftClient3.world.getLightLevel(LightType.BLOCK, var2) == 0
            ? false
            : var1.getBlock() == Blocks.NETHERRACK || var1.getBlock() == Blocks.STONE;
      }
   }

   public boolean on23(BaseFinder.ScanRequest var1) {
      if (var1.size < this.min.getCurrent() || var1.size > this.max.getCurrent()) {
         return false;
      } else if (var1.height() < this.minLength.getCurrent()) {
         return false;
      } else {
         return var1.width() < this.minWidth.getCurrent() ? false : var1.val120 || var1.val373;
      }
   }

   public BaseFinder.ScanRequest on23(BlockPos var1, Set<BlockPos> var2, World var3, BaseFinder.Translation var4) {
      ArrayDeque arraydeque = new ArrayDeque();
      BaseFinder.ScanRequest lii1lli1ill1lllilll11ll11i1l_l1i1illlili = new BaseFinder.ScanRequest();
      arraydeque.add(var1);
      var2.add(var1);

      while (!arraydeque.isEmpty()) {
         BlockPos blockpos = (BlockPos)arraydeque.poll();
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val236.add(blockpos);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.size++;
         int i = blockpos.getX();
         int j = blockpos.getY();
         int k = blockpos.getZ();
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val237 = Math.min(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val237, i);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val238 = Math.max(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val238, i);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.minY = Math.min(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.minY, j);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val239 = Math.max(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val239, j);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val240 = Math.min(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val240, k);
         lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val241 = Math.max(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val241, k);
         if (lii1lli1ill1lllilll11ll11i1l_l1i1illlili.size > this.max.getCurrent()) {
            break;
         }

         for (Direction direction : Direction.values()) {
            BlockPos blockpos1 = blockpos.offset(direction);
            if (!var4.EmotePlayback(blockpos1)) {
               lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val120 = true;
               lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val171.add(blockpos);
            } else if (!var3.isChunkLoaded(blockpos1)) {
               lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val120 = true;
               lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val171.add(blockpos);
            } else if (!var2.contains(blockpos1)) {
               if (var3.isAir(blockpos1)) {
                  var2.add(blockpos1);
                  arraydeque.add(blockpos1);
               } else if (this.on23(var3, blockpos1)) {
                  lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val120 = true;
                  lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val171.add(blockpos1);
               }
            }
         }
      }

      lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val373 = this.on23(lii1lli1ill1lllilll11ll11i1l_l1i1illlili.val236, var3);
      return lii1lli1ill1lllilll11ll11i1l_l1i1illlili;
   }

   public boolean on23(List<BlockPos> var1, World var2) {
      HashSet hashset = new HashSet<>(var1);

      for (BlockPos blockpos : var1) {
         for (Direction direction : Direction.values()) {
            BlockPos blockpos1 = blockpos.offset(direction);
            if (!var2.isChunkLoaded(blockpos1)) {
               return false;
            }

            if (!hashset.contains(blockpos1) && var2.isAir(blockpos1)) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean on23(World var1, BlockPos var2) {
      return var1.isChunkLoaded(var2) && var1.getBlockState(var2).isReplaceable();
   }

   public void on23(BlockPos var1, int var2) {
      WorldRender.on23(new Box(var1), var2, 1.0F);
   }

   public void int458() {
      try (InputStream inputstream = Translator.class.getResourceAsStream("/assets/zenith/fonts/msdf/hold.json")) {
         if (inputstream == null) {
            this.jsonObject = null;
         } else {
            byte[] abyte = Base64.getDecoder().decode(inputstream.readAllBytes());
            byte[] abyte1 = CryptoUtils.ColorAnimator(abyte, "1.21.4");

            try (InputStreamReader inputstreamreader = new InputStreamReader(new ByteArrayInputStream(abyte1), StandardCharsets.UTF_8)) {
               this.jsonObject = (JsonObject)new Gson().fromJson(inputstreamreader, JsonObject.class);
            }
         }
      } catch (Exception exception) {
         this.jsonObject = null;
         exception.printStackTrace();
      }
   }

   public static List<List<BlockPos>> on23(Collection<BlockPos> var0, int var1) {
      ArrayList arraylist = new ArrayList();
      HashSet hashset = new HashSet<>(var0);

      while (!hashset.isEmpty()) {
         BlockPos blockpos = (BlockPos)hashset.iterator().next();
         ArrayList arraylist1 = new ArrayList();
         ArrayDeque arraydeque = new ArrayDeque();
         arraydeque.add(blockpos);
         hashset.remove(blockpos);

         while (!arraydeque.isEmpty()) {
            BlockPos blockpos1 = (BlockPos)arraydeque.poll();
            arraylist1.add(blockpos1);

            for (BlockPos blockpos2 : EmoteMetadata(blockpos1)) {
               if (hashset.contains(blockpos2)) {
                  hashset.remove(blockpos2);
                  arraydeque.add(blockpos2);
               }
            }
         }

         if (arraylist1.size() <= var1) {
            arraylist.add(arraylist1);
         }
      }

      return arraylist;
   }

   public static List<BlockPos> EmoteMetadata(BlockPos var0) {
      List<BlockPos> arraylist = new ArrayList<>(6);

      for (Direction direction : Direction.values()) {
         arraylist.add(var0.offset(direction));
      }

      return arraylist;
   }

   public static CopyOnWriteArrayList<BaseFinder.ScanResult> on23(InputStream var0, BlockPos var1, double var2) {
      CopyOnWriteArrayList copyonwritearraylist = new CopyOnWriteArrayList();
      double d0 = var2 * var2;

      String s;
      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(var0))) {
         while ((s = bufferedreader.readLine()) != null) {
            String[] astring = s.split(" ");
            if (astring.length == 4) {
               try {
                  int i = Integer.parseInt(astring[0]);
                  int j = Integer.parseInt(astring[1]);
                  int k = Integer.parseInt(astring[2]);
                  double d1 = i - var1.getX();
                  double d2 = k - var1.getZ();
                  if (!(d1 * d1 + d2 * d2 > d0)) {
                     String s1 = new String(Base64.getDecoder().decode(astring[3]), StandardCharsets.UTF_8);
                     if (s1.startsWith("block.minecraft.")) {
                        s1 = s1.substring("block.minecraft.".length());
                     }

                     Identifier identifier = Identifier.of("minecraft", s1);
                     Block block = (Block)Registries.BLOCK.get(identifier);
                     if (block != Blocks.AIR) {
                        copyonwritearraylist.add(new BaseFinder.ScanResult(new BlockPos(i, j, k), block));
                     }
                  }
               } catch (Exception exception) {
                  exception.printStackTrace();
               }
            }
         }
      } catch (Exception exception1) {
         exception1.printStackTrace();
      }

      return copyonwritearraylist;
   }


   public static class ScanRequest {
      final List<BlockPos> val236 = new ArrayList<>();
      final List<BlockPos> val171 = new ArrayList<>();
      boolean val120 = false;
      boolean val373 = true;
      int size = 0;
      int val237 = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int val240 = Integer.MAX_VALUE;
      int val238 = Integer.MIN_VALUE;
      int val239 = Integer.MIN_VALUE;
      int val241 = Integer.MIN_VALUE;

      public ScanRequest() {
      }

      int width() {
         return Math.max(this.val238 - this.val237 + 1, this.val241 - this.val240 + 1);
      }

      int height() {
         return this.val239 - this.minY + 1;
      }
   }

   public static class Service {
      protected final Translation baseFinderVar7;
      protected final Set<BlockPos> set9 = new HashSet<>();
      protected int x;
      protected int y;
      protected int z;
      protected boolean finished;

      public Service(Translation var1) {
         this.baseFinderVar7 = var1;
         this.x = var1.int192;
         this.y = var1.int194;
         this.z = var1.int196;
      }

      protected void advance() {
         if (!this.finished && ++this.z > this.baseFinderVar7.int197) {
            this.z = this.baseFinderVar7.int196;
            if (++this.y > this.baseFinderVar7.int195) {
               this.y = this.baseFinderVar7.int194;
               if (++this.x > this.baseFinderVar7.int193) {
                  this.finished = true;
               }
            }
         }
      }
   }

   public static class ScanResult {
      public final BlockPos blockPos28;
      public final Block block3;

      public ScanResult(BlockPos var1, Block var2) {
         this.blockPos28 = var1;
         this.block3 = var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof ScanResult lii1lli1ill1lllilll11ll11i1l_Var160
               ? this.blockPos28.equals(lii1lli1ill1lllilll11ll11i1l_Var160.blockPos28) && this.block3.equals(lii1lli1ill1lllilll11ll11i1l_Var160.block3)
               : false;
         }
      }

      @Override
      public int hashCode() {
         return this.blockPos28.hashCode() * 31 + this.block3.hashCode();
      }
   }

   public static final class ServiceCore extends Service {
      public final Set<BlockPos> set10 = new HashSet<>();

      public ServiceCore(Translation var1) {
         super(var1);
      }
   }

   public static final class Translation {
      public final int int192;
      public final int int193;
      public final int int194;
      public final int int195;
      public final int int196;
      public final int int197;

      public Translation(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.int192 = var1;
         this.int193 = var2;
         this.int194 = var3;
         this.int195 = var4;
         this.int196 = var5;
         this.int197 = var6;
      }

      public boolean EmotePlayback(BlockPos var1) {
         return var1.getX() >= this.int192
            && var1.getX() <= this.int193
            && var1.getY() >= this.int194
            && var1.getY() <= this.int195
            && var1.getZ() >= this.int196
            && var1.getZ() <= this.int197;
      }

      public int float365() {
         return (this.int193 - this.int192 + 1) * (this.int195 - this.int194 + 1) * (this.int197 - this.int196 + 1);
      }
   }
}
