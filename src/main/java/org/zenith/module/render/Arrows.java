package org.zenith.module.render;

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
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.core.Easing;
import org.zenith.core.EffectEngine;
import org.zenith.core.UiAnimation;
import org.zenith.event.HudRenderEvent;
import org.zenith.event.EventTick;
import org.zenith.managers.FriendFilter;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.GradientRadius;

@ModuleInfo(name = "Arrows", description = "Arrows", category = Category.RENDER)
public final class Arrows extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final Identifier identifier = ZenithClient.on23("textures/arrows.png");
   public final UiAnimation var143 = new UiAnimation(150L, 12.0F, Easing.StopUsingItemEvent);
   public final NumberSetting radiusSetting = new NumberSetting(
      "module.arrows.radiusSetting", 50.0F, 30.0F, 100.0F, 10.0F, "module.arrows.radiusSetting.desc", "b"
   );
   public final NumberSetting sizeSetting = new NumberSetting("module.arrows.sizeSetting", 16.0F, 8.0F, 20.0F, 1.0F, "module.arrows.sizeSetting.desc", "px");
   public static final Arrows arrows = new Arrows();
   public final BooleanSetting ignoreGol = new BooleanSetting("module.arrows.ignoreGol", "module.arrows.ignoreGol.desc", true);

   @EventTarget
   public void UiAnimation(EventTick var1) {
   }

   @EventTarget
   public void onDraw(HudRenderEvent var1) {
      MatrixStack matrixstack = org.zenith.render.GuiMatrixAdapter.toMatrixStack(var1.Bot().getMatrices());
      List<AbstractClientPlayerEntity> list = Objects.requireNonNull(minecraftClient3.world)
         .getPlayers()
         .stream()
         .filter(
            var1x -> var1x != minecraftClient3.player
               && !FriendFilter.PotionItemBuilder(var1x.getId())
               && (!this.ignoreGol.isEnabled() || EffectEngine.ItemServiceBase(var1x) != 0.0F)
         )
         .toList();
      float f = minecraftClient3.getWindow().getScaledWidth() / 2.0F;
      float f1 = minecraftClient3.getWindow().getScaledHeight() / 2.0F;
      float f2 = f1
         - this.radiusSetting.getCurrent()
         - this.var143
            .on23(
               minecraftClient3.currentScreen instanceof InventoryScreen
                  ? 80.0F
                  : (
                     minecraftClient3.currentScreen instanceof GenericContainerScreen
                        ? 100.0F
                        : (Objects.requireNonNull(minecraftClient3.player).isSprinting() ? 12.0F : 0.0F)
                  )
            );
      float f3 = this.sizeSetting.getCurrent();
      if (!minecraftClient3.options.hudHidden && minecraftClient3.options.getPerspective().equals(Perspective.FIRST_PERSON) && !list.isEmpty()) {
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.disableCull();
         org.zenith.render.LegacyRenderBridge.disableDepthTest();
         org.zenith.render.LegacyRenderBridge.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         org.zenith.render.LegacyRenderBridge.setTexture(0, this.identifier);
         org.zenith.render.LegacyRenderBridge.usePositionTexColor();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         list.forEach(
            var6x -> {
               int i = val003.MediaTrackInfo().UiAnimation(var6x)
                  ? val003.TextScanner().getCurrentStyle().getFriendColor().getColor().call001()
                  : ZenithClient.on23().TextScanner().getClientColor(90).call001();
               float f4 = SimpleItemBuilder(var6x) - Objects.requireNonNull(minecraftClient3.player).getYaw();
               matrixstack.push();
               matrixstack.translate(f, f1, 0.0F);
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f4));
               matrixstack.translate(-f, -f1, 0.0F);
               Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
               if (val003.MediaTrackInfo().UiAnimation(var6x)) {
                  bufferbuilder.vertex(matrix4f, f - f3 / 2.0F, f2 + f3, 0.0F).texture(0.0F, 1.0F).color(i);
                  bufferbuilder.vertex(matrix4f, f + f3 / 2.0F, f2 + f3, 0.0F).texture(1.0F, 1.0F).color(i);
                  bufferbuilder.vertex(matrix4f, f + f3 / 2.0F, f2, 0.0F).texture(1.0F, 0.0F).color(i);
                  bufferbuilder.vertex(matrix4f, f - f3 / 2.0F, f2, 0.0F).texture(0.0F, 0.0F).color(i);
               } else {
                  GradientRadius liil11l111liil1ll = val003.TextScanner().getClientColor();
                  bufferbuilder.vertex(matrix4f, f - f3 / 2.0F, f2 + f3, 0.0F)
                     .texture(0.0F, 1.0F)
                     .color(liil11l111liil1ll.call014().call001());
                  bufferbuilder.vertex(matrix4f, f + f3 / 2.0F, f2 + f3, 0.0F)
                     .texture(1.0F, 1.0F)
                     .color(liil11l111liil1ll.call017().call001());
                  bufferbuilder.vertex(matrix4f, f + f3 / 2.0F, f2, 0.0F).texture(1.0F, 0.0F).color(liil11l111liil1ll.call052().call001());
                  bufferbuilder.vertex(matrix4f, f - f3 / 2.0F, f2, 0.0F).texture(0.0F, 0.0F).color(liil11l111liil1ll.call010().call001());
               }

               matrixstack.translate(f, f1, 0.0F);
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-f4));
               matrixstack.translate(-f, -f1, 0.0F);
               matrixstack.pop();
            }
         );
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         org.zenith.render.LegacyRenderBridge.enableDepthTest();
         org.zenith.render.LegacyRenderBridge.enableCull();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.disableBlend();
      }
   }

   public static float SimpleItemBuilder(Entity var0) {
      double d0 = MathUtils.NbtEditor(var0.lastX, var0.getX())
         - MathUtils.NbtEditor(minecraftClient3.player.lastX, minecraftClient3.player.getX());
      double d1 = MathUtils.NbtEditor(var0.lastZ, var0.getZ())
         - MathUtils.NbtEditor(minecraftClient3.player.lastZ, minecraftClient3.player.getZ());
      return (float)(-(Math.atan2(d0, d1) * (180.0 / Math.PI)));
   }
}
