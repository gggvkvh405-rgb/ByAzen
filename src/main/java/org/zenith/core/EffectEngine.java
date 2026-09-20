package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.zenith.ZenithClient;
import org.zenith.rotation.Rotation;
import org.zenith.setting.KeySetting;
import org.zenith.util.ColorUtils;

public final class EffectEngine implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void on23(SequencedPacketCreator var0) {
      minecraftClient3.interactionManager.sendSequencedPacket(minecraftClient3.world, var0);
   }

   public static void double65() {
      minecraftClient3.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient3.player, Mode.START_FALL_FLYING));
      minecraftClient3.player.startGliding();
   }

   public static void ItemServiceBase(Packet<?> var0) {
      System.out.println(var0);
      minecraftClient3.getNetworkHandler().getConnection().send(var0, null);
   }

   public static List<BlockPos> on23(BlockPos var0, float var1) {
      return on23(var0, var1, var1, true);
   }

   public static List<BlockPos> on23(BlockPos var0, float var1, float var2) {
      return on23(var0, var1, var2, true);
   }

   public static List<BlockPos> on23(BlockPos var0, float var1, float var2, boolean var3) {
      List<BlockPos> arraylist = new ArrayList<>();
      int i = var0.getX();
      int j = var0.getY();
      int k = var0.getZ();
      int l = var3 ? j - (int)var2 : j;

      for (int i1 = i - (int)var1; i1 <= i + var1; i1++) {
         for (int j1 = k - (int)var1; j1 <= k + var1; j1++) {
            for (int k1 = l; k1 <= j + var2; k1++) {
               arraylist.add(new BlockPos(i1, k1, j1));
            }
         }
      }

      return arraylist;
   }

   public static List<BlockPos> TextScanner(BlockPos var0, BlockPos var1) {
      List<BlockPos> arraylist = new ArrayList<>();

      for (int i = var0.getX(); i <= var1.getX(); i++) {
         for (int j = var0.getZ(); j <= var1.getZ(); j++) {
            for (int k = var0.getY(); k <= var1.getY(); k++) {
               arraylist.add(new BlockPos(i, k, j));
            }
         }
      }

      return arraylist;
   }

   public static Type PlayerMoveEvent(int var0) {
      return var0 < 8 ? Type.MOUSE : Type.KEYSYM;
   }

   public static Stream<Entity> double66() {
      return StreamSupport.stream(minecraftClient3.world.getEntities().spliterator(), false);
   }

   public static boolean UiAnimation(EntityPose var0) {
      return minecraftClient3.player
         .getEntityWorld()
         .isSpaceEmpty(
            minecraftClient3.player,
            minecraftClient3.player.getDimensions(var0).getBoxAt(minecraftClient3.player.getEntityPos()).contract(1.0E-7)
         );
   }

   public static boolean ColorAnimator(RegistryEntry<StatusEffect> var0) {
      return minecraftClient3.player.getActiveStatusEffects().containsKey(var0);
   }

   public static boolean ItemSpec(Block var0) {
      return on23(minecraftClient3.player.getBoundingBox().expand(-0.001), var0);
   }

   public static void on23(BlockHitResult var0, Hand var1) {
      if (minecraftClient3.interactionManager.interactBlock(minecraftClient3.player, var1, var0) instanceof Success success
         && success.swingSource() == SwingSource.CLIENT) {
         minecraftClient3.player.swingHand(var1);
      }
   }

   public static boolean on23(Box var0, Block var1) {
      return on23(var0, var1xx -> minecraftClient3.world.getBlockState(var1xx).getBlock().equals(var1));
   }

   public static boolean on23(Box var0, List<Block> var1) {
      return on23(var0, var1xx -> var1.contains(minecraftClient3.world.getBlockState(var1xx).getBlock()));
   }

   public static FireworkRocketEntity double67() {
      for (Entity entity : minecraftClient3.world.getEntities()) {
         if (entity instanceof FireworkRocketEntity fireworkrocketentity && fireworkrocketentity.shooter == minecraftClient3.player) {
            return fireworkrocketentity;
         }
      }

      return null;
   }

   public static int double68() {
      int i = 0;

      while (minecraftClient3.player.getAttackCooldownProgress(i) < 0.9F && i < 20) {
         i++;
      }

      return i;
   }

   public static boolean on23(Box var0, Predicate<BlockPos> var1) {
      return BlockPos.stream(var0).anyMatch(var1);
   }

   public static boolean on23(Key var0) {
      return on23(var0.getCategory(), var0.getCode());
   }

   public static boolean on23(KeySetting var0) {
      int i = var0.getKeyCode();
      return minecraftClient3.currentScreen == null && var0.isVisible() && on23(PlayerMoveEvent(i), i);
   }

   public static boolean on23(Type var0, int var1) {
      if (var1 != -1) {
         switch (var0) {
            case KEYSYM:
               return GLFW.glfwGetKey(minecraftClient3.getWindow().getHandle(), var1) == 1;
            case MOUSE:
               return GLFW.glfwGetMouseButton(minecraftClient3.getWindow().getHandle(), var1) == 1;
         }
      }

      return false;
   }

   public static boolean EventMouseButton(BlockPos var0) {
      return ItemSpec(minecraftClient3.world.getBlockState(var0));
   }

   public static boolean ItemSpec(BlockState var0) {
      return var0.isAir() || var0.getBlock().equals(Blocks.CAVE_AIR) || var0.getBlock().equals(Blocks.VOID_AIR);
   }

   public static boolean UiAnimation(Screen var0) {
      return var0 instanceof ChatScreen;
   }

   public static boolean double69() {
      return minecraftClient3.player == null || minecraftClient3.world == null;
   }

   public static void useItem(Hand var0) {
      if (minecraftClient3.interactionManager.interactItem(minecraftClient3.player, var0) instanceof Success success
         && success.swingSource() == SwingSource.CLIENT) {
         minecraftClient3.player.swingHand(var0);
      }
   }

   public static float SimpleItemBuilder(LivingEntity var0) {
      float f = var0.getHealth() + var0.getAbsorptionAmount();
      if (var0 instanceof PlayerEntity playerentity) {
         String s = ZenithClient.on23().CloudApiClient().getServer();
         switch (s) {
            case "FunTime":
            case "ReallyWorld":
               ScoreboardObjective scoreboardobjective = playerentity.getEntityWorld().getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
               if (scoreboardobjective != null) {
                  MutableText mutabletext = ReadableScoreboardScore.getFormattedScore(
                     playerentity.getEntityWorld().getScoreboard().getScore(playerentity, scoreboardobjective), scoreboardobjective.getNumberFormatOr(StyledNumberFormat.EMPTY)
                  );

                  try {
                     f = Float.parseFloat(ColorUtils.HudSelectedItemPanel(mutabletext.getString()));
                  } catch (NumberFormatException var9) {
                  }
               }
         }
      }

      return f;
   }

   public static float ItemServiceBase(LivingEntity var0) {
      return var0 instanceof PlayerEntity playerentity
         ? (float)Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
            .filter(slot -> !playerentity.getEquippedStack(slot).isEmpty()).count()
         : var0.getArmor();
   }

   public static boolean NbtEditor(LivingEntity var0) {
      Rotation ililiiili1ll1li11 = Rotation.CancellableEvent(minecraftClient3.player.getBoundingBox().getCenter().subtract(var0.getEyePos()));
      boolean flag = var0.isUsingItem() && var0.getActiveItem().getItem().equals(Items.SHIELD);
      boolean flag1 = Math.abs(MathHelper.wrapDegrees(var0.getYaw() - ililiiili1ll1li11.GrimGlide())) < 60.0F;
      return flag && flag1;
   }

   public static String PotionItemBuilder(LivingEntity var0) {
      return RefreshCacheEvent(SimpleItemBuilder(var0));
   }

   public static String RefreshCacheEvent(float var0) {
      return String.format("%.1f", var0).replace(",", ".").replace(".0", "");
   }

   public static void on23(double var0, Rotation var2) {
      ItemServiceBase(
         new Full(
            minecraftClient3.player.getX(),
            minecraftClient3.player.getY() + var0,
            minecraftClient3.player.getZ(),
            var2.GrimGlide(),
            var2.GuiWalk(),
            minecraftClient3.player.isOnGround(),
            minecraftClient3.player.horizontalCollision
         )
      );
   }

   public static BlockFaceHit EventModifyMouseRotationInput(BlockPos var0) {
      if (minecraftClient3.world.getBlockState(var0.add(0, -1, 0)).isSolid()) {
         return new BlockFaceHit(var0.add(0, -1, 0), Direction.UP);
      } else if (minecraftClient3.world.getBlockState(var0.add(-1, 0, 0)).isSolid()) {
         return new BlockFaceHit(var0.add(-1, 0, 0), Direction.EAST);
      } else if (minecraftClient3.world.getBlockState(var0.add(1, 0, 0)).isSolid()) {
         return new BlockFaceHit(var0.add(1, 0, 0), Direction.WEST);
      } else if (minecraftClient3.world.getBlockState(var0.add(0, 0, 1)).isSolid()) {
         return new BlockFaceHit(var0.add(0, 0, 1), Direction.NORTH);
      } else {
         return minecraftClient3.world.getBlockState(var0.add(0, 0, -1)).isSolid()
            ? new BlockFaceHit(var0.add(0, 0, -1), Direction.SOUTH)
            : null;
      }
   }

   public EffectEngine() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
