package org.zenith.client.screens.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ImplOtherClientPlayerEntity extends OtherClientPlayerEntity {
   public ImplOtherClientPlayerEntity(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean canHit() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void pushAwayFrom(Entity entity) {
   }

   protected void pushAway(Entity entity) {
   }

   public boolean canBeHitByProjectile() {
      return false;
   }
}
