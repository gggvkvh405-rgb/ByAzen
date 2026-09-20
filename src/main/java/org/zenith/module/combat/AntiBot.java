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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "AntiBot", category = Category.COMBAT, description = "")
public final class AntiBot extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AntiBot antiBot = new AntiBot();
   public final List<PlayerEntity> list = new ArrayList<>();
   public final CooldownTimer zClass0673 = new CooldownTimer();

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows()
         && minecraftClient3.world != null
         && var1.ItemScroller() instanceof EntitySpawnS2CPacket entityspawns2cpacket
         && entityspawns2cpacket.getEntityType() == EntityType.PLAYER) {
         minecraftClient3.execute(
            () -> {
               if (minecraftClient3.world != null
                  && minecraftClient3.world.getEntityById(entityspawns2cpacket.getEntityId()) instanceof PlayerEntity playerentity) {
                  StyledTextBuilder.RefreshCacheEvent(playerentity.getGameProfile().name());
               }
            }
         );
      }
   }

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (this.zClass0673.EventModifyMouseRotationInput(10000L) && !this.list.isEmpty()) {
            this.list.clear();
            this.zClass0673.reset();
         }

         for (PlayerEntity playerentity : minecraftClient3.world.getPlayers()) {
            if (playerentity != null && playerentity != minecraftClient3.player && this.ItemRegistry(playerentity) && !this.list.contains(playerentity)) {
               this.list.add(playerentity);
            }
         }
      }
   }

   public boolean ItemRegistry(PlayerEntity var1) {
      return this.UiAnimation(var1, 3).getItem() == Items.LEATHER_HELMET && this.Easing(var1, 3) && !this.UiAnimation(var1, 3).hasEnchantments()
         || this.UiAnimation(var1, 2).getItem() == Items.LEATHER_CHESTPLATE && this.Easing(var1, 2) && !this.UiAnimation(var1, 2).hasEnchantments()
         || this.UiAnimation(var1, 1).getItem() == Items.LEATHER_LEGGINGS && this.Easing(var1, 1) && !this.UiAnimation(var1, 1).hasEnchantments()
         || this.UiAnimation(var1, 0).getItem() == Items.LEATHER_BOOTS && this.Easing(var1, 0) && !this.UiAnimation(var1, 0).hasEnchantments()
         || this.UiAnimation(var1, 2).getItem() == Items.IRON_CHESTPLATE && !this.UiAnimation(var1, 2).hasEnchantments()
         || this.UiAnimation(var1, 1).getItem() == Items.IRON_LEGGINGS && !this.UiAnimation(var1, 1).hasEnchantments();
   }

   public ItemStack UiAnimation(PlayerEntity var1, int var2) {
      return var1.getInventory().getStack(36 + var2);
   }

   public boolean Easing(PlayerEntity var1, int var2) {
      return !this.UiAnimation(var1, var2).contains(DataComponentTypes.DYED_COLOR);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (!this.list.isEmpty()) {
         this.list.clear();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (!this.list.isEmpty()) {
         this.list.clear();
      }
   }

   public boolean ItemSpec(PlayerEntity var1) {
      return this.list.contains(var1);
   }
}
