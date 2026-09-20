package org.zenith.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public class TaskQueueWorker {
   public final Class<?> classField;
   public static final AtomicLong category = new AtomicLong(1L);
   public final long long120 = category.getAndIncrement();
   public final Queue<QueueEntry<?>> queue4 = new LinkedList<>();
   public final LinkedList<QueueEntry<?>> linkedList = new LinkedList<>();
   public int int210 = 0;
   public int int211 = Integer.MAX_VALUE;
   public int int171 = 0;

   public TaskQueueWorker() {
      this.classField = TaskQueue.class;
   }

   public TaskQueueWorker(Class<?> var1) {
      this.classField = var1;
   }

   public TaskQueueWorker StringCodec(int var1) {
      this.int211 = Math.max(1, var1);
      TaskQueue.logger.finest(this.AvatarRenderer() + " set maxIdleTicks=" + this.int211);
      return this;
   }

   public <E> TaskQueueWorker on23(Class<E> var1, CapacityLimited<E> var2) {
      return this.on23(var1, var2, 0);
   }

   public <E> TaskQueueWorker on23(Class<E> var1, CapacityLimited<E> var2, int var3) {
      QueueEntry ll1ill11111i_l1i1illlili_ii1il11l111ii11iil = new QueueEntry<>(var1, var2, this.queue4.size() + 1, false, var3);
      if (var3 > this.ScoreboardUtils()) {
         LinkedList linkedlist = (LinkedList)this.queue4;
         linkedlist.addFirst(ll1ill11111i_l1i1illlili_ii1il11l111ii11iil);
      } else {
         this.queue4.add(ll1ill11111i_l1i1illlili_ii1il11l111ii11iil);
      }

      TaskQueue.logger
         .finest(this.AvatarRenderer() + " scheduled step#" + ll1ill11111i_l1i1illlili_ii1il11l111ii11iil.int212 + " for event=" + var1.getSimpleName());
      return this;
   }

   public int ScoreboardUtils() {
      int i = Integer.MIN_VALUE;

      for (QueueEntry ll1ill11111i_l1i1illlili_ii1il11l111ii11iil : this.queue4) {
         if (ll1ill11111i_l1i1illlili_ii1il11l111ii11iil.getMaxLength > i) {
            i = ll1ill11111i_l1i1illlili_ii1il11l111ii11iil.getMaxLength;
         }
      }

      return i == Integer.MIN_VALUE ? 0 : i;
   }

   public <E> TaskQueueWorker UiAnimation(Class<E> var1, CapacityLimited<E> var2) {
      QueueEntry ll1ill11111i_l1i1illlili_ii1il11l111ii11iil = new QueueEntry<>(var1, var2, this.linkedList.size() + 1, true, 0);
      this.linkedList.add(ll1ill11111i_l1i1illlili_ii1il11l111ii11iil);
      TaskQueue.logger
         .finest(
            this.AvatarRenderer() + " scheduled PERSISTENT step#" + ll1ill11111i_l1i1illlili_ii1il11l111ii11iil.int212 + " for event=" + var1.getSimpleName()
         );
      return this;
   }

   public boolean ColorAnimator(Object var1) {
      if (var1 == null) {
         return false;
      }

      QueueEntry ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx = this.queue4.peek();
      if (ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx == null) {
         TaskQueue.logger.info(this.AvatarRenderer() + "task COMPLETE.");
         return true;
      }

      for (QueueEntry<?> zenithHandler : this.linkedList) {
         if (zenithHandler.class2.isInstance(var1)) {
            try {
               zenithHandler.ItemRegistry(var1);
            } catch (Throwable throwable1) {
               throwable1.printStackTrace();
            }
         }
      }

      boolean flag = false;
      if (ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx.class2.isInstance(var1)) {
         boolean flag1;
         try {
            flag1 = ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx.ItemRegistry(var1);
         } catch (Throwable throwable) {
            throwable.printStackTrace();
            flag1 = true;
         }

         if (flag1) {
            this.queue4.poll();
            flag = true;
            TaskQueue.logger.info(this.AvatarRenderer() + " step#" + ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx.int212 + " DONE size=" + this.queue4.size());
         } else {
            TaskQueue.logger.finest(this.AvatarRenderer() + " step#" + ll1ill11111i_l1i1illlili_ii1il11l111ii11iilx.int212 + " NOT done");
         }
      }

      if (flag) {
         this.int210 = 0;
         boolean flag2 = this.queue4.isEmpty();
         if (flag2) {
            TaskQueue.logger.info(this.AvatarRenderer() + "TASK COMPLETE.");
         }

         return flag2;
      } else {
         if (this.int211 != Integer.MAX_VALUE) {
            this.int210++;
         }

         if (this.int210 > this.int211) {
            this.queue4.clear();
            this.linkedList.clear();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isCompleted() {
      boolean flag = this.queue4.isEmpty();
      TaskQueue.logger.finest(this.AvatarRenderer() + " isCompleted() -> " + flag);
      return flag;
   }

   public String AvatarRenderer() {
      return "[Task#" + this.long120 + "@" + (this.classField != null ? this.classField.getSimpleName() : "Unknown") + "]";
   }

   @Override
   public String toString() {
      return "ScriptTask{id="
         + this.long120
         + ", owner="
         + (this.classField != null ? this.classField.getSimpleName() : "null")
         + ", stepsLeft="
         + this.queue4.size()
         + ", persistent="
         + this.linkedList.size()
         + ", idleTicks="
         + this.int210
         + "/"
         + this.int211
         + ", priority="
         + this.int171
         + "}";
   }

   public Class<?> ScrollHandler() {
      QueueEntry ll1ill11111i_l1i1illlili_ii1il11l111ii11iil = this.queue4.peek();
      return ll1ill11111i_l1i1illlili_ii1il11l111ii11iil == null ? null : ll1ill11111i_l1i1illlili_ii1il11l111ii11iil.class2;
   }

   public long getId() {
      return this.long120;
   }

   public Class<?> SearchBox() {
      return this.classField;
   }

   public int TextureIdFactory() {
      return this.int171;
   }

   public void FileLogger(int var1) {
      this.int171 = var1;
   }
}
