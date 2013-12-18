/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.event;

import static js.lang.Global.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import jsx.bwt.UIAction;
import jsx.bwt.UIEvent;
import kiss.Disposable;
import kiss.model.ClassUtil;

/**
 * @version 2013/10/09 15:53:49
 */
public class Publishable {

    /** The global event bus. */
    public static final Publishable Global = new Publishable();

    /** The cache for event types. */
    private static final Map<Class, Set<Class<?>>> cache = new HashMap();

    /** The actual listeners holder. */
    private Map<Object, List<Listener>> holder;

    /**
     * <p>
     * Publish the specified event.
     * </p>
     * 
     * @param event
     */
    public final void publish(Object event) {
        if (holder != null && event != null) {
            Set types;

            if (event instanceof UIEvent) {
                types = Collections.singleton(((UIEvent) event).action);
            } else {
                types = cache.computeIfAbsent(event.getClass(), type -> ClassUtil.getTypes(type));
            }

            for (Object type : types) {
                if (holder != null) {
                    List<Listener> subscribers = holder.get(type);

                    if (subscribers != null) {
                        for (Listener subscriber : subscribers) {
                            subscriber.accept(event);
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Start subscribing events from which the specified {@link Publishable} emits.
     * </p>
     * 
     * @param subscribable A target event subscriber.
     */
    public final void register(Object subscribable) {
        if (subscribable != null) {
            for (Entry<Method, List<Annotation>> entry : ClassUtil.getAnnotations(subscribable.getClass()).entrySet()) {
                for (Annotation annotation : entry.getValue()) {
                    for (Info info : collect(annotation)) {
                        Object eventType;
                        Method method = entry.getKey();

                        if (method.getParameterTypes().length == 1) {
                            eventType = ClassUtil.wrap(method.getParameterTypes()[0]);
                        } else {
                            eventType = info.type;
                        }

                        Listener listener = new Invoker(subscribable, method, info.abort);

                        if (holder == null) {
                            holder = new HashMap();
                            startListening(Object.class);
                        }

                        List<Listener> subscribers = holder.get(eventType);

                        if (subscribers == null) {
                            subscribers = new CopyOnWriteArrayList();
                            holder.put(eventType, subscribers);

                            startListening(eventType);
                        } else {
                            for (Listener registered : subscribers) {
                                if (registered.equals(subscribable, method)) {
                                    return;
                                }
                            }
                        }

                        // ===========================
                        // Execution Count Wrapper
                        // ===========================
                        int count = info.count;

                        if (0 < count) {
                            listener = new Count(count, this, subscribable, listener);
                        }

                        // ===========================
                        // Timing Related Wrappers
                        // ===========================
                        long time = info.delay;

                        if (0 < time) {
                            listener = new Delay(time, listener);
                        }

                        time = info.throttle;

                        if (0 < time) {
                            listener = new Throttle(time, listener);
                        }

                        time = info.debounce;

                        if (0 < time) {
                            listener = new Debounce(time, listener);
                        }

                        // ===========================
                        // UIAction Wrapper
                        // ===========================
                        if (info.type instanceof UIAction) {
                            UIAction action = (UIAction) info.type;

                            if (0 < action.code) {
                                listener = new UIBind(action, listener);
                            }
                        }

                        subscribers.add(listener);
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Stop subscribing events from which the specified {@link Publishable} emits.
     * </p>
     * 
     * @param subscribable A target event subscriber.
     */
    public final void unregister(Object subscribable) {
        if (holder != null && subscribable != null) {
            for (Entry<Method, List<Annotation>> entry : ClassUtil.getAnnotations(subscribable.getClass()).entrySet()) {
                for (Annotation annotation : entry.getValue()) {
                    for (Info info : collect(annotation)) {
                        Object eventType;
                        Method method = entry.getKey();

                        if (method.getParameterTypes().length == 1) {
                            eventType = ClassUtil.wrap(method.getParameterTypes()[0]);
                        } else {
                            eventType = info.type;
                        }

                        List<Listener> subscribers = holder.get(eventType);

                        if (subscribers != null) {
                            for (int i = subscribers.size() - 1; 0 <= i; i--) {
                                Listener subscliber = subscribers.get(i);

                                if (subscliber.equals(subscribable, null)) {
                                    subscribers.remove(i);

                                    if (subscribers.isEmpty()) {
                                        holder.remove(eventType);
                                        stopListening(eventType);

                                        if (holder.isEmpty()) {
                                            holder = null;
                                            stopListening(Object.class);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>
     * This method is called whenever this event target starts listening event.
     * </p>
     */
    protected void startListening(Object type) {
    }

    /**
     * <p>
     * This method is called whenever this event target stops listening event.
     * </p>
     */
    protected void stopListening(Object type) {
    }

    /**
     * <p>
     * Helper method to collect all lisnteners.
     * </p>
     * 
     * @param annotation
     * @return
     */
    private List<Info> collect(Annotation annotation) {
        Class type = annotation.annotationType();
        List<Info> infos = new ArrayList();

        if (type == Subscribe.class) {
            infos.add(new Info((Subscribe) annotation));
        } else if (type == Subscribes.class) {
            for (Subscribe subscribe : ((Subscribes) annotation).value()) {
                infos.add(new Info(subscribe));
            }
        } else if (type == SubscribeUI.class) {
            infos.add(new Info((SubscribeUI) annotation));
        } else if (type == SubscribeUIs.class) {
            for (SubscribeUI subscribe : ((SubscribeUIs) annotation).value()) {
                infos.add(new Info(subscribe));
            }
        }
        return infos;
    }

    /**
     * @version 2013/12/18 15:26:58
     */
    private static class Info {

        /** The event type. */
        private Object type;

        /**
         * <p>
         * Set the execution debounce time (ms).
         * </p>
         * 
         * @return A time (ms);
         */
        private long debounce;

        /**
         * <p>
         * Set the execution throttle time (ms).
         * </p>
         * 
         * @return A time (ms);
         */
        private long throttle;

        /**
         * <p>
         * Set the execution delay time (ms).
         * </p>
         * 
         * @return A time (ms);
         */
        private long delay;

        /**
         * <p>
         * Set a number of execution.
         * </p>
         * 
         * @return
         */
        private int count;

        /**
         * <p>
         * Stop event propagation and default behavior. {@link UIEvent#stopPropagation()} and
         * {@link UIEvent#preventDefault()} methods will be called.
         * </p>
         * 
         * @return The <code>true</code> will stop the current processing event.
         */
        private boolean abort;

        /**
         * @param subscribe
         */
        private Info(Subscribe subscribe) {
            this.type = ClassUtil.wrap(subscribe.value());
            this.debounce = subscribe.debounce();
            this.throttle = subscribe.throttle();
            this.delay = subscribe.delay();
            this.count = subscribe.count();
            this.abort = subscribe.abort();
        }

        /**
         * @param subscribe
         */
        private Info(SubscribeUI subscribe) {
            this.type = subscribe.type();
            this.debounce = subscribe.debounce();
            this.throttle = subscribe.throttle();
            this.delay = subscribe.delay();
            this.count = subscribe.count();
            this.abort = subscribe.abort();
        }
    }

    /**
     * @version 2013/12/18 9:29:13
     */
    private static abstract class Listener {

        /** The delegator. */
        protected Listener delegator;

        /**
         * <p>
         * Receieve message.
         * </p>
         * 
         * @param event A message object.
         */
        protected abstract void accept(Object event);

        /**
         * <p>
         * Test whether the specified event listener is my consumer or not.
         * </p>
         * 
         * @param instance A target listener.
         * @param method A target listener method.
         * @return A result.
         */
        protected boolean equals(Object instance, Method method) {
            return delegator.equals(instance, method);
        }
    }

    /**
     * @version 2013/12/18 9:30:25
     */
    private static class Invoker extends Listener {

        /** The listener instance. */
        private final Object instance;

        /** The listener method. */
        private final Method method;

        /** The parameter flag. */
        private final boolean hasParam;

        /** The event termination flag. */
        private final boolean abort;

        /**
         * @param instance A {@link Subscribe} listener.
         * @param method A subscribe method.
         * @param abort The event is stoppable.
         */
        private Invoker(Object instance, Method method, boolean abort) {
            method.setAccessible(true);

            this.instance = instance;
            this.method = method;
            this.hasParam = method.getParameterTypes().length == 1;
            this.abort = abort;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object event) {
            if (abort) {
                if (event instanceof Disposable) {
                    ((Disposable) event).dispose();
                }
            }

            try {
                if (hasParam) {
                    method.invoke(instance, event);
                } else {
                    method.invoke(instance);
                }
            } catch (Exception e) {
                // If this exception will be thrown, it is bug of this program. So we must rethrow
                // the wrapped error in here.
                throw new Error(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object instance, Method method) {
            return this.instance == instance && (method == null || this.method.equals(method));
        }
    }

    /**
     * @version 2013/12/18 9:31:12
     */
    private static class Count extends Listener {

        /** The delegator. */
        private final Publishable publishable;

        /** The delegator. */
        private final Object subscribable;

        /** The execution limit. */
        private final int limit;

        /** The current number of execution. */
        private int current = 0;

        /**
         * @param limit
         * @param publishable
         * @param subscribable
         * @param listener
         */
        private Count(int limit, Publishable publishable, Object subscribable, Listener listener) {
            this.limit = limit;
            this.publishable = publishable;
            this.subscribable = subscribable;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object event) {
            delegator.accept(event);

            if (++current == limit) {
                publishable.unregister(subscribable);
            }
        }
    }

    /**
     * <p>
     * Built-in listener wrapper.
     * </p>
     * 
     * @version 2013/12/18 9:22:56
     */
    private static class Throttle extends Listener {

        /** The delay time. */
        private final long delay;

        /** The latest execution time. */
        private long latest;

        /**
         * @param delay
         * @param listener
         */
        private Throttle(long delay, Listener listener) {
            this.delay = delay;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object event) {
            long now = System.currentTimeMillis();

            if (latest + delay < now) {
                latest = now;

                delegator.accept(event);
            }
        }
    }

    /**
     * <p>
     * Built-in listener wrapper.
     * </p>
     * 
     * @version 2013/12/18 9:19:21
     */
    private static class Debounce extends Listener {

        /** The delay time. */
        private final long delay;

        /** The time out id. */
        private long id = -1;

        /**
         * @param delay
         * @param listener
         */
        private Debounce(long delay, Listener listener) {
            this.delay = delay;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object event) {
            if (id != -1) {
                clearTimeout(id);
            }

            this.id = setTimeout(() -> {
                id = -1;
                delegator.accept(event);
            }, delay);
        }
    }

    /**
     * <p>
     * Built-in listener wrapper.
     * </p>
     * 
     * @version 2013/12/18 9:18:45
     */
    private static class Delay extends Listener {

        /** The delay time. */
        private final long delay;

        /**
         * @param delay
         * @param listener
         */
        public Delay(long delay, Listener listener) {
            this.delay = delay;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Object event) {
            setTimeout(() -> {
                delegator.accept(event);
            }, delay);
        }
    }

    /**
     * <p>
     * Built-in listener wrapper.
     * </p>
     * 
     * @version 2013/04/08 10:11:19
     */
    private static class UIBind extends Listener {

        /** The ui action type. */
        private final UIAction type;

        /**
         * @param type
         * @param listener
         */
        private UIBind(UIAction type, Listener listener) {
            this.type = type;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void accept(Object event) {
            if (event instanceof UIEvent) {
                UIEvent ui = (UIEvent) event;

                if (ui.type.equals(type.name)) {
                    delegator.accept(event);
                }
            }
        }
    }

    /**
     * <p>
     * Built-in listener wrapper.
     * </p>
     * 
     * @version 2013/04/08 10:11:19
     */
    private static class KeyBind extends Listener {

        /** The target key. */
        private final Key key;

        /**
         * @param key
         * @param listener
         */
        private KeyBind(Key key, Listener listener) {
            this.key = key;
            this.delegator = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void accept(Object event) {
            if (event instanceof KeyDetectable) {
                if (((KeyDetectable) event).getKey() == key) {
                    delegator.accept(event);
                }
            } else {
                throw new IllegalArgumentException(event + " is not " + KeyDetectable.class.getSimpleName() + ".");
            }
        }
    }
}
