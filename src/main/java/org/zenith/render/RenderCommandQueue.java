package org.zenith.render;


import java.util.function.Consumer;
import java.util.function.Supplier;
import org.zenith.base.font.MsdfFont;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.core.ShaderWrapper;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.GradientRadius;

public final class RenderCommandQueue {
   public static boolean enabled;
   public static final RenderCommandQueue zClass112 = new RenderCommandQueue();
   public static RenderCommandQueue zClass1122;
   public static final Comparator<RenderCommandQueue.Batch> comparator2 = Comparator.comparingInt(var0 -> var0.val512);
   public final List<RenderCommandQueue.Command> list115 = new ArrayList<>(256);
   public final List<RenderCommandQueue.Batch> list116 = new ArrayList<>(256);
   public final RenderCommandQueue.PooledCommand<RenderCommandQueue.Command> zClass112Var159 = new RenderCommandQueue.PooledCommand<>(
      RenderCommandQueue.Command::new, RenderCommandQueue.Command::var1436
   );
   public final RenderCommandQueue.PooledCommand<RenderCommandQueue.Batch> zClass112Var1592 = new RenderCommandQueue.PooledCommand<>(
      RenderCommandQueue.Batch::new, RenderCommandQueue.Batch::var1436
   );
   public final MatrixStack matrixStack5 = new MatrixStack();
   public boolean replaying;

   public static void map44() {
      if (!enabled) {
         zClass1122 = null;
      } else {
         zClass112.mutableText();
         zClass112.replaying = false;
         zClass1122 = zClass112;
      }
   }

   public static void finish() {
      RenderCommandQueue llliiill111li = zClass1122;
      zClass1122 = null;
      if (llliiill111li != null) {
         try {
            llliiill111li.var110();
         } finally {
            llliiill111li.mutableText();
         }
      }
   }

   public static boolean set14() {
      RenderCommandQueue llliiill111li = zClass1122;
      return llliiill111li != null && !llliiill111li.replaying;
   }

   public static void var1439() {
      RenderCommandQueue llliiill111li = zClass1122;
      if (llliiill111li != null && !llliiill111li.replaying && (!llliiill111li.list115.isEmpty() || !llliiill111li.list116.isEmpty())) {
         llliiill111li.var110();
      }
   }

   public static void on23(Matrix4f var0, float var1, float var2, float var3, float var4, ArgbColor var5) {
      RenderCommandQueue.Command llliiill111li_l1i1illlili = zClass1122.zClass112Var159.var1435();
      llliiill111li_l1i1illlili.val074 = RenderCommandQueue.DepthMode.val438;
      llliiill111li_l1i1illlili.val028.set(var0);
      llliiill111li_l1i1illlili.x = var1;
      llliiill111li_l1i1illlili.y = var2;
      llliiill111li_l1i1illlili.width = var3;
      llliiill111li_l1i1illlili.height = var4;
      llliiill111li_l1i1illlili.val037 = var5;
      zClass1122.list115.add(llliiill111li_l1i1illlili);
   }

   public static void Easing(Matrix4f var0, float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      RenderCommandQueue.Command llliiill111li_l1i1illlili = zClass1122.zClass112Var159.var1435();
      llliiill111li_l1i1illlili.val074 = RenderCommandQueue.DepthMode.val323;
      llliiill111li_l1i1illlili.val028.set(var0);
      llliiill111li_l1i1illlili.x = var1;
      llliiill111li_l1i1illlili.y = var2;
      llliiill111li_l1i1illlili.width = var3;
      llliiill111li_l1i1illlili.height = var4;
      llliiill111li_l1i1illlili.val200 = var5.var14311();
      llliiill111li_l1i1illlili.val201 = var5.string63();
      llliiill111li_l1i1illlili.val202 = var5.var14312();
      llliiill111li_l1i1illlili.val203 = var5.itemStack9();
      llliiill111li_l1i1illlili.val075 = var6.call001();
      zClass1122.list115.add(llliiill111li_l1i1illlili);
   }

   public static void ColorAnimator(Matrix4f var0, float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      if (var5.var14311() == var5.string63() && var5.var14311() == var5.var14312() && var5.var14311() == var5.itemStack9()) {
         Easing(var0, var1, var2, var3, var4, var5, var6);
      } else {
         RenderCommandQueue.Command llliiill111li_l1i1illlili = zClass1122.zClass112Var159.var1435();
         llliiill111li_l1i1illlili.val074 = RenderCommandQueue.DepthMode.val439;
         llliiill111li_l1i1illlili.val028.set(var0);
         llliiill111li_l1i1illlili.x = var1;
         llliiill111li_l1i1illlili.y = var2;
         llliiill111li_l1i1illlili.width = var3;
         llliiill111li_l1i1illlili.height = var4;
         llliiill111li_l1i1illlili.val076 = var5;
         llliiill111li_l1i1illlili.val037 = var6;
         zClass1122.list115.add(llliiill111li_l1i1illlili);
      }
   }

   public static void on23(Matrix4f var0, float var1, float var2, float var3, float var4, CornerRadius var5, GradientRadius var6) {
      RenderCommandQueue.Command llliiill111li_l1i1illlili = zClass1122.zClass112Var159.var1435();
      llliiill111li_l1i1illlili.val074 = RenderCommandQueue.DepthMode.val440;
      llliiill111li_l1i1illlili.val028.set(var0);
      llliiill111li_l1i1illlili.x = var1;
      llliiill111li_l1i1illlili.y = var2;
      llliiill111li_l1i1illlili.width = var3;
      llliiill111li_l1i1illlili.height = var4;
      llliiill111li_l1i1illlili.val076 = var5;
      llliiill111li_l1i1illlili.val037 = var6.call010();
      llliiill111li_l1i1illlili.val150 = var6.call014();
      llliiill111li_l1i1illlili.val151 = var6.call017();
      llliiill111li_l1i1illlili.val152 = var6.call052();
      zClass1122.list115.add(llliiill111li_l1i1illlili);
   }

   public static void on23(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      CornerRadius var6,
      ArgbColor var7,
      ArgbColor var8,
      ArgbColor var9,
      ArgbColor var10
   ) {
      RenderCommandQueue.Command llliiill111li_l1i1illlili = zClass1122.zClass112Var159.var1435();
      llliiill111li_l1i1illlili.val074 = RenderCommandQueue.DepthMode.val441;
      llliiill111li_l1i1illlili.val028.set(var0);
      llliiill111li_l1i1illlili.x = var1;
      llliiill111li_l1i1illlili.y = var2;
      llliiill111li_l1i1illlili.width = var3;
      llliiill111li_l1i1illlili.height = var4;
      llliiill111li_l1i1illlili.val442 = var5;
      llliiill111li_l1i1illlili.val076 = var6;
      llliiill111li_l1i1illlili.val037 = var7;
      llliiill111li_l1i1illlili.val150 = var8;
      llliiill111li_l1i1illlili.val151 = var9;
      llliiill111li_l1i1illlili.val152 = var10;
      zClass1122.list115.add(llliiill111li_l1i1illlili);
   }

   public static void on23(Font var0, String var1, Matrix4f var2, float var3, float var4, int var5) {
      if (var1 != null && !var1.isEmpty()) {
         RenderCommandQueue.Batch llliiill111li_Var160 = on23(var0, var1, var2, var3, var4);
         llliiill111li_Var160.val205 = RenderCommandQueue.BlendMode.val447;
         llliiill111li_Var160.val075 = var5;
         zClass1122.list116.add(llliiill111li_Var160);
      }
   }

   public static void on23(Font var0, String var1, Matrix4f var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (var1 != null && !var1.isEmpty()) {
         RenderCommandQueue.Batch llliiill111li_Var160 = on23(var0, var1, var2, var3, var4);
         llliiill111li_Var160.val205 = RenderCommandQueue.BlendMode.val448;
         llliiill111li_Var160.val075 = var5;
         llliiill111li_Var160.val443 = var6;
         llliiill111li_Var160.val444 = var7;
         llliiill111li_Var160.val445 = var8;
         llliiill111li_Var160.val446 = var9;
         zClass1122.list116.add(llliiill111li_Var160);
      }
   }

   public static void on23(Font var0, String var1, Matrix4f var2, float var3, float var4, GradientRadius var5) {
      if (var1 != null && !var1.isEmpty()) {
         RenderCommandQueue.Batch llliiill111li_Var160 = on23(var0, var1, var2, var3, var4);
         llliiill111li_Var160.val205 = RenderCommandQueue.BlendMode.val449;
         llliiill111li_Var160.val324 = var5;
         zClass1122.list116.add(llliiill111li_Var160);
      }
   }

   public static RenderCommandQueue.Batch on23(Font var0, String var1, Matrix4f var2, float var3, float var4) {
      RenderCommandQueue.Batch llliiill111li_Var160 = zClass1122.zClass112Var1592.var1435();
      llliiill111li_Var160.font = var0.getFont();
      llliiill111li_Var160.size = var0.getSize();
      llliiill111li_Var160.text = var1;
      llliiill111li_Var160.val204.set(var2);
      llliiill111li_Var160.x = var3;
      llliiill111li_Var160.y = var4;
      llliiill111li_Var160.val512 = llliiill111li_Var160.font.getTextureId();
      return llliiill111li_Var160;
   }

   public void var110() {
      if (!this.list115.isEmpty() || !this.list116.isEmpty()) {
         this.replaying = true;

         try {
            this.list85();
            this.var14316();
         } finally {
            this.replaying = false;
            this.mutableText();
         }
      }
   }

   public void list85() {
      if (!this.list115.isEmpty()) {
         BufferBuilder bufferbuilder = null;
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;

         for (int i = 0; i < this.list115.size(); i++) {
            RenderCommandQueue.Command llliiill111li_l1i1illlili = this.list115.get(i);
            if (llliiill111li_l1i1illlili.val074 == RenderCommandQueue.DepthMode.val323) {
               if (bufferbuilder == null
                  || llliiill111li_l1i1illlili.val200 != f
                  || llliiill111li_l1i1illlili.val201 != f1
                  || llliiill111li_l1i1illlili.val202 != f2
                  || llliiill111li_l1i1illlili.val203 != f3) {
                  bufferbuilder = this.UiAnimation(bufferbuilder);
                  bufferbuilder = this.on23(llliiill111li_l1i1illlili);
                  f = llliiill111li_l1i1illlili.val200;
                  f1 = llliiill111li_l1i1illlili.val201;
                  f2 = llliiill111li_l1i1illlili.val202;
                  f3 = llliiill111li_l1i1illlili.val203;
               }

               this.on23(bufferbuilder, llliiill111li_l1i1illlili);
            } else {
               bufferbuilder = this.UiAnimation(bufferbuilder);
               this.UiAnimation(llliiill111li_l1i1illlili);
            }
         }

         this.UiAnimation(bufferbuilder);
      }
   }

   public BufferBuilder on23(RenderCommandQueue.Command var1) {
      ShaderWrapper l1l1ii11lllll = ShapeRenderer.zClass0722;
      l1l1ii11lllll.float251();
      l1l1ii11lllll.HudArmorPanel("Radius").set(var1.val200, var1.val201, var1.val202, var1.val203);
      l1l1ii11lllll.HudArmorPanel("Smoothness").set(0.8F);
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
      return Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
   }

   public BufferBuilder UiAnimation(BufferBuilder var1) {
      if (var1 != null) {
         org.zenith.render.LegacyRenderBridge.draw(var1.end());
         org.zenith.render.LegacyRenderBridge.disableBlend();
      }

      return null;
   }

   public void on23(BufferBuilder var1, RenderCommandQueue.Command var2) {
      float f = 0.8F;
      float f1 = -f / 2.0F + f * 2.0F;
      float f2 = f / 2.0F + f;
      float f3 = var2.x - f1 / 2.0F;
      float f4 = var2.y - f2 / 2.0F;
      float f5 = var2.width + f1;
      float f6 = var2.height + f2;
      int i = var2.val075;
      var1.vertex(var2.val028, f3, f4, 0.0F).texture(var2.width, var2.height).color(i);
      var1.vertex(var2.val028, f3, f4 + f6, 0.0F).texture(var2.width, var2.height).color(i);
      var1.vertex(var2.val028, f3 + f5, f4 + f6, 0.0F).texture(var2.width, var2.height).color(i);
      var1.vertex(var2.val028, f3 + f5, f4, 0.0F).texture(var2.width, var2.height).color(i);
   }

   public void UiAnimation(RenderCommandQueue.Command var1) {
      this.matrixStack5.peek().getPositionMatrix().set(var1.val028);
      switch (var1.val074) {
         case val438:
            ShapeRenderer.on23(this.matrixStack5, var1.x, var1.y, var1.width, var1.height, var1.val037);
         case val323:
         default:
            break;
         case val439:
            ShapeRenderer.on23(this.matrixStack5, var1.x, var1.y, var1.width, var1.height, var1.val076, var1.val037);
            break;
         case val440:
            ShapeRenderer.on23(this.matrixStack5, var1.x, var1.y, var1.width, var1.height, var1.val076, var1.val037, var1.val150, var1.val151, var1.val152);
            break;
         case val441:
            ShapeRenderer.on23(
               this.matrixStack5, var1.x, var1.y, var1.width, var1.height, var1.val442, var1.val076, var1.val037, var1.val150, var1.val151, var1.val152
            );
      }
   }

   public void var14316() {
      if (!this.list116.isEmpty()) {
         this.list116.sort(comparator2);

         for (int i = 0; i < this.list116.size(); i++) {
            RenderCommandQueue.Batch llliiill111li_Var160 = this.list116.get(i);
            switch (llliiill111li_Var160.val205) {
               case val447:
                  MsdfRenderer.renderText(
                     llliiill111li_Var160.font,
                     llliiill111li_Var160.text,
                     llliiill111li_Var160.size,
                     llliiill111li_Var160.val075,
                     llliiill111li_Var160.val204,
                     llliiill111li_Var160.x,
                     llliiill111li_Var160.y,
                     0.0F
                  );
                  break;
               case val448:
                  MsdfRenderer.renderText(
                     llliiill111li_Var160.font,
                     llliiill111li_Var160.text,
                     llliiill111li_Var160.size,
                     llliiill111li_Var160.val075,
                     llliiill111li_Var160.val204,
                     llliiill111li_Var160.x,
                     llliiill111li_Var160.y,
                     0.0F,
                     llliiill111li_Var160.val443,
                     llliiill111li_Var160.val444,
                     llliiill111li_Var160.val445,
                     llliiill111li_Var160.val446
                  );
                  break;
               case val449:
                  MsdfRenderer.renderText(
                     llliiill111li_Var160.font,
                     llliiill111li_Var160.text,
                     llliiill111li_Var160.size,
                     llliiill111li_Var160.val324,
                     llliiill111li_Var160.val204,
                     llliiill111li_Var160.x,
                     llliiill111li_Var160.y,
                     0.0F
                  );
            }
         }
      }

      MsdfRenderer.flushBatch();
   }

   public void mutableText() {
      this.list115.clear();
      this.list116.clear();
      this.zClass112Var159.reset();
      this.zClass112Var1592.reset();
   }


   public static final class Command {
      final Matrix4f val028 = new Matrix4f();
      DepthMode val074;
      float x;
      float y;
      float width;
      float height;
      float val200;
      float val201;
      float val202;
      float val203;
      float val442;
      int val075;
      CornerRadius val076;
      ArgbColor val037;
      ArgbColor val150;
      ArgbColor val151;
      ArgbColor val152;

      public Command() {
      }

      void var1436() {
         this.val076 = null;
         this.val037 = null;
         this.val150 = null;
         this.val151 = null;
         this.val152 = null;
      }
   }

   public static final class PooledCommand<T> {
      public final ArrayList<T> arrayList2 = new ArrayList<>();
      public final Supplier<T> supplier4;
      public final Consumer<T> consumer2;
      public int int178;

      PooledCommand(Supplier<T> var1, Consumer<T> var2) {
         this.supplier4 = var1;
         this.consumer2 = var2;
      }

      T var1435() {
         if (this.int178 < this.arrayList2.size()) {
            return this.arrayList2.get(this.int178++);
         }

         Object object = this.supplier4.get();
         this.arrayList2.add((T)object);
         this.int178++;
         return (T)object;
      }

      void reset() {
         for (int i = 0; i < this.int178; i++) {
            this.consumer2.accept(this.arrayList2.get(i));
         }

         this.int178 = 0;
      }
   }

   public static final class Batch {
      final Matrix4f val204 = new Matrix4f();
      BlendMode val205;
      MsdfFont font;
      String text;
      float size;
      float x;
      float y;
      int val075;
      int val512;
      boolean val443;
      float val444;
      float val445;
      float val446;
      GradientRadius val324;

      public Batch() {
      }

      void var1436() {
         this.font = null;
         this.text = null;
         this.val324 = null;
      }
   }

   public enum DepthMode {
      val438,
      val323,
      val439,
      val440,
      val441;
   }

   public enum BlendMode {
      val447,
      val448,
      val449;
   }
}
