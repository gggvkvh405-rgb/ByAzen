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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import org.zenith.base.font.Fonts;
import org.zenith.core.EffectEngine;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.EventTick;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.event.MovementInputEvent;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScoreboardUtils;

@ModuleInfo(name = "PathTeleport", description = "", category = Category.MISC)
public final class PathTeleport extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final PathTeleport pathTeleport = new PathTeleport();
   public static final float float151 = 10.0F;
   public static final float float152 = 20.0F;
   public static final float float153 = 15.0F;
   public final KeySetting startStopRecording2 = new KeySetting("Start/Stop Recording", "empty", -1);
   public final KeySetting startStopMoving2 = new KeySetting("Start/Stop Moving", "empty", -1);
   public final List<PathTeleport.Snapshot> list69 = new ArrayList<>();
   public boolean boolean51 = false;
   public boolean boolean52 = false;
   public int int222 = 0;

   @Override
   public void onEnable() {
      this.boolean51 = false;
      this.boolean52 = false;
      this.int222 = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.boolean51 = false;
      this.boolean52 = false;
      this.int222 = 0;
      super.onDisable();
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (minecraftClient3.player != null) {
         if (var1.ItemRegistry(this.startStopRecording2.getKeyCode())) {
            this.call172();
         }

         if (var1.ItemRegistry(this.startStopMoving2.getKeyCode())) {
            this.call173();
         }
      }
   }

   @EventTarget
   public void onMoveInput(MovementInputEvent var1) {
      if (this.boolean52) {
         var1.NoSlow();
      }
   }

   @EventTarget
   public void on23(EventRenderScreenHook var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.on23(var1, "Recording " + this.boolean51 + "  " + ScoreboardUtils.EventPosHook(this.startStopRecording2.getKeyCode()), 20.0F);
         this.on23(var1, "Moving " + this.boolean52 + "  " + ScoreboardUtils.EventPosHook(this.startStopMoving2.getKeyCode()), 35.0F);
         this.on23(var1, "Points " + this.list69.size(), 50.0F);
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.boolean51) {
            this.call135();
         } else if (!this.boolean52) {
            this.int222 = 0;
         } else if (this.list69.isEmpty()) {
            this.boolean52 = false;
            this.int222 = 0;
         } else if (this.int222 >= this.list69.size()) {
            this.boolean52 = false;
            this.int222 = 0;
         } else {
            this.on23(this.list69.get(this.int222));
            this.int222++;
         }
      }
   }

   public void call172() {
      this.boolean51 = !this.boolean51;
      if (this.boolean51) {
         this.boolean52 = false;
         this.int222 = 0;
         this.list69.clear();
         this.call135();
      }
   }

   public void call173() {
      if (this.boolean52) {
         this.boolean52 = false;
         this.int222 = 0;
      } else if (this.list69.isEmpty()) {
         StyledTextBuilder.RotationLegitStrategy("PathTeleport: сначала запиши маршрут");
      } else {
         this.boolean51 = false;
         this.boolean52 = true;
         this.int222 = 0;
      }
   }

   public void call135() {
      PathTeleport.Snapshot ilil1lli1ii11li111lil1_ii1il11l111ii11iil = new PathTeleport.Snapshot(
         minecraftClient3.player.getX(),
         minecraftClient3.player.getY(),
         minecraftClient3.player.getZ(),
         minecraftClient3.player.getYaw(),
         minecraftClient3.player.getPitch(),
         minecraftClient3.player.isOnGround()
      );
      this.list69.add(ilil1lli1ii11li111lil1_ii1il11l111ii11iil);
   }

   public boolean on23(PathTeleport.Snapshot var1, PathTeleport.Snapshot var2) {
      return Math.abs(var1.double32() - var2.double32()) > 0.001
         || Math.abs(var1.double33() - var2.double33()) > 0.001
         || Math.abs(var1.double34() - var2.double34()) > 0.001
         || Math.abs(var1.float69() - var2.float69()) > 0.05F
         || Math.abs(var1.float70() - var2.float70()) > 0.05F
         || var1.boolean82() != var2.boolean82();
   }

   public void on23(PathTeleport.Snapshot var1) {
      minecraftClient3.player.setVelocity(0.0, 0.0, 0.0);
      minecraftClient3.player.setOnGround(var1.boolean82());
      minecraftClient3.player.setPosition(var1.double32(), var1.double33(), var1.double34());
      EffectEngine.ItemServiceBase(
         new Full(
            var1.double32(), var1.double33(), var1.double34(), var1.float69(), var1.float70(), var1.boolean82(), minecraftClient3.player.horizontalCollision
         )
      );
   }

   public void on23(EventRenderScreenHook var1, String var2, float var3) {
      float f = minecraftClient3.getWindow().getScaledWidth() - Fonts.NEW_MEDIUM.getWidth(var2, 10.0F) - 20.0F;
      var1.WarpFarm().drawText(Fonts.NEW_MEDIUM.getFont(10.0F), var2, f, var3, ArgbColor.var11934);
   }


   public record Snapshot(double double32, double double33, double double34, float float69, float float70, boolean boolean82) {
      public double x() {
         return this.double32;
      }

      public double y() {
         return this.double33;
      }

      public double z() {
         return this.double34;
      }

      public float yaw() {
         return this.float69;
      }

      public float pitch() {
         return this.float70;
      }

      public boolean onGround() {
         return this.boolean82;
      }
   }
}
