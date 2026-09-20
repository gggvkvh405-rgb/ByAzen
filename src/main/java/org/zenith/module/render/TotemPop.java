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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.ZenithClient;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.PacketEvent;
import org.zenith.render.WorldRender;
import org.zenith.render.WorldRender;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ColorUtils;
import org.zenith.util.MathUtils;

@ModuleInfo(name = "Totem Pop", category = Category.RENDER, description = "3D тотем над головой при отмене смерти")
public final class TotemPop extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static TotemPop totemPop = new TotemPop();
   public static final Identifier identifier7 = Identifier.of("zenith", "textures/glow.png");
   public static final float float191 = 0.6F;
   public static final float float192 = 1.4F;
   public static final float float193 = 0.16F;
   public static final float float194 = 0.32F;
   public static final float float195 = 0.68F;
   public static final float float196 = 0.22F;
   public static final float float197 = 0.44F;
   public static final float float198 = 4.2F;
   public static final float float199 = 1.05F;
   public static final float float200 = 5.6F;
   public static final float float201 = 1.35F;
   public NumberSetting u0420U0430U0437U043cU0435U0440 = new NumberSetting("Размер", 0.8F, 0.3F, 2.0F, 0.05F);
   public NumberSetting u0414U043bU0438U0442U0435U043bU044cU043dU043eU0441U0442U044c = new NumberSetting("Длительность", 800.0F, 200.0F, 3000.0F, 50.0F);
   public NumberSetting u0421U0432U0435U0447U0435U043dU0438U0435 = new NumberSetting("Свечение", 1.2F, 0.0F, 2.0F, 0.1F);
   public Map<PlayerEntity, TotemPop.Pop> map34 = new HashMap<>();
   public Matrix3f matrix3f3 = new Matrix3f();
   public Vector3f vector3f12 = new Vector3f();
   public Vector3f vector3f13 = new Vector3f();
   public List<TotemPop.Animation> list82 = new ArrayList<>();
   public double double89;
   public double double90;
   public double double91;
   public float float148;
   public float[] val211;
   public float[] val333;
   public float[] val334;
   public float[] val475;
   public float[] val476;
   public float[] val477;
   public boolean boolean145;

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (minecraftClient3.world != null && !this.map34.isEmpty() && this.int323()) {
         long i = System.currentTimeMillis();
         float f = this.u0414U043bU0438U0442U0435U043bU044cU043dU043eU0441U0442U044c.getCurrent();
         float f1 = this.u0420U0430U0437U043cU0435U0440.getCurrent();
         this.float148 = Math.max(0.0F, this.u0421U0432U0435U0447U0435U043dU0438U0435.getCurrent());
         Vec3d vec3d = minecraftClient3.getEntityRenderDispatcher().camera.getCameraPos();
         this.double89 = vec3d.x;
         this.double90 = vec3d.y;
         this.double91 = vec3d.z;
         boolean flag = minecraftClient3.options.getPerspective().isFirstPerson();
         ClientPlayerEntity clientplayerentity = minecraftClient3.player;
         Iterator<Entry<PlayerEntity, TotemPop.Pop>> iterator = this.map34.entrySet().iterator();

         while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (((PlayerEntity)entry.getKey()).isRemoved() || (float)(i - ((TotemPop.Pop)entry.getValue()).val322) / f >= 1.0F) {
               iterator.remove();
            }
         }

         if (!this.map34.isEmpty()) {
            for (Entry<PlayerEntity, TotemPop.Pop> entry1 : this.map34.entrySet()) {
               PlayerEntity playerentity = entry1.getKey();
               if (!flag || playerentity != clientplayerentity) {
                  TotemPop.Pop lilliliililil1i_l1i1illlili = entry1.getValue();
                  float f2 = (float)(i - lilliliililil1i_l1i1illlili.val322) / f;
                  this.on23(playerentity, lilliliililil1i_l1i1illlili.val436, f2, f1);
               }
            }

            this.UiAnimation(var1.ClanUpgrade());
         }
      }
   }

   public void UiAnimation(MatrixStack var1) {
      if (!this.list82.isEmpty()) {
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
         org.zenith.render.LegacyRenderBridge.enableDepthTest();
         org.zenith.render.LegacyRenderBridge.depthMask(false);
         org.zenith.render.LegacyRenderBridge.disableCull();
         org.zenith.render.LegacyRenderBridge.usePositionTexColor();
         org.zenith.render.LegacyRenderBridge.setTexture(0, identifier7);
         Matrix4f matrix4f = var1.peek().getPositionMatrix();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (TotemPop.Animation lilliliililil1i_ii1il11l111ii11iil : this.list82) {
            double d0 = lilliliililil1i_ii1il11l111ii11iil.val049 * lilliliililil1i_ii1il11l111ii11iil.val049
               + lilliliililil1i_ii1il11l111ii11iil.val050 * lilliliililil1i_ii1il11l111ii11iil.val050
               + lilliliililil1i_ii1il11l111ii11iil.val051 * lilliliililil1i_ii1il11l111ii11iil.val051;
            if (!(d0 < 1.0E-10)) {
               double d1 = 1.0 / Math.sqrt(d0);
               double d2 = lilliliililil1i_ii1il11l111ii11iil.val049 * d1;
               double d3 = lilliliililil1i_ii1il11l111ii11iil.val050 * d1;
               double d4 = lilliliililil1i_ii1il11l111ii11iil.val051 * d1;
               double d5 = -d4;
               double d6 = d5 * d5 + d2 * d2;
               double d7;
               double d8;
               double d9;
               if (d6 < 1.0E-8) {
                  d7 = 1.0;
                  d8 = 0.0;
                  d9 = 0.0;
               } else {
                  double d10 = 1.0 / Math.sqrt(d6);
                  d7 = d5 * d10;
                  d8 = 0.0;
                  d9 = d2 * d10;
               }

               double d26 = d8 * d4 - d9 * d3;
               double d11 = d9 * d2 - d7 * d4;
               double d12 = d7 * d3 - d8 * d2;
               double d13 = lilliliililil1i_ii1il11l111ii11iil.float271;
               double d14 = -d7 * d13 - d26 * d13;
               double d15 = -d8 * d13 - d11 * d13;
               double d16 = -d9 * d13 - d12 * d13;
               double d17 = -d7 * d13 + d26 * d13;
               double d18 = -d8 * d13 + d11 * d13;
               double d19 = -d9 * d13 + d12 * d13;
               double d20 = d7 * d13 + d26 * d13;
               double d21 = d8 * d13 + d11 * d13;
               double d22 = d9 * d13 + d12 * d13;
               double d23 = d7 * d13 - d26 * d13;
               double d24 = d8 * d13 - d11 * d13;
               double d25 = d9 * d13 - d12 * d13;
               bufferbuilder.vertex(
                     matrix4f,
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val049 + d14),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val050 + d15),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val051 + d16)
                  )
                  .texture(0.0F, 0.0F)
                  .color(lilliliililil1i_ii1il11l111ii11iil.val148);
               bufferbuilder.vertex(
                     matrix4f,
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val049 + d17),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val050 + d18),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val051 + d19)
                  )
                  .texture(0.0F, 1.0F)
                  .color(lilliliililil1i_ii1il11l111ii11iil.val148);
               bufferbuilder.vertex(
                     matrix4f,
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val049 + d20),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val050 + d21),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val051 + d22)
                  )
                  .texture(1.0F, 1.0F)
                  .color(lilliliililil1i_ii1il11l111ii11iil.val148);
               bufferbuilder.vertex(
                     matrix4f,
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val049 + d23),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val050 + d24),
                     (float)(lilliliililil1i_ii1il11l111ii11iil.val051 + d25)
                  )
                  .texture(1.0F, 0.0F)
                  .color(lilliliililil1i_ii1il11l111ii11iil.val148);
            }
         }

         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         org.zenith.render.LegacyRenderBridge.depthMask(true);
         org.zenith.render.LegacyRenderBridge.enableCull();
         org.zenith.render.LegacyRenderBridge.disableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         this.list82.clear();
      }
   }

   public void on23(PlayerEntity var1, float var2, float var3, float var4) {
      Vec3d vec3d = MathUtils.CloudResponse(var1);
      double d0 = vec3d.x;
      double d1 = vec3d.y + var1.getHeight() + 0.6F;
      double d2 = vec3d.z;
      float f = var2 * (float) (Math.PI / 180.0);
      float f1 = -f;
      if (var3 < 0.32F) {
         float f11 = 1.0F;
         float f12 = 1.0F;
         double d5 = 0.0;
         float f13 = 0.0F;
         float f14 = 0.0F;
         float f15 = 1.0F;
         float f16 = 1.0F;
         if (var3 < 0.16F) {
            float f17 = var3 / 0.16F;
            float f19 = EventMouseScrollHook(f17);
            f11 = f19;
            float f21 = 1.0F + 0.18F * MathHelper.sin(f19 * (float) Math.PI);
            f12 = MathHelper.lerp(f19, 0.45F, 1.0F) * (f17 > 0.6F ? f21 : 1.0F);
            d5 = -0.69F * (1.0F - f19);
            f14 = (1.0F - f19) * (float) Math.PI * 0.55F;
         } else {
            float f18 = (var3 - 0.16F) / 0.16F;
            f13 = MathHelper.sin(f18 * (float) Math.PI) * 0.07F;
            f14 = f18 * 0.35F;
            if (f18 > 0.55F) {
               float f20 = (f18 - 0.55F) / 0.45F;
               float f22 = EventMouseScrollHook(f20);
               f15 = 1.0F - f22 * 0.12F;
               f16 = 1.0F + f22 * 0.08F;
            }
         }

         double d6 = d1 + d5 + f13;
         float f23 = var4 * f12;
         int j = ColorUtils.ColorAnimator(ZenithClient.on23().TextScanner().getClientColor(0).call001(), f11);
         this.matrix3f3.identity().rotateY(f1 + f14).scale(f16, f15, f16);
         this.on23(this.val211, d0, d6, d2, f23, j);
         this.UiAnimation(this.val475, d0, d6, d2, f23, j);
         this.on23(d0, d6, d2, f23, f1, var3);
      } else {
         float f2 = (var3 - 0.32F) / 0.68F;
         float f3 = 1.0F - f2;
         float f4 = 1.0F - f3 * f3 * f3;
         float f5 = 1.0F - f3 * f3;
         float f6 = f3 * f3;
         int i = ColorUtils.ColorAnimator(ZenithClient.on23().TextScanner().getClientColor(0).call001(), f6);
         float f7 = f4 * 4.2F * var4;
         float f8 = (1.05F * f2 - 2.8F * f2 * f2) * var4;
         float f9 = f5 * (float) (Math.PI * 2) * 1.35F;
         float f10 = f2 * (float) Math.PI * 0.45F;
         double d3 = Math.cos(f);
         double d4 = Math.sin(f);
         this.matrix3f3.identity().rotateY(f1 + f10).rotateZ(f9);
         this.on23(this.val333, d0 - d3 * f7, d1 + f8, d2 - d4 * f7, var4, i);
         this.UiAnimation(this.val476, d0 - d3 * f7, d1 + f8, d2 - d4 * f7, var4, i);
         this.matrix3f3.identity().rotateY(f1 - f10).rotateZ(-f9);
         this.on23(this.val334, d0 + d3 * f7, d1 + f8, d2 + d4 * f7, var4, i);
         this.UiAnimation(this.val477, d0 + d3 * f7, d1 + f8, d2 + d4 * f7, var4, i);
         this.on23(d0, d1, d2, var4, f1, var3);
      }
   }

   public boolean int323() {
      if (this.boolean145) {
         return this.val211 != null;
      }

      this.boolean145 = true;
      InputStream inputstream = TotemPop.class.getResourceAsStream("/assets/zenith/visuals/particles/totem_undying.obj");
      if (inputstream == null) {
         return false;
      }

      try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))) {
         ArrayList arraylist = new ArrayList();
         HashMap<Long, TotemPop.Renderer> hashmap = new HashMap<>();

         String s;
         while ((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if (s.startsWith("v ")) {
               String[] astring2 = s.split("\\s+");
               arraylist.add(new Vector3f(Float.parseFloat(astring2[1]), Float.parseFloat(astring2[2]) - 0.75F, Float.parseFloat(astring2[3])));
            } else if (s.startsWith("f ")) {
               String[] astring = s.split("\\s+");

               for (int i = 1; i < astring.length; i++) {
                  int j = i == astring.length - 1 ? 1 : i + 1;
                  int j1 = Integer.parseInt(astring[i].split("/")[0]) - 1;
                  int k = Integer.parseInt(astring[j].split("/")[0]) - 1;
                  if (j1 != k) {
                     String[] astring1 = astring[i].split("/");
                     int l = astring1.length > 2 && !astring1[2].isEmpty() ? Integer.parseInt(astring1[2]) - 1 : -1;
                     long i1 = (long)Math.min(j1, k) << 32 | Math.max(j1, k) & 4294967295L;
                     TotemPop.Renderer lilliliililil1i_illi1l1l1 = (TotemPop.Renderer)hashmap.get(i1);
                     if (lilliliililil1i_illi1l1l1 == null) {
                        Vector3f vector3f = (Vector3f)arraylist.get(j1);
                        Vector3f vector3f1 = (Vector3f)arraylist.get(k);
                        hashmap.put(i1, new TotemPop.Renderer(vector3f.x, vector3f.y, vector3f.z, vector3f1.x, vector3f1.y, vector3f1.z, l));
                     } else {
                        lilliliililil1i_illi1l1l1.count++;
                        if (lilliliililil1i_illi1l1l1.val435 != l) {
                           lilliliililil1i_illi1l1l1.val066 = false;
                        }
                     }
                  }
               }
            }
         }

         ArrayList arraylist1 = new ArrayList();
         ArrayList arraylist2 = new ArrayList();
         ArrayList arraylist3 = new ArrayList();

         for (TotemPop.Renderer lilliliililil1i_illi1l1l1x : hashmap.values()) {
            if (lilliliililil1i_illi1l1l1x.count == 1 || !lilliliililil1i_illi1l1l1x.val066) {
               arraylist1.add(lilliliililil1i_illi1l1l1x.val036);
               arraylist1.add(lilliliililil1i_illi1l1l1x.val052);
               arraylist1.add(lilliliililil1i_illi1l1l1x.val053);
               arraylist1.add(lilliliililil1i_illi1l1l1x.val054);
               arraylist1.add(lilliliililil1i_illi1l1l1x.val072);
               arraylist1.add(lilliliililil1i_illi1l1l1x.val073);
               on23(lilliliililil1i_illi1l1l1x, arraylist2, arraylist3);
            }
         }

         this.val211 = SimpleItemBuilder(arraylist1);
         this.val333 = SimpleItemBuilder(arraylist2);
         this.val334 = SimpleItemBuilder(arraylist3);
         this.val475 = ServiceException(this.val211);
         this.val476 = ServiceException(this.val333);
         this.val477 = ServiceException(this.val334);
         int j1 = 1;
         return j1 != 0;
      } catch (Exception exception) {
         return false;
      }
   }

   public static void on23(TotemPop.Renderer var0, List<Float> var1, List<Float> var2) {
      boolean flag = var0.val036 <= 0.0F;
      boolean flag1 = var0.val054 <= 0.0F;
      if (flag && flag1) {
         var1.add(var0.val036);
         var1.add(var0.val052);
         var1.add(var0.val053);
         var1.add(var0.val054);
         var1.add(var0.val072);
         var1.add(var0.val073);
      } else if (!flag && !flag1) {
         var2.add(var0.val036);
         var2.add(var0.val052);
         var2.add(var0.val053);
         var2.add(var0.val054);
         var2.add(var0.val072);
         var2.add(var0.val073);
      } else {
         float f = var0.val054 - var0.val036;
         if (f == 0.0F) {
            var1.add(var0.val036);
            var1.add(var0.val052);
            var1.add(var0.val053);
            var1.add(var0.val054);
            var1.add(var0.val072);
            var1.add(var0.val073);
            return;
         }

         float f1 = -var0.val036 / f;
         float f2 = var0.val052 + (var0.val072 - var0.val052) * f1;
         float f3 = var0.val053 + (var0.val073 - var0.val053) * f1;
         List<Float> list = flag ? var1 : var2;
         List<Float> list1 = flag ? var2 : var1;
         list.add(var0.val036);
         list.add(var0.val052);
         list.add(var0.val053);
         list.add(0.0F);
         list.add(f2);
         list.add(f3);
         list1.add(0.0F);
         list1.add(f2);
         list1.add(f3);
         list1.add(var0.val054);
         list1.add(var0.val072);
         list1.add(var0.val073);
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.map34.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.map34.clear();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows()
         && minecraftClient3.world != null
         && var1.ItemScroller() instanceof EntityStatusS2CPacket entitystatuss2cpacket
         && entitystatuss2cpacket.getStatus() == 35) {
         if (entitystatuss2cpacket.getEntity(minecraftClient3.world) instanceof PlayerEntity playerentity) {
            this.map34.put(playerentity, new TotemPop.Pop(System.currentTimeMillis(), playerentity.bodyYaw));
         }
      }
   }

   public static float EventMouseScrollHook(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else {
         return var0 >= 1.0F ? 1.0F : var0 * var0 * var0 * (var0 * (var0 * 6.0F - 15.0F) + 10.0F);
      }
   }

   public void on23(float[] var1, double var2, double var4, double var6, float var8, int var9) {
      List<WorldRender.Line> list = WorldRender.list97;
      double d0 = var2 - this.double89;
      double d1 = var4 - this.double90;
      double d2 = var6 - this.double91;

      for (byte b0 = 0; b0 < var1.length; b0 = (byte)(b0 + 6)) {
         this.vector3f12.set(var1[b0] * var8, var1[b0 + 1] * var8, var1[b0 + 2] * var8);
         this.vector3f13.set(var1[b0 + 3] * var8, var1[b0 + 4] * var8, var1[b0 + 5] * var8);
         this.matrix3f3.transform(this.vector3f12);
         this.matrix3f3.transform(this.vector3f13);
         Vec3d vec3d = new Vec3d(d0 + this.vector3f12.x, d1 + this.vector3f12.y, d2 + this.vector3f12.z);
         Vec3d vec3d1 = new Vec3d(d0 + this.vector3f13.x, d1 + this.vector3f13.y, d2 + this.vector3f13.z);
         list.add(new WorldRender.Line(vec3d, vec3d1, var9, var9, 2.24F));
      }
   }

   public void UiAnimation(float[] var1, double var2, double var4, double var6, float var8, int var9) {
      float f = this.float148;
      if (!(f <= 0.02F) && var1 != null && var1.length != 0) {
         float f1 = var8 * (0.5F + 0.28F * f);
         int i = ColorUtils.ColorAnimator(var9, Math.min(0.0083F + 0.0047F * f, 0.0283F));
         double d0 = var2 - this.double89;
         double d1 = var4 - this.double90;
         double d2 = var6 - this.double91;

         for (byte b0 = 0; b0 < var1.length; b0 = (byte)(b0 + 3)) {
            this.vector3f12.set(var1[b0] * var8, var1[b0 + 1] * var8, var1[b0 + 2] * var8);
            this.matrix3f3.transform(this.vector3f12);
            this.list82.add(new TotemPop.Animation(d0 + this.vector3f12.x, d1 + this.vector3f12.y, d2 + this.vector3f12.z, f1, i));
         }
      }
   }

   public void on23(double var1, double var3, double var5, float var7, float var8, float var9) {
      if (!(var9 < 0.22F) && !(var9 > 0.44F)) {
         float f = (var9 - 0.22F) / 0.22F;
         float f1 = EventMouseScrollHook(f);
         float f2 = MathHelper.lerp(f1, 1.05F, -1.25F) * var7;
         float f3 = MathHelper.lerp(EventMouseScrollHook(f), 0.65F, 0.18F) * var7;
         float f4 = f2 - f3;
         float f5 = MathHelper.sin(f * (float) Math.PI);
         float f6 = MathHelper.clamp(f5 * f5 * 1.8F, 0.0F, 1.0F);
         if (!(f6 <= 0.0F)) {
            this.matrix3f3.identity().rotateY(var8);
            this.vector3f12.set(0.0F, f2, 0.0F);
            this.vector3f13.set(0.0F, f4, 0.0F);
            this.matrix3f3.transform(this.vector3f12);
            this.matrix3f3.transform(this.vector3f13);
            Vec3d vec3d = new Vec3d(
               var1 + this.vector3f12.x - this.double89, var3 + this.vector3f12.y - this.double90, var5 + this.vector3f12.z - this.double91
            );
            Vec3d vec3d1 = new Vec3d(
               var1 + this.vector3f13.x - this.double89, var3 + this.vector3f13.y - this.double90, var5 + this.vector3f13.z - this.double91
            );
            int i = ColorUtils.ColorAnimator(-1, f6);
            WorldRender.list97.add(new WorldRender.Line(vec3d, vec3d1, i, i, 2.24F));
         }
      }
   }

   public static float[] SimpleItemBuilder(List<Float> var0) {
      float[] afloat = new float[var0.size()];

      for (int i = 0; i < var0.size(); i++) {
         afloat[i] = var0.get(i);
      }

      return afloat;
   }

   public static float[] ServiceException(float[] var0) {
      LinkedHashSet linkedhashset = new LinkedHashSet();
      ArrayList arraylist = new ArrayList();

      for (byte b0 = 0; b0 < var0.length; b0 = (byte)(b0 + 3)) {
         int i = Math.round(var0[b0] * 4096.0F);
         int j = Math.round(var0[b0 + 1] * 4096.0F);
         int k = Math.round(var0[b0 + 2] * 4096.0F);
         long l = (i & 2097151L) << 42 | (j & 2097151L) << 21 | k & 2097151L;
         if (linkedhashset.add(l)) {
            arraylist.add(var0[b0]);
            arraylist.add(var0[b0 + 1]);
            arraylist.add(var0[b0 + 2]);
         }
      }

      return SimpleItemBuilder(arraylist);
   }


   public static final class Pop {
      final long val322;
      final float val436;

      Pop(long var1, float var3) {
         this.val322 = var1;
         this.val436 = var3;
      }
   }

   public static final class Animation {
      final double val049;
      final double val050;
      final double val051;
      final float float271;
      final int val148;

      Animation(double var1, double var3, double var5, float var7, int var8) {
         this.val049 = var1;
         this.val050 = var3;
         this.val051 = var5;
         this.float271 = var7;
         this.val148 = var8;
      }
   }

   public static final class Renderer {
      final float val036;
      final float val052;
      final float val053;
      final float val054;
      final float val072;
      final float val073;
      final int val435;
      int count = 1;
      boolean val066 = true;

      Renderer(float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
         this.val036 = var1;
         this.val052 = var2;
         this.val053 = var3;
         this.val054 = var4;
         this.val072 = var5;
         this.val073 = var6;
         this.val435 = var7;
      }
   }
}
