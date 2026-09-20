package org.zenith.core;

@FunctionalInterface
public interface CapacityLimited<E> {
   boolean accept(E var1);
}
