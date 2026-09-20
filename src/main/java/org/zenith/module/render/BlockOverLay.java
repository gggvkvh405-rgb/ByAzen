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
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.event.EventHookWorldRender;
import org.zenith.render.BoxShaderRenderer;
import org.zenith.render.WorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;

@ModuleInfo(name = "BlockOverLay", category = Category.RENDER, description = "module.blockOverLay.desc")
public final class BlockOverLay extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final BlockOverLay blockOverLay = new BlockOverLay();
   public final ModeSetting mode5 = new ModeSetting(
      "module.blockOverLay.mode", "module.blockOverLay.mode.desc", "module.blockOverLay.shader", "module.blockOverLay.box"
   );
   public final ModeSetting shaderEffect = new ModeSetting(
      "module.blockOverLay.shaderEffect",
      "module.blockOverLay.shaderEffect.desc",
      () -> this.mode5.is(0),
      "module.blockOverLay.effect.flame",
      "module.blockOverLay.effect.aqua",
      "module.blockOverLay.effect.rainbow",
      "module.blockOverLay.effect.hologram",
      "module.blockOverLay.effect.aurora",
      "module.blockOverLay.effect.plasma"
   );
   public final BooleanSetting animation = new BooleanSetting("module.blockOverLay.animation", "module.blockOverLay.animation.desc", true);
   public final BooleanSetting fadeAnimation = new BooleanSetting("module.blockOverLay.fadeAnimation", "module.blockOverLay.fadeAnimation.desc", true);
   public final NumberSetting fadeSpeed = new NumberSetting(
      "module.blockOverLay.fadeSpeed", 8.0F, 1.0F, 30.0F, 0.5F, "module.blockOverLay.fadeSpeed.desc", "x", this.fadeAnimation::isEnabled, null
   );
   public final NumberSetting animationSpeed = new NumberSetting(
      "module.blockOverLay.animationSpeed", 12.0F, 1.0F, 30.0F, 0.5F, "module.blockOverLay.animationSpeed.desc", "x", this.animation::isEnabled, null
   );
   public final NumberSetting lineWidth = new NumberSetting(
      "module.blockOverLay.lineWidth", 1.5F, 0.5F, 5.0F, 0.1F, "module.blockOverLay.lineWidth.desc", "px", () -> this.mode5.is(1), null
   );
   public final ColorSetting boxColor = new ColorSetting("module.blockOverLay.boxColor", new ArgbColor(170, 120, 255, 190), () -> this.mode5.is(1));
   public final NumberSetting timeSpeed = new NumberSetting(
      "module.blockOverLay.timeSpeed", 1.0F, 0.0F, 5.0F, 0.05F, "module.blockOverLay.timeSpeed.desc", "x", () -> this.mode5.is(0), null
   );
   public final ColorSetting outlineColor = new ColorSetting("module.blockOverLay.outlineColor", new ArgbColor(255, 255, 255), () -> this.mode5.is(0));
   public final ColorSetting firstFillColor = new ColorSetting("module.blockOverLay.firstFillColor", new ArgbColor(170, 120, 255, 110), () -> this.mode5.is(0));
   public final ColorSetting secondFillColor = new ColorSetting("module.blockOverLay.secondFillColor", new ArgbColor(60, 210, 255, 110), () -> this.mode5.is(0));
   public Box box;
   public long long79 = -1L;
   public float float12;

   @EventTarget
   public void NbtEditor(EventHookWorldRender var1) {
      if (minecraftClient3.world == null) {
         this.int328();
      } else {
         long i = System.nanoTime();
         double d0 = this.long79 < 0L ? 0.0 : Math.min((i - this.long79) / 1.0E9, 0.1);
         this.long79 = i;
         Box box = this.int327();
         boolean flag = box != null;
         this.on23(flag, d0);
         if (flag) {
            this.on23(box, d0);
         }

         if (this.box != null && !(this.float12 <= 0.001F)) {
            List<Box> list = List.of(this.box);
            if (this.mode5.is(0)) {
               BoxShaderRenderer.on23(
                  list,
                  this.outlineColor.getIntColor(),
                  this.firstFillColor.getIntColor(),
                  this.secondFillColor.getIntColor(),
                  this.timeSpeed.getCurrent(),
                  this.shaderEffect.getIndex(),
                  this.float12
               );
            } else {
               int j = ColorUtils.ColorAnimator(this.boxColor.getIntColor(), this.float12);

               for (Box box1 : list) {
                  WorldRender.on23(box1, j, this.lineWidth.getCurrent());
               }
            }
         }
      }
   }

   @Override
   public void onEnable() {
      this.int328();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.int328();
      super.onDisable();
   }

   public Box int327() {
      ClientWorld clientworld = minecraftClient3.world;
      if (clientworld == null) {
         return null;
      } else if (minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult && blockhitresult.getType() == Type.BLOCK) {
         BlockPos blockpos = blockhitresult.getBlockPos();
         BlockState blockstate = clientworld.getBlockState(blockpos);
         VoxelShape voxelshape = blockstate.getOutlineShape(clientworld, blockpos);
         return voxelshape.isEmpty() ? null : voxelshape.getBoundingBox().offset(blockpos);
      } else {
         return null;
      }
   }

   public void on23(Box var1, double var2) {
      if (this.animation.isEnabled() && this.box != null) {
         double d0 = 1.0 - Math.exp(-this.animationSpeed.getCurrent() * var2);
         this.box = new Box(
            TextScanner(this.box.minX, var1.minX, d0),
            TextScanner(this.box.minY, var1.minY, d0),
            TextScanner(this.box.minZ, var1.minZ, d0),
            TextScanner(this.box.maxX, var1.maxX, d0),
            TextScanner(this.box.maxY, var1.maxY, d0),
            TextScanner(this.box.maxZ, var1.maxZ, d0)
         );
      } else {
         this.box = var1;
      }
   }

   public void on23(boolean var1, double var2) {
      if (!this.fadeAnimation.isEnabled()) {
         this.float12 = var1 ? 1.0F : 0.0F;
      } else {
         double d0 = var1 ? 1.0 : 0.0;
         double d1 = 1.0 - Math.exp(-this.fadeSpeed.getCurrent() * var2);
         this.float12 = (float)TextScanner(this.float12, d0, d1);
         if (var1 && this.float12 > 0.995F) {
            this.float12 = 1.0F;
         } else if (!var1 && this.float12 < 0.005F) {
            this.float12 = 0.0F;
         }
      }
   }

   public static double TextScanner(double var0, double var2, double var4) {
      return var0 + (var2 - var0) * var4;
   }

   public void int328() {
      this.box = null;
      this.long79 = -1L;
      this.float12 = 0.0F;
   }
}
