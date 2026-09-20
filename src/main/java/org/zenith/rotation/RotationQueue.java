package org.zenith.rotation;


import org.zenith.module.Module;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import net.minecraft.client.MinecraftClient;

public class RotationQueue<T> {
   public int int483 = 0;
   public final PriorityBlockingQueue<RotationQueue.QueueEntry<T>> priorityBlockingQueue = new PriorityBlockingQueue<>(
      11, Comparator.comparingInt(var0 -> -var0.int171)
   );

   public void ProfileItemBuilder(int var1) {
      this.int483 += var1;
   }

   public void tick() {
      this.ProfileItemBuilder(1);
   }

   public void on23(RotationQueue.QueueEntry<T> var1) {
      this.priorityBlockingQueue.removeIf(var1xx -> var1xx.module3 == var1.module3);
      var1.int213 = var1.int213 + this.int483;
      this.priorityBlockingQueue.add(var1);
   }

   public T BotGotoEntity() {
      RotationQueue.QueueEntry<T> lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil = this.priorityBlockingQueue.peek();
      if (lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil == null) {
         return null;
      }

      if (MinecraftClient.getInstance().isOnThread()) {
         while (
            lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil != null
               && (
                  lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.int213 <= this.int483
                     || !lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.module3.isEnabled()
               )
         ) {
            this.priorityBlockingQueue.poll();
            lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil = this.priorityBlockingQueue.peek();
         }
      }

      return lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil != null ? lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.call263 : null;
   }

   public RotationQueue.QueueEntry<T> PositionProvider() {
      RotationQueue.QueueEntry<T> lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil = this.priorityBlockingQueue.peek();
      if (lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil == null) {
         return null;
      }

      if (MinecraftClient.getInstance().isOnThread()) {
         while (
            lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil != null
               && (
                  lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.int213 <= this.int483
                     || !lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil.module3.isEnabled()
               )
         ) {
            this.priorityBlockingQueue.poll();
            lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil = this.priorityBlockingQueue.peek();
         }
      }

      return lll1iill1il1liiiiilli11i1l1l1_ii1il11l111ii11iil;
   }

   public void clear() {
      this.priorityBlockingQueue.clear();
   }


   public static class QueueEntry<T> {
      public int int213;
      public final int int171;
      public final Module module3;
      public final T call263;

      public QueueEntry(int var1, int var2, Module var3, T var4) {
         this.int213 = var1;
         this.int171 = var2;
         this.module3 = var3;
         this.call263 = var4;
      }
   }
}
