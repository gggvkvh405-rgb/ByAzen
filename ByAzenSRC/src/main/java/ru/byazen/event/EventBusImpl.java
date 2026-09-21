/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.byazen.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.byazen.event.Event;
import ru.byazen.event.EventBus;
import ru.byazen.event.EventListener;
import ru.byazen.event.RegisteredListener;

public class EventBusImpl
implements EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusImpl.class);
    private final Map<Class<?>, List<RegisteredListener<?>>> listenersByEvent = new ConcurrentHashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T extends Event> void subscribe(Class<T> eventType, EventListener<? super T> listener, int priority) {
        List listeners;
        List list = listeners = this.listenersByEvent.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList());
        synchronized (list) {
            int index;
            for (index = 0; index < listeners.size() && ((RegisteredListener)listeners.get(index)).priority() <= priority; ++index) {
            }
            listeners.add(index, new RegisteredListener<T>(listener, priority));
        }
    }

    @Override
    public <T extends Event> void subscribe(Class<T> eventType, EventListener<? super T> listener) {
        this.subscribe(eventType, listener, 0);
    }

    @Override
    public <T extends Event> void post(T event) {
        for (RegisteredListener listener : this.listenersByEvent.getOrDefault(event.getClass(), List.of())) {
            try {
                listener.dispatch(event);
            }
            catch (RuntimeException exception) {
                LOGGER.error("Event listener failed for {}", (Object)event.getClass().getName(), (Object)exception);
            }
        }
    }
}

