/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package js.lang.reflect;

import static js.lang.Global.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import js.lang.NativeArray;
import js.lang.NativeFunction;
import js.lang.NativeObject;
import booton.translator.JavaAPIProvider;

/**
 * <p>
 * {@link Class} representation in Javascript runtime. This class doesn't provide all
 * functionalities.
 * </p>
 * 
 * @version 2013/08/21 17:01:17
 */
@JavaAPIProvider(Class.class)
class JSClass<T> extends JSAnnotatedElement {

    /** The class definition in runtime. */
    private final NativeObject clazz;

    /** The annotation in runtime. */
    private final NativeObject metadata;

    /** The super class. */
    private final Class superclass;

    /** The interface classes. */
    private final Class[] interfaces;

    /** The modifier value. */
    private final int modifiers;

    /** The cache for enum constants. */
    private Map<String, Enum> enumerationConstants;

    /** The cache for array class. */
    private JSClass arrayClass;

    /** The cache for public constructors. */
    private List<Constructor> publicConstructors;

    /** The cache for declared constructors. */
    private List<Constructor> privateConstructors;

    /** The cache for public methods. */
    private Map<Integer, Method> publicMethods;

    /** The cache for declared methods. */
    private List<Method> privateMethods;

    /** The cache for public fields. */
    private Map<Integer, Field> publicFields;

    /** The cache for declared fields. */
    private List<Field> privateFields;

    /**
     * <p>
     * Create native class.
     * </p>
     * 
     * @param name
     * @param clazz
     * @param metadata
     */
    protected JSClass(String name, NativeObject clazz, NativeObject metadata, Class superclass, String[] interfaces) {
        super(name, findAnnotations(metadata, "$", 1, name));

        this.clazz = clazz;
        this.metadata = metadata;
        this.superclass = superclass;
        this.interfaces = new Class[interfaces.length];

        for (int i = 0; i < interfaces.length; i++) {
            this.interfaces[i] = forName(interfaces[i]);
        }

        NativeArray info = metadata.getPropertyAs(NativeArray.class, "$");
        this.modifiers = info == null ? 0 : info.getAsInt(0);
    }

    /**
     * Returns the Java language modifiers for this class or interface, encoded in an integer. The
     * modifiers consist of the Java Virtual Machine's constants for {@code public},
     * {@code protected}, {@code private}, {@code final}, {@code static}, {@code abstract} and
     * {@code interface}; they should be decoded using the methods of class {@code Modifier}.
     * <p>
     * If the underlying class is an array class, then its {@code public}, {@code private} and
     * {@code protected} modifiers are the same as those of its component type. If this
     * {@code Class} represents a primitive type or void, its {@code public} modifier is always
     * {@code true}, and its {@code protected} and {@code private} modifiers are always
     * {@code false}. If this object represents an array class, a primitive type or void, then its
     * {@code final} modifier is always {@code true} and its interface modifier is always
     * {@code false}. The values of its other modifiers are not determined by this specification.
     * <p>
     * The modifier encodings are defined in <em>The Java Virtual Machine
     * Specification</em>, table 4.1.
     * 
     * @return the {@code int} representing the modifiers for this class
     * @see java.lang.reflect.Modifier
     * @since JDK1.1
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns the class loader for the class. Some implementations may use null to represent the
     * bootstrap class loader. This method will return null in such implementations if this class
     * was loaded by the bootstrap class loader.
     * <p>
     * If a security manager is present, and the caller's class loader is not null and the caller's
     * class loader is not the same as or an ancestor of the class loader for the class whose class
     * loader is requested, then this method calls the security manager's {@code checkPermission}
     * method with a {@code RuntimePermission("getClassLoader")} permission to ensure it's ok to
     * access the class loader for the class.
     * <p>
     * If this object represents a primitive type or void, null is returned.
     * 
     * @return the class loader that loaded the class or interface represented by this object.
     * @throws SecurityException if a security manager exists and its {@code checkPermission} method
     *             denies access to the class loader for the class.
     * @see java.lang.ClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public ClassLoader getClassLoader() {
        return null;
    }

    /**
     * <p>
     * Returns an array containing Constructor objects reflecting all the public constructors of the
     * class represented by this Class object. An array of length 0 is returned if the class has no
     * public constructors, or if the class is an array class, or if the class reflects a primitive
     * type or void. Note that while this method returns an array of Constructor<T> objects (that is
     * an array of constructors from this class), the return type of this method is Constructor<?>[]
     * and not Constructor<T>[] as might be expected. This less informative return type is necessary
     * since after being returned from this method, the array could be modified to hold Constructor
     * objects for different classes, which would violate the type guarantees of Constructor<T>[].
     * </p>
     * 
     * @return The array of Constructor objects representing the public constructors of this class.
     */
    public Constructor[] getConstructors() {
        if (publicConstructors == null) {
            publicConstructors = new ArrayList();

            for (Constructor constructor : ((Class) (Object) this).getDeclaredConstructors()) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    publicConstructors.add(constructor);
                }
            }
        }

        // defensive copy
        return publicConstructors.toArray(new Constructor[publicConstructors.size()]);
    }

    /**
     * <p>
     * Returns an array containing Constructor objects reflecting all the public constructors of the
     * class represented by this Class object. An array of length 0 is returned if the class has no
     * public constructors, or if the class is an array class, or if the class reflects a primitive
     * type or void. Note that while this method returns an array of Constructor<T> objects (that is
     * an array of constructors from this class), the return type of this method is Constructor<?>[]
     * and not Constructor<T>[] as might be expected. This less informative return type is necessary
     * since after being returned from this method, the array could be modified to hold Constructor
     * objects for different classes, which would violate the type guarantees of Constructor<T>[].
     * </p>
     * 
     * @return The array of Constructor objects representing the public constructors of this class.
     */
    public Constructor[] getDeclaredConstructors() {
        if (privateConstructors == null) {
            privateConstructors = new ArrayList();

            // collect non-static methods only
            for (String name : metadata.keys()) {
                char ch = name.charAt(0);

                if (ch == '$' && name.length() != 1) {
                    privateConstructors.add((Constructor) (Object) new JSConstructor(name, clazz, clazz.getPropertyAs(NativeFunction.class, name), metadata.getPropertyAs(NativeArray.class, name)));
                }
            }
        }

        // defensive copy
        return privateConstructors.toArray(new Constructor[privateConstructors.size()]);
    }

    /**
     * <p>
     * Returns an array containing Method objects reflecting all the public member methods of the
     * class or interface represented by this Class object, including those declared by the class or
     * interface and those inherited from superclasses and superinterfaces. Array classes return all
     * the (public) member methods inherited from the Object class. The elements in the array
     * returned are not sorted and are not in any particular order. This method returns an array of
     * length 0 if this Class object represents a class or interface that has no public member
     * methods, or if this Class object represents a primitive type or void.
     * </p>
     * 
     * @return The array of Method objects representing the public methods of this class.
     */
    public Method[] getMethods() {
        if (publicMethods == null) {
            publicMethods = new HashMap();

            for (Class type : collectTypes((Class) (Object) this, new HashSet())) {
                for (Method method : type.getDeclaredMethods()) {
                    Integer hash = hash(method.getName(), method.getParameterTypes());

                    if (Modifier.isPublic(method.getModifiers()) && !publicMethods.containsKey(hash)) {
                        publicMethods.put(hash, method);
                    }
                }
            }
        }

        // defensive copy
        return publicMethods.values().toArray(new Method[publicMethods.size()]);
    }

    /**
     * <p>
     * Returns an array of Method objects reflecting all the methods declared by the class or
     * interface represented by this Class object. This includes public, protected, default
     * (package) access, and private methods, but excludes inherited methods. The elements in the
     * array returned are not sorted and are not in any particular order. This method returns an
     * array of length 0 if the class or interface declares no methods, or if this Class object
     * represents a primitive type, an array class, or void. The class initialization method
     * <clinit> is not included in the returned array. If the class declares multiple public member
     * methods with the same parameter types, they are all included in the returned array.
     * </p>
     * 
     * @return The array of Method objects representing all the declared methods of this class.
     */
    public Method[] getDeclaredMethods() {
        if (privateMethods == null) {
            privateMethods = new ArrayList();

            // collect non-static methods only
            for (String name : metadata.keys()) {
                char ch = name.charAt(0);

                if (ch != '$' && ch < 'a' || 'p' < ch) {
                    privateMethods.add((Method) (Object) new JSMethod(name, clazz, metadata.getPropertyAs(NativeArray.class, name)));
                }
            }
        }

        // defensive copy
        return privateMethods.toArray(new Method[privateMethods.size()]);
    }

    /**
     * <p>
     * Compute hash.
     * </p>
     * 
     * @param name
     * @param types
     * @return
     */
    private Integer hash(String name, Class[] types) {
        return name.hashCode() + Arrays.hashCode(types);
    }

    private Set<Class> collectTypes(Class type, Set<Class> types) {
        if (type != null && types.add(type)) {
            // super class
            collectTypes(type.getSuperclass(), types);

            // interfaces
            for (Class interfaceType : type.getInterfaces()) {
                collectTypes(interfaceType, types);
            }
        }
        return types;
    }

    /**
     * <p>
     * Returns an array containing Field objects reflecting all the accessible public fields of the
     * class or interface represented by this Class object. The elements in the array returned are
     * not sorted and are not in any particular order. This method returns an array of length 0 if
     * the class or interface has no accessible public fields, or if it represents an array class, a
     * primitive type, or void.
     * </p>
     * <p>
     * Specifically, if this Class object represents a class, this method returns the public fields
     * of this class and of all its superclasses. If this Class object represents an interface, this
     * method returns the fields of this interface and of all its superinterfaces.
     * </p>
     * <p>
     * The implicit length field for array class is not reflected by this method. User code should
     * use the methods of class Array to manipulate arrays.
     * </p>
     * 
     * @return The array of Field objects representing the public fields.
     */
    public Field getField(String name) {
        return null;
    }

    /**
     * <p>
     * Returns an array containing Field objects reflecting all the accessible public fields of the
     * class or interface represented by this Class object. The elements in the array returned are
     * not sorted and are not in any particular order. This method returns an array of length 0 if
     * the class or interface has no accessible public fields, or if it represents an array class, a
     * primitive type, or void.
     * </p>
     * <p>
     * Specifically, if this Class object represents a class, this method returns the public fields
     * of this class and of all its superclasses. If this Class object represents an interface, this
     * method returns the fields of this interface and of all its superinterfaces.
     * </p>
     * <p>
     * The implicit length field for array class is not reflected by this method. User code should
     * use the methods of class Array to manipulate arrays.
     * </p>
     * 
     * @return The array of Field objects representing the public fields.
     */
    public Field[] getFields() {
        if (publicFields == null) {
            publicFields = new HashMap();

            for (Class type : collectTypes((Class) (Object) this, new HashSet())) {
                for (Field field : type.getDeclaredFields()) {
                    Integer hash = hash(field.getName(), new Class[] {field.getDeclaringClass(), field.getType()});

                    if (Modifier.isPublic(field.getModifiers()) && !publicFields.containsKey(hash)) {
                        publicFields.put(hash, field);
                    }
                }
            }
        }

        // defensive copy
        return publicFields.values().toArray(new Field[publicFields.size()]);
    }

    /**
     * <p>
     * Returns an array of {@code Field} objects reflecting all the fields declared by the class or
     * interface represented by this {@code Class} object. This includes public, protected, default
     * (package) access, and private fields, but excludes inherited fields. The elements in the
     * array returned are not sorted and are not in any particular order. This method returns an
     * array of length 0 if the class or interface declares no fields, or if this {@code Class}
     * object represents a primitive type, an array class, or void.
     * </p>
     * <p>
     * See <em>The Java Language Specification</em>, sections 8.2 and 8.3.
     * </p>
     * 
     * @return the array of {@code Field} objects representing all the declared fields of this class
     * @exception SecurityException If a security manager, <i>s</i>, is present and any of the
     *                following conditions is met:
     *                <ul>
     *                <li>invocation of {@link SecurityManager#checkMemberAccess
     *                s.checkMemberAccess(this, Member.DECLARED)} denies access to the declared
     *                fields within this class
     *                <li>the caller's class loader is not the same as or an ancestor of the class
     *                loader for the current class and invocation of
     *                {@link SecurityManager#checkPackageAccess s.checkPackageAccess()} denies
     *                access to the package of this class
     *                </ul>
     * @since JDK1.1
     */
    public Field[] getDeclaredFields() throws SecurityException {
        if (privateFields == null) {
            privateFields = new ArrayList();

            // collect non-static methods only
            for (String name : metadata.keys()) {
                char ch = name.charAt(0);

                if ('a' <= ch && ch <= 'p') {
                    privateFields.add((Field) (Object) new JSField(name, clazz, metadata.getPropertyAs(NativeArray.class, name)));
                }
            }
        }

        // defensive copy
        return privateFields.toArray(new Field[privateFields.size()]);
    }

    /**
     * <p>
     * Determines if the class or interface represented by this Class object is either the same as,
     * or is a superclass or superinterface of, the class or interface represented by the specified
     * Class parameter. It returns true if so; otherwise it returns false. If this Class object
     * represents a primitive type, this method returns true if the specified Class parameter is
     * exactly this Class object; otherwise it returns false.
     * </p>
     * <p>
     * Specifically, this method tests whether the type represented by the specified Class parameter
     * can be converted to the type represented by this Class object via an identity conversion or
     * via a widening reference conversion. See The Java Language Specification, sections 5.1.1 and
     * 5.1.4 , for details.
     * </p>
     * 
     * @param clazz The Class object to be checked.
     * @return The boolean value indicating whether objects of the type cls can be assigned to
     *         objects of this class.
     */
    public boolean isAssignableFrom(Class<?> clazz) {
        while (clazz != null) {
            // match against class
            if (this == (Object) clazz) {
                return true;
            }

            // match agains interfaces
            for (Class type : clazz.getInterfaces()) {
                if (isAssignableFrom(type)) {
                    return true;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * <p>
     * Determines if the specified {@code Object} is assignment-compatible with the object
     * represented by this {@code Class}. This method is the dynamic equivalent of the Java language
     * {@code instanceof} operator. The method returns {@code true} if the specified {@code Object}
     * argument is non-null and can be cast to the reference type represented by this {@code Class}
     * object without raising a {@code ClassCastException.} It returns {@code false} otherwise.
     * </p>
     * <p>
     * Specifically, if this {@code Class} object represents a declared class, this method returns
     * {@code true} if the specified {@code Object} argument is an instance of the represented class
     * (or of any of its subclasses); it returns {@code false} otherwise. If this {@code Class}
     * object represents an array class, this method returns {@code true} if the specified
     * {@code Object} argument can be converted to an object of the array class by an identity
     * conversion or by a widening reference conversion; it returns {@code false} otherwise. If this
     * {@code Class} object represents an interface, this method returns {@code true} if the class
     * or any superclass of the specified {@code Object} argument implements this interface; it
     * returns {@code false} otherwise. If this {@code Class} object represents a primitive type,
     * this method returns {@code false}.
     * </p>
     * 
     * @param instance The object to check.
     * @return True if {@code instance} is an instance of this class.
     */
    public boolean isInstance(Object instance) {
        return isAssignableFrom(instance.getClass());
    }

    /**
     * Determines if this {@code Class} object represents an array class.
     * 
     * @return {@code true} if this object represents an array class; {@code false} otherwise.
     * @since JDK1.1
     */
    public boolean isArray() {
        return name.startsWith("[");
    }

    /**
     * Determines if the specified {@code Class} object represents an interface type.
     * 
     * @return {@code true} if this object represents an interface; {@code false} otherwise.
     */
    public boolean isInterface() {
        return Modifier.isInterface(modifiers);
    }

    /**
     * Determines if the specified {@code Class} object represents a primitive type.
     * <p>
     * There are nine predefined {@code Class} objects to represent the eight primitive types and
     * void. These are created by the Java Virtual Machine, and have the same names as the primitive
     * types that they represent, namely {@code boolean}, {@code byte}, {@code char}, {@code short},
     * {@code int}, {@code long}, {@code float}, and {@code double}.
     * <p>
     * These objects may only be accessed via the following public static final variables, and are
     * the only {@code Class} objects for which this method returns {@code true}.
     * 
     * @return true if and only if this class represents a primitive type
     * @see java.lang.Boolean#TYPE
     * @see java.lang.Character#TYPE
     * @see java.lang.Byte#TYPE
     * @see java.lang.Short#TYPE
     * @see java.lang.Integer#TYPE
     * @see java.lang.Long#TYPE
     * @see java.lang.Float#TYPE
     * @see java.lang.Double#TYPE
     * @see java.lang.Void#TYPE
     * @since JDK1.1
     */
    public boolean isPrimitive() {
        Class type = (Class) (Object) this;

        return type == int.class || type == long.class || type == float.class || type == double.class || type == boolean.class || type == short.class || type == byte.class || type == void.class;
    }

    /**
     * Returns {@code true} if and only if the underlying class is a local class.
     * 
     * @return {@code true} if and only if this class is a local class.
     * @since 1.5
     */
    public boolean isLocalClass() {
        return false; // FIXME
    }

    /**
     * Returns an array of {@code TypeVariable} objects that represent the type variables declared
     * by the generic declaration represented by this {@code GenericDeclaration} object, in
     * declaration order. Returns an array of length 0 if the underlying generic declaration
     * declares no type variables.
     * 
     * @return an array of {@code TypeVariable} objects that represent the type variables declared
     *         by this generic declaration
     * @throws java.lang.reflect.GenericSignatureFormatError if the generic signature of this
     *             generic declaration does not conform to the format specified in <cite>The
     *             Java&trade; Virtual Machine Specification</cite>
     * @since 1.5
     */
    public TypeVariable<Class<T>>[] getTypeParameters() {
        if (getGenericSignature() != null) {
            return (TypeVariable<Class<T>>[]) null;
        } else {
            return (TypeVariable<Class<T>>[]) new TypeVariable<?>[0];
        }
    }

    /**
     * @return
     */
    private Object getGenericSignature() {
        return null;
    }

    /**
     * Returns the {@code Class} representing the component type of an array. If this class does not
     * represent an array class this method returns null.
     * 
     * @return the {@code Class} representing the component type of this class if this class is an
     *         array
     * @see java.lang.reflect.Array
     * @since JDK1.1
     */
    public Class<?> getComponentType() {
        return isArray() ? forName(name.substring(1)) : null;
    }

    /**
     * <p>
     * Returns the Class representing the superclass of the entity (class, interface, primitive type
     * or void) represented by this Class. If this Class represents either the Object class, an
     * interface, a primitive type, or void, then null is returned. If this object represents an
     * array class then the Class object representing the Object class is returned.
     * </p>
     * 
     * @return The superclass of the class represented by this object.
     */
    public Class<? super T> getSuperclass() {
        if ((Object) this == Object.class || isInterface()) {
            return null;
        } else if (superclass == null) {
            return Object.class;
        } else {
            return superclass;
        }
    }

    /**
     * Returns the {@code Type} representing the direct superclass of the entity (class, interface,
     * primitive type or void) represented by this {@code Class}.
     * <p>
     * If the superclass is a parameterized type, the {@code Type} object returned must accurately
     * reflect the actual type parameters used in the source code. The parameterized type
     * representing the superclass is created if it had not been created before. See the declaration
     * of {@link java.lang.reflect.ParameterizedType ParameterizedType} for the semantics of the
     * creation process for parameterized types. If this {@code Class} represents either the
     * {@code Object} class, an interface, a primitive type, or void, then null is returned. If this
     * object represents an array class then the {@code Class} object representing the
     * {@code Object} class is returned.
     * 
     * @throws java.lang.reflect.GenericSignatureFormatError if the generic class signature does not
     *             conform to the format specified in <cite>The Java&trade; Virtual Machine
     *             Specification</cite>
     * @throws TypeNotPresentException if the generic superclass refers to a non-existent type
     *             declaration
     * @throws java.lang.reflect.MalformedParameterizedTypeException if the generic superclass
     *             refers to a parameterized type that cannot be instantiated for any reason
     * @return the superclass of the class represented by this object
     * @since 1.5
     */
    public Type getGenericSuperclass() {
        // if (getGenericSignature() != null) {
        // // Historical irregularity:
        // // Generic signature marks interfaces with superclass = Object
        // // but this API returns null for interfaces
        // if (isInterface()) return null;
        // return getGenericInfo().getSuperclass();
        // } else
        return getSuperclass();
    }

    /**
     * <p>
     * Determines the interfaces implemented by the class or interface represented by this object.
     * </p>
     * <p>
     * If this object represents an interface, the array contains objects representing all
     * interfaces extended by the interface. The order of the interface objects in the array
     * corresponds to the order of the interface names in the extends clause of the declaration of
     * the interface represented by this object.
     * </p>
     * <p>
     * If this object represents a class or interface that implements no interfaces, the method
     * returns an array of length 0.
     * </p>
     * <p>
     * If this object represents a primitive type or void, the method returns an array of length 0.
     * </p>
     * 
     * @return
     */
    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    /**
     * <p>
     * Returns the name of the entity (class, interface, array class, primitive type, or void)
     * represented by this Class object, as a String.
     * </p>
     * <p>
     * If this class object represents a reference type that is not an array type then the binary
     * name of the class is returned, as specified by The Java™ Language Specification.
     * </p>
     * <p>
     * If this class object represents a primitive type or void, then the name returned is a String
     * equal to the Java language keyword corresponding to the primitive type or void.
     * </p>
     * 
     * @return
     */
    public String getName() {
        return "boot." + name;
    }

    /**
     * <p>
     * Returns the simple name of the underlying class as given in the source code. Returns an empty
     * string if the underlying class is anonymous.
     * </p>
     * <p>
     * The simple name of an array is the simple name of the component type with "[]" appended. In
     * particular the simple name of an array whose component type is anonymous is "[]".
     * </p>
     * 
     * @return The simple name of the underlying class.
     */
    public String getSimpleName() {
        return name;
    }

    /**
     * <p>
     * Returns the canonical name of the underlying class as defined by the Java Language
     * Specification. Returns null if the underlying class does not have a canonical name (i.e., if
     * it is a local or anonymous class or an array whose component type does not have a canonical
     * name).
     * </p>
     * 
     * @return the canonical name of the underlying class if it exists, and {@code null} otherwise.
     * @since 1.5
     */
    public String getCanonicalName() {
        return name;
    }

    /**
     * <p>
     * Creates a new instance of the class represented by this Class object. The class is
     * instantiated as if by a new expression with an empty argument list. The class is initialized
     * if it has not already been initialized.
     * </p>
     * <p>
     * Note that this method propagates any exception thrown by the nullary constructor, including a
     * checked exception. Use of this method effectively bypasses the compile-time exception
     * checking that would otherwise be performed by the compiler. The Constructor.newInstance
     * method avoids this problem by wrapping any exception thrown by the constructor in a (checked)
     * InvocationTargetException.
     * </p>
     * 
     * @return A newly allocated instance of the class represented by this object.
     */
    public Object newInstance() {
        try {
            return getConstructors()[0].newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Returns a {@code Constructor} object that reflects the specified public constructor of the
     * class represented by this {@code Class} object. The {@code parameterTypes} parameter is an
     * array of {@code Class} objects that identify the constructor's formal parameter types, in
     * declared order. If this {@code Class} object represents an inner class declared in a
     * non-static context, the formal parameter types include the explicit enclosing instance as the
     * first parameter.
     * <p>
     * The constructor to reflect is the public constructor of the class represented by this
     * {@code Class} object whose formal parameter types match those specified by
     * {@code parameterTypes}.
     * 
     * @param parameterTypes the parameter array
     * @return the {@code Constructor} object of the public constructor that matches the specified
     *         {@code parameterTypes}
     * @exception NoSuchMethodException if a matching method is not found.
     * @exception SecurityException If a security manager, <i>s</i>, is present and any of the
     *                following conditions is met:
     *                <ul>
     *                <li> invocation of {@link SecurityManager#checkMemberAccess
     *                s.checkMemberAccess(this, Member.PUBLIC)} denies access to the constructor 
     *                <li> the caller's class loader is not the same as or an ancestor of the class
     *                loader for the current class and invocation of
     *                {@link SecurityManager#checkPackageAccess s.checkPackageAccess()} denies
     *                access to the package of this class
     *                </ul>
     * @since JDK1.1
     */
    public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * <p>
     * Returns a map from simple name to enum constant. This package-private method is used
     * internally by Enum to implement public static <T extends Enum<T>> T valueOf(Class<T>, String)
     * efficiently.
     * </p>
     * <p>
     * Note that the map is returned by this method is created lazily on first use. Typically it
     * won't ever get created.
     * </p>
     */
    public Map<String, Enum> enumConstantDirectory() {
        if (enumerationConstants == null) {
            enumerationConstants = new HashMap();

            NativeObject definition = clazz.getPropertyAs(NativeObject.class, "$");

            for (String name : definition.keys()) {
                NativeObject value = definition.getPropertyAs(NativeObject.class, name);

                if (value.isArray()) {
                    for (Enum item : (Enum[]) (Object) value) {
                        enumerationConstants.put(item.name(), item);
                    }
                }
            }
        }
        return enumerationConstants;
    }

    /**
     * Returns the assertion status that would be assigned to this class if it were to be
     * initialized at the time this method is invoked. If this class has had its assertion status
     * set, the most recent setting will be returned; otherwise, if any package default assertion
     * status pertains to this class, the most recent setting for the most specific pertinent
     * package default assertion status is returned; otherwise, if this class is not a system class
     * (i.e., it has a class loader) its class loader's default assertion status is returned;
     * otherwise, the system class default assertion status is returned.
     * <p>
     * Few programmers will have any need for this method; it is provided for the benefit of the JRE
     * itself. (It allows a class to determine at the time that it is initialized whether assertions
     * should be enabled.) Note that this method is not guaranteed to return the actual assertion
     * status that was (or will be) associated with the specified class when it was (or will be)
     * initialized.
     * 
     * @return the desired assertion status of the specified class.
     * @see java.lang.ClassLoader#setClassAssertionStatus
     * @see java.lang.ClassLoader#setPackageAssertionStatus
     * @see java.lang.ClassLoader#setDefaultAssertionStatus
     * @since 1.4
     */
    public boolean desiredAssertionStatus() {
        return true;
    }

    /**
     * <p>
     * Create {@link Class} for the array of this {@link Class}.
     * </p>
     * 
     * @return
     */
    protected JSClass getArrayClass() {
        if (arrayClass == null) {
            arrayClass = new JSClass("[".concat(name), new NativeObject(), new NativeObject(), null, new String[0]);
        }
        return arrayClass;
    }

    /**
     * <p>
     * Returns the Class object associated with the class or interface with the given string name.
     * </p>
     * 
     * @param fqcn The fully qualified name of the desired class.
     * @return The Class object for the class with the specified name.
     */
    public static Class forName(String fqcn) {
        int size = 0;

        while (fqcn.startsWith("[")) {
            size++;
            fqcn = fqcn.substring(1);
        }

        NativeObject definition = boot.getPropertyAs(NativeObject.class, fqcn);

        if (definition == null) {
            return (Class) (Object) new JSClass(fqcn, new NativeObject(), new NativeObject(), Object.class, new String[0]);
        }

        JSClass clazz = (JSClass) definition.getProperty("$");

        for (int i = 0; i < size; i++) {
            clazz = clazz.getArrayClass();
        }
        return (Class) (Object) clazz;
    }
}