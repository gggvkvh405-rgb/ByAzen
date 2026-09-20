package org.zenith.module.movement;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.PacketEvent;
import org.zenith.event.PlayerMoveEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.NumberSetting;
import org.zenith.util.RaycastUtils;

@ModuleInfo(name = "WallBypass", description = "", category = Category.MOVEMENT)
public final class WallBypass extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final WallBypass wallBypass = new WallBypass();
   public static final double double111 = 3.0;
   public static final int int383 = 70;
   public static final int[] val518 = new int[0];
   public final NumberSetting attackDistance = new NumberSetting(
      "module.wallBypass.attackDistance", 3.0F, 3.0F, 6.0F, 0.1F, "module.wallBypass.attackDistance.desc", "b"
   );
   public Rotation var11811;
   public boolean boolean167;
   public boolean boolean168;
   public int int384;

   @Override
   public void onEnable() {
      this.var11811 = this.float35();
      this.boolean167 = this.var11811 != null;
      this.boolean168 = false;
      this.int384 = 1;
      if (!this.boolean167) {
         this.setToggled(false);
      } else {
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.var11811 = null;
      this.boolean167 = false;
      this.boolean168 = false;
      this.int384 = 0;
      super.onDisable();
   }

   @EventTarget
   public void on23(PlayerMoveEvent var1) {
      if (this.int384 <= 0) {
         var1.on23(Vec3d.ZERO);
      }
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (!this.boolean167 || this.var11811 == null || minecraftClient3.player == null || minecraftClient3.world == null) {
         this.setToggled(false);
      } else if (!this.boolean168) {
         Rotation ililiiili1ll1li11 = this.var11811;
         val002.on23(new RotationTask(ililiiili1ll1li11, () -> ililiiili1ll1li11, val001.HudPreviewItem()), 70, this, 1);
         this.boolean168 = true;
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.ItemScroller() instanceof PlayerMoveC2SPacket) {
         if (this.int384 > 0) {
            if (this.boolean168) {
               this.int384--;
            }

            return;
         }

         var1.setCancelled(true);
      }
   }

   public boolean float34() {
      return this.isEnabled() && this.boolean167 && this.var11811 != null && this.int384 <= 0;
   }

   public float var1356() {
      return this.attackDistance.getCurrent();
   }

   public Rotation float35() {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         Rotation ililiiili1ll1li11 = val002.LineShader();
         float f = ililiiili1ll1li11.GrimGlide();
         Rotation[] aililiiili1ll1li11 = new Rotation[]{
            new Rotation(f, -90.0F),
            new Rotation(f, 90.0F),
            new Rotation(f, 0.0F),
            new Rotation(minecraftClient3.player.getYaw(), -90.0F),
            new Rotation(minecraftClient3.player.getYaw(), 90.0F),
            new Rotation(minecraftClient3.player.getYaw(), 0.0F)
         };

         for (Rotation ililiiili1ll1li111 : aililiiili1ll1li11) {
            if (this.ProtocolMessage(ililiiili1ll1li111)) {
               return ililiiili1ll1li111;
            }
         }

         for (int i : val518) {
            Rotation ililiiili1ll1li112 = this.ColorAnimator(f, i);
            if (ililiiili1ll1li112 != null) {
               return ililiiili1ll1li112;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public Rotation ColorAnimator(float var1, int var2) {
      int i = -90;

      while (i <= 90) {
         for (int j = 0; j < 360; j += var2) {
            Rotation ililiiili1ll1li11 = new Rotation(var1 + j, i);
            if (this.ProtocolMessage(ililiiili1ll1li11)) {
               return ililiiili1ll1li11;
            }

            if (j != 0) {
               Rotation ililiiili1ll1li111 = new Rotation(var1 - j, i);
               if (this.ProtocolMessage(ililiiili1ll1li111)) {
                  return ililiiili1ll1li111;
               }
            }
         }

         i += var2;
      }

      return null;
   }

   public boolean ProtocolMessage(Rotation var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         BlockHitResult blockhitresult = RaycastUtils.on23(minecraftClient3.player.getCameraPosVec(1.0F), var1, 3.0, var0 -> true);
         return blockhitresult != null
            && blockhitresult.getType() == Type.MISS
            && EffectEngine.EventMouseButton(BlockPos.ofFloored(blockhitresult.getPos()));
      } else {
         return false;
      }
   }
}
