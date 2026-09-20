package com.darkmagician6.eventapi;

import com.darkmagician6.eventapi.events.Event;

public final class EventManager {
   private static final EventBus MAIN = new EventBus();

   private EventManager() {
   }

   public static EventBus main() {
      return MAIN;
   }

   public static void register(Object var0) {
      MAIN.register(var0);
   }

   public static void register(Object var0, Class<? extends Event> var1) {
      MAIN.register(var0, var1);
   }

   public static void unregister(Object var0) {
      MAIN.unregister(var0);
   }

   public static void unregister(Object var0, Class<? extends Event> var1) {
      MAIN.unregister(var0, var1);
   }

   public static void removeEntry(Class<? extends Event> var0) {
      MAIN.removeEntry(var0);
   }

   public static void cleanMap(boolean var0) {
      MAIN.cleanMap(var0);
   }

   public static boolean hasListeners(Class<? extends Event> var0) {
      return MAIN.hasListeners(var0);
   }

   public static Event call(Event var0) {
      return MAIN.call(var0);
   }
}
