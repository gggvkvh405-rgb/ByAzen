package com.darkmagician6.eventapi;

@FunctionalInterface
public interface ListenerInvoker {
   void invoke(Object var1, Object var2) throws Throwable;
}
