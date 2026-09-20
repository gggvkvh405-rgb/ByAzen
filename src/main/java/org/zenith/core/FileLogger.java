package org.zenith.core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;

public final class FileLogger {
   public static final DateTimeFormatter FriendFilter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   public static final ConcurrentLinkedQueue<String> NpcCloneManager = new ConcurrentLinkedQueue<>();
   private static volatile boolean PlayerStateService;

   public static void log(String var0) {
      NpcCloneManager.add(LocalTime.now().format(FriendFilter) + " " + var0);
      EventClickSlotHook();
   }

   public static String trim(String var0) {
      if (var0 == null) {
         return "null";
      }

      String s = var0.trim().replace('\n', ' ');
      return s.length() > 140 ? s.substring(0, 140) + "..." : s;
   }

   public static void EventClickSlotHook() {
      if (!PlayerStateService) {
         synchronized (FileLogger.class) {
            if (!PlayerStateService) {
               PlayerStateService = true;
               ScheduledExecutorService scheduledexecutorservice = Executors.newSingleThreadScheduledExecutor(var0 -> {
                  Thread thread = new Thread(var0, "captcha-log");
                  thread.setDaemon(true);
                  return thread;
               });
               scheduledexecutorservice.scheduleWithFixedDelay(FileLogger::flush, 0L, 400L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   public static void flush() {
      if (!NpcCloneManager.isEmpty()) {
         ArrayList arraylist = new ArrayList();

         String s;
         while ((s = NpcCloneManager.poll()) != null) {
            arraylist.add(s);
         }

         try {
            MinecraftClient minecraftclient = MinecraftClient.getInstance();
            Path path = minecraftclient.runDirectory.toPath().resolve("captcha.log");
            Files.write(path, arraylist, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Throwable var4) {
         }
      }
   }
}
