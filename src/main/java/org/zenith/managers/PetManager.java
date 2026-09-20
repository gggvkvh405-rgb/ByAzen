package org.zenith.managers;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.event.EventHookWorldRender;
import org.zenith.render.WorldRender;
import org.zenith.util.CooldownTimer;

public class PetManager {
   public BlockPos blockPos30;
   public BlockPos blockPos31;
   public final CooldownTimer zClass06744 = new CooldownTimer();

   public PetManager() {
      EventManager.register(this);
   }

   public void on23(BlockPos var1) {
      this.blockPos30 = var1;
      this.zClass06744.reset();
   }

   public void UiAnimation(BlockPos var1) {
      this.blockPos31 = var1;
      this.zClass06744.reset();
   }

   @EventTarget
   public void UiAnimation(EventHookWorldRender var1) {
      if (this.blockPos30 != null && this.blockPos31 != null && !this.zClass06744.EventModifyMouseRotationInput(5000L)) {
         WorldRender.on23(
            new Box(new Vec3d(this.blockPos30), new Vec3d(this.blockPos31)).expand(1.5, 0.0, 1.5),
            ZenithClient.on23().TextScanner().getClientColor(90).call001(),
            1.0F
         );
      } else {
         if (this.blockPos30 != null) {
            WorldRender.on23(new Box(this.blockPos30), ZenithClient.on23().TextScanner().getClientColor(90).call001(), 1.0F);
         }

         if (this.blockPos31 != null) {
            WorldRender.on23(new Box(this.blockPos31), ZenithClient.on23().TextScanner().getClientColor(90).call001(), 1.0F);
         }
      }
   }

   public void clear() {
      this.blockPos30 = null;
      this.blockPos31 = null;
   }

   public BlockPos BooleanValue() {
      return this.blockPos30;
   }

   public BlockPos BotFollowEntity() {
      return this.blockPos31;
   }

   public CooldownTimer BotEntity() {
      return this.zClass06744;
   }
}
