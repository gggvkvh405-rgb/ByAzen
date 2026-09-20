package org.zenith.core;

import com.darkmagician6.eventapi.EventManager;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import org.zenith.event.GuiWalkEvent;

public class TaskQueueThread {
   public Thread thread;
   public volatile boolean running;

   public void start() {
      if (!this.running) {
         this.running = true;
         this.thread = new Thread(() -> {
            while (this.running) {
               try {
                  try {
                     if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().execute(() -> EventManager.call(new GuiWalkEvent()));
                     }
                  } catch (Exception exception) {
                     exception.printStackTrace();
                  }

                  int i = ThreadLocalRandom.current().nextInt(40, 60);
                  Thread.sleep(i);
               } catch (InterruptedException var3) {
               }
            }
         }, "RandomEventCaller");
         this.thread.setDaemon(true);
         this.thread.start();
      }
   }

   public void stop() {
      this.running = false;
      if (this.thread != null) {
         this.thread.interrupt();
         this.thread = null;
      }
   }
}
