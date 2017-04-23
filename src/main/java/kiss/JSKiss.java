/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import static js.lang.Global.*;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.script.ScriptException;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import js.lang.Global;
import js.lang.NativeArray;
import js.lang.NativeObject;
import kiss.model.Model;
import kiss.model.Property;

/**
 * @version 2013/08/02 12:39:06
 */
// @JavaAPIProvider(I.class)
class JSKiss {

    /** No Operation */
    public static final Runnable NoOP = () -> {
        // no operation
    };

    public static ScheduledExecutorService $scheduler = Executors.newScheduledThreadPool(1);

    /** The mapping from extension point to extensions. */
    private static Table<Class, Class> extensions;

    /** The mapping from extension point to assosiated extension mapping. */
    private static final Table<String, Class> keys = new Table();

    /** The circularity dependency graph per thread. */
    static final ThreadSpecific<Deque<Class>> dependencies = new ThreadSpecific(ArrayDeque.class);

    /** The cache between Model and Lifestyle. */
    private static final ConcurrentHashMap<Class, Lifestyle> lifestyles = new ConcurrentHashMap();

    /** The task manager. */
    private static final ScheduledExecutorService parallel = Executors.newScheduledThreadPool(4);

    /** The associatable object holder. */
    private static final WeakHashMap<Object, WeakHashMap> associatables = new WeakHashMap();

    /** The list of primitive classes. (except for void type) */
    private static final Class[] primitives = {boolean.class, int.class, long.class, float.class, double.class, byte.class, short.class,
            char.class, void.class};

    /** The list of wrapper classes. (except for void type) */
    private static final Class[] wrappers = {Boolean.class, Integer.class, Long.class, Float.class, Double.class, Byte.class, Short.class,
            Character.class, Void.class};

    /** The holder for lambda reference. */
    private static final Map<Class, Executable> executables = new ConcurrentHashMap();

    static {
        // built-in lifestyles
        lifestyles.put(List.class, new Prototype(ArrayList.class));
        lifestyles.put(Map.class, new Prototype(HashMap.class));
        lifestyles.put(Prototype.class, new Prototype(Prototype.class));
        lifestyles.put(ListProperty.class, () -> {
            return new SimpleListProperty(FXCollections.observableArrayList());
        });
        lifestyles.put(ObservableList.class, FXCollections::observableArrayList);
        lifestyles.put(MapProperty.class, () -> {
            return new SimpleMapProperty(FXCollections.observableHashMap());
        });
        lifestyles.put(ObservableMap.class, FXCollections::observableHashMap);
        lifestyles.put(SetProperty.class, () -> {
            return new SimpleSetProperty(FXCollections.observableSet());
        });
        lifestyles.put(ObservableSet.class, FXCollections::observableSet);
        lifestyles.put(Locale.class, () -> Locale.getDefault());
    }

    /**
     * <p>
     * Create {@link Predicate} which accpets any item.
     * </p>
     * 
     * @return An acceptable {@link Predicate}.
     */
    public static <V> Predicate<V> accept() {
        return e -> true;
    }

    /**
     * <p>
     * Retrieve the associated value with the specified object by the specified type.
     * </p>
     *
     * @param host A host object.
     * @param type An association type.
     * @return An associated value.
     */
    public static <V> V associate(Object host, Class<V> type) {
        WeakHashMap<Class<V>, V> association = associatables.computeIfAbsent(host, key -> new WeakHashMap());
        return association.computeIfAbsent(type, I::make);
    }

    /**
     * <p>
     * Create the partial applied {@link Consumer}.
     * </p>
     *
     * @param function A target function.
     * @param param A parameter to apply.
     * @return A partial applied function.
     */
    public static <Param> Runnable bind(Consumer<Param> function, Param param) {
        return function == null ? NoOP : () -> function.accept(param);
    }

    /**
     * <p>
     * Create the partial applied {@link Consumer}.
     * </p>
     *
     * @param function A target function.
     * @param param A parameter to apply.
     * @return A partial applied function.
     */
    public static <Param1, Param2> Runnable bind(BiConsumer<Param1, Param2> function, Param1 param1, Param2 param2) {
        return function == null ? NoOP : () -> function.accept(param1, param2);
    }

    /**
     * <p>
     * Create the partial applied {@link Function}.
     * </p>
     *
     * @param function A target function.
     * @param param A parameter to apply.
     * @return A partial applied function.
     */
    public static <Param, Return> Supplier<Return> bind(Function<Param, Return> function, Param param) {
        Objects.requireNonNull(function);
        return () -> function.apply(param);
    }

    /**
     * <p>
     * Create the partial applied {@link Function}.
     * </p>
     *
     * @param function A target function.
     * @param param A parameter to apply.
     * @return A partial applied function.
     */
    public static <Param1, Param2, Return> Supplier<Return> bind(BiFunction<Param1, Param2, Return> function, Param1 param1, Param2 param2) {
        Objects.requireNonNull(function);
        return () -> function.apply(param1, param2);
    }

    /**
     * <p>
     * Bundle all given funcitons into single function.
     * </p>
     * 
     * @param functions A list of functions to bundle.
     * @return A bundled function.
     */
    public static <F> F bundle(F... functions) {
        return bundle((Class<F>) functions.getClass().getComponentType(), functions);
    }

    /**
     * <p>
     * Bundle all given funcitons into single function.
     * </p>
     * 
     * @param functions A list of functions to bundle.
     * @return A bundled function.
     */
    public static <F> F bundle(Collection<F> functions) {
        return bundle(findNCA(functions, Class::isInterface), functions);
    }

    // /**
    // * <p>
    // * Find the nearest common ancestor class of the given classes.
    // * </p>
    // *
    // * @param <X> A type.
    // * @param classes A list of classes.
    // * @return A nearest common ancestor class.
    // */
    // private static <X> Class findNCA(X... classes) {
    // return classes.getClass().getComponentType();
    // }

    /**
     * <p>
     * Find the nearest common ancestor class of the given classes.
     * </p>
     * 
     * @param <X> A type.
     * @param items A list of items.
     * @return A nearest common ancestor class.
     */
    private static <X> Class<X> findNCA(Collection<X> items, Predicate<Class> filter) {
        if (filter == null) {
            filter = accept();
        }

        Set<Class> types = null;
        Iterator<X> iterator = items.iterator();

        if (iterator.hasNext()) {
            types = Model.collectTypes(iterator.next().getClass());
            types.removeIf(filter.negate());

            while (iterator.hasNext()) {
                types.retainAll(Model.collectTypes(iterator.next().getClass()));
            }
        }
        return types == null || types.isEmpty() ? null : types.iterator().next();
    }

    /**
     * <p>
     * Bundle all given typed funcitons into single typed function.
     * </p>
     * 
     * @param type A function type.
     * @param functions A list of functions to bundle.
     * @return A bundled function.
     */
    public static <F> F bundle(Class<F> type, F... functions) {
        return bundle(type, Arrays.asList(functions));
    }

    /**
     * <p>
     * Bundle all given typed funcitons into single typed function.
     * </p>
     * 
     * @param type A function type.
     * @param functions A list of functions to bundle.
     * @return A bundled function.
     */
    public static <F> F bundle(Class<F> type, Collection<F> functions) {
        return make(type, (proxy, method, args) -> {
            Object result = null;

            if (functions != null) {
                for (Object fun : functions) {
                    if (fun != null) {
                        result = method.invoke(fun, args);
                    }
                }
            }
            return result;
        });
    }

    /**
     * <p>
     * Create the specified {@link Collection} with the specified items.
     * </p>
     * 
     * @param type A {@link Collection} type.
     * @param items A list of itmes.
     * @return The new created {@link Collection}.
     */
    public static <T extends Collection<V>, V> T collect(Class<T> type, V... items) {
        T collection = I.make(type);

        if (items != null) {
            for (V item : items) {
                collection.add(item);
            }
        }
        return collection;
    }

    /**
     * <p>
     * Find all <a href="Extensible.html#Extension">Extensions</a> which are specified by the given
     * <a href="Extensible#ExtensionPoint">Extension Point</a>.
     * </p>
     * <p>
     * The returned list will be "safe" in that no references to it are maintained by Sinobu. (In
     * other words, this method must allocate a new list). The caller is thus free to modify the
     * returned list.
     * </p>
     *
     * @param <E> An Extension Point.
     * @param extensionPoint An extension point class. The
     *            <a href="Extensible#ExtensionPoint">Extension Point</a> class is only accepted,
     *            otherwise this method will return empty list.
     * @return All Extensions of the given Extension Point or empty list.
     * @throws NullPointerException If the Extension Point is <code>null</code>.
     */
    public static <E extends Extensible> List<E> find(Class<E> extensionPoint) {
        initialize();

        // Skip null check because this method can throw NullPointerException.
        List<Class> classes = extensions.get(extensionPoint);

        // instantiate all found extesions
        List list = new ArrayList(classes.size());

        for (Class extension : classes) {
            list.add(make(extension));
        }
        return list;
    }

    /**
     * <p>
     * Find the <a href="Extensible.html#Extension">Extension</a> which are specified by the given
     * <a href="Extensible#ExtensionPoint">Extension Point</a> and the given key.
     * </p>
     *
     * @param <E> An Extension Point.
     * @param extensionPoint An Extension Point class. The
     *            <a href="Extensible#ExtensionPoint">Extension Point</a> class is only accepted,
     *            otherwise this method will return <code>null</code>.
     * @param key An <a href="Extensible.html#ExtensionKey">Extension Key</a> class.
     * @return A associated Extension of the given Extension Point and the given Extension Key or
     *         <code>null</code>.
     */
    public static <E extends Extensible> E find(Class<E> extensionPoint, Class key) {
        initialize();

        if (extensionPoint == null || key == null) {
            return null;
        }

        for (Class clazz : Model.collectTypes(key)) {
            Class<E> supplier = keys.find(extensionPoint.getName().concat(clazz.getName()));

            if (supplier != null) {
                return make(supplier);
            }
        }

        if (extensionPoint != ExtensionFactory.class) {
            ExtensionFactory factory = find(ExtensionFactory.class, extensionPoint);

            if (factory != null) {
                return (E) factory.create(key);
            }
        }
        return null;
    }

    /**
     * <p>
     * Find all <a href="Extensible.html#Extension">Extensions</a> which are specified by the given
     * <a href="Extensible#ExtensionPoint">Extension Point</a>.
     * </p>
     * <p>
     * The returned list will be "safe" in that no references to it are maintained by Sinobu. (In
     * other words, this method must allocate a new list). The caller is thus free to modify the
     * returned list.
     * </p>
     *
     * @param <E> An Extension Point.
     * @param extensionPoint An extension point class. The
     *            <a href="Extensible#ExtensionPoint">Extension Point</a> class is only accepted,
     *            otherwise this method will return empty list.
     * @return All Extension classes of the given Extension Point or empty list.
     * @throws NullPointerException If the Extension Point is <code>null</code>.
     */
    public static <E extends Extensible> List<Class<E>> findAs(Class<E> extensionPoint) {
        initialize();

        // Skip null check because this method can throw NullPointerException.
        List<Class> classes = extensions.get(extensionPoint);

        // instantiate all found extesions
        List list = new ArrayList(classes.size());

        for (Class extension : classes) {
            list.add(extension);
        }
        return list;
    }

    /**
     * <p>
     * Gets a <em>type-safe and refactoring-safe</em> resource bundle (<em>not</em>
     * {@link java.util.ResourceBundle}) corresponding to the specified resource bundle class.
     * </p>
     * <p>
     * Conceptually, i18n method uses the following strategy for locating and instantiating resource
     * bundles:
     * </p>
     * <p>
     * i18n method uses the bundle class name and the default locale (obtained from
     * <code>I.make(Locale.class)</code>)) to generate a sequence of candidate bundle names. If the
     * default locale's language, country, and variant are all empty strings, then the bundle class
     * name is the only candidate bundle name. Otherwise, the following sequence is generated from
     * the attribute values of the default locale (language, country, and variant):
     * </p>
     * <ol>
     * <li>bundleClassSimpleName + "_" + language + "_" + country + "_" + variant</li>
     * <li>bundleClassSimpleName + "_" + language + "_" + country</li>
     * <li>bundleClassSimpleName + "_" + language</li>
     * <li>bundleClassSimpleName</li>
     * </ol>
     * <p>
     * Candidate bundle names where the final component is an empty string are omitted. For example,
     * if country is an empty string, the second candidate bundle name is omitted.
     * </p>
     * <p>
     * i18n method then iterates over the candidate bundle names to find the first one for which it
     * can instantiate an actual resource bundle. For each candidate bundle name, it attempts to
     * create a resource bundle:
     * </p>
     * <ol>
     * <li>First, it attempts to find a class using the candidate bundle name. If such a class can
     * be found and loaded using {@link I#find(Class)}, is assignment compatible with the given
     * bundle class, and can be instantiated, i18n method creates a new instance of this class and
     * uses it as the result resource bundle.</li>
     * </ol>
     * <p>
     * If the following classes are provided:
     * </p>
     * <ul>
     * <li>MyResources.class</li>
     * <li>MyResources_fr.class</li>
     * <li>MyResources_fr_CH.class</li>
     * </ul>
     * <p>
     * The contents of all files are valid (that is non-abstract subclasses of {@link Extensible}
     * for the ".class" files). The default locale is Locale("en", "GB").
     * </p>
     * <p>
     * Calling i18n method with the shown locale argument values instantiates resource bundles from
     * the following sources:
     * </p>
     * <ol>
     * <li>Locale("fr", "CH"): result MyResources_fr_CH.class</li>
     * <li>Locale("fr", "FR"): result MyResources_fr.class</li>
     * <li>Locale("es"): result MyResources.class</li>
     * </ol>
     *
     * @param <B> A resource bundle.
     * @param bundleClass A resource bundle class. <code>null</code> will throw
     *            {@link NullPointerException}.
     * @return A suitable resource bundle class for the given bundle class and locale.
     * @throws NullPointerException If the bundle class is <code>null</code>.
     */
    public static <B extends Extensible> B i18n(Class<B> bundleClass) {
        String lang = "_".concat(make(Locale.class).getLanguage());

        for (Class clazz : extensions.get(bundleClass)) {
            if (clazz.getName().endsWith(lang)) {
                bundleClass = clazz;
                break;
            }
        }
        return make(bundleClass);
    }

    public static <P> Consumer<P> imitateConsumer(Runnable lambda) {
        return p -> {
            if (lambda != null) lambda.run();
        };
    }

    public static <P, R> Function<P, R> imitateFunction(Consumer<P> function) {
        return p -> {
            function.accept(p);
            return null;
        };
    }

    public static <P, R> Function<P, R> imitateFunction(Supplier<R> function) {
        return p -> function.get();
    }

    /**
     * <p>
     * Returns a string containing the string representation of each of items, using the specified
     * separator between each.
     * </p>
     *
     * @param delimiter A sequence of characters that is used to separate each of the elements in
     *            the resulting String.
     * @param items A {@link Iterable} items.
     * @return A concat expression.
     * @throws NullPointerException If items is <code>null</code>.
     */
    public static String join(CharSequence delimiter, CharSequence... items) {
        return join(delimiter, Arrays.asList(items));
    }

    /**
     * <p>
     * Returns a string containing the string representation of each of items, using the specified
     * separator between each.
     * </p>
     *
     * @param delimiter A sequence of characters that is used to separate each of the elements in
     *            the resulting String.
     * @param items A {@link Iterable} items.
     * @return A concat expression.
     * @throws NullPointerException If items is <code>null</code>.
     */
    public static String join(CharSequence delimiter, Iterable items) {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = items.iterator();

        if (iterator.hasNext()) {
            builder.append(iterator.next());

            while (iterator.hasNext()) {
                builder.append(delimiter).append(iterator.next());
            }
        }
        return builder.toString();
    }

    /**
     * <p>
     * Retrieve lambda reference.
     * </p>
     * 
     * @param lambda A lambda instance.
     * @return An actual reference.
     */
    static Executable lambda(Serializable lambda) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * <p>
     * Create {@link ArrayList} with the specified items.
     * </p>
     * 
     * @param items A list of itmes.
     * @return The new created {@link ArrayList}.
     */
    public static <V> List<V> list(V... items) {
        return collect(ArrayList.class, items);
    }

    /**
     * <p>
     * Returns a new or cached instance of the model class.
     * </p>
     * <p>
     * This method supports the top-level class and the member type. If the local class or the
     * anonymous class is passed to this argument, {@link UnsupportedOperationException} will be
     * thrown. There is a possibility that a part of this limitation will be removed in the future.
     * </p>
     *
     * @param <M>
     * @param modelClass
     * @return A instance of the specified model class. This instance is managed by Sinobu.
     * @throws NullPointerException If the model class is <code>null</code>.
     * @throws IllegalArgumentException If the model class is non-accessible or final class.
     * @throws UnsupportedOperationException If the model class is inner-class.
     * @throws ClassCircularityError If the model has circular dependency.
     * @throws InstantiationException If Sinobu can't instantiate(resolve) the model class.
     */
    public static <M> M make(Class<M> modelClass) {
        return makeLifestyle(modelClass).get();
    }

    /**
     * <p>
     * Returns a new or cached instance of the model class.
     * </p>
     * <p>
     * This method supports the top-level class and the member type. If the local class or the
     * anonymous class is passed to this argument, {@link UnsupportedOperationException} will be
     * thrown. There is a possibility that a part of this limitation will be removed in the future.
     * </p>
     *
     * @param <M>
     * @param modelClass
     * @return A instance of the specified model class. This instance is managed by Sinobu.
     * @throws NullPointerException If the model class is <code>null</code>.
     * @throws IllegalArgumentException If the model class is non-accessible or final class.
     * @throws UnsupportedOperationException If the model class is inner-class.
     * @throws ClassCircularityError If the model has circular dependency.
     * @throws InstantiationException If Sinobu can't instantiate(resolve) the model class.
     */
    static <M> Lifestyle<M> makeLifestyle(Class<M> modelClass) {
        // At first, we must confirm the cached lifestyle associated with the model class. If
        // there is no such cache, we will try to create newly lifestyle.
        Lifestyle<M> lifestyle = lifestyles.get(modelClass);

        if (lifestyle != null) {
            return lifestyle; // use cache
        }

        // Skip null check because this method can throw NullPointerException.
        // if (modelClass == null) throw new NullPointerException("NPE");

        // The model class have some preconditions to have to meet.
        if (modelClass.isLocalClass()) {
            throw new UnsupportedOperationException(modelClass + " is  inner class.");
        }

        // Construct dependency graph for the current thred.
        Deque<Class> dependency = dependencies.get();
        dependency.add(modelClass);

        // Don't use 'contains' method check here to resolve singleton based
        // circular reference. So we must judge it from the size of context. If the
        // context contains too many classes, it has a circular reference
        // independencies.
        if (16 < dependency.size()) {
            // Deque will be contain repeated Classes so we must shrink it with
            // maintaining its class order.
            throw new ClassCircularityError(new LinkedHashSet(dependency).toString());
        }

        try {
            // At first, we should search the associated lifestyle from extension points.
            lifestyle = find(Lifestyle.class, modelClass);

            // Then, check its Manageable annotation.
            if (lifestyle == null) {
                // If the actual model class doesn't provide its lifestyle explicitly, we use
                // Prototype lifestyle which is default lifestyle in Sinobu.
                Manageable manageable = modelClass.getAnnotation(Manageable.class);

                // Create new lifestyle for the actual model class
                lifestyle = (Lifestyle<M>) make((Class) (manageable == null ? Prototype.class : manageable.lifestyle()));
            }

            // Trace dependency graph to detect circular dependencies.
            Constructor constructor = Model.collectConstructors(modelClass)[0];

            if (constructor != null) {
                for (Class param : constructor.getParameterTypes()) {
                    if (param != Lifestyle.class && param != Class.class) {
                        makeLifestyle(param);
                    }
                }
            }

            // This lifestyle is safe and has no circular dependencies.
            lifestyles.putIfAbsent(modelClass, lifestyle);

            // API definition
            return lifestyles.get(modelClass);
        } finally {
            dependency.pollLast();
        }
    }

    /**
     * <p>
     * Create proxy instance.
     * </p>
     * 
     * @param type A model type.
     * @param handler A proxy handler.
     * @return
     */
    public static <T> T make(Class<T> type, InvocationHandler handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);

        if (type.isInterface() == false) {
            throw new IllegalArgumentException("Type must be interface.");
        }
        return (T) Proxy.newProxyInstance(I.class.getClassLoader(), new Class[] {type}, handler);
    }

    /**
     * <p>
     * Findthe first parameter name of lambda method.
     * </p>
     *
     * @param object A lambda instance.
     * @return A parameter name.
     */
    static String method(Object object) {
        return "type";
    }

    /**
     * <p>
     * Observe the specified {@link ObservableValue}.
     * </p>
     * <p>
     * An implementation of {@link ObservableValue} may support lazy evaluation, which means that
     * the value is not immediately recomputed after changes, but lazily the next time the value is
     * requested.
     * </p>
     *
     * @param observable A target to observe.
     * @return A observable event stream.
     */
    public static <E extends javafx.beans.Observable> Signal<E> observe(E observable) {
        if (observable == null) {
            return Signal.NEVER;
        }

        return new Signal<>((observer, disposer) -> {
            // create actual listener
            InvalidationListener listener = value -> {
                observer.accept((E) value);
            };

            observable.addListener(listener); // register listener

            return () -> {
                observable.removeListener(listener); // unregister listener
            };
        });
    }

    /**
     * <p>
     * Observe the specified {@link ObservableValue}.
     * </p>
     * <p>
     * An implementation of {@link ObservableValue} may support lazy evaluation, which means that
     * the value is not immediately recomputed after changes, but lazily the next time the value is
     * requested.
     * </p>
     *
     * @param observable A target to observe.
     * @return A observable event stream.
     */
    public static <E> Signal<E> observe(ObservableValue<E> observable) {
        if (observable == null) {
            return Signal.NEVER;
        }

        return new Signal<>((observer, disposer) -> {
            // create actual listener
            ChangeListener<E> listener = (o, oldValue, newValue) -> {
                observer.accept(newValue);
            };

            observable.addListener(listener); // register listener

            // notify the current value
            E value = observable.getValue();

            if (value != null) {
                listener.changed(observable, null, value);
            }

            return () -> {
                observable.removeListener(listener); // unregister listener
            };
        });
    }

    /**
     * <p>
     * Create value set.
     * </p>
     *
     * @param param1 A first parameter.
     * @param param2 A second parameter.
     * @return
     */
    public static <Param1, Param2> Ⅱ<Param1, Param2> pair(Param1 param1, Param2 param2) {
        return new Ⅱ(param1, param2);
    }

    /**
     * <p>
     * Create value set.
     * </p>
     *
     * @param param1 A first parameter.
     * @param param2 A second parameter.
     * @param param3 A third parameter.
     * @return
     */
    public static <Param1, Param2, Param3> Ⅲ<Param1, Param2, Param3> pair(Param1 param1, Param2 param2, Param3 param3) {
        return new Ⅲ(param1, param2, param3);
    }

    /**
     * <p>
     * Create paired value {@link Consumer}.
     * </p>
     *
     * @param consumer A {@link BiConsumer} to make parameters paired.
     * @return A paired value {@link Consumer}.
     */
    public static <Param1, Param2> Consumer<Ⅱ<Param1, Param2>> pair(BiConsumer<Param1, Param2> consumer) {
        return params -> consumer.accept(params.ⅰ, params.ⅱ);
    }

    /**
     * <p>
     * Create paired value {@link Function}.
     * </p>
     *
     * @param funtion A {@link BiFunction} to make parameters paired.
     * @return A paired value {@link Function}.
     */
    public static <Param1, Param2, Return> Function<Ⅱ<Param1, Param2>, Return> pair(BiFunction<Param1, Param2, Return> funtion) {
        return params -> funtion.apply(params.ⅰ, params.ⅱ);
    }

    /**
     * <p>
     * Close the specified object quietly if it is {@link AutoCloseable}. Equivalent to
     * {@link AutoCloseable#close()}, except any exceptions will be ignored. This is typically used
     * in finally block like the following.
     * </p>
     * <pre>
     * AutoCloseable input = null;
     *
     * try {
     *     // some IO action
     * } catch (Exception e) {
     *     throw e;
     * } finally {
     *     I.quiet(input);
     * }
     * </pre>
     * <p>
     * Throw the specified checked exception quietly or close the specified {@link AutoCloseable}
     * object quietly.
     * </p>
     * <p>
     * This method <em>doesn't</em> wrap checked exception around unchecked exception (e.g. new
     * RuntimeException(e)) and <em>doesn't</em> shelve it. This method deceive the compiler that
     * the checked exception is unchecked one. So you can catch a raw checked exception in the
     * caller of the method which calls this method.
     * </p>
     * <pre>
     * private void callerWithoutErrorHandling() {
     *     methodQuietly();
     * }
     *
     * private void callerWithErrorHandling() {
     *     try {
     *         methodQuietly();
     *     } catch (Exception e) {
     *         // you can catch the checked exception here
     *     }
     * }
     *
     * private void methodQuietly() {
     *     try {
     *         // throw some cheched exception
     *     } catch (CheckedException e) {
     *         throw I.quiet(e); // rethrow checked exception quietly
     *     }
     * }
     * </pre>
     *
     * @param object A exception to throw quietly or a object to close quietly.
     * @return A pseudo unchecked exception.
     * @throws NullPointerException If the specified exception is <code>null</code>.
     */
    public static RuntimeException quiet(Object object) {
        if (object instanceof Throwable) {
            Throwable throwable = (Throwable) object;

            // retrieve original exception from the specified wrapped exception
            if (throwable instanceof InvocationTargetException) throwable = throwable.getCause();

            // throw quietly
            return (RuntimeException) throwable;
        }

        if (object instanceof AutoCloseable) {
            try {
                ((AutoCloseable) object).close();
            } catch (Exception e) {
                throw quiet(e);
            }
        }

        // API definition
        return null;
    }

    /**
     * <p>
     * Create {@link Predicate} which rejects any item.
     * </p>
     * 
     * @return An rejectable {@link Predicate}.
     */
    public static <V> Predicate<V> reject() {
        return e -> false;
    }

    /**
     * <p>
     * Create {@link Set} with the specified items.
     * </p>
     * 
     * @param items A list of itmes.
     * @return The new created {@link Set}.
     */
    public static <V> Set<V> set(V... items) {
        Set<V> set = new HashSet();

        if (items != null) {
            for (V item : items) {
                set.add(item);
            }
        }

        return set;
    }

    /**
     * <p>
     * Transform any type object into the specified type if possible.
     * </p>
     *
     * @param <In> A input type you want to transform from.
     * @param <Out> An output type you want to transform into.
     * @param input A target object.
     * @param output A target type.
     * @return A transformed object.
     * @throws NullPointerException If the output type is <code>null</code>.
     */
    public static <In, Out> Out transform(In input, Class<Out> output) {
        if (input == null) {
            return null;
        }

        String encoded = input instanceof String ? (String) input : find(Encoder.class, input.getClass()).encode(input);

        if (output == String.class) {
            return (Out) encoded;
        }
        return ((Decoder<Out>) find(Decoder.class, output)).decode(encoded);
    }

    /**
     * <p>
     * Find the class by the specified fully qualified class name.
     * </p>
     *
     * @param fqcn A fully qualified class name to want.
     * @return The specified class.
     */
    public static Class type(String fqcn) {
        for (Class clazz : primitives) {
            if (clazz.getName().equals(fqcn)) {
                return clazz;
            }
        }

        try {
            return Class.forName(fqcn, false, null);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(fqcn);
        }
    }

    /**
     * <p>
     * Load the file as an additional classpath into JVM. If the file indicates the classpath which
     * is already loaded, that will do nothing at all. The classpath can accept directory or archive
     * (like Jar). If it is <code>null</code> or a file, this method does nothing.
     * </p>
     * <p>
     * There are two advantages in the classpath loaded by this method. One is that you can add
     * classpath dynamically and the other is that you can listen to the specified class loading
     * event.
     * </p>
     * <p>
     * Generally, JVM collects classpath information from various sources (environment variable,
     * command line option and so on). However those means can't add or remove a classpath
     * dynamically. This method removes such limitations.
     * </p>
     * <p>
     * <em>NOTE</em> : System class loader in JVM can recognize the classpath which is specified by
     * usual means, but not by this method. Because Sinobu manages additional classpath for enabling
     * dynamic manipulation.
     * </p>
     *
     * @param classPath A classpath to load.
     * @param filter Filter classes by package of the specified class.
     * @return A managed {@link ClassLoader}.
     * @see #unload(Path)
     * @see kiss.ClassListener#load(Class)
     * @see java.lang.ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader load(Class classPath, boolean filter) {
        // reset
        extensions = null;

        // API definition
        return null;
    }

    /**
     * Lazy initialization.
     */
    private static void initialize() {
        if (extensions == null) {
            extensions = new Table();

            for (Class<? extends Extensible> extension : search(Extensible.class)) {
                load(extension);
            }
        }
    }

    private static void load(Class extension) {
        // search and collect information for all extension points
        for (Class extensionPoint : Model.collectTypes(extension)) {
            if (Arrays.asList(extensionPoint.getInterfaces()).contains(Extensible.class)) {
                // register new extension
                extensions.push(extensionPoint, extension);

                // register extension key
                Type[] params = Model.collectParameters(extension, extensionPoint);

                if (params.length != 0 && params[0] != Object.class) {
                    keys.push(extensionPoint.getName().concat(((Class) params[0]).getName()), extension);

                    // The user has registered a newly custom lifestyle, so we should update
                    // lifestyle for this extension key class. Normally, when we update some data,
                    // it is desirable to store the previous data to be able to restore it later.
                    // But, in this case, the contextual sensitive instance that the lifestyle emits
                    // changes twice on "load" and "unload" event from the point of view of the
                    // user. So the previous data becomes all but meaningless for a cacheable
                    // lifestyles (e.g. Singleton and ThreadSpecifiec). Therefore we we completely
                    // refresh lifestyles associated with this extension key class.
                    if (extensionPoint == Lifestyle.class) {
                        lifestyles.remove(params[0]);
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Find all sub class of the specified class.
     * </p>
     *
     * @param type A type to search.
     * @return A list of found classes.
     */
    private static <T> List<Class<? extends T>> search(Class<T> type) {
        List<Class<? extends T>> matched = new ArrayList();
        NativeArray extensions = boot.getPropertyAs(NativeArray.class, "extensions");

        for (int i = 0; i < extensions.length(); i++) {
            NativeObject object = (NativeObject) extensions.get(i);

            if (object != null) {
                Class clazz = object.getPropertyAs(Class.class, "$");

                if (((Modifier.INTERFACE | Modifier.ABSTRACT) & clazz.getModifiers()) == 0 && type != clazz && type
                        .isAssignableFrom(clazz)) {
                    matched.add(clazz);
                }
            }
        }
        return matched;
    }

    /**
     * <p>
     * Reads Java object tree from the given XML or JSON input.
     * </p>
     *
     * @param input A serialized Java object tree data as XML or JSON. If the input is incompatible
     *            with Java object, this method ignores the input. <code>null</code> will throw
     *            {@link NullPointerException}. The empty or invalid format data will throw
     *            {@link ScriptException}.
     * @param output A root Java object. All properties will be assigned from the given data deeply.
     *            If the input is incompatible with Java object, this method ignores the input.
     *            <code>null</code> will throw {@link java.lang.NullPointerException}.
     * @return A root Java object.
     * @throws NullPointerException If the input data or the root Java object is <code>null</code>.
     * @throws ScriptException If the input data is empty or invalid format.
     */
    public static <M> M read(CharSequence input, M output) {
        try {
            return read(Model.of(output), output, Global.JSON.parse(input.toString()));
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Read property and write it.
     * </p>
     *
     * @param java
     * @param js
     * @return
     */
    private static <T> T read(Model model, T java, NativeObject js) throws Exception {
        for (String name : js.keys()) {
            Object value;
            Property property = model.property(name);

            if (property != null) {
                if (property.isAttribute()) {
                    value = transform(js.getProperty(property.name), property.model.type);
                } else {
                    value = read(property.model, make(property.model.type), js.getPropertyAs(NativeObject.class, property.name));
                }

                // assign value
                model.set(java, property, value);
            }
        }
        return java;
    }

    /**
     * <p>
     * Execute the specified task in background {@link Thread}.
     * </p>
     *
     * @param task A task to execute.
     */
    public static Future<?> schedule(Runnable task) {
        return schedule(0, TimeUnit.MILLISECONDS, true, task);
    }

    /**
     * <p>
     * Execute the specified task in background {@link Thread} with the specified delay.
     * </p>
     *
     * @param time A delay time.
     * @param unit A delay time unit.
     * @param parallelExecution The <code>true</code> will execute task in parallel,
     *            <code>false</code> will execute task in serial.
     * @param task A task to execute.
     */
    public static Future<?> schedule(long time, TimeUnit unit, boolean parallelExecution, Runnable task) {
        return parallel.schedule(task, time, unit);
    }

    /**
     * <p>
     * Execute the specified task infinitely in background {@link Thread} with the specified delay
     * and period.
     * </p>
     *
     * @param delay A initial delay time.
     * @param period A period time.
     * @param unit A delay time unit.
     * @param parallelExecution The <code>true</code> will execute task in parallel,
     *            <code>false</code> will execute task in serial.
     * @param task A task to execute.
     */
    public static Future<?> schedule(long delay, long period, TimeUnit unit, boolean parallelExecution, Runnable task) {
        return parallel.scheduleAtFixedRate(task, delay, period, unit);
    }

    /**
     * <p>
     * Execute the specified task in background {@link Thread} with the specified delay.
     * </p>
     *
     * @param delay A initial delay time.
     * @param unit A delay time unit.
     * @param parallelExecution The <code>true</code> will execute task in parallel,
     *            <code>false</code> will execute task in serial.
     * @param task A task to execute.
     */
    public static Future<?> schedule(Runnable task, long interval, TimeUnit unit) {
        // return schedule.scheduleAtFixedRate(task, 0, interval, unit);
        return null;
    }

    /**
     * <p>
     * Signal the specified values.
     * </p>
     *
     * @param values A list of values to emit.
     * @return The {@link Signal} to emit sequencial values.
     */
    @SafeVarargs
    public static <V> Signal<V> signal(V... values) {
        return Signal.EMPTY.startWith(values);
    }

    /**
     * <p>
     * Signal the specified values.
     * </p>
     *
     * @param values A list of values to emit.
     * @return The {@link Signal} to emit sequencial values.
     */
    public static <V> Signal<V> signal(Iterable<V> values) {
        return Signal.EMPTY.startWith(values);
    }

    /**
     * <p>
     * Signal the specified values.
     * </p>
     *
     * @param values A list of values to emit.
     * @return The {@link Signal} to emit sequencial values.
     */
    public static <V> Signal<V> signal(Enumeration<V> values) {
        return Signal.EMPTY.startWith(values);
    }

    /**
     * <p>
     * Signal the specified values.
     * </p>
     *
     * @param values A list of values to emit.
     * @return The {@link Signal} to emit sequencial values.
     */
    public static <V> Signal<V> signal(Variable<V> value) {
        return Signal.EMPTY.startWith(value);
    }

    /**
     * @param value A initial value.
     * @param time A time to interval.
     * @param unit A time unit.
     * @param <V> Value type.
     * @return An {@link Signal} that emits values as a first sequence.
     */
    public static <V> Signal<V> signalInfinite(V value, long time, TimeUnit unit) {
        return new Signal<>((observer, disposer) -> {
            Future schedule = schedule(() -> observer.accept(value), time, unit);

            return disposer.add(() -> schedule.cancel(true));
        });
    }

    /**
     * Returns a sequential ordered {@code Events} from {@code startInclusive} (inclusive) to
     * {@code endExclusive} (exclusive) by an incremental step of {@code 1}.
     *
     * @apiNote An equivalent sequence of increasing values can be produced sequentially using a
     *          {@code for} loop as follows: <pre>{@code
     *     for (int i = startInclusive; i < endExclusive ; i++) { ... }
     * }</pre>
     * @param startInclusive A (inclusive) initial value.
     * @param endExclusive An exclusive upper bound.
     * @return a sequential {@code Events} for the range of {@code Integer} elements
     */
    public static Signal<Integer> signalRange(int startInclusive, int endExclusive) {
        return signalRange(startInclusive, 1, endExclusive);
    }

    /**
     * Returns a sequential ordered {@code Events} from {@code startInclusive} (inclusive) to
     * {@code endExclusive} (exclusive) by an incremental step of {@code 1}.
     *
     * @apiNote An equivalent sequence of increasing values can be produced sequentially using a
     *          {@code for} loop as follows: <pre>{@code
     *     for (int i = startInclusive; i < endExclusive ; i += step) { ... }
     * }</pre>
     * @param startInclusive A (inclusive) initial value.
     * @param step A incremental step.
     * @param endExclusive An exclusive upper bound.
     * @return a sequential {@code Events} for the range of {@code Integer} elements
     */
    public static Signal<Integer> signalRange(int startInclusive, int step, int endExclusive) {
        if (step == 0) {
            throw new IllegalArgumentException();
        }

        return (0 < step ? endExclusive <= startInclusive : startInclusive <= endExclusive) ? Signal.EMPTY
                : signal(() -> new Iterator<Integer>() {

                    private int now = startInclusive;

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public boolean hasNext() {
                        return 0 < step ? now < endExclusive : endExclusive < now;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Integer next() {
                        return (now += step) - step; // guilty?
                    }
                });
    }

    /**
     * <p>
     * Return a non-primitive {@link Class} of the specified {@link Class} object. <code>null</code>
     * will be return <code>null</code>.
     * </p>
     *
     * @param type A {@link Class} object to convert to non-primitive class.
     * @return A non-primitive {@link Class} object.
     */
    public static Class wrap(Class type) {
        // check primitive classes
        for (int i = 0; i < primitives.length; i++) {
            if (primitives[i] == type) {
                return wrappers[i];
            }
        }

        // the specified class is not primitive
        return type;
    }

    /**
     * <p>
     * Writes Java object tree to the given output as XML or JSON.
     * </p>
     * <p>
     * If the output object implements {@link AutoCloseable}, {@link AutoCloseable#close()} method
     * will be invoked certainly.
     * </p>
     *
     * @param input A Java object. All properties will be serialized deeply. <code>null</code> will
     *            throw {@link java.lang.NullPointerException}.
     * @param output A serialized data output. <code>null</code> will throw
     *            {@link NullPointerException}.
     * @param json <code>true</code> will produce JSON expression, <code>false</code> will produce
     *            XML expression.
     * @throws NullPointerException If the input Java object or the output is <code>null</code> .
     */
    public static void write(Object input, Appendable out) {
        Objects.nonNull(out);

        try {
            // traverse object as json
            Model model = Model.of(input);
            Format format = new Format();
            format.out = out;
            format.accept(pair(model, new Property(model, ""), input));
        } finally {
            // close carefuly
            quiet(out);
        }
    }

    /**
     * @version 2013/10/01 15:53:27
     */
    private static class Copy implements Consumer<Ⅲ<Model, Property, Object>> {

        /** The current model. */
        private Model model;

        /** The curret object. */
        private Object object;

        /**
         * @param model
         * @param object
         */
        private Copy(Object object, Model model) {
            this.model = model;
            this.object = object;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Ⅲ<Model, Property, Object> t) {
            Property dest = this.model.property(t.ⅱ.name);

            // never check null because PropertyWalker traverses existing properties
            this.model.set(object, dest, I.transform(t.ⅲ, dest.model.type));
        }
    }
}
