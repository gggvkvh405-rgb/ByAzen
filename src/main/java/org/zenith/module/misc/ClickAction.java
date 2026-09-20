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


import net.minecraft.item.Item;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.zenith.ZenithClient;
import org.zenith.core.BooleanValue;
import org.zenith.core.EffectEngine;
import org.zenith.core.MovementController;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.event.RefreshCacheEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.Setting;
import org.zenith.setting.KeySetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "ClickAction", description = "Делает что то по бинду", category = Category.MISC)
public final class ClickAction extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final KeySetting friendBind = new KeySetting("module.clickAction.friendBind", "module.clickAction.friendBind.desc");
   public final KeySetting expBind = new KeySetting("module.clickAction.expBind", "module.clickAction.expBind.desc");
   public final List<ClickAction.ItemAction> list12 = new ArrayList<>();
   public final CooldownTimer zClass06721 = new CooldownTimer();
   public static final ClickAction clickAction = new ClickAction();
   public Slot slot2 = null;
   public boolean boolean44 = false;

   public ClickAction() {
      this.list12
         .add(
            new ClickAction.ItemAction(
               Items.ENDER_PEARL, new KeySetting("module.clickAction.enderPearl", "module.clickAction.enderPearl.desc"), new BooleanValue()
            )
         );
      this.list12
         .add(
            new ClickAction.ItemAction(
               Items.WIND_CHARGE, new KeySetting("module.clickAction.windCharge", "module.clickAction.windCharge.desc"), new BooleanValue()
            )
         );
   }

   @Override
   public List<Setting> getSettings() {
      List<Setting> arraylist = new ArrayList<>();
      arraylist.add(this.expBind);
      arraylist.add(this.friendBind);
      arraylist.addAll(this.list12.stream().map(ClickAction.ItemAction::double22).toList());
      return arraylist;
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (var1.ItemRegistry(this.friendBind.getKeyCode()) && minecraftClient3.player != null && minecraftClient3.world != null) {
         EntityHitResult entityhitresult = RaycastUtils.on23(
            new Rotation(minecraftClient3.player.getYaw(), minecraftClient3.player.getPitch()),
            var0 -> var0 instanceof PlayerEntity && var0 != minecraftClient3.player
         );
         if (entityhitresult != null && entityhitresult.getEntity() instanceof PlayerEntity playerentity) {
            String s = playerentity.getGameProfile().name();
            if (ZenithClient.on23().MediaTrackInfo().getItems().contains(s)) {
               ZenithClient.on23().MediaTrackInfo().ItemServiceBase(s);
            } else {
               ZenithClient.on23().MediaTrackInfo().add(s);
            }

            ZenithClient.on23().MediaTrackInfo().save();
         }
      }

      this.list12
         .stream()
         .filter(var1xx -> var1.ItemRegistry(var1xx.stringSetting22().getKeyCode()) && ScreenUtils.SimpleItemBuilder(var1xx.item5()) != null)
         .forEach(var0 -> var0.var42().setValue(true));
      this.list12.stream().filter(var1xx -> var1.ItemSpec(var1xx.stringSetting22().getKeyCode())).forEach(var0 -> {
         ScreenUtils.ItemServiceBase(var0.item5());
         var0.var42().setValue(false);
      });
      if (var1.ItemRegistry(this.expBind.getKeyCode())) {
         Slot slot = ScreenUtils.SimpleItemBuilder(Items.EXPERIENCE_BOTTLE);
         if (slot == null) {
            ZenithClient.on23()
               .ConfigJsonUtil()
               .on23(
                  "M",
                  Text.of(
                     Items.EXPERIENCE_BOTTLE
                        .getName()
                        .copy()
                        .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                        .append(
                           Text.of("не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
                        )
                  )
               );
            return;
         }
      }
   }

   @EventTarget
   public void ColorAnimator(EventHookWorldRender var1) {
      Predictions.predictions
         .on23(var1.ClanUpgrade(), this.list12.stream().filter(var0 -> var0.var42().isValue()).map(var0 -> var0.item5().getDefaultStack()).toList());
   }

   @EventTarget(4)
   public void Easing(RefreshCacheEvent var1) {
      if (!var1.isCancelled() && minecraftClient3.player != null) {
         if (this.boolean44) {
            this.boolean44 = false;
         } else {
            boolean flag = minecraftClient3.player.getMainHandStack().getItem().equals(Items.EXPERIENCE_BOTTLE);
            Slot slot = ScreenUtils.SimpleItemBuilder(Items.EXPERIENCE_BOTTLE);
            if (EffectEngine.on23(this.expBind) && slot != null) {
               MovementController il11i11i111i1i1l1il = MovementController.TargetAcquireEvent(3);
               Rotation ililiiili1ll1li11 = new Rotation(
                  minecraftClient3.player.getYaw(), RotationMath.BotChatEvent(il11i11i111i1i1l1il.box9.getCenter()).GuiWalk()
               );
               val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11), val001.HudPreviewItem()), 5, this);
               if (!flag) {
                  if (TaskScheduler.Easing(ClickAction.class)) {
                     TaskScheduler.on23(ClickAction.class, () -> {
                        if (this.slot2 == null) {
                           this.slot2 = slot;
                        }

                        ScreenUtils.on23(slot, Hand.MAIN_HAND, true);
                        this.boolean44 = true;
                     });
                  }
               } else if (this.zClass06721.EventModifyMouseRotationInput(70L) && val002.LineShader().EmoteManager(ililiiili1ll1li11).BotChatEvent(180.0F, 10.0F)) {
                  EffectEngine.useItem(Hand.MAIN_HAND);
                  var1.cancel();
                  this.zClass06721.reset();
               }
            } else if (this.slot2 != null) {
               TaskScheduler.on23(ClickAction.class, () -> {
                  if (!EffectEngine.on23(this.expBind)) {
                     ScreenUtils.on23(this.slot2, Hand.MAIN_HAND, true);
                     this.slot2 = null;
                  }
               });
            }
         }
      }
   }

   public List<ClickAction.ItemAction> double18() {
      return Collections.unmodifiableList(this.list12);
   }

   public KeySetting double19() {
      return this.expBind;
   }


   public record ItemAction(Item item5, KeySetting stringSetting22, BooleanValue var42) {
      public Item double21() {
         return this.item5;
      }

      public KeySetting double22() {
         return this.stringSetting22;
      }

      public BooleanValue double23() {
         return this.var42;
      }
   }
}
