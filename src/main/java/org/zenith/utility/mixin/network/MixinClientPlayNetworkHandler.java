package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.ZenithClient;
import org.zenith.core.TranslationKey;
import org.zenith.event.ChatMessageEvent;
import org.zenith.event.ChatMessageEvent;
import org.zenith.event.ChatMessageEvent;
import org.zenith.event.EventUpdateHealth;
import org.zenith.event.HealthUpdateEvent;
import org.zenith.event.GameMessageEvent;
import org.zenith.rotation.Rotation;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
   @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
   public void sendChatMessageHook(@NotNull String var1, CallbackInfo var2) {
      if (var1.startsWith(ZenithClient.on23().CloudResponse().getPrefix())) {
         try {
            ZenithClient.on23()
               .CloudResponse()
               .getDispatcher()
               .execute(var1.substring(ZenithClient.on23().CloudResponse().getPrefix().length()), ZenithClient.on23().CloudResponse().getSource());
         } catch (CommandSyntaxException var4) {
         }

         var2.cancel();
      } else {
         ChatMessageEvent l1iiliili1iiii1l1i1li = new ChatMessageEvent(var1, ChatMessageEvent.Source.call264, ChatMessageEvent.Direction.call270);
         EventManager.call(l1iiliili1iiii1l1i1li);
         if (l1iiliili1iiii1l1i1li.isCancelled()) {
            var2.cancel();
         }
      }
   }

   @Inject(method = "sendChatMessage", at = @At("RETURN"))
   public void sendChatMessageHookPost(@NotNull String var1, CallbackInfo var2) {
      ChatMessageEvent l1iiliili1iiii1l1i1li = new ChatMessageEvent(var1, ChatMessageEvent.Source.call205, ChatMessageEvent.Direction.call270);
      EventManager.call(l1iiliili1iiii1l1i1li);
   }

   @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
   public void sendChatCommandMessageHook(@NotNull String var1, CallbackInfo var2) {
      ChatMessageEvent l1iiliili1iiii1l1i1li = new ChatMessageEvent(var1, ChatMessageEvent.Source.call264, ChatMessageEvent.Direction.call206);
      EventManager.call(l1iiliili1iiii1l1i1li);
      if (l1iiliili1iiii1l1i1li.isCancelled()) {
         var2.cancel();
      }
   }

   @Inject(method = "sendChatMessage", at = @At("RETURN"))
   public void sendChatCommandHookPost(@NotNull String var1, CallbackInfo var2) {
      ChatMessageEvent l1iiliili1iiii1l1i1li = new ChatMessageEvent(var1, ChatMessageEvent.Source.call205, ChatMessageEvent.Direction.call206);
      EventManager.call(l1iiliili1iiii1l1i1li);
   }

   @Inject(
      method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"),
      cancellable = true
   )
   public void onGameMessage(GameMessageS2CPacket var1, CallbackInfo var2) {
      GameMessageEvent l1l11ii1iiillilll = new GameMessageEvent(var1.content());
      EventManager.call(l1l11ii1iiillilll);
      if (l1l11ii1iiillilll.isCancelled()) {
         var2.cancel();
         if (l1l11ii1iiillilll.XrayBypass()) {
            MinecraftClient.getInstance().getMessageHandler().onGameMessage(l1l11ii1iiillilll.InventorySetting(), var1.overlay());
         }
      }
   }

   @Inject(method = "onEntityStatus", at = @At("RETURN"))
   public void updateHealth(EntityStatusS2CPacket var1, CallbackInfo var2) {
      EventManager.call(new EventUpdateHealth(var1));
   }

   @Inject(method = "<init>", at = @At("RETURN"))
   public void init(MinecraftClient var1, ClientConnection var2, ClientConnectionState var3, CallbackInfo var4) {
      TranslationKey.val158.clear();
   }

   @Inject(method = "onHealthUpdate", at = @At("RETURN"))
   public void updateHealth(HealthUpdateS2CPacket var1, CallbackInfo var2) {
      EventManager.call(new HealthUpdateEvent());
   }

   @ModifyExpressionValue(
      method = {"onPlayerPositionLook", "onPlayerRotation"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F")
   )
   public float hookSilentRotationYaw(float var1) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.GrimGlide();
   }

   @ModifyExpressionValue(
      method = {"onPlayerPositionLook", "onPlayerRotation"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F")
   )
   public float hookSilentRotationPitch(float var1) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.GuiWalk();
   }
}
