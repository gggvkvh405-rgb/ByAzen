package org.zenith.base.bot.client;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.EventStoppable;
import com.darkmagician6.eventapi.types.Priority;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BotEventBus {
   public final Map<Class<? extends Event>, List<BotEventBus_MethodData>> registry = new HashMap<>();

   public Event call(Event var1) {
      try {
         List<BotEventBus_MethodData> list = this.registry.get(var1.getClass());
         if (list == null) {
            return var1;
         }

         if (var1 instanceof EventStoppable eventstoppable) {
            for (BotEventBus_MethodData boteventbus_methoddata : list) {
               boteventbus_methoddata.target().invoke(boteventbus_methoddata.source(), var1);
               if (eventstoppable.isStopped()) {
                  break;
               }
            }
         } else {
            for (BotEventBus_MethodData boteventbus_methoddata1 : list) {
               try {
                  boteventbus_methoddata1.target().invoke(boteventbus_methoddata1.source(), var1);
               } catch (Exception exception) {
                  exception.printStackTrace();
               }
            }
         }
      } catch (Exception exception1) {
         exception1.printStackTrace();
      }

      return var1;
   }

   public void register(Object var1) {
      for (Method method : var1.getClass().getDeclaredMethods()) {
         if (!isMethodBad(method)) {
            this.register(method, var1);
         }
      }
   }

   public void register(Object var1, Class<? extends Event> var2) {
      for (Method method : var1.getClass().getDeclaredMethods()) {
         if (!isMethodBad(method, var2)) {
            this.register(method, var1);
         }
      }
   }

   public void unregister(Object var1) {
      for (List<BotEventBus_MethodData> list : this.registry.values()) {
         list.removeIf(var1xx -> var1xx.source() == var1);
      }

      this.cleanMap(true);
   }

   public void register(Method var1, Object var2) {
      if (var1.getParameterCount() == 1) {
         Class oclass = var1.getParameterTypes()[0];
         BotEventBus_MethodData boteventbus_methoddata = new BotEventBus_MethodData(var2, var1, var1.getAnnotation(EventTarget.class).value());
         if (!boteventbus_methoddata.target().isAccessible()) {
            boteventbus_methoddata.target().setAccessible(true);
         }

         this.registry.computeIfAbsent(oclass, var0 -> new CopyOnWriteArrayList<>());
         List<BotEventBus_MethodData> list = this.registry.get(oclass);
         if (!list.contains(boteventbus_methoddata)) {
            list.add(boteventbus_methoddata);
            this.sortListValue(oclass);
         }
      }
   }

   public void sortListValue(Class<? extends Event> var1) {
      CopyOnWriteArrayList copyonwritearraylist = new CopyOnWriteArrayList();

      for (byte b0 : Priority.VALUE_ARRAY) {
         for (BotEventBus_MethodData boteventbus_methoddata : this.registry.get(var1)) {
            if (boteventbus_methoddata.priority() == b0) {
               copyonwritearraylist.add(boteventbus_methoddata);
            }
         }
      }

      this.registry.put(var1, copyonwritearraylist);
   }

   public void cleanMap(boolean var1) {
      Iterator<Entry<Class<? extends Event>, List<BotEventBus_MethodData>>> iterator = this.registry.entrySet().iterator();

      while (iterator.hasNext()) {
         if (!var1 || iterator.next().getValue().isEmpty()) {
            iterator.remove();
         }
      }
   }

   public static boolean isMethodBad(Method var0) {
      return var0.getParameterTypes().length != 1 || !var0.isAnnotationPresent(EventTarget.class);
   }

   public static boolean isMethodBad(Method var0, Class<? extends Event> var1) {
      return isMethodBad(var0) || !var0.getParameterTypes()[0].equals(var1);
   }
}
