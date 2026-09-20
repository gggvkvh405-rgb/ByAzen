package org.zenith.base.bot.world;

import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.EntityHandler;

final class BotWorld_BotEntityHandler implements EntityHandler<Entity> {
   public final BotWorld this_0;

   public BotWorld_BotEntityHandler(BotWorld var1) {
      this.this_0 = var1;
   }

   @Override
   public void create(Entity var1) {
   }

   @Override
   public void destroy(Entity var1) {
   }

   @Override
   public void startTicking(Entity var1) {
      this.this_0.entityList.add(var1);
   }

   @Override
   public void stopTicking(Entity var1) {
      this.this_0.entityList.remove(var1);
   }

   @Override
   public void startTracking(Entity var1) {
      if (var1 instanceof PlayerEntity playerentity) {
         this.this_0.players.add(playerentity);
      } else if (var1 instanceof EnderDragonEntity enderdragonentity) {
         this.this_0.enderDragonParts.addAll(Arrays.asList(enderdragonentity.getBodyParts()));
      }
   }

   @Override
   public void stopTracking(Entity var1) {
      var1.detach();
      if (var1 instanceof PlayerEntity playerentity) {
         this.this_0.players.remove(playerentity);
      } else if (var1 instanceof EnderDragonEntity enderdragonentity) {
         this.this_0.enderDragonParts.removeAll(Arrays.asList(enderdragonentity.getBodyParts()));
      }
   }

   @Override
   public void updateLoadStatus(Entity var1) {
   }
}
