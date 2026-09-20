package org.zenith.render;


import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.core.HudPreviewItem;
import org.zenith.core.HudPreviewType;
import org.zenith.module.render.Interface;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudQueuedContext;

public final class HudPreviewRenderQueue {
   public final HudPreviewRenderQueue.Batch hudPreviewRenderQueueVar165;
   public final List list19;
   public final List list20;
   public float float25;
   public final HudPreviewRenderQueue.Batch hudPreviewRenderQueueVar1652;
   public final RectBatch rectBatch;
   public final List list21;
   public boolean boolean49;
   public final List list22;
   public final List list23;
   public static final Identifier identifier2 = null;
   public final HudPreviewRenderQueue.Batch hudPreviewRenderQueueVar1653;
   public final HudPreviewRenderQueue.Batch hudPreviewRenderQueueVar1654;
   public final List list24;
   public final List list25;
   public final List list26;
   public final RoundedRectBatch roundedRectBatch;
   public final List list27 = new ArrayList(64);
   public static HudPreviewRenderQueue hudPreviewRenderQueue2 = new HudPreviewRenderQueue();
   public final List<HudPreviewRenderQueue.ExecutableCommand> list28;
   public final MatrixStack matrixStack2;
   public CornerRadius val242;
   public final List list29;
   public final HudPreviewRenderQueue.Batch hudPreviewRenderQueueVar1655;

   public HudPreviewRenderQueue() {
      this.list23 = new ArrayList(192);
      this.list28 = new ArrayList<>(256);
      this.list26 = new ArrayList(128);
      this.list21 = new ArrayList(128);
      this.list19 = new ArrayList(64);
      this.roundedRectBatch = new RoundedRectBatch();
      this.rectBatch = new RectBatch();
      this.matrixStack2 = new MatrixStack();
      this.hudPreviewRenderQueueVar1654 = new HudPreviewRenderQueue.Batch<>(HudPreviewItem::new, HudPreviewItem::var1436);
      this.hudPreviewRenderQueueVar1652 = new HudPreviewRenderQueue.Batch<>(HudPreviewRenderQueue.MatrixCommand::new, HudPreviewRenderQueue.MatrixCommand::var1436);
      this.hudPreviewRenderQueueVar1653 = new HudPreviewRenderQueue.Batch<>(HudPreviewRenderQueue.ClipCommand::new, HudPreviewRenderQueue.ClipCommand::var1436);
      this.hudPreviewRenderQueueVar1655 = new HudPreviewRenderQueue.Batch<>(HudPreviewRenderQueue.Context::new, HudPreviewRenderQueue.Context::var1436);
      this.hudPreviewRenderQueueVar165 = new HudPreviewRenderQueue.Batch<>(HudPreviewRenderQueue_Bound::new, HudPreviewRenderQueue_Bound::var1436);
      this.list20 = new ArrayList(8);
      this.list22 = new ArrayList(8);
      this.list29 = new ArrayList(8);
      this.list25 = new ArrayList(8);
      this.list24 = new ArrayList(8);
      this.float25 = Float.NaN;
      this.val242 = CornerRadius.var159;
   }

   public static void on23(HudPreviewRenderQueue var0) {
      var0.clear();
      hudPreviewRenderQueue2 = var0;
   }

   public static void UiAnimation(HudPreviewRenderQueue var0) {
      if (hudPreviewRenderQueue2 == var0) {
         hudPreviewRenderQueue2 = null;
         var0.clear();
      }
   }

   public static boolean set14() {
      return hudPreviewRenderQueue2 != null && !hudPreviewRenderQueue2.boolean49;
   }

   public static void NbtItemSpec(Runnable var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list28, li1illlill.list29);
      li1illlill.list28.add(new HudPreviewRenderQueue.RunnableCommand(var0));
   }

   public static void on23(Matrix4f var0, Consumer<MatrixStack> var1) {
      Matrix4f matrix4f = new Matrix4f(var0);
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list28, li1illlill.list29);
      li1illlill.list28.add(() -> {
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.peek().getPositionMatrix().set(matrix4f);
         var1.accept(matrixstack);
      });
   }

   public static void EnchantItemSpec(Runnable var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list26, li1illlill.list25);
      li1illlill.list26.add(new HudPreviewRenderQueue.RunnableCommand(var0));
   }

   public static void SimpleItemBuilder(Runnable var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list21, li1illlill.list24);
      li1illlill.list21.add(new HudPreviewRenderQueue.RunnableCommand(var0));
   }

   public static void ItemServiceBase(Runnable var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list23, li1illlill.list22);
      li1illlill.list23.add(new HudPreviewRenderQueue.RunnableCommand(var0));
   }

   public static void UiAnimation(Matrix4f var0, Consumer<MatrixStack> var1) {
      Matrix4f matrix4f = new Matrix4f(var0);
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list23, li1illlill.list22);
      li1illlill.list23.add(new HudPreviewRenderQueue.RunnableCommand(() -> {
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.peek().getPositionMatrix().set(matrix4f);
         var1.accept(matrixstack);
      }));
   }

   public static HudPreviewItem on23(HudQueuedContext var0) {
      HudPreviewItem i11liiii1i11l11l11il = (HudPreviewItem)infoBoxes2().hudPreviewRenderQueueVar1654.var1435();
      i11liiii1i11l11l11il.hudQueuedContext = var0;
      return i11liiii1i11l11l11il;
   }

   public static void on23(HudPreviewItem var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list23, li1illlill.list22);
      li1illlill.list23.add(var0);
   }

   public static void UiAnimation(HudPreviewItem var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list28, li1illlill.list29);
      li1illlill.list28.add(var0);
   }

   public static void Easing(HudPreviewItem var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list26, li1illlill.list25);
      li1illlill.list26.add(var0);
   }

   public static void ColorAnimator(HudPreviewItem var0) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list21, li1illlill.list24);
      li1illlill.list21.add(var0);
   }

   public static void on23(HudQueuedContext var0, Matrix4f var1, int var2, int var3, int var4, int var5) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      HudPreviewRenderQueue_Bound li1illlill_l11liliill1iii1 = (HudPreviewRenderQueue_Bound)li1illlill.hudPreviewRenderQueueVar165.var1435();
      li1illlill_l11liliill1iii1.hudQueuedContext = var0;
      li1illlill_l11liliill1iii1.val378.set(var1);
      li1illlill_l11liliill1iii1.val379 = var2;
      li1illlill_l11liliill1iii1.val380 = var3;
      li1illlill_l11liliill1iii1.val381 = var4;
      li1illlill_l11liliill1iii1.val382 = var5;
      li1illlill.list20.add(li1illlill_l11liliill1iii1);
   }

   public static void float267() {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      if (!li1illlill.list20.isEmpty()) {
         li1illlill.list20.removeLast();
      }
   }

   public static void on23(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7, boolean var8, boolean var9
   ) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      HudPreviewRenderQueue.Context li1illlill_l1i1illlili = (HudPreviewRenderQueue.Context)li1illlill.hudPreviewRenderQueueVar1655.var1435();
      li1illlill_l1i1illlili.val124.set(var0);
      li1illlill_l1i1illlili.x = var1;
      li1illlill_l1i1illlili.y = var2;
      li1illlill_l1i1illlili.width = var3;
      li1illlill_l1i1illlili.height = var4;
      li1illlill_l1i1illlili.blurRadius = var5;
      li1illlill_l1i1illlili.val012 = var6;
      li1illlill_l1i1illlili.color = var7;
      li1illlill_l1i1illlili.boolean87 = var8;
      li1illlill_l1i1illlili.boolean86 = var9;
      li1illlill_l1i1illlili.val383 = Interface.interfaceField.boolean68();
      li1illlill_l1i1illlili.val384 = Interface.interfaceField.float30();
      li1illlill_l1i1illlili.val385 = Interface.interfaceField.float31();
      li1illlill_l1i1illlili.val386 = Interface.interfaceField.boolean69();
      li1illlill_l1i1illlili.val243 = li1illlill.modeSettingVar15921();
      li1illlill.list27.add(li1illlill_l1i1illlili);
   }

   public static void ItemRegistry(Matrix4f var0, float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list23, li1illlill.list22);
      HudPreviewRenderQueue.MatrixCommand li1illlill_l1iil11li = (HudPreviewRenderQueue.MatrixCommand)li1illlill.hudPreviewRenderQueueVar1652.var1435();
      li1illlill_l1iil11li.val387.set(var0);
      li1illlill_l1iil11li.x = var1;
      li1illlill_l1iil11li.y = var2;
      li1illlill_l1iil11li.width = var3;
      li1illlill_l1iil11li.height = var4;
      li1illlill_l1iil11li.val012 = var5;
      li1illlill_l1iil11li.color = var6;
      li1illlill.list23.add(li1illlill_l1iil11li);
   }

   public static void UiAnimation(Matrix4f var0, float var1, float var2, float var3, float var4, ArgbColor var5) {
      HudPreviewRenderQueue li1illlill = infoBoxes2();
      li1illlill.on23(li1illlill.list23, li1illlill.list22);
      HudPreviewRenderQueue.ClipCommand li1illlill_Var160 = (HudPreviewRenderQueue.ClipCommand)li1illlill.hudPreviewRenderQueueVar1653.var1435();
      li1illlill_Var160.val388.set(var0);
      li1illlill_Var160.x = var1;
      li1illlill_Var160.y = var2;
      li1illlill_Var160.width = var3;
      li1illlill_Var160.height = var4;
      li1illlill_Var160.color = var5;
      li1illlill.list23.add(li1illlill_Var160);
   }

   public static HudPreviewRenderQueue infoBoxes2() {
      if (!set14()) {
         throw new IllegalStateException("HudRenderQueue is not recording");
      } else {
         return hudPreviewRenderQueue2;
      }
   }

   public CornerRadius modeSettingVar15921() {
      float f = Interface.float212();
      if (f != this.float25) {
         this.float25 = f;
         this.val242 = CornerRadius.MovementInputEvent(f);
      }

      return this.val242;
   }

   public void on23(List<? super HudPreviewItem> var1, List<HudPreviewRenderQueue_Bound> var2) {
      int i = var2.size();
      int j = this.list20.size();
      if (i != 0 || j != 0) {
         int k = 0;

         while (k < i && k < j && var2.get(k) == this.list20.get(k)) {
            k++;
         }

         for (int l = i - 1; l >= k; l--) {
            var1.add(this.UiAnimation(var2.get(l).hudQueuedContext));
            var2.remove(l);
         }

         for (int i1 = k; i1 < j; i1++) {
            HudPreviewRenderQueue_Bound li1illlill_l11liliill1iii1 = (HudPreviewRenderQueue_Bound)this.list20.get(i1);
            var1.add(this.on23(li1illlill_l11liliill1iii1));
            var2.add(li1illlill_l11liliill1iii1);
         }
      }
   }

   public HudPreviewItem on23(HudPreviewRenderQueue_Bound var1) {
      HudPreviewItem i11liiii1i11l11l11il = (HudPreviewItem)this.hudPreviewRenderQueueVar1654.var1435();
      i11liiii1i11l11l11il.hudQueuedContext = var1.hudQueuedContext;
      i11liiii1i11l11l11il.var13Var159 = HudPreviewType.val244;
      i11liiii1i11l11l11il.matrix4f11.set(var1.val378);
      i11liiii1i11l11l11il.int350 = var1.val379;
      i11liiii1i11l11l11il.int351 = var1.val380;
      i11liiii1i11l11l11il.int352 = var1.val381;
      i11liiii1i11l11l11il.int353 = var1.val382;
      return i11liiii1i11l11l11il;
   }

   public HudPreviewItem UiAnimation(HudQueuedContext var1) {
      HudPreviewItem i11liiii1i11l11l11il = (HudPreviewItem)this.hudPreviewRenderQueueVar1654.var1435();
      i11liiii1i11l11l11il.hudQueuedContext = var1;
      i11liiii1i11l11l11il.var13Var159 = HudPreviewType.val245;
      i11liiii1i11l11l11il.matrix4f11.identity();
      return i11liiii1i11l11l11il;
   }

   public void modeSettingVar15922() {
      this.UiAnimation(this.list23, this.list22);
      this.UiAnimation(this.list28, this.list29);
      this.UiAnimation(this.list26, this.list25);
      this.UiAnimation(this.list21, this.list24);
   }

   public void UiAnimation(List<? super HudPreviewItem> var1, List<HudPreviewRenderQueue_Bound> var2) {
      for (int i = var2.size() - 1; i >= 0; i--) {
         var1.add(this.UiAnimation(var2.get(i).hudQueuedContext));
      }

      var2.clear();
   }

   public void flush() {
      if (!this.boolean49) {
         this.modeSettingVar15922();
         this.boolean49 = true;

         try {
            this.ChatMessageEvent(true);
            this.var14352();
            this.ChatMessageEvent(false);
            this.var14353();
            this.modeSettingVar15923();
            this.var14354();
            this.string29();
            this.float56();
         } finally {
            this.boolean49 = false;
            this.clear();
         }
      }
   }

   public void modeSettingVar15923() {
      for (int i = 0; i < this.list27.size(); i++) {
         HudPreviewRenderQueue.Context li1illlill_l1i1illlili = (HudPreviewRenderQueue.Context)this.list27.get(i);
         if (li1illlill_l1i1illlili.val383) {
            this.matrixStack2.peek().getPositionMatrix().set(li1illlill_l1i1illlili.val124);
            ShapeRenderer.UiAnimation(
               this.matrixStack2,
               identifier2,
               li1illlill_l1i1illlili.x,
               li1illlill_l1i1illlili.y,
               li1illlill_l1i1illlili.width,
               li1illlill_l1i1illlili.height,
               li1illlill_l1i1illlili.val012,
               new ArgbColor(255, 255, 255, 2),
               0.0F,
               0.0F,
               li1illlill_l1i1illlili.width / 1024.0F,
               li1illlill_l1i1illlili.height / 1024.0F
            );
         }
      }
   }

   public void clear() {
      this.list27.clear();
      this.list23.clear();
      this.list28.clear();
      this.list26.clear();
      this.list21.clear();
      this.list19.clear();
      this.list20.clear();
      this.list22.clear();
      this.list29.clear();
      this.list25.clear();
      this.list24.clear();
      this.hudPreviewRenderQueueVar1654.reset();
      this.hudPreviewRenderQueueVar1652.reset();
      this.hudPreviewRenderQueueVar1653.reset();
      this.hudPreviewRenderQueueVar1655.reset();
      this.hudPreviewRenderQueueVar165.reset();
   }

   public void ChatMessageEvent(boolean var1) {
      for (int i = 0; i < this.list27.size(); i++) {
         HudPreviewRenderQueue.Context li1illlill_l1i1illlili = (HudPreviewRenderQueue.Context)this.list27.get(i);
         if (li1illlill_l1i1illlili.boolean86 && li1illlill_l1i1illlili.val385 == var1) {
            ShapeRenderer.on23(
               li1illlill_l1i1illlili.val124,
               li1illlill_l1i1illlili.x,
               li1illlill_l1i1illlili.y,
               li1illlill_l1i1illlili.width,
               li1illlill_l1i1illlili.height,
               li1illlill_l1i1illlili.val386,
               li1illlill_l1i1illlili.val243
            );
         }
      }
   }

   public void var14352() {
      this.list19.clear();

      for (int i = 0; i < this.list27.size(); i++) {
         HudPreviewRenderQueue.Context li1illlill_l1i1illlili = (HudPreviewRenderQueue.Context)this.list27.get(i);
         if (li1illlill_l1i1illlili.boolean87) {
            if (li1illlill_l1i1illlili.val384) {
               ShapeRenderer.on23(
                  li1illlill_l1i1illlili.val124,
                  li1illlill_l1i1illlili.x,
                  li1illlill_l1i1illlili.y,
                  li1illlill_l1i1illlili.width,
                  li1illlill_l1i1illlili.height,
                  li1illlill_l1i1illlili.val012,
                  li1illlill_l1i1illlili.color,
                  true
               );
            } else {
               this.list19
                  .add(
                     new BlurRenderer.BlurCommand(
                        li1illlill_l1i1illlili.val124,
                        li1illlill_l1i1illlili.x,
                        li1illlill_l1i1illlili.y,
                        li1illlill_l1i1illlili.width,
                        li1illlill_l1i1illlili.height,
                        li1illlill_l1i1illlili.val012,
                        li1illlill_l1i1illlili.color
                     )
                  );
            }
         }
      }

      if (!this.list19.isEmpty()) {
         ZenithClient.on23().ModuleStateStore().ServiceException(this.list19);
      }
   }

   public void var14353() {
      for (int i = 0; i < this.list23.size(); i++) {
         HudPreviewRenderQueue.Command li1illlill_ii1il11l111ii11iil = (HudPreviewRenderQueue.Command)this.list23.get(i);
         if (li1illlill_ii1il11l111ii11iil instanceof HudPreviewRenderQueue.MatrixCommand li1illlill_l1iil11li) {
            this.rectBatch.flush();
            this.roundedRectBatch.on23(li1illlill_l1iil11li.val012);
            this.roundedRectBatch
               .Easing(
                  li1illlill_l1iil11li.val387,
                  li1illlill_l1iil11li.x,
                  li1illlill_l1iil11li.y,
                  li1illlill_l1iil11li.width,
                  li1illlill_l1iil11li.height,
                  li1illlill_l1iil11li.color
               );
         } else if (li1illlill_ii1il11l111ii11iil instanceof HudPreviewRenderQueue.ClipCommand li1illlill_Var160) {
            this.roundedRectBatch.flush();
            this.rectBatch.map44();
            this.rectBatch
               .Easing(
                  li1illlill_Var160.val388,
                  li1illlill_Var160.x,
                  li1illlill_Var160.y,
                  li1illlill_Var160.width,
                  li1illlill_Var160.height,
                  li1illlill_Var160.color
               );
         } else {
            this.roundedRectBatch.flush();
            this.rectBatch.flush();
            if (li1illlill_ii1il11l111ii11iil instanceof HudPreviewRenderQueue.ExecutableCommand li1illlill_liil11l111liil1ll) {
               li1illlill_liil11l111liil1ll.render();
            }
         }
      }

      this.roundedRectBatch.flush();
      this.rectBatch.flush();
   }

   public void var14354() {
      for (int i = 0; i < this.list28.size(); i++) {
         this.list28.get(i).render();
      }
   }

   public void string29() {
      for (int i = 0; i < this.list26.size(); i++) {
         ((HudPreviewRenderQueue.ExecutableCommand)this.list26.get(i)).render();
      }
   }

   public void float56() {
      for (int i = 0; i < this.list21.size(); i++) {
         ((HudPreviewRenderQueue.ExecutableCommand)this.list21.get(i)).render();
      }
   }


   public static final class MatrixCommand implements Command {
      final Matrix4f val387 = new Matrix4f();
      float x;
      float y;
      float width;
      float height;
      CornerRadius val012;
      ArgbColor color;

      public MatrixCommand() {
      }

      void var1436() {
         this.val012 = null;
         this.color = null;
      }
   }

   public static final class Context {
      final Matrix4f val124 = new Matrix4f();
      float x;
      float y;
      float width;
      float height;
      float blurRadius;
      CornerRadius val012;
      ArgbColor color;
      boolean boolean87;
      boolean boolean86;
      boolean val383;
      boolean val384;
      boolean val385;
      int val386;
      CornerRadius val243;

      public Context() {
      }

      void var1436() {
         this.val012 = null;
         this.color = null;
         this.val243 = null;
      }
   }

   public record RunnableCommand(Runnable runnable) implements ExecutableCommand {
      @Override
      public void render() {
         this.runnable.run();
      }

      public Runnable list42() {
         return this.runnable;
      }
   }

   public interface Command {
   }

   public static final class ClipCommand implements Command {
      final Matrix4f val388 = new Matrix4f();
      float x;
      float y;
      float width;
      float height;
      ArgbColor color;

      public ClipCommand() {
      }

      void var1436() {
         this.color = null;
      }
   }

   public static final class Batch<T> {
      public final ArrayList<T> arrayList = new ArrayList<>();
      public final Supplier<T> supplier2;
      public final Consumer<T> consumer;
      public int int178;

      Batch(Supplier<T> var1, Consumer<T> var2) {
         this.supplier2 = var1;
         this.consumer = var2;
      }

      T var1435() {
         if (this.int178 < this.arrayList.size()) {
            return this.arrayList.get(this.int178++);
         }

         Object object = this.supplier2.get();
         this.arrayList.add((T)object);
         this.int178++;
         return (T)object;
      }

      void reset() {
         for (int i = 0; i < this.int178; i++) {
            this.consumer.accept(this.arrayList.get(i));
         }

         this.int178 = 0;
      }
   }

   public interface ExecutableCommand extends Command {
      void render();
   }
}
