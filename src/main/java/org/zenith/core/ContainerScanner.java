package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.zenith.ZenithClient;
import org.zenith.event.EventMouseButton;
import org.zenith.module.misc.AutoCraft;

public final class ContainerScanner {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final AutoCraft autoCraft2;
   public WaypointData zClass047 = WaypointData.call100();
   public boolean boolean185;

   public boolean Easing(EventMouseButton var1) {
      if (this.zClass047.call038()) {
         return false;
      }

      if (AutoCraft.minecraftClient3.player != null && AutoCraft.minecraftClient3.world != null) {
         if (var1.TridentAimbot() != 1 || var1.ContainerScanner() != 1) {
            return true;
         }

         if (AutoCraft.minecraftClient3.currentScreen != null) {
            return true;
         }

         if (AutoCraft.minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult
            && AutoCraft.minecraftClient3.crosshairTarget.getType() == Type.BLOCK) {
            ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.autoCraft2.CloudRouter(this.zClass047.call061(), this.zClass047.call062());
            if (iiilili1lli1i11lilillliiii1iii == null) {
               this.zClass047 = WaypointData.call100();
               this.call155();
               return true;
            }

            BlockPos blockpos = blockhitresult.getBlockPos();
            if (this.zClass047.call079() == WaypointKind.val191
               && AutoCraft.minecraftClient3.world.getBlockState(blockpos).getBlock() != Blocks.CRAFTING_TABLE) {
               this.autoCraft2.VisualSettingsStore("Этот блок не является верстаком");
               return true;
            }

            if ((this.zClass047.call079() == WaypointKind.val189 || this.zClass047.call079() == WaypointKind.val190) && !this.ItemRegistry(blockpos)) {
               this.autoCraft2.VisualSettingsStore("Этот тип сундуков пока не поддерживается");
               return true;
            }

            BlockPosEntry iili1i11ii1l1l11il = BlockPosEntry.FileLogger(blockpos);
            if (this.zClass047.call079() == WaypointKind.val189 && !this.zClass047.double127().isBlank()) {
               iiilili1lli1i11lilillliiii1iii.call034().put(this.zClass047.double127(), iili1i11ii1l1l11il);
               this.autoCraft2.call184();
               this.autoCraft2
                  .PacketSendEvent("Сундук-источник сохранен для " + iiilili1lli1i11lilillliiii1iii.on23(this.zClass047.double127(), this.autoCraft2));
            } else if (this.zClass047.call079() == WaypointKind.val190) {
               iiilili1lli1i11lilillliiii1iii.on23(iili1i11ii1l1l11il);
               this.autoCraft2.PacketSendEvent("Сундук-склад сохранен для " + iiilili1lli1i11lilillliiii1iii.getDisplayName());
            } else if (this.zClass047.call079() == WaypointKind.val191) {
               iiilili1lli1i11lilillliiii1iii.UiAnimation(iili1i11ii1l1l11il);
               this.autoCraft2.PacketSendEvent("Верстак сохранен для " + iiilili1lli1i11lilillliiii1iii.getDisplayName());
            }

            this.zClass047 = WaypointData.call100();
            this.call155();
            ZenithClient.on23().TradeGuardService().save();
            return true;
         } else {
            this.autoCraft2.VisualSettingsStore("Наведитесь на блок для привязки Автокрафта");
            return true;
         }
      } else {
         return true;
      }
   }

   public ContainerScanner(AutoCraft var1) {
      this.autoCraft2 = var1;
   }

   public boolean call149() {
      return !this.zClass047.call038();
   }

   public String call116() {
      return this.zClass047.call038() ? "" : this.zClass047.Easing(this.autoCraft2);
   }

   public void call240() {
      this.zClass047 = WaypointData.call100();
   }

   public void reset() {
      this.zClass047 = WaypointData.call100();
      this.EmoteMetadata(true);
   }

   public void Event18Ext3(String var1) {
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.autoCraft2.call086();
      if (iiilili1lli1i11lilillliiii1iii != null && var1 != null && !var1.isBlank()) {
         this.zClass047 = new WaypointData(WaypointKind.val189, iiilili1lli1i11lilillliiii1iii.string112(), iiilili1lli1i11lilillliiii1iii.getId(), var1);
         this.call117();
         AutoCraft.minecraftClient3.setScreen(null);
      }
   }

   public void string89() {
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.autoCraft2.call086();
      if (iiilili1lli1i11lilillliiii1iii != null) {
         this.zClass047 = new WaypointData(WaypointKind.val190, iiilili1lli1i11lilillliiii1iii.string112(), iiilili1lli1i11lilillliiii1iii.getId(), "");
         this.call117();
         AutoCraft.minecraftClient3.setScreen(null);
      }
   }

   public void path6() {
      ItemFilterRules iiilili1lli1i11lilillliiii1iii = this.autoCraft2.call086();
      if (iiilili1lli1i11lilillliiii1iii != null) {
         this.zClass047 = new WaypointData(WaypointKind.val191, iiilili1lli1i11lilillliiii1iii.string112(), iiilili1lli1i11lilillliiii1iii.getId(), "");
         this.call117();
         AutoCraft.minecraftClient3.setScreen(null);
      }
   }

   public void call117() {
      if (!this.boolean185 && !this.autoCraft2.isEnabledRaw()) {
         EventManager.register(this.autoCraft2);
         this.boolean185 = true;
      }
   }

   public void call155() {
      this.EmoteMetadata(false);
   }

   public void EmoteMetadata(boolean var1) {
      if (this.boolean185) {
         if (!var1 && this.autoCraft2.isEnabledRaw()) {
            this.boolean185 = false;
         } else {
            EventManager.unregister(this.autoCraft2);
            this.boolean185 = false;
         }
      }
   }

   public boolean ItemRegistry(BlockPos var1) {
      BlockEntity blockentity = AutoCraft.minecraftClient3.world.getBlockEntity(var1);
      return blockentity instanceof ChestBlockEntity || blockentity instanceof BarrelBlockEntity || blockentity instanceof ShulkerBoxBlockEntity;
   }
}
