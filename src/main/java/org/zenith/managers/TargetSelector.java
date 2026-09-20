package org.zenith.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.GameService;
import org.zenith.module.combat.AntiBot;
import org.zenith.rotation.Rotation;
import org.zenith.util.RandomSource;

public final class TargetSelector implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final RandomSource zClass0462 = new RandomSource();
   public static LivingEntity livingEntity3;

   public static LivingEntity on23(
      Iterable<Entity> var0, float var1, boolean var2, List<String> var3, List<String> var4, Predicate<LivingEntity> var5, boolean var6
   ) {
      if (var6 && livingEntity3 != null && on23(var3, livingEntity3, var1, var2, var5)) {
         return livingEntity3;
      }

      ArrayList arraylist = new ArrayList();

      for (Entity entity : var0) {
         if (entity instanceof LivingEntity livingentity && on23(var3, entity, var1, var2, var5)) {
            arraylist.add(livingentity);
         }
      }

      arraylist.sort(on23(Comparator.comparing(var0x -> true), var4));
      livingEntity3 = arraylist.isEmpty() ? null : (LivingEntity)arraylist.getFirst();
      return livingEntity3;
   }

   public static Comparator<LivingEntity> on23(Comparator<LivingEntity> var0, List<String> var1) {
      var0 = var0.thenComparing((var0x, var1x) -> Boolean.compare(!(var0x instanceof PlayerEntity), !(var1x instanceof PlayerEntity)));
      if (var1.contains("module.aura.targetFov")) {
         var0 = var0.thenComparingDouble(TargetSelector::ProfileItemBuilder);
      }

      if (var1.contains("module.aura.targetArmor")) {
         var0 = var0.thenComparing(Comparator.<LivingEntity>comparingDouble(EffectEngine::ItemServiceBase).reversed());
      }

      if (var1.contains("module.aura.targetHp")) {
         var0 = var0.thenComparingDouble(EffectEngine::SimpleItemBuilder);
      }

      if (var1.contains("module.aura.targetDistance")) {
         var0 = var0.thenComparingDouble(minecraftClient3.player::squaredDistanceTo);
      }

      return var0;
   }

   public static double ProfileItemBuilder(LivingEntity var0) {
      if (minecraftClient3.player == null) {
         return Double.MAX_VALUE;
      }

      Rotation ililiiili1ll1li11 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch());
      Vec3d vec3d = minecraftClient3.player.getEyePos();
      Rotation ililiiili1ll1li111 = Rotation.ItemServiceBase(var0.getBoundingBox().getCenter(), vec3d);
      return ililiiili1ll1li11.EmoteMetadata(ililiiili1ll1li111);
   }

   public static boolean on23(List<String> var0, Entity var1, float var2, boolean var3, Predicate<LivingEntity> var4) {
      return var1 instanceof LivingEntity livingentity && on23(var0, livingentity) && var4.test((LivingEntity)var1)
         ? zClass0462.on23(livingentity, var2, var3)
         : false;
   }

   public static LivingEntity on23(Iterable<Entity> var0, float var1, boolean var2, List<String> var3, List<String> var4, boolean var5) {
      return on23(var0, var1, var2, var3, var4, var0x -> true, var5);
   }

   public static boolean on23(List<String> var0, LivingEntity var1) {
      if (StringCodec(var1)) {
         return false;
      } else if (FileLogger(var1)) {
         return false;
      } else {
         return CloudApiClient(var1) ? false : UiAnimation(var0, var1);
      }
   }

   public static boolean StringCodec(LivingEntity var0) {
      return var0 == minecraftClient3.player;
   }

   public static boolean FileLogger(LivingEntity var0) {
      return !var0.isAlive() || var0.getHealth() <= 0.0F;
   }

   public static boolean CloudApiClient(LivingEntity var0) {
      return var0 instanceof PlayerEntity playerentity && AntiBot.antiBot.ItemSpec(playerentity);
   }

   public static boolean UiAnimation(List<String> var0, LivingEntity var1) {
      if (var1 instanceof PlayerEntity playerentity) {
         if (FriendFilter.PotionItemBuilder(playerentity.getId())) {
            return false;
         } else if (ZenithClient.on23().MediaTrackInfo().isFriend(playerentity.getGameProfile().name())) {
            return false;
         } else {
            return EffectEngine.ItemServiceBase(playerentity) == 0.0F ? var0.contains("module.aura.noarmor") : var0.contains("module.aura.targetPlayers");
         }
      } else if (var1 instanceof MobEntity) {
         return var0.contains("module.aura.targetHostile");
      } else {
         return var1 instanceof AnimalEntity ? var0.contains("module.aura.targetPeaceful") : !(var1 instanceof ArmorStandEntity);
      }
   }

   public TargetSelector() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
