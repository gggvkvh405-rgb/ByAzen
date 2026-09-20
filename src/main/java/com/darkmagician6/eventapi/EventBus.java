package com.darkmagician6.eventapi;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.EventStoppable;
import com.darkmagician6.eventapi.types.Priority;
import com.mojang.logging.LogUtils;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;

public final class EventBus {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<Class<?>, List<EventBus.ListenerMethod>> SCAN_CACHE = new ConcurrentHashMap<>();
   private static final long ERROR_LOG_INTERVAL_MS = 1000L;
   private static final Map<Class<?>, Long> LAST_ERROR_LOG_TIME = new ConcurrentHashMap<>();
   private final Map<Class<? extends Event>, List<EventBus.MethodData>> registry = new ConcurrentHashMap<>();

   public void register(Object var1) {
      for (EventBus.ListenerMethod var3 : scannedListeners(var1.getClass())) {
         this.addData(var3.eventClass, new EventBus.MethodData(var1, var3));
      }
   }

   public void register(Object var1, Class<? extends Event> var2) {
      for (EventBus.ListenerMethod var4 : scannedListeners(var1.getClass())) {
         if (var4.eventClass.equals(var2)) {
            this.addData(var2, new EventBus.MethodData(var1, var4));
         }
      }
   }

   public void unregister(Object var1) {
      for (List<EventBus.MethodData> var3 : this.registry.values()) {
         for (EventBus.MethodData var5 : var3) {
            if (var5.getSource().equals(var1)) {
               var3.remove(var5);
            }
         }
      }

      this.cleanMap(true);
   }

   public void unregister(Object var1, Class<? extends Event> var2) {
      if (this.registry.containsKey(var2)) {
         for (EventBus.MethodData var4 : this.registry.get(var2)) {
            if (var4.getSource().equals(var1)) {
               this.registry.get(var2).remove(var4);
            }
         }

         this.cleanMap(true);
      }
   }

   private static List<EventBus.ListenerMethod> scannedListeners(Class<?> var0) {
      return SCAN_CACHE.computeIfAbsent(var0, EventBus::scanListenerMethods);
   }

   private static List<EventBus.ListenerMethod> scanListenerMethods(Class<?> var0) {
      ArrayList var1 = new ArrayList();

      for (Method var5 : var0.getDeclaredMethods()) {
         if (!isMethodBad(var5)) {
            Class var6 = var5.getParameterTypes()[0];
            var1.add(new EventBus.ListenerMethod(var5, var6, var5.getAnnotation(EventTarget.class).value(), createInvoker(var5)));
         }
      }

      return var1;
   }

   private static ListenerInvoker createInvoker(Method var0) {
      try {
         var0.setAccessible(true);
      } catch (Throwable var8) {
      }

      Lookup var1 = MethodHandles.lookup();
      Lookup var2 = var1;

      try {
         var2 = MethodHandles.privateLookupIn(var0.getDeclaringClass(), var1);
      } catch (Throwable var7) {
      }

      MethodHandle var3 = null;

      try {
         var3 = var2.unreflect(var0);
      } catch (Throwable var6) {
      }

      if (var3 != null && !Modifier.isStatic(var0.getModifiers())) {
         try {
            CallSite var11 = LambdaMetafactory.metafactory(
               var2,
               "invoke",
               MethodType.methodType(ListenerInvoker.class),
               MethodType.methodType(void.class, Object.class, Object.class),
               var3,
               var3.type().changeReturnType(void.class)
            );
            return (ListenerInvoker)var11.getTarget().invokeExact();
         } catch (Throwable var10) {
         }
      }

      if (var3 != null) {
         try {
            MethodHandle var4 = var3;
            if (Modifier.isStatic(var0.getModifiers())) {
               var4 = MethodHandles.dropArguments(var4, 0, Object.class);
            }

            MethodHandle var5 = var4.asType(MethodType.methodType(void.class, Object.class, Object.class));
            return var5::invokeExact;
         } catch (Throwable var9) {
         }
      }

      return (var1x, var2x) -> var0.invoke(var1x, var2x);
   }

   private void addData(Class<? extends Event> var1, final EventBus.MethodData var2) {
      if (this.registry.containsKey(var1)) {
         if (!this.registry.get(var1).contains(var2)) {
            this.registry.get(var1).add(var2);
            this.sortListValue(var1);
         }
      } else {
         this.registry.put(var1, new CopyOnWriteArrayList<EventBus.MethodData>() {
            private static final long serialVersionUID = 666L;

            {
               this.add(var2);
            }
         });
      }
   }

   public void copyNonModuleListenersFrom(EventBus var1) {
      for (Entry<Class<? extends Event>, List<EventBus.MethodData>> var3 : var1.registry.entrySet()) {
         for (EventBus.MethodData var5 : var3.getValue()) {
            if (shouldCopyToBotBus(var5.getSource())) {
               this.addData((Class<? extends Event>)var3.getKey(), var5);
            }
         }
      }
   }

   private static boolean shouldCopyToBotBus(Object var0) {
      String[] excludedTypes = {
         "org.zenith.lII1lll1l1lI1II1IIIllII",
         "org.zenith.Ill1I111I1l1",
         "org.zenith.I1II1lIIl11ll1",
         "org.zenith.ll1IlIIl1l1lI11lI111l"
      };

      for (String typeName : excludedTypes) {
         try {
            Class<?> type = Class.forName(typeName, false, var0.getClass().getClassLoader());
            if (type.isInstance(var0)) {
               return false;
            }
         } catch (ClassNotFoundException ignored) {
         }
      }

      return true;
   }

   public void removeEntry(Class<? extends Event> var1) {
      this.registry.remove(var1);
   }

   public void cleanMap(boolean var1) {
      if (!var1) {
         this.registry.clear();
      } else {
         for (Entry var3 : this.registry.entrySet()) {
            if (((List)var3.getValue()).isEmpty()) {
               this.registry.remove(var3.getKey(), var3.getValue());
            }
         }
      }
   }

   public void clear() {
      this.registry.clear();
   }

   private void sortListValue(Class<? extends Event> var1) {
      CopyOnWriteArrayList var2 = new CopyOnWriteArrayList();

      for (byte var6 : Priority.VALUE_ARRAY) {
         for (EventBus.MethodData var8 : this.registry.get(var1)) {
            if (var8.getPriority() == var6) {
               var2.add(var8);
            }
         }
      }

      this.registry.put(var1, var2);
   }

   private static boolean isMethodBad(Method var0) {
      return var0.getParameterTypes().length != 1 || !var0.isAnnotationPresent(EventTarget.class);
   }

   public boolean hasListeners(Class<? extends Event> var1) {
      List<EventBus.MethodData> var2 = this.registry.get(var1);
      return var2 != null && !var2.isEmpty();
   }

   public Event call(Event var1) {
      try {
         List<EventBus.MethodData> var2 = this.registry.get(var1.getClass());
         if (var2 != null) {
            if (var1 instanceof EventStoppable var3) {
               for (EventBus.MethodData var5 : var2) {
                  try {
                     var5.invoke(var1);
                  } catch (Throwable var8) {
                     logListenerError(var5, var8);
                     break;
                  }

                  if (var3.isStopped()) {
                     break;
                  }
               }
            } else {
               for (EventBus.MethodData var11 : var2) {
                  try {
                     var11.invoke(var1);
                  } catch (Throwable var7) {
                     logListenerError(var11, var7);
                  }
               }
            }
         }
      } catch (Throwable var9) {
         logListenerError(null, var9);
      }

      return var1;
   }

   private static void logListenerError(EventBus.MethodData var0, Throwable var1) {
      Class var2 = var0 != null ? var0.getSource().getClass() : EventBus.class;
      long var3 = System.currentTimeMillis();
      Long var5 = LAST_ERROR_LOG_TIME.get(var2);
      if (var5 == null || var3 - var5 >= 1000L) {
         LAST_ERROR_LOG_TIME.put(var2, var3);
         if (var0 != null) {
            LOGGER.error("Ошибка в слушателе {}#{}", new Object[]{var2.getName(), var0.getTarget().getName(), var1});
         } else {
            LOGGER.error("Ошибка при диспатче события", var1);
         }
      }
   }

   private static final class ListenerMethod {
      private final Method target;
      private final Class<? extends Event> eventClass;
      private final byte priority;
      private final ListenerInvoker invoker;

      private ListenerMethod(Method var1, Class<? extends Event> var2, byte var3, ListenerInvoker var4) {
         this.target = var1;
         this.eventClass = var2;
         this.priority = var3;
         this.invoker = var4;
      }
   }

   private static final class MethodData {
      private final Object source;
      private final Method target;
      private final byte priority;
      private final ListenerInvoker invoker;

      public MethodData(Object var1, EventBus.ListenerMethod var2) {
         this.source = var1;
         this.target = var2.target;
         this.priority = var2.priority;
         this.invoker = var2.invoker;
      }

      public void invoke(Event var1) throws Throwable {
         this.invoker.invoke(this.source, var1);
      }

      public Object getSource() {
         return this.source;
      }

      public Method getTarget() {
         return this.target;
      }

      public byte getPriority() {
         return this.priority;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof EventBus.MethodData var2) ? false : this.source.equals(var2.source) && this.target.equals(var2.target);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.source, this.target);
      }
   }
}
