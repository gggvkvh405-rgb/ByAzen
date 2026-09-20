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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.MovementController;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.EventTickEnd;
import org.zenith.event.PacketEvent;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MovementUtils;

@ModuleInfo(name = "Debug", category = Category.MISC, description = "Logs packets with timestamp")
public class Debug extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Debug debug = new Debug();
   public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   public final NumberSetting messageDelay = new NumberSetting(
      "module.debug.messageDelay", 250.0F, 0.0F, 2000.0F, 50.0F, "module.debug.messageDelay.desc", "ms"
   );
   public final CooldownTimer zClass06725 = new CooldownTimer();
   public final File file2;
   public MovementController var154;
   public Vec3d vec3d7;

   public Debug() {
      File file1 = new File(MinecraftClient.getInstance().runDirectory, "zenith");
      if (!file1.exists() && !file1.mkdirs()) {
         StyledTextBuilder.RotationLegitStrategy("Debug: failed to create log directory");
      }

      this.file2 = new File(file1, "debug_packets.log");
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.vec3d7 = minecraftClient3.player.getEntityPos();
         this.var154 = MovementController.TargetAcquireEvent(1);
      } else {
         this.var154 = null;
         this.vec3d7 = null;
      }
   }

   @EventTarget
   public void UiAnimation(EventTickEnd var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && this.var154 != null && this.vec3d7 != null) {
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         Vec3d vec3d1 = this.var154.TriggerBot;
         Vec3d vec3d2 = vec3d.subtract(vec3d1);
         double d0 = Math.sqrt(vec3d2.x * vec3d2.x + vec3d2.z * vec3d2.z);
         double d1 = vec3d2.length();
         if (this.zClass06725.EventModifyMouseRotationInput((long)this.messageDelay.getCurrent())) {
            StyledTextBuilder.RefreshCacheEvent(
               String.format(
                  Locale.ROOT,
                  "Predict diff: total=%.5f h=%.5f x=%.5f y=%.5f z=%.5f | real=(%.3f %.3f %.3f) pred=(%.3f %.3f %.3f)",
                  d1,
                  d0,
                  vec3d2.x,
                  vec3d2.y,
                  vec3d2.z,
                  vec3d.x,
                  vec3d.y,
                  vec3d.z,
                  vec3d1.x,
                  vec3d1.y,
                  vec3d1.z
               )
            );
            this.zClass06725.reset();
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (minecraftClient3.player != null && var1.ItemScroller() instanceof ClickSlotC2SPacket clickslotc2spacket) {
         int i = clickslotc2spacket.slot();
         Object object = minecraftClient3.player.currentScreenHandler.isValid(i) ? minecraftClient3.player.currentScreenHandler.getSlot(i).getStack() : "";
         this.ServerConfigStore(clickslotc2spacket.actionType().name() + ": " + i + " " + MovementUtils.double64() + " " + object);
      }
   }

   public void ServerConfigStore(String var1) {
      String s = LocalTime.now().format(dateTimeFormatter);

      try (BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(this.file2, true))) {
         bufferedwriter.write(s + " | " + var1);
         bufferedwriter.newLine();
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }
   }
}
