package com.example.novaclient.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<? extends Event>, List<MethodData>> subscribers = new ConcurrentHashMap<>();
    
    public void register(Object subscriber) {
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                if (method.getParameterCount() == 1) {
                    Class<?> eventClass = method.getParameterTypes()[0];
                    if (Event.class.isAssignableFrom(eventClass)) {
                        method.setAccessible(true);
                        EventHandler annotation = method.getAnnotation(EventHandler.class);
                        MethodData data = new MethodData(subscriber, method, annotation.priority());
                        subscribers.computeIfAbsent((Class<? extends Event>) eventClass, k -> new ArrayList<>()).add(data);
                        subscribers.get(eventClass).sort(Comparator.comparingInt(m -> -m.priority));
                    }
                }
            }
        }
    }
    
    public void unregister(Object subscriber) {
        subscribers.values().forEach(list -> list.removeIf(data -> data.instance == subscriber));
    }
    
    public void post(Event event) {
        List<MethodData> methods = subscribers.get(event.getClass());
        if (methods != null) {
            for (MethodData data : methods) {
                try {
                    data.method.invoke(data.instance, event);
                    if (event.isCancelled()) break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static class MethodData {
        final Object instance;
        final Method method;
        final int priority;
        
        MethodData(Object instance, Method method, int priority) {
            this.instance = instance;
            this.method = method;
            this.priority = priority;
        }
    }
}