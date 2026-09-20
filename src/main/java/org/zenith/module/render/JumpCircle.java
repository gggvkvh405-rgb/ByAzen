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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ColorUtils;

@ModuleInfo(name = "JumpCircle", category = Category.RENDER, description = "module.jumpCircle.desc")
public class JumpCircle extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int142 = 8;
   public static final String[][] val098 = new String[][]{
      {"module.jumpCircle.texture.storm", "shtorm1", "shtorm2", "glow"},
      {"module.jumpCircle.texture.pool", "glow", "omut2", "omut3"},
      {"module.jumpCircle.texture.rings", "colso1", "omut2", "colso3"},
      {"module.jumpCircle.texture.explosion", "boom1", "boom2", "glow"}
   };
   public static final Identifier[][] val421 = call414();
   public static final JumpCircle jumpCircle = new JumpCircle();
   public final ModeSetting texture = new ModeSetting("module.jumpCircle.texture", "module.jumpCircle.texture.desc", call415());
   public final NumberSetting size2 = new NumberSetting("module.jumpCircle.size", 1.4F, 0.5F, 3.0F, 0.1F, "module.jumpCircle.size.desc", "x");
   public final NumberSetting speed3 = new NumberSetting("module.jumpCircle.speed", 4.8F, 0.0F, 8.0F, 0.1F, "module.jumpCircle.speed.desc", "x");
   public final NumberSetting lifetime2 = new NumberSetting(
      "module.jumpCircle.lifetime", 650.0F, 250.0F, 1500.0F, 50.0F, "module.jumpCircle.lifetime.desc", "ms"
   );
   public final List<JumpCircle.Circle> list45 = new ArrayList<>();
   public boolean lastOnGround;
   public Vec3d vec3d23;

   @Override
   public void onEnable() {
      super.onEnable();
      this.list45.clear();
      this.lastOnGround = minecraftClient3.player != null && minecraftClient3.player.isOnGround();
      this.vec3d23 = minecraftClient3.player == null ? null : minecraftClient3.player.getEntityPos();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.list45.clear();
      this.vec3d23 = null;
   }

   @EventTarget
   public void NbtEditor(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         boolean flag = minecraftClient3.player.isOnGround();
         if (flag) {
            this.vec3d23 = minecraftClient3.player.getEntityPos();
         }

         if (this.lastOnGround && !flag && minecraftClient3.player.getVelocity().y > 0.05) {
            this.list45
               .add(new JumpCircle.Circle(this.vec3d23 == null ? minecraftClient3.player.getEntityPos() : this.vec3d23, System.currentTimeMillis()));
            int i = this.list45.size() - 8;
            if (i > 0) {
               this.list45.subList(0, i).clear();
            }
         }

         this.lastOnGround = flag;
      }
   }

   @EventTarget
   public void ProfileItemBuilder(EventHookWorldRender var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && !this.list45.isEmpty()) {
         long i = System.currentTimeMillis();
         float f = this.lifetime2.getCurrent();

         for (int j = this.list45.size() - 1; j >= 0; j--) {
            if ((float)(i - this.list45.get(j).long112) > f) {
               this.list45.remove(j);
            }
         }

         if (!this.list45.isEmpty()) {
            this.on23(var1.ClanUpgrade(), i, f);
         }
      }
   }

   public void on23(MatrixStack var1, long var2, float var4) {
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      int i = this.texture.getIndex();
      if (i < 0 || i >= val421.length) {
         i = 0;
      }

      Identifier[] aidentifier = val421[i];
      Camera camera = minecraftClient3.getEntityRenderDispatcher().camera;
      Vec3d vec3d = camera.getCameraPos();
      Matrix4f matrix4f = var1.peek().getPositionMatrix();
      float f = this.size2.getCurrent();
      float f1 = this.speed3.getCurrent();
      int j = val003.TextScanner().getClientColor(0).call001();
      int k = val003.TextScanner().getClientColor(180).call001();

      for (JumpCircle.Circle l1ii111lii11lililli11illl_ii1il11l111ii11iilx : this.list45) {
         this.on23(l1ii111lii11lililli11illl_ii1il11l111ii11iilx, var2, vec3d, f1, var4, f, j, k);
      }

      for (Identifier identifier : aidentifier) {
         org.zenith.render.LegacyRenderBridge.setTexture(0, identifier);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (JumpCircle.Circle l1ii111lii11lililli11illl_ii1il11l111ii11iil : this.list45) {
            this.on23(matrix4f, bufferbuilder, l1ii111lii11lililli11illl_ii1il11l111ii11iil);
         }

         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      }

      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
   }

   public void on23(JumpCircle.Circle var1, long var2, Vec3d var4, float var5, float var6, float var7, int var8, int var9) {
      long i = var2 - var1.long112;
      var1.x = (float)(var1.vec3d26.x - var4.x);
      var1.y = (float)(var1.vec3d26.y - var4.y) + 0.04F;
      var1.z = (float)(var1.vec3d26.z - var4.z);
      float f = (float)i * 0.001F * (float) (Math.PI * 2) * var5;
      var1.float89 = MathHelper.cos(f);
      var1.float90 = MathHelper.sin(f);
      float f1 = MathHelper.clamp((float)i / var6, 0.0F, 1.0F);
      float f2 = 1.0F - f1;
      float f3 = f2 * f2;
      float f4 = MathHelper.lerp(f1, 0.15F, var7);
      var1.float91 = f4 * 1.22F;
      var1.float92 = f4;
      var1.int157 = ColorUtils.ColorAnimator(var8, f3 * 0.45F);
      var1.int158 = ColorUtils.ColorAnimator(var9, f3);
   }

   public void on23(Matrix4f var1, BufferBuilder var2, JumpCircle.Circle var3) {
      this.on23(var1, var2, var3.x, var3.y, var3.z, var3.float91, var3.float89, var3.float90, var3.int157);
      this.on23(var1, var2, var3.x, var3.y + 0.002F, var3.z, var3.float92, var3.float89, var3.float90, var3.int158);
   }

   public void on23(Matrix4f var1, BufferBuilder var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      float f = var6 * var7;
      float f1 = var6 * var8;
      this.on23(var1, var2, var3 - f + f1, var4, var5 - f1 - f, 0.0F, 0.0F, var9);
      this.on23(var1, var2, var3 - f - f1, var4, var5 - f1 + f, 0.0F, 1.0F, var9);
      this.on23(var1, var2, var3 + f - f1, var4, var5 + f1 + f, 1.0F, 1.0F, var9);
      this.on23(var1, var2, var3 + f + f1, var4, var5 + f1 - f, 1.0F, 0.0F, var9);
   }

   public void on23(Matrix4f var1, BufferBuilder var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      var2.vertex(var1, var3, var4, var5).texture(var6, var7).color(var8);
   }

   public static String[] call415() {
      String[] astring = new String[val098.length];

      for (int i = 0; i < val098.length; i++) {
         astring[i] = val098[i][0];
      }

      return astring;
   }

   public static Identifier[][] call414() {
      Identifier[][] aidentifier = new Identifier[val098.length][];

      for (int i = 0; i < val098.length; i++) {
         String[] astring = val098[i];
         int j = astring.length == 1 ? 0 : 1;
         aidentifier[i] = new Identifier[astring.length - j];

         for (int k = j; k < astring.length; k++) {
            aidentifier[i][k - j] = ZenithClient.on23("visuals/jumpcircle/" + astring[k] + ".png");
         }
      }

      return aidentifier;
   }


   public static class Circle {
      public final Vec3d vec3d26;
      public final long long112;
      public float x;
      public float y;
      public float z;
      public float float89;
      public float float90;
      public float float91;
      public float float92;
      public int int157;
      public int int158;

      public Circle(Vec3d var1, long var2) {
         this.vec3d26 = var1;
         this.long112 = var2;
      }
   }
}
