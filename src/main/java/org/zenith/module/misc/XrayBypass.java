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
import java.awt.Color;
import java.util.LinkedHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.RaycastUtils;

@ModuleInfo(name = "XrayBypass", category = Category.MISC, description = "")
public final class XrayBypass extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final XrayBypass xrayBypass = new XrayBypass();
   public final NumberSetting range3 = new NumberSetting("module.xrayBypass.range", 30.0F, 1.0F, 128.0F, 2.0F, "module.xrayBypass.range.desc", "b");
   public final NumberSetting height = new NumberSetting("module.xrayBypass.height", 20.0F, 1.0F, 255.0F, 2.0F, "module.xrayBypass.height.desc", "b");
   public final NumberSetting delay6 = new NumberSetting("module.xrayBypass.delay", 3.0F, 1.0F, 5.0F, 1.0F, "module.xrayBypass.delay.desc", "t");
   public LinkedHashSet<BlockPos> linkedHashSet2;
   public CooldownTimer zClass06739 = new CooldownTimer();

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onEnable() {
      this.linkedHashSet2 = new LinkedHashSet<>();
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.linkedHashSet2
            .addAll(
               EffectEngine.on23(minecraftClient3.player.getBlockPos(), this.range3.getCurrent(), this.height.getCurrent(), false)
                  .stream()
                  .filter(minecraftClient3.world::isChunkLoaded)
                  .toList()
            );
      }

      this.zClass06739 = new CooldownTimer();
      this.zClass06739.reset();
      super.onEnable();
   }

   @EventTarget
   public void ItemServiceBase(EventHookWorldRender var1) {
      BlockHitResult blockhitresult = RaycastUtils.on23(minecraftClient3.player.getCameraPosVec(1.0F), val002.LineShader(), 5.0, var0 -> true);
      if (blockhitresult.getType() != Type.MISS) {
         WorldRender.on23(new Box(blockhitresult.getBlockPos()), Color.RED.getRGB(), 1.0F);
      }

      if (!this.linkedHashSet2.isEmpty()) {
         WorldRender.on23(new Box(this.linkedHashSet2.getFirst()), Color.GREEN.getRGB(), 1.0F);
         WorldRender.on23(
            minecraftClient3.player
               .getBoundingBox()
               .expand(this.range3.getCurrent(), 0.0, this.range3.getCurrent())
               .withMaxY(minecraftClient3.player.getEntityPos().y + this.height.getCurrent()),
            ZenithClient.on23().TextScanner().getClientColor(90).call001(),
            1.0F
         );
      }
   }

   @EventTarget
   public void NbtEditor(PacketEvent var1) {
      if (var1.ItemScroller() instanceof BlockUpdateS2CPacket) {
         this.zClass06739.reset();
      }
   }

   @EventTarget
   public void PotionItemBuilder(RotationUpdateStartEvent var1) {
      Rotation ililiiili1ll1li11 = new Rotation(val002.LineShader().GrimGlide(), -90.0F);
      val002.on23(new RotationTask(ililiiili1ll1li11, () -> ililiiili1ll1li11, val002.int150().RectBatch()), 50, this);
   }

   @EventTarget(4)
   public void CloudApiClient(MovementInputEvent var1) {
      var1.NoSlow();
   }

   @EventTarget
   public void on23(EventTick var1) {
      BlockHitResult blockhitresult = RaycastUtils.on23(minecraftClient3.player.getCameraPosVec(1.0F), val002.LineShader(), 5.0, var0 -> true);
      if (blockhitresult.getType() != Type.MISS) {
         if (minecraftClient3.player.age % 80 == 0) {
            ZenithClient.on23().ConfigJsonUtil().on23("4", Text.of("Сломайте блок над  головой"), 4000L);
         }

         this.zClass06739.reset();
      } else {
         if (this.zClass06739.EventModifyMouseRotationInput(20000L)) {
            this.zClass06739.reset();
         }

         if (!this.zClass06739.EventModifyMouseRotationInput(1000L)) {
            for (int i = 0; i < this.delay6.getCurrent(); i++) {
               if (!this.linkedHashSet2.isEmpty()) {
                  BlockPos blockpos = this.linkedHashSet2.getFirst();
                  EffectEngine.on23(var1x -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.UP, var1x));
                  EffectEngine.on23(var1x -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.UP, var1x));
                  this.linkedHashSet2.removeFirst();
               }
            }
         }
      }
   }
}
