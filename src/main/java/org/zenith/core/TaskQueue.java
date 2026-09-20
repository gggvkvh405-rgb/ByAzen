package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.GuiWalkEvent;
import org.zenith.event.EventInjectHandleInputEvents;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.PacketEvent;
import org.zenith.event.RefreshCacheEvent;

public class TaskQueue {
   public static final Logger logger = Logger.getLogger("org.zenith.TaskQueue");
   public final LinkedList<TaskQueueWorker> linkedList2 = new LinkedList<>();

   public LinkedList<TaskQueueWorker> ParticleRenderer() {
      return this.linkedList2;
   }

   public TaskQueue() {
      this(true);
   }

   protected TaskQueue(boolean var1) {
      if (var1) {
         EventManager.register(this);
         new TaskQueueThread().start();
      }
   }

   public void Easing(Object var1) {
      if (var1 != null) {
         LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
         TaskQueueWorker ll1ill11111i_l1i1illlili;
         synchronized (linkedlist) {
            ll1ill11111i_l1i1illlili = linkedlist.peek();
         }

         if (ll1ill11111i_l1i1illlili != null) {
            boolean flag = true;

            try {
               flag = ll1ill11111i_l1i1illlili.ColorAnimator(var1);
            } catch (Throwable throwable) {
               flag = false;
            }

            if (flag) {
               synchronized (linkedlist) {
                  linkedlist.poll();
               }
            }
         }
      }
   }

   @EventTarget
   public void on23(GuiWalkEvent var1) {
      this.Easing(var1);
   }

   @EventTarget
   public void on23(RotationUpdateStartEvent var1) {
      this.Easing(var1);
   }

   @EventTarget(4)
   public void on23(MovementInputEvent var1) {
      this.Easing(var1);
   }

   @EventTarget(0)
   public void Easing(EventTick var1) {
      this.Easing((Object)var1);
   }

   @EventTarget(0)
   public void on23(PacketEvent var1) {
      this.Easing(var1);
   }

   @EventTarget(3)
   public void on23(RefreshCacheEvent var1) {
      this.Easing(var1);
   }

   @EventTarget(0)
   public void on23(EventInjectHandleInputEvents var1) {
      this.Easing(var1);
   }

   public TaskQueueWorker on23(Class<?> var1) {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         for (TaskQueueWorker ll1ill11111i_l1i1illlili : linkedlist) {
            if (ll1ill11111i_l1i1illlili.SearchBox() == var1) {
               return ll1ill11111i_l1i1illlili;
            }
         }

         return new TaskQueueWorker(var1);
      }
   }

   public void on23(TaskQueueWorker var1) {
      this.on23(var1, 0);
   }

   public void on23(TaskQueueWorker var1, int var2) {
      if (var1 != null) {
         LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
         synchronized (linkedlist) {
            boolean flag = linkedlist.stream().anyMatch(var1xx -> var1xx == var1);
            if (!flag) {
               var1.FileLogger(var2);
               if (var2 > this.ParticleTextures()) {
                  if (!linkedlist.isEmpty()) {
                     linkedlist.add(1, var1);
                  } else {
                     linkedlist.addFirst(var1);
                  }
               } else {
                  linkedlist.add(var1);
               }
            }
         }
      }
   }

   public int ParticleTextures() {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      int i = Integer.MIN_VALUE;

      for (TaskQueueWorker ll1ill11111i_l1i1illlili : linkedlist) {
         if (ll1ill11111i_l1i1illlili.TextureIdFactory() > i) {
            i = ll1ill11111i_l1i1illlili.TextureIdFactory();
         }
      }

      return i == Integer.MIN_VALUE ? 0 : i;
   }

   public void BotGuardEntity() {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         linkedlist.clear();
         logger.warning("All tasks cleared manually. tasks.size=0");
      }
   }

   public void UiAnimation(Class<?> var1) {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         linkedlist.removeIf(var1xx -> var1xx.SearchBox() == var1);
         logger.info("Tasks cleared for owner: " + var1.getSimpleName());
      }
   }

   public int SelectionOutline() {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         return linkedlist.size();
      }
   }

   public boolean ImageEncoder() {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         boolean flag = linkedlist.isEmpty();
         logger.finest("isFinished() -> " + flag);
         return flag;
      }
   }

   public boolean Easing(Class<?> var1) {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         return linkedlist.stream().filter(var1xx -> var1xx.SearchBox() == var1).allMatch(TaskQueueWorker::isCompleted);
      }
   }

   public boolean ColorAnimator(Class<?> var1) {
      LinkedList<TaskQueueWorker> linkedlist = this.ParticleRenderer();
      synchronized (linkedlist) {
         TaskQueueWorker ll1ill11111i_l1i1illlili = linkedlist.peek();
         if (ll1ill11111i_l1i1illlili == null) {
            return false;
         }

         Class oclass = ll1ill11111i_l1i1illlili.ScrollHandler();
         return oclass == var1;
      }
   }
}
