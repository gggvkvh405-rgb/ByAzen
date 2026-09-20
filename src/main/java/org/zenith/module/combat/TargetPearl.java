package org.zenith.module.combat;

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
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.TargetAcquireEvent;
import org.zenith.event.EventInjectAddEntity;
import org.zenith.event.RefreshCacheEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MathUtils;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "TargetPearl", description = "Target Pearl", category = Category.COMBAT)
public final class TargetPearl extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final TargetPearl targetPearl = new TargetPearl();
   public final CooldownTimer zClass06737 = new CooldownTimer();
   public final ModeSetting modeSetting = new ModeSetting(
      "module.targetPearl.modeSetting", "module.targetPearl.modeSetting.desc", "module.targetPearl.modeBind", "module.targetPearl.modeAlways"
   );
   public final ModeSetting targetSetting = new ModeSetting(
      "module.targetPearl.targetSetting", "module.targetPearl.targetSetting.desc", "module.targetPearl.targetTarget", "module.targetPearl.targetAll"
   );
   public final KeySetting throwSetting = new KeySetting(
      "module.targetPearl.throwSetting", "module.targetPearl.throwSetting.desc", () -> this.modeSetting.is(0)
   );
   public final NumberSetting distanceSetting2 = new NumberSetting(
      "module.targetPearl.distanceSetting", 6.0F, 3.0F, 15.0F, 1.0F, "module.targetPearl.distanceSetting.desc", "b"
   );
   public Rotation rotation = null;
   public HitResult hitResult = null;
   int int88 = 0;

   @EventTarget
   public void Easing(EventInjectAddEntity var1) {
      if (var1.ElytraFly() instanceof EnderPearlEntity enderpearlentity) {
         minecraftClient3.world
            .getPlayers()
            .stream()
            .filter(var1x -> var1x.distanceTo(enderpearlentity) <= 3.0F)
            .min(Comparator.comparingDouble(var1x -> var1x.distanceTo(enderpearlentity)))
            .ifPresent(enderpearlentity::setOwner);
      }
   }

   @EventTarget
   public void ItemRegistry(RotationUpdateStartEvent var1) {
      LivingEntity livingentity = Aura.aura.var11813();
      Slot slot = ScreenUtils.SimpleItemBuilder(Items.ENDER_PEARL);
      if (slot != null
         && this.zClass06737.EventModifyMouseRotationInput(1000L)
         && (!this.modeSetting.is(0) || EffectEngine.on23(this.throwSetting))
         && !EffectEngine.double66()
            .filter(EnderPearlEntity.class::isInstance)
            .map(EnderPearlEntity.class::cast)
            .anyMatch(var0 -> Objects.equals(var0.getOwner(), minecraftClient3.player))) {
         Predictions lii1l1lll1 = Predictions.predictions;
         EffectEngine.double66()
            .filter(EnderPearlEntity.class::isInstance)
            .map(EnderPearlEntity.class::cast)
            .sorted(
               Comparator.comparingDouble(
                  var1x -> ZenithClient.on23().CloudRouter().LineShader().EmoteManager(RotationMath.BotChatEvent(lii1l1lll1.on23(var1x).getPos())).gson2()
               )
            )
            .filter(
               var2x -> !ZenithClient.on23().MediaTrackInfo().UiAnimation(var2x.getOwner())
                  && (this.targetSetting.is(1) || livingentity != null && livingentity.equals(var2x.getOwner()))
            )
            .findFirst()
            .ifPresent(
               var2x -> {
                  HitResult hitresult = lii1l1lll1.on23(var2x);
                  this.hitResult = hitresult;
                  if (hitresult != null
                     && !(
                        MathUtils.PotionItemBuilder(minecraftClient3.player.getEntityPos(), hitresult.getPos()) < this.distanceSetting2.getCurrent()
                     )) {
                     float f = RotationMath.BotChatEvent(hitresult.getPos()).GrimGlide();
                     IntStream.range(-89, 89).mapToObj(var1xx -> new Rotation(f, var1xx)).filter(var2xx -> {
                        HitResult hitresult1 = lii1l1lll1.on23(var2xx.int202(), new EnderPearlEntity(EntityType.ENDER_PEARL, minecraftClient3.world), 1.5);
                        return hitresult1 == null ? false : MathUtils.PotionItemBuilder(hitresult1.getPos(), hitresult.getPos()) <= 3.0;
                     }).max(Comparator.comparingDouble(Rotation::GuiWalk)).ifPresent(var2xxx -> {
                        this.rotation = var2xxx;
                        this.int88 = 0;
                        ScreenUtils.ItemServiceBase(Items.ENDER_PEARL);
                        var2x.setOwner(null);
                        this.zClass06737.reset();
                     });
                  }
               }
            );
      }
   }

   @EventTarget(0)
   public void UiAnimation(RefreshCacheEvent var1) {
      if (this.hitResult != null) {
         Predictions lii1l1lll1 = Predictions.predictions;
         float f = RotationMath.BotChatEvent(this.hitResult.getPos()).GrimGlide();
         IntStream.range(-89, 89)
            .mapToObj(var1x -> new Rotation(f, var1x))
            .filter(
               var2x -> {
                  HitResult hitresult = lii1l1lll1.on23(var2x.int202(), new EnderPearlEntity(EntityType.ENDER_PEARL, minecraftClient3.world), 1.5);
                  return hitresult != null && hitresult.getType() != Type.ENTITY
                     ? MathUtils.PotionItemBuilder(hitresult.getPos(), this.hitResult.getPos()) <= 3.0
                     : false;
               }
            )
            .max(Comparator.comparingDouble(Rotation::GuiWalk))
            .ifPresent(var1x -> {
               this.rotation = var1x;
               this.zClass06737.reset();
            });
         if (this.rotation != null) {
            if (ZenithClient.on23().CloudRouter().LineShader().EmoteManager(this.rotation).EventMotion(10.0F)) {
               if (this.int88 >= 1) {
                  this.hitResult = null;
                  return;
               }

               this.int88++;
            }

            ZenithClient.on23()
               .CloudRouter()
               .on23(new RotationTask(this.rotation, () -> val001.on23(Aura.aura.zClass088(), this.rotation), Aura.aura.zClass088()), 20, this, 2);
            var1.cancel();
         }
      }
   }

   @EventTarget
   public void UiAnimation(TargetAcquireEvent var1) {
   }
}
