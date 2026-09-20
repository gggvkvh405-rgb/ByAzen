package org.zenith.rotation;


import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.TargetAcquireEvent;
import org.zenith.event.EventImpl;
import org.zenith.event.EventInjectHandleInputEvents;
import org.zenith.event.PacketEvent;
import org.zenith.module.Module;

public class RotationManager {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public Rotation currentRotation = new Rotation(0.0F, 0.0F);
   public Rotation var11812 = new Rotation(0.0F, 0.0F);
   public final RotationQueue<RotationTask> zClass110 = new RotationQueue<>();
   public final RotationEasing zClass013 = new RotationEasing();
   public RotationTask var162 = new RotationTask(this.currentRotation, () -> this.currentRotation, this.zClass013.HudPreviewItem());
   public Module module4 = null;
   public Rotation var11813 = Rotation.var1189;
   public int int391 = 0;
   public final RotationManager.RotationFilter zClass007Var159;

   public Rotation LineShader() {
      return this.currentRotation == null
         ? new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch(), true)
         : this.currentRotation;
   }

   public boolean Var05() {
      return this.currentRotation == null;
   }

   public RotationManager() {
      this(EventManager::call, true);
   }

   protected RotationManager(RotationManager.RotationFilter var1, boolean var2) {
      this.zClass007Var159 = var1;
      if (var2) {
         EventManager.register(this);
      }
   }

   @EventTarget(3)
   public void UiAnimation(EventInjectHandleInputEvents var1) {
      if (minecraftClient3.player != null) {
         if (minecraftClient3.player.isDead()) {
            if (this.currentRotation != null) {
               minecraftClient3.player
                  .setYaw(
                     this.currentRotation.GrimGlide() + MathHelper.wrapDegrees(minecraftClient3.player.getYaw() - this.currentRotation.GrimGlide())
                  );
               minecraftClient3.player.renderYaw = minecraftClient3.player.getYaw();
               minecraftClient3.player.lastRenderYaw = minecraftClient3.player.getYaw();
               minecraftClient3.player.renderPitch = minecraftClient3.player.getPitch();
               minecraftClient3.player.lastRenderPitch = minecraftClient3.player.getPitch();
               this.int391 = 2;
               this.currentRotation = null;
            }
         } else {
            this.zClass007Var159.call(new RotationUpdateStartEvent());
            Rotation ililiiili1ll1li11 = new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch(), true);
            RotationQueue.QueueEntry lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil = this.zClass110.PositionProvider();
            if (lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil != null) {
               RotationTask ll1lii1ii1l11ii11lil111lili11 = (RotationTask)lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.call263;
               Rotation ililiiili1ll1li111 = ll1lii1ii1l11ii11lil111lili11.long104().get();
               this.var11812 = this.currentRotation == null ? ililiiili1ll1li11 : this.currentRotation;
               this.currentRotation = ililiiili1ll1li111;
               this.var162 = ll1lii1ii1l11ii11lil111lili11;
               this.module4 = lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.module3;
               this.currentRotation = new Rotation(this.currentRotation.GrimGlide(), MathHelper.clamp(this.currentRotation.GuiWalk(), -90.0F, 90.0F));
               this.int391--;
            } else if (this.currentRotation != null) {
               this.module4 = null;
               Rotation ililiiili1ll1li112 = this.zClass013.on23(this.var162.long103(), ililiiili1ll1li11);
               this.var11812 = this.currentRotation;
               this.currentRotation = ililiiili1ll1li112;
               if (ililiiili1ll1li11.EmoteManager(ililiiili1ll1li112).EventMotion(9.0F)) {
                  minecraftClient3.player
                     .setYaw(
                        ililiiili1ll1li112.GrimGlide() + MathHelper.wrapDegrees(minecraftClient3.player.getYaw() - ililiiili1ll1li112.GrimGlide())
                     );
                  minecraftClient3.player.renderYaw = minecraftClient3.player.getYaw();
                  minecraftClient3.player.lastRenderYaw = minecraftClient3.player.getYaw();
                  minecraftClient3.player.renderPitch = minecraftClient3.player.getPitch();
                  minecraftClient3.player.lastRenderPitch = minecraftClient3.player.getPitch();
                  this.int391 = 2;
                  this.currentRotation = null;
               } else {
                  this.currentRotation = new Rotation(this.currentRotation.GrimGlide(), MathHelper.clamp(this.currentRotation.GuiWalk(), -90.0F, 90.0F));
               }
            }

            this.zClass110.tick();
            minecraftClient3.gameRenderer.updateCrosshairTarget(1.0F);
            this.zClass007Var159.call(new TargetAcquireEvent());
            if (this.currentRotation != null && this.var11812 != null) {
            }
         }
      }
   }

   @EventTarget
   public void UiAnimation(PacketEvent var1) {
      Packet packet = var1.ItemScroller();
      Objects.requireNonNull(packet);
      Object object = packet;
      switch (object) {
         case PlayerMoveC2SPacket playermovec2spacket:
            if (playermovec2spacket.getYaw(2.1474836E9F) != 2.1474836E9F) {
               EventImpl l1il1i1i1i1i1l1l1lli111ix = new EventImpl(
                  this.currentRotation != null
                     ? new Rotation(this.var11813.GrimGlide(), this.var11813.GuiWalk(), true)
                     : new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch(), true)
               );
               this.var11813 = new Rotation(playermovec2spacket.getYaw(0.0F), playermovec2spacket.getPitch(0.0F), true);
               this.zClass007Var159.call(l1il1i1i1i1i1l1l1lli111ix);
               this.int391--;
            }
            break;
         case PlayerPositionLookS2CPacket playerpositionlooks2cpacket:
            if (minecraftClient3.player != null) {
               EventImpl l1il1i1i1i1i1l1l1lli111ix = new EventImpl(
                  this.currentRotation != null
                     ? new Rotation(this.var11813.GrimGlide(), this.var11813.GuiWalk(), true)
                     : new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch(), true)
               );
               this.var11813 = new Rotation(playerpositionlooks2cpacket.change().yaw(), playerpositionlooks2cpacket.change().pitch(), true);
               this.zClass007Var159.call(l1il1i1i1i1i1l1l1lli111ix);
               this.int391--;
            }
            break;
         case CommonPongC2SPacket commonpongc2spacket:
            commonpongc2spacket.parameter = commonpongc2spacket.parameter;
            break;
         case PlayerInteractItemC2SPacket playerinteractitemc2spacket:
            EventImpl l1il1i1i1i1i1l1l1lli111i = new EventImpl(new Rotation(this.var11813.GrimGlide(), this.var11813.GuiWalk(), true));
            this.var11813 = new Rotation(playerinteractitemc2spacket.getYaw(), playerinteractitemc2spacket.getPitch(), true);
            this.zClass007Var159.call(l1il1i1i1i1i1l1l1lli111i);
            break;
         case ScreenHandlerSlotUpdateS2CPacket screenhandlerslotupdates2cpacket:
            break;
         case InventoryS2CPacket inventorys2cpacket:
            break;
         case ClickSlotC2SPacket clickslotc2spacket:
            clickslotc2spacket.revision = minecraftClient3.player.currentScreenHandler.getRevision();
            break;
         default:
      }
   }

   public void on23(RotationTask var1, int var2, Module var3, int var4) {
      this.zClass110.on23(new RotationQueue.QueueEntry<>(var4, var2, var3, var1));
   }

   public void on23(RotationTask var1, int var2, Module var3) {
      this.zClass110.on23(new RotationQueue.QueueEntry<>(1, var2, var3, var1));
   }

   public Rotation ZClass092() {
      return this.currentRotation;
   }

   public Rotation ZClass018() {
      return this.var11812;
   }

   public RotationQueue<RotationTask> long107() {
      return this.zClass110;
   }

   public RotationEasing int150() {
      return this.zClass013;
   }

   public RotationTask boolean111() {
      return this.var162;
   }

   public Module list49() {
      return this.module4;
   }

   public Rotation list50() {
      return this.var11813;
   }

   public int list51() {
      return this.int391;
   }

   public RotationManager.RotationFilter string43() {
      return this.zClass007Var159;
   }

   public void UiAnimation(Rotation var1) {
      this.currentRotation = var1;
   }


   @FunctionalInterface
   public interface RotationFilter {
      void call(Event var1);
   }
}
